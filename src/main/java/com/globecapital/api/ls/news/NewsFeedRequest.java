package com.globecapital.api.ls.news;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.api.ls.generics.LSRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class NewsFeedRequest extends LSRequest{

	public NewsFeedRequest() throws AppConfigNoKeyFoundException {
		super();
		this.respType = RESP_TYPE.JSON;
	}

}
