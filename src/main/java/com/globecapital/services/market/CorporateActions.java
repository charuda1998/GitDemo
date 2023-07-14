package com.globecapital.services.market;

import com.globecapital.business.market.Market;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class CorporateActions extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sPeriod = gcRequest.getFromData(DeviceConstants.DATE_S);
		String sCategory = gcRequest.getFromData(DeviceConstants.CATEGORY);
		try
		{
			gcResponse.setData(Market.
					getCorporateActionsNew(sPeriod, sCategory, session.getAppID()));
		}
		catch(Exception e)
		{
			gcResponse.setNoDataAvailable();
		}
	}


}
