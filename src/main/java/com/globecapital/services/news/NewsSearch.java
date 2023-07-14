package com.globecapital.services.news;

import org.json.JSONArray;

import com.globecapital.business.news.NewsFeed;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.SessionService;

public class NewsSearch extends SessionService {

	private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		String searchString = gcRequest.getFromData(DeviceConstants.SEARCH_STRING);
		String category = gcRequest.getFromData(DeviceConstants.CATEGORY);
		
		if (searchString.length() < 1 && !searchString.equals("")) {
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
		
		JSONArray newsArray = NewsFeed.searchNews(searchString, category);
		
		if (newsArray.length() < 1) {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
		    gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.data_unavailable"));
		}
		
		gcResponse.addToData(DeviceConstants.NEWSARRAY, newsArray);
	}
}
