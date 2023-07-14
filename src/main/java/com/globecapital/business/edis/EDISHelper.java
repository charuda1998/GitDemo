package com.globecapital.business.edis;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.watchlist.CreateWatchlistResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionHelper;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.google.gson.Gson;
import com.msf.log.Logger;

public class EDISHelper {
	
	static SecureRandom rand = new SecureRandom();
	
	private static Logger log = Logger.getLogger(EDISHelper.class);
	
	public static String generateRandomString(int len){
	   SimpleDateFormat sdf = new SimpleDateFormat(DeviceConstants.CURRENT_TIMESTAMP);
	   Date date = new Date();
	   StringBuilder sb = new StringBuilder(len);
	   for(int i = 0; i < len; i++)
	      sb.append(DeviceConstants.POSSIBLE_INPUTS .charAt(rand.nextInt(DeviceConstants.POSSIBLE_INPUTS.length())));
	   return sb.toString().concat(sdf.format(date));
	}
	
	public static GetEDISConfigResponse getEDISConfigDetails(Session session,ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse) throws JSONException, Exception {
		FetchEDISConfigAPI edisConfigAPI = new FetchEDISConfigAPI();
		GetEDISConfigRequest edisConfigRequest = new GetEDISConfigRequest();
		GetEDISConfigResponse edisConfigResponse = new GetEDISConfigResponse();
		edisConfigRequest.setUserID(session.getUserID());
		edisConfigRequest.setGroupId(session.getGroupId());
		edisConfigRequest.setJKey(session.getjKey());
		edisConfigRequest.setJSession(session.getjSessionID());
		try {
		edisConfigResponse = edisConfigAPI.post(edisConfigRequest, GetEDISConfigResponse.class, session.getAppID(),"EdisConfigDetails");
		}
		catch (GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(edisConfigRequest,session, servletContext, gcRequest, gcResponse)) {
            		edisConfigResponse = edisConfigAPI.post(edisConfigRequest, GetEDISConfigResponse.class, session.getAppID(),"EdisConfigDetails");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}
		return edisConfigResponse;
	}
	
	public static GetEDISConfigResponseObject fetchEDISConfigDetails(Session session, JSONObject edisConfigJSON,ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse)
			throws JSONException, Exception {
		GetEDISConfigResponseObject configResponseObject = null;
		if(edisConfigJSON.has(FTConstants.DEPOSITORY_S)) {
			edisConfigJSON.put(FTConstants.DEPOSITORY , edisConfigJSON.getString(FTConstants.DEPOSITORY_S));
			edisConfigJSON.put(FTConstants.URL , edisConfigJSON.getString(FTConstants.URL_S));
			edisConfigJSON.put(FTConstants.BENEFICIARY_ID , edisConfigJSON.getString(FTConstants.BENEFICIARY_ID_S));
			edisConfigJSON.remove(FTConstants.DEPOSITORY_S);
			edisConfigJSON.remove(FTConstants.URL_S);
			edisConfigJSON.remove(FTConstants.BENEFICIARY_ID_S);
			
			configResponseObject = new Gson().fromJson(edisConfigJSON.toString() , GetEDISConfigResponseObject.class);
		}else {
			GetEDISConfigResponse edisConfigResponse = EDISHelper.getEDISConfigDetails(session,servletContext,gcRequest,gcResponse);
			configResponseObject = edisConfigResponse.getResponseObject();
			JSONObject userInfo = session.getUserInfo();
			try {
				userInfo.put(UserInfoConstants.EDIS_CONFIG_DETAILS, AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key") ,new JSONObject(userInfo).toString()));
				session.setUserInfo(configResponseObject);
				SessionHelper.updateUserInfo(session);
			} catch (JSONException | AppConfigNoKeyFoundException | GeneralSecurityException | SQLException e) {
				log.info(e);
			}
		}
		return configResponseObject;
	}
	
	public static List<JSONObject> getEDISQuantityDetailsForPreFill(Session session,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws JSONException, Exception {
		List<JSONObject> edisDetails = new ArrayList<>();
		FetchEDISQuantityAPI edisQuantityAPI = new FetchEDISQuantityAPI();
		GetEDISQuantityRequest edisQuantityRequest = new GetEDISQuantityRequest();
		GetEDISQuantityResponse edisQuantityResponse = new GetEDISQuantityResponse();
		edisQuantityRequest.setUserID(session.getUserID());
		edisQuantityRequest.setGroupId(session.getGroupId());
		edisQuantityRequest.setJKey(session.getjKey());
		edisQuantityRequest.setJSession(session.getjSessionID());
		edisQuantityRequest.setMktSegId(FTConstants.EQ_COMBINED_SEGMENT_ID);
		edisQuantityRequest.setToken(FTConstants.ALL_TOKENS);
		try {
		edisQuantityResponse = edisQuantityAPI.post(edisQuantityRequest, GetEDISQuantityResponse.class, session.getAppID(),"EDISQuantityDetails");
		}
		catch(GCException e) {
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
			GetEDISScripDetailsObject scripDetailObj = scripDetailsList.get(i);
			if(scripDetailObj.getTotalFreeQty() - scripDetailObj.getApprovedQuantity() > 0 && scripDetailObj.geteDISDPQty() - scripDetailObj.getApprovedQuantity() > 0) {
				if(SymbolMap.isValidSymbol(scripDetailObj.getISINCode()+"_"+ExchangeSegment.NSE_SEGMENT_ID)) {
					SymbolRow symRow = SymbolMap.getISINSymbolRow(scripDetailObj.getISINCode()+"_"+ExchangeSegment.NSE_SEGMENT_ID);
					JSONObject symbolDetail = new JSONObject();
					symbolDetail.put(DeviceConstants.QTY, scripDetailObj.geteDISDPQty() - scripDetailObj.getApprovedQuantity());
					symbolDetail.put(DeviceConstants.SYMBOL, symRow.getSymbol());
					symbolDetail.put(DeviceConstants.TOKEN, symRow.getSymbolToken());
					symbolDetail.put(DeviceConstants.TOTAL_QTY, scripDetailObj.geteDISDPQty() - scripDetailObj.getApprovedQuantity());
					edisDetails.add(symbolDetail);
				}else if(SymbolMap.isValidSymbol(scripDetailObj.getISINCode()+"_"+ExchangeSegment.BSE_SEGMENT_ID)) {
					SymbolRow symRow = SymbolMap.getISINSymbolRow(scripDetailObj.getISINCode()+"_"+ExchangeSegment.BSE_SEGMENT_ID);
					JSONObject symbolDetail = new JSONObject();
					symbolDetail.put(DeviceConstants.QTY, scripDetailObj.geteDISDPQty() - scripDetailObj.getApprovedQuantity());
					symbolDetail.put(DeviceConstants.SYMBOL, symRow.getSymbol());
					symbolDetail.put(DeviceConstants.TOKEN, symRow.getSymbolToken());
					symbolDetail.put(DeviceConstants.TOTAL_QTY, scripDetailObj.geteDISDPQty() - scripDetailObj.getApprovedQuantity());
					edisDetails.add(symbolDetail);
				}
			}
		}
		return edisDetails;
	}
}
