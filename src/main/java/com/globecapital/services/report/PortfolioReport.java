package com.globecapital.services.report;

import org.json.JSONObject;

import com.globecapital.business.report.Portfolio;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class PortfolioReport extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
	
		try
		{
			JSONObject getPortfolioReports = Portfolio.getPortfolioReports(session.getUserID(), session.getAppID());
			gcResponse.setData(getPortfolioReports.getJSONObject(DeviceConstants.REPORTS));
		}catch(GCException e) {
			gcResponse.setNoDataAvailable();
		}catch(Exception e)
		{
			throw new RequestFailedException();
		}
	}

}