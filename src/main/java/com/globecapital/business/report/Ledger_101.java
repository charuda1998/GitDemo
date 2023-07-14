package com.globecapital.business.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.GetLedgerReportAPI;
import com.globecapital.api.gc.backoffice.GetLedgerReportRequest;
import com.globecapital.api.gc.backoffice.GetLedgerReportResponse;
import com.globecapital.api.gc.backoffice.GetLedgerRows;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class Ledger_101 {

	private static Logger log = Logger.getLogger(Ledger.class);

	public static JSONArray getLedgerReports(Session session, String segmentType, JSONObject filterObj)
			throws JSONException, Exception {

		String userId = session.getUserID();
		JSONArray ledgerArray = new JSONArray();
		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		try {
			/*** Get Auth code required for Holdings API by Logging in ***/
			String segment = "";
			int precision = 0;
			List<GetLedgerRows> ledgerRows = new ArrayList<>();
			GetLedgerReportAPI ledgerAPI = new GetLedgerReportAPI();
			GetLedgerReportRequest ledgerRequest = new GetLedgerReportRequest();
			GetLedgerReportResponse ledgerResponse = new GetLedgerReportResponse();
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				segment = GCConstants.EQUITY;
				precision = 2;
			} else {
				precision = 4;
				segment = GCConstants.COMMODITY;
			}
			JSONObject reportDates = FilterType.getFilterDates(filterObj.getString(DeviceConstants.DATE_FILTER),
					filterObj);
			String currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);
			ledgerRequest.setClientCode(userId);
			ledgerRequest.setToken(GCAPIAuthToken.getAuthToken());
			if (filterBy.toString().contains(DeviceConstants.WITHOUT_MARGIN_FILTER)) {
				ledgerRequest.setEntryType("WM");	//Contains only the useful records of Ledger (WM- Without Margin Type)
			} else {
				ledgerRequest.setEntryType("A");	//Contains all the records of the Ledger. Includes spam/unwanted records from broker
			}
			ledgerRequest.setSegment(segment);
			ledgerRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
			ledgerRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));

			ledgerRequest.setYear(currentFinancialYear);
			ledgerResponse = ledgerAPI.get(ledgerRequest, GetLedgerReportResponse.class, session.getAppID(),DeviceConstants.LEDGER_REPORT_L);
			if (ledgerResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				GetLedgerReportAPI newLedgerAPI = new GetLedgerReportAPI();
				ledgerRequest.setToken(GCAPIAuthToken.getAuthToken());
				ledgerResponse = newLedgerAPI.get(ledgerRequest, GetLedgerReportResponse.class, session.getAppID(),DeviceConstants.LEDGER_REPORT_L);
				if (ledgerResponse.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS))
					ledgerRows = ledgerResponse.getDetails();
			}
			if (ledgerResponse.getMsg().equalsIgnoreCase(MessageConstants.SUCCESS)) {
				
				ledgerRows = ledgerResponse.getDetails();
				Collections.reverse(ledgerRows);
				for (GetLedgerRows row : ledgerRows) {
					String debit=row.getDebit();
					String credit=row.getCredit();
					JSONObject ledgerObject = new JSONObject();
					if(filterBy.length() == 0 || (filterBy.toString().contains(DeviceConstants.DEBIT) && (filterBy.toString().contains(DeviceConstants.CREDIT))))
					{		
					//Do nothing
					}
					else if(filterBy.toString().contains(DeviceConstants.DEBIT) && (!credit.equals("0") && debit.equals("0")))
					{			
					continue;
					}
					else if(filterBy.toString().contains(DeviceConstants.CREDIT) && (!debit.equals("0") && credit.equals("0")))
					{			
					continue;
					}
					
					ledgerObject.put(DeviceConstants.REPORT_DATE, row.getLedDate());
					if(debit.equals("0")) {
						ledgerObject.put(DeviceConstants.CREDIT_OR_DEBIT,
								PriceFormat.priceToRupee(String.valueOf(Double.parseDouble(credit)), precision));
					}else {
						ledgerObject.put(DeviceConstants.CREDIT_OR_DEBIT,
								PriceFormat.priceToRupee(String.valueOf(Double.parseDouble(debit)*-1), precision));
					}
					ledgerObject.put(DeviceConstants.BALANCE, PriceFormat.priceToRupee(row.getBalance(), precision));
					ledgerObject.put(DeviceConstants.DESCRIPTION, row.getNarration());
					ledgerArray.put(ledgerObject);
				}
			}	
		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return ledgerArray;
	}

}
