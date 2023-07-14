package com.globecapital.api.spyder.market;

import com.google.gson.annotations.SerializedName;

public class MarketMoversObject {
	
	@SerializedName(value="ScCode", alternate="sccode")
	protected String cCode;
	
	@SerializedName(value="LTP", alternate="Ltp")
	protected String ltp;
	
	@SerializedName("PChg")
	protected String pChg;
	
	@SerializedName("Change")
	protected String change;
	
	@SerializedName("Volume")
	protected String volume;
	
	@SerializedName("Value")
	protected String value;
	
	@SerializedName("CurrentRate")
	protected String currentRate;
	
	public String getCode() {
		return cCode;
	}

	public void setCode(String code) {
		this.cCode = code;
	}
	
	public String getLtp() {
		return ltp;
	}

	public void setLtp(String Ltp) {
		this.ltp = Ltp;
	}
	
	public String getPChg() {
		return pChg;
	}

	public void setPChg(String PChg) {
		this.pChg = PChg;
	}
	
	public String getChange() {
		return change;
	}

	public void setChange(String chng) {
		this.change = chng;
	}
	
	public String getVolume() {
		return volume;
	}

	public void setVolume(String vol) {
		this.volume = vol;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String val) {
		this.value = val;
	}
	
	public String getCurrentRate() {
		return currentRate;
	}

	public void setCurrentRate(String currRate) {
		this.currentRate = currRate;
	}
}
