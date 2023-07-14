package com.globecapital.jobs;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.razorpay.generics.PollOrderStatusAPI;
import com.globecapital.api.razorpay.generics.PollOrderStatusRequest;
import com.globecapital.api.razorpay.generics.PollOrderStatusResponse;
import com.globecapital.api.razorpay.generics.PollPaymentStatusAPI;
import com.globecapital.api.razorpay.generics.PollPaymentStatusRequest;
import com.globecapital.api.razorpay.generics.PollPaymentStatusResponse;
import com.globecapital.api.razorpay.generics.PollPaymentStatusRows;
import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.business.wcf.client.WCFClient;
import com.globecapital.business.wcf.db.WCFDbHandler;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.WCFConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.razorpay.PostTransactionDetailsBackOffice;
import com.globecapital.services.session.Session;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;

public class PollOrderStatus {
	
	private static Logger log;
	
	public static void main(String[] args) throws Exception {

		long beforeExecution = System.currentTimeMillis();
		
		String config_file = args[0];
		try {
			Properties JSLogProperties = new Properties();
			AppConfig.loadFile(config_file);
			FileInputStream stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			stream.close();
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(PollOrderStatus.class);
	        GCDBPool.initDataSource(AppConfig.getProperties());
			log.info("#############################################################################");
			log.info("##### JOB NAME : Poll Order - BEGINS");
			log.info("##### TIME : " + DateUtils.getCurrentDateTime(DeviceConstants.OPTIONS_DATE_FORMAT_1));
		} catch (ConfigurationException e) {
			System.out.println("Exception while configuring the JSLOG properties");
		}
		List<JSONObject> merchRefList = WCFDbHandler.getFailedTransactionDetails();
		log.info("No of Records to Poll : "+merchRefList.size());
		if(!merchRefList.isEmpty()) {
			for(JSONObject merchRef : merchRefList) {
				log.info("Record :"+merchRef);
				String encryptedDetails=merchRef.getString(RazorPayConstants.BANK_INFO);
				JSONObject bankObj= new JSONObject();
				JSONObject userInfo= new JSONObject();
				String merchTransNo=merchRef.getString(DeviceConstants.MERCHANT_TRANS_NO);
				String method=merchRef.getString(DeviceConstants.METHOD);
				String amount=merchRef.getString(RazorPayConstants.AMOUNT);
				String userId=merchRef.getString(RazorPayConstants.CLIENT_ID);
				String bankInfo=AESEncryption.decrypt(merchTransNo,encryptedDetails);
				getBankDetails(bankObj,bankInfo,amount);  
				userInfo.put(UserInfoConstants.USER_NAME,bankObj.getString(UserInfoConstants.USER_NAME));
				Session session=setSession(userId,userInfo);
				PollOrderStatusResponse pollOrderStatusResponse = getStatus(merchRef.getString(DeviceConstants.PG_ORDER_ID));
				bankObj.put(RazorPayConstants.CREATED_AT,pollOrderStatusResponse.getCreatedAt());
				PollPaymentStatusResponse pollPaymentStatusResponse = getOrderStatus(merchRef.getString(DeviceConstants.PG_ORDER_ID),"");
				if(pollPaymentStatusResponse.getCount()==0) {
					log.info("Failed Transaction Update "+merchTransNo);
					WCFDbHandler.gatewayFailResReceived(RazorPayConstants.GATEWAY_RES_RECEIVED+" Status :"+RazorPayConstants.FAILURE,RazorPayConstants.FAILURE, merchTransNo);
					PostTransactionDetailsBackOffice.updateTransactionDetails(session,method,bankObj,merchTransNo,RazorPayConstants.FAILURE,RazorPayConstants.FAILURE, merchTransNo,"Status :"+RazorPayConstants.FAILURE);
					continue;
				}
				List<PollPaymentStatusRows> orderRows = pollPaymentStatusResponse.getItems();
				String razorpayPaymentId = "", status= "" ;
				boolean isSuccess= false;
				for(PollPaymentStatusRows rows : orderRows) {
					razorpayPaymentId = rows.getId();
					status = rows.getStatus();
					if(rows.getStatus().equalsIgnoreCase(RazorPayConstants.CAPTURED)) {
						isSuccess=true;
						break;
					}
				}
				if(!isSuccess) {
					WCFDbHandler.gatewayFailResReceived(RazorPayConstants.GATEWAY_RES_RECEIVED+" Status :"+status,RazorPayConstants.FAILURE, merchTransNo);
					PostTransactionDetailsBackOffice.updateTransactionDetails(session,method,bankObj,razorpayPaymentId,RazorPayConstants.FAILURE,RazorPayConstants.FAILURE, merchTransNo, status);
				}
			}
		}

		long afterExecution = System.currentTimeMillis();
		
		log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
		log.info("##### JOB NAME : Poll Order - ENDS");
		log.info("#############################################################################");
	} 
	
	public static Session setSession(String userId,JSONObject userInfo) {
		Session session =new Session();
		session.setUserId(userId);
		session.setAppID("");
		session.setUserInfo(userInfo);
		return session;
	}
	
	public static PollPaymentStatusResponse getOrderStatus(String pgOrderId, String appId) throws GCException{
		PollPaymentStatusAPI pollPaymentStatusAPI = new PollPaymentStatusAPI(pgOrderId);
		PollPaymentStatusRequest razorPayRequest = new PollPaymentStatusRequest();
		return pollPaymentStatusAPI.get(razorPayRequest, PollPaymentStatusResponse.class,appId,"PaymentStatus");
	}
	
	public static void getBankDetails(JSONObject bankObj, String bankInfo, String amount) {
		if (!bankInfo.isEmpty()){
			String[] bankDetails=bankInfo.split("\\|");
		    bankObj.put(RazorPayConstants.BANK_ACCOUNT,bankDetails[0]);
		    bankObj.put(RazorPayConstants.IFSC,bankDetails[1]);
		    bankObj.put(RazorPayConstants.BANK_NAME,bankDetails[2]);
		    bankObj.put(UserInfoConstants.USER_NAME,bankDetails[3]);
		    bankObj.put(RazorPayConstants.AMOUNT,amount);
		    bankObj.put(RazorPayConstants.GROUP_ID, bankDetails[4]);
		}
	}
	
	public static PollOrderStatusResponse getStatus(String pgOrderId) throws JSONException, GCException {
		PollOrderStatusAPI pollOrderStatusAPI = new PollOrderStatusAPI(pgOrderId);
		PollOrderStatusRequest razorPayRequest = new PollOrderStatusRequest();
		return pollOrderStatusAPI.get(razorPayRequest, PollOrderStatusResponse.class, "","OrderStatus");
	}

}
