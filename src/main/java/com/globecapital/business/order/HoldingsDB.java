package com.globecapital.business.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.GCDBPool;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class HoldingsDB {
	private static Logger log = Logger.getLogger(HoldingsDB.class);
	public static String toCheckHoldingEntry(String clientId) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		String holdingsObj = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Query = "+DBQueryConstants.GET_CLIENT_HOLDINGS);
			st = conn.prepareStatement(DBQueryConstants.GET_CLIENT_HOLDINGS);
			st.setString(1,clientId);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				holdingsObj= rs.getString(DeviceConstants.HOLDINGS_);
				log.info("Holdings retrieved Succesfully");
			}
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		return holdingsObj;
	}
	
	public static String updateHoldingsDB(String clientId, String holdingsObj) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Query "+DBQueryConstants.INSERT_CLIENT_HOLDINGS);
			st = conn.prepareStatement(DBQueryConstants.INSERT_CLIENT_HOLDINGS);
			st.setString(1,clientId);
			st.setString(2, holdingsObj);
			int rs = st.executeUpdate();
			log.debug("Inserted In to CLIENT_HOLDINGS");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		return holdingsObj;
	}
	

}
