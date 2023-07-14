package com.globecapital.business.wcf.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.WCFConstants;
import com.globecapital.db.GCDBPool;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class WCFDbHandler {

	
	private static Logger log = Logger.getLogger(WCFDbHandler.class);
	
	public static final String INSERT_SESSION_IN_DB = "INSERT INTO WCF_CLIENT_SESSION (`dealer_id`,`session_id`,`status`)"
			+ " values (?,?,'"+WCFConstants.DB_STATUS_ACTIVE+"')";
	
	public static final String UPDATE_SESSION_IN_DB = "UPDATE WCF_CLIENT_SESSION set `session_id`=?, `status`= '"+
			WCFConstants.DB_STATUS_ACTIVE+"' "+ "where `dealer_id`=?";
	
	public static final String GET_SESSION_FROM_DB = "select session_id from WCF_CLIENT_SESSION where "
			+ "dealer_id = ? and status = '"+WCFConstants.DB_STATUS_ACTIVE+"'";
	
	public static final String UPDATE_SESSION_STATUS = "UPDATE WCF_CLIENT_SESSION set status = '"+WCFConstants.DB_STATUS_EXPIRED+"'"
			+ " where dealer_id = ?";
	
	public static final String CHECK_DEALER_DB = "SELECT `dealer_id` FROM WCF_CLIENT_SESSION where `dealer_id` = ?";
		
	public static final String LMT_UPDATE_INITIATED = "UPDATE PAYMENT set STAGE=?,LIMIT_UPDATE_STATUS=? , LIMIT_UPDATE_TIME=now() where MERCHANT_REF_NO=?";
	
	public static final String LMT_UPDATE_FAILED = "UPDATE PAYMENT set REMARKS=? ,STATUS=?, LIMIT_UPDATE_STATUS=? , STAGE=? , LIMIT_UPDATE_TIME= now() where MERCHANT_REF_NO=?";

	public static final String LMT_UPDATE_RECEIVED = "UPDATE PAYMENT set STATUS=?, LIMIT_UPDATE_STATUS=? , STAGE=? , LIMIT_UPDATE_TIME= now() where MERCHANT_REF_NO=?";
	
	public static final String GATEWAY_RES_RECEIVED = "UPDATE PAYMENT set PG_STATUS=?,STAGE=?,PG_PAYMENT_ID=?,PGTRANS_UPDATE_TIME=now() where MERCHANT_REF_NO=?";
	
	public static final String GATEWAY_RES_FAIL_RECEIVED = "UPDATE PAYMENT set REMARKS=?,PG_STATUS=?,STATUS=?,STAGE=?,PGTRANS_UPDATE_TIME=now() where MERCHANT_REF_NO=?";
	
	public static final String FAILED_TRANS_UPDATE_POLL = "select PG_ORDER_ID, MERCHANT_REF_NO, CLIENT_ID, TRANS_ADDITIONAL_INFO, PAYMENT_CHANNEL, AMOUNT from PAYMENT where PG_STATUS =? AND CREATED_AT < CURRENT_TIMESTAMP - INTERVAL 5 MINUTE";
	
	public static boolean checkDealerInDB(String dealerId) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Dealer Query = " + CHECK_DEALER_DB);
			st = conn.prepareStatement(CHECK_DEALER_DB);
			st.setString(1, dealerId);
			ResultSet rs = st.executeQuery();
			if (rs.next())
				return true;

			return false;

		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}

	}
	
	
	public static void insertSessionInDB(String dealerId,String sessionId) throws SQLException
	{
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Insert Query = "+INSERT_SESSION_IN_DB);
			st = conn.prepareStatement(INSERT_SESSION_IN_DB);
			st.setString(1,dealerId);
			st.setString(2,sessionId);
			
			int rs = st.executeUpdate();

		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	
	public static void updateSessionInDB(String dealerId,String sessionId) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+UPDATE_SESSION_IN_DB);
			st = conn.prepareStatement(UPDATE_SESSION_IN_DB);
			st.setString(1,sessionId);
			st.setString(2,dealerId);
			
			int rs = st.executeUpdate();
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	
	public static String getSessionFromDb(String dealerId) throws SQLException
	{
		String sessionId = null;
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("GetSession Query = "+GET_SESSION_FROM_DB);
			st = conn.prepareStatement(GET_SESSION_FROM_DB);
			st.setString(1,dealerId);
			
			ResultSet rs = st.executeQuery();
			if (rs.next())
				sessionId = rs.getString("session_id");
			return sessionId;

		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	
	public static void setExpiredSessionStatus(String dealerId) throws SQLException {

		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+UPDATE_SESSION_STATUS);
			st = conn.prepareStatement(UPDATE_SESSION_STATUS);
			st.setString(1,dealerId);
			
			int rs = st.executeUpdate();

		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	
	}
	
	public static void limitUpdate(String stage,String status,String merchRefNo) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+LMT_UPDATE_INITIATED);
			st = conn.prepareStatement(LMT_UPDATE_INITIATED);
			st.setString(1,stage);
			st.setString(2,status);
			st.setString(3,merchRefNo);
			
			int rs = st.executeUpdate();
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	
	public static void limitUpdateFailed(String remark,String status,String stage,String merchRefNo) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+LMT_UPDATE_FAILED);
			st = conn.prepareStatement(LMT_UPDATE_FAILED);
			st.setString(1,remark);
			st.setString(2,status);
			st.setString(3,status);
			st.setString(4,stage);
			st.setString(5,merchRefNo);
			
			int rs = st.executeUpdate();
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	
	public static void limitUpdateReceived(String status,String stage,String merchRefNo) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+LMT_UPDATE_RECEIVED);
			st = conn.prepareStatement(LMT_UPDATE_RECEIVED);
			st.setString(1,status);
			st.setString(2,status);
			st.setString(3,stage);
			st.setString(4,merchRefNo);
			
			int rs = st.executeUpdate();
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	public static void gatewayResReceived(String status,String paymentId,String merchRefNo) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+GATEWAY_RES_RECEIVED);
			st = conn.prepareStatement(GATEWAY_RES_RECEIVED);
			st.setString(1,status);
			st.setString(2,RazorPayConstants.GATEWAY_RES_RECEIVED);
			st.setString(3,paymentId);
			st.setString(4,merchRefNo);
			
			int rs = st.executeUpdate();
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	public static void gatewayFailResReceived(String remark,String status,String merchRefNo) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+GATEWAY_RES_FAIL_RECEIVED);
			st = conn.prepareStatement(GATEWAY_RES_FAIL_RECEIVED);
			st.setString(1,remark);
			st.setString(2,status);
			st.setString(3,status);
			st.setString(4,RazorPayConstants.TRANS_COMPLETED);
			st.setString(5,merchRefNo);
			
			int rs = st.executeUpdate();
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
	}
	public static List<JSONObject> getFailedTransactionDetails() throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		List<JSONObject> merchRefNoList = new ArrayList<>();
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+FAILED_TRANS_UPDATE_POLL);
			st = conn.prepareStatement(FAILED_TRANS_UPDATE_POLL);
			st.setString(1,DeviceConstants.NOT_INITIATED);
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				JSONObject merchRefNo = new JSONObject();
				merchRefNo.put(DeviceConstants.MERCHANT_TRANS_NO, rs.getString(RazorPayConstants.MERCHANT_REF_NO));
				merchRefNo.put(RazorPayConstants.AMOUNT, String.valueOf(rs.getDouble(RazorPayConstants.AMOUNT_)));
				merchRefNo.put(RazorPayConstants.CLIENT_ID,rs.getString(RazorPayConstants.CLIENT_ID));
				merchRefNo.put(DeviceConstants.PG_ORDER_ID,rs.getString(RazorPayConstants.PG_ORDER_ID));
				merchRefNo.put(RazorPayConstants.BANK_INFO,rs.getString(RazorPayConstants.TRANS_ADDITIONAL_INFO));
				merchRefNo.put(DeviceConstants.METHOD,rs.getString(RazorPayConstants.PAYMENT_CHANNEL));
				merchRefNoList.add(merchRefNo);
			}
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		return merchRefNoList;
	}


}
