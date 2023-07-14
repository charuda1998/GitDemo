package com.globecapital.business.report;

import java.util.Calendar;

import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;

public class FilterType {

	private static Logger log = Logger.getLogger(FilterType.class);

	public static JSONObject getOtherReportFilterDates(JSONObject filterObj) {
		JSONObject dates = new JSONObject();
		String toDate = "";
		try {
			toDate = DateUtils.formatDate(filterObj.getString(DeviceConstants.TO_DATE));
			dates.put(DeviceConstants.TO_DATE, toDate);

		} catch (Exception e) {
			log.error(e);
		}
		return dates;
	}

	public static JSONObject getFilterDates(String filterType, JSONObject filterObj) {

		JSONObject dates = new JSONObject();
		String toDate = "";
		String fromDate = "";
		String pastFinancialYear="Financial Year "+DateUtils.getPreviousFinancialYear();
		String currentFinancialYear="Financial Year "+DateUtils.getFinancialYear();

		toDate = DateUtils.getCurrentDate();

		try {

			if (filterType.isEmpty() || filterType.contains(DeviceConstants.LAST_THIRTY_DAYS)
					|| filterType.contains(DeviceConstants.FINANCIAL_YEAR+DateUtils.getFinancialYear())
					|| filterType.contains(DeviceConstants.FINANCIAL_YEAR+DateUtils.getPreviousFinancialYear())) {

				fromDate = DateUtils.getLastThirtyDays();

			} else if (filterType.contains(DeviceConstants.QUARTERLY)) {

				fromDate = DateUtils.getQuarterlyPeriod();

			} else if (filterType.contains(DeviceConstants.HALF_YEARLY)) {

				fromDate = DateUtils.getHalfYearlyPeriod();
			} else if (filterType.contains(DeviceConstants.YEARLY)) {

				fromDate = DateUtils.getYearlyPeriod();
			} else if(filterType.contains(pastFinancialYear)) {
				
				fromDate = DateUtils.getFromDateByYear(DateUtils.getPreviousFinancialYear());
				toDate = DateUtils.getToDateByYear(DateUtils.getPreviousFinancialYear());
				
			} else if(filterType.contains(currentFinancialYear)) {
				
				int CurrentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
				if(CurrentMonth >=4) { 
					fromDate=DateUtils.getFromDateByYear(DateUtils.getFinancialYear());
				} else {
					fromDate = DateUtils.getFromDateByYear(DateUtils.getPreviousFinancialYear());
				}
			} else {

				fromDate = DateUtils.formatDate(filterObj.getString(DeviceConstants.FROM_DATE));
				toDate = DateUtils.formatDate(filterObj.getString(DeviceConstants.TO_DATE));

			}
			dates.put(DeviceConstants.FROM_DATE, fromDate);
			dates.put(DeviceConstants.TO_DATE, toDate);
			return dates;
		} catch (Exception e) {
			log.error(e);
		}

		return dates;

	}

}
