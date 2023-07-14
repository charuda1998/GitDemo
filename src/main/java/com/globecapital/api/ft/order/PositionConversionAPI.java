package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class PositionConversionAPI extends FTApi<PositionConversionRequest, PositionConversionResponse> {

	public PositionConversionAPI() throws GCException {
			super(AppConfig.getValue("moon.api.sendPosConvRequest"));
		}

}
