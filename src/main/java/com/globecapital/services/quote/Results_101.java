package com.globecapital.services.quote;

import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class Results_101 extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sPeriodTxt = gcRequest.getFromData(DeviceConstants.PERIOD);
		JSONObject symObj = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ);
		
		try
		{
			gcResponse.addToData(DeviceConstants.RESULT, AdvanceQuote.getResults_101(symObj, sPeriodTxt, 
					gcRequest.getFromData(DeviceConstants.TYPE), session.getAppID()));
		}
		catch(Exception e) {
			gcResponse.setNoDataAvailable();
		}
	}

}
