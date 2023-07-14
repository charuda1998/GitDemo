package com.globecapital.services.user;

import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class UserPreference  extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		gcResponse.setData(AdvanceLogin.getUserPreference(session.getUserID(), session.getAppID()));
		
	}

}
