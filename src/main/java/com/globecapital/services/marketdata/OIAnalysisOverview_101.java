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

public class OIAnalysisOverview_101 extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String category = gcRequest.getFromData(DeviceConstants.CATEGORY);
		
		JSONArray oiAnalysis = new JSONArray();
		
		if(segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			oiAnalysis =MarketMovers_101.getOiAnalysisDerivativesOverview(category, gcRequest.getAppID());
		else if(segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			oiAnalysis =MarketMovers_101.getOiAnalysisCommodityOverview(category, gcRequest.getAppID());
		
		if(oiAnalysis.length() != 0) {
			int limit = AppConfig.getIntValue("market_limit");
			JSONArray oiAnalysisOverview = new JSONArray();
			for (int i = 0; i < limit && i < oiAnalysis.length(); i++) {
				oiAnalysisOverview.put(oiAnalysis.get(i));
		}
		gcResponse.addToData(DeviceConstants.SORT_ORDER, DeviceConstants.DESCENDING);
		gcResponse.addToData(DeviceConstants.SORT_BY, DeviceConstants.OI.toUpperCase());
		gcResponse.addToData(DeviceConstants.OI_ANALYSIS_DATA, oiAnalysisOverview);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
