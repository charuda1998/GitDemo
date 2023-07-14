package com.globecapital.business.funds;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.utils.DateUtils;

public class FilterList {
	
	private static JSONArray allowedTransactionFilterTypes;
	private static JSONArray allowedTransactionSortTypes;
	
	static {
		allowedTransactionSortTypes = new JSONArray();
		JSONObject sortObjDate = new JSONObject();
		sortObjDate.put(DeviceConstants.TYPE, DeviceConstants.DATE);
		sortObjDate.put(DeviceConstants.DISP_NAME, DeviceConstants.DATE);
		allowedTransactionSortTypes.put(sortObjDate);
	}
	
	static {
		allowedTransactionFilterTypes = new JSONArray();
		allowedTransactionFilterTypes.put(DeviceConstants.DEPOSIT_S);
		allowedTransactionFilterTypes.put(DeviceConstants.WITHDRAW_S);
	}
	
	public static JSONObject getTransactionFilterList() {
		JSONObject transactionFilterObj = new JSONObject();
		transactionFilterObj.put(DeviceConstants.FILTER_BY, allowedTransactionFilterTypes);
		transactionFilterObj.put(DeviceConstants.SORT_BY, allowedTransactionSortTypes);
		transactionFilterObj.put(DeviceConstants.DATE_FILTER, getDateFilterTypes());
		return transactionFilterObj;
	}
	
	public static JSONObject getDateFilterTypes() {

		JSONObject dateFilterObj = new JSONObject();
		try {
			dateFilterObj.put(DeviceConstants.FROM_DATE, DateUtils.formatDate(DateUtils.getLastThirtyDays(),DeviceConstants.TO_DATE_FORMAT, DeviceConstants.TRANSACTION_DATE_FORMAT));
			dateFilterObj.put(DeviceConstants.TO_DATE, DateUtils.getCurrentDateTime(DeviceConstants.TRANSACTION_DATE_FORMAT));
			dateFilterObj.put(DeviceConstants.MAX_DATE_RANGE, DateUtils.formatDate(AppConfig.getValue("transaction.max.range"), DeviceConstants.TRANSACTION_DATE_FORMAT, DeviceConstants.TRANSACTION_DATE_FORMAT));
		} catch (JSONException | AppConfigNoKeyFoundException | ParseException e) {
			e.printStackTrace();
		}
		return dateFilterObj;
	}
}
