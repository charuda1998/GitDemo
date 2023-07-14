package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.business.report.FilterList_101;
import com.globecapital.business.report.ProfitLoss;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class RealisedProfitLossReport_101 extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);

		JSONArray getProfitLossReports = ProfitLoss.getRealisedProfitLossReports(session, segmentType,
				filterObj,DeviceConstants.REALISED_PROFIT_LOSS);

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
			gcResponse.addToData(DeviceConstants.REPORTS, getProfitLossReports);
			gcResponse.addToData(DeviceConstants.AS_ON_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.TOTAL_SUMMARY,
					summaryObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY));
			if(segmentType.equalsIgnoreCase(DeviceConstants.EQUITY))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_EQUITY,filterObj));
			else if(segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_DERIVATIVE,filterObj));
			else if(segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_CURRENCY,filterObj));
			else if(segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_COMMODITY,filterObj));
		} else
			gcResponse.setNoDataAvailable();
	}

}
