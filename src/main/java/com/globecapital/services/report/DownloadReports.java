package com.globecapital.services.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.globecapital.business.report.Download;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class DownloadReports extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Download.downloadProcess(gcRequest, gcResponse);
		
	}


	@Override
	protected void sendResponse(GCRequest gcRequest, GCResponse gcResponse, HttpServletResponse httpServletResponse,
			HttpServletRequest httpServletRequest) {

		// Do nothing here to avoid sending empty response 
	}
}
