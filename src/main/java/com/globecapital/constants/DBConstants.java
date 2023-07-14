
package com.globecapital.constants;

public class DBConstants {

	public static final String SYMBOL = "symbol";

	public static final String SYMBOL_NAME = "sSymbol";

	public static final String EXCHANGE = "exchange";

	public static final String EXCHANGE_NAME = "ExchangeName";

	public static final String EXPIRY_SEARCH = "expiryForSearch";

	public static final String INSTRUMENT_SEARCH = "instrumentForSearch";

	public static final String SERIES = "sSeries";

	public static final String COMPANY_NAME = "companyName";

	public static final String TRADED_CURRENCY = "traded_currency";

	public static final String TOKEN = "nToken";

	public static final String SEGMENT = "nMarketSegmentId";

	public static final String TOKEN_SEGMENT = "nTokenSegment";

	public static final String SYMBOL_DETAILS = "SymbolDetails";

	public static final String SYMBOL_UNIQ_DESC = "SymbolUniqDesc";

	public static final String INSTRUMENT_NAME = "sInstrumentName";

	public static final String TICK_PRICE = "nPriceTick";

	public static final String ISINSEGMENT = "isinSegment";

	public static final String DISP_PRICE_TICK = "dispPriceTick";

	public static final String LOT_SIZE = "nRegularLot";

	public static final String STRIKE_PRICE = "nStrikePrice";

	public static final String DECIMAL_LOCATOR = "DecimalLocator";

	public static final String EXPIRY_DATE = "ExpiryDate";

	public static final String N_EXPIRY_DATE = "nExpiryDate";

	public static final String PRECISION = "nPrecision";

	public static final String OPTION = "sOptionType";

	public static final String ISIN = "sISINCode";

	public static final String SECURITY_DESC = "sSecurityDesc";

	public static final String PRICE_NUM = "nPriceNum";

	public static final String PRICE_DEN = "nPriceDen";

	public static final String INSTRUMENT_TYPE = "nInstrumentType";

	public static final String MARKET_ALLOWED = "nNormal_MarketAllowed";

	public static final String MINIMUM_LOT = "nMinimumLot";

	public static final String LOW_PRICE_RANGE = "nLowPriceRange";

	public static final String HIGH_PRICE_RANGE = "nHighPriceRange";

	public static final String OPEN_INTEREST = "nOpenInterest";

	public static final String SEARCH_SYMBOL_DETAILS = "nSearchSymbolDetails";

	public static final String BASE_PRICE = "nBasePrice";

	public static final String LAST_PRICE = "LAST_PRICE";

	public static final String CLOSE_PRICE = "CLOSE_PRICE";

	public static final String HIGH_PRICE = "HIGH_PRICE";

	public static final String LOW_PRICE = "LOW_PRICE";
	
	public static final String UPPER_CIR_LIMIT = "UPPER_CIR_LIMIT";
	
	public static final String LOWER_CIR_LIMIT = "LOWER_CIR_LIMIT";

	public static final String VOLUME = "VOLUME";

	public static final String TIME_UTC = "TIME_UTC";

	public static final String CHANGE = "CHANGE";

	public static final String CHANGE_PER = "CHANGE_PER";

	public static final String OPEN_PRICE = "OPEN_PRICE";
	
	public static final String OPEN = "OPEN";
	
	public static final String HIGH = "HIGH";

	public static final String CLOSE = "CLOSE";

	public static final String LOW = "LOW";

	public static final String PREV_CLOSE = "PRE_CLOSE_PRICE";

	public static final String QUOTE_SYMBOL = "SYMBOL";
	
	public static final String OPENINTEREST = "OPENINTEREST";

	public static final String CM_SECTOR_NAME = "CM_SectorName";

	public static final String CM_CO_CODE = "CM_co_code";

	public static final String MARKET_CAP = "CM_mcaptype";

	public static final String ASSET_TOKEN = "nAssetToken";
	
	public static final String CM_SECTOR_FORMAT = "CM_SectorFormat";
	
	public static final String IS_FNO_EXISTS = "isFNOExists";
	
	public static final String MAPPING_SYMBOL_UNIQ_DESC = "mappingSymbolUniqDesc";
	
	// CLIENT_SESSION table
	public static final String USER_ID = "user_id";

	public static final String BUILD = "build";

	public static final String APP_ID = "app_id";

	public static final String J_KEY = "j_key";

	public static final String FT_SESSION_ID = "ft_session_id";
	
	public static final String FT_SESSION = "ft_session";

	public static final String USER_TYPE = "user_type";

	public static final String CLIENT_ORDER_NO = "client_order_no";

	public static final String USER_INFO = "user_info";
	
	public static final String IS_2FA_AUTHENTICATED = "is_2FA_authenticated";

	// Advance Login
	public static final String MPIN_ENABLED = "MPIN_ENABLED";
	public static final String FINGERPRINT_ENABLED = "FINGERPRINT_ENABLED";
	public static final String MPIN_ACTIVE = "MPIN_ACTIVE";
	public static final String FINGERPRINT_ACTIVE = "FINGERPRINT_ACTIVE";
	public static final String MPIN_FAILURE_COUNT = "MPIN_FAILURE_COUNT";
	public static final String APP_ID_CL = "APP_ID";
	public static final String NOTIFICATION_ACTIVE = "NOTIFICATION_ACTIVE";
	
