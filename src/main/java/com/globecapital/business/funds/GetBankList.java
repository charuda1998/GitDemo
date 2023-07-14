package com.globecapital.business.funds;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetBankListAPI;
import com.globecapital.api.gc.backoffice.GetBankListRequest;
import com.globecapital.api.gc.backoffice.GetBankListResponse;
import com.globecapital.api.gc.backoffice.GetBankListRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.msf.log.Logger;

public class GetBankList {

	private static Logger log = Logger.getLogger(GetBankList.class);

	public static JSONArray bankDetails(Session session, String segment) throws RequestFailedException {

		String userId = session.getUserID();
		JSONArray bankList = new JSONArray();
		try {

			GetBankListRequest bankListRequest = new GetBankListRequest();
			GetBankListResponse bankListResponse = new GetBankListResponse();
			bankListRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
			bankListRequest.setClientCode(userId);

			/*if (segment.contains("Equity") || segment.contains("EQ")) {
				bankListRequest.setSegment("EQ");
			} else {
				bankListRequest.setSegment("CO");
			}*/
			
			bankListRequest.setSegment("EQ");

			GetBankListAPI bankListAPI = new GetBankListAPI();
			bankListResponse = bankListAPI.get(bankListRequest, GetBankListResponse.class, session.getAppID(),DeviceConstants.BANK_LIST_L);
			if (bankListResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				bankListRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				bankListResponse = bankListAPI.get(bankListRequest, GetBankListResponse.class, session.getAppID(),DeviceConstants.BANK_LIST_L);
			}
			if (bankListResponse.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {

				List<GetBankListRows> rows = bankListResponse.getDetails();
				for (GetBankListRows bankDetails : rows) {
					JSONObject bankListObj = new JSONObject();
					bankListObj.put(DeviceConstants.BANK_ACCOUNT_NO, bankDetails.getBankNo());
					bankListObj.put(DeviceConstants.BANK_NAME, bankDetails.getBankName());
					bankListObj.put(DeviceConstants.DISP_BANK_DETAILS,
							bankDetails.getBankName() + "-" + bankDetails.getBankNo());
					bankListObj.put(DeviceConstants.S_NET_BANKING, "true");
					bankListObj.put(DeviceConstants.S_UPI, "true");
					bankListObj.put(DeviceConstants.DEFAULT, DeviceConstants.S_NET_BANKING);
					bankList.put(bankListObj);
				}

			}
		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return bankList;

	}

}
