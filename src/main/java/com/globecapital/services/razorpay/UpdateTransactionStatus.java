package com.globecapital.services.razorpay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.razorpay.generics.PollPaymentStatusResponse;
import com.globecapital.api.razorpay.generics.PollPaymentStatusRows;
import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.business.wcf.db.WCFDbHandler;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.WCFConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.jobs.PollOrderStatus;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.msf.cmots.config.AppConfig;
import com.msf.utils.helper.Helper;

public class UpdateTransactionStatus extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		JSONObject updatePGResp = new JSONObject();
		String respStatus = null;
		String userId=session.getUserID();
		JSONObject echoDetails = gcRequest.getObjectFromData(DeviceConstants.ECHO_DETAILS);
		JSONObject pgPayload = gcRequest.getObjectFromData(DeviceConstants.PG_PAYLOAD);
		String orderId = echoDetails.getString(DeviceConstants.PG_ORDER_ID);
		String merchantTransId = echoDetails.getString(DeviceConstants.MERCHANT_TRANS_NO);
		String method = echoDetails.getString(RazorPayConstants.METHOD);
		String status=gcRequest.getFromData(DeviceConstants.STATUS);
		log.info(session.getAppID()+" "+userId+" "+" "+merchantTransId);
		
		JSONObject transactionDetails = getTransactionStatus(merchantTransId);
		boolean fundsStatus = RazorPayConstants.FAILURE.equalsIgnoreCase(transactionDetails.getString(DBConstants.STATUS));
		boolean isTransactionCompleted = RazorPayConstants.TRANS_COMPLETED.equalsIgnoreCase(transactionDetails.getString(DBConstants.STAGE));
		if(!isTransactionCompleted) {
			if(status.equalsIgnoreCase(DeviceConstants.TRUE)) {
				String generated_signature=null;
				String razorpaySignature=pgPayload.getString(DeviceConstants.RAZORPAY_SIGNATURE);
				String razorpayPaymentId=pgPayload.getString(DeviceConstants.RAZORPAY_PAYMENT_ID);
				generated_signature = Signature.calculateRFC2104HMAC(orderId+"|"+razorpayPaymentId,AppConfig.getValue(DeviceConstants.RAZORPAY_KEY_SECRET));
				if (!generated_signature.equals(razorpaySignature)) {
					WCFDbHandler.gatewayFailResReceived(RazorPayConstants.GATEWAY_RES_RECEIVED+" "+RazorPayConstants.SIGNATURE_FAILED,RazorPayConstants.FAILURE, merchantTransId);
					respStatus=RazorPayConstants.SIGNATURE_FAILED;
					PostTransactionDetailsBackOffice.updateTransactionDetails(session,method,echoDetails,razorpayPaymentId,RazorPayConstants.FAILURE,RazorPayConstants.FAILURE, merchantTransId,respStatus);
				}else
					respStatus=WCFConstants.FUNDS_ADDED_SUCCESSFULLY;
			}else {
				String paymentId = isPaymentStatusUpdated(orderId, session.getAppID());
				if(paymentId.equalsIgnoreCase(DeviceConstants.FALSE)) {
					JSONObject errorObj = pgPayload.getJSONObject(RazorPayConstants.DESCRIPTION);
					log.info(errorObj);
					try {
						respStatus = new JSONObject(errorObj.getString(RazorPayConstants.DESCRIPTION)).getJSONObject(RazorPayConstants.ERROR).getString(RazorPayConstants.DESCRIPTION);
					}catch(JSONException ex) {
						respStatus= errorObj.getString(RazorPayConstants.DESCRIPTION);
						if(respStatus.contains("-")) {
							 respStatus = respStatus.split("-")[1];
						}
					}
					WCFDbHandler.gatewayFailResReceived(RazorPayConstants.GATEWAY_RES_RECEIVED+" "+respStatus,RazorPayConstants.FAILURE, merchantTransId);
					PostTransactionDetailsBackOffice.updateTransactionDetails(session,method,echoDetails,merchantTransId,RazorPayConstants.FAILURE,RazorPayConstants.FAILURE, merchantTransId,respStatus);
				}else
					respStatus=WCFConstants.FUNDS_ADDED_SUCCESSFULLY;
			}
			updatePGResp.put(DeviceConstants.MSG,respStatus);
		}else {
			if(!fundsStatus)
				updatePGResp.put(DeviceConstants.MSG, WCFConstants.FUNDS_ADDED_SUCCESSFULLY);
			else
				updatePGResp.put(DeviceConstants.MSG, RazorPayConstants.FAILURE);
		}
		if (!updatePGResp.isEmpty()) {
			gcResponse.setData(updatePGResp);
		} 
	}

	private JSONObject getTransactionStatus(String merchantTransId) {
		String query = DBQueryConstants.GET_TRANSACTION_DETAILS;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		JSONObject transactionDetails = new JSONObject();
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, merchantTransId);
			
			res = ps.executeQuery();

			String transactionStatus = "";
			String status = "";
			if (res.next()) {
				transactionStatus = res.getString(DBConstants.STAGE);
				status = res.getString(DBConstants.STATUS);
			}
			transactionDetails.put(DBConstants.STAGE, transactionStatus);
			transactionDetails.put(DBConstants.STATUS, status);
		} catch (SQLException e) {
			log.error(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return transactionDetails;
	}

	private String isPaymentStatusUpdated(String orderId, String appId) {
		try {
			PollPaymentStatusResponse pollPaymentStatusResponse = PollOrderStatus.getOrderStatus(orderId, appId);
			if(pollPaymentStatusResponse.getCount()==0)
				return "false";
			else {
				List<PollPaymentStatusRows> orderRows = pollPaymentStatusResponse.getItems();
				for(PollPaymentStatusRows rows : orderRows) {
					log.info("Payment status after repolling transaction :"+appId+" "+rows.getStatus());
					if(rows.getStatus().equalsIgnoreCase(RazorPayConstants.CAPTURED))
						return rows.getId();
					else {
						return "false";
					}
				}
			}
		} catch (GCException e) {
			log.error(e);
		}
		return "false";
	}
}
