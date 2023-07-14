package com.globecapital.services.user;

import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class GetAdvanceLoginDetails extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();

		String sUserID = session.getUserID();
		
		
		if(AppConfig.getValue("advancelogin.check_different_device").equalsIgnoreCase("true"))
		{
			String sAppID = session.getAppID();
			String sAdvanceLoginAppID = AdvanceLogin.getAppID(sUserID);
			
			if(!sAppID.equals(sAdvanceLoginAppID))
				AdvanceLogin.inactiveAdvanceLogin(sUserID);
		}

		gcResponse.addToData(DeviceConstants.MPIN_ACTIVE, Boolean.toString(AdvanceLogin.isMPINActive(sUserID)));
		gcResponse.addToData(DeviceConstants.FINGERPRINT_ACTIVE,
				Boolean.toString(AdvanceLogin.isFingerprintActive(sUserID)));

		boolean isMPINEnabled = AdvanceLogin.isMPINEnabled(sUserID);
		boolean isFingerprintEnabled = AdvanceLogin.isFingerprintEnabled(sUserID);
		gcResponse.addToData(DeviceConstants.MPIN_REGISTERED, Boolean.toString(isMPINEnabled));
		gcResponse.addToData(DeviceConstants.FINGERPRINT_REGISTERED, Boolean.toString(isFingerprintEnabled));
		gcResponse.addToData(DeviceConstants.NOTIFICATION_ENABLED,Boolean.toString(AdvanceLogin.isNotificationActive(sUserID)));
		gcResponse.addToData(DeviceConstants.IS_REGISTERED,
				Boolean.toString(AdvanceLogin.checkOneOfAdvLoginRegistered(isMPINEnabled, isFingerprintEnabled)));
	}

}
