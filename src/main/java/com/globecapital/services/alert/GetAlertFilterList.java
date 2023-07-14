package com.globecapital.services.alert;

import com.globecapital.business.alert.AlertFilterList;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class GetAlertFilterList extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		gcResponse.addToData(DeviceConstants.ALERT_FILTER_LIST,
				AlertFilterList.getFilterList());

	}
}