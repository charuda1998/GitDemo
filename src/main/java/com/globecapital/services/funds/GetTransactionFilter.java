package com.globecapital.services.funds;

import com.globecapital.business.funds.FundTransfer;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class GetTransactionFilter extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		gcResponse.addToData(DeviceConstants.VIEW_TRANS_FILTER, FundTransfer.getViewTransactionFilter());
		
	}

}
