package com.globecapital.listeners;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.ConfigurationException;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.business.market.Indices;
import com.globecapital.business.market.Market;
import com.globecapital.business.order.AMODetails;
import com.globecapital.business.watchlist.PredefinedWatchList;
import com.globecapital.config.AppConfig;
import com.globecapital.config.IndicesChart;
import com.globecapital.config.IndicesMapping;
import com.globecapital.config.InfoMessage;
import com.globecapital.config.MarketConfig;
import com.globecapital.config.IndicesChart;
import com.globecapital.config.IndicesMapping;
import com.globecapital.config.InfoMessage;
import com.globecapital.config.MarketConfig;
import com.globecapital.config.MarketMoversFilter;
import com.globecapital.config.UnitTesting;
import com.globecapital.db.ChartDBPool;
import com.globecapital.db.GCDBPool;
import com.globecapital.db.NewsDBPool;
import com.globecapital.db.QuoteDBPool;
import com.globecapital.db.QuoteDataDBPool;
import com.globecapital.db.RedisPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.market.CmotsCacheThread;
import com.globecapital.services.market.CmotsCacheThread;
import com.globecapital.services.marketdata.LoadExpiriesCacheThread;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.utils.ChartUtils;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class ApplicationContextListener implements ServletContextListener {

	private static Logger log;

	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub

		ServletContext ctx = event.getServletContext();

		String fileSeparator = System.getProperty("file.separator");
		String classFolder = ctx.getRealPath("/") + "WEB-INF" + fileSeparator + "classes" + fileSeparator;

		String jsLogFile = classFolder + "jslog.properties";
		String configFile = classFolder + "config.properties";
		String infoMessageFile = classFolder + "InfoMessage.properties";
		String unitTestingConfigFile = classFolder + "unit_testing.properties";
		String indicesChartConfigFile = classFolder + "indices_chart.properties";
		String marketTimingsConfigFile = classFolder + "market_timing.properties";
		String indexMappingConfigFile = classFolder + "index_mapping.properties";
		String marketMoversFilterConfigFile = classFolder + "market_movers_filter.properties";

		loadConfigAndSetLogger(jsLogFile);

		loadAllConfigProperties(configFile, infoMessageFile, unitTestingConfigFile, indicesChartConfigFile, marketTimingsConfigFile, indexMappingConfigFile, marketMoversFilterConfigFile);

		registerMonitJMXBeans();

		checkDBConnectivity();

		loadAllCaches();
	}

	public void loadConfigAndSetLogger(String jsLogFile)
	{
		try { 
			Properties properties = new Properties();
			FileInputStream logFileInputStream = new FileInputStream(jsLogFile);
			properties.load(logFileInputStream);
			logFileInputStream.close();
			Logger.setLogger(properties);
			log = Logger.getLogger(ApplicationContextListener.class);
		}
		catch (Exception e) {
			System.out.println("Exception while configuring the JSLOG properties");
		}
	}

	public void loadAllConfigProperties(String configFile, String infoMsgFile, String unitTestingConfigFile, 
			String indicesChartConfigFile, String marketTimingsConfigFile, String indexMappingConfigFile, String marketMoversFilterConfigFile) {

		log.info("Config message file " + configFile);
		log.info("info message file " + infoMsgFile);
		log.info("UnitTesting properties file " + unitTestingConfigFile);
		log.info("Indices chart properties file " + indicesChartConfigFile);
		log.info("Indices mapping properties file " + indexMappingConfigFile);
		log.info("Market movers properties file " + marketMoversFilterConfigFile);

		try {
			InfoMessage.loadFile(infoMsgFile);
		} catch (ConfigurationException e) {
			log.error("Cannot load info message properties" + e);
		}

		try {
			AppConfig.loadFile(configFile);
			com.msf.cmots.config.AppConfig.loadFile(configFile);
		} catch (Exception e) {
			log.error("Cannot load config properties");
			log.error("", e);
		}

		try {
			UnitTesting.loadResponse(unitTestingConfigFile);
		} catch (Exception e) {
			log.error("Cannot load unit_testing properties");
			log.error("", e);
		}
		
		try {
			IndicesChart.loadFile(indicesChartConfigFile);
		} catch (Exception e) {
			log.error("Cannot load indices_chart properties");
			log.error("", e);
		}
		
		try {
			MarketConfig.loadFile(marketTimingsConfigFile);
		} catch (Exception e) {
			log.error("Cannot load Market Config properties");
			log.error("", e);
		}
		
		try {
			IndicesMapping.loadFile(indexMappingConfigFile);
		} catch (Exception e) {
			log.error("Cannot load indices mappin properties");
			log.error("", e);
		}
		
		try {
			MarketMoversFilter.loadResponse(marketMoversFilterConfigFile);
		} catch (Exception e) {
			log.error("Cannot load Market movers filter properties");
			log.error("", e);
		}

	}

	public void registerMonitJMXBeans() {

		try {
			String ftAPIBeanName = AppConfig.getValue("gcservices.jmx.beans.FT_API");
			String spiderAPIBeanName = AppConfig.getValue("gcservices.jmx.beans.SPYDER_API");
			String gcAPIBeanName = AppConfig.getValue("gcservices.jmx.beans.GC_API");
			String gcDBBeanName = AppConfig.getValue("gcservices.jmx.beans.GLOBECAPITAL_DB");
			String quoteDBBeanName = AppConfig.getValue("gcservices.jmx.beans.QUOTE_DB");
			String quoteDataDBBeanName = AppConfig.getValue("gcservices.jmx.beans.QUOTE_DATA_DB");
			String chartDBBeanName = AppConfig.getValue("gcservices.jmx.beans.CHART_DB");
			String newsDBBeanName = AppConfig.getValue("gcservices.jmx.beans.NEWS_DB");
			String cmotsCacheBeanName = AppConfig.getValue("gcservices.jmx.beans.CMOTS_CACHE");
			
			FTApi.API_BEAN = ftAPIBeanName;
			SpyderApi.API_BEAN = spiderAPIBeanName;
			GCApi.API_BEAN = gcAPIBeanName;
			GCDBPool.GLOBECAPITAL_DB_BEAN = gcDBBeanName;
			QuoteDataDBPool.QUOTE_DATA_DB_BEAN = quoteDataDBBeanName;
			QuoteDBPool.QUOTE_DB_BEAN = quoteDBBeanName;
			ChartDBPool.CHART_DB_BEAN = chartDBBeanName;
			NewsDBPool.NEWS_DB_BEAN = newsDBBeanName;
			Market.CMOTS_API_BEAN = cmotsCacheBeanName;

			Monitor.registerBean(ftAPIBeanName);
			Monitor.registerBean(spiderAPIBeanName);
			Monitor.registerBean(gcAPIBeanName);
			Monitor.registerBean(gcDBBeanName);
			Monitor.registerBean(quoteDBBeanName);
			Monitor.registerBean(chartDBBeanName);
			Monitor.registerBean(newsDBBeanName);
			Monitor.registerBean(cmotsCacheBeanName);

		} catch (Exception e) {
			log.error("Error while registering the JMX beans: " + e);
		}
	}

	public void loadAllCaches() {
		try {
			log.debug("loading symbols");
			log.info("LoadAllCaches: Loading symbols");
			SymbolMap.loadSymbols();
		} catch (Exception e) {
			log.error("LoadAllCaches: Error in loading symbol info: " + e);
		}

		try {
			log.debug("loading predefined watchlists");
			log.info("LoadAllCaches: Loading predefined watchlists");
			PredefinedWatchList.loadWatchlists();
		} catch (SQLException e) {
			log.error("LoadAllCaches: Error while loading the watchlists: " + e);
		}

		try {
			log.debug("loading AMO details");
			log.info("LoadAllCaches: Loading AMO details");
			AMODetails.loadAMODetails();
		} catch (Exception e) {
			log.error("LoadAllCaches: Exception while fetching AMO Detail: " + e);
		}

		try {
			log.debug("loading indices");
			log.info("LoadAllCaches: Loading indices");
			Indices.loadIndices();
		} catch (Exception e) {
			log.error("LoadAllCaches: Error while loading the indices: " + e);
		}
		
		try {
			log.debug("loading chart start date");
			log.info("LoadAllCaches: Loading chart start date");
			ChartUtils.loadChartDate();
		} catch (Exception e) {
			log.error("LoadAllCaches: Error while loading chart start date: " + e);
		}
		
		try
		{
			log.debug("loading cmots cache data");
			log.info("LoadAllCaches: Loading cmots cache data");
			CmotsCacheThread cmotsCacheThread = new CmotsCacheThread();
			cmotsCacheThread.run();
		} catch(Exception e) {
			log.error("LoadAllCaches: Error while loading cmots data: " + e);
		}
		
		try
		{
			log.debug("loading expiries cache data");
			LoadExpiriesCacheThread expiriesCacheThread = new LoadExpiriesCacheThread();
			expiriesCacheThread.run();
		} catch(Exception e) {
			log.error("Error while loading expiry data: " + e);
		}
	}

	public void checkDBConnectivity() {
		// Checking for database connections
		try {
			log.info("Checking for globecapital database connection");
			GCDBPool.initTomcatDataSource();
			Connection gcDBConnection = GCDBPool.getInstance().getConnection();
			Helper.closeConnection(gcDBConnection);
			log.info("Database connectivity is available with globecapital");
			
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true")) {
				log.info("Checking for quote_data and chart database connection");
				QuoteDataDBPool.initTomcatDataSource();
				Connection quoteDataConnection = QuoteDataDBPool.getInstance().getConnection();
				Helper.closeConnection(quoteDataConnection);
				log.info("Database connectivity is available with quote_data");
			}
			
			log.info("Checking for news database connection");
			NewsDBPool.initTomcatDataSource();
			Connection newsDBConnection = NewsDBPool.getInstance().getConnection();
			Helper.closeConnection(newsDBConnection);
			log.info("Database connectivity is available with chart");
			
		} catch (Exception e) {
			log.error("Error in creating sql connection ", e);
		} 
	}

	public void contextDestroyed(ServletContextEvent arg0) {

		try {
			log.info("Closing GC database pool");
			GCDBPool.getInstance().releasePool();
		} catch (Exception e) {
			log.error("Exception while closing GC DB Pool", e);
		}

		try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true")) {
				try {
					log.info("Closing QuoteData database pool");
					QuoteDataDBPool.getInstance().releasePool();
				} catch (Exception e) {
					log.error("Exception while closing QuoteData DB Pool", e);
				}
			}
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("Exception occurred", ex);
		}
		
		try {
			log.info("Closing News database pool");
			NewsDBPool.getInstance().releasePool();
		} catch (Exception e) {
			log.error("Exception while closing News DB Pool", e);
		}
		
		try {
			log.info("Closing QuoteDBPool");
			QuoteDBPool.getInstance().releasePool();
		} catch (Exception e) {
			log.error("Exception while closing QuoteDBPool", e);
		}
		
		try {
			RedisPool redisPool = new RedisPool();
			redisPool.releaseConnection();
		} catch (Exception e) {
			log.error("Exception while closing RedisPool", e);
		}

		try {
			Monitor.release();
		} catch (Exception e) {
			log.error("Error while unregistering monitoring beans", e);
		}

		try {
			log.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}