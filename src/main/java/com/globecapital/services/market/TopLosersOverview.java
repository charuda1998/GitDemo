package com.globecapital.services.market;

import org.json.JSONArray;

import com.globecapital.business.market.MarketMovers;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class TopLosersOverview extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray topLosers = new JSONArray();
		if (segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			topLosers = MarketMovers.getTopLosersEquity(session.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			topLosers = MarketMovers.getTopLosersDerivatives(session.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			topLosers = MarketMovers.getTopLosersCurrency(session.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			topLosers = MarketMovers.getTopLosersCommodity(session.getAppID());

		if (topLosers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray arr = new JSONArray();
			for (int i = 0; i < limit && i < topLosers.length(); i++) {
				arr.put(topLosers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, arr);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
