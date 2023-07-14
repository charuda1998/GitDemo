package com.globecapital.services.market;

import java.text.ParseException;
import com.globecapital.business.market.AnnouncementCache;
import com.globecapital.business.market.CorporateActionsCache;
import com.globecapital.business.market.FIIDIICache;
import com.globecapital.business.market.Market;
import com.globecapital.business.market.ResultsInfoCache;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.jmx.Monitor;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;

public class CmotsCacheThread implements Runnable {

	private static Logger log = Logger.getLogger(CmotsCacheThread.class);
	
	@Override
	public void run() {
		try {
			loadCorporateOverviewCache();
			loadResultsOverviewCache();
			loadFIIDIIOverviewCache();
		} catch (CMOTSException e) {
			log.error(e);
		}
		
	}
	
	private void loadCorporateOverviewCache() throws CMOTSException {
		CorporateActionsCache.loadCorporateActionCacheNextWeek();
		AnnouncementCache.loadNSEAnnouncementCache();
		AnnouncementCache.loadBSEAnnouncementCache();
	}
	
	private void loadResultsOverviewCache() throws CMOTSException {
		ResultsInfoCache.loadResultsInfoCache(DeviceConstants.LATER, "",ExchangeSegment.NSE);
		ResultsInfoCache.loadResultsInfoCache(DeviceConstants.LATER, "",ExchangeSegment.BSE);
	}
	
	private void loadFIIDIIOverviewCache() throws CMOTSException {
		try {
			FIIDIICache.loadFIIDIICache(DeviceConstants.DAILY, DeviceConstants.ALL, "");
		} catch (ParseException e) {
			log.error(e);
		}
	}

}
