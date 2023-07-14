package com.globecapital.services.order;

import java.util.List;

import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetResolvedViewDiscrepancyAPI;
import com.globecapital.api.gc.backoffice.GetResolvedViewDiscrepancyRequest;
import com.globecapital.api.gc.backoffice.GetResolvedViewDiscrepancyResponse;
import com.globecapital.api.gc.backoffice.GetResolvedViewDiscrepancyRow;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.business.order.DiscrepancyHoldings;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class ResolvedTransactions extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		
		String sUserID = session.getUserID();
		JSONObject scripDetails = gcRequest.getObjectFromData(DeviceConstants.SCRIP_DETAILS);
		
		GetResolvedViewDiscrepancyRequest discrepancyReq = new GetResolvedViewDiscrepancyRequest();
		discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
		discrepancyReq.setClientCode(sUserID);
		discrepancyReq.setScripCode(scripDetails.getString(DeviceConstants.SCRIP_CODE));
		
		GetResolvedViewDiscrepancyAPI discrepancyAPI = new GetResolvedViewDiscrepancyAPI();
		GetResolvedViewDiscrepancyResponse discrepancyRes = discrepancyAPI.get(discrepancyReq, 
				GetResolvedViewDiscrepancyResponse.class, session.getAppID(),"GetResolvedViewDiscrepancy");
		if (discrepancyRes.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
			discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
			discrepancyRes = discrepancyAPI.get(discrepancyReq,GetResolvedViewDiscrepancyResponse.class, session.getAppID()
					,"GetResolvedViewDiscrepancy");
		}if(!discrepancyRes.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS)) {
			gcResponse.setNoDataAvailable();
			return;
		}if (discrepancyRes.getMessage().equalsIgnoreCase(MessageConstants.SUCCESS))  {
			List<GetResolvedViewDiscrepancyRow> discrepancyRows = discrepancyRes.getDetails();
			if(discrepancyRows.size() == 0) // If there is not record in GETDISCREPANCY API, then return
			{
				gcResponse.setNoDataAvailable();
				return;
			}
			gcResponse.setData(DiscrepancyHoldings.getResolvedDiscrepancyDetails(discrepancyRows));
		}
		
	}

}
