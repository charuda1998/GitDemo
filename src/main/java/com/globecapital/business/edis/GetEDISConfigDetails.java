package com.globecapital.business.edis;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.edis.FetchEDISQuantityAPI;
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
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;

public class GetEDISConfigDetails {
	
	static SecureRandom rand = new SecureRandom();
	
	private static Logger log = Logger.getLogger(GetEDISConfigDetails.class);
	
	public static JSONObject getEDISApprovalDetails(Session session, JSONArray approvalDetails, boolean isApproveAll, boolean isHoldingsFlow,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws Exception {
		JSONObject edisDetails = new JSONObject();
		List<Map<SymbolRow, Integer>> approvalQuantityList = new ArrayList<>();
		JSONObject edisConfigJSON = new JSONObject();
		if(session.getUserInfo().has(UserInfoConstants.EDIS_CONFIG_DETAILS))
			edisConfigJSON = new JSONObject(AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), session.getUserInfo().getString(UserInfoConstants.EDIS_CONFIG_DETAILS)));
		GetEDISConfigResponseObject configResponseObject = EDISHelper.fetchEDISConfigDetails(session, edisConfigJSON,servletContext,gcRequest,gcResponse);
		if(!isApproveAll || configResponseObject.getDepository().equalsIgnoreCase(DeviceConstants.DEPOSITORY_NSDL)) {
			for(int i = 0; i < approvalDetails.length(); i++ ) {
				String symbolToken = approvalDetails.getJSONObject(i).getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
				int iQty = Integer.parseInt(approvalDetails.getJSONObject(i).getString(DeviceConstants.QTY).replace(",", ""));
				SymbolRow symbolRow = SymbolMap.getSymbolRow(symbolToken);
				if(isHoldingsFlow)
					getEDISQuantityDetailsForHoldings(session, symbolRow, iQty, edisDetails, approvalQuantityList,servletContext,gcRequest,gcResponse);
				else
					getEDISQuantityDetails(session, symbolRow, iQty, edisDetails, approvalQuantityList,servletContext,gcRequest,gcResponse);
			}
		}else {
			getEDISQuantityDetailsForApproveAll(session, edisDetails, approvalQuantityList,servletContext,gcRequest,gcResponse);
		}
		if(!approvalQuantityList.isEmpty()) {
			if(configResponseObject.getDepository().equals(DeviceConstants.DEPOSITORY_CDSL))
				frameEDISUrlForCDSL(configResponseObject, session, edisDetails, approvalQuantityList);
			else {
				Entry<SymbolRow, Integer> entry = approvalQuantityList.get(0).entrySet().iterator().next();
				frameEDISUrlforNSDL(configResponseObject, session, edisDetails, entry.getKey(), entry.getValue());
			}
		}else {
			edisDetails.put(DeviceConstants.URL, "--");
		}
		return edisDetails;
	}

	private static void getEDISQuantityDetailsForHoldings(Session session, SymbolRow symbolRow, int iQty,JSONObject edisDetails, 
			List<Map<SymbolRow, Integer>> approvalQuantityList,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws Exception {
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
			GetEDISScripDetailsObject scripDetail = scripDetailsList.get(i);
			if(scripDetail.getISINCode().equals(symbolRow.getISIN())){
				Map<SymbolRow, Integer> approvalMap = new HashMap<>();
				if(iQty > 0) {
					approvalMap.put(symbolRow, iQty);
					approvalQuantityList.add(approvalMap);
				}
			}
		}
	}

	private static void frameEDISUrlForCDSL(GetEDISConfigResponseObject configResponseObject , Session session, JSONObject edisDetails, List<Map<SymbolRow, Integer>> approvalQuantityList) throws JSONException, UnsupportedEncodingException {
		String managerIP = session.getUserInfo().getString(UserInfoConstants.MANAGER_IP);
		String userCode = session.getUserInfo().getString(UserInfoConstants.USER_CODE);
		String userID = session.getUserID();
		String groupID = session.getGroupId();
		String sessionID = session.getjSessionIDWithoutEncryption();
		String exchCode = "NSE"; 
//				symbolRow.getExchange();
		String mktSegId = "1";
//				symbolRow.getMktSegId()
		String scripDetail = formEDISApprovalObject(approvalQuantityList);
		String depository = configResponseObject.getDepository();
		String url = configResponseObject.getUrl();
		String beneficiary = configResponseObject.getBeneficiaryId();
		String dpId = beneficiary.split("#")[0];
		String completeURL = "";
		String requestId = EDISHelper.generateRandomString(4);
		if(depository.equals(DeviceConstants.DEPOSITORY_CDSL)) {
			if(dpId.charAt(0) == '1' && dpId.charAt(1) == '2')
				dpId = dpId.substring(2,dpId.length());
			completeURL = url+"?enct=";
			String rawContent = "userCode="+userCode+"&mangerIP="+managerIP+"&sessionID="+sessionID+"&channel=MOB&isin=&isinName=&exchangeCd="+exchCode+"&product=1"
					+"&instrument=Equity&quantity=&dpId="+dpId+"&clientId="+beneficiary.replace("#", "")+"&depository=CDSL&productCode=MOBILE API"
					+"&MarketSegId="+mktSegId+"&ScripDetails=["+scripDetail+"]&ReqId="+requestId+"&userId="+userID+"&groupId="+groupID;
			log.info(completeURL+rawContent);
			completeURL+=new String(Base64.getEncoder().encode(rawContent.getBytes("UTF-8")));
		}
		edisDetails.put(DeviceConstants.URL, completeURL);
	}
	
	private static void frameEDISUrlforNSDL(GetEDISConfigResponseObject configResponseObject , Session session, JSONObject edisDetails, SymbolRow symbolRow, Integer iQty) throws JSONException, UnsupportedEncodingException {
		String managerIP = session.getUserInfo().getString(UserInfoConstants.MANAGER_IP);
		String userID = session.getUserID();
		String groupID = session.getGroupId();
		String userCode = session.getUserInfo().getString(UserInfoConstants.USER_CODE);
		String sessionID = session.getjSessionIDWithoutEncryption();
		String exchCode = symbolRow.getExchange();
		String depository = configResponseObject.getDepository();
		String url = configResponseObject.getUrl();
		String beneficiary = configResponseObject.getBeneficiaryId();
		String dpId = beneficiary.split("#")[0];
		String completeURL = "";
		String requestId = EDISHelper.generateRandomString(4);
		if(depository.equals(DeviceConstants.DEPOSITORY_NSDL)) {
			completeURL = url+"?enct=";
			String rawContent = "userCode="+userCode+"&mangerIP="+managerIP+"&sessionID="+sessionID+"&channel=MOB&isin="+symbolRow.getISIN()+"&isinName="+symbolRow.getCompanyName().replace("'", "").replace("&", "")+"&exchangeCd="+exchCode+"&product=1"
					+"&instrument=Equity&quantity="+iQty+"&dpId="+dpId+"&clientId="+beneficiary.split("#")[1]+"&depository=NSDL&productCode=MOBILE API"
					+"&MarketSegId="+symbolRow.getMktSegId()+"&ScripDetails=&ReqId="+requestId+"&userId="+userID+"&groupId="+groupID;
			completeURL+=new String(Base64.getEncoder().encode(rawContent.getBytes("UTF-8")));
		} 
		edisDetails.put(DeviceConstants.URL, completeURL);
	}

	private static String formEDISApprovalObject(List<Map<SymbolRow, Integer>> approvalQuantityList) {
		String result = "";
		for(int i = 0; i < approvalQuantityList.size(); i++) {
			Map<SymbolRow, Integer> approvalDetailMap = approvalQuantityList.get(i);
			Entry<SymbolRow, Integer> entry = approvalDetailMap.entrySet().iterator().next();
			SymbolRow symbolRow = entry.getKey();
			String ISIN = "'"+symbolRow.getISIN()+"'";
			String companyName = "'"+symbolRow.getCompanyName().replace("'", "").replace("&", "")+"'";
//			.replace("&", "%26").replace("'", "%27")+"'";
			String qty = "'"+entry.getValue()+"'";
			if(i != approvalQuantityList.size()-1)
				result+= "{'ISIN':"+ISIN+",'Quantity':"+qty+",'ISINName':"+companyName+"},";
			else
				result+= "{'ISIN':"+ISIN+",'Quantity':"+qty+",'ISINName':"+companyName+"}";
		}
		return result;
	}

	private static void getEDISQuantityDetails(Session session, SymbolRow symbolRow, int iQty, JSONObject edisDetails, List<Map<SymbolRow, Integer>> approvalQuantityList,
			ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws JSONException, Exception {
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
		catch(GCException e){
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
				if(iQty <= scripDetail.getTodayFreeQty() || iQty <= scripDetail.getApprovedQuantity() || iQty <= scripDetail.geteDISCheckQty() || scripDetail.getApprovedQuantity() > scripDetail.getTotalFreeQty()) {
//					edisDetails.put(DeviceConstants.CLOSE, scripDetail.getClosePrice());
					return;
				}else {
					if(iQty > scripDetail.getTotalFreeQty()) {
						iQty = scripDetail.getTotalFreeQty() - scripDetail.getTodayFreeQty() - scripDetail.getApprovedQuantity();
					}else {
						iQty = Math.abs(scripDetail.getTodayFreeQty() + scripDetail.getApprovedQuantity() - iQty); 
					}
					Map<SymbolRow, Integer> approvalMap = new HashMap<>();
					if(iQty > 0)
						approvalMap.put(symbolRow, iQty);
					approvalQuantityList.add(approvalMap);
				}
			}
		}
	}
	
	private static void getEDISQuantityDetailsForApproveAll(Session session, JSONObject edisDetails, List<Map<SymbolRow, Integer>> approvalQuantityList,
			ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws JSONException, Exception {
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
			GetEDISScripDetailsObject scripDetail = scripDetailsList.get(i);
			Map<SymbolRow, Integer> approvalMap = new HashMap<>();
			SymbolRow symbolRow = null;
			if(SymbolMap.isValidSymbol(scripDetail.getISINCode()+"_"+ExchangeSegment.NSE_SEGMENT_ID)){
				symbolRow = SymbolMap.getISINSymbolRow(scripDetail.getISINCode()+"_"+ExchangeSegment.NSE_SEGMENT_ID);
			}else if(SymbolMap.isValidSymbol(scripDetail.getISINCode()+"_"+ExchangeSegment.BSE_SEGMENT_ID)){
				symbolRow = SymbolMap.getISINSymbolRow(scripDetail.getISINCode()+"_"+ExchangeSegment.BSE_SEGMENT_ID);
			}
			if(symbolRow!=null) {
				if(scripDetail.getApprovedQuantity() < scripDetail.getTotalFreeQty()) {
					if(scripDetail.getTotalFreeQty() - scripDetail.getApprovedQuantity() - scripDetail.getTodayFreeQty() != 0) {
						approvalMap.put(symbolRow, scripDetail.getTotalFreeQty() - scripDetail.getApprovedQuantity() - scripDetail.getTodayFreeQty());
						approvalQuantityList.add(approvalMap);
					}
				} 	
			}
		}
	}
}


