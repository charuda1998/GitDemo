package com.globecapital.business.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.business.market.Indices;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class OrderDetails_101 {

	private static Logger log = Logger.getLogger(OrderDetails.class);

	public static JSONObject getOrderPadDetailsForModifyOrder(String sSymbolToken, JSONObject orderInfo, boolean isSquareOff)
			throws Exception {
		JSONObject orderPadDetailRetObj = new JSONObject();

		String orderType = orderInfo.getString(OrderConstants.ORDER_TYPE);
		String orderAction = orderInfo.getString(OrderConstants.ORDER_ACTION);
		String productType = orderInfo.getString(OrderConstants.PRODUCT_TYPE);

		JSONObject symbolObj = SymbolMap.getSymbolRow(sSymbolToken).getJSONObject(SymbolConstants.SYMBOL_OBJ);
		String sMarketSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		String sExch = ExchangeSegment.getExchangeName(sMarketSegID);
		JSONArray associateExch = new JSONArray();
		JSONObject exchProps = new JSONObject();
		associateExch.put(ExchangeSegment.getExchangeName(sMarketSegID));

		JSONArray prodList = new JSONArray();
		prodList.put(productType);

		exchProps.put(sExch, addToExchObjModifyOrder(symbolObj, prodList, orderType, orderAction, isSquareOff)); 
		
		orderPadDetailRetObj.put(SymbolConstants.ASSOCIATE_EXCH, associateExch);
		orderPadDetailRetObj.put(SymbolConstants.EXCH_PROPS, exchProps);
		orderPadDetailRetObj.put(OrderConstants.IS_AMO, orderInfo.getString(OrderConstants.IS_AMO));
		orderPadDetailRetObj.put(OrderConstants.VALIDATE_DAILY_PRICE_RANGE,
				AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));

		return orderPadDetailRetObj;
	}
	
	public static JSONObject getOrderPadDetailsForModifyOrder_101(String sSymbolToken, JSONObject orderInfo, boolean isSquareOff, Session session)
			throws Exception {
		JSONObject orderPadDetailRetObj = new JSONObject();

		String orderType = orderInfo.getString(OrderConstants.ORDER_TYPE);
		String orderAction = orderInfo.getString(OrderConstants.ORDER_ACTION);
		String productType = orderInfo.getString(OrderConstants.PRODUCT_TYPE);

		JSONObject symbolObj = SymbolMap.getSymbolRow(sSymbolToken).getJSONObject(SymbolConstants.SYMBOL_OBJ);
		String sMarketSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		String sExch = ExchangeSegment.getExchangeName(sMarketSegID);
		JSONArray associateExch = new JSONArray();
		JSONObject exchProps = new JSONObject();
		associateExch.put(ExchangeSegment.getExchangeName(sMarketSegID));

		JSONArray prodList = new JSONArray();
		prodList.put(productType);
		exchProps.put(sExch, addToExchObjModifyOrder_101(symbolObj, prodList, orderType, orderAction, isSquareOff, session));

		//To add Dpr 
		JSONObject dprValidate = new JSONObject();

		if(orderInfo.getString(OrderConstants.IS_AMO).equalsIgnoreCase("true")) {
			dprValidate.put(OrderConstants.REGULAR,AppConfig.optValue("order.validateDPRForAMO", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.BRACKET,AppConfig.optValue("order.validateDPRForAMO", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.COVER,AppConfig.optValue("order.validateDPRForAMO", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.GTC,AppConstants.STR_FALSE);
		}else {
			dprValidate.put(OrderConstants.REGULAR,AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.BRACKET,AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.COVER,AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.GTC,AppConstants.STR_FALSE);
		}
		
		orderPadDetailRetObj.put(SymbolConstants.ASSOCIATE_EXCH, associateExch);
		orderPadDetailRetObj.put(SymbolConstants.EXCH_PROPS, exchProps);
		orderPadDetailRetObj.put(OrderConstants.IS_AMO, orderInfo.getString(OrderConstants.IS_AMO));
		orderPadDetailRetObj.put(OrderConstants.VALIDATE_DAILY_PRICE_RANGE,dprValidate);
		return orderPadDetailRetObj;
	}

	public static JSONObject getOrderPadDetails(String sSymbolToken, JSONArray prodList, boolean isOrderPad, Session session)
			throws Exception {
		boolean isIndex = false, isIndexDerivative = false;
		JSONObject symbolObj = new JSONObject();

		if (SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			symbolObj = SymbolMap.getSymbolRow(sSymbolToken).getJSONObject(SymbolConstants.SYMBOL_OBJ);
			String instrument = SymbolMap.getSymbolRow(sSymbolToken).getInstrument();
			if (InstrumentType.isIndex(instrument))
				isIndexDerivative = true;
		} else if (Indices.isValidIndex(sSymbolToken)) {
			symbolObj = Indices.getSymbolRow(sSymbolToken).getJSONObject(SymbolConstants.SYMBOL_OBJ);
			isIndex = true;
		}

		String sMarketSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		String sExch = ExchangeSegment.getExchangeName(sMarketSegID);
		String symbol = symbolObj.getString(SymbolConstants.SYMBOL);

		JSONObject orderPadDetail = new JSONObject();

		if (isOrderPad && (!checkSegIDSupported(sMarketSegID, prodList))) {
			JSONArray associateExch = new JSONArray();
			JSONObject exchProps = new JSONObject();
			associateExch.put(sExch);
			exchProps.put(sExch, addToExchObj(symbolObj, prodList, isOrderPad, true, isIndex, session));
			orderPadDetail.put(OrderConstants.ORDER_ALLOWED, "false");
			orderPadDetail.put(OrderConstants.REASON,
					String.format(InfoMessage.getInfoMSG("info_msg.order_exchange_not_supported"), sExch));
			orderPadDetail.put(SymbolConstants.ASSOCIATE_EXCH, associateExch);
			orderPadDetail.put(SymbolConstants.EXCH_PROPS, exchProps);
			return orderPadDetail;
		}

		if (isIndex)
			orderPadDetail.put(DeviceConstants.SHOW_ONLY_OHLC, "true");
		else
			orderPadDetail.put(DeviceConstants.SHOW_ONLY_OHLC, "false");

		if (isOrderPad)
			getOrderPadDetails(orderPadDetail, sExch);
		else
			getQuoteSymbolDetails(orderPadDetail, sExch, isIndex, isIndexDerivative, symbol);

		getExchangeDetails(orderPadDetail, symbolObj, prodList, isOrderPad, isIndex, session);

		return orderPadDetail;
	}

	private static void getQuoteSymbolDetails(JSONObject orderPadDetail, String sExch, boolean isIndex,
			boolean isIndexDerivative, String symbol) throws Exception {

		String sMarketSegID = ExchangeSegment.getMarketSegmentID(sExch);
		orderPadDetail.put(DeviceConstants.CHART_IQ_URL, AppConfig.getValue("chart_iq_url"));
		if (sExch.equals(ExchangeSegment.NFO))
			orderPadDetail.put(DeviceConstants.IS_FNO, "true");
		else
			orderPadDetail.put(DeviceConstants.IS_FNO, "false");

		orderPadDetail.put(DeviceConstants.IS_EQUITY, Boolean.toString(ExchangeSegment.isEquitySegment(sMarketSegID)));
		orderPadDetail.put(DeviceConstants.IS_COMMODITY,
				Boolean.toString(ExchangeSegment.isCommoditySegment(sMarketSegID)));
		orderPadDetail.put(DeviceConstants.IS_CURRENCY,
				Boolean.toString(ExchangeSegment.isCurrencySegment(sMarketSegID)));

		if (sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			orderPadDetail.put(DeviceConstants.SHOW_DETAIL_QUOTE, "false");
		else
			orderPadDetail.put(DeviceConstants.SHOW_DETAIL_QUOTE, "true");

		if (ExchangeSegment.isEquitySegment(sMarketSegID) || sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			orderPadDetail.put(DeviceConstants.APPLICABLE_TABS, getEquityApplicableTabs(symbol));
		else if (ExchangeSegment.isCurrencySegment(sMarketSegID))
			orderPadDetail.put(DeviceConstants.APPLICABLE_TABS, getCurrencyApplicableTabs());
		else if (sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
			orderPadDetail.put(DeviceConstants.APPLICABLE_TABS, getMCXApplicableTabs());
		else if (sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			orderPadDetail.put(DeviceConstants.APPLICABLE_TABS, getNCDEXApplicableTabs());

		if (isIndex)
			orderPadDetail.put(DeviceConstants.APPLICABLE_TABS, getIndexApplicableTabs());

		if (isIndexDerivative)
			orderPadDetail.put(DeviceConstants.APPLICABLE_TABS, getIndexDerivativeApplicableTabs());

	}

	private static void getOrderPadDetails(JSONObject orderPadDetail, String sExch) throws Exception {
		orderPadDetail.put(DeviceConstants.IS_EQUITY,
				Boolean.toString(ExchangeSegment.isEquitySegment(ExchangeSegment.getMarketSegmentID(sExch))));
		orderPadDetail.put(OrderConstants.IS_AMO, Boolean.toString(AMODetails.isAMOOrder(sExch)));
		orderPadDetail.put(OrderConstants.VALIDATE_DAILY_PRICE_RANGE,AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));
		orderPadDetail.put(OrderConstants.NORMAL_ORDER_PRODUCT_TYPE_LIST,
				ProductType.getApplicableProductTypesForFEPrefernces());
		orderPadDetail.put(OrderConstants.LAST_USED_PROD_TYPE_PREFERENCE,
				AppConfig.optValue("order.lastUsedProdType", AppConstants.STR_FALSE));

		orderPadDetail.put(OrderConstants.ORDER_ALLOWED, "true");
		orderPadDetail.put(OrderConstants.REASON, "");

	}
	
	private static void getOrderPadDetails_101(JSONObject orderPadDetail, String sExch) throws Exception {
		JSONObject dprValidate = new JSONObject();
		if(Boolean.toString(AMODetails.isAMOOrder(sExch)).equalsIgnoreCase("true")) {
			dprValidate.put(OrderConstants.REGULAR,AppConfig.optValue("order.validateDPRForAMO", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.BRACKET,AppConfig.optValue("order.validateDPRForAMO", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.COVER,AppConfig.optValue("order.validateDPRForAMO", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.GTC,AppConstants.STR_FALSE);
		}else {
			dprValidate.put(OrderConstants.REGULAR,AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.BRACKET,AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.COVER,AppConfig.optValue("order.validateDPR", AppConstants.STR_FALSE));
			dprValidate.put(OrderConstants.GTC,AppConstants.STR_FALSE);
		}
		orderPadDetail.put(DeviceConstants.IS_EQUITY,
				Boolean.toString(ExchangeSegment.isEquitySegment(ExchangeSegment.getMarketSegmentID(sExch))));
		orderPadDetail.put(OrderConstants.IS_AMO, Boolean.toString(AMODetails.isAMOOrder(sExch)));
		orderPadDetail.put(OrderConstants.VALIDATE_DAILY_PRICE_RANGE,dprValidate);
		orderPadDetail.put(OrderConstants.NORMAL_ORDER_PRODUCT_TYPE_LIST,
				ProductType.getApplicableProductTypesForFEPrefernces());
		orderPadDetail.put(OrderConstants.LAST_USED_PROD_TYPE_PREFERENCE,
				AppConfig.optValue("order.lastUsedProdType", AppConstants.STR_FALSE));

		orderPadDetail.put(OrderConstants.ORDER_ALLOWED, "true");
		orderPadDetail.put(OrderConstants.REASON, "");

	}

	private static void getExchangeDetails(JSONObject orderPadDetail, JSONObject symbolObj, JSONArray prodList,
			boolean isOrderPad, boolean isIndex, Session session) throws Exception {
		JSONArray associateExch = new JSONArray();
		JSONObject exchProps = new JSONObject();

		String sMarketSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		String sExch = ExchangeSegment.getExchangeName(sMarketSegID);

		String sAssociatedSegID = getAssociateSegID(sMarketSegID);
		boolean isAssociateExch = false;

		if (sAssociatedSegID != null)
			isAssociateExch = checkSegIDSupported(sAssociatedSegID, prodList) | !isOrderPad;

		if (isAssociateExch && !isIndex) {
			JSONObject associateSymObj = null;
			boolean isDerivative = checkForDerivatives(sAssociatedSegID);
			if (isDerivative)
				associateSymObj = getAssociateDerivativeSymbolInfo(symbolObj, sAssociatedSegID);
			else if (ExchangeSegment.isCurrencySegment(sAssociatedSegID)) {
				associateSymObj = getAssociateCurrencySymbolInfo(symbolObj, sAssociatedSegID);
			} else
				associateSymObj = getAssociateSymbolInfo(symbolObj, sAssociatedSegID);

			if (associateSymObj != null) {
				String sAssociatedExch = ExchangeSegment.getExchangeName(sAssociatedSegID);
				if (sExch.equals(ExchangeSegment.NSE) || sExch.equals(ExchangeSegment.MCX)) {
					associateExch.put(sExch);
					exchProps.put(sExch, addToExchObj(symbolObj, prodList, isOrderPad, false, isIndex, session));
					associateExch.put(sAssociatedExch);
					exchProps.put(sAssociatedExch,
							addToExchObj(associateSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ), prodList,
									isOrderPad, false, isIndex, session));
				} else {
					associateExch.put(sAssociatedExch);
					exchProps.put(sAssociatedExch,
							addToExchObj(associateSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ), prodList,
									isOrderPad, false, isIndex, session));
					associateExch.put(sExch);
					exchProps.put(sExch, addToExchObj(symbolObj, prodList, isOrderPad, false, isIndex, session));
				}

			} else {
				associateExch.put(sExch);
				exchProps.put(sExch, addToExchObj(symbolObj, prodList, isOrderPad, false, isIndex, session));
			}

		} else {
			associateExch.put(sExch);
			exchProps.put(sExch, addToExchObj(symbolObj, prodList, isOrderPad, false, isIndex, session));

		}

		orderPadDetail.put(SymbolConstants.ASSOCIATE_EXCH, associateExch);
		orderPadDetail.put(SymbolConstants.EXCH_PROPS, exchProps);

	}
	
	private static void getExchangeDetails_101(JSONObject orderPadDetail, JSONObject symbolObj, JSONArray prodList,
			boolean isOrderPad, boolean isIndex, Session session) throws Exception {
		JSONArray associateExch = new JSONArray();
		JSONObject exchProps = new JSONObject();

		String sMarketSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		String sExch = ExchangeSegment.getExchangeName(sMarketSegID);

		String sAssociatedSegID = getAssociateSegID(sMarketSegID);
		boolean isAssociateExch = false;

		if (sAssociatedSegID != null)
			isAssociateExch = checkSegIDSupported(sAssociatedSegID, prodList) | !isOrderPad;

		if (isAssociateExch && !isIndex) {
			JSONObject associateSymObj = null;
			boolean isDerivative = checkForDerivatives(sAssociatedSegID);
			if (isDerivative)
				associateSymObj = getAssociateDerivativeSymbolInfo(symbolObj, sAssociatedSegID);
			else if (ExchangeSegment.isCurrencySegment(sAssociatedSegID)) {
				associateSymObj = getAssociateCurrencySymbolInfo(symbolObj, sAssociatedSegID);
			} else
				associateSymObj = getAssociateSymbolInfo(symbolObj, sAssociatedSegID);

			if (associateSymObj != null) {
				String sAssociatedExch = ExchangeSegment.getExchangeName(sAssociatedSegID);
				if (sExch.equals(ExchangeSegment.NSE) || sExch.equals(ExchangeSegment.MCX)) {
					associateExch.put(sExch);
					exchProps.put(sExch, addToExchObj_101(symbolObj, prodList, isOrderPad, false, isIndex, session));
					associateExch.put(sAssociatedExch);
					exchProps.put(sAssociatedExch,
							addToExchObj_101(associateSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ), prodList,
									isOrderPad, false, isIndex, session));
				} else {
					associateExch.put(sAssociatedExch);
					exchProps.put(sAssociatedExch,
							addToExchObj_101(associateSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ), prodList,
									isOrderPad, false, isIndex, session));
					associateExch.put(sExch);
					exchProps.put(sExch, addToExchObj_101(symbolObj, prodList, isOrderPad, false, isIndex, session));
				}

			} else {
				associateExch.put(sExch);
				exchProps.put(sExch, addToExchObj_101(symbolObj, prodList, isOrderPad, false, isIndex, session));
			}

		} else {
			associateExch.put(sExch);
			exchProps.put(sExch, addToExchObj_101(symbolObj, prodList, isOrderPad, false, isIndex, session));

		}

		orderPadDetail.put(SymbolConstants.ASSOCIATE_EXCH, associateExch);
		orderPadDetail.put(SymbolConstants.EXCH_PROPS, exchProps);

	}

	private static JSONObject getAssociateCurrencySymbolInfo(JSONObject symbolObj, String sAssociatedSegID)
			throws Exception {
		JSONObject symbol = new JSONObject();
		String sSymbolUniqDesc = symbolObj.getString(SymbolConstants.SYMBOL_UNIQ_DESC);

		String[] arrSymbolUniqDesc = sSymbolUniqDesc.split("_");

		symbol = SymbolMap
				.getSymbolUniqDescRow(arrSymbolUniqDesc[0] + "_" + ExchangeSegment.getExchangeName(sAssociatedSegID));
		return symbol;
	}

	private static JSONObject getNCDEXApplicableTabs() {
		JSONObject applicableTabsObj = new JSONObject();

		applicableTabsObj.put(DeviceConstants.STOCK_ALERT, "false");
		applicableTabsObj.put(DeviceConstants.EVENTS, "false");
		applicableTabsObj.put(DeviceConstants.KEY_STATS, "false");
		applicableTabsObj.put(DeviceConstants.FNO, "false");
		applicableTabsObj.put(DeviceConstants.NEWS, "true");
		applicableTabsObj.put(DeviceConstants.FINANCIALS, "false");
		applicableTabsObj.put(DeviceConstants.SH_PATTERN, "false");
		applicableTabsObj.put(DeviceConstants.SHOW_DETAIL_QUOTE, "false");
		applicableTabsObj.put(DeviceConstants.DEPTH, "true");
		applicableTabsObj.put(DeviceConstants.SHOW_ONLY_OHLC, "false");

		return applicableTabsObj;
	}

	private static JSONObject getMCXApplicableTabs() {
		JSONObject applicableTabsObj = new JSONObject();

		applicableTabsObj.put(DeviceConstants.STOCK_ALERT, "false");
		applicableTabsObj.put(DeviceConstants.EVENTS, "false");
		applicableTabsObj.put(DeviceConstants.KEY_STATS, "false");
		applicableTabsObj.put(DeviceConstants.FNO, "false");
		applicableTabsObj.put(DeviceConstants.NEWS, "true");
		applicableTabsObj.put(DeviceConstants.FINANCIALS, "false");
		applicableTabsObj.put(DeviceConstants.SH_PATTERN, "false");
		applicableTabsObj.put(DeviceConstants.SHOW_DETAIL_QUOTE, "true");
		applicableTabsObj.put(DeviceConstants.DEPTH, "true");
		applicableTabsObj.put(DeviceConstants.SHOW_ONLY_OHLC, "false");

		return applicableTabsObj;
	}

	private static JSONObject getCurrencyApplicableTabs() {
		JSONObject applicableTabsObj = new JSONObject();

		applicableTabsObj.put(DeviceConstants.STOCK_ALERT, "false");
		applicableTabsObj.put(DeviceConstants.EVENTS, "false");
		applicableTabsObj.put(DeviceConstants.KEY_STATS, "false");
		applicableTabsObj.put(DeviceConstants.FNO, "false");
		applicableTabsObj.put(DeviceConstants.NEWS, "false");
		applicableTabsObj.put(DeviceConstants.FINANCIALS, "false");
		applicableTabsObj.put(DeviceConstants.SH_PATTERN, "false");
		applicableTabsObj.put(DeviceConstants.SHOW_DETAIL_QUOTE, "true");
		applicableTabsObj.put(DeviceConstants.DEPTH, "true");
		applicableTabsObj.put(DeviceConstants.SHOW_ONLY_OHLC, "false");

		return applicableTabsObj;
	}

	private static JSONObject getEquityApplicableTabs(String symbol) {
		JSONObject applicableTabsObj = new JSONObject();

		applicableTabsObj.put(DeviceConstants.STOCK_ALERT, "true");
		applicableTabsObj.put(DeviceConstants.EVENTS, "true");
		applicableTabsObj.put(DeviceConstants.KEY_STATS, "true");
		if(SymbolMap.isNFOExists(symbol))
			applicableTabsObj.put(DeviceConstants.FNO, "true");
		else
			applicableTabsObj.put(DeviceConstants.FNO, "false");
		applicableTabsObj.put(DeviceConstants.NEWS, "true");
		applicableTabsObj.put(DeviceConstants.FINANCIALS, "true");
		applicableTabsObj.put(DeviceConstants.SH_PATTERN, "true");
		applicableTabsObj.put(DeviceConstants.SHOW_DETAIL_QUOTE, "true");
		applicableTabsObj.put(DeviceConstants.DEPTH, "true");
		applicableTabsObj.put(DeviceConstants.SHOW_ONLY_OHLC, "false");

		return applicableTabsObj;
	}

	private static JSONObject getIndexApplicableTabs() {
		JSONObject applicableTabsObj = new JSONObject();

		applicableTabsObj.put(DeviceConstants.STOCK_ALERT, "false");
		applicableTabsObj.put(DeviceConstants.EVENTS, "false");
		applicableTabsObj.put(DeviceConstants.KEY_STATS, "false");
		applicableTabsObj.put(DeviceConstants.FNO, "false");
		applicableTabsObj.put(DeviceConstants.NEWS, "false");
		applicableTabsObj.put(DeviceConstants.FINANCIALS, "false");
		applicableTabsObj.put(DeviceConstants.SH_PATTERN, "false");
		applicableTabsObj.put(DeviceConstants.SHOW_DETAIL_QUOTE, "false");
		applicableTabsObj.put(DeviceConstants.DEPTH, "false");
		applicableTabsObj.put(DeviceConstants.SHOW_ONLY_OHLC, "true"); // OpenHighLowClose should be shown for Index
																		// alone. This is controlled form MW

		return applicableTabsObj;
	}

	private static JSONObject getIndexDerivativeApplicableTabs() {
		JSONObject applicableTabsObj = new JSONObject();

		applicableTabsObj.put(DeviceConstants.STOCK_ALERT, "false");
		applicableTabsObj.put(DeviceConstants.EVENTS, "false");
		applicableTabsObj.put(DeviceConstants.KEY_STATS, "false");
		applicableTabsObj.put(DeviceConstants.FNO, "true");
		applicableTabsObj.put(DeviceConstants.NEWS, "false");
		applicableTabsObj.put(DeviceConstants.FINANCIALS, "false");
		applicableTabsObj.put(DeviceConstants.SH_PATTERN, "false");
		applicableTabsObj.put(DeviceConstants.SHOW_DETAIL_QUOTE, "true");
		applicableTabsObj.put(DeviceConstants.DEPTH, "true");
		applicableTabsObj.put(DeviceConstants.SHOW_ONLY_OHLC, "true");

		return applicableTabsObj;
	}

	public static String getAssociateSegID(String sMarketSegID) {
		String sAssociatedSegID = null;
		if (sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
			sAssociatedSegID = ExchangeSegment.BSE_SEGMENT_ID;
		else if (sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
			sAssociatedSegID = ExchangeSegment.NSE_SEGMENT_ID;
		else if (sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
			sAssociatedSegID = ExchangeSegment.NCDEX_SEGMENT_ID;
		else if (sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			sAssociatedSegID = ExchangeSegment.MCX_SEGMENT_ID;
		else if (sMarketSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
			sAssociatedSegID = ExchangeSegment.BSECDS_SEGMENT_ID;
		else if (sMarketSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
			sAssociatedSegID = ExchangeSegment.NSECDS_SEGMENT_ID;

		return sAssociatedSegID;
	}

	public static boolean checkForDerivatives(String sSegmentID) {
		if (sSegmentID.equals(ExchangeSegment.MCX_SEGMENT_ID) || sSegmentID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			return true;
		else
			return false;
	}

	public static boolean checkSegIDSupported(String sSearchMktSegID, JSONArray prodList) {
		for (int i = 0; i < prodList.length(); i++) {
			JSONObject prodDetails = prodList.getJSONObject(i);

			String sMarketSegID = prodDetails.getString(SymbolConstants.MKT_SEG_ID);
			if (sMarketSegID.equals(sSearchMktSegID))
				return true;
		}

		return false;
	}

	public static JSONObject getAssociateSymbolInfo(JSONObject symbolObj, String sMarketSegID) throws SQLException {
		JSONObject symbol = new JSONObject();
		String sISIN = symbolObj.getString(SymbolConstants.ISIN);
		symbol = SymbolMap.getISINSymbolRow(sISIN + "_" + sMarketSegID);
		return symbol;
	}

	public static JSONObject getAssociateDerivativeSymbolInfo(JSONObject symbolObj, String sMarketSegID)
			throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		String sToken;
		String sSymbol = symbolObj.getString(SymbolConstants.SYMBOL);
		String sExpiry = symbolObj.getString(SymbolConstants.EXPIRY);

		JSONObject symbol = null;

		String query = DBQueryConstants.GET_DERIVATIVE_TOKEN;
		log.debug(query);

		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, sSymbol);
			ps.setString(2, sMarketSegID);
			ps.setString(3, sExpiry);

			res = ps.executeQuery();

			if (res.next()) {
				sToken = res.getString(1);
			} else {
				return symbol;
			}
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}

		symbol = SymbolMap.getSymbolRow(sToken, sMarketSegID);

		return symbol;
	}

	public static JSONObject addToExchObj(JSONObject symbolObj, JSONArray prodList, boolean isOrderPad,
			boolean isExchNotSupported, boolean isIndex, Session session) throws JSONException, ParseException, Exception {
		JSONObject exchObj = new JSONObject();
		String sSearchMktSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		JSONObject minimisedSymObj = new JSONObject();
		SymbolRow symbolRow = null;
		int lotsize;
		String series = "", remarks = "", isin = "";
		if (isIndex)
		{
			symbolRow = Indices.getSymbolRow(symbolObj.getString(SymbolConstants.SYMBOL_TOKEN));
			lotsize=symbolRow.getLotSizeInt();
			minimisedSymObj = symbolRow.getMinimisedSymbolRow();
			series = symbolRow.getSeries();
			isin = symbolRow.getISIN();
		}
		else
		{
			symbolRow = SymbolMap.getSymbolRow(symbolObj.getString(SymbolConstants.SYMBOL_TOKEN));
			lotsize=symbolRow.getLotSizeInt();
			minimisedSymObj = symbolRow.getMinimisedSymbolRow();
			series = symbolRow.getSeries();
			isin = symbolRow.getISIN();
		}

		if (isOrderPad) {
			exchObj.put(SymbolConstants.SYMBOL_OBJ, minimisedSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ));
			if (isIndex)
				exchObj.put(OrderConstants.ORDER_ACTION, new JSONArray());
			else
				exchObj.put(OrderConstants.ORDER_ACTION, getOrderActions(""));

			exchObj.put(OrderConstants.VALIDITY, Validity.getValidities());
			JSONArray productList = new JSONArray();

			if (isExchNotSupported)
				productList = ProductType.getDefaultProductTypes(sSearchMktSegID);
			else
				productList = getProductList(sSearchMktSegID, prodList);

			// TODO: To be removed, once we get clarification from the FT for AMO MTF
			if (AMODetails.isAMOOrder(ExchangeSegment.getExchangeName(sSearchMktSegID))) {
				for (int i = 0; i < productList.length(); i++) {
					if (productList.getString(i).equalsIgnoreCase(ProductType.MTF))
						productList.remove(i);
				}
			}
			exchObj.put(OrderConstants.SHOW_LOT, isShowLot(sSearchMktSegID,lotsize));
			exchObj.put(OrderConstants.SHOW_DISC_QTY, Boolean.toString(isShowDiscQty(sSearchMktSegID)));
			exchObj.put(OrderConstants.DISC_PER, getDiscPercent(sSearchMktSegID));
			exchObj.put(UserInfoConstants.PRODUCT_TYPE, productList);
			exchObj.put(OrderConstants.ORDER_TYPE_PROPS, getOrderList(productList));
			if(sSearchMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)) {
                if(series.trim().equals("BE"))
                	remarks = AppConfig.getValue("warn.trade_to_trade");
				if(!remarks.equalsIgnoreCase("NULL") && !remarks.isEmpty()) {
					exchObj.put(DeviceConstants.IS_CAUTION, "true");
					exchObj.put(DeviceConstants.CAUTION_MSG, remarks);
				}
			}else if(sSearchMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)) {
                if(series.trim().equals("T"))
                	remarks = AppConfig.getValue("warn.trade_to_trade");
				if(!remarks.equalsIgnoreCase("NULL") && !remarks.isEmpty()) {
					exchObj.put(DeviceConstants.IS_CAUTION, "true");
					exchObj.put(DeviceConstants.CAUTION_MSG, remarks);
				}
			}
		} else {
			exchObj.put(SymbolConstants.SYMBOL_OBJ, minimisedSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ));
			if (isIndex)
				exchObj.put(OrderConstants.ORDER_ACTION, new JSONArray());
			else
				exchObj.put(OrderConstants.ORDER_ACTION, getOrderActions(""));
			if ((sSearchMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)
					|| sSearchMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)) && !isIndex) {
				exchObj.put(OrderConstants.SHOW_PEER_COMP, true);
			} else {
				exchObj.put(OrderConstants.SHOW_PEER_COMP, false);
			}
			
			exchObj.put(DeviceConstants.CHART_REQ_OBJ, getChartObj(symbolRow, session));

		}

		return exchObj;
	}

	private static String getDiscPercent(String sSearchMktSegID) throws AppConfigNoKeyFoundException {

		if (ExchangeSegment.isCommoditySegment(sSearchMktSegID))
			return AppConfig.getValue("disclosed_qty_min_percent_commodity");
		else
			return AppConfig.getValue("disclosed_qty_min_percent_non_commodity");
	}

	private static boolean isShowLot(String sSearchMktSegID, int lotsize) {
		if ((sSearchMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)
				|| sSearchMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))&&  lotsize == 1)
			return false;
		else
			return true;
	}

	private static boolean isShowDiscQty(String sSearchMktSegID) {
		if (sSearchMktSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			return false;
		else
			return true;
	}

	public static JSONObject addToExchObjModifyOrder(JSONObject symbolObj, JSONArray prodList, String orderType,
			String orderAction, boolean isSquareOff) throws JSONException, AppConfigNoKeyFoundException {
		JSONObject exchObj = new JSONObject();
		String sSearchMktSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		exchObj.put(OrderConstants.ORDER_ACTION, getOrderActions(orderAction));
		exchObj.put(OrderConstants.VALIDITY, Validity.getValidities());
		exchObj.put(OrderConstants.SHOW_LOT, isShowLot(sSearchMktSegID,Integer.parseInt(symbolObj.getString(SymbolConstants.LOT_SIZE))));
		exchObj.put(OrderConstants.SHOW_DISC_QTY, Boolean.toString(isShowDiscQty(sSearchMktSegID)));
		if(isSquareOff)
			exchObj.put(OrderConstants.ORDER_TYPE_PROPS, getOrderList(prodList));
		else
			exchObj.put(OrderConstants.ORDER_TYPE_PROPS, getOrderListForModify(prodList, orderType));
		exchObj.put(OrderConstants.DISC_PER, getDiscPercent(sSearchMktSegID));
		return exchObj;

	}
	
	public static JSONObject addToExchObjModifyOrder_101(JSONObject symbolObj, JSONArray prodList, String orderType,
			String orderAction, boolean isSquareOff, Session session) throws JSONException, AppConfigNoKeyFoundException {
		JSONObject exchObj = new JSONObject();
		String sSearchMktSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		JSONArray exchGTD = session.getUserInfo().getJSONArray(UserInfoConstants.ALLOWED_GTD_EXCH);
		exchObj.put(OrderConstants.ORDER_ACTION, getOrderActions(orderAction));
		exchObj.put(OrderConstants.VALIDITY, Validity.getValidities());
		exchObj.put(OrderConstants.SHOW_LOT, isShowLot(sSearchMktSegID,Integer.parseInt(symbolObj.getString(SymbolConstants.LOT_SIZE))));
		exchObj.put(OrderConstants.SHOW_DISC_QTY, Boolean.toString(isShowDiscQty(sSearchMktSegID)));
		JSONObject productListObj = getProductModifyList_101(sSearchMktSegID, prodList, exchGTD);
		if(isSquareOff)
			exchObj.put(OrderConstants.ORDER_TYPE_PROPS, getOrderList_101(prodList, productListObj));
		else
			exchObj.put(OrderConstants.ORDER_TYPE_PROPS, getOrderListForModify_101(prodList, orderType, productListObj));
		exchObj.put(OrderConstants.DISC_PER, getDiscPercent(sSearchMktSegID));
		return exchObj;

	}

	public static JSONArray getOrderActions(String defaultValue) {
		JSONArray orderActions = new JSONArray();
		if (defaultValue.length() < 1) {
			orderActions.put(OrderAction.BUY);
			orderActions.put(OrderAction.SELL);
			// orderActions.put(MoonConstants.SIP);

		} else {
			orderActions.put(defaultValue);
		}
		return orderActions;
	}

	public static JSONArray getProductList(String sSearchMktSegID, JSONArray prodList) {
		JSONArray productList = new JSONArray();
		for (int i = 0; i < prodList.length(); i++) {
			JSONObject prodDetails = prodList.getJSONObject(i);

			String sMarketSegID = prodDetails.getString(SymbolConstants.MKT_SEG_ID);
			if (sMarketSegID.equals(sSearchMktSegID)) {
				productList = prodDetails.getJSONArray(UserInfoConstants.PRODUCT_LIST);
			}

		}

		return productList;

	}

	public static JSONObject getOrderList(JSONArray productList) {
		JSONObject orderTypeList = new JSONObject();
		for (int i = 0; i < productList.length(); i++) {
			String sProduct = productList.getString(i);

			JSONArray orderList = new JSONArray();

			if (sProduct.equals(ProductType.DELIVERY) || sProduct.equals(ProductType.INTRADAY)
					|| sProduct.equals(ProductType.MARGIN) || sProduct.equals(ProductType.MTF)
					|| sProduct.equals(ProductType.CARRYFORWARD)) {
				orderList.put(OrderConstants.LIMIT);
				orderList.put(OrderConstants.MARKET);
				orderList.put(OrderConstants.NORMAL);
				orderList.put(OrderConstants.STOP_LOSS);
			} else if (sProduct.equals(ProductType.BRACKET_ORDER)) {
				orderList.put(OrderConstants.LIMIT);
				orderList.put(OrderConstants.NORMAL);
			} else if (sProduct.equals(OrderConstants.PRODUCT_COVER_ORDER)) {
	            orderList.put(OrderConstants.NORMAL);
				orderList.put(OrderConstants.MARKET);
			}
			orderTypeList.put(sProduct, orderList);

		}

		return orderTypeList;
	}
	
	public static JSONObject getOrderList_101(JSONArray productList, JSONObject prodList) {
		JSONObject orderTypeList = new JSONObject();
		for (int i = 0; i < productList.length(); i++) {
			String sProduct = productList.getString(i);

			JSONArray orderList = new JSONArray();

			if (sProduct.equals(ProductType.DELIVERY) || sProduct.equals(ProductType.INTRADAY)
					|| sProduct.equals(ProductType.MARGIN) || sProduct.equals(ProductType.MTF)
					|| sProduct.equals(ProductType.CARRYFORWARD)) {
				orderList.put(OrderConstants.LIMIT);
				orderList.put(OrderConstants.MARKET);
				orderList.put(OrderConstants.NORMAL);
				orderList.put(OrderConstants.STOP_LOSS);
			} else if (sProduct.equals(ProductType.BRACKET_ORDER)) {
				orderList.put(OrderConstants.LIMIT);
				orderList.put(OrderConstants.NORMAL);
			} else if (sProduct.equals(OrderConstants.PRODUCT_COVER_ORDER)) {
                orderList.put(OrderConstants.NORMAL);
				orderList.put(OrderConstants.MARKET);			
			}
			orderTypeList.put(sProduct, orderList);

		}
		if(prodList.has(OrderConstants.GTC)) {
			JSONArray orderList = new JSONArray();
			orderList.put(OrderConstants.LIMIT);
			orderList.put(OrderConstants.NORMAL);
			orderTypeList.put(OrderConstants.GTC,orderList);
		}
		return orderTypeList;
	}

	public static JSONObject getOrderListForModify(JSONArray productList, String sOrderType) {
		JSONObject orderTypeList = new JSONObject();
		for (int i = 0; i < productList.length(); i++) {
			String sProduct = productList.getString(i);
			JSONArray orderList = new JSONArray();

			if (sProduct.equals(ProductType.DELIVERY) || sProduct.equals(ProductType.INTRADAY)
					|| sProduct.equals(ProductType.MARGIN) || sProduct.equals(ProductType.MTF)
					|| sProduct.equals(ProductType.CARRYFORWARD)) {
				if (sOrderType.equalsIgnoreCase(OrderType.REGULAR_LOT_LIMIT)
						|| sOrderType.equalsIgnoreCase(OrderType.REGULAR_LOT_MARKET)) {
					orderList.put(OrderConstants.LIMIT);
					orderList.put(OrderConstants.MARKET);
					orderList.put(OrderConstants.NORMAL);
				} else {
					orderList.put(OrderConstants.LIMIT);
					orderList.put(OrderConstants.MARKET);
					orderList.put(OrderConstants.STOP_LOSS);
					orderList.put(OrderConstants.NORMAL);
				}

			} else if (sProduct.equals(ProductType.BRACKET_ORDER)) {
				orderList.put(OrderConstants.LIMIT);
				orderList.put(OrderConstants.NORMAL);
			} else if (sProduct.equals(OrderConstants.PRODUCT_COVER_ORDER)) {
				orderList.put(OrderConstants.MARKET);
				orderList.put(OrderConstants.NORMAL);
			}
			orderTypeList.put(sProduct, orderList);

		}
		return orderTypeList;
	}
	
	public static JSONObject getOrderListForModify_101(JSONArray productList, String sOrderType, JSONObject prodList) {
		JSONObject orderTypeList = new JSONObject();
		for (int i = 0; i < productList.length(); i++) {
			String sProduct = productList.getString(i);
			JSONArray orderList = new JSONArray();

			if (sProduct.equals(ProductType.DELIVERY) || sProduct.equals(ProductType.INTRADAY)
					|| sProduct.equals(ProductType.MARGIN) || sProduct.equals(ProductType.MTF)
					|| sProduct.equals(ProductType.CARRYFORWARD)) {
				if (sOrderType.equalsIgnoreCase(OrderType.REGULAR_LOT_LIMIT)
						|| sOrderType.equalsIgnoreCase(OrderType.REGULAR_LOT_MARKET)) {
					orderList.put(OrderConstants.LIMIT);
					orderList.put(OrderConstants.MARKET);
					orderList.put(OrderConstants.NORMAL);
				} else {
					orderList.put(OrderConstants.LIMIT);
					orderList.put(OrderConstants.MARKET);
					orderList.put(OrderConstants.STOP_LOSS);
					orderList.put(OrderConstants.NORMAL);
				}

			} else if (sProduct.equals(ProductType.BRACKET_ORDER)) {
				orderList.put(OrderConstants.LIMIT);
				orderList.put(OrderConstants.NORMAL);
			} else if (sProduct.equals(OrderConstants.PRODUCT_COVER_ORDER)) {
	            orderList.put(OrderConstants.NORMAL);
				orderList.put(OrderConstants.MARKET);			
			}
			orderTypeList.put(sProduct, orderList);

		}
		
		if(prodList.has(OrderConstants.GTC)) {
			JSONArray orderList = new JSONArray();
			orderList.put(OrderConstants.LIMIT);
			orderList.put(OrderConstants.NORMAL);
			orderTypeList.put(OrderConstants.GTC,orderList);
		}
		return orderTypeList;
	}

	private static JSONObject getChartObj(SymbolRow symbolRow, Session session)
	{
		JSONObject chartDataObj = new JSONObject();
		JSONObject minSymObjForCharts = new JSONObject();
		minSymObjForCharts.put(SymbolConstants.SYMBOL, symbolRow.getSymbol());
		minSymObjForCharts.put(SymbolConstants.EXCHANGE, symbolRow.getExchange());
		minSymObjForCharts.put(SymbolConstants.SYMBOL_TOKEN, symbolRow.getSymbolToken());
		minSymObjForCharts.put(SymbolConstants.SYMBOL_DETAILS_2, symbolRow.getSymbolDetails2());
		chartDataObj.put(SymbolConstants.SYMBOL_OBJ, minSymObjForCharts);
		chartDataObj.put(DeviceConstants.APP_ID, session.getAppID());
		chartDataObj.put(AppConstants.SESSIONID, session.getSessionID());
		return chartDataObj;
	}
	
	public static JSONObject addToExchObj_101(JSONObject symbolObj, JSONArray prodList, boolean isOrderPad,
			boolean isExchNotSupported, boolean isIndex, Session session) throws JSONException, ParseException, Exception {
		JSONObject exchObj = new JSONObject();
		String sSearchMktSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		JSONObject minimisedSymObj = new JSONObject();
		JSONArray exchGTD = session.getUserInfo().getJSONArray(UserInfoConstants.ALLOWED_GTD_EXCH);
		SymbolRow symbolRow = null;
		int lotsize;
		String series = "", remarks = "", isin = "";
		if (isIndex)
		{
			symbolRow = Indices.getSymbolRow(symbolObj.getString(SymbolConstants.SYMBOL_TOKEN));
			lotsize=symbolRow.getLotSizeInt();
			minimisedSymObj = symbolRow.getMinimisedSymbolRow();
			series = symbolRow.getSeries();
			isin = symbolRow.getISIN();
		}
		else
		{
			symbolRow = SymbolMap.getSymbolRow(symbolObj.getString(SymbolConstants.SYMBOL_TOKEN));
			lotsize=symbolRow.getLotSizeInt();
			minimisedSymObj = symbolRow.getMinimisedSymbolRow();
			series = symbolRow.getSeries();
			isin = symbolRow.getISIN();
		}

		if (isOrderPad) {
			exchObj.put(SymbolConstants.SYMBOL_OBJ, minimisedSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ));
			if (isIndex)
				exchObj.put(OrderConstants.ORDER_ACTION, new JSONArray());
			else
				exchObj.put(OrderConstants.ORDER_ACTION, getOrderActions(""));

			exchObj.put(OrderConstants.VALIDITY, Validity.getValidities());

			JSONArray productList = new JSONArray();
			JSONObject productListObj = getProductList_101(sSearchMktSegID, prodList, exchGTD);

			if (isExchNotSupported)
				productList = ProductType.getDefaultProductTypes(sSearchMktSegID);
			else
				productList = getProductList(sSearchMktSegID, prodList);

			// TODO: To be removed, once we get clarification from the FT for AMO MTF
			if (AMODetails.isAMOOrder(ExchangeSegment.getExchangeName(sSearchMktSegID))) {
				for (int i = 0; i < productList.length(); i++) {
					if (productList.getString(i).equalsIgnoreCase(ProductType.MTF))
						productList.remove(i);
				}
			}
			if(sSearchMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)) {
                if(series.trim().equals("BE"))
                	remarks = AppConfig.getValue("warn.trade_to_trade");
				if(!remarks.equalsIgnoreCase("NULL") && !remarks.isEmpty()) {
					exchObj.put(DeviceConstants.IS_CAUTION, "true");
					exchObj.put(DeviceConstants.CAUTION_MSG, remarks);
				}
//				getRMSMessageByISIN(exchObj, isin);
			}else if(sSearchMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)) {
                if(series.trim().equals("T"))
                	remarks = AppConfig.getValue("warn.trade_to_trade");
				if(!remarks.equalsIgnoreCase("NULL") && !remarks.isEmpty()) {
					exchObj.put(DeviceConstants.IS_CAUTION, "true");
					exchObj.put(DeviceConstants.CAUTION_MSG, remarks);
				}
//				getRMSMessageByISIN(exchObj, isin);
			}
			exchObj.put(OrderConstants.SHOW_LOT, isShowLot(sSearchMktSegID,lotsize));
			exchObj.put(OrderConstants.SHOW_DISC_QTY, Boolean.toString(isShowDiscQty(sSearchMktSegID)));
			exchObj.put(OrderConstants.DISC_PER, getDiscPercent(sSearchMktSegID));
			
			exchObj.put(UserInfoConstants.PRODUCT_TYPE, productListObj);
			exchObj.put(OrderConstants.ORDER_TYPE_PROPS, getOrderList_101(productList, productListObj));
		} else {
			exchObj.put(SymbolConstants.SYMBOL_OBJ, minimisedSymObj.getJSONObject(SymbolConstants.SYMBOL_OBJ));
			if (isIndex)
				exchObj.put(OrderConstants.ORDER_ACTION, new JSONArray());
			else
				exchObj.put(OrderConstants.ORDER_ACTION, getOrderActions(""));
			if ((sSearchMktSegID.equals(ExchangeSegment.NSE_SEGMENT_ID)
					|| sSearchMktSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)) && !isIndex) {
				exchObj.put(OrderConstants.SHOW_PEER_COMP, true);
			} else {
				exchObj.put(OrderConstants.SHOW_PEER_COMP, false);
			}
			
			exchObj.put(DeviceConstants.CHART_REQ_OBJ, getChartObj(symbolRow, session));

		}

		return exchObj;
	}

	public static JSONObject getOrderPadDetails_101(String sSymbolToken, JSONArray prodList, boolean isOrderPad, Session session)
			throws Exception {
		boolean isIndex = false, isIndexDerivative = false;
		JSONObject symbolObj = new JSONObject();

		if (SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			symbolObj = SymbolMap.getSymbolRow(sSymbolToken).getJSONObject(SymbolConstants.SYMBOL_OBJ);
			String instrument = SymbolMap.getSymbolRow(sSymbolToken).getInstrument();
			if (InstrumentType.isIndex(instrument))
				isIndexDerivative = true;
		} else if (Indices.isValidIndex(sSymbolToken)) {
			symbolObj = Indices.getSymbolRow(sSymbolToken).getJSONObject(SymbolConstants.SYMBOL_OBJ);
			isIndex = true;
		}

		String sMarketSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
		String sExch = ExchangeSegment.getExchangeName(sMarketSegID);
		String symbol = symbolObj.getString(SymbolConstants.SYMBOL);
		
		JSONObject orderPadDetail = new JSONObject();

		if (isOrderPad && (!checkSegIDSupported(sMarketSegID, prodList))) {
			JSONArray associateExch = new JSONArray();
			JSONObject exchProps = new JSONObject();
			associateExch.put(sExch);
			exchProps.put(sExch, addToExchObj_101(symbolObj, prodList, isOrderPad, true, isIndex, session));
			orderPadDetail.put(OrderConstants.ORDER_ALLOWED, "false");
			orderPadDetail.put(OrderConstants.REASON,
					String.format(InfoMessage.getInfoMSG("info_msg.order_exchange_not_supported"), sExch));
			orderPadDetail.put(SymbolConstants.ASSOCIATE_EXCH, associateExch);
			orderPadDetail.put(SymbolConstants.EXCH_PROPS, exchProps);
			return orderPadDetail;
		}

		if (isIndex)
			orderPadDetail.put(DeviceConstants.SHOW_ONLY_OHLC, "true");
		else
			orderPadDetail.put(DeviceConstants.SHOW_ONLY_OHLC, "false");

		if (isOrderPad)
			getOrderPadDetails_101(orderPadDetail, sExch);
		else
			getQuoteSymbolDetails(orderPadDetail, sExch, isIndex, isIndexDerivative, symbol);

		getExchangeDetails_101(orderPadDetail, symbolObj, prodList, isOrderPad, isIndex, session);

		return orderPadDetail;
	}
	
	public static JSONObject getProductList_101(String sSearchMktSegID, JSONArray prodList, JSONArray exchGTDList) {
		JSONArray productList = new JSONArray();
		boolean isGTC = false;
		String strProdList = "";
		for (int i = 0; i < prodList.length(); i++) {
			JSONObject prodDetails = prodList.getJSONObject(i);
			String sMarketSegID = prodDetails.getString(SymbolConstants.MKT_SEG_ID);
			if (sMarketSegID.equals(sSearchMktSegID)) {
				productList = prodDetails.getJSONArray(UserInfoConstants.PRODUCT_LIST);
			}	
		}
		strProdList = productList.toString();
		for (int i = 0; i < exchGTDList.length(); i++) {
			JSONObject gtdDetails = exchGTDList.getJSONObject(i);
			JSONArray exchDetails =gtdDetails.getJSONArray(UserInfoConstants.EXCH_LIST);
			for(int j = 0; j < exchDetails.length(); j++) {
				if (exchDetails.getString(j).equals(sSearchMktSegID) && strProdList.contains(gtdDetails.getString(UserInfoConstants.PRODUCT_TYPE))) {
					isGTC=false;
				}
			}	
		}
		strProdList = productList.toString();
		return setProductAndOrderType(strProdList, isGTC);

	}
	
	public static JSONObject getProductModifyList_101(String sSearchMktSegID, JSONArray prodList, JSONArray exchGTDList) {
		boolean isGTC = false;
		String strProdList = prodList.getString(0);
		if(strProdList.contains(OrderConstants.GTC))
			isGTC = true;
		return setProductAndOrderType(strProdList, isGTC);

	}
	
	public static JSONObject setProductAndOrderType(String prodType, boolean isGTC) {
		JSONArray regPrdLst = new JSONArray();
		JSONArray covPrdLst = new JSONArray();
		JSONArray braPrdLst = new JSONArray();
		JSONArray gtcPrdLst = new JSONArray();
		JSONObject regularOrd = new JSONObject();
		
		if (prodType.contains(OrderConstants.DELIVERY)) {
			JSONObject deliveryObj = new JSONObject();
			deliveryObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.DELIVERY);
			deliveryObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.DELIVERY);
			regPrdLst.put(deliveryObj);
			regularOrd.put(OrderConstants.REGULAR,regPrdLst);
		}
		if(prodType.contains(OrderConstants.CARRYFORWARD)) {
			JSONObject carryForwardObj = new JSONObject();
			carryForwardObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.CARRYFORWARD);
			carryForwardObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.CARRYFORWARD);
			regPrdLst.put(carryForwardObj);
			regularOrd.remove(OrderConstants.REGULAR);
			regularOrd.put(OrderConstants.REGULAR,regPrdLst);
		}
		if( prodType.contains(OrderConstants.INTRADAY)) {
			JSONObject intradayObj = new JSONObject();
			intradayObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.INTRADAY);
			intradayObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.INTRADAY);
			regPrdLst.put(intradayObj);
			regularOrd.remove(OrderConstants.REGULAR);
			regularOrd.put(OrderConstants.REGULAR,regPrdLst);
		}
		if(prodType.contains(OrderConstants.MTF)) {
			JSONObject mtfObj = new JSONObject();
			mtfObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.MTF);
			mtfObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.MTF);
			regPrdLst.put(mtfObj);
			regularOrd.remove(OrderConstants.REGULAR);
			regularOrd.put(OrderConstants.REGULAR,regPrdLst);
		}
		if(prodType.contains(OrderConstants.PRODUCT_COVER_ORDER)) {
			JSONObject coverObj = new JSONObject();
			coverObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.PRODUCT_COVER_ORDER);
			coverObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.INTRADAY);
			covPrdLst.put(coverObj);
			regularOrd.put(OrderConstants.COVER,covPrdLst);
		}
		if(prodType.contains(OrderConstants.BRACKET_ORDER)) {
			JSONObject bracketObj = new JSONObject();
			bracketObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.BRACKET_ORDER);
			bracketObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.INTRADAY);
			braPrdLst.put(bracketObj);
			regularOrd.put(OrderConstants.BRACKET,braPrdLst);
		}
		if(isGTC) {
			JSONObject gtcObj = new JSONObject();
			gtcObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.DELIVERY);
			gtcObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.DELIVERY);
			gtcPrdLst.put(gtcObj);
			regularOrd.put(OrderConstants.GTC,gtcPrdLst);
		}
		return regularOrd;
	}
	
	public static void main(String[] args) {
		String prodList = "CARRYFORWARD,INTRADAY";
		System.out.println(setProductAndOrderType(prodList, true));
	}
}
