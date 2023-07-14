package com.globecapital.services.market;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.market.MarketMovers;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class AllTimeHigh extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();

		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);

		JSONObject allTimeHigh = new JSONObject();

		if (segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			allTimeHigh = MarketMovers.getAllTimeHighEquity(exchange, session.getAppID());

		JSONArray topArr = allTimeHigh.getJSONArray(DeviceConstants.MARKET_DATA);
		if (topArr.length() != 0) {
			gcResponse.setData(allTimeHigh);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}

	}

}
