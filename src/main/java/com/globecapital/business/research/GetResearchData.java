package com.globecapital.business.research;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetResearchAPI;
import com.globecapital.api.gc.backoffice.GetResearchRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
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

public class GetResearchData {

	private static Logger log = Logger.getLogger(GetResearchData.class);

	public static JSONArray getResearchData(Session session, String segment) throws GCException, Exception {

		String userId = session.getUserID();
		
		JSONArray researchArray = new JSONArray();
		JSONArray researchData = new JSONArray();
		JSONObject fundamentalObj = new JSONObject();
		JSONObject strategiesObj = new JSONObject();
		JSONObject technicalObj = new JSONObject();

		LinkedHashSet<String> symbolToken = new LinkedHashSet<String>();

		try {

			GetResearchAPI researchAPI = new GetResearchAPI();
			GetResearchRequest researchRequest = new GetResearchRequest();
			String researchResponse;
			if (segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {
				researchRequest.setSegment("1");
			} else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
				researchRequest.setSegment("2");
			} else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
				researchRequest.setSegment("3");
			} else if (segment.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
				researchRequest.setSegment("4");
			}
			researchRequest.setClientCode(userId);
			researchResponse = researchAPI.get(researchRequest, session.getAppID(),"GetResearchData");

			if (researchResponse.length() > 0) {

				JSONArray research = new JSONArray(researchResponse);

				for (int i = 0; i < research.length(); i++) {
					String isinTokenSegmentNSE = "";
					String isinTokenSegmentBSE = "";
					String uniqSym = "";
					String exch = "";
					JSONObject researchObj = research.getJSONObject(i);

					SymbolRow symObj = new SymbolRow();
					JSONObject targetOrExitInfo = new JSONObject();
					if (segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {
						isinTokenSegmentNSE = researchObj.getString(GCConstants.ISIN) + "_"
								+ ExchangeSegment.NSE_SEGMENT_ID;
						isinTokenSegmentBSE = researchObj.getString(GCConstants.ISIN) + "_"
								+ ExchangeSegment.BSE_SEGMENT_ID;
						if (SymbolMap.isValidSymbol(isinTokenSegmentNSE)) {
							symObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentNSE).getMinimisedSymbolRow());
						} else if (SymbolMap.isValidSymbol(isinTokenSegmentBSE)) {
							symObj.extend(SymbolMap.getISINSymbolRow(isinTokenSegmentBSE).getMinimisedSymbolRow());
						}else{
							continue;
						}
					} else if (segment.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
						exch = ExchangeSegment.NFO;
					} else if (segment.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
						exch = ExchangeSegment.NSECDS;
					} else {
						exch = ExchangeSegment.MCX;

					}

					if (!segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {

							uniqSym = getUniqSymDesc(segment, researchObj.getString(DeviceConstants.SCRIPCODE),
									DateUtils
											.formatDate(researchObj.getString(DeviceConstants.EXPIRY),
													DBConstants.EXP_DATE_FORMAT, DBConstants.UNIQ_DESC_DATE_FORMAT)
											.toUpperCase(),
									researchObj.getString(DeviceConstants.OPTION_TYPE),
									researchObj.getString(DeviceConstants.OPTION_PRICE), exch);
							if (!uniqSym.isEmpty()) {
								if (SymbolMap.isValidSymbolUniqDescMap(uniqSym)) {
									symObj.extend(SymbolMap.getSymbolUniqDescRow(uniqSym).getMinimisedSymbolRow());
								} else {
									log.debug("Invalid uniqSym" + uniqSym);
									continue;
								}
							} else {
								continue;
							}
					}
					String sTokenMktSegID = symObj.getJSONObject(SymbolConstants.SYMBOL_OBJ)
							.getString(SymbolConstants.SYMBOL_TOKEN);

					symObj.put(DeviceConstants.SEGMENT_TYPE, researchObj.getString(GCConstants.RESEARCH_SEGMENT));
					symObj.put(DeviceConstants.CALL_ACTION, researchObj.getString(GCConstants.CALL_ACTION));
					symObj.put(DeviceConstants.REPORT_DATE,
							DateUtils.formatResearchDate(researchObj.getString(GCConstants.ENTRY_DATE)).toUpperCase());
					symObj.put(DeviceConstants.CALL_TYPE, researchObj.getString(GCConstants.CALL_TYPE));
					symObj.put(DeviceConstants.ORDER_ACTION,
							OrderAction.formatToDevice(researchObj.getString(GCConstants.BUY_SELL)));
					symObj.put(DeviceConstants.IS_REVISED,
							String.valueOf(researchObj.getBoolean(GCConstants.IS_REVISED)));
					symObj.put(DeviceConstants.ENTRY_PRICE,

							PriceFormat.formatPrice(String.valueOf(researchObj.getFloat(GCConstants.ENTRY_PRICE)),
									symObj.getPrecisionInt(), false));

					symObj.put(DeviceConstants.SL_PRICE, PriceFormat.formatPrice(
							String.valueOf(researchObj.getFloat(GCConstants.SL_PRICE)), symObj.getPrecisionInt(), false));
					symObj.put(DeviceConstants.PARTIAL_GAIN_LOSS,
							PriceFormat.formatPrice(String.valueOf(researchObj.getFloat(GCConstants.PARTIAL_GAIN_LOSS)),
									symObj.getPrecisionInt(), false));
					symObj.put(DeviceConstants.TOTAL_GAIN_LOSS,
							PriceFormat.formatPrice(String.valueOf(researchObj.getFloat(GCConstants.TOTAL_GAIN_LOSS)),
									symObj.getPrecisionInt(), false));
					symObj.put(DeviceConstants.START_DATE,
							DateUtils.formatResearchDate(researchObj.getString(GCConstants.START_DATE)).toUpperCase());
					symObj.put(DeviceConstants.END_DATE,
							DateUtils.formatResearchDate(researchObj.getString(GCConstants.END_DATE)).toUpperCase());

					if (researchObj.getFloat(GCConstants.TARGET_PRICE) > 0) {
						targetOrExitInfo.put(DeviceConstants.TYPE, "Target Price");
						targetOrExitInfo.put(DeviceConstants.VALUE,
								PriceFormat.formatPrice(String.valueOf(researchObj.getFloat(GCConstants.TARGET_PRICE)),
										symObj.getPrecisionInt(), false));
						targetOrExitInfo.put(DeviceConstants.RETURN,
								PriceFormat.formatPrice(String.valueOf(researchObj.getFloat(GCConstants.RETURN)), 
										symObj.getPrecisionInt(), false) + "%");
					} else {
						targetOrExitInfo.put(DeviceConstants.TYPE, "Exit Price");
						targetOrExitInfo.put(DeviceConstants.VALUE,
								PriceFormat.formatPrice(String.valueOf(researchObj.getFloat(GCConstants.EXIT_PRICE)),
										symObj.getPrecisionInt(), false));
						targetOrExitInfo.put(DeviceConstants.RETURN,
								PriceFormat.formatPrice(String.valueOf(researchObj.getFloat(GCConstants.RETURN)), 
										symObj.getPrecisionInt(), false) + "%");
					}
					symObj.put(DeviceConstants.TARGET_EXIT_INFO, targetOrExitInfo);
					JSONArray callArray = new JSONArray();
					JSONArray callHistoryArray = new JSONArray();
					callArray = researchObj.getJSONArray(GCConstants.CALL_HISTORY);
					for (int j = 0; j < callArray.length(); j++) {
						JSONObject callObj = new JSONObject();

						JSONObject callHistoryObj = callArray.getJSONObject(j);
						if (callHistoryObj.has("Action")) {
							callObj.put(DeviceConstants.CALL_ACTION, callHistoryObj.getString("Action"));
						}
						if (callHistoryObj.has("Act")) {
							callObj.put(DeviceConstants.ACTION_DETAILS, callHistoryObj.getString("Act"));
						}
						if (callHistoryObj.has("Date")) {
							callObj.put(DeviceConstants.REPORT_DATE, DateUtils
									.formatResearchHistoryDate(callHistoryObj.getString("Date")).toUpperCase());
						}
						callHistoryArray.put(callObj);

					}
					symObj.put(DeviceConstants.CALL_HISTORY, callHistoryArray);

					if (segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {
						if (researchObj.getString("Categoary").contains("Investment")) {
							symObj.put(DeviceConstants.CATEGORY, DeviceConstants.FUNDAMENTAL);
						} else {
							symObj.put(DeviceConstants.CATEGORY, DeviceConstants.TECHNICAL);
						}
						researchArray.put(symObj);
					} else {
						if (researchObj.getString("Categoary").contains("Trading")) {
							symObj.put(DeviceConstants.CATEGORY, DeviceConstants.TECHNICAL);
						} else {
							symObj.put(DeviceConstants.CATEGORY, DeviceConstants.STRATEGIES);
						}
						researchArray.put(symObj);
					}
					symbolToken.add(sTokenMktSegID);
//					researchArray.put(symObj);
				}
				getQuote(researchArray, symbolToken);

				if (segment.equalsIgnoreCase(DeviceConstants.EQUITY)) {
					fundamentalObj.put(DeviceConstants.TYPE, DeviceConstants.FUNDAMENTAL);
					fundamentalObj.put(DeviceConstants.CALL_RECORDS,
							getCategoryResearchData(researchArray, DeviceConstants.FUNDAMENTAL));
					technicalObj.put(DeviceConstants.TYPE, DeviceConstants.TECHNICAL);
					technicalObj.put(DeviceConstants.CALL_RECORDS,
							getCategoryResearchData(researchArray, DeviceConstants.TECHNICAL));
					researchData.put(fundamentalObj);
					researchData.put(technicalObj);
				} else {
					technicalObj.put(DeviceConstants.TYPE, DeviceConstants.TECHNICAL);
					technicalObj.put(DeviceConstants.CALL_RECORDS,
							getCategoryResearchData(researchArray, DeviceConstants.TECHNICAL));
					strategiesObj.put(DeviceConstants.TYPE, DeviceConstants.STRATEGIES);
					strategiesObj.put(DeviceConstants.CALL_RECORDS,
							getCategoryResearchData(researchArray, DeviceConstants.STRATEGIES));
					researchData.put(technicalObj);
					researchData.put(strategiesObj);
				}

			}
		} catch (Exception e) {
			log.error(e);
			throw new RequestFailedException();
		}
		return researchData;
	}

