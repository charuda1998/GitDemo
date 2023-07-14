package com.globecapital.jobs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.news.NewsFeed;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.NewsDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class GetNews {
	
	private static Logger log;
	
	public static void main(String[] args) throws Exception {

		long beforeExecution = System.currentTimeMillis();
		
		String config_file = args[0];
		String log_file = args[1];
		
		try {
			Properties JSLogProperties = new Properties();
			AppConfig.loadFile(config_file);
			FileInputStream stream = new FileInputStream(log_file);
			JSLogProperties.load(stream);
			stream.close();
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(GetNews.class);
			log.info("#############################################################################");
			log.info("##### JOB NAME : News Dump - BEGINS");
			log.info("##### TIME : " + DateUtils.getCurrentDateTime(DeviceConstants.OPTIONS_DATE_FORMAT_1));
		} catch (ConfigurationException e) {
			System.out.println("Exception while configuring the JSLOG properties");
		}

		Monitor.setJobsBeans(Monitor.LOAD_NEWS);
		
		NewsDBPool.initDataSource(AppConfig.getProperties());
		
		Connection con = null;
		PreparedStatement ps = null;
		String query = "";
	
		JSONArray newsArray = NewsFeed.getNewsFeed();

		try {
//			String date = newsArray.getJSONObject(0).getString("date");
//			JSONArray news = newsArray.getJSONObject(0).getJSONArray("news");

			con = NewsDBPool.getInstance().getConnection();
			query = "INSERT INTO news(uniqid,description,time,category,symbol,date,created_at,newsWeightage, news_time) VALUES(";

			int j=1;
			for(int i=0; i<newsArray.length(); i++) {

				query += "?,?,?,?,?,?,NOW(),?,?),("; 
				
			}
			query = query.substring(0, query.length()-2);
			
			query+=" ON DUPLICATE KEY UPDATE description = VALUES(description), time = VALUES(time), category = VALUES(category), date = VALUES(date), symbol = VALUES(symbol), newsWeightage = VALUES(newsWeightage), news_time = VALUES(news_time), updated_at = NOW()";
			log.info("Query :: " + query);
			ps = con.prepareStatement(query);
			for(int i=0; i<newsArray.length(); i++) {
				JSONObject newsObj = newsArray.getJSONObject(i);
				ps.setString(j++,newsObj.getString(DeviceConstants.GUID));
				ps.setString(j++,newsObj.getString(DeviceConstants.NEWS_DESCRIPTION));
				ps.setString(j++,newsObj.getString(DeviceConstants.TIME));
				ps.setString(j++,newsObj.getString(DeviceConstants.CATEGORY));
				ps.setString(j++,newsObj.optString(DeviceConstants.SYMBOL));
				ps.setString(j++,newsObj.getString(DeviceConstants.DATE_LS));
				ps.setString(j++,newsObj.getString(DeviceConstants.NEWS_WEIGHTAGE));
				String dateTime = newsObj.getString(DeviceConstants.DATE_LS) + " " + newsObj.getString(DeviceConstants.TIME);
				String newsTime = DateUtils.formatDate(dateTime, DBConstants.NEWS_DATE, DBConstants.DB_DATE_TIME_FORMAT);
				ps.setString(j++,newsTime);
				
			}
			int rows = ps.executeUpdate();

			
			log.info("************************ STATS ************************");
			log.info("Total no of records received : " + newsArray.length());
			log.info("Total no of records inserted : " + rows);
			log.info("*******************************************************");
			
			Monitor.markSuccess(String.format("No of Rows received : %d, No of rows inserted : %d", newsArray.length(), rows));
		} catch (Exception e) {
			Monitor.markCritical("Exception : "+e.getMessage());
			log.info("Exception :: " + e);
		} finally {

			Helper.closeStatement(ps);
			Helper.closeConnection(con);
		}	

		long afterExecution = System.currentTimeMillis();
		
		log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
		log.info("##### JOB NAME : News Dump - ENDS");
		log.info("#############################################################################");
	} 

}
