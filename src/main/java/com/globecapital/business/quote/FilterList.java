package com.globecapital.business.quote;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.exception.GCException;

public class FilterList {
	
	private static JSONArray allowedFilterTypes;
	private static JSONArray allowedEventsFilterTypes;
	private static JSONArray allowedResultsFilterTypes;
	
	static {
		allowedFilterTypes = new JSONArray();
		allowedFilterTypes.put(DeviceConstants.PERIOD_QUARTERLY);
		allowedFilterTypes.put(DeviceConstants.PERIOD_YEARLY);
	}
	
	static {
		allowedEventsFilterTypes = new JSONArray();
		allowedEventsFilterTypes.put(DeviceConstants.ALL);
		allowedEventsFilterTypes.put(DeviceConstants.FILTER_DIVIDEND);
		allowedEventsFilterTypes.put(DeviceConstants.FILTER_BONUS);
		allowedEventsFilterTypes.put(DeviceConstants.FILTER_STOCK_SPLIT);
		allowedEventsFilterTypes.put(DeviceConstants.FILTER_RIGHTS);
		allowedEventsFilterTypes.put(DeviceConstants.FILTER_COMP_ANNOUNCEMENTS);
	}
	
	static {
		allowedResultsFilterTypes = new JSONArray();
		allowedResultsFilterTypes.put(DeviceConstants.PERIOD_QUARTERLY);
		allowedResultsFilterTypes.put(DeviceConstants.PERIOD_HALF_YEARLY);
		allowedResultsFilterTypes.put(DeviceConstants.PERIOD_YEARLY);
	}
	
	public static final JSONArray getDefaultFilters()
	{
		return allowedFilterTypes;
	}

	public static JSONObject getFilterDetails(String sOptFilter)
	{
		JSONObject filterObj = new JSONObject();
		filterObj.put(DeviceConstants.FILTER_LIST, getDefaultFilters());
		filterObj.put(DeviceConstants.OPTED_FILTER, sOptFilter);
		return filterObj;
		
	}
	
	public static JSONArray getFilterList(String sType) throws GCException {
		if(sType.equalsIgnoreCase(DeviceConstants.EVENTS))
			return allowedEventsFilterTypes;
		else if(sType.equalsIgnoreCase(DeviceConstants.RESULTS))
			return allowedResultsFilterTypes;
		throw new GCException("Invalid filter type");
	}
	
}
