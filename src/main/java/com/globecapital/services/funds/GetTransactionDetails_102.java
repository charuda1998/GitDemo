package com.globecapital.services.funds;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.business.order.GetFundHistory;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;
import com.msf.cmots.helper.CMOTSHelper;

public class GetTransactionDetails_102 extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcomsRequest, GCResponse gcomsResponse) throws Exception {

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
			String toDate, String sortOrder, String sortBy) throws SQLException, ParseException, RequestFailedException {

		JSONArray transactionDataArray = new JSONArray();
		JSONArray filter = filterObj.getJSONArray(DeviceConstants.FILTER_BY);
		if(sortOrder.isEmpty())
			sortOrder = DeviceConstants.DESCENDING;
		if(sortBy.isEmpty())
			sortBy = DeviceConstants.DATE;
		
		ArrayList<String> status = new ArrayList<String>();
		transactionDataArray=GetFundHistory.getFundDetails(session,
				CMOTSHelper.formatDate(fromDate,DeviceConstants.FROM_DATE_FORMAT,DeviceConstants.TO_DATE_FORMAT),
				CMOTSHelper.formatDate(toDate,DeviceConstants.FROM_DATE_FORMAT,DeviceConstants.TO_DATE_FORMAT),filter);
	    
	    return sortArrByDate(transactionDataArray, sortOrder);
	}

	private static JSONArray sortArrByDate(JSONArray toBeSorted , String sortOrder) {
		return GetFundHistory.sortArray(toBeSorted, DeviceConstants.DATE_S, sortOrder);
	}

}
