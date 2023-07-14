package com.globecapital.business.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.edis.GetEDISConfigResponseObject;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.api.gc.backoffice.GetHoldingsAPI;
import com.globecapital.api.gc.backoffice.GetHoldingsRequest;
import com.globecapital.api.gc.backoffice.GetHoldingsResponse;
import com.globecapital.api.gc.backoffice.GetHoldingsRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.business.edis.EDISHelper;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.business.report.SortHelper;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.log.Logger;

public class EquityHoldings_104 {
	
	private static Logger log = Logger.getLogger(EquityHoldings_104.class);
	
	private static Map<String, GetHoldingsResponse> clientHoldings = new HashMap<>();
	/*public static JSONObject getHoldings(List<GetNetPositionRows> positionRows, String sUserID,
			String sAppID, JSONObject filterObj) throws Exception {
		
		JSONArray finalArr = new JSONArray();
		String optedSortBy = filterObj.getString(DeviceConstants.OPTED_SORT_BY);
		String optedSortOrder = filterObj.getString(DeviceConstants.OPTED_SORT_ORDER);
		if(optedSortOrder.isEmpty())
			optedSortOrder = DeviceConstants.ASCENDING;
		if(optedSortBy.isEmpty())
			optedSortBy = DeviceConstants.ALPHABETICALLY;
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
					&& (sProductType.equalsIgnoreCase(ProductType.DELIVERY) 
					|| sProductType.equalsIgnoreCase(ProductType.MTF))
					&& mSymbolTokenHolding.containsKey(symbolToken)
					&& iPosNetQty < 0)
			{
				JSONObject holding = mSymbolTokenHolding.get(symbolToken);
				
				int iQty = Integer.parseInt(holding.getString(OrderConstants.QTY)) + iPosNetQty;
				int mtfQty = Integer.parseInt(holding.getString(OrderConstants.MTF_QTY));

				int nrmlQty = Integer.parseInt(holding.getString(DeviceConstants.NRML_QTY));
				holding.remove(OrderConstants.QTY);
				holding.put(OrderConstants.QTY, Integer.toString(iQty));
				holding.remove(DeviceConstants.DISP_QTY);
				holding.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(iQty));
				if(sProductType.equalsIgnoreCase(ProductType.DELIVERY)) {
					nrmlQty+= iPosNetQty;
					holding.put(DeviceConstants.NRML_QTY, String.valueOf(nrmlQty + iPosNetQty));
					holding.put(DeviceConstants.DISP_NRML_QTY, PriceFormat.addComma(nrmlQty + iPosNetQty));
				}
				else if (sProductType.equalsIgnoreCase(ProductType.MTF)) {
					mtfQty+= iPosNetQty;
					holding.put(OrderConstants.MTF_QTY, String.valueOf(mtfQty + iPosNetQty));
					holding.put(DeviceConstants.DISP_MTF_QTY, PriceFormat.addComma(mtfQty + iPosNetQty));
				}
				holding.remove(DeviceConstants.IS_SQUARE_OFF);
				holding.remove(DeviceConstants.IS_MTF_SQUARE_OFF);
				holding.remove(DeviceConstants.IS_NRML_SQUARE_OFF);
				
				getProductTypeByQty(mtfQty, nrmlQty, holding);
				generateSquareOffFlags(mtfQty, nrmlQty, holding);
				
				holding.put(DeviceConstants.NRML_PRODUCT_TYPE, ProductType.DELIVERY);
				holding.put(DeviceConstants.MTF_PRODUCT_TYPE, ProductType.MTF);
			}
		}
		
		for (String hol : mSymbolTokenHolding.keySet()) // Adding remaining holdings
			finalArr.put(mSymbolTokenHolding.get(hol));
		
		for(JSONObject obj : listDiscHolding)
			finalArr.put(obj); 

		JSONObject summaryObj = getAvgPriceAndPL(finalArr, linkedHashSetSymbolToken);
		summaryObj.put(DeviceConstants.DISCREPANCY_COUNT, Integer.toString(listDiscHolding.size()));
		summaryObj.put(DeviceConstants.RECORDS_COUNT, String.valueOf(finalArr.length()));
		JSONObject finalObj = new JSONObject();
//		finalObj.put(DeviceConstants.POSITION_LIST, finalArr);
		finalArr = sort(finalArr, optedSortOrder, optedSortBy);
		finalObj.put(DeviceConstants.POSITION_LIST, finalArr);
		finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);
		return finalObj;
		
	} */
	
