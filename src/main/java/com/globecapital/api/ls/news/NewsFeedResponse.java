package com.globecapital.api.ls.news;

import java.util.List;

import com.globecapital.api.ls.generics.LSResponse;
import com.google.gson.annotations.SerializedName;

public class NewsFeedResponse extends LSResponse {
	
	@SerializedName("items")
	protected List<GetNewsRows> items;
	
	public List<GetNewsRows> getItems() {
		return items;
	}

	public void setItems(List<GetNewsRows> Items) {
		items = Items;
	}

}
