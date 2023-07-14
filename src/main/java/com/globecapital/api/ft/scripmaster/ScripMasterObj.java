package com.globecapital.api.ft.scripmaster;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.google.gson.annotations.SerializedName;

public class ScripMasterObj extends JSONObject {

	public ScripMasterObj(JSONObject obj) throws JSONException {
		// TODO Auto-generated constructor stub
		super(obj.toString());
	}

	@SerializedName("nMarketSegmentId")
	protected String nMarketSegmentId;

	@SerializedName("nToken")
	protected String nToken;

	@SerializedName("sSymbol")
	protected String sSymbol;

	@SerializedName("sSeries")
	protected String sSeries;

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

	public String getsSeries() {
		return sSeries;
	}

	public void setsSeries(String sSeries) {
		this.sSeries = sSeries;
	}

}
