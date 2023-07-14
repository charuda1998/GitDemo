package com.globecapital.business.order;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.business.report.SortHelper;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class TodaysPosition_104 {

	private static Logger log = Logger.getLogger(TodaysPosition_104.class);
	
	public static JSONObject getPositions(List<GetNetPositionRows> positionRows, String sUserID,
			String sAppID, JSONObject filterObj) throws JSONException, GCException, SQLException, ParseException {

		JSONObject finalObj = new JSONObject();
		JSONArray positionsList = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		String optedSortBy = filterObj.getString(DeviceConstants.OPTED_SORT_BY);
		String optedSortOrder = filterObj.getString(DeviceConstants.OPTED_SORT_ORDER);
		JSONArray optedFilter = filterObj.getJSONArray(DeviceConstants.OPTED_FILTER);
		if(optedSortOrder.isEmpty() || optedSortOrder.equalsIgnoreCase(DeviceConstants.DEFAULT_ORDER))
			optedSortOrder = DeviceConstants.ASCENDING;
		if(optedSortBy.isEmpty())
			optedSortBy = DeviceConstants.ALPHABETICALLY;
		for (int i = 0; i < positionRows.size(); i++) {

			try {
				SymbolRow positions = new SymbolRow();

				GetNetPositionRows positionRow = positionRows.get(i);
				String tokenSegment = positionRow.getToken() + "_" + positionRow.getSegmentId();

				/*** Position price info ***/
				String segment = positionRow.getSegmentId();
				SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(tokenSegment);

				if (tempSymbolRow == null)
					continue;

				/* TODO: Temporavary fix to avoid changes in muliple places */
				String sNetQty = OrderQty.formatToDevice(positionRow.getNetQty(), tempSymbolRow.getLotSizeInt(),
						tempSymbolRow.getMktSegId());
				String sBuyQty = OrderQty.formatToDevice(positionRow.getBuyQty(), tempSymbolRow.getLotSizeInt(),
						tempSymbolRow.getMktSegId());
				String sSellQty = OrderQty.formatToDevice(positionRow.getSellQty(), tempSymbolRow.getLotSizeInt(),
						tempSymbolRow.getMktSegId());

				String sAvgNetPrc = positionRow.getAvgNetPrc();
				int lotsize = tempSymbolRow.getLotSizeInt();

				// OrderPrice.formatPriceToDevice(positionRow.getAvgNetPrc(), 1);
				// segment.equals(ExchangeSegment.MCX_SEGMENT_ID) ?
				// tempSymbolRow.getMultiplier() : 1);
				String sAvgBuyPrc = positionRow.getAvgBuyPrc(); // OrderPrice.formatPriceToDevice(positionRow.getAvgBuyPrc(),
																// 1);
				// segment.equals(ExchangeSegment.MCX_SEGMENT_ID) ?
				// tempSymbolRow.getMultiplier() : 1);
				String sAvgSellPrc = positionRow.getAvgSellPrc(); // OrderPrice.formatPriceToDevice(positionRow.getAvgSellPrc(),
																	// 1);
				// segment.equals(ExchangeSegment.MCX_SEGMENT_ID) ?
				// tempSymbolRow.getMultiplier() : 1);
				String sBuyValue = positionRow.getBuyValue(); // OrderPrice.formatPriceToDevice(positionRow.getBuyValue(),
																// 1);
				// segment.equals(ExchangeSegment.MCX_SEGMENT_ID) ?
				// tempSymbolRow.getMultiplier() : 1);
				String sSellValue = positionRow.getSellValue(); // OrderPrice.formatPriceToDevice(positionRow.getSellValue(),
																// 1);
				// segment.equals(ExchangeSegment.MCX_SEGMENT_ID) ?
				// tempSymbolRow.getMultiplier() : 1);

				/************************************************** */
				positions.extend(tempSymbolRow.getMinimisedSymbolRow());
				positions.put(DeviceConstants.INSTRUMENT, tempSymbolRow.getInstrument());
				positions.put(DeviceConstants.EXCH, tempSymbolRow.getExchange());
				//positions.put(DeviceConstants.IS_STREAMING_REQUIRED, String.valueOf(PositionsHelper_101.isStreamingRequired(tempSymbolRow.getExchange())));
				
				linkedsetSymbolToken.add(tokenSegment);

				// TODO: Profit and Loss calculation
				Double netQty = Double.parseDouble(sNetQty);
				Double avgNetPrice = Double.parseDouble(sAvgNetPrc);

				int precision = positions.getPrecisionInt();

				positions.put(OrderConstants.AVG_PRICE, PriceFormat.formatPrice(sAvgNetPrc, precision, false));
				if(positionRow.getExch().equals(ExchangeSegment.NSECDS)) {
					positions.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(Integer.parseInt(sNetQty) / lotsize));
					positions.put(DeviceConstants.BUY_QTY, Integer.toString(Integer.parseInt(sBuyQty) / lotsize));
					positions.put(OrderConstants.QTY, Integer.toString(PositionsHelper_101.getQty(sNetQty) / lotsize));
					positions.put(DeviceConstants.SELL_QTY, Integer.toString(Integer.parseInt(sSellQty) / lotsize));
				}else {
					positions.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(Integer.parseInt(sNetQty)));
					positions.put(DeviceConstants.BUY_QTY, sBuyQty);
					positions.put(OrderConstants.QTY, Integer.toString(PositionsHelper_101.getQty(sNetQty)));
					positions.put(DeviceConstants.SELL_QTY, sSellQty);
				}
				positions.put(DeviceConstants.BOD_QTY, "--");
				positions.put(DeviceConstants.BOD_RATE, "--");
				positions.put(DeviceConstants.BOD_VALUE, "--");
				positions.put(DeviceConstants.BUY_AVG, PriceFormat.formatPrice(sAvgBuyPrc, precision, false));
				positions.put(DeviceConstants.BUY_VALUE, PriceFormat.formatPrice(sBuyValue, precision, false));
				positions.put(DeviceConstants.SELL_AVG, PriceFormat.formatPrice(sAvgSellPrc, precision, false));
				positions.put(DeviceConstants.SELL_VALUE, PriceFormat.formatPrice(sSellValue, precision, false));
				String sProductType = ProductType.formatToDisplay2(positionRow.getProdType(), segment);
				positions.put(OrderConstants.PRODUCT_TYPE, sProductType);
				positions.put(OrderConstants.DISP_PRODUCT_TYPE, sProductType);
				//positions.put(DeviceConstants.OPEN_VALUE,
				//		PriceFormat.formatPrice(String.valueOf(netQty * avgNetPrice), precision, false));
				positions.put(DeviceConstants.CONVERTABLE_TYPES,
						ProductType.getConvertableProductTypes(positionRow.getProdType(), segment));
				positions.put(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
				positions.put(OrderConstants.VALIDITY, Validity.DAY); // For Square-off and Buy more navigation
				positions.put(OrderConstants.DISC_QTY, "--");

				positions.put(DeviceConstants.IS_SQUARE_OFF,
						PositionsHelper_101.isSquareOff(netQty, positionRow.getProdType()));
				positions.put(DeviceConstants.IS_CONVERT, PositionsHelper_101.isSquareOff(netQty, positionRow.getProdType()));
				positions.put(DeviceConstants.IS_BUY_MORE, PositionsHelper_101.isBuyMore(netQty));
				positions.put(DeviceConstants.IS_SELL_MORE, PositionsHelper_101.isSellMore(netQty));
				positions.put(OrderConstants.ORDER_ACTION, PositionsHelper_101.getOrderAction(netQty));
				positions.put(OrderConstants.TO_CONVERT_ACTION, PositionsHelper_101.getConvertOrderAction(netQty));
				
				positions.put(OrderConstants.NR_DR, PositionsHelper_101.getPriceNRDR(positionRow));
				positionsList.put(positions);
				
			} catch (Exception e) {

				log.error(e);
			}
		} // End of for loop

		int recordsCount = positionsList.length();
		PositionsHelper_101.getMarketValueAndPLUsingLTP_101(positionsList, linkedsetSymbolToken);
		positionsList = sort(filterPositions(positionsList, optedFilter), optedSortOrder, optedSortBy);
		finalObj.put(DeviceConstants.POSITION_LIST, positionsList);
		JSONObject summaryObj = PositionsHelper_101.makeSummaryObjectForPositions(positionsList);
		summaryObj.put(DeviceConstants.RECORDS_COUNT, String.valueOf(recordsCount));
		finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);

		return finalObj;

	}
	
	public static JSONArray sort(JSONArray positionsList, final String sortOrder, String sortBy) {

        JSONArray sortedArray = new JSONArray();

        if (sortBy.contains(DeviceConstants.PROFIT_LOSS_PERCENTAGE)) {
        	
        	List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < positionsList.length(); i++) {
				if(!(positionsList.getJSONObject(i).getString(DeviceConstants.PNL_PERCENT).equals("--") || positionsList.getJSONObject(i).getString(DeviceConstants.PNL_PERCENT).equals("NA"))) 
					toBeSorted.add(positionsList.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.PNL_PERCENT, toBeSorted,"[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sortedArray = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sortedArray = new JSONArray(toBeSorted);	
			}
			return sortedArray;

        }else if (sortBy.contains(DeviceConstants.PROFIT_LOSS_ABSOLUTE)) {
        	
        	List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < positionsList.length(); i++) {
				if(!(positionsList.getJSONObject(i).getString(DeviceConstants.PROFIT_AND_LOSS).equals("--") || positionsList.getJSONObject(i).getString(DeviceConstants.PROFIT_AND_LOSS).equals("NA"))) 
					toBeSorted.add(positionsList.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.PROFIT_AND_LOSS, toBeSorted,"[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sortedArray = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sortedArray = new JSONArray(toBeSorted);	
			}
			return sortedArray;

        }
        else if (sortBy.equalsIgnoreCase(DeviceConstants.QUANTITY))
            return SortHelper.sortByInteger(positionsList, sortOrder, DeviceConstants.DISP_QTY, ",");

        else if (sortBy.contains(DeviceConstants.ALPHABETICALLY) || sortBy.isEmpty()) {
				List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
                for (int i = 0; i < positionsList.length(); i++)
                    toBeSorted.add(positionsList.getJSONObject(i));
                Collections.sort(toBeSorted, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject obj1, JSONObject obj2) {
                        if (sortOrder.equalsIgnoreCase(DeviceConstants.ASCENDING))
                            return obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
                                    .compareTo(obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ)
                                            .getString(DeviceConstants.SYMBOL));
                        else
                            return obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
                                    .compareTo(obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ)
                                            .getString(DeviceConstants.SYMBOL));
                    }
                });
                sortedArray = new JSONArray(toBeSorted);
                return sortedArray;
		} else {
            return positionsList;
        }
    }
	
	public static JSONArray filterPositions(JSONArray positionsList, JSONArray filterArray) {
		
		List<String> filterItems = new ArrayList<>();
		for(int i = 0; i < filterArray.length() ; i++ )
			filterItems.add(filterArray.getString(i));
		
        if(filterItems.isEmpty() || (filterItems.contains(DeviceConstants.FILTER_EQUITIES) && filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY)))
        	return positionsList;
        else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY))
        	return getFilteredHoldings(true,true,true,true,false,positionsList);
        else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
        	return getFilteredHoldings(true,true,true,false,true,positionsList);
        else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.FILTER_EQUITIES) && filterItems.contains(DeviceConstants.COMMODITY))
        	return getFilteredHoldings(true,true,false,true,true,positionsList);
        else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
        	return getFilteredHoldings(true,false,true,true,true,positionsList);
        else if (filterItems.contains(DeviceConstants.FILTER_EQUITIES) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY))
        	return getFilteredHoldings(false,true,true,true,true,positionsList);

        else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY))
        	return getFilteredHoldings(true,true,true,false,false,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(true,true,false,true,false,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(true,true,false,false,true,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.COMMODITY) && filterItems.contains(DeviceConstants.CURRENCY))
			return getFilteredHoldings(true,false,true,true,false,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(true,false,true,false,true,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.COMMODITY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(true,false,false,true,true,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false,true,true,true,false,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(false,true,true,false,true,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.COMMODITY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(false,true,false,true,true,positionsList);
		else if (filterItems.contains(DeviceConstants.FILTER_EQUITIES) && filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false,false,true,true,true,positionsList);
		else if (filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.FUTURE))
			return getFilteredHoldings(true,false,true,false,false,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(true,false,false,true,false,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(true,false,false,false,true,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.FUTURE))
			return getFilteredHoldings(true,true,false,false,false,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.CURRENCY))
			return getFilteredHoldings(false,true,true,false,false,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false,true,false,true,false,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(false,true,false,false,true,positionsList);
		else if (filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false,false,true,true,false,positionsList);
		else if (filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(false,false,true,false,true,positionsList);
		else if (filterItems.contains(DeviceConstants.COMMODITY) && filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(false,false,false,true,true,positionsList);
		else if (filterItems.contains(DeviceConstants.CURRENCY))
			return getFilteredHoldings(false,false,true,false,false,positionsList);
		else if (filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false,false,false,true,false,positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE))
			return getFilteredHoldings(true,false,false,false,false,positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS))
			return getFilteredHoldings(false,true,false,false,false,positionsList);
		else if (filterItems.contains(DeviceConstants.FILTER_EQUITIES))
			return getFilteredHoldings(false,false,false,false,true,positionsList);
        return positionsList;
    }

	private static JSONArray getFilteredHoldings(boolean showFNOFuture, boolean showFNOOption, boolean showCurrency, boolean showCommodity, boolean showEquities, JSONArray positionsList) {
		JSONArray finalArray = new JSONArray();
		for(int i = 0; i < positionsList.length(); i++) {
			JSONObject obj = positionsList.getJSONObject(i);
			String instrument = obj.getString(DeviceConstants.INSTRUMENT);
			String exch = obj.getString(DeviceConstants.EXCH);
			obj.remove(DeviceConstants.INSTRUMENT);
			obj.remove(DeviceConstants.EXCH);
			if(showFNOFuture && exch.equals(ExchangeSegment.NFO) && InstrumentType.isFutures(instrument)) {
				finalArray.put(obj);
			}
			else if(showFNOOption && exch.equals(ExchangeSegment.NFO) && InstrumentType.isOptions(instrument)) {
				finalArray.put(obj);
			}
			else if(showCurrency && exch.equals(ExchangeSegment.NSECDS)) {
				finalArray.put(obj);
			}
			else if(showCommodity && exch.equals(ExchangeSegment.MCX)) {
				finalArray.put(obj);
			}
			else if(showEquities && (exch.equals(ExchangeSegment.NSE) || exch.equals(ExchangeSegment.BSE))) {
				finalArray.put(obj);
			}
				
		}
		return finalArray;
	}

}