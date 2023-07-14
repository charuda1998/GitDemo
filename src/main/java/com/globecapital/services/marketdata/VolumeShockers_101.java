package com.globecapital.services.marketdata;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.market.FilterList_101;
import com.globecapital.business.marketdata.MarketMovers_101;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class VolumeShockers_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);
		String indexName = gcRequest.getFromData(DeviceConstants.INDEX_NAME);
		if(indexName.isEmpty())
			indexName = AppConfig.getValue("market_movers.default_index");
		else
			indexName = FilterList_101.indicesLookupMap.get(indexName.toUpperCase());
		JSONObject volumeShockers = new JSONObject();
		if (segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			volumeShockers = MarketMovers_101.getVolumeShockersEquity(exchange, gcRequest.getAppID(), indexName);

		JSONArray topArr = volumeShockers.getJSONArray(DeviceConstants.MARKET_DATA);
		if (topArr.length() != 0) {
			gcResponse.setData(volumeShockers);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
