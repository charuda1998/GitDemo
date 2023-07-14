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

import com.globecapital.api.gc.backoffice.GetBankTransactionAPI;
import com.globecapital.api.gc.backoffice.GetBankTransactionRequest;
import com.globecapital.api.gc.backoffice.GetBankTransactionResponse;
import com.globecapital.api.gc.backoffice.GetBankTransactionRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.session.Session;
import com.msf.cmots.helper.CMOTSHelper;
import com.msf.log.Logger;

public class GetPayoutTransactions {

	private static Logger log = Logger.getLogger(GetPayoutTransactions.class);

	public static String ASCENDING = "ascending";

	public static String DESCENDING = "descending";

	public static JSONArray getTransactions(Session session, String fromDate, String toDate) {

		String userId = session.getUserID();

		JSONArray transactionList = new JSONArray();

		try {

			GetBankTransactionRequest bankTransactionRequest = new GetBankTransactionRequest();
			GetBankTransactionResponse bankTransactionResponse = null;

			bankTransactionRequest.setToken(GCAPIAuthToken.getAuthToken());
			bankTransactionRequest.setClientCode(userId);
			bankTransactionRequest.setFromDate(fromDate);
			bankTransactionRequest.setToDate(toDate);
			GetBankTransactionAPI bankTransactionAPI = new GetBankTransactionAPI();

			bankTransactionResponse = bankTransactionAPI.get(bankTransactionRequest, GetBankTransactionResponse.class,
					session.getAppID(),"GetFundTransaction");
			if (bankTransactionResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				bankTransactionRequest.setToken(GCAPIAuthToken.getAuthToken());
				bankTransactionResponse = bankTransactionAPI.get(bankTransactionRequest, GetBankTransactionResponse.class, session.getAppID(),"GetFundTransaction");
			}
			List<GetBankTransactionRows> transRows = bankTransactionResponse.getDetails();

			for (GetBankTransactionRows rows : transRows) {

				JSONObject jo = new JSONObject();
				JSONObject txnInfo = new JSONObject();
				jo.put("date",
						CMOTSHelper.formatDate(rows.getDateTime(), "dd/MM/yyyy hh:mm:ss a", "dd MMM yyyy HH:mm:ss"));
				jo.put("dispTransType", "PAY OUT");
				jo.put("transType", "payout");

				if (rows.getReleasedAmt().equals("") || rows.getReleasedAmt() == null||rows.getReleasedAmt().equalsIgnoreCase("0"))
					jo.put("amt", "\u20B9 "+rows.getRequestAmt());
				else
					jo.put("amt", "\u20B9 "+rows.getReleasedAmt());
				
				
				String status = rows.getStatus();
				
				if(status.equalsIgnoreCase("Canceled"))
					jo.put("amt", "\u20B9 "+rows.getRequestAmt());
				
				if(status.equalsIgnoreCase("Canceled"))
					status = "Cancelled";
				else if(status.equalsIgnoreCase("Registered"))
					status = "Pending";
				
				jo.put("transStatus", status.toUpperCase());

				txnInfo.put("transId", rows.getTrxnId());

				txnInfo.put("refNo", rows.getTrxnId());

				txnInfo.put("segment", rows.getSegment().toUpperCase());
				txnInfo.put("paymentType", "");
				if (status.equalsIgnoreCase("PENDING"))
					jo.put("isCancellable", true);
				else
					jo.put("isCancellable", false);

				jo.put("transAddtnlInfo", txnInfo);

				transactionList.put(jo);
			}

		} catch (Exception e) {

		}

		return transactionList;
	}

	public static JSONArray mergeJsonArrays(JSONArray list1, JSONArray list2) {
		for (int i = 0; i < list2.length(); i++) {
			list1.put(list2.getJSONObject(i));
		}
		return list1;
	}

	public static JSONArray getStatusBasedArray(JSONArray list, ArrayList<String> status) {
		JSONArray result = new JSONArray();
		int statusLen = status.size();

		for (int i = 0; i < list.length(); i++) {
			for (int j = 0; j < statusLen; j++) {
				String chkStatus = status.get(j);
				
				if(chkStatus.equalsIgnoreCase("CANCELED"))
					chkStatus = "Cancelled";
				if (list.getJSONObject(i).getString("transStatus").equalsIgnoreCase(chkStatus))
					result.put(list.getJSONObject(i));
			}
		}

		return result;
	}

	public static JSONArray sortArray(JSONArray listTosort, final String key, final String order) {

		if (listTosort != null) {

			List<JSONObject> JsonArrayAsList = new ArrayList<JSONObject>();
			for (int i = 0; i < listTosort.length(); i++)
				JsonArrayAsList.add(listTosort.getJSONObject(i));

			Collections.sort(JsonArrayAsList, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {

					if (order.equals(GetPayoutTransactions.ASCENDING))
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date1;
	}

}
