package com.globecapital.api.ft.order;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class ComputeBracketOrderRangeRequest extends FTRequest{

	public ComputeBracketOrderRangeRequest() throws AppConfigNoKeyFoundException {
		super();
		setProductType(ProductType.FT_BRACKET_ORDER);
	}
	
	public void setToken(int iToken) throws JSONException {
		addToData(FTConstants.TOKEN, iToken);
	}
	
	public void setMktSegId(int iMktSegId) throws JSONException {
		addToData(FTConstants.MKT_SEGID, iMktSegId);
	}
	
	public void setInstName(String sInstName) throws JSONException {
		addToData(FTConstants.INST_NAME, sInstName);
	}

	public void setSymbol(String sSymbol) throws JSONException {
		addToData(FTConstants.SYMBOL, sSymbol);
	}
	
	public void setProductType(String sProductType) throws JSONException {
		addToData(FTConstants.PRODUCT_TYPE, sProductType);
	}
	
	public void setBuySell(int iBuySell) throws JSONException {
		addToData(FTConstants.BUY_SELL, iBuySell);
	}
	
	public void setLTP(String sLTP) throws JSONException {
		addToData(FTConstants.LTP, sLTP);
	}
	
	public void setClosePrice(String sClosePrice) throws JSONException {
		addToData(FTConstants.CLOSE_PRICE, sClosePrice);
	}
	
	public void setBasePrice(float fBasePrice) throws JSONException {
		addToData(FTConstants.BASE_PRICE, fBasePrice);
	}
	
	public void setHighPrice(String sHighPrice) throws JSONException {
		addToData(FTConstants.HIGH_PRICE, sHighPrice);
	}
	
	public void setLowPrice(String sLowPrice) throws JSONException {
		addToData(FTConstants.LOW_PRICE, sLowPrice);
	}
	
	public void setMainOrderPrice(String sMainOrderPrice) throws JSONException {
		addToData(FTConstants.MAIN_ORDER_PRICE, sMainOrderPrice);
	}
	
	public void setSLOrdPrice(String sSLOrderPrice) throws JSONException {
		addToData(FTConstants.SL_ORD_PRICE, sSLOrderPrice);
	}
	
	public void setSLTriggerPrice(String sSLTriggerPrice) throws JSONException {
		addToData(FTConstants.SL_TRIGGER_PRICE, sSLTriggerPrice);
	}
	
	public void setStrikePrc(String sStrikePrc) throws JSONException {
		addToData(FTConstants.STRIKE_PRC, sStrikePrc);
	}
	
	public void setExpiryDate(String sExpiryDate) throws JSONException {
		addToData(FTConstants.EXPIRY_DATE, sExpiryDate);
	}
}
