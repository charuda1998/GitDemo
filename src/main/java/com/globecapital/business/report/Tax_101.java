package com.globecapital.business.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetCommodityTaxAPI;
import com.globecapital.api.gc.backoffice.GetCurrencyTaxAPI;
import com.globecapital.api.gc.backoffice.GetDerivativeTaxAPI;
import com.globecapital.api.gc.backoffice.GetEquityTaxAPI;
import com.globecapital.api.gc.backoffice.GetTaxRequest;
import com.globecapital.api.gc.backoffice.GetTaxResponse;
import com.globecapital.api.gc.backoffice.GetTaxRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.GCUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class Tax_101 {

	private static Logger log = Logger.getLogger(Tax_101.class);

	public static JSONArray getTaxReports(Session session, String segmentType, String reportType, JSONObject filterObj)
			throws JSONException, RequestFailedException {

		String userId = session.getUserID();
		
		JSONArray taxArray = new JSONArray();
		JSONObject summaryObj = new JSONObject();
		JSONObject totalSummary = new JSONObject();
		JSONObject taxPercentObj = new JSONObject();
		JSONObject taxPercent = new JSONObject();
		String[] scripArray;
		int precision = 0;
		String filterType = filterObj.getString(DeviceConstants.DATE_FILTER);
		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
		String sortBy = filterObj.getString(DeviceConstants.SORT_BY);
		if(filterType.isEmpty())
			filterType=DeviceConstants.FINANCIAL_YEAR+DateUtils.getFinancialYear();
		try {

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE) 
					|| segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
				precision = 2;
			} else {
				precision = 4;
			}

			GetTaxRequest taxRequest = new GetTaxRequest();
			GetTaxResponse taxResponse = new GetTaxResponse();
//			taxRequest.setToken(GCAPIAuthToken.getAuthToken());
			taxRequest.setClientCode(userId);
			taxRequest.setToken(GCAPIAuthToken.getAuthToken());
			taxRequest.setYear(GCUtils.getFinancialYear(filterType));
		
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				GetEquityTaxAPI eqTaxApi = new GetEquityTaxAPI();
				taxResponse = eqTaxApi.get(taxRequest, GetTaxResponse.class, session.getAppID(),
						"GetEquityTaxReport");
				if(taxResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					taxRequest.setToken(GCAPIAuthToken.getAuthToken());
					taxResponse = eqTaxApi.get(taxRequest, GetTaxResponse.class, session.getAppID()
							,"GetEquityTaxReport");
				}
				List<GetTaxRows> taxRows = new ArrayList<>();
				if(taxResponse.getMsg().equalsIgnoreCase(DeviceConstants.SUCCESS))
					taxRows = taxResponse.getDetails();
				for (GetTaxRows rows : taxRows) {

					SymbolRow taxRowObj = new SymbolRow();
					JSONObject taxObj = new JSONObject();
					String isinTokenSegmentNSE = rows.getIsin() + "_" + ExchangeSegment.NSE_SEGMENT_ID;
					String isinTokenSegmentBSE = rows.getIsin() + "_" + ExchangeSegment.BSE_SEGMENT_ID;

					if (SymbolMap.isValidSymbol(isinTokenSegmentNSE)) {
						taxRowObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentNSE).getMinimisedSymbolRow());
					}else if (SymbolMap.isValidSymbol(isinTokenSegmentBSE)) {
						taxRowObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentBSE).getMinimisedSymbolRow());
					}else {
						continue;
					}
					taxObj.put(DeviceConstants.SYMBOL,
							taxRowObj.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL));
					taxObj.put(DeviceConstants.SYMBOL_DETAILS, rows.getScripName());
					taxObj.put(DeviceConstants.INTRADAY_PL, PriceFormat.priceToRupee(rows.getIntradayPL(), precision));
					taxObj.put(DeviceConstants.SHORT_TERM_PL,
							PriceFormat.priceToRupee(rows.getShortTermPL(), precision));
					taxObj.put(DeviceConstants.LONG_TERM_PL, PriceFormat.priceToRupee(rows.getLongTermPL(), precision));
					taxObj.put(DeviceConstants.INTRADAY_TAX_AMOUNT,
							PriceFormat.priceToRupee(rows.getIntradyTaxAmount(), precision));
					taxObj.put(DeviceConstants.SHORT_TERM_TAX_AMOUNT,
							PriceFormat.priceToRupee(rows.getShortTermTaxAmount(), precision));
					taxObj.put(DeviceConstants.LONG_TERM_TAX_AMOUNT,
							PriceFormat.priceToRupee(rows.getLongTermTaxAmount(), precision));
					taxObj.put(DeviceConstants.TOTAL_TAX,
							PriceFormat.priceToRupee(String.valueOf((Double.parseDouble(rows.getIntradyTaxAmount())
									+ Double.parseDouble(rows.getShortTermTaxAmount())
									+ Double.parseDouble(rows.getLongTermTaxAmount()))), precision));
					taxArray.put(taxObj);
				}
				summaryObj.put(DeviceConstants.TOTAL_INTRADAY_PERCENT, taxResponse.getTotalIntradayPercent());
				summaryObj.put(DeviceConstants.TOTAL_INTRADAY_PL,
						PriceFormat.priceInCrores(taxResponse.getTotalIntradayPnL(), precision));
				summaryObj.put(DeviceConstants.TOTAL_INTRADAY_TAX,
						PriceFormat.priceInCrores(taxResponse.getTotalIntradayTax(), precision));
				summaryObj.put(DeviceConstants.TOTAL_SHORT_TERM_PERCENT, taxResponse.getTotalShortTermPercent());
				summaryObj.put(DeviceConstants.TOTAL_SHORT_TERM_PL,
						PriceFormat.priceInCrores(taxResponse.getTotalShortTermPnL(), precision));
				summaryObj.put(DeviceConstants.TOTAL_SHORT_TERM_TAX,
						PriceFormat.priceInCrores(taxResponse.getTotalShortTermTax(), precision));
				summaryObj.put(DeviceConstants.TOTAL_LONG_TERM_PERCENT, taxResponse.getTotalLongTermPercent());
				summaryObj.put(DeviceConstants.TOTAL_LONG_TERM_PL,
						PriceFormat.priceInCrores(taxResponse.getTotalLongTermPnL(), precision));
				summaryObj.put(DeviceConstants.TOTAL_LONG_TERM_TAX,
						PriceFormat.priceInCrores(taxResponse.getTotalLongTermTax(), precision));
				summaryObj.put(DeviceConstants.TOTAL_TAX_AMOUNT,
						PriceFormat.priceInCrores(taxResponse.getTotalTaxAmount(), precision));
				summaryObj.put(DeviceConstants.TOTAL_CHARGES,
						PriceFormat.priceInCrores(taxResponse.getTotalCharges(), precision));
				taxPercentObj.put(DeviceConstants.TOTAL_INTRADAY_PERCENT, taxResponse.getTotalIntradayPercent());
				taxPercentObj.put(DeviceConstants.TOTAL_SHORT_TERM_PERCENT, taxResponse.getTotalShortTermPercent());
				taxPercentObj.put(DeviceConstants.TOTAL_LONG_TERM_PERCENT, taxResponse.getTotalLongTermPercent());

				taxPercent.put(DeviceConstants.TAX_PERCENTAGE, taxPercentObj);
				totalSummary.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);
				taxArray = getSortedTaxArray(taxArray, filterBy, sortOrder, sortBy, reportType, filterObj, segmentType);
				taxArray.put(taxPercent);
				taxArray.put(totalSummary);
				return taxArray;
			} else {
				taxResponse = performDerivativeScripAPICall(session, segmentType, taxRequest);
				
				if (taxResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					taxRequest.setToken(GCAPIAuthToken.getAuthToken());
					taxResponse = performDerivativeScripAPICall(session, segmentType, taxRequest);
				}
				List<GetTaxRows> taxRows = new ArrayList<>();
				if(taxResponse.getMsg().equalsIgnoreCase(DeviceConstants.SUCCESS))
					taxRows = taxResponse.getDetails();
				for (GetTaxRows rows : taxRows) {
					String scripName=rows.getScripName().trim();
					scripArray = scripName.split(" ");
					String instType="";
					if(scripArray.length>2 &&( scripArray[2].equals(InstrumentType.CALL_OPTION) || scripArray[2].equals(InstrumentType.PUT_OPTION))) {
						instType=InstrumentType.OPTIONS;
					}else {
						instType=InstrumentType.FUTURES;
					}
					if(filterBy.length() == 0 || (filterBy.toString().contains(GCConstants.FUTURES) && (filterBy.toString().contains(GCConstants.OPTIONS))))
					{
					//Do nothing
					}
					else if(filterBy.toString().contains(GCConstants.FUTURES) && !instType.equals(InstrumentType.FUTURES))
					{
					continue;
					}
					else if(filterBy.toString().contains(GCConstants.OPTIONS) && !instType.equals(InstrumentType.OPTIONS))
					{
					continue;
					}
					JSONObject taxObj = new JSONObject();
					taxObj.put(DeviceConstants.SYMBOL, scripArray[0]);
					taxObj.put(DeviceConstants.SYMBOL_DETAILS, scripName
							.substring(scripName.indexOf(" "), scripName.length()).trim());
					taxObj.put(DeviceConstants.PROFIT_AND_LOSS, PriceFormat.priceToRupee(rows.getProfitLoss(), precision));
					taxObj.put(DeviceConstants.TAX_AMOUNT, PriceFormat.priceToRupee(rows.getTaxAmount(), precision));
					taxArray.put(taxObj);
				}
				summaryObj.put(DeviceConstants.TOTAL_TAX_AMOUNT,
						PriceFormat.priceInCrores(taxResponse.getTotalTaxAmount(), precision));
				summaryObj.put(DeviceConstants.TOTAL_CHARGES,
						PriceFormat.priceInCrores(taxResponse.getTotalCharges(), precision));
				summaryObj.put(DeviceConstants.TOTAL_PROFIT_AND_LOSS,
						PriceFormat.priceInCrores(taxResponse.getTotalPL(), precision));
				summaryObj.put(DeviceConstants.TOTAL_BUY_VALUE,
						PriceFormat.priceInCrores(taxResponse.getTotalBuy(), precision));
				summaryObj.put(DeviceConstants.TOTAL_SELL_VALUE,
						PriceFormat.priceInCrores(taxResponse.getTotalSell(), precision));
				summaryObj.put(DeviceConstants.TOTAL_TAX_RATE, taxResponse.getTotalTaxRate());
				summaryObj.put(DeviceConstants.TOTAL_VALUE,
						PriceFormat.priceInCrores(taxResponse.getTotalValue(), precision));
				totalSummary.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);
				taxArray = getSortedTaxArray(taxArray, filterBy, sortOrder, sortBy, reportType, filterObj, segmentType);
				taxArray.put(totalSummary);
			}
		} catch(NullPointerException e) {
			throw new RequestFailedException();
		}
		catch (Exception e) {
			log.error(e);
		}
		return taxArray;
	}

	public static GetTaxResponse performDerivativeScripAPICall(Session session, String segmentType, GetTaxRequest taxRequest)
			throws GCException {
		GetTaxResponse taxResponse;
		if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			GetDerivativeTaxAPI derivativeApi = new GetDerivativeTaxAPI();
			taxResponse = derivativeApi.get(taxRequest, GetTaxResponse.class, session.getAppID()
					,"GetDerivativeTaxReport");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			GetCurrencyTaxAPI currencyApi = new GetCurrencyTaxAPI();
			taxResponse = currencyApi.get(taxRequest, GetTaxResponse.class, session.getAppID()
					,"GetCurrencyTaxReport");
		} else {
			GetCommodityTaxAPI commodityApi = new GetCommodityTaxAPI();
			taxResponse = commodityApi.get(taxRequest, GetTaxResponse.class, session.getAppID()
					,"GetCommodityTaxReport");
		}
		return taxResponse;
	}

	public static JSONArray getSortedTaxArray(JSONArray taxArray, JSONArray filterBy, String sortOrder, String sortBy,
			String reportType, JSONObject filterObj, String segmentType) {
		JSONArray sortedArray = new JSONArray();
		try {
			sortedArray = sort(taxArray, sortOrder, sortBy, segmentType);
			return sortedArray;
		} catch (Exception e) {
			log.error(e);
		}
		return sortedArray;
	}

	public static JSONArray sort(JSONArray toBeSortedArray, final String sortOrder, String sortBy, String segmentType) {
		
        JSONArray sorted = new JSONArray();
		if (sortBy.contains(DeviceConstants.TOTAL_TAX_FILTER)) {

			List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < toBeSortedArray.length(); i++) {
				toBeSorted.add(toBeSortedArray.getJSONObject(i));
			}
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY))
				SortHelper.sortByDouble(DeviceConstants.TOTAL_TAX, toBeSorted,"[,\u20B9]");
			else
				SortHelper.sortByDouble(DeviceConstants.TAX_AMOUNT, toBeSorted,"[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sorted = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sorted = new JSONArray(toBeSorted);	
			}
			return sorted;
		}else if(sortBy.contains(GCConstants.PROFIT_N_LOSS)) {
        	List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
        	for (int i = 0; i < toBeSortedArray.length(); i++) {
				if(!toBeSortedArray.getJSONObject(i).getString(DeviceConstants.PROFIT_AND_LOSS).equals("--")) 
					toBeSorted.add(toBeSortedArray.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.PROFIT_AND_LOSS,toBeSorted,"[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sorted = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sorted = new JSONArray(toBeSorted);	
			}
			return sorted;
		}else if (sortBy.contains(GCConstants.ALPHABETICALLY) || sortBy.isEmpty()) {
			if(sortBy.isEmpty()) {
				return SortHelper.sortBySymbol(toBeSortedArray,DeviceConstants.ASCENDING);
			}else {
				return SortHelper.sortBySymbol(toBeSortedArray, sortOrder);
			}
		}else {
			return toBeSortedArray;
		}
	}

}
