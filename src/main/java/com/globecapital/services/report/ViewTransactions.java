package com.globecapital.services.report;

import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class ViewTransactions extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

//		Session session = gcRequest.getSession();
//		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
//		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
//		
//		JSONArray getLedgerReports = Ledger.getLedgerReports(session, segmentType,filterObj);
//		
//		
//		if (getLedgerReports.length() > 0) {
//			gcResponse.addToData(DeviceConstants.AS_ON_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
//			gcResponse.addToData(DeviceConstants.REPORTS, getLedgerReports);
//			gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList.getFilterTypes(DeviceConstants.LEDGER,filterObj));
//		} else
//			gcResponse.setNoDataAvailable();
	}
}
