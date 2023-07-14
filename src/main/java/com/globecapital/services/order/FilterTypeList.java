package com.globecapital.services.order;

import org.json.JSONObject;
import com.globecapital.business.order.FilterList;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class FilterTypeList extends BaseService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		JSONObject orderFilter = new JSONObject();
		orderFilter.put(DeviceConstants.HOLDINGS_FILTER, FilterList.getEquityFilterList());
		orderFilter.put(DeviceConstants.POSITIONS_FILTER, FilterList.getDerivativeFilterList());
		gcResponse.addToData(DeviceConstants.FILTER_LIST, orderFilter);
	}

}
