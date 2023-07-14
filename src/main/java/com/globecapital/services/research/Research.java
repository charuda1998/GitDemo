package com.globecapital.services.research;

import org.json.JSONArray;

import com.globecapital.business.research.GetResearchData;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class Research extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		
		Session session = gcRequest.getSession();
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		
		JSONArray getResearchData = GetResearchData.getResearchData(session,segmentType);
		if (getResearchData.length() > 0) {
			if(getResearchData.getJSONObject(0).getJSONArray(DeviceConstants.CALL_RECORDS).length() > 0 || 
					getResearchData.getJSONObject(1).getJSONArray(DeviceConstants.CALL_RECORDS).length() > 0)
				gcResponse.addToData(DeviceConstants.RESEARCH, getResearchData);
			else
				gcResponse.setNoDataAvailable();
		} else
			gcResponse.setNoDataAvailable();
	}
}
