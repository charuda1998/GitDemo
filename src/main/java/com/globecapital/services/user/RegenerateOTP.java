package com.globecapital.services.user;

import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.user.GenerateOTPAPI;
import com.globecapital.api.ft.user.GenerateOTPRequest;
import com.globecapital.api.ft.user.GenerateOTPResponse;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;

public class RegenerateOTP extends SessionService {
	private static final long serialVersionUID = 1L;

	
	@Override
	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {
		Session session = request.getSession();
        String sUserID, sGroupID;
        sUserID = session.getUserID();
        sGroupID = session.getGroupId();
        GenerateOTPRequest generateOTPRequest = new GenerateOTPRequest();
        generateOTPRequest.setUserID(sUserID);
        generateOTPRequest.setGroupId(sGroupID);
        generateOTPRequest.setJKey(session.getjKey());
        generateOTPRequest.setJSession(session.getjSessionID());

        GenerateOTPAPI generateOtpAPI = new GenerateOTPAPI();
        GenerateOTPResponse generateOTPresponse=new GenerateOTPResponse();
        try {
        generateOTPresponse = generateOtpAPI.post(generateOTPRequest, GenerateOTPResponse.class, session.getAppID(),"GenerateOTP");
        }
        catch(GCException e) {
        	if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(generateOTPRequest,session, getServletContext(), request, response)) {
                    generateOTPresponse = generateOtpAPI.post(generateOTPRequest, GenerateOTPResponse.class, session.getAppID(),"GenerateOTP");
                    session = request.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
        }
        if(!generateOTPresponse.isSuccessResponse()) 
            throw new GCException(InfoIDConstants.DYNAMIC_MSG, generateOTPresponse.getErrorMsg());
        else
            response.setInfoMsg("OTP Generated Succesfully");
	}

}
