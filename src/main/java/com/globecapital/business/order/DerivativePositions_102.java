package com.globecapital.business.order;

import java.sql.SQLException;
import java.text.ParseException;
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

import com.globecapital.api.ft.order.GetNetPositionAPI;
import com.globecapital.api.ft.order.GetNetPositionObject;
import com.globecapital.api.ft.order.GetNetPositionRequest;
import com.globecapital.api.ft.order.GetNetPositionResponse;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionAPI;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionRequest;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionResponse;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class DerivativePositions_102 {

	private static Logger log = Logger.getLogger(DerivativePositions_102.class);

	public static JSONObject getDerivativePositions(List<GetNetPositionRows> todayPositionRows, Session session)
			throws JSONException, GCException, ParseException, org.json.me.JSONException, SQLException {
		JSONObject finalObj = new JSONObject();
		JSONArray positionsList = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		
		// GC API call to get the expiry positions with actual buy/sell average prices
		Map<String, JSONObject> mDerivativePositionTokenSegmentAvgPrice = getDerivativePositionFromGCAPI(session.getUserID(),
				session.getAppID());
				
		//FT Today's postion API data parsing 
		Map<String, JSONObject> mTodayPositionTokenSegmentAvgPrice = parseTodaysPositionFTAPIData(todayPositionRows);
		
		//FT API call to get all expiry postions data
		List<GetNetPositionRows> positionRows = getDerivativePositionFromFTAPI(session);
		
		for (int i = 0; i < positionRows.size(); i++) {
			try {

				SymbolRow positions = new SymbolRow();

				GetNetPositionRows positionRow = positionRows.get(i);
				String tokenSegment = positionRow.getToken() + "_" + positionRow.getSegmentId();

				if (!positionRow.getInst().equalsIgnoreCase(DeviceConstants.EQUITIES)
						&& SymbolMap.isValidSymbolTokenSegmentMap(tokenSegment)) {

					/*** Position price info ***/
					String segment = positionRow.getSegmentId();
					SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(tokenSegment);
					int precision = tempSymbolRow.getPrecisionInt();

					/*
					 * TODO: Temporavary fix to avoid changes in muliple places
					 */
					String sNetQty = OrderQty.formatToDevice(positionRow.getNetQty(), tempSymbolRow.getLotSizeInt(),
							tempSymbolRow.getMktSegId());
					int iNetQty = Integer.parseInt(sNetQty);
					String sBuyQty = OrderQty.formatToDevice(positionRow.getBuyQty(), tempSymbolRow.getLotSizeInt(),
							tempSymbolRow.getMktSegId());
					int iBuyQty = Integer.parseInt(sBuyQty);
					String sSellQty = OrderQty.formatToDevice(positionRow.getSellQty(), tempSymbolRow.getLotSizeInt(),
							tempSymbolRow.getMktSegId());
					int iSellQty = Integer.parseInt(sSellQty);
					
					String sAvgNetPrc = "", sAvgBuyPrc = "", sAvgSellPrc = "", sBuyValue = "", sSellValue = "";
					JSONObject priceNRDR = PositionsHelper.getPriceNRDR(positionRow);

					if (mDerivativePositionTokenSegmentAvgPrice.containsKey(tokenSegment) && (!positionRow.getProdType().equalsIgnoreCase(ProductType.FT_INTRADAY_FULL_TEXT))) {
						
						JSONObject derObj = mDerivativePositionTokenSegmentAvgPrice.get(tokenSegment);
						
						if((derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.B)
								&& Integer.parseInt(positionRow.getBuyQty()) != 0)
								||
							(derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.S)
								&& Integer.parseInt(positionRow.getSellQty()) != 0))
						{
							
						
							int iTodayBuyQty = 0, iTodaySellQty =0;
							double dTodayBuyAvg = 0, dTodaySellAvg = 0;
						
							if(mTodayPositionTokenSegmentAvgPrice.containsKey(tokenSegment))
							{
								JSONObject todayObj = mTodayPositionTokenSegmentAvgPrice.get(tokenSegment);
								iTodayBuyQty = Integer.parseInt(todayObj.getString(DeviceConstants.BUY_QTY));
								iTodaySellQty = Integer.parseInt(todayObj.getString(DeviceConstants.SELL_QTY));
								dTodayBuyAvg = Double.parseDouble(todayObj.getString(DeviceConstants.BUY_AVG));
								dTodaySellAvg = Double.parseDouble(todayObj.getString(DeviceConstants.SELL_AVG));
							
							}
							
							double buyValue, sellValue;
						
							if(derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.B))
							{
								double buyAvg = ((Integer.parseInt(derObj.getString(OrderConstants.QTY))
													* Double.parseDouble(derObj.getString(DeviceConstants.AVG_PRICE)))
												+ (iTodayBuyQty * dTodayBuyAvg)) / iBuyQty;
								sAvgBuyPrc = Double.toString(buyAvg);
							
								buyValue = Double.parseDouble(PriceFormat.formatPrice(sAvgBuyPrc, precision, false)
									.replaceAll(",", "")) * iBuyQty;

								buyValue = PositionsHelper.formatValuesForDerivatives(buyValue, positionRow.getExch(), priceNRDR);
								sBuyValue = Double.toString(buyValue);
							
								sSellValue = positionRow.getSellValue();
								sellValue = Double.parseDouble(sSellValue);
								sAvgSellPrc = positionRow.getAvgSellPrc();
							}
							else
							{
								double sellAvg = ((Integer.parseInt(derObj.getString(OrderConstants.QTY))
									* Double.parseDouble(derObj.getString(DeviceConstants.AVG_PRICE)))
								+ (iTodaySellQty * dTodaySellAvg)) / iSellQty;
								sAvgSellPrc = Double.toString(sellAvg);
							
								sellValue = Double.parseDouble(PriceFormat.formatPrice(sAvgSellPrc, precision, false)
									.replaceAll(",", "")) * iSellQty;

								sellValue = PositionsHelper.formatValuesForDerivatives(sellValue, positionRow.getExch(), priceNRDR);
								sSellValue = Double.toString(sellValue);
							
								sBuyValue = positionRow.getBuyValue();
								buyValue = Double.parseDouble(sBuyValue);
								sAvgBuyPrc = positionRow.getAvgBuyPrc();
							
							}
						
							double netAvg = 0.0;
						
							if(iNetQty != 0) {
								double dNetQty = PositionsHelper.formatValuesForDerivatives(Double.valueOf(iNetQty), positionRow.getExch(), priceNRDR);
								netAvg = ((buyValue - sellValue)) / dNetQty;
							}

							sAvgNetPrc = Double.toString(netAvg);
						} else
						{
							sAvgNetPrc = positionRow.getAvgNetPrc();
							sAvgBuyPrc = positionRow.getAvgBuyPrc();
							sBuyValue = positionRow.getBuyValue();
							sAvgSellPrc = positionRow.getAvgSellPrc();
							sSellValue = positionRow.getSellValue();
						}
					} else
					{
						sAvgNetPrc = positionRow.getAvgNetPrc();
						sAvgBuyPrc = positionRow.getAvgBuyPrc();
						sBuyValue = positionRow.getBuyValue();
						sAvgSellPrc = positionRow.getAvgSellPrc();
						sSellValue = positionRow.getSellValue();
					}


					/************************************************** */
					positions.extend(tempSymbolRow.getMinimisedSymbolRow());
					linkedsetSymbolToken.add(tokenSegment);

					// TODO: Profit and Loss calculation
					Double netQty = Double.parseDouble(sNetQty);
					Double avgNetPrice = Double.parseDouble(sAvgNetPrc);

					

					positions.put(OrderConstants.AVG_PRICE, PriceFormat.formatPrice(sAvgNetPrc, precision, false));
					positions.put(DeviceConstants.DISP_QTY, sNetQty);
					positions.put(OrderConstants.QTY, Integer.toString(PositionsHelper.getQty(sNetQty)));
					positions.put(DeviceConstants.BOD_QTY, "--");
					positions.put(DeviceConstants.BOD_RATE, "--");
					positions.put(DeviceConstants.BOD_VALUE, "--");
					positions.put(DeviceConstants.BUY_QTY, sBuyQty);
					positions.put(DeviceConstants.BUY_AVG, PriceFormat.formatPrice(sAvgBuyPrc, precision, false));
					positions.put(DeviceConstants.BUY_VALUE, PriceFormat.formatPrice(sBuyValue, precision, false));
					positions.put(DeviceConstants.SELL_QTY, sSellQty);
					positions.put(DeviceConstants.SELL_AVG, PriceFormat.formatPrice(sAvgSellPrc, precision, false));
					positions.put(DeviceConstants.SELL_VALUE, PriceFormat.formatPrice(sSellValue, precision, false));
					String sProductType = ProductType.formatToDisplay2(positionRow.getProdType(), segment);
					positions.put(OrderConstants.PRODUCT_TYPE, sProductType);
					positions.put(OrderConstants.DISP_PRODUCT_TYPE, sProductType);
				//	positions.put(DeviceConstants.OPEN_VALUE,
				//			PriceFormat.formatPrice(String.valueOf(iNetQty * avgNetPrice), precision, false));
					positions.put(DeviceConstants.CONVERTABLE_TYPES,
							ProductType.getConvertableProductTypes(positionRow.getProdType(), segment));
					positions.put(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
					positions.put(OrderConstants.VALIDITY, Validity.DAY); // For
																			// Square-off
																			// and
																			// Buy
																			// more
																			// navigation
					positions.put(OrderConstants.DISC_QTY, "--");

					positions.put(DeviceConstants.IS_SQUARE_OFF,
							PositionsHelper.isSquareOff(netQty, positionRow.getProdType()));
					positions.put(DeviceConstants.IS_CONVERT,
							PositionsHelper.isSquareOff(netQty, positionRow.getProdType()));
					positions.put(DeviceConstants.IS_BUY_MORE, PositionsHelper.isBuyMore(netQty));
					positions.put(DeviceConstants.IS_SELL_MORE, PositionsHelper.isSellMore(netQty));
					positions.put(OrderConstants.ORDER_ACTION, PositionsHelper.getOrderAction(netQty));
					positions.put(OrderConstants.TO_CONVERT_ACTION, PositionsHelper.getConvertOrderAction(netQty));
					positions.put(OrderConstants.NR_DR, priceNRDR);
					positionsList.put(positions);
					
				
				}
				
				} catch (Exception e) {

					log.error(e);
				}
			} // End of for loop

		

		if (positionsList.length() <= 0)
			throw new GCException(InfoIDConstants.NO_DATA);

		JSONObject summaryObj = PositionsHelper.getMarketValueAndPLUsingLTP(positionsList, linkedsetSymbolToken);
		finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);
