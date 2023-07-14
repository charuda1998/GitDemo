package com.globecapital.jobs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;

import com.globecapital.api.gc.backoffice.AMODetailAPI;
import com.globecapital.api.gc.backoffice.AMODetailRequest;
import com.globecapital.api.gc.backoffice.AMODetailResponse;
import com.globecapital.api.gc.backoffice.AMODetailRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.GCDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class AMODetailsDump {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private static Logger log;

	public static HashMap<String, String> token = new HashMap<String, String>();
	public static String DATE_TIME_FORMAT = "dd/MMM/yy HH:mm";

	public static void main(String[] args) throws Exception  {

        long beforeExecution = System.currentTimeMillis();

        String config_file = args[0];
		
		try {
			Properties JSLogProperties = new Properties();
			AppConfig.loadFile(config_file);
			FileInputStream stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			stream.close();
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(AMODetailsDump.class);
			log.info("#############################################################################");
			log.info("##### JOB NAME : AMO Details Dump - BEGINS");
			log.info("##### TIME : " + DateUtils.getCurrentDateTime(DeviceConstants.OPTIONS_DATE_FORMAT_1));
		} catch (ConfigurationException e) {
			System.out.println("Exception while configuring the JSLOG properties");
		}
		
		log.info("************************ STATS ************************");

		GCDBPool.initDataSource(AppConfig.getProperties());
		
		if(AppConfig.getValue("amo_configuration").equalsIgnoreCase("API"))
		{
			List<AMODetailRows> rows = invokeAMODetail();
			dumpAMODetails(rows);
		}
		else if(AppConfig.getValue("amo_configuration").equalsIgnoreCase("hardcoded"))
			dumpAMODetailsHardCoded();
		
		log.info("*******************************************************");
		
        long afterExecution = System.currentTimeMillis();

		log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
		log.info("##### JOB NAME : AMO Details Dump - ENDS");
		log.info("#############################################################################");

	}
	
	private static List<AMODetailRows> invokeAMODetail() throws Exception {
		
		log.debug("Invoking AMO Details API");
		String sAPIToken = GCAPIAuthToken.getAuthToken();
		
		AMODetailRequest amoDetailReq = new AMODetailRequest();
		amoDetailReq.setAuthCode(sAPIToken);
		amoDetailReq.setDate(DateUtils.getCurrentDate());

		AMODetailAPI amoDetailAPI = new AMODetailAPI();
		
		AMODetailResponse amoDetailRes = amoDetailAPI.get(amoDetailReq, AMODetailResponse.class, "","GetAMODetails");
		
		if (amoDetailRes.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
			amoDetailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
			amoDetailRes = amoDetailAPI.get(amoDetailReq, AMODetailResponse.class, "","GetAMODetails");
		}
		if (amoDetailRes.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {

			List<AMODetailRows> rows = amoDetailRes.getDetails();
			
			return rows;
			
			
		}
		return null;
	}

	private static void dumpAMODetailsHardCoded() throws ParseException, SQLException, ConfigurationException, AppConfigNoKeyFoundException {
		
		log.info("Dumping hardcoded values");
		String sStartDateTime = DateUtils.formatDate(DateUtils.getCurrentDate() + " " + "19:30" + ":00",
				DBConstants.AMO_FROM_FORMAT, DBConstants.DB_DATE_TIME_FORMAT);
		String sEndDateTime = DateUtils.formatDate(DateUtils.getCurrentDate() + " " + "23:30" + ":00",
				DBConstants.AMO_FROM_FORMAT, DBConstants.DB_DATE_TIME_FORMAT);
		String sTodayDate = DateUtils.formatDate(DateUtils.getCurrentDate(), DBConstants.AMO_FROM_DATE_FORMAT,
				DBConstants.DB_DATE_FORMAT);
		
		long beforeExecution = System.currentTimeMillis();
		insertAMODetails(ExchangeSegment.NSE, sStartDateTime, sEndDateTime, sTodayDate);
		long afterExecution = System.currentTimeMillis();
		
		log.info("total time taken: " + (afterExecution - beforeExecution));
		log.info("Total rows inserted :: " + 1);
		Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NSE);
		Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + "EndTime: %s"
						,sStartDateTime, sEndDateTime));
		
		long beforeExecution1 = System.currentTimeMillis();
		insertAMODetails(ExchangeSegment.BSE, sStartDateTime, sEndDateTime, sTodayDate);
		long afterExecution1 = System.currentTimeMillis();
		log.info("total time taken: " + (afterExecution1 - beforeExecution1));
		log.info("Total rows inserted :: " + 1);
		Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_BSE);
		Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + "EndTime: %s"
				,sStartDateTime, sEndDateTime));
		
		long beforeExecution2 = System.currentTimeMillis();
		insertAMODetails(ExchangeSegment.NFO, sStartDateTime, sEndDateTime, sTodayDate);
		long afterExecution2 = System.currentTimeMillis();
		log.info("total time taken: " + (afterExecution2 - beforeExecution2));
		log.info("Total rows inserted :: " + 1);
		Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NFO);
		Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + "EndTime: %s"
				,sStartDateTime, sEndDateTime));
		
		long beforeExecution3 = System.currentTimeMillis();
		insertAMODetails(ExchangeSegment.MCX, sStartDateTime, sEndDateTime, sTodayDate);
		long afterExecution3 = System.currentTimeMillis();
		log.info("total time taken: " + (afterExecution3 - beforeExecution3));
		log.info("Total rows inserted :: " + 1);
		Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_MCX);
		Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + "EndTime: %s"
				,sStartDateTime, sEndDateTime));
		
		long beforeExecution4 = System.currentTimeMillis();
		insertAMODetails(ExchangeSegment.NCDEX, sStartDateTime, sEndDateTime, sTodayDate);
		long afterExecution4 = System.currentTimeMillis();
		log.info("total time taken: " + (afterExecution4 - beforeExecution4));
		log.info("Total rows inserted :: " + 1);
		Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NCDEX);
		Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + "EndTime: %s"
				,sStartDateTime, sEndDateTime));
		
		long beforeExecution5 = System.currentTimeMillis();
		insertAMODetails(ExchangeSegment.NSECDS, sStartDateTime, sEndDateTime, sTodayDate);
		long afterExecution5 = System.currentTimeMillis();
		log.info("total time taken: " + (afterExecution5 - beforeExecution5));
		log.info("Total rows inserted :: " + 1);
		Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NSECDS);
		Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + "EndTime: %s"
				,sStartDateTime, sEndDateTime));
		
		long beforeExecution6 = System.currentTimeMillis();
		insertAMODetails(ExchangeSegment.BSECDS, sStartDateTime, sEndDateTime, sTodayDate);
		long afterExecution6 = System.currentTimeMillis();
		log.info("total time taken: " + (afterExecution6 - beforeExecution6));
		log.info("Total rows inserted :: " + 1);
		Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_BSECDS);
		Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + "EndTime: %s"
				,sStartDateTime, sEndDateTime));
		
	}
	
	private static void insertAMODetails(String sExchange, String sStartDateTime, String sEndDateTime, 
			String sDate) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.INSERT_AMO_DETAILS);

			ps.setString(1, sExchange);
			ps.setString(2, sStartDateTime);
			ps.setString(3, sEndDateTime);
			ps.setString(4, sDate);
		

			ps.executeUpdate();


		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}

	public static void dumpAMODetails(List<AMODetailRows> rows) throws Exception {

		try {

			for (AMODetailRows amoDetails : rows) {
				Connection conn = null;
				PreparedStatement ps = null;
				String query = DBQueryConstants.INSERT_AMO_DETAILS;
				log.info("Query:" + query);
				
				try
				{
					conn = GCDBPool.getInstance().getConnection();
					ps = conn.prepareStatement(query);
					String sExchange = "";
					String sMonitStartDateTime = "", sMonitEndDateTime = "";
	
					if (amoDetails.getExchange().equalsIgnoreCase(ExchangeSegment.NSE))
					{
						sExchange = ExchangeSegment.NSE;
						Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NSE);
					}
					else if (amoDetails.getExchange().equalsIgnoreCase(ExchangeSegment.BSE))
					{
						sExchange = ExchangeSegment.BSE;
						Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_BSE);
					}
					else if (amoDetails.getExchange().equalsIgnoreCase(ExchangeSegment.NFO))
					{
						sExchange = ExchangeSegment.NFO;
						Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NFO);
					}
					else if (amoDetails.getExchange().equalsIgnoreCase(ExchangeSegment.MCX))
					{
						sExchange = ExchangeSegment.MCX;
						Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_MCX);
					}
					else if (amoDetails.getExchange().equalsIgnoreCase(ExchangeSegment.NCDEX))
					{
						sExchange = ExchangeSegment.NCDEX;
						Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NCDEX);
					}
					else if (amoDetails.getExchange().equalsIgnoreCase(GCConstants.NSEC))
					{
						sExchange = ExchangeSegment.NSECDS;
						Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_NSECDS);
					}
					else if (amoDetails.getExchange().equalsIgnoreCase(GCConstants.BSEC))
					{
						sExchange = ExchangeSegment.BSECDS;
						Monitor.setJobsBeans(Monitor.LOAD_AMO_DETAILS_BSECDS);
					}
					else
						continue;
	
					String sStartTime = amoDetails.getStarttime();
					String sEndTime = amoDetails.getEndtime();
	
					String[] arrStartHourAndMin = sStartTime.split(":");
					String[] arrEndHourAndMin = sEndTime.split(":");
					
					
					
	
					if (Integer.parseInt(arrStartHourAndMin[0]) < Integer.parseInt(arrEndHourAndMin[0])) {
						String sStartDateTime = DateUtils.formatDate(DateUtils.getCurrentDate() + " " + sStartTime + ":00",
								DBConstants.AMO_FROM_FORMAT, DBConstants.DB_DATE_TIME_FORMAT);
						String sEndDateTime = DateUtils.formatDate(DateUtils.getCurrentDate() + " " + sEndTime + ":00",
								DBConstants.AMO_FROM_FORMAT, DBConstants.DB_DATE_TIME_FORMAT);
						
						sMonitStartDateTime = sStartDateTime;
						sMonitEndDateTime = sEndDateTime;
						
						ps.setString(1, sExchange);
						ps.setString(2, sStartDateTime);
						ps.setString(3, sEndDateTime);
						ps.setString(4, DateUtils.formatDate(DateUtils.getCurrentDate(), DBConstants.AMO_FROM_DATE_FORMAT,
								DBConstants.DB_DATE_FORMAT));
						ps.addBatch();
					} else if (Integer.parseInt(arrStartHourAndMin[0]) > Integer.parseInt(arrEndHourAndMin[0])) {
						String sTodayStartDateTime = DateUtils.formatDate(DateUtils.getCurrentDate() + " " + sStartTime + ":00",
								DBConstants.AMO_FROM_FORMAT, DBConstants.DB_DATE_TIME_FORMAT);
						String sTodayEndDateTime = DateUtils.formatDate(DateUtils.getCurrentDate() + " " + "23:59:59",
								DBConstants.AMO_FROM_FORMAT, DBConstants.DB_DATE_TIME_FORMAT);
						
						sMonitStartDateTime = sTodayStartDateTime;
						
						ps.setString(1, sExchange);
						ps.setString(2, sTodayStartDateTime);
						ps.setString(3, sTodayEndDateTime);
						ps.setString(4, DateUtils.formatDate(DateUtils.getCurrentDate(), DBConstants.AMO_FROM_DATE_FORMAT,
								DBConstants.DB_DATE_FORMAT));
						ps.addBatch();
	
						String sTomorrowStartDateTime = DateUtils.getTomorrowDate(DBConstants.DB_DATE_FORMAT) + " "
								+ "00:00:00";
						String sTomorrowEndDateTime = DateUtils.getTomorrowDate(DBConstants.DB_DATE_FORMAT) + " " + sEndTime
								+ ":00";
						
						sMonitEndDateTime = sTomorrowEndDateTime;
						
						ps.setString(1, sExchange);
						ps.setString(2, sTomorrowStartDateTime);
						ps.setString(3, sTomorrowEndDateTime);
						ps.setString(4, DateUtils.getTomorrowDate(DBConstants.DB_DATE_FORMAT));
						ps.addBatch();
					}
					
					long beforeExecution = System.currentTimeMillis();
					int[] rows1 = ps.executeBatch();
					long afterExecution = System.currentTimeMillis();
					log.info("total time taken: " + (afterExecution - beforeExecution));
					log.info("Total rows inserted :: " + rows1.length);
					Monitor.markSuccess(String.format("Updated successfully, " + "StartTime: %s, " + 
					"EndTime: %s", sMonitStartDateTime, sMonitEndDateTime));
				}catch(Exception e){
					Monitor.markCritical("Exception : "+e.getMessage());
					log.info("Error :: " + e.getMessage());
				} finally {
					Helper.closeStatement(ps);
					Helper.closeConnection(conn);
				}
				
			}
			
		} catch (Exception e) {
			log.warn(e);
		} 

	}

}