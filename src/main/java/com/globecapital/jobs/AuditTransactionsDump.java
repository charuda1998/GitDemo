package com.globecapital.jobs;

import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;

import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.AuditDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;

import java.util.ArrayList;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.*;

public class AuditTransactionsDump {

	private static Logger log = Logger.getLogger(AuditTransactionsDump.class);

	public static void main(String args[]) throws Exception {

		long beforeExecution = System.currentTimeMillis();

		FileInputStream stream = null;

		String config_file = args[0];
		Properties JSLogProperties = new Properties();
		try {
			stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(AuditTransactionsDump.class);
			log.info("#############################################################################");
			log.info("##### JOB NAME : Audit Transactions Dump - BEGINS");
			log.info("##### TIME : " + DateUtils.getCurrentDateTime(DeviceConstants.OPTIONS_DATE_FORMAT_1));
		} catch (ConfigurationException e) {
			e.getMessage();
		}

		try {
			AppConfig.loadFile(config_file);
		} catch (Exception e) {
			log.error("Cannot load  config properties %s", e);
			System.exit(1);
		}
		
		Monitor.setJobsBeans(Monitor.AUDIT_TRANSACTIONS);

		String scrip = AppConfig.getValue("audit.scrip");

		try {
		ProcessBuilder pb = new ProcessBuilder(scrip);
		Process process = pb.start();
		process.waitFor();
		} catch (Exception e) {
			log.error("Exception : " + e);
		}
		String filePath = AppConfig.getValue("audit.file");

		AuditDBPool.initDataSource(AppConfig.getProperties());

		Connection conn = null;
		Statement ps = null;

		try {
			
			conn = AuditDBPool.getInstance().getConnection();
			ps = conn.createStatement();
			List<String> lines = Files.readAllLines(Paths.get(filePath));
			for (String query : lines) {
				if (query == null)
					continue;
				ps.addBatch(query);
			}
			
			int[] rows = ps.executeBatch();

			log.info("************************ STATS ************************");
			log.info("No of rows received : " + lines.size());
			log.info("No of rows inserted : " + rows.length);
			log.info("*******************************************************");
			
			Monitor.markSuccess(String.format("No of rows received : %d, No of rows inserted : %d", lines.size(), rows.length));

		} catch (Exception e) {
			Monitor.markCritical("Exception "+e.getMessage());
			log.info("Exception : " + e);
			e.printStackTrace();
		} finally {
			ps.close();
			conn.close();
		}

		long afterExecution = System.currentTimeMillis();
		
		log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
		log.info("##### JOB NAME : Audit Transactions Dump - ENDS");
		log.info("#############################################################################");
	}

}
