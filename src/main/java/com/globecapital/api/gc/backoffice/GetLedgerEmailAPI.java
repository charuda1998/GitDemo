package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetLedgerEmailAPI extends GCApi<GetLedgerEmailDownloadRequest, EmailResponse> {

	public GetLedgerEmailAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getLedgerEmail"));
	}
}