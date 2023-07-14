package com.globecapital.services.marketdata;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.marketdata.MarketMovers_101;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.utils.DateUtils;

public class OIGainersAll_101 extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String exchange = "", instrument = "", expiry = "";
		JSONObject oiGainers = new JSONObject();
		if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			expiry = gcRequest.getFromData(DeviceConstants.EXPIRY_FILTER);
			if(expiry.isEmpty() || expiry.equalsIgnoreCase(DeviceConstants.ALL_EXPIRIES))
				expiry = DeviceConstants.ALL_EXPIRIES;
			else
				expiry = DateUtils.formatDate(expiry, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, DeviceConstants.FROM_DATE_FORMAT);
			oiGainers = MarketMovers_101.getOIGainersDerivatives(ExchangeSegment.NFO, instrument, gcRequest.getAppID(), expiry);
		} else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			exchange = gcRequest.getFromData(DeviceConstants.EXCHANGE);
			expiry = gcRequest.getFromData(DeviceConstants.EXPIRY_FILTER);
			if(expiry.isEmpty() || expiry.equalsIgnoreCase(DeviceConstants.ALL_EXPIRIES))
				expiry = DeviceConstants.ALL_EXPIRIES;
			else
				expiry = DateUtils.formatDate(expiry, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, DeviceConstants.FROM_DATE_FORMAT);
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			oiGainers = MarketMovers_101.getOIGainersCurrency(exchange, instrument, gcRequest.getAppID(), expiry);
		} else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
			expiry = gcRequest.getFromData(DeviceConstants.EXPIRY_FILTER);
			if(expiry.isEmpty() || expiry.equalsIgnoreCase(DeviceConstants.ALL_EXPIRIES))
				expiry = DeviceConstants.ALL_EXPIRIES;
			else
				expiry = DateUtils.formatDate(expiry, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, DeviceConstants.FROM_DATE_FORMAT);
			oiGainers = MarketMovers_101.getOIGainersCommodity(ExchangeSegment.MCX, instrument, gcRequest.getAppID(), expiry);
		}
		JSONArray topArr = oiGainers.getJSONArray(DeviceConstants.MARKET_DATA);
		if (topArr.length() != 0) {
			gcResponse.setData(oiGainers);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
