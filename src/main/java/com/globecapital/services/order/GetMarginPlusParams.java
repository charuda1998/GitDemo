package com.globecapital.services.order;

import java.text.DecimalFormat;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.FetchMarginPlusParamsObject;
import com.globecapital.api.ft.order.FetchMarginPlusParamsResponse;
import com.globecapital.business.order.GetMarginPlusParameter;
import com.globecapital.business.quote.ExchangeQuote;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;


public class GetMarginPlusParams extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		
		Session session = gcRequest.getSession();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		String sProductType = gcRequest.getFromData(OrderConstants.PRODUCT_TYPE);
		
		JSONObject advanceOrderDetailsObj = new JSONObject();
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
        QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		int moneyMode;		
		if(sProductType.equals(ProductType.COVER_ORDER))
		{
			moneyMode = GetMarginPlusParameter.getStrPrcMoneyMode(gcRequest.getFromData(OrderConstants.ORDER_ACTION), symRow.getStrikePrice(),gcRequest.getFromData(OrderConstants.PRICE));
			FetchMarginPlusParamsResponse marginPlusResponse=GetMarginPlusParameter.getMarginPlusParameter(symRow, session, moneyMode);
			
			if(!marginPlusResponse.getResponseStatus())
				throw new GCException(InfoIDConstants.DYNAMIC_MSG, marginPlusResponse.getErrorCode());
			else
			{
				FetchMarginPlusParamsObject marginPlusParams = marginPlusResponse.getResponseObject();
				if(marginPlusParams.getLTP().equals("0")) {
				    marginPlusParams.setLTP(quoteDetails.sLTP); 
				    marginPlusParams.setClosePrice(quoteDetails.sPreviousClose);
				}
				if(marginPlusParams.getLTP().equals(FTConstants.ZERO)||marginPlusParams.getLTP().equals("0"))
					advanceOrderDetailsObj.put(DeviceConstants.FIRST_LEG_PRICE, marginPlusParams.getClosePrice());
				else
					advanceOrderDetailsObj.put(DeviceConstants.FIRST_LEG_PRICE,marginPlusParams.getLTP());
				if(symRow.getSeries().equals(FTConstants.EQUITY)) {
					advanceOrderDetailsObj.put(DeviceConstants.STRIKE_PRICE_VALUE,FTConstants.MINUS_ONE);
					advanceOrderDetailsObj.put(DeviceConstants.EXPIRY_DATE, FTConstants.MINUS_ONE);
				}else {
					advanceOrderDetailsObj.put(DeviceConstants.STRIKE_PRICE_VALUE,marginPlusParams.getStrikeValue());
					advanceOrderDetailsObj.put(DeviceConstants.EXPIRY_DATE, marginPlusParams.getExpiryValue());
				}
				if( marginPlusParams.getBasePrice().equals("0") || marginPlusParams.getBasePrice().equals("0.00"))
				    advanceOrderDetailsObj.put(DeviceConstants.BASE_PRICE, quoteDetails.sLTP);
				else 
				    advanceOrderDetailsObj.put(DeviceConstants.BASE_PRICE,marginPlusParams.getBasePrice());
				advanceOrderDetailsObj.put(OrderConstants.SL_PRICE, getStoplossPrice(marginPlusParams,gcRequest.getFromData(OrderConstants.ORDER_ACTION)));
				advanceOrderDetailsObj.put(OrderConstants.SL_TRIG_PRICE, getStoplossTriggerPrice(marginPlusParams,gcRequest.getFromData(OrderConstants.PRICE),gcRequest.getFromData(OrderConstants.ORDER_ACTION)));
			}
			gcResponse.setData(advanceOrderDetailsObj);
			gcResponse.addToData(OrderConstants.PRODUCT_TYPE, sProductType);
		}
		//TODO: Cover order is not allowed for this sprint 
		else
		{
			throw new RequestFailedException();
		}

	}
