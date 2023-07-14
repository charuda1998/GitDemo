package com.globecapital.services.user;

import org.json.JSONObject;

import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class ForgotMPIN extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sUserID = session.getUserID();
		String sMPIN = gcRequest.getFromData(DeviceConstants.S_MPIN);
		String sAppID = gcRequest.getAppID();
		
		boolean isSuccess = AdvanceLogin.changeMPIN(sUserID, sMPIN, sAppID);
		
		if(isSuccess)
		{
			JSONObject userInfoObj = session.getUserInfo();
			gcResponse.addToData(UserInfoConstants.BROADCAST_INFO, 
					userInfoObj.getJSONObject(UserInfoConstants.BROADCAST_INFO));
			gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.login.mpin_change_successful"));
		}
		else
			throw new RequestFailedException();
		
	}

}
