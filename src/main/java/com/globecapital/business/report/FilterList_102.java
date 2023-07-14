package com.globecapital.business.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.utils.DateUtils;

public class FilterList_102 {

	private static JSONArray allowedFilterTypes;
	private static JSONArray allowedTaxFilterTypes;
	private static JSONArray allowedFilterByList;
	private static JSONArray allowedSortByList;

	static {
		allowedFilterTypes = new JSONArray();
		allowedFilterTypes.put(DeviceConstants.LAST_THIRTY_DAYS);
		allowedFilterTypes.put(DeviceConstants.QUARTERLY);
		allowedFilterTypes.put(DeviceConstants.HALF_YEARLY);
		allowedFilterTypes.put(DeviceConstants.YEARLY);
	}
	static {
		allowedTaxFilterTypes = new JSONArray();
		allowedTaxFilterTypes.put(DeviceConstants.FINANCIAL_YEAR+DateUtils.getFinancialYear());
		allowedTaxFilterTypes.put(DeviceConstants.FINANCIAL_YEAR+DateUtils.getPreviousFinancialYear());
	}

	public static JSONObject getFilterTypes(String reportType, JSONObject filterObj) throws JSONException, ParseException {
		JSONObject advancedFilterObject = getAdvancedFilterTypes(reportType, filterObj);
		advancedFilterObject.put(DeviceConstants.DATE_FILTER, getDateFilterTypes(reportType, filterObj));
		return advancedFilterObject;
	}

