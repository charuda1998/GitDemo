package com.globecapital.services.funds;

import org.json.JSONArray;

import com.globecapital.business.funds.GetBankList;
import com.globecapital.business.funds.GetBankList_101;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.msf.log.Logger;

public class BankList_101 extends SessionService {
	private static final long serialVersionUID = 1L;
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		JSONArray getBankDetails = GetBankList_101.bankDetails(session);

		if (getBankDetails.length() > 0) {
			gcResponse.addToData(DeviceConstants.BANK_LIST, getBankDetails);
		} else
			gcResponse.setNoDataAvailable();

	}
}