//		finalObj.put(DeviceConstants.POSITION_LIST, positionsList);
		finalObj.put(DeviceConstants.POSITION_LIST, sortBySymbol(positionsList));

		return finalObj;
	}
	 private static JSONArray sortBySymbol(JSONArray positionsList) {
			JSONArray sortedArray=null;
			 List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
	         for (int i = 0; i < positionsList.length(); i++)
	             toBeSorted.add(positionsList.getJSONObject(i));
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

	private static Map<String, JSONObject> parseTodaysPositionFTAPIData(List<GetNetPositionRows> todayPositionRows) {
		Map<String, JSONObject> todayPositionTokenSegmentAvgPrice = new HashMap<>();

		for (int i = 0; i < todayPositionRows.size(); i++) {
			GetNetPositionRows positionRow = todayPositionRows.get(i);
			if (SymbolMap.isValidSymbolTokenSegmentMap(positionRow.getToken() + "_" + positionRow.getSegmentId())) {

				SymbolRow symRow = SymbolMap.getSymbolRow(positionRow.getToken() + "_" + positionRow.getSegmentId());
				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.BUY_AVG, positionRow.getAvgBuyPrc());
				obj.put(DeviceConstants.BUY_QTY, 
						OrderQty.formatToDevice(positionRow.getBuyQty(), symRow.getLotSizeInt(), symRow.getMktSegId()));
				obj.put(DeviceConstants.SELL_AVG, positionRow.getAvgSellPrc());
				obj.put(DeviceConstants.SELL_QTY, 
						OrderQty.formatToDevice(positionRow.getSellQty(), symRow.getLotSizeInt(), symRow.getMktSegId()));
				if(!positionRow.getProdType().equalsIgnoreCase(ProductType.FT_INTRADAY_FULL_TEXT)) {
				todayPositionTokenSegmentAvgPrice.put(symRow.getSymbolToken(), obj);
				}

			}
		}

		return todayPositionTokenSegmentAvgPrice;

	}

	private static List<GetNetPositionRows> getDerivativePositionFromFTAPI(Session session)
			throws JSONException, GCException, org.json.me.JSONException {
		
		GetNetPositionRequest positionRequest = new GetNetPositionRequest();
		positionRequest.setUserID(session.getUserID());
		positionRequest.setGroupId(session.getGroupId());
		positionRequest.setJKey(session.getjKey());
		positionRequest.setJSession(session.getjSessionID());
		positionRequest.setPosType(AppConstants.STR_ONE);

		GetNetPositionAPI positionApi = new GetNetPositionAPI();
		GetNetPositionResponse positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class,
				session.getAppID(),"GetNetPosition");

		GetNetPositionObject positionObj = positionResponse.getResponseObject();
		List<GetNetPositionRows> positionRows = positionObj.getObjJSONRows();

		return positionRows;

	}
	
	private static Map<String, JSONObject> getDerivativePositionFromGCAPI(String sUserID, String sAppID)
			throws JSONException, GCException, ParseException {

		Map<String, JSONObject> derivativePositionTokenSegmentAvgPrice = new HashMap<>();

		GetFOCombinedPositionRequest positionRequest = new GetFOCombinedPositionRequest();
		positionRequest.setToken(GCAPIAuthToken.getAuthToken());
		positionRequest.setClientCode(sUserID);
		GetFOCombinedPositionAPI positionsApi = new GetFOCombinedPositionAPI();
		GetFOCombinedPositionResponse positionsResponse = positionsApi.get(positionRequest,
				GetFOCombinedPositionResponse.class, sAppID,DeviceConstants.GC_POSITION_L);
		
		List<GetFOCombinedPositionRows> positionRows = new ArrayList<>();

		if (positionsResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
			positionRequest.setToken(GCAPIAuthToken.getAuthToken());
			positionsResponse = positionsApi.get(positionRequest, GetFOCombinedPositionResponse.class, sAppID,DeviceConstants.GC_POSITION_L);
		}
		
		if(positionsResponse.getStatus().equalsIgnoreCase(DeviceConstants.FALSE))
			return derivativePositionTokenSegmentAvgPrice;
			
		positionRows = positionsResponse.getDetails();
		
		for (int i = 0; i < positionRows.size(); i++) {
			GetFOCombinedPositionRows positionRow = positionRows.get(i);
			String uniqSym = getUniqSymDesc(positionRow.getScripName(), positionRow.getSegments(),
					positionRow.getExchange(), positionRow.getProductType());

			if (SymbolMap.isValidSymbolUniqDescMap(uniqSym)) {

				SymbolRow symRow = SymbolMap.getSymbolUniqDescRow(uniqSym);
				
				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.BUY_OR_SELL, positionRow.getBuySell());
				obj.put(DeviceConstants.AVG_PRICE,  positionRow.getPrice());
				obj.put(OrderConstants.QTY, positionRow.getQty());
				derivativePositionTokenSegmentAvgPrice.put(symRow.getSymbolToken(), obj);

			}
		}

	
		return derivativePositionTokenSegmentAvgPrice;
	}
	
	private static String getUniqSymDesc(String scripName, String segments, String exchange, String productType)
			throws ParseException {

		String[] arrScripName = scripName.split(" ");

		String sScripName = arrScripName[0];
		String sExpiryDate = DateUtils
				.formatDate(arrScripName[1], DeviceConstants.EXPIRY_DATE_FORMAT, DBConstants.UNIQ_DESC_DATE_FORMAT)
				.toUpperCase();
		String sStrikePrice = "", sOptionType = "";
		
		String sExchange = getExchange(segments, exchange);
		
		if(arrScripName.length == 4)
		{
			sOptionType = arrScripName[2];
			sStrikePrice = arrScripName[3];
		}
		else if (arrScripName.length == 3)
			sOptionType = arrScripName[2];
		
		if (productType.equalsIgnoreCase(GCConstants.FUTURE))
		{
			productType = "FUT";
			sOptionType = "";
		}
		else
			productType = "";

		return sScripName + sExpiryDate + productType + sStrikePrice + sOptionType + "_" + sExchange;
	}

	private static String getExchange(String segments, String exchange) {

		if (segments.equalsIgnoreCase(GCConstants.EQUITY_FO))
			return ExchangeSegment.NFO;
		else if (segments.equalsIgnoreCase(GCConstants.COMMODITY_POSITION)) {
			if (exchange.equalsIgnoreCase(ExchangeSegment.MCX))
				return ExchangeSegment.MCX;
			else
				return ExchangeSegment.NCDEX;
		} else if (segments.equalsIgnoreCase(GCConstants.CURRENCY)) {
			if (exchange.equalsIgnoreCase(ExchangeSegment.NSE))
				return ExchangeSegment.NSECDS;
			else
				return ExchangeSegment.BSECDS;
		}
		return exchange;

	}
}
