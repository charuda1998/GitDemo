package com.globecapital.business.market;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.api.spyder.market.AllTimeHighEquityAPI;
import com.globecapital.api.spyder.market.AllTimeLowEquityAPI;
import com.globecapital.api.spyder.market.LowerCircuitEquityAPI;
import com.globecapital.api.spyder.market.MarketMoversObject;
import com.globecapital.api.spyder.market.MarketMoversRequest;
import com.globecapital.api.spyder.market.MarketMoversResponse;
import com.globecapital.api.spyder.market.MostActiveByValueCommodityAPI;
import com.globecapital.api.spyder.market.MostActiveByValueCurrencyAPI;
import com.globecapital.api.spyder.market.MostActiveByValueDerivativesAPI;
import com.globecapital.api.spyder.market.MostActiveByValueEquityAPI;
import com.globecapital.api.spyder.market.MostActiveByVolumeCommodityAPI;
import com.globecapital.api.spyder.market.MostActiveByVolumeCurrencyAPI;
import com.globecapital.api.spyder.market.MostActiveByVolumeDerivativesAPI;
import com.globecapital.api.spyder.market.MostActiveByVolumeEquityAPI;
import com.globecapital.api.spyder.market.OIGainersCommodityAPI;
import com.globecapital.api.spyder.market.OIGainersCurrencyAPI;
import com.globecapital.api.spyder.market.OIGainersDerivativesAPI;
import com.globecapital.api.spyder.market.OILosersCommodityAPI;
import com.globecapital.api.spyder.market.OILosersCurrencyAPI;
import com.globecapital.api.spyder.market.OILosersDerivativesAPI;
import com.globecapital.api.spyder.market.PriceShockersEquityAPI;
import com.globecapital.api.spyder.market.TopGainersCommodityAPI;
import com.globecapital.api.spyder.market.TopGainersCurrencyAPI;
import com.globecapital.api.spyder.market.TopGainersDerivativesAPI;
import com.globecapital.api.spyder.market.TopGainersEquityAPI;
import com.globecapital.api.spyder.market.TopLosersCommodityAPI;
import com.globecapital.api.spyder.market.TopLosersCurrencyAPI;
import com.globecapital.api.spyder.market.TopLosersDerivativesAPI;
import com.globecapital.api.spyder.market.TopLosersEquityAPI;
import com.globecapital.api.spyder.market.UpperCircuitEquityAPI;
import com.globecapital.api.spyder.market.VolumeShockersEquityAPI;
import com.globecapital.api.spyder.market.WeeksHighEquityAPI;
import com.globecapital.api.spyder.market.WeeksLowEquityAPI;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class MarketMovers {
	
	private static Logger log = Logger.getLogger(MarketMovers.class);
	private static int precision = 0;
	
//	private static LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
//	private static LinkedHashMap<String, JSONObject> LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();

	
	public static JSONArray getTopGainersEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
	
		MarketMoversRequest topGainersReq = new MarketMoversRequest();
		topGainersReq.setExch(ExchangeSegment.NSE);
		TopGainersEquityAPI topGainersAPI = new TopGainersEquityAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		for (int i = 0; i < topGainersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return topArray;
	}

	public static JSONObject getTopGainersEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topGainersReq = new MarketMoversRequest();
		topGainersReq.setExch(exchange);
		TopGainersEquityAPI topGainersAPI = new TopGainersEquityAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topGainersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topGainersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));

		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, "");
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopGainersDerivatives(String sAppID) throws Exception {
		JSONArray topGainers = new JSONArray();
		topGainers = callTopGainersDerivativesAPI(sAppID);
		return sortArrayDate(topGainers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}
	
	public static JSONArray callTopGainersDerivativesAPI(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topGainersReq = new MarketMoversRequest();
		topGainersReq.setExch(ExchangeSegment.NFO);
		topGainersReq.setType(SpyderConstants.ALL);
		
		TopGainersDerivativesAPI topGainersAPI = new TopGainersDerivativesAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		for (int i = 0; i < topGainersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO));
		return topArray;
	}

	public static JSONObject getTopGainersDerivatives(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		String type = getFilterType(instrumentFilter);
		MarketMoversRequest topGainersReq = new MarketMoversRequest();
		topGainersReq.setExch(exchange);
		topGainersReq.setType(type);
		TopGainersDerivativesAPI topGainersAPI = new TopGainersDerivativesAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topGainersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topGainersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		topGainers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}
	
	public static JSONArray getTopGainersCurrency(String sAppID) throws Exception {
		JSONArray topGainers = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topGainersReq = new MarketMoversRequest();
		topGainersReq.setExch(ExchangeSegment.NSE);
		topGainersReq.setType(SpyderConstants.ALL);
		
		TopGainersCurrencyAPI topGainersAPI = new TopGainersCurrencyAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		for (int i = 0; i < topGainersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topGainers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS));
		return sortArrayDate(topGainers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getTopGainersCurrency(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();

		MarketMoversRequest topGainersReq = new MarketMoversRequest();
        if (exchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
            topGainersReq.setExch(ExchangeSegment.NSE);
        else if (exchange.equalsIgnoreCase(ExchangeSegment.BSECDS))
            topGainersReq.setExch(ExchangeSegment.BSE);		
        
        topGainersReq.setType(typeFormatToAPI(instrumentFilter));
        
        TopGainersCurrencyAPI topGainersAPI = new TopGainersCurrencyAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topGainersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topGainersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		topGainers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopGainersCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topGainersReq = new MarketMoversRequest();
		topGainersReq.setExch(ExchangeSegment.MCX);
		topGainersReq.setType(SpyderConstants.ALL);
		
		TopGainersCommodityAPI topGainersAPI = new TopGainersCommodityAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		for (int i = 0; i < topGainersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX));
		return sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getTopGainersCommodity(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topGainers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topGainersReq = new MarketMoversRequest();
		topGainersReq.setExch(exchange);
		topGainersReq.setType(typeFormatToAPI(instrumentFilter));
		
		TopGainersCommodityAPI topGainersAPI = new TopGainersCommodityAPI();
		MarketMoversResponse topGainersResp = topGainersAPI.get(topGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_GAINERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> topGainersObj = topGainersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topGainersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topGainersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		topGainers.put(topObj);

		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topGainers);
		return finalObj;
	}

	public static JSONArray getTopLosersEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
		topLosersReq.setExch(ExchangeSegment.NSE);
		TopLosersEquityAPI topLosersAPI = new TopLosersEquityAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		for (int i = 0; i < topLosersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return topArray;
	}

	public static JSONObject getTopLosersEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topLosers = new JSONArray();
		JSONArray topArray = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
		topLosersReq.setExch(exchange);
		TopLosersEquityAPI topLosersAPI = new TopLosersEquityAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topLosersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topLosersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, "");
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		topLosers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topLosers);
		return finalObj;
	}

	public static JSONArray getTopLosersDerivatives(String sAppID) throws Exception {
		JSONArray topLosers = new JSONArray();
		topLosers = callTopLosersDerivativesAPI(sAppID);
		return sortArrayDate(topLosers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}
	
	public static JSONArray callTopLosersDerivativesAPI(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
		topLosersReq.setExch(ExchangeSegment.NFO);
		topLosersReq.setType(SpyderConstants.ALL);
		
		TopLosersDerivativesAPI topLosersAPI = new TopLosersDerivativesAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		for (int i = 0; i < topLosersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO));
		return topArray;
	}

	public static JSONObject getTopLosersDerivatives(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topLosers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		JSONArray topArray = new JSONArray();

		String type = getFilterType(instrumentFilter);
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
		topLosersReq.setExch(exchange);
		topLosersReq.setType(type);
		
		TopLosersDerivativesAPI topLosersAPI = new TopLosersDerivativesAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topLosersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topLosersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray,objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		topLosers.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topLosers);
		return finalObj;
	}

	public static JSONArray getTopLosersCurrency(String sAppID) throws Exception {
		JSONArray topLosers = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
		topLosersReq.setExch(ExchangeSegment.NSE);
		topLosersReq.setType(SpyderConstants.ALL);
		
		TopLosersCurrencyAPI topLosersAPI = new TopLosersCurrencyAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		for (int i = 0; i < topLosersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topLosers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS));
		return sortArrayDate(topLosers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getTopLosersCurrency(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topLosers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
        if (exchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
        	topLosersReq.setExch(ExchangeSegment.NSE);
        else if (exchange.equalsIgnoreCase(ExchangeSegment.BSECDS))
        	topLosersReq.setExch(ExchangeSegment.BSE);
        topLosersReq.setType(typeFormatToAPI(instrumentFilter));
        
		TopLosersCurrencyAPI topLosersAPI = new TopLosersCurrencyAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topLosersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topLosersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		topLosers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topLosers);
		return finalObj;
	}

	public static JSONArray getTopLosersCommodity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
		topLosersReq.setExch(ExchangeSegment.MCX);
		topLosersReq.setType(SpyderConstants.ALL);
		
		TopLosersCommodityAPI topLosersAPI = new TopLosersCommodityAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		for (int i = 0; i < topLosersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX));
		return sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getTopLosersCommodity(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray topLosers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest topLosersReq = new MarketMoversRequest();
		topLosersReq.setExch(exchange);
		topLosersReq.setType(typeFormatToAPI(instrumentFilter));
		
		TopLosersCommodityAPI topLosersAPI = new TopLosersCommodityAPI();
		MarketMoversResponse topLosersResp = topLosersAPI.get(topLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.TOP_LOSERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> topLosersObj = topLosersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(topLosersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < topLosersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = topLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		topLosers.put(topObj);

		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, topLosers);
		return finalObj;
	}

	public static JSONArray getOIGainersDerivatives(String sAppID) throws Exception {
		JSONArray oiGainers = new JSONArray();
		oiGainers = callOIGainersDerivativesAPI(sAppID);
		return sortArrayDate(oiGainers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONArray callOIGainersDerivativesAPI(String sAppID) throws Exception {
		JSONArray oiArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiGainersReq = new MarketMoversRequest();
		oiGainersReq.setExch(ExchangeSegment.NFO);
		oiGainersReq.setType(SpyderConstants.ALL);
		
		OIGainersDerivativesAPI oiGainersAPI = new OIGainersDerivativesAPI();
		MarketMoversResponse oiGainersResp = oiGainersAPI.get(oiGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_GAINERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> oiGainersObj = oiGainersResp.getResponseObject();
		for (int i = 0; i < oiGainersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(oiArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO));
		return oiArray;
	}
	
	public static JSONObject getOIGainersDerivatives(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray oiGainers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		JSONArray topArray = new JSONArray();
		
		String type = getFilterType(instrumentFilter);
		MarketMoversRequest oiGainersReq = new MarketMoversRequest();
		oiGainersReq.setExch(exchange);
		oiGainersReq.setType(type);
		OIGainersDerivativesAPI oiGainersAPI = new OIGainersDerivativesAPI();
		MarketMoversResponse oiGainersResp = oiGainersAPI.get(oiGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_GAINERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> oiGainersObj = oiGainersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(oiGainersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < oiGainersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		oiGainers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, oiGainers);
		return finalObj;
	}

	public static JSONArray getOIGainersCurrency(String sAppID) throws Exception {
		JSONArray oiGainers = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiGainersReq = new MarketMoversRequest();
		oiGainersReq.setExch(ExchangeSegment.NSE);
		oiGainersReq.setType(SpyderConstants.ALL);
		
		OIGainersCurrencyAPI oiGainersAPI = new OIGainersCurrencyAPI();
		MarketMoversResponse oiGainersResp = oiGainersAPI.get(oiGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_GAINERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> oiGainersObj = oiGainersResp.getResponseObject();
		for (int i = 0; i < oiGainersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(oiGainers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS));
		return sortArrayDate(oiGainers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getOIGainersCurrency(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray oiGainers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiGainersReq = new MarketMoversRequest();
        if (exchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
        	oiGainersReq.setExch(ExchangeSegment.NSE);
        else if (exchange.equalsIgnoreCase(ExchangeSegment.BSECDS))
        	oiGainersReq.setExch(ExchangeSegment.BSE);	
        oiGainersReq.setType(typeFormatToAPI(instrumentFilter));
        
		OIGainersCurrencyAPI oiGainersAPI = new OIGainersCurrencyAPI();
		MarketMoversResponse oiGainersResp = oiGainersAPI.get(oiGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_GAINERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> oiGainersObj = oiGainersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(oiGainersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < oiGainersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
            getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		oiGainers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, oiGainers);
		return finalObj;
	}

	public static JSONArray getOIGainersCommodity(String sAppID) throws Exception {
		JSONArray oiArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiGainersReq = new MarketMoversRequest();
		oiGainersReq.setExch(ExchangeSegment.MCX);
		oiGainersReq.setType(SpyderConstants.ALL);
		
		OIGainersCommodityAPI oiGainersAPI = new OIGainersCommodityAPI();
		MarketMoversResponse oiGainersResp = oiGainersAPI.get(oiGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_GAINERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> oiGainersObj = oiGainersResp.getResponseObject();
		for (int i = 0; i < oiGainersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(oiArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX));
		return sortArrayDate(oiArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getOIGainersCommodity(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray oiGainers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiGainersReq = new MarketMoversRequest();
		oiGainersReq.setExch(exchange);
		oiGainersReq.setType(typeFormatToAPI(instrumentFilter));
		
		OIGainersCommodityAPI oiGainersAPI = new OIGainersCommodityAPI();
		MarketMoversResponse oiGainersResp = oiGainersAPI.get(oiGainersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_GAINERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> oiGainersObj = oiGainersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(oiGainersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < oiGainersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiGainersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		oiGainers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, oiGainers);
		return finalObj;
	}

	public static JSONArray getOILosersDerivatives(String sAppID) throws Exception {
		JSONArray oiLosers = new JSONArray();
		oiLosers = callOILosersDerivativesAPI(sAppID);
		return sortArrayDate(oiLosers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}
	
	public static JSONArray callOILosersDerivativesAPI(String sAppID) throws Exception {
		JSONArray oiArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiLosersReq = new MarketMoversRequest();
		oiLosersReq.setExch(ExchangeSegment.NFO);
		oiLosersReq.setType(SpyderConstants.ALL);
		
		OILosersDerivativesAPI oiLosersAPI = new OILosersDerivativesAPI();
		MarketMoversResponse oiLosersResp = oiLosersAPI.get(oiLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_LOSERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> oiLosersObj = oiLosersResp.getResponseObject();
		for (int i = 0; i < oiLosersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(oiArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO));
		return oiArray;
	}

	public static JSONObject getOILosersDerivatives(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray oiLosers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		JSONArray topArray = new JSONArray();

		String type = getFilterType(instrumentFilter);
		MarketMoversRequest oiLosersReq = new MarketMoversRequest();
		oiLosersReq.setExch(exchange);
		oiLosersReq.setType(type);
		OILosersDerivativesAPI oiLosersAPI = new OILosersDerivativesAPI();
		MarketMoversResponse oiLosersResp = oiLosersAPI.get(oiLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_LOSERS_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> oiLosersObj = oiLosersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(oiLosersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < oiLosersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray,objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		oiLosers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, oiLosers);
		return finalObj;
	}

	public static JSONArray getOILosersCurrency(String sAppID) throws Exception {
		JSONArray oiLosers = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiLosersReq = new MarketMoversRequest();
		oiLosersReq.setExch(ExchangeSegment.NSE);
		oiLosersReq.setType(SpyderConstants.ALL);
		
		OILosersCurrencyAPI oiLosersAPI = new OILosersCurrencyAPI();
		MarketMoversResponse oiLosersResp = oiLosersAPI.get(oiLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_LOSERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> oiLosersObj = oiLosersResp.getResponseObject();
		for (int i = 0; i < oiLosersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(oiLosers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS));
		return sortArrayDate(oiLosers, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getOILosersCurrency(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray oiLosers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiLosersReq = new MarketMoversRequest();
		if (exchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
			oiLosersReq.setExch(ExchangeSegment.NSE);
        else if (exchange.equalsIgnoreCase(ExchangeSegment.BSECDS))
        	oiLosersReq.setExch(ExchangeSegment.BSE);	
		oiLosersReq.setType(typeFormatToAPI(instrumentFilter));
		
		OILosersCurrencyAPI oiLosersAPI = new OILosersCurrencyAPI();
		MarketMoversResponse oiLosersResp = oiLosersAPI.get(oiLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_LOSERS_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> oiLosersObj = oiLosersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(oiLosersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < oiLosersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		oiLosers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, oiLosers);
		return finalObj;
	}

	public static JSONArray getOILosersCommodity(String sAppID) throws Exception {
		JSONArray oiArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiLosersReq = new MarketMoversRequest();
		oiLosersReq.setExch(ExchangeSegment.MCX);
		oiLosersReq.setType(SpyderConstants.ALL);
		
		OILosersCommodityAPI oiLosersAPI = new OILosersCommodityAPI();
		MarketMoversResponse oiLosersResp = oiLosersAPI.get(oiLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_LOSERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> oiLosersObj = oiLosersResp.getResponseObject();
		for (int i = 0; i < oiLosersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(oiArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX));
		return sortArrayDate(oiArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getOILosersCommodity(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray oiLosers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest oiLosersReq = new MarketMoversRequest();
		oiLosersReq.setExch(exchange);
		oiLosersReq.setType(typeFormatToAPI(instrumentFilter));
		
		OILosersCommodityAPI oiLosersAPI = new OILosersCommodityAPI();
		MarketMoversResponse oiLosersResp = oiLosersAPI.get(oiLosersReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.OI_LOSERS_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> oiLosersObj = oiLosersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(oiLosersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < oiLosersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiLosersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		oiLosers.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, oiLosers);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		mostActiveByVolumeReq.setExch(ExchangeSegment.NSE);
		MostActiveByVolumeEquityAPI mostActiveByVolumeAPI = new MostActiveByVolumeEquityAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		for (int i = 0; i < mostActiveByVolumeObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return topArray;
	}

	public static JSONObject getMostActiveByVolumeEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByVolume = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		JSONArray topArray = new JSONArray();

		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		mostActiveByVolumeReq.setExch(exchange);
		MostActiveByVolumeEquityAPI mostActiveByVolumeAPI = new MostActiveByVolumeEquityAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByVolumeResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByVolumeObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, "");
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		mostActiveByVolume.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByVolume);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeDerivatives(String sAppID) throws Exception {
		JSONArray mostActiveByVolume = new JSONArray();
		mostActiveByVolume = callMostActiveByVolumeDerivativesAPI(sAppID);
		return sortArrayDate(mostActiveByVolume, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}
	
	public static JSONArray callMostActiveByVolumeDerivativesAPI(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		mostActiveByVolumeReq.setExch(ExchangeSegment.NFO);
		mostActiveByVolumeReq.setType(SpyderConstants.ALL);
		
		MostActiveByVolumeDerivativesAPI mostActiveByVolumeAPI = new MostActiveByVolumeDerivativesAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID, DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		for (int i = 0; i < mostActiveByVolumeObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO));
		return topArray;
	}

	public static JSONObject getMostActiveByVolumeDerivatives(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByVolume = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		JSONArray topArray = new JSONArray();

		String type = getFilterType(instrumentFilter);
		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		mostActiveByVolumeReq.setExch(exchange);
		mostActiveByVolumeReq.setType(type);
		MostActiveByVolumeDerivativesAPI mostActiveByVolumeAPI = new MostActiveByVolumeDerivativesAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByVolumeResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByVolumeObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		mostActiveByVolume.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByVolume);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeCurrency(String sAppID) throws Exception {
		JSONArray mostActiveByVolume = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		mostActiveByVolumeReq.setExch(ExchangeSegment.NSE);
		mostActiveByVolumeReq.setType(SpyderConstants.ALL);
		
		MostActiveByVolumeCurrencyAPI mostActiveByVolumeAPI = new MostActiveByVolumeCurrencyAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		for (int i = 0; i < mostActiveByVolumeObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(mostActiveByVolume, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS));
		return sortArrayDate(mostActiveByVolume, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getMostActiveByVolumeCurrency(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByVolume = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		if (exchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
			mostActiveByVolumeReq.setExch(ExchangeSegment.NSE);
        else if (exchange.equalsIgnoreCase(ExchangeSegment.BSECDS))
        	mostActiveByVolumeReq.setExch(ExchangeSegment.BSE);
		mostActiveByVolumeReq.setType(typeFormatToAPI(instrumentFilter));
		
		MostActiveByVolumeCurrencyAPI mostActiveByVolumeAPI = new MostActiveByVolumeCurrencyAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByVolumeResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByVolumeObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		mostActiveByVolume.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByVolume);
		return finalObj;
	}

	public static JSONArray getMostActiveByVolumeCommodity(String sAppID) throws Exception {
		JSONArray mostActiveArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		mostActiveByVolumeReq.setExch(ExchangeSegment.MCX);
		mostActiveByVolumeReq.setType(SpyderConstants.ALL);
		
		MostActiveByVolumeCommodityAPI mostActiveByVolumeAPI = new MostActiveByVolumeCommodityAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		for (int i = 0; i < mostActiveByVolumeObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(mostActiveArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX));
		return sortArrayDate(mostActiveArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getMostActiveByVolumeCommodity(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByVolume = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByVolumeReq = new MarketMoversRequest();
		mostActiveByVolumeReq.setExch(exchange);
		mostActiveByVolumeReq.setType(typeFormatToAPI(instrumentFilter));
		
		MostActiveByVolumeCommodityAPI mostActiveByVolumeAPI = new MostActiveByVolumeCommodityAPI();
		MarketMoversResponse mostActiveByVolumeResp = mostActiveByVolumeAPI.get(mostActiveByVolumeReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VOLUME_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> mostActiveByVolumeObj = mostActiveByVolumeResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByVolumeResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByVolumeObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByVolumeObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			obj.put(DeviceConstants.M_VOLUME,
					PriceFormat.formatPrice(mostActiveByVolumeObj.get(i).getVolume(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		mostActiveByVolume.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByVolume);
		return finalObj;
	}

	public static JSONArray getMostActiveByValueEquity(String sAppID) throws Exception {
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		mostActiveByValueReq.setExch(ExchangeSegment.NSE);
		MostActiveByValueEquityAPI mostActiveByValueAPI = new MostActiveByValueEquityAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		for (int i = 0; i < mostActiveByValueObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return topArray;
	}

	public static JSONObject getMostActiveByValueEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByValue = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		JSONArray topArray = new JSONArray();

		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		mostActiveByValueReq.setExch(exchange);
		MostActiveByValueEquityAPI mostActiveByValueAPI = new MostActiveByValueEquityAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByValueResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByValueObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, "");
		topObj.put(DeviceConstants.SYMBOL_LIST, topArray);
		mostActiveByValue.put(topObj);
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByValue);
		return finalObj;
	}

	public static JSONArray getMostActiveByValueDerivatives(String sAppID) throws Exception {
		JSONArray mostActiveByValue = new JSONArray();
		mostActiveByValue = callMostActiveByValueDerivativesAPI(sAppID);
		return sortArrayDate(mostActiveByValue, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}
	
	public static JSONArray callMostActiveByValueDerivativesAPI(String sAppID) throws Exception { 
		JSONArray topArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		mostActiveByValueReq.setExch(ExchangeSegment.NFO);
		mostActiveByValueReq.setType(SpyderConstants.ALL);
		
		MostActiveByValueDerivativesAPI mostActiveByValueAPI = new MostActiveByValueDerivativesAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		for (int i = 0; i < mostActiveByValueObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO));
		return topArray;
	}

	public static JSONObject getMostActiveByValueDerivatives(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByValue = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		JSONArray topArray = new JSONArray();

		String type = getFilterType(instrumentFilter);
		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		mostActiveByValueReq.setExch(exchange);
		mostActiveByValueReq.setType(type);
		MostActiveByValueDerivativesAPI mostActiveByValueAPI = new MostActiveByValueDerivativesAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.DERIVATIVE);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByValueResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByValueObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(topArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(topArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		mostActiveByValue.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByValue);
		return finalObj;
	}
	
	public static JSONArray getMostActiveByValueCurrency(String sAppID) throws Exception {
		JSONArray mostActiveByValue = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		mostActiveByValueReq.setExch(ExchangeSegment.NSE);
		mostActiveByValueReq.setType(SpyderConstants.ALL);
		
		MostActiveByValueCurrencyAPI mostActiveByValueAPI = new MostActiveByValueCurrencyAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		for (int i = 0; i < mostActiveByValueObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(mostActiveByValue, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSECDS));
		return sortArrayDate(mostActiveByValue, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getMostActiveByValueCurrency(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByValue = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		if (exchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
			mostActiveByValueReq.setExch(ExchangeSegment.NSE);
        else if (exchange.equalsIgnoreCase(ExchangeSegment.BSECDS))
        	mostActiveByValueReq.setExch(ExchangeSegment.BSE);
		mostActiveByValueReq.setType(typeFormatToAPI(instrumentFilter));
		
		MostActiveByValueCurrencyAPI mostActiveByValueAPI = new MostActiveByValueCurrencyAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.CURRENCY);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByValueResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByValueObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		mostActiveByValue.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByValue);
		return finalObj;
	}

	public static JSONArray getMostActiveByValueCommodity(String sAppID) throws Exception {
		JSONArray mostActiveArray = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		mostActiveByValueReq.setExch(ExchangeSegment.MCX);
		mostActiveByValueReq.setType(SpyderConstants.ALL);
		
		MostActiveByValueCommodityAPI mostActiveByValueAPI = new MostActiveByValueCommodityAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		for (int i = 0; i < mostActiveByValueObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(mostActiveArray, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.MCX));
		return sortArrayDate(mostActiveArray, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	}

	public static JSONObject getMostActiveByValueCommodity(String exchange, String instrumentFilter, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray mostActiveByValue = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest mostActiveByValueReq = new MarketMoversRequest();
		mostActiveByValueReq.setExch(exchange);
		mostActiveByValueReq.setType(typeFormatToAPI(instrumentFilter));
		
		MostActiveByValueCommodityAPI mostActiveByValueAPI = new MostActiveByValueCommodityAPI();
		MarketMoversResponse mostActiveByValueResp = mostActiveByValueAPI.get(mostActiveByValueReq,
				MarketMoversResponse.class, sAppID,DeviceConstants.MOST_ACTIVE_BY_VALUE_L+" "+DeviceConstants.COMMODITY);
		List<MarketMoversObject> mostActiveByValueObj = mostActiveByValueResp.getResponseObject();
		String dateTime = DateUtils.formatDate(mostActiveByValueResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < mostActiveByValueObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = mostActiveByValueObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			obj.put(DeviceConstants.VALUE,
					PriceFormat.formatPrice(mostActiveByValueObj.get(i).getValue(), precision, true));
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		JSONArray finalArr = getQuoteDetails(objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		
		JSONObject topObj = new JSONObject();
		topObj.put(DeviceConstants.TYPE, instrumentFilter);
		topObj.put(DeviceConstants.SYMBOL_LIST, sortArrayDate(finalArr, DeviceConstants.EXPIRY_DATE, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		mostActiveByValue.put(topObj);
		
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, mostActiveByValue);
		return finalObj;
	}

	public static JSONArray getAllTimeHighEquity(String sAppID) throws Exception {
		JSONArray allTimeHigh = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest allTimeHighReq = new MarketMoversRequest();
		allTimeHighReq.setExch(ExchangeSegment.NSE);
		AllTimeHighEquityAPI allTimeHighAPI = new AllTimeHighEquityAPI();
		MarketMoversResponse allTimeHighResp = allTimeHighAPI.get(allTimeHighReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.ALL_TIME_HIGH_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> allTimeHighObj = allTimeHighResp.getResponseObject();
		for (int i = 0; i < allTimeHighObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = allTimeHighObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(allTimeHigh, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return allTimeHigh;
	}
	
	public static JSONObject getAllTimeHighEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray allTimeHigh = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest allTimeHighReq = new MarketMoversRequest();
		allTimeHighReq.setExch(exchange);
		AllTimeHighEquityAPI allTimeHighAPI = new AllTimeHighEquityAPI();
		MarketMoversResponse allTimeHighResp = allTimeHighAPI.get(allTimeHighReq, MarketMoversResponse.class, sAppID
				,DeviceConstants.ALL_TIME_HIGH_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> allTimeHighObj = allTimeHighResp.getResponseObject();
		String dateTime = DateUtils.formatDate(allTimeHighResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < allTimeHighObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = allTimeHighObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(allTimeHigh, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, allTimeHigh);
		return finalObj;
	}

	public static JSONArray getAllTimeLowEquity(String sAppID) throws Exception {
		JSONArray allTimeLow = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest allTimeLowReq = new MarketMoversRequest();
		allTimeLowReq.setExch(ExchangeSegment.NSE);
		AllTimeLowEquityAPI allTimeLowAPI = new AllTimeLowEquityAPI();
		MarketMoversResponse allTimeLowResp = allTimeLowAPI.get(allTimeLowReq, MarketMoversResponse.class, sAppID,
				DeviceConstants.ALL_TIME_LOW_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> allTimeLowObj = allTimeLowResp.getResponseObject();
		for (int i = 0; i < allTimeLowObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = allTimeLowObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(allTimeLow, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return allTimeLow;
	}
	
	public static JSONObject getAllTimeLowEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray allTimeLow = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest allTimeLowReq = new MarketMoversRequest();
		allTimeLowReq.setExch(exchange);
		AllTimeLowEquityAPI allTimeLowAPI = new AllTimeLowEquityAPI();
		MarketMoversResponse allTimeLowResp = allTimeLowAPI.get(allTimeLowReq, MarketMoversResponse.class, sAppID
				,DeviceConstants.ALL_TIME_LOW_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> allTimeLowObj = allTimeLowResp.getResponseObject();
		String dateTime = DateUtils.formatDate(allTimeLowResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < allTimeLowObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = allTimeLowObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(allTimeLow, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, allTimeLow);
		return finalObj;
	}

	public static JSONArray getUpperCircuitEquity(String sAppID) throws Exception {
		JSONArray upperCircuit = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest upperCircuitReq = new MarketMoversRequest();
		upperCircuitReq.setExch(ExchangeSegment.NSE);
		UpperCircuitEquityAPI upperCircuitAPI = new UpperCircuitEquityAPI();
		MarketMoversResponse upperCircuitResp = upperCircuitAPI.get(upperCircuitReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.UPPER_CIRCUIT_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> upperCircuitObj = upperCircuitResp.getResponseObject();
		for (int i = 0; i < upperCircuitObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = upperCircuitObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(upperCircuit, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return upperCircuit;
	}
	
	public static JSONObject getUpperCircuitEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray upperCircuit = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest upperCircuitReq = new MarketMoversRequest();
		upperCircuitReq.setExch(exchange);
		UpperCircuitEquityAPI upperCircuitAPI = new UpperCircuitEquityAPI();
		MarketMoversResponse upperCircuitResp = upperCircuitAPI.get(upperCircuitReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.UPPER_CIRCUIT_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> upperCircuitObj = upperCircuitResp.getResponseObject();
		String dateTime = DateUtils.formatDate(upperCircuitResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < upperCircuitObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = upperCircuitObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(upperCircuit, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, upperCircuit);
		return finalObj;
	}
	
	public static JSONArray getLowerCircuitEquity(String sAppID) throws Exception {
		JSONArray lowerCircuit = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest lowerCircuitReq = new MarketMoversRequest();
		lowerCircuitReq.setExch(ExchangeSegment.NSE);
		LowerCircuitEquityAPI lowerCircuitAPI = new LowerCircuitEquityAPI();
		MarketMoversResponse lowerCircuitResp = lowerCircuitAPI.get(lowerCircuitReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.LOWER_CIRCUIT_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> lowerCircuitObj = lowerCircuitResp.getResponseObject();
		for (int i = 0; i < lowerCircuitObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = lowerCircuitObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(lowerCircuit, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return lowerCircuit;
	}

	public static JSONObject getLowerCircuitEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray lowerCircuit = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest lowerCircuitReq = new MarketMoversRequest();
		lowerCircuitReq.setExch(exchange);
		LowerCircuitEquityAPI lowerCircuitAPI = new LowerCircuitEquityAPI();
		MarketMoversResponse lowerCircuitResp = lowerCircuitAPI.get(lowerCircuitReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.LOWER_CIRCUIT_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> lowerCircuitObj = lowerCircuitResp.getResponseObject();
		String dateTime = DateUtils.formatDate(lowerCircuitResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < lowerCircuitObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = lowerCircuitObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(lowerCircuit, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, lowerCircuit);
		return finalObj;
	}
	
	public static JSONArray getPriceShockersEquity(String sAppID) throws Exception {
		JSONArray priceShockers = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest priceShockersReq = new MarketMoversRequest();
		priceShockersReq.setExch(ExchangeSegment.NSE);
		PriceShockersEquityAPI priceShockersAPI = new PriceShockersEquityAPI();
		MarketMoversResponse priceShockersResp = priceShockersAPI.get(priceShockersReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.PRICE_SHOCKERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> priceShockersObj = priceShockersResp.getResponseObject();
		for (int i = 0; i < priceShockersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = priceShockersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(priceShockers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return priceShockers;
	}

	public static JSONObject getPriceShockersEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray priceShockers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest priceShockersReq = new MarketMoversRequest();
		priceShockersReq.setExch(exchange);
		PriceShockersEquityAPI priceShockersAPI = new PriceShockersEquityAPI();
		MarketMoversResponse priceShockersResp = priceShockersAPI.get(priceShockersReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.PRICE_SHOCKERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> priceShockersObj = priceShockersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(priceShockersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < priceShockersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = priceShockersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(priceShockers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, priceShockers);
		return finalObj;
	}
	
	public static JSONArray getVolumeShockersEquity(String sAppID) throws Exception {
		JSONArray volumeShockers = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest volumeShockersReq = new MarketMoversRequest();
		volumeShockersReq.setExch(ExchangeSegment.NSE);
		VolumeShockersEquityAPI volumeShockersAPI = new VolumeShockersEquityAPI();
		MarketMoversResponse volumeShockersResp = volumeShockersAPI.get(volumeShockersReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.VOLUME_SHOCKERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> volumeShockersObj = volumeShockersResp.getResponseObject();
		for (int i = 0; i < volumeShockersObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = volumeShockersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(volumeShockers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return volumeShockers;
	}

	public static JSONObject getVolumeShockersEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray volumeShockers = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest volumeShockersReq = new MarketMoversRequest();
		volumeShockersReq.setExch(exchange);
		VolumeShockersEquityAPI volumeShockersAPI = new VolumeShockersEquityAPI();
		MarketMoversResponse volumeShockersResp = volumeShockersAPI.get(volumeShockersReq, MarketMoversResponse.class,
				sAppID,DeviceConstants.VOLUME_SHOCKERS_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> volumeShockersObj = volumeShockersResp.getResponseObject();
		String dateTime = DateUtils.formatDate(volumeShockersResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < volumeShockersObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = volumeShockersObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(volumeShockers, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, volumeShockers);
		return finalObj;
	}

	public static JSONArray getWeeksHighEquity(String sAppID) throws Exception {
		JSONArray weeksHigh = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest weeksHighReq = new MarketMoversRequest();
		weeksHighReq.setExch(ExchangeSegment.NSE);
		WeeksHighEquityAPI weeksHighAPI = new WeeksHighEquityAPI();
		MarketMoversResponse weeksHighResp = weeksHighAPI.get(weeksHighReq, MarketMoversResponse.class, sAppID,DeviceConstants.WEEKS_HIGH_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> weeksHighObj = weeksHighResp.getResponseObject();
		for (int i = 0; i < weeksHighObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = weeksHighObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(weeksHigh, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return weeksHigh;
	}
	
	public static JSONObject getWeeksHighEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray weeksHigh = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest weeksHighReq = new MarketMoversRequest();
		weeksHighReq.setExch(exchange);
		WeeksHighEquityAPI weeksHighAPI = new WeeksHighEquityAPI();
		MarketMoversResponse weeksHighResp = weeksHighAPI.get(weeksHighReq, MarketMoversResponse.class, sAppID,DeviceConstants.WEEKS_HIGH_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> weeksHighObj = weeksHighResp.getResponseObject();
		String dateTime = DateUtils.formatDate(weeksHighResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < weeksHighObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = weeksHighObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(weeksHigh, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, weeksHigh);
		return finalObj;
	}
	
	public static JSONArray getWeeksLowEquity(String sAppID) throws Exception {
		JSONArray weeksLow = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest weeksLowReq = new MarketMoversRequest();
		weeksLowReq.setExch(ExchangeSegment.NSE);
		WeeksLowEquityAPI weeksLowAPI = new WeeksLowEquityAPI();
		MarketMoversResponse weeksLowResp = weeksLowAPI.get(weeksLowReq, MarketMoversResponse.class, sAppID,DeviceConstants.WEEKS_LOW_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> weeksLowObj = weeksLowResp.getResponseObject();
		for (int i = 0; i < weeksLowObj.size() && objMap.size()<limit; i++) {
			JSONObject obj = new JSONObject();
			String token = weeksLowObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(weeksLow, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(ExchangeSegment.NSE));
		return weeksLow;
	}

	public static JSONObject getWeeksLowEquity(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray weeksLow = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		LinkedHashMap<String, JSONObject> objMap = new LinkedHashMap<String, JSONObject>();
		
		MarketMoversRequest weeksLowReq = new MarketMoversRequest();
		weeksLowReq.setExch(exchange);
		WeeksLowEquityAPI weeksLowAPI = new WeeksLowEquityAPI();
		MarketMoversResponse weeksLowResp = weeksLowAPI.get(weeksLowReq, MarketMoversResponse.class, sAppID,DeviceConstants.WEEKS_LOW_L+" "+DeviceConstants.EQUITY);
		List<MarketMoversObject> weeksLowObj = weeksLowResp.getResponseObject();
		String dateTime = DateUtils.formatDate(weeksLowResp.getDateTime(),DeviceConstants.API_DATE_FORMAT, DeviceConstants.INDEX_DATE_FORMAT);
		for (int i = 0; i < weeksLowObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = weeksLowObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			getSymbolDetails(obj, objMap, linkedsetSymbolToken, token);
		}
		getQuoteDetails(weeksLow, objMap, linkedsetSymbolToken, ExchangeSegment.getMarketSegmentID(exchange));
		finalObj.put(DeviceConstants.DATE_LS, dateTime);
		finalObj.put(DeviceConstants.MARKET_DATA, weeksLow);
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
		precision = sSymObj.getPrecisionInt();
		obj.put(SymbolConstants.PRECISION, sSymObj.getPrecisionInt());
		obj.put(SymbolConstants.INSTRUMENT_NAME, sSymObj.getInstrument());
		linkedsetSymbolToken.add(symToken);
		objMap.put(symToken, obj);
		return;
	}

	public static void getQuoteDetails(JSONArray topArray, LinkedHashMap<String, JSONObject> objMap, LinkedHashSet<String> linkedsetSymbolToken, String mrktSegID) throws Exception {
		Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedsetSymbolToken, mrktSegID);
		for (String sToken : linkedsetSymbolToken) {
			QuoteDetails quote = mQuoteDetails.get(sToken);
			JSONObject obj = objMap.get(sToken);
			int precision = obj.getInt(SymbolConstants.PRECISION);
			if(obj.isEmpty() || obj == null)
				continue;
			if (quote == null) {
				obj.put(DeviceConstants.LTP, "0.00");
				obj.put(DeviceConstants.CHANGE, "0.00");
				obj.put(DeviceConstants.CHANGE_PERCENT, "0");
			} else {
				obj.put(DeviceConstants.LTP, PriceFormat.formatPrice(quote.sLTP, precision, false));
				obj.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quote.sChange, precision, false));
				obj.put(DeviceConstants.CHANGE_PERCENT, String.valueOf(Math.abs(Double.parseDouble(quote.sChangePercent))));
			}
			topArray.put(obj);
		}
	}
	
	public static JSONArray getQuoteDetails(LinkedHashMap<String, JSONObject> objMap, 
			LinkedHashSet<String> linkedsetSymbolToken, String mrktSegID) throws Exception {
		
		JSONArray finalArr = new JSONArray();
		Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedsetSymbolToken, mrktSegID);
		for (String sToken : linkedsetSymbolToken) {
			QuoteDetails quote = mQuoteDetails.get(sToken);
			JSONObject obj = objMap.get(sToken);
			int precision = obj.getInt(SymbolConstants.PRECISION);
			if (quote == null) {
				obj.put(DeviceConstants.LTP, "0.00");
				obj.put(DeviceConstants.CHANGE, "0.00");
				obj.put(DeviceConstants.CHANGE_PERCENT, "0");
			} else {
				obj.put(DeviceConstants.LTP, PriceFormat.formatPrice(quote.sLTP, precision, false));
				obj.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quote.sChange, precision, false));
				obj.put(DeviceConstants.CHANGE_PERCENT, String.valueOf(Math.abs(Double.parseDouble(quote.sChangePercent))));
			}
			finalArr.put(obj);
		}
		return finalArr;
	}
	
	public static String getFilterType(String instrumentFilter) {
		String type = DeviceConstants.STOCK_FUTURES;
		if(instrumentFilter.equals(InstrumentType.STOCK_FUTURES))
			type = DeviceConstants.STOCK_FUTURES;
		else if(instrumentFilter.equals(InstrumentType.INDEX_FUTURES))
			type = DeviceConstants.INDEX_FUTURES;
		else if(instrumentFilter.equals(InstrumentType.STOCK_OPTIONS))
			type = DeviceConstants.STOCK_OPTIONS;
		else if(instrumentFilter.equals(InstrumentType.INDEX_OPTIONS))
			type = DeviceConstants.INDEX_OPTIONS;
		return type;			
	}

	private static String typeFormatToAPI(String sType)
	{
		if(sType.equals(DeviceConstants.FILTER_OPTIONS))
			return SpyderConstants.OPTION;
		else if(sType.equals(DeviceConstants.FILTER_FUTURES))
			return SpyderConstants.FUTURE;
		return SpyderConstants.ALL;
	}
}
