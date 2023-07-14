package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.report.FilterList;
import com.globecapital.business.report.Holdings;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class HoldingsReport extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();

		
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		JSONArray getHoldingsReports = Holdings.getHoldingsReports(session,filterObj,DeviceConstants.HOLDINGS);
		JSONObject summaryObj = new JSONObject();
		for (int i = 0; i < getHoldingsReports.length(); i++) {

			JSONObject profitLossObject = new JSONObject();
			profitLossObject = getHoldingsReports.getJSONObject(i);

			if (profitLossObject.toString().contains(DeviceConstants.TOTAL_SUMMARY)) {
				summaryObj = getHoldingsReports.getJSONObject(i);
				getHoldingsReports.remove(i);
			}
		}
		if (getHoldingsReports.length() > 0) {
			gcResponse.addToData(DeviceConstants.TOTAL_SUMMARY,
					summaryObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY));
			gcResponse.addToData(DeviceConstants.REPORTS, getHoldingsReports);
			gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList.getFilterTypes(DeviceConstants.HOLDINGS,filterObj));
		} else
			gcResponse.setNoDataAvailable();
	}
}