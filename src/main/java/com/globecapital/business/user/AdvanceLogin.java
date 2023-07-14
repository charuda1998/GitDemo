package com.globecapital.business.user;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.session.Session;
import com.msf.utils.helper.Helper;


public class AdvanceLogin {
	
	private static String sMpinKey = InfoMessage.getInfoMSG("info_msg.login.mpin_encryption_key");

	public static boolean registerMPIN(String sUserID, String sMPIN, String sAppID) throws SQLException, GeneralSecurityException {
		
		String sEncryptedToken = AESEncryption.encrypt(sUserID + sMpinKey, sMPIN);

		Connection conn = null;
		PreparedStatement ps = null;

		boolean isMPINInserted = false;

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.INSERT_ADVANCE_LOGIN_MPIN);

			ps.setString(1, sUserID);
			ps.setString(2, sEncryptedToken);
			ps.setString(3, AppConstants.Y);
			ps.setString(4, AppConstants.Y);
			ps.setInt(5, 0);
			ps.setString(6, sAppID);
		

			int _result = ps.executeUpdate();

			if (_result > 0)
				isMPINInserted = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isMPINInserted;
	}
	
	public static boolean registerFingerPrint(String sUserID, String sAppID) throws SQLException {

		Connection conn = null;
		PreparedStatement ps = null;

		boolean isFingerPrintInserted = false;

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.INSERT_ADVANCE_LOGIN_FINGER_PRINT);

			ps.setString(1, sUserID);
			ps.setString(2, AppConstants.Y);
			ps.setString(3, AppConstants.Y);
			ps.setString(4, sAppID);
		

			int _result = ps.executeUpdate();

			if (_result > 0)
				isFingerPrintInserted = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isFingerPrintInserted;
	}


	public static boolean validateMPIN(String sUserID, String sMPIN) throws SQLException, GeneralSecurityException {
		
		
		String sEncryptedMPIN = getMPIN(sUserID);
		String sDecryptedMPIN = AESEncryption.decrypt(sUserID + sMpinKey, sEncryptedMPIN);
		
		if(sMPIN.equals(sDecryptedMPIN))
		{
			return true;
		}
		return false;
	}


	private static String getMPIN(String sUserID) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String sMPIN = null;
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.GET_MPIN);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
				sMPIN = res.getString(1);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return sMPIN;
		
		
	}

	public static boolean isMPINEnabled(String sUserID) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String sMPIN = "";
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.CHECK_MPIN_ENABLED);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
			{
				sMPIN = res.getString(DBConstants.MPIN_ENABLED);
				sMPIN = res.wasNull() ? "" : sMPIN;
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		if(sMPIN.isEmpty())
			return false;
		else
			return true;
		
	}
	
