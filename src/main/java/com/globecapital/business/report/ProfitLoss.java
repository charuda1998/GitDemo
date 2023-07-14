package com.globecapital.business.report;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.GetCommodityScripAPI;
import com.globecapital.api.gc.backoffice.GetCurrencyScripAPI;
import com.globecapital.api.gc.backoffice.GetDerivativeScripAPI;
import com.globecapital.api.gc.backoffice.GetEquityScripAPI;
import com.globecapital.api.gc.backoffice.GetEquityTrxnScripRequest;
import com.globecapital.api.gc.backoffice.GetRLCommodityPLAPI;
import com.globecapital.api.gc.backoffice.GetRLCurrencyPLAPI;
import com.globecapital.api.gc.backoffice.GetRLDerivativesPLAPI;
import com.globecapital.api.gc.backoffice.GetRLEquityPLAPI;
import com.globecapital.api.gc.backoffice.GetRealisedDerivativesPLResponse;
import com.globecapital.api.gc.backoffice.GetRealisedEquityProfitLossResponse;
import com.globecapital.api.gc.backoffice.GetRealisedPLRows;
import com.globecapital.api.gc.backoffice.GetRealisedProfitLossRequest;
import com.globecapital.api.gc.backoffice.GetTransactionScripRequest;
import com.globecapital.api.gc.backoffice.GetTransactionScripResponse;
import com.globecapital.api.gc.backoffice.GetTransactionScripRows;
import com.globecapital.api.gc.backoffice.GetUNRLCommodityPLAPI;
import com.globecapital.api.gc.backoffice.GetUNRLCurrencyPLAPI;
import com.globecapital.api.gc.backoffice.GetUNRLDerivativesPLAPI;
import com.globecapital.api.gc.backoffice.GetUNRLEquityPLAPI;
import com.globecapital.api.gc.backoffice.GetUnRealisedCommodityScripAPI;
import com.globecapital.api.gc.backoffice.GetUnRealisedCurrencyScripAPI;
import com.globecapital.api.gc.backoffice.GetUnRealisedDerivativeScripAPI;
import com.globecapital.api.gc.backoffice.GetUnRealisedEquityScripAPI;
import com.globecapital.api.gc.backoffice.GetUnrealisedPLRows;
import com.globecapital.api.gc.backoffice.GetUnrealisedProfitLossRequest;
import com.globecapital.api.gc.backoffice.GetUnrealisedProfitLossResponse;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class ProfitLoss {

	private static Logger log = Logger.getLogger(ProfitLoss.class);

	public static JSONArray getRealisedProfitLossReports(Session session, String segmentType, JSONObject filterObj,
			String reportType) throws JSONException {

		String userId= session.getUserID();
		
		JSONArray profitLossArray = new JSONArray();
		JSONObject totalSummary = new JSONObject();
		JSONObject summaryDetails = new JSONObject();

		JSONArray sorted = new JSONArray();
		String[] scripArray;
		int precision = 0;
		String filterType = filterObj.getString(DeviceConstants.DATE_FILTER);
		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
		String sortBy = filterObj.getString(DeviceConstants.SORT_BY);

		try {

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				precision = 2;
			} else {
				precision = 4;
			}
			JSONObject reportDates = FilterType.getFilterDates(filterType, filterObj);

			String currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);
			GetRealisedProfitLossRequest profitLossRequest = new GetRealisedProfitLossRequest();
			GetRealisedEquityProfitLossResponse realisedProfitLossResponse = new GetRealisedEquityProfitLossResponse();
			GetRealisedDerivativesPLResponse realisedDerivativesProfitLossResponse = new GetRealisedDerivativesPLResponse();
//			profitLossRequest.setToken(GCAPIAuthToken.getAuthToken());
			profitLossRequest.setClientCode(userId);
			profitLossRequest.setToken(GCAPIAuthToken.getAuthToken());
			profitLossRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
			profitLossRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));

			if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				profitLossRequest.setType("N");
				profitLossRequest.setYear(currentFinancialYear);
			}

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				GetRLEquityPLAPI rlEquityProfitLossAPI = new GetRLEquityPLAPI();
				realisedProfitLossResponse = rlEquityProfitLossAPI.get(profitLossRequest,
						GetRealisedEquityProfitLossResponse.class, session.getAppID(),"GetRealisedEquityProfitLoss");
				if (realisedProfitLossResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					profitLossRequest.setToken(GCAPIAuthToken.getAuthToken());
					realisedProfitLossResponse = rlEquityProfitLossAPI.get(profitLossRequest, GetRealisedEquityProfitLossResponse.class, session.getAppID(),
							"GetRealisedEquityProfitLoss");
				}
				List<GetRealisedPLRows> profitLossRows = new ArrayList<>();
				if(realisedProfitLossResponse.getMsg().equalsIgnoreCase(DeviceConstants.SUCCESS))
					profitLossRows = realisedProfitLossResponse.getTradeDetails();
				Collections.reverse(profitLossRows);

				for (GetRealisedPLRows rows : profitLossRows) {

					SymbolRow symbolProfitLossObj = new SymbolRow();
					JSONObject profitLossObj = new JSONObject();
					String isinTokenSegmentNSE = rows.getIsin() + "_" + ExchangeSegment.NSE_SEGMENT_ID;
					String isinTokenSegmentBSE = rows.getIsin() + "_" + ExchangeSegment.BSE_SEGMENT_ID;

					if (SymbolMap.isValidSymbol(isinTokenSegmentNSE)) {
						symbolProfitLossObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentNSE).getMinimisedSymbolRow());
					}else if (SymbolMap.isValidSymbol(isinTokenSegmentBSE)) {
						symbolProfitLossObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentBSE).getMinimisedSymbolRow());
					}else{
						continue;
					}

					JSONObject transApiObj = new JSONObject();
					profitLossObj.put(DeviceConstants.SYMBOL, symbolProfitLossObj
							.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL));
					profitLossObj.put(DeviceConstants.SYMBOL_DETAILS, rows.getScripname());
					profitLossObj.put(DeviceConstants.REALISED_PROFIT_LOSS,
							PriceFormat.priceToRupees(rows.getRealizedPL()));
					profitLossObj.put(DeviceConstants.BUY_QTY, rows.getBuyQty());
					profitLossObj.put(DeviceConstants.SELL_QTY, rows.getSellQty());
					profitLossObj.put(DeviceConstants.BUY_AVG, PriceFormat.priceToRupee(rows.getBuyAvg(), precision));
					profitLossObj.put(DeviceConstants.SELL_AVG, PriceFormat.priceToRupee(rows.getSellAvg(), precision));
					profitLossObj.put(DeviceConstants.BUY_VALUE,
							PriceFormat.priceToRupee(rows.getBuyValue(), precision));
					profitLossObj.put(DeviceConstants.SELL_VALUE,
							PriceFormat.priceToRupee(rows.getSellValue(), precision));
					profitLossObj.put(DeviceConstants.SCRIP_CODE, rows.getScripCode());

					transApiObj.put(DeviceConstants.SEGMENT_TYPE, segmentType);
					transApiObj.put(DeviceConstants.SCRIP_CODE, rows.getScripCode());

					profitLossObj.put(DeviceConstants.TRANS_API_OBJ, transApiObj);
					profitLossArray.put(profitLossObj);

				}

				summaryDetails.put(DeviceConstants.TOTAL_REALISED_PROFIT_AND_LOSS,
						PriceFormat.priceInCrores(realisedProfitLossResponse.getTotalRLPL()));
				if (realisedProfitLossResponse.getTotalRLPL().equals("0")) {
					summaryDetails.put(DeviceConstants.REALISED_PROFIT_LOSS_INDICATOR, "");
				} else if (Double.parseDouble(realisedProfitLossResponse.getTotalRLPL()) > 0.0) {
					summaryDetails.put(DeviceConstants.REALISED_PROFIT_LOSS_INDICATOR, "+");
				} else {
					summaryDetails.put(DeviceConstants.REALISED_PROFIT_LOSS_INDICATOR, "-");
				}

				summaryDetails.put(DeviceConstants.TOTAL_CHARGES,
						PriceFormat.priceInCrores(realisedProfitLossResponse.getTotalCharges()));
				summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS,
						PriceFormat.priceInCrores(realisedProfitLossResponse.getNetRLPL()));

				if (realisedProfitLossResponse.getNetRLPL().equals("0")) {
					summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS_INDICATOR, "");
				} else if (Double.parseDouble(realisedProfitLossResponse.getNetRLPL()) > 0.0) {
					summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS_INDICATOR, "+");
				} else {
					summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS_INDICATOR, "-");
				}
				totalSummary.put(DeviceConstants.TOTAL_SUMMARY, summaryDetails);
				sorted = getFilteredRecords(profitLossArray, filterBy, sortOrder, sortBy, filterObj);
				sorted.put(totalSummary);
				return sorted;
			} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {

				GetRLDerivativesPLAPI rlDerivativeProfitLossAPI = new GetRLDerivativesPLAPI();
				realisedDerivativesProfitLossResponse = rlDerivativeProfitLossAPI.get(profitLossRequest,
						GetRealisedDerivativesPLResponse.class, session.getAppID(),"GetRealisedDerivativeProfitLoss");
			} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {

				GetRLCurrencyPLAPI rlCurrencyProfitLossAPI = new GetRLCurrencyPLAPI();
				realisedDerivativesProfitLossResponse = rlCurrencyProfitLossAPI.get(profitLossRequest,
						GetRealisedDerivativesPLResponse.class, session.getAppID(),"GetRealisedCurrencyProfitLoss");
			} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {

				GetRLCommodityPLAPI rlCommodityProfitLossAPI = new GetRLCommodityPLAPI();
				realisedDerivativesProfitLossResponse = rlCommodityProfitLossAPI.get(profitLossRequest,
						GetRealisedDerivativesPLResponse.class, session.getAppID(),"GetRealisedCommodityProfitLoss");
			}
			if (realisedDerivativesProfitLossResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				profitLossRequest.setToken(GCAPIAuthToken.getAuthToken());
				if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
					GetRLDerivativesPLAPI rlDerivativeProfitLossAPI = new GetRLDerivativesPLAPI();
					realisedDerivativesProfitLossResponse = rlDerivativeProfitLossAPI.get(profitLossRequest, GetRealisedDerivativesPLResponse.class, session.getAppID(),
							"GetRealisedDerivativeProfitLoss");
				}else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
					GetRLCurrencyPLAPI rlCurrencyProfitLossAPI = new GetRLCurrencyPLAPI();
					realisedDerivativesProfitLossResponse = rlCurrencyProfitLossAPI.get(profitLossRequest, GetRealisedDerivativesPLResponse.class, session.getAppID(),
							"GetRealisedCurrencyProfitLoss");
				}else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
					GetRLCommodityPLAPI rlCommodityProfitLossAPI = new GetRLCommodityPLAPI();
					realisedDerivativesProfitLossResponse = rlCommodityProfitLossAPI.get(profitLossRequest, GetRealisedDerivativesPLResponse.class, session.getAppID(),
							"GetRealisedCommodityProfitLoss");
				}
			}
			List<GetRealisedPLRows> profitLossRows = new ArrayList<>();
			if(realisedDerivativesProfitLossResponse.getMsg().equalsIgnoreCase(DeviceConstants.SUCCESS))
				profitLossRows = realisedDerivativesProfitLossResponse.getTradeDetails();
			Collections.reverse(profitLossRows);

			for (GetRealisedPLRows rows : profitLossRows) {

				scripArray = rows.getScripname().split(" ");
				JSONObject profitLossObj = new JSONObject();
				JSONObject transApiObj = new JSONObject();

				profitLossObj.put(DeviceConstants.SYMBOL, scripArray[0]);
				profitLossObj.put(DeviceConstants.SYMBOL_DETAILS, rows.getScripname()
						.substring(rows.getScripname().indexOf(" "), rows.getScripname().length()).trim());
				profitLossObj.put(DeviceConstants.REALISED_PROFIT_LOSS,
						PriceFormat.priceToRupee(rows.getRealizedPL(), precision));
				profitLossObj.put(DeviceConstants.BUY_QTY, rows.getBuyQty());
				profitLossObj.put(DeviceConstants.SELL_QTY, rows.getSellQty());
				profitLossObj.put(DeviceConstants.BUY_AVG, PriceFormat.priceToRupee(rows.getBuyAvg(), precision));
				profitLossObj.put(DeviceConstants.SELL_AVG, PriceFormat.priceToRupee(rows.getSellAvg(), precision));
				profitLossObj.put(DeviceConstants.BUY_VALUE, PriceFormat.priceToRupee(rows.getBuyValue(), precision));
				profitLossObj.put(DeviceConstants.SELL_VALUE, PriceFormat.priceToRupee(rows.getSellValue(), precision));
				transApiObj.put(DeviceConstants.SEGMENT_TYPE, segmentType);
				transApiObj.put(DeviceConstants.SCRIP_NAME, rows.getScripname().trim());

				profitLossObj.put(DeviceConstants.TRANS_API_OBJ, transApiObj);
				profitLossArray.put(profitLossObj);
			}

			summaryDetails.put(DeviceConstants.TOTAL_REALISED_PROFIT_AND_LOSS,
					PriceFormat.priceInCrores(realisedDerivativesProfitLossResponse.getTotalRLPL(), precision));

			if (realisedDerivativesProfitLossResponse.getTotalRLPL().equals("0")) {
				summaryDetails.put(DeviceConstants.REALISED_PROFIT_LOSS_INDICATOR, "");
			} else if (Double.parseDouble(realisedDerivativesProfitLossResponse.getTotalRLPL()) > 0.0) {
				summaryDetails.put(DeviceConstants.REALISED_PROFIT_LOSS_INDICATOR, "+");
			} else {
				summaryDetails.put(DeviceConstants.REALISED_PROFIT_LOSS_INDICATOR, "-");
			}
			summaryDetails.put(DeviceConstants.TOTAL_CHARGES,
					PriceFormat.priceInCrores(realisedDerivativesProfitLossResponse.getTotalCharges(), precision));
			summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS,
					PriceFormat.priceInCrores(realisedDerivativesProfitLossResponse.getNetRLPL(), precision));

			if (realisedDerivativesProfitLossResponse.getNetRLPL().equals("0")) {
				summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS_INDICATOR, "");
			} else if (Double.parseDouble(realisedDerivativesProfitLossResponse.getNetRLPL()) > 0.0) {
				summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS_INDICATOR, "+");
			} else {
				summaryDetails.put(DeviceConstants.NET_REALISED_PROFIT_LOSS_INDICATOR, "-");
			}
			totalSummary.put(DeviceConstants.TOTAL_SUMMARY, summaryDetails);
			sorted = getFilteredRecords(profitLossArray, filterBy, sortOrder, sortBy, filterObj);
			sorted.put(totalSummary);

		} catch (Exception e) {
			log.error(e);
		}
		return sorted;
	}

	public static JSONArray getUnRealisedProfitLossReports(Session session, String segmentType, JSONObject filterObj)
			throws JSONException {

		String userId = session.getUserID();
		
		JSONArray profitLossArray = new JSONArray();
		JSONObject totalSummary = new JSONObject();
		JSONObject summaryDetails = new JSONObject();
		JSONArray sorted = new JSONArray();
		String[] scripArray;
		int precision = 0;
		String[] financialYear = DateUtils.getFinancialYear().split("-");
		String currentFinancialYear = financialYear[0].substring(2) + financialYear[1].substring(2);
		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		String sortOrder = filterObj.getString(DeviceConstants.SORT_ORDER);
		String sortBy = filterObj.getString(DeviceConstants.SORT_BY);

		try {

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				precision = 2;
			} else {
				precision = 4;
			}
			GetUnrealisedProfitLossRequest profitLossRequest = new GetUnrealisedProfitLossRequest();
			GetUnrealisedProfitLossResponse unrealisedProfitLossResponse = new GetUnrealisedProfitLossResponse();

//			profitLossRequest.setToken(GCAPIAuthToken.getAuthToken());

			profitLossRequest.setClientCode(userId);
			profitLossRequest.setToken(GCAPIAuthToken.getAuthToken());
			if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				profitLossRequest.setDate(DateUtils.getCurrentDate());
				profitLossRequest.setYear(currentFinancialYear);
			}

			unrealisedProfitLossResponse = performUnRlAPICall(session, segmentType, profitLossRequest, unrealisedProfitLossResponse);
			if (unrealisedProfitLossResponse.getMsg().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				profitLossRequest.setToken(GCAPIAuthToken.getAuthToken());
				unrealisedProfitLossResponse = performUnRlAPICall(session, segmentType, profitLossRequest, unrealisedProfitLossResponse);
			}

			List<GetUnrealisedPLRows> unrealisedProfitLossRows = new ArrayList<>();
			if(unrealisedProfitLossResponse.getMsg().equalsIgnoreCase(DeviceConstants.SUCCESS))
				unrealisedProfitLossRows = unrealisedProfitLossResponse.getTradeDetails();
			Collections.reverse(unrealisedProfitLossRows);
			
			LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
			
			int iDiscrepancyCount = 0;

			for (GetUnrealisedPLRows rows : unrealisedProfitLossRows) {

				
				JSONObject profitLossObj = new JSONObject();
				
				profitLossObj.put(DeviceConstants.AS_ON_DATE, unrealisedProfitLossResponse.getDate());
				
				if (rows.getScripname().isEmpty()) {
					continue;
				} else
					scripArray = rows.getScripname().split(" ");

				JSONObject transApiObj = new JSONObject();

				if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
					SymbolRow symbolProfitLossObj = new SymbolRow();
					
					String isinTokenSegment_NSE = rows.getIsin() + "_" + ExchangeSegment.NSE_SEGMENT_ID;
					String isinTokenSegment_BSE = rows.getIsin() + "_" + ExchangeSegment.BSE_SEGMENT_ID;

					if (SymbolMap.isValidSymbol(isinTokenSegment_NSE)) {
						symbolProfitLossObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegment_NSE).getMinimisedSymbolRow());
					}else if (SymbolMap.isValidSymbol(isinTokenSegment_BSE)) {
						symbolProfitLossObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegment_BSE).getMinimisedSymbolRow());
					}else {
						continue;
					}
					if(rows.getIsDiscrepancy().equalsIgnoreCase(GCConstants.Y))
						iDiscrepancyCount = iDiscrepancyCount + 1;
					linkedsetSymbolToken.add(symbolProfitLossObj.getSymbolToken());
					profitLossObj.put(SymbolConstants.SYMBOL_TOKEN, symbolProfitLossObj.getSymbolToken());
					profitLossObj.put(DeviceConstants.SYMBOL, symbolProfitLossObj
							.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL));
					profitLossObj.put(DeviceConstants.SYMBOL_DETAILS, rows.getScripname());
					profitLossObj.put(DeviceConstants.SCRIP_CODE, rows.getScripCode());
					transApiObj.put(DeviceConstants.SCRIP_CODE, rows.getScripCode());
				} else {
					SymbolRow symbolProfitLossObj = new SymbolRow();
					String sSymbolUniqDesc = getSymbolUniqDesc(rows.getScripname().trim(), rows.getExchange(), 
							segmentType, rows.getInstrument());
					
					if (SymbolMap.isValidSymbolUniqDescMap(sSymbolUniqDesc)) {
						symbolProfitLossObj.extend(SymbolMap.getSymbolUniqDescRow(sSymbolUniqDesc).getMinimisedSymbolRow());
					} else {
						log.info("Invalid SymbolUniqDesc:" + sSymbolUniqDesc);
						continue;
					}
					linkedsetSymbolToken.add(symbolProfitLossObj.getSymbolToken());
					profitLossObj.put(SymbolConstants.SYMBOL_TOKEN, symbolProfitLossObj.getSymbolToken());
					profitLossObj.put(DeviceConstants.SYMBOL, scripArray[0]);
					profitLossObj.put(DeviceConstants.SYMBOL_DETAILS, rows.getScripname()
							.substring(rows.getScripname().indexOf(" "), rows.getScripname().length()).trim());
					profitLossObj.put(DeviceConstants.SCRIP_CODE,  "--" );
					transApiObj.put(DeviceConstants.SCRIP_NAME, rows.getScripname().trim());
				}

				profitLossObj.put(DeviceConstants.UNREALISED_PROFIT_LOSS,
						PriceFormat.priceToRupee(rows.getUnRealizedPL(), precision));
				profitLossObj.put(DeviceConstants.OPEN_QTY, String.valueOf(Math.abs(Integer.parseInt(rows.getOpenQty()))));
				profitLossObj.put(DeviceConstants.MARKET_VALUE,
						PriceFormat.priceToRupee(rows.getMarketValue(), precision));
				profitLossObj.put(DeviceConstants.AVG_PRICE, PriceFormat.priceToRupee(rows.getAvgPrice(), precision));
				profitLossObj.put(DeviceConstants.OPEN_VALUE, PriceFormat.priceToRupee(rows.getOpenValue(), precision));
				
				transApiObj.put(DeviceConstants.SEGMENT_TYPE, segmentType);
				profitLossObj.put(DeviceConstants.TRANS_API_OBJ, transApiObj);
				profitLossObj.put(DeviceConstants.PREV_CLOSE, PriceFormat.formatPrice(rows.getLTP(), precision, false));
				
				profitLossArray.put(profitLossObj);

			}