	// News Constants
	public static final String GUID = "guid";
	public static final String HAS_TITLE = "hasTitle";
	public static final String TITLE = "title";
	public static final String NEWS_DESCRIPTION = "description";
	public static final String TIME = "time";
	public static final String DATE = "date";
	public static final String CATEGORY = "category";
	public static final int LIMIT = 3;
	public static final String NEWS_FROM_FORMAT = "dd/MM/yyyy";
	public static final String NEWS_TO_FORMAT = "dd-MM-yyyy";

	public static final String INDEX_CODE = "indexCode";
	public static final String INDEX_NAME = "indexName";
	public static final String S_ISIN = "isin";

	// SCRIPMASTER Constants
	public static final String EXPIRY_DATE_FORMAT = "ddMMMyyyy";
	public static final String UNIQ_DESC_DATE_FORMAT = "ddMMMyy";
	public static final String EXPIRY_DATE_FROM_FORMAT = "dd-MM-yyyy";

	// Scripmaster and Companymaster integration constants
	public static final String SCRIP_EXPIRY_DATE_FORMAT = "MMM";
	public static final String SYMBOL_DETAILS_1 = "SymbolDetails1";
	public static final String SYMBOL_DETAILS_2 = "SymbolDetails2";
	
	public static final String EXPIRY_DATE_TO_FORMAT = "dd-MMM-yyyy";
	public static final String DATE_MONTH_FORMAT = "dd MMM";
	public static final String MAPPING_DATE_FORMAT = "yyyy-MM-dd";

	// Research constants

	public static final String EXP_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss aa";
	public static final String OPTION_TYPE = "OptionType";
	
	//AMO
	public static final String DB_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String AMO_FROM_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
	public static final String AMO_FROM_DATE_FORMAT = "dd/MM/yyyy";
	public static final String AMO_EXCHANGE = "EXCHANGE";
	public static final String START_TIME = "START_TIME";
	public static final String END_TIME = "END_TIME";
	public static final String AMO_DATE = "DATE";
	
	public static final String S_EXCHANGE = "sExchange";
	
	public static final String USERID = "USERID";
	public static final String PASSWORD = "PASSWORD";
	public static final String CONFIG = "CONFIG";
	
	public static final String NEWS_DATE = "dd-MM-yyyy hh:mm:ss a";
	public static final String NEWS_TIME = "news_time";
	public static final String NEWS_TIME_FORMAT = "hh:mm:ss a";

	//MarketData Constants
	
	public static final String QUOTE = "_QUOTE";
	public static final String NSE_QUOTE = "NSE_QUOTE";
	public static final String NFO_QUOTE = "NFO_QUOTE";
	public static final String NSECDS_QUOTE = "NCD_QUOTE";
	public static final String MCX_QUOTE = "MFO_QUOTE";
	public static final String EXCHANGE_TOKEN = "EXCHANGE_TOKEN";
	public static final String INSTRUMENT_TYPE_MARKET_DATA = "INSTRUMENT_TYPE";
	public static final String OPT_STK = "OPTSTK";
	public static final String OPT_IDX = "OPTIDX";
	public static final String FUT_IDX = "FUTIDX";
	public static final String FUT_STK = "FUTSTK";
	public static final String OI_CHANGE = "OI_CHANGE";
	public static final String OI_PER = "OI_PER";
	public static final String PREV_OPENINTEREST = "PREV_OPENINTEREST";
	public static final String ASCENDING = "ASC";
	public static final String DESCENDING = "DESC";
	public static final String ROLLOVER_COST = "ROLLOVER_COST";
	public static final String ROLLOVER_COST_PER = "ROLLOVER_COST_PER";
	public static final String ROLLOVER_PERCENTAGE = "ROLLOVER_PERCENTAGE";
	
	//FNOOverview Constants
	public static final String EXP_DATE="EXPIRY_DATE";
	public static final String OI_RATIO="OI_RATIO";
	public static final String PCR = "pcr";
	public static final String BASE_SYMBOL = "BASE_SYMBOL";
	public static final String EXCHANGE_ROLLOVER = "EXCHANGE";
	
	//View transaction status constants
	public static final String AMOUNT = "AMOUNT";
	public static final String STATUS = "STATUS";
	public static final String MERCHANT_REF_NO = "MERCHANT_REF_NO";
	public static final String CREATED_AT = "CREATED_AT";
	public static final String PAYMENT_CHANNEL = "PAYMENT_CHANNEL";
	public static final String STAGE = "STAGE";
	
	public static final String EXCHANGE_QUOTE = "Exchange Quote";
	public static final String OMDF_QUOTE = "OMDF Quote";
	
	//Special Version Update cosntants
	public static final String CHANNEL = "channel";
	public static final String APP_VERSION = "app_version";
	public static final String HAS_VERSION_UPDATE = "hasVersionUpdate";
	
	//Advance session table
	public static final String PASSWORD_S = "password";
	public static final String IS_OTP_REQD = "is_otp_reqd";
	
}
