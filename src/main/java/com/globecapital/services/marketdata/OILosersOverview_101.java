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

public class OILosersOverview_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray oiLosers = new JSONArray();
		if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			oiLosers = MarketMovers_101.getOILosersDerivatives(gcRequest.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			oiLosers = MarketMovers_101.getOILosersCurrency(gcRequest.getAppID());
		else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			oiLosers = MarketMovers_101.getOILosersCommodity(gcRequest.getAppID());

		if (oiLosers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray oiLosersOverview = new JSONArray();
			for (int i = 0; i < limit && i < oiLosers.length(); i++) {
				oiLosersOverview.put(oiLosers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, oiLosersOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
