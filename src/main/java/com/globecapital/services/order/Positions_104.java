package com.globecapital.services.order;

import java.util.List;
import org.json.JSONObject;
import com.globecapital.api.ft.order.GetNetPositionAPI;
import com.globecapital.api.ft.order.GetNetPositionObject;
import com.globecapital.api.ft.order.GetNetPositionRequest;
import com.globecapital.api.ft.order.GetNetPositionResponse;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.business.order.DerivativePositions_103;
import com.globecapital.business.order.TodaysPosition_103;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;

public class Positions_104 extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		JSONObject todaysFilterObj = gcRequest.getObjectFromData(DeviceConstants.TODAYS_FILTER_OBJ);
		JSONObject derivativesFilterObj = gcRequest.getObjectFromData(DeviceConstants.DERIVATIVES_FILTER_OBJ);
		
		String sGroupID = session.getGroupId();
		String sUserID = session.getUserID();

		GetNetPositionRequest positionRequest = new GetNetPositionRequest();
		positionRequest.setUserID(sUserID);
		positionRequest.setGroupId(sGroupID);
		positionRequest.setJKey(session.getjKey());
		positionRequest.setJSession(session.getjSessionID());

		/** * Position type set to ZERO, as we are getting only  today's positios
		 *  date here for all the tabs 'Today, Derivative and Equity'		*/		
		positionRequest.setPosType(AppConstants.STR_ZERO); 

		GetNetPositionAPI positionApi = new GetNetPositionAPI();
		GetNetPositionResponse positionResponse=new GetNetPositionResponse();

		try {
				positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class, session.getAppID(),"GetNetPosition");
		}
		catch(GCException e) {
			log.debug(e);
			if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(positionRequest,session, getServletContext(), gcRequest, gcResponse)) {
                    try {
                	    positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class, session.getAppID(),"GetNetPosition");
                    }
                    catch(GCException ex) {
                        log.debug(ex+"  After ReInitiate Login");
                        throw new RequestFailedException();
                    }
                    session = gcRequest.getSession();
                } else {
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                }
			}
			else
				throw new RequestFailedException();
		}

		GetNetPositionObject positionObj = positionResponse.getResponseObject();
		List<GetNetPositionRows> positionRows = positionObj.getObjJSONRows();		
		
		try
		{
			JSONObject derivativePositions = new JSONObject();
			JSONObject todaysPositionObj = TodaysPosition_103.getPositions_101(positionRows, sUserID, session.getAppID(), todaysFilterObj);
			JSONObject derivativePositionObj = DerivativePositions_103.getDerivativePositions_101(positionRows, session, derivativesFilterObj,getServletContext(),gcRequest,gcResponse);
			String totalCount = "0";
			if(derivativePositionObj.has(DeviceConstants.TOTAL_COUNT)) {
				totalCount = String.valueOf(derivativePositionObj.getInt(DeviceConstants.TOTAL_COUNT));
				derivativePositionObj.remove(DeviceConstants.TOTAL_COUNT);
				if(totalCount.equals("0") && todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY))
					totalCount = String.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY).getString(DeviceConstants.RECORDS_COUNT));
			}else if(todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY)) {
				totalCount = String.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY).getString(DeviceConstants.RECORDS_COUNT));
			}
			derivativePositions.put(DeviceConstants.TOTAL_COUNT, totalCount);
			derivativePositions.put(DeviceConstants.TODAYS_POSITIONS, todaysPositionObj);
			derivativePositions.put(DeviceConstants.DERIVATIVE_POSITIONS, derivativePositionObj);
			if(todaysPositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty() && derivativePositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty())
				gcResponse.setNoDataAvailable();
			else
				gcResponse.setData(derivativePositions);			
		}
		catch(Exception e)
		{
			log.error(e);
			gcResponse.setNoDataAvailable();
		}
		
	}
}
