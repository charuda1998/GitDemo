package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.report.FilterList_102;
import com.globecapital.business.report.ProfitLoss_101;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class UnRealisedProfitLossReport_101 extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);

		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);

		JSONArray getProfitLossReports = ProfitLoss_101.getUnRealisedProfitLossReports(session, segmentType, filterObj);

		JSONObject summaryObj = new JSONObject();
		for (int i = 0; i < getProfitLossReports.length(); i++) {

			JSONObject profitLossObject = new JSONObject();
			profitLossObject = getProfitLossReports.getJSONObject(i);

			if (profitLossObject.toString().contains(DeviceConstants.TOTAL_SUMMARY)) {
				summaryObj = getProfitLossReports.getJSONObject(i);
				getProfitLossReports.remove(i);
			}
		}

		if (getProfitLossReports.length() > 0) {
			gcResponse.addToData(DeviceConstants.AS_ON_DATE, getProfitLossReports.getJSONObject(0).getString(DeviceConstants.AS_ON_DATE));
			gcResponse.addToData(DeviceConstants.REPORTS, getProfitLossReports);
			gcResponse.addToData(DeviceConstants.TOTAL_SUMMARY,
					summaryObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY));
		} else
			gcResponse.setNoDataAvailable();
	}
}