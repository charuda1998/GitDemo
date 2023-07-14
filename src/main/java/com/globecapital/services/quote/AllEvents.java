package com.globecapital.services.quote;

import org.json.JSONArray;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class AllEvents extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ)
				.getString(SymbolConstants.SYMBOL_TOKEN);
		
		try
		{
			JSONArray eventsArr = AdvanceQuote.getEvents(sSymbolToken, 
					gcRequest.getFromData(DeviceConstants.ACTION), false, gcRequest.getAppID());
			if(eventsArr.length() > 0)
				gcResponse.addToData(DeviceConstants.EVENTS, eventsArr);
			else
				gcResponse.setNoDataAvailable();
		}
		catch(Exception e)
		{
			gcResponse.setNoDataAvailable();
		}

	}

}
