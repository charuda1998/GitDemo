package com.globecapital.business.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class AMODetails {
	
	private static Logger log = Logger.getLogger(AMODetails.class);
	public static Map<String, JSONObject> mapExchangeDateToAMOTime = new LinkedHashMap<>();

	public static void loadAMODetails() throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		try {

		
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.GET_AMO_DETAILS);
			res = ps.executeQuery();
			
			
			while (res.next()) {
				String sKey = res.getString(DBConstants.AMO_EXCHANGE) + "_" + 
						DateUtils.getStringDate(res.getDate(DBConstants.AMO_DATE));
				JSONObject obj = new JSONObject();
				obj.put(OrderConstants.START_TIME, 
						DateUtils.getStringDateTime(res.getTimestamp(DBConstants.START_TIME)));
				obj.put(OrderConstants.END_TIME, 
						DateUtils.getStringDateTime(res.getTimestamp(DBConstants.END_TIME)));
				mapExchangeDateToAMOTime.put(sKey, obj);
			}
			log.info("LoadAllCaches: AMODetails : AMO_DETAILS "+mapExchangeDateToAMOTime.size());
		} catch (Exception e) {
			log.error("LoadAllCaches: AMODetails : Error while fetching AMO details " + e);
			
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}

	public static boolean isAMOOrder(String sExch) throws JSONException, ParseException {
		
		boolean isAMO = false;
		//return isAMO;
		
		String sTodayDate = DateUtils.formatDate(DateUtils.getCurrentDate(), DBConstants.AMO_FROM_DATE_FORMAT,
				DBConstants.DB_DATE_FORMAT);
		
		
		if(mapExchangeDateToAMOTime.containsKey(sExch + "_" + sTodayDate))
		{
			JSONObject obj = mapExchangeDateToAMOTime.get(sExch + "_" + sTodayDate);
			long lStartTime = DateUtils.getDate(obj.getString(OrderConstants.START_TIME), 
					DBConstants.DB_DATE_TIME_FORMAT).getTime();
			long lEndTime = DateUtils.getDate(obj.getString(OrderConstants.END_TIME), 
					DBConstants.DB_DATE_TIME_FORMAT).getTime();
			
			long currentTime = DateUtils.getDate(DateUtils.getCurrentDateTime(DBConstants.DB_DATE_TIME_FORMAT), 
					DBConstants.DB_DATE_TIME_FORMAT).getTime();
			
			if(currentTime >= lStartTime && currentTime <= lEndTime)
					isAMO = true;
		}
		else
			isAMO = false;
		
		return isAMO;
		
	}

}
