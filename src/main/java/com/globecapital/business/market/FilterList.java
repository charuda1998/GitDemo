package com.globecapital.business.market;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;

public class FilterList {
	
	private static JSONArray allowedCategoryFIIDIIFilterTypes;
	private static JSONArray allowedResultsFilterTypes;
	private static JSONArray allowedIPOFilterTypes;
	private static JSONArray allowedCategoryOIFilterTypes;
	private static JSONArray allowedCategoryRolloverFilterTypes;
	private static JSONArray allowedInstrumentRolloverFilterTypes;
	private static JSONArray allowedInstrumentDerivativeFilterTypes;
	private static JSONArray allowedInstrumentCurrencyCommodityFilterTypes;
	private static JSONArray allowedCategoryCorporateActionsFilterTypes;
	private static JSONArray allowedDateCorporateActionsFilterTypes;
	private static JSONArray allowedExchangeFilterTypes;
	
	static {
		allowedCategoryFIIDIIFilterTypes = new JSONArray();
		allowedCategoryFIIDIIFilterTypes.put(DeviceConstants.ALL);
		allowedCategoryFIIDIIFilterTypes.put(DeviceConstants.FII_CASH);
		allowedCategoryFIIDIIFilterTypes.put(DeviceConstants.DII_CASH);
		allowedCategoryFIIDIIFilterTypes.put(DeviceConstants.FII_FUTURE);
		allowedCategoryFIIDIIFilterTypes.put(DeviceConstants.FII_OPTION);
	}
	
	static {
		allowedResultsFilterTypes = new JSONArray();
		allowedResultsFilterTypes.put(DeviceConstants.FILTER_TODAY);
		allowedResultsFilterTypes.put(DeviceConstants.FILTER_THIS_WEEK);
		allowedResultsFilterTypes.put(DeviceConstants.FILTER_LATER);
	}
	
	static {
		allowedIPOFilterTypes = new JSONArray();
		allowedIPOFilterTypes.put(DeviceConstants.UPCOMING);
		allowedIPOFilterTypes.put(DeviceConstants.ONGOING);
		allowedIPOFilterTypes.put(DeviceConstants.LISTED);
	}
	
	static {
		allowedCategoryOIFilterTypes = new JSONArray();
		allowedCategoryOIFilterTypes.put(DeviceConstants.LONG_UNWINDING);
		allowedCategoryOIFilterTypes.put(DeviceConstants.LONG_BUILDUP);
		allowedCategoryOIFilterTypes.put(DeviceConstants.SHORT_BUILDUP);
		allowedCategoryOIFilterTypes.put(DeviceConstants.SHORT_COVERING);
	}
	
	static {
		allowedCategoryRolloverFilterTypes = new JSONArray();
		allowedCategoryRolloverFilterTypes.put(DeviceConstants.HIGHEST);
		allowedCategoryRolloverFilterTypes.put(DeviceConstants.LOWEST);
	}
	
	static {
		allowedInstrumentRolloverFilterTypes = new JSONArray();
		allowedInstrumentRolloverFilterTypes.put(DeviceConstants.FILTER_INDEX);
		allowedInstrumentRolloverFilterTypes.put(DeviceConstants.FILTER_STOCK);
	}
	
	static {
		allowedInstrumentDerivativeFilterTypes = new JSONArray();		
		allowedInstrumentDerivativeFilterTypes.put(InstrumentType.STOCK_FUTURES);
		allowedInstrumentDerivativeFilterTypes.put(InstrumentType.INDEX_FUTURES);
		allowedInstrumentDerivativeFilterTypes.put(InstrumentType.STOCK_OPTIONS);
		allowedInstrumentDerivativeFilterTypes.put(InstrumentType.INDEX_OPTIONS);
	}
	
	static {
		allowedInstrumentCurrencyCommodityFilterTypes = new JSONArray();
		allowedInstrumentCurrencyCommodityFilterTypes.put(DeviceConstants.FILTER_FUTURES);
		allowedInstrumentCurrencyCommodityFilterTypes.put(DeviceConstants.FILTER_OPTIONS);
	}
	
