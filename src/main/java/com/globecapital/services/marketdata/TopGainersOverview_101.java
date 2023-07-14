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

public class TopGainersOverview_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray topGainers = new JSONArray();
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			topGainers = MarketMovers_101.getTopGainersEquity(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			topGainers = MarketMovers_101.getTopGainersDerivatives(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 topGainers = MarketMovers_101.getTopGainersCurrency(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 topGainers = MarketMovers_101.getTopGainersCommodity(gcRequest.getAppID());
		
		if(topGainers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray topGainersOverview = new JSONArray();
			for (int i = 0; i < limit && i < topGainers.length(); i++) {
				topGainersOverview.put(topGainers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, topGainersOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
