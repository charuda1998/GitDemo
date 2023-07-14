package com.globecapital.business.funds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.msf.log.Logger;
import java.math.BigDecimal;

public class GetPayInTransactions {

	private static Logger log = Logger.getLogger(GetPayInTransactions.class);

	public static JSONArray mergeJsonArrays(JSONArray list1, JSONArray list2) {
		for (int i = 0; i < list2.length(); i++) {
			list1.put(list2.getJSONObject(i));
		}
		return list1;
	}

	public static JSONArray formatResponse(JSONArray payInDataArray) {

		JSONArray transactionList = new JSONArray();

		try {

			for (int i = 0 ; i < payInDataArray.length() ; i++ ) {
				
				JSONObject payInDataObj = payInDataArray.getJSONObject(i);

				JSONObject formattedResponseObj = new JSONObject();
				JSONObject txnInfo = new JSONObject();
				formattedResponseObj.put(DeviceConstants.DATE_S, payInDataObj.getString(DBConstants.CREATED_AT));
				formattedResponseObj.put(DeviceConstants.DISP_TRANS_TYPE, DeviceConstants.DEPOSIT);
				formattedResponseObj.put(DeviceConstants.TRANS_TYPE, DeviceConstants.PAY_IN_S);
				
				BigDecimal bd = new BigDecimal(String.valueOf(payInDataObj.getDouble(RazorPayConstants.AMOUNT))).setScale(2);
				
				formattedResponseObj.put(DeviceConstants.AMT, "\u20B9 "+ bd);
				formattedResponseObj.put(DeviceConstants.TRANS_STATUS, payInDataObj.getString(DeviceConstants.STATUS));
				txnInfo.put(DeviceConstants.TRANS_ID, payInDataObj.getString(DeviceConstants.MERCHANT_TRANS_NO));
				txnInfo.put(DeviceConstants.REF_NO, payInDataObj.getString(DeviceConstants.MERCHANT_TRANS_NO));
				txnInfo.put(DeviceConstants.SEGMENT, DeviceConstants.EQUITY.toUpperCase());
				txnInfo.put(DeviceConstants.PAYMENT_TYPE, payInDataObj.getString(DeviceConstants.PAYMENT_TYPE));
				formattedResponseObj.put(DeviceConstants.IS_CANCELLABLE, false);
				formattedResponseObj.put(DeviceConstants.TRANS_ADDITIONAL_INFO, txnInfo);

				transactionList.put(formattedResponseObj);
			}

		} catch (Exception e) {
			log.info(e);
		}
		return transactionList;
	}
	
	public static JSONArray sortArray(JSONArray listTosort, final String key, final String order) {

		if (listTosort != null) {

			List<JSONObject> JsonArrayAsList = new ArrayList<JSONObject>();
			for (int i = 0; i < listTosort.length(); i++)
				JsonArrayAsList.add(listTosort.getJSONObject(i));

			Collections.sort(JsonArrayAsList, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {

					if (order.equalsIgnoreCase(DeviceConstants.ASCENDING))
						return getDate(obj1.getString(key)).compareTo(getDate(obj2.getString(key)));
					else
						return getDate(obj2.getString(key)).compareTo(getDate(obj1.getString(key)));
				}
			});

			JSONArray resArray = new JSONArray(JsonArrayAsList);

			return resArray;

		} else
			return null;

	}

	public static Date getDate(String date) {
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date1;
	}

}
