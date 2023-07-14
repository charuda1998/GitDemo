package com.globecapital.services.news;

import org.json.JSONObject;

import com.globecapital.business.news.NewsFeed;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class GetNewsByMarkets extends SessionService {

	private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		JSONObject news = NewsFeed.getNewsByMarketsFromDB (gcRequest.getSession().getAppID());
		
		if (news.length() > 0) {

			gcResponse.addToData(DeviceConstants.NEWS, news);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
		    gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.data_unavailable"));
		}
	}
}
	
