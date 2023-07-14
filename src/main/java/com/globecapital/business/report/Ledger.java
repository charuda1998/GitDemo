package com.globecapital.business.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.GetLedgerReportAPI;
import com.globecapital.api.gc.backoffice.GetLedgerReportRequest;
import com.globecapital.api.gc.backoffice.GetLedgerReportResponse;
import com.globecapital.api.gc.backoffice.GetLedgerRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.session.Session;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class Ledger {

	private static Logger log = Logger.getLogger(Ledger.class);

	public static JSONArray getLedgerReports(Session session, String segmentType, JSONObject filterObj)
			throws JSONException {

		String userId = session.getUserID();
		
		JSONArray ledgerArray = new JSONArray();
		JSONArray sorted = new JSONArray();

		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
		String sortBy = filterObj.getString(DeviceConstants.SORT_BY);

		try {

			/*** Get Auth code required for Holdings API by Logging in ***/
			String segment = "";
			int precision = 0;
			List<GetLedgerRows> ledgerRows = new ArrayList<>();
			GetLedgerReportAPI ledgerAPI = new GetLedgerReportAPI();
			GetLedgerReportRequest ledgerRequest = new GetLedgerReportRequest();
			GetLedgerReportResponse ledgerResponse = new GetLedgerReportResponse();

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				segment = GCConstants.EQUITY;
				precision = 2;
			} else {
				precision = 4;
				segment = GCConstants.COMMODITY;
			}
			JSONObject reportDates = FilterType.getFilterDates(filterObj.getString(DeviceConstants.DATE_FILTER),
					filterObj);

			String currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);
			ledgerRequest.setClientCode(userId);
			ledgerRequest.setToken(GCAPIAuthToken.getAuthToken());
			if (filterBy.toString().contains(DeviceConstants.WITH_MARGIN_FILTER) || filterBy.length()==0) {
				ledgerRequest.setEntryType("A");	//Contains all the records of the Ledger. Includes spam/unwanted records from broker
			} else {
				ledgerRequest.setEntryType("WM");	//Contains only the useful records of Ledger (WM- Without Margin Type)
			}
			ledgerRequest.setSegment(segment);
			ledgerRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
			ledgerRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));

			ledgerRequest.setYear(currentFinancialYear);
			ledgerResponse = ledgerAPI.get(ledgerRequest, GetLedgerReportResponse.class, session.getAppID(),DeviceConstants.LEDGER_REPORT_L);
			if (ledgerResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				GetLedgerReportAPI newLedgerAPI = new GetLedgerReportAPI();
				ledgerRequest.setToken(GCAPIAuthToken.getAuthToken());
				ledgerResponse = newLedgerAPI.get(ledgerRequest, GetLedgerReportResponse.class, session.getAppID(),DeviceConstants.LEDGER_REPORT_L);
				if (ledgerResponse.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS))
					ledgerRows = ledgerResponse.getDetails();
			}
			if (ledgerResponse.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS)) {
				
				ledgerRows = ledgerResponse.getDetails();
				Collections.reverse(ledgerRows);
				for (GetLedgerRows row : ledgerRows) {
	
					JSONObject ledgerObject = new JSONObject();
					ledgerObject.put(DeviceConstants.REPORT_DATE, row.getLedDate());
					if (Double.parseDouble(row.getCredit()) > 0) {
						ledgerObject.put(DeviceConstants.CREDIT_OR_DEBIT,
								PriceFormat.priceToRupee(row.getCredit(), precision));
						if(sortBy.equalsIgnoreCase(DeviceConstants.CREDIT)
								|| sortBy.equalsIgnoreCase(DeviceConstants.DEBIT))
							ledgerObject.put(DeviceConstants.TYPE, DeviceConstants.CREDIT); //only for sortBy purpose
					} else {
						ledgerObject.put(DeviceConstants.CREDIT_OR_DEBIT,
								PriceFormat.priceToRupee(String.valueOf(Double.parseDouble(row.getDebit())*-1), precision));
						if(sortBy.equalsIgnoreCase(DeviceConstants.CREDIT)
								|| sortBy.equalsIgnoreCase(DeviceConstants.DEBIT))
							ledgerObject.put(DeviceConstants.TYPE, DeviceConstants.DEBIT); //only for sortBy purpose
					}
					ledgerObject.put(DeviceConstants.BALANCE, PriceFormat.priceToRupee(row.getBalance(), precision));
					ledgerObject.put(DeviceConstants.DESCRIPTION, row.getNarration());
					ledgerArray.put(ledgerObject);
				}
				sorted = getFilteredLedger(ledgerArray, filterBy, sortOrder, sortBy);
			}	
		} catch (Exception e) {

			log.error(e);
		}
		return sorted;
	}

	public static JSONArray getFilteredLedger(JSONArray ledgerArray, JSONArray filterBy, String sortOrder,
			String sortBy) {

		JSONArray sortedArray = new JSONArray();

		try {

			if (sortOrder.isEmpty()) {
				return ledgerArray;
			} else {
				sortedArray = sort(ledgerArray, sortOrder, sortBy);
			}

		} catch (Exception e) {
			log.error(e);
		}
		return sortedArray;

	}

	public static JSONArray sort(JSONArray ledgerArray, final String orderType, String sortBy) throws Exception {

		JSONArray sorted = new JSONArray();

			if (sortBy.contains(DeviceConstants.CREDIT)) {
				
				List<JSONObject> toBeSorted = SortHelper.sortByCreditDebit(ledgerArray,DeviceConstants.CREDIT,DeviceConstants.CREDIT_OR_DEBIT,"[,\u20B9]");
				if (orderType.contains(DeviceConstants.ASCENDING))
					sorted = new JSONArray(toBeSorted);
				else {
					Collections.reverse(toBeSorted);
					sorted = new JSONArray(toBeSorted);	
				}
				return sorted;
			} else if (sortBy.contains(DeviceConstants.DEBIT)) {
				List<JSONObject> toBeSorted = SortHelper.sortByCreditDebit(ledgerArray,DeviceConstants.DEBIT,DeviceConstants.CREDIT_OR_DEBIT, "[,\u20B9]");
				if (orderType.contains(DeviceConstants.ASCENDING))
					sorted = new JSONArray(toBeSorted);
				else {
					Collections.reverse(toBeSorted);
					sorted = new JSONArray(toBeSorted);	
				}
				return sorted;
			} else if (sortBy.contains(GCConstants.BALANCE)) {
				List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
				for (int i = 0; i < ledgerArray.length(); i++) 
					toBeSorted.add(ledgerArray.getJSONObject(i));
				SortHelper.sortByDouble(DeviceConstants.BALANCE, toBeSorted, "[,\u20B9]");
				if (orderType.contains(DeviceConstants.ASCENDING))
					sorted = new JSONArray(toBeSorted);
				else {
					Collections.reverse(toBeSorted);
					sorted = new JSONArray(toBeSorted);	
				}
				return sorted;
			} else {

				if (orderType.contains(DeviceConstants.DESCENDING)) {
					return ledgerArray;
				} else {

					for (int i = ledgerArray.length() - 1; i >= 0; i--) {
						JSONObject ledger = ledgerArray.getJSONObject(i);
						sorted.put(ledger);
					}
					return sorted;
				}
			}
	}
}
