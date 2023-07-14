package com.globecapital.services.news;

import org.json.JSONArray;

import com.globecapital.business.news.NewsFeed;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class GetNewsByStockAlerts extends BaseService {

	private static final long serialVersionUID = 1L;

	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		String company = gcRequest.getObjectFromData(DeviceConstants.SYMBOL_OBJECT).getString(DeviceConstants.SYMBOL);

		JSONArray news = NewsFeed.getNewsBySymbolFromDB(gcRequest.getAppID(), company);

		if (news.length() > 0) {

			gcResponse.addToData(DeviceConstants.NEWSARRAY, news);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
		    gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.data_unavailable"));
		}
	}

}