package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class AMODetailRows {

	@SerializedName("AMOALLOWED")
	protected String amoAllowed;

	@SerializedName("EXCHANGE")
	protected String exchange;
	
	@SerializedName("Endtime")
	protected String endTime;
	
	@SerializedName("Holidaydate")
	protected String holidayDate;
	
	@SerializedName("Starttime")
	protected String startTime;

	public String getAMOAllowed() {
		return amoAllowed;
	}

	public void setAMOAllowed(String amoAllowed) {
		this.amoAllowed = amoAllowed;
	}
	
	
	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	
	public String getEndtime() {
		return endTime;
	}

	public void setEndtime(String endTime) {
		this.endTime = endTime;
	}
	
	
	public String getHolidaydate() {
		return holidayDate;
	}

	public void setHolidaydate(String holidayDate) {
		this.holidayDate = holidayDate;
	}
	
	
	public String getStarttime() {
		return startTime;
	}

	public void setStarttime(String startTime) {
		this.startTime = exchange;
	}
	
}
