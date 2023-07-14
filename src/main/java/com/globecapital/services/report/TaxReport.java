package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.report.FilterList;
import com.globecapital.business.report.Tax;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class TaxReport extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);

		JSONArray getTaxReports = Tax.getTaxReports(session, segmentType,DeviceConstants.TAX,filterObj);
	
		JSONObject summaryObj = new JSONObject();
		JSONObject percentObj = new JSONObject();
		for (int i = 0; i < getTaxReports.length(); i++) {

			JSONObject profitLossObject = new JSONObject();
			profitLossObject = getTaxReports.getJSONObject(i);

			if (profitLossObject.toString().contains(DeviceConstants.TOTAL_SUMMARY)) {
				summaryObj = getTaxReports.getJSONObject(i);
				getTaxReports.remove(i);
			}

		}

		for (int i = 0; i < getTaxReports.length(); i++) {

			JSONObject profitLossObject = new JSONObject();
			profitLossObject = getTaxReports.getJSONObject(i);

			if (profitLossObject.toString().contains(DeviceConstants.TAX_PERCENTAGE)) {
				percentObj = getTaxReports.getJSONObject(i);
				getTaxReports.remove(i);
			}

		}
		if (getTaxReports.length() > 0) {
			gcResponse.addToData(DeviceConstants.AS_ON_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.REPORTS, getTaxReports);
			gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList.getFilterTypes(DeviceConstants.TAX,filterObj));
			gcResponse.addToData(DeviceConstants.TOTAL_SUMMARY,
					summaryObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY));
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				gcResponse.addToData(DeviceConstants.TAX_PERCENTAGE,
						percentObj.getJSONObject(DeviceConstants.TAX_PERCENTAGE));
			}

		} else
			gcResponse.setNoDataAvailable();
	}
}