package com.globecapital.services.order;

import java.util.List;
import org.json.JSONObject;
import com.globecapital.api.ft.order.GetNetPositionAPI;
import com.globecapital.api.ft.order.GetNetPositionObject;
import com.globecapital.api.ft.order.GetNetPositionRequest;
import com.globecapital.api.ft.order.GetNetPositionResponse;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.business.order.EquityHoldings_103;
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
import com.msf.sbu2.service.exception.NoDataAvailableException;

public class Holdings extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);
		
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
				positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class,session.getAppID(),"GetNetPosition");
		}
		catch(GCException e) {
			log.debug(e);
			if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(positionRequest,session, getServletContext(), gcRequest, gcResponse)) {
                    try {
                            positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class,session.getAppID(),"GetNetPosition");
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
			JSONObject holdingRows = EquityHoldings_103.getHoldings(positionRows, sUserID, session, filterObj,getServletContext(),gcRequest,gcResponse);
			if(holdingRows.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty())
				gcResponse.setNoDataAvailable();
			else
				gcResponse.setData(holdingRows);
		}catch(RequestFailedException e) {
			log.error(e);
			throw new RequestFailedException();		
			}
		catch(Exception e)
		{
			log.error(e);
			gcResponse.setNoDataAvailable();
		}
		
	}
}