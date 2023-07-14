package com.globecapital.business.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.GetHoldingsAPI;
import com.globecapital.api.gc.backoffice.GetHoldingsRequest;
import com.globecapital.api.gc.backoffice.GetHoldingsResponse;
import com.globecapital.api.gc.backoffice.GetHoldingsRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.business.order.HoldingsDB;
import com.globecapital.business.quote.AllocationDetails;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.log.Logger;

public class Holdings {

    private static Logger log = Logger.getLogger(Holdings.class);

    public static JSONArray getHoldingsReports(Session session, JSONObject filterObj, String reportType)
            throws JSONException, SQLException {

        String userId = session.getUserID();

        JSONArray holdingsArray = new JSONArray();
        JSONArray finalHoldingsArray = new JSONArray();
        JSONObject totalSummary = new JSONObject();
        JSONArray sorted = new JSONArray();
        String hasUnsettledQty = "";
        int precision = 2;
        JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
        String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
        String sortBy = filterObj.getString(DeviceConstants.SORT_BY);

        LinkedHashSet<String> symbolToken = new LinkedHashSet<String>();

        try {

            GetHoldingsAPI holdingsAPI = new GetHoldingsAPI();
            GetHoldingsRequest holdingsRequest = new GetHoldingsRequest();
            JSONObject summary = new JSONObject();

            holdingsRequest.setClientCode(userId);
            holdingsRequest.setToken(GCAPIAuthToken.getAuthToken());
            GetHoldingsResponse holdingsResponse = null;
            
            try {
                String holdings=HoldingsDB.toCheckHoldingEntry(userId);
    			if(holdings!=null) 
    				holdingsResponse = new Gson().fromJson(holdings,GetHoldingsResponse.class);
    			else {
    				holdingsResponse = holdingsAPI.get(holdingsRequest, GetHoldingsResponse.class, session.getAppID(),DeviceConstants.HOLDINGS_L);
    				if(holdingsResponse.getStatus()) {
    					HoldingsDB.updateHoldingsDB(userId,new Gson().toJson(holdingsResponse));
    				}
    			}
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			log.debug(e);
    		}
            
			if(holdingsResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				holdingsRequest.setToken(GCAPIAuthToken.getAuthToken());
				holdingsResponse = holdingsAPI.get(holdingsRequest, GetHoldingsResponse.class, session.getAppID(),DeviceConstants.HOLDINGS_L);
            }

            List<GetHoldingsRows> holdingsRows = new ArrayList<>();
            if(holdingsResponse.getMessage().equalsIgnoreCase(DeviceConstants.SUCCESS))
            	holdingsRows = holdingsResponse.getDetails();
            Collections.reverse(holdingsRows);
            
            int iDiscrepancyCount = 0;
            for (GetHoldingsRows row : holdingsRows) {

                SymbolRow holdingsObject = new SymbolRow();
                String isinTokenSegmentNSE = row.getISIN() + "_" + ExchangeSegment.NSE_SEGMENT_ID;
                String isinTokenSegmentBSE = row.getISIN() + "_" + ExchangeSegment.BSE_SEGMENT_ID;

                if (SymbolMap.isValidSymbol(isinTokenSegmentNSE)) {
                    holdingsObject.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentNSE).getMinimisedSymbolRow());
                }else if (SymbolMap.isValidSymbol(isinTokenSegmentBSE)) {
                    holdingsObject.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentBSE).getMinimisedSymbolRow());
                }else {
                    continue;
                }
                String sTokenMktSegID = holdingsObject.getJSONObject(SymbolConstants.SYMBOL_OBJ)
                        .getString(SymbolConstants.SYMBOL_TOKEN);
                
                symbolToken.add(sTokenMktSegID);
                holdingsObject.put(DeviceConstants.NET_QTY, row.getQty());
                holdingsObject.put(DeviceConstants.TOTAL_QTY, row.getQty());
                holdingsObject.put(DeviceConstants.BUY_AVG, PriceFormat.priceToRupee(row.getPrice(), precision));
                holdingsObject.put(DeviceConstants.INVESTMENT_VALUE,
                        PriceFormat.priceToRupee(row.getValue(), precision));
                holdingsObject.put(DeviceConstants.UNSETTLED_QTY, row.getUnsettledQty());
                if (Integer.parseInt(row.getUnsettledQty()) > 0) {
                    hasUnsettledQty = "true";

                } else {
                    hasUnsettledQty = "false";
                }
                holdingsObject.put(DeviceConstants.HAS_UNSETTLED_QTY, hasUnsettledQty);
                holdingsObject.put(DeviceConstants.MARKET_VALUE,
                        PriceFormat.priceToRupee(row.getMarketValue(), precision));
                holdingsObject.put(DeviceConstants.ISIN, row.getISIN());
                if(Integer.parseInt(row.getQty()) > 0) {
            		holdingsArray.put(holdingsObject);
                } 
                if((Integer.parseInt(row.getQty()) > 0 && Integer.parseInt(row.getDiscQty()) > 0)
                		|| (Integer.parseInt(row.getQty()) == 0 && Integer.parseInt(row.getDiscQty()) > 0))
                	iDiscrepancyCount = iDiscrepancyCount + 1;
            }
            summary.put(DeviceConstants.AS_ON_DATE, holdingsResponse.getAsOnDate());
            Map<String, QuoteDetails> getQuoteDetails = Quote.getLTP(symbolToken);
            getQuote(holdingsArray, symbolToken, precision, finalHoldingsArray, getQuoteDetails);
            getMarketDetails(finalHoldingsArray, symbolToken, getQuoteDetails);
            getMarketCap(finalHoldingsArray, symbolToken);

            Double previousMarketValue = 0.0;
            Double profitLossPercent = 0.0;
            Double totalDayProfitAndLoss = 0.0;
            Double marketValue = getTotalFromArray(finalHoldingsArray, DeviceConstants.MARKET_VALUE);

            for (int i = 0; i < finalHoldingsArray.length(); i++) {
                JSONObject holdingObj = finalHoldingsArray.getJSONObject(i);
            	Double prevClose = Double.parseDouble(holdingObj.getString(DeviceConstants.PREV_CLOSE).replaceAll("[,\u20B9]", ""));
            	Double qty = Double.parseDouble(holdingObj.getString(DeviceConstants.NET_QTY));
            	previousMarketValue+= Double.parseDouble(PriceFormat.formatPrice(String.valueOf(prevClose*qty),precision,false).replaceAll("[,\u20B9]", ""));
            }
            if(previousMarketValue!=0)
            	profitLossPercent = ((marketValue - previousMarketValue)/previousMarketValue)*100;
            totalDayProfitAndLoss = getTotalFromArray(finalHoldingsArray, DeviceConstants.DAY_PROFIT_LOSS);

            summary.put(DeviceConstants.TOTAL_PROFIT_AND_LOSS,PriceFormat.priceInCrores(String.valueOf(totalDayProfitAndLoss)));

                if (totalDayProfitAndLoss == 0) {
                    summary.put(DeviceConstants.DAY_PROFIT_LOSS_INDICATOR, "");
                } else if (totalDayProfitAndLoss > 0.0) {
                    summary.put(DeviceConstants.DAY_PROFIT_LOSS_INDICATOR, "+");
                } else
                    summary.put(DeviceConstants.DAY_PROFIT_LOSS_INDICATOR, "-");

            summary.put(DeviceConstants.TOTAL_PROFIT_LOSS_PERCENTAGE,
                    PriceFormat.formatPrice(String.valueOf(profitLossPercent), precision, false));
            summary.put(DeviceConstants.HOLDINGS_VALUE, PriceFormat.priceInCrores(String.valueOf(getTotalFromArray(finalHoldingsArray, DeviceConstants.MARKET_VALUE))));
            summary.put(DeviceConstants.DISCREPANCY_COUNT, Integer.toString(iDiscrepancyCount));
            totalSummary.put(DeviceConstants.TOTAL_SUMMARY, summary);
            sorted = getFilteredHoldings(finalHoldingsArray, filterBy, sortOrder, sortBy, reportType, filterObj);
            sorted.put(totalSummary);
        } catch (Exception e) {
            log.error(e);
        }

        return sorted;
    }

	public static void getMarketCap(JSONArray holdingsList, LinkedHashSet<String> symbolToken) {

        try {

            Map<String, AllocationDetails> allocationDetails = Quote.getAllocationDetails(symbolToken);

            for (int i = 0; i < holdingsList.length(); i++) {
                if (!holdingsList.getJSONObject(i).toString().contains(DeviceConstants.TOTAL_SUMMARY)) {

                    SymbolRow holdingsObject = (SymbolRow) holdingsList.getJSONObject(i);
                    String sSymbolToken = (holdingsList.getJSONObject(i)).getJSONObject(SymbolConstants.SYMBOL_OBJ)
                            .getString(SymbolConstants.SYMBOL_TOKEN);

                    if (allocationDetails.containsKey(sSymbolToken)) {
                        AllocationDetails quoteDetails = allocationDetails.get(sSymbolToken);
                        holdingsObject.put(DeviceConstants.MARKET_CAP, quoteDetails.marketCap);

                    } else {
                        holdingsObject.put(DeviceConstants.MARKET_CAP, "--");
                    }

                }
            }
        } catch (Exception e) {
            log.error(e);
        }

    }

    public static void getQuote(JSONArray holdingsList, LinkedHashSet<String> symbolToken, int precision, JSONArray finalHoldingsArray, Map<String, QuoteDetails> getQuoteDetails) throws SQLException {

        for (int i = 0; i < holdingsList.length(); i++) {
            if (!holdingsList.getJSONObject(i).toString().contains(DeviceConstants.TOTAL_SUMMARY)) {
                SymbolRow holdingsObject = (SymbolRow) holdingsList.getJSONObject(i);

                String sSymbolToken = holdingsObject.getSymbolToken();
                if (getQuoteDetails.containsKey(sSymbolToken)) {
                    QuoteDetails quoteDetails = getQuoteDetails.get(sSymbolToken);
                    holdingsObject.put(DeviceConstants.LTP, PriceFormat.priceToRupees(quoteDetails.sLTP));
                    holdingsObject.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quoteDetails.sChange, 2, false));
                    holdingsObject.put(DeviceConstants.CHANGE_PERCENT,
                            Math.abs(Double.parseDouble(quoteDetails.sChangePercent)));
                    holdingsObject.put(DeviceConstants.PREV_CLOSE,
                            PriceFormat.priceToRupees(quoteDetails.sPreviousClose));
                    double quantity = Double.parseDouble(holdingsObject.getString(DeviceConstants.NET_QTY));
                    double ltp = Double.parseDouble(quoteDetails.sLTP);
                    holdingsObject.put(DeviceConstants.MARKET_VALUE, PriceFormat.priceToRupees(String.valueOf(quantity*ltp)));
                    
                    finalHoldingsArray.put(holdingsObject);
                } else {
                    holdingsObject.put(DeviceConstants.LTP, "--");
                    holdingsObject.put(DeviceConstants.CHANGE, "--");
                    holdingsObject.put(DeviceConstants.CHANGE_PERCENT, "--");
                    holdingsObject.put(DeviceConstants.PREV_CLOSE, "--");
                }

            }
        }

    }

    public static void getMarketDetails(JSONArray holdingsList, LinkedHashSet<String> symbolToken, Map<String, QuoteDetails> getQuoteDetails) {

        QuoteDetails quoteDetails = new QuoteDetails();

        try {

            String unrealisedPL;
            Double dayPL = 0.0;
            Double dayPLPercent = 0.0;

            for (int i = 0; i < holdingsList.length(); i++) {

                if (!holdingsList.getJSONObject(i).toString().contains(DeviceConstants.TOTAL_SUMMARY)) {
                    SymbolRow holdingsObject = (SymbolRow) holdingsList.getJSONObject(i);
                    String qty = (holdingsList.getJSONObject(i)).getString(DeviceConstants.NET_QTY);

                    String sSymbolToken = holdingsObject.getSymbolToken();
                    if (getQuoteDetails.containsKey(sSymbolToken)) {
                        quoteDetails = getQuoteDetails.get(sSymbolToken);
                        Double investmentValue = Double.parseDouble(holdingsObject.getString(DeviceConstants.INVESTMENT_VALUE).replaceAll("[,\u20B9]",""));
                        Double marketValue = Double.parseDouble(holdingsObject.getString(DeviceConstants.MARKET_VALUE).replaceAll("[,\u20B9]", ""));
                        unrealisedPL = String.valueOf(marketValue - investmentValue);
                        holdingsObject.put(DeviceConstants.UNREALISED_PROFIT_LOSS,
                                PriceFormat.priceToRupees(unrealisedPL));
                        if (!(quoteDetails.sPreviousClose.equals("0") || quoteDetails.sLTP.equals("0"))) {
                        	dayPL = (Double.parseDouble(quoteDetails.sLTP)
                                    - Double.parseDouble(quoteDetails.sPreviousClose)) * Double.parseDouble(qty);
                            dayPLPercent = (((Double.parseDouble(quoteDetails.sLTP)
                                    - Double.parseDouble(quoteDetails.sPreviousClose))
                                    / Double.parseDouble(quoteDetails.sPreviousClose)) * 100);
                        } else {
                            dayPL = (Double.parseDouble(quoteDetails.sLTP)) * Double.parseDouble(qty);
                            dayPLPercent = 0.0;
                        }
                        holdingsObject.put(DeviceConstants.DAY_PROFIT_LOSS,
                                PriceFormat.priceToRupees(String.valueOf(dayPL)));
                        holdingsObject.put(DeviceConstants.DAY_PROFIT_CHANGE_PERCENTAGE,
                                PriceFormat.formatPrice(String.valueOf(dayPLPercent), 2, false));
                    } else {
                        holdingsObject.put(DeviceConstants.MARKET_VALUE, "--");
                        dayPL = (Double.parseDouble(quoteDetails.sPreviousClose) * Double.parseDouble(qty));
                        dayPLPercent = (((Double.parseDouble(quoteDetails.sPreviousClose))
                                / Double.parseDouble(quoteDetails.sPreviousClose)) * 100);
                        holdingsObject.put(DeviceConstants.DAY_PROFIT_LOSS,
                                PriceFormat.priceToRupees(String.valueOf(dayPL)));
                        holdingsObject.put(DeviceConstants.DAY_PROFIT_CHANGE_PERCENTAGE,
                                PriceFormat.formatPrice(String.valueOf(dayPLPercent), 2, false));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static JSONArray getFilteredHoldings(JSONArray holdingsArray, JSONArray filterBy, String sortOrder,
            String sortBy, String reportType, JSONObject filterObj) {

        JSONArray filtered = new JSONArray();
        JSONArray sortedArray = new JSONArray();
        String removingComma = "";

        JSONObject allowedFilters = FilterList.getAdvancedFilterTypes(reportType, filterObj);

        try {

            if (filterBy.length() == 0 || filterBy.toString().contains(DeviceConstants.ALL_FILTER)
                    || (filterBy.length() == allowedFilters.getJSONArray(DeviceConstants.FILTER_BY).length())) {

            	if(sortBy.isEmpty()) 
            		sortOrder = DeviceConstants.ASCENDING;
                sortedArray = sort(holdingsArray, sortOrder, sortBy);

                return sortedArray;

            }

            else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {

                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {
                        filtered.put(trxnObject);

                    }

                }
            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {

                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if ((filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {
                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) > 0.0) {
                            filtered.put(trxnObject);
                        }
                    }

                }
            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))) {

                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {

                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) > 0.0) {
                            filtered.put(trxnObject);
                        }
                    }
                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {

                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) > 0.0) {
                            filtered.put(trxnObject);
                        }
                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {

                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) < 0.0) {
                            filtered.put(trxnObject);
                        }
                    }

                }
            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {

                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) < 0.0) {
                            filtered.put(trxnObject);
                        }
                    }
                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {

                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {

                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) < 0.0) {
                            filtered.put(trxnObject);
                        }
                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER)) {

                return holdingsArray;
            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")
                            || trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))) {

                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {
                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) > 0.0)
                            filtered.put(trxnObject);
                    }
                }

            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")) {
                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) > 0.0) {
                            filtered.put(trxnObject);
                        }
                    }
                }

            } else if (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) > 0.0) {
                            filtered.put(trxnObject);
                        }
                    }
                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {
                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) < 0.0) {
                            filtered.put(trxnObject);
                        }

                    }
                }
            } else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")) {

                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) < 0.0) {
                            filtered.put(trxnObject);
                        }
                    }
                }

            } else if (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER)
                    && (filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {

                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]",
                                "");
                        if (Double.parseDouble(removingComma) < 0.0) {
                            filtered.put(trxnObject);
                        }
                    }

                }

            } else if ((filterBy.toString().contains(DeviceConstants.PROFIT_FILTER)
                    && filterBy.toString().contains(DeviceConstants.LOSS_FILTER))) {

                return holdingsArray;
            }

            else if (filterBy.toString().contains(DeviceConstants.SMALL_CAP_FILTER)) {

                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Small Cap")) {
                        filtered.put(trxnObject);

                    }

                }

            } else if (filterBy.toString().contains(DeviceConstants.MID_CAP_FILTER)) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Mid Cap")) {
                        filtered.put(trxnObject);

                    }
                }

            } else if (filterBy.toString().contains(DeviceConstants.LARGE_CAP_FILTER)) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    if (trxnObject.getString(DeviceConstants.MARKET_CAP).contains("Large Cap")) {
                        filtered.put(trxnObject);
                    }
                }
            } else if (filterBy.toString().contains(DeviceConstants.PROFIT_FILTER)) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]", "");
                    if (Double.parseDouble(removingComma) > 0.0) {
                        filtered.put(trxnObject);
                    }
                }
            } else if (filterBy.toString().contains(DeviceConstants.LOSS_FILTER)) {
                for (int i = 0; i < holdingsArray.length(); i++) {

                    JSONObject trxnObject = new JSONObject();

                    trxnObject = holdingsArray.getJSONObject(i);
                    removingComma = trxnObject.getString(DeviceConstants.DAY_PROFIT_LOSS).replaceAll("[,\u20B9]", "");

                    if (Double.parseDouble(removingComma) < 0.0) {
                        filtered.put(trxnObject);
                    }
                }
            } else {
                return holdingsArray;
            }

            if(sortBy.isEmpty()) 
        		sortOrder = DeviceConstants.ASCENDING;
            sortedArray = sort(filtered, sortOrder, sortBy);
        } catch (Exception e) {

            log.error(e);
        }
        return sortedArray;
    }

    public static JSONArray sort(JSONArray holdingsArray, final String sortOrder, String sortBy) {

        JSONArray sortedArray = new JSONArray();

        if (sortBy.contains(DeviceConstants.MARKET_VALUE_FILTER)) {
        	
        	List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArray.length(); i++) {
				if(!holdingsArray.getJSONObject(i).getString(DeviceConstants.MARKET_VALUE).equals("--")) 
					toBeSorted.add(holdingsArray.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.MARKET_VALUE, toBeSorted,"[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sortedArray = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sortedArray = new JSONArray(toBeSorted);	
			}
			return sortedArray;

        }
        else if (sortBy.equalsIgnoreCase(DeviceConstants.QUANTITY))
            return SortHelper.sortByInteger(holdingsArray, sortOrder, DeviceConstants.NET_QTY);

        else if (sortBy.contains
                (DeviceConstants.SYMBOL_FILTER) || sortBy.isEmpty()) {
				List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
                for (int i = 0; i < holdingsArray.length(); i++)
                    toBeSorted.add(holdingsArray.getJSONObject(i));
                Collections.sort(toBeSorted, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject obj1, JSONObject obj2) {
                        if (sortOrder.equalsIgnoreCase(DeviceConstants.ASCENDING))
                            return obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
                                    .compareTo(obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ)
                                            .getString(DeviceConstants.SYMBOL));
                        else
                            return obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
                                    .compareTo(obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ)
                                            .getString(DeviceConstants.SYMBOL));
                    }
                });
                sortedArray = new JSONArray(toBeSorted);
                return sortedArray;
		} else {
            return holdingsArray;
        }
    }
    
	private static double getTotalFromArray(JSONArray valueArray, String key) {
		double total = 0.0;
		for(int i=0; i < valueArray.length();i++) {
			String value = valueArray.getJSONObject(i).getString(key).replaceAll("[,\u20B9]","");
			total+= Double.parseDouble(value);
		}
		return total;
	}

}