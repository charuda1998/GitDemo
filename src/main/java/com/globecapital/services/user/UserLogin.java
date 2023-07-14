package com.globecapital.services.user;

import javax.servlet.ServletContext;

import org.json.JSONException;

import com.globecapital.api.ft.user.LoginResponse;
import com.globecapital.audit.GCAuditObject;
import com.globecapital.business.user.Login;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.SessionHelper;

public class UserLogin extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void logRequest(GCRequest gcRequest) throws JSONException {
		gcRequest.maskValueInData(UserInfoConstants.PASSWORD);
		super.logRequest(gcRequest);
	}

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		GCAuditObject auditObj = gcRequest.getAuditObj();

		LoginResponse loginResponse = Login.verify_login(gcRequest.getFromData(UserInfoConstants.USER_ID),
				gcRequest.getFromData(UserInfoConstants.PASSWORD), "", gcRequest.getClientIP(), 
				gcRequest.getAppID());

		if (loginResponse.isLogonSucccess()) {

			ServletContext servletContext = getServletContext();
			SessionHelper.addSession(gcRequest, gcResponse, loginResponse, servletContext);
			gcResponse.setData(loginResponse.getLoginObj());

			auditObj.setAuditInfo(loginResponse.getSession());
			//TO-DO : audit for failure cases
			
		} else if (loginResponse.isAccBlockedOrExpired()) {
			gcResponse.setData(loginResponse.getLoginObj());
		} else {
			
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, loginResponse.getErrorMsg());
		}
	}

}