	public static JSONObject getHoldings(List<GetNetPositionRows> positionRows, String sUserID,
			Session session, JSONObject filterObj,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws Exception {
		
		Map<String, List<String>> isinSymbolTokenMap = new HashMap<>();
		JSONArray finalArr = new JSONArray();
		String optedSortBy = filterObj.getString(DeviceConstants.OPTED_SORT_BY);
		String optedSortOrder = filterObj.getString(DeviceConstants.OPTED_SORT_ORDER);
		if(optedSortOrder.isEmpty())
			optedSortOrder = DeviceConstants.ASCENDING;
		if(optedSortBy.isEmpty())
			optedSortBy = DeviceConstants.ALPHABETICALLY;
		LinkedHashSet<String> linkedHashSetSymbolToken = new LinkedHashSet<>();
		Map<String, JSONObject> mSymbolTokenHolding = new HashMap<>();
		LinkedHashSet<JSONObject> listDiscHolding = new LinkedHashSet<>();
		Map<String, Integer> positionRowsISINMap = parseTodaysPositionISINQtyData(positionRows);
		Map<String, GetNetPositionRows> positionRowsMap = parseTodaysPositionFTAPIData(positionRows);
		getHolding(sUserID, session, linkedHashSetSymbolToken, mSymbolTokenHolding, listDiscHolding, isinSymbolTokenMap, positionRowsISINMap);

		for (Entry<String, JSONObject> entry : mSymbolTokenHolding.entrySet()) {
		    
			GetNetPositionRows positionRow = null;
			List<String> associatedSymbolTokenList = new ArrayList<>();
			if(isinSymbolTokenMap.containsKey(entry.getValue().getString(DeviceConstants.ISIN)))
				associatedSymbolTokenList = isinSymbolTokenMap.get(entry.getValue().getString(DeviceConstants.ISIN));
			int iPosNetQty = 0;
			int convertibleProdTypeNetQty = 0;
			String convertibleProdType = entry.getValue().getString(OrderConstants.DISP_PRODUCT_TYPE).equalsIgnoreCase(ProductType.MTF) ? ProductType.DELIVERY : ProductType.MTF;

			String isModificationRequired = entry.getValue().getString(DeviceConstants.REQ_FLAG);
			if(isModificationRequired.equals("true") && positionRowsISINMap.containsKey(entry.getValue().getString(DeviceConstants.ISIN))) {
				iPosNetQty = positionRowsISINMap.get(entry.getValue().getString(DeviceConstants.ISIN));
				isModificationRequired = "false";
			}	
			for(String token : associatedSymbolTokenList) {
				String productType = entry.getValue().getString(OrderConstants.DISP_PRODUCT_TYPE);
				if(positionRowsMap.containsKey(token+"_"+ productType)) {
					 
					int netQty = Integer.parseInt(positionRowsMap.get(token+"_"+productType).getNetQty());
					positionRow = positionRowsMap.get(token+"_"+productType);
					if(isModificationRequired.equals("true")) {
						if(netQty < 0)
							iPosNetQty+= netQty;
						if(productType.equalsIgnoreCase(ProductType.DELIVERY)) {
							if(positionRowsMap.containsKey(token+"_"+ ProductType.MTF)) {
								int qty = Integer.parseInt(positionRowsMap.get(token+"_"+ProductType.MTF).getNetQty());
								if(qty < 0)
									convertibleProdTypeNetQty+= qty;
							}
						}else if(productType.equalsIgnoreCase(ProductType.MTF)) {
							if(positionRowsMap.containsKey(token+"_"+ ProductType.DELIVERY)) {
								int qty = Integer.parseInt(positionRowsMap.get(token+"_"+ProductType.DELIVERY).getNetQty());
								if(qty < 0)
									convertibleProdTypeNetQty+= qty;
							}
						}
					}
				}
				else if(positionRowsMap.containsKey(token+"_"+ convertibleProdType)) {
					 
					int netQty = Integer.parseInt(positionRowsMap.get(token+"_"+convertibleProdType).getNetQty());
					positionRow = positionRowsMap.get(token+"_"+convertibleProdType);
					if(isModificationRequired.equals("true")) {
						if(netQty < 0)
							iPosNetQty+= netQty;
						if(convertibleProdType.equalsIgnoreCase(ProductType.DELIVERY)) {
							if(positionRowsMap.containsKey(token+"_"+ ProductType.MTF)) {
								int qty = Integer.parseInt(positionRowsMap.get(token+"_"+ProductType.MTF).getNetQty());
								if(qty < 0)
									convertibleProdTypeNetQty+= qty;
							}
						}else if(convertibleProdType.equalsIgnoreCase(ProductType.MTF)) {
							if(positionRowsMap.containsKey(token+"_"+ ProductType.DELIVERY)) {
								int qty = Integer.parseInt(positionRowsMap.get(token+"_"+ProductType.DELIVERY).getNetQty());
								if(qty < 0)
									convertibleProdTypeNetQty+= qty;
							}
						}
					}
				} 
			}
			
			if((iPosNetQty < 0 || convertibleProdTypeNetQty < 0) && Objects.nonNull(positionRow)){
				
				String symbolToken = positionRow.getToken() + "_" + positionRow.getSegmentId();
				String sProductType = ProductType.formatToDisplay2(positionRow.getProdType(), 
						positionRow.getSegmentId());
				
				JSONObject holding = entry.getValue();
				if(ExchangeSegment.isEquitySegment(positionRow.getSegmentId()) 
						&& SymbolMap.isValidSymbolTokenSegmentMap(symbolToken)
						&& (sProductType.equalsIgnoreCase(ProductType.DELIVERY) 
						|| sProductType.equalsIgnoreCase(ProductType.MTF))
						&& mSymbolTokenHolding.containsKey(entry.getKey())
						&& (iPosNetQty < 0 || convertibleProdTypeNetQty < 0)
						&& holding.getString(DeviceConstants.REQ_FLAG).equals("true"))
				{

					int iQty = Integer.parseInt(holding.getString(OrderConstants.QTY)) + iPosNetQty + convertibleProdTypeNetQty;
					int mtfQty = Integer.parseInt(holding.getString(OrderConstants.MTF_QTY));
					
					int nrmlQty = Integer.parseInt(holding.getString(DeviceConstants.NRML_QTY));
					holding.remove(OrderConstants.QTY);
					holding.put(OrderConstants.QTY, Integer.toString(iQty));
					holding.remove(DeviceConstants.DISP_QTY);
					holding.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(iQty));
					if(sProductType.equalsIgnoreCase(ProductType.DELIVERY)) {
						nrmlQty+= iPosNetQty;
						if(convertibleProdType == sProductType)
							nrmlQty+= convertibleProdTypeNetQty;
						else {
							mtfQty+= convertibleProdTypeNetQty;
							holding.put(OrderConstants.MTF_QTY, String.valueOf(mtfQty));
							holding.put(DeviceConstants.DISP_MTF_QTY, PriceFormat.addComma(mtfQty));
						}
						holding.put(DeviceConstants.NRML_QTY, String.valueOf(nrmlQty));
						holding.put(DeviceConstants.DISP_NRML_QTY, PriceFormat.addComma(nrmlQty));
					}
					else if (sProductType.equalsIgnoreCase(ProductType.MTF)) {
						mtfQty+= iPosNetQty;
						if(convertibleProdType == sProductType)
							mtfQty+= convertibleProdTypeNetQty; 
						else {
							nrmlQty+= convertibleProdTypeNetQty;
							holding.put(OrderConstants.MTF_QTY, String.valueOf(nrmlQty));
							holding.put(DeviceConstants.DISP_MTF_QTY, PriceFormat.addComma(nrmlQty));
						}
						holding.put(OrderConstants.MTF_QTY, String.valueOf(mtfQty));
						holding.put(DeviceConstants.DISP_MTF_QTY, PriceFormat.addComma(mtfQty));
					}
					holding.remove(DeviceConstants.IS_SQUARE_OFF);
					holding.remove(DeviceConstants.IS_MTF_SQUARE_OFF);
					holding.remove(DeviceConstants.IS_NRML_SQUARE_OFF);
					
					getProductTypeByQty(mtfQty, nrmlQty, holding);
					generateSquareOffFlags(mtfQty, nrmlQty, holding);
					
					holding.put(DeviceConstants.NRML_PRODUCT_TYPE, ProductType.DELIVERY);
					holding.put(DeviceConstants.MTF_PRODUCT_TYPE, ProductType.MTF);
				}
				finalArr.put(holding);
			}
			else
				finalArr.put(entry.getValue());
		}
		
		for(JSONObject obj : listDiscHolding)
			finalArr.put(obj); 

		JSONObject summaryObj = getAvgPriceAndPL(finalArr, linkedHashSetSymbolToken);
		summaryObj.put(DeviceConstants.DISCREPANCY_COUNT, Integer.toString(listDiscHolding.size()));
		summaryObj.put(DeviceConstants.RECORDS_COUNT, String.valueOf(finalArr.length()));
		JSONObject finalObj = new JSONObject();
		finalArr = sort(finalArr, optedSortOrder, optedSortBy);
		finalObj.put(DeviceConstants.POSITION_LIST, finalArr);
		finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);
		try {
			boolean poaStatus = Boolean.parseBoolean(session.getUserInfo().getString(UserInfoConstants.POA_STATUS));
			if(!poaStatus) {
				JSONObject edisConfigJSON = new JSONObject();
				if(session.getUserInfo().has(UserInfoConstants.EDIS_CONFIG_DETAILS))
					edisConfigJSON = new JSONObject(AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), session.getUserInfo().getString(UserInfoConstants.EDIS_CONFIG_DETAILS)));
				GetEDISConfigResponseObject configResponseObject = EDISHelper.fetchEDISConfigDetails(session, edisConfigJSON,servletContext,gcRequest,gcResponse);
				String depository = configResponseObject.getDepository();
				if(depository.equals(DeviceConstants.DEPOSITORY_CDSL) && !poaStatus) {
					//List<JSONObject> qtyDetails = EDISHelper.getEDISQuantityDetailsForPreFill(session);
					//if(qtyDetails.isEmpty())
						finalObj.put(DeviceConstants.IS_AUTH_REQUIRED, "false");
					//else {
					//	finalObj.put(DeviceConstants.IS_AUTH_REQUIRED, "false");
						//finalObj.put(DeviceConstants.EDIS_APPROVAL_DETAILS, qtyDetails);
					//}
				}
			}
			else
				finalObj.put(DeviceConstants.IS_AUTH_REQUIRED, "false");
		}catch(GCException ex) {
			finalObj.put(DeviceConstants.IS_AUTH_REQUIRED, "false");
		}
		return finalObj;
	}
	
	private static Map<String, GetNetPositionRows> parseTodaysPositionFTAPIData(List<GetNetPositionRows> todayPositionRows) {
		Map<String, GetNetPositionRows> todayPositionTokenSegmentAvgPrice = new HashMap<>();

		for (int i = 0; i < todayPositionRows.size(); i++) {
			GetNetPositionRows positionRow = todayPositionRows.get(i);
			if (SymbolMap.isValidSymbolTokenSegmentMap(positionRow.getToken() + "_" + positionRow.getSegmentId())) {
				SymbolRow symRow = SymbolMap.getSymbolRow(positionRow.getToken() + "_" + positionRow.getSegmentId());
				todayPositionTokenSegmentAvgPrice.put(symRow.getSymbolToken()+"_"+positionRow.getProdType(), positionRow);
			}
		}
		return todayPositionTokenSegmentAvgPrice;
	}
	
	private static Map<String, Integer> parseTodaysPositionISINQtyData(List<GetNetPositionRows> todayPositionRows) {
		Map<String, Integer> isinQtyMap = new HashMap<>();

		for (int i = 0; i < todayPositionRows.size(); i++) {
			GetNetPositionRows positionRow = todayPositionRows.get(i);
			if (SymbolMap.isValidSymbolTokenSegmentMap(positionRow.getToken() + "_" + positionRow.getSegmentId())) {
				SymbolRow symRow = SymbolMap.getSymbolRow(positionRow.getToken() + "_" + positionRow.getSegmentId());
				if(positionRow.getProdType().equalsIgnoreCase(ProductType.DELIVERY) || positionRow.getProdType().equalsIgnoreCase(ProductType.MTF)) {
					if(isinQtyMap.containsKey(symRow.getISIN()))
						isinQtyMap.put(symRow.getISIN(), isinQtyMap.get(symRow.getISIN()) + Integer.parseInt(positionRow.getNetQty()));
					else	
						isinQtyMap.put(symRow.getISIN(), Integer.parseInt(positionRow.getNetQty()));
				}	
			}
		}
		return isinQtyMap;
	}
	
	private static void getHolding(String sUserID, Session session, 
			LinkedHashSet<String> linkedHashSetSymbolToken, Map<String, JSONObject> mSymbolTokenHolding, 
			LinkedHashSet<JSONObject> listDiscHolding, Map<String, List<String>> isinSymbolTokenMap, Map<String, Integer> positionRowsISINMap) throws JSONException, GCException {
		
		GetHoldingsRequest holdingsRequest = new GetHoldingsRequest();
        holdingsRequest.setToken(GCAPIAuthToken.getAuthToken());
        holdingsRequest.setClientCode(sUserID);
        GetHoldingsAPI holdingsApi = new GetHoldingsAPI();
        GetHoldingsResponse holdingsResponse = null;
        try {
        	if(clientHoldings.containsKey(session.getUserID())) {
        		holdingsResponse = clientHoldings.get(session.getUserID());
        	}else {
        		String holdings=HoldingsDB.toCheckHoldingEntry(sUserID);
        		if(holdings!=null) {
        			holdingsResponse = new Gson().fromJson(holdings,GetHoldingsResponse.class);
        			clientHoldings.put(session.getUserID() , holdingsResponse);
        		}else {
        			holdingsResponse = holdingsApi.get(holdingsRequest, GetHoldingsResponse.class, session.getAppID(),DeviceConstants.HOLDINGS_L);
    				if(holdingsResponse.getStatus()) {
    					HoldingsDB.updateHoldingsDB(sUserID,new Gson().toJson(holdingsResponse));
    					clientHoldings.put(session.getUserID() , holdingsResponse);
    				}
        		}
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.debug(e);
			throw new RequestFailedException();
		}   catch(Exception e) {
			log.debug(e);
			throw new RequestFailedException();
		}
        List<GetHoldingsRows> holdingRows = new ArrayList<>();

        if (holdingsResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
            holdingsRequest.setToken(GCAPIAuthToken.getAuthToken());
            holdingsResponse = holdingsApi.get(holdingsRequest, GetHoldingsResponse.class, session.getAppID(),DeviceConstants.HOLDINGS_L);           
        }

        holdingRows = holdingsResponse.getDetails();
		
		for(int i = 0; i < holdingRows.size(); i++)
		{
			GetHoldingsRows holdingsRow = holdingRows.get(i);
			
			String sISIN = holdingsRow.getISIN();
			
			JSONObject symObj = PositionsHelper_101.getSymbolObj(sISIN);
			
			if(symObj != null)
			{
				String symbolToken = symObj.getJSONObject(SymbolConstants.SYMBOL_OBJ)
						.getString(SymbolConstants.SYMBOL_TOKEN);
				if(symbolToken.endsWith(ExchangeSegment.NSE_SEGMENT_ID)) {
					if(SymbolMap.isValidSymbol(sISIN+"_"+ExchangeSegment.BSE_SEGMENT_ID)) {
						isinSymbolTokenMap.put(sISIN, Arrays.asList(symbolToken, SymbolMap.getISINSymbolRow(sISIN+"_"+ExchangeSegment.BSE_SEGMENT_ID).getSymbolToken()));
					}
					else {
						isinSymbolTokenMap.put(sISIN, Arrays.asList(symbolToken));
					}
				}else if(symbolToken.endsWith(ExchangeSegment.BSE_SEGMENT_ID)) {
					if(SymbolMap.isValidSymbol(sISIN+"_"+ExchangeSegment.NSE_SEGMENT_ID))
						isinSymbolTokenMap.put(sISIN, Arrays.asList(symbolToken, SymbolMap.getISINSymbolRow(sISIN+"_"+ExchangeSegment.NSE_SEGMENT_ID).getSymbolToken()));
					else
						isinSymbolTokenMap.put(sISIN, Arrays.asList(symbolToken));
				}
				linkedHashSetSymbolToken.add(symbolToken);
				
				int iDiscQty = Integer.parseInt(holdingsRow.getDiscQty());
				int iQty = Integer.parseInt(holdingsRow.getQty());
				
				if(iDiscQty > 0 && iQty > 0)
				{
					if(positionRowsISINMap.containsKey(sISIN) && Math.abs(positionRowsISINMap.get(sISIN)) <= iDiscQty)
						mSymbolTokenHolding.put(symbolToken, getHoldingRecord(holdingsRow, symObj, false, false, positionRowsISINMap, sISIN));
					else
						mSymbolTokenHolding.put(symbolToken, getHoldingRecord(holdingsRow, symObj, false, true, positionRowsISINMap, sISIN));
					listDiscHolding.add(getHoldingRecord(holdingsRow, symObj, true, true, positionRowsISINMap, sISIN));
				}
				else if (iDiscQty == 0 && iQty > 0) 
					mSymbolTokenHolding.put(symbolToken, getHoldingRecord(holdingsRow, symObj, false, true, positionRowsISINMap, sISIN));
				else if (iQty == 0 && iDiscQty > 0) { 
					listDiscHolding.add(getHoldingRecord(holdingsRow, symObj, true, false, positionRowsISINMap, sISIN));
				}
				
			}
		}
		
	}
	
	private static JSONObject getHoldingRecord(GetHoldingsRows holdingsRow, JSONObject symObj, boolean isDiscrepancy, boolean isQtyAdjustRequired, Map<String, Integer> positionRowsISINMap, String sISIN) {
		
		int mtfQty = Integer.parseInt(holdingsRow.getMTFQty());
		int nrmlQty = Integer.parseInt(holdingsRow.getQty()) - Integer.parseInt(holdingsRow.getMTFQty());
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
		
		holding.put(DeviceConstants.ISIN, holdingsRow.getISIN());
		holding.put(DeviceConstants.IS_BUY_MORE, "true");
		holding.put(DeviceConstants.IS_SELL_MORE, "false");
		holding.put(OrderConstants.ORDER_ACTION, OrderAction.BUY);
		holding.put(OrderConstants.TO_CONVERT_ACTION, OrderAction.SELL);
		holding.put(OrderConstants.VALIDITY, Validity.DAY); // For Square-off and Buy more
		holding.put(OrderConstants.DISC_QTY, "--");
		holding.put(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
		holding.put(OrderConstants.MTF_INCLUDED, String.valueOf((Integer.parseInt(holdingsRow.getMTFQty()) > 0)));
		holding.put(OrderConstants.MTF_QTY, String.valueOf(mtfQty));
		holding.put(DeviceConstants.DISP_MTF_QTY, PriceFormat.addComma(mtfQty));
		holding.put(OrderConstants.SHOW_ADD_WATCHLIST, "true");
		holding.put(OrderConstants.SHOW_CHART, "true");
		holding.put(OrderConstants.SHOW_QUOTE, "true");
		
		int iQty;
		
		if(isDiscrepancy)
		{
			if(positionRowsISINMap.containsKey(sISIN) && positionRowsISINMap.get(sISIN) < 0)
				if(Integer.parseInt(holdingsRow.getDiscQty()) > Math.abs(positionRowsISINMap.get(sISIN)))
						iQty = Integer.parseInt(holdingsRow.getDiscQty()) - Math.abs(positionRowsISINMap.get(sISIN));
				else
						iQty = 0;
			else	
				iQty = Integer.parseInt(holdingsRow.getDiscQty());
			holding.put(OrderConstants.DISP_PRODUCT_TYPE, ProductType.DISCREPANCY);
			holding.put(OrderConstants.PRODUCT_TYPE, ProductType.DELIVERY);
			holding.put(OrderConstants.QTY, String.valueOf(iQty));
			holding.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(iQty));
			holding.put(OrderConstants.AVG_PRICE, "NA");
			holding.put(DeviceConstants.IS_DISCREPANCY, "true");
			holding.put(DeviceConstants.REQ_FLAG , Boolean.toString(isQtyAdjustRequired));
			if(positionRowsISINMap.containsKey(sISIN) && positionRowsISINMap.get(sISIN) < 0)
				positionRowsISINMap.put(sISIN, positionRowsISINMap.get(sISIN) + Math.abs(Integer.parseInt(holdingsRow.getDiscQty())));
		}
		else
		{
			iQty = Integer.parseInt(holdingsRow.getQty());
			mtfQty = Integer.parseInt(holdingsRow.getMTFQty());
			getProductTypeByQty(mtfQty, nrmlQty, holding);
			
			holding.put(OrderConstants.QTY, Integer.toString(iQty));
			holding.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(iQty));
			holding.put(DeviceConstants.NRML_QTY, String.valueOf(nrmlQty));
			holding.put(DeviceConstants.DISP_NRML_QTY, PriceFormat.addComma(nrmlQty));
			holding.put(OrderConstants.AVG_PRICE,
					PriceFormat.formatPrice(holdingsRow.getPrice(), holding.getPrecisionInt(), false));
			holding.put(DeviceConstants.IS_DISCREPANCY, "false");
			holding.put(DeviceConstants.REQ_FLAG , Boolean.toString(isQtyAdjustRequired));
		}
		
		generateSquareOffFlags(mtfQty, nrmlQty, holding);
		return holding;
		
	}

	private static void getProductTypeByQty(int mtfQty, int nrmlQty, JSONObject holding) {
		if(nrmlQty == 0 && mtfQty > 0) {
			holding.put(OrderConstants.DISP_PRODUCT_TYPE, ProductType.MTF);
			holding.put(OrderConstants.PRODUCT_TYPE, ProductType.MTF);
		}else if(nrmlQty > 0 && mtfQty == 0) {
			holding.put(OrderConstants.DISP_PRODUCT_TYPE, ProductType.DELIVERY);
			holding.put(OrderConstants.PRODUCT_TYPE, ProductType.DELIVERY);
		}else {
			holding.put(OrderConstants.DISP_PRODUCT_TYPE, ProductType.DELIVERY);
			holding.put(OrderConstants.PRODUCT_TYPE, ProductType.DELIVERY);
		}
		holding.put(DeviceConstants.NRML_PRODUCT_TYPE, ProductType.DELIVERY);
		holding.put(DeviceConstants.MTF_PRODUCT_TYPE, ProductType.MTF);
	}

	private static void generateSquareOffFlags(int mtfQty, int nrmlQty, JSONObject holding) {
		if(!getSquareOffFlag(nrmlQty) &&  getSquareOffFlag(mtfQty)) {
			holding.put(DeviceConstants.IS_MTF, Boolean.toString(true));
			holding.put(OrderConstants.MTF_INCLUDED, Boolean.toString(false));
			holding.put(DeviceConstants.IS_SQUARE_OFF, Boolean.toString(true));
			holding.put(DeviceConstants.IS_NRML_SQUARE_OFF, Boolean.toString(false));
			holding.put(DeviceConstants.IS_MTF_SQUARE_OFF, Boolean.toString(false));
		}
		else {
			if(mtfQty > 0) {
				holding.put(OrderConstants.MTF_INCLUDED, Boolean.toString(true));
				holding.put(DeviceConstants.IS_SQUARE_OFF, Boolean.toString(false));
				holding.put(DeviceConstants.IS_NRML_SQUARE_OFF, Boolean.toString(true));
				holding.put(DeviceConstants.IS_MTF_SQUARE_OFF, Boolean.toString(true));
			}else {
				holding.put(OrderConstants.MTF_INCLUDED, Boolean.toString(false));
				holding.put(DeviceConstants.IS_SQUARE_OFF, Boolean.toString(true));
				holding.put(DeviceConstants.IS_NRML_SQUARE_OFF, Boolean.toString(false));
				holding.put(DeviceConstants.IS_MTF_SQUARE_OFF, Boolean.toString(false));
			}
			holding.put(DeviceConstants.IS_MTF, Boolean.toString(false));
		}
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
		Double todaysPL = 0.0;
		Double dayPLPercent = 0.0;
		Double yesterdayPl = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");
		
		/*** Average price and PL Calculations ***/
		for (int i = 0; i < holdings.length(); i++) {
			
			Double marketValue = 0.0;
			Double openValue = 0.0;
			Double avgprice = 0.0;
			Double profitLoss = 0.0;
			Double prevPl = 0.0;
			
			try {
				SymbolRow holding = (SymbolRow) holdings.getJSONObject(i);
				
				String sSymbolToken = (holdings.getJSONObject(i)).getJSONObject(SymbolConstants.SYMBOL_OBJ)
						.getString(SymbolConstants.SYMBOL_TOKEN);
				
				if (mQuoteDetails.containsKey(sSymbolToken)) {
					QuoteDetails quoteDetails = mQuoteDetails.get(sSymbolToken);
					int precision = holding.getPrecisionInt();
					holding.put(DeviceConstants.LTP, PriceFormat.formatPrice(quoteDetails.sLTP, precision, false));
					holding.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quoteDetails.sChange, precision, false));
					holding.put(DeviceConstants.CHANGE_PERCENT, PriceFormat.formatPrice(quoteDetails.sChangePercent, precision, false));
					if ( !holding.getString(DeviceConstants.LTP).equals("--")) {
						Double qty = Double.parseDouble(holding.getString(OrderConstants.QTY));
						Double ltp = Double.parseDouble(quoteDetails.sLTP);
	
							marketValue = Double.parseDouble(String.valueOf((new BigDecimal(String.valueOf(qty)).multiply(new BigDecimal(String.valueOf(ltp))))));
						holding.put(DeviceConstants.MARKET_VALUE,
							PriceFormat.formatPrice(String.valueOf(marketValue), precision, false));
						
						if(!holding.getString(DeviceConstants.LTP).equals("--")
								&& !quoteDetails.sPreviousClose.equals("0"))
						{
							double dayPnL = Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(ltp)).subtract((new BigDecimal(quoteDetails.sPreviousClose))).multiply(new BigDecimal(String.valueOf(qty)))));
							holding.put(DeviceConstants.DAY_PROFIT_LOSS, 
									PriceFormat.formatPrice(String.valueOf(dayPnL), precision, false));
							prevPl = Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(new BigDecimal(quoteDetails.sPreviousClose).multiply(new BigDecimal(String.valueOf(qty)))))));
							yesterdayPl+= prevPl;
							todaysPL+= dayPnL;
						}
						else
							holding.put(DeviceConstants.DAY_PROFIT_LOSS, "--");
					
						if(holding.getString(DeviceConstants.IS_DISCREPANCY).equalsIgnoreCase("false"))
						{
							avgprice = Double.parseDouble(
									holding.getString(OrderConstants.AVG_PRICE).replaceAll(",", ""));
							
							currentValue = Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(currentValue)).add(new BigDecimal(String.valueOf(marketValue)))));
							openValue = Double.parseDouble(String.valueOf((new BigDecimal(qty).multiply(new BigDecimal(avgprice)))));
							investmentAmt = Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(investmentAmt)).add(new BigDecimal(String.valueOf(openValue)))));
							holding.put(DeviceConstants.OPEN_VALUE,
									PriceFormat.formatPrice(String.valueOf(openValue), precision, false));
	
							if(openValue !=0)
								holding.put(DeviceConstants.PNL_PERCENT, String.valueOf(Double.parseDouble(String.valueOf((new BigDecimal(String.valueOf(marketValue)).subtract(new BigDecimal(String.valueOf(openValue)))).divide(new BigDecimal(String.valueOf(openValue)),2, RoundingMode.HALF_UP)))*100));
							else
								holding.put(DeviceConstants.PNL_PERCENT, "--");
							
							if (!holding.getString(DeviceConstants.LTP).equals("--")) 
								profitLoss = Double.parseDouble(String.valueOf((new BigDecimal(String.valueOf(ltp)).subtract((new BigDecimal(String.valueOf(avgprice))))).multiply(new BigDecimal(String.valueOf(qty)))));
							
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
							holding.put(DeviceConstants.DAY_PROFIT_CHANGE_PERCENTAGE, "NA");
							holding.put(DeviceConstants.PNL_PERCENT, "NA");
						}
					}else {
						fillDefaultHoldingValues(holding);
					}
				} else {
					fillDefaultHoldingValues(holding);
				}
			} catch (Exception e) {
				log.error("Error while calculation equity holdings PnL" + e.getMessage());
				log.error(e);
			}

		}
		dayPLPercent = (todaysPL/yesterdayPl)*100;
		
		profitLossPercent = (Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(currentValue)).subtract(new BigDecimal(String.valueOf(investmentAmt))))) / investmentAmt) * 100;
		
		JSONObject summaryObj = new JSONObject();
		
		if(holdings.isEmpty()) {
			summaryObj.put(DeviceConstants.TOTAL_PROFIT_AND_LOSS, "--");
			summaryObj.put(DeviceConstants.DISP_TOTAL_PROFIT_AND_LOSS, "--");
			summaryObj.put(DeviceConstants.CURRENT_VALUE, "--");
			summaryObj.put(DeviceConstants.INVESTMENT_AMT, "--");
			summaryObj.put(DeviceConstants.TOTAL_PROFIT_LOSS_PERCENTAGE, "--");
			summaryObj.put(DeviceConstants.DAY_PROFIT_CH_PERCENTAGE, "--");	
			summaryObj.put(DeviceConstants.DAY_PROFIT_LOSS, "--");
			return summaryObj;
		}
		
		summaryObj.put(DeviceConstants.TOTAL_PROFIT_AND_LOSS,
				PriceFormat.numberFormat(totalProfitAndLoss, 2));
		summaryObj.put(DeviceConstants.DISP_TOTAL_PROFIT_AND_LOSS,
				PriceFormat.numberFormat(totalProfitAndLoss, 2));
		summaryObj.put(DeviceConstants.CURRENT_VALUE,
				PriceFormat.numberFormat(currentValue, 2));
		summaryObj.put(DeviceConstants.INVESTMENT_AMT,
				PriceFormat.numberFormat(investmentAmt, 2));
		summaryObj.put(DeviceConstants.TOTAL_PROFIT_LOSS_PERCENTAGE,
				PriceFormat.formatPrice(String.valueOf(profitLossPercent), 2, false) + "%");
		summaryObj.put(DeviceConstants.DAY_PROFIT_CH_PERCENTAGE, PriceFormat.formatPrice(df.format(dayPLPercent), 2, false)+ "%");	
		summaryObj.put(DeviceConstants.DAY_PROFIT_LOSS, PriceFormat.numberFormat(todaysPL, 2));
		return summaryObj;
		
	}

	private static void fillDefaultHoldingValues(SymbolRow holding) {
		holding.put(DeviceConstants.PNL_PERCENT, "--");
		holding.put(DeviceConstants.LTP, "--");
		holding.put(DeviceConstants.CHANGE, "--");
		holding.put(DeviceConstants.CHANGE_PERCENT, "--");
		holding.put(DeviceConstants.MARKET_VALUE, "--");
		holding.put(DeviceConstants.PROFIT_AND_LOSS, "--");
		holding.put(DeviceConstants.OPEN_VALUE, "--");
		holding.put(DeviceConstants.DAY_PROFIT_LOSS, "--");
		holding.put(DeviceConstants.DAY_PROFIT_CHANGE_PERCENTAGE, "--");
	}
	
	public static JSONArray sort(JSONArray holdingsArray, final String sortOrder, String sortBy) {

        JSONArray sortedArray = new JSONArray();

        if (sortBy.contains(DeviceConstants.PROFIT_LOSS_PERCENTAGE)) {
        	
        	List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArray.length(); i++) {
				if(!(holdingsArray.getJSONObject(i).getString(DeviceConstants.PNL_PERCENT).equals("--") || holdingsArray.getJSONObject(i).getString(DeviceConstants.PNL_PERCENT).equals("NA"))) 
					toBeSorted.add(holdingsArray.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.PNL_PERCENT, toBeSorted,"[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sortedArray = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sortedArray = new JSONArray(toBeSorted);	
			}
			return sortedArray;

        }else if (sortBy.contains(DeviceConstants.PROFIT_LOSS_ABSOLUTE)) {
        	
        	List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArray.length(); i++) {
				if(!(holdingsArray.getJSONObject(i).getString(DeviceConstants.PROFIT_AND_LOSS).equals("--") || holdingsArray.getJSONObject(i).getString(DeviceConstants.PROFIT_AND_LOSS).equals("NA"))) 
					toBeSorted.add(holdingsArray.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.PROFIT_AND_LOSS, toBeSorted,"[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sortedArray = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sortedArray = new JSONArray(toBeSorted);	
			}
			return sortedArray;

        }
        else if (sortBy.equalsIgnoreCase(DeviceConstants.QUANTITY))
            return SortHelper.sortByInteger(holdingsArray, sortOrder, DeviceConstants.DISP_QTY, ",");

        else if (sortBy.contains(DeviceConstants.ALPHABETICALLY) || sortBy.isEmpty()) {
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

}
