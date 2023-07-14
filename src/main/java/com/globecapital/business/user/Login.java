package com.globecapital.business.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.user.GenerateOTPAPI;
import com.globecapital.api.ft.user.GenerateOTPRequest;
import com.globecapital.api.ft.user.GenerateOTPResponse;
import com.globecapital.api.ft.user.LoginAPI;
import com.globecapital.api.ft.user.LoginRequest;
import com.globecapital.api.ft.user.LoginResponse;
import com.globecapital.api.ft.user.LoginResponseObject;
import com.globecapital.api.ft.user.SetAuthStatusResponseObject;
import com.globecapital.api.ft.user.SetAuthenticationStatusAPI;
import com.globecapital.api.ft.user.SetAuthenticationStatusRequest;
import com.globecapital.api.ft.user.SetAuthenticationStatusResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.ProductType;
import com.globecapital.db.GCDBPool;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.utils.GCUtils;
import com.globecapital.validator.Validation;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Login {
	
	private static Logger log = Logger.getLogger(Login.class);

	public static LoginResponse verify_login(String sUserID, String sPassword, String sNewPwd, String sClientIP, 
			String appIDForLogging)
			throws Exception {

		String newPwd = "", sNewEncryptedPwd = "";
		//String password = Validation.passwordValidation(sPassword);
		String sEncryptedPwd = GCUtils.encryptPassword(sPassword);

		

		LoginRequest loginReq = new LoginRequest();

		loginReq.setUserID(sUserID);
		loginReq.setPassword(sEncryptedPwd);
		loginReq.setForceLoginTag(true);
		loginReq.setIPAddr(sClientIP);
		
		/*** sNewPwd is not empty, change password. Otherwise normal login ***/
		if (!sNewPwd.isEmpty()) {
			//newPwd = Validation.passwordValidation(sNewPwd);
			sNewEncryptedPwd = GCUtils.encryptPassword(sNewPwd);
		}

		if (!sNewEncryptedPwd.isEmpty())
			loginReq.setNewPassword(sNewEncryptedPwd);
		else
			loginReq.setNewPassword("");

		LoginAPI loginAPI = new LoginAPI();
		LoginResponse loginResp = loginAPI.post(loginReq, LoginResponse.class, appIDForLogging,"NetNetLogin");

		LoginResponseObject loginObj = loginResp.getResponseObject();

		loginResp.setUserStatus(getLogonStatus(loginObj.getLogonStatus()));
		loginResp.setErrorMsg(loginObj.getErrStr());
		
		String sUserStatus = loginResp.getUserStatus();
		
		if (sUserStatus.equals(LoginResponse.LOGIN_SUCCESS)) {
			loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, "OK");
			loginResp.setParticipantIDList(getParticipantIDList(loginObj));
			loginResp.setUserID(sUserID);
			loginResp.setProductList(getProductList(loginObj.getOeproduct()));
			loginResp.addToLoginObj(UserInfoConstants.USER_ID, sUserID);
			loginResp.addToLoginObj(UserInfoConstants.USER_NAME, loginObj.getUserName());
			loginResp.addToLoginObj(UserInfoConstants.LAST_LOGIN_TIME, loginObj.getLastLogonTime());
			loginResp.addToLoginObj(UserInfoConstants.BROADCAST_INFO, getBroadcastInfo(loginObj));
			loginResp.addToLoginObj(UserInfoConstants.IS_2FA_ENABLED,get2FAFlag(sUserID, appIDForLogging, loginObj));
			if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("4")) {
				loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "true");
				loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "false");
			}else if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("3")) {
				loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "false");
				loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "true");
			}
			loginResp.addToLoginObj(UserInfoConstants.REMARK, "");
			loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch("").isEmpty() ? false : true);
			//loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch(loginObj.getGtdAllowedExchanges()).isEmpty() ? false : true);
			loginResp.setProductList(getProductList(loginObj.getOeproduct()));
            loginResp.setGTDAllowed(getallowedGTDExch(""));
			//loginResp.setGTDAllowed(getallowedGTDExch(loginObj.getGtdAllowedExchanges()));
		}
		else if(sUserStatus.equals(LoginResponse.ACCOUNT_BLOCKED))
		{
			loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.ACCOUNT_BLOCKED);
			loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
		}
		else if(sUserStatus.equals(LoginResponse.PASSWORD_EXPIRED))
		{
			loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.PASSWORD_EXPIRED);
			loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
		}
		else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, 
					loginResp.getErrorMsg().isEmpty() ? 
							InfoMessage.getInfoMSG("info_msg.login.failed") : loginResp.getErrorMsg());

		return loginResp;

	}
	
	public static LoginResponse verify_login_101(String sUserID, String sPassword, String sNewPwd, String sClientIP, 
			String appIDForLogging)
			throws Exception {

		String newPwd = "", sNewEncryptedPwd = "";
		//String password = Validation.passwordValidation(sPassword);
		String sEncryptedPwd = GCUtils.encryptPassword(sPassword);

		

		LoginRequest loginReq = new LoginRequest();

		loginReq.setUserID(sUserID);
		loginReq.setPassword(sEncryptedPwd);
		loginReq.setForceLoginTag(true);
		loginReq.setIPAddr(sClientIP);
		
		/*** sNewPwd is not empty, change password. Otherwise normal login ***/
		if (!sNewPwd.isEmpty()) {
			//newPwd = Validation.passwordValidation(sNewPwd);
			sNewEncryptedPwd = GCUtils.encryptPassword(sNewPwd);
		}

		if (!sNewEncryptedPwd.isEmpty())
			loginReq.setNewPassword(sNewEncryptedPwd);
		else
			loginReq.setNewPassword("");

		LoginAPI loginAPI = new LoginAPI();
		LoginResponse loginResp = loginAPI.post(loginReq, LoginResponse.class, appIDForLogging,"NetNetLogin");

		LoginResponseObject loginObj = loginResp.getResponseObject();

		loginResp.setUserStatus(getLogonStatus(loginObj.getLogonStatus()));
		loginResp.setErrorMsg(loginObj.getErrStr());
		
		String sUserStatus = loginResp.getUserStatus();
		
		if (sUserStatus.equals(LoginResponse.LOGIN_SUCCESS)) {
			String otpFlag="";
			if(sNewPwd.isEmpty()) {
			otpFlag = addLoginUserToDB(AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"),sUserID),
					AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"),sPassword),appIDForLogging);
			log.info("user ID :"+sUserID+" AppID :"+appIDForLogging+" login Success flag value :"+otpFlag);
			}
			else {
				otpFlag = addLoginUserToDB(AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"),sUserID),
						AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"),sNewPwd),appIDForLogging);
				log.info("user ID :"+sUserID+" AppID :"+appIDForLogging+" change password flag value :"+otpFlag);
			}
						
			loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, "OK");
			loginResp.setParticipantIDList(getParticipantIDList(loginObj));
			loginResp.setUserID(sUserID);
			loginResp.setProductList(getProductList(loginObj.getOeproduct()));
			loginResp.addToLoginObj(UserInfoConstants.USER_ID, sUserID);
			loginResp.addToLoginObj(UserInfoConstants.USER_NAME, loginObj.getUserName());
			loginResp.addToLoginObj(UserInfoConstants.LAST_LOGIN_TIME, loginObj.getLastLogonTime());
			loginResp.addToLoginObj(UserInfoConstants.BROADCAST_INFO, getBroadcastInfo(loginObj));
			loginResp.addToLoginObj(UserInfoConstants.IS_2FA_ENABLED,get2FAFlag_101(sUserID, appIDForLogging, loginObj));
			loginResp.addToLoginObj(UserInfoConstants.REMARK, "");
			//loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch(loginObj.getGtdAllowedExchanges()).isEmpty() ? false : true);
			loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch("").isEmpty() ? false : true);
			loginResp.addToLoginObj(UserInfoConstants.IS_2FA_MANDATORY, true );
			loginResp.addToLoginObj(UserInfoConstants.IS_OTP_2FA_REQUIRED,otpFlag.equals("Y")?true:false);
			loginResp.addToLoginObj(UserInfoConstants.MOBILE_NUMBER, maskMobileNumber(loginObj.getMobileNo()) );
			if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("4")) {
				loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "true");
				loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "false");
			}else if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("3")) {
				loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "false");
				loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "true");
			}
			loginResp.setProductList(getProductList(loginObj.getOeproduct()));
			//loginResp.setGTDAllowed(getallowedGTDExch(loginObj.getGtdAllowedExchanges()));
			loginResp.setGTDAllowed(getallowedGTDExch(""));
		}
		else if(sUserStatus.equals(LoginResponse.ACCOUNT_BLOCKED))
		{
			loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.ACCOUNT_BLOCKED);
			loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
		}
		else if(sUserStatus.equals(LoginResponse.PASSWORD_EXPIRED))
		{
			boolean otpStatus=AdvanceLogin.setOtpStatus("Y",sUserID, appIDForLogging);
			
			if(otpStatus==true)
				log.info("OTP updated successfully from Manual Login");

			loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.PASSWORD_EXPIRED);
			loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
		}
		else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, 
					loginResp.getErrorMsg().isEmpty() ? 
							InfoMessage.getInfoMSG("info_msg.login.failed") : loginResp.getErrorMsg());

		return loginResp;

	}

	public static String addLoginUserToDB(String sUserID, String sPwd, String appIDForLogging) throws SQLException {
		
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet res = null;
		String otpValidQuery = DBQueryConstants.OTP_VALIDATION_PROCEDURE;
		String otpFlag="";
		try {
			conn = GCDBPool.getInstance().getConnection();
			cstmt = conn.prepareCall(otpValidQuery);
			cstmt.setString(1, sUserID);
			cstmt.setString(2, sPwd);
			cstmt.setString(3, appIDForLogging);
			cstmt.registerOutParameter(4,java.sql.Types.VARCHAR);
			cstmt.executeQuery();
			otpFlag=cstmt.getString(4);			
		} catch (Exception e) {
			log.error(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(cstmt);
			Helper.closeConnection(conn);
		}
		return otpFlag;
	}

	public static Boolean get2FAFlag(String sUserID, String appIDForLogging, LoginResponseObject loginRespObj)
			throws JSONException, GCException {
		SetAuthenticationStatusRequest authStatusReq = new SetAuthenticationStatusRequest();

		authStatusReq.setJKey(loginRespObj.getTransnID());
		String sEncryptJSession = GCUtils.encryptPassword(loginRespObj.getSessionId());
		authStatusReq.setJSession(sEncryptJSession);

		authStatusReq.setUserID(sUserID);
		authStatusReq.setGroupId(loginRespObj.getGroupId());
		authStatusReq.setLogonTag("1");
		authStatusReq.setVendorID(loginRespObj.getSecondLevelVendorID());

		SetAuthenticationStatusAPI authStatusAPI = new SetAuthenticationStatusAPI();

		SetAuthenticationStatusResponse authStatusRes = authStatusAPI.post(authStatusReq,
				SetAuthenticationStatusResponse.class, appIDForLogging,"AuthenticationStatus");

		SetAuthStatusResponseObject authStatusResObj = authStatusRes.getResponseObject();

		if ((authStatusResObj.getLogin2FA()).equals("1"))
			return true;
		else
			return false;
	}

	
	public static Boolean get2FAFlag_101(String sUserID, String appIDForLogging, LoginResponseObject loginRespObj)
			throws JSONException, GCException {
		SetAuthenticationStatusRequest authStatusReq = new SetAuthenticationStatusRequest();

		authStatusReq.setJKey(loginRespObj.getTransnID());
		String sEncryptJSession = GCUtils.encryptPassword(loginRespObj.getSessionId());
		authStatusReq.setJSession(sEncryptJSession);

		authStatusReq.setUserID(sUserID);
		authStatusReq.setGroupId(loginRespObj.getGroupId());
		authStatusReq.setLogonTag("1");
		authStatusReq.setVendorID(loginRespObj.getSecondLevelVendorID());

		SetAuthenticationStatusAPI authStatusAPI = new SetAuthenticationStatusAPI();

		SetAuthenticationStatusResponse authStatusRes =new SetAuthenticationStatusResponse();
		
		authStatusRes= authStatusAPI.post(authStatusReq,
				SetAuthenticationStatusResponse.class, appIDForLogging,"AuthenticationStatus");
		
		SetAuthStatusResponseObject authStatusResObj = authStatusRes.getResponseObject();
		
		if(loginRespObj.getSecondLevelVendorID().equalsIgnoreCase("4") && !authStatusResObj.getOTPEnabled().equals("1"))
		    throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.otp_not_enabled"));
		    
		if ((authStatusResObj.getLogin2FA()).equals("1"))
			return true;
		else
		    throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.order_error"));
	}

	public static String getLogonStatus(int iStatusCode) {
		if (Integer.compare(iStatusCode, FTConstants.USER_LOGIN_SUCCESS_1) == 0
				|| Integer.compare(iStatusCode, FTConstants.USER_LOGIN_SUCCESS_2) == 0
				|| Integer.compare(iStatusCode, FTConstants.PWD_LOGIN_SUCCESS) == 0)
			return LoginResponse.LOGIN_SUCCESS;
		else if (Integer.compare(iStatusCode, FTConstants.INVALID_USER_CODE) == 0)
			return LoginResponse.INVALID_USER;
		else if (Integer.compare(iStatusCode, FTConstants.INVALID_PASSWORD) == 0)
			return LoginResponse.INVALID_PASSSWORD;
		else if (Integer.compare(iStatusCode, FTConstants.USER_LOCKED_1) == 0
				|| Integer.compare(iStatusCode, FTConstants.USER_LOCKED_2) == 0)
			return LoginResponse.ACCOUNT_BLOCKED;
		else if (Integer.compare(iStatusCode, FTConstants.USER_PWD_EXPIRED) == 0)
			return LoginResponse.PASSWORD_EXPIRED;
		else if (Integer.compare(iStatusCode, FTConstants.PWD_VALIDATION_FAILED) == 0)
			return LoginResponse.PWD_VALIDATION_FAILED;
		else if (Integer.compare(iStatusCode, FTConstants.INVALID_LISENCE) == 0)
			return LoginResponse.INVALID_LICENSE;
		else
			return "";
	}

	public static JSONObject getParticipantIDList(LoginResponseObject responseObject) throws JSONException {

		JSONObject participantObj = new JSONObject();

		participantObj.put(ExchangeSegment.NSE_SEGMENT_ID, responseObject.getNsePartCode());
		participantObj.put(ExchangeSegment.NFO_SEGMENT_ID, responseObject.getNsefaoPartCode());
		participantObj.put(ExchangeSegment.NSECDS_SEGMENT_ID, responseObject.getNsecdsPartCode());
		participantObj.put(ExchangeSegment.BSE_SEGMENT_ID, responseObject.getBsePartType());

		return participantObj;

	}

	public static JSONArray getProductList(String sOEProducts) throws Exception {
		JSONArray productListObj = new JSONArray();
		String sOEProduct = sOEProducts;

		if (sOEProduct.isEmpty())
			return productListObj;
		else {
			//Sample sOEProduct Value: 1:DELIVERY$MARGIN$BRACKET ORDER;2:CARRYFORWARD$INTRADAY;
			String[] arrOfOeproduct = sOEProduct.split(";");

			for (String sMarketSegmentAndProducts : arrOfOeproduct) {
				
				JSONObject prodObj = new JSONObject();

				String[] arrOfSegmentAndProductStr = sMarketSegmentAndProducts.split(":");
				String sMarketSegID = arrOfSegmentAndProductStr[0].trim();

				if( ExchangeSegment.isValidSegmentID(sMarketSegID))
				{
					prodObj.put(SymbolConstants.MKT_SEG_ID, sMarketSegID);
					prodObj.put(SymbolConstants.EXCHANGE, ExchangeSegment.getExchangeName(sMarketSegID));

					JSONArray products = new JSONArray();
					String sArrProduct = arrOfSegmentAndProductStr[1];
					String[] arrProducts = sArrProduct.split("\\$");
					for (int i = 0; i < arrProducts.length; i++) 
					{
						String productType = arrProducts[i];
						if(ProductType.isValidProduct(productType))
							products.put(ProductType.formatToDisplay(productType, sMarketSegID));						
					}
					
					prodObj.put(UserInfoConstants.PRODUCT_LIST, products);
					productListObj.put(prodObj);
				}
			}
			return productListObj;
		}

	}

	public static JSONObject getBroadcastInfo(LoginResponseObject responseObject) throws JSONException {
		JSONObject broadcastObj = new JSONObject();

		broadcastObj.put(UserInfoConstants.IP, responseObject.getBroadCastIP());
		broadcastObj.put(UserInfoConstants.PORT, responseObject.getBroadCastPort());

		return broadcastObj;

	}

	public static void updateIs2FAAuthenticatedSuccess(String sUserID) {
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_2FA_AUTHENTICATED_SUCCESS);
			
			ps.setString(1, sUserID);

			 ps.executeUpdate();

		} catch(Exception e) {
			log.warn("Error in updating 2FA:" + e);
			
		} finally {
		 
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
	}
	
	public static JSONArray getallowedGTDExch(String sGTDAllowed) throws Exception {
		JSONArray allowedGTDListObj = new JSONArray();
		String isGTDAllowed = sGTDAllowed;

		if (isGTDAllowed.isEmpty())
			return allowedGTDListObj;
		else {
			String[] arrOfGTDAllowedExch = isGTDAllowed.split(";");
			for (String sProductsAndMarketSegment : arrOfGTDAllowedExch) {
				JSONObject exchObj = new JSONObject();
				String[] arrOfSegmentAndProductStr = sProductsAndMarketSegment.split(":");
				if (arrOfSegmentAndProductStr.length > 1) {
					String sProductType = arrOfSegmentAndProductStr[0].trim();
					String sArrExch = arrOfSegmentAndProductStr[1];
					String[] arrExch = sArrExch.split("\\$");
					JSONArray exchange = new JSONArray();
					if(ProductType.isValidProduct(sProductType)) {
						for(int i = 0; i < arrExch.length; i++) {
							String exchSegId = arrExch[i];
							if( ExchangeSegment.isValidSegmentID(exchSegId)) {
								exchange.put(exchSegId);
							exchObj.put(UserInfoConstants.PRODUCT_TYPE,ProductType.formatToDisplay(sProductType, exchSegId) );
							}
						}
						exchObj.put(UserInfoConstants.EXCH_LIST, exchange);
						allowedGTDListObj.put(exchObj);
					}
				}
			}
			return allowedGTDListObj;
		}

	}
	
	public static String maskMobileNumber(String sMobileNo) {
	    final String mask = "XXXXXXX";
	    sMobileNo = sMobileNo == null ? mask : sMobileNo;
	    final int lengthOfMobileNumber = sMobileNo.length();
	    if (lengthOfMobileNumber > 2) {
	        final int maskLen = Math.min(Math.max(lengthOfMobileNumber / 2, 2), 6);
	        final int start = (lengthOfMobileNumber - maskLen) / 2;
	        return sMobileNo.substring(0, start) + mask.substring(0, maskLen) + sMobileNo.substring(start + maskLen);
	    }
	    return sMobileNo;      
    }
	
	public static LoginResponse verify_login_102(String sUserID, String sPassword, String sNewPwd, String sClientIP, 
            String appIDForLogging)
            throws Exception {

        String newPwd = "", sNewEncryptedPwd = "";
        //String password = Validation.passwordValidation(sPassword);
        String sEncryptedPwd = GCUtils.encryptPassword(sPassword);

        

        LoginRequest loginReq = new LoginRequest();

        loginReq.setUserID(sUserID);
        loginReq.setPassword(sEncryptedPwd);
        loginReq.setForceLoginTag(true);
        loginReq.setIPAddr(sClientIP);
        
        /*** sNewPwd is not empty, change password. Otherwise normal login ***/
        if (!sNewPwd.isEmpty()) {
            //newPwd = Validation.passwordValidation(sNewPwd);
            sNewEncryptedPwd = GCUtils.encryptPassword(sNewPwd);
        }

        if (!sNewEncryptedPwd.isEmpty())
            loginReq.setNewPassword(sNewEncryptedPwd);
        else
            loginReq.setNewPassword("");

        LoginAPI loginAPI = new LoginAPI();
        LoginResponse loginResp = loginAPI.post(loginReq, LoginResponse.class, appIDForLogging,"NetNetLogin");

        LoginResponseObject loginObj = loginResp.getResponseObject();

        loginResp.setUserStatus(getLogonStatus(loginObj.getLogonStatus()));
        loginResp.setErrorMsg(loginObj.getErrStr());
        
        String sUserStatus = loginResp.getUserStatus();
        
        if (sUserStatus.equals(LoginResponse.LOGIN_SUCCESS)) {
                        
            loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, "OK");
            loginResp.setParticipantIDList(getParticipantIDList(loginObj));
            loginResp.setUserID(sUserID);
            loginResp.setProductList(getProductList(loginObj.getOeproduct()));
            loginResp.addToLoginObj(UserInfoConstants.USER_ID, sUserID);
            loginResp.addToLoginObj(UserInfoConstants.USER_NAME, loginObj.getUserName());
            loginResp.addToLoginObj(UserInfoConstants.LAST_LOGIN_TIME, loginObj.getLastLogonTime());
            loginResp.addToLoginObj(UserInfoConstants.BROADCAST_INFO, getBroadcastInfo(loginObj));
            loginResp.addToLoginObj(UserInfoConstants.IS_2FA_ENABLED,true);//get2FAFlag_101(sUserID, appIDForLogging, loginObj));
            loginResp.addToLoginObj(UserInfoConstants.REMARK, "");
            loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch(loginObj.getGtdAllowedExchanges()).isEmpty() ? false : true);
            //loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch("").isEmpty() ? false : true);
            loginResp.addToLoginObj(UserInfoConstants.IS_2FA_MANDATORY, true );
            //loginResp.addToLoginObj(UserInfoConstants.IS_OTP_2FA_REQUIRED,otpFlag.equals("Y")?true:false);
            loginResp.addToLoginObj(UserInfoConstants.MOBILE_NUMBER, maskMobileNumber(loginObj.getMobileNo()) );
            if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("4")) {
                loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "true");
                loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "false");
            }else if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("3")) {
                loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "false");
                loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "true");
            }
            loginResp.setProductList(getProductList(loginObj.getOeproduct()));
            loginResp.setGTDAllowed(getallowedGTDExch(loginObj.getGtdAllowedExchanges()));
            //loginResp.setGTDAllowed(getallowedGTDExch(""));
            
            //insert user data into DB
            
            
        }
        else if(sUserStatus.equals(LoginResponse.ACCOUNT_BLOCKED))
        {
            boolean otpStatus=AdvanceLogin.setOtpStatus("Y",sUserID, appIDForLogging);
            
            if(otpStatus==true)
                log.info("OTP updated successfully on Account Blocked");
            loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.ACCOUNT_BLOCKED);
            loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
        }
        else if(sUserStatus.equals(LoginResponse.PASSWORD_EXPIRED))
        {
            boolean otpStatus=AdvanceLogin.setOtpStatus("Y",sUserID, appIDForLogging);
            
            if(otpStatus==true)
                log.info("OTP updated successfully on Password Expired");

            loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.PASSWORD_EXPIRED);
            loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
        }
        else
            throw new GCException(InfoIDConstants.DYNAMIC_MSG, 
                    loginResp.getErrorMsg().isEmpty() ? 
                            InfoMessage.getInfoMSG("info_msg.login.failed") : loginResp.getErrorMsg());

        return loginResp;

    }
	
	public static LoginResponse verify_login_103(String sUserID, String sPassword, String sNewPwd, String sClientIP, 
            String appIDForLogging)
            throws Exception {

        String newPwd = "", sNewEncryptedPwd = "";
        //String password = Validation.passwordValidation(sPassword);
        String sEncryptedPwd = GCUtils.encryptPassword(sPassword);

        

        LoginRequest loginReq = new LoginRequest();

        loginReq.setUserID(sUserID);
        loginReq.setPassword(sEncryptedPwd);
        loginReq.setForceLoginTag(true);
        loginReq.setIPAddr(sClientIP);
        
        /*** sNewPwd is not empty, change password. Otherwise normal login ***/
        if (!sNewPwd.isEmpty()) {
            //newPwd = Validation.passwordValidation(sNewPwd);
            sNewEncryptedPwd = GCUtils.encryptPassword(sNewPwd);
        }

        if (!sNewEncryptedPwd.isEmpty())
            loginReq.setNewPassword(sNewEncryptedPwd);
        else
            loginReq.setNewPassword("");

        LoginAPI loginAPI = new LoginAPI();
        LoginResponse loginResp = loginAPI.post(loginReq, LoginResponse.class, appIDForLogging,"NetNetLogin");

        LoginResponseObject loginObj = loginResp.getResponseObject();

        loginResp.setUserStatus(getLogonStatus(loginObj.getLogonStatus()));
        loginResp.setErrorMsg(loginObj.getErrStr());
        
        String sUserStatus = loginResp.getUserStatus();
        
        if (sUserStatus.equals(LoginResponse.LOGIN_SUCCESS)) {
                        
            loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, "OK");
            loginResp.setParticipantIDList(getParticipantIDList(loginObj));
            loginResp.setUserID(sUserID);
            loginResp.setProductList(getProductList(loginObj.getOeproduct()));
            loginResp.addToLoginObj(UserInfoConstants.USER_ID, sUserID);
            loginResp.addToLoginObj(UserInfoConstants.USER_NAME, loginObj.getUserName());
            loginResp.addToLoginObj(UserInfoConstants.LAST_LOGIN_TIME, loginObj.getLastLogonTime());
            loginResp.addToLoginObj(UserInfoConstants.BROADCAST_INFO, getBroadcastInfo(loginObj));
            loginResp.addToLoginObj(UserInfoConstants.IS_2FA_ENABLED,true);//get2FAFlag_101(sUserID, appIDForLogging, loginObj));
            loginResp.addToLoginObj(UserInfoConstants.REMARK, "");
            loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch("").isEmpty() ? false : true);
            //loginResp.addToLoginObj(UserInfoConstants.IS_GTD_ENABLED, getallowedGTDExch(loginObj.getGtdAllowedExchanges()).isEmpty() ? false : true);
            loginResp.addToLoginObj(UserInfoConstants.IS_2FA_MANDATORY, true );
            //loginResp.addToLoginObj(UserInfoConstants.IS_OTP_2FA_REQUIRED,otpFlag.equals("Y")?true:false);
            loginResp.addToLoginObj(UserInfoConstants.MOBILE_NUMBER, maskMobileNumber(loginObj.getMobileNo()) );
            if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("4")) {
                loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "true");
                loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "false");
            }else if (loginObj.getSecondLevelVendorID().equalsIgnoreCase("3")) {
                loginResp.addToLoginObj(UserInfoConstants.SHOW_OTP_SCREEN, "false");
                loginResp.addToLoginObj(UserInfoConstants.SHOW_PAN_SCREEN, "true");
            }
            loginResp.setProductList(getProductList(loginObj.getOeproduct()));
            loginResp.setGTDAllowed(getallowedGTDExch(""));
            //loginResp.setGTDAllowed(getallowedGTDExch(loginObj.getGtdAllowedExchanges()));
            
            //insert user data into DB
            
            
        }
        else if(sUserStatus.equals(LoginResponse.ACCOUNT_BLOCKED))
        {
            boolean otpStatus=AdvanceLogin.setOtpStatus("Y",sUserID, appIDForLogging);
            
            if(otpStatus==true)
                log.info("OTP updated successfully on Account Blocked");
            loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.ACCOUNT_BLOCKED);
            loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
        }
        else if(sUserStatus.equals(LoginResponse.PASSWORD_EXPIRED))
        {
            boolean otpStatus=AdvanceLogin.setOtpStatus("Y",sUserID, appIDForLogging);
            
            if(otpStatus==true)
                log.info("OTP updated successfully on Password Expired");

            loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.PASSWORD_EXPIRED);
            loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
        } else if(sUserStatus.equals(LoginResponse.INVALID_PASSSWORD)) {
            boolean otpStatus=AdvanceLogin.setOtpStatus("Y",sUserID, appIDForLogging);
            
            if(otpStatus==true)
                log.info("OTP updated successfully on Password Expired");

            loginResp.addToLoginObj(UserInfoConstants.LOG_ON_STATUS, LoginResponse.INVALID_PASSSWORD);
            loginResp.addToLoginObj(UserInfoConstants.REMARK, loginResp.getRemark());
        }
        else
            throw new GCException(InfoIDConstants.DYNAMIC_MSG, 
                    loginResp.getErrorMsg().isEmpty() ? 
                            InfoMessage.getInfoMSG("info_msg.login.failed") : loginResp.getErrorMsg());

        return loginResp;

    }
	
	public static void updateIs2FAAuthenticatedFalse(String sUserID) {
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_2FA_AUTHENTICATED_FALSE);
			
			ps.setString(1, sUserID);

			 ps.executeUpdate();

		} catch(Exception e) {
			log.warn("Error in updating 2FA:" + e);
			
		} finally {
		 
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
	}
	

}
