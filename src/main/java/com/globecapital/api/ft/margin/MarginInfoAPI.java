package com.globecapital.api.ft.margin;

import com.globecapital.api.ft.esip.FtEQsipApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;
public class MarginInfoAPI extends FtEQsipApi<MarginInfoRequest, MarginInfoResponse> {

	public MarginInfoAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getMarginInfo"));

	}
}
