package com.globecapital.services.funds;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.funds.FundTransfer;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class GetFundTransferDetails extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		JSONObject userInfoObj = session.getUserInfo();
		JSONArray prodList = userInfoObj.getJSONArray(UserInfoConstants.PRODUCT_TYPE);
		
		gcResponse.setData(FundTransfer.getFundTransferDetails(prodList));
		
	}

}
