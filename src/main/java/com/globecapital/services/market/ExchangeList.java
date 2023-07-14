package com.globecapital.services.market;

import com.globecapital.business.market.FilterList;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class ExchangeList extends SessionService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		gcResponse.setData(FilterList.getExchangeList());

	}

}
