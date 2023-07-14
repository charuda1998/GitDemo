package com.globecapital.business.quote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.spyder.quote.CurrencyAPI;
import com.globecapital.api.spyder.quote.CurrencyAPIObject;
import com.globecapital.api.spyder.quote.CurrencyAPIRequest;
import com.globecapital.api.spyder.quote.CurrencyAPIResponse;
import com.globecapital.api.spyder.quote.FOAPI;
import com.globecapital.api.spyder.quote.FOAPIObject;
import com.globecapital.api.spyder.quote.FOAPIRequest;
import com.globecapital.api.spyder.quote.FOAPIResponse;
import com.globecapital.api.spyder.quote.FORolloverAPI;
import com.globecapital.api.spyder.quote.FORolloverObject;
import com.globecapital.api.spyder.quote.FORolloverRequest;
import com.globecapital.api.spyder.quote.FORolloverResponse;
import com.globecapital.api.spyder.quote.MCXAPI;
import com.globecapital.api.spyder.quote.MCXAPIObject;
import com.globecapital.api.spyder.quote.MCXAPIRequest;
import com.globecapital.api.spyder.quote.MCXAPIResponse;
import com.globecapital.business.market.AnnouncementCache;
import com.globecapital.business.market.Market;
import com.globecapital.config.AppConfig;
import com.globecapital.config.IndicesMapping;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.db.GCDBPool;
import com.globecapital.db.RedisPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetBalanceSheet;
import com.msf.cmots.api.corporateInfo_v1.GetBonus;
import com.msf.cmots.api.corporateInfo_v1.GetDividend;
import com.msf.cmots.api.corporateInfo_v1.GetExpiry;
import com.msf.cmots.api.corporateInfo_v1.GetFinancialEquity;
import com.msf.cmots.api.corporateInfo_v1.GetKeyRatios;
import com.msf.cmots.api.corporateInfo_v1.GetOptionChain;
import com.msf.cmots.api.corporateInfo_v1.GetProfitAndLoss;
import com.msf.cmots.api.corporateInfo_v1.GetResults;
import com.msf.cmots.api.corporateInfo_v1.GetRights;
import com.msf.cmots.api.corporateInfo_v1.GetSplits;
import com.msf.cmots.api.data_v1.Announcement;
import com.msf.cmots.api.data_v1.AnnouncementList;
import com.msf.cmots.api.data_v1.BalanceSheet;
import com.msf.cmots.api.data_v1.BalanceSheetBank;
import com.msf.cmots.api.data_v1.BalanceSheetBankList;
import com.msf.cmots.api.data_v1.BalanceSheetList;
import com.msf.cmots.api.data_v1.BalanceSheetNonBank;
import com.msf.cmots.api.data_v1.BalanceSheetNonBankList;
import com.msf.cmots.api.data_v1.Bonus;
import com.msf.cmots.api.data_v1.BonusList;
import com.msf.cmots.api.data_v1.CashFlowRatio;
import com.msf.cmots.api.data_v1.CashFlowRatioList;
import com.msf.cmots.api.data_v1.CompPeerRatioList;
import com.msf.cmots.api.data_v1.CorporateActionsList;
import com.msf.cmots.api.data_v1.Dividend;
import com.msf.cmots.api.data_v1.DividendList;
import com.msf.cmots.api.data_v1.EfficiencyRatio;
import com.msf.cmots.api.data_v1.EfficiencyRatioBank;
import com.msf.cmots.api.data_v1.EfficiencyRatioBankList;
import com.msf.cmots.api.data_v1.EfficiencyRatioList;
import com.msf.cmots.api.data_v1.EfficiencyRatioNonBank;
import com.msf.cmots.api.data_v1.EfficiencyRatioNonBankList;
import com.msf.cmots.api.data_v1.Expiry;
import com.msf.cmots.api.data_v1.ExpiryList;
import com.msf.cmots.api.data_v1.FinancialFNOEquity;
import com.msf.cmots.api.data_v1.FinancialFNOEquityList;
import com.msf.cmots.api.data_v1.FinancialStabilityRatio;
import com.msf.cmots.api.data_v1.FinancialStabilityRatioList;
import com.msf.cmots.api.data_v1.FinancialStabilityRatioNonBank;
import com.msf.cmots.api.data_v1.FinancialStabilityRatioNonBankList;
import com.msf.cmots.api.data_v1.GrowthRatioBank;
import com.msf.cmots.api.data_v1.GrowthRatioBankList;
import com.msf.cmots.api.data_v1.GrowthRatioNonBank;
import com.msf.cmots.api.data_v1.GrowthRatioNonBankList;
import com.msf.cmots.api.data_v1.LiquidityRatioBank;
import com.msf.cmots.api.data_v1.LiquidityRatioBankList;
import com.msf.cmots.api.data_v1.MarginRatio;
import com.msf.cmots.api.data_v1.MarginRatioBank;
import com.msf.cmots.api.data_v1.MarginRatioBankList;
import com.msf.cmots.api.data_v1.MarginRatioList;
import com.msf.cmots.api.data_v1.MarginRatioNonBank;
import com.msf.cmots.api.data_v1.MarginRatioNonBankList;
import com.msf.cmots.api.data_v1.OptionChain;
import com.msf.cmots.api.data_v1.OptionChainList;
import com.msf.cmots.api.data_v1.PerformanceRatio;
import com.msf.cmots.api.data_v1.PerformanceRatioBank;
import com.msf.cmots.api.data_v1.PerformanceRatioBankList;
import com.msf.cmots.api.data_v1.PerformanceRatioList;
import com.msf.cmots.api.data_v1.PerformanceRatioNonBank;
import com.msf.cmots.api.data_v1.PerformanceRatioNonBankList;
import com.msf.cmots.api.data_v1.ProfitAndLoss;
import com.msf.cmots.api.data_v1.ProfitAndLossBank;
import com.msf.cmots.api.data_v1.ProfitAndLossBankList;
import com.msf.cmots.api.data_v1.ProfitAndLossList;
import com.msf.cmots.api.data_v1.ProfitAndLossNonBank;
import com.msf.cmots.api.data_v1.ProfitAndLossNonBankList;
import com.msf.cmots.api.data_v1.Results;
import com.msf.cmots.api.data_v1.ResultsHalfYearlyBank;
import com.msf.cmots.api.data_v1.ResultsHalfYearlyBankList;
import com.msf.cmots.api.data_v1.ResultsHalfYearlyNonBank;
import com.msf.cmots.api.data_v1.ResultsHalfYearlyNonBankList;
import com.msf.cmots.api.data_v1.ResultsList;
import com.msf.cmots.api.data_v1.ResultsQuarterlyBank;
import com.msf.cmots.api.data_v1.ResultsQuarterlyBankList;
import com.msf.cmots.api.data_v1.ResultsQuarterlyNonBank;
import com.msf.cmots.api.data_v1.ResultsQuarterlyNonBankList;
import com.msf.cmots.api.data_v1.ResultsYearlyBank;
import com.msf.cmots.api.data_v1.ResultsYearlyBankList;
import com.msf.cmots.api.data_v1.ResultsYearlyNonBank;
import com.msf.cmots.api.data_v1.ResultsYearlyNonBankList;
import com.msf.cmots.api.data_v1.Rights;
import com.msf.cmots.api.data_v1.RightsList;
import com.msf.cmots.api.data_v1.Splits;
import com.msf.cmots.api.data_v1.SplitsList;
import com.msf.cmots.api.data_v1.ValuationRatio;
import com.msf.cmots.api.data_v1.ValuationRatioBank;
import com.msf.cmots.api.data_v1.ValuationRatioBankList;
import com.msf.cmots.api.data_v1.ValuationRatioList;
import com.msf.cmots.api.data_v1.ValuationRatioNonBank;
import com.msf.cmots.api.data_v1.ValuationRatioNonBankList;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class AdvanceQuote {

	private static Logger log = Logger.getLogger(AdvanceQuote.class);
	private static boolean isTimedOut = false;

	public static JSONObject getBalanceSheet(JSONObject symObj, JSONObject filterObj, String sType, String sAppID)
			throws CMOTSException {
		JSONObject finalObj = new JSONObject();

		String sOptedFilter = filterObj.getString(DeviceConstants.OPTED_FILTER);

		if (sOptedFilter.isEmpty())
			sOptedFilter = DeviceConstants.PERIOD_QUARTERLY;

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		int precision = symRow.getPrecisionInt();

		String sCoCode = symRow.getCMCoCode();

		int index = sCoCode.indexOf(".");
		sCoCode = sCoCode.substring(0, index);

		GetBalanceSheet balanceSheetObj = new GetBalanceSheet(getAppIDForLogging(sAppID));
		balanceSheetObj.setCoCode(sCoCode);
		balanceSheetObj.setFinFormat(sType);

		BalanceSheetList balanceSheetList = new BalanceSheetList();

		JSONArray balanceSheetArr = new JSONArray();

		RedisPool redisPool = new RedisPool();

		try {
			if (sOptedFilter.equals(DeviceConstants.PERIOD_QUARTERLY)) {
				try {
					if (redisPool.isExists(RedisConstants.BALANCE_SHEET_QUARTERLY))
						balanceSheetList = new Gson().fromJson(
								redisPool.getValue(RedisConstants.BALANCE_SHEET_QUARTERLY), BalanceSheetList.class);
					else {
						balanceSheetList = balanceSheetObj.invokeQuarterlyBalanceSheet();
						redisPool.setValues(RedisConstants.BALANCE_SHEET_QUARTERLY,
								new Gson().toJson(balanceSheetList));
					}
				} catch (Exception e) {
					log.error(e);
					balanceSheetList = balanceSheetObj.invokeQuarterlyBalanceSheet();
				}
			} else if (sOptedFilter.equals(DeviceConstants.PERIOD_YEARLY)) {
				try {
					if (redisPool.isExists(RedisConstants.BALANCE_SHEET_YEARLY))
						balanceSheetList = new Gson().fromJson(RedisConstants.BALANCE_SHEET_YEARLY,
								BalanceSheetList.class);
					else {
						balanceSheetList = balanceSheetObj.invokeYearlyBalanceSheet();
						redisPool.setValues(RedisConstants.BALANCE_SHEET_YEARLY, new Gson().toJson(balanceSheetList));
					}
				} catch (JedisConnectionException e) {
					log.error(e);
					balanceSheetList = balanceSheetObj.invokeYearlyBalanceSheet();
				}
			}
		} catch (CMOTSException ex) {
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Balance Sheet : " + sOptedFilter + " " + ex.getMessage());
			log.error(ex);
		}

		List<JSONObject> parsedBalanceSheetList = new ArrayList<JSONObject>();

		for (BalanceSheet balanceSheet : balanceSheetList) {
			JSONObject obj = new JSONObject();

			double capitalLiabilitiesTotal = 0, assetsTotal = 0;

			String sYRC = balanceSheet.getYRC();
			int indexYRC = sYRC.indexOf(".");
			if (indexYRC != -1)
				sYRC = sYRC.substring(0, indexYRC);
			int year = Integer.parseInt(sYRC.substring(0, 4));
			int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));

			obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

			JSONObject capitalLiabilities = new JSONObject();
			capitalLiabilities.put(DeviceConstants.CAPITAL,
					PriceFormat.formatPrice(balanceSheet.getCapital(), precision, true));
			capitalLiabilitiesTotal = capitalLiabilitiesTotal + Double.parseDouble(balanceSheet.getCapital());
			capitalLiabilities.put(DeviceConstants.EMP_STOCK_OPTION,
					balanceSheet.getEmployeesstockoption() == null ? "--"
							: PriceFormat.formatPrice(balanceSheet.getEmployeesstockoption(), precision, true));
			capitalLiabilitiesTotal = capitalLiabilitiesTotal + Double.parseDouble(
					balanceSheet.getEmployeesstockoption() == null ? "0" : balanceSheet.getEmployeesstockoption());
			capitalLiabilities.put(DeviceConstants.RESERVE_SURPLUS,
					PriceFormat.formatPrice(balanceSheet.getReserveandsurplus(), precision, true));
			capitalLiabilitiesTotal = capitalLiabilitiesTotal + Double.parseDouble(balanceSheet.getReserveandsurplus());
			capitalLiabilities.put(DeviceConstants.DEPOSITS,
					PriceFormat.formatPrice(balanceSheet.getDeposits(), precision, true));
			capitalLiabilitiesTotal = capitalLiabilitiesTotal + Double.parseDouble(balanceSheet.getDeposits());
			capitalLiabilities.put(DeviceConstants.BORROWINGS,
					PriceFormat.formatPrice(balanceSheet.getBorrowings(), precision, true));
			capitalLiabilitiesTotal = capitalLiabilitiesTotal + Double.parseDouble(balanceSheet.getBorrowings());
			capitalLiabilities.put(DeviceConstants.TOTAL,
					PriceFormat.formatPrice(Double.toString(capitalLiabilitiesTotal), precision, true));
			obj.put(DeviceConstants.CAPITAL_LIABILITIES, capitalLiabilities);

			JSONObject assets = new JSONObject();
			assets.put(DeviceConstants.CONTINGENT_LIABILITIES,
					PriceFormat.formatPrice(balanceSheet.getContingentLiabilities(), precision, true));
			assetsTotal = assetsTotal + Double.parseDouble(balanceSheet.getContingentLiabilities());
			assets.put(DeviceConstants.BILLS_COLLECTION,
					PriceFormat.formatPrice(balanceSheet.getBillsforcollection(), precision, true));
			assetsTotal = assetsTotal + Double.parseDouble(balanceSheet.getBillsforcollection());
			assets.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(Double.toString(assetsTotal), precision, true));
			obj.put(DeviceConstants.ASSETS, assets);

			parsedBalanceSheetList.add(obj);

		}

		sortJSONArrayDate(parsedBalanceSheetList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
				false);

		for (int i = 0; i < parsedBalanceSheetList.size(); i++) {
			JSONObject obj = new JSONObject();

			obj.put(DeviceConstants.PERIOD, parsedBalanceSheetList.get(i).getString(DeviceConstants.PERIOD));
			parsedBalanceSheetList.get(i).remove(DeviceConstants.PERIOD);
			obj.put(DeviceConstants.BALANCE_DATA, parsedBalanceSheetList.get(i));
			balanceSheetArr.put(obj);

		}
		finalObj.put(DeviceConstants.FILTER_OBJ, FilterList.getFilterDetails(sOptedFilter));
		finalObj.put(DeviceConstants.BALANCE_SHEET, balanceSheetArr);

		return finalObj;
	}

	public static void sortJSONArrayDate(List<JSONObject> listObj, final String sKey, final String sFormat,
			final boolean isAscendingOrder) {
		Collections.sort(listObj, new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject a, JSONObject b) {

				// SimpleDateFormat sdfo = new
				// SimpleDateFormat(DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
				SimpleDateFormat sdfo = new SimpleDateFormat(sFormat);

				Date d1 = null, d2 = null;
				try {
					d1 = sdfo.parse(a.getString(sKey));
					d2 = sdfo.parse(b.getString(sKey));
				} catch (JSONException e) {
					log.error(e);
				} catch (ParseException e) {
					log.error(e);
				}

				if (isAscendingOrder)
					return d1.compareTo(d2);
				else
					return -d1.compareTo(d2);
			}
		});

	}

	public static JSONArray profitLoss(JSONObject symObj, String sType, String sAppID) {

		JSONArray finalArr = new JSONArray();

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		int precision = symRow.getPrecisionInt();

		String sCoCode = symRow.getCMCoCode();

		GetProfitAndLoss profitLossObj = new GetProfitAndLoss(getAppIDForLogging(sAppID));
		profitLossObj.setCoCode(sCoCode);
		profitLossObj.setFinFormat(sType);

		ProfitAndLossList profitLossList = new ProfitAndLossList();

		List<JSONObject> parsedProfitLossList = new ArrayList<JSONObject>();

		try {
			profitLossList = profitLossObj.invoke();
		} catch (CMOTSException e) {
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Dividend" + " " + e.getMessage());
			log.warn(e);
		}

		for (ProfitAndLoss profitLoss : profitLossList) {
			JSONObject obj = new JSONObject();

			double expenditureTotal = 0;

			String sYRC = profitLoss.getyrc();
			int index = sYRC.indexOf(".");
			if (index != -1)
				sYRC = sYRC.substring(0, index);
			int year = Integer.parseInt(sYRC.substring(0, 4));
			int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));

			obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

			JSONObject incomeObj = new JSONObject();
			incomeObj.put(DeviceConstants.NET_SALES,
					PriceFormat.formatPrice(profitLoss.getNetSales(), precision, true));
			incomeObj.put(DeviceConstants.OTHER_INCOME,
					PriceFormat.formatPrice(profitLoss.getOtherIncome(), precision, true));
			incomeObj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(profitLoss.getTotal(), precision, true));
			obj.put(DeviceConstants.INCOME, incomeObj);

			JSONObject expenditureObj = new JSONObject();
			expenditureObj.put(DeviceConstants.OPERATING_PROFIT,
					PriceFormat.formatPrice(profitLoss.getOperatingProfit(), precision, true));
			expenditureObj.put(DeviceConstants.INTEREST,
					PriceFormat.formatPrice(profitLoss.getInterest(), precision, true));
			expenditureObj.put(DeviceConstants.PBDT, PriceFormat.formatPrice(profitLoss.getPBDT(), precision, true));
			expenditureObj.put(DeviceConstants.DEPRECIATION,
					PriceFormat.formatPrice(profitLoss.getDepreciation(), precision, true));
			expenditureObj.put(DeviceConstants.PROFIT_BEFORE_TAX_EXCEPTIONAL,
					PriceFormat.formatPrice(profitLoss.getProfitbefore_Taxation_ExceptionalItems(), precision, true));
			expenditureObj.put(DeviceConstants.EXPENSES,
					PriceFormat.formatPrice(profitLoss.getExceptional_Income_Expense(), precision, true));
			expenditureObj.put(DeviceConstants.PROFIT_BEFORE_TAX,
					PriceFormat.formatPrice(profitLoss.getProfitbeforeTax(), precision, true));
			expenditureObj.put(DeviceConstants.PROVISION_TAX,
					PriceFormat.formatPrice(profitLoss.getProvisionforTax(), precision, true));
			expenditureObj.put(DeviceConstants.PROFIT_AFTER_TAX,
					PriceFormat.formatPrice(profitLoss.getProfitafterTax(), precision, true));
			expenditureObj.put(DeviceConstants.EXTRA_ITEMS,
					PriceFormat.formatPrice(profitLoss.getExtraItems(), precision, true));
			expenditureObj.put(DeviceConstants.ADJUSTMENTS_PAT,
					PriceFormat.formatPrice(profitLoss.getAdjustmentstoPAT(), precision, true));
			expenditureObj.put(DeviceConstants.PROFIT_BALANCE,
					PriceFormat.formatPrice(profitLoss.getProfitBalance(), precision, true));
			expenditureObj.put(DeviceConstants.APPROPRIATIONS,
					PriceFormat.formatPrice(profitLoss.getAppropriations(), precision, true));
			expenditureObj.put(DeviceConstants.EARNINGS_PER_SHARE,
					PriceFormat.formatPrice(profitLoss.getEarningspershare(), precision, true));
			expenditureObj.put(DeviceConstants.ADJUSTED_EPS,
					PriceFormat.formatPrice(profitLoss.getAdjustedEPS(), precision, true));

			expenditureTotal = expenditureTotal
					+ Double.parseDouble(
							profitLoss.getOperatingProfit() == null ? "0" : profitLoss.getOperatingProfit())
					+ Double.parseDouble(profitLoss.getInterest() == null ? "0" : profitLoss.getInterest())
					+ Double.parseDouble(profitLoss.getPBDT() == null ? "0" : profitLoss.getPBDT())
					+ Double.parseDouble(profitLoss.getDepreciation() == null ? "0" : profitLoss.getDepreciation())
					+ Double.parseDouble(profitLoss.getProfitbefore_Taxation_ExceptionalItems() == null ? "0"
							: profitLoss.getProfitbefore_Taxation_ExceptionalItems())
					+ Double.parseDouble(profitLoss.getExceptional_Income_Expense() == null ? "0"
							: profitLoss.getExceptional_Income_Expense())
					+ Double.parseDouble(
							profitLoss.getProfitbeforeTax() == null ? "0" : profitLoss.getProfitbeforeTax())
					+ Double.parseDouble(
							profitLoss.getProvisionforTax() == null ? "0" : profitLoss.getProvisionforTax())
					+ Double.parseDouble(profitLoss.getProfitafterTax() == null ? "0" : profitLoss.getProfitafterTax())
					+ Double.parseDouble(profitLoss.getExtraItems() == null ? "0" : profitLoss.getExtraItems())
					+ Double.parseDouble(
							profitLoss.getAdjustmentstoPAT() == null ? "0" : profitLoss.getAdjustmentstoPAT())
					+ Double.parseDouble(profitLoss.getProfitBalance() == null ? "0" : profitLoss.getProfitBalance())
					+ Double.parseDouble(profitLoss.getAppropriations() == null ? "0" : profitLoss.getAppropriations())
					+ Double.parseDouble(
							profitLoss.getEarningspershare() == null ? "0" : profitLoss.getEarningspershare())
					+ Double.parseDouble(profitLoss.getAdjustedEPS() == null ? "0" : profitLoss.getAdjustedEPS());
			expenditureObj.put(DeviceConstants.TOTAL,
					PriceFormat.formatPrice(Double.toString(expenditureTotal), precision, true));
			obj.put(DeviceConstants.EXPENDITURE, expenditureObj);

			parsedProfitLossList.add(obj);

		}

		sortJSONArrayDate(parsedProfitLossList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
				false);

		for (int i = 0; i < parsedProfitLossList.size(); i++) {
			JSONObject obj = new JSONObject();

			obj.put(DeviceConstants.PERIOD, parsedProfitLossList.get(i).getString(DeviceConstants.PERIOD));
			parsedProfitLossList.get(i).remove(DeviceConstants.PERIOD);
			obj.put(DeviceConstants.PL_DATA, parsedProfitLossList.get(i));
			finalArr.put(obj);

		}

		return finalArr;

	}

	public static JSONArray getKeyRatio(JSONObject symObj, String sType, String sAppID) {
		JSONArray finalArr = new JSONArray();

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		int precision = symRow.getPrecisionInt();

		String sCoCode = symRow.getCMCoCode();

		GetKeyRatios keyRatioObj = new GetKeyRatios(getAppIDForLogging(sAppID));

		keyRatioObj.setCoCode(sCoCode);
		keyRatioObj.setFinFormat(sType);

		JSONArray periodArr = new JSONArray();
		List<JSONObject> parsedMarginRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedPerformanceRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedEfficiencyRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedFinancialStabilityRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedValuationRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedCashFlowRatioList = new ArrayList<JSONObject>();

		try {
			MarginRatioList marginRatioList = keyRatioObj.invokeMarginRatio();
			for (MarginRatio marginRatio : marginRatioList) {
				JSONObject obj = new JSONObject();
				double total = 0;

				String sYRC = marginRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));
				obj.put(DeviceConstants.PBIDTM, PriceFormat.formatPrice(marginRatio.getPBIDTIM(), precision, true));
				obj.put(DeviceConstants.EBITM, PriceFormat.formatPrice(marginRatio.getEBITM(), precision, true));
				obj.put(DeviceConstants.PRE_TAX_MARGIN,
						PriceFormat.formatPrice(marginRatio.getPreTaxMargin(), precision, true));
				obj.put(DeviceConstants.PATM, PriceFormat.formatPrice(marginRatio.getPATM(), precision, true));
				obj.put(DeviceConstants.CPM, PriceFormat.formatPrice(marginRatio.getCPM(), precision, true));
				total = total + Double.parseDouble(marginRatio.getPBIDTIM() == null ? "0" : marginRatio.getPBIDTIM())
						+ Double.parseDouble(marginRatio.getEBITM() == null ? "0" : marginRatio.getEBITM())
						+ Double.parseDouble(
								marginRatio.getPreTaxMargin() == null ? "0" : marginRatio.getPreTaxMargin())
						+ Double.parseDouble(marginRatio.getPATM() == null ? "0" : marginRatio.getPATM())
						+ Double.parseDouble(marginRatio.getCPM() == null ? "0" : marginRatio.getCPM());
				obj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(Double.toString(total), precision, true));
				parsedMarginRatioList.add(obj);

			}
		} catch (CMOTSException e) {
			log.warn(e);
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Key Ratio" + " " + e.getMessage());
		}

		try {
			PerformanceRatioList performanceRatioList = keyRatioObj.invokePerformanceRatio();
			for (PerformanceRatio performanceRatio : performanceRatioList) {
				JSONObject obj = new JSONObject();
				double total = 0;

				String sYRC = performanceRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));
				obj.put(DeviceConstants.ROA, PriceFormat.formatPrice(performanceRatio.getROA(), precision, true));
				obj.put(DeviceConstants.ROE, PriceFormat.formatPrice(performanceRatio.getROE(), precision, true));
				total = total + Double.parseDouble(performanceRatio.getROA() == null ? "0" : performanceRatio.getROA())
						+ Double.parseDouble(performanceRatio.getROE() == null ? "0" : performanceRatio.getROE());
				obj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(Double.toString(total), precision, true));
				parsedPerformanceRatioList.add(obj);

			}
		} catch (CMOTSException e) {
			log.warn(e);
		}

		try {
			EfficiencyRatioList efficiencyRatioList = keyRatioObj.invokeEfficiencyRatio();
			for (EfficiencyRatio efficiencyRatio : efficiencyRatioList) {
				JSONObject obj = new JSONObject();
				double total = 0;

				String sYRC = efficiencyRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

				obj.put(DeviceConstants.FIXED_CAPITAL,
						PriceFormat.formatPrice(efficiencyRatio.getFixedCapitals_Sales(), precision, true));
				obj.put(DeviceConstants.RECEIVABLE_DAYS,
						Integer.toString((int) Double.parseDouble(efficiencyRatio.getReceivableDays())));
				obj.put(DeviceConstants.INVENTORY_DAYS,
						Integer.toString((int) Double.parseDouble(efficiencyRatio.getInventoryDays())));
				obj.put(DeviceConstants.PAYABLE_DAYS,
						Integer.toString((int) Double.parseDouble(efficiencyRatio.getPayableDays())));

				total = total
						+ Double.parseDouble(efficiencyRatio.getFixedCapitals_Sales() == null ? "0"
								: efficiencyRatio.getFixedCapitals_Sales())
						+ (int) Double.parseDouble(
								efficiencyRatio.getReceivableDays() == null ? "0" : efficiencyRatio.getReceivableDays())
						+ (int) Double.parseDouble(
								efficiencyRatio.getInventoryDays() == null ? "0" : efficiencyRatio.getInventoryDays())
						+ (int) Double.parseDouble(
								efficiencyRatio.getPayableDays() == null ? "0" : efficiencyRatio.getPayableDays());

				obj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(Double.toString(total), precision, true));
				parsedEfficiencyRatioList.add(obj);
			}
		} catch (CMOTSException e) {
			log.warn(e);
		}

		try {
			FinancialStabilityRatioList financialStabilityList = keyRatioObj.invokeFinancialStabilityRatio();

			for (FinancialStabilityRatio financialRatio : financialStabilityList) {
				JSONObject obj = new JSONObject();
				double total = 0;

				String sYRC = financialRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

				obj.put(DeviceConstants.TOTAL_DEBT_EQUITY,
						PriceFormat.formatPrice(financialRatio.getTotalDebt_Equity(), precision, true));
				obj.put(DeviceConstants.CURRENT_RATIO,
						PriceFormat.formatPrice(financialRatio.getCurrentRatio(), precision, true));
				obj.put(DeviceConstants.QUICK_RATIO,
						PriceFormat.formatPrice(financialRatio.getQuickRatio(), precision, true));
				obj.put(DeviceConstants.INTEREST_COVER,
						PriceFormat.formatPrice(financialRatio.getInterestCover(), precision, true));
				obj.put(DeviceConstants.TOTAL_DEBT_MCAP,
						PriceFormat.formatPrice(financialRatio.getTotalDebt_MCap(), precision, true));

				total = total
						+ Double.parseDouble(financialRatio.getTotalDebt_Equity() == null ? "0"
								: financialRatio.getTotalDebt_Equity())
						+ Double.parseDouble(
								financialRatio.getCurrentRatio() == null ? "0" : financialRatio.getCurrentRatio())
						+ Double.parseDouble(
								financialRatio.getQuickRatio() == null ? "0" : financialRatio.getQuickRatio())
						+ Double.parseDouble(
								financialRatio.getInterestCover() == null ? "0" : financialRatio.getInterestCover())
						+ Double.parseDouble(
								financialRatio.getTotalDebt_MCap() == null ? "0" : financialRatio.getTotalDebt_MCap());

				obj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(Double.toString(total), precision, true));
				parsedFinancialStabilityRatioList.add(obj);
			}
		} catch (CMOTSException e) {
			log.warn(e);
		}

		try {
			ValuationRatioList valuationRatioList = keyRatioObj.invokeValuationRatio();
			for (ValuationRatio valuationRatio : valuationRatioList) {
				JSONObject obj = new JSONObject();
				double total = 0;

				String sYRC = valuationRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

				obj.put(DeviceConstants.PE, PriceFormat.formatPrice(valuationRatio.getPE(), precision, true));
				obj.put(DeviceConstants.BOOK_VALUE,
						PriceFormat.formatPrice(valuationRatio.getPrice_BookValue(), precision, true));
				obj.put(DeviceConstants.DIV_YIELD,
						PriceFormat.formatPrice(valuationRatio.getDividendYield(), precision, true));
				obj.put(DeviceConstants.EBITDA,
						PriceFormat.formatPrice(valuationRatio.getEV_EBITDA(), precision, true));
				obj.put(DeviceConstants.MCAP, PriceFormat.formatPrice(valuationRatio.getMcap_Sales(), precision, true));

				total = total + Double.parseDouble(valuationRatio.getPE() == null ? "0" : valuationRatio.getPE())
						+ Double.parseDouble(
								valuationRatio.getPrice_BookValue() == null ? "0" : valuationRatio.getPrice_BookValue())
						+ Double.parseDouble(
								valuationRatio.getDividendYield() == null ? "0" : valuationRatio.getDividendYield())
						+ Double.parseDouble(
								valuationRatio.getEV_EBITDA() == null ? "0" : valuationRatio.getEV_EBITDA())
						+ Double.parseDouble(
								valuationRatio.getMcap_Sales() == null ? "0" : valuationRatio.getMcap_Sales());
				obj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(Double.toString(total), precision, true));
				parsedValuationRatioList.add(obj);

			}
		} catch (CMOTSException e) {
			log.warn(e);
		}

		try {
			CashFlowRatioList cashFlowRatioList = keyRatioObj.invokeCashFlowRatio();
			for (CashFlowRatio cashFlowRatio : cashFlowRatioList) {
				JSONObject obj = new JSONObject();
				double total = 0;

				String sYRC = cashFlowRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

				obj.put(DeviceConstants.CASH_PER_SHARE,
						PriceFormat.formatPrice(cashFlowRatio.getCashFlowPerShare(), precision, true));
				obj.put(DeviceConstants.PRICE_CASH_RATIO,
						PriceFormat.formatPrice(cashFlowRatio.getPricetoCashFlowRatio(), precision, true));
				obj.put(DeviceConstants.FREE_CASH_PER_SHARE,
						PriceFormat.formatPrice(cashFlowRatio.getFreeCashFlowperShare(), precision, true));
				obj.put(DeviceConstants.PRICE_FREE_CASH,
						PriceFormat.formatPrice(cashFlowRatio.getPricetoFreeCashFlow(), precision, true));
				obj.put(DeviceConstants.FREE_CASH_YIELD,
						PriceFormat.formatPrice(cashFlowRatio.getFreeCashFlowYield(), precision, true));
				obj.put(DeviceConstants.SALES_CASH_RATIO,
						PriceFormat.formatPrice(cashFlowRatio.getSalestocashflowratio(), precision, true));

				total = total
						+ Double.parseDouble(
								cashFlowRatio.getCashFlowPerShare() == null ? "0" : cashFlowRatio.getCashFlowPerShare())
						+ Double.parseDouble(cashFlowRatio.getPricetoCashFlowRatio() == null ? "0"
								: cashFlowRatio.getPricetoCashFlowRatio())
						+ Double.parseDouble(cashFlowRatio.getFreeCashFlowperShare() == null ? "0"
								: cashFlowRatio.getFreeCashFlowperShare())
						+ Double.parseDouble(cashFlowRatio.getPricetoFreeCashFlow() == null ? "0"
								: cashFlowRatio.getPricetoFreeCashFlow())
						+ Double.parseDouble(cashFlowRatio.getFreeCashFlowYield() == null ? "0"
								: cashFlowRatio.getFreeCashFlowYield())
						+ Double.parseDouble(cashFlowRatio.getSalestocashflowratio() == null ? "0"
								: cashFlowRatio.getSalestocashflowratio());

				obj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(Double.toString(total), precision, true));
				parsedCashFlowRatioList.add(obj);
			}
		} catch (CMOTSException e) {
			log.warn(e);
		}

		try {
			sortJSONArrayDate(parsedMarginRatioList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
					false);
			sortJSONArrayDate(parsedPerformanceRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			sortJSONArrayDate(parsedEfficiencyRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			sortJSONArrayDate(parsedFinancialStabilityRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			sortJSONArrayDate(parsedValuationRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			sortJSONArrayDate(parsedCashFlowRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);

			for (int i = 0; i < parsedMarginRatioList.size(); i++) {
				JSONObject obj = parsedMarginRatioList.get(i);
				if (obj.has(DeviceConstants.PERIOD)) {
					periodArr.put(obj.getString(DeviceConstants.PERIOD));
				}
			}

			for (int i = 0; i < periodArr.length(); i++) {
				JSONObject eachObj = new JSONObject();
				JSONObject ratioObj = new JSONObject();
				eachObj.put(DeviceConstants.PERIOD, periodArr.get(i));
				ratioObj.put(DeviceConstants.MARGIN_RATIOS, getKeyRatioByYear((String) periodArr.get(i),
						parsedMarginRatioList, DeviceConstants.MARGIN_RATIOS));
				ratioObj.put(DeviceConstants.PERFORMANCE_RATIOS, getKeyRatioByYear((String) periodArr.get(i),
						parsedPerformanceRatioList, DeviceConstants.PERFORMANCE_RATIOS));
				ratioObj.put(DeviceConstants.EFFICIENCY_RATIOS, getKeyRatioByYear((String) periodArr.get(i),
						parsedEfficiencyRatioList, DeviceConstants.EFFICIENCY_RATIOS));
				ratioObj.put(DeviceConstants.FINANCIAL_STABILITY_RATIOS, getKeyRatioByYear((String) periodArr.get(i),
						parsedFinancialStabilityRatioList, DeviceConstants.FINANCIAL_STABILITY_RATIOS));
				ratioObj.put(DeviceConstants.VALUATION_RATIOS, getKeyRatioByYear((String) periodArr.get(i),
						parsedValuationRatioList, DeviceConstants.VALUATION_RATIOS));
				ratioObj.put(DeviceConstants.CASH_FLOW_RATIOS, getKeyRatioByYear((String) periodArr.get(i),
						parsedCashFlowRatioList, DeviceConstants.CASH_FLOW_RATIOS));

				eachObj.put(DeviceConstants.RATIO_DATA, ratioObj);
				finalArr.put(eachObj);
			}
		} catch (Exception e) {
			log.warn(e);
		}

		return finalArr;
	}

	private static JSONObject getKeyRatioByYear(String sPeriod, List<JSONObject> keyRatio, String sRatioType) {
		JSONObject obj = new JSONObject();

		boolean foundPeriod = false;

		if (keyRatio.size() > 0) {
			for (int i = 0; i < keyRatio.size(); i++) {
				JSONObject keyRatioObj = keyRatio.get(i);
				if (keyRatioObj.getString(DeviceConstants.PERIOD).equals(sPeriod)) {
					foundPeriod = true;
					obj = keyRatioObj;
				}
			}
		}

		if ((!foundPeriod) || (keyRatio.size() <= 0)) {
			if (sRatioType.equals(DeviceConstants.MARGIN_RATIOS)) {
				obj.put(DeviceConstants.PERIOD, sPeriod);
				obj.put(DeviceConstants.PBIDTM, "--");
				obj.put(DeviceConstants.EBITM, "--");
				obj.put(DeviceConstants.PRE_TAX_MARGIN, "--");
				obj.put(DeviceConstants.PATM, "--");
				obj.put(DeviceConstants.CPM, "--");
				obj.put(DeviceConstants.TOTAL, "--");
			} else if (sRatioType.equals(DeviceConstants.PERFORMANCE_RATIOS)) {
				obj.put(DeviceConstants.PERIOD, sPeriod);
				obj.put(DeviceConstants.ROA, "--");
				obj.put(DeviceConstants.ROE, "--");
				obj.put(DeviceConstants.TOTAL, "--");
			} else if (sRatioType.equals(DeviceConstants.EFFICIENCY_RATIOS)) {
				obj.put(DeviceConstants.PERIOD, sPeriod);
				obj.put(DeviceConstants.FIXED_CAPITAL, "--");
				obj.put(DeviceConstants.RECEIVABLE_DAYS, "--");
				obj.put(DeviceConstants.INVENTORY_DAYS, "--");
				obj.put(DeviceConstants.PAYABLE_DAYS, "--");
				obj.put(DeviceConstants.TOTAL, "--");
			} else if (sRatioType.equals(DeviceConstants.FINANCIAL_STABILITY_RATIOS)) {
				obj.put(DeviceConstants.PERIOD, sPeriod);
				obj.put(DeviceConstants.TOTAL_DEBT_EQUITY, "--");
				obj.put(DeviceConstants.CURRENT_RATIO, "--");
				obj.put(DeviceConstants.QUICK_RATIO, "--");
				obj.put(DeviceConstants.INTEREST_COVER, "--");
				obj.put(DeviceConstants.TOTAL_DEBT_MCAP, "--");
				obj.put(DeviceConstants.TOTAL, "--");
			} else if (sRatioType.equals(DeviceConstants.VALUATION_RATIOS)) {
				obj.put(DeviceConstants.PERIOD, sPeriod);
				obj.put(DeviceConstants.PE, "--");
				obj.put(DeviceConstants.BOOK_VALUE, "--");
				obj.put(DeviceConstants.DIV_YIELD, "--");
				obj.put(DeviceConstants.EBITDA, "--");
				obj.put(DeviceConstants.MCAP, "--");
				obj.put(DeviceConstants.TOTAL, "--");
			} else if (sRatioType.equals(DeviceConstants.CASH_FLOW_RATIOS)) {
				obj.put(DeviceConstants.PERIOD, sPeriod);
				obj.put(DeviceConstants.CASH_PER_SHARE, "--");
				obj.put(DeviceConstants.PRICE_CASH_RATIO, "--");
				obj.put(DeviceConstants.FREE_CASH_PER_SHARE, "--");
				obj.put(DeviceConstants.PRICE_FREE_CASH, "--");
				obj.put(DeviceConstants.FREE_CASH_YIELD, "--");
				obj.put(DeviceConstants.SALES_CASH_RATIO, "--");
				obj.put(DeviceConstants.TOTAL, "--");
			}
		}
		return obj;

	}

	public static JSONArray getKeyFinancials(JSONObject symObj, String type, String sAppID) throws Exception {

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		int precision = symRow.getPrecisionInt();

		String sCoCode = symRow.getCMCoCode();
		int index = sCoCode.indexOf(".");
		if (index != -1)
			sCoCode = sCoCode.substring(0, index);
		GetFinancialEquity financialEquityObj = new GetFinancialEquity(getAppIDForLogging(sAppID));

		financialEquityObj.setCoCode(sCoCode);
		financialEquityObj.setType(type);

		RedisPool redisPool = new RedisPool();
		FinancialFNOEquityList financialEquityList = new FinancialFNOEquityList();
		try {
			if (redisPool.isExists(RedisConstants.FINANCIALS + "_" + sCoCode + "_" + type)) {
				financialEquityList = new Gson().fromJson(
						redisPool.getValue(RedisConstants.FINANCIALS + "_" + sCoCode + "_" + type),
						FinancialFNOEquityList.class);
			} else {

				financialEquityList = financialEquityObj.invoke();
				redisPool.setValues(RedisConstants.FINANCIALS + "_" + sCoCode + "_" + type,
						new Gson().toJson(financialEquityList));
			}
		} catch (Exception e) {
			log.error(e);
			financialEquityList = financialEquityObj.invoke();
		}
		List<JSONObject> parsedFinancialEquityList = new ArrayList<JSONObject>();

		JSONArray finalFinancialEquity = new JSONArray();

		for (FinancialFNOEquity financialEquity : financialEquityList) {
			JSONObject obj = new JSONObject();
			String sYRC = financialEquity.getYRC();
			int indexYRC = sYRC.indexOf(".");
			if (indexYRC != -1)
				sYRC = sYRC.substring(0, indexYRC);
			int year = Integer.parseInt(sYRC.substring(0, 4));
			int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));

			obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));
			obj.put(DeviceConstants.TOTAL_INCOME,
					PriceFormat.formatPrice(financialEquity.getTotalIncome(), precision, true));
			obj.put(DeviceConstants.EBITDA, PriceFormat.formatPrice(financialEquity.getEbitda(), precision, true));
			obj.put(DeviceConstants.NET_PROFIT_LOSS,
					PriceFormat.formatPrice(financialEquity.getNetProfitLoss(), precision, true));
			obj.put(DeviceConstants.EPS, PriceFormat.formatPrice(financialEquity.getEps(), precision, true));
			obj.put(DeviceConstants.BOOK_VALUE,
					PriceFormat.formatPrice(financialEquity.getBookValue(), precision, true));

			parsedFinancialEquityList.add(obj);

		}
		sortJSONArrayDate(parsedFinancialEquityList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
				false);

		finalFinancialEquity.put(parsedFinancialEquityList.get(0));
		finalFinancialEquity.put(parsedFinancialEquityList.get(1));

		return finalFinancialEquity;
	}

	public static JSONArray getKeyFinancialsRatios(JSONObject symObj, String type, String sAppID) throws Exception {

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		int precision = symRow.getPrecisionInt();

		String sCoCode = symRow.getCMCoCode();
		RedisPool redisPool = new RedisPool();

		int index = sCoCode.indexOf(".");
		if (index != -1)
			sCoCode = sCoCode.substring(0, index);
		GetFinancialEquity financialEquityObj = new GetFinancialEquity(getAppIDForLogging(sAppID));

		financialEquityObj.setCoCode(sCoCode);
		financialEquityObj.setType(type);

		FinancialFNOEquityList financialEquityList = new FinancialFNOEquityList();
		try {
			if (redisPool.isExists(RedisConstants.FINANCIALS_EQUITY + "_" + sCoCode + "_" + type)) {

				financialEquityList = new Gson().fromJson(
						redisPool.getValue(RedisConstants.FINANCIALS_EQUITY + "_" + sCoCode + "_" + type),
						FinancialFNOEquityList.class);
			} else {
				financialEquityList = financialEquityObj.invoke();
				redisPool.setValues(RedisConstants.FINANCIALS_EQUITY + "_" + sCoCode + "_" + type,
						new Gson().toJson(financialEquityList));
			}
		} catch (Exception e) {
			log.error(e);
			financialEquityList = financialEquityObj.invoke();
		}

		List<JSONObject> parsedFinancialEquityList = new ArrayList<JSONObject>();

		JSONArray finalFinancialEquity = new JSONArray();

		for (FinancialFNOEquity financialEquity : financialEquityList) {
			JSONObject obj = new JSONObject();
			String sYRC = financialEquity.getYRC();
			int indexYRC = sYRC.indexOf(".");
			if (indexYRC != -1)
				sYRC = sYRC.substring(0, indexYRC);
			int year = Integer.parseInt(sYRC.substring(0, 4));
			int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));

			obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));
			obj.put(DeviceConstants.PE, PriceFormat.formatPrice(financialEquity.getPe(), precision, true));
			obj.put(DeviceConstants.PBV, PriceFormat.formatPrice(financialEquity.getPbv(), precision, true));
			obj.put(DeviceConstants.DEBT_EQUITY,
					PriceFormat.formatPrice(financialEquity.getDebtEquity(), precision, true));
			obj.put(DeviceConstants.RETURN_ON_ASSET,
					PriceFormat.formatPrice(financialEquity.getReturnOnAsset(), precision, true));
			obj.put(DeviceConstants.RETURN_ON_EQUITY,
					PriceFormat.formatPrice(financialEquity.getReturnOnEquity(), precision, true));

			parsedFinancialEquityList.add(obj);

		}
		sortJSONArrayDate(parsedFinancialEquityList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
				false);

		finalFinancialEquity.put(parsedFinancialEquityList.get(0));
		finalFinancialEquity.put(parsedFinancialEquityList.get(1));

		return finalFinancialEquity;
	}

	public static JSONObject getOptionChain(JSONObject symObj, JSONObject filterObj, String sAppID) throws Exception {

		JSONObject finalObj = new JSONObject();
		JSONObject finalFilterObj = new JSONObject();

		String sOptedFilter = filterObj.getString(DeviceConstants.OPTED_FILTER);

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		try {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			int precision = symRow.getPrecisionInt();
			String symbol = symRow.getSymbol();
			String mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
			int currDiff, prevDiff = Integer.MAX_VALUE, highlight = 0;
			String tokenSegID = "";
			if (ExchangeSegment.isEquitySegment(symRow.getMktSegId()))
				tokenSegID = symRow.getSymbolToken();
			else {
				tokenSegID = symRow.getAssetToken() + "_1";
				if(!InstrumentType.isIndex(symRow.getInstrument()))
					mappingSymbolUniqDesc = SymbolMap.getSymbolRow(tokenSegID).getMappingSymbolUniqDesc();
				else
					mappingSymbolUniqDesc = IndicesMapping.getIndicesScrip(tokenSegID);
			}

			QuoteDetails quote = Quote.getLTP(tokenSegID, mappingSymbolUniqDesc);
			int ltp = 0;
			if (quote != null) {
				String sLtp = quote.sLTP;
				int pos = sLtp.indexOf(".");
				if (pos != -1)
					sLtp = sLtp.substring(0, pos);
				ltp = Integer.valueOf(sLtp);
			}

			List<String> expiryDates = new ArrayList<String>();

			RedisPool redisPool = new RedisPool();

			GetExpiry expiryObj = new GetExpiry();
			expiryObj.setSymbol(symbol);

			ExpiryList expiryList = null;
			try {
				if (redisPool.isExists(RedisConstants.EXPIRY_CACHE + "_" + symbol)) {
					expiryList = new Gson().fromJson(redisPool.getValue(RedisConstants.EXPIRY_CACHE + "_" + symbol),
							ExpiryList.class);
				} else {
					expiryList = expiryObj.invoke();
					redisPool.setValues(RedisConstants.EXPIRY_CACHE + "_" + symbol, new Gson().toJson(expiryList));
				}
			} catch (Exception e) {
				log.error(e);
				expiryList = expiryObj.invoke();
			}
			if (expiryList == null)
				return finalObj;
			for (Expiry expiry : expiryList) {
				expiryDates.add(DateUtils.formatDate(expiry.getExpDate(), DeviceConstants.OPTIONS_DATE_FORMAT_1,
						DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT).toUpperCase());
			}
			sortArrayDate(expiryDates);

			if (sOptedFilter.isEmpty())
				sOptedFilter = expiryDates.get(0);

			finalFilterObj.put(DeviceConstants.FILTER_LIST, expiryDates);
			finalFilterObj.put(DeviceConstants.OPTED_FILTER, sOptedFilter);

			GetOptionChain optionChainObj = new GetOptionChain(getAppIDForLogging(sAppID));

			optionChainObj.setSymbol(symbol);
			optionChainObj.setExpiry(DateUtils.formatDate(sOptedFilter, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
					DeviceConstants.OPTIONS_DATE_FORMAT_2));

			OptionChainList optionsList = null;
			try {
				if (redisPool.isExists(RedisConstants.EXPIRY_CACHE + "_" + symbol + "_" + sOptedFilter)) {
					optionsList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.EXPIRY_CACHE + "_" + symbol + "_" + sOptedFilter),
							OptionChainList.class);
				} else {
					optionsList = optionChainObj.invoke();
					redisPool.setValues(RedisConstants.EXPIRY_CACHE + "_" + symbol + "_" + sOptedFilter,
							new Gson().toJson(optionsList));
				}
			} catch (JedisConnectionException e) {
				log.error(e);
				optionsList = optionChainObj.invoke();
			}
			Map<String, QuoteDetails> QuoteLTPDetails = Quote.getLTPMapUsingSymbolUniqDesc(optionsList,
					ExchangeSegment.getMarketSegmentID(ExchangeSegment.NFO));

			JSONArray optionsObj = new JSONArray();

			for (OptionChain option : optionsList) {

				JSONObject obj = new JSONObject();
				JSONObject callObj = new JSONObject();
				JSONObject putObj = new JSONObject();

				String strikePrice = option.getStrikePrice();
				int ind = strikePrice.indexOf(".");
				if (ind != -1) {
					if (strikePrice.substring(ind + 1, strikePrice.length()).equals("0")
							|| strikePrice.substring(ind + 1, strikePrice.length()).equals("00"))
						strikePrice = strikePrice.substring(0, ind);
					else
						strikePrice = String.valueOf(Double.parseDouble(strikePrice));
				}
				if (strikePrice != null) {
					obj.put(DeviceConstants.STRIKE_PRICE, PriceFormat.formatPrice(strikePrice, precision, true));

					String sym = option.getSymbol().trim();
					String exp = DateUtils.formatDate(option.getExpDate(), DeviceConstants.OPTIONS_DATE_FORMAT_1,
							DBConstants.UNIQ_DESC_DATE_FORMAT).toUpperCase();
					String symbolUniq = sym + exp + strikePrice;

					String symbolUniqCall = symbolUniq + "CE_NFO";
					if (!SymbolMap.isValidSymbolUniqDescMap(symbolUniqCall))
						continue;
					SymbolRow sSymObjCall = SymbolMap.getSymbolUniqDescRow(symbolUniqCall);
					callObj.put(SymbolConstants.SYMBOL_OBJ,
							sSymObjCall.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
					if (QuoteLTPDetails.containsKey(sSymObjCall.getMappingSymbolUniqDesc())) {
						QuoteDetails quoteCall = QuoteLTPDetails.get(sSymObjCall.getMappingSymbolUniqDesc());
						callObj.put(DeviceConstants.LTP, quoteCall.sLTP == null ? "--"
								: PriceFormat.formatPrice(quoteCall.sLTP, precision, false));
						callObj.put(DeviceConstants.CHANGE, quoteCall.sChange == null ? "--"
								: PriceFormat.formatPrice(quoteCall.sChange, precision, false));
						callObj.put(DeviceConstants.CHANGE_PERCENT, quoteCall.sChangePercent == null ? "--"
								: Math.abs(Double.parseDouble(quoteCall.sChangePercent)));
					} else {
						callObj.put(DeviceConstants.LTP, "0.00");
						callObj.put(DeviceConstants.CHANGE, "0.00");
						callObj.put(DeviceConstants.CHANGE_PERCENT, "0");
					}

					String symbolUniqPut = symbolUniq + "PE_NFO";
					if (!SymbolMap.isValidSymbolUniqDescMap(symbolUniqPut))
						continue;
					SymbolRow sSymObjPut = SymbolMap.getSymbolUniqDescRow(symbolUniqPut);
					putObj.put(SymbolConstants.SYMBOL_OBJ, sSymObjPut.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
				
					if(QuoteLTPDetails.containsKey(sSymObjPut.getMappingSymbolUniqDesc())) {
						QuoteDetails quotePut =  QuoteLTPDetails.get(sSymObjPut.getMappingSymbolUniqDesc());
						putObj.put(DeviceConstants.LTP, quotePut.sLTP==null ? "--" : PriceFormat.formatPrice(quotePut.sLTP, precision, false));
						putObj.put(DeviceConstants.CHANGE, quotePut.sChange==null ? "--" : PriceFormat.formatPrice( quotePut.sChange, precision, false));
						putObj.put(DeviceConstants.CHANGE_PERCENT, quotePut.sChangePercent==null ? "--" : Math.abs(Double.parseDouble(quotePut.sChangePercent)));
					}else {
					    putObj.put(DeviceConstants.LTP, "0.00");
                        putObj.put(DeviceConstants.CHANGE, "0.00");
                        putObj.put(DeviceConstants.CHANGE_PERCENT, "0");
					}

				} else {
					obj.put(DeviceConstants.STRIKE_PRICE, "--");
					callObj.put(DeviceConstants.LTP, "--");
					callObj.put(DeviceConstants.CHANGE, "--");
					callObj.put(DeviceConstants.CHANGE_PERCENT, "--");
					putObj.put(DeviceConstants.LTP, "--");
					putObj.put(DeviceConstants.CHANGE, "--");
					putObj.put(DeviceConstants.CHANGE_PERCENT, "--");
				}

				callObj.put(DeviceConstants.OI, option.getCallOI() == null ? "--"
						: PriceFormat.numberFormat(Math.round(Float.parseFloat(option.getCallOI()))));
				callObj.put(DeviceConstants.OI_CHANGE, option.getCallOIChange() == null ? "--"
						: PriceFormat.numberFormat(Math.round(Float.parseFloat(option.getCallOIChange()))));
				String callVol = option.getCallVolume();
				if (callVol == "" || callVol == null) {
					callVol = "-";
					callObj.put(DeviceConstants.M_VOLUME, callVol);
				} else {
					int indx = callVol.indexOf(".");
					callVol = callVol.substring(0, indx);
					callObj.put(DeviceConstants.M_VOLUME, PriceFormat.numberFormat(Integer.valueOf(callVol)));
				}
				callObj.put(DeviceConstants.IV, "--");

				putObj.put(DeviceConstants.OI, option.getPutOI() == null ? "--"
						: PriceFormat.numberFormat(Math.round(Float.parseFloat(option.getPutOI()))));
				putObj.put(DeviceConstants.OI_CHANGE, option.getPutOIChange() == null ? "--"
						: PriceFormat.numberFormat(Math.round(Float.parseFloat(option.getPutOIChange()))));
				String putVol = option.getPutVolume();
				if (putVol == "" || putVol == null) {
					putVol = "-";
					putObj.put(DeviceConstants.M_VOLUME, putVol);
				} else {
					int index = putVol.indexOf(".");
					putVol = putVol.substring(0, index);
					putObj.put(DeviceConstants.M_VOLUME, PriceFormat.numberFormat(Integer.valueOf(putVol)));
				}
				putObj.put(DeviceConstants.IV, "--");

				obj.put(DeviceConstants.CALL_OBJ, callObj);
				obj.put(DeviceConstants.PUT_OBJ, putObj);

				obj.put(DeviceConstants.HIGHLIGHT, "false");

				int strike;
				if (PriceFormat.isDouble(strikePrice))
					strike = (int) Double.parseDouble(strikePrice);
				else
					strike = Integer.parseInt(strikePrice);
				if (ltp != 0) {
					currDiff = Math.abs(ltp - strike);
					if (currDiff < prevDiff)
						highlight = optionsObj.length();
					prevDiff = currDiff;
				}
				optionsObj.put(obj);
			}
			if (ltp != 0 && optionsObj.length() != 0) {
				JSONObject highlightObj = optionsObj.getJSONObject(highlight);
				highlightObj.put(DeviceConstants.HIGHLIGHT, "true");
			}
			finalObj.put(DeviceConstants.FILTER_OBJ, finalFilterObj);
			finalObj.put(sOptedFilter, optionsObj);

			return finalObj;
		} catch (Exception e) {
			log.warn(e);
		}
		return finalObj;
	}

	public static JSONObject getOptionChain_101(JSONObject symObj, JSONObject filterObj, String sAppID)
			throws Exception {

		JSONObject finalObj = new JSONObject();
		try {
			JSONObject finalFilterObj = new JSONObject();

			String sOptedFilter = filterObj.getString(DeviceConstants.OPTED_FILTER);

			String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			int precision = symRow.getPrecisionInt();
			String symbol = symRow.getSymbol();
			RedisPool redisPool = new RedisPool();

			List<String> expiryDates = new ArrayList<String>();

			GetExpiry expiryObj = new GetExpiry();
			expiryObj.setSymbol(symbol);

			ExpiryList expiryList = new ExpiryList();
			try {
				if (redisPool.isExists(RedisConstants.OPTION_CHAIN_EXPIRY_LIST + "_" + symbol)) {
					expiryList = new Gson().fromJson(RedisConstants.OPTION_CHAIN_EXPIRY_LIST + "_" + symbol,
							ExpiryList.class);
				} else {
					expiryList = expiryObj.invoke();
					redisPool.setValues(RedisConstants.OPTION_CHAIN_EXPIRY_LIST + "_" + symbol,
							new Gson().toJson(expiryList));
				}
			} catch (Exception e) {
				log.error(e);
				expiryList = expiryObj.invoke();
			}

			if (expiryList == null)
				return finalObj;
			for (Expiry expiry : expiryList) {
				expiryDates.add(DateUtils.formatDate(expiry.getExpDate(), DeviceConstants.OPTIONS_DATE_FORMAT_1,
						DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT).toUpperCase());
			}
			sortArrayDate(expiryDates);

			if (sOptedFilter.isEmpty())
				sOptedFilter = expiryDates.get(0);

			finalFilterObj.put(DeviceConstants.FILTER_LIST, expiryDates);
			finalFilterObj.put(DeviceConstants.OPTED_FILTER, sOptedFilter);

			GetOptionChain optionChainObj = new GetOptionChain(getAppIDForLogging(sAppID));

			optionChainObj.setSymbol(symbol);
			optionChainObj.setExpiry(DateUtils.formatDate(sOptedFilter, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
					DeviceConstants.OPTIONS_DATE_FORMAT_2));

			OptionChainList optionsList = new OptionChainList();
			try {
				if (redisPool.isExists(RedisConstants.OPTION_CHAIN_LIST)) {
					optionsList = new Gson().fromJson(RedisConstants.OPTION_CHAIN_LIST, OptionChainList.class);
				} else {
					optionsList = optionChainObj.invoke();
					redisPool.setValues(RedisConstants.OPTION_CHAIN_LIST, new Gson().toJson(optionsList));
				}
			} catch (JedisConnectionException e) {
				log.error(e);
				optionsList = optionChainObj.invoke();
			}

			JSONArray optionsObj = new JSONArray();

			for (OptionChain option : optionsList) {

				JSONObject obj = new JSONObject();
				String strikePrice = option.getStrikePrice();
				int ind = strikePrice.indexOf(".");
				if (ind != -1)
					strikePrice = strikePrice.substring(0, ind);
				if (strikePrice != null) {
					obj.put(DeviceConstants.STRIKE_PRICE, PriceFormat.formatPrice(strikePrice, precision, true));

					String sym = option.getSymbol().trim();
					String exp = DateUtils.formatDate(option.getExpDate(), DeviceConstants.OPTIONS_DATE_FORMAT_1,
							DBConstants.UNIQ_DESC_DATE_FORMAT).toUpperCase();
					String symbolUniq = sym + exp + strikePrice;

					String symbolUniqCall = symbolUniq + "CE_NFO";
					log.info("Uniq : " + symbolUniqCall);
					if (!SymbolMap.isValidSymbolUniqDescMap(symbolUniqCall))
						continue;

					SymbolRow sSymObjCall = SymbolMap.getSymbolUniqDescRow(symbolUniqCall);
					obj.put(SymbolConstants.SYMBOL_OBJ,
							sSymObjCall.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));

					QuoteDetails quoteCall = Quote.getLTPUsingSymbolUniqDesc(symbolUniqCall);
					obj.put(DeviceConstants.CALL_LTP,
							quoteCall.sLTP == null ? "--" : PriceFormat.formatPrice(quoteCall.sLTP, precision, false));
					obj.put(DeviceConstants.CALL_PRICE_CHANGE, quoteCall.sChange == null ? "--"
							: PriceFormat.formatPrice(quoteCall.sChange, precision, false));
					obj.put(DeviceConstants.CALL_PRICE_PER_CHANGE, quoteCall.sChangePercent == null ? "--"
							: Math.abs(Double.parseDouble(quoteCall.sChangePercent)));

					String symbolUniqPut = symbolUniq + "PE_NFO";
					log.info("Uniq : " + symbolUniqCall);
					if (!SymbolMap.isValidSymbolUniqDescMap(symbolUniqPut))
						continue;

					SymbolRow sSymObjPut = SymbolMap.getSymbolUniqDescRow(symbolUniqPut);
					obj.put(SymbolConstants.SYMBOL_OBJ,
							sSymObjPut.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));

					QuoteDetails quotePut = Quote.getLTPUsingSymbolUniqDesc(symbolUniqPut);
					obj.put(DeviceConstants.PUT_LTP,
							quotePut.sLTP == null ? "--" : PriceFormat.formatPrice(quotePut.sLTP, precision, false));
					obj.put(DeviceConstants.PUT_PRICE_CHANGE, quotePut.sChange == null ? "--"
							: PriceFormat.formatPrice(quotePut.sChange, precision, false));
					obj.put(DeviceConstants.PUT_PRICE_PER_CHANGE, quotePut.sChangePercent == null ? "--"
							: Math.abs(Double.parseDouble(quotePut.sChangePercent)));

				} else {
					obj.put(DeviceConstants.STRIKE_PRICE, "--");
					obj.put(DeviceConstants.CALL_LTP, "--");
					obj.put(DeviceConstants.CALL_PRICE_CHANGE, "--");
					obj.put(DeviceConstants.CALL_PRICE_PER_CHANGE, "--");
					obj.put(DeviceConstants.PUT_LTP, "--");
					obj.put(DeviceConstants.PUT_PRICE_CHANGE, "--");
					obj.put(DeviceConstants.PUT_PRICE_PER_CHANGE, "--");
				}

				obj.put(DeviceConstants.CALL_OI, option.getCallOI() == null ? "--"
						: PriceFormat.formatPrice(option.getCallOI(), precision, true).replaceAll(",", ""));
				obj.put(DeviceConstants.CALL_OI_CHANGE, option.getCallOIChange() == null ? "--"
						: PriceFormat.formatPrice(option.getCallOIChange(), precision, true).replaceAll(",", ""));
				String callVol = option.getCallVolume();
				if (callVol != null) {
					int indx = callVol.indexOf(".");
					callVol = callVol.substring(0, indx);
				} else
					callVol = "-";
				obj.put(DeviceConstants.CALL_VOLUME, callVol);
				obj.put(DeviceConstants.CALL_IV, "--");

				obj.put(DeviceConstants.PUT_OI, option.getPutOI() == null ? "--"
						: PriceFormat.formatPrice(option.getPutOI(), precision, true).replaceAll(",", ""));
				obj.put(DeviceConstants.PUT_OI_CHANGE, option.getPutOIChange() == null ? "--"
						: PriceFormat.formatPrice(option.getPutOIChange(), precision, true).replaceAll(",", ""));
				String putVol = option.getPutVolume();
				if (putVol == "" || putVol == null)
					putVol = "-";
				else {
					int index = putVol.indexOf(".");
					putVol = putVol.substring(0, index);
				}
				obj.put(DeviceConstants.PUT_VOLUME, putVol);
				obj.put(DeviceConstants.PUT_IV, "--");

				optionsObj.put(obj);
			}

			finalObj.put(DeviceConstants.FILTER_OBJ, finalFilterObj);
			finalObj.put(sOptedFilter, optionsObj);
		} catch (Exception e) {
			log.warn(e);
		}
		return finalObj;

	}

	public static void sortArrayDate(List<String> arrObj) {
		Collections.sort(arrObj, new Comparator<String>() {

			@Override
			public int compare(String a, String b) {

				SimpleDateFormat sdfo = new SimpleDateFormat(DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);

				Date d1 = null, d2 = null;
				try {
					d1 = sdfo.parse(a);
					d2 = sdfo.parse(b);
				} catch (JSONException e) {
					log.error(e);
				} catch (ParseException e) {
					log.error(e);
				}

				return d1.compareTo(d2);
			}
		});

	}

	@SuppressWarnings("unchecked")
	public static JSONArray getFutures(JSONObject symObj) throws Exception {

		JSONArray finalArray = new JSONArray();

		SymbolRow symRow = SymbolMap.getSymbolRow(symObj.getString(SymbolConstants.SYMBOL_TOKEN));
		String symbol = symRow.getSymbol();
		int precision = symRow.getPrecisionInt();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		String query = DBQueryConstants.GET_FUTURES_TOKEN;

		log.debug("Query :: " + query);

		RedisPool redisPool = new RedisPool();
		try {
			if (redisPool.isExists(RedisConstants.FUTURES_TOKEN + "_" + symbol + "_2" + "_" + InstrumentType.FUTURES)) {
				linkedsetSymbolToken = new Gson().fromJson(
						redisPool.getValue(
								RedisConstants.FUTURES_TOKEN + "_" + symbol + "_2" + "_" + InstrumentType.FUTURES),
						LinkedHashSet.class);
			} else {
				try {
					conn = GCDBPool.getInstance().getConnection();
					ps = conn.prepareStatement(query);
					ps.setString(1, symbol);
					ps.setString(2, ExchangeSegment.NFO_SEGMENT_ID);
					ps.setString(3, InstrumentType.FUTURES + "%");

					res = ps.executeQuery();

					while (res.next()) {
						linkedsetSymbolToken.add(res.getString(DBConstants.TOKEN_SEGMENT));
					}
					redisPool.setValues(
							RedisConstants.FUTURES_TOKEN + "_" + symbol + "_2" + "_" + InstrumentType.FUTURES,
							new Gson().toJson(linkedsetSymbolToken));
				} catch (Exception e) {
					log.warn(e);
				} finally {
					Helper.closeResultSet(res);
					Helper.closeStatement(ps);
					Helper.closeConnection(conn);
				}
			}
		} catch (JedisConnectionException e1) {
			log.error(e1);

			try {
				conn = GCDBPool.getInstance().getConnection();
				ps = conn.prepareStatement(query);
				ps.setString(1, symbol);
				ps.setString(2, ExchangeSegment.NFO_SEGMENT_ID);
				ps.setString(3, InstrumentType.FUTURES + "%");

				res = ps.executeQuery();

				while (res.next()) {
					linkedsetSymbolToken.add(res.getString(DBConstants.TOKEN_SEGMENT));
				}
			} catch (Exception e) {
				log.warn(e);
			} finally {
				Helper.closeResultSet(res);
				Helper.closeStatement(ps);
				Helper.closeConnection(conn);
			}

		}

		Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedsetSymbolToken);

		for (String sToken : linkedsetSymbolToken) {

			JSONObject obj = new JSONObject();

			QuoteDetails quote = mQuoteDetails.get(sToken);

			if (!SymbolMap.isValidSymbolTokenSegmentMap(sToken))
				continue;

			SymbolRow sSymObj = SymbolMap.getSymbolRow(sToken);

			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));

			if (quote == null) {
				obj.put(DeviceConstants.LTP, "--");
				obj.put(DeviceConstants.CHANGE, "--");
				obj.put(DeviceConstants.CHANGE_PERCENT, "--");
			} else {
				obj.put(DeviceConstants.LTP, PriceFormat.formatPrice(quote.sLTP, precision, false));
				obj.put(DeviceConstants.CHANGE, PriceFormat.formatPrice(quote.sChange, precision, false));
				obj.put(DeviceConstants.CHANGE_PERCENT, Math.abs(Double.parseDouble(quote.sChangePercent)));
			}

			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(sSymObj.getExpiry(), "ddMMMyyyy", "dd MMM yyyy"));

			finalArray.put(obj);
		}

		return finalArray;

	}

	public static JSONObject getCurrencyFNOOverview_101(SymbolRow symRow, String sToken, String sAppID)
			throws JSONException, GCException, SQLException {
		JSONObject fnoOverviewObj = new JSONObject();
		ExchangeQuote.executeFNOOverviewScripDataQuery(sToken, DeviceConstants.ASSET_EXCHANGE_NCD, fnoOverviewObj,
				symRow.getPrecision());
		String mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		ExchangeQuote.executeFNOOverviewDataQuery(symRow.getSymbol(), mappingSymbolUniqDesc,
				DBQueryConstants.FNO_OVERVIEW_NCD, symRow.getPrecisionInt(), fnoOverviewObj, symRow.getInstrument());
		fnoOverviewObj.put(DeviceConstants.CONTRACT_START, "--");
		fnoOverviewObj.put(DeviceConstants.CONTRACT_END, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_OPEN, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_CLOSE, "--");
		fnoOverviewObj.put(DeviceConstants.MAX_ORDER_SIZE, "--");
		fnoOverviewObj.put(DeviceConstants.DEL_UNITS, "--");
		fnoOverviewObj.put(DeviceConstants.MARKET_LOT, symRow.getLotSize());
		return fnoOverviewObj;
	}

	public static JSONObject getCurrencyFNOOverview(SymbolRow symRow, String sAppID)
			throws JSONException, GCException, SQLException {
		int precision = symRow.getPrecisionInt();
		String sToken = symRow.gettokenId();
		String sSymbolToken = symRow.getSymbolToken();
		String sExch = symRow.getExchange();

		JSONObject fnoOverviewObj = new JSONObject();
		CurrencyAPIRequest currencyAPIReq = new CurrencyAPIRequest();
		if (sExch.equals(ExchangeSegment.NSECDS))
			currencyAPIReq.setExch(ExchangeSegment.NSE);
		else if (sExch.equals(ExchangeSegment.BSECDS))
			currencyAPIReq.setExch(ExchangeSegment.BSE);
		currencyAPIReq.setScripCode(sToken);

		CurrencyAPI currencyAPI = new CurrencyAPI();
		CurrencyAPIResponse currencyAPIRes = currencyAPI.get(currencyAPIReq, CurrencyAPIResponse.class, sAppID,
				DeviceConstants.FNO_OVERVIEW_L + " " + DeviceConstants.CURRENCY);

		if (currencyAPIRes.getResponseObject().size() == 0)
			return fnoOverviewObj;

		CurrencyAPIObject currencyAPIObj = currencyAPIRes.getResponseObject().get(0);

		QuoteDetails quote = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		fnoOverviewObj.put(DeviceConstants.LTP, quote.sLTP);
		fnoOverviewObj.put(DeviceConstants.SPOT_PRICE,
				PriceFormat.formatPrice(currencyAPIObj.getSpotPrice(), precision, true));
		fnoOverviewObj.put(DeviceConstants.OI,
				PriceFormat.numberFormat(Math.round(Float.parseFloat(currencyAPIObj.getOI()))));
		fnoOverviewObj.put(DeviceConstants.OI_CHANGE,
				PriceFormat.numberFormat(Math.round(Float.parseFloat(currencyAPIObj.getOIChange()))));
		fnoOverviewObj.put(DeviceConstants.ROLLOVER,
				PriceFormat.formatPrice(currencyAPIObj.getRolloverPercentage(), precision, true) + "%");
		fnoOverviewObj.put(DeviceConstants.ROLLOVER_COST,
				PriceFormat.formatPrice(currencyAPIObj.getRollovercost(), precision, true));
		fnoOverviewObj.put(DeviceConstants.PCR, "--");
		fnoOverviewObj.put(DeviceConstants.IMPLIED_VOLATILITY, "--");
		fnoOverviewObj.put(DeviceConstants.CONTRACT_START, "--");
		fnoOverviewObj.put(DeviceConstants.CONTRACT_END, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_OPEN, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_CLOSE, "--");
		fnoOverviewObj.put(DeviceConstants.MAX_ORDER_SIZE, "--");
		fnoOverviewObj.put(DeviceConstants.DEL_UNITS, "--");
		fnoOverviewObj.put(DeviceConstants.PREMIUM, "-- %");
		fnoOverviewObj.put(DeviceConstants.OI_PER_CHANGE, "-- %");
		fnoOverviewObj.put(DeviceConstants.MARKET_LOT, symRow.getLotSize());

		return fnoOverviewObj;
	}

	public static JSONObject getMCXFNOOverview(SymbolRow symRow, String sAppID)
			throws JSONException, GCException, SQLException, ParseException {
		int precision = symRow.getPrecisionInt();
		String sToken = symRow.gettokenId();
		String sSymbolToken = symRow.getSymbolToken();

		JSONObject fnoOverviewObj = new JSONObject();
		MCXAPIRequest mcxAPIReq = new MCXAPIRequest();
		mcxAPIReq.setScripCode(sToken);

		MCXAPI mcxAPI = new MCXAPI();
		MCXAPIResponse mcxAPIRes = mcxAPI.get(mcxAPIReq, MCXAPIResponse.class, sAppID,
				DeviceConstants.FNO_OVERVIEW_L + " " + DeviceConstants.COMMODITY);

		if (mcxAPIRes.getResponseObject().size() == 0)
			return fnoOverviewObj;

		MCXAPIObject mcxAPIObj = mcxAPIRes.getResponseObject().get(0);

		QuoteDetails quote = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		fnoOverviewObj.put(DeviceConstants.LTP, quote.sLTP);
		fnoOverviewObj.put(DeviceConstants.SPOT_PRICE,
				PriceFormat.formatPrice(mcxAPIObj.getSpotPrice(), precision, true));
		fnoOverviewObj.put(DeviceConstants.OI,
				PriceFormat.numberFormat(Math.round(Float.parseFloat(mcxAPIObj.getOI()))));
		fnoOverviewObj.put(DeviceConstants.OI_CHANGE, mcxAPIObj.getOIChange().equalsIgnoreCase("NaN") ? "--"
				: PriceFormat.numberFormat(Math.round(Float.parseFloat(mcxAPIObj.getOIChange()))));
		fnoOverviewObj.put(DeviceConstants.ROLLOVER,
				PriceFormat.formatPrice(mcxAPIObj.getRolloverPercentage(), precision, true) + "%");
		fnoOverviewObj.put(DeviceConstants.ROLLOVER_COST,
				PriceFormat.formatPrice(mcxAPIObj.getRollovercost(), precision, true));
		fnoOverviewObj.put(DeviceConstants.CONTRACT_START, DateUtils.formatDate(mcxAPIObj.getContractstart(),
				DeviceConstants.MCX_DATE_FORMAT_FROM, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		fnoOverviewObj.put(DeviceConstants.CONTRACT_END, DateUtils.formatDate(mcxAPIObj.getContractEnd(),
				DeviceConstants.MCX_DATE_FORMAT_FROM, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		fnoOverviewObj.put(DeviceConstants.TENDER_OPEN, DateUtils.formatDate(mcxAPIObj.getTenderstart(),
				DeviceConstants.MCX_DATE_FORMAT_FROM, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		fnoOverviewObj.put(DeviceConstants.TENDER_CLOSE, DateUtils.formatDate(mcxAPIObj.getTenderEnd(),
				DeviceConstants.MCX_DATE_FORMAT_FROM, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
		fnoOverviewObj.put(DeviceConstants.MAX_ORDER_SIZE, mcxAPIObj.getMaxordersize());
		fnoOverviewObj.put(DeviceConstants.DEL_UNITS, mcxAPIObj.getDeliveryunit());
		fnoOverviewObj.put(DeviceConstants.PCR, "--");
		fnoOverviewObj.put(DeviceConstants.IMPLIED_VOLATILITY, "--");
		fnoOverviewObj.put(DeviceConstants.PREMIUM,
				PriceFormat.formatPrice(mcxAPIObj.getPremiumPercentage(), precision, false) + " %");
		fnoOverviewObj.put(DeviceConstants.OI_PER_CHANGE,
				PriceFormat.formatPrice(mcxAPIObj.getOIChangePercentage(), precision, false) + " %");

		fnoOverviewObj.put(DeviceConstants.MARKET_LOT, symRow.getLotSize());

		return fnoOverviewObj;
	}

	public static JSONObject getMCXFNOOverview_101(SymbolRow symRow, String sToken, String sAppID)
			throws JSONException, GCException, SQLException, ParseException {

		JSONObject fnoOverviewObj = new JSONObject();
		ExchangeQuote.executeFNOOverviewScripDataQuery(sToken, DeviceConstants.ASSET_EXCHANGE_MCX, fnoOverviewObj,
				symRow.getPrecision());
		String mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		ExchangeQuote.executeFNOOverviewDataQuery(symRow.getSymbol(), mappingSymbolUniqDesc,
				DBQueryConstants.FNO_OVERVIEW_MFO, symRow.getPrecisionInt(), fnoOverviewObj, symRow.getInstrument());
		fnoOverviewObj.put(DeviceConstants.CONTRACT_START,
				getDateFormat(Integer.parseInt(fnoOverviewObj.getString(DeviceConstants.CONTRACT_START))));
		fnoOverviewObj.put(DeviceConstants.CONTRACT_END,
				getDateFormat(Integer.parseInt(fnoOverviewObj.getString(DeviceConstants.CONTRACT_END))));
		fnoOverviewObj.put(DeviceConstants.TENDER_OPEN,
				getDateFormat(Integer.parseInt(fnoOverviewObj.getString(DeviceConstants.TENDER_OPEN))));
		fnoOverviewObj.put(DeviceConstants.TENDER_CLOSE,
				getDateFormat(Integer.parseInt(fnoOverviewObj.getString(DeviceConstants.TENDER_CLOSE))));
		fnoOverviewObj.put(DeviceConstants.MARKET_LOT, symRow.getLotSize());
		return fnoOverviewObj;
	}

	private static String getDateFormat(int time) {
		long timeFrom1980 = 315513000 + time;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeFrom1980 * 1000);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT);
		return sdf.format(date);
	}

	public static JSONObject getEquityNFOOverView(SymbolRow symRow, String sAppID)
			throws JSONException, GCException, SQLException {
		int precision = symRow.getPrecisionInt();
		String sToken = symRow.gettokenId();
		String sSymbolToken = symRow.getSymbolToken();
		JSONObject fnoOverviewObj = new JSONObject();
		FOAPIRequest foAPIReq = new FOAPIRequest();
		foAPIReq.setScripCode(sToken);

		FOAPI foAPI = new FOAPI();

		FOAPIResponse foAPIRes = foAPI.get(foAPIReq, FOAPIResponse.class, sAppID,
				DeviceConstants.FNO_OVERVIEW_L + " " + DeviceConstants.EQUITY);

		if (foAPIRes.getResponseObject().size() == 0)
			return fnoOverviewObj;

		FOAPIObject foAPIObj = foAPIRes.getResponseObject().get(0);

		QuoteDetails quote = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());

		String sLTP = quote.sLTP;

		QuoteDetails assetQuote = null;
		if (symRow.getMktSegId().equals(ExchangeSegment.NFO_SEGMENT_ID)) {
			String sAssetToken = symRow.getAssetToken();
			assetQuote = Quote.getLTP(sAssetToken + "_" + ExchangeSegment.NSE_SEGMENT_ID,
					symRow.getMappingSymbolUniqDesc());
			String sAssetTokenLTP = assetQuote.sLTP;

			if ((sLTP == null || sAssetTokenLTP == null)
					|| (sLTP.equalsIgnoreCase("--") || sAssetTokenLTP.equalsIgnoreCase("--"))
					|| (sLTP.isEmpty() || sAssetTokenLTP.isEmpty())) {
				fnoOverviewObj.put(DeviceConstants.SPOT_PRICE, "--");
				fnoOverviewObj.put(DeviceConstants.PREMIUM, "--");
			} else {
				double dLTP = Double.parseDouble(quote.sLTP);
				double dAssetLTP = Double.parseDouble(assetQuote.sLTP);

				fnoOverviewObj.put(DeviceConstants.SPOT_PRICE,
						PriceFormat.formatPrice(assetQuote.sLTP, precision, true));
				fnoOverviewObj.put(DeviceConstants.PREMIUM, PriceFormat
						.formatPrice(Double.toString(((dLTP - dAssetLTP) / dAssetLTP) * 100), precision, true) + " %");
			}

		} else {
			fnoOverviewObj.put(DeviceConstants.SPOT_PRICE,
					PriceFormat.formatPrice(foAPIObj.getSpotPrice(), precision, true));
			fnoOverviewObj.put(DeviceConstants.PREMIUM,
					PriceFormat.formatPrice(foAPIObj.getPremiumPercentage(), precision, true) + " %");
		}
		fnoOverviewObj.put(DeviceConstants.LTP, sLTP != null ? sLTP : "--");
		fnoOverviewObj.put(DeviceConstants.OI,
				PriceFormat.numberFormat(Math.round(Float.parseFloat(foAPIObj.getOI()))));
		if (foAPIObj.getOIChange().equals(""))
			fnoOverviewObj.put(DeviceConstants.OI_CHANGE, "--");
		else
			fnoOverviewObj.put(DeviceConstants.OI_CHANGE,
					PriceFormat.numberFormat(Math.round(Float.parseFloat(foAPIObj.getOIChange()))));
		fnoOverviewObj.put(DeviceConstants.OI_PER_CHANGE,
				PriceFormat.formatPrice(foAPIObj.getOIChangePercentage(), precision, true) + " %");
		fnoOverviewObj.put(DeviceConstants.PCR, PriceFormat.formatPrice(foAPIObj.getPCR(), precision, true));
		fnoOverviewObj.put(DeviceConstants.IMPLIED_VOLATILITY,
				PriceFormat.formatPrice(foAPIObj.getIV(), precision, true));

		FORolloverRequest foRolloverReq = new FORolloverRequest();
		foRolloverReq.setScripCode(sToken);

		FORolloverAPI foRolloverAPI = new FORolloverAPI();

		FORolloverResponse foRolloverRes = foRolloverAPI.get(foRolloverReq, FORolloverResponse.class, sAppID,
				"FO Rollover " + DeviceConstants.EQUITY);

		FORolloverObject foRolloverObj = foRolloverRes.getResponseObject().get(0);

		fnoOverviewObj.put(DeviceConstants.ROLLOVER,
				PriceFormat.formatPrice(foRolloverObj.getRolloverPercentage(), precision, true) + "%");
		fnoOverviewObj.put(DeviceConstants.ROLLOVER_COST,
				PriceFormat.formatPrice(foRolloverObj.getRollovercost(), precision, true));
		fnoOverviewObj.put(DeviceConstants.CONTRACT_START, "--");
		fnoOverviewObj.put(DeviceConstants.CONTRACT_END, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_OPEN, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_CLOSE, "--");
		fnoOverviewObj.put(DeviceConstants.MAX_ORDER_SIZE, "--");
		fnoOverviewObj.put(DeviceConstants.DEL_UNITS, "--");
		fnoOverviewObj.put(DeviceConstants.MARKET_LOT, symRow.getLotSize());

		return fnoOverviewObj;
	}

	public static JSONObject getNFOFNOOverView_101(SymbolRow symRow, String sToken, String sAppID)
			throws JSONException, GCException, SQLException {
		JSONObject fnoOverviewObj = new JSONObject();
		ExchangeQuote.executeFNOOverviewScripDataQuery(sToken, DeviceConstants.ASSET_EXCHANGE_NFO, fnoOverviewObj,
				symRow.getPrecision());
		String mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		ExchangeQuote.executeFNOOverviewDataQuery(symRow.getSymbol(), mappingSymbolUniqDesc,
				DBQueryConstants.FNO_OVERVIEW_NFO, symRow.getPrecisionInt(), fnoOverviewObj, symRow.getInstrument());
		fnoOverviewObj.put(DeviceConstants.CONTRACT_START, "--");
		fnoOverviewObj.put(DeviceConstants.CONTRACT_END, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_OPEN, "--");
		fnoOverviewObj.put(DeviceConstants.TENDER_CLOSE, "--");
		fnoOverviewObj.put(DeviceConstants.MARKET_LOT, symRow.getLotSize());
		return fnoOverviewObj;
	}

	public static JSONArray getResults(JSONObject symObj, String sType, String sAppID) {
		JSONArray finalArr = new JSONArray();

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		int precision = symRow.getPrecisionInt();

		String sCoCode = symRow.getCMCoCode();
		int indexCoCode = sCoCode.indexOf(".");
		if (indexCoCode != -1)
			sCoCode = sCoCode.substring(0, indexCoCode);

		GetResults resultsObj = new GetResults(getAppIDForLogging(sAppID));

		resultsObj.setCoCode(sCoCode);
		resultsObj.setFinFormat(sType);

		ResultsList resultsList = new ResultsList();

		List<JSONObject> parsedResultsList = new ArrayList<JSONObject>();

		try {
			resultsList = resultsObj.invoke();
		} catch (CMOTSException e) {
			log.warn(e);
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Results" + " " + e.getMessage());
		}

		for (Results results : resultsList) {
			JSONObject obj = new JSONObject();

			double expenditureTotal = 0;

			String sYRC = results.getYRC();
			int index = sYRC.indexOf(".");
			if (index != -1)
				sYRC = sYRC.substring(0, index);
			int year = Integer.parseInt(sYRC.substring(0, 4));
			int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));

			obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

			JSONObject netSalesObj = new JSONObject();
			netSalesObj.put(DeviceConstants.TOTAL, PriceFormat.formatPrice(results.getTotal(), precision, true));
			obj.put(DeviceConstants.NET_SALES, netSalesObj);

			JSONObject expenditureObj = new JSONObject();
			expenditureObj.put(DeviceConstants.PBIDT, PriceFormat.formatPrice(results.getPBIDT(), precision, true));
			expenditureObj.put(DeviceConstants.OTHER_INCOME,
					PriceFormat.formatPrice(results.getOtherIncome(), precision, true));
			expenditureObj.put(DeviceConstants.OPERATING_PROFIT,
					PriceFormat.formatPrice(results.getOperatingProfit(), precision, true));
			expenditureObj.put(DeviceConstants.INTEREST,
					PriceFormat.formatPrice(results.getInterest(), precision, true));
			expenditureObj.put(DeviceConstants.EXCEP_ITEMS,
					PriceFormat.formatPrice(results.getExceptionalItem(), precision, true));
			expenditureObj.put(DeviceConstants.PBDT, PriceFormat.formatPrice(results.getPBDT(), precision, true));
			expenditureObj.put(DeviceConstants.DEPRECIATION,
					PriceFormat.formatPrice(results.getDepreciation(), precision, true));
			expenditureObj.put(DeviceConstants.PBT, PriceFormat.formatPrice(results.getPBT(), precision, true));
			expenditureObj.put(DeviceConstants.TAX, PriceFormat.formatPrice(results.getTax(), precision, true));
			expenditureObj.put(DeviceConstants.PROFIT_AFTER_TAX,
					PriceFormat.formatPrice(results.getProfitAfterTax(), precision, true));

			expenditureTotal = expenditureTotal + Double.parseDouble(results.getPBIDT())
					+ Double.parseDouble(results.getOtherIncome()) + Double.parseDouble(results.getOperatingProfit())
					+ Double.parseDouble(results.getInterest()) + Double.parseDouble(results.getExceptionalItem())
					+ Double.parseDouble(results.getPBDT()) + Double.parseDouble(results.getDepreciation())
					+ Double.parseDouble(results.getPBT()) + Double.parseDouble(results.getTax())
					+ Double.parseDouble(results.getProfitAfterTax());
			expenditureObj.put(DeviceConstants.TOTAL,
					PriceFormat.formatPrice(Double.toString(expenditureTotal), precision, true));
			obj.put(DeviceConstants.EXPENDITURE, expenditureObj);

			parsedResultsList.add(obj);

		}

		sortJSONArrayDate(parsedResultsList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);

		for (int i = 0; i < parsedResultsList.size(); i++) {
			JSONObject obj = new JSONObject();

			obj.put(DeviceConstants.PERIOD, parsedResultsList.get(i).getString(DeviceConstants.PERIOD));
			parsedResultsList.get(i).remove(DeviceConstants.PERIOD);
			obj.put(DeviceConstants.RESULT_DATA, parsedResultsList.get(i));
			finalArr.put(obj);

		}

		return finalArr;
	}

	public static String getAppIDForLogging(String sAppID) {
		return "AppID: " + sAppID + " ";
	}

	public static JSONArray getEvents(String sSymbolToken, String sAction, boolean isOverview, String sAppID)
			throws AppConfigNoKeyFoundException, JSONException, ParseException, CMOTSException {

		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		String sCoCode = symRow.getCMCoCode();
		String sExchange = symRow.getExchange();

		int index = sCoCode.indexOf(".");
		if (index != -1)
			sCoCode = sCoCode.substring(0, index);

		String sRecordCount = "-";

		if (isOverview)
			sRecordCount = AppConfig.getValue("market_limit");
		String sAppIDForLogging = getAppIDForLogging(sAppID);

		List<JSONObject> parsedFinalList = new ArrayList<JSONObject>();

		if (sAction.equalsIgnoreCase(DeviceConstants.ALL)) {
			List<JSONObject> parsedDividendList = new ArrayList<>();
			List<JSONObject> parsedBonusList = new ArrayList<>();
			List<JSONObject> parsedSplitsList = new ArrayList<>();
			List<JSONObject> parsedRightsList = new ArrayList<>();
			List<JSONObject> parsedAnnouncementList = new ArrayList<>();
			isTimedOut = false;
			if (!isTimedOut)
				parsedDividendList = getDividend(sCoCode, sRecordCount, sAction, sAppIDForLogging);
			if (!isTimedOut)
				parsedBonusList = getBonus(sCoCode, sRecordCount, sAction, sAppIDForLogging);
			if (!isTimedOut)
				parsedSplitsList = getSplits(sCoCode, sRecordCount, sAction, sAppIDForLogging);
			if (!isTimedOut)
				parsedRightsList = getRights(sCoCode, sRecordCount, sAction, sAppIDForLogging);
			if (!isTimedOut)
				parsedAnnouncementList = getCompAnnouncements(sCoCode, sExchange, sAction, sAppIDForLogging);

			for (int i = 0; i < parsedDividendList.size(); i++)
				parsedFinalList.add(parsedDividendList.get(i));
			for (int i = 0; i < parsedBonusList.size(); i++)
				parsedFinalList.add(parsedBonusList.get(i));
			for (int i = 0; i < parsedSplitsList.size(); i++)
				parsedFinalList.add(parsedSplitsList.get(i));
			for (int i = 0; i < parsedRightsList.size(); i++)
				parsedFinalList.add(parsedRightsList.get(i));
			for (int i = 0; i < parsedAnnouncementList.size(); i++)
				parsedFinalList.add(parsedAnnouncementList.get(i));

		} else if (sAction.equalsIgnoreCase(DeviceConstants.FILTER_DIVIDEND))
			parsedFinalList = getDividend(sCoCode, sRecordCount, sAction, sAppIDForLogging);
		else if (sAction.equalsIgnoreCase(DeviceConstants.FILTER_BONUS))
			parsedFinalList = getBonus(sCoCode, sRecordCount, sAction, sAppIDForLogging);
		else if (sAction.equalsIgnoreCase(DeviceConstants.FILTER_STOCK_SPLIT))
			parsedFinalList = getSplits(sCoCode, sRecordCount, sAction, sAppIDForLogging);
		else if (sAction.equalsIgnoreCase(DeviceConstants.FILTER_RIGHTS))
			parsedFinalList = getRights(sCoCode, sRecordCount, sAction, sAppIDForLogging);
		else if (sAction.equalsIgnoreCase(DeviceConstants.FILTER_COMP_ANNOUNCEMENTS))
			parsedFinalList = getCompAnnouncements(sCoCode, sExchange, sAction, sAppIDForLogging);

		sortJSONArrayDate(parsedFinalList, DeviceConstants.EX_DATE_S, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);

		JSONArray finalArr = new JSONArray();
		for (int i = 0; i < parsedFinalList.size(); i++) {
			JSONObject obj = parsedFinalList.get(i);

			if ((sAction.equalsIgnoreCase(DeviceConstants.FILTER_COMP_ANNOUNCEMENTS))
					|| (sAction.equalsIgnoreCase(DeviceConstants.ALL) && obj.getString(DeviceConstants.ACTION)
							.equalsIgnoreCase(DeviceConstants.COMPANY_ANNOUNCEMENT))) {
				obj.remove(DeviceConstants.EX_DATE_S);
				obj.put(DeviceConstants.EX_DATE_S, "");

			}

			finalArr.put(obj);
		}

		return finalArr;
	}

	@SuppressWarnings("unchecked")
	private static List<JSONObject> getDividend(String sCoCode, String sRecordCount, String sAction,
			String sAppIDForLogging) throws JSONException, ParseException {
		GetDividend dividendObj = new GetDividend(sAppIDForLogging);
		dividendObj.setCoCode(sCoCode);
		dividendObj.setRecordCount(sRecordCount);
		RedisPool redisPool = new RedisPool();
		List<JSONObject> parsedDividendList = new ArrayList<JSONObject>();
		try {
			DividendList dividendList = new DividendList();
			try {
			if (redisPool.isExists(RedisConstants.DIVIDENT_LIST + "_" + sCoCode)) {
				dividendList = new Gson().fromJson(redisPool.getValue(RedisConstants.DIVIDENT_LIST + "_" + sCoCode),
						DividendList.class);
			} else {
				dividendList = dividendObj.invoke();
				redisPool.setValues(RedisConstants.DIVIDENT_LIST + "_" + sCoCode, new Gson().toJson(dividendList));
			}
			}catch (Exception e) {
				log.error(e);
				dividendList = dividendObj.invoke();
			}

			for (Dividend dividend : dividendList) {
				JSONObject obj = new JSONObject();

				if (sAction.equalsIgnoreCase(DeviceConstants.ALL))
					obj.put(DeviceConstants.ACTION, DeviceConstants.DIVIDEND);

				obj.put(DeviceConstants.ANNOUNCE_DATE,
						dividend.getAnnouncementDate() == null ? "--"
								: DateUtils.formatDate(dividend.getAnnouncementDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.EX_DATE_S, DateUtils.formatDate(dividend.getDivDate(),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.RECORD_DATE,
						dividend.getRecordDate() == null ? "--"
								: DateUtils.formatDate(dividend.getRecordDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.SHORT_DESC,
						dividend.getDivAmount() == null ? "--" : getDividendShortDesc(dividend.getDivAmount()));
				obj.put(DeviceConstants.DETAIL_DESC, "");
				obj.put(DeviceConstants.IS_SINGLE_DATE, "false");
				obj.put(DeviceConstants.TYPE,
						dividend.getDividendType() == null ? "--" : dividend.getDividendType().toUpperCase());
				parsedDividendList.add(obj);

			}
		} catch (CMOTSException e) {
			if (e.getMessage().contains("Unable to connect to API"))
				isTimedOut = true;
			log.warn(e);
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Dividend" + " " + e.getMessage());
		}

		return parsedDividendList;
	}

	@SuppressWarnings("unchecked")
	private static List<JSONObject> getBonus(String sCoCode, String sRecordCount, String sAction,
			String sAppIDForLogging) throws JSONException, ParseException {
		List<JSONObject> parsedBonusList = new ArrayList<JSONObject>();
		GetBonus bonusObj = new GetBonus(sAppIDForLogging);
		bonusObj.setCoCode(sCoCode);
		bonusObj.setRecordCount(sRecordCount);
		RedisPool redisPool = new RedisPool();

		try {
			BonusList bonusList = new BonusList();
			try {
			if (redisPool.isExists(RedisConstants.BONUS_LIST + "_" + sCoCode)) {
				bonusList = new Gson().fromJson(redisPool.getValue(RedisConstants.BONUS_LIST + "_" + sCoCode),
						BonusList.class);
			} else {
				bonusList = bonusObj.invoke();
				redisPool.setValues(RedisConstants.BONUS_LIST + "_" + sCoCode, new Gson().toJson(bonusList));
			}
			}catch (Exception e) {
				log.error(e);
				bonusList = bonusObj.invoke();
			}

			for (Bonus bonus : bonusList) {
				JSONObject obj = new JSONObject();
				if (sAction.equalsIgnoreCase(DeviceConstants.ALL))
					obj.put(DeviceConstants.ACTION, DeviceConstants.BONUS);

				obj.put(DeviceConstants.ANNOUNCE_DATE,
						bonus.getAnnouncementDate() == null ? "--"
								: DateUtils.formatDate(bonus.getAnnouncementDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.EX_DATE_S, DateUtils.formatDate(bonus.getBonusDate(),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.RECORD_DATE,
						bonus.getRecordDate() == null ? "--"
								: DateUtils.formatDate(bonus.getRecordDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.SHORT_DESC,
						bonus.getBonusRatio() == null ? "--" : getBonusShortDesc(bonus.getBonusRatio()));
				obj.put(DeviceConstants.DETAIL_DESC, "");
				obj.put(DeviceConstants.IS_SINGLE_DATE, "false");
				obj.put(DeviceConstants.TYPE, "");
				parsedBonusList.add(obj);
			}
		} catch (CMOTSException e) {
			if (e.getMessage().contains("Unable to connect to API"))
				isTimedOut = true;
			log.warn(e);
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Bonus" + " " + e.getMessage());
		}
		return parsedBonusList;
	}

	@SuppressWarnings("unchecked")
	private static List<JSONObject> getSplits(String sCoCode, String sRecordCount, String sAction,
			String sAppIDForLogging) throws JSONException, ParseException {
		List<JSONObject> parsedSplitsList = new ArrayList<JSONObject>();
		GetSplits splitsObj = new GetSplits(sAppIDForLogging);
		splitsObj.setCoCode(sCoCode);
		splitsObj.setRecordCount(sRecordCount);
		RedisPool redisPool = new RedisPool();

		try {
			SplitsList splitsList = new SplitsList();
			try {
			if (redisPool.isExists(RedisConstants.SPLITS_LIST + "_" + sCoCode)) {
				splitsList = new Gson().fromJson(redisPool.getValue(RedisConstants.SPLITS_LIST + "_" + sCoCode),
						SplitsList.class);
			} else {
				splitsList = splitsObj.invoke();
				redisPool.setValues(RedisConstants.SPLITS_LIST + "_" + sCoCode, new Gson().toJson(splitsList));
			}
			}catch (Exception e) {
				log.error(e);
				splitsList = splitsObj.invoke();
			}
			
			for (Splits splits : splitsList) {
				JSONObject obj = new JSONObject();

				if (sAction.equalsIgnoreCase(DeviceConstants.ALL))
					obj.put(DeviceConstants.ACTION, DeviceConstants.STOCK_SPLIT);

				obj.put(DeviceConstants.ANNOUNCE_DATE,
						splits.getAnnouncementDate() == null ? "--"
								: DateUtils.formatDate(splits.getAnnouncementDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.EX_DATE_S, DateUtils.formatDate(splits.getSplitDate(),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.RECORD_DATE,
						splits.getRecordDate() == null ? "--"
								: DateUtils.formatDate(splits.getRecordDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.SHORT_DESC,
						getSplitsShortDesc(splits.getSplitRatio(), splits.getFVBefore(), splits.getFVAfter()));
				obj.put(DeviceConstants.DETAIL_DESC, "");
				obj.put(DeviceConstants.IS_SINGLE_DATE, "false");
				obj.put(DeviceConstants.TYPE, "");
				parsedSplitsList.add(obj);
			}
		} catch (CMOTSException e) {
			if (e.getMessage().contains("Unable to connect to API"))
				isTimedOut = true;
			log.warn(e);
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Splits" + " " + e.getMessage());
		}
		return parsedSplitsList;
	}

	@SuppressWarnings("unchecked")
	private static List<JSONObject> getRights(String sCoCode, String sRecordCount, String sAction,
			String sAppIDForLogging) throws JSONException, ParseException {
		List<JSONObject> parsedRightsList = new ArrayList<JSONObject>();
		GetRights rightsObj = new GetRights(sAppIDForLogging);
		rightsObj.setCoCode(sCoCode);
		rightsObj.setRecordCount(sRecordCount);
		RedisPool redisPool = new RedisPool();

		try {
			RightsList rightsList = new RightsList();
			try {
			if (redisPool.isExists(RedisConstants.RIGHTS_LIST + "_" + sCoCode)) {
				rightsList = new Gson().fromJson(redisPool.getValue(RedisConstants.RIGHTS_LIST + "_" + sCoCode),
						RightsList.class);
			} else {
				rightsList = rightsObj.invoke();
				redisPool.setValues(RedisConstants.RIGHTS_LIST + "_" + sCoCode, new Gson().toJson(rightsList));
			}
			}catch (Exception e) {
				log.error(e);
				rightsList = rightsObj.invoke();
			}

			for (Rights rights : rightsList) {
				JSONObject obj = new JSONObject();

				if (sAction.equalsIgnoreCase(DeviceConstants.ALL))
					obj.put(DeviceConstants.ACTION, DeviceConstants.RIGHTS);

				obj.put(DeviceConstants.ANNOUNCE_DATE,
						rights.getAnnouncementDate() == null ? "--"
								: DateUtils.formatDate(rights.getAnnouncementDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.EX_DATE_S, DateUtils.formatDate(rights.getRightDate(),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.RECORD_DATE,
						rights.getRecordDate() == null ? "--"
								: DateUtils.formatDate(rights.getRecordDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.SHORT_DESC, getRightsShortDesc(rights.getRightsRatio(), rights.getPremium()));
				obj.put(DeviceConstants.DETAIL_DESC, "");
				obj.put(DeviceConstants.IS_SINGLE_DATE, "false");
				obj.put(DeviceConstants.TYPE, "");
				parsedRightsList.add(obj);

			}
		} catch (CMOTSException e) {
			if (e.getMessage().contains("Unable to connect to API"))
				isTimedOut = true;
			log.warn(e);
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Rights" + " " + e.getMessage());
		}
		return parsedRightsList;
	}

	private static List<JSONObject> getCompAnnouncements(String sCoCode, String sExchange, String sAction,
			String sAppIDForLogging) throws JSONException, ParseException, CMOTSException {
		List<JSONObject> parsedAnnouncementList = new ArrayList<JSONObject>();
		AnnouncementList announcementList = new AnnouncementList();
		if (sExchange.equalsIgnoreCase(ExchangeSegment.NSE)) {
			announcementList = AnnouncementCache.getNSEAnnouncementList();
			if (announcementList.size() == 0) {
				AnnouncementCache.loadNSEAnnouncementCache();
				announcementList = AnnouncementCache.getNSEAnnouncementList();
			}
		} else {
			announcementList = AnnouncementCache.getBSEAnnouncementList();
			if (announcementList.size() == 0) {
				AnnouncementCache.loadBSEAnnouncementCache();
				announcementList = AnnouncementCache.getBSEAnnouncementList();
			}
		}

		try {
			for (Announcement announcement : announcementList) {

				String sAnnouncementCoCode = announcement.getCoCode();
				int indexCoCode = sAnnouncementCoCode.indexOf(".");
				if (indexCoCode != -1)
					sAnnouncementCoCode = sAnnouncementCoCode.substring(0, indexCoCode);

				if (sAnnouncementCoCode.equalsIgnoreCase(sCoCode)) {
					JSONObject obj = new JSONObject();

					if (sAction.equalsIgnoreCase(DeviceConstants.ALL))
						obj.put(DeviceConstants.ACTION, DeviceConstants.COMPANY_ANNOUNCEMENT);
					obj.put(DeviceConstants.ANNOUNCE_DATE,
							DateUtils.formatDate(announcement.getDate(), DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
									DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
					obj.put(DeviceConstants.EX_DATE_S,
							DateUtils.formatDate(announcement.getDate(), DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
									DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
					obj.put(DeviceConstants.RECORD_DATE, "");
					obj.put(DeviceConstants.SHORT_DESC, announcement.getCaption());
					obj.put(DeviceConstants.DETAIL_DESC, announcement.getMemo() + "\n");
					obj.put(DeviceConstants.IS_SINGLE_DATE, "true");
					obj.put(DeviceConstants.TYPE, "");
					parsedAnnouncementList.add(obj);
				}
			}
		} catch (Exception e) {
			if (e.getMessage().contains("Unable to connect to API"))
				isTimedOut = true;
			log.warn(e);
		}
		return parsedAnnouncementList;
	}

	public static String getRightsShortDesc(String rightsRatio, String premium) {

		int indexPremium = premium.indexOf(".");
		if (indexPremium != -1)
			premium = premium.substring(0, indexPremium);

		String sRightRatio = String.format(InfoMessage.getInfoMSG("info_msg.events.right"), rightsRatio);
		String sRightDesc = String.format(InfoMessage.getInfoMSG("info_msg.events.right_desc"), premium);

		return sRightRatio + "\n" + sRightDesc;
	}

	public static String getSplitsShortDesc(String splitRatio, String fvBefore, String fvAfter) {

		String sSplitRatio = String.format(InfoMessage.getInfoMSG("info_msg.events.split"), splitRatio);

		int indexBefore = fvBefore.indexOf(".");
		if (indexBefore != -1)
			fvBefore = fvBefore.substring(0, indexBefore);

		int indexAfter = fvAfter.indexOf(".");
		if (indexAfter != -1)
			fvAfter = fvAfter.substring(0, indexAfter);

		String sFVBefore = String.format(InfoMessage.getInfoMSG("info_msg.events.split_desc_before"), fvBefore);
		String sFVAfter = String.format(InfoMessage.getInfoMSG("info_msg.events.split_desc_after"), fvAfter);

		return sSplitRatio + "\n" + sFVBefore + "\n" + sFVAfter;
	}

	public static String getBonusShortDesc(String bonusRatio) {
		String[] arrSplit = bonusRatio.split(":");
		String sRatioValue1 = arrSplit[0];
		String sRatioValue2 = arrSplit[1];

		String sBonusRatio = String.format(InfoMessage.getInfoMSG("info_msg.events.bonus"), bonusRatio);
		String sBonusDesc = String.format(InfoMessage.getInfoMSG("info_msg.events.bonus_desc"), sRatioValue2,
				sRatioValue1);

		return sBonusRatio + "\n" + sBonusDesc;
	}

	public static String getDividendShortDesc(String divAmount) {

		int index = divAmount.indexOf(".");
		if (index != -1) {
			String sAfterPoint = divAmount.substring(index + 1, divAmount.length());

			if (sAfterPoint.equals("0"))
				divAmount = divAmount.substring(0, index);
		}

		return String.format(InfoMessage.getInfoMSG("info_msg.events.dividend"), divAmount);
	}

	public static SymbolRow getSymbolRowUsingISIN(String sISIN) {
		SymbolRow symRow = SymbolMap.getISINSymbolRow(sISIN + "_" + ExchangeSegment.NSE_SEGMENT_ID);

		if (symRow == null)
			symRow = SymbolMap.getISINSymbolRow(sISIN + "_" + ExchangeSegment.BSE_SEGMENT_ID);

		return symRow;
	}

	public static JSONArray getKeyRatio_101(JSONObject symObj, String sType, String sAppID) {
		JSONArray finalArr = new JSONArray();

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);

		String sCoCode = symRow.getCMCoCode();
		String sSectorFormat = symRow.getCMSectorFormat();

		GetKeyRatios keyRatioObj = new GetKeyRatios(getAppIDForLogging(sAppID));

		keyRatioObj.setCoCode(sCoCode);
		keyRatioObj.setFinFormat(sType);

		JSONArray periodArr = new JSONArray();
		List<JSONObject> parsedMarginRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedPerformanceRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedEfficiencyRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedGrowthRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedLiquidityRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedFinancialStabilityRatioList = new ArrayList<JSONObject>();
		List<JSONObject> parsedValuationRatioList = new ArrayList<JSONObject>();

		parsedMarginRatioList = getMarginRatio(keyRatioObj, symRow, sType, sCoCode);
		parsedPerformanceRatioList = getPerformanceRatio(keyRatioObj, symRow, sType, sCoCode);
		parsedEfficiencyRatioList = getEfficiencyRatio(keyRatioObj, symRow, sType, sCoCode);
		parsedGrowthRatioList = getGrowthRatio(keyRatioObj, symRow, sType, sCoCode);
		parsedValuationRatioList = getValuationRatio(keyRatioObj, symRow, sType, sCoCode);

		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK))
			parsedLiquidityRatioList = getLiquidityRatio(keyRatioObj, symRow, sType, sCoCode);
		else
			parsedFinancialStabilityRatioList = getFinancialStabilityRatio(keyRatioObj, symRow, sType, sCoCode);

		try {
			sortJSONArrayDate(parsedMarginRatioList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
					false);
			sortJSONArrayDate(parsedPerformanceRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			sortJSONArrayDate(parsedEfficiencyRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			sortJSONArrayDate(parsedGrowthRatioList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
					false);
			if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK))
				sortJSONArrayDate(parsedLiquidityRatioList, DeviceConstants.PERIOD,
						DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			else
				sortJSONArrayDate(parsedFinancialStabilityRatioList, DeviceConstants.PERIOD,
						DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
			sortJSONArrayDate(parsedValuationRatioList, DeviceConstants.PERIOD,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);

			for (int i = 0; i < parsedMarginRatioList.size(); i++) {
				JSONObject obj = parsedMarginRatioList.get(i);
				if (obj.has(DeviceConstants.PERIOD)) {
					periodArr.put(obj.getString(DeviceConstants.PERIOD));
				}
			}

			for (int i = 0; i < periodArr.length(); i++) {

				JSONArray ratioArr = new JSONArray();
				JSONObject eachObj = new JSONObject();

				eachObj.put(DeviceConstants.PERIOD, periodArr.get(i));

				ratioArr.put(getKeyRatioByYear_101((String) periodArr.get(i), parsedMarginRatioList,
						DeviceConstants.MARGIN_RATIOS, sSectorFormat));
				ratioArr.put(getKeyRatioByYear_101((String) periodArr.get(i), parsedPerformanceRatioList,
						DeviceConstants.PERFORMANCE_RATIOS, sSectorFormat));
				ratioArr.put(getKeyRatioByYear_101((String) periodArr.get(i), parsedEfficiencyRatioList,
						DeviceConstants.EFFICIENCY_RATIOS, sSectorFormat));
				ratioArr.put(getKeyRatioByYear_101((String) periodArr.get(i), parsedGrowthRatioList,
						DeviceConstants.GROWTH_RATIOS, sSectorFormat));
				if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK))
					ratioArr.put(getKeyRatioByYear_101((String) periodArr.get(i), parsedLiquidityRatioList,
							DeviceConstants.LIQUIDITY_RATIOS, sSectorFormat));
				else
					ratioArr.put(getKeyRatioByYear_101((String) periodArr.get(i), parsedFinancialStabilityRatioList,
							DeviceConstants.FINANCIAL_STABILITY_RATIOS, sSectorFormat));

				ratioArr.put(getKeyRatioByYear_101((String) periodArr.get(i), parsedValuationRatioList,
						DeviceConstants.VALUATION_RATIOS, sSectorFormat));

				eachObj.put(DeviceConstants.RATIO_DATA, ratioArr);
				finalArr.put(eachObj);
			}
		} catch (Exception e) {
			log.warn(e);
		}

		return finalArr;
	}

	private static JSONObject getKeyRatioByYear_101(String sPeriod, List<JSONObject> keyRatio, String sRatioType,
			String sSectorFormat) {
		JSONObject obj = new JSONObject();

		boolean foundPeriod = false;

		if (keyRatio.size() > 0) {
			for (int i = 0; i < keyRatio.size(); i++) {
				JSONObject keyRatioObj = keyRatio.get(i);
				if (keyRatioObj.getString(DeviceConstants.PERIOD).equals(sPeriod)) {
					foundPeriod = true;
					obj = keyRatioObj;
				}
			}
		}

		if ((!foundPeriod) || (keyRatio.size() <= 0)) {
			if (sRatioType.equals(DeviceConstants.MARGIN_RATIOS)) {
				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_MARGIN_RATIOS);
				obj.put(DeviceConstants.PERIOD, sPeriod);
				JSONArray nameValuesArr = new JSONArray();
				if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
					nameValuesArr.put(getNameValueObj(DeviceConstants.YIELD_ON_ADVANCES, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.YIELD_ON_INVESTMENTS, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.COST_OF_LIABILITIES, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.NIM, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_SPREAD, "--"));
				} else {
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBIDTM, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_EBITM, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PRE_TAX_MARGIN, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PATM, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_CPM, "--"));
				}
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);
			} else if (sRatioType.equals(DeviceConstants.PERFORMANCE_RATIOS)) {

				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_PERFORMACE_RATIOS);
				obj.put(DeviceConstants.PERIOD, sPeriod);
				JSONArray nameValuesArr = new JSONArray();
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_ROA, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_ROE, "--"));
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

			} else if (sRatioType.equals(DeviceConstants.EFFICIENCY_RATIOS)) {

				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_EFFICIENCY_RATIOS);
				obj.put(DeviceConstants.PERIOD, sPeriod);
				JSONArray nameValuesArr = new JSONArray();
				if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
					nameValuesArr.put(getNameValueObj(DeviceConstants.COST_INCOME_RATIO, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.CORE_COST_INCOME_RATIO, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_COSTS_TO_ASSETS, "--"));
				} else {
					nameValuesArr.put(getNameValueObj(DeviceConstants.FIXED_CAPITAL_SALES, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_RECEIVABLE_DAYS, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_INVENTORY_DAYS, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PAYABLE_DAYS, "--"));
				}
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);
			} else if (sRatioType.equals(DeviceConstants.GROWTH_RATIOS)) {
				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_GROWTH_RARIOS);
				obj.put(DeviceConstants.PERIOD, sPeriod);
				JSONArray nameValuesArr = new JSONArray();
				if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
					nameValuesArr.put(getNameValueObj(DeviceConstants.CORE_OPERA_INCOME, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.NET_PROFIT_GROWTH, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.BVPS_GROWTH, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.ADVANCES_GROWTH, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_EPS, "--"));
				} else {
					nameValuesArr.put(getNameValueObj(DeviceConstants.NET_SALES_GROWTH, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EBITDA_GROWTH, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EBIT_GROWTH, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PAT_GROWTH, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_EPS, "--"));
				}

				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

			} else if (sRatioType.equals(DeviceConstants.LIQUIDITY_RATIOS)) {

				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_LIQUIDITY_RATIOS);
				obj.put(DeviceConstants.PERIOD, sPeriod);
				JSONArray nameValuesArr = new JSONArray();
				nameValuesArr.put(getNameValueObj(DeviceConstants.LOANS_DEPOSITS, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.CASH_DEPOSITS, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INVESTMENT_DEPOSITS, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INC_LOAN_DEPOSIT, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.CREDIT_DEPOSITS, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_EXPENDED_EARNED, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_INCOME_TOTAL_FUNDS, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_EXPENDED_TOTAL_FUNDS, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.CASA, "--"));
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

			} else if (sRatioType.equals(DeviceConstants.FINANCIAL_STABILITY_RATIOS)) {
				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_FINANCIAL_STABILITY_RATIOS);
				obj.put(DeviceConstants.PERIOD, sPeriod);
				JSONArray nameValuesArr = new JSONArray();
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL_DEBT_EQUITY, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_CURRENT_RATIO, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_QUICK_RATIO, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_INTEREST_COVER, "--"));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL_DEBT_MCAP, "--"));
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);
			} else if (sRatioType.equals(DeviceConstants.VALUATION_RATIOS)) {
				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_GROWTH_RARIOS);
				obj.put(DeviceConstants.PERIOD, sPeriod);
				JSONArray nameValuesArr = new JSONArray();
				if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PE, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PRICE_BOOK, "--"));
				} else {
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PE, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PRICE_BOOK, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DIVIDEND_YIELD, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EV_EBITDA, "--"));
					nameValuesArr.put(getNameValueObj(DeviceConstants.MCAP_SALES, "--"));
				}
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);
			}
		}
		return obj;
	}

	private static List<JSONObject> getValuationRatio(GetKeyRatios keyRatioObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedValuationRatioList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();
		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {

			try {
				ValuationRatioBankList valuationRatioList = new ValuationRatioBankList();
				try {
				if (redisPool.isExists(RedisConstants.VALUE_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					valuationRatioList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.VALUE_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType),
							ValuationRatioBankList.class);
				} else {
					valuationRatioList = keyRatioObj.invokeValuationRatioBank();
					redisPool.setValues(RedisConstants.VALUE_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(valuationRatioList));
				}
				}catch (Exception e) {
					log.error(e);
					valuationRatioList = keyRatioObj.invokeValuationRatioBank();
				}

				for (ValuationRatioBank valuationRatio : valuationRatioList) {
					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_VALUATION_RATIOS);

					String sYRC = valuationRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PE,
							PriceFormat.formatPrice(valuationRatio.getPE(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PRICE_BOOK,
							PriceFormat.formatPrice(valuationRatio.getPrice_BookValue(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedValuationRatioList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Valuation ratio" + " " + e.getMessage());
			}
		} else {

			try {
				ValuationRatioNonBankList valuationRatioList = new ValuationRatioNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.VALUE_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					valuationRatioList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.VALUE_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							ValuationRatioNonBankList.class);
				} else {
					valuationRatioList = keyRatioObj.invokeValuationRatioNonBank();
					redisPool.setValues(RedisConstants.VALUE_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(valuationRatioList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					valuationRatioList = keyRatioObj.invokeValuationRatioNonBank();
				}

				for (ValuationRatioNonBank valuationRatio : valuationRatioList) {
					JSONObject obj = new JSONObject();

					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_VALUATION_RATIOS);

					String sYRC = valuationRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PE,
							PriceFormat.formatPrice(valuationRatio.getPE(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PRICE_BOOK,
							PriceFormat.formatPrice(valuationRatio.getPrice_BookValue(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DIVIDEND_YIELD,
							PriceFormat.formatPrice(valuationRatio.getDividendYield(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EV_EBITDA,
							PriceFormat.formatPrice(valuationRatio.getEV_EBITDA(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.MCAP_SALES,
							PriceFormat.formatPrice(valuationRatio.getMcap_Sales(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedValuationRatioList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}
		return parsedValuationRatioList;
	}

	private static List<JSONObject> getFinancialStabilityRatio(GetKeyRatios keyRatioObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedFinancialStabilityRatioList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		RedisPool redisPool = new RedisPool();
		try {
			FinancialStabilityRatioNonBankList financialStabilityList = new FinancialStabilityRatioNonBankList();
			try {
			if (redisPool.isExists(RedisConstants.FINANCIAL_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
				financialStabilityList = new Gson().fromJson(
						redisPool.getValue(RedisConstants.FINANCIAL_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
						FinancialStabilityRatioNonBankList.class);
			} else {
				financialStabilityList = keyRatioObj.invokeFinancialStabilityRatioNonBank();
				redisPool.setValues(RedisConstants.FINANCIAL_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
						new Gson().toJson(financialStabilityList));
			}
			}catch (Exception e) {
				log.error(e);
				financialStabilityList = keyRatioObj.invokeFinancialStabilityRatioNonBank();
			}

			for (FinancialStabilityRatioNonBank financialRatio : financialStabilityList) {
				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_FINANCIAL_STABILITY_RATIOS);
				String sYRC = financialRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

				JSONArray nameValuesArr = new JSONArray();
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL_DEBT_EQUITY,
						PriceFormat.formatPrice(financialRatio.getTotalDebt_Equity(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_CURRENT_RATIO,
						PriceFormat.formatPrice(financialRatio.getCurrentRatio(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_QUICK_RATIO,
						PriceFormat.formatPrice(financialRatio.getQuickRatio(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_INTEREST_COVER,
						PriceFormat.formatPrice(financialRatio.getInterestCover(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL_DEBT_MCAP,
						PriceFormat.formatPrice(financialRatio.getTotalDebt_MCap(), precision, true)));
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

				parsedFinancialStabilityRatioList.add(obj);
			}
		} catch (CMOTSException e) {
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Financial Stability Ratio" + " " + e.getMessage());
			log.warn(e);
		}
		return parsedFinancialStabilityRatioList;
	}

	private static List<JSONObject> getLiquidityRatio(GetKeyRatios keyRatioObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedLiquidityRatioList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		RedisPool redisPool = new RedisPool();
		try {
			LiquidityRatioBankList liquidityRatioList = new LiquidityRatioBankList();
			try {
			if (redisPool.isExists(RedisConstants.LIQUIDITY_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType)) {
				liquidityRatioList = new Gson().fromJson(
						redisPool.getValue(RedisConstants.LIQUIDITY_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType),
						LiquidityRatioBankList.class);
			} else {
				liquidityRatioList = keyRatioObj.invokeLiquidityRatioBank();
				redisPool.setValues(RedisConstants.LIQUIDITY_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType,
						new Gson().toJson(liquidityRatioList));
			}
			}catch (Exception e) {
				log.error(e);
				liquidityRatioList = keyRatioObj.invokeLiquidityRatioBank();
			}

			for (LiquidityRatioBank liquidityRatio : liquidityRatioList) {

				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_LIQUIDITY_RATIOS);

				String sYRC = liquidityRatio.getYRC();
				int index = sYRC.indexOf(".");
				if (index != -1)
					sYRC = sYRC.substring(0, index);
				int year = Integer.parseInt(sYRC.substring(0, 4));
				int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
				obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

				JSONArray nameValuesArr = new JSONArray();
				nameValuesArr.put(getNameValueObj(DeviceConstants.LOANS_DEPOSITS,
						PriceFormat.formatPrice(liquidityRatio.getLoans_to_Deposits(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.CASH_DEPOSITS,
						PriceFormat.formatPrice(liquidityRatio.getCash_to_Deposits(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INVESTMENT_DEPOSITS,
						PriceFormat.formatPrice(liquidityRatio.getInvestment_toDeposits(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INC_LOAN_DEPOSIT,
						PriceFormat.formatPrice(liquidityRatio.getIncLoan_to_Deposit(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.CREDIT_DEPOSITS,
						PriceFormat.formatPrice(liquidityRatio.getCredit_to_Deposits(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_EXPENDED_EARNED, PriceFormat
						.formatPrice(liquidityRatio.getInterestExpended_to_Interestearned(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_INCOME_TOTAL_FUNDS,
						PriceFormat.formatPrice(liquidityRatio.getInterestincome_to_Totalfunds(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_EXPENDED_TOTAL_FUNDS,
						PriceFormat.formatPrice(liquidityRatio.getInterestExpended_to_Totalfunds(), precision, true)));
				nameValuesArr.put(getNameValueObj(DeviceConstants.CASA,
						PriceFormat.formatPrice(liquidityRatio.getCASA(), precision, true)));
				obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

				parsedLiquidityRatioList.add(obj);

			}

		} catch (CMOTSException e) {
			log.warn(e);
			Monitor.markFailure(Market.CMOTS_API_BEAN,
					"Error while invoking CMOTS data for Liquidity Ratio" + " " + e.getMessage());
		}

		return parsedLiquidityRatioList;
	}

	private static List<JSONObject> getGrowthRatio(GetKeyRatios keyRatioObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedGrowthRatioList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();
		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {

			try {
				GrowthRatioBankList growthRatioList = new GrowthRatioBankList();
				try {
				if (redisPool.isExists(RedisConstants.GROWTH_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					growthRatioList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.GROWTH_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType),
							GrowthRatioBankList.class);
				} else {
					growthRatioList = keyRatioObj.invokeGrowthRatioBank();
					redisPool.setValues(RedisConstants.GROWTH_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(growthRatioList));
				}
				}catch (Exception e) {
					log.error(e);
					growthRatioList = keyRatioObj.invokeGrowthRatioBank();
				}
				for (GrowthRatioBank growthRatio : growthRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_GROWTH_RARIOS);

					String sYRC = growthRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.CORE_OPERA_INCOME,
							PriceFormat.formatPrice(growthRatio.getCoreOperatingIncomeGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.NET_PROFIT_GROWTH,
							PriceFormat.formatPrice(growthRatio.getNetProfitGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.BVPS_GROWTH,
							PriceFormat.formatPrice(growthRatio.getBVPSGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.ADVANCES_GROWTH,
							PriceFormat.formatPrice(growthRatio.getAdvancesGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_EPS,
							PriceFormat.formatPrice(growthRatio.getEPS(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedGrowthRatioList.add(obj);

				}

			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Growth Ratio" + " " + e.getMessage());
			}

		} else {

			try {
				GrowthRatioNonBankList growthRatioList = new GrowthRatioNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.GROWTH_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					growthRatioList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.GROWTH_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							GrowthRatioNonBankList.class);
				} else {
					growthRatioList = keyRatioObj.invokeGrowthRatioNonBank();
					redisPool.setValues(RedisConstants.GROWTH_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(growthRatioList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					growthRatioList = keyRatioObj.invokeGrowthRatioNonBank();
				}

				for (GrowthRatioNonBank growthRatio : growthRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_GROWTH_RARIOS);

					String sYRC = growthRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.NET_SALES_GROWTH,
							PriceFormat.formatPrice(growthRatio.getNetSalesGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EBITDA_GROWTH,
							PriceFormat.formatPrice(growthRatio.getEBITDAGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EBIT_GROWTH,
							PriceFormat.formatPrice(growthRatio.getEBITGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PAT_GROWTH,
							PriceFormat.formatPrice(growthRatio.getPATGrowth(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_EPS,
							PriceFormat.formatPrice(growthRatio.getEPS(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedGrowthRatioList.add(obj);

				}

			} catch (CMOTSException e) {
				log.warn(e);
			}

		}

		return parsedGrowthRatioList;
	}

	private static List<JSONObject> getEfficiencyRatio(GetKeyRatios keyRatioObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedEfficiencyRatioList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();

		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {

			try {
				EfficiencyRatioBankList efficiencyRatioList = new EfficiencyRatioBankList();
				try {
				if (redisPool.isExists(RedisConstants.EFFICIENCY_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType)) {

					efficiencyRatioList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.EFFICIENCY_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType),
							EfficiencyRatioBankList.class);
				} else {
					efficiencyRatioList = keyRatioObj.invokeEfficiencyRatioBank();
					redisPool.setValues(RedisConstants.EFFICIENCY_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(efficiencyRatioList));
				}
				}catch (Exception e) {
					efficiencyRatioList = keyRatioObj.invokeEfficiencyRatioBank();
				}
				for (EfficiencyRatioBank efficiencyRatio : efficiencyRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_EFFICIENCY_RATIOS);

					String sYRC = efficiencyRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.COST_INCOME_RATIO,
							PriceFormat.formatPrice(efficiencyRatio.getCostIncomeRatio(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.CORE_COST_INCOME_RATIO,
							PriceFormat.formatPrice(efficiencyRatio.getCoreCostIncomeRatio(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_COSTS_TO_ASSETS,
							PriceFormat.formatPrice(efficiencyRatio.getOperatingCoststoAssets(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedEfficiencyRatioList.add(obj);
				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Efficiency Ratio" + " " + e.getMessage());
			}

		} else {

			try {
				EfficiencyRatioNonBankList efficiencyRatioList = new EfficiencyRatioNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.EFFICIENCY_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					efficiencyRatioList = new Gson().fromJson(
							redisPool.getValue(
									RedisConstants.EFFICIENCY_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							EfficiencyRatioNonBankList.class);
				} else {
					efficiencyRatioList = keyRatioObj.invokeEfficiencyRatioNonBank();
					redisPool.setValues(RedisConstants.EFFICIENCY_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(efficiencyRatioList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					efficiencyRatioList = keyRatioObj.invokeEfficiencyRatioNonBank();
				}

				for (EfficiencyRatioNonBank efficiencyRatio : efficiencyRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_EFFICIENCY_RATIOS);

					String sYRC = efficiencyRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.FIXED_CAPITAL_SALES,
							PriceFormat.formatPrice(efficiencyRatio.getFixedCapitals_Sales(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_RECEIVABLE_DAYS,
							Integer.toString((int) Double.parseDouble(efficiencyRatio.getReceivableDays()))));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_INVENTORY_DAYS,
							Integer.toString((int) Double.parseDouble(efficiencyRatio.getInventoryDays()))));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PAYABLE_DAYS,
							Integer.toString((int) Double.parseDouble(efficiencyRatio.getPayableDays()))));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedEfficiencyRatioList.add(obj);
				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}
		return parsedEfficiencyRatioList;
	}

	private static List<JSONObject> getPerformanceRatio(GetKeyRatios keyRatioObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedPerformanceRatioList = new ArrayList<JSONObject>();
		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();
		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {

			try {
				PerformanceRatioBankList performanceRatioList = new PerformanceRatioBankList();
				try {
				if (redisPool.isExists(RedisConstants.PERFORMANCE_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					performanceRatioList = new Gson().fromJson(
							redisPool
									.getValue(RedisConstants.PERFORMANCE_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType),
							PerformanceRatioBankList.class);
				} else {
					performanceRatioList = keyRatioObj.invokePerformanceRatioBank();
					redisPool.setValues(RedisConstants.PERFORMANCE_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(performanceRatioList));
				}
				}catch (Exception e) {
					log.error(e);
					performanceRatioList = keyRatioObj.invokePerformanceRatioBank();
				}

				for (PerformanceRatioBank performanceRatio : performanceRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_PERFORMACE_RATIOS);

					String sYRC = performanceRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_ROA,
							PriceFormat.formatPrice(performanceRatio.getROA(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_ROE,
							PriceFormat.formatPrice(performanceRatio.getROE(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedPerformanceRatioList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Performance Ratio" + " " + e.getMessage());
			}

		} else {

			try {
				PerformanceRatioNonBankList performanceRatioList = new PerformanceRatioNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.PERFORMANCE_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					performanceRatioList = new Gson().fromJson(
							redisPool.getValue(
									RedisConstants.PERFORMANCE_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							PerformanceRatioNonBankList.class);
				} else {
					performanceRatioList = keyRatioObj.invokePerformanceRatioNonBank();
					redisPool.setValues(RedisConstants.PERFORMANCE_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(performanceRatioList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					performanceRatioList = keyRatioObj.invokePerformanceRatioNonBank();
				}

				for (PerformanceRatioNonBank performanceRatio : performanceRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_PERFORMACE_RATIOS);

					String sYRC = performanceRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_ROA,
							PriceFormat.formatPrice(performanceRatio.getROA(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_ROE,
							PriceFormat.formatPrice(performanceRatio.getROE(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedPerformanceRatioList.add(obj);
				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}

		return parsedPerformanceRatioList;
	}

	private static List<JSONObject> getMarginRatio(GetKeyRatios keyRatioObj, SymbolRow symRow, String sType,
			String sCoCode) {

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();

		List<JSONObject> parsedMarginRatioList = new ArrayList<JSONObject>();

		RedisPool redisPool = new RedisPool();
		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
			try {
				MarginRatioBankList marginRatioList = new MarginRatioBankList();
				try {
				if (redisPool.isExists(RedisConstants.MARGIN_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					marginRatioList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.MARGIN_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType),
							MarginRatioBankList.class);
				} else {
					marginRatioList = keyRatioObj.invokeMarginRatioBank();
					redisPool.setValues(RedisConstants.MARGIN_RATIO_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(marginRatioList));
				}
				}catch (Exception e) {
					log.error(e);
					marginRatioList = keyRatioObj.invokeMarginRatioBank();
				}

				for (MarginRatioBank marginRatio : marginRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_MARGIN_RATIOS);

					String sYRC = marginRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.YIELD_ON_ADVANCES,
							PriceFormat.formatPrice(marginRatio.getYieldonAdvances(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.YIELD_ON_INVESTMENTS,
							PriceFormat.formatPrice(marginRatio.getYieldonInvestments(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.COST_OF_LIABILITIES,
							PriceFormat.formatPrice(marginRatio.getCostofLiabilities(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.NIM,
							PriceFormat.formatPrice(marginRatio.getNIM(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_SPREAD,
							PriceFormat.formatPrice(marginRatio.getInterestSpread(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedMarginRatioList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Margin ratio" + " " + e.getMessage());
			}
		} else {
			try {
				MarginRatioNonBankList marginRatioList = new MarginRatioNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.MARGIN_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					marginRatioList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.MARGIN_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							MarginRatioNonBankList.class);
				} else {
					marginRatioList = keyRatioObj.invokeMarginRatioNonBank();
					redisPool.setValues(RedisConstants.MARGIN_RATIO_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(marginRatioList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					marginRatioList = keyRatioObj.invokeMarginRatioNonBank();
				}

				for (MarginRatioNonBank marginRatio : marginRatioList) {

					JSONObject obj = new JSONObject();
					obj.put(DeviceConstants.TYPE, DeviceConstants.DISP_MARGIN_RATIOS);

					String sYRC = marginRatio.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBIDTM,
							PriceFormat.formatPrice(marginRatio.getPBIDTIM(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_EBITM,
							PriceFormat.formatPrice(marginRatio.getEBITM(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PRE_TAX_MARGIN,
							PriceFormat.formatPrice(marginRatio.getPreTaxMargin(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PATM,
							PriceFormat.formatPrice(marginRatio.getPATM(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_CPM,
							PriceFormat.formatPrice(marginRatio.getCPM(), precision, true)));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedMarginRatioList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}
		return parsedMarginRatioList;
	}

	private static JSONObject getNameValueObj(String sName, String sValue) {

		JSONObject obj = new JSONObject();
		obj.put(DeviceConstants.NAME, sName);
		obj.put(DeviceConstants.VAL, sValue);
		return obj;
	}

	public static JSONArray profitLoss_101(JSONObject symObj, String sType, String sAppID) {
		JSONArray finalArr = new JSONArray();

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);

		String sCoCode = symRow.getCMCoCode();

		GetProfitAndLoss profitLossObj = new GetProfitAndLoss(getAppIDForLogging(sAppID));
		profitLossObj.setCoCode(sCoCode);
		profitLossObj.setFinFormat(sType);

		List<JSONObject> parsedProfitLossList = getProfitLoss(profitLossObj, symRow, sType, sCoCode);

		sortJSONArrayDate(parsedProfitLossList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
				false);

		for (int i = 0; i < parsedProfitLossList.size(); i++) {
			JSONObject obj = new JSONObject();

			obj.put(DeviceConstants.PERIOD, parsedProfitLossList.get(i).getString(DeviceConstants.PERIOD));
			parsedProfitLossList.get(i).remove(DeviceConstants.PERIOD);
			obj.put(DeviceConstants.PL_DATA, parsedProfitLossList.get(i));
			finalArr.put(obj);

		}

		return finalArr;
	}

	private static List<JSONObject> getProfitLoss(GetProfitAndLoss profitLossObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedProfitLossList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();

		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
			try {
				ProfitAndLossBankList profitLossList = new ProfitAndLossBankList();
				try {
				if (redisPool.isExists(RedisConstants.PROFIT_AND_LOSS + "_" + sCoCode + "_" + sType)) {
					profitLossList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.PROFIT_AND_LOSS + "_" + sCoCode + "_" + sType),
							ProfitAndLossBankList.class);
				} else {
					profitLossList = profitLossObj.invokeBank();
					redisPool.setValues(RedisConstants.PROFIT_AND_LOSS + "_" + sCoCode + "_" + sType,
							new Gson().toJson(profitLossList));
				}
				}catch (Exception e) {
					log.error(e);
					profitLossList = profitLossObj.invokeBank();
				}

				for (ProfitAndLossBank profitLoss : profitLossList) {

					JSONObject obj = new JSONObject();

					String sYRC = profitLoss.getyrc();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.INTEREST_EARNED,
							PriceFormat.formatPrice(profitLoss.getInterestEarned_Income(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(profitLoss.getOtherIncome_Income(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_TOTAL_INCOME,
							PriceFormat.formatPrice(profitLoss.getTotal_Income(), precision, true), true));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_EXPENDITURE,
							PriceFormat.formatPrice(profitLoss.getExpenditure(), precision, true), true));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.INTEREST_EXPENDED,
							PriceFormat.formatPrice(profitLoss.getInterestexpended_Expenditure(), precision, true),
							false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.PAYMENTS_TO_PROVISIONS_FOR_EMPLOYEES,
							PriceFormat.formatPrice(profitLoss.getPaymentsto_ProvisionsforEmployees_Expenditure(),
									precision, true),
							false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.OPERATING_EXPENSES_ADMINISTRATIVE_EXPENSES,
							PriceFormat.formatPrice(
									profitLoss.getOperatingExpenses_AdministrativeExpenses_Expenditure(), precision,
									true),
							false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(profitLoss.getDepreciation_Expenditure(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.OTHER_EXPENSES_PROVISIONS_CONTIGENCIES,
							PriceFormat.formatPrice(profitLoss.getOtherExpenses_Provisions_Contingencies_Expenditure(),
									precision, true),
							false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.PROVISION_FOR_TAX,
							PriceFormat.formatPrice(profitLoss.getProvisionforTax_Expenditure(), precision, true),
							false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.FRINGE_BENEFIT_TAX,
							PriceFormat.formatPrice(profitLoss.getFringeBenefittax_Expenditure(), precision, true),
							false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DEFERRED_TAX,
							PriceFormat.formatPrice(profitLoss.getDeferredTax_Expenditure(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_NET_PROFIT,
							PriceFormat.formatPrice(profitLoss.getNetProfit_Expenditure(), precision, true), false));

					if (sType.equalsIgnoreCase(DeviceConstants.CONSOLIDATED)) {
						nameValuesArr.put(getNameValueObjForPL(DeviceConstants.MINORITY_INTEREST_AFTER_TAX, PriceFormat
								.formatPrice(profitLoss.getMinorityInterest_aftertax_Expenditure(), precision, true),
								false));
						nameValuesArr.put(getNameValueObjForPL(DeviceConstants.PROFIT_LOSS_OF_ASSOCIATE_COMPANY,
								PriceFormat.formatPrice(profitLoss.getProfit_LossofAssociateCompany_Expenditure(),
										precision, true),
								false));
						nameValuesArr.put(getNameValueObjForPL(DeviceConstants.NET_PROFIT_AFTER_MINORITY_INTEREST,
								PriceFormat.formatPrice(
										profitLoss.getNetProfitafterMinorityInterest_PLAssoCo_Expenditure(), precision,
										true),
								false));
						nameValuesArr.put(getNameValueObjForPL(DeviceConstants.EPS_AFTER_MINORITY_INTEREST, PriceFormat
								.formatPrice(profitLoss.getEPSafterMinorityInterest_Expenditure(), precision, true),
								false));
					}
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedProfitLossList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Profit Loss" + " " + e.getMessage());
			}
		} else {
			try {
				ProfitAndLossNonBankList profitLossList = new ProfitAndLossNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.PROFIT_AND_LOSS_NON_BANK + "_" + sCoCode + "_" + sType)) {
					profitLossList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.PROFIT_AND_LOSS_NON_BANK + "_" + sCoCode + "_" + sType),
							ProfitAndLossNonBankList.class);
				} else {
					profitLossList = profitLossObj.invokeNonBank();
					redisPool.setValues(RedisConstants.PROFIT_AND_LOSS_NON_BANK + "_" + sCoCode + "_" + sType,
							new Gson().toJson(profitLossList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					profitLossList = profitLossObj.invokeNonBank();
				}

				for (ProfitAndLossNonBank profitLoss : profitLossList) {

					JSONObject obj = new JSONObject();

					String sYRC = profitLoss.getyrc();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_SALES,
							PriceFormat.formatPrice(profitLoss.getNetSales(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(profitLoss.getOtherIncome(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_TOTAL_INCOME,
							PriceFormat.formatPrice(profitLoss.getTotal(), precision, true), true));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_EXPENDITURE,
							PriceFormat.formatPrice(profitLoss.getExpenditure(), precision, true), true));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_INTEREST,
							PriceFormat.formatPrice(profitLoss.getInterest(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_PBDT,
							PriceFormat.formatPrice(profitLoss.getPBDT(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(profitLoss.getDepreciation(), precision, true), false));
					nameValuesArr
							.put(getNameValueObjForPL(DeviceConstants.PROFIT_BEFORE_TAXATION, PriceFormat.formatPrice(
									profitLoss.getProfitbefore_Taxation_ExceptionalItems(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.EXCEPTIONAL_INCOME_EXPENSE,
							PriceFormat.formatPrice(profitLoss.getExceptional_Income_Expense(), precision, true),
							false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_PROFIT_BEFORE_TAX,
							PriceFormat.formatPrice(profitLoss.getProfitbeforeTax(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_PROVISION_FOR_TAX,
							PriceFormat.formatPrice(profitLoss.getProvisionforTax(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_EXTRA_ITEMS,
							PriceFormat.formatPrice(profitLoss.getExtraItems(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.ADJUSTMENTS_TO_PAT,
							PriceFormat.formatPrice(profitLoss.getAdjustmentstoPAT(), precision, true), false));
					nameValuesArr.put(getNameValueObjForPL(DeviceConstants.DISP_PROFIT_BALANCE,
							PriceFormat.formatPrice(profitLoss.getProfitBalance(), precision, true), false));
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedProfitLossList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}

		return parsedProfitLossList;
	}

	private static JSONObject getNameValueObjForPL(String sName, String sValue, boolean isHighlight) {
		JSONObject obj = new JSONObject();
		obj.put(DeviceConstants.NAME, sName);
		obj.put(DeviceConstants.VAL, sValue);
		obj.put(DeviceConstants.HIGHLIGHT, Boolean.toString(isHighlight));
		return obj;
	}

	public static JSONArray getBalanceSheet_101(JSONObject symObj, String sType, String sAppID) {

		JSONArray finalArr = new JSONArray();

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);

		String sCoCode = symRow.getCMCoCode();

		GetBalanceSheet balanceSheetObj = new GetBalanceSheet(getAppIDForLogging(sAppID));
		balanceSheetObj.setCoCode(sCoCode);
		balanceSheetObj.setFinFormat(sType);

		List<JSONObject> parsedBalanceSheetList = getBalanceSheetAPI(balanceSheetObj, symRow, sType, sCoCode);

		sortJSONArrayDate(parsedBalanceSheetList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT,
				false);

		for (int i = 0; i < parsedBalanceSheetList.size(); i++) {
			JSONObject obj = parsedBalanceSheetList.get(i);
			finalArr.put(obj);

		}

		return finalArr;
	}

	private static List<JSONObject> getBalanceSheetAPI(GetBalanceSheet balanceSheetObj, SymbolRow symRow, String sType,
			String sCoCode) {

		List<JSONObject> parsedBalanceSheetList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();

		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
			try {
				BalanceSheetBankList balanceSheetList = new BalanceSheetBankList();
				try {
					if (redisPool.isExists(RedisConstants.BALANCE_SHEET_YEARLY + "_" + sCoCode + "_" + sType)) {
						balanceSheetList = new Gson().fromJson(
								redisPool.getValue(RedisConstants.BALANCE_SHEET_YEARLY + "_" + sCoCode + "_" + sType),
								BalanceSheetBankList.class);
					} else {
						balanceSheetList = balanceSheetObj.invokeYearlyBalanceSheetBank();
						redisPool.setValues(RedisConstants.BALANCE_SHEET_YEARLY + "_" + sCoCode + "_" + sType,
								new Gson().toJson(balanceSheetList));
					}
				} catch (Exception e) {
					log.error(e);
					balanceSheetList = balanceSheetObj.invokeYearlyBalanceSheetBank();
				}

				for (BalanceSheetBank balanceSheet : balanceSheetList) {

					JSONObject finalObj = new JSONObject();
					JSONArray finalArr = new JSONArray();

					String sYRC = balanceSheet.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					String sPeriod = DateUtils.getEndofMonthDate(month, year);

					finalObj.put(DeviceConstants.PERIOD, sPeriod);

					JSONObject sourceObj = new JSONObject();
					sourceObj.put(DeviceConstants.TYPE, DeviceConstants.SOURCE_OF_FUNDS);

					JSONArray nameValuesSourceArr = new JSONArray();
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.SHARE_CAPITAL,
							PriceFormat.formatPrice(balanceSheet.getShareCapital(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.SHARE_WARRANTS_OUTSTANDINGS,
							PriceFormat.formatPrice(balanceSheet.getShareWarrants_Outstandings(), precision, true),
							false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_RESERVES,
							PriceFormat.formatPrice(balanceSheet.getTotalReserves(), precision, true), false));

					if (sType.equalsIgnoreCase(DeviceConstants.CONSOLIDATED))
						nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.MINORITY_INTEREST,
								PriceFormat.formatPrice(balanceSheet.getMinorityInterest(), precision, true), false));

					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.DISP_DEPOSITS,
							PriceFormat.formatPrice(balanceSheet.getDeposits(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.BORROWINGS,
							PriceFormat.formatPrice(balanceSheet.getBorrowings(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.OTHER_LIABILITIES_PROVISIONS,
							PriceFormat.formatPrice(balanceSheet.getOtherLiabilities_Provisions(), precision, true),
							false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_LIABILITIES,
							PriceFormat.formatPrice(balanceSheet.getTotalLiabilities(), precision, true), true));

					sourceObj.put(DeviceConstants.NAME_VALUES, nameValuesSourceArr);

					finalArr.put(sourceObj);

					JSONObject applicationObj = new JSONObject();
					applicationObj.put(DeviceConstants.TYPE, DeviceConstants.APPLICATION_OF_FUNDS);

					JSONArray nameValuesApplicationArr = new JSONArray();
					nameValuesApplicationArr
							.put(getNameValueObjForPL(DeviceConstants.CASH_BALANCE_RESERVE_BANK_OF_INDIA,
									PriceFormat.formatPrice(balanceSheet.getCashandbalancewithReserveBankofIndia(),
											precision, true),
									false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.BALANCES_BANKS_MONEY_AT_CALL,
							PriceFormat.formatPrice(balanceSheet.getBalanceswithbanksandmoneyatcall(), precision, true),
							false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.INVESTMENTS,
							PriceFormat.formatPrice(balanceSheet.getInvestments(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.ADVANCES,
							PriceFormat.formatPrice(balanceSheet.getAdvances(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.GROSS_BLOCK,
							PriceFormat.formatPrice(balanceSheet.getGrossblock(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.LESS_ACCUMULATED_DEPRECIATION,
							PriceFormat.formatPrice(balanceSheet.getLess_AccumulatedDepreciation(), precision, true),
							false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.LESS_IMPAIRMENT_OF_ASSETS,
							PriceFormat.formatPrice(balanceSheet.getLess_ImpairmentofAssets(), precision, true),
							false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.NET_BLOCK,
							PriceFormat.formatPrice(balanceSheet.getNetBlock(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.LEASE_ADJUSTMENT,
							PriceFormat.formatPrice(balanceSheet.getLeaseAdjustment(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.CAPITAL_WORK_IN_PROGRESS,
							PriceFormat.formatPrice(balanceSheet.getCapitalWorkinProgress(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.OTHER_ASSETS,
							PriceFormat.formatPrice(balanceSheet.getOtherAssets(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_ASSETS,
							PriceFormat.formatPrice(balanceSheet.getTotalAssets(), precision, true), true));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.DISP_CONTINGENT_LIABILITIES,
							PriceFormat.formatPrice(balanceSheet.getContingentLiabilities(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.BILLS_FOR_COLLECTION,
							PriceFormat.formatPrice(balanceSheet.getBillsforcollection(), precision, true), false));

					if (sType.equalsIgnoreCase(DeviceConstants.CONSOLIDATED)) {
						nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.BOOK_VALUE,
								PriceFormat.formatPrice(balanceSheet.getBookValue(), precision, true), false));
						nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.ADJUSTED_BOOK_VALUE,
								PriceFormat.formatPrice(balanceSheet.getAdjustedBookValue(), precision, true), false));
					}

					applicationObj.put(DeviceConstants.NAME_VALUES, nameValuesApplicationArr);

					finalArr.put(applicationObj);

					finalObj.put(DeviceConstants.BALANCE_DATA, finalArr);

					parsedBalanceSheetList.add(finalObj);

				}
			} catch (CMOTSException e) {
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Balance Sheet" + " " + e.getMessage());
				log.warn(e);
			}
		} else {
			try {
				BalanceSheetNonBankList balanceSheetList = new BalanceSheetNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.BALANCE_SHEET_YEARLY_NON_BANK + "_" + sCoCode + "_" + sType)) {
					balanceSheetList = new Gson().fromJson(
							redisPool.getValue(
									RedisConstants.BALANCE_SHEET_YEARLY_NON_BANK + "_" + sCoCode + "_" + sType),
							BalanceSheetNonBankList.class);
				} else {
					balanceSheetList = balanceSheetObj.invokeYearlyBalanceSheetNonBank();
					redisPool.setValues(RedisConstants.BALANCE_SHEET_YEARLY_NON_BANK + "_" + sCoCode + "_" + sType,
							new Gson().toJson(balanceSheetList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					balanceSheetList = balanceSheetObj.invokeYearlyBalanceSheetNonBank();
				}

				for (BalanceSheetNonBank balanceSheet : balanceSheetList) {

					JSONObject finalObj = new JSONObject();
					JSONArray finalArr = new JSONArray();

					String sYRC = balanceSheet.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					String sPeriod = DateUtils.getEndofMonthDate(month, year);

					finalObj.put(DeviceConstants.PERIOD, sPeriod);

					JSONObject sourceObj = new JSONObject();
					sourceObj.put(DeviceConstants.TYPE, DeviceConstants.SOURCE_OF_FUNDS);

					JSONArray nameValuesSourceArr = new JSONArray();
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.SHARE_CAPITAL,
							PriceFormat.formatPrice(balanceSheet.getShareCapital(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.RESERVES_SURPLUS,
							PriceFormat.formatPrice(balanceSheet.getReserves_Surplus(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.EQUITY_SHARE_WARRANTS,
							PriceFormat.formatPrice(balanceSheet.getEquityShareWarrants(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.EQUITY_APPLICATION_MONEY,
							PriceFormat.formatPrice(balanceSheet.getEquityApplicationMoney(), precision, true), false));

					if (sType.equalsIgnoreCase(DeviceConstants.CONSOLIDATED))
						nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.MINORITY_INTEREST,
								PriceFormat.formatPrice(balanceSheet.getMinorityInterest(), precision, true), false));

					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.ESOP_OUTSTANDING, "--", false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_DEBT,
							PriceFormat.formatPrice(balanceSheet.getTotalDebt(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.OTHER_LIABLITIES,
							PriceFormat.formatPrice(balanceSheet.getOtherLiabilities(), precision, true), false));
					nameValuesSourceArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_LIABILITIES,
							PriceFormat.formatPrice(balanceSheet.getTotalLiabilities(), precision, true), true));

					sourceObj.put(DeviceConstants.NAME_VALUES, nameValuesSourceArr);

					finalArr.put(sourceObj);

					JSONObject applicationObj = new JSONObject();
					applicationObj.put(DeviceConstants.TYPE, DeviceConstants.APPLICATION_OF_FUNDS);

					JSONArray nameValuesApplicationArr = new JSONArray();
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.FIXED_ASSETS,
							PriceFormat.formatPrice(balanceSheet.getFixedAssets(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.INVESTMENTS,
							PriceFormat.formatPrice(balanceSheet.getInvestments(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.NET_CURRENT_ASSETS,
							PriceFormat.formatPrice(balanceSheet.getNetCurrentAssets(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.NET_DEFERRED_TAX,
							PriceFormat.formatPrice(balanceSheet.getNetDeferredTax(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.OTHER_ASSETS,
							PriceFormat.formatPrice(balanceSheet.getOtherAssets(), precision, true), false));
					nameValuesApplicationArr.put(getNameValueObjForPL(DeviceConstants.TOTAL_ASSETS,
							PriceFormat.formatPrice(balanceSheet.getTotalAssets(), precision, true), true));

					applicationObj.put(DeviceConstants.NAME_VALUES, nameValuesApplicationArr);

					finalArr.put(applicationObj);
					finalObj.put(DeviceConstants.BALANCE_DATA, finalArr);

					parsedBalanceSheetList.add(finalObj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}

		return parsedBalanceSheetList;

	}

	public static JSONArray getResults_101(JSONObject symObj, String sPeriodTxt, String sType, String sAppID)
			throws GCException {

		JSONArray finalArr = new JSONArray();

		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);

		String sCoCode = symRow.getCMCoCode();
		int indexCoCode = sCoCode.indexOf(".");
		if (indexCoCode != -1)
			sCoCode = sCoCode.substring(0, indexCoCode);

		GetResults resultsObj = new GetResults(getAppIDForLogging(sAppID));
		resultsObj.setCoCode(sCoCode);
		resultsObj.setFinFormat(sType);

		List<JSONObject> parsedResultsList = new ArrayList<JSONObject>();

		if (sPeriodTxt.equals(DeviceConstants.PERIOD_QUARTERLY)) {
			parsedResultsList = getResultsQuarterly(resultsObj, symRow, sType, sCoCode);
		} else if (sPeriodTxt.equals(DeviceConstants.PERIOD_HALF_YEARLY)) {
			parsedResultsList = getResultsHalfYearly(resultsObj, symRow, sType, sCoCode);
		} else if (sPeriodTxt.equals(DeviceConstants.PERIOD_YEARLY)) {
			parsedResultsList = getResultsYearly(resultsObj, symRow, sType, sCoCode);
		} else
			throw new GCException("Invalid filter type");

		sortJSONArrayDate(parsedResultsList, DeviceConstants.PERIOD, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);

		for (int i = 0; i < parsedResultsList.size(); i++) {
			JSONObject obj = new JSONObject();

			obj.put(DeviceConstants.PERIOD, parsedResultsList.get(i).getString(DeviceConstants.PERIOD));
			parsedResultsList.get(i).remove(DeviceConstants.PERIOD);
			obj.put(DeviceConstants.RESULT_DATA, parsedResultsList.get(i));
			finalArr.put(obj);

		}

		return finalArr;

	}

	private static List<JSONObject> getResultsYearly(GetResults resultsObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedResultsList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();

		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
			try {
				ResultsYearlyBankList resultsList = new ResultsYearlyBankList();
				try {
				if (redisPool.isExists(RedisConstants.RESULTS_YEARLY_LIST + "_" + sCoCode + "_" + sType)) {
					resultsList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.RESULTS_YEARLY_LIST + "_" + sCoCode + "_" + sType),
							ResultsYearlyBankList.class);
				} else {
					resultsList = resultsObj.invokeYearlyBank();
					redisPool.setValues(RedisConstants.RESULTS_YEARLY_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(resultsList));
				}
				}catch (Exception e) {
					log.error(e);
					resultsList = resultsObj.invokeYearlyBank();
				}

				for (ResultsYearlyBank results : resultsList) {

					JSONObject obj = new JSONObject();

					String sYRC = results.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_EARNED,
							PriceFormat.formatPrice(results.getInterestEarned(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(results.getOtherIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL_INCOME,
							PriceFormat.formatPrice(results.getTotalIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_EXPENSES,
							PriceFormat.formatPrice(results.getOperatingExpenses(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_PROFIT_BEFORE_PROV_CONT,
							PriceFormat.formatPrice(results.getOperatingProfitBeforeProvCont(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PROVISIONS_CONTIGENCIES,
							PriceFormat.formatPrice(results.getProvisionsContingencies(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(results.getDepreciation(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBT,
							PriceFormat.formatPrice(results.getPBT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PROVISION_FOR_TAXES,
							PriceFormat.formatPrice(results.getProvisionforTaxes(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.FRINGE_BENEFIT_TAX,
							PriceFormat.formatPrice(results.getFringeBenefitTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DEFERRED_TAX,
							PriceFormat.formatPrice(results.getDeferredTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_NET_PROFIT,
							PriceFormat.formatPrice(results.getNetProfit(), precision, true)));

					if (sType.equalsIgnoreCase(DeviceConstants.CONSOLIDATED)) {
						nameValuesArr.put(getNameValueObj(DeviceConstants.MINORITY_INTEREST_AFTER_NP,
								PriceFormat.formatPrice(results.getMinorityInterestafterNP(), precision, true)));
						nameValuesArr.put(getNameValueObj(DeviceConstants.PROFIT_LOSS_OF_ASSOCIATE_COMPANY,
								PriceFormat.formatPrice(results.getProfit_LossofAssociateCompany(), precision, true)));
						nameValuesArr.put(getNameValueObj(DeviceConstants.NET_PROFIT_AFTER_MINORITY_INTEREST,
								PriceFormat.formatPrice(results.getNetProfitafterMinorityInterest_PLAssoCo(), precision,
										true)));
					}
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedResultsList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Results yearly" + " " + e.getMessage());
			}
		} else {
			try {
				ResultsYearlyNonBankList resultsList = new ResultsYearlyNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.RESULTS_YEARLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					resultsList = new Gson().fromJson(
							redisPool.getValue(
									RedisConstants.RESULTS_YEARLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							ResultsYearlyNonBankList.class);
				} else {
					resultsList = resultsObj.invokeYearlyNonBank();
					redisPool.setValues(RedisConstants.RESULTS_YEARLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(resultsList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					resultsList = resultsObj.invokeYearlyNonBank();
				}

				for (ResultsYearlyNonBank results : resultsList) {

					JSONObject obj = new JSONObject();

					String sYRC = results.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_SALES,
							PriceFormat.formatPrice(results.getNetSales_OtherOperatingIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL,
							PriceFormat.formatPrice(results.getTotal(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.TOTAL_EXPENDITURE,
							PriceFormat.formatPrice(results.getTotalExpenditure(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBIDT,
							PriceFormat.formatPrice(results.getPBIDT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(results.getOtherIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OPERATING_PROFIT,
							PriceFormat.formatPrice(results.getOperatingProfit(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_INTEREST,
							PriceFormat.formatPrice(results.getInterest(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EXCEPTIONAL_ITEMS,
							PriceFormat.formatPrice(results.getExceptionalItem(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBDT,
							PriceFormat.formatPrice(results.getPBDT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(results.getDepreciation(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBT,
							PriceFormat.formatPrice(results.getPBT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TAX,
							PriceFormat.formatPrice(results.getTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PROFIT_AFTER_TAX,
							PriceFormat.formatPrice(results.getProfitAfterTax(), precision, true)));

					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedResultsList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}

		return parsedResultsList;
	}

	private static List<JSONObject> getResultsHalfYearly(GetResults resultsObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedResultsList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();
		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
			try {
				ResultsHalfYearlyBankList resultsList = new ResultsHalfYearlyBankList();
				try {
				if (redisPool.isExists(RedisConstants.RESULTS_HALF_YEARLY_LIST + "_" + sCoCode + "_" + sType)) {
					resultsList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.RESULTS_HALF_YEARLY_LIST + "_" + sCoCode + "_" + sType),
							ResultsHalfYearlyBankList.class);
				} else {
					resultsList = resultsObj.invokeHalfYearlyBank();
					redisPool.setValues(RedisConstants.RESULTS_HALF_YEARLY_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(resultsList));
				}
				}catch (Exception e) {
					log.error(e);
					resultsList = resultsObj.invokeHalfYearlyBank();
				}

				for (ResultsHalfYearlyBank results : resultsList) {

					JSONObject obj = new JSONObject();

					String sYRC = results.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_EARNED,
							PriceFormat.formatPrice(results.getInterestEarned(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(results.getOtherIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL_INCOME,
							PriceFormat.formatPrice(results.getTotalIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_EXPENSES,
							PriceFormat.formatPrice(results.getOperatingExpenses(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_PROFIT_BEFORE_PROV_CONT,
							PriceFormat.formatPrice(results.getOperatingProfitBeforeProvCont(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PROVISIONS_CONTIGENCIES,
							PriceFormat.formatPrice(results.getProvisionsContingencies(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(results.getDepreciation(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBT,
							PriceFormat.formatPrice(results.getPBT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PROVISION_FOR_TAXES,
							PriceFormat.formatPrice(results.getProvisionforTaxes(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.FRINGE_BENEFIT_TAX,
							PriceFormat.formatPrice(results.getFringeBenefitTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DEFERRED_TAX,
							PriceFormat.formatPrice(results.getDeferredTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_NET_PROFIT,
							PriceFormat.formatPrice(results.getNetProfit(), precision, true)));

					if (sType.equalsIgnoreCase(DeviceConstants.CONSOLIDATED)) {
						nameValuesArr.put(getNameValueObj(DeviceConstants.MINORITY_INTEREST_AFTER_NP,
								PriceFormat.formatPrice(results.getMinorityInterestafterNP(), precision, true)));
						nameValuesArr.put(getNameValueObj(DeviceConstants.PROFIT_LOSS_OF_ASSOCIATE_COMPANY,
								PriceFormat.formatPrice(results.getProfit_LossofAssociateCompany(), precision, true)));
						nameValuesArr.put(getNameValueObj(DeviceConstants.NET_PROFIT_AFTER_MINORITY_INTEREST,
								PriceFormat.formatPrice(results.getNetProfitafterMinorityInterest_PLAssoCo(), precision,
										true)));
					}
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedResultsList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Results H/Y" + " " + e.getMessage());
			}
		} else {
			try {
				ResultsHalfYearlyNonBankList resultsList = new ResultsHalfYearlyNonBankList();
				try {
				if (redisPool
						.isExists(RedisConstants.RESULTS_HALF_YEARLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					resultsList = new Gson().fromJson(
							redisPool.getValue(
									RedisConstants.RESULTS_HALF_YEARLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							ResultsHalfYearlyNonBankList.class);
				} else {
					resultsList = resultsObj.invokeHalfYearlyNonBank();
					redisPool.setValues(RedisConstants.RESULTS_HALF_YEARLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(resultsList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					resultsList = resultsObj.invokeHalfYearlyNonBank();
				}

				for (ResultsHalfYearlyNonBank results : resultsList) {

					JSONObject obj = new JSONObject();

					String sYRC = results.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_SALES,
							PriceFormat.formatPrice(results.getNetSales_OtherOperatingIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL,
							PriceFormat.formatPrice(results.getTotal(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.TOTAL_EXPENDITURE,
							PriceFormat.formatPrice(results.getTotalExpenditure(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBIDT,
							PriceFormat.formatPrice(results.getPBIDT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(results.getOtherIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OPERATING_PROFIT,
							PriceFormat.formatPrice(results.getOperatingProfit(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_INTEREST,
							PriceFormat.formatPrice(results.getInterest(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EXCEPTIONAL_ITEMS,
							PriceFormat.formatPrice(results.getExceptionalItem(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBDT,
							PriceFormat.formatPrice(results.getPBDT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(results.getDepreciation(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBT,
							PriceFormat.formatPrice(results.getPBT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TAX,
							PriceFormat.formatPrice(results.getTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PROFIT_AFTER_TAX,
							PriceFormat.formatPrice(results.getProfitAfterTax(), precision, true)));

					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedResultsList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}

		return parsedResultsList;
	}

	private static List<JSONObject> getResultsQuarterly(GetResults resultsObj, SymbolRow symRow, String sType,
			String sCoCode) {
		List<JSONObject> parsedResultsList = new ArrayList<JSONObject>();

		int precision = symRow.getPrecisionInt();
		String sSectorFormat = symRow.getCMSectorFormat();
		RedisPool redisPool = new RedisPool();
		if (sSectorFormat.equalsIgnoreCase(DeviceConstants.BANK)) {
			try {
				ResultsQuarterlyBankList resultsList = new ResultsQuarterlyBankList();
				try {
				if (redisPool.isExists(RedisConstants.RESULTS_QUARTERLY_LIST + "_" + sCoCode + "_" + sType)) {
					resultsList = new Gson().fromJson(
							redisPool.getValue(RedisConstants.RESULTS_QUARTERLY_LIST + "_" + sCoCode + "_" + sType),
							ResultsQuarterlyBankList.class);
				} else {
					resultsList = resultsObj.invokeQuarterlyBank();
					redisPool.setValues(RedisConstants.RESULTS_QUARTERLY_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(resultsList));
				}
				}catch (Exception e) {
					log.error(e);
					resultsList = resultsObj.invokeQuarterlyBank();
				}

				for (ResultsQuarterlyBank results : resultsList) {

					JSONObject obj = new JSONObject();

					String sYRC = results.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.INTEREST_EARNED,
							PriceFormat.formatPrice(results.getInterestEarned(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(results.getOtherIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL_INCOME,
							PriceFormat.formatPrice(results.getTotalIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_EXPENSES,
							PriceFormat.formatPrice(results.getOperatingExpenses(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.OPERATING_PROFIT_BEFORE_PROV_CONT,
							PriceFormat.formatPrice(results.getOperatingProfitBeforeProvCont(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PROVISIONS_CONTIGENCIES,
							PriceFormat.formatPrice(results.getProvisionsContingencies(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(results.getDepreciation(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBT,
							PriceFormat.formatPrice(results.getPBT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.PROVISION_FOR_TAXES,
							PriceFormat.formatPrice(results.getProvisionforTaxes(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.FRINGE_BENEFIT_TAX,
							PriceFormat.formatPrice(results.getFringeBenefitTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DEFERRED_TAX,
							PriceFormat.formatPrice(results.getDeferredTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_NET_PROFIT,
							PriceFormat.formatPrice(results.getNetProfit(), precision, true)));

					if (sType.equalsIgnoreCase(DeviceConstants.CONSOLIDATED)) {
						nameValuesArr.put(getNameValueObj(DeviceConstants.MINORITY_INTEREST_AFTER_NP,
								PriceFormat.formatPrice(results.getMinorityInterestafterNP(), precision, true)));
						nameValuesArr.put(getNameValueObj(DeviceConstants.PROFIT_LOSS_OF_ASSOCIATE_COMPANY,
								PriceFormat.formatPrice(results.getProfit_LossofAssociateCompany(), precision, true)));
						nameValuesArr.put(getNameValueObj(DeviceConstants.NET_PROFIT_AFTER_MINORITY_INTEREST,
								PriceFormat.formatPrice(results.getNetProfitafterMinorityInterest_PLAssoCo(), precision,
										true)));
					}
					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedResultsList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
				Monitor.markFailure(Market.CMOTS_API_BEAN,
						"Error while invoking CMOTS data for Results Q/Y" + " " + e.getMessage());
			}
		} else {
			try {
				ResultsQuarterlyNonBankList resultsList = new ResultsQuarterlyNonBankList();
				try {
				if (redisPool.isExists(RedisConstants.RESULTS_QUARTERLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType)) {
					resultsList = new Gson().fromJson(
							redisPool.getValue(
									RedisConstants.RESULTS_QUARTERLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType),
							ResultsQuarterlyNonBankList.class);
				} else {
					resultsList = resultsObj.invokeQuarterlyNonBank();
					redisPool.setValues(RedisConstants.RESULTS_QUARTERLY_NON_BANK_LIST + "_" + sCoCode + "_" + sType,
							new Gson().toJson(resultsList));
				}
				}catch (JedisConnectionException e) {
					log.error(e);
					resultsList = resultsObj.invokeQuarterlyNonBank();
				}

				for (ResultsQuarterlyNonBank results : resultsList) {

					JSONObject obj = new JSONObject();

					String sYRC = results.getYRC();
					int index = sYRC.indexOf(".");
					if (index != -1)
						sYRC = sYRC.substring(0, index);
					int year = Integer.parseInt(sYRC.substring(0, 4));
					int month = Integer.parseInt(sYRC.substring(4, sYRC.length()));
					obj.put(DeviceConstants.PERIOD, DateUtils.getEndofMonthDate(month, year));

					JSONArray nameValuesArr = new JSONArray();
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_SALES,
							PriceFormat.formatPrice(results.getNetSales_OtherOperatingIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TOTAL,
							PriceFormat.formatPrice(results.getTotal(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.TOTAL_EXPENDITURE,
							PriceFormat.formatPrice(results.getTotalExpenditure(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBIDT,
							PriceFormat.formatPrice(results.getPBIDT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OTHER_INCOME,
							PriceFormat.formatPrice(results.getOtherIncome(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_OPERATING_PROFIT,
							PriceFormat.formatPrice(results.getOperatingProfit(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_INTEREST,
							PriceFormat.formatPrice(results.getInterest(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.EXCEPTIONAL_ITEMS,
							PriceFormat.formatPrice(results.getExceptionalItem(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBDT,
							PriceFormat.formatPrice(results.getPBDT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_DEPRECIATION,
							PriceFormat.formatPrice(results.getDepreciation(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PBT,
							PriceFormat.formatPrice(results.getPBT(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_TAX,
							PriceFormat.formatPrice(results.getTax(), precision, true)));
					nameValuesArr.put(getNameValueObj(DeviceConstants.DISP_PROFIT_AFTER_TAX,
							PriceFormat.formatPrice(results.getProfitAfterTax(), precision, true)));

					obj.put(DeviceConstants.NAME_VALUES, nameValuesArr);

					parsedResultsList.add(obj);

				}
			} catch (CMOTSException e) {
				log.warn(e);
			}

		}

		return parsedResultsList;
	}

	public static void getLTPPeerComparison(JSONArray peerComparison, LinkedHashSet<String> linkedsetSymbolToken)
			throws SQLException {
		Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedsetSymbolToken);

		for (int i = 0; i < peerComparison.length(); i++) {
			JSONObject obj = peerComparison.getJSONObject(i);
			JSONObject symObj = obj.getJSONObject(SymbolConstants.SYMBOL_OBJ);
			int precision = Integer.parseInt(symObj.getString(SymbolConstants.PRECISION));
			String symbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
			if (mQuoteDetails.containsKey(symbolToken)) {
				QuoteDetails quoteDetails = mQuoteDetails.get(symbolToken);
				obj.put(DeviceConstants.LTP, PriceFormat.formatPrice(quoteDetails.sLTP, precision, false));

			} else
				obj.put(DeviceConstants.LTP, "--");

		}
	}
}