	public static String getUniqSymDesc(String segment, String symbol, String exp, String optionType,
			String strikePrice, String exch) {
		String uniqSymDesc = "";

		try {

				if (!(strikePrice.isEmpty())) {
					if (Double.parseDouble(strikePrice) > 0) {
						uniqSymDesc = symbol + exp + String.format("%.0f", Double.parseDouble(strikePrice)) + optionType
								+ "_" + exch;
					} else {
						uniqSymDesc = symbol + exp + optionType + "_" + exch;
					}
				} else {
					uniqSymDesc = symbol + exp + optionType + "_" + exch;
					return uniqSymDesc;
				}

		} catch (Exception e) {
			log.info("error" + e.getMessage());
		}
		return uniqSymDesc;
	}

	public static JSONArray getCategoryResearchData(JSONArray research, String type) {

		JSONArray finalResearch = new JSONArray();
		for (int i = 0; i < research.length(); i++) {
			JSONObject researchobj = new JSONObject();
			researchobj = research.getJSONObject(i);
			if (researchobj.getString(DeviceConstants.CATEGORY).contains(type)) {
				finalResearch.put(researchobj);
			}
		}
		return finalResearch;
	}

	public static void getQuote(JSONArray researchList, LinkedHashSet<String> symbolToken) throws SQLException {

		Map<String, QuoteDetails> getQuoteDetails = Quote.getLTP(symbolToken);

		for (int i = 0; i < researchList.length(); i++) {

			SymbolRow researchQuote = (SymbolRow) researchList.getJSONObject(i);
			
			String sSymbolToken = researchQuote.getSymbolToken();
			
			if (getQuoteDetails.containsKey(sSymbolToken)) {
				
				QuoteDetails quoteDetails = getQuoteDetails.get(sSymbolToken);
				researchQuote.put(DeviceConstants.LTP, 
						PriceFormat.priceToRupee(quoteDetails.sLTP, researchQuote.getPrecisionInt()));
				
			} else {
				researchQuote.put(DeviceConstants.LTP, "--");

			}

		}

	}

}
