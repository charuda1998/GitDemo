package com.globecapital.services.marketdata;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.market.FilterList_101;
import com.globecapital.business.marketdata.MarketMovers_101;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.utils.DateUtils;

public class MostActiveByVolumeAll_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String exchange = "", instrument = "", indexName = "", expiry = "";
		JSONObject mostActiveByVolume = new JSONObject();
		if (segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);
			indexName = gcRequest.getFromData(DeviceConstants.INDEX_NAME);
			if(indexName.isEmpty())
				indexName = AppConfig.getValue("market_movers.default_index");
			else
				indexName = FilterList_101.indicesLookupMap.get(indexName.toUpperCase());
			mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeEquity(exchange, gcRequest.getAppID(),indexName);
		} else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			expiry = gcRequest.getFromData(DeviceConstants.EXPIRY_FILTER);
			if(expiry.isEmpty() || expiry.equalsIgnoreCase(DeviceConstants.ALL_EXPIRIES))
				expiry = DeviceConstants.ALL_EXPIRIES;
			else
				expiry = DateUtils.formatDate(expiry, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, DeviceConstants.FROM_DATE_FORMAT);
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeDerivatives(ExchangeSegment.NFO, instrument, gcRequest.getAppID(), expiry);
		} else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			expiry = gcRequest.getFromData(DeviceConstants.EXPIRY_FILTER);
			if(expiry.isEmpty() || expiry.equalsIgnoreCase(DeviceConstants.ALL_EXPIRIES))
				expiry = DeviceConstants.ALL_EXPIRIES;
			else
				expiry = DateUtils.formatDate(expiry, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, DeviceConstants.FROM_DATE_FORMAT);
			exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeCurrency(exchange, instrument, gcRequest.getAppID(), expiry);
		} else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			expiry = gcRequest.getFromData(DeviceConstants.EXPIRY_FILTER);
			if(expiry.isEmpty() || expiry.equalsIgnoreCase(DeviceConstants.ALL_EXPIRIES))
				expiry = DeviceConstants.ALL_EXPIRIES;
			else
				expiry = DateUtils.formatDate(expiry, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, DeviceConstants.FROM_DATE_FORMAT);
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			mostActiveByVolume = MarketMovers_101.getMostActiveByVolumeCommodity(ExchangeSegment.MCX, instrument, gcRequest.getAppID(), expiry);
		}
		JSONArray topArr = mostActiveByVolume.getJSONArray(DeviceConstants.MARKET_DATA);
		if (topArr.length() != 0) {
			gcResponse.setData(mostActiveByVolume);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
