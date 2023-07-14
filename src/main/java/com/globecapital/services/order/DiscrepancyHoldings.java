package com.globecapital.services.order;

import java.util.List;

import com.globecapital.api.gc.backoffice.GetDiscrepancyAPI;
import com.globecapital.api.gc.backoffice.GetDiscrepancyRequest;
import com.globecapital.api.gc.backoffice.GetDiscrepancyResponse;
import com.globecapital.api.gc.backoffice.GetDiscrepancyRow;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class DiscrepancyHoldings extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		
		String sUserID = session.getUserID();
		
		GetDiscrepancyRequest discrepancyReq = new GetDiscrepancyRequest();
		discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
		discrepancyReq.setClientCode(sUserID);
		GetDiscrepancyAPI holdingsApi = new GetDiscrepancyAPI();
		GetDiscrepancyResponse discrepancyRes = holdingsApi.get(discrepancyReq, GetDiscrepancyResponse.class,
													session.getAppID(),"GetDiscrepancy");
		if (discrepancyRes.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
			discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
			discrepancyRes = holdingsApi.get(discrepancyReq, GetDiscrepancyResponse.class, session.getAppID(),"GetDiscrepancy");
		} 
		if(!discrepancyRes.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {
			gcResponse.setNoDataAvailable();
			return;
		}
		else {
			List<GetDiscrepancyRow> discrepancyRows = discrepancyRes.getDetails();
			if(discrepancyRows.size() == 0) // If there is not record in GETDISCREPANCY API, then return
			{
				gcResponse.setNoDataAvailable();
				return;
			}
			gcResponse.addToData(DeviceConstants.DISCREPANCIES, 
					com.globecapital.business.order.DiscrepancyHoldings.getDiscrepancyDetails(discrepancyRows));	
			gcResponse.addToData(DeviceConstants.DISCREPANCY_COUNT, discrepancyRes.getTotRecords());
		}
		
	}

}
