package com.globecapital.services.market;

import org.json.JSONArray;

import com.globecapital.business.market.Market;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class OIAnalysisOverview extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String category = gcRequest.getFromData(DeviceConstants.CATEGORY);
		
		JSONArray oiAnalysis = new JSONArray();
		
		if(segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			oiAnalysis = Market.getOiAnalysisDerivativesOverview(category, session.getAppID());
		else if(segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			oiAnalysis = Market.getOiAnalysisCommodityOverview(category, session.getAppID());
		
		if(oiAnalysis.length() != 0) {
		gcResponse.addToData(DeviceConstants.SORT_ORDER, DeviceConstants.DESCENDING);
		gcResponse.addToData(DeviceConstants.SORT_BY, DeviceConstants.OI.toUpperCase());
		gcResponse.addToData(DeviceConstants.OI_ANALYSIS_DATA, oiAnalysis);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
	}

}
