package com.globecapital.services.market;

import org.json.JSONObject;
import com.globecapital.business.market.Market;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;

public class MarketLive extends BaseService{

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		JSONObject news = Market.getLive();

		if (news.length() > 0) {

			gcResponse.setData(news);
		} else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.invalid.no_data"));

	}


}
