package com.globecapital.api.ft.margin;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class MarginInfoRequest extends FTRequest {
	 
	 JSONObject legdata = new JSONObject(); 
	 JSONArray legDetails =new JSONArray();
	 
	 public MarginInfoRequest() throws AppConfigNoKeyFoundException {
			super();
			addToData(FTConstants.LEG_DETAILS,legDetails);
		}
	
	public void setFTUserId(String userid) throws JSONException {
		legdata.put(FTConstants.USERID, userid);
	}
	public void setGroupId(String groupid )throws JSONException {
		addToData(FTConstants.GROUP_ID, groupid);
	}
	public void setNoOfLegs(int noof_legs )throws JSONException {
		addToData(FTConstants.NO_OF_LEGS, noof_legs);	
	}
	public void setMode(String mode )throws JSONException {
		addToData(FTConstants.MODE, mode);
	}
	public void setFETraceId(String fetraceid)throws JSONException{
		addToData(FTConstants.FETRACE_ID, fetraceid);
	}
	public void setLegNo(int legno )throws JSONException {
		legdata.put(FTConstants.LEG_NO,legno);
	}
	public void setBuyOrSell(int buyorsell )throws JSONException {
		legdata.put(FTConstants.BUY_OR_SELL,buyorsell);
	}
	public void setMarketSegmentId(String sMarketSegID )throws JSONException {
		legdata.put(FTConstants.MARKET_SEGMENT_ID, sMarketSegID);
	}
	public void setToken(String string )throws JSONException {
		legdata.put(FTConstants.TOKEN, string);
	}
	public void setQuantity(String string )throws JSONException {
		legdata.put(FTConstants.QUANTITY, string);
	}
	public void setPrice(String price )throws JSONException {
		legdata.put(FTConstants.PRICE, price);
	}
	public void setMktFlag(int i )throws JSONException {
		legdata.put(FTConstants.MKTFLAG,i);
	}
	public void setProductType(String producttype )throws JSONException {
		legdata.put(FTConstants.PRODUCT_TYPE, producttype);
	}
	public void setLegIndicator(int legindicator )throws JSONException {
		legdata.put(FTConstants.LEG_INDICATOR, legindicator);
		
	}	
	public void setOldQuantity(String string )throws JSONException {
		legdata.put(FTConstants.OLD_QUANTITY, string);
	}
	public void setOldPrice(String string )throws JSONException {
		legdata.put(FTConstants.OLD_PRICE, string);
	}
	
	public void setLegDetails() throws JSONException{
		legDetails.put(legdata);
	}
	
	
}
