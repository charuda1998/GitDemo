package com.globecapital.services.razorpay;

import org.json.JSONObject;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class GetBeneficiaryDetails extends BaseService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		JSONObject responseDetails = new JSONObject();
		JSONObject bankDetails = new JSONObject();
		JSONObject disclaimerDetails = new JSONObject();
		
		bankDetails.put(DeviceConstants.BANK_NAME, AppConfig.getValue("globe.beneficiary.bank.name"));
		bankDetails.put(DeviceConstants.BENEFICIARY, AppConfig.getValue("globe.beneficiary.name"));
		bankDetails.put(DeviceConstants.BANK_ACCOUNT, AppConfig.getValue("globe.beneficiary.account.name"));
		bankDetails.put(DeviceConstants.BANK_ACCOUNT_NO, AppConfig.getValue("globe.beneficiary.account.number"));
		bankDetails.put(DeviceConstants.IFSC, AppConfig.getValue("globe.beneficiary.ifsc.code"));
		bankDetails.put(DeviceConstants.AMOUNT_TYPE, AppConfig.getValue("globe.beneficiary.account.type"));
		
		disclaimerDetails.put(DeviceConstants.HEADER, AppConfig.getValue("globe.beneficiary.disclaimer.header"));
		disclaimerDetails.put(DeviceConstants.DISCLAIMER, AppConfig.getValue("globe.beneficiary.disclaimer.content"));
		
		responseDetails.put(DeviceConstants.BENEFICIARY_INFO, bankDetails);
		responseDetails.put(DeviceConstants.DISCLAIMER_INFO, disclaimerDetails);
		gcResponse.setData(responseDetails);
	}

}
