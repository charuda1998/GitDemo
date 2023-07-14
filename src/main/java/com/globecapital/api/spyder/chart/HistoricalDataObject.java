package com.globecapital.api.spyder.chart;

import com.google.gson.annotations.SerializedName;

public class HistoricalDataObject{
	
	@SerializedName("D")
	protected String date;
	
	@SerializedName("O")
	protected String open;
	
	@SerializedName("H")
	protected String high;
	
	@SerializedName("L")
	protected String low;
	
	@SerializedName("C")
	protected String close;
	
	@SerializedName("V")
	protected String volume;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}
	
	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}
	
	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}
	
	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}
	
	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}
	

}
