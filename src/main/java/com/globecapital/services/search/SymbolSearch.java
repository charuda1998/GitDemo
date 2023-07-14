package com.globecapital.services.search;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.search.SearchHelper;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.SessionService;

public class SymbolSearch extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		// TODO Auto-generated method stub

		String searchString = gcRequest.getFromData(DeviceConstants.SEARCH_STRING);

		if (searchString.length() < 1 && !searchString.equals("")) {
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.invalid.symbol.search"));
		}

		JSONArray resultArray = SearchHelper.SymbolSearch(searchString);
//		JSONArray resultArray = SearchProcess.getEquitySymbols(searchString, gcRequest.getSession());

		JSONArray symbolInfo = new JSONArray();

		for (int i = 0; i < resultArray.length(); i++) {

			symbolInfo.put(new JSONObject(resultArray.get(i).toString()).getJSONObject(SymbolConstants.SYMBOL_OBJ));
		}

		if (resultArray.length() < 1) {
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getValue("info_msg.invalid.symbol.details"));
		}

		gcResponse.addToData(DeviceConstants.SYMBOL_LIST, symbolInfo);

	}

}
