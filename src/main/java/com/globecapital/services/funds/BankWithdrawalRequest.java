package com.globecapital.services.funds;

import org.json.JSONObject;

import com.globecapital.business.funds.GetBankWithdrawalRequest;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class BankWithdrawalRequest extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();

		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String amount = gcRequest.getFromData(DeviceConstants.AMT);
		String refNo= gcRequest.getFromData(DeviceConstants.REFERENCE_NO);
		String accountNo=gcRequest.getFromData(DeviceConstants.BANK_ACCOUNT_NO);
		
		JSONObject withdrawalDetails = GetBankWithdrawalRequest.withdrawalRequest(session, segmentType, amount,refNo,accountNo);
		boolean status = withdrawalDetails.getBoolean(DeviceConstants.STATUS);
		if (status) {
			gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.fund.request_submitted"));
			gcResponse.addToData(DeviceConstants.DISCLAIMER, withdrawalDetails.getString(DeviceConstants.DISCLAIMER));
		}
	}
}