package com.globecapital.api.ft.edis;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class UpdateEDISApprovedQuantityAPI extends FTApi<UpdateEDISApprovedQtyRequest, UpdateEDISApprovedQtyResponse> {

	public UpdateEDISApprovedQuantityAPI() throws GCException {
		super(AppConfig.getValue("moon.api.updateEdisApprovedQuantity"));

	}

}
