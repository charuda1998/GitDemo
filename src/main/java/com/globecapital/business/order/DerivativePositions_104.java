package com.globecapital.business.order;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.order.GetNetPositionAPI;
import com.globecapital.api.ft.order.GetNetPositionObject;
import com.globecapital.api.ft.order.GetNetPositionRequest;
import com.globecapital.api.ft.order.GetNetPositionResponse;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionAPI;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionRequest;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionResponse;
import com.globecapital.api.gc.backoffice.GetFOCombinedPositionRows;
import com.globecapital.api.gc.backoffice.GetHoldingsResponse;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.business.report.SortHelper;
import com.globecapital.config.UnitTesting;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.GCUtils;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.log.Logger;

public class DerivativePositions_104 {

	private static Logger log = Logger.getLogger(DerivativePositions_104.class);
	
	private static Map<String, GetFOCombinedPositionResponse> foCombinedPosition = new HashMap<>();
	
	public static JSONObject getDerivativePositions(List<GetNetPositionRows> todayPositionRows, Session session,JSONObject filterObj, ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse)
			throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray positionsList = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		String optedSortBy = filterObj.getString(DeviceConstants.OPTED_SORT_BY);
		String optedSortOrder = filterObj.getString(DeviceConstants.OPTED_SORT_ORDER);
		JSONArray optedFilter = filterObj.getJSONArray(DeviceConstants.OPTED_FILTER);
		if (optedSortOrder.isEmpty())
			optedSortOrder = DeviceConstants.ASCENDING;
		if (optedSortBy.isEmpty() || optedSortOrder.equalsIgnoreCase(DeviceConstants.DEFAULT_ORDER))
			optedSortBy = DeviceConstants.ALPHABETICALLY;

		// GC API call to get the expiry positions with actual buy/sell average prices
		Map<String, JSONObject> mDerivativePositionTokenSegmentAvgPrice = getDerivativePositionFromGCAPI_101(
				session.getUserID(), session.getAppID());

		// FT Today's postion API data parsing
		Map<String, JSONObject> mTodayPositionTokenSegmentAvgPrice = parseTodaysPositionFTAPIData(todayPositionRows);

		// FT API call to get all expiry postions data
		List<GetNetPositionRows> positionRows = getDerivativePositionFromFTAPI(session, servletContext, gcRequest,gcResponse);

