package com.globecapital.business.market;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.spyder.market.OiAnalysisCommodityAPI;
import com.globecapital.api.spyder.market.OiAnalysisDerivativesAPI;
import com.globecapital.api.spyder.market.OiAnalysisObject;
import com.globecapital.api.spyder.market.OiAnalysisRequest;
import com.globecapital.api.spyder.market.OiAnalysisResponse;
import com.globecapital.api.spyder.market.RolloverAnalysisAPI;
import com.globecapital.api.spyder.market.RolloverAnalysisObject;
import com.globecapital.api.spyder.market.RolloverAnalysisRequest;
import com.globecapital.api.spyder.market.RolloverAnalysisResponse;
import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.db.GCDBPool;
import com.globecapital.db.NewsDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.cmots.api.corporateInfo_v1.GetListedIPO;
import com.msf.cmots.api.data_v1.Announcement;
import com.msf.cmots.api.data_v1.AnnouncementList;
import com.msf.cmots.api.data_v1.CorporateActions;
import com.msf.cmots.api.data_v1.CorporateActionsList;
import com.msf.cmots.api.data_v1.FIIDII;
import com.msf.cmots.api.data_v1.FIIDIIList;
import com.msf.cmots.api.data_v1.ListedIPO;
import com.msf.cmots.api.data_v1.ListedIPOList;
import com.msf.cmots.api.data_v1.ResultsInfo;
import com.msf.cmots.api.data_v1.ResultsInfoList;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Market {

	private static Logger log = Logger.getLogger(Market.class);

	private static final String[] magnitudes = { "K", "L", "Cr" };
	public static String CMOTS_API_BEAN = "";

	public static JSONObject getCorporateActions(String sPeriod, String sCategory, boolean isOverview,
			boolean isSymObjReq, String sAppID) throws JSONException, ParseException, GCException, CMOTSException {
		JSONObject finalObj = new JSONObject();

		CorporateActionsList corpActionsList = new CorporateActionsList();

		if (sPeriod.equalsIgnoreCase(DeviceConstants.NEXT_WEEK)) {
			corpActionsList = CorporateActionsCache.getCorporateActionListNextWeek();
			if (corpActionsList.size() == 0) {
				CorporateActionsCache.loadCorporateActionCacheNextWeek();
				corpActionsList = CorporateActionsCache.getCorporateActionListNextWeek();
			}

		} else if (sPeriod.equalsIgnoreCase(DeviceConstants.THIS_WEEK)) {
			corpActionsList = CorporateActionsCache.getCorporateActionListThisWeek();
			if (corpActionsList.size() == 0) {
				CorporateActionsCache.loadCorporateActionCacheThisWeek();
				corpActionsList = CorporateActionsCache.getCorporateActionListThisWeek();
			}
		} else {
			corpActionsList = CorporateActionsCache.getCorporateActionListToday();
			if (corpActionsList.size() == 0) {
				CorporateActionsCache.loadCorporateActionCacheToday();
				corpActionsList = CorporateActionsCache.getCorporateActionListToday();
			}
		}

		List<JSONObject> parsedCorpActionsList = new ArrayList<JSONObject>();

		for (CorporateActions corpAction : corpActionsList) {
			if (isValidAction(corpAction.getAction())) {
				try {
					JSONObject corpActionObj = new JSONObject();
					if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.SPLIT))
						corpActionObj.put(DeviceConstants.ACTION, DeviceConstants.STOCK_SPLIT);
					else
						corpActionObj.put(DeviceConstants.ACTION, corpAction.getAction());

					corpActionObj.put(DeviceConstants.EX_DATE_S,
							DateUtils.formatDate(corpAction.getCorpDate(),
									DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
									DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
					corpActionObj.put(DeviceConstants.ANNOUNCE_DATE,
							corpAction.getAnnouncementDate() == null ? "--"
									: DateUtils.formatDate(corpAction.getAnnouncementDate(),
											DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
											DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
					corpActionObj.put(DeviceConstants.RECORD_DATE,
							corpAction.getRecordDate() == null ? "--"
									: DateUtils.formatDate(corpAction.getRecordDate(),
											DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
											DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
					corpActionObj.put(DeviceConstants.TYPE,
							corpAction.getDivType().isEmpty() ? "" : corpAction.getDivType().toUpperCase());
					corpActionObj.put(DeviceConstants.DETAIL_DESC, "");
					corpActionObj.put(DeviceConstants.IS_SINGLE_DATE, "false");
					corpActionObj.put(DeviceConstants.COMPANY_NAME, corpAction.getCompanyName());

					if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.DIVIDEND))
						corpActionObj.put(DeviceConstants.SHORT_DESC,
								AdvanceQuote.getDividendShortDesc(corpAction.getValue()));
					else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.BONUS))
						corpActionObj.put(DeviceConstants.SHORT_DESC,
								AdvanceQuote.getBonusShortDesc(corpAction.getValue()));
					else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.SPLIT)) {
						String[] arrSplit = corpAction.getValue().split(":");
						String sSplitValue = arrSplit[1] + ":" + arrSplit[0];

						corpActionObj.put(DeviceConstants.SHORT_DESC, AdvanceQuote.getSplitsShortDesc(sSplitValue,
								corpAction.getFVBefore(), corpAction.getFVAfter()));
					} else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.RIGHTS))
						corpActionObj.put(DeviceConstants.SHORT_DESC,
								AdvanceQuote.getRightsShortDesc(corpAction.getValue(), corpAction.getPremium()));

					if (isSymObjReq) {
						corpActionObj.put(SymbolConstants.SYMBOL_OBJ,
								AdvanceQuote.getSymbolRowUsingISIN((corpAction.getISIN())).getMinimisedSymbolRow()
										.getJSONObject(SymbolConstants.SYMBOL_OBJ));
					}

					parsedCorpActionsList.add(corpActionObj);
				} catch (Exception e) {
					log.warn(e);
				}
			}
		}

		getCompAnnouncement(parsedCorpActionsList, ExchangeSegment.NSE, sPeriod, sAppID);
		getCompAnnouncement(parsedCorpActionsList, ExchangeSegment.BSE, sPeriod, sAppID);

		if (!isOverview) {
			JSONObject corpActionObj = new JSONObject();
			Map<Date, List<JSONObject>> mapDateCorpActions = listToMap(parsedCorpActionsList, sCategory);
			JSONArray corpDate = new JSONArray();
			for (Entry<Date, List<JSONObject>> entry : mapDateCorpActions.entrySet()) {
				String sDate = DateUtils.formatDateToString(entry.getKey(),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
				corpDate.put(sDate);
				corpActionObj.put(sDate, new JSONArray(entry.getValue()));
			}
			corpActionObj.put(DeviceConstants.EX_DATE_S, corpDate);

			if (corpDate.length() <= 0)
				throw new GCException(InfoIDConstants.NO_DATA);
			finalObj.put(DeviceConstants.CORP_ACTIONS, corpActionObj);
		} else {
			JSONArray corpActionsArr = new JSONArray();
			AdvanceQuote.sortJSONArrayDate(parsedCorpActionsList, DeviceConstants.EX_DATE_S,
					DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO, true);

			int count = 0;
			int market_limit = AppConfig.getIntValue("market_limit");

			for (int i = 0; i < parsedCorpActionsList.size(); i++) {
				if (count == market_limit)
					break;

				JSONObject corpObj = parsedCorpActionsList.get(i);

				if (corpObj.getString(DeviceConstants.ACTION).equalsIgnoreCase(sCategory)
						|| sCategory.equalsIgnoreCase(DeviceConstants.ALL)) {
					if (corpObj.getString(DeviceConstants.ACTION)
							.equalsIgnoreCase(DeviceConstants.FILTER_COMP_ANNOUNCEMENTS)) {
						corpObj.remove(DeviceConstants.ACTION);
						corpObj.put(DeviceConstants.ACTION, DeviceConstants.COMPANY_ANNOUNCEMENT);

						corpObj.remove(DeviceConstants.EX_DATE_S);
						corpObj.put(DeviceConstants.EX_DATE_S, "");
					}
					corpActionsArr.put(parsedCorpActionsList.get(i));
					count++;
				}
			}

			if (corpActionsArr.length() <= 0)
				throw new GCException(InfoIDConstants.NO_DATA);
			finalObj.put(DeviceConstants.CORP_ACTIONS, corpActionsArr);

		}

		return finalObj;

	}

	public static JSONObject getCorporateActionsNew(String sPeriod, String sCategory, String sAppID)
			throws JSONException, ParseException, GCException, CMOTSException {
		JSONObject finalObj = new JSONObject();

		CorporateActionsList corpActionsList = new CorporateActionsList();

		if (sPeriod.equalsIgnoreCase(DeviceConstants.NEXT_WEEK)) {
			corpActionsList = CorporateActionsCache.getCorporateActionListNextWeek();
			if (corpActionsList.size() == 0) {
				CorporateActionsCache.loadCorporateActionCacheNextWeek();
				corpActionsList = CorporateActionsCache.getCorporateActionListNextWeek();
			}

		} else if (sPeriod.equalsIgnoreCase(DeviceConstants.THIS_WEEK)) {
			corpActionsList = CorporateActionsCache.getCorporateActionListThisWeek();
			if (corpActionsList.size() == 0) {
				CorporateActionsCache.loadCorporateActionCacheThisWeek();
				corpActionsList = CorporateActionsCache.getCorporateActionListThisWeek();
			}
		} else {
			corpActionsList = CorporateActionsCache.getCorporateActionListToday();
			if (corpActionsList.size() == 0) {
				CorporateActionsCache.loadCorporateActionCacheToday();
				corpActionsList = CorporateActionsCache.getCorporateActionListToday();
			}
		}

		List<JSONObject> parsedCorpActionsList = new ArrayList<JSONObject>();

		for (CorporateActions corpAction : corpActionsList) {
			if (isValidAction(corpAction.getAction())) {
				try {
					JSONObject symObj = getSymbolObj(corpAction.getISIN());

					if (symObj != null) {
						JSONObject corpActionObj = new JSONObject();

						corpActionObj.put(SymbolConstants.SYMBOL_OBJ, symObj.getJSONObject(SymbolConstants.SYMBOL_OBJ));

						if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.SPLIT))
							corpActionObj.put(DeviceConstants.ACTION, DeviceConstants.STOCK_SPLIT);
						else
							corpActionObj.put(DeviceConstants.ACTION, corpAction.getAction());

						corpActionObj.put(DeviceConstants.EX_DATE_S,
								DateUtils.formatDate(corpAction.getCorpDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
						corpActionObj.put(DeviceConstants.ANNOUNCE_DATE,
								corpAction.getAnnouncementDate() == null ? "--"
										: DateUtils.formatDate(corpAction.getAnnouncementDate(),
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
						corpActionObj.put(DeviceConstants.RECORD_DATE,
								corpAction.getRecordDate() == null ? "--"
										: DateUtils.formatDate(corpAction.getRecordDate(),
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
						corpActionObj.put(DeviceConstants.TYPE,
								corpAction.getDivType().isEmpty() ? "" : corpAction.getDivType().toUpperCase());
						corpActionObj.put(DeviceConstants.DETAIL_DESC, "");
						corpActionObj.put(DeviceConstants.IS_SINGLE_DATE, "false");
						corpActionObj.put(DeviceConstants.COMPANY_NAME, corpAction.getCompanyName());

						if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.DIVIDEND))
							corpActionObj.put(DeviceConstants.SHORT_DESC,
									AdvanceQuote.getDividendShortDesc(corpAction.getValue()));
						else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.BONUS))
							corpActionObj.put(DeviceConstants.SHORT_DESC,
									AdvanceQuote.getBonusShortDesc(corpAction.getValue()));
						else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.SPLIT)) {
							String[] arrSplit = corpAction.getValue().split(":");
							String sSplitValue = arrSplit[1] + ":" + arrSplit[0];

							corpActionObj.put(DeviceConstants.SHORT_DESC, AdvanceQuote.getSplitsShortDesc(sSplitValue,
									corpAction.getFVBefore(), corpAction.getFVAfter()));
						} else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.RIGHTS))
							corpActionObj.put(DeviceConstants.SHORT_DESC,
									AdvanceQuote.getRightsShortDesc(corpAction.getValue(), corpAction.getPremium()));

						parsedCorpActionsList.add(corpActionObj);
					}
				} catch (Exception e) {
					log.warn(e);
				}
			}
		}

		getCompAnnouncement(parsedCorpActionsList, ExchangeSegment.NSE, sPeriod, sAppID);
		getCompAnnouncement(parsedCorpActionsList, ExchangeSegment.BSE, sPeriod, sAppID);

		JSONArray finalArr = new JSONArray();

		Map<Date, List<JSONObject>> mapDateCorpActions = listToMap(parsedCorpActionsList, sCategory);
		for (Entry<Date, List<JSONObject>> entry : mapDateCorpActions.entrySet()) {

			JSONObject obj = new JSONObject();
			String sDate = DateUtils.formatDateToString(entry.getKey(), DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
			obj.put(DeviceConstants.DATE_TITLE, sDate);
			obj.put(DeviceConstants.SYMBOL_LIST, new JSONArray(entry.getValue()));
			finalArr.put(obj);
		}

		if (finalArr.length() <= 0)
			throw new GCException(InfoIDConstants.NO_DATA);
		finalObj.put(DeviceConstants.CORP_ACTIONS, finalArr);

		return finalObj;

	}

	private static JSONObject getSymbolObj(String sISIN) {

		JSONObject symObj = null;

		if (SymbolMap.isValidSymbol(sISIN + "_" + ExchangeSegment.NSE_SEGMENT_ID))
			symObj = SymbolMap.getISINSymbolRow(sISIN + "_" + ExchangeSegment.NSE_SEGMENT_ID).getMinimisedSymbolRow();
		else if (SymbolMap.isValidSymbol(sISIN + "_" + ExchangeSegment.BSE_SEGMENT_ID))
			symObj = SymbolMap.getISINSymbolRow(sISIN + "_" + ExchangeSegment.BSE_SEGMENT_ID).getMinimisedSymbolRow();

		return symObj;
	}

	private static boolean isValidAction(String sAction) {

		if (sAction.equalsIgnoreCase(DeviceConstants.DIVIDEND) || sAction.equalsIgnoreCase(DeviceConstants.BONUS)
				|| sAction.equalsIgnoreCase(DeviceConstants.SPLIT) || sAction.equalsIgnoreCase(DeviceConstants.RIGHTS))
			return true;
		return false;
	}

	private static void getCompAnnouncement(List<JSONObject> parsedCorpActionsList, String sExchange, String sPeriod,
			String sAppIDForLogging) throws JSONException, ParseException, CMOTSException {
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

				String sDate = announcement.getDate();

				boolean isDateWithinPeriod = checkDateWithinPeriod(sDate, sPeriod);

				if (isDateWithinPeriod) {
					JSONObject obj = new JSONObject();

					obj.put(DeviceConstants.ACTION, DeviceConstants.FILTER_COMP_ANNOUNCEMENTS);
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

					parsedCorpActionsList.add(obj);
				}
			}
		} catch (Exception e) {
			log.warn(e);
		}
	}

	private static void getCompAnnouncementOverview(List<JSONObject> parsedCorpActionsList, String sExchange,
			String sAppIDForLogging) throws JSONException, ParseException, CMOTSException {
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

				String sDate = announcement.getDate();

				if (!isDateLessThanToday(sDate)) {
					JSONObject obj = new JSONObject();

					obj.put(DeviceConstants.ACTION, DeviceConstants.FILTER_COMP_ANNOUNCEMENTS);
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

					parsedCorpActionsList.add(obj);
				}
			}
		} catch (Exception e) {
			log.warn(e);
		}
	}

	private static boolean checkDateWithinPeriod(String sDate, String sPeriod) throws ParseException {

		long lStartTime = 0, lEndTime = 0;

		long lDate = DateUtils.getDate(sDate, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM).getTime();

		if (sPeriod.equalsIgnoreCase(DeviceConstants.TODAY)) {
			lStartTime = DateUtils
					.getDate(DateUtils.getCurrentDate() + " 00:00:00", DeviceConstants.CORP_ACTION_DATE_FORMAT)
					.getTime();
			lEndTime = DateUtils
					.getDate(DateUtils.getCurrentDate() + " 23:59:59", DeviceConstants.CORP_ACTION_DATE_FORMAT)
					.getTime();
		} else if (sPeriod.equalsIgnoreCase(DeviceConstants.THIS_WEEK)) {
			lStartTime = DateUtils
					.getDate(DateUtils.getCurrentDate() + " 00:00:00", DeviceConstants.CORP_ACTION_DATE_FORMAT)
					.getTime();
			lEndTime = DateUtils
					.getDate(DateUtils.getNthDateFromTodayDate(DeviceConstants.TO_DATE_FORMAT, 7) + " 23:59:59",
							DeviceConstants.CORP_ACTION_DATE_FORMAT)
					.getTime();
		} else if (sPeriod.equalsIgnoreCase(DeviceConstants.NEXT_WEEK)) {
			lStartTime = DateUtils
					.getDate(DateUtils.getCurrentDate() + " 00:00:00", DeviceConstants.CORP_ACTION_DATE_FORMAT)
					.getTime();
			lEndTime = DateUtils
					.getDate(DateUtils.getNthDateFromTodayDate(DeviceConstants.TO_DATE_FORMAT, 14) + " 23:59:59",
							DeviceConstants.CORP_ACTION_DATE_FORMAT)
					.getTime();
		}

		if (lDate >= lStartTime && lDate <= lEndTime)
			return true;

		return false;
	}

	private static boolean isDateLessThanToday(String sDate) throws ParseException {

		long lDateTime = DateUtils.getDate(sDate, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM).getTime();

		long lCurrentTime = DateUtils.getDate(DateUtils.getCurrentDate(), DeviceConstants.TO_DATE_FORMAT).getTime();

		if (lDateTime < lCurrentTime)
			return true;

		return false;

	}

	static String corpActionFormatToAPI(String sPeriod) {
		if (sPeriod.equalsIgnoreCase(DeviceConstants.THIS_WEEK))
			return DeviceConstants.PERIOD_WEEK;
		else if (sPeriod.equalsIgnoreCase(DeviceConstants.NEXT_WEEK))
			return DeviceConstants.PERIOD_15DAY;
		else
			return DeviceConstants.TODAY;
	}

	public static Map<Date, List<JSONObject>> listToMap(List<JSONObject> lt, String sActionFilter)
			throws JSONException, ParseException {
		Map<Date, List<JSONObject>> map = new TreeMap<>();

		for (int i = 0; i < lt.size(); i++) {
			JSONObject obj = lt.get(i);
			String sAction = obj.getString(DeviceConstants.ACTION);
			if (sActionFilter.equalsIgnoreCase(DeviceConstants.ALL) || sAction.equalsIgnoreCase(sActionFilter)) {
				Date sKey = DateUtils.getDate(obj.getString(DeviceConstants.EX_DATE_S),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
				if (sAction.equalsIgnoreCase(DeviceConstants.FILTER_COMP_ANNOUNCEMENTS)) {
					obj.remove(DeviceConstants.ACTION);
					obj.put(DeviceConstants.ACTION, DeviceConstants.COMPANY_ANNOUNCEMENT);

					obj.remove(DeviceConstants.EX_DATE_S);
					obj.put(DeviceConstants.EX_DATE_S, "");
				}
				if (map.containsKey(sKey)) {
					map.get(sKey).add(obj);
				} else {
					List<JSONObject> list = new ArrayList<>();
					list.add(obj);
					map.put(sKey, list);
				}
			}
		}

		return map;
	}

	public static JSONObject getFIIDIIData(String sPeriod, String sCategory, boolean isOverview, String sAppID)
			throws JSONException, ParseException, AppConfigNoKeyFoundException, CMOTSException {
		JSONObject finalObj = new JSONObject();
		FIIDIIList fiiDiiList = new FIIDIIList();
		if (FIIDIICache.getFIIDIIList(sPeriod, sCategory, sAppID).isEmpty()) {
			log.info("data is null");
			FIIDIICache.loadFIIDIICache(sPeriod, sCategory, sAppID);
		}
		fiiDiiList = FIIDIICache.getFIIDIIList(sPeriod, sCategory, sAppID);
		log.info(fiiDiiList);
		List<JSONObject> parsedFIIDIIList = new ArrayList<JSONObject>();
		for (FIIDII fiiDii : fiiDiiList) {
			JSONObject obj = new JSONObject();

			if (sPeriod.equalsIgnoreCase(DeviceConstants.DAILY)) {
				obj.put(DeviceConstants.DATE_S, DateUtils.formatDate(fiiDii.getDate(),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
				obj.put(DeviceConstants.RECORD_DATE, DateUtils.formatDate(fiiDii.getDate(),
						DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
			} else if (sPeriod.equalsIgnoreCase(DeviceConstants.MONTHLY))
				obj.put(DeviceConstants.DATE_S, fiiDii.getDate());
			else if (sPeriod.equalsIgnoreCase(DeviceConstants.YEARLY))
				obj.put(DeviceConstants.DATE_S, fiiDii.getDate());

			if (sCategory.equalsIgnoreCase(DeviceConstants.ALL)) {
				Double maxValue = 0.0;

				String sFIICash = PriceFormat.formatPrice(fiiDii.getFIICash(),
						Integer.parseInt(OrderConstants.PRECISION_2), false);
				Double dFIICash = Math.abs(Double.parseDouble(sFIICash.replace(",", "")));
				if (dFIICash > maxValue)
					maxValue = dFIICash;
				obj.put(DeviceConstants.FII_CASH_API, sFIICash);

				String sDIICash = PriceFormat.formatPrice(fiiDii.getDIICash(),
						Integer.parseInt(OrderConstants.PRECISION_2), false);
				Double dDIICash = Math.abs(Double.parseDouble(sDIICash.replace(",", "")));
				if (dDIICash > maxValue)
					maxValue = dDIICash;
				obj.put(DeviceConstants.DII_CASH_API, sDIICash);

				String sFIIFuture = PriceFormat.formatPrice(fiiDii.getFIIFuture(),
						Integer.parseInt(OrderConstants.PRECISION_2), false);
				Double dFIIFuture = Math.abs(Double.parseDouble(sFIIFuture.replace(",", "")));
				if (dFIIFuture > maxValue)
					maxValue = dFIIFuture;
				obj.put(DeviceConstants.FII_FUTURE_API, sFIIFuture);

				String sFIIOption = PriceFormat.formatPrice(fiiDii.getFIIOption(),
						Integer.parseInt(OrderConstants.PRECISION_2), false);
				Double dFIIOption = Math.abs(Double.parseDouble(sFIIOption.replace(",", "")));
				if (dFIIOption > maxValue)
					maxValue = dFIIOption;
				obj.put(DeviceConstants.FII_OPTION_API, sFIIOption);

				obj.put(DeviceConstants.MAX_VALUE, Double.toString(maxValue));
			} else if (sCategory.equalsIgnoreCase(DeviceConstants.FII_CASH))
				obj.put(DeviceConstants.VALUE, PriceFormat.formatPrice(fiiDii.getFIICash(),
						Integer.parseInt(OrderConstants.PRECISION_2), false));
			else if (sCategory.equalsIgnoreCase(DeviceConstants.DII_CASH))
				obj.put(DeviceConstants.VALUE, PriceFormat.formatPrice(fiiDii.getDIICash(),
						Integer.parseInt(OrderConstants.PRECISION_2), false));
			else if (sCategory.equalsIgnoreCase(DeviceConstants.FII_FUTURE))
				obj.put(DeviceConstants.VALUE, PriceFormat.formatPrice(fiiDii.getFIIFuture(),
						Integer.parseInt(OrderConstants.PRECISION_2), false));
			else if (sCategory.equalsIgnoreCase(DeviceConstants.FII_OPTION))
				obj.put(DeviceConstants.VALUE, PriceFormat.formatPrice(fiiDii.getFIIOption(),
						Integer.parseInt(OrderConstants.PRECISION_2), false));

			parsedFIIDIIList.add(obj);

		}

		if (sPeriod.equalsIgnoreCase(DeviceConstants.DAILY))
			AdvanceQuote.sortJSONArrayDate(parsedFIIDIIList, DeviceConstants.DATE_S,
					DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT, false);
		else if (sPeriod.equalsIgnoreCase(DeviceConstants.MONTHLY))
			AdvanceQuote.sortJSONArrayDate(parsedFIIDIIList, DeviceConstants.DATE_S,
					DeviceConstants.MONTHLY_DATE_FORMAT, false);
		else if (sPeriod.equalsIgnoreCase(DeviceConstants.YEARLY))
			AdvanceQuote.sortJSONArrayDate(parsedFIIDIIList, DeviceConstants.DATE_S, DeviceConstants.YEARLY_DATE_FORMAT,
					false);

		JSONArray finalArr = new JSONArray();

		if (!isOverview) {
			for (int i = 0; i < parsedFIIDIIList.size(); i++) {
				JSONObject obj = parsedFIIDIIList.get(i);
				if (sPeriod.equalsIgnoreCase(DeviceConstants.DAILY)) {
					String sPeriodBeforeFormat = obj.getString(DeviceConstants.DATE_S);
					obj.remove(DeviceConstants.DATE_S);
					Date dDate = DateUtils.getDate(sPeriodBeforeFormat, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
					String sFinalFormattedDate = DateUtils.getFormattedDay(dDate);
					obj.put(DeviceConstants.DATE_S, sFinalFormattedDate);
				} else if (sPeriod.equalsIgnoreCase(DeviceConstants.MONTHLY)) {
					String sPeriodBeforeFormat = obj.getString(DeviceConstants.DATE_S);
					obj.remove(DeviceConstants.DATE_S);
					obj.put(DeviceConstants.DATE_S, sPeriodBeforeFormat.toUpperCase());

				}

				finalArr.put(obj);

			}
		} else {
			int fiiDiiDataLimit;
			if (sCategory.equalsIgnoreCase(DeviceConstants.ALL_FILTER))
				fiiDiiDataLimit = AppConfig.getIntValue("fii_dii_all_limit");
			else
				fiiDiiDataLimit = AppConfig.getIntValue("fii_dii_others_limit");

			for (int i = 0; i < parsedFIIDIIList.size() && i < fiiDiiDataLimit; i++) {
				JSONObject obj = parsedFIIDIIList.get(i);
				if (sPeriod.equalsIgnoreCase(DeviceConstants.DAILY)) {
					String sPeriodBeforeFormat = obj.getString(DeviceConstants.DATE_S);
					obj.remove(DeviceConstants.DATE_S);
					Date dDate = DateUtils.getDate(sPeriodBeforeFormat, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
					String sFinalFormattedDate = DateUtils.getFormattedDay(dDate);
					obj.put(DeviceConstants.DATE_S, sFinalFormattedDate);
				} else if (sPeriod.equalsIgnoreCase(DeviceConstants.MONTHLY)) {
					String sPeriodBeforeFormat = obj.getString(DeviceConstants.DATE_S);
					obj.remove(DeviceConstants.DATE_S);
					obj.put(DeviceConstants.DATE_S, sPeriodBeforeFormat.toUpperCase());

				}

				finalArr.put(obj);
			}
		}

		finalObj.put(DeviceConstants.FII_DII_DATA, finalArr);

		return finalObj;
	}

	public static JSONArray getResultsListOverview(String sAppID) throws Exception {
		JSONArray result = new JSONArray();
		ArrayList<String> resultsToken = new ArrayList<String>();
		int market_limit = AppConfig.getIntValue("market_limit");

		result = callResultsListAPI(DeviceConstants.LATER, resultsToken, sAppID);

		/*
		 * if(result.length() < market_limit) { JSONArray res =
		 * callResultsListAPI(DeviceConstants.PERIOD_WEEK, resultsToken, sAppID); int
		 * cnt = market_limit - result.length(); for( int i=0; i<cnt && i<res.length() ;
		 * i++) { result.put(res.getJSONObject(i)); } if(result.length() < market_limit)
		 * { res = callResultsListAPI(DeviceConstants.LATER, resultsToken, sAppID); cnt
		 * = market_limit - result.length(); for( int i=0; i<cnt && i<res.length() ;
		 * i++) { result.put(res.getJSONObject(i)); } } }
		 */
		return result;
	}

	public static JSONArray callResultsListAPI(String type, ArrayList<String> resultsToken, String appID)
			throws Exception {
		JSONArray result = new JSONArray();
		JSONArray combinedResult = new JSONArray();
		ResultsInfoList resultsListNSE = new ResultsInfoList();
		ResultsInfoList resultsListBSE = new ResultsInfoList();
		int market_limit = AppConfig.getIntValue("market_limit");
		if (ResultsInfoCache.getResultInfoList(type, appID, ExchangeSegment.NSE).isEmpty()
				&& ResultsInfoCache.getResultInfoList(type, appID, ExchangeSegment.BSE).isEmpty()) {
			ResultsInfoCache.loadResultsInfoCache(type, appID, ExchangeSegment.NSE);
			ResultsInfoCache.loadResultsInfoCache(type, appID, ExchangeSegment.BSE);
		}
		resultsListNSE = ResultsInfoCache.getResultInfoList(type, appID, ExchangeSegment.NSE);
		resultsListBSE = ResultsInfoCache.getResultInfoList(type, appID, ExchangeSegment.BSE);
		if (resultsListNSE == null && resultsListBSE == null)
			return result;

		formResultListOverview(resultsToken, result, resultsListNSE, ExchangeSegment.NSE_SEGMENT_ID, market_limit);
		formResultListOverview(resultsToken, result, resultsListBSE, ExchangeSegment.BSE_SEGMENT_ID, market_limit * 2);
		result = sortByDate(result, DeviceConstants.DATE_S);
		if (result.length() < market_limit)
			market_limit = result.length();
		for (int i = 0; i < market_limit; i++)
			combinedResult.put(result.get(i));
		return combinedResult;
	}

	public static JSONArray sortByDate(JSONArray recordArray, final String sortBy) {
		JSONArray sorted = new JSONArray();
		final SimpleDateFormat format = new SimpleDateFormat(DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
		List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
		try {
			for (int i = 0; i < recordArray.length(); i++)
				toBeSorted.add(recordArray.getJSONObject(i));

			Collections.sort(toBeSorted, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {
					try {
						if (format.parse(obj2.getString(sortBy)).compareTo(format.parse(obj1.getString(sortBy))) > 0)
							return -1;
						else if (format.parse(obj2.getString(sortBy))
								.compareTo(format.parse(obj1.getString(sortBy))) < 0)
							return 1;
						return 0;
					} catch (JSONException | ParseException e) {
						log.error(e.getMessage());
						e.printStackTrace();
					}
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		sorted = new JSONArray(toBeSorted);
		return sorted;
	}

	public static void formResultListOverview(ArrayList<String> resultsToken, JSONArray result,
			ResultsInfoList resultsList, String exch, int market_limit) throws SQLException, ParseException {
		for (ResultsInfo results : resultsList) {
			JSONObject obj = new JSONObject();
			String isin = results.getISIN() + "_" + exch;
			if (SymbolMap.isValidSymbol(isin)) {
				SymbolRow symObj = SymbolMap.getISINSymbolRow(isin);
				if (resultsToken.contains(symObj.getSymbolToken()))
					continue;
				else
					resultsToken.add(symObj.getSymbolToken());
				obj.put(SymbolConstants.SYMBOL_OBJ, symObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
				QuoteDetails quote = Quote.getLTP(symObj.getSymbolToken(), symObj.getMappingSymbolUniqDesc());
				obj.put(DeviceConstants.LTP, quote.sLTP);
				obj.put(DeviceConstants.CHANGE, quote.sChange);
				obj.put(DeviceConstants.CHANGE_PERCENT, quote.sChangePercent);
				obj.put(DeviceConstants.PERIOD, results.getQtrPeriod());
				obj.put(DeviceConstants.DATE_LS, DateUtils.formatDate(results.getResultDate(),
						DeviceConstants.OPTIONS_DATE_FORMAT_1, DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT));
				result.put(obj);
			} else
				continue;
			if (result.length() == market_limit)
				break;
		}
	}

	public static JSONArray getResultsList(String type, String exchange, String sAppID) throws Exception {
		JSONArray finalObj = new JSONArray();
		ArrayList<String> dateList = new ArrayList<String>();
		ArrayList<JSONArray> resultInfo = new ArrayList<JSONArray>();
		JSONArray result = new JSONArray();
		ResultsInfoList resultsList = new ResultsInfoList();

		if (ResultsInfoCache.getResultInfoList(type, sAppID, exchange).isEmpty())
			ResultsInfoCache.loadResultsInfoCache(type, sAppID, exchange);
		resultsList = ResultsInfoCache.getResultInfoList(type, sAppID, exchange);

		if (resultsList == null)
			return result;

		for (ResultsInfo results : resultsList) {
			JSONObject obj = new JSONObject();
			JSONObject resultObj = new JSONObject();
			String isin = results.getISIN() + "_" + ExchangeSegment.getMarketSegmentID(exchange);
			if (SymbolMap.isValidSymbol(isin)) {
				SymbolRow symObj = SymbolMap.getISINSymbolRow(isin);
				obj.put(SymbolConstants.SYMBOL_OBJ, symObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
				QuoteDetails quote = Quote.getLTP(symObj.getSymbolToken(), symObj.getMappingSymbolUniqDesc());
				obj.put(DeviceConstants.LTP, quote.sLTP);
				obj.put(DeviceConstants.CHANGE, quote.sChange);
				obj.put(DeviceConstants.CHANGE_PERCENT, quote.sChangePercent);
				obj.put(DeviceConstants.PERIOD, results.getQtrPeriod());
				String date = DateUtils.formatDate(results.getResultDate(), DeviceConstants.OPTIONS_DATE_FORMAT_1,
						DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
				if (!dateList.contains(date)) {
					dateList.add(date);
					result = new JSONArray();
				} else {
					result = finalObj.getJSONObject(dateList.indexOf(date)).getJSONArray(DeviceConstants.SYMBOL_LIST);

				}
				result.put(obj);
				resultInfo.add(dateList.indexOf(date), result);

				resultObj.put(DeviceConstants.DATE_TITLE, date);

				resultObj.put(DeviceConstants.SYMBOL_LIST, resultInfo.get(dateList.indexOf(date)));
				finalObj.put(dateList.indexOf(date), resultObj);
			} else
				continue;

		}
		return finalObj;

	}

	public static JSONArray getIndicesListOverview(String segmentType) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		JSONArray indices = new JSONArray();

		String query = DBQueryConstants.GET_FT_INDICES;
		log.debug("Query :: " + query);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, segmentType);
			res = ps.executeQuery();

			while (res.next()) {
				String token = res.getString(DBConstants.TOKEN_SEGMENT);
				if (!Indices.isValidIndex(token))
					continue;
				JSONObject symObj = Indices.getSymbolRow(token).getMinimisedSymbolRow()
						.getJSONObject(SymbolConstants.SYMBOL_OBJ);

				symObj.put(DeviceConstants.ADV_DEC, "10/15");
				indices.put(symObj);
			}
		} catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return indices;
	}

	public static JSONArray getIndicesList(String exchange) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		JSONArray indices = new JSONArray();

		String query = DBQueryConstants.GET_FT_INDICES_BY_EXCHANGE;
		log.debug("Query :: " + query);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);

			ps.setString(1, DeviceConstants.EQUITY);
			ps.setString(2, exchange);

			res = ps.executeQuery();

			while (res.next()) {
				String token = res.getString(DBConstants.TOKEN_SEGMENT);
				if (!Indices.isValidIndex(token))
					continue;
				JSONObject symObj = Indices.getSymbolRow(token).getMinimisedSymbolRow()
						.getJSONObject(SymbolConstants.SYMBOL_OBJ);

				symObj.put(DeviceConstants.ADV_DEC, "10/15");
				indices.put(symObj);
			}
		} catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return indices;
	}

	public static JSONObject getListedIPO(String exchange, String sAppID) throws Exception {
		JSONObject finalObj = new JSONObject();
		JSONArray finalArray = new JSONArray();

		GetListedIPO listedIPOObj = new GetListedIPO(AdvanceQuote.getAppIDForLogging(sAppID));

		listedIPOObj.setTop("50");
		listedIPOObj.setExchange(exchange);

		ListedIPOList listedIPOList = listedIPOObj.invoke();

		for (ListedIPO listedIpo : listedIPOList) {
			JSONObject obj = new JSONObject();
			String isin = listedIpo.getIsin() + "_" + ExchangeSegment.getMarketSegmentID(exchange.toUpperCase());
			if (!SymbolMap.isValidSymbol(isin))
				continue;
			SymbolRow sSymObj = SymbolMap.getISINSymbolRow(isin);
			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
			obj.put(DeviceConstants.ISSUE_PRICE, listedIpo.getIssuePrice());
			obj.put(DeviceConstants.DATE_LS, DateUtils.formatDate(listedIpo.getDate(),
					DeviceConstants.OPTIONS_DATE_FORMAT_1, DeviceConstants.NEWS_DATE_TO_FORMAT));

			QuoteDetails quote = Quote.getLTP(sSymObj.getSymbolToken(), sSymObj.getMappingSymbolUniqDesc());
			obj.put(DeviceConstants.LTP, quote.sLTP);
			obj.put(DeviceConstants.CHANGE, quote.sChange);
			obj.put(DeviceConstants.CHANGE_PERCENT, quote.sChangePercent);

			finalArray.put(obj);
		}
		finalObj.put(DeviceConstants.DATE_LS, DateUtils.getCurrentDateTime(DeviceConstants.INDEX_DATE_FORMAT));
		finalObj.put(DeviceConstants.LISTED_IPO, finalArray);

		return finalObj;
	}

	public static JSONObject getLive() throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		JSONObject news = new JSONObject();

		String category = DeviceConstants.COMMENTARY;

		String query = DBQueryConstants.GET_LIVE_NEWS;
		log.debug("Query :: " + query);

		try {
			conn = NewsDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, category);
			res = ps.executeQuery();
			while (res.next()) {
				String title = "";
				String desc = res.getString(DBConstants.NEWS_DESCRIPTION);
				title = res.getString(DBConstants.NEWS_DESCRIPTION);
//				int index = desc.indexOf(":");
//				if (index >= 0) {
//					title = desc.substring(0, index);
//					desc = desc.substring(index + 1, desc.length());
//					if ( desc.startsWith(" ")) {
//						int indx = desc.indexOf(" ");
//						desc = desc.substring(indx+1);
//					}
//				}
				news.put(DeviceConstants.TITLE, title);
				news.put(DeviceConstants.NEWS_DESCRIPTION, desc);
				Date date = DateUtils.getDate(res.getString(DBConstants.DATE), DBConstants.EXPIRY_DATE_FROM_FORMAT);
				String formattedDate = DateUtils.getFormattedDayMonth(date);
				String formattedTime = DateUtils.formatDate(res.getString(DBConstants.TIME),
						DBConstants.NEWS_TIME_FORMAT, DeviceConstants.NEWS_TIME_FORMAT);
				news.put(DeviceConstants.DATE_LS, formattedDate + " " + formattedTime);
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return news;
	}

	public static JSONArray getOiAnalysisDerivativesOverview(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		OiAnalysisRequest oiAnalysisReq = new OiAnalysisRequest();
		oiAnalysisReq.setBuild(getCategory(category));
		OiAnalysisDerivativesAPI oiAnalysisAPI = new OiAnalysisDerivativesAPI();
		OiAnalysisResponse oiAnalysisResp = oiAnalysisAPI.get(oiAnalysisReq, OiAnalysisResponse.class, appID,
				DeviceConstants.OI_ANALYSIS_L + " " + DeviceConstants.DERIVATIVE);
		List<OiAnalysisObject> oiAnalysisObj = oiAnalysisResp.getResponseObject();
		for (int i = 0; i < oiAnalysisObj.size() && oiAnalysis.length() < limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiAnalysisObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
				continue;
			SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
			QuoteDetails quote = Quote.getLTP(sSymObj.getSymbolToken(), sSymObj.getMappingSymbolUniqDesc());
			obj.put(DeviceConstants.LTP, quote.sLTP);
			obj.put(DeviceConstants.CHANGE, quote.sChange);
			obj.put(DeviceConstants.CHANGE_PERCENT, quote.sChangePercent);
			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(sSymObj.getExpiry(),
					DBConstants.EXPIRY_DATE_FORMAT, DeviceConstants.TRANS_DATE_FORMAT));
			obj.put(DeviceConstants.OI, oiAnalysisObj.get(i).getOi());
			obj.put(DeviceConstants.DISP_OI, PriceFormat.numberFormat(oiAnalysisObj.get(i).getOi()));
			obj.put(DeviceConstants.OI_PER, oiAnalysisObj.get(i).getOiPer());
			oiAnalysis.put(obj);
		}
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}

	public static JSONArray getOiAnalysisDerivatives(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();

		OiAnalysisRequest oiAnalysisReq = new OiAnalysisRequest();
		oiAnalysisReq.setBuild(getCategory(category));
		OiAnalysisDerivativesAPI oiAnalysisAPI = new OiAnalysisDerivativesAPI();
		OiAnalysisResponse oiAnalysisResp = oiAnalysisAPI.get(oiAnalysisReq, OiAnalysisResponse.class, appID,
				DeviceConstants.OI_ANALYSIS_L + " " + DeviceConstants.DERIVATIVE);
		List<OiAnalysisObject> oiAnalysisObj = oiAnalysisResp.getResponseObject();
		for (int i = 0; i < oiAnalysisObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiAnalysisObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
				continue;
			SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
			QuoteDetails quote = Quote.getLTP(sSymObj.getSymbolToken(), sSymObj.getMappingSymbolUniqDesc());
			obj.put(DeviceConstants.LTP, quote.sLTP);
			obj.put(DeviceConstants.CHANGE, quote.sChange);
			obj.put(DeviceConstants.CHANGE_PERCENT, quote.sChangePercent);
			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(sSymObj.getExpiry(),
					DBConstants.EXPIRY_DATE_FORMAT, DeviceConstants.TRANS_DATE_FORMAT));
			obj.put(DeviceConstants.OI, oiAnalysisObj.get(i).getOi());
			obj.put(DeviceConstants.DISP_OI, PriceFormat.numberFormat(oiAnalysisObj.get(i).getOi()));
			obj.put(DeviceConstants.OI_PER, oiAnalysisObj.get(i).getOiPer());
			oiAnalysis.put(obj);
		}
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}

	public static JSONArray getOiAnalysisCommodityOverview(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		OiAnalysisRequest oiAnalysisReq = new OiAnalysisRequest();
		oiAnalysisReq.setBuild(getCategory(category));
		OiAnalysisCommodityAPI oiAnalysisAPI = new OiAnalysisCommodityAPI();
		OiAnalysisResponse oiAnalysisResp = oiAnalysisAPI.get(oiAnalysisReq, OiAnalysisResponse.class, appID,
				DeviceConstants.OI_ANALYSIS_L + " " + DeviceConstants.COMMODITY);
		List<OiAnalysisObject> oiAnalysisObj = oiAnalysisResp.getResponseObject();
		for (int i = 0; i < oiAnalysisObj.size() && oiAnalysis.length() < limit; i++) {
			JSONObject obj = new JSONObject();
			String token = oiAnalysisObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.MCX_SEGMENT_ID;
			if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
				continue;
			SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
			QuoteDetails quote = Quote.getLTP(sSymObj.getSymbolToken(), sSymObj.getMappingSymbolUniqDesc());
			obj.put(DeviceConstants.LTP, quote.sLTP);
			obj.put(DeviceConstants.CHANGE, quote.sChange);
			obj.put(DeviceConstants.CHANGE_PERCENT, quote.sChangePercent);
			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(sSymObj.getExpiry(),
					DBConstants.EXPIRY_DATE_FORMAT, DeviceConstants.TRANS_DATE_FORMAT));
			obj.put(DeviceConstants.OI, oiAnalysisObj.get(i).getOi());
			obj.put(DeviceConstants.DISP_OI, PriceFormat.numberFormat(oiAnalysisObj.get(i).getOi()));
			obj.put(DeviceConstants.OI_PER, oiAnalysisObj.get(i).getOiPer());
			oiAnalysis.put(obj);
		}
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}

	public static JSONArray getOiAnalysisCommodity(String category, String appID) throws Exception {
		JSONArray oiAnalysis = new JSONArray();

		OiAnalysisRequest oiAnalysisReq = new OiAnalysisRequest();
		oiAnalysisReq.setBuild(getCategory(category));
		OiAnalysisCommodityAPI oiAnalysisAPI = new OiAnalysisCommodityAPI();
		OiAnalysisResponse oiAnalysisResp = oiAnalysisAPI.get(oiAnalysisReq, OiAnalysisResponse.class, appID,
				DeviceConstants.OI_ANALYSIS_L + " " + DeviceConstants.COMMODITY);
		List<OiAnalysisObject> oiAnalysisObj = oiAnalysisResp.getResponseObject();
		for (int i = 0; i < oiAnalysisObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = oiAnalysisObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.MCX_SEGMENT_ID;
			if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
				continue;
			SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
			QuoteDetails quote = Quote.getLTP(sSymObj.getSymbolToken(), sSymObj.getMappingSymbolUniqDesc());
			obj.put(DeviceConstants.LTP, quote.sLTP);
			obj.put(DeviceConstants.CHANGE, quote.sChange);
			obj.put(DeviceConstants.CHANGE_PERCENT, quote.sChangePercent);
			obj.put(DeviceConstants.EXPIRY_DATE, DateUtils.formatDate(sSymObj.getExpiry(),
					DBConstants.EXPIRY_DATE_FORMAT, DeviceConstants.TRANS_DATE_FORMAT));
			obj.put(DeviceConstants.OI, oiAnalysisObj.get(i).getOi());
			obj.put(DeviceConstants.DISP_OI, PriceFormat.numberFormat(oiAnalysisObj.get(i).getOi()));
			obj.put(DeviceConstants.OI_PER, oiAnalysisObj.get(i).getOiPer());
			oiAnalysis.put(obj);
		}
		return sortArray(oiAnalysis, DeviceConstants.OI, DeviceConstants.DESCENDING);
	}

	public static JSONArray getRolloverAnalysisOverview(String category, String instrument, String appID)
			throws Exception {
		JSONArray rolloverAnalysis = new JSONArray();
		int limit = AppConfig.getIntValue("market_limit");
		RolloverAnalysisRequest rolloverAnalysisReq = new RolloverAnalysisRequest();
		if (instrument.equalsIgnoreCase(InstrumentType.D_STOCK))
			instrument = InstrumentType.STOCK;
		else if (instrument.equalsIgnoreCase(InstrumentType.D_INDEX))
			instrument = InstrumentType.INDEX;
		if (category.equalsIgnoreCase(DeviceConstants.HIGHEST))
			category = "Highest";
		else if (category.equalsIgnoreCase(DeviceConstants.LOWEST))
			category = "Lowest";
		rolloverAnalysisReq.setRolloverType(category);
		rolloverAnalysisReq.setStock(instrument);
		RolloverAnalysisAPI rolloverAnalysisAPI = new RolloverAnalysisAPI();
		RolloverAnalysisResponse rolloverAnalysisResp = rolloverAnalysisAPI.get(rolloverAnalysisReq,
				RolloverAnalysisResponse.class, appID, DeviceConstants.ROLLOVER_ANALYSIS_L);
		List<RolloverAnalysisObject> rolloverAnalysisObj = rolloverAnalysisResp.getResponseObject();
		for (int i = 0; i < rolloverAnalysisObj.size() && rolloverAnalysis.length() < limit; i++) {
			JSONObject obj = new JSONObject();
			String token = rolloverAnalysisObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
				continue;
			SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
			obj.put(DeviceConstants.ROLLOVER_COST, rolloverAnalysisObj.get(i).getRolloverCost());
			obj.put(DeviceConstants.ROLLOVER_COST_PER, rolloverAnalysisObj.get(i).getRolloverCostPer());
			obj.put(DeviceConstants.ROLLOVER_PER, rolloverAnalysisObj.get(i).getRolloverPer());
			rolloverAnalysis.put(obj);
		}
		return rolloverAnalysis;
	}

	public static JSONArray getRolloverAnalysis(String category, String instrument, String appID) throws Exception {
		JSONArray rolloverAnalysis = new JSONArray();

		RolloverAnalysisRequest rolloverAnalysisReq = new RolloverAnalysisRequest();
		if (instrument.equalsIgnoreCase(InstrumentType.D_STOCK))
			instrument = InstrumentType.STOCK;
		else if (instrument.equalsIgnoreCase(InstrumentType.D_INDEX))
			instrument = InstrumentType.INDEX;
		if (category.equalsIgnoreCase(DeviceConstants.HIGHEST))
			category = "Highest";
		else if (category.equalsIgnoreCase(DeviceConstants.LOWEST))
			category = "Lowest";
		rolloverAnalysisReq.setRolloverType(category);
		rolloverAnalysisReq.setStock(instrument);
		RolloverAnalysisAPI rolloverAnalysisAPI = new RolloverAnalysisAPI();
		RolloverAnalysisResponse rolloverAnalysisResp = rolloverAnalysisAPI.get(rolloverAnalysisReq,
				RolloverAnalysisResponse.class, appID, DeviceConstants.ROLLOVER_ANALYSIS_L);
		List<RolloverAnalysisObject> rolloverAnalysisObj = rolloverAnalysisResp.getResponseObject();
		for (int i = 0; i < rolloverAnalysisObj.size(); i++) {
			JSONObject obj = new JSONObject();
			String token = rolloverAnalysisObj.get(i).getCode();
			token = token + "_" + ExchangeSegment.NFO_SEGMENT_ID;
			if (!SymbolMap.isValidSymbolTokenSegmentMap(token))
				continue;
			SymbolRow sSymObj = SymbolMap.getSymbolRow(token);
			obj.put(SymbolConstants.SYMBOL_OBJ, sSymObj.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
			obj.put(DeviceConstants.ROLLOVER_COST, rolloverAnalysisObj.get(i).getRolloverCost());
			obj.put(DeviceConstants.ROLLOVER_COST_PER, rolloverAnalysisObj.get(i).getRolloverCostPer());
			obj.put(DeviceConstants.ROLLOVER_PER, rolloverAnalysisObj.get(i).getRolloverPer());
			rolloverAnalysis.put(obj);
		}
		return rolloverAnalysis;
	}

	public static String getCategory(String filter) {
		String category = "";
		if (filter.equals(DeviceConstants.LONG_BUILDUP))
			category = "longbuildup";
		else if (filter.equals(DeviceConstants.SHORT_BUILDUP))
			category = "shortbuildup";
		else if (filter.equals(DeviceConstants.LONG_UNWINDING))
			category = "longbinding";
		else if (filter.equals(DeviceConstants.SHORT_COVERING))
			category = "shortbinding";
		return category;
	}

	public static String getDateString(String sAction, String sDate) throws ParseException {
		String sFormatedDate = DateUtils.formatDate(sDate, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
				DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
		if (sAction.equalsIgnoreCase(DeviceConstants.BONUS))
			return sFormatedDate + " " + DeviceConstants.EX_DATE;
		else
			return sFormatedDate + " " + DeviceConstants.ANNOUNCEMENT_DATE;
	}

	public static JSONArray sortArray(JSONArray arrayTosort, final String key, final String order) {
		if (arrayTosort != null) {
			List<JSONObject> JsonArrayAsList = new ArrayList<JSONObject>();
			for (int i = 0; i < arrayTosort.length(); i++)
				JsonArrayAsList.add(arrayTosort.getJSONObject(i));
			Collections.sort(JsonArrayAsList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {
					if (order.equals(DeviceConstants.ASCENDING))
						return obj1.getInt(key) - obj2.getInt(key);
					else
						return obj2.getInt(key) - obj1.getInt(key);
				}
			});
			JSONArray resArray = new JSONArray(JsonArrayAsList);
			return resArray;
		} else
			return null;
	}

	public static JSONObject getCorporateActionsOverview(String sAppID)
			throws JSONException, ParseException, GCException, CMOTSException {

		JSONObject finalObj = new JSONObject();

		int market_limit = AppConfig.getIntValue("market_limit");

		CorporateActionsList corpActionsList = new CorporateActionsList();

		corpActionsList = CorporateActionsCache.getCorporateActionListNextWeek();

		if (corpActionsList.size() == 0) {
			CorporateActionsCache.loadCorporateActionCacheNextWeek();
			corpActionsList = CorporateActionsCache.getCorporateActionListNextWeek();
		}

		List<JSONObject> parsedCorpActionsList = new ArrayList<JSONObject>();

		parsedCorpActionsList = getCorporateAction(corpActionsList);

		getCompAnnouncementOverview(parsedCorpActionsList, ExchangeSegment.NSE, sAppID);
		getCompAnnouncementOverview(parsedCorpActionsList, ExchangeSegment.BSE, sAppID);

		JSONArray corpActionsArr = new JSONArray();
		AdvanceQuote.sortJSONArrayDate(parsedCorpActionsList, DeviceConstants.EX_DATE_S,
				DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO, true);

		int count = 0;

		for (int i = 0; i < parsedCorpActionsList.size(); i++) {
			if (count == market_limit)
				break;

			JSONObject corpObj = parsedCorpActionsList.get(i);

			if (corpObj.getString(DeviceConstants.ACTION).equalsIgnoreCase(DeviceConstants.FILTER_COMP_ANNOUNCEMENTS)) {
				corpObj.remove(DeviceConstants.ACTION);
				corpObj.put(DeviceConstants.ACTION, DeviceConstants.COMPANY_ANNOUNCEMENT);
				corpObj.remove(DeviceConstants.EX_DATE_S);
				corpObj.put(DeviceConstants.EX_DATE_S, "");
			}
			corpActionsArr.put(parsedCorpActionsList.get(i));
			count++;
		}

		if (corpActionsArr.length() <= 0)
			throw new GCException(InfoIDConstants.NO_DATA);
		finalObj.put(DeviceConstants.CORP_ACTIONS, corpActionsArr);

		return finalObj;
	}

	static List<JSONObject> getCorporateAction(CorporateActionsList corpActionsList) {
		List<JSONObject> parsedCorpActionsList = new ArrayList<JSONObject>();

		for (CorporateActions corpAction : corpActionsList) {
			if (isValidAction(corpAction.getAction())) {
				try {
					SymbolRow symRow = AdvanceQuote.getSymbolRowUsingISIN((corpAction.getISIN()));

					if (symRow != null) {
						JSONObject corpActionObj = new JSONObject();
						corpActionObj.put(SymbolConstants.SYMBOL_OBJ,
								symRow.getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ));
						if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.SPLIT))
							corpActionObj.put(DeviceConstants.ACTION, DeviceConstants.STOCK_SPLIT);
						else
							corpActionObj.put(DeviceConstants.ACTION, corpAction.getAction());

						corpActionObj.put(DeviceConstants.EX_DATE_S,
								DateUtils.formatDate(corpAction.getCorpDate(),
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
										DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
						corpActionObj.put(DeviceConstants.ANNOUNCE_DATE,
								corpAction.getAnnouncementDate() == null ? "--"
										: DateUtils.formatDate(corpAction.getAnnouncementDate(),
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
						corpActionObj.put(DeviceConstants.RECORD_DATE,
								corpAction.getRecordDate() == null ? "--"
										: DateUtils.formatDate(corpAction.getRecordDate(),
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
												DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO));
						corpActionObj.put(DeviceConstants.TYPE,
								corpAction.getDivType().isEmpty() ? "" : corpAction.getDivType().toUpperCase());
						corpActionObj.put(DeviceConstants.DETAIL_DESC, "");
						corpActionObj.put(DeviceConstants.IS_SINGLE_DATE, "false");
						corpActionObj.put(DeviceConstants.COMPANY_NAME, corpAction.getCompanyName());

						if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.DIVIDEND))
							corpActionObj.put(DeviceConstants.SHORT_DESC,
									AdvanceQuote.getDividendShortDesc(corpAction.getValue()));
						else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.BONUS))
							corpActionObj.put(DeviceConstants.SHORT_DESC,
									AdvanceQuote.getBonusShortDesc(corpAction.getValue()));
						else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.SPLIT)) {
							String[] arrSplit = corpAction.getValue().split(":");
							String sSplitValue = arrSplit[1] + ":" + arrSplit[0];

							corpActionObj.put(DeviceConstants.SHORT_DESC, AdvanceQuote.getSplitsShortDesc(sSplitValue,
									corpAction.getFVBefore(), corpAction.getFVAfter()));
						} else if (corpAction.getAction().equalsIgnoreCase(DeviceConstants.RIGHTS))
							corpActionObj.put(DeviceConstants.SHORT_DESC,
									AdvanceQuote.getRightsShortDesc(corpAction.getValue(), corpAction.getPremium()));

						parsedCorpActionsList.add(corpActionObj);
					}
				} catch (Exception e) {
					log.warn(e);
				}
			}
		}

		return parsedCorpActionsList;
	}
}
