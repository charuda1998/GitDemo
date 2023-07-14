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

public class OILosersOverview extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray oiLosers = new JSONArray();
		if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			oiLosers = MarketMovers.getOILosersDerivatives(session.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			oiLosers = MarketMovers.getOILosersCurrency(session.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			oiLosers = MarketMovers.getOILosersCommodity(session.getAppID());

		if (oiLosers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray arr = new JSONArray();
			for (int i = 0; i < limit && i<oiLosers.length(); i++) {
				arr.put(oiLosers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, arr);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
