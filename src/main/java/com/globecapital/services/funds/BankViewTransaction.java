package com.globecapital.services.funds;

import org.json.JSONArray;

import com.globecapital.business.funds.GetPayoutTransactions;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class BankViewTransaction extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		String fromDate= gcRequest.getFromData(DeviceConstants.FROM_DATE);
		String toDate= gcRequest.getFromData(DeviceConstants.TO_DATE);
		
		JSONArray getTransactionDetails = GetPayoutTransactions.getTransactions(session,fromDate,toDate);

		if (getTransactionDetails.length() > 0) {
			gcResponse.addToData(DeviceConstants.BANK_LIST, getTransactionDetails);

		} else
			gcResponse.setNoDataAvailable();

	}
}