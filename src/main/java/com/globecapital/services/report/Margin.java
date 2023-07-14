package com.globecapital.services.report;

import java.util.List;

import org.json.JSONArray;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.report.GetAdvanceFundsViewAPI;
import com.globecapital.api.ft.report.GetAdvanceFundsViewObject;
import com.globecapital.api.ft.report.GetAdvanceFundsViewRequest;
import com.globecapital.api.ft.report.GetAdvanceFundsViewResponse;
import com.globecapital.api.ft.report.GetAdvanceFundsViewRows;
import com.globecapital.api.ft.report.GetPeriodicitesForFundsSummaryAPI;
import com.globecapital.api.ft.report.GetPeriodicitesForFundsSummaryObject;
import com.globecapital.api.ft.report.GetPeriodicitesForFundsSummaryRequest;
import com.globecapital.api.ft.report.GetPeriodicitesForFundsSummaryResponse;
import com.globecapital.api.ft.report.GetPeriodicitesForFundsSummaryRows;
import com.globecapital.business.report.FundsView;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.GCUtils;

public class Margin extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String sFundType = gcRequest.getFromData(DeviceConstants.FUND_TYPE);
		Session session = gcRequest.getSession();
		
		String sUserID = session.getUserID();
		String sGroupID = session.getGroupId();
		String jKey = session.getjKey();
		String jSessionID = session.getjSessionID();
		
		GetPeriodicitesForFundsSummaryRequest periodicitesRequest
					= new GetPeriodicitesForFundsSummaryRequest();
		
		periodicitesRequest.setUserID(sUserID);
		periodicitesRequest.setGroupId(sGroupID);
		periodicitesRequest.setJKey(jKey);
		periodicitesRequest.setJSession(jSessionID);

		GetPeriodicitesForFundsSummaryAPI periodicitesAPI = new GetPeriodicitesForFundsSummaryAPI();
		GetPeriodicitesForFundsSummaryResponse periodicitesResponse =new GetPeriodicitesForFundsSummaryResponse();
		try {
		periodicitesResponse= periodicitesAPI.post(periodicitesRequest, GetPeriodicitesForFundsSummaryResponse.class, session.getAppID(),"GetPeriodicitesForFundsSummary");
		}
		catch (GCException e) {
			log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(periodicitesRequest,session,  getServletContext(), gcRequest, gcResponse)) {
                	periodicitesResponse= periodicitesAPI.post(periodicitesRequest, GetPeriodicitesForFundsSummaryResponse.class, session.getAppID(),"GetPeriodicitesForFundsSummary");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}
		GetPeriodicitesForFundsSummaryObject periodicitesObj = periodicitesResponse.getResponseObject();
		
		List<GetPeriodicitesForFundsSummaryRows> periodicitesRows = 
				periodicitesObj.getListResponseObject();
				
		long nPeriodicity = 0;
		
		for(GetPeriodicitesForFundsSummaryRows getPeriodicitesForFundsSummaryRow : periodicitesRows)
		{
			String sPeriodicityName = getPeriodicitesForFundsSummaryRow.getPeriodicityName();
			if(sPeriodicityName.equals(FTConstants.PERIODICITY_ALL_EXCHANGE_COMBINED))
				nPeriodicity = getPeriodicitesForFundsSummaryRow.getnPeriodicity();	
			else if(sPeriodicityName.equals(FTConstants.PERIODICITY_EQUITY_NAME) 
					&& sFundType.equals(DeviceConstants.EQUITY))
				nPeriodicity = getPeriodicitesForFundsSummaryRow.getnPeriodicity();
			else if(sPeriodicityName.equals(FTConstants.PERIODICITY_COMMODITY_NAME) &&
					sFundType.equals(DeviceConstants.COMMODITY))
				nPeriodicity = getPeriodicitesForFundsSummaryRow.getnPeriodicity();
			else if(sPeriodicityName.equals(FTConstants.PERIODICITY_COMMODITY_COMBINED) &&
					sFundType.equals(DeviceConstants.COMMODITY))
				nPeriodicity = getPeriodicitesForFundsSummaryRow.getnPeriodicity();
			
		}
		JSONArray limitList = new JSONArray();
		
		if(nPeriodicity != 0)
		{
		GetAdvanceFundsViewRequest fundsRequest = new GetAdvanceFundsViewRequest();
		jKey = session.getjKey();
		jSessionID = session.getjSessionID();
		fundsRequest.setUserID(sUserID);
		fundsRequest.setGroupId(sGroupID);
		fundsRequest.setIntPeriodicities(nPeriodicity);
		fundsRequest.setJKey(jKey);
		fundsRequest.setJSession(jSessionID);

		GetAdvanceFundsViewAPI fundsAPI = new GetAdvanceFundsViewAPI();
		GetAdvanceFundsViewResponse fundsResponse 
					= fundsAPI.post(fundsRequest, GetAdvanceFundsViewResponse.class, session.getAppID(),"GetAdvanceFundsView");
		
		GetAdvanceFundsViewObject fundsObj = fundsResponse.getResponseObject();
		
		List<GetAdvanceFundsViewRows> fundRows = fundsObj.getListResponseObject();
		limitList = FundsView.getLimits(fundRows);
		
		if (limitList.length() > 0)
		{
			gcResponse.addToData(DeviceConstants.REPORT_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.LIMITS, limitList);
		}
		else
		{
			
			limitList = FundsView.getDefaultLimits();
			gcResponse.addToData(DeviceConstants.REPORT_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.LIMITS, limitList);
		}
		}
		else
		{
			limitList = FundsView.getDefaultLimits();
			gcResponse.addToData(DeviceConstants.REPORT_DATE, DateUtils.getCurrentDateTime(DeviceConstants.REPORT_DATE_FORMAT));
			gcResponse.addToData(DeviceConstants.LIMITS, limitList);
		}
		
	}

}
