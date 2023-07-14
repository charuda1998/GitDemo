package com.globecapital.services.news;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.news.NewsFeed;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class GetNewsByCompany extends BaseService {

	private static final long serialVersionUID = 1L;

	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		String symToken = gcRequest.getObjectFromData(DeviceConstants.SYMBOL_OBJECT).getString(SymbolConstants.SYMBOL_TOKEN);
		JSONObject paginationObj = gcRequest.getOptObjectFromData(DeviceConstants.PAGINATION_OBJ);
		if( paginationObj!=null && !paginationObj.has(DeviceConstants.TIME) )
			paginationObj = null;
		JSONObject newsObj = NewsFeed.getNewsByCompanyFromDB(gcRequest.getAppID(), symToken, paginationObj);
		JSONArray news = newsObj.getJSONArray(DeviceConstants.NEWSARRAY);
		if (news.length() > 0) {
			gcResponse.setData(newsObj);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
		    gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.data_unavailable"));
		}
	}

}