//			getPrevClose(profitLossArray, linkedsetSymbolToken, precision);
			summaryDetails.put(DeviceConstants.TOTAL_INVESTMENT_VALUE,
					PriceFormat.priceInCrores(unrealisedProfitLossResponse.getTotalInvestmentValue(), precision));
			summaryDetails.put(DeviceConstants.TOTAL_MARKET_VALUE,
					PriceFormat.priceInCrores(unrealisedProfitLossResponse.getTotalMarketValue(), precision));
			summaryDetails.put(DeviceConstants.TOTAL_UNREALISED_PROFIT_AND_LOSS,
					PriceFormat.priceInCrores(unrealisedProfitLossResponse.getTotalUnRelPl(), precision));
			if (unrealisedProfitLossResponse.getTotalUnRelPl().equals("0")) {
				summaryDetails.put(DeviceConstants.UNREALISED_PROFIT_LOSS_INDICATOR, "");
			} else if (Double.parseDouble(unrealisedProfitLossResponse.getTotalUnRelPl()) > 0.0) {
				summaryDetails.put(DeviceConstants.UNREALISED_PROFIT_LOSS_INDICATOR, "+");
			} else {
				summaryDetails.put(DeviceConstants.UNREALISED_PROFIT_LOSS_INDICATOR, "-");
			}
			
			summaryDetails.put(DeviceConstants.DISCREPANCY_COUNT, Integer.toString(iDiscrepancyCount));

			totalSummary.put(DeviceConstants.TOTAL_SUMMARY, summaryDetails);
			sorted = getFilteredRecords(profitLossArray, filterBy, sortOrder, sortBy, filterObj);
			sorted.put(totalSummary);

		} catch (Exception e) {
			log.error(e);
		}
		return sorted;

	}

	public static GetUnrealisedProfitLossResponse performUnRlAPICall(Session session, String segmentType, GetUnrealisedProfitLossRequest profitLossRequest,
			GetUnrealisedProfitLossResponse unrealisedProfitLossResponse) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			GetUNRLEquityPLAPI unrlEquityProfitLossAPI = new GetUNRLEquityPLAPI();
			unrealisedProfitLossResponse = unrlEquityProfitLossAPI.get(profitLossRequest,
					GetUnrealisedProfitLossResponse.class, session.getAppID(),"GetUnRealisedEquityProfitLoss");

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			GetUNRLDerivativesPLAPI unrlDerivativeProfitLossAPI = new GetUNRLDerivativesPLAPI();
			unrealisedProfitLossResponse = unrlDerivativeProfitLossAPI.get(profitLossRequest,
					GetUnrealisedProfitLossResponse.class, session.getAppID(),"GetUnRealisedDerivativeProfitLoss");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			GetUNRLCurrencyPLAPI unrlCurrencyProfitLossAPI = new GetUNRLCurrencyPLAPI();
			unrealisedProfitLossResponse = unrlCurrencyProfitLossAPI.get(profitLossRequest,
					GetUnrealisedProfitLossResponse.class, session.getAppID(),"GetUnRealisedCurrencyProfitLoss");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			GetUNRLCommodityPLAPI unrlCommodityProfitLossAPI = new GetUNRLCommodityPLAPI();
			unrealisedProfitLossResponse = unrlCommodityProfitLossAPI.get(profitLossRequest,
					GetUnrealisedProfitLossResponse.class, session.getAppID(),"GetUnRealisedCommodityProfitLoss");
		}
		return unrealisedProfitLossResponse;
	}

	private static String getSymbolUniqDesc(String sScripFullName, String exchange, String segmentType, String sInstrument) throws ParseException {
		String sScripName = sScripFullName.substring(0, sScripFullName.indexOf(" "));
		
		String sRemainingDetail = sScripFullName.substring(sScripFullName.indexOf(" ") + 1, 
				sScripFullName.length());
		int iThirdSpace = sRemainingDetail.indexOf(" ");
		if(iThirdSpace != -1)
		{
			String sDate = DateUtils.formatDate(sRemainingDetail.substring(0, iThirdSpace), 
					DeviceConstants.PL_DATE_FORMAT, DBConstants.UNIQ_DESC_DATE_FORMAT).toUpperCase();
			String sDetail = sRemainingDetail.substring(sRemainingDetail.indexOf(" ")+1, sRemainingDetail.length());
			String[] arrDetail = sDetail.split(" ");
			sScripName = sScripName + sDate + arrDetail[1] + arrDetail[0];
			
		}
		else
		{
			String sDate = DateUtils.formatDate(sRemainingDetail, DeviceConstants.PL_DATE_FORMAT, 
					DBConstants.UNIQ_DESC_DATE_FORMAT).toUpperCase();
			sScripName = sScripName + sDate;
		}
		
		String sInst = "";
		
		if(sInstrument.equalsIgnoreCase("FUT"))
			sInst = sInstrument;
			
		if(segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY))
		{
			if(exchange.equalsIgnoreCase(ExchangeSegment.NSE))
				sScripName = sScripName + sInst + "_" + ExchangeSegment.NSECDS;
			else if(exchange.equalsIgnoreCase(ExchangeSegment.BSE))
				sScripName = sScripName + sInst + "_" + ExchangeSegment.BSECDS;
		} 
		else if(segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE))
			sScripName = sScripName + sInst + "_" + ExchangeSegment.NFO;
		else
			sScripName = sScripName +  sInst + "_" + exchange;
			
		return sScripName;
	}

	private static void getPrevClose(JSONArray profitLossArray, LinkedHashSet<String> linkedsetSymbolToken, 
			int precision) 
			throws SQLException {
		Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedsetSymbolToken);	
		for (int i = 0; i < profitLossArray.length(); i++) {
			JSONObject profitLossObj = profitLossArray.getJSONObject(i);
			String sSymbolToken = profitLossObj.getString(SymbolConstants.SYMBOL_TOKEN);

			if (mQuoteDetails.containsKey(sSymbolToken)) {
				QuoteDetails quoteDetails = mQuoteDetails.get(sSymbolToken);
				profitLossObj.put(DeviceConstants.PREV_CLOSE, 
						PriceFormat.formatPrice(quoteDetails.sPreviousClose, precision, false));

			} else {
				profitLossObj.put(DeviceConstants.PREV_CLOSE, "--");
			}
			
			profitLossObj.remove(SymbolConstants.SYMBOL_TOKEN);

		}
	}

	public static JSONArray getRealisedProfitLossTransaction(Session session, String maximumSlot, JSONObject transObj)
			throws JSONException, RequestFailedException {

		String userId = session.getUserID();
		
		JSONArray transactionArray = new JSONArray();
		String segmentType = transObj.getString(DeviceConstants.SEGMENT_TYPE);
		String scripCode = "";
		String scripName = "";
		String[] financialYear = DateUtils.getFinancialYear().split("-");
		String currentFinancialYear = financialYear[0].substring(2) + financialYear[1].substring(2);
		if (transObj.has(DeviceConstants.SCRIP_CODE)) {
			scripCode = transObj.getString(DeviceConstants.SCRIP_CODE);
		} else {
			scripName = transObj.getString(DeviceConstants.SCRIP_NAME);
		}
		int precision = 0;
		try {

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				precision = 2;
			} else {
				precision = 4;
			}
			GetTransactionScripRequest transactionScripRequest = new GetTransactionScripRequest();
			GetTransactionScripResponse transactionScripResponse = new GetTransactionScripResponse();
			transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
			transactionScripRequest.setClientCode(userId);
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				GetEquityTrxnScripRequest equityTransactionScripRequest = new GetEquityTrxnScripRequest();
//				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setClientCode(userId);
				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setScripName(scripCode);
				if (!maximumSlot.isEmpty()) {
					equityTransactionScripRequest.setMaxSlot(maximumSlot);
				} else {
					equityTransactionScripRequest.setMaxSlot("0");
				}
				GetEquityScripAPI transactionScrip = new GetEquityScripAPI();
				transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
						GetTransactionScripResponse.class, session.getAppID(),"GetRealisedEquityTransactionScrip");
				if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
					transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
							GetTransactionScripResponse.class, session.getAppID(),"GetRealisedEquityTransactionScrip");
				}

				List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

				if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
					for (GetTransactionScripRows scripRows : transactionScripRows) {
						JSONObject scripObject = new JSONObject();
						scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
						if (scripRows.getBuyOrSell().equals("B")) {
							scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
						} else
							scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
						scripObject.put(DeviceConstants.PRICE,
								PriceFormat.priceToRupee(scripRows.getNetrate(), precision));
						scripObject.put(DeviceConstants.QTY, String.valueOf(scripRows.getQty()));
						transactionArray.put(scripObject);
					}
				}
				return transactionArray;
			} else {

				transactionScripRequest.setScripName(scripName);
				// Scripname example  : Nifty 26-Mar-2021, Nifty 26-Mar-2021 14400 CE. 
				// We are parsing the expiry date and using it for deriving financial year
				String scripname[] = scripName.split(" ");
				if(scripname.length >=2) {
					String dateStr = scripname[1];
					SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.PL_DATE_FORMAT);
					SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
					Date responseDate = sourceFormat.parse(dateStr);
					String parsedDate = destinationFormat.format(responseDate);
					JSONObject reportDates = new JSONObject();
					reportDates.put(DeviceConstants.TO_DATE, parsedDate);
					currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);
				}
				transactionScripRequest.setYear(currentFinancialYear);
			}

			transactionScripResponse = performTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);

			if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				transactionScripResponse = performTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);
			}

			List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

			if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
				for (GetTransactionScripRows scripRows : transactionScripRows) {
					JSONObject scripObject = new JSONObject();
					scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
					if (scripRows.getBuyOrSell().equals("B")) {
						scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
					} else
						scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
					scripObject.put(DeviceConstants.PRICE, PriceFormat.priceToRupee(scripRows.getNetrate(),precision));
					scripObject.put(DeviceConstants.QTY, String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty()))));
					
					transactionArray.put(scripObject);
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return transactionArray;

	}
	
	public static JSONArray getRealisedProfitLossTransaction_101(Session session, String maximumSlot, JSONObject transObj)
			throws JSONException, RequestFailedException {

		String userId = session.getUserID();
		
		JSONArray transactionArray = new JSONArray();
		String segmentType = transObj.getString(DeviceConstants.SEGMENT_TYPE);
		String scripCode = "";
		String scripName = "";
		String[] financialYear = DateUtils.getFinancialYear().split("-");
		String currentFinancialYear = financialYear[0].substring(2) + financialYear[1].substring(2);
		if (transObj.has(DeviceConstants.SCRIP_CODE)) {
			scripCode = transObj.getString(DeviceConstants.SCRIP_CODE);
		} else {
			scripName = transObj.getString(DeviceConstants.SCRIP_NAME);
		}
		int precision = 0;
		try {

			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				precision = 2;
			} else {
				precision = 4;
			}
			GetTransactionScripRequest transactionScripRequest = new GetTransactionScripRequest();
			GetTransactionScripResponse transactionScripResponse = new GetTransactionScripResponse();
			transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
			transactionScripRequest.setClientCode(userId);
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				GetEquityTrxnScripRequest equityTransactionScripRequest = new GetEquityTrxnScripRequest();
//				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setClientCode(userId);
				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setScripName(scripCode);
				if (!maximumSlot.isEmpty()) {
					equityTransactionScripRequest.setMaxSlot(maximumSlot);
				} else {
					equityTransactionScripRequest.setMaxSlot("0");
				}
				GetEquityScripAPI transactionScrip = new GetEquityScripAPI();
				transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
						GetTransactionScripResponse.class, session.getAppID(),"GetRealisedEquityTransactionScrip");
				if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
					transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
							GetTransactionScripResponse.class, session.getAppID(),"GetRealisedEquityTransactionScrip");
				}

				List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

				if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
					for (GetTransactionScripRows scripRows : transactionScripRows) {
						JSONObject scripObject = new JSONObject();
						scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
						if (scripRows.getBuyOrSell().equals("B")) {
							scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
						} else
							scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
						scripObject.put(DeviceConstants.PRICE,
								PriceFormat.priceToRupee(scripRows.getNetrate(), precision));
						scripObject.put(DeviceConstants.QTY, String.valueOf(scripRows.getQty()));
						transactionArray.put(scripObject);
					}
				}
				return transactionArray;
			} else {

				transactionScripRequest.setScripName(scripName);
				// Scripname example  : Nifty 26-Mar-2021, Nifty 26-Mar-2021 14400 CE. 
				// We are parsing the expiry date and using it for deriving financial year
				String scripname[] = scripName.split(" ");
				if(scripname.length >=2) {
					String dateStr = scripname[1];
					SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.PL_DATE_FORMAT);
					SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
					Date responseDate = sourceFormat.parse(dateStr);
					String parsedDate = destinationFormat.format(responseDate);
					JSONObject reportDates = new JSONObject();
					reportDates.put(DeviceConstants.TO_DATE, parsedDate);
					currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);
				}
				transactionScripRequest.setYear(currentFinancialYear);
			}

			transactionScripResponse = performTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);

			if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				transactionScripResponse = performTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);
			}

			List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

			if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
				for (GetTransactionScripRows scripRows : transactionScripRows) {
					JSONObject scripObject = new JSONObject();
					scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
					if (scripRows.getBuyOrSell().equals("B")) {
						scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
					} else
						scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
					scripObject.put(DeviceConstants.PRICE, PriceFormat.priceToRupee(scripRows.getNetrate(),precision));
					if(segmentType.equals(DeviceConstants.CURRENCY)) {
						int lotsize = AppConfig.getIntValue("currency_min_lot_size");
						scripObject.put(DeviceConstants.QTY, String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty())/lotsize)));
					}else {
						scripObject.put(DeviceConstants.QTY, String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty()))));
					}
					
					transactionArray.put(scripObject);
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return transactionArray;

	}

	public static GetTransactionScripResponse performTrnsScripAPICall(Session session, String segmentType,
			GetTransactionScripRequest transactionScripRequest, GetTransactionScripResponse transactionScripResponse)
			throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			GetDerivativeScripAPI derivativeTransactionScripAPI = new GetDerivativeScripAPI();
			transactionScripResponse = derivativeTransactionScripAPI.get(transactionScripRequest,
					GetTransactionScripResponse.class, session.getAppID(),"GetRealisedDerivativeTransactionScrip");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			GetCurrencyScripAPI currencyTransactionScripAPI = new GetCurrencyScripAPI();
			transactionScripResponse = currencyTransactionScripAPI.get(transactionScripRequest,
					GetTransactionScripResponse.class, session.getAppID(),"GetRealisedCurrencyTransactionScrip");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			GetCommodityScripAPI commodityTransactionScripAPI = new GetCommodityScripAPI();
			transactionScripResponse = commodityTransactionScripAPI.get(transactionScripRequest,
					GetTransactionScripResponse.class, session.getAppID(),"GetRealisedCommodityTransactionScrip");
		}
		return transactionScripResponse;
	}

	public static JSONArray getUnRealisedProfitLossTransaction(Session session, String maximumSlot, JSONObject transObj)
			throws JSONException, RequestFailedException {

		String userId = session.getUserID();
		
		JSONArray transactionArray = new JSONArray();
		String segmentType = transObj.getString(DeviceConstants.SEGMENT_TYPE);
		String scripCode = "";
		String scripName = "";
		String[] financialYear = DateUtils.getFinancialYear().split("-");
		String currentFinancialYear = financialYear[0].substring(2) + financialYear[1].substring(2);
		if (transObj.has(DeviceConstants.SCRIP_CODE)) {
			scripCode = transObj.getString(DeviceConstants.SCRIP_CODE);
		} else {
			scripName = transObj.getString(DeviceConstants.SCRIP_NAME);
		}
		int precision = 0;
		try {
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				precision = 2;
			} else {
				precision = 4;
			}
			GetTransactionScripRequest transactionScripRequest = new GetTransactionScripRequest();
			GetTransactionScripResponse transactionScripResponse = new GetTransactionScripResponse();
			transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
			transactionScripRequest.setClientCode(userId);
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				GetEquityTrxnScripRequest equityTransactionScripRequest = new GetEquityTrxnScripRequest();
//				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setClientCode(userId);
				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setScripName(scripCode);

				equityTransactionScripRequest.setScripName(scripCode);
				if (!maximumSlot.isEmpty()) {
					equityTransactionScripRequest.setMaxSlot(maximumSlot);
				} else {
					equityTransactionScripRequest.setMaxSlot("0");
				}
				GetUnRealisedEquityScripAPI transactionScrip = new GetUnRealisedEquityScripAPI();
				transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
						GetTransactionScripResponse.class, session.getAppID(),"GetUnRealisedEquityTransactionScrip");
				if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
					transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
							GetTransactionScripResponse.class, session.getAppID(),"GetUnRealisedEquityTransactionScrip");
				}

				List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

				if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
					for (GetTransactionScripRows scripRows : transactionScripRows) {
						JSONObject scripObject = new JSONObject();
						scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
						if (scripRows.getBuyOrSell().equals("B")) {
							scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
						} else
							scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
						scripObject.put(DeviceConstants.PRICE,
								PriceFormat.priceToRupee(scripRows.getNetrate(), precision));
						scripObject.put(DeviceConstants.QTY, 
									String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty()))));
						transactionArray.put(scripObject);
					}
				}
				return transactionArray;

			} else {
				transactionScripRequest.setScripName(scripName);
				transactionScripRequest.setYear(currentFinancialYear);
			}

			transactionScripResponse = performUnRlTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);

			if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				performUnRlTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);
			}

			List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

			if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
				for (GetTransactionScripRows scripRows : transactionScripRows) {
					JSONObject scripObject = new JSONObject();
					scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
					if (scripRows.getBuyOrSell().equals("B")) {
						scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
					} else
						scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
					scripObject.put(DeviceConstants.PRICE, PriceFormat.priceToRupee(scripRows.getNetrate(), precision));
					scripObject.put(DeviceConstants.QTY, String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty()))));
					
					transactionArray.put(scripObject);
				}
			}

		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return transactionArray;

	}
	
	public static JSONArray getUnRealisedProfitLossTransaction_101(Session session, String maximumSlot, JSONObject transObj)
			throws JSONException, RequestFailedException {

		String userId = session.getUserID();
		
		JSONArray transactionArray = new JSONArray();
		String segmentType = transObj.getString(DeviceConstants.SEGMENT_TYPE);
		String scripCode = "";
		String scripName = "";
		String[] financialYear = DateUtils.getFinancialYear().split("-");
		String currentFinancialYear = financialYear[0].substring(2) + financialYear[1].substring(2);
		if (transObj.has(DeviceConstants.SCRIP_CODE)) {
			scripCode = transObj.getString(DeviceConstants.SCRIP_CODE);
		} else {
			scripName = transObj.getString(DeviceConstants.SCRIP_NAME);
		}
		int precision = 0;
		try {
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)
					|| segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				precision = 2;
			} else {
				precision = 4;
			}
			GetTransactionScripRequest transactionScripRequest = new GetTransactionScripRequest();
			GetTransactionScripResponse transactionScripResponse = new GetTransactionScripResponse();
			transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
			transactionScripRequest.setClientCode(userId);
			if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				GetEquityTrxnScripRequest equityTransactionScripRequest = new GetEquityTrxnScripRequest();
