package com.globecapital.jobs;

import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.market.IndicesAPI;
import com.globecapital.api.ft.market.IndicesRequest;
import com.globecapital.api.ft.user.LoginAPI;
import com.globecapital.api.ft.user.LoginRequest;
import com.globecapital.api.ft.user.LoginResponse;
import com.globecapital.api.ft.user.LoginResponseObject;
import com.globecapital.config.AppConfig;
import com.globecapital.config.IndicesMapping;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.GCDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.GCUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class FTIndicesDump {
	
	private static Logger log;
	private static JsonObject config = new JsonObject();
	private static int derivativeWeightage = 0;
	
	public static void main(String[] args) throws Exception {
		
		long beforeExecution = System.currentTimeMillis();

		String config_file = args[0];
		String indices_config_file = args[1];
		
		try {
			Properties JSLogProperties = new Properties();
			FileInputStream stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			stream.close();
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(FTIndicesDump.class);
			log.info("#############################################################################");
			log.info("##### JOB NAME : FTIndices Dump - BEGINS");
			log.info("##### TIME : " + DateUtils.getCurrentDateTime(DeviceConstants.OPTIONS_DATE_FORMAT_1));
		} catch (ConfigurationException e) {
			System.out.println("Exception while configuring the JSLOG properties");
		}
		
		try {
			AppConfig.loadFile(config_file);
			IndicesMapping.loadFile(indices_config_file);
		} catch (Exception e) {
			log.error("Cannot load  config properties %s", e);
			System.exit(1);
		}
		
		log.info("************************ STATS ************************");

		GCDBPool.initDataSource(AppConfig.getProperties());
		
		loadIndices();
		
		loadIndicesForMarket();
		
		deleteExpiredContractsFromDB();
		
		log.info("*******************************************************");

		long afterExecution = System.currentTimeMillis();
		
		log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
		log.info("##### JOB NAME : FTIndices Dump - ENDS");
		log.info("#############################################################################");
	}
	
	private static LoginResponseObject login(String userID, String sPassword, String sNewPwd) throws Exception {
		
		String newPwd = "", newEncryptedPwd = "";
		//String password = Validation.passwordValidation(sPassword);
		String sEncryptedPwd = GCUtils.encryptPassword(sPassword);
					
		LoginRequest loginReq = new LoginRequest();
		
		loginReq.setUserID(userID);
		loginReq.setPassword(sEncryptedPwd);
		loginReq.setForceLoginTag(true);
		
		if (!sNewPwd.isEmpty()) {
			//newPwd = Validation.passwordValidation(sNewPwd);
			newEncryptedPwd = GCUtils.encryptPassword(sNewPwd);
		}
		
		if (!newEncryptedPwd.isEmpty())
			loginReq.setNewPassword(newEncryptedPwd);
		else
			loginReq.setNewPassword("");
		
		LoginAPI loginAPI = new LoginAPI();
		LoginResponse loginResp = loginAPI.post(loginReq, LoginResponse.class, "Daily job","NetNetLogin");

		LoginResponseObject loginObj = loginResp.getResponseObject();
		
		Integer userStatus = loginObj.getLogonStatus();

		if (Integer.compare(userStatus, FTConstants.USER_PWD_EXPIRED) == 0) {

			String newPasswordGenerated = getNewPassword(sPassword);
			loginObj = login(userID, sPassword, newPasswordGenerated);
			updateCredentials(userID, newPasswordGenerated);
		}
		
		return loginObj;
	}
	
	private static JSONObject getCredentials() throws SQLException {
		
		JSONObject obj = new JSONObject();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String query = DBQueryConstants.GET_JOB_INFO;
//		log.info("Query :: " + query);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, "FTIndicesDump");
			
			res = ps.executeQuery();
			while(res.next()) {
			obj.put(DBConstants.USERID, res.getString(DBConstants.USERID));
			obj.put(DBConstants.PASSWORD, res.getString(DBConstants.PASSWORD));
			obj.put(DBConstants.CONFIG, res.getString(DBConstants.CONFIG));
			}
		} catch (Exception e) {
			log.debug("Error :: " + e);
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return obj;
	}
	
	private static void updateCredentials(String userID, String password) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		String query = DBQueryConstants.UPDATE_CREDENTIALS;

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, password);
			ps.setString(2, userID);
			ps.setString(3, "FTIndicesDump");
			
			ps.executeUpdate();
						
		} catch (Exception e) {
			log.debug("Error :: " + e);
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}
	private static void loadIndices() throws Exception {
		
		JSONObject credentials = getCredentials();
		
		Gson gson = new Gson();
		config = gson.fromJson(credentials.getString(DBConstants.CONFIG), JsonObject.class);
		JsonArray segments = config.getAsJsonArray("equities");	
		JsonArray equityAllArr = config.getAsJsonArray("equity_all_index");	
		JsonArray equityArr = config.getAsJsonArray("equity_index");	
		JsonArray derivativeArr = config.getAsJsonArray("derivative_index");	
		
		Type type = new TypeToken<List<String>>(){}.getType();

	    List<String> equityAll = gson.fromJson(equityAllArr, type);
	    List<String> equity = gson.fromJson(equityArr, type);
	    List<String> derivative = gson.fromJson(derivativeArr, type);

		IndicesRequest ftRequest = new IndicesRequest();
		
		
		Connection conn = null;
//		Connection conn_scripmaster = null;
		PreparedStatement ps = null;
//		PreparedStatement ps_scripmaster = null;
		String query = DBQueryConstants.INSERT_FT_INDICES;
//		String scripmaster_query = DBQueryConstants.INSERT_SCRIPMASTER_INDICES;
//		log.info("Query :: " + query);

		try {
			for (int i=0; i<segments.size(); i++) {
				
				if(segments.get(i).getAsInt() == 1)
					Monitor.setJobsBeans(Monitor.INDICES_NSE);
				else if(segments.get(i).getAsInt() == 3)
					Monitor.setJobsBeans(Monitor.INDICES_BSE);
				
				String userID = credentials.getString(DBConstants.USERID);
				LoginResponseObject loginObj = login( userID,
						credentials.getString(DBConstants.PASSWORD), "");
				
				if(loginObj.getLogonStatus() != FTConstants.USER_LOGIN_SUCCESS_1) {
					Monitor.markCritical("API error : "+loginObj.getErrStr());
					log.info("Error :: "+loginObj.getErrStr());
					continue;
				}
				
				ftRequest.setJSession(GCUtils.encryptPassword(loginObj.getSessionId()));
				ftRequest.setUserID(userID);
				ftRequest.setGroupId(loginObj.getGroupId());
				ftRequest.setMKtSegId(segments.get(i).getAsInt());
				ftRequest.setRecordCount("");
				ftRequest.toString();
//				log.info("API Request :: "+ftRequest);
				IndicesAPI indicesAPI = new IndicesAPI();
				String indicesResponse = indicesAPI.post(ftRequest);
				JSONObject indicesObject = new JSONObject(indicesResponse);
				JSONArray symbolResp = indicesObject.getJSONArray("ResponseObject");
				try {
					conn = GCDBPool.getInstance().getConnection();
//					conn_scripmaster = GCDBPool.getInstance().getConnection();
					ps = conn.prepareStatement(query);
//					ps_scripmaster = conn_scripmaster.prepareStatement(scripmaster_query);
					for (int j = 0; j < symbolResp.length(); j++) {

						JSONObject obj = symbolResp.getJSONObject(j);

						String nTokenSegment = obj.getInt(DBConstants.TOKEN) + "_" + obj.getInt(DBConstants.SEGMENT);
					
						if( !(equity.contains(obj.getString(DBConstants.SYMBOL_NAME))
								|| equityAll.contains(obj.getString(DBConstants.SYMBOL_NAME))
								|| derivative.contains(obj.getString(DBConstants.SYMBOL_NAME))))
							continue;
						
//						ps_scripmaster = formScripMasterInsertionBatch(obj, ps_scripmaster, nTokenSegment);
						ps.setString(1, nTokenSegment);
						String symbol = obj.getString(DBConstants.SYMBOL_NAME);
						ps.setString(2, symbol);
						ps.setString(3, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
						ps.setString(4, String.valueOf(obj.getInt(DBConstants.TOKEN)));
						ps.setString(5, String.valueOf(obj.getInt(DBConstants.SEGMENT)));
						ps.setString(6, obj.getString(DBConstants.S_EXCHANGE));
						
						ps.setString(7, "");
						ps.setString(8, getPrecision(String.valueOf(obj.getInt(DBConstants.SEGMENT))));
						ps.setString(9, "1");
						ps.setString(10, "100");
						
						ps.setString(11, obj.getString(DBConstants.S_EXCHANGE));
						ps.setString(12, "IDX");
						ps.setString(13, "");
						ps.setString(14, "");
						ps.setString(15, "");
						ps.setString(16, "");
						ps.setString(17, "");
						ps.setString(18, "");
						ps.setString(19, "");
						ps.setString(20, symbol + "_" + obj.getString(DBConstants.S_EXCHANGE));
						ps.setString(21, obj.getString(DBConstants.S_EXCHANGE));
						ps.setString(22, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
						
						String mappingSymbolUniq = "IDX" + "_"+String.valueOf(obj.getInt(DBConstants.TOKEN))+"_" + obj.getString(DBConstants.S_EXCHANGE);
						if(equity.contains(obj.getString(DBConstants.SYMBOL_NAME))) {
							ps.setString(23,  "1");
							ps.setString(24, DeviceConstants.EQUITY);
							int weight = equity.indexOf(symbol)+1;
							ps.setString(25, String.valueOf(weight));
							ps.setString(26, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
							ps.setString(27,IndicesMapping.optValue(nTokenSegment,mappingSymbolUniq));
							ps.addBatch();
						} else if(equityAll.contains(obj.getString(DBConstants.SYMBOL_NAME))) {
							ps.setString(23,  "0");
							ps.setString(24, DeviceConstants.EQUITY);
							ps.setString(25, "0");
							ps.setString(26, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
							ps.setString(27,IndicesMapping.optValue(nTokenSegment,mappingSymbolUniq));
							ps.addBatch();
						} 
//						ps_scripmaster.addBatch();
						if(derivative.contains(obj.getString(DBConstants.SYMBOL_NAME))) {
							ps.setString(23, "1");
							ps.setString(24, DeviceConstants.DERIVATIVE);
							
							JsonArray derivativeIndex = config.getAsJsonArray("derivative_scrip_index");
							int scripSize = derivativeIndex.size();
							int weight = derivative.indexOf(symbol)+scripSize+1;
							ps.setString(25, String.valueOf(weight));
							ps.setString(26, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
							ps.setString(27,IndicesMapping.optValue(nTokenSegment,mappingSymbolUniq));
//							ps.setString(25, "0");
							ps.addBatch();
						}  
						
						
					}
					int[] rows = ps.executeBatch();
//					int scripmasterRows[] = ps_scripmaster.executeBatch();
					
					log.info("Total no of records received for " + ExchangeSegment.getExchangeName(segments.get(i).getAsString()) + " : " + symbolResp.length());
					log.info("Total no of records inserted for " + ExchangeSegment.getExchangeName(segments.get(i).getAsString()) + " : " + rows.length );
//					log.info("Total no of Scripmaster records inserted for " + ExchangeSegment.getExchangeName(segments.get(i).getAsString()) + " : " + scripmasterRows.length );
					
					Monitor.markSuccess(String.format("No of Rows received : %d, No of Rows inserted : %d", symbolResp.length(), rows.length));
					
				} catch (Exception e) {
					log.debug("Error :: " + e);
					Monitor.markCritical("SQL Exception"+e.getMessage());
				} finally {
					Helper.closeStatement(ps);
					Helper.closeConnection(conn);
				}
			}
		} catch (Exception e) {
			Monitor.setJobsBeans(Monitor.INDICES_NSE);
			Monitor.markCritical("Exception : "+e.getMessage());
			Monitor.setJobsBeans(Monitor.INDICES_BSE);
			Monitor.markCritical("Exception : "+e.getMessage());
			log.debug("Error :: " + e);
		}
	}

	private static void loadIndicesForMarket() throws Exception {
		
		JsonArray derivativeIndex = config.getAsJsonArray("derivative_scrip_index");
		loadIndicesFromScripmaster(derivativeIndex, ExchangeSegment.NFO_SEGMENT_ID);
		
		JsonArray currencyIndex = config.getAsJsonArray("currency_scrip_index");
		loadIndicesFromScripmaster(currencyIndex, ExchangeSegment.NSECDS_SEGMENT_ID);
		
		JsonArray commodityIndex =config.getAsJsonArray("commodity_scrip_index");
		loadIndicesFromScripmaster(commodityIndex, ExchangeSegment.MCX_SEGMENT_ID);		
		
	}
	
	private static void loadIndicesFromScripmaster(JsonArray symbolArray, String mrkt) throws Exception {
		
		if(mrkt.equals("2"))
			Monitor.setJobsBeans(Monitor.INDICES_NFO);
		else if(mrkt.equals("13"))
			Monitor.setJobsBeans(Monitor.INDICES_NSECDS);
		else if(mrkt.equals("5"))
			Monitor.setJobsBeans(Monitor.INDICES_MCX);
		
		Connection conn = null;
		PreparedStatement ps = null;
		String query = DBQueryConstants.INSERT_FT_INDICES_MARKET;
//		log.info("Query :: " + query);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);

			for(int i =0; i<symbolArray.size(); i++) {
			
			ps.setString(1, symbolArray.get(i).getAsString());
			ps.setString(2, mrkt);
			if(mrkt.equalsIgnoreCase(ExchangeSegment.NFO_SEGMENT_ID)) {
				ps.setString(3, DeviceConstants.DERIVATIVE);
				ps.setString(4, String.valueOf(i+1));
			} else if(mrkt.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID)) {
				ps.setString(3, DeviceConstants.CURRENCY);
				ps.setString(4, String.valueOf(i+1));
			} else if(mrkt.equalsIgnoreCase(ExchangeSegment.MCX_SEGMENT_ID)) {
				ps.setString(3, DeviceConstants.COMMODITY);
				ps.setString(4, String.valueOf(i+1));
			}
			ps.addBatch();
		}
		int[] rows = ps.executeBatch();
		
		log.info("Total no of records received for " + ExchangeSegment.getExchangeName(mrkt) + " : " + symbolArray.size());
		log.info("Total no of records inserted for " + ExchangeSegment.getExchangeName(mrkt) + " : " + rows.length );
		
		Monitor.markSuccess(String.format("No of Rows received : %d, No of Rows inserted : %d", symbolArray.size(), rows.length));
		
		
		} catch (Exception e) {
			Monitor.markCritical("Exception : "+e.getMessage());
			log.debug("Error  :: " + e);
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}
	
	public static void deleteExpiredContractsFromDB() throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;

		String query = DBQueryConstants.DELETE_EXPIRED_INDICES;
//		log.info("Query :: " + query);
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			int rows = ps.executeUpdate();
			log.info("Deleted Expired rows :: " + rows);
		} catch (Exception e) {
			log.info("Error :: " + e);
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

	}

	public static String getNewPassword(String oldPasswd)
    {
        String[] dataList = oldPasswd.split("@");
        int state = 0;
        String first = "", second = "";
        for (String str : dataList)
        {
            if (state == 0)
            {
                first = str;
            }
            else if (state == 1)
            {
                second = str;
            }
            state++;
        }

        int x = 1000;
        if (second.trim().length() > 0)
        {
            x = Integer.parseInt(second) % 1000;
            x++;
        }
        return first + "@" + Integer.toString(x);
    }
	
	public static String getPrecision(String marketsegment) throws ClassNotFoundException {

		if (marketsegment.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID)
				|| marketsegment.equalsIgnoreCase(ExchangeSegment.BSECDS_SEGMENT_ID))

			return OrderConstants.PRECISION_4;

		else
			return OrderConstants.PRECISION_2;

	}
	
	private static PreparedStatement formScripMasterInsertionBatch(JSONObject obj, PreparedStatement ps, String nTokenSegment) throws SQLException, ClassNotFoundException, JSONException {
		ps.setString(1, nTokenSegment);
		String symbol = obj.getString(DBConstants.SYMBOL_NAME);
		ps.setString(2, symbol);
		ps.setString(3, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
		ps.setString(4, String.valueOf(obj.getInt(DBConstants.TOKEN)));
		ps.setString(5, String.valueOf(obj.getInt(DBConstants.SEGMENT)));
		ps.setString(6, "");
		ps.setString(7, getPrecision(String.valueOf(obj.getInt(DBConstants.SEGMENT))));
		ps.setString(8, "1");
		ps.setString(9, "100");
		ps.setString(10, obj.getString(DBConstants.S_EXCHANGE));
		ps.setString(11, "IDX");
		ps.setString(12, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
		ps.setString(13, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
		ps.setString(14, obj.getString(DBConstants.SECURITY_DESC).toUpperCase());
		String mappingSymbolUniq = "IDX" + "_"+String.valueOf(obj.getInt(DBConstants.TOKEN))+"_" + obj.getString(DBConstants.S_EXCHANGE);
		ps.setString(15,IndicesMapping.optValue(nTokenSegment,mappingSymbolUniq));
		return ps;
	}
}
