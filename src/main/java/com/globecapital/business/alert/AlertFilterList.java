package com.globecapital.business.alert;

import java.text.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.constants.DeviceConstants;

public class AlertFilterList {
	
	public static JSONArray getFilterList()throws JSONException, ParseException {
		
		JSONArray alertFilterList = new JSONArray();
		JSONObject compareFilterList = new JSONObject();
		JSONObject compareFilter = new JSONObject();
		JSONArray comparisonTypes = new JSONArray();
		JSONArray criteriaTypes = new JSONArray();
		
		criteriaTypes.put(DeviceConstants.LAST_TRADED_PRICE);
		criteriaTypes.put(DeviceConstants.DAY_CHANGE);
		criteriaTypes.put(DeviceConstants.DAY_CHANGE_PER);
		criteriaTypes.put(DeviceConstants.INTRADAY_CHANGE);
		criteriaTypes.put(DeviceConstants.INTRADAY_CHANGE_PER);
		
		JSONObject lessThan = new JSONObject();
		lessThan.put(DeviceConstants.TYPE, DeviceConstants.LT);
		lessThan.put(DeviceConstants.DISP_NAME,DeviceConstants.LESS_THAN);
		
		JSONObject greaterThan = new JSONObject();
		greaterThan.put(DeviceConstants.TYPE,DeviceConstants.GT);
		greaterThan.put(DeviceConstants.DISP_NAME,DeviceConstants.GREATER_THAN);
		
		JSONObject greaterThanEqualTo = new JSONObject();
		greaterThanEqualTo.put(DeviceConstants.TYPE,DeviceConstants.GTE);
		greaterThanEqualTo.put(DeviceConstants.DISP_NAME,DeviceConstants.GREATER_THAN_EQUAL_TO);
		
		JSONObject lessThanEqualTo = new JSONObject();
		lessThanEqualTo.put(DeviceConstants.TYPE,DeviceConstants.LTE);
		lessThanEqualTo.put(DeviceConstants.DISP_NAME,DeviceConstants.LESS_THAN_EQUAL_TO);
		
		comparisonTypes.put(lessThan);
		comparisonTypes.put(greaterThan);
		comparisonTypes.put(greaterThanEqualTo);
		comparisonTypes.put(lessThanEqualTo);
		
		compareFilter.put(DeviceConstants.COMPARE_TYPES, comparisonTypes);
		compareFilter.put(DeviceConstants.CRITERIA_TYPES, criteriaTypes);
		compareFilterList.put(DeviceConstants.FILTER_BY,compareFilter);
		
		return alertFilterList.put(compareFilterList);
	}
	
	public static void main(String[] args) throws JSONException, ParseException {
		System.out.println(getFilterList());
	}

}
