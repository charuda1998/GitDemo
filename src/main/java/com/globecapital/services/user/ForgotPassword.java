package com.globecapital.services.user;

import com.globecapital.api.ft.user.ForgotPasswordAPI;
import com.globecapital.api.ft.user.ForgotPasswordRequest;
import com.globecapital.api.ft.user.ForgotPasswordResponse;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;

public class ForgotPassword extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String sUserID, sPhoneNo, sPanNo;
		
		sUserID = gcRequest.getFromData(UserInfoConstants.USER_ID);
		sPhoneNo = gcRequest.getOptFromData(UserInfoConstants.PHONE_NO, "");
		sPanNo = gcRequest.getOptFromData(UserInfoConstants.PAN, "");
		
		ForgotPasswordRequest ftRequest = new ForgotPasswordRequest();
		
		ftRequest.setUserID(sUserID);
		ftRequest.setSecretQAFlag("0");
		ftRequest.setSendMode("0");
		
		if( ! sPhoneNo.isEmpty() )
			ftRequest.setMobileNo(sPhoneNo);
		
		if( ! sPanNo.isEmpty() )
			ftRequest.setPan(sPanNo);
		
		
		ForgotPasswordAPI api = new ForgotPasswordAPI();
		
		ForgotPasswordResponse sendForgotPasswordResponse 
			= api.post(ftRequest, ForgotPasswordResponse.class, gcRequest.getAppID(),"SendForgotpwdRequest");
		
		String sStatus = sendForgotPasswordResponse.getResponseObject().getStatus();
		
		if(sendForgotPasswordResponse.getResponseStatus())
			gcResponse.setSuccessMsg(sStatus);
		else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, sStatus);

		
	}

}
