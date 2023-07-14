package com.globecapital.business.edis;

import java.util.List;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.edis.FetchEDISConfigAPI;
import com.globecapital.api.ft.edis.FetchEDISQuantityAPI;
import com.globecapital.api.ft.edis.GetEDISConfigRequest;
import com.globecapital.api.ft.edis.GetEDISConfigResponse;
import com.globecapital.api.ft.edis.GetEDISConfigResponseObject;
import com.globecapital.api.ft.edis.GetEDISQuantityRequest;
import com.globecapital.api.ft.edis.GetEDISQuantityResponse;
import com.globecapital.api.ft.edis.GetEDISScripDetailsObject;
import com.globecapital.api.ft.edis.InsertEDISReqRespAPI;
import com.globecapital.api.ft.edis.InsertEDISReqRespRequest;
import com.globecapital.api.ft.edis.InsertEDISReqRespResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;

public class InsertEDISReqResponseDetails {
	
	private static Logger log = Logger.getLogger(InsertEDISReqResponseDetails.class);
	
	public static JSONObject insertEDISApprovalDetails(Session session, SymbolRow symbolRow, String requestQty,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws Exception {
		JSONObject edisDetails = new JSONObject();
		JSONObject apiResponse = new JSONObject();
		JSONObject edisConfigJSON = new JSONObject();
		if(session.getUserInfo().has(UserInfoConstants.EDIS_CONFIG_DETAILS))
			edisConfigJSON = new JSONObject(AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), session.getUserInfo().getString(UserInfoConstants.EDIS_CONFIG_DETAILS)));
		GetEDISConfigResponseObject configResponseObject = EDISHelper.fetchEDISConfigDetails(session, edisConfigJSON,servletContext,gcRequest,gcResponse);
		int iQty = Integer.parseInt(requestQty);
		getEDISQuantityDetails(session, symbolRow, iQty, edisDetails,servletContext,gcRequest,gcResponse);
		if(configResponseObject.getDepository().equals(DeviceConstants.DEPOSITORY_CDSL)) {
			Boolean responseStatus = frameInsertEDISRequest(session, symbolRow, edisDetails, configResponseObject, iQty,servletContext,gcRequest,gcResponse);
			apiResponse.put(DeviceConstants.STATUS, responseStatus.toString());
		}
		return apiResponse;
	}

	private static Boolean frameInsertEDISRequest(Session session, SymbolRow symbolRow, JSONObject edisDetails, GetEDISConfigResponseObject configResponseObject,
			int iQty,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws JSONException, Exception {
		InsertEDISReqRespAPI edisReqAPI = new InsertEDISReqRespAPI();
		InsertEDISReqRespRequest edisReq = new InsertEDISReqRespRequest();
		InsertEDISReqRespResponse edisResp = new InsertEDISReqRespResponse();
		edisReq.setUserID(session.getUserID());
		edisReq.setProdCode(DeviceConstants.MOBILE_API);
		edisReq.setGroupId(session.getGroupId());
		edisReq.setJKey(session.getjKey());
		edisReq.setJSession(session.getjSessionID());
		edisReq.setApprovedFreeQty(edisDetails.getInt(DeviceConstants.QTY));
		edisReq.setDepository(configResponseObject.getDepository());
		String dpId = configResponseObject.getBeneficiaryId().split("#")[0];
		if(configResponseObject.getDepository().equals(DeviceConstants.DEPOSITORY_CDSL)) {
			if(dpId.charAt(0) == '1' && dpId.charAt(1) == '2')
				dpId = dpId.substring(2,dpId.length());
		}	
		edisReq.setDPId(dpId);
		edisReq.setISIN(symbolRow.getISIN());
		edisReq.setMktSegId(Integer.parseInt(symbolRow.getMktSegId()));
		edisReq.setOrderQty(iQty);
		edisReq.setRequestType(DeviceConstants.ORDER_ENTRY);
		edisReq.setToken(Integer.parseInt(symbolRow.getSymbolToken().split("_")[0]));
		edisReq.setTotalAvailableQty(edisDetails.getInt(DeviceConstants.TOTAL_QTY));
		edisReq.setUserCode(session.getUserInfo().getString(UserInfoConstants.USER_CODE));
		try {
		edisResp = edisReqAPI.post(edisReq, InsertEDISReqRespResponse.class, session.getAppID(),"InsertEDISReqResponseDetails");
		}
		catch(GCException e) {
			if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(edisReq,session, servletContext, gcRequest, gcResponse)) {
            		edisResp = edisReqAPI.post(edisReq, InsertEDISReqRespResponse.class, session.getAppID(),"InsertEDISReqResponseDetails");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            }
			else 
				throw new RequestFailedException();
		}
		return edisResp.getResponseStatus();
	}

	private static void getEDISQuantityDetails(Session session, SymbolRow symbolRow, int iQty, JSONObject edisDetails,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws JSONException, Exception {
		FetchEDISQuantityAPI edisQuantityAPI = new FetchEDISQuantityAPI();
		GetEDISQuantityRequest edisQuantityRequest = new GetEDISQuantityRequest();
		GetEDISQuantityResponse edisQuantityResponse = new GetEDISQuantityResponse();
		edisQuantityRequest.setUserID(session.getUserID());
		edisQuantityRequest.setGroupId(session.getGroupId());
		edisQuantityRequest.setJKey(session.getjKey());
		edisQuantityRequest.setJSession(session.getjSessionID());
		edisQuantityRequest.setMktSegId(symbolRow.getMktSegId());
		edisQuantityRequest.setToken(symbolRow.getSymbolToken().split("_")[0]);
		try {
		edisQuantityResponse = edisQuantityAPI.post(edisQuantityRequest, GetEDISQuantityResponse.class, session.getAppID(),"EDISQuantityDetails");
		}
		catch (GCException e) {
			if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(edisQuantityRequest,session, servletContext, gcRequest, gcResponse)) {
            		edisQuantityResponse = edisQuantityAPI.post(edisQuantityRequest, GetEDISQuantityResponse.class, session.getAppID(),"EDISQuantityDetails");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            }
			else 
				throw new RequestFailedException();
		}
		List<GetEDISScripDetailsObject> scripDetailsList = edisQuantityResponse.getResponseObject().getScripDetailList();
		for(int i = 0; i < scripDetailsList.size(); i++) {
			GetEDISScripDetailsObject scripDetail = scripDetailsList.get(i);
			if(scripDetail.getISINCode().equals(symbolRow.getISIN())){
				edisDetails.put(DeviceConstants.QTY, scripDetail.getApprovedQuantity());
				edisDetails.put(DeviceConstants.TOTAL_QTY, scripDetail.geteDISDPQty());
			}
		}
	}
	
}
