package com.globecapital.services.market;

import org.json.JSONArray;

import com.globecapital.business.market.MarketMovers;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class MostActiveByVolumeOverview extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray mostActiveByVolume = new JSONArray();
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			mostActiveByVolume = MarketMovers.getMostActiveByVolumeEquity(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			 mostActiveByVolume = MarketMovers.getMostActiveByVolumeDerivatives(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			 mostActiveByVolume = MarketMovers.getMostActiveByVolumeCurrency(session.getAppID());
		 else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			 mostActiveByVolume = MarketMovers.getMostActiveByVolumeCommodity(session.getAppID());

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
