package com.globecapital.api.ls.news;

import com.globecapital.api.ls.generics.LSApi;

public class NewsFeedAPI extends LSApi<NewsFeedRequest, NewsFeedResponse> {

	public NewsFeedAPI(String serviceUrl) {
		super(serviceUrl);
	}

}
