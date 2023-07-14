package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.report.FilterList_102;
import com.globecapital.business.report.Tax_101;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class TaxReport_101 extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);

		JSONArray getTaxReports = Tax_101.getTaxReports(session, segmentType,DeviceConstants.TAX,filterObj);
	
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
			gcResponse.addToData(DeviceConstants.TOTAL_SUMMARY,
					summaryObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY));
			if(segmentType.equalsIgnoreCase(DeviceConstants.EQUITY))
   				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_102.getFilterTypes(DeviceConstants.TAX_EQUITY,filterObj));
   			else if(segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
   				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_102.getFilterTypes(DeviceConstants.TAX_DERIVATIVE,filterObj));
   			else if(segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY))
   				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_102.getFilterTypes(DeviceConstants.TAX_CURRENCY,filterObj));
   			else if(segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY))
   				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_102.getFilterTypes(DeviceConstants.TAX_COMMODITY,filterObj));
   		
			} else
			gcResponse.setNoDataAvailable();
	}
}