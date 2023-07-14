package com.globecapital.business.order;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.GetFundHistoryAPI;
import com.globecapital.api.gc.backoffice.GetFundHistoryRequest;
import com.globecapital.api.gc.backoffice.GetFundHistoryResponse;
import com.globecapital.api.gc.backoffice.GetFundHistoryRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.msf.cmots.helper.CMOTSHelper;
import com.msf.log.Logger;

public class GetFundHistory {

	private static Logger log = Logger.getLogger(GetFundHistory.class);
	

	public static JSONArray getFundDetails(Session session, String fromDate, String toDate, JSONArray filter) throws RequestFailedException {

		String userId = session.getUserID();

		JSONArray transactionList = new JSONArray();

		try {

			GetFundHistoryRequest fundHistoryRequest = new GetFundHistoryRequest();
			GetFundHistoryResponse fundHistoryResponse = null;

			fundHistoryRequest.setToken(GCAPIAuthToken.getAuthToken());
			fundHistoryRequest.setTrCode(userId);
			fundHistoryRequest.setFromDate(fromDate);
			fundHistoryRequest.setToDate(toDate);
			GetFundHistoryAPI fundHistoryAPI = new GetFundHistoryAPI();

			fundHistoryResponse = fundHistoryAPI.get(fundHistoryRequest, GetFundHistoryResponse.class,
					session.getAppID(),"GetFundHistory");
			if (fundHistoryResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				fundHistoryRequest.setToken(GCAPIAuthToken.getAuthToken());
				fundHistoryResponse = fundHistoryAPI.get(fundHistoryRequest, GetFundHistoryResponse.class, session.getAppID()
						,"GetFundHistory");
			}
			
			if(fundHistoryResponse.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {
				List<GetFundHistoryRows> transRows = fundHistoryResponse.getDetails();
			
				for (GetFundHistoryRows rows : transRows) {
			
					JSONObject fundDataObj = new JSONObject();
					JSONObject txnInfo = new JSONObject();
					if(filter.length() == 0 || filter.length() == 2) {
				    	//Do nothing
				    }else if(filter.getString(0).equalsIgnoreCase(DeviceConstants.WITHDRAW) && !rows.getTrxnType().equalsIgnoreCase(DeviceConstants.WITHDRAW)) {
				    	continue;
				    }
				    else if(filter.getString(0).equalsIgnoreCase(DeviceConstants.DEPOSIT) && !rows.getTrxnType().equalsIgnoreCase(DeviceConstants.DEPOSIT)) {
				    	continue;
				    }
					fundDataObj.put(DeviceConstants.DATE_S,
							CMOTSHelper.formatDate(rows.getTransTimeStamp(), DeviceConstants.FROM_HISTORY_DATE_FORMAT, DeviceConstants.DATE_FORMAT_1_TRANS));
					fundDataObj.put(DeviceConstants.DISP_TRANS_TYPE, rows.getTrxnType());
					if(rows.getTrxnType().equalsIgnoreCase(DeviceConstants.DEPOSIT))
						fundDataObj.put(DeviceConstants.TRANS_TYPE, DeviceConstants.PAY_IN_S);
					else
						fundDataObj.put(DeviceConstants.TRANS_TYPE, DeviceConstants.PAY_OUT_S);
			
					BigDecimal bd = new BigDecimal(rows.getAmt()).setScale(2);
					 
					if (rows.getAmt().equals("") || rows.getAmt() == null||rows.getAmt().equalsIgnoreCase("0")) 
						fundDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
					else
						fundDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
					
					String status = rows.getTrxnStatus();
					
					if(status.equalsIgnoreCase(DeviceConstants.CANCELED))
						fundDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
					
					if(DeviceConstants.DISP_TRANS_TYPE.equalsIgnoreCase(DeviceConstants.DEPOSIT))
						fundDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
					
					if(status.equalsIgnoreCase(DeviceConstants.CANCELED))
						status = DeviceConstants.CANCELLED;
					else if(status.equalsIgnoreCase(DeviceConstants.REGISTERED))
						status = DeviceConstants.PENDING;
					else if(status.equalsIgnoreCase(DeviceConstants.FAILED))
						status =  DeviceConstants.FAILURE;
					
					fundDataObj.put(DeviceConstants.TRANS_STATUS, status.toUpperCase());
			
					txnInfo.put(DeviceConstants.TRANS_ID, rows.getTrxnNo());
			
					txnInfo.put(DeviceConstants.REF_NO, rows.getTrxnNo());
			
					txnInfo.put(DeviceConstants.SEGMENT, rows.getSegments().toUpperCase());
					txnInfo.put(DeviceConstants.PAYMENT_TYPE, "");
					if (status.equalsIgnoreCase(DeviceConstants.PENDING))
						fundDataObj.put(DeviceConstants.IS_CANCELLABLE, true);
					else
						fundDataObj.put(DeviceConstants.IS_CANCELLABLE, false);
			
					fundDataObj.put(DeviceConstants.TRANS_ADDITIONAL_INFO, txnInfo);
			
					transactionList.put(fundDataObj);
				}
			}
		}catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
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
