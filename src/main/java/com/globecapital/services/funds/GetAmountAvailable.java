package com.globecapital.services.funds;

import java.util.List;

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
import com.globecapital.api.ft.watchlist.CreateWatchlistResponse;
import com.globecapital.business.report.FundsView;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;
import com.globecapital.utils.PriceFormat;

public class GetAmountAvailable extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();

		String sSegmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);

		String sUserID = session.getUserID();
		String sGroupID = session.getGroupId();
		String jKey = session.getjKey();
		String jSessionID = session.getjSessionID();

		GetPeriodicitesForFundsSummaryRequest periodicitesRequest = new GetPeriodicitesForFundsSummaryRequest();
		periodicitesRequest.setUserID(sUserID);
		periodicitesRequest.setGroupId(sGroupID);
		periodicitesRequest.setJKey(jKey);
		periodicitesRequest.setJSession(jSessionID);

		GetPeriodicitesForFundsSummaryAPI periodicitesAPI = new GetPeriodicitesForFundsSummaryAPI();
		GetPeriodicitesForFundsSummaryResponse periodicitesResponse =new GetPeriodicitesForFundsSummaryResponse();	
		try {
		periodicitesResponse= periodicitesAPI.post(periodicitesRequest,
				GetPeriodicitesForFundsSummaryResponse.class, session.getAppID(),"GetPeriodicitesForFundsSummary");
		}
		catch (GCException e){
			log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(periodicitesRequest,session,  getServletContext(), gcRequest, gcResponse)) {
                	periodicitesResponse= periodicitesAPI.post(periodicitesRequest,
            				GetPeriodicitesForFundsSummaryResponse.class, session.getAppID(),"GetPeriodicitesForFundsSummary");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}

		GetPeriodicitesForFundsSummaryObject periodicitesObj = periodicitesResponse.getResponseObject();

		List<GetPeriodicitesForFundsSummaryRows> periodicitesRows = periodicitesObj.getListResponseObject();

		long nPeriodicity = 0;

		for (GetPeriodicitesForFundsSummaryRows getPeriodicitesForFundsSummaryRow : periodicitesRows) {
			String sPeriodicityName = getPeriodicitesForFundsSummaryRow.getPeriodicityName();
			if (sPeriodicityName.equals(FTConstants.PERIODICITY_EQUITY_NAME)
					&& sSegmentType.equals(DeviceConstants.EQ_DERIVATIVES_CURRENCY))
				nPeriodicity = getPeriodicitesForFundsSummaryRow.getnPeriodicity();
			else if (sPeriodicityName.equals(FTConstants.PERIODICITY_COMMODITY_NAME)
					&& sSegmentType.equals(DeviceConstants.COMMODITY))
				nPeriodicity = getPeriodicitesForFundsSummaryRow.getnPeriodicity();

		}

		String sAmtAvail = "--";
		if (nPeriodicity != 0) {
			GetAdvanceFundsViewRequest fundsRequest = new GetAdvanceFundsViewRequest();

			jKey = session.getjKey();
			jSessionID = session.getjSessionID();
			
			fundsRequest.setUserID(sUserID);
			fundsRequest.setGroupId(sGroupID);
			fundsRequest.setIntPeriodicities(nPeriodicity);
			fundsRequest.setJKey(jKey);
			fundsRequest.setJSession(jSessionID);

			GetAdvanceFundsViewAPI fundsAPI = new GetAdvanceFundsViewAPI();
			GetAdvanceFundsViewResponse fundsResponse=new GetAdvanceFundsViewResponse();
			fundsResponse = fundsAPI.post(fundsRequest, GetAdvanceFundsViewResponse.class,session.getAppID(),"GetAdvanceFundsView");
			
			GetAdvanceFundsViewObject fundsObj = fundsResponse.getResponseObject();

			List<GetAdvanceFundsViewRows> fundRows = fundsObj.getListResponseObject();
			sAmtAvail = FundsView.getAmountAvailable(fundRows);

		}
		gcResponse.addToData(DeviceConstants.AVAILABLE_AMT, sAmtAvail);
		gcResponse.addToData(DeviceConstants.DISP_AMT, "\u20B9" + PriceFormat.formatPrice(sAmtAvail, 2, false));

	}

	}

