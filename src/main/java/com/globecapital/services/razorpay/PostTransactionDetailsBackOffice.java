package com.globecapital.services.razorpay;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.GetPayinTransactionsAPI;
import com.globecapital.api.gc.backoffice.GetPayinTransactionsRequest;
import com.globecapital.api.gc.backoffice.GetPayinTransactionsResponse;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.msf.log.Logger;

public class PostTransactionDetailsBackOffice {
	
	static SecureRandom rand = new SecureRandom();
	private static Logger log = Logger.getLogger(PostTransactionDetailsBackOffice.class);
	
	public static void updateTransactionDetails(Session session, String method, JSONObject echoDetails, String paymentID, String ftApiStatus, String razorPayStatus, String merchantTransId, String description) {
		try {
			
			String userName = session.getUserInfo().getString(UserInfoConstants.USER_NAME);
			String userId = session.getUserID();
			String bankName = echoDetails.getString(RazorPayConstants.BANK_NAME);
			String bankAccount = echoDetails.getString(RazorPayConstants.BANK_ACCOUNT);
			String ifsc = echoDetails.getString(RazorPayConstants.IFSC);

			SimpleDateFormat transactionDateFormatter = new SimpleDateFormat(DeviceConstants.CORP_ACTION_DATE_FORMAT);
			SimpleDateFormat valueDateFormatter = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
			
			GetPayinTransactionsAPI getPayinTransactionsAPI = new GetPayinTransactionsAPI();
			GetPayinTransactionsRequest getPayinTransactionsRequest = new GetPayinTransactionsRequest();
			GetPayinTransactionsResponse getPayinTransactionsResponse = new GetPayinTransactionsResponse();
			
			if(echoDetails.has(RazorPayConstants.CREATED_AT)) {
				long datentime =echoDetails.getLong(RazorPayConstants.CREATED_AT)*1000;
				getPayinTransactionsRequest.setTransactionDate(transactionDateFormatter.format(new Date(datentime)));
				getPayinTransactionsRequest.setValueDate(valueDateFormatter.format(new Date(datentime)));
			}else {
				getPayinTransactionsRequest.setTransactionDate(transactionDateFormatter.format(new Date()));
				getPayinTransactionsRequest.setValueDate(valueDateFormatter.format(new Date()));
			}
			getPayinTransactionsRequest.setAlertSequenceNumber(merchantTransId);
			getPayinTransactionsRequest.setRemitterName(userName);
			getPayinTransactionsRequest.setRemitterAccount(bankAccount);
			getPayinTransactionsRequest.setRemitterBank(bankName);
			getPayinTransactionsRequest.setUserReferenceNumber(paymentID);
			getPayinTransactionsRequest.setBenefDetails("beneficiaryDetails");
			getPayinTransactionsRequest.setAmount(echoDetails.getString(RazorPayConstants.AMOUNT));
			getPayinTransactionsRequest.setMnemonicCode(method);
			getPayinTransactionsRequest.setDebitCredit(DeviceConstants.CREDIT);
			getPayinTransactionsRequest.setRemitterIFSC(ifsc);
			getPayinTransactionsRequest.setChequeNo(merchantTransId);
			getPayinTransactionsRequest.setTransactionDescription(description);
			getPayinTransactionsRequest.setAccountnumber(AppConfig.getValue("globe.beneficiary.account.number.payin.api"));
			getPayinTransactionsRequest.setTokenID(GCAPIAuthToken.getAuthToken());
			getPayinTransactionsRequest.setTrCode(userId);
			getPayinTransactionsRequest.setFtApiStatus(ftApiStatus);
			getPayinTransactionsRequest.setRazorApiStatus(razorPayStatus);
			
			getPayinTransactionsResponse = getPayinTransactionsAPI.post(getPayinTransactionsRequest, GetPayinTransactionsResponse.class, session.getAppID(),"GetPayinTransactions");
			
		} catch (GCException e) {
			log.info(e);
			e.printStackTrace();
		}
	}

	private static String generateRandomString(int len){
	   SimpleDateFormat sdf = new SimpleDateFormat(DeviceConstants.CURRENT_TIMESTAMP);
	   Date date = new Date();
	   StringBuilder sb = new StringBuilder(len);
	   for(int i = 0; i < len; i++)
	      sb.append(DeviceConstants.POSSIBLE_INPUTS .charAt(rand.nextInt(DeviceConstants.POSSIBLE_INPUTS_NUMERIC.length())));
	   return sb.toString().concat(sdf.format(date));
	}
}
