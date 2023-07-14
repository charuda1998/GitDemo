package com.globecapital.services.funds;

import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class AddFundsURL extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();  
		
		String sPayInURL = AppConfig.getValue("pay_in_url");
		String UserId = session.getUserID();
		String GroupId = session.getGroupId();
		String SessionId = session.getjSessionIDWithoutEncryption();
		String PageNo = "1";
		String IsMobileDevice = "Y";
		String IsHeaderReqd = "N";
		String EncryptType = "0";
		
		gcResponse.addToData(DeviceConstants.PAYMENT_METHOD, "GET");
		gcResponse.addToData(DeviceConstants.PAYMENT_URL, 
				String.format(InfoMessage.getInfoMSG("info_msg.funds.pay_in_url"), 
						sPayInURL, UserId, GroupId, SessionId, PageNo, IsMobileDevice, IsHeaderReqd, 
						EncryptType));
		
	}

}
