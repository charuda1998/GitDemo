package com.globecapital.business.funds;

import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetCancelWithdrawalRequest;
import com.globecapital.api.gc.backoffice.GetCancelWithdrawalResponse;
import com.globecapital.api.gc.backoffice.GetCancelWithdrawalRqstAPI;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.msf.log.Logger;

public class GetCancelTransactions extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(GetCancelTransactions.class);

	@Override
	protected void doPostProcess(GCRequest gcomsRequest, GCResponse gcomsResponse) throws Exception {
		// TODO Auto-generated method stub

		Session session = gcomsRequest.getSession();
		String txnid = gcomsRequest.getFromData("transId");
		String refNo = gcomsRequest.getFromData("refNo");

		getCancelResponse(txnid, session, gcomsResponse);

	}

	private static void getCancelResponse(String refNo, Session session, GCResponse gcomsResponse) {

		JSONObject cancelObject = new JSONObject();
		try {
			GetCancelWithdrawalRequest cancelRequest = new GetCancelWithdrawalRequest();
			GetCancelWithdrawalResponse cancelResponse = new GetCancelWithdrawalResponse();

			cancelRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
			cancelRequest.setClientCode(session.getUserID());
			cancelRequest.setTransactionId(refNo);

			GetCancelWithdrawalRqstAPI cancelApi = new GetCancelWithdrawalRqstAPI();
			cancelResponse = cancelApi.get(cancelRequest, GetCancelWithdrawalResponse.class, session.getAppID(),"GetCancelWithdrawRequest");

			if (cancelResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				cancelRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				cancelResponse = cancelApi.get(cancelRequest, GetCancelWithdrawalResponse.class, session.getAppID(),"GetCancelWithdrawRequest");
			}if(!cancelResponse.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)){
				cancelObject.put("refNo", cancelResponse.getReferenceNo());
				gcomsResponse.setInfoID(InfoIDConstants.DYNAMIC_MSG);
				gcomsResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.request_failed"));
			}
			else if (cancelResponse.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {

				String msg = cancelResponse.getMessage();
				if (msg.equalsIgnoreCase("Not Canceled")) {
					gcomsResponse.setInfoID(InfoIDConstants.DYNAMIC_MSG);
					gcomsResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.funds.not_canceled"));
				} else {
					cancelObject.put("refNo", cancelResponse.getReferenceNo());
					if (cancelResponse.getTxnStatus().equalsIgnoreCase("CANCELED")) {
						cancelObject.put(DeviceConstants.MSG, "Transaction Cancelled Successfully");
						cancelObject.put(DeviceConstants.IS_CANCELLED, "true");
					}
					else {
						if (cancelResponse.getTxnStatus().equalsIgnoreCase("Not Canceled")) {
							cancelObject.put(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.payment_not_cancelled"));
							cancelObject.put(DeviceConstants.IS_CANCELLED, "false");
						}
						else {
							cancelObject.put(DeviceConstants.MSG, cancelResponse.getTxnStatus());
							cancelObject.put(DeviceConstants.IS_CANCELLED, "false");
						}
					}
					gcomsResponse.addToData(DeviceConstants.CANCEL_DETAILS, cancelObject);
				}
			}

		} catch (Exception e) {
			gcomsResponse.setInfoID(InfoIDConstants.DYNAMIC_MSG);
			gcomsResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.request_failed"));
			log.error(e);
		}

	}

}
