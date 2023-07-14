package com.globecapital.business.funds;

import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetBankWithdrawalAmountAPI;
import com.globecapital.api.gc.backoffice.GetBankWithdrawalAmountRequest;
import com.globecapital.api.gc.backoffice.GetBankWithdrawalAmountResponse;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.session.Session;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class GetBankWithdrawalAmount {

	private static Logger log = Logger.getLogger(GetBankWithdrawalAmount.class);

	public static JSONObject withdrawalAmount(Session session, String segment) {

		String userId = session.getUserID();
		
		JSONObject withdrawalObject = new JSONObject();
		try {

			GetBankWithdrawalAmountRequest withdrawalRequest = new GetBankWithdrawalAmountRequest();
			GetBankWithdrawalAmountResponse withdrawalResponse = new GetBankWithdrawalAmountResponse();

			withdrawalRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
			withdrawalRequest.setClientCode(userId);

			/*if (segment.contains("Equity") || segment.contains("EQ") || segment.equalsIgnoreCase(DeviceConstants.ALL)) {
				withdrawalRequest.setSegment("EQ");
			} else {
				withdrawalRequest.setSegment("CO");
			}*/
			
			withdrawalRequest.setSegment("EQ");

			GetBankWithdrawalAmountAPI withdrawalAPI = new GetBankWithdrawalAmountAPI();
			withdrawalResponse = withdrawalAPI.get(withdrawalRequest, GetBankWithdrawalAmountResponse.class,
					session.getAppID(),"GetFundWithdrawalAmt");
			
			if (withdrawalResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				withdrawalRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				withdrawalResponse = withdrawalAPI.get(withdrawalRequest, GetBankWithdrawalAmountResponse.class, session.getAppID(),"GetFundWithdrawalAmt");
			} 

			if (withdrawalResponse.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {
				JSONObject withDrawableAmt = new JSONObject();
				withDrawableAmt.put(DeviceConstants.AMT, withdrawalResponse.getWithdrawableAmt());
				withDrawableAmt.put(DeviceConstants.DISP_AMT, "\u20B9" + 
						PriceFormat.formatPrice(withdrawalResponse.getWithdrawableAmt(), 2, false));

				JSONObject availableAmt = new JSONObject();
				availableAmt.put(DeviceConstants.AMT, withdrawalResponse.getAvailableAmt());
				availableAmt.put(DeviceConstants.DISP_AMT, "\u20B9" + 
						PriceFormat.formatPrice(withdrawalResponse.getAvailableAmt(), 2, false));
				withdrawalObject.put(DeviceConstants.AVAILABLE_AMT, availableAmt);
				withdrawalObject.put(DeviceConstants.WITHDRAWABLE_AMT, withDrawableAmt);
				withdrawalObject.put(DeviceConstants.REFERENCE_NO, withdrawalResponse.getReferenceNo());

			}
		} catch (Exception e) {
			log.error(e);
		}
		return withdrawalObject;

	}

}
