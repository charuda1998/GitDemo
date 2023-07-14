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

public class OIGainersOverview_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray oiGainers = new JSONArray();
		if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			oiGainers = MarketMovers_101.getOIGainersDerivatives(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 oiGainers = MarketMovers_101.getOIGainersCurrency(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 oiGainers = MarketMovers_101.getOIGainersCommodity(gcRequest.getAppID());

		if(oiGainers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray oiGainersOverview = new JSONArray();
			for (int i = 0; i < limit && i < oiGainers.length(); i++) {
				oiGainersOverview.put(oiGainers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, oiGainersOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