	public static JSONObject getDateFilterTypes(String reportType, JSONObject filterObj) throws ParseException {

		String optedFilter = filterObj.optString(DeviceConstants.DATE_FILTER);
		String fromDate = filterObj.optString(DeviceConstants.FROM_DATE);
		String toDate = filterObj.optString(DeviceConstants.TO_DATE);

		JSONObject dateFilterObj = new JSONObject();
		
		if (reportType.equalsIgnoreCase(DeviceConstants.TAX_DERIVATIVE)||
				reportType.equalsIgnoreCase(DeviceConstants.TAX_CURRENCY) ||
				reportType.equalsIgnoreCase(DeviceConstants.TAX_COMMODITY) ||
				reportType.equalsIgnoreCase(DeviceConstants.TAX_EQUITY)) {
			dateFilterObj.put(DeviceConstants.FILTER_LIST, allowedTaxFilterTypes);
			dateFilterObj.put(DeviceConstants.CUSTOM_ENABLED, DeviceConstants.FALSE);
			if (optedFilter.isEmpty()) {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.FINANCIAL_YEAR+DateUtils.getFinancialYear());
			} else {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, optedFilter);
			}
			return dateFilterObj;

		}else if(reportType.equalsIgnoreCase(DeviceConstants.LEDGER_EQ) 
				|| reportType.equalsIgnoreCase(DeviceConstants.LEDGER_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_DERIVATIVE) ||
				reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_CURRENCY) 
				|| reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_DERIVATIVE) ||
				reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_CURRENCY) 
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_EQ) 
				|| reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_DERIVATIVE) ||
				reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_CURRENCY) 
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_COMMODITY)) {
			if(fromDate.isEmpty() || toDate.isEmpty())
				getCustomStartAndEndDateWithFYRestriction(dateFilterObj);
			else {
				setFromAndToDateFromRequest(dateFilterObj, fromDate, toDate);
				dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "true");
			}
		}else if(reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_EQUITY)){
			if(fromDate.isEmpty() || toDate.isEmpty())
				getCustomStartAndEndDateWithoutFYRestriction(dateFilterObj);
			else {
				setFromAndToDateFromRequest(dateFilterObj, fromDate, toDate);
				dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "false");
			}
		}else {
			dateFilterObj.put(DeviceConstants.FILTER_LIST, allowedFilterTypes);
			dateFilterObj.put(DeviceConstants.CUSTOM_ENABLED, DeviceConstants.TRUE);
			if (optedFilter.isEmpty()) {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, DeviceConstants.LAST_THIRTY_DAYS);
			} else {
				dateFilterObj.put(DeviceConstants.OPTED_FILTER, optedFilter);
			}
			if (optedFilter.equalsIgnoreCase(DeviceConstants.CUSTOM)) {

				dateFilterObj.put(DeviceConstants.FROM_DATE, filterObj.getString(DeviceConstants.FROM_DATE));
				dateFilterObj.put(DeviceConstants.TO_DATE, filterObj.getString(DeviceConstants.TO_DATE));

			}
		}
		return dateFilterObj;
	}

	public static JSONObject getAdvancedFilterTypes(String reportType, JSONObject filterObj) {

		JSONObject advancedFilterObject = new JSONObject();
		allowedFilterByList = new JSONArray();
		allowedSortByList = new JSONArray();
		JSONObject alphabetically=new JSONObject();
		alphabetically.put(DeviceConstants.TYPE,GCConstants.ALPHA);
		alphabetically.put(DeviceConstants.DISP_NAME,GCConstants.ALPHABETICALLY);
		JSONObject date=new JSONObject();
		date.put(DeviceConstants.TYPE,GCConstants.DATE_);
		date.put(DeviceConstants.DISP_NAME,GCConstants.DATE);
		JSONObject quantity=new JSONObject();
		quantity.put(DeviceConstants.TYPE,GCConstants.QTY);
		quantity.put(DeviceConstants.DISP_NAME,GCConstants.QUANTITY);
		JSONObject profitNLoss=new JSONObject();
		profitNLoss.put(DeviceConstants.TYPE,GCConstants.PNL);
		profitNLoss.put(DeviceConstants.DISP_NAME,GCConstants.PROFIT_N_LOSS);
		JSONObject marketValue=new JSONObject();
		marketValue.put(DeviceConstants.TYPE,GCConstants.MKTVAL);
		marketValue.put(DeviceConstants.DISP_NAME,GCConstants.MKT_VAL);
		JSONObject totalTax=new JSONObject();
		totalTax.put(DeviceConstants.TYPE,GCConstants.TTL_TX);
		totalTax.put(DeviceConstants.DISP_NAME,GCConstants.TTL_TAX);
		if(reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_DERIVATIVE)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_CURRENCY)
				|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_COMMODITY)) { 
			if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_DERIVATIVE)
					|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_CURRENCY)
					|| reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION_COMMODITY)) {
				allowedFilterByList.put(GCConstants.BUY);
				allowedFilterByList.put(GCConstants.SELL);
				allowedFilterByList.put(GCConstants.FUTURES);
				allowedFilterByList.put(GCConstants.OPTIONS);
			}else {
				allowedFilterByList.put(GCConstants.BUY);
				allowedFilterByList.put(GCConstants.SELL);
			}
			allowedSortByList.put(alphabetically);
			allowedSortByList.put(date);
			allowedSortByList.put(quantity);
		
		}else if (reportType.equalsIgnoreCase(DeviceConstants.LEDGER_EQ)
				|| reportType.equalsIgnoreCase(DeviceConstants.LEDGER_COMMODITY)) {
			allowedFilterByList.put(DeviceConstants.WITHOUT_MARGIN_FILTER);
			allowedFilterByList.put(DeviceConstants.DEBIT);
			allowedFilterByList.put(DeviceConstants.CREDIT);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.HOLDINGS)) {
			allowedFilterByList.put(DeviceConstants.LARGE_CAP_FILTER);
			allowedFilterByList.put(DeviceConstants.MID_CAP_FILTER);
			allowedFilterByList.put(DeviceConstants.SMALL_CAP_FILTER);
			allowedSortByList.put(alphabetically);
			allowedSortByList.put(profitNLoss);
			allowedSortByList.put(marketValue);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.TAX_DERIVATIVE)||
				reportType.equalsIgnoreCase(DeviceConstants.TAX_CURRENCY) ||
				reportType.equalsIgnoreCase(DeviceConstants.TAX_COMMODITY)) {
			allowedFilterByList.put(GCConstants.FUTURES);
			allowedFilterByList.put(GCConstants.OPTIONS);
			allowedSortByList.put(alphabetically);
			allowedSortByList.put(profitNLoss);
			allowedSortByList.put(totalTax);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.TAX_EQUITY)) {
			allowedSortByList.put(alphabetically);
			allowedSortByList.put(totalTax);
		} else if (reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_EQUITY)||
				reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS_EQU)) {
			allowedSortByList.put(alphabetically);
			allowedSortByList.put(profitNLoss);
		}else if(reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_DERIVATIVE)||
				reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS_DER )|| 
				reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS_COM)||
				reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS_CUR)||
				reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_CURRENCY)||
				reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS_COMMODITY)) {
			allowedFilterByList.put(GCConstants.FUTURES);
			allowedFilterByList.put(GCConstants.OPTIONS);
			allowedSortByList.put(alphabetically);
			allowedSortByList.put(profitNLoss);
		}
		 else if (reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_EQ)
				|| reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES_COMMODITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_EQUITY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_DERIVATIVE)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_CURRENCY)
				|| reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL_COMMODITY)) {
			allowedSortByList.put(GCConstants.DATE);
		}
		advancedFilterObject.put(DeviceConstants.FILTER_BY, allowedFilterByList);
		advancedFilterObject.put(DeviceConstants.SORT_BY, allowedSortByList);
		return advancedFilterObject;

	}
	
	private static void getCustomStartAndEndDateWithFYRestriction(JSONObject dateFilterObj) {
		Calendar endCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		Date endDate = endCal.getTime();
		startCal.add(Calendar.MONTH, -1);
		Date startDate;
		if(endCal.get(Calendar.MONTH) == 3) {
			startCal.set(Calendar.MONTH, endCal.get(Calendar.MONTH));
			startCal.set(Calendar.DATE, 1);
			startDate = startCal.getTime();
		}else
			startDate = startCal.getTime();
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		String startDateStr = destinationFormat.format(startDate);
		String endDateStr = destinationFormat.format(endDate);
		dateFilterObj.put(DeviceConstants.FROM_DATE, startDateStr);
		dateFilterObj.put(DeviceConstants.TO_DATE, endDateStr);
		dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "true");
	}
	
	private static void getCustomStartAndEndDateWithoutFYRestriction(JSONObject dateFilterObj) {
		Calendar endCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		Date endDate = endCal.getTime();
		startCal.add(Calendar.MONTH, -1);
		Date startDate;
		startDate = startCal.getTime();
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		String startDateStr = destinationFormat.format(startDate);
		String endDateStr = destinationFormat.format(endDate);
		dateFilterObj.put(DeviceConstants.FROM_DATE, startDateStr);
		dateFilterObj.put(DeviceConstants.TO_DATE, endDateStr);
		dateFilterObj.put(DeviceConstants.IS_FY_CALENDAR, "false");
	}
	
	private static void setFromAndToDateFromRequest(JSONObject dateFilterObj, String fromDate, String toDate) throws ParseException {
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		Date startDate = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT).parse(fromDate);
		Date endDate = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT).parse(toDate);
		String startDateStr = destinationFormat.format(startDate);
		String endDateStr = destinationFormat.format(endDate);
		
		dateFilterObj.put(DeviceConstants.FROM_DATE, startDateStr);
		dateFilterObj.put(DeviceConstants.TO_DATE, endDateStr);
	}
}