package com.globecapital.services.order;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class ResolveDiscrepancy extends SessionService {

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
		GetResolvedDiscrepancyResponse discrepancyRes = new GetResolvedDiscrepancyResponse();

		for (int i = 0; i < transactionArr.length(); i++) {
			JSONObject transObj = transactionArr.getJSONObject(i);
			String sQty = transObj.getString(DeviceConstants.QTY);
			String sPrice = transObj.getString(DeviceConstants.PRICE);
			String sTransDate = transObj.getString(DeviceConstants.TRANS_DATE);

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

			discrepancyRes = discrepancyAPI.get(discrepancyReq, GetResolvedDiscrepancyResponse.class,
					session.getAppID(),DeviceConstants.RESOLVED_DISCRIPANCY_L);

			if(discrepancyRes.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				discrepancyReq.setToken(GCAPIAuthToken.getAuthToken());
				discrepancyRes = discrepancyAPI.get(discrepancyReq, GetResolvedDiscrepancyResponse.class, session.getAppID()
						,DeviceConstants.RESOLVED_DISCRIPANCY_L);
			}
			
			if (!discrepancyRes.getStatus().equalsIgnoreCase("True"))
				throw new GCException(InfoIDConstants.DYNAMIC_MSG,
						InfoMessage.getInfoMSG("info_msg.order.discrepancy_added_unsuccessful"));

		}

		if (discrepancyRes.getStatus().equalsIgnoreCase("True"))
			gcResponse.addToData(DeviceConstants.MSG,
					InfoMessage.getInfoMSG("info_msg.order.discrepancy_placed_successful"));
		else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG,
					InfoMessage.getInfoMSG("info_msg.order.discrepancy_added_unsuccessful"));
	}

}
