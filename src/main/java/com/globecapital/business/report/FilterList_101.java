package com.globecapital.business.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.utils.DateUtils;

public class FilterList_101 {

	private static JSONArray allowedFilterTypes;
	private static JSONArray allowedTaxFilterTypes;
	private static JSONArray allowedFilterByList;
	private static JSONArray allowedSortByList;

	static {
		allowedFilterTypes = new JSONArray();
		allowedFilterTypes.put(DeviceConstants.LAST_THIRTY_DAYS);
		allowedFilterTypes.put(DeviceConstants.QUARTERLY);
		allowedFilterTypes.put(DeviceConstants.HALF_YEARLY);
		allowedFilterTypes.put(DeviceConstants.YEARLY);
	}
	static {
		allowedTaxFilterTypes = new JSONArray();
		allowedTaxFilterTypes.put(DeviceConstants.FINANCIAL_YEAR+DateUtils.getFinancialYear());
		allowedTaxFilterTypes.put(DeviceConstants.FINANCIAL_YEAR+DateUtils.getPreviousFinancialYear());
	}

	public static JSONObject getFilterTypes(String reportType, JSONObject filterObj) throws JSONException, ParseException {
		JSONObject advancedFilterObject = getAdvancedFilterTypes(reportType, filterObj);
		advancedFilterObject.put(DeviceConstants.DATE_FILTER, getDateFilterTypes(reportType, filterObj));
		return advancedFilterObject;
	}

