package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetUnRelCommodityDownloadAPI extends GCApi<GetUnPLEmailRequest, DownloadResponse> {

	public GetUnRelCommodityDownloadAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getUnRelCommodityPLDownload"));
	}
	
}