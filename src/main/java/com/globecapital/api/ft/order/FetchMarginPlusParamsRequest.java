package com.globecapital.api.ft.order;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class FetchMarginPlusParamsRequest extends FTRequest{

	public FetchMarginPlusParamsRequest() throws AppConfigNoKeyFoundException {
		
		super();
		
		setProductType(ProductType.FT_MARGIN_PLUS);
		
		setUCCClientId("");
		
		setUCCGroupId("");
		
		setAPICall(true);
		
	}
	
	public void setToken(int iToken) throws JSONException {
		addToData(FTConstants.SCRIP_TKN, iToken);
	}
	
	public void setMktSegId(int iMktSegId) throws JSONException {
		addToData(FTConstants.MKT_SEGID, iMktSegId);
	}
	
	public void setInstName(String sInstName) throws JSONException {
		addToData(FTConstants.INST_NM, sInstName);
	}

	public void setSymbol(String sSymbol) throws JSONException {
		addToData(FTConstants.SYMBOL, sSymbol);
	}
	
	public void setSeries(String sSeries) throws JSONException {
		addToData(FTConstants.SERIES, sSeries);
	}
	
	public void setExpiry(String expiry) throws JSONException {
		addToData(FTConstants.EXP_DT, expiry);
	}
	
	public void setStrkPrc(String sStrikePrice) throws JSONException {
		addToData(FTConstants.STRK_PRC, sStrikePrice);
	}

	public void setOptType(String OptType) throws JSONException {
		addToData(FTConstants.OPT_TYPE, OptType);
	}
	
	public void setProductType(String sProductType) throws JSONException {
		addToData(FTConstants.PRD_TYPE, sProductType);
	}
	
	public void setDecimalLocator(String decimalLocator) throws JSONException {
		addToData(FTConstants.DECIMAL_LOCATOR, decimalLocator);
	}
	
	public void setStrikePrcMoneyMode(String strikePriceMoneyMode) throws JSONException {
		addToData(FTConstants.STRIKE_PRC_MONEY_MODE, strikePriceMoneyMode);
	}
	
	public void setAPICall(boolean bAPICall) throws JSONException {
		addToData(FTConstants.API_CALL, bAPICall);
	}
	
	public void setUCCClientId(String sUCCClientId) throws JSONException {
		addToData(FTConstants.UCC_CLIENT_ID, sUCCClientId);
	}
	
	public void setUCCGroupId(String sUCCGroupId) throws JSONException {
		addToData(FTConstants.UCC_GROUP_ID, sUCCGroupId);
	}
	
}
