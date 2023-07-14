package com.globecapital.services.quote;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class KeyRatio extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		
		JSONObject symObj = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ);
		
		JSONArray marginList = new JSONArray();
		marginList = AdvanceQuote.getKeyRatio(symObj, 
				gcRequest.getFromData(DeviceConstants.TYPE), session.getAppID());
		
		if(marginList.length() > 0)
			gcResponse.addToData(DeviceConstants.KEY_RATIO, marginList);
		else
			gcResponse.setNoDataAvailable();
		
	}

}
