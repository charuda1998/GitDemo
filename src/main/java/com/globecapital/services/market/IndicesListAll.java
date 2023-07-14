package com.globecapital.services.market;

import org.json.JSONArray;

import com.globecapital.business.market.Market;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class IndicesListAll extends SessionService {

	private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);
		JSONArray indices = Market.getIndicesList(exchange);
		if(indices.length()!=0) {
			gcResponse.addToData("indexList", indices);
			gcResponse.addToData(DeviceConstants.DATE_LS, DateUtils.getCurrentDateTime(DeviceConstants.INDEX_DATE_FORMAT));
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
