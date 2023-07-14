package com.globecapital.business.order;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.ProductType;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class PositionsHelper {
	
	private static Logger log = Logger.getLogger(PositionsHelper.class);

	static int getQty(String sNetQty) {

		int iQty = Integer.parseInt(sNetQty);

		if (iQty < 0)
			return iQty * (-1);
		else
			return iQty;
	}

	static JSONObject getMarketValueAndPLUsingLTP(JSONArray positionsList, LinkedHashSet<String> linkedsetSymbolToken)
			throws SQLException {

		Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedsetSymbolToken);

		Double totalProfitLoss = 0.0, currentValue = 0.0, investmentAmt = 0.0, profitLossPer = 0.0;

		for (int i = 0; i < positionsList.length(); i++) {
			SymbolRow position = (SymbolRow) positionsList.getJSONObject(i);
			String sSymbolToken = (positionsList.getJSONObject(i)).getJSONObject(SymbolConstants.SYMBOL_OBJ)
					.getString(SymbolConstants.SYMBOL_TOKEN);

			if (mQuoteDetails.containsKey(sSymbolToken)) {

				QuoteDetails quoteDetails = mQuoteDetails.get(sSymbolToken);

				int precision = position.getPrecisionInt();

				position.put(DeviceConstants.LTP, PriceFormat.formatPrice(quoteDetails.sLTP, precision, false));
				position.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quoteDetails.sChange, precision, false));
				position.put(DeviceConstants.CHANGE_PERCENT,
						PriceFormat.formatPrice(quoteDetails.sChangePercent, 2, false));

				Double netQty = Double.parseDouble(position.getString(DeviceConstants.DISP_QTY));
				Double buyAvg = Double.parseDouble(position.getString(DeviceConstants.BUY_AVG).replaceAll(",", ""));
				Double sellAvg = Double.parseDouble(position.getString(DeviceConstants.SELL_AVG).replaceAll(",", ""));
				Double sellQty = Double.parseDouble(position.getString(DeviceConstants.SELL_QTY));
				Double buyQty = Double.parseDouble(position.getString(DeviceConstants.BUY_QTY));
				Double avgPrice = Double.parseDouble(position.getString(DeviceConstants.AVG_PRICE).replaceAll(",", ""));

				Double ltp = Double.parseDouble(quoteDetails.sLTP);

				//Double openValue = Double
				//		.parseDouble(position.getString(DeviceConstants.OPEN_VALUE).replaceAll(",", ""));
				Double openValue = netQty * avgPrice;
				openValue = formatValuesForDerivatives(openValue, position.getExchange(), position.getJSONObject(OrderConstants.NR_DR));
				investmentAmt = investmentAmt + openValue;

				String sOpenValue = PriceFormat.formatPrice(String.valueOf(openValue), position.getPrecisionInt(),false);
				position.put(DeviceConstants.OPEN_VALUE, sOpenValue);

				Double dMarketValue = netQty * ltp;
				dMarketValue = formatValuesForDerivatives(dMarketValue, position.getExchange(), position.getJSONObject(OrderConstants.NR_DR));

				currentValue = currentValue + dMarketValue;
				String sMarketValue = PriceFormat.formatPrice(String.valueOf(dMarketValue), position.getPrecisionInt(),
						false);
				position.put(DeviceConstants.MARKET_VALUE, sMarketValue);

				Double profitLoss = getProfitAndLoss(buyAvg, sellAvg, sellQty, buyQty, netQty, ltp, avgPrice, 
						position.getExchange(), position.getJSONObject(OrderConstants.NR_DR));
				position.remove(OrderConstants.NR_DR);
				totalProfitLoss = totalProfitLoss + profitLoss;
				position.put(DeviceConstants.PROFIT_AND_LOSS,
						PriceFormat.formatPrice(String.valueOf(profitLoss), position.getPrecisionInt(), false));

			} else {
				position.put(DeviceConstants.LTP, "--");
				position.put(DeviceConstants.CHANGE, "--");
				position.put(DeviceConstants.CHANGE_PERCENT, "--");
				position.put(DeviceConstants.MARKET_VALUE, "--");
				position.put(DeviceConstants.PROFIT_AND_LOSS, "--");
				position.put(DeviceConstants.OPEN_VALUE, "--");
			}

		}

		if (investmentAmt != 0)
			profitLossPer = ((currentValue - investmentAmt) / investmentAmt) * 100;

		JSONObject summaryObj = new JSONObject();
		summaryObj.put(DeviceConstants.TOTAL_PROFIT_AND_LOSS,
				PriceFormat.formatPrice(String.valueOf(totalProfitLoss), 2, false));
		summaryObj.put(DeviceConstants.CURRENT_VALUE, PriceFormat.formatPrice(String.valueOf(currentValue), 2, false));
		summaryObj.put(DeviceConstants.INVESTMENT_AMT,
				PriceFormat.formatPrice(String.valueOf(investmentAmt), 2, false));
		summaryObj.put(DeviceConstants.TOTAL_PROFIT_LOSS_PERCENTAGE,
				PriceFormat.formatPrice(String.valueOf(profitLossPer), 2, false) + "%");

		return summaryObj;

	}

	static String getConvertOrderAction(Double qty) {
		if (qty >= 0)
			return OrderAction.SELL;
		else
			return OrderAction.BUY;
	}

	static Object getOrderAction(Double qty) {
		if (qty >= 0)
			return OrderAction.BUY;
		else
			return OrderAction.SELL;
	}

	static Object isSellMore(Double qty) {
		if (qty >= 0)
			return DeviceConstants.FALSE;
		else
			return DeviceConstants.TRUE;
	}

	static Object isBuyMore(Double qty) {
		if (qty >= 0)
			return DeviceConstants.TRUE;
		else
			return DeviceConstants.FALSE;
	}

	static String isSquareOff(Double qty, String sProdType) {

		if (qty != 0 && (!sProdType.equalsIgnoreCase(
				ProductType.BRACKET_ORDER))) /*** Quantity will be always greater than zero in Equity Holdings ***/
			return DeviceConstants.TRUE;
		else
			return DeviceConstants.FALSE;

	}

	public static Double getProfitAndLoss(Double buyPrice, Double sellPrice, Double sellQty, Double buyQty, Double Qty,
			Double ltp, Double avgPrice, String sExchange, JSONObject objNRDR) {

		Double profitLoss = 0.0;
		
		float priceNR, priceDR, generalNR, generalDR;

		priceNR = Float.parseFloat(objNRDR.getString(OrderConstants.PRICE_NR));
		priceDR = Float.parseFloat(objNRDR.getString(OrderConstants.PRICE_DR));
		generalNR = Float.parseFloat(objNRDR.getString(OrderConstants.GENERAL_NR));
		generalDR = Float.parseFloat(objNRDR.getString(OrderConstants.GENERAL_DR));
		
		try 
		{
			if (buyQty != 0 && sellQty != 0 && Qty != 0) // both buy and sell position (partial)
			{
				if (sellQty < buyQty)
				{
					if(sExchange.equalsIgnoreCase(ExchangeSegment.MCX) 
						|| sExchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
						profitLoss = ( ( sellQty * sellPrice ) - ( sellQty * buyPrice ) ) 
							* (priceNR / priceDR) * (generalNR / generalDR)
							 + ( ( Qty *( ltp - buyPrice )) * (priceNR / priceDR) * (generalNR / generalDR));
					else
						profitLoss = ((sellPrice * sellQty) - (buyPrice * sellQty)) + (Qty * (ltp - buyPrice));
				}
				else
				{
					if(sExchange.equalsIgnoreCase(ExchangeSegment.MCX) 
						|| sExchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
						profitLoss = ( ( buyQty * sellPrice ) - ( buyQty * buyPrice ) )  
							* (priceNR / priceDR) * (generalNR / generalDR)
							+( ( Qty *( ltp - sellPrice )) * (priceNR / priceDR) * (generalNR / generalDR));
					else
						profitLoss = ((sellPrice * buyQty) - (buyPrice * buyQty)) + (Qty * (ltp - sellPrice));
				}
			} 
			else if (buyQty != 0 && sellQty != 0 && Qty == 0) // both buy and sell position
			{
				if(sExchange.equalsIgnoreCase(ExchangeSegment.MCX) 
					|| sExchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
					profitLoss = ( ( sellQty * sellPrice ) - ( buyQty * buyPrice ) ) 
						* (priceNR / priceDR) * (generalNR / generalDR);
				else
					profitLoss = (sellPrice * sellQty) - (buyPrice * buyQty);
			}
			else // only buy or sell position
			{
				if(sExchange.equalsIgnoreCase(ExchangeSegment.MCX) 
					|| sExchange.equalsIgnoreCase(ExchangeSegment.NSECDS))
					profitLoss = (Qty * (ltp - avgPrice)) * (priceNR / priceDR) * (generalNR / generalDR);
				else	
					profitLoss = Qty * (ltp - avgPrice);
			}

		}
		catch(Exception e)
		{
			log.error("Error while calculating Today/Derivative position PnL:" + e);
		}
		if (profitLoss == 0)
			return 0.0;

		return profitLoss;

	}

	static JSONObject getPriceNRDR(GetNetPositionRows positionRow) {
		
		JSONObject obj = new JSONObject();
		obj.put(OrderConstants.PRICE_NR, positionRow.getPriceNum());
		obj.put(OrderConstants.PRICE_DR, positionRow.getPriceDen());
		obj.put(OrderConstants.GENERAL_NR, positionRow.getGenNum());
		obj.put(OrderConstants.GENERAL_DR, positionRow.getGenDen());
		return obj;
	}
	
	static JSONObject getSymbolObj(String sISIN) {
		
		JSONObject symObj = null;
		
		if (SymbolMap.isValidSymbol(sISIN + "_" + ExchangeSegment.NSE_SEGMENT_ID))
			symObj = SymbolMap.getISINSymbolRow(sISIN + "_" + ExchangeSegment.NSE_SEGMENT_ID).getMinimisedSymbolRow();
		else if(SymbolMap.isValidSymbol(sISIN + "_" + ExchangeSegment.BSE_SEGMENT_ID))
			symObj = SymbolMap.getISINSymbolRow(sISIN + "_" + ExchangeSegment.BSE_SEGMENT_ID).getMinimisedSymbolRow();
		
		return symObj;
	}

	static Double formatValuesForDerivatives(Double value, String exchange, JSONObject priceNRDR)
	{		
		if(exchange.equalsIgnoreCase(ExchangeSegment.MCX))
		{
			float priceNR, priceDR, generalNR, generalDR;
			priceNR = Float.parseFloat(priceNRDR.getString(OrderConstants.PRICE_NR));
			priceDR = Float.parseFloat(priceNRDR.getString(OrderConstants.PRICE_DR));
			generalNR = Float.parseFloat(priceNRDR.getString(OrderConstants.GENERAL_NR));
			generalDR = Float.parseFloat(priceNRDR.getString(OrderConstants.GENERAL_DR));
			return (value) * ((priceNR / priceDR) * (generalNR / generalDR));

		}
		else
			return value;


	}
	
}