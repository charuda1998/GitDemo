package com.globecapital.business.marketdata;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.business.quote.ExchangeQuote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class MarketMovers_101 {
	
	private static Logger log = Logger.getLogger(MarketMovers_101.class);
	
	public static JSONArray getTopGainersEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_GAINERS, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getTopGainersEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_GAINERS_EQUITY_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopGainersDerivatives(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_GAINERS_DERIVATIVES_SYM, DBConstants.NFO_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}
	
	public static JSONObject getTopGainersDerivatives(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterType(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.TOP_GAINERS_DERIVATIVES_ALL_NO_EXPIRY, DBConstants.NFO_QUOTE);
		else
			query = String.format(DBQueryConstants.TOP_GAINERS_DERIVATIVES_ALL_WITH_EXPIRY, DBConstants.NFO_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.DERIVATIVE);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}
	
	public static JSONArray getTopGainersCurrency(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_GAINERS_CUR_SYM, DBConstants.NSECDS_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getTopGainersCurrency(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCurrency(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.TOP_GAINERS_DERIVATIVES_ALL_NO_EXPIRY, DBConstants.NSECDS_QUOTE);
		else
			query = String.format(DBQueryConstants.TOP_GAINERS_DERIVATIVES_ALL_WITH_EXPIRY, DBConstants.NSECDS_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopGainersCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_GAINERS_COM_SYM, DBConstants.MCX_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getTopGainersCommodity(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCommodity(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.TOP_GAINERS_DERIVATIVES_ALL_NO_EXPIRY, DBConstants.MCX_QUOTE);
		else
			query = String.format(DBQueryConstants.TOP_GAINERS_DERIVATIVES_ALL_WITH_EXPIRY, DBConstants.MCX_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopLosersEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_LOSERS, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getTopLosersEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_LOSERS_EQUITY_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopLosersDerivatives(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_LOSERS_DERIVATIVES_SYM, DBConstants.NFO_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}
	
	public static JSONObject getTopLosersDerivatives(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterType(instrumentFilter);
		
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.TOP_LOSERS_DERIVATIVES_ALL_NO_EXPIRY, DBConstants.NFO_QUOTE);
		else
			query = String.format(DBQueryConstants.TOP_LOSERS_DERIVATIVES_ALL_WITH_EXPIRY, DBConstants.NFO_QUOTE);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.DERIVATIVE);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopLosersCurrency(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_LOSERS_CUR_SYM, DBConstants.NSECDS_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getTopLosersCurrency(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCurrency(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.TOP_LOSERS_DERIVATIVES_ALL_NO_EXPIRY, DBConstants.NSECDS_QUOTE);
		else
			query = String.format(DBQueryConstants.TOP_LOSERS_DERIVATIVES_ALL_WITH_EXPIRY, DBConstants.NSECDS_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopLosersCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.TOP_LOSERS_COM_SYM, DBConstants.MCX_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getTopLosersCommodity(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCommodity(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.TOP_LOSERS_DERIVATIVES_ALL_NO_EXPIRY, DBConstants.MCX_QUOTE);
		else
			query = String.format(DBQueryConstants.TOP_LOSERS_DERIVATIVES_ALL_WITH_EXPIRY, DBConstants.MCX_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getOIGainersDerivatives(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> oiGainersDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.OI_GAINERS_OVERVIEW_DERIVATIVE, DBConstants.NFO_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getMarketDataSymbolDetails(obj, objMap, oiGainersDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, oiGainersDataList);
		return topArray;
	}

	public static JSONObject getOIGainersDerivatives(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterType(instrumentFilter);
		String query = "";
		if(expiry.isEmpty())
			expiry = DeviceConstants.ALL_EXPIRIES;
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.OI_GAINERS_DERIVATIVES_NO_EXPIRY, DBConstants.NFO_QUOTE);
		else
			query = String.format(DBQueryConstants.OI_GAINERS_DERIVATIVES_WITH_EXPIRY, DBConstants.NFO_QUOTE);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.DERIVATIVE);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getOIGainersCurrency(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> oiGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.OI_GAINERS_OVERVIEW_CUR, DBConstants.NSECDS_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getMarketDataSymbolDetails(obj, objMap, oiGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, oiGainerDataList);
		return topArray;
	}

	public static JSONObject getOIGainersCurrency(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCurrency(instrumentFilter);
		if(expiry.isEmpty())
			expiry = DeviceConstants.ALL_EXPIRIES;
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.OI_GAINERS_DERIVATIVES_NO_EXPIRY, DBConstants.NSECDS_QUOTE);
		else
			query = String.format(DBQueryConstants.OI_GAINERS_DERIVATIVES_WITH_EXPIRY, DBConstants.NSECDS_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getOIGainersCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> oiGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.OI_GAINERS_OVERVIEW_COM, DBConstants.MCX_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getMarketDataSymbolDetails(obj, objMap, oiGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, oiGainerDataList);
		return topArray;
	}

	public static JSONObject getOIGainersCommodity(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCommodity(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.OI_GAINERS_DERIVATIVES_NO_EXPIRY, DBConstants.MCX_QUOTE);
		else
			query = String.format(DBQueryConstants.OI_GAINERS_DERIVATIVES_WITH_EXPIRY, DBConstants.MCX_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getOILosersDerivatives(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> oiLosersDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.OI_LOSERS_OVERVIEW_DERIVATIVE, DBConstants.NFO_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getMarketDataSymbolDetails(obj, objMap, oiLosersDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, oiLosersDataList);
		return topArray;	
	}
	
	public static JSONObject getOILosersDerivatives(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterType(instrumentFilter);
		String query = "";
		if(expiry.isEmpty())
			expiry = DeviceConstants.ALL_EXPIRIES;
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.OI_LOSERS_DERIVATIVES_NO_EXPIRY, DBConstants.NFO_QUOTE);
		else
			query = String.format(DBQueryConstants.OI_LOSERS_DERIVATIVES_WITH_EXPIRY, DBConstants.NFO_QUOTE);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.DERIVATIVE);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getOILosersCurrency(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> oiLosersDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.OI_LOSERS_OVERVIEW_CUR, DBConstants.NSECDS_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getMarketDataSymbolDetails(obj, objMap, oiLosersDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, oiLosersDataList);
		return topArray;
	
	}

	public static JSONObject getOILosersCurrency(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCurrency(instrumentFilter);
		String query = "";
		if(expiry.isEmpty())
			expiry = DeviceConstants.ALL_EXPIRIES;
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.OI_LOSERS_DERIVATIVES_NO_EXPIRY, DBConstants.NSECDS_QUOTE);
		else
			query = String.format(DBQueryConstants.OI_LOSERS_DERIVATIVES_WITH_EXPIRY, DBConstants.NSECDS_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getOILosersCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> oiLosersDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.OI_LOSERS_OVERVIEW_COM, DBConstants.MCX_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getMarketDataSymbolDetails(obj, objMap, oiLosersDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, oiLosersDataList);
		return topArray;
	}

	public static JSONObject getOILosersCommodity(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCommodity(instrumentFilter);
		String query = "";
		if(expiry.isEmpty())
			expiry = DeviceConstants.ALL_EXPIRIES;
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.OI_LOSERS_DERIVATIVES_NO_EXPIRY, DBConstants.MCX_QUOTE);
		else
			query = String.format(DBQueryConstants.OI_LOSERS_DERIVATIVES_WITH_EXPIRY, DBConstants.MCX_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ACTIVE_VOLUME_EQ, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getMostActiveByVolumeEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ACTIVE_VOLUME_EQ_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeDerivatives(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ACTIVE_VOLUME_DERIVATIVE, DBConstants.NFO_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}
	
	public static JSONObject getMostActiveByVolumeDerivatives(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterType(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.ACTIVE_VOLUME_DERIVATIVES_NO_EXPIRY, DBConstants.NFO_QUOTE);
		else
			query = String.format(DBQueryConstants.ACTIVE_VOLUME_DERIVATIVES_WITH_EXPIRY, DBConstants.NFO_QUOTE);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.DERIVATIVE);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeCurrency(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ACTIVE_VOLUME_CUR, DBConstants.NSECDS_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getMostActiveByVolumeCurrency(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCurrency(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.ACTIVE_VOLUME_DERIVATIVES_NO_EXPIRY, DBConstants.NSECDS_QUOTE);
		else
			query = String.format(DBQueryConstants.ACTIVE_VOLUME_DERIVATIVES_WITH_EXPIRY, DBConstants.NSECDS_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ACTIVE_VOLUME_COM, DBConstants.MCX_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getMostActiveByVolumeCommodity(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCommodity(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.ACTIVE_VOLUME_DERIVATIVES_NO_EXPIRY, DBConstants.MCX_QUOTE);
		else
			query = String.format(DBQueryConstants.ACTIVE_VOLUME_DERIVATIVES_WITH_EXPIRY, DBConstants.MCX_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getMostActiveByValueEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> mostActiveByValueOverviewDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.MOST_ACTIVE_BY_VALUE , DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, mostActiveByValueOverviewDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, mostActiveByValueOverviewDataList);
		return topArray;	
	}

	public static JSONObject getMostActiveByValueEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ACTIVE_VALUE_EQ_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getMostActiveByValueDerivatives(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> mostActiveByValueDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.MOST_ACTIVE_BY_VALUE_DER, DBConstants.NFO_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getMarketDataSymbolDetails(obj, objMap, mostActiveByValueDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, mostActiveByValueDataList);
		return topArray;
	}
	
	public static JSONObject getMostActiveByValueDerivatives(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterType(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.ACTIVE_VALUE_DERIVATIVES_NO_EXPIRY, DBConstants.NFO_QUOTE);
		else
			query = String.format(DBQueryConstants.ACTIVE_VALUE_DERIVATIVES_WITH_EXPIRY, DBConstants.NFO_QUOTE);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.DERIVATIVE);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}
	
	public static JSONArray getMostActiveByValueCurrency(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> mostActiveByValueDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.MOST_ACTIVE_BY_VALUE_CUR, DBConstants.NSECDS_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getMarketDataSymbolDetails(obj, objMap, mostActiveByValueDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, mostActiveByValueDataList);
		return topArray;
	}

	public static JSONObject getMostActiveByValueCurrency(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCurrency(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.ACTIVE_VALUE_DERIVATIVES_NO_EXPIRY, DBConstants.NSECDS_QUOTE);
		else
			query = String.format(DBQueryConstants.ACTIVE_VALUE_DERIVATIVES_WITH_EXPIRY, DBConstants.NSECDS_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.CURRENCY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getMostActiveByValueCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> mostActiveByValueDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.MOST_ACTIVE_BY_VALUE_COM, DBConstants.MCX_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, "", DeviceConstants.DERIVATIVES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getMarketDataSymbolDetails(obj, objMap, mostActiveByValueDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, mostActiveByValueDataList);
		return topArray;
	}

	public static JSONObject getMostActiveByValueCommodity(String exchange, String instrumentFilter, String sAppID, String expiry) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String type = getFilterTypeForCommodity(instrumentFilter);
		String query = "";
		if(expiry.equals(DeviceConstants.ALL_EXPIRIES))
			query = String.format(DBQueryConstants.ACTIVE_VALUE_DERIVATIVES_NO_EXPIRY, DBConstants.MCX_QUOTE);
		else
			query = String.format(DBQueryConstants.ACTIVE_VALUE_DERIVATIVES_WITH_EXPIRY, DBConstants.MCX_QUOTE, expiry);

		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQueryNonEq(query, expiry , type, DeviceConstants.COMMODITY);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getAllTimeHighEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ALL_TIME_HIGH_EQ_OVERVIEW, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}
	
	public static JSONObject getAllTimeHighEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ALL_TIME_HIGH_EQ_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topArray);
		return finalObj;
	}

	public static JSONArray getAllTimeLowEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ALL_TIME_LOW_EQ_OVERVIEW, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}
	
	public static JSONObject getAllTimeLowEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.ALL_TIME_LOW_EQ_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topArray);
		return finalObj;
	}

	public static JSONArray getUpperCircuitEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> upperCircuitOverviewDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.UPPER_CIRCUIT_EQ, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, upperCircuitOverviewDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, upperCircuitOverviewDataList);
		return topArray;
	}
	
	public static JSONObject getUpperCircuitEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.GET_UPPER_CIR_EQ_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topArray);
		return finalObj;
	}
	
	public static JSONArray getLowerCircuitEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> lowerCircuitOverviewDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.LOWER_CIRCUIT_EQ, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, lowerCircuitOverviewDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, lowerCircuitOverviewDataList);
		return topArray;	
	}

	public static JSONObject getLowerCircuitEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.GET_LOWER_CIR_EQ_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topArray);
		return finalObj;
	}
	
	public static JSONArray getPriceShockersEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.PRICE_SHOCKERS_OVERVIEW, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getPriceShockersEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.PRICE_SHOCKERS_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}
	
	public static JSONArray getVolumeShockersEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.VOLUME_SHOCKERS_OVERVIEW, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		return topArray;
	}

	public static JSONObject getVolumeShockersEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.VOLUME_SHOCKERS_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getWeeksHighEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> weekHighDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.WEEKS_HIGH_OVERVIEW_EQ, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, weekHighDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, weekHighDataList);
		return topArray;
	}
	
	public static JSONObject getWeeksHighEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.GET_YEAR_HIGH_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topArray);
		return finalObj;
	}
	
	public static JSONArray getWeeksLowEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_equity");
		LinkedHashMap<String, JSONObject> weeksLowDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.WEEKS_LOW_OVERVIEW_EQ, DBConstants.NSE_QUOTE, String.valueOf(limit));
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataQuery(query, AppConfig.getValue("market_movers.default_index"), DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getMarketDataSymbolDetails(obj, objMap, weeksLowDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, weeksLowDataList);
		return topArray;	
	}

	public static JSONObject getWeeksLowEquity(String exchange, String sAppID, String indexName) throws Exception {
		JSONArray topArray = new JSONArray();
		JSONObject finalObj = new JSONObject();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		String query = String.format(DBQueryConstants.GET_YEAR_LOW_ALL, exchange+DBConstants.QUOTE);
		List<JSONObject> marketDataList = ExchangeQuote.executeMarketDataDetailQuery(query, indexName, exchange, DeviceConstants.EQUITIES);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getMarketDataSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getMarketDataQuoteDetails(topArray, objMap, topGainerDataList);
		finalObj.put(DeviceConstants.DATE_LS, new SimpleDateFormat(DeviceConstants.INDEX_DATE_FORMAT).format(Calendar.getInstance().getTime()));
		finalObj.put(DeviceConstants.MARKET_DATA, topArray);
		return finalObj;
	}

	public static JSONArray sortArrayDate(JSONArray arrayTosort, final String sKey, final String sFormat) {
		if (arrayTosort != null) {
			List<JSONObject> JsonArrayAsList = new ArrayList<JSONObject>();
			for (int i = 0; i < arrayTosort.length(); i++)
				JsonArrayAsList.add(arrayTosort.getJSONObject(i));
			Collections.sort(JsonArrayAsList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject a, JSONObject b) {
					SimpleDateFormat sdfo = new SimpleDateFormat(sFormat);

					Date d1 = null, d2 = null;
					try {
						d1 = sdfo.parse(a.getString(sKey));
						d2 = sdfo.parse(b.getString(sKey));
					} catch (JSONException e) {
						log.error(e);
					} catch (ParseException e) {
						log.error(e);
					}

					return d1.compareTo(d2);
				}
			});
			JSONArray resArray = new JSONArray(JsonArrayAsList);
			return resArray;
		} else
			return null;
	}
	
	public static void getSymbolDetails(JSONObject obj, LinkedHashMap<String, JSONObject> objMap, LinkedHashSet<String> linkedsetSymbolToken, String token) throws Exception {
		if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
			return;
		SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
		obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
		String expiry = sSymObj.getExpiry();
		if(!expiry.equals(""))
			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(expiry, DBConstants.EXPIRY_DATE_FORMAT, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		String symToken = sSymObj.getSymbolToken();
		obj.put(SymbolConstants.PRECISION, sSymObj.getPrecisionInt());
		obj.put(SymbolConstants.INSTRUMENT_NAME, sSymObj.getInstrument());
		linkedsetSymbolToken.add(symToken);
		objMap.put(symToken, obj);
		return;
	}
	
	public static String getFilterType(String instrumentFilter) {
		String type = DeviceConstants.FUTSTK;
		if(instrumentFilter.equals(InstrumentType.STOCK_FUTURES))
			type = DeviceConstants.FUTSTK;
		else if(instrumentFilter.equals(InstrumentType.INDEX_FUTURES))
			type = DeviceConstants.FUTIDX;
		else if(instrumentFilter.equals(InstrumentType.STOCK_OPTIONS))
			type = DeviceConstants.OPTSTK;
		else if(instrumentFilter.equals(InstrumentType.INDEX_OPTIONS))
			type = DeviceConstants.OPTIDX;
		return type;			
	}

	public static String getFilterTypeForCurrency(String instrumentFilter) {
		String type = DeviceConstants.FUTCUR;
		if(instrumentFilter.equals(DeviceConstants.FILTER_FUTURES))
			type = DeviceConstants.FUTCUR;
		else if(instrumentFilter.equals(DeviceConstants.FILTER_OPTIONS))
			type = DeviceConstants.OPTCUR;
		return type;			
	}
	
	public static String getFilterTypeForCommodity(String instrumentFilter) {
		String type = DeviceConstants.FUTCOM;
		if(instrumentFilter.equals(DeviceConstants.FILTER_FUTURES))
			type = DeviceConstants.FUTCOM;
		else if(instrumentFilter.equals(DeviceConstants.FILTER_OPTIONS))
			type = DeviceConstants.OPTCOM;
		return type;			
	}
	
	public static void getMarketDataSymbolDetails(JSONObject obj, LinkedHashMap<String, JSONObject> objMap, LinkedHashMap<String, JSONObject> topGainerDataList, String token, JSONObject marketData) throws Exception {
		if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
			return;
		SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
		obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
		String expiry = sSymObj.getExpiry();
		if(!expiry.equals(""))
			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(expiry, DBConstants.EXPIRY_DATE_FORMAT, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		String symToken = sSymObj.getSymbolToken();
		obj.put(SymbolConstants.PRECISION, sSymObj.getPrecisionInt());
		obj.put(SymbolConstants.INSTRUMENT_NAME, sSymObj.getInstrument());
		topGainerDataList.put(symToken, marketData);
		objMap.put(symToken, obj);
		return;
	}
	
	public static void getOIAnalysisSymbolDetails(JSONObject obj, LinkedHashMap<String, JSONObject> objMap, LinkedHashMap<String, JSONObject> topGainerDataList, String token, JSONObject marketData) throws Exception {
		if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
			return;
		SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
		obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
		String expiry = sSymObj.getExpiry();
		if(!expiry.equals(""))
			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(expiry, DBConstants.EXPIRY_DATE_FORMAT, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		String symToken = sSymObj.getSymbolToken();
		obj.put(SymbolConstants.PRECISION, sSymObj.getPrecisionInt());
		topGainerDataList.put(symToken, marketData);
		objMap.put(symToken, obj);
		return;
	}
	
	public static void getMarketDataQuoteDetails(JSONArray topArray, LinkedHashMap<String, JSONObject> objMap, LinkedHashMap<String, JSONObject> topGainerDataList) throws Exception {
		Iterator<Entry<String, JSONObject>> iterator = topGainerDataList.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, JSONObject> action = iterator.next();
			String sToken = action.getKey();
			JSONObject obj = objMap.get(sToken);
			int precision = obj.getInt(SymbolConstants.PRECISION);
			obj.put(DeviceConstants.LTP, PriceFormat.formatPrice(action.getValue().getString(DBConstants.LAST_PRICE), precision, false));
			obj.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(action.getValue().getString(DBConstants.CHANGE), precision, false));
			obj.put(DeviceConstants.CHANGE_PERCENT, String.valueOf(Math.abs(Double.parseDouble(action.getValue().getString(DBConstants.CHANGE_PER)))));
			topArray.put(obj);
		}
	}
	
	public static void getOIAnalysisQuoteDetails(JSONArray topArray, LinkedHashMap<String, JSONObject> objMap, LinkedHashMap<String, JSONObject> topGainerDataList) throws Exception {
		Iterator<Entry<String, JSONObject>> iterator = topGainerDataList.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, JSONObject> action = iterator.next();
			String sToken = action.getKey();
			JSONObject obj = objMap.get(sToken);
			int precision = obj.getInt(SymbolConstants.PRECISION);
			obj.put(DeviceConstants.LTP, PriceFormat.formatPrice(action.getValue().getString(DBConstants.LAST_PRICE), precision, false));
			obj.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(action.getValue().getString(DBConstants.CHANGE), precision, false));
			obj.put(DeviceConstants.CHANGE_PERCENT, String.valueOf(Math.abs(Double.parseDouble(action.getValue().getString(DBConstants.CHANGE_PER)))));
			obj.put(DeviceConstants.OI, Double.parseDouble(action.getValue().getString(DBConstants.OPENINTEREST)));
			obj.put(DeviceConstants.DISP_OI, PriceFormat.numberFormat(Integer.parseInt(action.getValue().getString(DBConstants.OPENINTEREST))));
			Double oiPer = Double.parseDouble(action.getValue().getString(DBConstants.OI_PER))*100;
			DecimalFormat df = new DecimalFormat("#.##");
			obj.put(DeviceConstants.OI_PER, df.format(oiPer));
			topArray.put(obj);
		};
	}
	
	public static void getRollOverAnalysisQuoteDetails(JSONArray topArray, LinkedHashMap<String, JSONObject> objMap, LinkedHashMap<String, JSONObject> topGainerDataList) throws Exception {
		Iterator<Entry<String, JSONObject>> iterator = topGainerDataList.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, JSONObject> action = iterator.next();
			String sToken = action.getKey();
			JSONObject obj = objMap.get(sToken);
			obj.put(DeviceConstants.ROLLOVER_COST, action.getValue().getString(DBConstants.ROLLOVER_COST));
			obj.put(DeviceConstants.ROLLOVER_COST_PER, action.getValue().getString(DBConstants.ROLLOVER_COST_PER));
			obj.put(DeviceConstants.ROLLOVER_PER, action.getValue().getString(DBConstants.ROLLOVER_PERCENTAGE));
			topArray.put(obj);
		};
	}
	
	public static JSONArray getOiAnalysisDerivatives(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String query = String.format(getQueryByCategory(category), DBConstants.NFO_QUOTE);
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeOIAnalysisQuery(query);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			getOIAnalysisSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getOIAnalysisQuoteDetails(oiAnalysis, objMap, topGainerDataList);
		
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}
	
	public static JSONArray getOiAnalysisDerivativesOverview(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String query = String.format(getAnalysisOverviewQueryByCategoryDer(category), DBConstants.NFO_QUOTE,String.valueOf(limit));
		log.info(query);
		LinkedHashMap<String, JSONObject> oIAnalysisDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeOIAnalysisQuery(query);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			getOIAnalysisSymbolDetails(obj, objMap, oIAnalysisDataList, token, marketData);
		}
		getOIAnalysisQuoteDetails(oiAnalysis, objMap, oIAnalysisDataList);
		
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}
	
	public static JSONArray getOiAnalysisCommodityOverview(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		String query = String.format(getAnalysisOverviewQueryByCategoryCom(category), DBConstants.MCX_QUOTE,String.valueOf(limit));
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeOIAnalysisQuery(query);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.MCX_SEGMENT_ID;
			getOIAnalysisSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getOIAnalysisQuoteDetails(oiAnalysis, objMap, topGainerDataList);
		
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}
	
	public static JSONArray getOiAnalysisCommodity(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();		
		String query = String.format(getQueryByCategory(category), DBConstants.MCX_QUOTE);
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		List<JSONObject> marketDataList = ExchangeQuote.executeOIAnalysisQuery(query);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.MCX_SEGMENT_ID;
			getOIAnalysisSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getOIAnalysisQuoteDetails(oiAnalysis, objMap, topGainerDataList);
		
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}
	
	public static JSONArray sortArray(JSONArray arrayTosort, final String key, final String order) {
		if (arrayTosort != null) {
			List<JSONObject> JsonArrayAsList = new ArrayList<JSONObject>();
			for (int i = 0; i < arrayTosort.length(); i++)
				JsonArrayAsList.add(arrayTosort.getJSONObject(i));
			Collections.sort(JsonArrayAsList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {
					if (order.equals(DeviceConstants.ASCENDING))
						return obj1.getInt(key) - obj2.getInt(key);
					else
						return obj2.getInt(key) - obj1.getInt(key);
				}
			});
			JSONArray resArray = new JSONArray(JsonArrayAsList);
			return resArray;
		} else
			return null;
	}
	
	public static String getQueryByCategory(String filter) {
		String category = "";
		if(filter.equals(DeviceConstants.LONG_BUILDUP))
			return DBQueryConstants.OI_ANALYSIS_DERIVATIVES_LONG_BUILDUP;
		else if(filter.equals(DeviceConstants.SHORT_BUILDUP))
			return DBQueryConstants.OI_ANALYSIS_DERIVATIVES_SHORT_BUILDUP;
		else if(filter.equals(DeviceConstants.LONG_UNWINDING))
			return DBQueryConstants.OI_ANALYSIS_DERIVATIVES_LONG_UNWINDING;
		else if(filter.equals(DeviceConstants.SHORT_COVERING))
			return DBQueryConstants.OI_ANALYSIS_DERIVATIVES_SHORT_COVERING;
		return category;
	}
	
	public static String getAnalysisOverviewQueryByCategoryDer(String filter) {
		String category = "";
		if(filter.equalsIgnoreCase(DeviceConstants.LONG_BUILDUP))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_DER_LONG_BUILDUP;
		else if(filter.equalsIgnoreCase(DeviceConstants.SHORT_BUILDUP))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_DER_SHORT_BUILDUP;
		else if(filter.equalsIgnoreCase(DeviceConstants.LONG_UNWINDING))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_DER_LONG_UNWINDING;
		else if(filter.equalsIgnoreCase(DeviceConstants.SHORT_COVERING))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_DER_SHORT_COVERING;
		return category;
	}
	
	public static String getAnalysisOverviewQueryByCategoryCom(String filter) {
		String category = "";
		if(filter.equalsIgnoreCase(DeviceConstants.LONG_BUILDUP))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_COM_LONG_BUILDUP;
		else if(filter.equalsIgnoreCase(DeviceConstants.SHORT_BUILDUP))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_COM_SHORT_BUILDUP;
		else if(filter.equalsIgnoreCase(DeviceConstants.LONG_UNWINDING))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_COM_LONG_UNWINDING;
		else if(filter.equalsIgnoreCase(DeviceConstants.SHORT_COVERING))
			return DBQueryConstants.OI_ANALYSIS_OVERVIEW_COM_SHORT_COVERING;
		return category;
	}
	
	
	public static JSONArray getRolloverAnalysis(String category, String instrument, String appID) throws Exception {
		JSONArray rolloverAnalysis = new JSONArray();
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		if(instrument.equalsIgnoreCase(InstrumentType.D_STOCK))
			instrument = InstrumentType.FUTSTK;
		else if(instrument.equalsIgnoreCase(InstrumentType.D_INDEX))
			instrument = InstrumentType.FUTIDX;
		if(category.equalsIgnoreCase(DeviceConstants.HIGHEST))
			category = DBConstants.DESCENDING;
		else if(category.equalsIgnoreCase(DeviceConstants.LOWEST))
			category = DBConstants.ASCENDING;
		String query = String.format(DBQueryConstants.ROLLOVER_ANALYSIS_DERIVATIVE, category);
		List<JSONObject> marketDataList = ExchangeQuote.executeRolloverAnalysisQuery(query, instrument, ExchangeSegment.NFO);
		for(JSONObject marketData : marketDataList) {
			marketData.remove(SymbolConstants.SYMBOL_OBJ);
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			getOIAnalysisSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getRollOverAnalysisQuoteDetails(rolloverAnalysis, objMap, topGainerDataList);
		return rolloverAnalysis;
	}
	
	public static JSONArray getRolloverAnalysisOverview(String category, String instrument, String appID) throws Exception {

		JSONArray rolloverAnalysis = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit_non_equity");
		LinkedHashMap<String, JSONObject> topGainerDataList = new LinkedHashMap<String, JSONObject>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		if(instrument.equalsIgnoreCase(InstrumentType.D_STOCK))
			instrument = InstrumentType.FUTSTK;
		else if(instrument.equalsIgnoreCase(InstrumentType.D_INDEX))
			instrument = InstrumentType.FUTIDX;
		if(category.equalsIgnoreCase(DeviceConstants.HIGHEST))
			category = DBConstants.DESCENDING;
		else if(category.equalsIgnoreCase(DeviceConstants.LOWEST))
			category = DBConstants.ASCENDING;
		String query = String.format(DBQueryConstants.ROLLOVER_ANALYSIS_DERIVATIVE_OVERVIEW, category, limit);
		List<JSONObject> marketDataList = ExchangeQuote.executeRolloverAnalysisQuery(query, instrument, ExchangeSegment.NFO);
		for(JSONObject marketData : marketDataList) {
			JSONObject obj = new JSONObject();
			String token = marketData.getString(DBConstants.EXCHANGE_TOKEN) + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			getOIAnalysisSymbolDetails(obj, objMap, topGainerDataList, token, marketData);
		}
		getRollOverAnalysisQuoteDetails(rolloverAnalysis, objMap, topGainerDataList);
		return rolloverAnalysis;
	}
}
	