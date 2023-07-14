package com.globecapital.api.ft.market;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.google.gson.annotations.SerializedName;

public class IndicesObj  extends JSONObject {

	public IndicesObj(JSONObject obj) throws JSONException {
		super(obj.toString());
	}
	
	@SerializedName("nMarketSegmentId")
	protected String nMarketSegmentId;

	@SerializedName("nToken")
	protected String nToken;

	@SerializedName("sSymbol")
	protected String sSymbol;

	@SerializedName("sSecurityDesc")
	protected String sSecurityDesc;
	
	@SerializedName("sExchange")
	protected String sExchange;
	
	public String getnMarketSegmentId() {
		return nMarketSegmentId;
	}

	public void setnMarketSegmentId(String nMarketSegmentId) {
		this.nMarketSegmentId = nMarketSegmentId;
	}

	public String getnToken() {
		return nToken;
	}

	public void setnToken(String nToken) {
		this.nToken = nToken;
	}

	public String getsSymbol() {
		return sSymbol;
	}

	public void setsSymbol(String sSymbol) {
		this.sSymbol = sSymbol;
	}

	public String getsSecurityDesc() {
		return sSecurityDesc;
	}

	public void setsSecurityDesc(String sSecurityDesc) {
		this.sSecurityDesc = sSecurityDesc;
	}
	
	public String getsExchange() {
		return sExchange;
	}

	public void setsExchange(String sExchange) {
		this.sExchange = sExchange;
	}

}
