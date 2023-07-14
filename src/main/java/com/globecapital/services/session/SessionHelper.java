package com.globecapital.services.session;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.api.ft.user.LogOffAPI;
import com.globecapital.api.ft.user.LogOffRequest;
import com.globecapital.api.ft.user.LogOffResponse;
import com.globecapital.api.ft.user.LoginResponse;
import com.globecapital.api.ft.user.LoginResponseObject;
import com.globecapital.business.edis.EDISHelper;
import com.globecapital.business.edis.GetEDISConfigDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidSession;
import com.globecapital.utils.AESUtil;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;
import com.msf.sbu2.service.auth.AuthManager;
import com.msf.sbu2.service.auth.AuthPayload;
import com.msf.sbu2.service.common.SBU2RequestFields;
import com.msf.sbu2.service.exception.SBU2Exception;
import com.msf.utils.helper.Helper;

public class SessionHelper {

	private static Logger log = Logger.getLogger(SessionHelper.class);

	public static boolean logoutUser(String sessionID) throws SQLException, GCException {

		String logoutUserQuery = DBQueryConstants.LOGOUT_USER_QUERY_WITH_SESSIONID;
		Connection conn = null;
		PreparedStatement ps = null;
		
		log.debug(logoutUserQuery);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(logoutUserQuery);

			ps.setString(1, sessionID);

			int _result = ps.executeUpdate();

			if (_result > 0) {
				return true;
			} else {
				throw new GCException(InfoIDConstants.INVALID_SESSION, "Invalid session from db");
			}
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}

	public static void logoutUserUsingUserID(String userID) throws Exception {

		String logoutUserQuery = DBQueryConstants.LOGOUT_USER_QUERY_WITH_USERID;
		Connection conn = null;
		PreparedStatement ps = null;
		
		log.debug(logoutUserQuery);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(logoutUserQuery);

			ps.setString(1, userID);

			int _result = ps.executeUpdate();

			if (_result > 0)
				log.debug("USER Existing session details removed.");

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}

	public static boolean insertSessionToDB(Session session) throws SQLException {

		String insertUserSession = DBQueryConstants.INSERT_USER_SESSION;

		Connection conn = null;
		PreparedStatement ps = null;

		boolean isSessionUpdated = false;

		log.debug(insertUserSession);
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(insertUserSession);

			ps.setString(1, session.getUserID());
			ps.setString(2, session.getSessionID());
			ps.setString(3, session.getAppID());
			ps.setString(4, session.getAppID());
			ps.setString(5, session.getGroupId());
			if (session.getUserInfo() == null) {
				ps.setString(6, null);
			} else {
				ps.setString(6, session.getUserInfo().toString());
			}
			ps.setString(7, session.getjSessionIDWithoutEncryption());
			ps.setString(8, session.getjSessionID());
			ps.setString(9, session.getjKey());
			ps.setInt(10, session.getClientOrderNo());
			ps.setString(11, session.getIs2FAAuthenticated());

			int _result = ps.executeUpdate();

			if (_result > 0)
				isSessionUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isSessionUpdated;
	}
	
	public static boolean updateUserInfo(Session session) throws SQLException {

		String insertUserSession = DBQueryConstants.UPDATE_USER_INFO;

		Connection conn = null;
		PreparedStatement ps = null;

		boolean isSessionUpdated = false;

		log.debug(insertUserSession);
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(insertUserSession);

			if (session.getUserInfo() == null) {
				ps.setString(1, null);
			} else {
				ps.setString(1, session.getUserInfo().toString());
			}

			ps.setString(2, session.getUserID());
			int _result = ps.executeUpdate();

			if (_result > 0)
				isSessionUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isSessionUpdated;
	}

