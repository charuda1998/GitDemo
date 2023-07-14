package com.globecapital.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.globecapital.jmx.Monitor;
import com.msf.log.Logger;

public class GCDBPool implements DBPool {

	private static Logger log = Logger.getLogger(GCDBPool.class);

	private static GCDBPool instance = null;
	private static InitialContext context;
	private static BasicDataSource datasource;

	//Below bean initialized with value in ApplicationContextListener.
	public static String GLOBECAPITAL_DB_BEAN = ""; 

	private GCDBPool() {

	}

	public static void initTomcatDataSource() {
		try {

			context = new InitialContext();
			datasource = (BasicDataSource) context.lookup("java:comp/env/jdbc/globecapital");

		} catch (NamingException e) {
			log.error("", e);
		}
	}

	public static void initDataSource(PropertiesConfiguration properties) {

		datasource = new BasicDataSource();
		datasource.setDriverClassName(properties.getString("db.driver_class"));
		datasource.setUrl(properties.getString("db.gc.url"));
		datasource.setUsername(properties.getString("db.username"));
		datasource.setPassword(properties.getString("db.password"));
	}

	public static GCDBPool getInstance() {

		if (null == instance)
			instance = new GCDBPool();

		return instance;
	}
	
	public Connection getConnection() throws SQLException {

		try {
			return datasource.getConnection();
		} catch (SQLException e) {
			Monitor.markFailure(GLOBECAPITAL_DB_BEAN, e.getMessage());
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