	static {
		allowedCategoryCorporateActionsFilterTypes = new JSONArray();
		allowedCategoryCorporateActionsFilterTypes.put(DeviceConstants.ALL);
		allowedCategoryCorporateActionsFilterTypes.put(DeviceConstants.FILTER_DIVIDEND);
		allowedCategoryCorporateActionsFilterTypes.put(DeviceConstants.FILTER_BONUS);
		allowedCategoryCorporateActionsFilterTypes.put(DeviceConstants.FILTER_STOCK_SPLIT);
		allowedCategoryCorporateActionsFilterTypes.put(DeviceConstants.FILTER_RIGHTS);
		allowedCategoryCorporateActionsFilterTypes.put(DeviceConstants.FILTER_COMP_ANNOUNCEMENTS);
	}
	
	static {
		allowedDateCorporateActionsFilterTypes = new JSONArray();
		allowedDateCorporateActionsFilterTypes.put(DeviceConstants.FILTER_TODAY);
		allowedDateCorporateActionsFilterTypes.put(DeviceConstants.FILTER_THIS_WEEK);
		allowedDateCorporateActionsFilterTypes.put(DeviceConstants.FILTER_NEXT_WEEK);
	}
	
	static {
		allowedExchangeFilterTypes = new JSONArray();
		allowedExchangeFilterTypes.put(ExchangeSegment.NSE);
		allowedExchangeFilterTypes.put(ExchangeSegment.BSE);
		allowedExchangeFilterTypes.put(ExchangeSegment.MCX);
		allowedExchangeFilterTypes.put(ExchangeSegment.NCDEX);
		allowedExchangeFilterTypes.put(ExchangeSegment.NSECDS);
		allowedExchangeFilterTypes.put(ExchangeSegment.BSECDS);
	}
	
	public static JSONArray getEquityFilterList() {

		JSONArray filter = new JSONArray();
		
		JSONObject resultsFilter = new JSONObject();
		resultsFilter.put(DeviceConstants.TYPE, DeviceConstants.RESULTS);
		resultsFilter.put(DeviceConstants.DATE_FILTER, getDateFilterTypes(DeviceConstants.RESULTS) );
		
		JSONObject ipoFilter = new JSONObject();
		ipoFilter.put(DeviceConstants.TYPE, DeviceConstants.IPO);
		ipoFilter.put(DeviceConstants.CATEGORY_FILTER, getCategoryFilterTypes(DeviceConstants.IPO) );
		
		JSONObject corpActionsFilter = new JSONObject();
		corpActionsFilter.put(DeviceConstants.TYPE, DeviceConstants.CORP_ACTIONS);
		corpActionsFilter.put(DeviceConstants.CATEGORY_FILTER, 
				getCategoryFilterTypes(DeviceConstants.CORP_ACTIONS));
		corpActionsFilter.put(DeviceConstants.DATE_FILTER, getDateFilterTypes(DeviceConstants.CORP_ACTIONS));
		
		JSONObject fiiDiiFilter = new JSONObject();
		fiiDiiFilter.put(DeviceConstants.TYPE, DeviceConstants.FIIDII_DATA);
		fiiDiiFilter.put(DeviceConstants.CATEGORY_FILTER, 
				getCategoryFilterTypes(DeviceConstants.FIIDII_DATA));
		
		filter.put(resultsFilter);
		filter.put(ipoFilter);
		filter.put(corpActionsFilter);
		filter.put(fiiDiiFilter);
		
		
		return filter;
	}
	
	public static JSONArray getDerivativeFilterList() {

		JSONArray filter = new JSONArray();
		
		JSONObject oiAnalysisFilter = new JSONObject();
		oiAnalysisFilter.put(DeviceConstants.TYPE, DeviceConstants.OI_ANALYSIS);
		oiAnalysisFilter.put(DeviceConstants.CATEGORY_FILTER, getCategoryFilterTypes(DeviceConstants.OI_ANALYSIS) );
		
		JSONObject rolloverFilter = new JSONObject();
		rolloverFilter.put(DeviceConstants.TYPE, DeviceConstants.ROLLOVER_ANALYSIS);
		rolloverFilter.put(DeviceConstants.CATEGORY_FILTER, getCategoryFilterTypes(DeviceConstants.ROLLOVER_ANALYSIS));
		rolloverFilter.put(DeviceConstants.INSTRUMENT_FILTER, getInstrumentFilterTypes(DeviceConstants.ROLLOVER_ANALYSIS));
		
		JSONObject marketMoverFilter = new JSONObject();
		marketMoverFilter.put(DeviceConstants.TYPE, DeviceConstants.MARKET_MOVER);
		marketMoverFilter.put(DeviceConstants.INSTRUMENT_FILTER, getInstrumentFilterTypes(DeviceConstants.DERIVATIVE) );
		
		filter.put(oiAnalysisFilter);
		filter.put(rolloverFilter);
		filter.put(marketMoverFilter);
		
		return filter;
	}
	
