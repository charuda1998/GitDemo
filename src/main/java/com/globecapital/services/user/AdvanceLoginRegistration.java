package com.globecapital.services.user;

import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class AdvanceLoginRegistration extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sUserID = session.getUserID();
		String sMPIN = gcRequest.getFromData(DeviceConstants.S_MPIN);
		String sAuthType = gcRequest.getFromData(DeviceConstants.AUTH_TYPE);
		String sAppID = gcRequest.getAppID();
		
		boolean isSuccess = false;
		
		
			if(sAuthType.equals(DeviceConstants.MPIN))
			{
				isSuccess = AdvanceLogin.registerMPIN(sUserID, sMPIN, sAppID);
				if(isSuccess)
					gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.login.mpin_register_successful"));
			}
			else if(sAuthType.equals(DeviceConstants.FINGERPRINT))
			{
				isSuccess = AdvanceLogin.registerFingerPrint(sUserID, sAppID);
				if(isSuccess)
					gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.login.fingerprint_register_successful"));
			}
			else
				throw new RequestFailedException();
			
			if(!isSuccess)
				throw new RequestFailedException();
	}

}
