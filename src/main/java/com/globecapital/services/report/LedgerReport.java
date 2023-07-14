package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.report.FilterList;
import com.globecapital.business.report.Ledger;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class LedgerReport extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		
		JSONArray getLedgerReports = Ledger.getLedgerReports(session, segmentType,filterObj);
		
		
		if (getLedgerReports.length() > 0) {
			gcResponse.addToData(DeviceConstants.AS_ON_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.REPORTS, getLedgerReports);
			gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList.getFilterTypes(DeviceConstants.LEDGER,filterObj));
		} else
			gcResponse.setNoDataAvailable();
	}
}
