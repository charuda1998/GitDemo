package com.globecapital.services.search;

import java.util.Objects;

import org.json.JSONArray;
import org.json.me.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;

public class UpdateRecentSearchItems extends BaseService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		JSONArray symbolList = gcRequest.getData().getJSONArray(DeviceConstants.SYMBOL_LIST);
		JSONArray updatedSymbolList = new JSONArray();
		for(int i = 0 ; i < symbolList.length(); i++) {
			JSONObject symObj = new JSONObject(String.valueOf(symbolList.get(i)));
			String nTokenSegId = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
			SymbolRow symRow = SymbolMap.getSymbolRow(nTokenSegId);
			if(!Objects.isNull(SymbolMap.getSymbolRow(nTokenSegId)))
				updatedSymbolList.put(symRow.getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ));
		}
		gcResponse.addToData(DeviceConstants.SYMBOL_LIST, updatedSymbolList);
	}
}
