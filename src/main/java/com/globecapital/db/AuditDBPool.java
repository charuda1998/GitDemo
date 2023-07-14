package com.globecapital.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.globecapital.jmx.Monitor;
import com.msf.log.Logger;

public class AuditDBPool implements DBPool {

	private static Logger log = Logger.getLogger(AuditDBPool.class);

	private static AuditDBPool instance = null;
	private static InitialContext context;
	private static BasicDataSource datasource;

	//Below bean initialized with value in ApplicationContextListener.
//	public static String AUDIT_DB_BEAN = ""; 

	private AuditDBPool() {

	}

	public static void initTomcatDataSource() {
		try {

			context = new InitialContext();
			datasource = (BasicDataSource) context.lookup("java:comp/env/jdbc/audit");

		} catch (NamingException e) {
			log.error("", e);
		}
	}

	public static void initDataSource(PropertiesConfiguration properties) {

		datasource = new BasicDataSource();
		datasource.setDriverClassName(properties.getString("db.driver_class"));
		datasource.setUrl(properties.getString("db.audit.url"));
		datasource.setUsername(properties.getString("db.username"));
		datasource.setPassword(properties.getString("db.password"));
	}

	public static AuditDBPool getInstance() {

		if (null == instance)
			instance = new AuditDBPool();

		return instance;
	}
	
	public Connection getConnection() throws SQLException {

		try {
			return datasource.getConnection();
		} catch (SQLException e) {
//			Monitor.markFailure(AUDIT_DB_BEAN, e.getMessage());
			throw e;
		}
	}

	public void releasePool() {

		try {

			if (datasource != null)
				datasource.close();
			// log.info("Closing datasource " + datasource);

		} catch (SQLException e) {
			log.warn("Closing datasource ", e);
		}

	}

}
