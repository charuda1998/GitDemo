package com.globecapital.services.report;

import com.globecapital.business.report.AdvancedFilterList_102;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class FilterTypeList_102 extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		gcResponse.addToData(DeviceConstants.REPORTS_FILTER,
				AdvancedFilterList_102.getFilterList());

	}
}