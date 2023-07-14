package com.globecapital.business.market;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.symbology.SymbolRow;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Indices {

	private static Logger log = Logger.getLogger(Indices.class);
	private static Map<String, SymbolRow>indicesMap = new LinkedHashMap<String, SymbolRow>();
	
	public static void loadIndices() throws SQLException {
		Connection conn = null;
		PreparedStatement ps =null;
		ResultSet res = null;
		try {
			
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.GET_INDICES);
			res = ps.executeQuery();
			
			if (!res.isBeforeFirst()) {
				log.debug("No Symbols Found");
				log.info("No Symbols Found");
			}
			
			while (res.next()) {
				SymbolRow row = new SymbolRow(res);
				Indices.indicesMap.put(res.getString(DBConstants.TOKEN_SEGMENT), row);
			}
			log.info("LoadAllCaches: Indices : indicesMap "+indicesMap.size());
		} catch (Exception e) {
			log.error(e);
		
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}
	
	public static boolean isValidIndex(String token) {
		return indicesMap.containsKey(token);
	}
	
	public static SymbolRow getSymbolRow(String token) {
		SymbolRow index = new SymbolRow();
		index = Indices.indicesMap.get(token);
		if(index == null) {
			log.warn("Not able to find the index from indicesMap > "+token);
		}
		return index;
	}
	
}
