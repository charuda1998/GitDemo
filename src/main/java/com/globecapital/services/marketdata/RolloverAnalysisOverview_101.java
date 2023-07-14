package com.globecapital.services.marketdata;

import org.json.JSONArray;

import com.globecapital.business.marketdata.MarketMovers_101;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class RolloverAnalysisOverview_101 extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String category = gcRequest.getFromData(DeviceConstants.CATEGORY);
		String instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
		JSONArray rolloverAnalysis = new JSONArray();
		rolloverAnalysis = MarketMovers_101.getRolloverAnalysisOverview(category, instrument, gcRequest.getAppID());

		if (rolloverAnalysis.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray rolloverAnalysisOverview = new JSONArray();
			for (int i = 0; i < limit && i < rolloverAnalysis.length(); i++) {
				rolloverAnalysisOverview.put(rolloverAnalysis.get(i));
			}
			gcResponse.addToData(DeviceConstants.SORT_ORDER, DeviceConstants.ASCENDING);
			gcResponse.addToData(DeviceConstants.SORT_BY, DeviceConstants.OI.toUpperCase());
			gcResponse.addToData(DeviceConstants.ROLLOVER_ANALYSIS_DATA, rolloverAnalysisOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}

	}

}
