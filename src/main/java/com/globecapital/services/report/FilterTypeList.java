package com.globecapital.services.report;

import com.globecapital.business.report.AdvancedFilterList;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class FilterTypeList extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		gcResponse.addToData(DeviceConstants.REPORTS_FILTER,
				AdvancedFilterList.getFilterList(session));

	}
}