		int totalRecordsCount = 0;
		for (int i = 0; i < positionRows.size(); i++) {
			try {

				SymbolRow positions = new SymbolRow();

				GetNetPositionRows positionRow = positionRows.get(i);
				String tokenSegment = positionRow.getToken() + "_" + positionRow.getSegmentId();

				if (positionRow.getInst().equalsIgnoreCase(DeviceConstants.EQUITIES)
						&& SymbolMap.isValidSymbolTokenSegmentMap(tokenSegment) && mTodayPositionTokenSegmentAvgPrice
								.containsKey(tokenSegment + "_" + positionRow.getProdType()))
					totalRecordsCount++;

				if (!positionRow.getInst().equalsIgnoreCase(DeviceConstants.EQUITIES)
						&& SymbolMap.isValidSymbolTokenSegmentMap(tokenSegment)) {

					totalRecordsCount++;
					/*** Position price info ***/
					String segment = positionRow.getSegmentId();
					SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(tokenSegment);
					int precision = tempSymbolRow.getPrecisionInt();

					/*
					 * TODO: Temporavary fix to avoid changes in muliple places
					 */
					String sNetQty = OrderQty.formatToDevice(positionRow.getNetQty(), tempSymbolRow.getLotSizeInt(),
							tempSymbolRow.getMktSegId());
					int iNetQty = Integer.parseInt(sNetQty);
					String sBuyQty = OrderQty.formatToDevice(positionRow.getBuyQty(), tempSymbolRow.getLotSizeInt(),
							tempSymbolRow.getMktSegId());
					int iBuyQty = Integer.parseInt(sBuyQty);
					String sSellQty = OrderQty.formatToDevice(positionRow.getSellQty(), tempSymbolRow.getLotSizeInt(),
							tempSymbolRow.getMktSegId());
					int iSellQty = Integer.parseInt(sSellQty);

					String sAvgNetPrc = "", sAvgBuyPrc = "", sAvgSellPrc = "", sBuyValue = "", sSellValue = "";
					JSONObject priceNRDR = PositionsHelper_101.getPriceNRDR(positionRow);

					if (mDerivativePositionTokenSegmentAvgPrice.containsKey(tokenSegment)
							&& (!positionRow.getProdType().equalsIgnoreCase(ProductType.FT_INTRADAY_FULL_TEXT))) {

						JSONObject derObj = mDerivativePositionTokenSegmentAvgPrice.get(tokenSegment);

						if ((derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.B)
								&& Integer.parseInt(positionRow.getBuyQty()) != 0)
								|| (derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.S)
										&& Integer.parseInt(positionRow.getSellQty()) != 0)) {

							int iTodayBuyQty = 0, iTodaySellQty = 0;
							double dTodayBuyAvg = 0, dTodaySellAvg = 0;

							if (mTodayPositionTokenSegmentAvgPrice
									.containsKey(tokenSegment + "_" + positionRow.getProdType())) {
								JSONObject todayObj = mTodayPositionTokenSegmentAvgPrice
										.get(tokenSegment + "_" + positionRow.getProdType());
								iTodayBuyQty = Integer.parseInt(todayObj.getString(DeviceConstants.BUY_QTY));
								iTodaySellQty = Integer.parseInt(todayObj.getString(DeviceConstants.SELL_QTY));
								dTodayBuyAvg = Double.parseDouble(todayObj.getString(DeviceConstants.BUY_AVG));
								dTodaySellAvg = Double.parseDouble(todayObj.getString(DeviceConstants.SELL_AVG));

							}

							double buyValue, sellValue;

							if (derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.B)) {
								BigDecimal prevBuyPrc = new BigDecimal(derObj.getInt(OrderConstants.QTY))
										.multiply(new BigDecimal(derObj.getString(DeviceConstants.AVG_PRICE)));
								BigDecimal todayBuyPrc = new BigDecimal(String.valueOf(iTodayBuyQty))
										.multiply(new BigDecimal(String.valueOf(dTodayBuyAvg)));
								double buyAvg = (Double.parseDouble(String.valueOf(prevBuyPrc))
										+ Double.parseDouble(String.valueOf(todayBuyPrc))) / iBuyQty;

								sAvgBuyPrc = Double.toString(buyAvg);

								buyValue = Double.parseDouble(String.valueOf(new BigDecimal(
										PriceFormat.formatPrice(sAvgBuyPrc, precision, false).replaceAll(",", ""))
										.multiply(new BigDecimal(String.valueOf(iBuyQty)))));

								buyValue = PositionsHelper_101.formatValuesForDerivatives(buyValue,
										positionRow.getExch(), priceNRDR);
								sBuyValue = Double.toString(buyValue);

								sSellValue = positionRow.getSellValue();
								sellValue = Double.parseDouble(sSellValue);
								sAvgSellPrc = positionRow.getAvgSellPrc();
							} else {
								BigDecimal prevSellPrc = new BigDecimal(derObj.getInt(OrderConstants.QTY))
										.multiply(new BigDecimal(derObj.getString(DeviceConstants.AVG_PRICE)));
								BigDecimal todaySellPrc = new BigDecimal(String.valueOf(iTodaySellQty))
										.multiply(new BigDecimal(String.valueOf(dTodaySellAvg)));
								double sellAvg = (Double.parseDouble(String.valueOf(prevSellPrc))
										+ Double.parseDouble(String.valueOf(todaySellPrc))) / iSellQty;
								sAvgSellPrc = Double.toString(sellAvg);

								sellValue = Double.parseDouble(String.valueOf(new BigDecimal(
										PriceFormat.formatPrice(sAvgSellPrc, precision, false).replaceAll(",", ""))
										.multiply(new BigDecimal(String.valueOf(iSellQty)))));

								sellValue = PositionsHelper_101.formatValuesForDerivatives(sellValue,
										positionRow.getExch(), priceNRDR);
								sSellValue = Double.toString(sellValue);

								sBuyValue = positionRow.getBuyValue();
								buyValue = Double.parseDouble(sBuyValue);
								sAvgBuyPrc = positionRow.getAvgBuyPrc();

							}

							double netAvg = 0.0;

							if (iNetQty != 0) {
								double dNetQty = PositionsHelper_101.formatValuesForDerivatives(Double.valueOf(iNetQty),
										positionRow.getExch(), priceNRDR);
								BigDecimal buyVal = new BigDecimal(Double.toString(buyValue));
								BigDecimal sellVal = new BigDecimal(Double.toString(sellValue));
								netAvg = Double.parseDouble(String.valueOf(buyVal.subtract(sellVal))) / dNetQty;
							}

							sAvgNetPrc = Double.toString(netAvg);
						} else {
							sAvgNetPrc = positionRow.getAvgNetPrc();
							sAvgBuyPrc = positionRow.getAvgBuyPrc();
							sBuyValue = positionRow.getBuyValue();
							sAvgSellPrc = positionRow.getAvgSellPrc();
							sSellValue = positionRow.getSellValue();
						}
					} else {
						sAvgNetPrc = positionRow.getAvgNetPrc();
						sAvgBuyPrc = positionRow.getAvgBuyPrc();
						sBuyValue = positionRow.getBuyValue();
						sAvgSellPrc = positionRow.getAvgSellPrc();
						sSellValue = positionRow.getSellValue();
					}

					/************************************************** */
					positions.extend(tempSymbolRow.getMinimisedSymbolRow());
					linkedsetSymbolToken.add(tokenSegment);

					// TODO: Profit and Loss calculation
					Double netQty = Double.parseDouble(sNetQty);
					Double avgNetPrice = Double.parseDouble(sAvgNetPrc);

					int lotsize = tempSymbolRow.getLotSizeInt();

