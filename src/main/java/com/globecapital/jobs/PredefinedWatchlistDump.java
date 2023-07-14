package com.globecapital.jobs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONObject;

import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.GCDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.utils.DateUtils;
import com.msf.cmots.api.data_v1.IndexConstituents;
import com.msf.cmots.api.data_v1.IndexConstituentsList;
import com.msf.cmots.api.data_v1.IndexPerformance;
import com.msf.cmots.api.data_v1.IndexPerformanceList;
import com.msf.cmots.api.equity.GetIndexConstituents;
import com.msf.cmots.api.equity.GetIndexPerformance;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class PredefinedWatchlistDump {

	private static Logger log;

	public static HashMap<String, JSONObject> indices = new HashMap<String, JSONObject>();

	public static void main(String[] args) throws Exception {

		long beforeExecution = System.currentTimeMillis();

		String config_file = args[0];
		
		try {
			Properties JSLogProperties = new Properties();
			AppConfig.loadFile(config_file);
			FileInputStream stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			stream.close();
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(PredefinedWatchlistDump.class);
			log.info("#############################################################################");
			log.info("##### JOB NAME : Predefined Watchlists Dump - BEGINS");
			log.info("##### TIME : " + DateUtils.getCurrentDateTime(DeviceConstants.OPTIONS_DATE_FORMAT_1));
		} catch (ConfigurationException e) {
			System.out.println("Exception while configuring the JSLOG properties");
		}
		
		try {
			com.msf.cmots.config.AppConfig.loadFile(config_file);
		} catch (Exception e) {
			log.error("Cannot load cmots config properties %s", e);
			System.exit(1);
		}

		log.info("************************ STATS ************************");

		GCDBPool.initDataSource(AppConfig.getProperties());
		loadPredefinedWatchlist();
		loadPredefinedWatchlistSymbols();
		
		log.info("*******************************************************");

		long afterExecution = System.currentTimeMillis();
		
		log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
		log.info("##### JOB NAME : Predefined Watchlists Dump - ENDS");
		log.info("#############################################################################");
		
	}

	private static void loadPredefinedWatchlist() throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String query = DBQueryConstants.INSERT_PREDEFINED_WATCHLIST;
//		log.info("Query :: " + query);
		try {
			GetIndexPerformance indexPerformance = new GetIndexPerformance();
			String[] exchanges = AppConfig.getArray("exchanges");
			for (String exchange : exchanges) {
				
				if(exchange.equalsIgnoreCase(ExchangeSegment.NSE))
					Monitor.setJobsBeans(Monitor.PREDEFINED_WATCHLIST_NSE);
				else if(exchange.equalsIgnoreCase(ExchangeSegment.BSE))
					Monitor.setJobsBeans(Monitor.PREDEFINED_WATCHLIST_BSE);
				
				indexPerformance.setExchange(exchange);
				IndexPerformanceList indexPerformanceList = indexPerformance.invoke();
				try {
					conn = GCDBPool.getInstance().getConnection();
					ps = conn.prepareStatement(query);
					for (IndexPerformance index : indexPerformanceList) {

						String isWatchlist = "1";
						String weightage = "9";
						String indexCode = index.getIndexCode();
						int indx = indexCode.indexOf(".");
						indexCode = indexCode.substring(0, indx);
						ps.setString(1, indexCode);

						String indexName = index.getIndexName();
						if (indexName.equalsIgnoreCase("Nifty 50")) {
							indexName = "NIFTY 50";
							weightage = "1";
						} else if (indexName.equalsIgnoreCase("Nifty Bank")) {
							indexName = "NIFTY BANK";
							weightage = "3";
						} else if (indexName.equalsIgnoreCase("Nifty Pharma")) {
							indexName = "NIFTY PHARMA";
							weightage = "4";
						} else if (indexName.equalsIgnoreCase("Nifty Smallcap 100")) {
							indexName = "NIFTY SMALL 100";
							weightage = "5";
						} else if (indexName.equalsIgnoreCase("S&P BSE Sensex")) {
							indexName = "SENSEX";
							weightage = "2";
						} else
							isWatchlist = "0";

						ps.setString(2, indexName);
						ps.setString(3, exchange);
						ps.setString(4, isWatchlist);
						ps.setString(5, weightage);

						ps.addBatch();
					}
					int[] rows=ps.executeBatch();
					
					log.info("Total no of records received for " + exchange + " : " + indexPerformanceList.size());
					log.info("Total no of records inserted for " + exchange + " : " + rows.length );
										
					Monitor.markSuccess(String.format("No of Rows received : %d, No of Rows inserted : %d", indexPerformanceList.size(), rows.length));
					
				} catch (Exception e) {
					Monitor.markCritical("Exception : "+e.getMessage());
					log.debug("Error :: " + e);
				} finally {
					Helper.closeStatement(ps);
					Helper.closeConnection(conn);
				}
			}
		} catch (Exception e) {
			Monitor.setJobsBeans(Monitor.PREDEFINED_WATCHLIST_NSE);
			Monitor.markCritical("Exception : "+e.getMessage());
			Monitor.setJobsBeans(Monitor.PREDEFINED_WATCHLIST_BSE);
			Monitor.markCritical("Exception : "+e.getMessage());
			log.debug("Error :: " + e);
		}
	}

	private static void loadPredefinedWatchlistSymbols() {

		try {
			Connection conn = null;
			ResultSet res = null;
			PreparedStatement pst = null;
			String query = DBQueryConstants.GET_WATCHLISTS;
//			log.info("Query :: " + query);
			try {
				conn = GCDBPool.getInstance().getConnection();
				pst = conn.prepareStatement(query);
				res = pst.executeQuery();
				while (res.next()) {
					loadSymbolsToDB(res.getString(DBConstants.INDEX_CODE), res.getString(DBConstants.INDEX_NAME),
							res.getString(DBConstants.EXCHANGE));
				}
			} finally {
				Helper.closeStatement(pst);
				Helper.closeConnection(conn);
			}
		} catch (Exception e) {
			log.info("Error :: " + e);
		}
	}

	private static void loadSymbolsToDB(String indexCode, String indexName, String exchange) throws Exception {
		
		if(indexName.equalsIgnoreCase("SENSEX"))
			Monitor.setJobsBeans(Monitor.SENSEX_SYMBOLS);
		else if(indexName.equalsIgnoreCase("NIFTY 50"))
			Monitor.setJobsBeans(Monitor.NIFTY50_SYMBOLS);
		else if(indexName.equalsIgnoreCase("NIFTY BANK"))
			Monitor.setJobsBeans(Monitor.NIFTY_BANK_SYMBOLS);
		else if(indexName.equalsIgnoreCase("NIFTY PHARMA"))
			Monitor.setJobsBeans(Monitor.NIFTY_PHARMA_SYMBOLS);
		else if(indexName.equalsIgnoreCase("NIFTY SMALL 100"))
			Monitor.setJobsBeans(Monitor.NIFTY_SMALL100_SYMBOLS);
		
		Connection con = null;
		PreparedStatement ps = null;
		String query = DBQueryConstants.INSERT_PREDEFINED_WATCHLIST_SYMBOLS;
//		log.info("Query :: " + query);
		try {

			GetIndexConstituents indexConstituents = new GetIndexConstituents();
			indexConstituents.setExchange(exchange);
			indexConstituents.setIndexCode(indexCode);

			IndexConstituentsList indexList = indexConstituents.invoke();
			try {

				con = GCDBPool.getInstance().getConnection();
				ps = con.prepareStatement(query);
				for (IndexConstituents index : indexList) {

					ps.setString(1, indexCode);
					ps.setString(2, indexName);
					ps.setString(3, index.getNSESymbol());
					ps.setString(4, index.getISIN());

					ps.addBatch();
				}
				int[] rows = ps.executeBatch();
				
				log.info("Total no of records received for " + indexName + " : " + indexList.size());
				log.info("Total no of records inserted for " + indexName + " : " + rows.length );
								
				Monitor.markSuccess(String.format("No of Rows received : %d, No of Rows inserted : %d", indexList.size(), rows.length));
				
			} catch (Exception e) {
				Monitor.markCritical("Exception : "+e.getMessage());
				log.debug("Error :: " + e);
			} finally {
				Helper.closeStatement(ps);
				Helper.closeConnection(con);
			}
		} catch (Exception e) {
			Monitor.markCritical("Exception : "+e.getMessage());
			log.debug("Error :: " + e);
		}
	}

}
