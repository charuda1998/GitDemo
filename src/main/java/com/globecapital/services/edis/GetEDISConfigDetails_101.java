package com.globecapital.services.edis;

import javax.servlet.ServletContext;

import org.json.JSONArray;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.msf.log.Logger;

public class GetEDISConfigDetails_101 extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(GetEDISConfigDetails_101.class);

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		boolean isPOAUser = Boolean.parseBoolean(session.getUserInfo().getString(UserInfoConstants.POA_STATUS));
		boolean isApproveAll = Boolean.parseBoolean(gcRequest.getFromData(DeviceConstants.IS_APPROVE_ALL));
		boolean isHoldingsFlow = Boolean.parseBoolean(gcRequest.getOptFromData(DeviceConstants.IS_HOLDINGS_FLOW, "false"));
		JSONArray approvalDetails = gcRequest.getArrayFromData(DeviceConstants.APPROVAL_DETAILS);
		try
		{
			if(!isPOAUser)
				gcResponse.setData(com.globecapital.business.edis.GetEDISConfigDetails_101.getEDISApprovalDetails(session, approvalDetails, isApproveAll, isHoldingsFlow,getServletContext(),gcRequest,gcResponse));
			else
				gcResponse.setNoDataAvailable();
		}
		catch(Exception e)
		{
			log.info(e);
			gcResponse.setNoDataAvailable();
		}
		
	}

}
