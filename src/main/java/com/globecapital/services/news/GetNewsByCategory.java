package com.globecapital.services.news;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.news.NewsFeed;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class GetNewsByCategory extends SessionService {

	private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String category = gcRequest.getFromData(DeviceConstants.CATEGORY);
		JSONObject paginationObj = gcRequest.getOptObjectFromData(DeviceConstants.PAGINATION_OBJ);
		if(paginationObj!=null && !paginationObj.has(DeviceConstants.TIME))
			paginationObj = null;
		JSONObject newsObj = NewsFeed.getNewsByCategoryFromDB (gcRequest.getSession().getAppID(), category, paginationObj);
		JSONArray news = newsObj.getJSONArray(DeviceConstants.NEWSARRAY);
		if (news.length() > 0) {
			gcResponse.setData(newsObj);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
		    gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.data_unavailable"));
		}
	}

}


