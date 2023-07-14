package com.globecapital.business.funds;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.utils.DateUtils;

public class FundTransfer {

	private static JSONArray allowedFilterByList;
	private static JSONArray allowedSortByList;

	static {
		allowedFilterByList = new JSONArray();
		allowedSortByList = new JSONArray();
//		allowedFilterByList.put(DeviceConstants.PAY_IN);
//		allowedFilterByList.put(DeviceConstants.PAY_OUT);
		allowedFilterByList.put(DeviceConstants.SUCCESS);
		allowedFilterByList.put(DeviceConstants.FAILED);
//		allowedFilterByList.put(DeviceConstants.IN_PROGRESS);
//		allowedFilterByList.put(DeviceConstants.INITIATE);
//		allowedFilterByList.put(DeviceConstants.PROCESSED);
		allowedFilterByList.put(DeviceConstants.PENDING);
		allowedFilterByList.put(DeviceConstants.CANCELLED);
		allowedSortByList.put(DeviceConstants.DATE);
	}

	public static JSONObject getFundTransferDetails(JSONArray prodList) {

		JSONObject finalObj = new JSONObject();

		finalObj.put(DeviceConstants.SEGMENT_LIST, getMarketDetails(prodList));
		finalObj.put(DeviceConstants.AMOUNT_LIST, getAmountDetails());
		finalObj.put(DeviceConstants.PAYMENT_OPTIONS, getPaymentOptions());
		finalObj.put(DeviceConstants.VIEW_TRANS_FILTER, getViewTransactionFilter());

		return finalObj;

	}

	private static JSONArray getPaymentOptions() {
		JSONArray paymentArr = new JSONArray();
		paymentArr.put(getPaymentObject(DeviceConstants.NET_BANKING, true, true));
		paymentArr.put(getPaymentObject(DeviceConstants.UPI, true, false));
		return paymentArr;
	}

	private static JSONObject getPaymentObject(String sPaymentType, boolean isActive, boolean isDefault) {
		JSONObject paymentObj = new JSONObject();
		paymentObj.put(DeviceConstants.TYPE, sPaymentType);
		paymentObj.put(DeviceConstants.IS_ACTIVE, Boolean.toString(isActive));
		paymentObj.put(DeviceConstants.IS_DEFAULT, Boolean.toString(isDefault));
		return paymentObj;

	}

	private static JSONArray getMarketDetails(JSONArray prodList) {
		JSONArray marketArr = new JSONArray();
		for (int i = 0; i < prodList.length(); i++) {
			JSONObject prodDetails = prodList.getJSONObject(i);
			String sMarketSegID = prodDetails.getString(SymbolConstants.MKT_SEG_ID);

			if (sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)
					|| sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)
					|| sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID)
					|| sMarketSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID)
					|| sMarketSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID)) {
				if (!checkValueExists(marketArr, DeviceConstants.EQ_DERIVATIVES_CURRENCY))
					marketArr.put(DeviceConstants.EQ_DERIVATIVES_CURRENCY);
			} else if (sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID)
					|| sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID)) {
				if (!checkValueExists(marketArr, DeviceConstants.COMMODITY))
					marketArr.put(DeviceConstants.COMMODITY);
			}

		}
		return marketArr;
	}

	private static JSONArray getAmountDetails() {

		JSONArray amountArr = new JSONArray();
		amountArr.put(getAmountObject(DeviceConstants.AMOUNT_25000));
		amountArr.put(getAmountObject(DeviceConstants.AMOUNT_50000));
		amountArr.put(getAmountObject(DeviceConstants.AMOUNT_100000));
		amountArr.put(getAmountObject(DeviceConstants.AMOUNT_500000));

		return amountArr;
	}

	private static JSONObject getAmountObject(String sAmount) {
		JSONObject amountObj = new JSONObject();
		amountObj.put(DeviceConstants.AMT, sAmount);
		if (sAmount.equals(DeviceConstants.AMOUNT_25000))
			amountObj.put(DeviceConstants.DISP_AMT, DeviceConstants.DISP_25000);
		else if (sAmount.equals(DeviceConstants.AMOUNT_50000))
			amountObj.put(DeviceConstants.DISP_AMT, DeviceConstants.DISP_50000);
		if (sAmount.equals(DeviceConstants.AMOUNT_100000))
			amountObj.put(DeviceConstants.DISP_AMT, DeviceConstants.DISP_100000);
		if (sAmount.equals(DeviceConstants.AMOUNT_500000))
			amountObj.put(DeviceConstants.DISP_AMT, DeviceConstants.DISP_500000);
		return amountObj;
	}

	private static boolean checkValueExists(JSONArray marketArr, String sToCheck) {
		return marketArr.toString().contains(sToCheck);

	}

	public static JSONObject getViewTransactionFilter() {
		String optedSortBy = "Date";
		String optedSortOrder = "Descending";
		JSONArray optedFilterBy = new JSONArray();
		optedFilterBy.put("All");

//		if (filterObj.length() > 0) {
//			if (!filterObj.getString(DeviceConstants.SORT_BY).isEmpty()) {
//				optedSortBy = filterObj.getString(DeviceConstants.SORT_BY);
//			}
//			if (filterObj.getJSONArray(DeviceConstants.FILTER_BY).length() > 0) {
//				optedFilterBy = filterObj.getJSONArray(DeviceConstants.FILTER_BY);
//			}
//			if (!filterObj.getString(DeviceConstants.SORT_ORDER).isEmpty()) {
//				optedSortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
//			}
//		}

		JSONObject transFilter = new JSONObject();
		transFilter.put(DeviceConstants.FILTER_BY, allowedFilterByList);
		transFilter.put(DeviceConstants.SORT_BY, allowedSortByList);
		transFilter.put(DeviceConstants.OPTED_FILTER_BY, optedFilterBy);
		transFilter.put(DeviceConstants.OPTED_SORT_BY, optedSortBy);
		transFilter.put(DeviceConstants.OPTED_SORT_ORDER, optedSortOrder);
		transFilter.put(DeviceConstants.DATE_FILTER, getDateFilterTypes());

		return transFilter;

	}

	public static JSONObject getDateFilterTypes() {

		JSONObject dateFilterObj = new JSONObject();
		dateFilterObj.put(DeviceConstants.FROM_DATE, DateUtils.getPreviousDate(DeviceConstants.TRANSACTION_DATE_FORMAT));
		dateFilterObj.put(DeviceConstants.TO_DATE, DateUtils.getCurrentDateTime(DeviceConstants.TRANSACTION_DATE_FORMAT));
		return dateFilterObj;
	}

	public static JSONObject getPayInDetails(JSONArray prodList, String sAppID) {
		JSONObject finalObj = new JSONObject();

		finalObj.put(DeviceConstants.AMOUNT_LIST, getAmountDetails());
		finalObj.put(DeviceConstants.SEGMENT_LIST, getMarketDetails(prodList));
		
		return finalObj;
	}
	
}
