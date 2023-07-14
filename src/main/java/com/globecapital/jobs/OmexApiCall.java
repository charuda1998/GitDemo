package com.globecapital.jobs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONObject;

import com.globecapital.api.ft.OMEX.AuthenticateAPI;
import com.globecapital.api.ft.OMEX.AuthenticateRequest;
import com.globecapital.api.ft.OMEX.RegisterAPI;
import com.globecapital.api.ft.OMEX.RegisterRequest;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class OmexApiCall {
	private static Logger log;
//	private static Logger log = Logger.getLogger(OmexApiCall.class);
	
	public static void main(String[] args) {
		long beforeExecution = System.currentTimeMillis();
		System.out.println("Inside main");
		String config_file = args[0];
		try {
			Properties JSLogProperties = new Properties();
			AppConfig.loadFile(config_file);
			FileInputStream stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(OmexApiCall.class);
			log.info("#############################################################################");
			log.info("##### JOB NAME : OMEX API CALL - BEGINS");
			GCDBPool.initDataSource(AppConfig.getProperties());
			GCDBPool.getInstance().getConnection();
			
//			callApi();
			
//			Timer t = new Timer();
//		    Heartbeat heartbeat = new Heartbeat();
//		    t.scheduleAtFixedRate(heartbeat,1000, 180000);
		    
			ApiRegistration apiRegistration=new ApiRegistration();
			apiRegistration.checkConnection();
			stream.close();
		
			
		} catch (Exception e) {
			System.out.println("error"+e.getMessage());
			log.error("ERROR :"+e.getMessage());
			//log.info("error");
			
			e.printStackTrace();
		}
		
		
		
		
		
		long afterExecution = System.currentTimeMillis();
		
	    log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
		log.info("#############################################################################");
		
		
	}

	
	public static void callApi() {
		AuthenticateRequest authRequest;
		RegisterRequest registerRequest;
			try {
				System.out.println("calling API ");
				authRequest = new AuthenticateRequest();
				authRequest.setApiKey();
				authRequest.setSecretKey();
				long val=System.currentTimeMillis();
				String id=Long.toString(val);
				System.out.println(id);
				authRequest.setRequestId(id);
		

				System.out.println("Auth req :"+ authRequest.toString());
				log.info("Auth req :"+ authRequest.toString());
				
				AuthenticateAPI authApi=new AuthenticateAPI();

				String authResponse=authApi.post(authRequest);
				System.out.println("Authentication Response :"+authResponse);

				log.info("Authentication Response :"+authResponse);
				
				
				
				JSONObject js=new JSONObject(authResponse);
				String authStatus=js.getString(FTConstants.JSTATUS_CODE);
				if(authStatus.equals("-1")) {
					System.out.println("ERROR :"+js.getString("jErrorString"));
					log.error(js.getString("jErrorString"));
				}
				else {
				String token=js.getString(FTConstants.JTOKEN);
				
				
				log.info("tOKEN  :"+token);
				System.out.println("Token :"+token);
				registerRequest=new RegisterRequest();
				registerRequest.setReqId(Long.toString(System.currentTimeMillis()));
				registerRequest.setToken(token);
				registerRequest.setConnMode();
				registerRequest.setUrl();

				System.out.println("Register Request :"+registerRequest.toString());
				log.info("Register Request :"+registerRequest.toString());
				
				RegisterAPI regApi=new RegisterAPI();
				String regResponse=regApi.post(registerRequest);
				log.info("Register Response :"+regResponse);
				System.out.println("Register Response :"+regResponse);
				JSONObject js1=new JSONObject(authResponse);
				String status=js1.getString(FTConstants.JSTATUS_CODE);
				
				if(status.equals("1")) {
					Date date=new Date(System.currentTimeMillis());
					String time=String.valueOf(System.currentTimeMillis());
					addToDb(time,date);
					
				}
				
			}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				log.info("Auth Error :"+e.getMessage());
				
				e.printStackTrace();
			}
	}


	public static  boolean addToDb(String currentTimeMillis,Date date) {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			st = conn.prepareStatement(DBQueryConstants.INSERT_HEARTBEAT);
			st.setDate(1, date);
			st.setString(2, currentTimeMillis);
			st.setString(3, currentTimeMillis);
			st.executeUpdate();
//			ResultSet rs = st.executeQuery();
//			if (rs.next())
				return true;

//			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(e.getMessage());
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
			return false;
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		

		
	}
	
	
	
}

