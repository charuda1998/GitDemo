package com.globecapital.services.order;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetDiscrepancyModifyAPI;
import com.globecapital.api.gc.backoffice.GetDiscrepancyModifyRequest;
import com.globecapital.api.gc.backoffice.GetDiscrepancyModifyResponse;
import com.globecapital.api.gc.backoffice.GetResolvedDiscrepancyAPI;
import com.globecapital.api.gc.backoffice.GetResolvedDiscrepancyRequest;
import com.globecapital.api.gc.backoffice.GetResolvedDiscrepancyResponse;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;

public class ModifyDiscrepancy extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		String sUserID = session.getUserID();
		
		JSONObject scripDetails = gcRequest.getObjectFromData(DeviceConstants.SCRIP_DETAILS);
		String sScripCode = scripDetails.getString(DeviceConstants.SCRIP_CODE);
		String sScripName = scripDetails.getString(DeviceConstants.SCRIP_NAME);
		
		JSONArray transactionArr = gcRequest.getArrayFromData(DeviceConstants.TRANSACTIONS);
		
		String sAddStatus = "", sModifyStatus = "";
		
		for(int i = 0; i < transactionArr.length(); i++)
		{
			JSONObject transObj = transactionArr.getJSONObject(i);
			String sReferenceNo = transObj.getString(DeviceConstants.REFERENCE_NO);
			String sQty = transObj.getString(DeviceConstants.QTY);
			String sPrice = transObj.getString(DeviceConstants.PRICE);
			String sTransDate = transObj.getString(DeviceConstants.TRANS_DATE);
			
			if(sReferenceNo.isEmpty()) //If Reference number is empty, then it is new record to resolve discrepancy
			{
				GetResolvedDiscrepancyRequest discrepancyReq = new GetResolvedDiscrepancyRequest();
				discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
				discrepancyReq.setClientCode(sUserID);
				discrepancyReq.setScripCode(sScripCode);
				discrepancyReq.setScripName(sScripName);
				discrepancyReq.setQty(sQty);
				discrepancyReq.setRate(sPrice);
				discrepancyReq.setTrxnDate(DateUtils.formatDate(sTransDate, DeviceConstants.TRANS_DATE_FORMAT,
						DeviceConstants.TRANS_DATE_API_FORMAT));
				discrepancyReq.setRemarks("ADD");
	
				// TODO: Once it is removed from API, no need to set default value
				discrepancyReq.setBuySell("B");
				discrepancyReq.setTrxnType("B");
	
				GetResolvedDiscrepancyAPI discrepancyAPI = new GetResolvedDiscrepancyAPI();
	
				GetResolvedDiscrepancyResponse discrepancyResAdd = discrepancyAPI.get(discrepancyReq, 
						GetResolvedDiscrepancyResponse.class, session.getAppID(),DeviceConstants.RESOLVED_DISCRIPANCY_L);
				if(discrepancyResAdd.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
					discrepancyResAdd = discrepancyAPI.get(discrepancyReq,GetResolvedDiscrepancyResponse.class, session.getAppID()
							,DeviceConstants.RESOLVED_DISCRIPANCY_L);
				}
				sAddStatus = discrepancyResAdd.getStatus();
	
				if (!sAddStatus.equalsIgnoreCase("True"))
					throw new GCException(InfoIDConstants.DYNAMIC_MSG,
							InfoMessage.getInfoMSG("info_msg.order.discrepancy_added_unsuccessful"));
			}
			else
			{
				GetDiscrepancyModifyRequest discrepancyReq = new GetDiscrepancyModifyRequest();
				discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
				discrepancyReq.setToken(null);
				discrepancyReq.setClientCode(sUserID);
				discrepancyReq.setScripCode(sScripCode);
				discrepancyReq.setScripName(sScripName);
				discrepancyReq.setQty(sQty);
				discrepancyReq.setRate(sPrice);
				discrepancyReq.setTrxnDate(DateUtils.formatDate(sTransDate, DeviceConstants.TRANS_DATE_FORMAT, 
						DeviceConstants.TRANS_DATE_API_FORMAT));
				discrepancyReq.setReferenceNo(sReferenceNo);
				discrepancyReq.setRemarks("MODIFY");
				
				//TODO: Once it is removed from API, no need to set default value
				discrepancyReq.setBuySell("B");  
				discrepancyReq.setTrxnType("B");
				
				GetDiscrepancyModifyAPI discrepancyAPI = new GetDiscrepancyModifyAPI();
				
				GetDiscrepancyModifyResponse discrepancyResModify = discrepancyAPI.get(discrepancyReq, 
						GetDiscrepancyModifyResponse.class, session.getAppID(),"GetDiscrepancyModify");
				
				if(discrepancyResModify.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
					discrepancyResModify = discrepancyAPI.get(discrepancyReq,GetDiscrepancyModifyResponse.class, session.getAppID(),"GetDiscrepancyModify");
				}
				sModifyStatus = discrepancyResModify.getStatus();
				
				if (!sModifyStatus.equalsIgnoreCase("True"))
					throw new GCException(InfoIDConstants.DYNAMIC_MSG,
							InfoMessage.getInfoMSG("info_msg.order.discrepancy_added_unsuccessful"));
			}
		}
		
		if(sModifyStatus.equalsIgnoreCase("True") 
				|| sAddStatus.equalsIgnoreCase("True"))
			gcResponse.addToData(DeviceConstants.MSG,
					InfoMessage.getInfoMSG("info_msg.order.discrepancy_placed_successful"));
		else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, 
					InfoMessage.getInfoMSG("info_msg.order.discrepancy_edit_unsuccessful"));
	}

}
