package com.globecapital.business.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.ft.order.GetTradeBookObjectRow;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class TradeBook {
	
	private static Logger log = Logger.getLogger(TradeBook.class);

	private Map<String, JSONObject> mapAvgPriceTradeValue = new HashMap<String, JSONObject>();
	private Map<String, JSONArray> mapTradeSummary = new HashMap<String, JSONArray>();
	
	public TradeBook(List<GetTradeBookObjectRow> listTrade) throws Exception
	{
		parseTradeBook(listTrade);
	}
	
	public void parseTradeBook(List<GetTradeBookObjectRow> listTrade) throws Exception {
		/***
		 * Map contains key as Exchange order number and collection of trade book
		 * response object for exchange order number
		 ***/
		Map<String, Collection<GetTradeBookObjectRow>> mapOrderNoTrades = listToMap(listTrade);

		for (Entry<String, Collection<GetTradeBookObjectRow>> entry : mapOrderNoTrades.entrySet()) {
			Collection<GetTradeBookObjectRow> tradeCollection = entry.getValue();

			float fTradedValue = 0.0f;
			int iQty = 0;
			int precision = 0;

			JSONArray tradeSummaryArr = new JSONArray();

			int index = 1;

			for (GetTradeBookObjectRow obj : tradeCollection) {

				try
				{
				/*** Temporavary fix: For MCX segment quantity received from API instead of lot */
				String tempMktSegID = ExchangeSegment.getMarketSegmentID(obj.getExch(), obj.getInst());
				SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(obj.getScripCode(), tempMktSegID);
				String tradeQtyStr = OrderQty.formatToDevice(obj.getQty(), tempSymbolRow.getLotSizeInt(), tempMktSegID);	
				Integer tradeQtyInt = Integer.parseInt(tradeQtyStr);
				precision = tempSymbolRow.getPrecisionInt();
				
				/* ***************/




				/*** Trade value and average price calculation ***/
				iQty = iQty + tradeQtyInt;
				fTradedValue = fTradedValue + (Float.parseFloat(obj.getPrc()) * tradeQtyInt);

				/*** Trade Summary Details ***/
				JSONObject tradeSummaryObj = new JSONObject();
				tradeSummaryObj.put(OrderConstants.SHOW_ID, "true");
				tradeSummaryObj.put(OrderConstants.TRADE_ORD_ID, "Trade ID: " + obj.getTradeNo());
				tradeSummaryObj.put(OrderConstants.ORDER_TIME, obj.getTime());
				tradeSummaryObj.put(OrderConstants.DETAIL, String
						.format(InfoMessage.getInfoMSG("info_msg.trade_summary_detail"), tradeQtyStr, obj.getPrc()));
				tradeSummaryArr.put(index, tradeSummaryObj);
				index++;
				}
				catch(Exception e)
				{
					log.warn(e);
				}
			}

			/***
			 * Final average price and trade value for a exchange order number
			 ***/
			JSONObject tradeValue = new JSONObject();
			tradeValue.put(OrderConstants.TRADED_VALUE, PriceFormat.formatPrice(Float.toString(fTradedValue),
					precision, false));
			tradeValue.put(OrderConstants.AVG_PRICE, PriceFormat.formatPrice(Float.toString((fTradedValue / iQty)),
					precision, false));
			mapAvgPriceTradeValue.put(entry.getKey(), tradeValue);

			/*** Final trade summary details for a exchange order number ***/
			mapTradeSummary.put(entry.getKey(), tradeSummaryArr);
		}
	}
	
	private Map<String, Collection<GetTradeBookObjectRow>> listToMap(List<GetTradeBookObjectRow> lt) {
		Map<String, Collection<GetTradeBookObjectRow>> map = new HashMap<>();

		for (GetTradeBookObjectRow obj : lt) {
			String sKey = obj.getExOrderNo();

			if (map.containsKey(sKey)) {
				map.get(sKey).add(obj);
			} else {
				List<GetTradeBookObjectRow> list = new ArrayList<>();
				list.add(obj);
				map.put(sKey, list);
			}
		}

		return map;
	}

	
	public Map<String, JSONObject> getAvgPriceTradedValue()
	{
		return mapAvgPriceTradeValue;
	}
	
	public Map<String, JSONArray> getTradeSummary()
	{
		return mapTradeSummary;
	}
}
