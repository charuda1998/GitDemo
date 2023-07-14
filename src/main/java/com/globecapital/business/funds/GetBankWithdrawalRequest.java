package com.globecapital.business.funds;

import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetBankWithdrawAPI;
import com.globecapital.api.gc.backoffice.GetBankWithdrawRequest;
import com.globecapital.api.gc.backoffice.GetBankWithdrawResponse;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.msf.log.Logger;

public class GetBankWithdrawalRequest {

	private static Logger log = Logger.getLogger(GetBankWithdrawalRequest.class);

	public static JSONObject withdrawalRequest(Session session, String segment, String withdrawAmt, String refNo,
			String acctNo) {

		String userId = session.getUserID();
		
		boolean withdrawalRequest = true;
		
		JSONObject withdrawReqDetails = new JSONObject();

		try {

			GetBankWithdrawRequest withdrawRequest = new GetBankWithdrawRequest();
			GetBankWithdrawResponse withdrawResponse = new GetBankWithdrawResponse();

			withdrawRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
			withdrawRequest.setClientCode(userId);

			/*if (segment.contains("Equity") || segment.contains("EQ") || segment.equalsIgnoreCase(DeviceConstants.ALL)) {
				withdrawRequest.setSegment("EQ");
			} else {
				withdrawRequest.setSegment("CO");
			}*/

			withdrawRequest.setSegment("EQ");
			
			withdrawRequest.setAmount(withdrawAmt);
			withdrawRequest.setReferenceNo(refNo);
			withdrawRequest.setAccountNo(acctNo);
			GetBankWithdrawAPI withDrawRequestAPI = new GetBankWithdrawAPI();
			withdrawResponse = withDrawRequestAPI.get(withdrawRequest, GetBankWithdrawResponse.class,
					session.getAppID(),"GetFundWithdrawalAmtRequest");
			withdrawReqDetails.put(DeviceConstants.DISCLAIMER, withdrawResponse.getDisclaimer());
			if (withdrawResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				withdrawRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				withdrawResponse = withDrawRequestAPI.get(withdrawRequest, GetBankWithdrawResponse.class, session.getAppID(),"GetFundWithdrawalAmtRequest");
			}
			if (withdrawResponse.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {
				withdrawalRequest = true;

			} else {
				throw new RequestFailedException();
			}

		} catch (Exception e) {
			log.error(e);
		}
		
		withdrawReqDetails.put(DeviceConstants.STATUS, withdrawalRequest);
		return withdrawReqDetails;

	}
}