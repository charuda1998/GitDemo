package com.globecapital.business.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetCommodityTransactionAPI;
import com.globecapital.api.gc.backoffice.GetCurrencyTransactionAPI;
import com.globecapital.api.gc.backoffice.GetDerivativeTransactionAPI;
import com.globecapital.api.gc.backoffice.GetEquityTransactionAPI;
import com.globecapital.api.gc.backoffice.GetEquityTransactionResponse;
import com.globecapital.api.gc.backoffice.GetEquityTransactionRows;
import com.globecapital.api.gc.backoffice.GetTransactionRequest;
import com.globecapital.api.gc.backoffice.GetTransactionResponse;
import com.globecapital.api.gc.backoffice.GetTransactionRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class Transaction {

	private static Logger log = Logger.getLogger(Transaction.class);

	public static JSONArray getTransactionReports(Session session, String segmentType, JSONObject filterObj,
			String reportType) throws JSONException {

		String userId = session.getUserID();
		
		JSONArray transactionArray = new JSONArray();
		JSONArray sortedArray = new JSONArray();

		String filterType = filterObj.getString(DeviceConstants.DATE_FILTER);
		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
		String sortBy = filterObj.getString(DeviceConstants.SORT_BY);
		int precision=0;
		try {
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				precision = 2;
			} else {
				precision = 4;
			}
			String exchange = "";
			String[] scripArray;
			JSONObject reportDates = FilterType.getFilterDates(filterType, filterObj);
			String currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);

			GetTransactionRequest transactionRequest = new GetTransactionRequest();
			GetTransactionResponse transactionResponse = null;
			GetEquityTransactionResponse equityTransactionResponse = null;

			List<GetTransactionRows> transactionRows = new ArrayList<>();
			List<GetEquityTransactionRows> equityTransactionRows = new ArrayList<>();
			transactionRequest.setToken(GCAPIAuthToken.getAuthToken());
			transactionRequest.setClientCode(userId);
			transactionRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));
			transactionRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));

			if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				transactionRequest.setYear(currentFinancialYear);
			}

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				exchange = ExchangeSegment.NSE;
				GetEquityTransactionAPI equityTransactionAPI = new GetEquityTransactionAPI();
				equityTransactionResponse = equityTransactionAPI.get(transactionRequest,
						GetEquityTransactionResponse.class, session.getAppID(),"GetEquityTransaction");
				if(equityTransactionResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					transactionRequest.setToken(GCAPIAuthToken.getAuthToken());
					equityTransactionResponse = equityTransactionAPI.get(transactionRequest, GetEquityTransactionResponse.class, session.getAppID(),
							"GetEquityTransaction");
				}
				if(equityTransactionResponse.getMsg().equalsIgnoreCase(DeviceConstants.SUCCESS))
					equityTransactionRows = equityTransactionResponse.getTradeDetails();
				Collections.reverse(equityTransactionRows);
				try {
					for (GetEquityTransactionRows rows : equityTransactionRows) {

						SymbolRow transObj = new SymbolRow();
						JSONObject transactionObj = new JSONObject();
						String isinTokenSegmentNSE = rows.getISINCODE() + "_" + ExchangeSegment.NSE_SEGMENT_ID;
						String isinTokenSegmentBSE = rows.getISINCODE() + "_" + ExchangeSegment.BSE_SEGMENT_ID;

						if (SymbolMap.isValidSymbol(isinTokenSegmentNSE)) {
							transObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentNSE).getMinimisedSymbolRow());
						}else if (SymbolMap.isValidSymbol(isinTokenSegmentBSE)) {
							transObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentBSE).getMinimisedSymbolRow());
						}else {
							continue;
						}

						transactionObj.put(DeviceConstants.REPORT_DATE, rows.getTrdate());
						transactionObj.put(DeviceConstants.PRICE, PriceFormat.priceToRupee(rows.getNetrate(),precision));
						transactionObj.put(DeviceConstants.QTY,
								String.valueOf(Math.abs(Integer.parseInt(rows.getQty()))));
						if (rows.getBs().equalsIgnoreCase("B")) {
							transactionObj.put(DeviceConstants.BUY_OR_SELL, DeviceConstants.BUY_FILTER);
						} else {
							transactionObj.put(DeviceConstants.BUY_OR_SELL, DeviceConstants.SELL_FILTER);
						}
						transactionObj.put(DeviceConstants.SYMBOL_DETAILS, rows.getScripname());
						transactionObj.put(DeviceConstants.SYMBOL,
								transObj.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL));
						transactionArray.put(transactionObj);

					}
				} catch (Exception e) {
					log.debug(e.getMessage());
					log.error(e);
				}
				sortedArray = getTransactionArray(transactionArray, filterBy, sortOrder, sortBy, reportType, filterObj);
				return sortedArray;

			} else
				transactionResponse = performDerivativeTransactionAPICall(session, segmentType, transactionRequest, transactionResponse);

			if (transactionResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				transactionRequest.setToken(GCAPIAuthToken.getAuthToken());
				transactionResponse = performDerivativeTransactionAPICall(session, segmentType, transactionRequest, transactionResponse);
			}

			if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY) && transactionResponse.getMsg().equalsIgnoreCase(DeviceConstants.SUCCESS) ) {
				transactionRows = transactionResponse.getTradeDetails();
			}

			for (GetTransactionRows rows : transactionRows) {
				scripArray = rows.getScripname().split(" ");
				JSONObject transactionObj = new JSONObject();
				transactionObj.put(DeviceConstants.REPORT_DATE, rows.getTrdate());
				transactionObj.put(DeviceConstants.PRICE, PriceFormat.priceToRupee(rows.getTradeprice(),precision));
				transactionObj.put(DeviceConstants.QTY, String.valueOf(Math.abs(Integer.parseInt(rows.getQty()))));
				if (rows.getBs().equalsIgnoreCase("B")) {
					transactionObj.put(DeviceConstants.BUY_OR_SELL, DeviceConstants.BUY_FILTER);
				} else {
					transactionObj.put(DeviceConstants.BUY_OR_SELL, DeviceConstants.SELL_FILTER);
				}
				transactionObj.put(DeviceConstants.SYMBOL, scripArray[0]);
				transactionObj.put(DeviceConstants.SYMBOL_DETAILS, rows.getScripname()
						.substring(rows.getScripname().indexOf(" "), rows.getScripname().length()).trim());
				transactionArray.put(transactionObj);
			}
			sortedArray = getTransactionArray(transactionArray, filterBy, sortOrder, sortBy, reportType, filterObj);
		} catch (Exception e) {
			log.error(e);
		}
		return sortedArray;
	}

	public static GetTransactionResponse performDerivativeTransactionAPICall(Session session, String segmentType,
			GetTransactionRequest transactionRequest, GetTransactionResponse transactionResponse) throws GCException {
		String exchange;
		if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			exchange = ExchangeSegment.BSE;
			GetDerivativeTransactionAPI derivativesTransactionAPI = new GetDerivativeTransactionAPI();
			transactionResponse = derivativesTransactionAPI.get(transactionRequest, GetTransactionResponse.class,
					session.getAppID(),"GetDerivativeTransaction");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			exchange = ExchangeSegment.NCDEX;
			GetCurrencyTransactionAPI currencyTransactionAPI = new GetCurrencyTransactionAPI();
			transactionResponse = currencyTransactionAPI.get(transactionRequest, GetTransactionResponse.class,
					session.getAppID(),"GetCurrencyTransaction");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			exchange = ExchangeSegment.MCX;
			GetCommodityTransactionAPI commodityTransactionAPI = new GetCommodityTransactionAPI();
			transactionResponse = commodityTransactionAPI.get(transactionRequest, GetTransactionResponse.class,
					session.getAppID(),"GetCommodityTransaction");
		}
		return transactionResponse;
	}

	public static JSONArray getTransactionArray(JSONArray transactionArray, JSONArray filterBy, String sortOrder,
			String sortBy, String reportType, JSONObject filterObj) {

		JSONArray sorted = new JSONArray();
		JSONArray sortedArray = new JSONArray();

		JSONObject allowedFilters = FilterList.getAdvancedFilterTypes(reportType, filterObj);

		try {

			if (filterBy.length() == 0 || filterBy.toString().contains(DeviceConstants.ALL_FILTER)
					|| (filterBy.length() == allowedFilters.getJSONArray(DeviceConstants.FILTER_BY).length())) {

				if (sortOrder.isEmpty()) {
					return transactionArray;
				} else {
					sortedArray = sort(transactionArray, sortOrder, sortBy);
				}
				return sortedArray;

			} else if (filterBy.toString().contains(DeviceConstants.BUY_FILTER)) {

				for (int i = 0; i < transactionArray.length(); i++) {

					JSONObject trxnObject = new JSONObject();

					trxnObject = transactionArray.getJSONObject(i);
					if (trxnObject.getString(DeviceConstants.BUY_OR_SELL)
							.equalsIgnoreCase(DeviceConstants.BUY_FILTER)) {
						sorted.put(trxnObject);

					}

				}
				sortedArray = sort(sorted, sortOrder, sortBy);
				return sortedArray;

			} else if (filterBy.toString().contains(DeviceConstants.SELL_FILTER)) {

				for (int i = 0; i < transactionArray.length(); i++) {
					JSONObject trxnObject = transactionArray.getJSONObject(i);
					if (trxnObject.getString(DeviceConstants.BUY_OR_SELL).contains(DeviceConstants.SELL_FILTER)) {

						sorted.put(trxnObject);
					}

				}
				sortedArray = sort(sorted, sortOrder, sortBy);
				return sortedArray;
			}

		} catch (Exception e) {
			log.error(e);
		}
		return sortedArray;
	}

	public static JSONArray sort(JSONArray finalTransactionArray, final String sortOrder, String sortBy) {

		JSONArray sortedArray = new JSONArray();

		try {

			if (sortBy.equalsIgnoreCase(DeviceConstants.QUANTITY)) {
				return SortHelper.sortByInteger(finalTransactionArray, sortOrder, DeviceConstants.QTY);
			} else if (sortBy.equalsIgnoreCase(DeviceConstants.DATE)) {
				
				if (sortOrder.contains(DeviceConstants.DESCENDING)) {
					return finalTransactionArray;
				} else {

					for (int i = finalTransactionArray.length() - 1; i >= 0; i--) {
						JSONObject trans = finalTransactionArray.getJSONObject(i);
						sortedArray.put(trans);
					}
					return sortedArray;
				}

			} else if (sortBy.equalsIgnoreCase(DeviceConstants.SYMBOL_FILTER)) {
				return SortHelper.sortBySymbol(finalTransactionArray, sortOrder);
			} else {
				return finalTransactionArray;
			}
		} catch (Exception e) {
			log.error(e);
		}
		return sortedArray;
	}

}