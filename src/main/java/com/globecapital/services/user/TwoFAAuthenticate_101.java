package com.globecapital.services.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.user.TwoFAAuthResponseObject;
import com.globecapital.api.ft.user.TwoFAAuthenticateAPI;
import com.globecapital.api.ft.user.TwoFAAuthenticateResponse;
import com.globecapital.api.ft.user.TwoFAAuthenticationRequest;
import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.business.user.Login;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;

public class TwoFAAuthenticate_101 extends SessionService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void logRequest(GCRequest gcRequest) throws JSONException {
		gcRequest.maskValueInData(DeviceConstants.TOKEN);
		super.logRequest(gcRequest);
	}
	
	@Override
	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {
		Session session = request.getSession();
        List<String> maskValueList = new ArrayList<>();
        String sUserID, sGroupID, sToken;
        if(!request.getFromData(DeviceConstants.TOKEN).isEmpty()) {
            sUserID = session.getUserID();
            sGroupID = session.getGroupId();
            sToken = request.getFromData(DeviceConstants.TOKEN);
            TwoFAAuthenticationRequest ftrequest = new TwoFAAuthenticationRequest();
            ftrequest.setUserID(sUserID);
            ftrequest.setGroupId(sGroupID);
            ftrequest.setToken(sToken);
            ftrequest.setLoginAuth(true);
            ftrequest.setSsoLoginSession(false);
            ftrequest.setJKey(session.getjKey());
            ftrequest.setJSession(session.getjSessionID());
            ftrequest.isMaskEnabled = true;     //For Masking purpose
            maskValueList.add(FTConstants.TOKEN);
            ftrequest.addMaskToList(maskValueList); //For Masking purpose
            
            TwoFAAuthenticateAPI api = new TwoFAAuthenticateAPI();
            TwoFAAuthenticateResponse ftresponse =new TwoFAAuthenticateResponse();
            try {
            ftresponse= api.post(ftrequest, TwoFAAuthenticateResponse.class, session.getAppID(),"TwoFAAuthenticationApi");
            }
            catch(GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), request, response)) {
                    ftresponse= api.post(ftrequest, TwoFAAuthenticateResponse.class, session.getAppID(),"TwoFAAuthenticationApi");
                	session = request.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
            }
            java.util.List<TwoFAAuthResponseObject> obj = ftresponse.getResponseObject();
            String code = (obj.get(0)).getParam2();
        
            if (Integer.parseInt(code) == 0){
            	boolean otpStatus=AdvanceLogin.setOtpStatus("N",sUserID, request.getAppID());
            	boolean isUpdateSuccess=AdvanceLogin.updateOtpStatus(sUserID, request.getAppID());
    			if(otpStatus==true && isUpdateSuccess==true)
    				log.info("DB updated successfully");
    			
                Login.updateIs2FAAuthenticatedSuccess(session.getUserID());
                response.setSuccessMsg(InfoMessage.getValue("info_msg.valid.otp_validated"));
            }else if (Integer.parseInt(code) == 4) {
                throw new GCException(InfoIDConstants.DYNAMIC_MSG, String.format(InfoMessage.getValue("info_msg.otp_invalid_attempt3"),","));
            }else if (Integer.parseInt(code) == 1 || Integer.parseInt(code) == 9) {
                throw new GCException(InfoIDConstants.DYNAMIC_MSG, getErrorMsg((obj.get(0)).getParam3()));
            }
        }else {
            Login.updateIs2FAAuthenticatedSuccess(session.getUserID());
            response.setSuccessMsg(InfoMessage.getValue("info_msg.valid.otp_validated"));
        }
	}
	
	private static String getErrorMsg(String msg) {
	    try {
    	    if(msg == null ? false : msg.contains("Attempt 1 of 3"))
                return InfoMessage.getValue("info_msg.otp_invalid_attempt1");
            else if(msg == null ? false : msg.contains("Attempt 2 of 3"))
                return InfoMessage.getValue("info_msg.otp_invalid_attempt2");
        } catch (AppConfigNoKeyFoundException e) {
            log.info(e);
        }
        return msg;
    
	}

}
