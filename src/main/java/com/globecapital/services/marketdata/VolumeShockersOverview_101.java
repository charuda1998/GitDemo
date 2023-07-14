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

public class VolumeShockersOverview_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray volumeShockers = new JSONArray();
		if (segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			volumeShockers = MarketMovers_101.getVolumeShockersEquity(gcRequest.getAppID());

		if (volumeShockers.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray volumeShockersOverview = new JSONArray();
			for (int i = 0; i < limit && i < volumeShockers.length(); i++) {
				volumeShockersOverview.put(volumeShockers.get(i));
			}
			gcResponse.addToData(DeviceConstants.MARKET_DATA, volumeShockersOverview);
		}	else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