	public static Session getSession(String sessionID, ServletContext servletContext, GCRequest gcomsRequest, GCResponse gcomsResponse) throws SQLException {

		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet res = null;
		String sessionValidQuery = DBQueryConstants.SESSION_VALIDATION_PROCEDURE;

		Session session = null;
		log.info(sessionID);
		Session reSession = getUserDetailsUsingSessionID(sessionID);
		try {
			conn = GCDBPool.getInstance().getConnection();
			cstmt = conn.prepareCall(sessionValidQuery);
			cstmt.setString(1, sessionID);
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			cstmt.executeUpdate();
			if (cstmt.getInt(2) == 0) {

				res = cstmt.getResultSet();

				while (res.next()) {

					session = new Session();
					session.setSessionID(sessionID);
					session.setUserId(res.getString(DBConstants.USER_ID).toUpperCase().trim());
					session.setBuild(res.getString(DBConstants.BUILD));
					session.setAppID(res.getString(DBConstants.APP_ID));
					session.setjKey(res.getString(DBConstants.J_KEY));
					session.setjSessionIDWithoutEncryption(res.getString(DBConstants.FT_SESSION));
					session.setjSessionID(res.getString(DBConstants.FT_SESSION_ID));
					session.setGroupId(res.getString(DBConstants.USER_TYPE));
					session.setClientOrderNo(res.getInt(DBConstants.CLIENT_ORDER_NO));
					session.setIs2FAAuthenticated(res.getString(DBConstants.IS_2FA_AUTHENTICATED));

					if (res.getString(DBConstants.USER_INFO) != null) {
						JSONObject obj = new JSONObject(res.getString(DBConstants.USER_INFO));
						session.setUserInfo(obj);
					} else {
						session.setUserInfo(null);
					}

				}
			} else if(cstmt.getInt(2) == -2){
			    log.debug("Reinitiated login in session base class");
			    if( (reSession != null) && GCUtils.reInitiateLogIn(new FTRequest(),reSession, servletContext, gcomsRequest, gcomsResponse))
			        return gcomsRequest.getSession();
			    else {
			        log.debug("errror in reInitiate login");
			        return null;
			    }
			    
			} else {
			    log.debug("Reinitiated login in -1 case");
			    if(isUserOtpReqdFlag(gcomsRequest.getAppID(),reSession)) {
			        log.debug("Appid didn,t had a succesive login");
	                return null;
	            } else {
    			    try {
                        if(GCUtils.reInitiateLogIn(new FTRequest(),reSession, servletContext, gcomsRequest, gcomsResponse))
                            return gcomsRequest.getSession();
                        else {
                            return null;
                        }
                    } catch (Exception e) {
                        log.debug("errror "+e);
                    }
    				return null;
	            }
			}

		} catch (Exception e) {
			log.error(e);
		} finally {

			Helper.closeResultSet(res);
			Helper.closeStatement(cstmt);
			Helper.closeConnection(conn);
		}

		return session;
	}

	public static Session validateSessionAndAppID(String sessionID, String appID, ServletContext servletContext, GCRequest gcRequest, GCResponse gcResponse) throws SQLException, GCException 
	{
		Session session = getSession(sessionID, servletContext, gcRequest, gcResponse);
		if (session != null &&
					 (false == (session.getAppID()).equals(appID))) 
		{
                    logoutUser(sessionID);
                    throw new InvalidSession();
		}
		return session;
	}

	public static int updateClientOrderNo(String sUserID) throws SQLException {
		String updateUser = DBQueryConstants.CLIENT_ORDER_NO_RETRIEVAL;
		log.debug(updateUser);
		
		int iClientOrderNo = 0;

		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet res = null;
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			cstmt = conn.prepareCall(updateUser);
			cstmt.setString(1, sUserID);
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);

