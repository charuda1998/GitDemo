package com.globecapital.services.user;

import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class AdvanceLoginUpdate extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sUserID = session.getUserID();
		String sAuthType = gcRequest.getFromData(DeviceConstants.AUTH_TYPE);
		String sActive = gcRequest.getFromData(DeviceConstants.ACTIVE);
		String sMPIN = gcRequest.getFromData(DeviceConstants.S_MPIN);
		
		boolean isSuccess = false;
		
			if(sAuthType.equalsIgnoreCase(DeviceConstants.MPIN) && sActive.equalsIgnoreCase("false"))
			{
				if(AdvanceLogin.validateMPIN(sUserID, sMPIN))
					isSuccess = AdvanceLogin.inactiveMPIN(sUserID);
				else
				{
					int maxAttempt = Integer.parseInt(InfoMessage.getInfoMSG("info_msg.login.max_mpin_attempt"));
					int mpinFailureCount = AdvanceLogin.getMPINFailureCount(sUserID)+ 1;
					AdvanceLogin.updateFailureCount(sUserID, mpinFailureCount);
					
					if(mpinFailureCount >= maxAttempt)
					{
						//isSuccess = true;
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
			else if(sAuthType.equalsIgnoreCase(DeviceConstants.FINGERPRINT) && sActive.equalsIgnoreCase("false"))
			{
				isSuccess = AdvanceLogin.inactiveFingerprint(sUserID);
			}
			else if(sAuthType.equalsIgnoreCase(DeviceConstants.MPIN) && sActive.equalsIgnoreCase("true"))
			{
				if(AdvanceLogin.validateMPIN(sUserID, sMPIN))
				{
					isSuccess = AdvanceLogin.activeMPIN(sUserID);
					
					if(AppConfig.getValue("advancelogin.check_different_device").equalsIgnoreCase("true"))
					{
						if(isSuccess)
						{
							String sAppID = session.getAppID();
							String sAdvanceLoginAppID = AdvanceLogin.getAppID(sUserID);
						
							if(!sAppID.equals(sAdvanceLoginAppID))
							{
								AdvanceLogin.updateAppID(sUserID, sAppID);
							}
						}
					}
				}
				else
				{
					int maxAttempt = Integer.parseInt(InfoMessage.getInfoMSG("info_msg.login.max_mpin_attempt"));
					int mpinFailureCount = AdvanceLogin.getMPINFailureCount(sUserID)+ 1;
					AdvanceLogin.updateFailureCount(sUserID, mpinFailureCount);
					
					if(mpinFailureCount >= maxAttempt)
					{
						//isSuccess = true;
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
			else if(sAuthType.equalsIgnoreCase(DeviceConstants.FINGERPRINT) && sActive.equalsIgnoreCase("true"))
			{
				isSuccess = AdvanceLogin.activeFingerprint(sUserID);
				
				if(AppConfig.getValue("advancelogin.check_different_device").equalsIgnoreCase("true"))
				{
					if(isSuccess)
					{
						String sAppID = session.getAppID();
						String sAdvanceLoginAppID = AdvanceLogin.getAppID(sUserID);
					
						if(!sAppID.equals(sAdvanceLoginAppID))
						{
							String sAppID1 = session.getAppID();
							String sAdvanceLoginAppID1 = AdvanceLogin.getAppID(sUserID);
						
							if(!sAppID1.equals(sAdvanceLoginAppID1))
							{
								AdvanceLogin.updateAppID(sUserID, sAppID1);
							}
						}
					}
				}
			}else if(sAuthType.equalsIgnoreCase(DeviceConstants.NOTIFICATION) && sActive.equalsIgnoreCase("true")) 
				isSuccess =AdvanceLogin.activeNotification(sUserID);
			else if(sAuthType.equalsIgnoreCase(DeviceConstants.NOTIFICATION) && sActive.equalsIgnoreCase("false")) 
				isSuccess = AdvanceLogin.inactiveNotification(sUserID);
			else
				throw new RequestFailedException();
			
			if(isSuccess)
				gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.login.successful"));
//			else
//				throw new RequestFailedException();
	}

}
