package com.globecapital.services.market;

import com.globecapital.business.market.FilterList_101;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class FilterTypeList_101 extends BaseService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList_101.getEquityFilterList());
		else if(segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList_101.getDerivativeFilterList());
		else if(segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList_101.getCurrencyFilterList());
		else if(segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList_101.getCommodityFilterList());

	}

}
