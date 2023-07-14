package com.globecapital.services.market;

import com.globecapital.business.market.Market;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class CorporateActionsOverview_101 extends BaseService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String sPeriod = gcRequest.getFromData(DeviceConstants.DATE_S);
		String sCategory = gcRequest.getFromData(DeviceConstants.CATEGORY);
		try
		{
			gcResponse.setData(Market.getCorporateActions(sPeriod, sCategory, true, true, gcRequest.getAppID()));
		}
		catch(Exception e)
		{
			gcResponse.setNoDataAvailable();
		}
	}

}
