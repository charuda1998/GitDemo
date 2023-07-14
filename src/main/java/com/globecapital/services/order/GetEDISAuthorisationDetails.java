package com.globecapital.services.order;

import java.security.GeneralSecurityException;
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
import com.globecapital.business.edis.EDISHelper;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ProductType;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;


public class GetEDISAuthorisationDetails extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		
		Session session = gcRequest.getSession();
		JSONObject userInfoObj = session.getUserInfo();
		
		JSONObject orderDetails = new JSONObject();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		
		int iQty = Integer.parseInt(gcRequest.getFromData(DeviceConstants.QTY));
		
		String POAStatus = userInfoObj.getString(UserInfoConstants.POA_STATUS);
		
		String productType = gcRequest.getFromData(DeviceConstants.PRODUCT_TYPE);
		
		int modifyApprovedQty = Integer.parseInt(gcRequest.getOptFromData(DeviceConstants.MODIFY_APP_QTY,"0"));

		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		
		String isin = symRow.getISIN();
		
		setEDISAuthorisationFlags(session, POAStatus, orderDetails, isin, iQty, productType, modifyApprovedQty,getServletContext(),gcRequest,gcResponse);
		
		gcResponse.setData(orderDetails);
	}

	private static boolean getEDISQuantityDetails(Session session, String isin, int iQty, int modifyApprovedQty,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws JSONException, Exception {
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
			if(scripDetail.getISINCode().equals(isin)) {
				if(iQty <= scripDetail.getTodayFreeQty() || iQty <= scripDetail.getApprovedQuantity() || iQty <= scripDetail.geteDISCheckQty() || scripDetail.getApprovedQuantity() > scripDetail.getTotalFreeQty()) {
					return false;
				}
				
				if(iQty > scripDetail.getTotalFreeQty()) {
					iQty = scripDetail.getTotalFreeQty() + modifyApprovedQty- scripDetail.getTodayFreeQty() - scripDetail.getApprovedQuantity();
				}
				
				if(iQty - modifyApprovedQty - scripDetail.getApprovedQuantity() - scripDetail.getTodayFreeQty() > 0 )
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	private void setEDISAuthorisationFlags(Session session, String POAStatus, JSONObject orderDetails, String isin, int iQty, String productType,
			int modifyApprovedQty,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse)throws JSONException, Exception {
		boolean isEDISApplicable = false;
		if(productType.equalsIgnoreCase(ProductType.DELIVERY) && !Boolean.parseBoolean(POAStatus))
			isEDISApplicable = getEDISQuantityDetails(session, isin, iQty, modifyApprovedQty,servletContext,gcRequest,gcResponse);
		orderDetails.put(DeviceConstants.IS_AUTH_REQUIRED, String.valueOf(isEDISApplicable));
		if(!Boolean.parseBoolean(POAStatus) && isEDISApplicable) {
			JSONObject edisConfigJSON = new JSONObject();
			try {
				if(session.getUserInfo().has(UserInfoConstants.EDIS_CONFIG_DETAILS))
					edisConfigJSON = new JSONObject(AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), session.getUserInfo().getString(UserInfoConstants.EDIS_CONFIG_DETAILS)));
			} catch (JSONException | AppConfigNoKeyFoundException | GeneralSecurityException e) {
				log.error(e);
			}
			GetEDISConfigResponseObject configResponseObject = EDISHelper.fetchEDISConfigDetails(session, edisConfigJSON,servletContext,gcRequest,gcResponse);
			if(configResponseObject.getDepository().equalsIgnoreCase(DeviceConstants.DEPOSITORY_CDSL)) {
				orderDetails.put(DeviceConstants.IS_APPROVE_ALL, "false");
				//orderDetails.put(DeviceConstants.IS_APPROVE_SINGLE, "true");
				orderDetails.put(DeviceConstants.OPTED_APPROVAL, DeviceConstants.APPROVE_SINGLE);
				orderDetails.put(DeviceConstants.IS_APPROVE_SINGLE, "true");
				orderDetails.put(DeviceConstants.SHOW_TPIN, "true");
			}else {
				orderDetails.put(DeviceConstants.IS_APPROVE_ALL, "false");
				orderDetails.put(DeviceConstants.IS_APPROVE_SINGLE, "true");
				orderDetails.put(DeviceConstants.OPTED_APPROVAL, DeviceConstants.APPROVE_SINGLE);
				orderDetails.put(DeviceConstants.SHOW_TPIN, "false");
			}
		}else {
			orderDetails.put(DeviceConstants.IS_APPROVE_ALL, "false");
			orderDetails.put(DeviceConstants.IS_APPROVE_SINGLE, "false");
			orderDetails.put(DeviceConstants.OPTED_APPROVAL, DeviceConstants.APPROVE_SINGLE);
			orderDetails.put(DeviceConstants.SHOW_TPIN, "false");
		}
	}
}
