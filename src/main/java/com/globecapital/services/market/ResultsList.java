package com.globecapital.services.market;

import org.json.JSONArray;

import com.globecapital.business.market.Market;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class ResultsList extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String type = gcRequest.getFromData(DeviceConstants.DATE_LS);
		String exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);

		String showTitle = "true";
		if(type.equalsIgnoreCase(DeviceConstants.TODAY))
			showTitle = "false";
		
		JSONArray results = Market.getResultsList(type, exchange,
				session.getAppID());
		if(results.length() != 0) {
		gcResponse.addToData(DeviceConstants.DATE_LS, DateUtils.getCurrentDateTime(DeviceConstants.INDEX_DATE_FORMAT));
		gcResponse.addToData(DeviceConstants.SHOW_TITLE, showTitle);
		gcResponse.addToData(DeviceConstants.RESULT_RECORDS, results);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
