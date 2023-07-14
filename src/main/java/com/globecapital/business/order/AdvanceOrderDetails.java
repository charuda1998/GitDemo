package com.globecapital.business.order;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.ComputeBracketOrderRangeAPI;
import com.globecapital.api.ft.order.ComputeBracketOrderRangeObject;
import com.globecapital.api.ft.order.ComputeBracketOrderRangeRequest;
import com.globecapital.api.ft.order.ComputeBracketOrderRangeResponse;
import com.globecapital.api.ft.order.GetGTDOrderBookResponse;
import com.globecapital.business.marketdata.MarketMovers_101;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderPrice;
import com.globecapital.constants.order.OrderType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;

public class AdvanceOrderDetails {
	
	public static JSONObject getBracketOrderDetails(Session session, String sSymbolToken, 
			String sOrderAction, JSONObject brackerOrderDetails, ServletContext servletContext,
            GCRequest gcRequest, GCResponse gcResponse) throws JSONException, Exception
	{
		JSONObject advanceOrderDetails = new JSONObject();
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		
		JSONObject quoteObj = Quote.getQuote(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		
		ComputeBracketOrderRangeRequest bracketOrderRangeReq
					= new ComputeBracketOrderRangeRequest();
		
		bracketOrderRangeReq.setUserID(session.getUserID());
		bracketOrderRangeReq.setGroupId(session.getGroupId());
		bracketOrderRangeReq.setJKey(session.getjKey());
		bracketOrderRangeReq.setJSession(session.getjSessionID());
		bracketOrderRangeReq.setToken(Integer.parseInt(symRow.gettokenId()));
		bracketOrderRangeReq.setMktSegId(Integer.parseInt(symRow.getMktSegId()));
		bracketOrderRangeReq.setInstName(symRow.getInstrument());
		bracketOrderRangeReq.setSymbol(symRow.getSymbol());
		bracketOrderRangeReq.setBuySell(OrderAction.formatToAPI(sOrderAction));
		bracketOrderRangeReq.setStrikePrc(symRow.getStrikePrice());
		bracketOrderRangeReq.setExpiryDate(symRow.getExpiry());
	
		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		
		if(quoteObj.length() != 0)
		{
			bracketOrderRangeReq.setLTP(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					Float.toString(quoteObj.getFloat(FTConstants.LTP)), symRow.getMultiplier(), quoteDetails.sLTP));
			bracketOrderRangeReq.setClosePrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					Float.toString(quoteObj.getFloat(FTConstants.CLOSE_PRICE)), symRow.getMultiplier(), quoteDetails.sLTP));
			bracketOrderRangeReq.setHighPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					Float.toString(quoteObj.getFloat(FTConstants.HIGH_PRICE)), symRow.getMultiplier(), quoteDetails.sLTP));
			bracketOrderRangeReq.setLowPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					Float.toString(quoteObj.getFloat(FTConstants.LOW_PRICE)), symRow.getMultiplier(), quoteDetails.sLTP));
		}
		bracketOrderRangeReq.setBasePrice(Float.parseFloat(symRow.getBasePrice()) * symRow.getMultiplier());
		if( ! brackerOrderDetails.getString(OrderConstants.PRICE).isEmpty())
		{
			bracketOrderRangeReq.setMainOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					brackerOrderDetails.getString(OrderConstants.PRICE), symRow.getMultiplier(), quoteDetails.sLTP));
		}
		
		if( ! brackerOrderDetails.getString(OrderConstants.SL_PRICE).isEmpty())
		{
			bracketOrderRangeReq.setSLOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2,
					brackerOrderDetails.getString(OrderConstants.SL_PRICE), symRow.getMultiplier(), quoteDetails.sLTP));
		}
		
		if( ! brackerOrderDetails.getString(OrderConstants.SL_TRIG_PRICE).isEmpty())
		{
			bracketOrderRangeReq.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2,
					brackerOrderDetails.getString(OrderConstants.SL_TRIG_PRICE), symRow.getMultiplier(), quoteDetails.sLTP));
		}
		
		ComputeBracketOrderRangeAPI brackOrderRangeAPI = new ComputeBracketOrderRangeAPI();
		
		ComputeBracketOrderRangeResponse brackOrderRangeRes = new ComputeBracketOrderRangeResponse();
		try {
		    brackOrderRangeRes = brackOrderRangeAPI.post(bracketOrderRangeReq, ComputeBracketOrderRangeResponse.class, session.getAppID()
						,"ComputeBracketOrderRange");
    	}catch(GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(bracketOrderRangeReq,session, servletContext, gcRequest, gcResponse)) {
                        brackOrderRangeRes = brackOrderRangeAPI.post(bracketOrderRangeReq, ComputeBracketOrderRangeResponse.class, session.getAppID()
                                ,"ComputeBracketOrderRange");
                        session = gcRequest.getSession();
                    } else {
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                    }
            }else 
                throw new RequestFailedException();
        }
		
		ComputeBracketOrderRangeObject bracketOrderRangeObj = brackOrderRangeRes.getResponseObject();
		
		if(bracketOrderRangeObj.getStatus().equals("0")) {
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, bracketOrderRangeObj.getDesc());}
		else
		{
			advanceOrderDetails.put(OrderConstants.PROFIT_PRICE, getProfitPrice(bracketOrderRangeObj));
			advanceOrderDetails.put(OrderConstants.SL_PRICE, getStoplossPrice(bracketOrderRangeObj));
			advanceOrderDetails.put(OrderConstants.SL_TRIG_PRICE, getStoplossTriggerPrice(bracketOrderRangeObj));
			
		}
		
		return advanceOrderDetails;
		
	}

	private static JSONObject getProfitPrice(ComputeBracketOrderRangeObject bracketOrderRangeObj) {
		
		JSONObject profitPriceObj = new JSONObject();
		profitPriceObj.put(OrderConstants.HIGH, bracketOrderRangeObj.getProfitPriceHigh());
		profitPriceObj.put(OrderConstants.LOW, bracketOrderRangeObj.getProfitPriceLow());
		return profitPriceObj;
		
	}
	
	private static JSONObject getStoplossPrice(ComputeBracketOrderRangeObject bracketOrderRangeObj) {
		
		JSONObject slPriceObj = new JSONObject();
		slPriceObj.put(OrderConstants.HIGH, bracketOrderRangeObj.getLimitPriceHigh());
		slPriceObj.put(OrderConstants.LOW, bracketOrderRangeObj.getLimitPriceLow());
		return slPriceObj;
		
	}
	
	private static JSONObject getStoplossTriggerPrice(ComputeBracketOrderRangeObject bracketOrderRangeObj) {
		
		JSONObject slTrigPriceObj = new JSONObject();
		slTrigPriceObj.put(OrderConstants.HIGH, bracketOrderRangeObj.getTriggerPriceHigh());
		slTrigPriceObj.put(OrderConstants.LOW, bracketOrderRangeObj.getTriggerPriceLow());
		return slTrigPriceObj;
		
	}
	
}
