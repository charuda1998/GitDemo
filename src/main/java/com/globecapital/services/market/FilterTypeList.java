package com.globecapital.services.market;

import com.globecapital.business.market.FilterList;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class FilterTypeList extends BaseService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		String segment = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		
		if(segment.equalsIgnoreCase(DeviceConstants.EQUITY))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList.getEquityFilterList());
		else if(segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList.getDerivativeFilterList());
		else if(segment.equalsIgnoreCase(DeviceConstants.CURRENCY))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList.getCurrencyFilterList());
		else if(segment.equalsIgnoreCase(DeviceConstants.COMMODITY))
			gcResponse.addToData(DeviceConstants.MARKET_FILTER, FilterList.getCommodityFilterList());

	}

}
