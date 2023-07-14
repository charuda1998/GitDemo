package com.globecapital.services.user;

import com.globecapital.business.user.WebLinkURL;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class IPOLink extends SessionService{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		gcResponse.addToData(DeviceConstants.URL, WebLinkURL.getIPOLinkUrl(gcRequest.getSession().getUserID()));
		
	}

}
