package com.globecapital.business.watchlist;

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
import com.globecapital.symbology.SymbolMap;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class PredefinedWatchList {

	private static Logger log = Logger.getLogger(SymbolMap.class);
	private static Map<String, String> watchListMap = new LinkedHashMap<String, String>();

	public static void loadWatchlists() throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			
			
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.GET_WATCHLISTS);
			res = ps.executeQuery();
			
			while (res.next()) {
				watchListMap.put(res.getString(DBConstants.INDEX_NAME).toUpperCase(), res.getString(DBConstants.INDEX_CODE));
			}
			log.info("LoadAllCaches: Watchlist : PredefinedWatchlist Size "+watchListMap.size());
		} catch (Exception e) {
			log.error("LoadAllCaches: Watchlist : Error while fetching predefined watchlists " + e);
			
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}

	public static Map<String, String> getPredefinedWatchlist() throws SQLException {
		if (watchListMap.size() == 0)
			try {
				loadWatchlists();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return watchListMap;
	}

	public static boolean isPredefinedWatch(String watchlistName, String watchlistId) {
		String indexProfileId = watchListMap.get(watchlistName);
		if (indexProfileId != null && indexProfileId.equalsIgnoreCase(watchlistId))
			return true;
		else
			return false;

	}

}
