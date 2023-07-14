package com.globecapital.business.report;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;

public class AdvancedFilterList_102 {

	public static JSONArray getFilterList() throws JSONException, ParseException {

		JSONArray filter = new JSONArray();
		JSONObject filterObj= new JSONObject();
		
		JSONObject ledgerEQFilter = new JSONObject();
		ledgerEQFilter.put(DeviceConstants.TYPE, DeviceConstants.LEDGER_EQ);
		ledgerEQFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.LEDGER_EQ, filterObj));
		
		JSONObject ledgerCommodityFilter = new JSONObject();
		ledgerCommodityFilter.put(DeviceConstants.TYPE, DeviceConstants.LEDGER_COMMODITY);
		ledgerCommodityFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.LEDGER_COMMODITY, filterObj));

		JSONObject holdingsFilter = new JSONObject();
		holdingsFilter.put(DeviceConstants.TYPE, DeviceConstants.HOLDINGS);
		holdingsFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.HOLDINGS, filterObj));

		JSONObject transactionEqFilter = new JSONObject();
		transactionEqFilter.put(DeviceConstants.TYPE, DeviceConstants.TRANSACTION_EQUITY);
		transactionEqFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.TRANSACTION_EQUITY, filterObj));
		
		JSONObject transactionDerivativeFilter = new JSONObject();
		transactionDerivativeFilter.put(DeviceConstants.TYPE, DeviceConstants.TRANSACTION_DERIVATIVE);
		transactionDerivativeFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.TRANSACTION_DERIVATIVE, filterObj));
		
		JSONObject transactionCurrencyFilter = new JSONObject();
		transactionCurrencyFilter.put(DeviceConstants.TYPE, DeviceConstants.TRANSACTION_CURRENCY);
		transactionCurrencyFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.TRANSACTION_CURRENCY, filterObj));
		
		JSONObject transactionCommodityFilter = new JSONObject();
		transactionCommodityFilter.put(DeviceConstants.TYPE, DeviceConstants.TRANSACTION_COMMODITY);
		transactionCommodityFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.TRANSACTION_COMMODITY, filterObj));

		JSONObject realisedPLEqFilter = new JSONObject();
		realisedPLEqFilter.put(DeviceConstants.TYPE, DeviceConstants.REALISED_PROFIT_LOSS_EQUITY);
		realisedPLEqFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_EQUITY, filterObj));
		
		JSONObject realisedPLDerivativeFilter = new JSONObject();
		realisedPLDerivativeFilter.put(DeviceConstants.TYPE, DeviceConstants.REALISED_PROFIT_LOSS_DERIVATIVE);
		realisedPLDerivativeFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_DERIVATIVE, filterObj));
		
		JSONObject realisedPLCurrencyFilter = new JSONObject();
		realisedPLCurrencyFilter.put(DeviceConstants.TYPE, DeviceConstants.REALISED_PROFIT_LOSS_CURRENCY);
		realisedPLCurrencyFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_CURRENCY, filterObj));
		
		JSONObject realisedPLCommodityFilter = new JSONObject();
		realisedPLCommodityFilter.put(DeviceConstants.TYPE, DeviceConstants.REALISED_PROFIT_LOSS_COMMODITY);
		realisedPLCommodityFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.REALISED_PROFIT_LOSS_COMMODITY, filterObj));

	
		JSONObject unRealisedPLCurrencyFilter = new JSONObject();
		unRealisedPLCurrencyFilter.put(DeviceConstants.TYPE, DeviceConstants.UNREALISED_PROFIT_LOSS_CUR);
		unRealisedPLCurrencyFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.UNREALISED_PROFIT_LOSS_CUR, filterObj));
		
		JSONObject unRealisedPLCommodityFilter = new JSONObject();
		unRealisedPLCommodityFilter.put(DeviceConstants.TYPE, DeviceConstants.UNREALISED_PROFIT_LOSS_COM);
		unRealisedPLCommodityFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.UNREALISED_PROFIT_LOSS_COM, filterObj));
		
		JSONObject unRealisedPLEqFilter = new JSONObject();
		unRealisedPLEqFilter.put(DeviceConstants.TYPE, DeviceConstants.UNREALISED_PROFIT_LOSS_EQU);
		unRealisedPLEqFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.UNREALISED_PROFIT_LOSS_EQU, filterObj));
		
		JSONObject unRealisedPLDerivativeFilter = new JSONObject();
		unRealisedPLDerivativeFilter.put(DeviceConstants.TYPE, DeviceConstants.UNREALISED_PROFIT_LOSS_DER);
		unRealisedPLDerivativeFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.UNREALISED_PROFIT_LOSS_DER, filterObj));
		
		JSONObject taxDerivativeFilter = new JSONObject();
		taxDerivativeFilter.put(DeviceConstants.TYPE, DeviceConstants.TAX_DERIVATIVE);
		taxDerivativeFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.TAX_DERIVATIVE, filterObj));
		
		JSONObject taxCurrencyFilter = new JSONObject();
		taxCurrencyFilter.put(DeviceConstants.TYPE, DeviceConstants.TAX_CURRENCY);
		taxCurrencyFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.TAX_CURRENCY, filterObj));
		
		JSONObject taxCommodityFilter = new JSONObject();
		taxCommodityFilter.put(DeviceConstants.TYPE, DeviceConstants.TAX_COMMODITY);
		taxCommodityFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.TAX_COMMODITY, filterObj));
		
		JSONObject taxEquityFilter = new JSONObject();
		taxEquityFilter.put(DeviceConstants.TYPE, DeviceConstants.TAX_EQUITY);
		taxEquityFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.TAX_EQUITY, filterObj));

		JSONObject saudaEqFilter = new JSONObject();
		saudaEqFilter.put(DeviceConstants.TYPE, DeviceConstants.SAUDA_BILL_EQUITY);
		saudaEqFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.SAUDA_BILL_EQUITY, filterObj));
		
		JSONObject saudaDerivativeFilter = new JSONObject();
		saudaDerivativeFilter.put(DeviceConstants.TYPE, DeviceConstants.SAUDA_BILL_DERIVATIVE);
		saudaDerivativeFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.SAUDA_BILL_DERIVATIVE, filterObj));
		
		JSONObject saudaCurrencyFilter = new JSONObject();
		saudaCurrencyFilter.put(DeviceConstants.TYPE, DeviceConstants.SAUDA_BILL_CURRENCY);
		saudaCurrencyFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.SAUDA_BILL_CURRENCY, filterObj));
		
		JSONObject saudaCommodityFilter = new JSONObject();
		saudaCommodityFilter.put(DeviceConstants.TYPE, DeviceConstants.SAUDA_BILL_COMMODITY);
		saudaCommodityFilter.put(DeviceConstants.ADVANCED_FILTER, FilterList_102.getFilterTypes(DeviceConstants.SAUDA_BILL_COMMODITY, filterObj));

		JSONObject contractNotesEqFilter = new JSONObject();
		contractNotesEqFilter.put(DeviceConstants.TYPE, DeviceConstants.CONTRACT_NOTES_EQ);
		contractNotesEqFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.CONTRACT_NOTES_EQ, filterObj));
		
		JSONObject contractNotesCommodityFilter = new JSONObject();
		contractNotesCommodityFilter.put(DeviceConstants.TYPE, DeviceConstants.CONTRACT_NOTES_COMMODITY);
		contractNotesCommodityFilter.put(DeviceConstants.ADVANCED_FILTER,
				FilterList_102.getFilterTypes(DeviceConstants.CONTRACT_NOTES_COMMODITY, filterObj));
		
		filter.put(ledgerEQFilter);
		filter.put(ledgerCommodityFilter);
		filter.put(holdingsFilter);
		filter.put(transactionEqFilter);
		filter.put(transactionDerivativeFilter);
		filter.put(transactionCurrencyFilter);
		filter.put(transactionCommodityFilter);
		filter.put(realisedPLEqFilter);
		filter.put(realisedPLDerivativeFilter);
		filter.put(realisedPLCurrencyFilter);
		filter.put(realisedPLCommodityFilter);
		filter.put(unRealisedPLEqFilter);
		filter.put(unRealisedPLDerivativeFilter);
		filter.put(unRealisedPLCurrencyFilter);
		filter.put(unRealisedPLCommodityFilter);
		filter.put(taxDerivativeFilter);
		filter.put(taxEquityFilter);
		filter.put(taxCurrencyFilter);
		filter.put(taxCommodityFilter);
		filter.put(saudaEqFilter);
		filter.put(saudaDerivativeFilter);
		filter.put(saudaCurrencyFilter);
		filter.put(saudaCommodityFilter);
		filter.put(contractNotesEqFilter);
		filter.put(contractNotesCommodityFilter);
		return filter;

	}

}
