package com.globecapital.services.user;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.business.user.Login;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;

public class AdvanceLoginAuthenticate extends SessionService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sUserID = session.getUserID();
		String sAuthType = gcRequest.getFromData(DeviceConstants.AUTH_TYPE);
		String sToken = gcRequest.getFromData(DeviceConstants.S_MPIN);
		
		boolean isSuccess = false;
		
		if(sAuthType.equalsIgnoreCase(DeviceConstants.MPIN))
			isSuccess = AdvanceLogin.validateMPIN(sUserID, sToken);
		else if(sAuthType.equalsIgnoreCase(DeviceConstants.FINGERPRINT))
			isSuccess = true;
		
		if(isSuccess)
		{ 
		    Login.updateIs2FAAuthenticatedSuccess(session.getUserID());
		    if(GCUtils.reInitiateLogInBase(new FTRequest(),session, getServletContext(), gcRequest, gcResponse)) {
                session = gcRequest.getSession();
            }
            else 
                throw new GCException(InfoIDConstants.INVALID_SESSION, InfoMessage.getInfoMSG("info_msg.invalid.session_expired"));
			AdvanceLogin.updateAuthType(sUserID, sAuthType);
			JSONObject userInfoObj = session.getUserInfo();
			gcResponse.addToData(UserInfoConstants.BROADCAST_INFO, 
					userInfoObj.getJSONObject(UserInfoConstants.BROADCAST_INFO));
			gcResponse.addToData(DeviceConstants.STATUS, "OK");
			gcResponse.addToData(UserInfoConstants.REMARK, InfoMessage.getInfoMSG("info_msg.login.successful"));
			AdvanceLogin.updateFailureCount(sUserID, 0);
		}
		else
		{
			int maxAttempt = Integer.parseInt(InfoMessage.getInfoMSG("info_msg.login.max_mpin_attempt"));
			int mpinFailureCount = AdvanceLogin.getMPINFailureCount(sUserID)+ 1;
			AdvanceLogin.updateFailureCount(sUserID, mpinFailureCount);
			
			if(mpinFailureCount >= maxAttempt)
			{
				gcResponse.addToData(DeviceConstants.STATUS, DeviceConstants.MPIN_BLOCKED);
				gcResponse.addToData(UserInfoConstants.REMARK, InfoMessage.getInfoMSG("info_msg.login.mpin_blocked"));
			}
			else
			{
				throw new GCException(InfoIDConstants.DYNAMIC_MSG, 
						String.format(InfoMessage.getInfoMSG("info_msg.login.mpin_invalid"), mpinFailureCount) );
			}
		}
		
	}

}
