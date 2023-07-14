package com.globecapital.services.quote;

import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class ProfitAndLoss_101 extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		
		JSONObject symObj = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ);
		
		try
		{
			gcResponse.addToData(DeviceConstants.PERIOD_TXT, DeviceConstants.DISP_YEAR_ENDED);
			gcResponse.addToData(DeviceConstants.PROFIT_LOSS, AdvanceQuote.profitLoss_101(symObj,  
				gcRequest.getFromData(DeviceConstants.TYPE), session.getAppID()));
		}
		catch(Exception e) {
			gcResponse.setNoDataAvailable();
		}

	}
	
}
