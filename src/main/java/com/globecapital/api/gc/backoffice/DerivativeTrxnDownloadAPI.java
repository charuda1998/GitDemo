package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class DerivativeTrxnDownloadAPI extends GCApi<GetTrxnMailDownloadRequest, DownloadResponse> {

	public DerivativeTrxnDownloadAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getDerivativeTrxnDownload"));
		
	}
}