package com.globecapital.business.report;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.utils.DateUtils;

public class FilterList {

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

	public static JSONObject getFilterTypes(String reportType, JSONObject filterObj) {
		JSONObject advancedFilterObject = getAdvancedFilterTypes(reportType, filterObj);
		advancedFilterObject.put(DeviceConstants.DATE_FILTER, getDateFilterTypes(reportType, filterObj));
		return advancedFilterObject;
	}

	public static JSONObject getDateFilterTypes(String reportType, JSONObject filterObj) {

		String optedFilter = filterObj.optString(DeviceConstants.DATE_FILTER);

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

		} else {
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
		if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION)
				|| reportType.equalsIgnoreCase(DeviceConstants.LEDGER)||(reportType.equalsIgnoreCase(DeviceConstants.OTHER))) {
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
			if (!filterObj.getString(DeviceConstants.SORT_ORDER).isEmpty()
					&& !filterObj.getString(DeviceConstants.SORT_BY).isEmpty()) {
				optedSortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
			}
		}

		if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION)) {
			allowedFilterByList.put(GCConstants.BUY);
			allowedFilterByList.put(GCConstants.SELL);
			allowedSortByList.put(GCConstants.DATE);
			allowedSortByList.put(GCConstants.QUANTITY);
			allowedSortByList.put(GCConstants.SYMBOL);

		} else if (reportType.equalsIgnoreCase(DeviceConstants.LEDGER)) {

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
		} else if (reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS)) {
			allowedSortByList.put(DeviceConstants.REALISED_PROFIT_LOSS_FILTER);
			allowedSortByList.put(GCConstants.QUANTITY);
			allowedSortByList.put(GCConstants.SYMBOL);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS)) {
			allowedSortByList.put(GCConstants.PROFIT);
			allowedSortByList.put(GCConstants.LOSS);
			allowedSortByList.put(GCConstants.SYMBOL);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.OTHER)) {
			allowedSortByList.put(GCConstants.DATE);
		}

		advancedFilterObject.put(DeviceConstants.FILTER_BY, allowedFilterByList);
		advancedFilterObject.put(DeviceConstants.SORT_BY, allowedSortByList);
		advancedFilterObject.put(DeviceConstants.OPTED_FILTER_BY, optedFilterBy);
		advancedFilterObject.put(DeviceConstants.OPTED_SORT_BY, optedSortBy);
		advancedFilterObject.put(DeviceConstants.OPTED_SORT_ORDER, optedSortOrder);

		return advancedFilterObject;

	}
}