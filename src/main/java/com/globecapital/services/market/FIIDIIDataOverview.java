package com.globecapital.services.market;

import com.globecapital.business.market.Market;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class FIIDIIDataOverview extends BaseService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String sCategory = gcRequest.getFromData(DeviceConstants.CATEGORY);
		gcResponse.setData(Market.getFIIDIIData(DeviceConstants.DAILY, sCategory, true, gcRequest.getAppID()));
		
	}

}
