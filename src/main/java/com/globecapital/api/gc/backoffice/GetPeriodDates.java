package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetPeriodDates {

	@SerializedName("PeriodDate")
	protected String Date;

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

}
