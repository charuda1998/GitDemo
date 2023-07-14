package com.globecapital.services.marketdata;

import org.json.JSONArray;

import com.globecapital.business.marketdata.MarketMovers_101;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class TopLosersOverview_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray topLosers = new JSONArray();
		if (segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			topLosers = MarketMovers_101.getTopLosersEquity(gcRequest.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			topLosers = MarketMovers_101.getTopLosersDerivatives(gcRequest.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			topLosers = MarketMovers_101.getTopLosersCurrency(gcRequest.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			topLosers = MarketMovers_101.getTopLosersCommodity(gcRequest.getAppID());

		if (topLosers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray topLosersOverview = new JSONArray();
			for (int i = 0; i < limit && i < topLosers.length(); i++) {
				topLosersOverview.put(topLosers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, topLosersOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
