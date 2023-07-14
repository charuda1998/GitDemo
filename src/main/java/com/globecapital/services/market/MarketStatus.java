package com.globecapital.services.market;

import java.util.List;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.user.GetMarketStatusAPI;
import com.globecapital.api.ft.user.GetMarketStatusObjectRow;
import com.globecapital.api.ft.user.GetMarketStatusRequest;
import com.globecapital.api.ft.user.GetMarketStatusResponse;
import com.globecapital.api.ft.user.GetMarketStatusResponseObject;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;

public class MarketStatus extends SessionService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();

		String sSegmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		
		GetMarketStatusRequest marketStatusReq = new GetMarketStatusRequest();
		marketStatusReq.setUserID(session.getUserID());
		marketStatusReq.setGroupId(session.getGroupId());
		marketStatusReq.setJKey(session.getjKey());
		marketStatusReq.setJSession(session.getjSessionID());

		GetMarketStatusAPI marketStatusAPI = new GetMarketStatusAPI();
		
		GetMarketStatusResponse marketStatusRes =new GetMarketStatusResponse();
		try {
		marketStatusRes= marketStatusAPI.post(marketStatusReq, 
				GetMarketStatusResponse.class, session.getAppID(),"GetMarketStatusResponse");
		}
		catch(GCException e) {
			if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(marketStatusReq,session, getServletContext(), gcRequest, gcResponse)) {
                	marketStatusRes= marketStatusAPI.post(marketStatusReq, 
            				GetMarketStatusResponse.class, session.getAppID(),"GetMarketStatusResponse");
                	session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}
		
		GetMarketStatusResponseObject marketStatusObj = marketStatusRes.getResponseObject();
		List<GetMarketStatusObjectRow> marketStatusRows = marketStatusObj.getObjJSONRows();
		
		if(marketStatusRows.size() == 0)
		{
			gcResponse.setNoDataAvailable();
			return;
		}
		
		String sStatus = "";
		JSONObject obj = new JSONObject();
		if(sSegmentType.equalsIgnoreCase(DeviceConstants.EQUITY))
		{
			sStatus = marketStatusRows.get(0).getNSEMarketStatus();
			obj.put(DeviceConstants.STATUS, getMarketStatus(sStatus));
			obj.put(DeviceConstants.COLOUR_STATUS, getColourCodeStatus(sStatus));
		} 
		else if(sSegmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVES))
		{
			sStatus = marketStatusRows.get(0).getNFOMarketStatus();
			obj.put(DeviceConstants.STATUS, getMarketStatus(sStatus));
			obj.put(DeviceConstants.COLOUR_STATUS, getColourCodeStatus(sStatus));
			
		}
		else if(sSegmentType.equalsIgnoreCase(DeviceConstants.CURRENCY))
		{
			sStatus = marketStatusRows.get(0).getNSECDSMarketStatus();
			obj.put(DeviceConstants.STATUS, getMarketStatus(sStatus));
			obj.put(DeviceConstants.COLOUR_STATUS, getColourCodeStatus(sStatus));
		}
		else if(sSegmentType.equalsIgnoreCase(DeviceConstants.COMMODITY))
		{
			sStatus = marketStatusRows.get(0).getMCXMarketStatus();
			obj.put(DeviceConstants.STATUS, getMarketStatus(sStatus));
			obj.put(DeviceConstants.COLOUR_STATUS, getColourCodeStatus(sStatus));
		}
		
		gcResponse.setData(obj);
		
	}

	private String getColourCodeStatus(String sStatus) {
		
		if(sStatus.equalsIgnoreCase(FTConstants.OPEN) || sStatus.equalsIgnoreCase(FTConstants.EXTENDED_MARKET))
			return DeviceConstants.COLOUR_STATUS_OPEN;
		else if(sStatus.equalsIgnoreCase(FTConstants.CLOSE) || sStatus.equalsIgnoreCase(FTConstants.POST_CLOSING))
			return DeviceConstants.COLOUR_STATUS_CLOSE;
		else
			return DeviceConstants.COLOUR_STATUS_OTHER;
	}

	private String getMarketStatus(String sStatus) {
		
		return "MARKET " + sStatus.toUpperCase();
	}
	
}
