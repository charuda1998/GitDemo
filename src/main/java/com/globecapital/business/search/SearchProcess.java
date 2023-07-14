package com.globecapital.business.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;

import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolRow;
import com.msf.utils.helper.Helper;

public class SearchProcess {

	public static JSONArray getEquitySymbols(String search, Session session)
			throws SQLException, GCException, JSONException {

		Connection conn = null;
		ResultSet res = null;
		PreparedStatement ps = null;

		JSONArray symlist = new JSONArray();

		try {

			conn = GCDBPool.getInstance().getConnection();
			search = search + "%";
			String searchQuery = DBQueryConstants.SYMBOL_SEARCH_ALL;

			ps = conn.prepareStatement(searchQuery);
			ps.setString(1, search);
			ps.setString(2, search);

			res = ps.executeQuery();
			if (!res.isBeforeFirst())
				return symlist;

			while (res.next()) {

				SymbolRow row = new SymbolRow(res);

				row.put(SymbolConstants.DISPLAY_SYMBOL, res.getString(DBConstants.SYMBOL_NAME));
				row.put(SymbolConstants.DISPLAY_SYMBOL_DETAILS, res.getString(DBConstants.EXCHANGE_NAME));
				symlist.put(row);

			}

			return symlist;
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

	}

}
