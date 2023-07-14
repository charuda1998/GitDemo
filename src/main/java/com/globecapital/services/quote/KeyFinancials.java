package com.globecapital.services.quote;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class KeyFinancials extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		JSONObject symObj = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ);
		String sType = gcRequest.getFromData(DeviceConstants.TYPE);
		
		JSONArray finalKeyFinancials = new JSONArray();
		try
		{
			finalKeyFinancials = AdvanceQuote.getKeyFinancials(symObj, sType, gcRequest.getAppID());
			gcResponse.addToData(DeviceConstants.KEY_FINANCIALS, finalKeyFinancials);
		}
		catch(Exception e) {
			gcResponse.setNoDataAvailable();
		}
		
	}

}
