package com.globecapital.services.omex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.globecapital.constants.RedisConstants;
import com.globecapital.db.RedisPool;
import com.globecapital.jobs.OmexApiCall;
import com.msf.log.Logger;

public class GetOMEXMessageResponse extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GetOMEXMessageResponse.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
		    String messageType = "";
			InputStream inp = req.getInputStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(inp, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);
			resp.setHeader("Content-type", "text/html");
			log.info("OMEX Response :" + responseStrBuilder);
			
			JSONObject orderResp = new JSONObject();
			if(!responseStrBuilder.toString().isEmpty())
				orderResp = new JSONObject(responseStrBuilder.toString());
			
			if (orderResp.has("MessageType")) {
				messageType = orderResp.getString("MessageType");
				log.info("MESSAGE TYPE OMEX :" + messageType);
				
				Date date = new Date(System.currentTimeMillis());
				String time = String.valueOf(System.currentTimeMillis());
				if (messageType.equals("HEARTBEAT")) {
					boolean result = OmexApiCall.addToDb(time, date);
					if (result) {
						log.info("OMEX :Successfully updated database for heartbeat ");
					}
				}
				if (messageType.equals("TRD_MSG") || messageType.equals("POS_CONV")) {
					
					String user_id = orderResp.getString("UCC");
					
					String positionVal = "{}";
					
					insertIntoRedis(user_id + "_" + RedisConstants.POSITIONS, "true", positionVal, positionVal);
					insertIntoRedis(user_id + "_" + RedisConstants.HOLDINGS, "true", positionVal, positionVal);
				}
				
			}
		}catch(Exception ex){
			log.error("Exception occurred while reading data from webhook :",ex);
		}
	}

	public static void insertIntoRedis(String key, String isRefreshRequired, String todays, String derivatives) {
		RedisPool redisPool = new RedisPool();

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		java.util.Date date = new java.util.Date(); 
	    String time=formatter.format(date);
		
		JSONObject orderData = new JSONObject();
		orderData.put(RedisConstants.IS_REFRESH_REQUIRED, isRefreshRequired);
		orderData.put(RedisConstants.TODAYS, todays);
		orderData.put(RedisConstants.DERIVATIVES, derivatives);
		orderData.put(RedisConstants.UPDATED_TIME, time);
		try {
			redisPool.setValues(key, orderData.toString());
		}catch(Exception ex) {
			log.error(ex);
		}
	}

}
