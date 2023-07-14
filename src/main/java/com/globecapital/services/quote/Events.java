package com.globecapital.services.quote;

import org.json.JSONArray;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class Events extends BaseService {

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
			JSONArray eventsArr = AdvanceQuote.getEvents(sSymbolToken, DeviceConstants.ALL, 
					true, gcRequest.getAppID());
			
			if(eventsArr.length() > 0)
			{
				int limit = AppConfig.getIntValue("market_limit");
				
				JSONArray arr = new JSONArray();
				for (int i = 0; i < limit && i < eventsArr.length(); i++) 
					arr.put(eventsArr.get(i));
				
				gcResponse.addToData(DeviceConstants.EVENTS, arr);
			}
			else
				gcResponse.setNoDataAvailable();
		}
		catch(Exception e)
		{
			gcResponse.setNoDataAvailable();
		}

	}

}
