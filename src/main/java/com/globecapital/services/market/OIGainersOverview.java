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

public class OIGainersOverview extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray oiGainers = new JSONArray();
		if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			oiGainers = MarketMovers.getOIGainersDerivatives(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 oiGainers = MarketMovers.getOIGainersCurrency(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 oiGainers = MarketMovers.getOIGainersCommodity(session.getAppID());

		if(oiGainers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray arr = new JSONArray();
			for (int i=0; i<limit && i<oiGainers.length(); i++) {
				arr.put(oiGainers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, arr);
			} else {
				gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
	}
	}
}
