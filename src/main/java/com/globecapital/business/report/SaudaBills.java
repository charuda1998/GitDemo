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

import com.globecapital.api.gc.backoffice.GetDerivativeSaudaReportAPI;
import com.globecapital.api.gc.backoffice.GetEquitySaudaReportAPI;
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

public class SaudaBills {

	private static Logger log = Logger.getLogger(SaudaBills.class);

	public static JSONArray getReports(Session session, JSONObject filterObj, String segment) throws JSONException, ParseException, RequestFailedException {

		String userId = session.getUserID();
		JSONArray reports = new JSONArray();
		JSONArray sorted = new JSONArray();
		List<GetPeriodDates> saudaRows = new ArrayList<>();
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
			
			GetEquitySaudaReportAPI equitySaudaReportAPI = new GetEquitySaudaReportAPI();
			GetDerivativeSaudaReportAPI derivativeSaudaReportAPI = new GetDerivativeSaudaReportAPI();
			if (segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				otherReport = equitySaudaReportAPI.get(reportRequest, GetOtherReportResponse.class, session.getAppID(),"GetEquitySaudaReport");
			} else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				reportRequest.setSegment("EQ");
				reportRequest.setYear(currentFinancialYear);
				otherReport = derivativeSaudaReportAPI.get(reportRequest, GetOtherReportResponse.class,
						session.getAppID(),DeviceConstants.DERIVATIVE_SAUDA_REPORT_L);
			} else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
				reportRequest.setSegment("CO");
				reportRequest.setYear(currentFinancialYear);
				otherReport = derivativeSaudaReportAPI.get(reportRequest, GetOtherReportResponse.class,
						session.getAppID(),DeviceConstants.DERIVATIVE_SAUDA_REPORT_L);
			} else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
				reportRequest.setSegment("CU");
				reportRequest.setYear(currentFinancialYear);
				otherReport = derivativeSaudaReportAPI.get(reportRequest, GetOtherReportResponse.class,
						session.getAppID(),DeviceConstants.DERIVATIVE_SAUDA_REPORT_L);
			}
			if (otherReport.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				reportRequest.setToken(GCAPIAuthToken.getAuthToken());
				if (segment.equalsIgnoreCase(DeviceConstants.EQUITY))
					otherReport = equitySaudaReportAPI.get(reportRequest, GetOtherReportResponse.class, session.getAppID(),"GetEquitySaudaReport");
				else
					otherReport = derivativeSaudaReportAPI.get(reportRequest, GetOtherReportResponse.class, session.getAppID(),DeviceConstants.DERIVATIVE_SAUDA_REPORT_L);
				if(otherReport.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS))
					saudaRows = otherReport.getDates();
			} else {
				saudaRows = otherReport.getDates();
			}
			if (otherReport.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS)) {

				Collections.reverse(saudaRows);
				for (GetPeriodDates row : saudaRows) {

					JSONObject saudaObject = new JSONObject();
					saudaObject.put(DeviceConstants.REPORT_DATE, row.getDate());
					saudaObject.put(DeviceConstants.DESCRIPTION, "NSE");
					reports.put(saudaObject);
				}

				sorted = getFilteredReports(reports, filterBy, sortOrder, sortBy);
			}

		}catch(NullPointerException e) {
			throw new RequestFailedException();
		} catch (Exception e) {
			log.error(e);
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

	public static JSONArray sort(JSONArray others, String orderType, String sortBy) {

		JSONArray sorted = new JSONArray();

		try {
			if (sortBy.contains(DeviceConstants.DATE) || sortBy.isEmpty()) {
				if (sortBy.contains(DeviceConstants.DATE) || sortBy.isEmpty()) {
					return SortHelper.sortByDate(others, orderType, DeviceConstants.REPORT_DATE);
				} else {
					return others;
				}
			} else {
				return others;
			}
		} catch (Exception e) {
			log.error(e);
		}
		return sorted;
	}

}
