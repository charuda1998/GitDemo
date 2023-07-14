package com.globecapital.services.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidSession;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

abstract public class SessionService extends BaseService {

	private static final long serialVersionUID = 1L;

	protected static Logger log = Logger.getLogger(SessionService.class);

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
	    ServletContext servletContext = getServletContext();
		String sessionID = gcRequest.createSession(false);
		if (sessionID == null) {
			log.info("Middleware session expired");
		   resetOTPFlag(gcRequest.getAppID());
           throw new InvalidSession();
		}else if (isValidSession(sessionID, servletContext, gcRequest, gcResponse)) {
			doPostProcess(gcRequest, gcResponse);
		} else {
			throw new InvalidSession();
		}
		

	}

	private void resetOTPFlag(String appID) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		String resetOTPStaus=DBQueryConstants.RESET_OTP;
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(resetOTPStaus);
			ps.setString(1, appID);
			ps.executeUpdate();
			
		} finally {
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		
	}

	protected boolean isValidSession(String jSessionID, ServletContext servletContext, GCRequest gcomsRequest, GCResponse gcomsResponse) throws SQLException, GCException {

		Session session = SessionHelper.validateSessionAndAppID(jSessionID, gcomsRequest.getAppID(), servletContext, gcomsRequest, gcomsResponse);
		
		if (session != null) {
			gcomsRequest.setSession(session);
			return true;
		} else {
			return false;
		}
	}

	protected String validUserStage() {
		
		return Session.USER_STAGE_LOGGED_IN;
	}

	protected boolean isValidAppID(GCRequest gcomsRequest) {
		return true;
	}

	abstract protected void doPostProcess(GCRequest gcomsRequest, GCResponse gcomsResponse) throws Exception;

}
