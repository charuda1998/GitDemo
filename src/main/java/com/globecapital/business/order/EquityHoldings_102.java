package com.globecapital.business.order;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.api.gc.backoffice.GetHoldingsAPI;
import com.globecapital.api.gc.backoffice.GetHoldingsRequest;
import com.globecapital.api.gc.backoffice.GetHoldingsResponse;
import com.globecapital.api.gc.backoffice.GetHoldingsRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class EquityHoldings_102 {
	
	private static Logger log = Logger.getLogger(EquityHoldings_102.class);

	public static JSONObject getHoldings(List<GetNetPositionRows> positionRows, String sUserID,
			String sAppID) throws Exception {
		
		JSONArray finalArr = new JSONArray();
		
		LinkedHashSet<String> linkedHashSetSymbolToken = new LinkedHashSet<>();
		Map<String, JSONObject> mSymbolTokenHolding = new HashMap<>();
		LinkedHashSet<JSONObject> listDiscHolding = new LinkedHashSet<>();
		getHolding(sUserID, sAppID, linkedHashSetSymbolToken, mSymbolTokenHolding, listDiscHolding);
		
		for(int i = 0; i < positionRows.size(); i++)
		{
			GetNetPositionRows positionRow = positionRows.get(i);
			
			String symbolToken = positionRow.getToken() + "_" + positionRow.getSegmentId();
			String sProductType = ProductType.formatToDisplay2(positionRow.getProdType(), 
					positionRow.getSegmentId());
			
			int iPosNetQty = Integer.parseInt(positionRow.getNetQty());
			
			if(ExchangeSegment.isEquitySegment(positionRow.getSegmentId()) 
					&& SymbolMap.isValidSymbolTokenSegmentMap(symbolToken)
					&& sProductType.equalsIgnoreCase(ProductType.DELIVERY)
					&& mSymbolTokenHolding.containsKey(symbolToken)
					&& iPosNetQty < 0)
			{
				JSONObject holding = mSymbolTokenHolding.get(symbolToken);
				
				int iQty = Integer.parseInt(holding.getString(OrderConstants.QTY)) + iPosNetQty;
				
				holding.remove(OrderConstants.QTY);
				holding.put(OrderConstants.QTY, Integer.toString(iQty));
				holding.remove(DeviceConstants.DISP_QTY);
				holding.put(DeviceConstants.DISP_QTY, Integer.toString(iQty));
				holding.remove(DeviceConstants.IS_SQUARE_OFF);
				holding.put(DeviceConstants.IS_SQUARE_OFF, Boolean.toString(getSquareOffFlag(iQty)));
				
			}
		}
		
		for (String hol : mSymbolTokenHolding.keySet()) // Adding remaining holdings
			finalArr.put(mSymbolTokenHolding.get(hol));
		
		for(JSONObject obj : listDiscHolding)
			finalArr.put(obj); 

		JSONObject summaryObj = getAvgPriceAndPL(finalArr, linkedHashSetSymbolToken);
		summaryObj.put(DeviceConstants.DISCREPANCY_COUNT, Integer.toString(listDiscHolding.size()));
		
		JSONObject finalObj = new JSONObject();
		finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);
//		finalObj.put(DeviceConstants.POSITION_LIST, finalArr);
		finalObj.put(DeviceConstants.POSITION_LIST, sortBySymbol(finalArr));
		return finalObj;
		
	}
	
	private static JSONArray sortBySymbol(JSONArray holdingsArray) {
		JSONArray sortedArray=null;
		 List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
         for (int i = 0; i < holdingsArray.length(); i++)
             toBeSorted.add(holdingsArray.getJSONObject(i));
         Collections.sort(toBeSorted, new Comparator<JSONObject>() {
             @Override
             public int compare(JSONObject obj1, JSONObject obj2) {
                    return obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
                             .compareTo(obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ)
                                     .getString(DeviceConstants.SYMBOL));                
             }
         });
         sortedArray = new JSONArray(toBeSorted);
         return sortedArray;
             
		
	}	

	private static void getHolding(String sUserID, String sAppID, 
			LinkedHashSet<String> linkedHashSetSymbolToken, Map<String, JSONObject> mSymbolTokenHolding, 
			LinkedHashSet<JSONObject> listDiscHolding) throws JSONException, GCException {
		
		GetHoldingsRequest holdingsRequest = new GetHoldingsRequest();
        holdingsRequest.setToken(GCAPIAuthToken.getAuthToken());
        holdingsRequest.setClientCode(sUserID);
        GetHoldingsAPI holdingsApi = new GetHoldingsAPI();
        GetHoldingsResponse holdingsResponse = holdingsApi.get(holdingsRequest, GetHoldingsResponse.class, sAppID,DeviceConstants.HOLDINGS_L);

        List<GetHoldingsRows> holdingRows = new ArrayList<>();

        if (holdingsResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
            holdingsRequest.setToken(GCAPIAuthToken.getAuthToken());
            holdingsResponse = holdingsApi.get(holdingsRequest, GetHoldingsResponse.class, sAppID,DeviceConstants.HOLDINGS_L);
        }

        holdingRows = holdingsResponse.getDetails();
		
		for(int i = 0; i < holdingRows.size(); i++)
		{
			GetHoldingsRows holdingsRow = holdingRows.get(i);
			
			String sISIN = holdingsRow.getISIN();
			
			JSONObject symObj = PositionsHelper.getSymbolObj(sISIN);
			
			if(symObj != null)
			{
				String symbolToken = symObj.getJSONObject(SymbolConstants.SYMBOL_OBJ)
						.getString(SymbolConstants.SYMBOL_TOKEN);
				linkedHashSetSymbolToken.add(symbolToken);
				
				int iDiscQty = Integer.parseInt(holdingsRow.getDiscQty());
				int iQty = Integer.parseInt(holdingsRow.getQty());
				
				if(iDiscQty > 0 && iQty > 0)
				{
					mSymbolTokenHolding.put(symbolToken, getHoldingRecord(holdingsRow, symObj, false));
					listDiscHolding.add(getHoldingRecord(holdingsRow, symObj, true));
				}
				else if (iDiscQty == 0 && iQty > 0) 
					mSymbolTokenHolding.put(symbolToken, getHoldingRecord(holdingsRow, symObj, false));
				else if (iQty == 0 && iDiscQty > 0)  
					listDiscHolding.add(getHoldingRecord(holdingsRow, symObj, true));
				
			}
		}
		
	}

	private static JSONObject getHoldingRecord(GetHoldingsRows holdingsRow, JSONObject symObj, boolean isDiscrepancy) {
		
		SymbolRow holding = new SymbolRow();
		holding.extend(symObj);
		
		holding.put(DeviceConstants.BOD_QTY, "--");
		holding.put(DeviceConstants.BOD_RATE, "--");
		holding.put(DeviceConstants.BUY_QTY, "--");
		holding.put(DeviceConstants.BUY_AVG, "--");
		holding.put(DeviceConstants.BUY_VALUE, "--");
		holding.put(DeviceConstants.SELL_QTY, "--");
		holding.put(DeviceConstants.SELL_AVG, "--");
		holding.put(DeviceConstants.SELL_VALUE, "--");
		holding.put(DeviceConstants.BOD_VALUE, "--");
		
		
		holding.put(DeviceConstants.IS_BUY_MORE, "true");
		holding.put(DeviceConstants.IS_SELL_MORE, "false");
		holding.put(OrderConstants.ORDER_ACTION, OrderAction.BUY);
		holding.put(OrderConstants.TO_CONVERT_ACTION, OrderAction.SELL);
		holding.put(OrderConstants.VALIDITY, Validity.DAY); // For Square-off and Buy more
		holding.put(OrderConstants.DISC_QTY, "--");
		holding.put(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
		
		int iQty;
		
		if(isDiscrepancy)
		{
			iQty = Integer.parseInt(holdingsRow.getDiscQty());
			holding.put(OrderConstants.DISP_PRODUCT_TYPE, ProductType.DISCREPANCY);
			holding.put(OrderConstants.PRODUCT_TYPE, ProductType.DELIVERY);
			holding.put(OrderConstants.QTY, holdingsRow.getDiscQty());
			holding.put(DeviceConstants.DISP_QTY, holdingsRow.getDiscQty());
			holding.put(OrderConstants.AVG_PRICE, "NA");
			holding.put(DeviceConstants.IS_DISCREPANCY, "true");
		}
		else
		{
			iQty = Integer.parseInt(holdingsRow.getQty());
			holding.put(OrderConstants.DISP_PRODUCT_TYPE, ProductType.DELIVERY);
			holding.put(OrderConstants.PRODUCT_TYPE, ProductType.DELIVERY);
			holding.put(OrderConstants.QTY, holdingsRow.getQty());
			holding.put(DeviceConstants.DISP_QTY, holdingsRow.getQty());
			holding.put(OrderConstants.AVG_PRICE,
					PriceFormat.formatPrice(holdingsRow.getPrice(), holding.getPrecisionInt(), false));
			holding.put(DeviceConstants.IS_DISCREPANCY, "false");
		}
		
		holding.put(DeviceConstants.IS_SQUARE_OFF, Boolean.toString(getSquareOffFlag(iQty)));
		
		return holding;
		
	}
	
	private static boolean getSquareOffFlag(int iQty)
	{
		if (iQty != 0) 
			return true;
		else
			return false;
	}

	private static JSONObject getAvgPriceAndPL(JSONArray holdings, LinkedHashSet<String> linkedHashSetSymbolToken) 
			throws SQLException {
		
		Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedHashSetSymbolToken);
		
		Double currentValue = 0.0;
		Double investmentAmt = 0.0;
		Double profitLossPercent = 0.0;
		Double totalProfitAndLoss = 0.0;
		
		
		/*** Average price and PL Calculations ***/
		for (int i = 0; i < holdings.length(); i++) {
			
			Double marketValue = 0.0;
			Double openValue = 0.0;
			Double avgprice = 0.0;
			Double profitLoss = 0.0;
			
			try {
				SymbolRow holding = (SymbolRow) holdings.getJSONObject(i);
				
				String symbolToken = holding.getSymbolToken();
				if (mQuoteDetails.containsKey(symbolToken)) {
					QuoteDetails quoteDetails = mQuoteDetails.get(symbolToken);
					int precision = holding.getPrecisionInt();
					holding.put(DeviceConstants.LTP, PriceFormat.formatPrice(quoteDetails.sLTP, precision, false));
					holding.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quoteDetails.sChange, precision, false));
					holding.put(DeviceConstants.CHANGE_PERCENT, PriceFormat.formatPrice(quoteDetails.sChangePercent, precision, false));
					
					Double qty = Double.parseDouble(holding.getString(OrderConstants.QTY));
					Double ltp = Double.parseDouble(quoteDetails.sLTP);

					if (!holding.getString(DeviceConstants.LTP).equals("--")) 
						marketValue = (qty * ltp);
					holding.put(DeviceConstants.MARKET_VALUE,
						PriceFormat.formatPrice(String.valueOf(marketValue), precision, false));
					
					if(!holding.getString(DeviceConstants.LTP).equals("--")
							&& !quoteDetails.sPreviousClose.equals("0"))
					{
						double dayPnL = (ltp - Double.parseDouble(quoteDetails.sPreviousClose)) * qty;
						holding.put(DeviceConstants.DAY_PROFIT_LOSS, 
								PriceFormat.formatPrice(String.valueOf(dayPnL), precision, false));
					}
					else
						holding.put(DeviceConstants.DAY_PROFIT_LOSS, "--");
				
					if(holding.getString(DeviceConstants.IS_DISCREPANCY).equalsIgnoreCase("false"))
					{
						avgprice = Double.parseDouble(
								holding.getString(OrderConstants.AVG_PRICE).replaceAll(",", ""));
						
						currentValue = currentValue + marketValue;
						openValue = (qty * avgprice);
						investmentAmt = investmentAmt + openValue;
						
						holding.put(DeviceConstants.OPEN_VALUE,
							PriceFormat.formatPrice(String.valueOf(openValue), precision, false));
						
						if (!holding.getString(DeviceConstants.LTP).equals("--")) 
							profitLoss = (ltp - avgprice) * qty; 
						
						if(profitLoss == 0)
				            profitLoss = 0.0;
					
						holding.put(DeviceConstants.PROFIT_AND_LOSS, 
							PriceFormat.formatPrice(String.valueOf(profitLoss), precision, false));
						totalProfitAndLoss = totalProfitAndLoss + profitLoss;
					}
					else //for discrepancy holdings open value, profit loss should be shown as NA
					{
						holding.put(DeviceConstants.OPEN_VALUE, "NA");
						holding.put(DeviceConstants.PROFIT_AND_LOSS, "NA");
					}
					
					

				} else {
					holding.put(DeviceConstants.LTP, "--");
					holding.put(DeviceConstants.CHANGE, "--");
					holding.put(DeviceConstants.CHANGE_PERCENT, "--");
					holding.put(DeviceConstants.MARKET_VALUE, "--");
					holding.put(DeviceConstants.PROFIT_AND_LOSS, "--");
					holding.put(DeviceConstants.OPEN_VALUE, "--");
					holding.put(DeviceConstants.DAY_PROFIT_LOSS, "--");
				}
			} catch (Exception e) {
				log.error("Error while calculation equity holdings PnL" + e.getMessage());
			}

		}
		
		profitLossPercent = ((currentValue - investmentAmt) / investmentAmt) * 100;
		
		JSONObject summaryObj = new JSONObject();
		summaryObj.put(DeviceConstants.TOTAL_PROFIT_AND_LOSS,
				PriceFormat.formatPrice(String.valueOf(totalProfitAndLoss), 2, false));
		summaryObj.put(DeviceConstants.DISP_TOTAL_PROFIT_AND_LOSS,
				PriceFormat.numberFormat(totalProfitAndLoss, 2));
		summaryObj.put(DeviceConstants.CURRENT_VALUE,
				PriceFormat.numberFormat(currentValue, 2));
		summaryObj.put(DeviceConstants.INVESTMENT_AMT,
				PriceFormat.numberFormat(investmentAmt, 2));
		summaryObj.put(DeviceConstants.TOTAL_PROFIT_LOSS_PERCENTAGE,
				PriceFormat.formatPrice(String.valueOf(profitLossPercent), 2, false) + "%");
		
		return summaryObj;
		
	}

}
