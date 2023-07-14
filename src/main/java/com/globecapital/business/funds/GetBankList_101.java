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

public class GetBankList_101 {

	private static Logger log = Logger.getLogger(GetBankList_101.class);
	
	public static JSONArray bankDetails(Session session) throws RequestFailedException {

		String userId = session.getUserID();
		JSONArray bankList = new JSONArray();
		
		try {
			GetBankListRequest bankListRequest = new GetBankListRequest();
			GetBankListResponse bankListResponse = new GetBankListResponse();
			bankListRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
			bankListRequest.setClientCode(userId);
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
					JSONObject bankListupiObj = new JSONObject();
					JSONObject bankListnetObj = new JSONObject();
					JSONObject bankListneftObj = new JSONObject();
					
					bankListObj.put(DeviceConstants.BANK_NAME, bankDetails.getBankName());
					bankListObj.put(DeviceConstants.BANK_ACCOUNT, bankDetails.getBankNo());
					String bankno= bankDetails.getBankNo();
					bankListObj.put(DeviceConstants.DISP_BANK_DETAILS,
							bankDetails.getBankName() + " " + getMaskedBankNo(bankno));
					
					bankListObj.put(DeviceConstants.OPTED_MODE,DeviceConstants.S_UPI);
					bankListObj.put(DeviceConstants.IFSC,bankDetails.getIfsc());
					bankListObj.put(DeviceConstants.BANK_CODE,bankDetails.getBankCode());
					
					JSONArray modes = new JSONArray();
					
					bankListupiObj.put(DeviceConstants.TYPE,DeviceConstants.S_UPI);
					bankListupiObj.put(DeviceConstants.IS_ENABLED, "true");
					bankListupiObj.put(DeviceConstants.DISP_NAME, DeviceConstants.UPINAME);

					bankListnetObj.put(DeviceConstants.TYPE,DeviceConstants.S_NET_BANKING);
					bankListnetObj.put(DeviceConstants.IS_ENABLED, "true");
					bankListnetObj.put(DeviceConstants.DISP_NAME,DeviceConstants.NETNAME);

					bankListneftObj.put(DeviceConstants.TYPE,DeviceConstants.S_NEFT);
					bankListneftObj.put(DeviceConstants.IS_ENABLED, "true");
					bankListneftObj.put(DeviceConstants.DISP_NAME, DeviceConstants.NEFTNAME);
					
					modes.put(bankListupiObj);
					modes.put(bankListnetObj);
					modes.put(bankListneftObj);
					bankList.put(bankListObj);
					bankListObj.put(DeviceConstants.MODES, modes);
				}

			}
			
		} 
		catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return bankList;
	}
	public static String getMaskedBankNo(String bankNo) {
		String lastFourDigits = "";
		String mask= "XXX";
		if (bankNo.length() > 4)
		{
			lastFourDigits = bankNo.substring(bankNo.length() - 4);
		} 
		return mask+lastFourDigits;
	}
}
