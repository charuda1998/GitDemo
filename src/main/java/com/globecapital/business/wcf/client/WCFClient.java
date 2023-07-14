package com.globecapital.business.wcf.client;

import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.business.wcf.db.WCFDbHandler;
import com.globecapital.business.wcf.helper.WCFHelper;
import com.globecapital.business.wcf.soap.FTResponseHandler;
import com.globecapital.business.wcf.soap.SoapReqResHandler;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.WCFConstants;
import com.msf.log.Logger;



public class WCFClient {

	private static Logger log = Logger.getLogger(WCFClient.class);

	static String dealerId = null;
	static String password = null;
	static String soapEndpointUrl = null;
	static String updatePGLimitResponse=null;
	static String logResponse=null;
	public static String sendLoginRequest(String clientIP, String appId) throws Exception {
		dealerId = AppConfig.getValue(WCFConstants.DEALER_ID);
		password = AppConfig.getValue(WCFConstants.PASSWORD);
		soapEndpointUrl = AppConfig.getValue(WCFConstants.SOAP_ENDPOINT_URL);
		String logonTxnId = dealerId + "-" + WCFHelper.getCurrentDateTime();
		String soapAction = AppConfig.getValue(WCFConstants.SOAP_ACTION_LOGIN);
		String request=String.format(WCFConstants.LIMIT_LOG_REQUEST ,dealerId,password, logonTxnId, clientIP);
		log.info(appId+" :"+request);
		log.info("Request URL = "+soapEndpointUrl);
		logResponse = SoapReqResHandler.callSoapWebService(soapEndpointUrl, soapAction, request);
		log.info(appId+" :"+logResponse);
		String sessionId = FTResponseHandler.handleLogonResponse(logResponse);
		
		if (WCFDbHandler.checkDealerInDB(dealerId))
			WCFDbHandler.updateSessionInDB(dealerId, sessionId);
		else
			WCFDbHandler.insertSessionInDB(dealerId, sessionId);
		return sessionId;
	}

	public static String sendPGUpdateLimitRequest(String userId, String amount, String sessionId,String merchantRefNo, String prodId, String groupId, String appId)
			throws Exception {

		String soapAction = AppConfig.getValue(WCFConstants.SOAP_ACTION_PG_UPDATE);
		String strRequestString = String.format(WCFConstants.PG_LIMIT_REQUEST, dealerId ,sessionId,userId,groupId, amount,merchantRefNo,prodId);
		log.info(strRequestString);
		char[] arr = strRequestString.toCharArray();
		long length = arr.length;
		String request = strRequestString + "|999=" + WCFHelper.generateCheckSum(arr, length);
		log.info(appId+" :"+request);
		updatePGLimitResponse = SoapReqResHandler.callSoapWebService(soapEndpointUrl, soapAction, request);
		log.info(appId+" :"+updatePGLimitResponse);
		String message =FTResponseHandler.handlePGLimitUpdateResponse(updatePGLimitResponse);
		return message;
	}
	
	public static String limitUpdate(String userId,String amount,String merchTransId, String clientIP, String groupId, String appId) {
		String response=null;
		String sessionId=null;
		try {
			try {
				sessionId=WCFClient.sendLoginRequest(clientIP, appId);
				WCFDbHandler.limitUpdate(RazorPayConstants.LMT_UPDATE_INITIATED,DeviceConstants.PENDING,merchTransId);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
			if(sessionId!=null) {
				try {
					response=WCFClient.sendPGUpdateLimitRequest(userId,amount,sessionId,merchTransId,"103",groupId, appId);
					if(response.equalsIgnoreCase(WCFConstants.PG_LIMIT_UPDATE_SUCCESS)) {
						response=WCFConstants.FUNDS_ADDED_SUCCESSFULLY;
					}
				} catch (Exception e) {
					log.error(e);
				}
				if(response==null) {
					response=FTResponseHandler.getFailureResponse(updatePGLimitResponse);
					WCFDbHandler.limitUpdateFailed(RazorPayConstants.LMT_UPDATE_RECEIVED+" "+response,RazorPayConstants.FAILURE,RazorPayConstants.TRANS_COMPLETED, merchTransId);
				}else {
					WCFDbHandler.limitUpdateReceived(DeviceConstants.SUCCESS,RazorPayConstants.TRANS_COMPLETED, merchTransId);
				}
			}else{
				response=FTResponseHandler.getFailureResponse(logResponse);
				WCFDbHandler.limitUpdateFailed(RazorPayConstants.LMT_UPDATE_INITIATED+" "+response,RazorPayConstants.FAILURE,RazorPayConstants.TRANS_COMPLETED, merchTransId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}	
		return response; 
	}
	
}
