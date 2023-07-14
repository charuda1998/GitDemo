package com.globecapital.services.report;

import org.json.JSONObject;

import com.globecapital.business.report.Email;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class EmailReport extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		String otherReportType = gcRequest.getFromData(DeviceConstants.REPORT_TYPE);
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);

		if (Email.getTransactionEmailStatus(session, segmentType, filterObj, otherReportType)
				.equalsIgnoreCase(MessageConstants.SUCCESS)) {

			gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.email.send.success"));
		} else
			throw new RequestFailedException();
	}
}