	public static JSONArray getCurrencyFilterList() {

		JSONArray filter = new JSONArray();

		JSONObject marketMoverFilter = new JSONObject();
		marketMoverFilter.put(DeviceConstants.TYPE, DeviceConstants.MARKET_MOVER);
		marketMoverFilter.put(DeviceConstants.INSTRUMENT_FILTER, getInstrumentFilterTypes(DeviceConstants.CURRENCY) );
		
		filter.put(marketMoverFilter);
		
		return filter;
	}
	
	public static JSONArray getCommodityFilterList() {

		JSONArray filter = new JSONArray();

		JSONObject oiAnalysisFilter = new JSONObject();
		oiAnalysisFilter.put(DeviceConstants.TYPE, DeviceConstants.OI_ANALYSIS);
		oiAnalysisFilter.put(DeviceConstants.CATEGORY_FILTER, getCategoryFilterTypes(DeviceConstants.OI_ANALYSIS) );
		
		JSONObject marketMoverFilter = new JSONObject();
		marketMoverFilter.put(DeviceConstants.TYPE, DeviceConstants.MARKET_MOVER);
		marketMoverFilter.put(DeviceConstants.INSTRUMENT_FILTER, getInstrumentFilterTypes(DeviceConstants.COMMODITY) );
		
		filter.put(oiAnalysisFilter);
		filter.put(marketMoverFilter);
		return filter;
	}
	
	public static JSONObject getDateFilterTypes(String type) {
		
		JSONObject filterObj = new JSONObject();

		if (type.equalsIgnoreCase(DeviceConstants.RESULTS)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedResultsFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.FILTER_TODAY);
		} 	
		else if(type.equalsIgnoreCase(DeviceConstants.CORP_ACTIONS)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedDateCorporateActionsFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.FILTER_TODAY);
		}
			
		return filterObj;
	}
	
	
	public static JSONObject getCategoryFilterTypes(String type) {
		
		JSONObject filterObj = new JSONObject();
		
		if(type.equalsIgnoreCase(DeviceConstants.IPO)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedIPOFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.UPCOMING);
		} else if(type.equalsIgnoreCase(DeviceConstants.OI_ANALYSIS)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedCategoryOIFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.LONG_UNWINDING);
		} else if(type.equalsIgnoreCase(DeviceConstants.ROLLOVER_ANALYSIS)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedCategoryRolloverFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.HIGHEST);
		} else if(type.equalsIgnoreCase(DeviceConstants.CORP_ACTIONS)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedCategoryCorporateActionsFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.ALL);
		} else if(type.equalsIgnoreCase(DeviceConstants.FIIDII_DATA)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedCategoryFIIDIIFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.ALL);
		}
			
			
		return filterObj;
	}
	
	public static JSONObject getInstrumentFilterTypes(String type) {
		
		JSONObject filterObj = new JSONObject();
		
		if(type.equalsIgnoreCase(DeviceConstants.ROLLOVER_ANALYSIS)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedInstrumentRolloverFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.FILTER_INDEX);
		} else if(type.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedInstrumentDerivativeFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, InstrumentType.STOCK_FUTURES);
		} else if(type.equalsIgnoreCase(DeviceConstants.CURRENCY) || type.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			filterObj.put(DeviceConstants.FILTER_LIST, allowedInstrumentCurrencyCommodityFilterTypes);
			filterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.FILTER_FUTURES);
		}
		return filterObj;
	}
	
	public static JSONObject getExchangeList() {
		
		JSONObject filterObj = new JSONObject();
		return filterObj.put(DeviceConstants.EXCH_LIST, allowedExchangeFilterTypes);
		
	}

}
