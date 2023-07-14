package com.globecapital.services.quote;

import com.globecapital.business.quote.FilterList;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class GetFilterList extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		gcResponse.addToData(DeviceConstants.FILTER_LIST, 
				FilterList.getFilterList(gcRequest.getFromData(DeviceConstants.TYPE)));
		
	}

}
