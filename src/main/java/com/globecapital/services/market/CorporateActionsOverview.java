package com.globecapital.services.market;

import com.globecapital.business.market.Market;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class CorporateActionsOverview extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		
		try
		{
			gcResponse.setData(Market.getCorporateActionsOverview(session.getAppID()));
		}
		catch(Exception e)
		{
			gcResponse.setNoDataAvailable();
		}
	}

}
