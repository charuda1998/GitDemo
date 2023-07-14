package com.globecapital.api.ls.news;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetNewsRows {
	
	@SerializedName("guid")
	protected String guid;
	
	@SerializedName("description")
	protected String description;
	
	@SerializedName("date")
	protected String date;
	
	@SerializedName("categories")
	protected List<String> categories;
	
	@SerializedName("custom_elements")
	protected List<GetElementsRows> custom_elements;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
		
	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public List<GetElementsRows> getCustom() {
		return custom_elements;
	}

	public void setCustom(List<GetElementsRows> custom) {
		custom_elements = custom;
	}

}

