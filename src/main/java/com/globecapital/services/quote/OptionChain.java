package com.globecapital.services.quote;

import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;

public class OptionChain extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		
		JSONObject symObj = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ);
		
		JSONObject options = AdvanceQuote.getOptionChain(symObj, filterObj, gcRequest.getAppID());
		if( options.length() > 0 )
			gcResponse.setData(options);
		else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG,
					InfoMessage.getInfoMSG("info_msg.invalid.no_options"));
		
	}

}
