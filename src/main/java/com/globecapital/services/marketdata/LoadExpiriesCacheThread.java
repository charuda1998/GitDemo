package com.globecapital.services.marketdata;

import com.globecapital.business.marketdata.ExpiriesCache;

public class LoadExpiriesCacheThread implements Runnable {

	@Override
	public void run() {
		ExpiriesCache.loadExpiriesCache();
	}
}