//				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setClientCode(userId);
				equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				equityTransactionScripRequest.setScripName(scripCode);

				equityTransactionScripRequest.setScripName(scripCode);
				if (!maximumSlot.isEmpty()) {
					equityTransactionScripRequest.setMaxSlot(maximumSlot);
				} else {
					equityTransactionScripRequest.setMaxSlot("0");
				}
				GetUnRealisedEquityScripAPI transactionScrip = new GetUnRealisedEquityScripAPI();
				transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
						GetTransactionScripResponse.class, session.getAppID(),"GetUnRealisedEquityTransactionScrip");
				if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					equityTransactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
					transactionScripResponse = transactionScrip.get(equityTransactionScripRequest,
							GetTransactionScripResponse.class, session.getAppID(),"GetUnRealisedEquityTransactionScrip");
				}

				List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

				if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
					for (GetTransactionScripRows scripRows : transactionScripRows) {
						JSONObject scripObject = new JSONObject();
						scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
						if (scripRows.getBuyOrSell().equals("B")) {
							scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
						} else
							scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
						scripObject.put(DeviceConstants.PRICE,
								PriceFormat.priceToRupee(scripRows.getNetrate(), precision));
						int lotsize = AppConfig.getIntValue("currency_min_lot_size");
						if(segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) 
							scripObject.put(DeviceConstants.QTY, 
								String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty()) / lotsize)));
						else
							scripObject.put(DeviceConstants.QTY, 
									String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty()))));
						transactionArray.put(scripObject);
					}
				}
				return transactionArray;

			} else {
				transactionScripRequest.setScripName(scripName);
				transactionScripRequest.setYear(currentFinancialYear);
			}

			transactionScripResponse = performUnRlTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);

			if (transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
				transactionScripRequest.setToken(GCAPIAuthToken.getAuthToken());
				performUnRlTrnsScripAPICall(session, segmentType, transactionScripRequest, transactionScripResponse);
			}

			List<GetTransactionScripRows> transactionScripRows = transactionScripResponse.getDetails();

			if (!transactionScripResponse.getMessage().equalsIgnoreCase(MessageConstants.FAILURE)) {
				for (GetTransactionScripRows scripRows : transactionScripRows) {
					JSONObject scripObject = new JSONObject();
					scripObject.put(DeviceConstants.REPORT_DATE, scripRows.getTrxnDate());
					if (scripRows.getBuyOrSell().equals("B")) {
						scripObject.put(DeviceConstants.TYPE, OrderAction.BUY);
					} else
						scripObject.put(DeviceConstants.TYPE, OrderAction.SELL);
					scripObject.put(DeviceConstants.PRICE, PriceFormat.priceToRupee(scripRows.getNetrate(), precision));
					if(segmentType.equals(DeviceConstants.CURRENCY)) {
						int lotsize = AppConfig.getIntValue("currency_min_lot_size");
						scripObject.put(DeviceConstants.QTY, String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty())/lotsize)));
					}else {
						scripObject.put(DeviceConstants.QTY, String.valueOf(Math.abs(Integer.parseInt(scripRows.getQty()))));
					}
					transactionArray.put(scripObject);
				}
			}

		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return transactionArray;

	}

	public static GetTransactionScripResponse performUnRlTrnsScripAPICall(Session session, String segmentType,
			GetTransactionScripRequest transactionScripRequest, GetTransactionScripResponse transactionScripResponse)
			throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			GetUnRealisedDerivativeScripAPI derivativeTransactionScripAPI = new GetUnRealisedDerivativeScripAPI();
			transactionScripResponse = derivativeTransactionScripAPI.get(transactionScripRequest,
					GetTransactionScripResponse.class, session.getAppID(),"GetUnRealisedDerivativeTransactionScrip");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			GetUnRealisedCurrencyScripAPI currencyTransactionScripAPI = new GetUnRealisedCurrencyScripAPI();
			transactionScripResponse = currencyTransactionScripAPI.get(transactionScripRequest,
					GetTransactionScripResponse.class, session.getAppID(),"GetUnRealisedCurrencyTransactionScrip");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			GetUnRealisedCommodityScripAPI commodityTransactionScripAPI = new GetUnRealisedCommodityScripAPI();
			transactionScripResponse = commodityTransactionScripAPI.get(transactionScripRequest,
					GetTransactionScripResponse.class, session.getAppID(),"GetUnRealisedCommodityTransactionScrip");
		}
		return transactionScripResponse;
	}

	public static JSONArray getFilteredRecords(JSONArray profitLossArray, JSONArray filterBy, String sortOrder,
			String sortBy, JSONObject filterObj) {
		JSONArray filtered = new JSONArray();

		try {

			if(sortBy.isEmpty()) 
        		sortOrder = DeviceConstants.ASCENDING;
			filtered = sort(profitLossArray, sortOrder, sortBy);
			return filtered;

		} catch (Exception e) {
			log.error(e);
		}

		return filtered;
	}

	public static JSONArray sort(JSONArray toBeSortedArray, final String sortOrder, String sortBy) {

		JSONArray sorted = new JSONArray();
		if (sortBy.contains(DeviceConstants.REALISED_PROFIT_LOSS_FILTER)) {
				
				List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
				for (int i = 0; i < toBeSortedArray.length(); i++) {
					toBeSorted.add(toBeSortedArray.getJSONObject(i));
				}
				SortHelper.sortByDouble(DeviceConstants.REALISED_PROFIT_LOSS, toBeSorted,"[,\u20B9 Cr]");
				if (sortOrder.contains(DeviceConstants.ASCENDING))
					sorted = new JSONArray(toBeSorted);
				else {
					Collections.reverse(toBeSorted);
					sorted = new JSONArray(toBeSorted);	
				}
				return sorted;

		} else if (sortBy.contains(DeviceConstants.PROFIT_FILTER)) {
			
			List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < toBeSortedArray.length(); i++) {
				if(Double.parseDouble(toBeSortedArray.getJSONObject(i).getString(DeviceConstants.UNREALISED_PROFIT_LOSS).replaceAll("[,\u20B9 Cr]", "")) > 0.0) {
					toBeSorted.add(toBeSortedArray.getJSONObject(i));
				}
			}
			SortHelper.sortByDouble(DeviceConstants.UNREALISED_PROFIT_LOSS, toBeSorted,"[,\u20B9 Cr]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sorted = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sorted = new JSONArray(toBeSorted);	
			}
			return sorted;
			
		} else if (sortBy.contains(DeviceConstants.LOSS_FILTER)) {
			
			List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < toBeSortedArray.length(); i++) {
				if(Double.parseDouble(toBeSortedArray.getJSONObject(i).getString(DeviceConstants.UNREALISED_PROFIT_LOSS).replaceAll("[,\u20B9 Cr]", "")) < 0.0) 
					toBeSorted.add(toBeSortedArray.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.UNREALISED_PROFIT_LOSS, toBeSorted,"[,\u20B9 Cr]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sorted = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sorted = new JSONArray(toBeSorted);	
			}
			return sorted;
		} else if (sortBy.equalsIgnoreCase(DeviceConstants.QUANTITY)) {
			return SortHelper.sortByInteger(toBeSortedArray, sortOrder, DeviceConstants.BUY_QTY);
		} else if (sortBy.equalsIgnoreCase(DeviceConstants.SYMBOL) || sortBy.isEmpty()) {
			return SortHelper.sortBySymbol(toBeSortedArray, sortOrder);
		} else {
			return toBeSortedArray;
		}

	}
}
