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

public class MostActiveByValueOverview extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray mostActiveByValue = new JSONArray();
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			mostActiveByValue = MarketMovers.getMostActiveByValueEquity(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			 mostActiveByValue = MarketMovers.getMostActiveByValueDerivatives(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 mostActiveByValue = MarketMovers.getMostActiveByValueCurrency(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 mostActiveByValue = MarketMovers.getMostActiveByValueCommodity(session.getAppID());

		if(mostActiveByValue.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray mostActiveByValueOverview = new JSONArray();
			for (int i = 0; i < limit && i < mostActiveByValue.length(); i++) {
				mostActiveByValueOverview.put(mostActiveByValue.get(i));
		}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, mostActiveByValueOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
