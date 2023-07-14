package com.globecapital.services.user;

import com.globecapital.api.ft.order.SendOrdReqResponse;
import com.globecapital.api.ft.user.GetMarketStatusAPI;
import com.globecapital.api.ft.user.GetMarketStatusRequest;
import com.globecapital.api.ft.user.GetMarketStatusResponse;
import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.business.user.Login;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.market.MarketStatus;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;

public class ValidateSession extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		String sIs2FAAuthenticated = session.getIs2FAAuthenticated();
		String sUserID = session.getUserID();
		String password = AdvanceLogin.getEncryptedPwd(AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"), 
                session.getUserID()),session.getAppID());
		if(password.isEmpty()) {
		    log.info("Session expired, Invalid advance session data");
		    throw new GCException(InfoIDConstants.INVALID_SESSION, 
                    InfoMessage.getInfoMSG("info_msg.invalid.session_expired"));
		}
		if(sIs2FAAuthenticated.equalsIgnoreCase("N") )//&& otpFlag.equalsIgnoreCase("Y"))
		{
			gcResponse.addToData(DeviceConstants.MPIN_ACTIVE, "false");
			gcResponse.addToData(DeviceConstants.FINGERPRINT_ACTIVE, "false");
			
			boolean isMPINEnabled = AdvanceLogin.isMPINEnabled(sUserID);
			boolean isFingerprintEnabled = AdvanceLogin.isFingerprintEnabled(sUserID);
			gcResponse.addToData(DeviceConstants.MPIN_REGISTERED,
					Boolean.toString(isMPINEnabled));
			gcResponse.addToData(DeviceConstants.FINGERPRINT_REGISTERED,
					Boolean.toString(isFingerprintEnabled));
			
			gcResponse.addToData(DeviceConstants.IS_REGISTERED, 
					Boolean.toString(AdvanceLogin.checkOneOfAdvLoginRegistered(isMPINEnabled, isFingerprintEnabled)));
			
		}
		else 
		{
			GetMarketStatusRequest marketStatusReq = new GetMarketStatusRequest();
			marketStatusReq.setUserID(session.getUserID());
			marketStatusReq.setGroupId(session.getGroupId());
			marketStatusReq.setJKey(session.getjKey());
			marketStatusReq.setJSession(session.getjSessionID());
			GetMarketStatusAPI marketStatusAPI = new GetMarketStatusAPI();
	
			try {
				GetMarketStatusResponse marketStatusRes = new GetMarketStatusResponse();
				try {
				    marketStatusRes = marketStatusAPI.post(marketStatusReq,GetMarketStatusResponse.class, session.getAppID(),"GetMarketStatusResponse");
				    log.info(marketStatusRes.getResponseStatus());
				}catch(GCException e) {
		            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
		                if(GCUtils.reInitiateLogIn(marketStatusReq,session, getServletContext(), gcRequest, gcResponse)) {
		                    marketStatusRes = marketStatusAPI.post(marketStatusReq,GetMarketStatusResponse.class, session.getAppID(),"GetMarketStatusResponse");
		                    session = gcRequest.getSession();
		                }
		                else 
		                    throw new GCException(InfoIDConstants.INVALID_SESSION, 
		                            InfoMessage.getInfoMSG("info_msg.invalid.session_expired"));
		            }    
		            else {
		                throw new GCException(InfoIDConstants.INVALID_SESSION, 
		                        InfoMessage.getInfoMSG("info_msg.invalid.session_expired"));
		            }
		        } catch(Exception e) {
	                if(GCUtils.reInitiateLogIn(marketStatusReq,session, getServletContext(), gcRequest, gcResponse)) {
                        marketStatusRes = marketStatusAPI.post(marketStatusReq,GetMarketStatusResponse.class, session.getAppID(),"GetMarketStatusResponse");
                        session = gcRequest.getSession();
                    }
                    else 
                        throw new GCException(InfoIDConstants.INVALID_SESSION, InfoMessage.getInfoMSG("info_msg.invalid.session_expired"));
		        }
				if (marketStatusRes.getResponseStatus()) {
					
					if(AppConfig.getValue("advancelogin.check_different_device").equalsIgnoreCase("true"))
					{
						String sAppID = session.getAppID();
						String sAdvanceLoginAppID = AdvanceLogin.getAppID(sUserID);
						
						if(!sAppID.equals(sAdvanceLoginAppID))
							AdvanceLogin.inactiveAdvanceLogin(sUserID);
					}
					
					gcResponse.addToData(DeviceConstants.MPIN_ACTIVE,
								Boolean.toString(AdvanceLogin.isMPINActive(sUserID)));
					gcResponse.addToData(DeviceConstants.FINGERPRINT_ACTIVE,
								Boolean.toString(AdvanceLogin.isFingerprintActive(sUserID)));
					
					boolean isMPINEnabled = AdvanceLogin.isMPINEnabled(sUserID);
					boolean isFingerprintEnabled = AdvanceLogin.isFingerprintEnabled(sUserID);
					gcResponse.addToData(DeviceConstants.MPIN_REGISTERED,
							Boolean.toString(isMPINEnabled));
					gcResponse.addToData(DeviceConstants.FINGERPRINT_REGISTERED,
							Boolean.toString(isFingerprintEnabled));
					
					gcResponse.addToData(DeviceConstants.IS_REGISTERED, 
							Boolean.toString(AdvanceLogin.
									checkOneOfAdvLoginRegistered(isMPINEnabled, isFingerprintEnabled)));
				}
			} catch (Exception e) {
				throw new GCException(InfoIDConstants.INVALID_SESSION, 
						InfoMessage.getInfoMSG("info_msg.invalid.session_expired"));
			}
		
		}

		

	}

}
