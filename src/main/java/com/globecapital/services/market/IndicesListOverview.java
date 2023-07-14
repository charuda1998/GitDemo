package com.globecapital.services.market;

import org.json.JSONArray;

import com.globecapital.business.market.Market;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.utils.DateUtils;

public class IndicesListOverview extends BaseService {

	private static final long serialVersionUID = 1L;

	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONArray indices = Market.getIndicesListOverview(segment);
		if(indices.length()!=0) {
			gcResponse.addToData(DeviceConstants.DATE_LS, DateUtils.getCurrentDateTime(DeviceConstants.INDEX_DATE_FORMAT));
			gcResponse.addToData("indexList", indices);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}
}
