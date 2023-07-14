package com.globecapital.services.order;

import java.util.List;

import com.globecapital.api.ft.order.GetNetPositionAPI;
import com.globecapital.api.ft.order.GetNetPositionObject;
import com.globecapital.api.ft.order.GetNetPositionRequest;
import com.globecapital.api.ft.order.GetNetPositionResponse;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.business.order.DerivativePositions_102;
import com.globecapital.business.order.EquityHoldings_102;
import com.globecapital.business.order.TodaysPosition_102;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class Positions_102 extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		String positionType = gcRequest.getFromData(OrderConstants.POS_TYPE);
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
		GetNetPositionResponse positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class,
					session.getAppID(),"GetNetPosition");

		GetNetPositionObject positionObj = positionResponse.getResponseObject();
		List<GetNetPositionRows> positionRows = positionObj.getObjJSONRows();		
		
		try
		{
			if(positionType.equalsIgnoreCase(DeviceConstants.TODAY))
				gcResponse.setData(TodaysPosition_102.getPositions(positionRows, sUserID, session.getAppID()));

			else if(positionType.equalsIgnoreCase(DeviceConstants.DERIVATIVES))
				gcResponse.setData(DerivativePositions_102.getDerivativePositions(positionRows, session));			
			
			else if(positionType.equalsIgnoreCase(DeviceConstants.EQUITIES))
				gcResponse.setData(EquityHoldings_102.getHoldings(positionRows, sUserID, 
						session.getAppID()));
			else 
				gcResponse.setNoDataAvailable();
		}
		catch(Exception e)
		{
			log.error(e);
			gcResponse.setNoDataAvailable();
		}
		
	}
}
