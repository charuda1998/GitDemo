package com.globecapital.services.razorpay;

import com.globecapital.api.razorpay.generics.PollOrderStatusAPI;
import com.globecapital.api.razorpay.generics.PollOrderStatusRequest;
import com.globecapital.api.razorpay.generics.PollOrderStatusResponse;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;
import com.msf.log.Logger;

public class PollOrderStatus extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(PollOrderStatus.class);
	
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String orderId = gcRequest.getFromData(DeviceConstants.PG_ORDER_ID);
		PollOrderStatusAPI pollOrderStatusAPI = new PollOrderStatusAPI(orderId);
		PollOrderStatusRequest razorPayRequest = new PollOrderStatusRequest();
		PollOrderStatusResponse pollOrderStatusResponse = new PollOrderStatusResponse();
		pollOrderStatusResponse = pollOrderStatusAPI.get(razorPayRequest, PollOrderStatusResponse.class, gcRequest.getAppID(),"OrderStatus");
		gcResponse.addToData(DeviceConstants.RESULT , pollOrderStatusResponse);
	}
}
