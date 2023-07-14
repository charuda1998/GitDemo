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

public class RolloverAnalysis extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		String category = gcRequest.getFromData(DeviceConstants.CATEGORY);
		String instrument = gcRequest.getFromData(DeviceConstants.INSTRUMENT);
		JSONArray rolloverAnalysis = new JSONArray();
			rolloverAnalysis = Market.getRolloverAnalysis(category, instrument, session.getAppID());
		
		if(rolloverAnalysis.length() != 0) {
			gcResponse.addToData(DeviceConstants.SORT_ORDER, DeviceConstants.ASCENDING);
			gcResponse.addToData(DeviceConstants.SORT_BY, DeviceConstants.OI.toUpperCase());
			gcResponse.addToData(DeviceConstants.ROLLOVER_ANALYSIS_DATA, rolloverAnalysis);
		} else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
		}
			
	}
	
	

}