			cstmt.executeUpdate();
			iClientOrderNo = cstmt.getInt(2);

		} catch (Exception e) {
			log.error(e);
		} finally {

			Helper.closeResultSet(res);
			Helper.closeStatement(cstmt);
			Helper.closeConnection(conn);
		}
		return iClientOrderNo;

	}

	public static void addSession(GCRequest gcRequest, GCResponse gcResponse, LoginResponse loginResp,
			ServletContext servletContext) throws JSONException, Exception {
		Session session = new Session();
		
		session.setSessionID(gcRequest.createSession(true));
		session.setUserId(loginResp.getUserID().toUpperCase().trim());
		session.setAppID(gcRequest.getAppID());

		String contextPath = servletContext.getContextPath();
		
		String fileSeparator = System.getProperty("file.separator");
		String classFolder = servletContext.getRealPath("/") + "WEB-INF" + fileSeparator + "classes" + fileSeparator;
		try {
			AuthManager.setPrivateKey(classFolder + "private_key.key");
			AuthManager.setPublicKey(classFolder + "public_key.pem");
		} catch (CertificateException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.error(e1);
		}
		
		LoginResponseObject loginResObj = loginResp.getResponseObject();
		AuthPayload authPayload = new AuthPayload();
		authPayload.setAppID(gcRequest.getAppID());
		authPayload.setUser(loginResp.getUserID().toUpperCase().trim());
		authPayload.setUserName(loginResObj.getUserName());
		authPayload.setLastLoginTime(loginResObj.getLastLogonTime());
		authPayload.setUserType("globecapital");
		authPayload.setBuild("hybrid-phone");
		
		gcResponse.setSession(session.getSessionID(), contextPath, authPayload);

		session.setjSessionID(GCUtils.encryptPassword(loginResObj.getSessionId()));
		session.setjSessionIDWithoutEncryption(loginResObj.getSessionId());
		session.setGroupId(loginResObj.getGroupId());
		session.setjKey(loginResObj.getTransnID());
		session.setClientOrderNo(Integer.parseInt(loginResObj.getClientOrdNo()) + 1);
		
		boolean is2FAEnabled = loginResp.getLoginObj().getBoolean(UserInfoConstants.IS_2FA_ENABLED);
		
		if(is2FAEnabled)
			session.setIs2FAAuthenticated("N"); //Default value would be N
		else
			session.setIs2FAAuthenticated("NA"); 

		JSONObject user_info = new JSONObject();
		user_info.put(UserInfoConstants.PARTICIPANT_ID, loginResp.getParticipantIDList());
		user_info.put(UserInfoConstants.PRODUCT_TYPE, loginResp.getProductList());
		user_info.put(UserInfoConstants.BROADCAST_INFO, loginResp.getLoginObj().getJSONObject(UserInfoConstants.BROADCAST_INFO));
		user_info.put(UserInfoConstants.POA_STATUS, loginResObj.getPOAStatus());
		user_info.put(UserInfoConstants.MANAGER_IP, loginResObj.getManagerIP());
		user_info.put(UserInfoConstants.USER_CODE, loginResObj.getUserCode());
		user_info.put(UserInfoConstants.USER_NAME, loginResObj.getUserName());
		user_info.put(UserInfoConstants.ALLOWED_GTD_EXCH,loginResp.getGTDAllowed());
		
		if(!Boolean.parseBoolean(loginResObj.getPOAStatus())) {
			try {
				user_info.put(UserInfoConstants.EDIS_CONFIG_DETAILS, AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key") ,
						new JSONObject(EDISHelper.getEDISConfigDetails(session,servletContext,gcRequest,gcResponse).getResponseObject()).toString()));
			} catch (GeneralSecurityException | GCException e) {
				log.error(e);
			}
		}

		session.setUserInfo(user_info);
		insertSessionToDB(session);

		loginResp.setSession(session);

	}
	
	public static boolean validateDuplicateOrder(String jSessionID, String request) throws Exception {

		boolean isValidOrder = false;

		String newOrderMD5 = Helper.MD5sum(request);

		Connection conn = null;
		CallableStatement cs = null;

		String query = DBQueryConstants.DUPLICATE_ORDER_VALIDATOR;

		try {

			conn = GCDBPool.getInstance().getConnection();
			cs = conn.prepareCall(query);

			cs.setString(1, jSessionID);
			cs.setString(2, newOrderMD5);
			cs.registerOutParameter(3, java.sql.Types.INTEGER);

			cs.executeUpdate();

			int _result = cs.getInt(3);

			if (_result == 1)
				isValidOrder = true;

		} finally {
			Helper.closeStatement(cs);
			Helper.closeConnection(conn);
		}

		return isValidOrder;
	}
	
	public static int getSessionExpiry() throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		int iSessionExpiry = 604800; // 7 days in seconds
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.GET_SESSION_EXPIRY);

			res = ps.executeQuery();

			if (res.next()) 
				iSessionExpiry = res.getInt(1);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return iSessionExpiry;
	}
	
    public static Session getUserDetailsUsingSessionID(String sessionId) throws SQLException {
	    Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        Session session = new Session();
        
        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(DBQueryConstants.GET_USER_DETAILS);
            ps.setString(1, sessionId);
            res = ps.executeQuery();

            if (res.next())  { 
                session.setUserId(res.getString(DBConstants.USER_ID).toUpperCase().trim());
                session.setAppID(res.getString(DBConstants.APP_ID));
            }
        } finally {
            Helper.closeResultSet(res);
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }
        return session;
    }
    
    public static boolean isUserOtpReqdFlag(String appId,Session session) throws SQLException, AppConfigNoKeyFoundException, GeneralSecurityException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        
        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(DBQueryConstants.GET_OTP_FLAG);
            ps.setString(1, "N");
            ps.setString(2, appId);
            res = ps.executeQuery();

            if (res.next())  {
                session.setUserId(AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"),res.getString(DBConstants.USER_ID)));
                session.setAppID(appId);
                return false;
            }
        } finally {
            Helper.closeResultSet(res);
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }
        return true;
    }
    
    public static void updateSession(GCRequest gcRequest, GCResponse gcResponse, LoginResponse loginResp,
            ServletContext servletContext) throws JSONException, GCException, SQLException {
        Session session = gcRequest.getSession();
        if(getUserDetailsUsingSessionID(gcRequest.createSession(false)).getAppID().isEmpty())
            throw new GCException (InfoIDConstants.SUCCESS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
        
        String fileSeparator = System.getProperty("file.separator");
        String classFolder = servletContext.getRealPath("/") + "WEB-INF" + fileSeparator + "classes" + fileSeparator;
        try {
            AuthManager.setPrivateKey(classFolder + "private_key.key");
            AuthManager.setPublicKey(classFolder + "public_key.pem");
        } catch (CertificateException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            log.error(e1);
        }
        
        LoginResponseObject loginResObj = loginResp.getResponseObject();
        session.setjSessionID(GCUtils.encryptPassword(loginResObj.getSessionId()));
        session.setjSessionIDWithoutEncryption(loginResObj.getSessionId());
        session.setjKey(loginResObj.getTransnID());
        session.setClientOrderNo(Integer.parseInt(loginResObj.getClientOrdNo()) + 1);
        

        updateSessionToDB(session);

        loginResp.setSession(session);

    }
    
    public static void updateSessionOnBiometric(GCRequest gcRequest, GCResponse gcResponse, LoginResponse loginResp,
            ServletContext servletContext) throws JSONException, GCException, SQLException {
        Session session = new Session();
        if(getUserDetailsUsingSessionID(gcRequest.createSession(false)).getAppID().isEmpty())
            throw new GCException (InfoIDConstants.SUCCESS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
        session.setSessionID(gcRequest.createSession(false));
        session.setUserId(loginResp.getUserID().toUpperCase().trim());
        session.setAppID(gcRequest.getAppID());

        String contextPath = servletContext.getContextPath();
        
        String fileSeparator = System.getProperty("file.separator");
        String classFolder = servletContext.getRealPath("/") + "WEB-INF" + fileSeparator + "classes" + fileSeparator;
        try {
            AuthManager.setPrivateKey(classFolder + "private_key.key");
            AuthManager.setPublicKey(classFolder + "public_key.pem");
        } catch (CertificateException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            log.error(e1);
        }
        
        LoginResponseObject loginResObj = loginResp.getResponseObject();
        AuthPayload authPayload = new AuthPayload();
        authPayload.setAppID(gcRequest.getAppID());
        authPayload.setUser(loginResp.getUserID().toUpperCase().trim());
        authPayload.setUserName(loginResObj.getUserName());
        authPayload.setLastLoginTime(loginResObj.getLastLogonTime());
        authPayload.setUserType("globecapital");
        authPayload.setBuild("hybrid-phone");
        
        gcResponse.setSession(session.getSessionID(), contextPath, authPayload);

        session.setjSessionID(GCUtils.encryptPassword(loginResObj.getSessionId()));
        session.setjSessionIDWithoutEncryption(loginResObj.getSessionId());
        session.setGroupId(loginResObj.getGroupId());
        session.setjKey(loginResObj.getTransnID());
        session.setClientOrderNo(Integer.parseInt(loginResObj.getClientOrdNo()) + 1);
        
        boolean is2FAEnabled = loginResp.getLoginObj().getBoolean(UserInfoConstants.IS_2FA_ENABLED);
        
        if(is2FAEnabled)
            session.setIs2FAAuthenticated("N"); //Default value would be N
        else
            session.setIs2FAAuthenticated("NA"); 

        JSONObject user_info = new JSONObject();
        user_info.put(UserInfoConstants.PARTICIPANT_ID, loginResp.getParticipantIDList());
        user_info.put(UserInfoConstants.PRODUCT_TYPE, loginResp.getProductList());
        user_info.put(UserInfoConstants.BROADCAST_INFO, loginResp.getLoginObj().getJSONObject(UserInfoConstants.BROADCAST_INFO));
        user_info.put(UserInfoConstants.POA_STATUS, loginResObj.getPOAStatus());
        user_info.put(UserInfoConstants.MANAGER_IP, loginResObj.getManagerIP());
        user_info.put(UserInfoConstants.USER_CODE, loginResObj.getUserCode());
        user_info.put(UserInfoConstants.USER_NAME, loginResObj.getUserName());
        user_info.put(UserInfoConstants.ALLOWED_GTD_EXCH,loginResp.getGTDAllowed());
        
        if(!Boolean.parseBoolean(loginResObj.getPOAStatus())) {
            try {
                user_info.put(UserInfoConstants.EDIS_CONFIG_DETAILS, AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key") ,
                        new JSONObject(EDISHelper.getEDISConfigDetails(session,servletContext,gcRequest,gcResponse).getResponseObject()).toString()));
            } catch (Exception e) {
                log.error(e);
            }
        }

        session.setUserInfo(user_info);
        insertReSessionToDB(session);

        loginResp.setSession(session);

    }
    
    public static boolean insertReSessionToDB(Session session) throws SQLException {

        String insertUserSession = DBQueryConstants.INSERT_USER_RESESSION;

        Connection conn = null;
        PreparedStatement ps = null;

        boolean isSessionUpdated = false;

        log.debug(insertUserSession);
        
        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(insertUserSession);

            ps.setString(1, session.getUserID());
            ps.setString(2, session.getSessionID());
            ps.setString(3, session.getAppID());
            ps.setString(4, session.getAppID());
            ps.setString(5, session.getGroupId());
            if (session.getUserInfo() == null) {
                ps.setString(6, null);
            } else {
                ps.setString(6, session.getUserInfo().toString());
            }
            ps.setString(7, session.getjSessionIDWithoutEncryption());
            ps.setString(8, session.getjSessionID());
            ps.setString(9, session.getjKey());
            ps.setInt(10, session.getClientOrderNo());

            int _result = ps.executeUpdate();

            if (_result > 0)
                isSessionUpdated = true;

        } finally {
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }

        return isSessionUpdated;
    }
    
    public static boolean updateSessionToDB(Session session) throws SQLException {

        String insertUserSession = DBQueryConstants.UPDATE_USER_SESSION;

        Connection conn = null;
        PreparedStatement ps = null;

        boolean isSessionUpdated = false;

        log.debug(insertUserSession);
        
        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(insertUserSession);

            ps.setString(1, session.getjSessionIDWithoutEncryption());
            ps.setString(2, session.getjSessionID());
            ps.setString(3, session.getjKey());
            ps.setInt(4, session.getClientOrderNo());
            ps.setString(5, session.getUserID());
            ps.setString(6, session.getSessionID());

            int _result = ps.executeUpdate();

            if (_result > 0)
                isSessionUpdated = true;

        } finally {
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }

        return isSessionUpdated;
    }

}