private static JSONObject getStoplossPrice(FetchMarginPlusParamsObject coverOrderRangeObj, String orderAction) {
        
        JSONObject slPriceObj = new JSONObject();
        Double ltp = Double.parseDouble(coverOrderRangeObj.getLTP());
        Double lmtTrigPercMin = Double.parseDouble(coverOrderRangeObj.getLimitTrigPercMin());
        Double priceTick = Double.parseDouble(coverOrderRangeObj.getPriceTick());
        Double lmtTrigPerc = Double.parseDouble(coverOrderRangeObj.getLimitTrigPerc());

        //price range calculation
        Double buyHighPrice = ltp - (Math.round((ltp*lmtTrigPerc)/priceTick)*priceTick) - priceTick;
        Double buyLowPrice = buyHighPrice - (Math.round((buyHighPrice*lmtTrigPercMin)/priceTick)*priceTick);
        Double sellLowPrice = ltp + (Math.round((ltp*lmtTrigPercMin)/priceTick)*priceTick) + priceTick;
        Double sellHighPrice = sellLowPrice + (Math.round((sellLowPrice*lmtTrigPerc)/priceTick)*priceTick);
        if(orderAction.equals(OrderAction.BUY)) {
            slPriceObj.put(OrderConstants.LOW, PriceFormat.formatDouble(buyLowPrice));
            slPriceObj.put(OrderConstants.HIGH, PriceFormat.formatDouble(buyHighPrice));
        }else {
            slPriceObj.put(OrderConstants.LOW, PriceFormat.formatDouble(sellLowPrice));
            slPriceObj.put(OrderConstants.HIGH, PriceFormat.formatDouble(sellHighPrice));
        }
        return slPriceObj;
    }
    
    private static JSONObject getStoplossTriggerPrice(FetchMarginPlusParamsObject coverOrderRangeObj, String price, String orderAction) {
        
        JSONObject slTrigPriceObj = new JSONObject();
        Double slPrice = Double.parseDouble(price);
        Double ltp = Double.parseDouble(coverOrderRangeObj.getLTP());
        Double priceTick = Double.parseDouble(coverOrderRangeObj.getPriceTick());
        Double lmtTrigPerc = Double.parseDouble(coverOrderRangeObj.getLimitTrigPerc());
        Double lmtTrigPercMin = Double.parseDouble(coverOrderRangeObj.getLimitTrigPercMin());
        Double buyLowPrice = slPrice - (Math.round((ltp*lmtTrigPercMin)/priceTick)*priceTick);
        Double sellHighPrice = slPrice + (Math.round((ltp*lmtTrigPerc)/priceTick)*priceTick);
        Double buyHighPrice = Math.round(ltp/priceTick)*priceTick;
        Double sellLowPrice = Math.round(ltp/priceTick)*priceTick;

        if(orderAction.equals(OrderAction.BUY)) {
            slTrigPriceObj.put(OrderConstants.HIGH, PriceFormat.formatDouble(buyHighPrice));
            slTrigPriceObj.put(OrderConstants.LOW, PriceFormat.formatDouble(buyLowPrice));
        }else {
            slTrigPriceObj.put(OrderConstants.HIGH, PriceFormat.formatDouble(sellHighPrice));
            slTrigPriceObj.put(OrderConstants.LOW, PriceFormat.formatDouble(sellLowPrice));
        }
        return slTrigPriceObj;  
    }
	
	public static String formatPrice(String value, int decimalLoc) {
	    int precision = 0;
	    while (decimalLoc != 0) {
	        decimalLoc /= 100;
	        ++precision;
	    }
	    System.out.println(precision);
	    String sPrecision = "";
	    if (value == null || value.isEmpty())
            return "0." + PriceFormat.genZeros(precision);
	    for (int i = 0; i < precision; i++)
            sPrecision += "0";
	    if (PriceFormat.isDouble(value)) {
            if (value.equals("0"))
                return "0." + sPrecision;
            final DecimalFormat dec = new DecimalFormat("#0."+sPrecision);
            final Double dval = Double.parseDouble(value);
            value = dec.format(dval);
            if (value.startsWith("."))
                value = "0" + value;
	    }
	    return value;
	}

}
