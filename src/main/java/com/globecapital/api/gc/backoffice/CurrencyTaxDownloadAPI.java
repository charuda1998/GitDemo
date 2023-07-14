package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class CurrencyTaxDownloadAPI extends GCApi<GetTaxEmailDownloadRequest, DownloadResponse> {

	public CurrencyTaxDownloadAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getCurrencyTaxDownload"));
	}

}