package com.globecapital.services.report;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.report.ProfitLoss;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class UnRealisedProfitLossTransaction_101 extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		JSONObject transApiObj= gcRequest.getObjectFromData(DeviceConstants.TRANS_API_OBJ);
		
		String maxSlot = gcRequest.getOptFromData(DeviceConstants.START_INDEX, "");
		JSONArray getProfitLossTransactionReports = ProfitLoss.getUnRealisedProfitLossTransaction_101(session,
				 maxSlot,transApiObj);
		if (getProfitLossTransactionReports.length() > 0) {
			gcResponse.addToData(DeviceConstants.AS_ON_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.REPORTS, getProfitLossTransactionReports);
			gcResponse.addToData(DeviceConstants.NEXT_INDEX, "20");
		} else
			gcResponse.setNoDataAvailable();
	}
}
