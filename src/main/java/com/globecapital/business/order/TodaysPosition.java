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
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class TodaysPosition {

	private static Logger log = Logger.getLogger(TodaysPosition.class);

	public static JSONObject getPositions(List<GetNetPositionRows> positionRows, String sUserID,
			String sAppID) throws JSONException, GCException, SQLException, ParseException {

		JSONObject finalObj = new JSONObject();
		JSONArray positionsList = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();

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
				linkedsetSymbolToken.add(tokenSegment);

				// TODO: Profit and Loss calculation
				Double netQty = Double.parseDouble(sNetQty);
				Double avgNetPrice = Double.parseDouble(sAvgNetPrc);

				int precision = positions.getPrecisionInt();

				positions.put(OrderConstants.AVG_PRICE, PriceFormat.formatPrice(sAvgNetPrc, precision, false));
				positions.put(DeviceConstants.DISP_QTY, sNetQty);
				positions.put(OrderConstants.QTY, Integer.toString(PositionsHelper.getQty(sNetQty)));
				positions.put(DeviceConstants.BOD_QTY, "--");
				positions.put(DeviceConstants.BOD_RATE, "--");
				positions.put(DeviceConstants.BOD_VALUE, "--");
				positions.put(DeviceConstants.BUY_QTY, sBuyQty);
				positions.put(DeviceConstants.BUY_AVG, PriceFormat.formatPrice(sAvgBuyPrc, precision, false));
				positions.put(DeviceConstants.BUY_VALUE, PriceFormat.formatPrice(sBuyValue, precision, false));
				positions.put(DeviceConstants.SELL_QTY, sSellQty);
				positions.put(DeviceConstants.SELL_AVG, PriceFormat.formatPrice(sAvgSellPrc, precision, false));
				positions.put(DeviceConstants.SELL_VALUE, PriceFormat.formatPrice(sSellValue, precision, false));
				positions.put(OrderConstants.PRODUCT_TYPE,
						ProductType.formatToDisplay2(positionRow.getProdType(), segment));
				//positions.put(DeviceConstants.OPEN_VALUE,
				//		PriceFormat.formatPrice(String.valueOf(netQty * avgNetPrice), precision, false));
				positions.put(DeviceConstants.CONVERTABLE_TYPES,
						ProductType.getConvertableProductTypes(positionRow.getProdType(), segment));
				positions.put(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
				positions.put(OrderConstants.VALIDITY, Validity.DAY); // For Square-off and Buy more navigation
				positions.put(OrderConstants.DISC_QTY, "--");

				positions.put(DeviceConstants.IS_SQUARE_OFF,
						PositionsHelper.isSquareOff(netQty, positionRow.getProdType()));
				positions.put(DeviceConstants.IS_CONVERT, PositionsHelper.isSquareOff(netQty, positionRow.getProdType()));
				positions.put(DeviceConstants.IS_BUY_MORE, PositionsHelper.isBuyMore(netQty));
				positions.put(DeviceConstants.IS_SELL_MORE, PositionsHelper.isSellMore(netQty));
				positions.put(OrderConstants.ORDER_ACTION, PositionsHelper.getOrderAction(netQty));
				positions.put(OrderConstants.TO_CONVERT_ACTION, PositionsHelper.getConvertOrderAction(netQty));
				
				positions.put(OrderConstants.NR_DR, PositionsHelper.getPriceNRDR(positionRow));

				positionsList.put(positions);
				
			} catch (Exception e) {

				log.error(e);
			}
		} // End of for loop

		

		if (positionsList.length() <= 0)
			throw new GCException(InfoIDConstants.NO_DATA);

		JSONObject summaryObj = PositionsHelper.getMarketValueAndPLUsingLTP(positionsList, linkedsetSymbolToken);
		finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);
//		finalObj.put(DeviceConstants.POSITION_LIST, positionsList);
		finalObj.put(DeviceConstants.POSITION_LIST, sortBySymbol(positionsList));

		return finalObj;

	}
	private static JSONArray sortBySymbol(JSONArray positionsList) {
		JSONArray sortedArray=null;
		 List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
         for (int i = 0; i < positionsList.length(); i++)
             toBeSorted.add(positionsList.getJSONObject(i));
         Collections.sort(toBeSorted, new Comparator<JSONObject>() {
             @Override
             public int compare(JSONObject obj1, JSONObject obj2) {
                    return obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
                             .compareTo(obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ)
                                     .getString(DeviceConstants.SYMBOL));                
             }
         });
         sortedArray = new JSONArray(toBeSorted);
         return sortedArray;
             
		
	}

}