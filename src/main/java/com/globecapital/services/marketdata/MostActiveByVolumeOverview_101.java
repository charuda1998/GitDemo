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

public class MostActiveByVolumeOverview_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray mostActiveByVolume = new JSONArray();
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeEquity(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			 mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeDerivatives(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeCurrency(gcRequest.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeCommodity(gcRequest.getAppID());

		if(mostActiveByVolume.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray mostActiveByVolumeOverview = new JSONArray();
			for (int i = 0; i < limit && i < mostActiveByVolume.length(); i++) {
				mostActiveByVolumeOverview.put(mostActiveByVolume.get(i));
		}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, mostActiveByVolumeOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
