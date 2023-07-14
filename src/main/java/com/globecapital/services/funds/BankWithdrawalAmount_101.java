package com.globecapital.services.funds;

import org.json.JSONObject;
import com.globecapital.business.funds.GetBankWithdrawalAmount_101;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class BankWithdrawalAmount_101 extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();

		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		
		JSONObject withdrableAmtObj = GetBankWithdrawalAmount_101.withdrawalAmount(session, segmentType);

		if (withdrableAmtObj.length() > 0) {
			gcResponse.addToData(DeviceConstants.WITHDRAWAL_DETAILS,
					withdrableAmtObj);

		} else
			gcResponse.setNoDataAvailable();
	}
}