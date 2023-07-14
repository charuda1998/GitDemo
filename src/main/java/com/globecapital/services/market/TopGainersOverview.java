package com.globecapital.services.market;

import org.json.JSONArray;

import com.globecapital.business.market.MarketMovers;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class TopGainersOverview extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray topGainers = new JSONArray();
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			topGainers = MarketMovers.getTopGainersEquity(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			topGainers = MarketMovers.getTopGainersDerivatives(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 topGainers = MarketMovers.getTopGainersCurrency(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 topGainers = MarketMovers.getTopGainersCommodity(session.getAppID());
		
		if(topGainers.length() != 0) {
		gcResponse.addToData(DeviceConstants.MARKET_DATA, topGainers);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
