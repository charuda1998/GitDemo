package com.globecapital.business.report;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetPortfolioAPI;
import com.globecapital.api.gc.backoffice.GetPortfolioRequest;
import com.globecapital.api.gc.backoffice.GetPortfolioResponse;
import com.globecapital.api.gc.backoffice.GetPortfolioRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.business.quote.AllocationDetails;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class Portfolio {

	private static Logger log = Logger.getLogger(Portfolio.class);
	
	public static JSONObject getPortfolioReports(String sUserID, String sAppID) throws JSONException, GCException {

		JSONObject finalObj = new JSONObject();
		
		JSONArray portfolioArray = new JSONArray();

		Double totalDayPL = 0.0;

		LinkedHashSet<String> symbolToken = new LinkedHashSet<String>();

		GetPortfolioAPI portfolioAPI = new GetPortfolioAPI();

		GetPortfolioRequest portfolioRequest = new GetPortfolioRequest();
			
		portfolioRequest.setClientCode(sUserID);
		portfolioRequest.setToken(GCAPIAuthToken.getAuthToken());

		GetPortfolioResponse portfolioResponse = portfolioAPI.get(portfolioRequest, GetPortfolioResponse.class,
					sAppID,"GetPortfolio");
			
		if (portfolioResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				portfolioRequest.setToken(GCAPIAuthToken.getAuthToken());
				portfolioResponse = portfolioAPI.get(portfolioRequest, GetPortfolioResponse.class,sAppID,"GetPortfolio");
		}
		if(!portfolioResponse.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS))
			throw new GCException(InfoIDConstants.NO_DATA);
			
		try {
				
			List<GetPortfolioRows> portfolioRows = portfolioResponse.getDetails();
			Collections.reverse(portfolioRows);
			
			int iDiscrepancyCount = 0;
			
			for (GetPortfolioRows rows : portfolioRows) {

				SymbolRow portfolioObj = new SymbolRow();
				String isinTokenSegmentNSE = rows.getIsin() + "_" + ExchangeSegment.NSE_SEGMENT_ID;
				String isinTokenSegmentBSE = rows.getIsin() + "_" + ExchangeSegment.BSE_SEGMENT_ID;
				
				if (SymbolMap.isValidSymbol(isinTokenSegmentNSE)) {
					portfolioObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentNSE).getMinimisedSymbolRow());
				}else if((SymbolMap.isValidSymbol(isinTokenSegmentBSE))) {
					portfolioObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentBSE).getMinimisedSymbolRow());
				}else
					continue;
				try
				{
					String sTokenMktSegID = portfolioObj.getJSONObject(SymbolConstants.SYMBOL_OBJ)
							.getString(SymbolConstants.SYMBOL_TOKEN);
					symbolToken.add(sTokenMktSegID);
					Map<String, QuoteDetails> getQuoteDetails = Quote.getLTP(symbolToken);
					if (getQuoteDetails.containsKey(sTokenMktSegID)) {
						QuoteDetails quoteDetails = getQuoteDetails.get(sTokenMktSegID);
						double quantity = Double.parseDouble(rows.getQty());
	                    double ltp = Double.parseDouble(quoteDetails.sLTP);
	                    portfolioObj.put(DeviceConstants.MARKET_VALUE, PriceFormat.priceToRupees(String.valueOf(quantity*ltp)));
						portfolioObj.put(DeviceConstants.QTY, Integer.toString(Integer.parseInt(rows.getQty())));
						portfolioObj.put(DeviceConstants.BUY_AVG, PriceFormat.priceToRupees(rows.getBuyAvg()));
						portfolioObj.put(DeviceConstants.INVESTMENT_VALUE, PriceFormat.priceToRupees(
								String.valueOf(Double.parseDouble(rows.getBuyAvg()) * Double.parseDouble(rows.getQty()))));
						portfolioObj.put(DeviceConstants.UNREALISED_PROFIT_LOSS,
								PriceFormat.priceToRupees(String.valueOf(quantity*ltp - (Double.parseDouble(rows.getBuyAvg())) * Double.parseDouble(rows.getQty()))));
						portfolioObj.put(DeviceConstants.PRODUCT_TYPE, ExchangeSegment.NSE);
						
						portfolioArray.put(portfolioObj);
					}
					if(rows.getIsDiscrepancy().equals(GCConstants.Y))
						iDiscrepancyCount = iDiscrepancyCount + 1;
				}
				catch(Exception e)
				{
					log.error(e);
				}
			} 
			
			finalObj.put(DeviceConstants.TODAY_MOVER_DETAILS, getMarketDetails(portfolioArray, symbolToken));
			getMarketCap(portfolioArray, symbolToken);
			
			TreeMap<String, JSONArray> capMap = new TreeMap<String, JSONArray>();
			Map<String, JSONArray> sectorMap = new HashMap<String, JSONArray>();

			double marketCapTotal = 0.0, sectorTotal = 0.0;
			
			for (int i = 0; i < portfolioArray.length(); i++) {
				
				totalDayPL = totalDayPL + Double.parseDouble(portfolioArray.getJSONObject(i).
						getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]", ""));
				JSONObject portfolioObj = portfolioArray.getJSONObject(i);

				if (!portfolioObj.getString(DeviceConstants.MARKET_CAP).equals("--")) {
					String sKey = portfolioObj.getString(DeviceConstants.MARKET_CAP);

					if (capMap.containsKey(sKey)) {
						capMap.get(sKey).put(portfolioObj);
					} else {
						JSONArray arr = new JSONArray();
						arr.put(portfolioObj);
						capMap.put(sKey, arr);
					}
					marketCapTotal+= Double.parseDouble(portfolioObj.getString(DeviceConstants.MARKET_VALUE).replaceAll("[,\u20B9]",""));

				}
				if (!portfolioObj.getString(DeviceConstants.SECTOR).equals("--")) {
					String sKey = portfolioObj.getString(DeviceConstants.SECTOR);

					if (sectorMap.containsKey(sKey)) {
						sectorMap.get(sKey).put(portfolioObj);
					} else {
						JSONArray arr = new JSONArray();
						arr.put(portfolioObj);
						sectorMap.put(sKey, arr);
					}
					sectorTotal+= Double.parseDouble(portfolioObj.getString(DeviceConstants.MARKET_VALUE).replaceAll("[,\u20B9]",""));

				}
			}
			finalObj.put(DeviceConstants.TOTAL_SUMMARY, 
					getSummaryDetails(portfolioResponse, totalDayPL, portfolioArray)
					.put(DeviceConstants.DISCREPANCY_COUNT, Integer.toString(iDiscrepancyCount)));
			finalObj.put(DeviceConstants.MY_HOLDINGS, getMyHoldings(capMap, sectorMap));
			finalObj.put(DeviceConstants.ALLOCATION, getAllocationDetails(capMap, sectorMap, marketCapTotal, 
					sectorTotal));
			finalObj.put(DeviceConstants.AS_ON_DATE, portfolioResponse.getAsOnDate());
			
		} catch (Exception e) {
			log.error(e);
		}
		JSONObject portfolioObj = new JSONObject();
		portfolioObj.put(DeviceConstants.REPORTS, finalObj);
		return portfolioObj;
	}

	private static JSONObject getAllocationDetails(TreeMap<String, JSONArray> capMap, 
			Map<String, JSONArray> sectorMap, double marketCapTotal, double sectorTotal) {
		JSONArray sector = new JSONArray();
		JSONArray marketCap = new JSONArray();
		
		for (Entry<String, JSONArray> val : sectorMap.entrySet()) {
			JSONObject sectorAllocation = new JSONObject();
			sectorAllocation.put(DeviceConstants.TYPE, val.getKey());
			sectorAllocation
					.put(DeviceConstants.ALLOCATION_PERCENT,
							PriceFormat.formatPrice(
											String.valueOf((Double.parseDouble(String.valueOf(getTotalFromArray(val.getValue(), DeviceConstants.MARKET_VALUE)))
													/ Double.parseDouble(String.valueOf(sectorTotal))) * 100),
											2, true));
			
			sector.put(sectorAllocation);
		}

		for (Entry<String, JSONArray> val : capMap.entrySet()) {

			JSONObject capAllocation = new JSONObject();
			capAllocation.put(DeviceConstants.TYPE, val.getKey());
			capAllocation
					.put(DeviceConstants.ALLOCATION_PERCENT,
							PriceFormat
									.formatPrice(
											String.valueOf((Double.parseDouble(String.valueOf(getTotalFromArray(val.getValue(),DeviceConstants.MARKET_VALUE)))
													/ Double.parseDouble(String.valueOf(marketCapTotal))) * 100),
											2, true));

			marketCap.put(capAllocation);
		}
		
		JSONObject allocationObj = new JSONObject();
		allocationObj.put(DeviceConstants.MARKET_CAP_DETAILS, marketCap);
		allocationObj.put(DeviceConstants.SECTOR, sector);
		
		return allocationObj;
		
	}

	private static double getTotalFromArray(JSONArray valueArray, String key) {
		double total = 0.0;
		for(int i=0; i < valueArray.length();i++) {
			String value = valueArray.getJSONObject(i).getString(key).replaceAll("[,\u20B9]","");
			total+= Double.parseDouble(value);
		}
		return total;
	}

	private static JSONObject getMyHoldings(Map<String, JSONArray> capMap, Map<String, JSONArray> sectorMap) {
		JSONObject myHoldingsObj = new JSONObject();
		
		myHoldingsObj.put(DeviceConstants.MARKET_CAP_DETAILS, getHoldingsFromMap(capMap));
		myHoldingsObj.put(DeviceConstants.SECTOR, getHoldingsFromMap(sectorMap));
		return myHoldingsObj;
	}

	private static JSONArray getHoldingsFromMap(Map<String, JSONArray> mapHoldings) {
		
		JSONArray arrHoldings = new JSONArray();
		for (Entry<String, JSONArray> entry : mapHoldings.entrySet())  
		{
			JSONObject holdingsObj = new JSONObject();
			holdingsObj.put(DeviceConstants.TYPE, entry.getKey());
			holdingsObj.put(DeviceConstants.HOLDINGS_LIST, entry.getValue());
			arrHoldings.put(holdingsObj);
		}
		return arrHoldings;
	}

	private static JSONObject getSummaryDetails(GetPortfolioResponse portfolioResponse, Double totalDayPL, JSONArray portfolioArray) {
		
		JSONObject summary = new JSONObject();
		
		Double marketValue = getTotalFromArray(portfolioArray, DeviceConstants.MARKET_VALUE);
		Double investmentValue = getTotalFromArray(portfolioArray,DeviceConstants.INVESTMENT_VALUE);
		summary.put(DeviceConstants.AS_ON_DATE, portfolioResponse.getAsOnDate());
		summary.put(DeviceConstants.MARKET_VALUE,
				PriceFormat.priceInCrores(String.valueOf(marketValue)));
		summary.put(DeviceConstants.PURCHASE_VALUE,
				PriceFormat.priceInCrores(String.valueOf(investmentValue)));
		summary.put(DeviceConstants.UNREALISED_PROFIT_LOSS,
				PriceFormat.priceInCrores(String.valueOf(marketValue-investmentValue)));
		
		summary.put(DeviceConstants.UNREALISED_PROFIT_LOSS_INDICATOR, 
				getIndicator(Double.parseDouble(String.valueOf(marketValue-investmentValue))));
		summary.put(DeviceConstants.DAY_PROFIT_LOSS_INDICATOR, getIndicator(totalDayPL));
		
		summary.put(DeviceConstants.DAY_PROFIT_LOSS, PriceFormat.priceInCrores(String.valueOf(totalDayPL)));
		
		return summary;
	}

	private static String getIndicator(double dValue) {
		
		String sIndicator;
		
		if (dValue == 0) 
			sIndicator = ""; 
		else if (dValue > 0) 
			sIndicator = "+";
		else
			sIndicator = "-";
		return sIndicator;
	}


	public static JSONArray getCapHoldings(String marketCap, JSONArray portfolioArray) {
		JSONArray capArray = new JSONArray();
		for (int i = 0; i < portfolioArray.length(); i++) {
			JSONObject portfolioobj = new JSONObject();
			portfolioobj = portfolioArray.getJSONObject(i);
			if (portfolioobj.getString(DeviceConstants.MARKET_CAP).contains(marketCap)) {
				capArray.put(portfolioobj);

			}
		}
		return capArray;
	}

	public static JSONArray getSectorHoldings(String sector, JSONArray portfolioArray) {
		JSONArray sectorArray = new JSONArray();
		for (int i = 0; i < portfolioArray.length(); i++) {
			JSONObject portfolioobj = new JSONObject();
			portfolioobj = portfolioArray.getJSONObject(i);
			if (portfolioobj.getString(DeviceConstants.SECTOR).contains(sector)) {
				sectorArray.put(portfolioobj);

			}
		}
		return sectorArray;
	}

	public static void getMarketCap(JSONArray portfolioList, LinkedHashSet<String> symbolToken) {

		try {

			Map<String, AllocationDetails> allocationDetails = Quote.getAllocationDetails(symbolToken);

			for (int i = 0; i < portfolioList.length(); i++) {
				if (!portfolioList.getJSONObject(i).toString().contains(DeviceConstants.TOTAL_SUMMARY)) {

					SymbolRow portfolioObj = (SymbolRow) portfolioList.getJSONObject(i);
					String sSymbolToken = (portfolioList.getJSONObject(i)).getJSONObject(SymbolConstants.SYMBOL_OBJ)
							.getString(SymbolConstants.SYMBOL_TOKEN);

					if (allocationDetails.containsKey(sSymbolToken)) {
						AllocationDetails quoteDetails = allocationDetails.get(sSymbolToken);
						portfolioObj.put(DeviceConstants.MARKET_CAP, quoteDetails.marketCap);
						portfolioObj.put(DeviceConstants.SECTOR, quoteDetails.sector);

					} else {
						portfolioObj.put(DeviceConstants.MARKET_CAP, "--");
						portfolioObj.put(DeviceConstants.SECTOR, "--");
					}

				}
			}
		} catch (Exception e) {
			log.error(e);
		}

	}

	public static JSONObject getMarketDetails(JSONArray portfolioList, LinkedHashSet<String> symbolToken) {

		JSONObject gainerObj = null;
		JSONObject loserObj = null;
		
		try {

			double dDayMin = Double.MAX_VALUE, dDayMax = (Double.MIN_VALUE);
			
			Map<String, QuoteDetails> getQuoteDetails = Quote.getLTP(symbolToken);

			for (int i = 0; i < portfolioList.length(); i++) {
				
				Double dayPL = 0.0;
				Double dayPLPercent = 0.0;
				
				JSONObject portfolioObj =  portfolioList.getJSONObject(i);
				String qty = (portfolioList.getJSONObject(i)).getString(DeviceConstants.QTY);
					
				String sSymbolToken = (portfolioList.getJSONObject(i)).getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
				if (getQuoteDetails.containsKey(sSymbolToken)) {
					QuoteDetails quoteDetails = getQuoteDetails.get(sSymbolToken);
					
					portfolioObj.put(DeviceConstants.LTP, PriceFormat.priceToRupees(quoteDetails.sLTP));

					portfolioObj.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quoteDetails.sChange,2 ,false));
					portfolioObj.put(DeviceConstants.CHANGE_PERCENT,Math.abs(Double.parseDouble(quoteDetails.sChangePercent)));
					portfolioObj.put(DeviceConstants.PREV_CLOSE,
							PriceFormat.priceToRupees(quoteDetails.sPreviousClose));
						
					if (!(quoteDetails.sPreviousClose.equals("0.00") || quoteDetails.sLTP.equals("0.00") ||
								quoteDetails.sPreviousClose.equals("0") || quoteDetails.sLTP.equals("0"))) {
							
						dayPL = (Double.parseDouble(quoteDetails.sLTP.replaceAll("[,\u20B9]", "")) -
								Double.parseDouble(quoteDetails.sPreviousClose.replaceAll("[,\u20B9]", "")))
								* Integer.parseInt(qty);
						dayPLPercent = (dayPL / 
								(Double.parseDouble(quoteDetails.sPreviousClose.replaceAll("[,\u20B9]", "")) 
								* Integer.parseInt(qty))) * 100;
							
					} else {
						dayPL = 0.0;
						dayPLPercent = 0.0;
					}
				} else {
					portfolioObj.put(DeviceConstants.LTP, "--");
					portfolioObj.put(DeviceConstants.CHANGE, "--");
					portfolioObj.put(DeviceConstants.CHANGE_PERCENT, "--");
					portfolioObj.put(DeviceConstants.PREV_CLOSE, "--");
					portfolioObj.put(DeviceConstants.MARKET_VALUE, "--");
					dayPL = 0.0;
					dayPLPercent = 0.0;
				}
				
				if(dayPL > dDayMax)
				{
					dDayMax = dayPL;
					gainerObj = new JSONObject();
					gainerObj.put(DeviceConstants.SYMBOL,
							portfolioObj.getJSONObject(SymbolConstants.SYMBOL_OBJ).
							getString(DeviceConstants.SYMBOL));
					gainerObj.put(DeviceConstants.DAY_PROFIT_LOSS, PriceFormat.priceToRupees(String.valueOf(dayPL)));
					gainerObj.put(DeviceConstants.DAY_PROFIT_CH_PERCENTAGE,
							PriceFormat.formatPrice(String.valueOf(dayPLPercent), 2, false));
				}
				else if(dayPL < dDayMin)
				{
					dDayMin = dayPL;
					loserObj = new JSONObject();
					loserObj.put(DeviceConstants.SYMBOL,
							portfolioObj.getJSONObject(SymbolConstants.SYMBOL_OBJ).
							getString(DeviceConstants.SYMBOL));
					loserObj.put(DeviceConstants.DAY_PROFIT_LOSS, PriceFormat.priceToRupees(String.valueOf(dayPL)));
					loserObj.put(DeviceConstants.DAY_PROFIT_CH_PERCENTAGE,
							PriceFormat.formatPrice(String.valueOf(dayPLPercent), 2, false));
				}
				
				if(Objects.isNull(gainerObj)) {
					gainerObj = new JSONObject();
					gainerObj.put(DeviceConstants.SYMBOL,"--");
					gainerObj.put(DeviceConstants.DAY_PROFIT_LOSS, "--");
					gainerObj.put(DeviceConstants.DAY_PROFIT_CH_PERCENTAGE, "--");
				}else if(Objects.isNull(loserObj)) {
					loserObj = new JSONObject();
					loserObj.put(DeviceConstants.SYMBOL,"--");
					loserObj.put(DeviceConstants.DAY_PROFIT_LOSS, "--");
					loserObj.put(DeviceConstants.DAY_PROFIT_CH_PERCENTAGE, "--");
				}
				portfolioObj.put(DeviceConstants.DAY_PROFIT_LOSS,
						PriceFormat.priceToRupees(String.valueOf(dayPL)));
				portfolioObj.put(DeviceConstants.DAY_PROFIT_CH_PERCENTAGE,
						PriceFormat.formatPrice(String.valueOf(dayPLPercent), 2, false));
			}
		} catch (Exception e) {
			log.error(e);
		}
		
		JSONObject marketmoversObj = new JSONObject();
		marketmoversObj.put(DeviceConstants.TODAY_GAINER, gainerObj);
		marketmoversObj.put(DeviceConstants.TODAY_LOSER, loserObj);
		return marketmoversObj;
	}
	
}
