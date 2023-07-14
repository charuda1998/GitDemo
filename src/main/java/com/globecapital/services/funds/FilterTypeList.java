package com.globecapital.services.funds;

import com.globecapital.business.funds.FilterList;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class FilterTypeList extends BaseService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		gcResponse.addToData(DeviceConstants.TRANSACTION_FILTER, FilterList.getTransactionFilterList());

	}

}
