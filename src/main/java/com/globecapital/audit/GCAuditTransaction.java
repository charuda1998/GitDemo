package com.globecapital.audit;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class GCAuditTransaction {

	private static Logger log = Logger.getLogger(GCAuditTransaction.class);
	
	public void addTransaction(GCAuditObject auditObj) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		String query = DBQueryConstants.AUDIT;
		log.info(query);
		try { 
			conn = GCDBPool.getInstance().getConnection();
			pstmt = conn.prepareStatement(query);
			
			pstmt.setString(1, auditObj.getMsgID());
			pstmt.setString(2, auditObj.getSvcGroup());
			pstmt.setString(3, auditObj.getSvcName());
			pstmt.setString(4, auditObj.getSvcVersion());
			pstmt.setString(5, auditObj.getInfoID());
			pstmt.setString(6, auditObj.getInfoMsg());
			pstmt.setString(7, auditObj.getAppID());
			pstmt.setString(8, auditObj.getUsername());
			pstmt.setString(9, auditObj.getUsertype());
			pstmt.setString(10, auditObj.getAPITime());
			pstmt.setString(11, auditObj.getReqTime());
			pstmt.setString(12, auditObj.getRespTime());
			pstmt.setString(13, auditObj.getSrcIP());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			throw e;
		} finally {
			Helper.closeStatement(pstmt);
		}
	}
	
	public void logTransaction(GCAuditObject auditObj) {
		
		String query = DBQueryConstants.LOG_AUDIT;
		
		query += "(";
		query += "'" + auditObj.getMsgID() + "',";
		query += "'" + auditObj.getSvcGroup() + "',";
		query += "'" + auditObj.getSvcName() + "',";
		query += "'" + auditObj.getSvcVersion() + "',";
		query += "'" + auditObj.getInfoID() + "',";
		query += "'" + auditObj.getInfoMsg() + "',";
		query += "'" + auditObj.getAppID() + "',";
		query += "'" + auditObj.getUsername() + "',";
		query += "'" + auditObj.getUsertype() + "',";
		query += "'" + auditObj.getAPITime() + "',";
		query += "'" + auditObj.getReqTime() + "',";
		query += "'" + auditObj.getRespTime() + "',";
		query += "'" + auditObj.getSrcIP() + "'";
		query += ")";
		
		log.info("GC-AUDIT-QUERY:"+query+"");
		
	}
	
}
