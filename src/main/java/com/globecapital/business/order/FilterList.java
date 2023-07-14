package com.globecapital.business.order;

import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.constants.DeviceConstants;

public class FilterList {
	
	private static JSONArray allowedPortfolioSortingTypes;
	private static JSONArray allowedTodaysPositionFilterTypes;
	private static JSONArray allowedDerivativePositionFilterTypes;
	
	static {
		allowedPortfolioSortingTypes = new JSONArray();
		JSONObject filterObjAlpha = new JSONObject();
		filterObjAlpha.put(DeviceConstants.TYPE, DeviceConstants.ALPHA);
		filterObjAlpha.put(DeviceConstants.DISP_NAME, DeviceConstants.ALPHABETICALLY);
		JSONObject filterObjPnlAbs = new JSONObject();
		filterObjPnlAbs.put(DeviceConstants.TYPE, DeviceConstants.PNL_ABS);
		filterObjPnlAbs.put(DeviceConstants.DISP_NAME, DeviceConstants.PROFIT_LOSS_ABSOLUTE);
		JSONObject filterObjPnlPerc = new JSONObject();
		filterObjPnlPerc.put(DeviceConstants.TYPE, DeviceConstants.PNL_PERCENT);
		filterObjPnlPerc.put(DeviceConstants.DISP_NAME, DeviceConstants.PROFIT_LOSS_PERCENTAGE);
		JSONObject filterObjQty = new JSONObject();
		filterObjQty.put(DeviceConstants.TYPE, DeviceConstants.QTY);
		filterObjQty.put(DeviceConstants.DISP_NAME, DeviceConstants.QUANTITY);
		allowedPortfolioSortingTypes.put(filterObjAlpha);
		allowedPortfolioSortingTypes.put(filterObjPnlAbs);
//		allowedPortfolioSortingTypes.put(filterObjPnlPerc);
		allowedPortfolioSortingTypes.put(filterObjQty);
	}
	
	static {
		allowedDerivativePositionFilterTypes = new JSONArray();
		allowedDerivativePositionFilterTypes.put(DeviceConstants.FUTURE);
		allowedDerivativePositionFilterTypes.put(DeviceConstants.OPTIONS_FILTER);
		allowedDerivativePositionFilterTypes.put(DeviceConstants.CURRENCY);
		allowedDerivativePositionFilterTypes.put(DeviceConstants.COMMODITY);
	}
	
	static {
		allowedTodaysPositionFilterTypes = new JSONArray();
		allowedTodaysPositionFilterTypes.put(DeviceConstants.FILTER_EQUITIES);
		allowedTodaysPositionFilterTypes.put(DeviceConstants.FUTURE);
		allowedTodaysPositionFilterTypes.put(DeviceConstants.OPTIONS_FILTER);
		allowedTodaysPositionFilterTypes.put(DeviceConstants.CURRENCY);
		allowedTodaysPositionFilterTypes.put(DeviceConstants.COMMODITY);
	}
	
	public static JSONArray getEquityFilterList() {

		JSONArray filter = new JSONArray();
		JSONObject equityFilter = new JSONObject();
		equityFilter.put(DeviceConstants.TYPE, DeviceConstants.EQUITIES);
		equityFilter.put(DeviceConstants.SORT_BY, allowedPortfolioSortingTypes);
		filter.put(equityFilter);
		
		
		return filter;
	}
	
	public static JSONArray getDerivativeFilterList() {

		JSONArray filter = new JSONArray();
		JSONObject todaysPositionFilter = new JSONObject();
		JSONObject derivativePositionFilter = new JSONObject();
		todaysPositionFilter.put(DeviceConstants.TYPE, DeviceConstants.TODAYS_POSITIONS);
		todaysPositionFilter.put(DeviceConstants.FILTER_BY, allowedTodaysPositionFilterTypes);
		todaysPositionFilter.put(DeviceConstants.SORT_BY, allowedPortfolioSortingTypes);
		filter.put(todaysPositionFilter);
		derivativePositionFilter.put(DeviceConstants.TYPE, DeviceConstants.DERIVATIVE_POSITIONS);
		derivativePositionFilter.put(DeviceConstants.FILTER_BY, allowedDerivativePositionFilterTypes);
		derivativePositionFilter.put(DeviceConstants.SORT_BY, allowedPortfolioSortingTypes);
		filter.put(derivativePositionFilter);
		return filter;
	}
	
}
