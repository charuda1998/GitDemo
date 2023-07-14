package com.globecapital.business.edis;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.edis.GetEDISQuantityResponse;
import com.globecapital.api.ft.edis.UpdateEDISApprovedQtyRequest;
import com.globecapital.api.ft.edis.UpdateEDISApprovedQtyResponse;
import com.globecapital.api.ft.edis.UpdateEDISApprovedQuantityAPI;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;

public class UpdateEDISApprovedQtyDetails {
	
	private static Logger log = Logger.getLogger(UpdateEDISApprovedQtyDetails.class);
	
	public static Boolean updateEDISApprovedQtyDetails(JSONObject sessionDetails, JSONObject response,ServletContext servletContext,HttpServletRequest req) throws JSONException, Exception {
		Session session=new Session();
		session.setAppID(sessionDetails.getString(DBConstants.APP_ID));
		session.setUserId(sessionDetails.getString(DBConstants.USER_ID));
		String request="{\"request\": { \"data\": {    },\"appID\":%s } }";
		String.format(request,sessionDetails.getString(DBConstants.APP_ID));
		GCRequest gcRequest=new GCRequest(request);
		gcRequest.setHttpRequest(req);
		GCResponse gcResponse=new GCResponse();
		
		UpdateEDISApprovedQuantityAPI updateEDISApprovedQtyAPI = new UpdateEDISApprovedQuantityAPI();
		UpdateEDISApprovedQtyRequest edisRequest = new UpdateEDISApprovedQtyRequest();
		UpdateEDISApprovedQtyResponse edisResponse = new UpdateEDISApprovedQtyResponse();
		edisRequest.setUserID(sessionDetails.getString(DBConstants.USER_ID));
		edisRequest.setGroupId(sessionDetails.getString(FTConstants.GROUPID));
		edisRequest.setJKey(sessionDetails.getString(DBConstants.J_KEY));
		edisRequest.setJSession(sessionDetails.getString(DBConstants.FT_SESSION_ID));
		if(response.getString(FTConstants.DEPOSITORY).equalsIgnoreCase(DeviceConstants.DEPOSITORY_CDSL))
			edisRequest.setScripDetails(processScripDetails(response.getJSONArray(FTConstants.SCRIP_DETAILS)));
		else
			edisRequest.setScripDetails(processScripDetails(response.getJSONObject(FTConstants.SCRIP_DETAILS)));
		try {
		edisResponse = updateEDISApprovedQtyAPI.post(edisRequest, UpdateEDISApprovedQtyResponse.class, sessionDetails.getString(DBConstants.APP_ID),"UpdateEDISApprovedQuantity");
		}
		catch(GCException e) {
			if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(edisRequest,session, servletContext, gcRequest, gcResponse)) {
            		edisResponse = updateEDISApprovedQtyAPI.post(edisRequest, UpdateEDISApprovedQtyResponse.class, sessionDetails.getString(DBConstants.APP_ID),"UpdateEDISApprovedQuantity");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            }
			else 
				throw new RequestFailedException();
		}
		return edisResponse.getResponseStatus();
	}

	private static JSONArray processScripDetails(JSONArray scripDetails) {
		JSONArray finalScripDetails =  new JSONArray();
		for (int i = 0; i < scripDetails.length(); i++) {
			JSONObject scripDetail = scripDetails.getJSONObject(i);
			if(!scripDetail.getString(FTConstants.STATUS).equalsIgnoreCase(DeviceConstants.FAILURE)) {
				JSONObject scripObj = new JSONObject();
				scripObj.put(FTConstants.ISIN_CODE, scripDetail.getString(FTConstants.ISIN_CODE));
				scripObj.put(FTConstants.QUANTITY, scripDetail.getInt(OrderConstants.QTY));
				finalScripDetails.put(scripObj);
			}
		}
		return finalScripDetails;
	}

	private static JSONArray processScripDetails(JSONObject scripDetail) {
		JSONArray finalScripDetails =  new JSONArray();
			if(!scripDetail.getString(FTConstants.STATUS).equalsIgnoreCase(DeviceConstants.FAILURE)) {
				JSONObject scripObj = new JSONObject();
				scripObj.put(FTConstants.ISIN_CODE, scripDetail.getString(FTConstants.ISIN_CODE));
				scripObj.put(FTConstants.QUANTITY, scripDetail.getInt(OrderConstants.QTY));
				finalScripDetails.put(scripObj);
			}
		return finalScripDetails;
	}
}
