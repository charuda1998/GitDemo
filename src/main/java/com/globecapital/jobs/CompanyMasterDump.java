package com.globecapital.jobs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.msf.cmots.api.data_v1.Company;
import com.msf.cmots.api.data_v1.CompanyList;
import com.msf.cmots.api.equity.GetCompanyMaster_v1;
import com.msf.cmots.config.AppConfig;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class CompanyMasterDump {
	private static Logger log = Logger.getLogger(CompanyMasterDump.class);
	
	public static void main(String args[]) throws Exception
	{		
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("CONFIG-PATH").hasArg().isRequired().create('C'));
		options.addOption(OptionBuilder.hasArg().isRequired().create('L'));

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException e1) {
			e1.printStackTrace();
		}

		String config_file = cmd.getOptionValue('C');
		String log_file = cmd.getOptionValue('L');

		Properties JSLogProperties = new Properties();
		try {
			JSLogProperties.load(new FileInputStream(log_file));
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(CompanyMasterDump.class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			AppConfig.loadFile(config_file);
		} catch (Exception e) {
			log.error("Cannot load  config properties %s", e);
			System.exit(1);
		}
		
		GCDBPool.initDataSource(AppConfig.getProperties());
		
		GetCompanyMaster_v1 companyMaster = new GetCompanyMaster_v1();
		
		CompanyList companyMasterList = companyMaster.invoke();
		
		loadDB(companyMasterList);

	}
	
	private static void loadDB(CompanyList companyList) throws SQLException
	{
		for (Company company : companyList) {
			
			Connection conn = null;
			PreparedStatement ps = null;

			String query = DBQueryConstants.UPDATE_SCRIPMASTER;

			try {

				conn = GCDBPool.getInstance().getConnection();

				ps = conn.prepareStatement(query);
				
				ps.setString(1, company.getCoCode());
				ps.setString(2, company.getBsecode());
				ps.setString(3, company.getCategoryname());
				ps.setString(4, company.getBsegroup());
				ps.setString(5, company.getMcaptype());
				ps.setString(6, company.getSectorcode());
				ps.setString(7, company.getSectorname());
				ps.setString(8, company.getIsin());
				
				log.info("Query :: "+ps);
				
				ps.executeUpdate();
				
			}
			catch (Exception e) {
				log.debug("error is" + e.getMessage());
			} finally {
				Helper.closeStatement(ps);
				Helper.closeConnection(conn);
			}
		}

	}

}
