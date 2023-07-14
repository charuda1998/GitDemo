package com.globecapital.api.ls.news;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetElementsRows {
	
	@SerializedName("notification")
	protected String notification;
	
	@SerializedName("customName")
	protected String customName;
	
	@SerializedName("companies")
	protected List<GetCompaniesRows> companies;
	
	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}
	
	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public List<GetCompaniesRows> getCompanies() {
		return companies;
	}

	public void setCustom(List<GetCompaniesRows> Companies) {
		companies = Companies;
	}
	
}
