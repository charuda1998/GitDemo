package com.globecapital.business.quote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.db.GCDBPool;
import com.globecapital.db.QuoteDataDBPool;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class ExchangeQuote {

	private static Logger log = Logger.getLogger(ExchangeQuote.class);

	public static QuoteDetails getLTP(String mappingSymbolUniqDesc, String sTokenMktSegID) throws SQLException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		String query = "";
		String sMktSegID = sTokenMktSegID.split("_")[1];
		
		QuoteDetails quoteDetails = new QuoteDetails();
		
		if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
			query = DBQueryConstants.GET_NSE_LTP;
		else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			query = DBQueryConstants.GET_NFO_LTP;
		else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
			query = DBQueryConstants.GET_BSE_LTP;
		else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
			query = DBQueryConstants.GET_MCX_LTP;
		else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			query = DBQueryConstants.GET_NCDEX_LTP;
		else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_NSECDS_LTP;
		else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_BSECDS_LTP;
		
		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+ query);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, mappingSymbolUniqDesc);

			res = ps.executeQuery();

			if (res.next()) {
				String sLTP = String.valueOf(res.getDouble(DBConstants.LAST_PRICE));
				quoteDetails.sLTP = res.wasNull() ? "0" : sLTP;
				String sChange = String.valueOf(res.getDouble(DBConstants.CHANGE));
				quoteDetails.sChange = res.wasNull() ? "0" : sChange;
				String sChangePercent = String.valueOf(res.getDouble(DBConstants.CHANGE_PER));
				quoteDetails.sChangePercent = res.wasNull() ? "0" : sChangePercent;
				String sPrevClose = String.valueOf(res.getString(DBConstants.PREV_CLOSE));
				quoteDetails.sPreviousClose = res.wasNull() ? "0" : sPrevClose;
				String sOpenPrice = String.valueOf(res.getDouble(DBConstants.OPEN_PRICE));
				quoteDetails.sOpenPrice = res.wasNull() ? "0" : sOpenPrice;
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
			query = DBQueryConstants.GET_NSE_LTP;
		else if (sExchange.equals(ExchangeSegment.NFO))
			query = DBQueryConstants.GET_NFO_LTP;
		else if (sExchange.equals(ExchangeSegment.BSE))
			query = DBQueryConstants.GET_BSE_LTP;
		else if (sExchange.equals(ExchangeSegment.MCX))
			query = DBQueryConstants.GET_MCX_LTP;
		else if (sExchange.equals(ExchangeSegment.NCDEX))
			query = DBQueryConstants.GET_NCDEX_LTP;
		else if (sExchange.equals(ExchangeSegment.NSECDS))
			query = DBQueryConstants.GET_NSECDS_LTP;
		else if (sExchange.equals(ExchangeSegment.BSECDS))
			query = DBQueryConstants.GET_BSECDS_LTP;

		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, symRow.getMappingSymbolUniqDesc());

			res = ps.executeQuery();

			if (res.next()) {
				String sLTP = String.valueOf(res.getDouble(DBConstants.LAST_PRICE));
				quoteDetails.sLTP = res.wasNull() ? "0" : sLTP;
				String sChange = String.valueOf(res.getDouble(DBConstants.CHANGE));
				quoteDetails.sChange = res.wasNull() ? "0" : sChange;
				String sChangePercent = String.valueOf(res.getDouble(DBConstants.CHANGE_PER));
				quoteDetails.sChangePercent = res.wasNull() ? "0" : sChangePercent;
				String sPrevClose = String.valueOf(res.getDouble(DBConstants.PREV_CLOSE));
				quoteDetails.sPreviousClose = res.wasNull() ? "0" : sPrevClose;
				String sOpenPrice = String.valueOf(res.getDouble(DBConstants.OPEN_PRICE));
				quoteDetails.sOpenPrice = res.wasNull() ? "0" : sOpenPrice;

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

			SymbolRow symObj = SymbolMap.getSymbolRow(sTokenMktSegID);
			String mappingSymbolUniqDesc  = symObj.getMappingSymbolUniqDesc();
			if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)) {
				queryNSE = queryNSE + "'" + mappingSymbolUniqDesc + "',";
			} else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID)) {
				queryNFO = queryNFO + "'" + mappingSymbolUniqDesc + "',";
			} else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)) {
				queryBSE = queryBSE + "'" + mappingSymbolUniqDesc + "',";
			} else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID)) {
				queryMCX = queryMCX + "'" + mappingSymbolUniqDesc + "',";
			} else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID)) {
				queryNCDEX = queryNCDEX + "'" + mappingSymbolUniqDesc + "',";
			} else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID)) {
				queryNSECDS = queryNSECDS + "'" + mappingSymbolUniqDesc + "',";
			} else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID)) {
				queryBSECDS = queryBSECDS + "'" + mappingSymbolUniqDesc + "',";
			}

		}
				
		if (queryNSE.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NSE_LTP;
			queryNSE = queryNSE.substring(0, queryNSE.length() - 1);
			sQuery = String.format(sQuery, queryNSE);
			executeQuery(sQuery, mQuoteDetails);

		}
		if (queryNFO.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NFO_LTP;
			queryNFO = queryNFO.substring(0, queryNFO.length() - 1);
			sQuery = String.format(sQuery, queryNFO);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryBSE.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_BSE_LTP;
			queryBSE = queryBSE.substring(0, queryBSE.length() - 1);
			sQuery = String.format(sQuery, queryBSE);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryMCX.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_MCX_LTP;
			queryMCX = queryMCX.substring(0, queryMCX.length() - 1);
			sQuery = String.format(sQuery, queryMCX);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryNCDEX.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NCDEX_LTP;
			queryNCDEX = queryNCDEX.substring(0, queryNCDEX.length() - 1);
			sQuery = String.format(sQuery, queryNCDEX);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryNSECDS.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_NSECDS_LTP;
			queryNSECDS = queryNSECDS.substring(0, queryNSECDS.length() - 1);
			sQuery = String.format(sQuery, queryNSECDS);
			executeQuery(sQuery, mQuoteDetails);
		}
		if (queryBSECDS.length() > 0) {
			String sQuery = DBQueryConstants.GET_GROUP_BSECDS_LTP;
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

		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+sQuery);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(sQuery);

			res = ps.executeQuery();

			while (res.next()) {
				QuoteDetails quoteDetails = new QuoteDetails();
				String symbol = res.getString(DBConstants.QUOTE_SYMBOL);
				String sLTP = String.valueOf(res.getDouble(DBConstants.LAST_PRICE));
				quoteDetails.sLTP = res.wasNull() ? "0" : sLTP;
				String sChange = String.valueOf(res.getDouble(DBConstants.CHANGE));
				quoteDetails.sChange = res.wasNull() ? "0" : sChange;
				String sChangePercent = String.valueOf(res.getDouble(DBConstants.CHANGE_PER));
				quoteDetails.sChangePercent = res.wasNull() ? "0" : sChangePercent;
				String sPrevClose = String.valueOf(res.getDouble(DBConstants.PREV_CLOSE));
				quoteDetails.sPreviousClose = res.wasNull() ? "0" : sPrevClose;
				String sOpenPrice = String.valueOf(res.getDouble(DBConstants.OPEN_PRICE));
				quoteDetails.sOpenPrice = res.wasNull() ? "0" : sOpenPrice;
				mQuoteDetails.put(SymbolMap.getMappingSymbolUniqDescRow(symbol).getSymbolToken(), quoteDetails);
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
			query = DBQueryConstants.GET_NSE_QUOTE;
		else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			query = DBQueryConstants.GET_NFO_QUOTE;
		else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
			query = DBQueryConstants.GET_BSE_QUOTE;
		else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
			query = DBQueryConstants.GET_MCX_QUOTE;
		else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			query = DBQueryConstants.GET_NCDEX_QUOTE;
		else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_NSECDS_QUOTE;
		else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
			query = DBQueryConstants.GET_BSECDS_QUOTE;

		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, mappingSymbolUniqDesc);

			res = ps.executeQuery();

			if (res.next()) {
				float fLTP = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.LAST_PRICE)));
				quoteObj.put(FTConstants.LTP, res.wasNull() ? "0" : fLTP);
				float fClosePrice = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.PREV_CLOSE)));
				quoteObj.put(FTConstants.CLOSE_PRICE, res.wasNull() ? "0" : fClosePrice);
				float fUpperCirPrice = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.UPPER_CIR_LIMIT)));
				quoteObj.put(FTConstants.HIGH_PRICE, res.wasNull() ? "0" : fUpperCirPrice);
				float fLowerCirPrice = Float.parseFloat(String.valueOf(res.getDouble(DBConstants.LOWER_CIR_LIMIT)));
				quoteObj.put(FTConstants.LOW_PRICE, res.wasNull() ? "0" : fLowerCirPrice);
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
			String mappingSymbolUniqDesc = SymbolMap.getSymbolRow(sTokenMktSegID).getMappingSymbolUniqDesc();
			query = query + "'" + mappingSymbolUniqDesc + "',";
		}
		if (query.length() > 0) {
			String sQuery = "";
			if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NSE_LTP;
			else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_BSE_LTP;
			else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NFO_LTP;
			else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_MCX_LTP;
			else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NCDEX_LTP;
			else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NSECDS_LTP;
			else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_BSECDS_LTP;
			query = query.substring(0, query.length() - 1);
			sQuery = String.format(sQuery, query);
			executeQuery(sQuery, mQuoteDetails);
		}
		return mQuoteDetails;
	}	
	
	public static List<JSONObject> executeMarketDataQuery (String query, String indexName, String segmentType) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		List<JSONObject> exchTokenList = new ArrayList<>();

		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			if(segmentType.equalsIgnoreCase(DeviceConstants.EQUITIES))
				ps.setString(1, indexName);
			res = ps.executeQuery();
			while(res.next()) {
				JSONObject marketDataObj = new JSONObject();
				marketDataObj.put(DBConstants.EXCHANGE_TOKEN, res.getString(DBConstants.EXCHANGE_TOKEN));
				marketDataObj.put(DBConstants.LAST_PRICE, String.valueOf(res.getDouble(DBConstants.LAST_PRICE)));
				marketDataObj.put(DBConstants.CHANGE, String.valueOf(res.getDouble(DBConstants.CHANGE)));
				marketDataObj.put(DBConstants.CHANGE_PER, String.valueOf(res.getDouble(DBConstants.CHANGE_PER)));
				exchTokenList.add(marketDataObj);
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return exchTokenList;
	}
	
	public static JSONObject executeFNOOverviewDataQuery (String symbol, String mappingSymbol,String query, int precision, JSONObject fnoOverviewObj, String instrumentType) throws SQLException {
		Connection conn = null;
		PreparedStatement psNFO = null,psRoll=null,psPcr=null;
		ResultSet resNFO = null,resRoll=null,resPcr=null;
		String expDate="";
		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
			log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);
			psNFO = conn.prepareStatement(query);
			psNFO.setString(1,mappingSymbol);
			resNFO = psNFO.executeQuery();
			if(resNFO.next()) {
				fnoOverviewObj.put(DeviceConstants.OI, PriceFormat.numberFormat(Math.round(Float.parseFloat(String.valueOf(resNFO.getInt(DBConstants.OPENINTEREST))))));
				fnoOverviewObj.put(DeviceConstants.OI_CHANGE, PriceFormat.numberFormat(Math.round(Float.parseFloat(String.valueOf(resNFO.getDouble(DBConstants.OI_CHANGE))))));
				fnoOverviewObj.put(DeviceConstants.OI_PER_CHANGE,
						PriceFormat.formatPrice(String.valueOf(resNFO.getDouble(DBConstants.CHANGE_PER)), precision, true) + " %");
				fnoOverviewObj.put(DeviceConstants.LTP,String.valueOf(resNFO.getDouble(DBConstants.LAST_PRICE)));
				expDate=String.valueOf(resNFO.getDate(DBConstants.EXP_DATE));
			}
			psPcr = conn.prepareStatement(DBQueryConstants.FNO_OVERVIEW_PCR);
			psPcr.setString(1, symbol);
			psPcr.setString(2,expDate);
			resPcr = psPcr.executeQuery();
			if(resPcr.next() && InstrumentType.isOptions(instrumentType)) {
				fnoOverviewObj.put(DeviceConstants.PCR, PriceFormat.formatPrice((String.valueOf(resPcr.getBigDecimal(DBConstants.OI_RATIO))), precision, true));
				fnoOverviewObj.put(DeviceConstants.IMPLIED_VOLATILITY,"--");
			}
			else {
				fnoOverviewObj.put(DeviceConstants.PCR,"--");
				fnoOverviewObj.put(DeviceConstants.IMPLIED_VOLATILITY,"--");
			}
			psRoll = conn.prepareStatement(DBQueryConstants.FNO_OVERVIEW_ROLLOVER);
			psRoll.setString(1, symbol);
			psRoll.setString(2,expDate);
			resRoll = psRoll.executeQuery();
			if(resRoll.next() && InstrumentType.isFutures(instrumentType)) {
				fnoOverviewObj.put(DeviceConstants.ROLLOVER,
						PriceFormat.formatPrice(String.valueOf(resRoll.getBigDecimal(DBConstants.ROLLOVER_PERCENTAGE)), precision, true) + "%");
				fnoOverviewObj.put(DeviceConstants.ROLLOVER_COST,
						PriceFormat.formatPrice(resRoll.getString(DBConstants.ROLLOVER_COST), precision, true));
			}else {
				if(InstrumentType.isCurrency(instrumentType)) {
					fnoOverviewObj.put(DeviceConstants.ROLLOVER,"NA");
					fnoOverviewObj.put(DeviceConstants.ROLLOVER_COST,"NA");
				}else {
					fnoOverviewObj.put(DeviceConstants.ROLLOVER,"--");
					fnoOverviewObj.put(DeviceConstants.ROLLOVER_COST,"--");
				}
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(resNFO);
			Helper.closeResultSet(resPcr);
			Helper.closeResultSet(resRoll);
			Helper.closeStatement(psNFO);
			Helper.closeStatement(psPcr);
			Helper.closeStatement(psRoll);
			Helper.closeConnection(conn);
		}
		return fnoOverviewObj;
	}
	
	public static JSONObject executeFNOOverviewScripDataQuery(String token, String segId, JSONObject fnoOverviewObj, String precision) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet res=null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.FNO_OVERVIEW_SCRIPMASTER);
			ps.setString(1, token);
			res = ps.executeQuery();
			
			log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
			log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+DBQueryConstants.FNO_OVERVIEW_SCRIPMASTER);
			if(res.next()) {
				fnoOverviewObj.put(DeviceConstants.CONTRACT_START,res.getString(DeviceConstants.ISSUE_ST_DT));
				fnoOverviewObj.put(DeviceConstants.CONTRACT_END,res.getString(DeviceConstants.ISSUE_MATURITY_DT));
				fnoOverviewObj.put(DeviceConstants.TENDER_OPEN,res.getString(DeviceConstants.DELIVERY_ST_DT));
				fnoOverviewObj.put(DeviceConstants.TENDER_CLOSE,res.getString(DeviceConstants.DELIVERY_END_DT));
				fnoOverviewObj.put(DeviceConstants.MAX_ORDER_SIZE,res.getString(DeviceConstants.MAX_SINGLE_TRANS_QTY));
				fnoOverviewObj.put(DeviceConstants.DEL_UNITS,res.getString(DeviceConstants.QTY_UNIT).trim());
				fnoOverviewObj.put(DeviceConstants.SPOT_INFO, makeSpotInfoObj(res.getString(DBConstants.ASSET_TOKEN), segId, precision));
			}

		}catch(Exception e) {
			log.warn(e);
		}finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		return fnoOverviewObj;
	}
	
	private static JSONObject makeSpotInfoObj(String nToken, String segId, String precision) {
		JSONObject minimisedsymbolObject = new JSONObject();
		JSONObject streamInfo = new JSONObject();
		streamInfo.put(SymbolConstants.MKT_SEG_ID, segId);
		streamInfo.put(SymbolConstants.TOKEN, nToken);
		minimisedsymbolObject.put(SymbolConstants.SYMBOL, "");
		minimisedsymbolObject.put(SymbolConstants.COMPANY_NAME, "");
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_DETAILS,"");
		minimisedsymbolObject.put(SymbolConstants.TICK_PRICE, "");
		minimisedsymbolObject.put(SymbolConstants.LOT_SIZE, "");
		minimisedsymbolObject.put(SymbolConstants.EXCHANGE, "");
		minimisedsymbolObject.put(SymbolConstants.PRECISION, precision);
		minimisedsymbolObject.put(SymbolConstants.STREAM_SYMBOL, streamInfo);
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_TOKEN, nToken+"_"+segId);
		minimisedsymbolObject.put(SymbolConstants.DISP_PRICE_TICK, "");
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_DETAILS_1,"");
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_DETAILS_2,"");
		minimisedsymbolObject.put(DBConstants.MAPPING_SYMBOL_UNIQ_DESC, "");
		return minimisedsymbolObject;
	}

	public static List<JSONObject> executeMarketDataDetailQuery (String query, String indexName, String exchange, String segmentType) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		List<JSONObject> exchTokenList = new ArrayList<>();

		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);
		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			if(segmentType.equalsIgnoreCase(DeviceConstants.EQUITIES)) {
				ps.setString(1, indexName);
				ps.setString(2, exchange);
			}

			res = ps.executeQuery();
			while(res.next()) {
				JSONObject marketDataObj = new JSONObject();
				marketDataObj.put(DBConstants.EXCHANGE_TOKEN, res.getString(DBConstants.EXCHANGE_TOKEN));
				marketDataObj.put(DBConstants.LAST_PRICE, String.valueOf(res.getDouble(DBConstants.LAST_PRICE)));
				marketDataObj.put(DBConstants.CHANGE, String.valueOf(res.getDouble(DBConstants.CHANGE)));
				marketDataObj.put(DBConstants.CHANGE_PER, String.valueOf(res.getDouble(DBConstants.CHANGE_PER)));
				exchTokenList.add(marketDataObj);
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return exchTokenList;
	}
	
	public static List<JSONObject> executeMarketDataDetailQueryNonEq (String query, String expiry, String instrument, String segmentType) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		List<JSONObject> exchTokenList = new ArrayList<>();

		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);
		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, instrument);
			if(!expiry.equals(DeviceConstants.ALL_EXPIRIES))
				ps.setString(2, expiry);
			res = ps.executeQuery();
			while(res.next()) {
				JSONObject marketDataObj = new JSONObject();
				marketDataObj.put(DBConstants.EXCHANGE_TOKEN, res.getString(DBConstants.EXCHANGE_TOKEN));
				marketDataObj.put(DBConstants.LAST_PRICE, String.valueOf(res.getDouble(DBConstants.LAST_PRICE)));
				marketDataObj.put(DBConstants.CHANGE, String.valueOf(res.getDouble(DBConstants.CHANGE)));
				marketDataObj.put(DBConstants.CHANGE_PER, String.valueOf(res.getDouble(DBConstants.CHANGE_PER)));
				exchTokenList.add(marketDataObj);
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return exchTokenList;
	}
	
	public static List<JSONObject> executeOIAnalysisQuery (String query) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		List<JSONObject> exchTokenList = new ArrayList<>();
		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);
		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			res = ps.executeQuery();
			while(res.next()) {
				JSONObject marketDataObj = new JSONObject();
				marketDataObj.put(DBConstants.EXCHANGE_TOKEN, res.getString(DBConstants.EXCHANGE_TOKEN));
				marketDataObj.put(DBConstants.LAST_PRICE, String.valueOf(res.getDouble(DBConstants.LAST_PRICE)));
				marketDataObj.put(DBConstants.CHANGE, String.valueOf(res.getDouble(DBConstants.CHANGE)));
				marketDataObj.put(DBConstants.CHANGE_PER, String.valueOf(res.getDouble(DBConstants.CHANGE_PER)));
				marketDataObj.put(DBConstants.OI_CHANGE, String.valueOf(res.getDouble(DBConstants.OI_CHANGE)));
				marketDataObj.put(DBConstants.OPENINTEREST, String.valueOf(res.getBigDecimal(DBConstants.OPENINTEREST)));
				marketDataObj.put(DBConstants.OI_PER, String.valueOf(res.getDouble(DBConstants.OI_PER)));
				exchTokenList.add(marketDataObj);
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return exchTokenList;
	}
	
	public static List<JSONObject> executeRolloverAnalysisQuery (String query, String instrument , String exchange) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		List<JSONObject> exchTokenList = new ArrayList<>();

		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+query);
		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, instrument);
			ps.setString(2, exchange);
			res = ps.executeQuery();
			while(res.next()) {
				JSONObject marketDataObj = new JSONObject();
				marketDataObj.put(DBConstants.ROLLOVER_PERCENTAGE, String.valueOf(res.getDouble(DBConstants.ROLLOVER_PERCENTAGE)));
				marketDataObj.put(DBConstants.ROLLOVER_COST, res.getString(DBConstants.ROLLOVER_COST));
				marketDataObj.put(DBConstants.ROLLOVER_COST_PER, String.valueOf(res.getDouble(DBConstants.ROLLOVER_COST_PER)));
				String baseSym = res.getString(DBConstants.BASE_SYMBOL);
				String exch = res.getString(DBConstants.EXCHANGE_ROLLOVER);
				String expDate = res.getString(DBConstants.EXP_DATE);
				String instType = res.getString(DBConstants.INSTRUMENT_TYPE_MARKET_DATA);
				String symbolId = instType + "_" + baseSym + "_" + exch + "_" + expDate;
				if(SymbolMap.isValidMappingSymbolUniqDescRow(symbolId)) {
					SymbolRow symRow = SymbolMap.getMappingSymbolUniqDescRow(symbolId);
					marketDataObj.put(DBConstants.EXCHANGE_TOKEN, symRow.gettokenId());
					marketDataObj.put(SymbolConstants.SYMBOL_OBJ, symRow.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
				}
				else
					continue;
				exchTokenList.add(marketDataObj);
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return exchTokenList;
	}
	
	public static Map<String, QuoteDetails> getLTPMapUsingSymbolUniqDesc(String querySymbols, String sMktSegID)
			throws SQLException {
		Map<String, QuoteDetails> mQuoteDetails = new HashMap<>();
		String query = querySymbols;
		
		if (query.length() > 0) {
			String sQuery = "";
			if (sMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NSE_LTP;
			else if (sMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_BSE_LTP;
			else if (sMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NFO_LTP;
			else if (sMktSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_MCX_LTP;
			else if (sMktSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NCDEX_LTP;
			else if (sMktSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_NSECDS_LTP;
			else if (sMktSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
				sQuery = DBQueryConstants.GET_GROUP_BSECDS_LTP;
			query = query.substring(0, query.length() - 1);
			sQuery = String.format(sQuery, query);
			executeLTPMapQuery(sQuery, mQuoteDetails);
		}
		return mQuoteDetails;
	}
	
	public static void executeLTPMapQuery(String sQuery, Map<String, QuoteDetails> mQuoteDetails)
			throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		log.info("Getting data from "+DBConstants.EXCHANGE_QUOTE);
		log.debug(DBConstants.EXCHANGE_QUOTE +" :: "+sQuery);

		try {
			conn = QuoteDataDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(sQuery);

			res = ps.executeQuery();

			while (res.next()) {
				QuoteDetails quoteDetails = new QuoteDetails();
				String symbol = res.getString(DBConstants.QUOTE_SYMBOL);
				String sLTP = String.valueOf(res.getDouble(DBConstants.LAST_PRICE));
				quoteDetails.sLTP = res.wasNull() ? "0" : sLTP;
				String sChange = String.valueOf(res.getDouble(DBConstants.CHANGE));
				quoteDetails.sChange = res.wasNull() ? "0" : sChange;
				String sChangePercent = String.valueOf(res.getDouble(DBConstants.CHANGE_PER));
				quoteDetails.sChangePercent = res.wasNull() ? "0" : sChangePercent;
				String sPrevClose = String.valueOf(res.getDouble(DBConstants.PREV_CLOSE));
				quoteDetails.sPreviousClose = res.wasNull() ? "0" : sPrevClose;
				String sOpenPrice = String.valueOf(res.getDouble(DBConstants.OPEN_PRICE));
				quoteDetails.sOpenPrice = res.wasNull() ? "0" : sOpenPrice;
				mQuoteDetails.put(SymbolMap.getMappingSymbolUniqDescRow(symbol).getMappingSymbolUniqDesc(), quoteDetails);
			}

		} catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

	}

}