	public static JSONObject getDateFilterTypes(String reportType, JSONObject filterObj) throws ParseException {

		String optedFilter = filterObj.optString(DeviceConstants.DATE_FILTER);
		String fromDate = filterObj.optString(DeviceConstants.FROM_DATE);
		String toDate = filterObj.optString(DeviceConstants.TO_DATE);

		JSONObject dateFilterObj = new JSONObject();
		
		if (reportType.equalsIgnoreCase(DeviceConstants.TAX)) {
			dateFilterObj.put(DeviceConstants.FILTER_LIST, allowedTaxFilterTypes);
			dateFilterObj.put(DeviceConstants.CUSTOM_ENABLED, DeviceConstants.FALSE);
			if (optedFilter.isEmpty()) {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.FINANCIAL_YEAR+DateUtils.getFinancialYear());
			} else {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, optedFilter);
			}
			return dateFilterObj;

		}else if(reportType.equalsIgnoreCase(DeviceConstants.LEDGER_EQ) 
				|| reportType.equalsIgnoreCase(DeviceConstants.LEDGER_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_DERIVATIVE) ||
				reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_CURRENCY) 
				|| reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_DERIVATIVE) ||
				reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_CURRENCY) 
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_EQ) 
				|| reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_DERIVATIVE) ||
				reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_CURRENCY) 
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_COMMODITY)) {
			if(fromDate.isEmpty() || toDate.isEmpty())
				getCustomStartAndEndDateWithFYRestriction(dateFilterObj);
			else {
				setFromAndToDateFromRequest(dateFilterObj, fromDate, toDate);
				dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "true");
			}
		}else if(reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_EQUITY)){
			if(fromDate.isEmpty() || toDate.isEmpty())
				getCustomStartAndEndDateWithoutFYRestriction(dateFilterObj);
			else {
				setFromAndToDateFromRequest(dateFilterObj, fromDate, toDate);
				dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "false");
			}
		}else {
			dateFilterObj.put(DeviceConstants.FILTER_LIST, allowedFilterTypes);
			dateFilterObj.put(DeviceConstants.CUSTOM_ENABLED, DeviceConstants.TRUE);
			if (optedFilter.isEmpty()) {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.LAST_THIRTY_DAYS);
			} else {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, optedFilter);
			}
			if (optedFilter.equalsIgnoreCase(DeviceConstants.CUSTOM)) {

				dateFilterObj.put(DeviceConstants.FROM_DATE, filterObj.getString(DeviceConstants.FROM_DATE));
				dateFilterObj.put(DeviceConstants.TO_DATE, filterObj.getString(DeviceConstants.TO_DATE));

			}
		}
		return dateFilterObj;
	}

	public static JSONObject getAdvancedFilterTypes(String reportType, JSONObject filterObj) {

		String optedSortBy = "";
		String optedSortOrder = "";
		JSONObject advancedFilterObject = new JSONObject();
		if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_DERIVATIVE)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_CURRENCY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.LEDGER_EQ)
				|| reportType.equalsIgnoreCase(DeviceConstants.LEDGER_COMMODITY)
				||(reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_EQ)
				|| reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_DERIVATIVE)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_CURRENCY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_COMMODITY)
						)) {
			optedSortBy = DeviceConstants.DATE;
			optedSortOrder = DeviceConstants.DESCENDING;
		} else {
			optedSortBy = DeviceConstants.SYMBOL_FILTER;
			optedSortOrder = DeviceConstants.ASCENDING;

		}
		JSONArray optedFilterBy = new JSONArray();
		optedFilterBy.put("All");

		allowedFilterByList = new JSONArray();
		allowedSortByList = new JSONArray();
		
		if (filterObj.length() > 0) {
			if (!filterObj.getString(DeviceConstants.SORT_BY).isEmpty()) {
				optedSortBy = filterObj.getString(DeviceConstants.SORT_BY);
			}
			if (filterObj.getJSONArray(DeviceConstants.FILTER_BY).length() > 0) {
				optedFilterBy = filterObj.getJSONArray(DeviceConstants.FILTER_BY);
			}
			if (!filterObj.getString(DeviceConstants.SORT_ORDER).isEmpty() &&
					!filterObj.getString(DeviceConstants.SORT_BY).isEmpty()) {
				optedSortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
			}
		}

		if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_DERIVATIVE)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_CURRENCY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_COMMODITY)) {
			allowedFilterByList.put(GCConstants.BUY);
			allowedFilterByList.put(GCConstants.SELL);
			allowedSortByList.put(GCConstants.DATE);
			allowedSortByList.put(GCConstants.QUANTITY);
			allowedSortByList.put(GCConstants.SYMBOL);

		} else if (reportType.equalsIgnoreCase(DeviceConstants.LEDGER_EQ)
				|| reportType.equalsIgnoreCase(DeviceConstants.LEDGER_COMMODITY)) {

			allowedFilterByList.put(DeviceConstants.WITH_MARGIN_FILTER);
			allowedFilterByList.put(DeviceConstants.WITHOUT_MARGIN_FILTER);
			allowedSortByList.put(DeviceConstants.CREDIT);
			allowedSortByList.put(DeviceConstants.DEBIT);
			allowedSortByList.put(GCConstants.DATE);
			allowedSortByList.put(GCConstants.BALANCE);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.HOLDINGS)) {
			allowedFilterByList.put(DeviceConstants.SMALL_CAP_FILTER);
			allowedFilterByList.put(DeviceConstants.MID_CAP_FILTER);
			allowedFilterByList.put(DeviceConstants.LARGE_CAP_FILTER);
			allowedFilterByList.put(GCConstants.PROFIT);
			allowedFilterByList.put(GCConstants.LOSS);
			allowedSortByList.put(DeviceConstants.MARKET_VALUE_FILTER);
			allowedSortByList.put(GCConstants.QUANTITY);
			allowedSortByList.put(GCConstants.SYMBOL);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.TAX)) {
			allowedSortByList.put(DeviceConstants.TOTAL_TAX_FILTER);
			allowedSortByList.put(GCConstants.SYMBOL);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_DERIVATIVE)
				|| reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_CURRENCY)
				|| reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_COMMODITY)) {
			allowedSortByList.put(DeviceConstants.REALISED_PROFIT_LOSS_FILTER);
			allowedSortByList.put(GCConstants.QUANTITY);
			allowedSortByList.put(GCConstants.SYMBOL);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS)) {
			allowedSortByList.put(GCConstants.PROFIT);
			allowedSortByList.put(GCConstants.LOSS);
			allowedSortByList.put(GCConstants.SYMBOL);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_EQ)
				|| reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_DERIVATIVE)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_CURRENCY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_COMMODITY)) {
			allowedSortByList.put(GCConstants.DATE);
		}

		advancedFilterObject.put(DeviceConstants.FILTER_BY, allowedFilterByList);
		advancedFilterObject.put(DeviceConstants.SORT_BY, allowedSortByList);
		advancedFilterObject.put(DeviceConstants.OPTED_FILTER_BY, optedFilterBy);
		advancedFilterObject.put(DeviceConstants.OPTED_SORT_BY, optedSortBy);
		advancedFilterObject.put(DeviceConstants.OPTED_SORT_ORDER, optedSortOrder);

		return advancedFilterObject;

	}
	
	private static void getCustomStartAndEndDateWithFYRestriction(JSONObject dateFilterObj) {
		Calendar endCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		Date endDate = endCal.getTime();
		startCal.add(Calendar.MONTH, -1);
		Date startDate;
		if(endCal.get(Calendar.MONTH) == 3) {
			startCal.set(Calendar.MONTH, endCal.get(Calendar.MONTH));
			startCal.set(Calendar.DATE, 1);
			startDate = startCal.getTime();
		}else
			startDate = startCal.getTime();
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		String startDateStr = destinationFormat.format(startDate);
		String endDateStr = destinationFormat.format(endDate);
		dateFilterObj.put(DeviceConstants.FROM_DATE, startDateStr);
		dateFilterObj.put(DeviceConstants.TO_DATE, endDateStr);
		dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "true");
	}
	
	private static void getCustomStartAndEndDateWithoutFYRestriction(JSONObject dateFilterObj) {
		Calendar endCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		Date endDate = endCal.getTime();
		startCal.add(Calendar.MONTH, -1);
		Date startDate;
		startDate = startCal.getTime();
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		String startDateStr = destinationFormat.format(startDate);
		String endDateStr = destinationFormat.format(endDate);
		dateFilterObj.put(DeviceConstants.FROM_DATE, startDateStr);
		dateFilterObj.put(DeviceConstants.TO_DATE, endDateStr);
		dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "false");
	}
	
	private static void setFromAndToDateFromRequest(JSONObject dateFilterObj, String fromDate, String toDate) throws ParseException {
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		Date startDate = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT).parse(fromDate);
		Date endDate = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT).parse(toDate);
		String startDateStr = destinationFormat.format(startDate);
		String endDateStr = destinationFormat.format(endDate);
		
		dateFilterObj.put(DeviceConstants.FROM_DATE, startDateStr);
		dateFilterObj.put(DeviceConstants.TO_DATE, endDateStr);
	}
}