public static boolean isMPINActive(String sUserID) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String sMPIN = "";
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.CHECK_MPIN_ACTIVE);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
			{
				sMPIN = res.getString(DBConstants.MPIN_ACTIVE);
				sMPIN = res.wasNull() ? "" : sMPIN;
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		if(sMPIN.isEmpty() || sMPIN.equals("N"))
			return false;
		else
			return true;
		
	}


	public static boolean isFingerprintEnabled(String sUserID) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String sFingerPrint = "";
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.CHECK_FINGERPRINT_ENABLED);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
			{
				sFingerPrint = res.getString(DBConstants.FINGERPRINT_ENABLED);
				sFingerPrint = res.wasNull() ? "" : sFingerPrint;
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		if(sFingerPrint.isEmpty())
			return false;
		else
			return true;
	}
	
	public static boolean isFingerprintActive(String sUserID) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String sFingerPrint = "";
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.CHECK_FINGERPRINT_ACTIVE);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
			{
				sFingerPrint = res.getString(DBConstants.FINGERPRINT_ACTIVE);
				sFingerPrint = res.wasNull() ? "" : sFingerPrint;
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		if(sFingerPrint.isEmpty() || sFingerPrint.equals("N"))
			return false;
		else
			return true;
	}



	public static boolean changeMPIN(String sUserID, String sMPIN, String sAppID) throws GeneralSecurityException, SQLException {
		boolean isUpdated = false;
		
		String sEncryptedToken = AESEncryption.encrypt(sUserID + sMpinKey, sMPIN);

		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_MPIN);

			ps.setString(1, sEncryptedToken);
			ps.setString(2, sAppID);
			ps.setString(3, sUserID);

			int _result = ps.executeUpdate();
			if (_result > 0)
				isUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isUpdated;

	}


	public static void updateAuthType(String sUserID, String sAuthType) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_AUTH_TYPE);

			ps.setString(1, sAuthType);
			ps.setString(2, sUserID);

			ps.executeUpdate();

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

	}

	public static boolean inactiveMPIN(String sUserID) throws SQLException {
		boolean isUpdated = false;
		
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_MPIN_INACTIVE);

			ps.setString(1, sUserID);

			int _result = ps.executeUpdate();
			if (_result > 0)
				isUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isUpdated;
	}

	public static boolean inactiveFingerprint(String sUserID) throws SQLException {
		boolean isUpdated = false;
		
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_FINGERPRINT_INACTIVE);

			ps.setString(1, sUserID);

			int _result = ps.executeUpdate();
			if (_result > 0)
				isUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isUpdated;
	}

	public static JSONObject getUserPreference(String userID, String sAppID) 
			throws JSONException, SQLException, AppConfigNoKeyFoundException {
		
		JSONObject userPreferenceObj = new JSONObject();
		
		JSONArray userPreferenceArr = new JSONArray();
		userPreferenceArr.put(DeviceConstants.MPIN);
		userPreferenceArr.put(DeviceConstants.FINGERPRINT);
		userPreferenceObj.put(DeviceConstants.GLOBE_SECURE, userPreferenceArr);
		
		if(AppConfig.getValue("advancelogin.check_different_device").equalsIgnoreCase("true"))
		{
			String sAdvanceLoginAppID = AdvanceLogin.getAppID(userID);
			
			if(!sAppID.equals(sAdvanceLoginAppID))
				AdvanceLogin.inactiveAdvanceLogin(userID);
		}
		
		JSONObject mpinObj = new JSONObject();
		mpinObj.put(DeviceConstants.ENABLED, Boolean.toString(isMPINEnabled(userID)));
		mpinObj.put(DeviceConstants.ACTIVE, Boolean.toString(isMPINActive(userID)));
		
		JSONObject fingerprintObj = new JSONObject();
		fingerprintObj.put(DeviceConstants.ENABLED, Boolean.toString(isFingerprintEnabled(userID)));
		fingerprintObj.put(DeviceConstants.ACTIVE, Boolean.toString(isFingerprintActive(userID)));
		
		userPreferenceObj.put(DeviceConstants.MPIN, mpinObj);
		userPreferenceObj.put(DeviceConstants.FINGERPRINT, fingerprintObj);
		
		return userPreferenceObj;
	}

	public static boolean activeMPIN(String sUserID) throws SQLException {
		boolean isUpdated = false;
		
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_MPIN_ACTIVE);

			ps.setString(1, sUserID);

			int _result = ps.executeUpdate();
			if (_result > 0)
				isUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isUpdated;
	}

	public static boolean activeFingerprint(String sUserID) throws SQLException {
		boolean isUpdated = false;
		
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_FINGERPRINT_ACTIVE);

			ps.setString(1, sUserID);

			int _result = ps.executeUpdate();
			if (_result > 0)
				isUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isUpdated;
	}

	public static void updateFailureCount(String sUserID, int count) throws SQLException {
		
		
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_MPIN_FAILURE_COUNT);
			
			ps.setInt(1, count);
			ps.setString(2, sUserID);

			ps.executeUpdate();

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		
	}

	public static int getMPINFailureCount(String sUserID) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		int iMPINFailureCount = 0;
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.CHECK_MPIN_FAILURE_COUNT);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
			{
				iMPINFailureCount = res.getInt(DBConstants.MPIN_FAILURE_COUNT);
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		return iMPINFailureCount;
	}

	public static boolean checkOneOfAdvLoginRegistered(boolean isMPINEnabled, boolean isFingerprintEnabled) {
		
		if (isMPINEnabled || isFingerprintEnabled)
			return true;
		else
			return false;
	}

	public static String getAppID(String sUserID) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String sAppID = "";
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.GET_APP_ID);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
			{
				sAppID = res.getString(DBConstants.APP_ID_CL);
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		return sAppID;
		
	}

	public static void inactiveAdvanceLogin(String sUserID) throws SQLException {
		
		if(isMPINActive(sUserID))
		{
			inactiveMPIN(sUserID);
		}
		
		if(isFingerprintActive(sUserID))
		{
			inactiveFingerprint(sUserID);
		}
		
		
	}
	
	public static void updateAppID(String sUserID, String sAppID) throws SQLException {
		
		
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_APP_ID);
			
			ps.setString(1, sAppID);
			ps.setString(2, sUserID);

			 ps.executeUpdate();

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		
	}
	
	public static boolean activeNotification(String sUserID) throws SQLException {
		boolean isUpdated = false;
		
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_NOTIFICATION_ACTIVE);

			ps.setString(1, sUserID);

			int _result = ps.executeUpdate();
			if (_result > 0)
				isUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isUpdated;
	}
	
	public static boolean inactiveNotification(String sUserID) throws SQLException {
		boolean isUpdated = false;
		
		Connection conn = null;
		PreparedStatement ps = null;


		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.UPDATE_NOTIFICATION_INACTIVE);

			ps.setString(1, sUserID);

			int _result = ps.executeUpdate();
			if (_result > 0)
				isUpdated = true;

		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		return isUpdated;
	}
	
	public static boolean isNotificationActive(String sUserID) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String sNotification = "";
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.CHECK_NOTIFICATION_ACTIVE);
			ps.setString(1, sUserID);

			res = ps.executeQuery();

			if (res.next()) 
			{
				sNotification = res.getString(DBConstants.NOTIFICATION_ACTIVE);
				sNotification = res.wasNull() ? "" : sNotification;
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
		if(sNotification.isEmpty() || sNotification.equals("N"))
			return false;
		else
			return true;
	}
	

   public static String getEncryptedPwd(String eUserName, String appId) throws SQLException {
       String password = "";
       
       Connection conn = null;
       PreparedStatement ps = null;
       ResultSet res = null;

       try {
           conn = GCDBPool.getInstance().getConnection();
           ps = conn.prepareStatement(DBQueryConstants.GET_USER_PASSWORD);

           ps.setString(1, eUserName);
           ps.setString(2, appId);

           res = ps.executeQuery();

           if (res.next()) 
           {
               password = res.getString(DBConstants.PASSWORD_S);
           }

       } finally {
           Helper.closeResultSet(res);
           Helper.closeStatement(ps);
           Helper.closeConnection(conn);
       }

       return password;
    }
   
   public static Session getEncryptedUsrId(String appId) throws SQLException, AppConfigNoKeyFoundException, GeneralSecurityException {
       String password = "";
       
       Connection conn = null;
       PreparedStatement ps = null;
       ResultSet res = null;
       Session session = null;

       try {
           conn = GCDBPool.getInstance().getConnection();
           ps = conn.prepareStatement(DBQueryConstants.GET_USER_DETAILS_ADVANCE);

           ps.setString(1, appId);

           res = ps.executeQuery();

           if (res.next()) 
           {
               session = new Session();
               session.setAppID(appId);
               session.setUserId(AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"),res.getString(DBConstants.USER_ID)));
           }

       } finally {
           Helper.closeResultSet(res);
           Helper.closeStatement(ps);
           Helper.closeConnection(conn);
       }

       return session;
    }

	public static boolean setOtpStatus(String otpStaus,String sUserID,String appID) throws SQLException, AppConfigNoKeyFoundException, GeneralSecurityException {
		Connection conn = null;
		PreparedStatement ps = null;
		String getOTPStaus=DBQueryConstants.SET_OTP_STATUS;
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(getOTPStaus);
			
			ps.setString(1,otpStaus);
			ps.setString(2,appID);
			ps.setString(3, AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"),sUserID));
			int result=ps.executeUpdate();
			if(result>0) {
				return true;
			}
			
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return false;
		
	}
	
	public static boolean updateOtpStatus(String sUserID,String appID) throws SQLException, AppConfigNoKeyFoundException, GeneralSecurityException {
		Connection conn = null;
		PreparedStatement ps = null;
		String getOTPStaus=DBQueryConstants.UPDATE_OTP_STATUS;
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(getOTPStaus);
			
			ps.setString(1, appID);
			ps.setString(2, AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"),sUserID));
			
			int result=ps.executeUpdate();
			if(result>0) {
				return true;
			}
			
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return false;
	}

}
