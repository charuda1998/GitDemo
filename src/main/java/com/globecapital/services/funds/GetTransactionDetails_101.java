package com.globecapital.services.funds;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.funds.GetPayoutTransactions;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;
import com.msf.cmots.helper.CMOTSHelper;

public class GetTransactionDetails_101 extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcomsRequest, GCResponse gcomsResponse) throws Exception {
		// TODO Auto-generated method stub

		String clientId = gcomsRequest.getSession().getUserID();
		String fromDate = DateUtils.formatDate(
				gcomsRequest.getOptFromData(DeviceConstants.FROM_DATE, DateUtils.getCurrentDateTime(DeviceConstants.TRANSACTION_DATE_FORMAT)), DeviceConstants.TRANSACTION_DATE_FORMAT,
				DeviceConstants.FROM_DATE_FORMAT);
		String toDate = DateUtils.formatDate(
				gcomsRequest.getOptFromData(DeviceConstants.TO_DATE, DateUtils.getCurrentDateTime(DeviceConstants.TRANSACTION_DATE_FORMAT)), DeviceConstants.TRANSACTION_DATE_FORMAT,
				DeviceConstants.FROM_DATE_FORMAT);

		JSONObject filterObj = gcomsRequest.getData().getJSONObject(DeviceConstants.FILTER_OBJ);
		String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
		String sortBy = filterObj.getString(DeviceConstants.SORT_BY);

		JSONArray result = getFinalResponse(gcomsRequest.getSession(), clientId, filterObj, fromDate, toDate, sortOrder,
				sortBy);

		if (result.length() > 0)
			gcomsResponse.addToData(DeviceConstants.TRANSACTIONS, result);
		else
			gcomsResponse.setNoDataAvailable();

	}

	private static JSONArray getFinalResponse(Session session, String clientId, JSONObject filterObj, String fromDate,
			String toDate, String sortOrder, String sortBy) throws SQLException, ParseException {

		JSONArray result = new JSONArray();
		JSONArray filter = filterObj.getJSONArray(DeviceConstants.FILTER_BY);
		
		ArrayList<String> status = new ArrayList<String>();
		
		if (filter.length()==0) {
			status.add(DeviceConstants.ALL_FILTER);
		}else{
			for (int i = 0; i < filter.length(); i++) {
				if (filter.getString(i).equalsIgnoreCase("SUCCESS")) {
					status.add(DeviceConstants.SUCCESS);}
				if (filter.getString(i).equalsIgnoreCase("FAILED")) {
					status.add(DeviceConstants.FAILED);}
				if (filter.getString(i).equalsIgnoreCase("PENDING")) {
					status.add(DeviceConstants.PENDING);}
				if (filter.getString(i).equalsIgnoreCase("CANCELLED")) {
					status.add(DeviceConstants.CANCELLED);}
			}
		}

	    result = getPayoutData(fromDate, toDate, session, status, sortOrder, filter.toString().contains(DeviceConstants.SUCCESS),
	    		filter.toString().contains(DeviceConstants.FAILED));

		return result;
	}

	private static JSONArray getPayoutData(String fromDate, String toDate, Session session, ArrayList<String> status,
			String sortOrder, boolean SUCCESS, boolean FAILED) {
		JSONArray result = new JSONArray();
		JSONArray transactionArrayData=new JSONArray();
		if (status.contains(DeviceConstants.ALL_FILTER)) {
			status.remove(DeviceConstants.ALL_FILTER);
			status.add(DeviceConstants.SUCCESS);
			status.add(DeviceConstants.FAILED);
			status.add(DeviceConstants.PENDING);
			status.add(DeviceConstants.CANCELLED);
		}
		if (status.size() > 0) {
		transactionArrayData = GetPayoutTransactions.getTransactions(session,
				CMOTSHelper.formatDate(fromDate,DeviceConstants.FROM_DATE_FORMAT,DeviceConstants.TO_DATE_FORMAT),
				CMOTSHelper.formatDate(toDate,DeviceConstants.FROM_DATE_FORMAT,DeviceConstants.TO_DATE_FORMAT));
		}
		if(transactionArrayData.length() > 0) {
			transactionArrayData = GetPayoutTransactions.getStatusBasedArray(transactionArrayData, status);
			result = GetPayoutTransactions.sortArray(transactionArrayData,DeviceConstants.DATE_S, sortOrder.toLowerCase());
		}
		return result;
	}

}
