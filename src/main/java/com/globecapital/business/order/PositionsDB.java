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

public class PositionsDB {
	
	private static Logger log = Logger.getLogger(PositionsDB.class);
	public static String toCheckFoCombinedPositionEntry(String clientId) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		String positionsObj = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Query = "+DBQueryConstants.GET_FO_POSITIONS);
			st = conn.prepareStatement(DBQueryConstants.GET_FO_POSITIONS);
			st.setString(1,clientId);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				positionsObj= rs.getString(DeviceConstants.POSITIONS_);
				log.info("POSITIONS retrieved Succesfully");
			}
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		return positionsObj;
	}
	
	public static String updatePositionsDB(String clientId, String positionsObj) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Query "+DBQueryConstants.INSERT_FO_POSITIONS);
			st = conn.prepareStatement(DBQueryConstants.INSERT_FO_POSITIONS);
			st.setString(1,clientId);
			st.setString(2, positionsObj);
			int rs = st.executeUpdate();
			log.debug("Inserted In to FO_COMBINED_POSITIONS ");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		return positionsObj;
	}
	
}
