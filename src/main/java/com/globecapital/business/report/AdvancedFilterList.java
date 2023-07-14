package com.globecapital.business.report;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.session.Session;

public class AdvancedFilterList {

	public static JSONArray getFilterList(Session session) throws JSONException {

		JSONArray filter = new JSONArray();
		JSONObject filterObj= new JSONObject();
		
		JSONObject ledgerFilter = new JSONObject();
		ledgerFilter.put(DeviceConstants.TYPE, DeviceConstants.LEDGER);
		ledgerFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList.getFilterTypes(DeviceConstants.LEDGER, filterObj));

		JSONObject holdingsFilter = new JSONObject();
		holdingsFilter.put(DeviceConstants.TYPE, DeviceConstants.HOLDINGS);
		holdingsFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList.getFilterTypes(DeviceConstants.HOLDINGS, filterObj));

		JSONObject transactionFilter = new JSONObject();
		transactionFilter.put(DeviceConstants.TYPE, DeviceConstants.TRANSACTION);
		transactionFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList.getFilterTypes(DeviceConstants.TRANSACTION, filterObj));

		JSONObject realisedPLFilter = new JSONObject();
		realisedPLFilter.put(DeviceConstants.TYPE, DeviceConstants.REALISED_PROFIT_LOSS);
		realisedPLFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS, filterObj));

		JSONObject unRealisedPLFilter = new JSONObject();
		unRealisedPLFilter.put(DeviceConstants.TYPE, DeviceConstants.UNREALISED_PROFIT_LOSS);
		unRealisedPLFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList.getFilterTypes(DeviceConstants.UNREALISED_PROFIT_LOSS, filterObj));

		JSONObject taxFilter = new JSONObject();
		taxFilter.put(DeviceConstants.TYPE, DeviceConstants.TAX);
		taxFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList.getFilterTypes(DeviceConstants.TAX, filterObj));

		JSONObject saudaFilter = new JSONObject();
		saudaFilter.put(DeviceConstants.TYPE, DeviceConstants.SAUDA_BILL);
		saudaFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList.getFilterTypes(DeviceConstants.OTHER, filterObj));

		JSONObject contractNotesFilter = new JSONObject();
		contractNotesFilter.put(DeviceConstants.TYPE, DeviceConstants.CONTRACT_NOTES);
		contractNotesFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList.getFilterTypes(DeviceConstants.OTHER, filterObj));

		filter.put(ledgerFilter);
		filter.put(holdingsFilter);
		filter.put(transactionFilter);
		filter.put(realisedPLFilter);
		filter.put(unRealisedPLFilter);
		filter.put(taxFilter);
		filter.put(saudaFilter);
		filter.put(contractNotesFilter);
		return filter;

	}

}
