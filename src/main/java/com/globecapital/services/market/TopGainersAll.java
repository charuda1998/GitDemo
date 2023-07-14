package com.globecapital.services.market;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.market.MarketMovers;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class TopGainersAll extends SessionService {

	private static final long serialVersionUID = 1L;
	public String dateTime = "";
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String exchange = "", instrument = "";
		JSONObject topGainers = new JSONObject();
		if (segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);
			topGainers = MarketMovers.getTopGainersEquity(exchange, session.getAppID());
		} else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			topGainers = MarketMovers.getTopGainersDerivatives(ExchangeSegment.NFO, instrument, session.getAppID());
		} else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			topGainers = MarketMovers.getTopGainersCurrency(exchange, instrument, session.getAppID());
		} else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			topGainers = MarketMovers.getTopGainersCommodity(ExchangeSegment.MCX, instrument, session.getAppID());
		}
		JSONArray topArr = topGainers.getJSONArray(DeviceConstants.MARKET_DATA);
		if (topArr.length() != 0) {
			gcResponse.setData(topGainers);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