					if (positionRow.getExch().equals(ExchangeSegment.NSECDS)) {
						positions.put(DeviceConstants.DISP_QTY,
								PriceFormat.addComma(Integer.parseInt(sNetQty) / lotsize));
						positions.put(OrderConstants.QTY,
								Integer.toString(PositionsHelper_101.getQty(sNetQty) / lotsize));
						positions.put(DeviceConstants.SELL_QTY, Integer.toString(iSellQty / lotsize));
						positions.put(DeviceConstants.BUY_QTY, Integer.toString(iBuyQty / lotsize));
					} else {
						positions.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(Integer.parseInt(sNetQty)));
						positions.put(OrderConstants.QTY, Integer.toString(PositionsHelper_101.getQty(sNetQty)));
						positions.put(DeviceConstants.SELL_QTY, sSellQty);
						positions.put(DeviceConstants.BUY_QTY, sBuyQty);
					}
					positions.put(OrderConstants.AVG_PRICE, PriceFormat.formatPrice(sAvgNetPrc, precision, false));
					positions.put(DeviceConstants.BOD_QTY, "--");
					positions.put(DeviceConstants.BOD_RATE, "--");
					positions.put(DeviceConstants.BOD_VALUE, "--");
					positions.put(DeviceConstants.BUY_AVG, PriceFormat.formatPrice(sAvgBuyPrc, precision, false));
					positions.put(DeviceConstants.BUY_VALUE, PriceFormat.formatPrice(sBuyValue, precision, false));
					positions.put(DeviceConstants.SELL_AVG, PriceFormat.formatPrice(sAvgSellPrc, precision, false));
					positions.put(DeviceConstants.SELL_VALUE, PriceFormat.formatPrice(sSellValue, precision, false));
					String sProductType = ProductType.formatToDisplay2(positionRow.getProdType(), segment);
					positions.put(OrderConstants.PRODUCT_TYPE, sProductType);
					positions.put(OrderConstants.DISP_PRODUCT_TYPE, sProductType);
					// positions.put(DeviceConstants.OPEN_VALUE,
					// PriceFormat.formatPrice(String.valueOf(iNetQty * avgNetPrice), precision,
					// false));
					positions.put(DeviceConstants.CONVERTABLE_TYPES,
							ProductType.getConvertableProductTypes(positionRow.getProdType(), segment));
					positions.put(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
					positions.put(OrderConstants.VALIDITY, Validity.DAY); // For
																			// Square-off
																			// and
																			// Buy
																			// more
																			// navigation
					positions.put(OrderConstants.DISC_QTY, "--");

					positions.put(DeviceConstants.IS_SQUARE_OFF,
							PositionsHelper_101.isSquareOff(netQty, positionRow.getProdType()));
					positions.put(DeviceConstants.IS_CONVERT,
							PositionsHelper_101.isSquareOff(netQty, positionRow.getProdType()));
					positions.put(DeviceConstants.IS_BUY_MORE, PositionsHelper_101.isBuyMore(netQty));
					positions.put(DeviceConstants.IS_SELL_MORE, PositionsHelper_101.isSellMore(netQty));
					positions.put(OrderConstants.ORDER_ACTION, PositionsHelper_101.getOrderAction(netQty));
					positions.put(OrderConstants.TO_CONVERT_ACTION, PositionsHelper_101.getConvertOrderAction(netQty));
					positions.put(OrderConstants.NR_DR, priceNRDR);
					positions.put(DeviceConstants.INSTRUMENT, tempSymbolRow.getInstrument());
					positions.put(DeviceConstants.EXCH, tempSymbolRow.getExchange());
					positionsList.put(positions);

				}
				finalObj.put(DeviceConstants.TOTAL_COUNT, totalRecordsCount);

			} catch (Exception e) {

				log.error(e);
			}
		} // End of for loop

		int recordCount = positionsList.length();
		PositionsHelper_101.getMarketValueAndPLUsingLTP_101(positionsList, linkedsetSymbolToken);
		positionsList = sort(filterPositions(positionsList, optedFilter), optedSortOrder, optedSortBy);
		finalObj.put(DeviceConstants.POSITION_LIST, positionsList);
		JSONObject summaryObj = PositionsHelper_101.makeSummaryObjectForPositions(positionsList);
		summaryObj.put(DeviceConstants.RECORDS_COUNT, String.valueOf(recordCount));
		finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);

		return finalObj;
	}

	private static Map<String, JSONObject> parseTodaysPositionFTAPIData(List<GetNetPositionRows> todayPositionRows) {
		Map<String, JSONObject> todayPositionTokenSegmentAvgPrice = new HashMap<>();

		for (int i = 0; i < todayPositionRows.size(); i++) {
			GetNetPositionRows positionRow = todayPositionRows.get(i);
			if (SymbolMap.isValidSymbolTokenSegmentMap(positionRow.getToken() + "_" + positionRow.getSegmentId())) {

				SymbolRow symRow = SymbolMap.getSymbolRow(positionRow.getToken() + "_" + positionRow.getSegmentId());
				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.BUY_AVG, positionRow.getAvgBuyPrc());
				obj.put(DeviceConstants.BUY_QTY,
						OrderQty.formatToDevice(positionRow.getBuyQty(), symRow.getLotSizeInt(), symRow.getMktSegId()));
				obj.put(DeviceConstants.SELL_AVG, positionRow.getAvgSellPrc());
				obj.put(DeviceConstants.SELL_QTY, OrderQty.formatToDevice(positionRow.getSellQty(),
						symRow.getLotSizeInt(), symRow.getMktSegId()));
				if (!positionRow.getProdType().equalsIgnoreCase(ProductType.FT_INTRADAY_FULL_TEXT)) {
					todayPositionTokenSegmentAvgPrice.put(symRow.getSymbolToken() + "_" + positionRow.getProdType(),
							obj);
				}

			}
		}

		return todayPositionTokenSegmentAvgPrice;

	}

	private static List<GetNetPositionRows> getDerivativePositionFromFTAPI(Session session, ServletContext servletContext, GCRequest gcRequest, GCResponse gcResponse) 
			throws Exception {

		GetNetPositionRequest positionRequest = new GetNetPositionRequest();
		positionRequest.setUserID(session.getUserID());
		positionRequest.setGroupId(session.getGroupId());
		positionRequest.setJKey(session.getjKey());
		positionRequest.setJSession(session.getjSessionID());
		positionRequest.setPosType(AppConstants.STR_ONE);

		GetNetPositionAPI positionApi = new GetNetPositionAPI();
		GetNetPositionResponse positionResponse = new GetNetPositionResponse();
		try {
			positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class, session.getAppID(),
					"GetNetPosition");
		} catch (GCException e) {
			log.debug(e);
			if (e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
				if (GCUtils.reInitiateLogIn(positionRequest, session, servletContext, gcRequest, gcResponse)) {
					positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class,
							session.getAppID(), "GetNetPosition");
					session = gcRequest.getSession();
				} else {
					throw new GCException(InfoIDConstants.INVALID_SESSION, "User logged out successfully");
				}
			} else
				throw new RequestFailedException();
		}
		GetNetPositionObject positionObj = positionResponse.getResponseObject();
		List<GetNetPositionRows> positionRows = positionObj.getObjJSONRows();

		return positionRows;

	}

	private static Map<String, JSONObject> getDerivativePositionFromGCAPI(String sUserID, String sAppID)
			throws JSONException, GCException, ParseException {

		Map<String, JSONObject> derivativePositionTokenSegmentAvgPrice = new HashMap<>();

		GetFOCombinedPositionRequest positionRequest = new GetFOCombinedPositionRequest();
		positionRequest.setToken(GCAPIAuthToken.getAuthToken());
		positionRequest.setClientCode(sUserID);
		GetFOCombinedPositionAPI positionsApi = new GetFOCombinedPositionAPI();
		GetFOCombinedPositionResponse positionsResponse = null;

		try {
			if (foCombinedPosition.containsKey(sUserID)) {
				positionsResponse = foCombinedPosition.get(sUserID);
			} else {
				String positions = PositionsDB.toCheckFoCombinedPositionEntry(sUserID);
				if (positions != null) {
					positionsResponse = new Gson().fromJson(positions, GetFOCombinedPositionResponse.class);
					foCombinedPosition.put(sUserID, positionsResponse);
				} else {
					positionsResponse = positionsApi.get(positionRequest, GetFOCombinedPositionResponse.class, sAppID,
							DeviceConstants.GC_POSITION_L);
					if (positionsResponse.getStatus().equalsIgnoreCase("true")) {
						PositionsDB.updatePositionsDB(sUserID, new Gson().toJson(positionsResponse));
						foCombinedPosition.put(sUserID, positionsResponse);
					}
					if (positionsResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
						positionRequest.setToken(GCAPIAuthToken.getAuthToken());
						positionsResponse = positionsApi.get(positionRequest, GetFOCombinedPositionResponse.class,
								sAppID, DeviceConstants.GC_POSITION_L);
						if (positionsResponse.getStatus().equalsIgnoreCase("true")) {
							PositionsDB.updatePositionsDB(sUserID, new Gson().toJson(positionsResponse));
							foCombinedPosition.put(sUserID, positionsResponse);
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.debug(e);
			throw new RequestFailedException();
		} catch (Exception e) {
			log.debug(e);
			throw new RequestFailedException();
		}

		List<GetFOCombinedPositionRows> positionRows = new ArrayList<>();

		if (positionsResponse.getStatus().equalsIgnoreCase(DeviceConstants.FALSE))
			return derivativePositionTokenSegmentAvgPrice;

		positionRows = positionsResponse.getDetails();

		for (int i = 0; i < positionRows.size(); i++) {
			GetFOCombinedPositionRows positionRow = positionRows.get(i);
			String uniqSym = getUniqSymDesc(positionRow.getScripName(), positionRow.getSegments(),
					positionRow.getExchange(), positionRow.getProductType());

			if (SymbolMap.isValidSymbolUniqDescMap(uniqSym)) {

				SymbolRow symRow = SymbolMap.getSymbolUniqDescRow(uniqSym);

				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.BUY_OR_SELL, positionRow.getBuySell());
				obj.put(DeviceConstants.AVG_PRICE, positionRow.getPrice());
				obj.put(OrderConstants.QTY, Integer.parseInt(positionRow.getQty()));
				derivativePositionTokenSegmentAvgPrice.put(symRow.getSymbolToken(), obj);

			}
		}

		return derivativePositionTokenSegmentAvgPrice;
	}

	private static Map<String, JSONObject> getDerivativePositionFromGCAPI_101(String sUserID, String sAppID)
			throws JSONException, GCException, ParseException {

		Map<String, JSONObject> derivativePositionTokenSegmentAvgPrice = new HashMap<>();
		GetFOCombinedPositionRequest positionRequest = new GetFOCombinedPositionRequest();
		positionRequest.setToken(GCAPIAuthToken.getAuthToken());
		positionRequest.setClientCode(sUserID);
		GetFOCombinedPositionAPI positionsApi = new GetFOCombinedPositionAPI();
		GetFOCombinedPositionResponse positionsResponse = null;

		try {
			if (foCombinedPosition.containsKey(sUserID)) {
				positionsResponse = foCombinedPosition.get(sUserID);
			} else {
				String positions = PositionsDB.toCheckFoCombinedPositionEntry(sUserID);
				if (positions != null) {
					positionsResponse = new Gson().fromJson(positions, GetFOCombinedPositionResponse.class);
					foCombinedPosition.put(sUserID, positionsResponse);
				} else {
					positionsResponse = positionsApi.get(positionRequest, GetFOCombinedPositionResponse.class, sAppID,
							DeviceConstants.GC_POSITION_L);
					if (positionsResponse.getStatus().equalsIgnoreCase("true")) {
						PositionsDB.updatePositionsDB(sUserID, new Gson().toJson(positionsResponse));
						foCombinedPosition.put(sUserID, positionsResponse);
					}
					if (positionsResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
						positionRequest.setToken(GCAPIAuthToken.getAuthToken());
						positionsResponse = positionsApi.get(positionRequest, GetFOCombinedPositionResponse.class,
								sAppID, DeviceConstants.GC_POSITION_L);
						if (positionsResponse.getStatus().equalsIgnoreCase("true")) {
							PositionsDB.updatePositionsDB(sUserID, new Gson().toJson(positionsResponse));
							foCombinedPosition.put(sUserID, positionsResponse);
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.debug(e);
			throw new RequestFailedException();
		} catch (Exception e) {
			log.debug(e);
			throw new RequestFailedException();
		}

		List<GetFOCombinedPositionRows> positionRows = new ArrayList<>();

		if (positionsResponse.getStatus().equalsIgnoreCase(DeviceConstants.FALSE))
			return derivativePositionTokenSegmentAvgPrice;

		positionRows = positionsResponse.getDetails();

		for (int i = 0; i < positionRows.size(); i++) {
			GetFOCombinedPositionRows positionRow = positionRows.get(i);
			String uniqSym = getUniqSymDesc(positionRow.getScripName(), positionRow.getSegments(),
					positionRow.getExchange(), positionRow.getProductType());

			if (SymbolMap.isValidSymbolUniqDescMap(uniqSym)) {

				SymbolRow symRow = SymbolMap.getSymbolUniqDescRow(uniqSym);

				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.BUY_OR_SELL, positionRow.getBuySell());
				obj.put(DeviceConstants.AVG_PRICE, positionRow.getPrice());
				if (symRow.getExchange().equalsIgnoreCase(ExchangeSegment.NSECDS))
					obj.put(OrderConstants.QTY, Integer.parseInt(positionRow.getQty()) / symRow.getDispLotSizeInt());
				else
					obj.put(OrderConstants.QTY, Integer.parseInt(positionRow.getQty()));
				derivativePositionTokenSegmentAvgPrice.put(symRow.getSymbolToken(), obj);

			}
		}

		return derivativePositionTokenSegmentAvgPrice;
	}
	
	private static String getUniqSymDesc(String scripName, String segments, String exchange, String productType)
			throws ParseException {

		String[] arrScripName = scripName.split(" ");

		String sScripName = arrScripName[0];
		String sExpiryDate = DateUtils
				.formatDate(arrScripName[1], DeviceConstants.EXPIRY_DATE_FORMAT, DBConstants.UNIQ_DESC_DATE_FORMAT)
				.toUpperCase();
		String sStrikePrice = "", sOptionType = "";

		String sExchange = getExchange(segments, exchange);

		if (arrScripName.length == 4) {
			sOptionType = arrScripName[2];
			sStrikePrice = arrScripName[3];
		} else if (arrScripName.length == 3)
			sOptionType = arrScripName[2];

		if (productType.equalsIgnoreCase(GCConstants.FUTURE)) {
			productType = "FUT";
			sOptionType = "";
		} else
			productType = "";

		return sScripName + sExpiryDate + productType + sStrikePrice + sOptionType + "_" + sExchange;
	}

	private static String getExchange(String segments, String exchange) {

		if (segments.equalsIgnoreCase(GCConstants.EQUITY_FO))
			return ExchangeSegment.NFO;
		else if (segments.equalsIgnoreCase(GCConstants.COMMODITY_POSITION)) {
			if (exchange.equalsIgnoreCase(ExchangeSegment.MCX))
				return ExchangeSegment.MCX;
			else
				return ExchangeSegment.NCDEX;
		} else if (segments.equalsIgnoreCase(GCConstants.CURRENCY)) {
			if (exchange.equalsIgnoreCase(ExchangeSegment.NSE))
				return ExchangeSegment.NSECDS;
			else
				return ExchangeSegment.BSECDS;
		}
		return exchange;

	}

	public static JSONArray sort(JSONArray positionsList, final String sortOrder, String sortBy) {

		JSONArray sortedArray = new JSONArray();

		if (sortBy.contains(DeviceConstants.PROFIT_LOSS_PERCENTAGE)) {

			List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < positionsList.length(); i++) {
				if (!(positionsList.getJSONObject(i).getString(DeviceConstants.PNL_PERCENT).equals("--")
						|| positionsList.getJSONObject(i).getString(DeviceConstants.PNL_PERCENT).equals("NA")))
					toBeSorted.add(positionsList.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.PNL_PERCENT, toBeSorted, "[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sortedArray = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sortedArray = new JSONArray(toBeSorted);
			}
			return sortedArray;

		} else if (sortBy.contains(DeviceConstants.PROFIT_LOSS_ABSOLUTE)) {

			List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < positionsList.length(); i++) {
				if (!(positionsList.getJSONObject(i).getString(DeviceConstants.PROFIT_AND_LOSS).equals("--")
						|| positionsList.getJSONObject(i).getString(DeviceConstants.PROFIT_AND_LOSS).equals("NA")))
					toBeSorted.add(positionsList.getJSONObject(i));
			}
			SortHelper.sortByDouble(DeviceConstants.PROFIT_AND_LOSS, toBeSorted, "[,\u20B9]");
			if (sortOrder.contains(DeviceConstants.ASCENDING))
				sortedArray = new JSONArray(toBeSorted);
			else {
				Collections.reverse(toBeSorted);
				sortedArray = new JSONArray(toBeSorted);
			}
			return sortedArray;

		} else if (sortBy.equalsIgnoreCase(DeviceConstants.QUANTITY))
			return SortHelper.sortByInteger(positionsList, sortOrder, DeviceConstants.DISP_QTY, ",");

		else if (sortBy.contains(DeviceConstants.ALPHABETICALLY) || sortBy.isEmpty()) {
			List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
			for (int i = 0; i < positionsList.length(); i++)
				toBeSorted.add(positionsList.getJSONObject(i));
			Collections.sort(toBeSorted, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {
					if (sortOrder.equalsIgnoreCase(DeviceConstants.ASCENDING))
						return obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
								.compareTo(obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ)
										.getString(DeviceConstants.SYMBOL));
					else
						return obj2.getJSONObject(SymbolConstants.SYMBOL_OBJ).getString(DeviceConstants.SYMBOL)
								.compareTo(obj1.getJSONObject(SymbolConstants.SYMBOL_OBJ)
										.getString(DeviceConstants.SYMBOL));
				}
			});
			sortedArray = new JSONArray(toBeSorted);
			return sortedArray;
		} else {
			return positionsList;
		}
	}

	public static JSONArray filterPositions(JSONArray positionsList, JSONArray filterArray) {

		List<String> filterItems = new ArrayList<>();
		for (int i = 0; i < filterArray.length(); i++)
			filterItems.add(filterArray.getString(i));

		if (filterItems.isEmpty() || (filterItems.contains(DeviceConstants.FUTURE)
				&& filterItems.contains(DeviceConstants.POSITIONS_OPTIONS)
				&& filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY)))
			return positionsList;
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS)
				&& filterItems.contains(DeviceConstants.CURRENCY))
			return getFilteredHoldings(true, true, true, false, positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.POSITIONS_OPTIONS)
				&& filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(true, true, false, true, positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.COMMODITY)
				&& filterItems.contains(DeviceConstants.CURRENCY))
			return getFilteredHoldings(true, false, true, true, positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS)
				&& filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false, true, true, true, positionsList);
		else if (filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.FUTURE))
			return getFilteredHoldings(true, false, true, false, positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(true, false, false, true, positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS)
				&& filterItems.contains(DeviceConstants.FUTURE))
			return getFilteredHoldings(true, true, false, false, positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS)
				&& filterItems.contains(DeviceConstants.CURRENCY))
			return getFilteredHoldings(false, true, true, false, positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS)
				&& filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false, true, false, true, positionsList);
		else if (filterItems.contains(DeviceConstants.CURRENCY) && filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false, false, true, true, positionsList);
		else if (filterItems.contains(DeviceConstants.CURRENCY))
			return getFilteredHoldings(false, false, true, false, positionsList);
		else if (filterItems.contains(DeviceConstants.COMMODITY))
			return getFilteredHoldings(false, false, false, true, positionsList);
		else if (filterItems.contains(DeviceConstants.FUTURE))
			return getFilteredHoldings(true, false, false, false, positionsList);
		else if (filterItems.contains(DeviceConstants.POSITIONS_OPTIONS))
			return getFilteredHoldings(false, true, false, false, positionsList);
		return positionsList;
	}

	private static JSONArray getFilteredHoldings(boolean showFNOFuture, boolean showFNOOption, boolean showCurrency,
			boolean showCommodity, JSONArray positionsList) {
		JSONArray finalArray = new JSONArray();
		for (int i = 0; i < positionsList.length(); i++) {
			JSONObject obj = positionsList.getJSONObject(i);
			String instrument = obj.getString(DeviceConstants.INSTRUMENT);
			String exch = obj.getString(DeviceConstants.EXCH);
			obj.remove(DeviceConstants.INSTRUMENT);
			obj.remove(DeviceConstants.EXCH);
			if (showFNOFuture && exch.equals(ExchangeSegment.NFO) && InstrumentType.isFutures(instrument)) {
				finalArray.put(obj);
			} else if (showFNOOption && exch.equals(ExchangeSegment.NFO) && InstrumentType.isOptions(instrument)) {
				finalArray.put(obj);
			} else if (showCurrency && exch.equals(ExchangeSegment.NSECDS)) {
				finalArray.put(obj);
			} else if (showCommodity && exch.equals(ExchangeSegment.MCX)) {
				finalArray.put(obj);
			}

		}
		return finalArray;
	}
	
	public static JSONObject getDerivativePositions_101(List<GetNetPositionRows> todayPositionRows,List<GetNetPositionRows> positionRows ,Session session, JSONObject filterObj)
            throws JSONException, GCException, ParseException, org.json.me.JSONException, SQLException {
        JSONObject finalObj = new JSONObject();
        JSONArray positionsList = new JSONArray();
        LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
        String optedSortBy = filterObj.getString(DeviceConstants.OPTED_SORT_BY);
        String optedSortOrder = filterObj.getString(DeviceConstants.OPTED_SORT_ORDER);
        JSONArray optedFilter = filterObj.getJSONArray(DeviceConstants.OPTED_FILTER);
        if(optedSortOrder.isEmpty())
            optedSortOrder = DeviceConstants.ASCENDING;
        if(optedSortBy.isEmpty() || optedSortOrder.equalsIgnoreCase(DeviceConstants.DEFAULT_ORDER))
            optedSortBy = DeviceConstants.ALPHABETICALLY;
        
        // GC API call to get the expiry positions with actual buy/sell average prices
        Map<String, JSONObject> mDerivativePositionTokenSegmentAvgPrice = getDerivativePositionFromGCAPI(session.getUserID(),
                session.getAppID());
                
        //FT Today's postion API data parsing 
        Map<String, JSONObject> mTodayPositionTokenSegmentAvgPrice = parseTodaysPositionFTAPIData(todayPositionRows);
        
        //FT API call to get all expiry postions data
//      List<GetNetPositionRows> positionRows = getDerivativePositionFromFTAPI(session);
        int totalRecordsCount = 0;
        for (int i = 0; i < positionRows.size(); i++) {
            try {

                SymbolRow positions = new SymbolRow();

                GetNetPositionRows positionRow = positionRows.get(i);
                String tokenSegment = positionRow.getToken() + "_" + positionRow.getSegmentId();

                if(positionRow.getInst().equalsIgnoreCase(DeviceConstants.EQUITIES) && SymbolMap.isValidSymbolTokenSegmentMap(tokenSegment) && mTodayPositionTokenSegmentAvgPrice.containsKey(tokenSegment+"_"+positionRow.getProdType()))
                    totalRecordsCount++;
                
                if (!positionRow.getInst().equalsIgnoreCase(DeviceConstants.EQUITIES)
                        && SymbolMap.isValidSymbolTokenSegmentMap(tokenSegment)) {

                    totalRecordsCount++;
                    /*** Position price info ***/
                    String segment = positionRow.getSegmentId();
                    SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(tokenSegment);
                    int precision = tempSymbolRow.getPrecisionInt();

                    /*
                     * TODO: Temporavary fix to avoid changes in muliple places
                     */
                    String sNetQty = OrderQty.formatToDevice(positionRow.getNetQty(), tempSymbolRow.getLotSizeInt(),
                            tempSymbolRow.getMktSegId());
                    int iNetQty = Integer.parseInt(sNetQty);
                    String sBuyQty = OrderQty.formatToDevice(positionRow.getBuyQty(), tempSymbolRow.getLotSizeInt(),
                            tempSymbolRow.getMktSegId());
                    int iBuyQty = Integer.parseInt(sBuyQty);
                    String sSellQty = OrderQty.formatToDevice(positionRow.getSellQty(), tempSymbolRow.getLotSizeInt(),
                            tempSymbolRow.getMktSegId());
                    int iSellQty = Integer.parseInt(sSellQty);
                    
                    String sAvgNetPrc = "", sAvgBuyPrc = "", sAvgSellPrc = "", sBuyValue = "", sSellValue = "";
                    JSONObject priceNRDR = PositionsHelper_101.getPriceNRDR(positionRow);

                    if (mDerivativePositionTokenSegmentAvgPrice.containsKey(tokenSegment) && (!positionRow.getProdType().equalsIgnoreCase(ProductType.FT_INTRADAY_FULL_TEXT))) {
                        
                        JSONObject derObj = mDerivativePositionTokenSegmentAvgPrice.get(tokenSegment);
                        
                        if((derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.B)
                                && Integer.parseInt(positionRow.getBuyQty()) != 0)
                                ||
                            (derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.S)
                                && Integer.parseInt(positionRow.getSellQty()) != 0))
                        {
                            
                        
                            int iTodayBuyQty = 0, iTodaySellQty =0;
                            double dTodayBuyAvg = 0, dTodaySellAvg = 0;
                        
                            if(mTodayPositionTokenSegmentAvgPrice.containsKey(tokenSegment+"_"+positionRow.getProdType()))
                            {
                                JSONObject todayObj = mTodayPositionTokenSegmentAvgPrice.get(tokenSegment+"_"+positionRow.getProdType());
                                iTodayBuyQty = Integer.parseInt(todayObj.getString(DeviceConstants.BUY_QTY));
                                iTodaySellQty = Integer.parseInt(todayObj.getString(DeviceConstants.SELL_QTY));
                                dTodayBuyAvg = Double.parseDouble(todayObj.getString(DeviceConstants.BUY_AVG));
                                dTodaySellAvg = Double.parseDouble(todayObj.getString(DeviceConstants.SELL_AVG));
                            
                            }
                            
                            double buyValue, sellValue;
                        
                            if(derObj.getString(DeviceConstants.BUY_OR_SELL).equalsIgnoreCase(GCConstants.B))
                            {
                                BigDecimal prevBuyPrc = new BigDecimal(derObj.getInt(OrderConstants.QTY)).multiply(new BigDecimal(derObj.getString(DeviceConstants.AVG_PRICE)));
                                BigDecimal todayBuyPrc = new BigDecimal(String.valueOf(iTodayBuyQty)).multiply(new BigDecimal(String.valueOf(dTodayBuyAvg)));
                                double buyAvg = (Double.parseDouble(String.valueOf(prevBuyPrc)) + Double.parseDouble(String.valueOf(todayBuyPrc))) / iBuyQty;
                                
                                sAvgBuyPrc = Double.toString(buyAvg);
                            
                                buyValue = Double.parseDouble(String.valueOf(new BigDecimal(PriceFormat.formatPrice(sAvgBuyPrc, precision, false)
                                        .replaceAll(",", "")).multiply(new BigDecimal(String.valueOf(iBuyQty)))));

                                buyValue = PositionsHelper_101.formatValuesForDerivatives(buyValue, positionRow.getExch(), priceNRDR);
                                sBuyValue = Double.toString(buyValue);
                            
                                sSellValue = positionRow.getSellValue();
                                sellValue = Double.parseDouble(sSellValue);
                                sAvgSellPrc = positionRow.getAvgSellPrc();
                            }
                            else
                            {
                                BigDecimal prevSellPrc = new BigDecimal(derObj.getInt(OrderConstants.QTY)).multiply(new BigDecimal(derObj.getString(DeviceConstants.AVG_PRICE)));
                                BigDecimal todaySellPrc = new BigDecimal(String.valueOf(iTodaySellQty)).multiply(new BigDecimal(String.valueOf(dTodaySellAvg)));
                                double sellAvg = (Double.parseDouble(String.valueOf(prevSellPrc)) + Double.parseDouble(String.valueOf(todaySellPrc))) / iSellQty;
                                sAvgSellPrc = Double.toString(sellAvg);
                            
                                sellValue = Double.parseDouble(String.valueOf(new BigDecimal(PriceFormat.formatPrice(sAvgSellPrc, precision, false)
                                        .replaceAll(",", "")).multiply(new BigDecimal(String.valueOf(iSellQty)))));

                                sellValue = PositionsHelper_101.formatValuesForDerivatives(sellValue, positionRow.getExch(), priceNRDR);
                                sSellValue = Double.toString(sellValue);
                            
                                sBuyValue = positionRow.getBuyValue();
                                buyValue = Double.parseDouble(sBuyValue);
                                sAvgBuyPrc = positionRow.getAvgBuyPrc();
                            
                            }
                        
                            double netAvg = 0.0;
                        
                            if(iNetQty != 0) {
                                double dNetQty = PositionsHelper_101.formatValuesForDerivatives(Double.valueOf(iNetQty), positionRow.getExch(), priceNRDR);
                                BigDecimal buyVal = new BigDecimal(Double.toString(buyValue));
                                BigDecimal sellVal = new BigDecimal(Double.toString(sellValue));
                                netAvg = Double.parseDouble(String.valueOf(buyVal.subtract(sellVal))) / dNetQty;
                            }

                            sAvgNetPrc = Double.toString(netAvg);
                        } else
                        {
                            sAvgNetPrc = positionRow.getAvgNetPrc();
                            sAvgBuyPrc = positionRow.getAvgBuyPrc();
                            sBuyValue = positionRow.getBuyValue();
                            sAvgSellPrc = positionRow.getAvgSellPrc();
                            sSellValue = positionRow.getSellValue();
                        }
                    } else
                    {
                        sAvgNetPrc = positionRow.getAvgNetPrc();
                        sAvgBuyPrc = positionRow.getAvgBuyPrc();
                        sBuyValue = positionRow.getBuyValue();
                        sAvgSellPrc = positionRow.getAvgSellPrc();
                        sSellValue = positionRow.getSellValue();
                    }


                    /************************************************** */
                    positions.extend(tempSymbolRow.getMinimisedSymbolRow());
                    linkedsetSymbolToken.add(tokenSegment);

                    // TODO: Profit and Loss calculation
                    Double netQty = Double.parseDouble(sNetQty);
                    Double avgNetPrice = Double.parseDouble(sAvgNetPrc);
                    
                    int lotsize = tempSymbolRow.getLotSizeInt();
                    
                    if(positionRow.getExch().equals(ExchangeSegment.NSECDS)) {
                        positions.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(Integer.parseInt(sNetQty) / lotsize));
                        positions.put(OrderConstants.QTY, Integer.toString(PositionsHelper_101.getQty(sNetQty) / lotsize));
                        positions.put(DeviceConstants.SELL_QTY, Integer.toString(iSellQty / lotsize));
                        positions.put(DeviceConstants.BUY_QTY, Integer.toString(iBuyQty / lotsize));
                    }else {
                        positions.put(DeviceConstants.DISP_QTY, PriceFormat.addComma(Integer.parseInt(sNetQty)));
                        positions.put(OrderConstants.QTY, Integer.toString(PositionsHelper_101.getQty(sNetQty)));
                        positions.put(DeviceConstants.SELL_QTY, sSellQty);
                        positions.put(DeviceConstants.BUY_QTY, sBuyQty);
                    }
                    positions.put(OrderConstants.AVG_PRICE, PriceFormat.formatPrice(sAvgNetPrc, precision, false));
                    positions.put(DeviceConstants.BOD_QTY, "--");
                    positions.put(DeviceConstants.BOD_RATE, "--");
                    positions.put(DeviceConstants.BOD_VALUE, "--");
                    positions.put(DeviceConstants.BUY_AVG, PriceFormat.formatPrice(sAvgBuyPrc, precision, false));
                    positions.put(DeviceConstants.BUY_VALUE, PriceFormat.formatPrice(sBuyValue, precision, false));
                    positions.put(DeviceConstants.SELL_AVG, PriceFormat.formatPrice(sAvgSellPrc, precision, false));
                    positions.put(DeviceConstants.SELL_VALUE, PriceFormat.formatPrice(sSellValue, precision, false));
                    String sProductType = ProductType.formatToDisplay2(positionRow.getProdType(), segment);
                    positions.put(OrderConstants.PRODUCT_TYPE, sProductType);
                    positions.put(OrderConstants.DISP_PRODUCT_TYPE, sProductType);
                //  positions.put(DeviceConstants.OPEN_VALUE,
                //          PriceFormat.formatPrice(String.valueOf(iNetQty * avgNetPrice), precision, false));
                    positions.put(DeviceConstants.CONVERTABLE_TYPES,
                            ProductType.getConvertableProductTypes(positionRow.getProdType(), segment));
                    positions.put(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
                    positions.put(OrderConstants.VALIDITY, Validity.DAY); // For
                                                                            // Square-off
                                                                            // and
                                                                            // Buy
                                                                            // more
                                                                            // navigation
                    positions.put(OrderConstants.DISC_QTY, "--");

                    positions.put(DeviceConstants.IS_SQUARE_OFF,
                            PositionsHelper_101.isSquareOff(netQty, positionRow.getProdType()));
                    positions.put(DeviceConstants.IS_CONVERT,
                            PositionsHelper_101.isSquareOff(netQty, positionRow.getProdType()));
                    positions.put(DeviceConstants.IS_BUY_MORE, PositionsHelper_101.isBuyMore(netQty));
                    positions.put(DeviceConstants.IS_SELL_MORE, PositionsHelper_101.isSellMore(netQty));
                    positions.put(OrderConstants.ORDER_ACTION, PositionsHelper_101.getOrderAction(netQty));
                    positions.put(OrderConstants.TO_CONVERT_ACTION, PositionsHelper_101.getConvertOrderAction(netQty));
                    positions.put(OrderConstants.NR_DR, priceNRDR);
                    positions.put(DeviceConstants.INSTRUMENT, tempSymbolRow.getInstrument());
                    positions.put(DeviceConstants.EXCH, tempSymbolRow.getExchange());
                    positionsList.put(positions);
                    
                
                }
                finalObj.put(DeviceConstants.TOTAL_COUNT, totalRecordsCount);
                
                } catch (Exception e) {

                    log.error(e);
                }
            } // End of for loop

        int recordCount = positionsList.length();
        PositionsHelper_101.getMarketValueAndPLUsingLTP_101(positionsList, linkedsetSymbolToken);
        positionsList = sort(filterPositions(positionsList, optedFilter), optedSortOrder, optedSortBy);
        finalObj.put(DeviceConstants.POSITION_LIST, positionsList);
        JSONObject summaryObj = PositionsHelper_101.makeSummaryObjectForPositions(positionsList);
        summaryObj.put(DeviceConstants.RECORDS_COUNT, String.valueOf(recordCount));
        finalObj.put(DeviceConstants.TOTAL_SUMMARY, summaryObj);

        return finalObj;
    }
}