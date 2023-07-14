package com.globecapital.business.funds;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.GetBankTransactionAPI;
import com.globecapital.api.gc.backoffice.GetBankTransactionRequest;
import com.globecapital.api.gc.backoffice.GetBankTransactionResponse;
import com.globecapital.api.gc.backoffice.GetBankTransactionRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.session.Session;
import com.msf.cmots.helper.CMOTSHelper;
import com.msf.log.Logger;
import java.math.BigDecimal;

public class GetPayoutTransactions_101 {

	private static Logger log = Logger.getLogger(GetPayoutTransactions_101.class);
	

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
				bankTransactionResponse = bankTransactionAPI.get(bankTransactionRequest, GetBankTransactionResponse.class, session.getAppID()
						,"GetFundTransaction");
			}
			List<GetBankTransactionRows> transRows = bankTransactionResponse.getDetails();

			for (GetBankTransactionRows rows : transRows) {

				JSONObject payinDataObj = new JSONObject();
				JSONObject txnInfo = new JSONObject();
				payinDataObj.put(DeviceConstants.DATE_S,
						CMOTSHelper.formatDate(rows.getDateTime(), DeviceConstants.CORP_ACTION_DATE_FORMAT, DeviceConstants.DATE_FORMAT_1_TRANS));
				payinDataObj.put(DeviceConstants.DISP_TRANS_TYPE, DeviceConstants.WITHDRAW);
				payinDataObj.put(DeviceConstants.TRANS_TYPE, DeviceConstants.PAY_OUT_S);
				
				BigDecimal bd = new BigDecimal(rows.getRequestAmt()).setScale(2);
				 
				if (rows.getReleasedAmt().equals("") || rows.getReleasedAmt() == null||rows.getReleasedAmt().equalsIgnoreCase("0")) 
					payinDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
				else
					payinDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
				
				String status = rows.getStatus();
				
				if(status.equalsIgnoreCase(DeviceConstants.CANCELED))
					payinDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
				
				if(DeviceConstants.DISP_TRANS_TYPE.equalsIgnoreCase("DEPOSIT"))
					payinDataObj.put(DeviceConstants.AMT, "\u20B9 "+bd);
				
				if(status.equalsIgnoreCase(DeviceConstants.CANCELED))
					status = DeviceConstants.CANCELLED;
				else if(status.equalsIgnoreCase(DeviceConstants.REGISTERED))
					status = DeviceConstants.PENDING;
				
				payinDataObj.put(DeviceConstants.TRANS_STATUS, status.toUpperCase());

				txnInfo.put(DeviceConstants.TRANS_ID, rows.getTrxnId());

				txnInfo.put(DeviceConstants.REF_NO, rows.getTrxnId());

				txnInfo.put(DeviceConstants.SEGMENT, rows.getSegment().toUpperCase());
				txnInfo.put(DeviceConstants.PAYMENT_TYPE, "");
				if (status.equalsIgnoreCase(DeviceConstants.PENDING))
					payinDataObj.put(DeviceConstants.IS_CANCELLABLE, true);
				else
					payinDataObj.put(DeviceConstants.IS_CANCELLABLE, false);

				payinDataObj.put(DeviceConstants.TRANS_ADDITIONAL_INFO, txnInfo);

				transactionList.put(payinDataObj);
			}

		} catch (Exception e) {
			log.error(e);
		}

		return transactionList;
	}
}
