package com.globecapital.api.spyder.chart;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.api.spyder.generics.SpyderRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class HistoricalDataRequest extends SpyderRequest{
	
	public HistoricalDataRequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
	public void setExch(String sExch) {
		addParam(SpyderConstants.EXCH, sExch);
	}
	
	public void setScripCode(String sScripCode) {
		addParam(SpyderConstants.SCRIP_CODE, sScripCode);
	}
	
	public void setFromDate(String sFromDate) {
		addParam(SpyderConstants.FROM_DATE, sFromDate);
	}
	
	public void setToDate(String sToDate) {
		addParam(SpyderConstants.TO_DATE, sToDate);
	}
	
	public void setTimeInterval(String sTimeInterval) {
		addParam(SpyderConstants.TIME_INTERVAL, sTimeInterval);
	}

}
