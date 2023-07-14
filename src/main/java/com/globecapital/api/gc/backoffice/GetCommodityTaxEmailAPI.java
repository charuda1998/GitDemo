package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetCommodityTaxEmailAPI extends GCApi<GetTaxEmailDownloadRequest, EmailResponse> {

	public GetCommodityTaxEmailAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getCommodityTaxEmail"));
	}
}