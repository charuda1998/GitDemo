package com.globecapital.services.user;

import com.globecapital.api.ft.user.LogOffAPI;
import com.globecapital.api.ft.user.LogOffRequest;
import com.globecapital.api.ft.user.LogOffResponse;
import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.business.user.Login;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class UserLogOff extends SessionService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		LogOffRequest logOffRequest = new LogOffRequest();
		Session session = gcRequest.getSession();
		logOffRequest.setUserID(session.getUserID());
		logOffRequest.setGroupId(session.getGroupId());
		logOffRequest.setJKey(session.getjKey());
		logOffRequest.setJSession(session.getjSessionID());

		LogOffAPI api = new LogOffAPI();
		
		LogOffResponse ftresponse = api.post(logOffRequest, LogOffResponse.class, session.getAppID(),"NetNetLogoff");

		if (ftresponse.getResponseStatus() && ftresponse.getResponseObject() == null) {
			Login.updateIs2FAAuthenticatedFalse(session.getUserID());
			boolean otpStatus=AdvanceLogin.setOtpStatus("Y",session.getUserID(), session.getAppID());
			if(otpStatus==true)
				log.info("UserID :"+session.getUserID()+" APPID :"+session.getAppID()+" OTP changed due to user Logout");
			gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.valid.login.LOG_OUT"));
		}
		else
			throw new RequestFailedException();
	}

}
