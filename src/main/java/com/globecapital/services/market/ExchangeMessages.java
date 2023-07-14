package com.globecapital.services.market;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.market.GetExchangeMessagesAPI;
import com.globecapital.api.ft.market.GetExchangeMessagesObjectRow;
import com.globecapital.api.ft.market.GetExchangeMessagesRequest;
import com.globecapital.api.ft.market.GetExchangeMessagesResponse;
import com.globecapital.api.ft.market.GetExchangeMessagesResponseObject;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.GCUtils;

public class ExchangeMessages extends SessionService {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();

		String sExch = gcRequest.getFromData(DeviceConstants.EXCH);
		
		GetExchangeMessagesRequest exchMessagesReq = new GetExchangeMessagesRequest();
		
		exchMessagesReq.setUserID(session.getUserID());
		exchMessagesReq.setGroupId(session.getGroupId());
		exchMessagesReq.setJKey(session.getjKey());
		exchMessagesReq.setJSession(session.getjSessionID());
		exchMessagesReq.setMktSegId(Integer.parseInt(ExchangeSegment.getMarketSegmentID(sExch)));
		exchMessagesReq.setHrs(AppConfig.getIntValue("ft_get_exchange_messages_hours"));
		
		GetExchangeMessagesAPI exchMessagesAPI = new GetExchangeMessagesAPI();
		
		GetExchangeMessagesResponse exchMessagesResp =new GetExchangeMessagesResponse();
		try {
		exchMessagesResp= exchMessagesAPI.post(exchMessagesReq, 
				GetExchangeMessagesResponse.class, session.getAppID(),"GetExchangeMessages");
		}
		catch(GCException e) {
			if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(exchMessagesReq,session, getServletContext(), gcRequest, gcResponse)) {
                	exchMessagesResp= exchMessagesAPI.post(exchMessagesReq, 
            				GetExchangeMessagesResponse.class, session.getAppID(),"GetExchangeMessages");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}
		
		GetExchangeMessagesResponseObject exchMessagesObj = exchMessagesResp.getResponseObject();
		List<GetExchangeMessagesObjectRow> exchMessagesRows = exchMessagesObj.getObjJSONRows();
		
		if(exchMessagesRows.size() == 0)
		{
			gcResponse.setNoDataAvailable();
			return;
		}
		
		JSONArray exchMessagesArr = new JSONArray();
		
		for(int i = 0; i < exchMessagesRows.size(); i++)
		{
			GetExchangeMessagesObjectRow exchMessagesRow = exchMessagesRows.get(i);
			JSONObject obj = new JSONObject();
			obj.put(DeviceConstants.DESC, 
					exchMessagesRow.getMsg().replaceAll("\\u0000", "").replaceAll("\\s+$",""));
			obj.put(DeviceConstants.TIME_MARKET, 
					DateUtils.convert24HoursTo12HoursTime(exchMessagesRow.getTime().replaceAll("\\s+$","")));
			exchMessagesArr.put(obj);
		}
		
		gcResponse.addToData(DeviceConstants.EXCH_MSGS, exchMessagesArr);
		
	}
	
}
