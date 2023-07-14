package com.globecapital.business.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetContractNotesAPI;
import com.globecapital.api.gc.backoffice.GetOtherReportRequest;
import com.globecapital.api.gc.backoffice.GetOtherReportResponse;
import com.globecapital.api.gc.backoffice.GetPeriodDates;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;

public class ContractNotes {

	private static Logger log = Logger.getLogger(ContractNotes.class);

	public static JSONArray getReports(Session session, JSONObject filterObj, String segment) throws JSONException, ParseException, RequestFailedException {

		String userId = session.getUserID();
		
		JSONArray reports = new JSONArray();
		JSONArray sorted = new JSONArray();
		String filterType = filterObj.getString(DeviceConstants.DATE_FILTER);
		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
		String sortBy = filterObj.getString(DeviceConstants.SORT_BY);
		JSONObject reportDates = FilterType.getFilterDates(filterType, filterObj);
		String currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);

		try {

			GetOtherReportRequest reportRequest = new GetOtherReportRequest();
			GetOtherReportResponse otherReport = new GetOtherReportResponse();
			reportRequest.setToken(GCAPIAuthToken.getAuthToken());
			
			reportRequest.setClientCode(userId);
			
//			reportRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
			reportRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));
			reportRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));

			GetContractNotesAPI contractAPI = new GetContractNotesAPI();
			if (segment.equalsIgnoreCase(DeviceConstants.EQUITY))
				reportRequest.setSegment("EQ");
			else
				reportRequest.setSegment("CO");
			reportRequest.setYear(currentFinancialYear);
			otherReport = contractAPI.get(reportRequest, GetOtherReportResponse.class, session.getAppID(),"GetContractReport");

			if (otherReport.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				reportRequest.setToken(GCAPIAuthToken.getAuthToken());
				otherReport = contractAPI.get(reportRequest, GetOtherReportResponse.class, session.getAppID(),"GetContractReport");
			}

			if (otherReport.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS)) {
				List<GetPeriodDates> saudaRows = otherReport.getDates();
				Collections.reverse(saudaRows);
				for (GetPeriodDates row : saudaRows) {

					JSONObject saudaObject = new JSONObject();
					saudaObject.put(DeviceConstants.REPORT_DATE, row.getDate());
					saudaObject.put(DeviceConstants.DESCRIPTION, "NSE");
					reports.put(saudaObject);
				}

				sorted = getFilteredReports(reports, filterBy, sortOrder, sortBy);
			}

		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return sorted;
	}

	public static JSONArray getFilteredReports(JSONArray others, JSONArray filterBy, String sortOrder, String sortBy) {

		JSONArray sortedArray = new JSONArray();

		try {
			if (sortOrder.isEmpty()) {
				return others;
			} else {
				sortedArray = sort(others, sortOrder, sortBy);
			}
			return sortedArray;

		} catch (Exception e) {
			log.error(e);
		}
		return sortedArray;

	}

	public static JSONArray sort(JSONArray recordArray, String orderType, String sortBy) {
		try {
			if (sortBy.contains(DeviceConstants.DATE) || sortBy.isEmpty()) {
				return SortHelper.sortByDate(recordArray, orderType, DeviceConstants.REPORT_DATE);
			} else {
				return recordArray;
			}
		} catch (Exception e) {
			log.error(e);
		}
		return recordArray;
	}
}