package com.globecapital.services.funds;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.funds.GetPayoutTransactions;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.DateUtils;
import com.msf.cmots.helper.CMOTSHelper;

public class GetTransactionDetails extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcomsRequest, GCResponse gcomsResponse) throws Exception {
		// TODO Auto-generated method stub

		String clientId = gcomsRequest.getSession().getUserID();
		String fromDate = DateUtils.formatDate(
				gcomsRequest.getOptFromData("fromDate", DateUtils.getCurrentDateTime("dd MMM yyyy")), "dd MMM yyyy",
				"yyyy-MM-dd");
		String toDate = DateUtils.formatDate(
				gcomsRequest.getOptFromData("toDate", DateUtils.getCurrentDateTime("dd MMM yyyy")), "dd MMM yyyy",
				"yyyy-MM-dd");

		JSONObject filterObj = gcomsRequest.getData().getJSONObject("filterObj");
		String sortOrder = filterObj.getString("sortOrder");
		String sortBy = filterObj.getString("sortBy");

		JSONArray result = getFinalResponse(gcomsRequest.getSession(), clientId, filterObj, fromDate, toDate, sortOrder,
				sortBy);

		if (result.length() > 0)
			gcomsResponse.addToData("transactions", result);
		else
			gcomsResponse.setNoDataAvailable();

	}

	private static JSONArray getFinalResponse(Session session, String clientId, JSONObject filterObj, String fromDate,
			String toDate, String sortOrder, String sortBy) throws SQLException, ParseException {

		JSONArray result = new JSONArray();
		JSONArray filter = filterObj.getJSONArray("filterBy");

		boolean PAYIN = false;
		boolean PAYOUT = false;
		boolean SUCCESS = false;
		boolean FAILED = false;
		boolean PROCESSED = false;
		boolean INITIATE = false;
		boolean PENDING = false;
		boolean CANCELLED = false;
		boolean ALL = false;

		for (int i = 0; i < filter.length(); i++) {
			if (filter.getString(i).equalsIgnoreCase("PAY IN"))
				PAYIN = true;
			else if (filter.getString(i).equalsIgnoreCase("PAY OUT"))
				PAYOUT = true;
			else if (filter.getString(i).equalsIgnoreCase("SUCCESS"))
				SUCCESS = true;
			else if (filter.getString(i).equalsIgnoreCase("FAILED"))
				FAILED = true;
			else if (filter.getString(i).equalsIgnoreCase("PROCESSED"))
				PROCESSED = true;
			else if (filter.getString(i).equalsIgnoreCase("PENDING"))
				PENDING = true;
			else if (filter.getString(i).equalsIgnoreCase("INITIATE"))
				INITIATE = true;
			else if (filter.getString(i).equalsIgnoreCase("CANCELLED"))
				CANCELLED = true;
			else
				ALL = true;
		}

		ArrayList<String> status = new ArrayList<String>();
		if (INITIATE)
			status.add("INITIATE");
		if (SUCCESS)
			status.add("SUCCESS");
		if (FAILED)
			status.add("FAILED");
		if (PROCESSED)
			status.add("PROCESSED");
		if (PENDING)
			status.add("PENDING");
		if (CANCELLED)
			status.add("CANCELLED");
		if (ALL)
			status.add("All");

	    result = getPayoutData(fromDate, toDate, session, status, sortOrder, SUCCESS, FAILED);

		return result;
	}

	private static JSONArray getPayoutData(String fromDate, String toDate, Session session, ArrayList<String> status,
			String sortOrder, boolean SUCCESS, boolean FAILED) {
		JSONArray result = new JSONArray();

		if (status.contains("All")) {
			status.remove("All");
			status.add("SUCCESS");
			status.add("FAILED");
			status.add("PROCESSED");
			status.add("PENDING");
			status.add("CANCELLED");
		}

		JSONArray jarr = GetPayoutTransactions.getTransactions(session,
				CMOTSHelper.formatDate(fromDate, "yyyy-MM-dd", "dd/MM/yyyy"),
				CMOTSHelper.formatDate(toDate, "yyyy-MM-dd", "dd/MM/yyyy"));

		if (status.size() > 0) {
			jarr = GetPayoutTransactions.getStatusBasedArray(jarr, status);
		}

		result = GetPayoutTransactions.sortArray(jarr, "date", sortOrder.toLowerCase());
		return result;
	}

	private static int compareDateWithCurrentDate(String date1) throws ParseException {
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = sdformat.parse(date1);
		Date d2 = sdformat.parse(sdformat.format(new Date()));

		if (d1.compareTo(d2) > 0) {
			return 1;
		} else if (d1.compareTo(d2) < 0) {
			return 2;
		} else if (d1.compareTo(d2) == 0) {
			return 0;
		}

		return 0;
	}

}
