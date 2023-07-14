package com.globecapital.business.quote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.json.JSONObject;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.QuoteDBPool;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class OMDFQuote {

	private static Logger log = Logger.getLogger(OMDFQuote.class);

	public static QuoteDetails getLTP(String mappingSymbolUniqDesc, String sTokenMktSegID) throws SQLException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		String query = "";
		String sMktSegID = sTokenMktSegID.split("_")[1];
		
		QuoteDetails quoteDetails = new QuoteDetails();

		if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
			query = DBQueryConstants.GET_NSE_LTP_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			query = DBQueryConstants.GET_NFO_LTP_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
			query = DBQueryConstants.GET_BSE_LTP_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
			query = DBQueryConstants.GET_MCX_LTP_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			query = DBQueryConstants.GET_NCDEX_LTP_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_NSECDS_LTP_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_BSECDS_LTP_OMDF;
		
		log.info("Getting data from "+DBConstants.OMDF_QUOTE);
		log.debug(DBConstants.OMDF_QUOTE +" :: "+ query);

		
		try {
			conn = QuoteDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, sTokenMktSegID);
			
			res = ps.executeQuery();

			if (res.next()) {
				String sLTP = String.valueOf(res.getDouble(DBConstants.LAST_PRICE));
				quoteDetails.sLTP = res.wasNull() ? "--" : sLTP;
				String sChange = String.valueOf(res.getDouble(DBConstants.CHANGE));
				quoteDetails.sChange = res.wasNull() ? "--" : sChange;
				String sChangePercent = String.valueOf(res.getDouble(DBConstants.CHANGE_PER));
				quoteDetails.sChangePercent = res.wasNull() ? "--" : sChangePercent;
				String sPrevClose = String.valueOf(res.getDouble(DBConstants.CLOSE_PRICE));
				quoteDetails.sPreviousClose = res.wasNull() ? "--" : sPrevClose;
				String sOpenPrice = String.valueOf(res.getDouble(DBConstants.OPEN_PRICE));
				quoteDetails.sOpenPrice = res.wasNull() ? "--" : sOpenPrice;

			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return quoteDetails;

	}

	public static QuoteDetails getLTPUsingSymbolUniqDesc(String sSymbolUniqDesc) throws SQLException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		String query = null;
		QuoteDetails quoteDetails = new QuoteDetails();
		
		SymbolRow symRow = SymbolMap.getSymbolUniqDescRow(sSymbolUniqDesc);

		String[] symbolUniqDesc = sSymbolUniqDesc.split("_");

		String sExchange = symbolUniqDesc[1];

		if (sExchange.equals(ExchangeSegment.NSE))
			query = DBQueryConstants.GET_NSE_LTP_OMDF;
		else if (sExchange.equals(ExchangeSegment.NFO))
			query = DBQueryConstants.GET_NFO_LTP_OMDF;
		else if (sExchange.equals(ExchangeSegment.BSE))
			query = DBQueryConstants.GET_BSE_LTP_OMDF;
		else if (sExchange.equals(ExchangeSegment.MCX))
			query = DBQueryConstants.GET_MCX_LTP_OMDF;
		else if (sExchange.equals(ExchangeSegment.NCDEX))
			query = DBQueryConstants.GET_NCDEX_LTP_OMDF;
		else if (sExchange.equals(ExchangeSegment.NSECDS))
			query = DBQueryConstants.GET_NSECDS_LTP_OMDF;
		else if (sExchange.equals(ExchangeSegment.BSECDS))
			query = DBQueryConstants.GET_BSECDS_LTP_OMDF;

		log.info("Getting data from "+DBConstants.OMDF_QUOTE);
		log.debug(DBConstants.OMDF_QUOTE +" :: "+ query);

		try {
			conn = QuoteDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, symRow.getSymbolToken());

			res = ps.executeQuery();

			if (res.next()) {
				String sLTP = String.valueOf(res.getDouble(DBConstants.LAST_PRICE));
				quoteDetails.sLTP = res.wasNull() ? "--" : sLTP;
				String sChange = String.valueOf(res.getDouble(DBConstants.CHANGE));
				quoteDetails.sChange = res.wasNull() ? "--" : sChange;
				String sChangePercent = String.valueOf(res.getDouble(DBConstants.CHANGE_PER));
				quoteDetails.sChangePercent = res.wasNull() ? "--" : sChangePercent;
				String sPrevClose = String.valueOf(res.getDouble(DBConstants.CLOSE_PRICE));
				quoteDetails.sPreviousClose = res.wasNull() ? "--" : sPrevClose;
				String sOpenPrice = String.valueOf(res.getDouble(DBConstants.OPEN_PRICE));
				quoteDetails.sOpenPrice = res.wasNull() ? "--" : sOpenPrice;

			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return quoteDetails;

	}
	
	public static Map<String, QuoteDetails> getLTP(LinkedHashSet<String> listTokenMktSegID) throws SQLException {
		Map<String, QuoteDetails> mQuoteDetails = new HashMap<>();

		String queryNSE = "", queryNFO = "", queryBSE = "", queryMCX = "", queryNCDEX = "", queryNSECDS = "",
				queryBSECDS = "";
		for (String sTokenMktSegID : listTokenMktSegID) {
			String[] TokenMktSegID = sTokenMktSegID.split("_");

			String sMktSegID = TokenMktSegID[1];

			String symbolToken  = SymbolMap.getSymbolRow(sTokenMktSegID).getSymbolToken();
			if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)) {
				queryNSE = queryNSE + "'" + symbolToken + "',";
			} else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID)) {
				queryNFO = queryNFO + "'" + symbolToken + "',";
			} else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)) {
				queryBSE = queryBSE + "'" + symbolToken + "',";
			} else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID)) {
				queryMCX = queryMCX + "'" + symbolToken + "',";
			} else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID)) {
				queryNCDEX = queryNCDEX + "'" + symbolToken + "',";
			} else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID)) {
				queryNSECDS = queryNSECDS + "'" + symbolToken + "',";
			} else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID)) {
				queryBSECDS = queryBSECDS + "'" + symbolToken + "',";
			}

		}
				
		if (queryNSE.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NSE_LTP_OMDF;
			queryNSE = queryNSE.substring(0, queryNSE.length() - 1);
			sQuery = String.format(sQuery, queryNSE);
			executeQuery(sQuery, mQuoteDetails);

		}
		if (queryNFO.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NFO_LTP_OMDF;
			queryNFO = queryNFO.substring(0, queryNFO.length() - 1);
			sQuery = String.format(sQuery, queryNFO);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryBSE.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_BSE_LTP_OMDF;
			queryBSE = queryBSE.substring(0, queryBSE.length() - 1);
			sQuery = String.format(sQuery, queryBSE);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryMCX.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_MCX_LTP_OMDF;
			queryMCX = queryMCX.substring(0, queryMCX.length() - 1);
			sQuery = String.format(sQuery, queryMCX);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryNCDEX.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NCDEX_LTP_OMDF;
			queryNCDEX = queryNCDEX.substring(0, queryNCDEX.length() - 1);
			sQuery = String.format(sQuery, queryNCDEX);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryNSECDS.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NSECDS_LTP_OMDF;
			queryNSECDS = queryNSECDS.substring(0, queryNSECDS.length() - 1);
			sQuery = String.format(sQuery, queryNSECDS);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryBSECDS.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_BSECDS_LTP_OMDF;
			queryBSECDS = queryBSECDS.substring(0, queryBSECDS.length() - 1);
			sQuery = String.format(sQuery, queryBSECDS);
			executeQuery(sQuery, mQuoteDetails);
		}

		return mQuoteDetails;

	}

	public static void executeQuery(String sQuery, Map<String, QuoteDetails> mQuoteDetails)
			throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		log.info("Getting data from "+DBConstants.OMDF_QUOTE);
		log.debug(DBConstants.OMDF_QUOTE +" :: "+ sQuery);

		try {
			conn = QuoteDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(sQuery);

			res = ps.executeQuery();

			while (res.next()) {
				QuoteDetails quoteDetails = new QuoteDetails();
				String sTokenMktSegID = res.getString(DBConstants.QUOTE_SYMBOL);
				String sLTP = String.valueOf(res.getDouble(DBConstants.LAST_PRICE));
				quoteDetails.sLTP = res.wasNull() ? "--" : sLTP;
				String sChange = String.valueOf(res.getDouble(DBConstants.CHANGE));
				quoteDetails.sChange = res.wasNull() ? "--" : sChange;
				String sChangePercent = String.valueOf(res.getDouble(DBConstants.CHANGE_PER));
				quoteDetails.sChangePercent = res.wasNull() ? "--" : sChangePercent;
				String sPrevClose = String.valueOf(res.getDouble(DBConstants.CLOSE_PRICE));
				quoteDetails.sPreviousClose = res.wasNull() ? "--" : sPrevClose;
				String sOpenPrice = String.valueOf(res.getDouble(DBConstants.OPEN_PRICE));
				quoteDetails.sOpenPrice = res.wasNull() ? "--" : sOpenPrice;
				mQuoteDetails.put(sTokenMktSegID, quoteDetails);

			}

		} catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

	}

	public static JSONObject getQuote(String sTokenMktSegID, String mappingSymbolUniqDesc) throws SQLException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		String query = null;
		JSONObject quoteObj = new JSONObject();

		String[] TokenMktSegID = sTokenMktSegID.split("_");

		String sMktSegID = TokenMktSegID[1];

		if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
			query = DBQueryConstants.GET_NSE_QUOTE_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			query = DBQueryConstants.GET_NFO_QUOTE_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
			query = DBQueryConstants.GET_BSE_QUOTE_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
			query = DBQueryConstants.GET_MCX_QUOTE_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			query = DBQueryConstants.GET_NCDEX_QUOTE_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_NSECDS_QUOTE_OMDF;
		else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_BSECDS_QUOTE_OMDF;

		log.info("Getting data from "+DBConstants.OMDF_QUOTE);
		log.debug(DBConstants.OMDF_QUOTE +" :: "+ query);

		try {
			conn = QuoteDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, sTokenMktSegID);

			res = ps.executeQuery();

			if (res.next()) {
				float fLTP = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.LAST_PRICE)));
				quoteObj.put(FTConstants.LTP, res.wasNull() ? "" : fLTP);
				float fClosePrice = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.CLOSE_PRICE)));
				quoteObj.put(FTConstants.CLOSE_PRICE, res.wasNull() ? "" : fClosePrice);
				float fUpperCirPrice = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.UPPER_CIR_LIMIT)));
				quoteObj.put(FTConstants.HIGH_PRICE, res.wasNull() ? "" : fUpperCirPrice);
				float fLowerCirPrice = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.LOWER_CIR_LIMIT)));
				quoteObj.put(FTConstants.LOW_PRICE, res.wasNull() ? "" : fLowerCirPrice);
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return quoteObj;

	}
	
	public static Map<String, QuoteDetails> getLTP(LinkedHashSet<String> listTokenMktSegID, String sMktSegID)
			throws SQLException {
		Map<String, QuoteDetails> mQuoteDetails = new HashMap<>();
		String query = "";
		for (String sTokenMktSegID : listTokenMktSegID) {
			String symbolToken = SymbolMap.getSymbolRow(sTokenMktSegID).getSymbolToken();
			query = query + "'" + symbolToken + "',";
		}
		if (query.length() > 0) {
			String sQuery = "";
			if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NSE_LTP_OMDF;
			else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_BSE_LTP_OMDF;
			else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NFO_LTP_OMDF;
			else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_MCX_LTP_OMDF;
			else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NCDEX_LTP_OMDF;
			else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NSECDS_LTP_OMDF;
			else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_BSECDS_LTP_OMDF;
			query = query.substring(0, query.length() - 1);
			sQuery = String.format(sQuery, query);
			executeQuery(sQuery, mQuoteDetails);
		}
		return mQuoteDetails;
	}
}
