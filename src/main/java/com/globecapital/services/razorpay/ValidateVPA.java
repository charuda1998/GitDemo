package com.globecapital.services.razorpay;

import java.util.Objects;

import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.api.razorpay.generics.ValidateVPAAPI;
import com.globecapital.api.razorpay.generics.ValidateVPARequest;
import com.globecapital.api.razorpay.generics.ValidateVPAResponse;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class ValidateVPA extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String vpa = gcRequest.getFromData(RazorPayConstants.VPA);
		
		ValidateVPAAPI validateVPAAPI = new ValidateVPAAPI();
		ValidateVPARequest validateVPARequest = new ValidateVPARequest();
		ValidateVPAResponse validateVPAResponse = new ValidateVPAResponse();
		
		validateVPARequest.setVPA(vpa);
		
		validateVPAResponse = validateVPAAPI.post(validateVPARequest, ValidateVPAResponse.class, gcRequest.getAppID(),"ValidateVPA");
		if(Objects.isNull(validateVPAResponse.getError())) {
			gcResponse.addToData(DeviceConstants.STATUS, "true");
		}else {
			gcResponse.addToData(DeviceConstants.STATUS, "false");
			gcResponse.addToData(DeviceConstants.NEWS_DESCRIPTION, InfoMessage.getInfoMSG("info_msg.invalid_upi"));
		}
	}
}
