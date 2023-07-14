package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.business.report.FilterList_101;
import com.globecapital.business.report.Transaction;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class TransactionReport_101 extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		
		JSONArray getTransactionReports = Transaction.getTransactionReports(session, segmentType,filterObj,DeviceConstants.TRANSACTION);
		if (getTransactionReports.length() > 0) {
			gcResponse.addToData(DeviceConstants.AS_ON_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.REPORTS, getTransactionReports);
			if(segmentType.equalsIgnoreCase(DeviceConstants.EQUITY))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.TRANSACTION_EQUITY,filterObj));
			else if(segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.TRANSACTION_DERIVATIVE,filterObj));
			else if(segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.TRANSACTION_CURRENCY,filterObj));
			else if(segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY))
				gcResponse.addToData(DeviceConstants.ADVANCED_FILTER,FilterList_101.getFilterTypes(DeviceConstants.TRANSACTION_COMMODITY,filterObj));
		} else
			gcResponse.setNoDataAvailable();
	}
}
