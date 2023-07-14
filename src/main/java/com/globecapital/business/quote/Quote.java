package com.globecapital.business.quote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.json.JSONObject;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.cmots.api.data_v1.OptionChain;
import com.msf.cmots.api.data_v1.OptionChainList;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Quote {

	private static Logger log = Logger.getLogger(Quote.class);

	public static QuoteDetails getLTP(String sTokenMktSegID, String mappingSymbolUniqDesc) throws SQLException {

		String[] TokenMktSegID = sTokenMktSegID.split("_");

		String sMktSegID = TokenMktSegID[1];

		try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true"))
				return ExchangeQuote.getLTP(mappingSymbolUniqDesc, sTokenMktSegID);
			else
				return OMDFQuote.getLTP(mappingSymbolUniqDesc, sTokenMktSegID);
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("quote_data.use_exch_quote_updater Key not found in App Config");
		}
		
		return new QuoteDetails();
	}

	public static QuoteDetails getLTPUsingSymbolUniqDesc(String sSymbolUniqDesc) throws SQLException {

		try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true"))
				return ExchangeQuote.getLTPUsingSymbolUniqDesc(sSymbolUniqDesc);
			else
				return OMDFQuote.getLTPUsingSymbolUniqDesc(sSymbolUniqDesc);
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("quote_data.use_exch_quote_updater Key not found in App Config");
		}

		return new QuoteDetails();

	}
	
	public static Map<String, AllocationDetails> getAllocationDetails(LinkedHashSet<String> tokenSegment)
			throws SQLException {
		Map<String, AllocationDetails> allocationDetails = new HashMap<>();

		String query = "";
		for (String tokenSeg : tokenSegment) {

			query = query + "'" + tokenSeg + "',";
		}
		if (query.length() > 0) {
			query = query.substring(0, query.length() - 1);
			getAllocationDetailsFromDb(query, allocationDetails);
		}
		return allocationDetails;
	}

	public static Map<String, QuoteDetails> getLTP(LinkedHashSet<String> listTokenMktSegID) throws SQLException {
		Map<String, QuoteDetails> mQuoteDetails = new HashMap<>();
		try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true"))
				return ExchangeQuote.getLTP(listTokenMktSegID);
			else
				return OMDFQuote.getLTP(listTokenMktSegID);
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("quote_data.use_exch_quote_updater Key not found in App Config");
		}
		
		return mQuoteDetails;
	}

	public static void getAllocationDetailsFromDb(String tokenSegment,
			Map<String, AllocationDetails> allocationDetailsMap) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		String query = null;

		query = DBQueryConstants.GET_GROUP_ALLOCATION;

		query = String.format(query, tokenSegment);

		log.debug(query);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);

			res = ps.executeQuery();

			while (res.next()) {

				AllocationDetails allocationDetails = new AllocationDetails();
				String sTokenMktSegID = res.getString(DBConstants.TOKEN_SEGMENT);
				String sector = res.getString(DBConstants.CM_SECTOR_NAME);
				allocationDetails.sector = res.wasNull() ? "--" : sector;
				String marketCap = res.getString(DBConstants.MARKET_CAP);
				allocationDetails.marketCap = res.wasNull() ? "--" : marketCap;
				allocationDetailsMap.put(sTokenMktSegID, allocationDetails);

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

		try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true"))
				return ExchangeQuote.getQuote(sTokenMktSegID, mappingSymbolUniqDesc);
			else
				return OMDFQuote.getQuote(sTokenMktSegID, mappingSymbolUniqDesc);
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("quote_data.use_exch_quote_updater Key not found in App Config");
		}
		return new JSONObject();

	}
	
	public static Map<String, QuoteDetails> getLTP(LinkedHashSet<String> listTokenMktSegID, String sMktSegID)
			throws SQLException {
		Map<String, QuoteDetails> mQuoteDetails = new HashMap<>();
		try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true"))
				return ExchangeQuote.getLTP(listTokenMktSegID, sMktSegID);
			else
				return OMDFQuote.getLTP(listTokenMktSegID, sMktSegID);
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("quote_data.use_exch_quote_updater Key not found in App Config");
		}
		return mQuoteDetails;
	}
	
	public static Map<String, QuoteDetails> getLTPMapUsingSymbolUniqDesc(OptionChainList optionsList, String mktSegId) throws SQLException, ParseException {
		Map<String, QuoteDetails> LtpQuoteDetailsMap = new HashMap<>();
		String querySymbols = "";
		for (OptionChain option : optionsList) {
			String strikePrice = option.getStrikePrice();
			int ind = strikePrice.indexOf(".");
			if(ind != -1) {
				if(strikePrice.substring(ind+1, strikePrice.length()).equals("0") || strikePrice.substring(ind+1, strikePrice.length()).equals("00"))
					strikePrice = strikePrice.substring(0, ind);
				else
					strikePrice = String.valueOf( Double.parseDouble(strikePrice));
			}
			if(strikePrice != null) {							
				String sym = option.getSymbol().trim();
				String exp = DateUtils.formatDate(option.getExpDate(), DeviceConstants.OPTIONS_DATE_FORMAT_1, DBConstants.UNIQ_DESC_DATE_FORMAT).toUpperCase();
				String symbolUniq = sym+exp+strikePrice;
			
				String symbolUniqCall = symbolUniq + "CE_NFO";
				log.info("Uniq : " + symbolUniqCall);
				if(!SymbolMap.isValidSymbolUniqDescMap(symbolUniqCall))
					continue;
				SymbolRow symRowCall = SymbolMap.getSymbolUniqDescRow(symbolUniqCall);
				querySymbols = querySymbols + "'" + symRowCall.getMappingSymbolUniqDesc()  + "',";
				String symbolUniqPut = symbolUniq + "PE_NFO";
				log.info("Uniq : " + symbolUniqPut);
				if(!SymbolMap.isValidSymbolUniqDescMap(symbolUniqPut))
					continue;
				SymbolRow symRowPut = SymbolMap.getSymbolUniqDescRow(symbolUniqPut);
				querySymbols = querySymbols + "'" + symRowPut.getMappingSymbolUniqDesc()  + "',";
			}
		}
		try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true")) 
				return ExchangeQuote.getLTPMapUsingSymbolUniqDesc(querySymbols, mktSegId);
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("quote_data.use_exch_quote_updater Key not found in App Config");
		}
		return LtpQuoteDetailsMap;
	}
}
