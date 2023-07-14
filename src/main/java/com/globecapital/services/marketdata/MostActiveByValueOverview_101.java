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

public class MostActiveByValueOverview_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray mostActiveByValue = new JSONArray();
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			mostActiveByValue = MarketMovers_101.getMostActiveByValueEquity(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			 mostActiveByValue = MarketMovers_101.getMostActiveByValueDerivatives(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 mostActiveByValue = MarketMovers_101.getMostActiveByValueCurrency(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 mostActiveByValue = MarketMovers_101.getMostActiveByValueCommodity(gcRequest.getAppID());

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
