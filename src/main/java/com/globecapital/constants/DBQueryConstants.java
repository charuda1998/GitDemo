package com.globecapital.constants;

import com.globecapital.constants.order.ExchangeSegment;

public class DBQueryConstants {
	
	public static final String AUDIT = "INSERT INTO AUDIT_TRANSACTIONS(MSG_ID,SVC_GROUP,SVC_NAME,SVC_VERSION,INFOID,INFO_MSG,APP_ID,USERNAME,USER_TYPE,API_TIME,REQ_TIME,RES_TIME,SRC_IP) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String LOG_AUDIT = "INSERT INTO AUDIT_TRANSACTIONS(MSG_ID,SVC_GROUP,SVC_NAME,SVC_VERSION,INFOID,INFO_MSG,APP_ID,USERNAME,USER_TYPE,API_TIME,REQ_TIME,RES_TIME,SRC_IP) "
			+ "VALUES";

	public static final String VERSION_MASTER = "SELECT TYPE ,MAX(VERSION) as VERSION " + "FROM VERSION_MASTER "
			+ "group by TYPE";

	public static final String LOGOUT_USER_QUERY_WITH_SESSIONID = "DELETE from CLIENT_SESSION where session_id = ?";

	public static final String LOGOUT_USER_QUERY_WITH_USERID = "DELETE from CLIENT_SESSION where user_id = ?";

	public static final String INSERT_USER_SESSION = "INSERT INTO CLIENT_SESSION (user_id, session_id, app_id, "
			+ "build, created_at, last_active, user_type, user_info, ft_session, ft_session_id, j_key, "
			+ "client_order_no, is_2FA_authenticated) VALUES(?, ?, ?, (select BUILD from APP_INFO where APP_ID = ?)," 
			+ "NOW(), UNIX_TIMESTAMP(), ?, ?,?,?, ?, ?, ?)"
			+ "ON DUPLICATE KEY UPDATE session_id = VALUES( session_id ), " + "app_id = VALUES(app_id),"
			+ "created_at = NOW(), last_active = UNIX_TIMESTAMP(), "
			+ "user_type= VALUES(user_type), user_info= VALUES(user_info), ft_session=VALUES(ft_session),"
			+ "ft_session_id=VALUES(ft_session_id),"
			+ "j_key = VALUES(j_key), client_order_no = VALUES(client_order_no), "
			+ "is_2FA_authenticated = VALUES(is_2FA_authenticated)";
	
	public static final String UPDATE_USER_INFO = "UPDATE CLIENT_SESSION set USER_INFO = ? where USER_ID = ?";

	public static final String SESSION_VALIDATION_PROCEDURE = "{call session_validation(?,?,?)}";
	
	public static final String INSERT_ADVANCE_LOGIN_MPIN = "INSERT INTO ADVANCE_LOGIN(USER_ID, MPIN, MPIN_ENABLED, "
			+ "MPIN_ACTIVE, MPIN_FAILURE_COUNT, APP_ID) VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE USER_ID = VALUES(USER_ID), "
	            + "MPIN = VALUES(MPIN), MPIN_ENABLED = VALUES(MPIN_ENABLED), MPIN_ACTIVE = VALUES(MPIN_ACTIVE), "
	            + "MPIN_FAILURE_COUNT = VALUES(MPIN_FAILURE_COUNT), APP_ID = VALUES(APP_ID), UPDATED_AT = NOW()";
	    
	public static final String INSERT_ADVANCE_LOGIN_FINGER_PRINT = "INSERT INTO ADVANCE_LOGIN(USER_ID, FINGERPRINT_ENABLED,"
			+ "FINGERPRINT_ACTIVE, APP_ID) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE USER_ID = VALUES(USER_ID), "
	            + "FINGERPRINT_ENABLED = VALUES(FINGERPRINT_ENABLED), FINGERPRINT_ACTIVE = VALUES(FINGERPRINT_ACTIVE),"
	            + "APP_ID = VALUES(APP_ID), UPDATED_AT = NOW()";
	
	public static final String GET_SESSION_EXPIRY = "select param_value from settings "
			+ "where param_name='session_timeout'";
	
	public static final String GET_APP_VERSION = " SELECT MAX(APP_VERSION) from APP_INFO_TRANS where APP_ID = ? ";
	
	public static final String GET_MPIN = "SELECT MPIN FROM ADVANCE_LOGIN WHERE USER_ID = ?";
	
	public static final String CHECK_MPIN_ENABLED = "SELECT MPIN_ENABLED FROM ADVANCE_LOGIN WHERE USER_ID = ?";
	
	public static final String CHECK_FINGERPRINT_ENABLED = "SELECT FINGERPRINT_ENABLED FROM ADVANCE_LOGIN "
			+ "WHERE USER_ID = ?";
	
	public static final String CHECK_MPIN_ACTIVE = "SELECT MPIN_ACTIVE FROM ADVANCE_LOGIN WHERE USER_ID = ?";
	
	public static final String CHECK_FINGERPRINT_ACTIVE = "SELECT FINGERPRINT_ACTIVE FROM ADVANCE_LOGIN "
			+ "WHERE USER_ID = ?";
	
	public static final String UPDATE_MPIN = "UPDATE ADVANCE_LOGIN SET MPIN = ?, APP_ID = ?, UPDATED_AT = NOW()"
			+ " WHERE USER_ID = ?";
	
	public static final String UPDATE_MPIN_INACTIVE = "UPDATE ADVANCE_LOGIN SET MPIN_ACTIVE = 'N'"
			+ " WHERE USER_ID = ?";
	
	public static final String UPDATE_FINGERPRINT_INACTIVE = "UPDATE ADVANCE_LOGIN SET FINGERPRINT_ACTIVE = 'N'"
			+ " WHERE USER_ID = ?";
	
	public static final String UPDATE_MPIN_ACTIVE = "UPDATE ADVANCE_LOGIN SET MPIN_ACTIVE = 'Y'"
			+ " WHERE USER_ID = ?";
	
	public static final String UPDATE_FINGERPRINT_ACTIVE = "UPDATE ADVANCE_LOGIN SET FINGERPRINT_ACTIVE = 'Y'"
			+ " WHERE USER_ID = ?";
	
	public static final String UPDATE_MPIN_FAILURE_COUNT = "UPDATE ADVANCE_LOGIN SET MPIN_FAILURE_COUNT = ?"
			+ " WHERE USER_ID = ?";
	
	public static final String CHECK_MPIN_FAILURE_COUNT = "SELECT MPIN_FAILURE_COUNT FROM ADVANCE_LOGIN"
			+ " WHERE USER_ID = ?";
	
	public static final String GET_APP_ID = "SELECT APP_ID FROM ADVANCE_LOGIN WHERE USER_ID = ?";
	
	public static final String UPDATE_APP_ID = "UPDATE ADVANCE_LOGIN SET APP_ID = ? WHERE USER_ID = ?";
	
	public static final String UPDATE_AUTH_TYPE = "UPDATE CLIENT_SESSION SET last_auth_type = ?"
			+ " WHERE user_id = ?";
	
	public static final String UPDATE_2FA_AUTHENTICATED_SUCCESS = "UPDATE CLIENT_SESSION SET "
			+ "is_2FA_authenticated = 'Y' WHERE user_id = ?";
	
	public static final String UPDATE_2FA_AUTHENTICATED_FALSE = "UPDATE CLIENT_SESSION SET "
			+ "is_2FA_authenticated = 'N' WHERE user_id = ?";
	
	
	public static final String GET_SYMBOLS = "SELECT * FROM SCRIPMASTER";

	public static final String SYMBOL_SEARCH_ALL = String.format(
			"SELECT sSymbol,sSecurityDesc,SymbolDetails,sInstrumentName,ExpiryDate,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,nStrikePrice,ExchangeName,nPrecision,nTokenSegment,companyName FROM SYMBOLS WHERE ((sSymbol LIKE ?) OR (sSecurityDesc LIKE ?))  AND ExchangeName IN ('NSE','BSE')"
					+ "ORDER BY FIELD(`ExchangeName`, '%s', '%s') LIMIT 25",
			ExchangeSegment.NSE, ExchangeSegment.BSE);

	public static final String INSERT_SYMBOLS = "INSERT INTO SCRIPMASTER(nTokenSegment,nMarketSegmentId,nToken,sSymbol,"
			+ "sSeries,sInstrumentName,nExpiryDate,ExpiryDate,nStrikePrice,sOptionType,"
			+ "sISINCode,DecimalLocator,nRegularLot,nPriceTick,sSecurityDesc,nAssetToken,"
			+ "nPriceNum,nPriceDen,nInstrumentType,nNormal_MarketAllowed,"
			+ "nMinimumLot,nLowPriceRange,nHighPriceRange,"
			+ "nOpenInterest,exchangeName,nPrecision,assetClass,symbolDetails,expiryForSearch,instrumentForSearch,nSearchSymbolDetails,nBasePrice,dispPriceTick,isinSegment,dispSearch, nSpread, SymbolUniqDesc, SymbolDetails1, SymbolDetails2, nFIIFlag, nNormal_SecurityStatus, companyName, isFNOExists, mappingSymbolUniqDesc) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
			+ "ON DUPLICATE KEY UPDATE nMarketSegmentId=VALUES(nMarketSegmentId),nToken=VALUES(nToken),sSymbol=VALUES(sSymbol),sSeries = VALUES(sSeries),sInstrumentName=VALUES(sInstrumentName),"
			+ "nExpiryDate=VALUES(nExpiryDate),ExpiryDate = VALUES(ExpiryDate),nStrikePrice = VALUES(nStrikePrice),sOptionType= VALUES(sOptionType),sISINCode = VALUES(sISINCode),DecimalLocator=VALUES(DecimalLocator),"
			+ "nRegularLot=VALUES(nRegularLot),nPriceTick=VALUES(nPriceTick),sSecurityDesc=VALUES(sSecurityDesc),nAssetToken = VALUES(nAssetToken),"
			+ "nPriceNum =VALUES(nPriceNum),nPriceDen = VALUES(nPriceDen),nInstrumentType=VALUES(nInstrumentType),nNormal_MarketAllowed= VALUES(nNormal_MarketAllowed),"
			+ "nMinimumLot=VALUES(nMinimumLot),nLowPriceRange = VALUES(nLowPriceRange),nHighPriceRange= VALUES(nHighPriceRange),nOpenInterest= VALUES(nOpenInterest),"
			+ "exchangeName= VALUES(exchangeName),nPrecision=VALUES(nPrecision),assetClass= VALUES(assetClass),symbolDetails=VALUES(symbolDetails),expiryForSearch=VALUES(expiryForSearch),"
			+ "instrumentForSearch=VALUES(instrumentForSearch),nSearchSymbolDetails=VALUES(nSearchSymbolDetails),nBasePrice=VALUES(nBasePrice),dispPriceTick=VALUES(dispPriceTick),isinSegment=VALUES(isinSegment),dispSearch=VALUES(dispSearch),"
			+ "SymbolUniqDesc=VALUES(SymbolUniqDesc), SymbolDetails1=VALUES(SymbolDetails1), SymbolDetails2=VALUES(SymbolDetails2), nFIIFlag=VALUES(nFIIFlag), nNormal_SecurityStatus=VALUES(nNormal_SecurityStatus), companyName=VALUES(companyName), isFNOExists=VALUES(isFNOExists), mappingSymbolUniqDesc=VALUES(mappingSymbolUniqDesc)";
			
	public static final String INSERT_DERIVATIVES = "INSERT INTO SCRIPMASTER(nTokenSegment,nMarketSegmentId,nToken,sSymbol,"
			+ "sSeries,sInstrumentName,nExpiryDate,ExpiryDate,nStrikePrice,sOptionType,"
			+ "sISINCode,DecimalLocator,nRegularLot,nPriceTick,sSecurityDesc,nAssetToken,"
			+ "nPriceNum,nPriceDen,nInstrumentType,nNormal_MarketAllowed,"
			+ "nMinimumLot,nLowPriceRange,nHighPriceRange,"
			+ "nOpenInterest,exchangeName,nPrecision,assetClass,symbolDetails,expiryForSearch,instrumentForSearch,nSearchSymbolDetails,nBasePrice,dispPriceTick,isinSegment,dispSearch, nSpread, SymbolUniqDesc, CM_co_code, CM_SectorFormat, SymbolDetails1, SymbolDetails2, nFIIFlag, nNormal_SecurityStatus, companyName, isFNOExists, mappingSymbolUniqDesc, nissueStartDate,nissueMaturityDate,nNoDeliveryStartDate,nNoDeliveryEndDate,nMaxSingleTransactionQty,sQtyUnit) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
			+ "ON DUPLICATE KEY UPDATE nMarketSegmentId=VALUES(nMarketSegmentId),nToken=VALUES(nToken),sSymbol=VALUES(sSymbol),sSeries = VALUES(sSeries),sInstrumentName=VALUES(sInstrumentName),"
			+ "nExpiryDate=VALUES(nExpiryDate),ExpiryDate = VALUES(ExpiryDate),nStrikePrice = VALUES(nStrikePrice),sOptionType= VALUES(sOptionType),sISINCode = VALUES(sISINCode),DecimalLocator=VALUES(DecimalLocator),"
			+ "nRegularLot=VALUES(nRegularLot),nPriceTick=VALUES(nPriceTick),sSecurityDesc=VALUES(sSecurityDesc),nAssetToken = VALUES(nAssetToken),"
			+ "nPriceNum =VALUES(nPriceNum),nPriceDen = VALUES(nPriceDen),nInstrumentType=VALUES(nInstrumentType),nNormal_MarketAllowed= VALUES(nNormal_MarketAllowed),"
			+ "nMinimumLot=VALUES(nMinimumLot),nLowPriceRange = VALUES(nLowPriceRange),nHighPriceRange= VALUES(nHighPriceRange),nOpenInterest= VALUES(nOpenInterest),"
			+ "exchangeName= VALUES(exchangeName),nPrecision=VALUES(nPrecision),assetClass= VALUES(assetClass),symbolDetails=VALUES(symbolDetails),expiryForSearch=VALUES(expiryForSearch),instrumentForSearch=VALUES(instrumentForSearch),nSearchSymbolDetails=VALUES(nSearchSymbolDetails),nBasePrice=VALUES(nBasePrice),dispPriceTick=VALUES(dispPriceTick),isinSegment=VALUES(isinSegment),dispSearch=VALUES(dispSearch),"
			+ "SymbolUniqDesc=VALUES(SymbolUniqDesc), CM_co_code=VALUES(CM_co_code), CM_SectorFormat=VALUES(CM_SectorFormat), SymbolDetails1=VALUES(SymbolDetails1), SymbolDetails2=VALUES(SymbolDetails2), nFIIFlag=VALUES(nFIIFlag), nNormal_SecurityStatus=VALUES(nNormal_SecurityStatus), companyName=VALUES(companyName), isFNOExists=VALUES(isFNOExists), mappingSymbolUniqDesc=VALUES(mappingSymbolUniqDesc), nissueStartDate=VALUES(nissueStartDate),nissueMaturityDate=VALUES(nissueMaturityDate),nNoDeliveryStartDate=VALUES(nNoDeliveryStartDate),nNoDeliveryEndDate=VALUES(nNoDeliveryEndDate),nMaxSingleTransactionQty=VALUES(nMaxSingleTransactionQty),sQtyUnit=VALUES(sQtyUnit)";

	public static final String CLIENT_ORDER_NO_RETRIEVAL = "{call client_order_no_retrieval(?,?)}";

	public static final String DUPLICATE_ORDER_VALIDATOR = "{call order_validator(?,?,?)}";
	
	public static final String GET_CHART_POINTS = "{call chart_data(?,?,?,?)}";
	
	public static final String GET_NSE_CHART = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NSE_CHART_INTRADAY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_NSECDS_CHART = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NCD_CHART_INTRADAY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_NCDEX_CHART = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NCO_CHART_INTRADAY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_NFO_CHART = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NFO_CHART_INTRADAY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_BSE_CHART = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from BSE_CHART_INTRADAY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_BSECDS_CHART = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from BCD_CHART_INTRADAY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_MCX_CHART = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from MFO_CHART_INTRADAY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	/* Derivative search constants */

public static final String SYMBOL = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and ExchangeName!='' order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),sSymbol,FIELD(instrumentForSearch,'FUT','OPT'),FIELD(sOptionType,'CE','PE'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)) LIMIT 25";
	
	public static final String SYMBOL_COMPANY = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,CM_co_code,CM_SectorName,nAssetToken,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE (sSecurityDesc like ? OR sSymbol like ?) and ExchangeName!='' order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),sSymbol,FIELD(instrumentForSearch,'FUT','OPT'),FIELD(sOptionType,'CE','PE'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)) LIMIT 25";

	public static final String SYMBOL_MONTH = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? AND ( ExpiryDate like ? OR nStrikePrice like ? ) AND (instrumentForSearch like 'FUT%' OR instrumentForSearch like 'OPT%') order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_MONTH_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? AND ExpiryDate LIKE ? AND sOptionType =? AND (instrumentForSearch like 'FUT%' OR instrumentForSearch like 'OPT%') order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)), FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DATE_MONTH = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? AND (ExpiryDate LIKE ? OR nStrikePrice LIKE ? ) AND ExpiryDate LIKE ?  order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)) LIMIT 25";

	public static final String SYMBOL_MONTH_INSTRUMENT = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc FROM SCRIPMASTER WHERE sSymbol like ? AND ExpiryDate like ? AND instrumentForSearch like ? order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)), FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_OPTION = "SELECT 	sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? AND sOptionType like ? AND (instrumentForSearch like 'OPT%')order by FIELD(ExchangeName,'NFO','MCX','NSECDS'), nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND (instrumentForSearch like 'FUT%' or instrumentForSearch like 'OPT%') AND nStrikePrice LIKE ?  order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'), nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_MONTH_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and ExpiryDate like ? and (ExpiryDate like ? OR nStrikePrice like ? ) order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)), FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_STRIKEPRICE_MONTH_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and ( nStrikePrice like ? or ExpiryDate like ? ) and ExpiryDate like ? and sOptionType like ? order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)), FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_STRIKEPRICE_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and ( ExpiryDate like ? or nStrikePrice like ? )  and sOptionType like ? order by FIELD(ExchangeName,'NFO','MCX','NSECDS'), nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_INSTRUMENT_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and instrumentForSearch like ? and sOptionType like ?  order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),sSymbol, nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_INSTRUMENT = "SELECT 	sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? order by FIELD(ExchangeName,'NFO','MCX','NSECDS'), nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_EXCHANGE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND (ExchangeName like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_COMPANY_1 = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND (sSecurityDesc like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	// Get token from symbol and exchange
	public static final String GET_DERIVATIVE_TOKEN = "SELECT nToken FROM SCRIPMASTER WHERE sSymbol = ? AND nMarketSegmentId = ? AND ExpiryDate = ?";

	// Get LTP, Change and Change percent for orderbook
	public static final String GET_NSE_LTP = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE from NSE_QUOTE where SYMBOL = ?";

	public static final String GET_NFO_LTP = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE from NFO_QUOTE where SYMBOL = ?";

	public static final String GET_BSE_LTP = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE from BSE_QUOTE where SYMBOL = ?";

	public static final String GET_MCX_LTP = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE from MFO_QUOTE where SYMBOL = ?";

	public static final String GET_NCDEX_LTP = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE from NCDEX_QUOTE where SYMBOL = ?";

	public static final String GET_NSECDS_LTP = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE from NCD_QUOTE where SYMBOL = ?";
	
	public static final String GET_BSECDS_LTP = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE from BCD_QUOTE where SYMBOL = ?";

	public static final String GET_NSE_LTP_UNIQ_DESC = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER, PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NSE_QUOTE where SYMBOL_UNIQ_DESC = ?";

	public static final String GET_NFO_LTP_UNIQ_DESC = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER, PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NFO_QUOTE where SYMBOL_UNIQ_DESC = ?";

	public static final String GET_BSE_LTP_UNIQ_DESC = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER, PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from BSE_QUOTE where SYMBOL_UNIQ_DESC = ?";

	public static final String GET_MCX_LTP_UNIQ_DESC = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER, PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from MFO_QUOTE where SYMBOL_UNIQ_DESC = ?";

	public static final String GET_NCDEX_LTP_UNIQ_DESC = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER, PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NCDEX_QUOTE where SYMBOL_UNIQ_DESC = ?";

	public static final String GET_NSECDS_LTP_UNIQ_DESC = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER, PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NCD_QUOTE where SYMBOL_UNIQ_DESC = ?";

	public static final String GET_BSECDS_LTP_UNIQ_DESC = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER, PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from BSECDS_QUOTE where SYMBOL_UNIQ_DESC = ?";
	
	public static final String GET_GROUP_NSE_LTP = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NSE_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_NFO_LTP = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NFO_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_BSE_LTP = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from BSE_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_MCX_LTP = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from MFO_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_NCDEX_LTP = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NCDEX_QUOTE where SYMBOL IN(%s)";
	
	public static final String GET_GROUP_NSECDS_LTP = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from NCD_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_BSECDS_LTP = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,PRE_CLOSE_PRICE, OPEN_PRICE "
			+ "from BSECDS_QUOTE where SYMBOL IN(%s)";
	
	public static final String GET_GROUP_NSE_LTP_UNIQ_DESC = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,"
			+ " PRE_CLOSE_PRICE, OPEN_PRICE from NSE_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_NFO_LTP_UNIQ_DESC = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,"
			+ " PRE_CLOSE_PRICE, OPEN_PRICE from NFO_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_BSE_LTP_UNIQ_DESC = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,"
			+ " PRE_CLOSE_PRICE, OPEN_PRICE from BSE_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_MCX_LTP_UNIQ_DESC = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,"
			+ " PRE_CLOSE_PRICE, OPEN_PRICE from MFO_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_NCDEX_LTP_UNIQ_DESC = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,"
			+ " PRE_CLOSE_PRICE, OPEN_PRICE from NCDEX_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_NSECDS_LTP_UNIQ_DESC = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,"
			+ " PRE_CLOSE_PRICE, OPEN_PRICE from NCD_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_BSECDS_LTP_UNIQ_DESC = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,"
			+ " PRE_CLOSE_PRICE, OPEN_PRICE from BSECDS_QUOTE where SYMBOL IN(%s)";
	
	public static final String GET_GROUP_ALLOCATION = "SELECT nTokenSegment,CM_mcaptype,CM_SectorName FROM SCRIPMASTER where nTokenSegment IN(%s)";
	
	public static final String GET_NSE_QUOTE = "SELECT LAST_PRICE, PRE_CLOSE_PRICE,  UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NSE_QUOTE where SYMBOL = ?";

	public static final String GET_NFO_QUOTE = "SELECT LAST_PRICE, PRE_CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NFO_QUOTE where SYMBOL = ?";

	public static final String GET_BSE_QUOTE = "SELECT LAST_PRICE, PRE_CLOSE_PRICE, UPPER_CIR_LIMIT,  "
			+ "LOWER_CIR_LIMIT from BSE_QUOTE where SYMBOL = ?";

	public static final String GET_MCX_QUOTE = "SELECT LAST_PRICE, PRE_CLOSE_PRICE, UPPER_CIR_LIMIT,  "
			+ "LOWER_CIR_LIMIT from MFO_QUOTE where SYMBOL = ?";

	public static final String GET_NCDEX_QUOTE = "SELECT LAST_PRICE, PRE_CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NCDEX_QUOTE where SYMBOL = ?";

	public static final String GET_NSECDS_QUOTE = "SELECT LAST_PRICE, PRE_CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NCD_QUOTE where SYMBOL = ?";

	public static final String GET_BSECDS_QUOTE = "SELECT LAST_PRICE, PRE_CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from BSECDS_QUOTE where SYMBOL = ?";
	
	public static final String GET_OI_MCX = "SELECT OPENINTEREST from MFO_QUOTE where SYMBOL = ?";

	public static final String GET_FUTURES_TOKEN = "SELECT nTokenSegment FROM SCRIPMASTER WHERE sSymbol = ? "
			+ "AND nMarketSegmentId = ? AND sInstrumentName LIKE ? ORDER BY nExpiryDate";

	public static final String GET_NEWS_NOW = "call get_news(?,?,now())";
	public static final String GET_NEWS = "call get_news(?,?,?)";
	public static final String GET_NEWS_BY_SYMBOL = "call get_news_by_symbol(?,?)";
	public static final String GET_NEWS_BY_COMPANY_NOW = "call get_news_by_company(?,?,?,now())";
	public static final String GET_NEWS_BY_COMPANY = "call get_news_by_company(?,?,?,?)";
	public static final String GET_NEWS_BY_MARKET = "call get_news_by_market()";
	public static final String UPDATE_SCRIPMASTER = "UPDATE SCRIPMASTER SET CM_co_code=?, CM_BSECode=?,CM_CategoryName=?, CM_BSEGroup=?, CM_mcaptype=?, CM_SectorCode=?, CM_SectorName=? WHERE sISINCode=?";
	public static final String INSERT_PREDEFINED_WATCHLIST = "INSERT INTO predefined_watchlist(indexCode, indexName, exchange, isWatchlist, watchlistWeightage, created_at) VALUES(?, ?, ?, ?, ?, NOW())"
			+" ON DUPLICATE KEY UPDATE indexCode=VALUES(indexCode), isWatchlist=VALUES(isWatchlist), watchlistWeightage=VALUES(watchlistWeightage)";
	public static final String INSERT_PREDEFINED_WATCHLIST_SYMBOLS = "INSERT INTO predefined_watchlist_symbols(indexCode, indexName, symbol, isin, created_at) values(?, ?, ?, ?, NOW())"
			+" ON DUPLICATE KEY UPDATE indexCode=VALUES(indexCode), symbol=VALUES(symbol)";
	public static final String GET_WATCHLISTS = "SELECT indexCode, indexName, exchange FROM predefined_watchlist WHERE isWatchlist = '1' order by watchlistWeightage";
	public static final String GET_PREDEFINED_WATCHLIST_SYMBOLS = "SELECT isin FROM predefined_watchlist_symbols WHERE indexCode = ?";
	
	public static final String DELETE_EQUITIES = "call delete_equities(?)";
	
	public static final String DELETE_EXPIRED_CONTRACTS = "call delete_expired_contracts()";
	
	public static final String DELETE_NCDEX_BSECDS_SCRIPS = "DELETE FROM SCRIPMASTER where nMarketSegmentId in ('7','38')";
	
	public static final String SELECT_COCODE = "Select nToken,CM_co_code,CM_SectorFormat from SCRIPMASTER where nMarketSegmentId='1'";
	
	public static final String UPDATE_COCODE = "UPDATE SCRIPMASTER SET CM_co_code=?, CM_BSECode=?,CM_CategoryName=?, CM_BSEGroup=?, CM_mcaptype=?, CM_SectorCode=?, CM_SectorName=?, CM_SectorFormat=? WHERE sISINCode=? AND nMarketSegmentId IN (1,3)";
	
	public static final String INSERT_AMO_DETAILS = "INSERT INTO AMO_DETAILS(EXCHANGE, START_TIME, END_TIME, "
			+ "DATE, CREATED_AT, UPDATED_AT) VALUES(?, ?, ?, ?, NOW(), NOW()) ON DUPLICATE KEY UPDATE "
			+ "EXCHANGE=VALUES(EXCHANGE), START_TIME=VALUES(START_TIME), END_TIME=VALUES(END_TIME),"
			+ "DATE=VALUES(DATE), UPDATED_AT=NOW()";
	public static final String GET_AMO_DETAILS = "SELECT EXCHANGE, DATE, START_TIME, END_TIME FROM AMO_DETAILS";

	 public static final String INSERT_FT_INDICES = "INSERT INTO FT_INDICES(nTokenSegment, sSymbol, sSecurityDesc, nToken, nMarketSegmentId, sExchange, sISINCode, nPrecision, nRegularLot, DecimalLocator, ExchangeName, sInstrumentName, nPriceTick,"
	 		+ " dispPriceTick, sSeries, ExpiryDate, nStrikePrice, sOptionType, nSearchSymbolDetails, SymbolUniqDesc, SymbolDetails1, SymbolDetails2,  isIndexforOverview, segmentType, indexWeightage, created_at, companyName, mappingSymbolUniqDesc)"
	 		+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?) ON DUPLICATE KEY UPDATE sSymbol=VALUES(sSymbol), sSecurityDesc=VALUES(sSecurityDesc), nToken=VALUES(nToken), nMarketSegmentId=VALUES(nMarketSegmentId),"
			+ " sExchange=VALUES(sExchange),sISINCode = VALUES(sISINCode), nPrecision = VALUES(nPrecision), nRegularLot = VALUES(nRegularLot), DecimalLocator = VALUES(DecimalLocator),"
			+ " isIndexforMarket=VALUES(isIndexforMarket), segmentType=VALUES(segmentType), indexWeightage = VALUES(indexWeightage), ExchangeName=VALUES(ExchangeName), nPriceTick=VALUES(nPriceTick), dispPriceTick=VALUES(dispPriceTick), sSeries=VALUES(sSeries), ExpiryDate=VALUES(ExpiryDate)," 
			+ " nStrikePrice=VALUES(nStrikePrice), sOptionType=VALUES(sOptionType), nSearchSymbolDetails=VALUES(nSearchSymbolDetails), SymbolUniqDesc=VALUES(SymbolUniqDesc),"
			+ " SymbolDetails1=VALUES(SymbolDetails1), SymbolDetails2=VALUES(SymbolDetails2), companyName=VALUES(companyName), updated_at=NOW(), mappingSymbolUniqDesc=VALUES(mappingSymbolUniqDesc)";
	
	public static final String GET_FT_INDICES = "SELECT nTokenSegment FROM FT_INDICES where isIndexforOverview = '1' and segmentType = ? order by indexWeightage";
	public static final String GET_FT_INDICES_BY_EXCHANGE = "SELECT nTokenSegment FROM FT_INDICES where segmentType = ? AND sExchange = ?";
	public static final String GET_LIVE_NEWS = "SELECT description, date, time from news where category = ? order by news_time desc limit 1 ";
	
	public static final String INSERT_FT_INDICES_MARKET = "call add_indices(?, ?, ?, ?)";
	
	public static final String GET_JOB_INFO = "SELECT USERID, PASSWORD, CONFIG FROM JOB_INFO WHERE JOBNAME = ?";
	public static final String UPDATE_CREDENTIALS = "UPDATE JOB_INFO SET PASSWORD = ? WHERE USERID = ? AND JOBNAME = ?";
	
	public static final String GET_INDICES = "SELECT * FROM FT_INDICES";
	public static final String SEARCH_NEWS = "call search_news(?,?,?)";
	
	public static final String DELETE_EXPIRED_INDICES = "call delete_expired_indices()";
	
	public static final String UPDATE_FNO_EXISTS = "UPDATE SCRIPMASTER SET isFNOExists=false WHERE sSymbol=?";

//Newly Added Search queries- 3 word search
	
public static final String SYMBOL_EXCHANGE_INSTRUMENT = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and instrumentForSearch like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_EXCHANGE_DATE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and ( ExpiryDate like ? OR nStrikePrice like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_EXCHANGE_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

	public static final String SYMBOL_EXCHANGE_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_OPTION_DATE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND sOptionType like ? and ( ExpiryDate like ? OR nStrikePrice like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_DATE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and (ExpiryDate like ? OR nStrikePrice like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DATE_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ( ExpiryDate like ? OR nStrikePrice like ? ) and ( nStrikePrice like ? OR ExpiryDate like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";

//Newly Added Search queries- 4 word search
	
	public static final String SYMBOL_INSTRUMENT_DATE_EXCHANGE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and (ExpiryDate like ? or nStrikePrice like ?) and ExchangeName like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_DATE_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and ( ExpiryDate like ? OR nStrikePrice like ? ) and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_DATE_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and ( ExpiryDate like ? or nStrikePrice like ? ) and ( nStrikePrice like ? or ExpiryDate like ? )"
			+ " order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_MONTH_DATE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and ExpiryDate like ? and ( ExpiryDate like ? or nStrikePrice like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_EXCHANGE_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and ExchangeName like ? and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and ExchangeName like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND instrumentForSearch like ? and sOptionType like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DAY_EXCHANGE_MONTH = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ( ExpiryDate like ? OR nStrikePrice like ? ) and ExchangeName like ? and ExpiryDate like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DAY_EXCHANGE_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND (ExpiryDate like ? OR nStrikePrice like ? ) and ExchangeName like ? and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DAY_EXCHANGE_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ( ExpiryDate like ? OR nStrikePrice like ? ) and ExchangeName like ? and ( nStrikePrice like ? or ExpiryDate like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DAY_OPTION_MONTH = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ( ExpiryDate like ? OR nStrikePrice like ? ) and sOptionType like ? and ExpiryDate like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DATE_STRIKEPRICE_MONTH = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExpiryDate like ? and nStrikePrice like ? and ExpiryDate like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DAY_OPTION_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ( ExpiryDate like ? OR nStrikePrice like ? ) and sOptionType like ? and ( nStrikePrice like ? OR ExpiryDate like ? ) order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_MONTH_EXCHANGE_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExpiryDate like ? and ExchangeName like ? and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_MONTH_EXCHANGE_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExpiryDate like ? and ExchangeName like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_MONTH_OPTION_EXCHANGE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExpiryDate like ? and sOptionType like ? and ExchangeName like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_MONTH_OPTION_STRIKE_PRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExpiryDate like ? and sOptionType like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_EXCHANGE_DAY_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and ExpiryDate like ? and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_EXCHANGE_INSTRUMENT_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and instrumentForSearch like ? and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_EXCHANGE_OPTION_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and sOptionType like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_OPTION_DAY_EXCHANGE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExchangeName like ? and instrumentForSearch like ? and sOptionType like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DAY_MONTH_EXCHANGE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ?  AND ExpiryDate like ? and ExpiryDate like ? and ExchangeName like ? order by FIELD(ExchangeName,'NSE','BSE','NFO','MCX','NSECDS'),FIELD(instrumentForSearch,'FUT','OPT'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)),FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DATE_MONTH_STRIKEPRICE = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and ExpiryDate like ? and ExpiryDate like ? and nStrikePrice like ? order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)), FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DATE_MONTH_OPTION = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and (ExpiryDate like ? or nStrikePrice like ?) and ExpiryDate like ? and sOptionType like ? order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)), FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	public static final String SYMBOL_DATE_MONTH_INSTRUMENT = "SELECT sISINCode,sSymbol,nTokenSegment,ExchangeName,instrumentForSearch,expiryForSearch,sOptionType,nStrikePrice,SymbolDetails,nPriceTick,nRegularLot,sSeries,nMarketSegmentId,nToken,sInstrumentName,ExpiryDate,nPrecision,nSearchSymbolDetails,sSecurityDesc,dispPriceTick,SymbolDetails,DecimalLocator,nBasePrice,SymbolDetails1,SymbolDetails2,companyName,mappingSymbolUniqDesc from SCRIPMASTER WHERE sSymbol like ? and (ExpiryDate like ? or nStrikePrice like ?) and ExpiryDate like ? and instrumentForSearch like ? order by FIELD(ExchangeName,'NFO','MCX','NSECDS'),nExpiryDate,CAST(nStrikePrice AS DECIMAL(8,2)), FIELD(sOptionType,'CE','PE') LIMIT 25";
	
	//MarketData Query Constants
	
	public static final String WEEKS_LOW_OVERVIEW_EQ="SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN FROM INDEX_DETAILS I  INNER JOIN %s Q on I.SYMBOL = Q.SYMBOL"
			+ " WHERE LOW_PRICE = 52_WK_LOW AND LOW_PRICE > 0 AND I.INDEX_NAME = ?  AND Q.SYMBOL IS NOT NULL ORDER BY CHANGE_PER limit %s";
	
	public static final String WEEKS_HIGH_OVERVIEW_EQ ="SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN FROM INDEX_DETAILS I  INNER JOIN %s Q on I.SYMBOL = Q.SYMBOL"
			+ " WHERE HIGH_PRICE = 52_WK_HIGH AND HIGH_PRICE > 0 AND I.INDEX_NAME = ?  AND Q.SYMBOL IS NOT NULL ORDER BY CHANGE_PER DESC limit %s";	
	
	public static final String UPPER_CIRCUIT_EQ="SELECT  Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE, Q.EXCHANGE_TOKEN FROM  INDEX_DETAILS I  INNER JOIN %s"
			+" Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.UPPER_CIR_LIMIT = Q.LAST_PRICE AND TOTAL_SELL_QTY = 0 AND TOTAL_BUY_QTY > 0 ORDER BY TOTAL_BUY_QTY DESC LIMIT %s";
	
	public static final String LOWER_CIRCUIT_EQ="SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE, Q.EXCHANGE_TOKEN FROM INDEX_DETAILS I  INNER JOIN %s"
			+" Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.LOWER_CIR_LIMIT = Q.LAST_PRICE AND TOTAL_SELL_QTY > 0 AND TOTAL_BUY_QTY = 0 ORDER BY TOTAL_SELL_QTY DESC LIMIT %s";
	
	public static final String MOST_ACTIVE_BY_VALUE ="SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE, Q.EXCHANGE_TOKEN from INDEX_DETAILS I INNER JOIN %s "
			+ "Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND TOTAL_TRADED_VALUE > 0 ORDER BY TOTAL_TRADED_VALUE DESC LIMIT %s";
	
	public static final String TOP_GAINERS = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND CHANGE_PER > 0 ORDER BY CHANGE_PER DESC LIMIT %s";
	
	public static final String TOP_GAINERS_DERIVATIVES_SYM = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.CHANGE_PER > 0"
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), Q.CHANGE_PER DESC LIMIT %s";
	
	public static final String TOP_GAINERS_CUR_SYM = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN"
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.CHANGE_PER > 0"
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCUR','OPTCUR'), Q.CHANGE_PER DESC LIMIT %s";

	public static final String TOP_GAINERS_COM_SYM = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN"
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.CHANGE_PER > 0 AND `CHANGE` > 0 "
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), Q.CHANGE_PER DESC LIMIT %s";

	
	public static final String TOP_LOSERS = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND CHANGE_PER < 0 ORDER BY CHANGE_PER LIMIT %s";
	
	public static final String TOP_LOSERS_DERIVATIVES_SYM = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.CHANGE_PER < 0"
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), Q.CHANGE_PER LIMIT %s";
	
	public static final String TOP_LOSERS_CUR_SYM = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.CHANGE_PER < 0"
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCUR','OPTCUR'), Q.CHANGE_PER LIMIT %s";
	
	public static final String TOP_LOSERS_COM_SYM = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.CHANGE_PER < 0 AND `CHANGE` < 0 AND LAST_PRICE > 0"
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), Q.CHANGE_PER LIMIT %s";
	
	public static final String ACTIVE_VOLUME_EQ = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN  from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND TOTAL_VOLUME > 0 ORDER BY TOTAL_VOLUME DESC LIMIT %s";
	
	public static final String OI_LOSERS_OVERVIEW_DERIVATIVE="SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS"
			+ " NOT NULL AND Q.OI_CHANGE < 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), Q.OI_CHANGE/Q.PREV_OPENINTEREST limit %s";
	
	public static final String OI_GAINERS_OVERVIEW_DERIVATIVE= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS"
			+ " NOT NULL  AND Q.OI_CHANGE > 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), Q.OI_CHANGE/Q.PREV_OPENINTEREST DESC limit %s";
	
	public static final String MOST_ACTIVE_BY_VALUE_DER= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS NOT NULL"
			+ " AND TOTAL_TRADED_VALUE > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), TOTAL_TRADED_VALUE DESC limit %s";
	
	public static final String ACTIVE_VOLUME_DERIVATIVE= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND TOTAL_VOLUME > 0 "
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), Q.CHANGE_PER LIMIT %s ";
	
	public static final String OI_GAINERS_OVERVIEW_COM= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS NOT NULL"
			+ " AND Q.OI_CHANGE > 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), Q.OI_CHANGE/Q.PREV_OPENINTEREST DESC limit %s";
	
	public static final String OI_GAINERS_OVERVIEW_CUR= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS NOT NULL"
			+ " AND Q.OI_CHANGE > 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCUR','OPTCUR'), Q.OI_CHANGE/Q.PREV_OPENINTEREST DESC limit %s";
	
	public static final String OI_LOSERS_OVERVIEW_CUR= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS NOT NULL"
			+ " AND Q.OI_CHANGE < 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCUR','OPTCUR'), Q.OI_CHANGE/Q.PREV_OPENINTEREST limit %s";
	
	public static final String OI_LOSERS_OVERVIEW_COM= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS NOT NULL"
			+ " AND Q.OI_CHANGE < 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), Q.OI_CHANGE/Q.PREV_OPENINTEREST limit %s";
	
	public static final String MOST_ACTIVE_BY_VALUE_CUR="SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS NOT NULL"
			+ " AND TOTAL_TRADED_VALUE > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCUR','OPTCUR'), TOTAL_TRADED_VALUE DESC limit %s";
	
	public static final String MOST_ACTIVE_BY_VALUE_COM="SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q WHERE Q.SYMBOL IS NOT NULL"
			+ " AND TOTAL_TRADED_VALUE > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), TOTAL_TRADED_VALUE DESC limit %s";
	
	public static final String ACTIVE_VOLUME_CUR= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN " 
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND TOTAL_VOLUME > 0 "
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCUR','OPTCUR'), Q.CHANGE_PER LIMIT %s ";

	public static final String ACTIVE_VOLUME_COM= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN " 
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND TOTAL_VOLUME > 0 "
			+ " ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), Q.CHANGE_PER LIMIT %s ";

	public static final String LOAD_EXPIRIES = "SELECT distinct EXPIRYDATE, EXCHANGENAME, SINSTRUMENTNAME from SCRIPMASTER WHERE EXCHANGENAME IN ('NFO','NSECDS','MCX') ORDER BY FIELD(EXCHANGENAME,'NFO','NSECDS','MCX'), nExpiryDate";
	
	public static final String TOP_GAINERS_EQUITY_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND CHANGE_PER > 0 ORDER BY CHANGE_PER DESC";
	
	public static final String TOP_LOSERS_EQUITY_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND CHANGE_PER < 0 ORDER BY CHANGE_PER";
	
	public static final String ACTIVE_VOLUME_EQ_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN  from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND TOTAL_VOLUME > 0 ORDER BY TOTAL_VOLUME DESC";
	
	public static final String ACTIVE_VALUE_EQ_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND TOTAL_TRADED_VALUE > 0 ORDER BY TOTAL_TRADED_VALUE DESC";
	
	public static final String ALL_TIME_HIGH_EQ_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND Q.HIGH_PRICE = Q.ALL_TIME_HIGH ORDER BY CHANGE_PER DESC";
	
	public static final String ALL_TIME_HIGH_EQ_OVERVIEW = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.HIGH_PRICE = Q.ALL_TIME_HIGH ORDER BY CHANGE_PER DESC limit %s";
	
	public static final String ALL_TIME_LOW_EQ_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND Q.LOW_PRICE = Q.ALL_TIME_LOW ORDER BY CHANGE_PER";
	
	public static final String ALL_TIME_LOW_EQ_OVERVIEW = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.LOW_PRICE = Q.ALL_TIME_LOW ORDER BY CHANGE_PER limit %s";
	
	public static final String GET_UPPER_CIR_EQ_ALL = "SELECT  Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN FROM  INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND Q.UPPER_CIR_LIMIT = Q.LAST_PRICE AND TOTAL_SELL_QTY = 0 AND TOTAL_BUY_QTY > 0 ORDER BY TOTAL_BUY_QTY DESC";

	public static final String GET_LOWER_CIR_EQ_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN FROM INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND Q.LOWER_CIR_LIMIT = Q.LAST_PRICE AND TOTAL_SELL_QTY > 0 AND TOTAL_BUY_QTY = 0 ORDER BY TOTAL_SELL_QTY DESC ";
	
	public static final String GET_YEAR_HIGH_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN FROM INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL"
			+ " WHERE HIGH_PRICE = 52_WK_HIGH AND HIGH_PRICE > 0 AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND Q.SYMBOL IS NOT NULL"
			+ " ORDER BY CHANGE_PER DESC";

	public static final String GET_YEAR_LOW_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN FROM INDEX_DETAILS I  INNER JOIN "
			+ " %s Q on I.SYMBOL = Q.SYMBOL"
			+ " WHERE LOW_PRICE = 52_WK_LOW AND LOW_PRICE > 0 AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND Q.SYMBOL IS NOT NULL"
			+ " ORDER BY CHANGE_PER";
	
	public static final String TOP_GAINERS_DERIVATIVES_ALL_NO_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND Q.CHANGE_PER > 0"
			+ " ORDER BY EXPIRY_DATE, Q.CHANGE_PER DESC";
	
	public static final String TOP_LOSERS_DERIVATIVES_ALL_NO_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND Q.CHANGE_PER < 0 AND LAST_PRICE > 0"
			+ " ORDER BY EXPIRY_DATE, Q.CHANGE_PER";
	
	public static final String TOP_GAINERS_DERIVATIVES_ALL_WITH_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND EXPIRY_DATE = ? AND Q.CHANGE_PER > 0"
			+ " ORDER BY Q.CHANGE_PER DESC";
	
	public static final String TOP_LOSERS_DERIVATIVES_ALL_WITH_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN "
			+ " from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND EXPIRY_DATE = ? AND Q.CHANGE_PER < 0 AND LAST_PRICE > 0"
			+ " ORDER BY Q.CHANGE_PER";
	
	public static final String ACTIVE_VOLUME_DERIVATIVES_NO_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN  from %s Q "
			+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND TOTAL_VOLUME > 0 ORDER BY TOTAL_VOLUME DESC";

	public static final String ACTIVE_VOLUME_DERIVATIVES_WITH_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN  from %s Q "
				+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND EXPIRY_DATE = ? AND TOTAL_VOLUME > 0 ORDER BY TOTAL_VOLUME DESC";
		
	public static final String ACTIVE_VALUE_DERIVATIVES_NO_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN  from %s Q "
				+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND TOTAL_TRADED_VALUE > 0 ORDER BY TOTAL_TRADED_VALUE DESC";
	
	public static final String ACTIVE_VALUE_DERIVATIVES_WITH_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN  from %s Q "
				+ " WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND EXPIRY_DATE = ? AND TOTAL_TRADED_VALUE > 0 ORDER BY TOTAL_TRADED_VALUE DESC";
	
	public static final String OI_GAINERS_DERIVATIVES_NO_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND Q.OI_CHANGE > 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY EXPIRY_DATE, Q.OI_CHANGE/Q.PREV_OPENINTEREST DESC";
	
	public static final String OI_GAINERS_DERIVATIVES_WITH_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND EXPIRY_DATE = ? AND Q.OI_CHANGE > 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY Q.OI_CHANGE/Q.PREV_OPENINTEREST DESC";

	public static final String OI_LOSERS_DERIVATIVES_NO_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND Q.OI_CHANGE < 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY EXPIRY_DATE, Q.OI_CHANGE/Q.PREV_OPENINTEREST ASC";
	
	public static final String OI_LOSERS_DERIVATIVES_WITH_EXPIRY = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.INSTRUMENT_TYPE = ? AND EXPIRY_DATE = ? AND  Q.OI_CHANGE < 0 AND Q.PREV_OPENINTEREST > 0 ORDER BY Q.OI_CHANGE/Q.PREV_OPENINTEREST ASC";
	
	public static final String OI_ANALYSIS_DERIVATIVES_LONG_BUILDUP = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL  AND  Q.OI_CHANGE > 0 AND Q.CHANGE > 0 ORDER BY OI_PER DESC";
	
	public static final String OI_ANALYSIS_DERIVATIVES_SHORT_BUILDUP = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL  AND  Q.OI_CHANGE > 0 AND Q.CHANGE < 0 ORDER BY OI_PER DESC";
	
	public static final String OI_ANALYSIS_DERIVATIVES_LONG_UNWINDING = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL  AND  Q.OI_CHANGE < 0 AND Q.CHANGE < 0 ORDER BY OI_PER";
	
	public static final String OI_ANALYSIS_DERIVATIVES_SHORT_COVERING = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL  AND  Q.OI_CHANGE < 0 AND Q.CHANGE > 0 ORDER BY OI_PER";
	
	public static final String ROLLOVER_ANALYSIS_DERIVATIVE = "SELECT ROLLOVER_PERCENTAGE, ROLLOVER_COST, ROLLOVER_COST_PER, BASE_SYMBOL, EXCHANGE, EXPIRY_DATE, INSTRUMENT_TYPE from ROLLOVER WHERE INSTRUMENT_TYPE = ? and EXCHANGE = ? order by ROLLOVER_PERCENTAGE %s ";
	
	public static final String ROLLOVER_ANALYSIS_DERIVATIVE_OVERVIEW = "SELECT ROLLOVER_PERCENTAGE, ROLLOVER_COST, ROLLOVER_COST_PER, BASE_SYMBOL, EXCHANGE, EXPIRY_DATE, INSTRUMENT_TYPE from ROLLOVER WHERE INSTRUMENT_TYPE = ? and EXCHANGE = ? order by ROLLOVER_PERCENTAGE %s limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_DER_SHORT_COVERING= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE < 0 AND Q.CHANGE > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), OI_PER limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_DER_LONG_UNWINDING= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE < 0 AND Q.CHANGE < 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), OI_PER limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_DER_SHORT_BUILDUP= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE > 0 AND Q.CHANGE < 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), OI_PER DESC limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_DER_LONG_BUILDUP= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE > 0 AND Q.CHANGE > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTSTK','FUTIDX','OPTSTK','OPTIDX'), OI_PER DESC limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_COM_SHORT_COVERING= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE < 0 AND Q.CHANGE > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), OI_PER limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_COM_LONG_UNWINDING= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE < 0 AND Q.CHANGE < 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), OI_PER limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_COM_SHORT_BUILDUP= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE > 0 AND Q.CHANGE < 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), OI_PER DESC limit %s";
	
	public static final String OI_ANALYSIS_OVERVIEW_COM_LONG_BUILDUP= "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.OI_CHANGE, Q.OPENINTEREST, Q.OI_CHANGE/Q.PREV_OPENINTEREST as OI_PER from %s Q "
			+ "WHERE Q.SYMBOL IS NOT NULL AND Q.OI_CHANGE > 0 AND Q.CHANGE > 0 ORDER BY FIELD(INSTRUMENT_TYPE,'FUTCOM','FUTIDX','OPTCOM'), OI_PER DESC limit %s";
	
	public static final String FNO_OVERVIEW_PCR="select OI_RATIO from PCR_DATA where BASE_SYMBOL=? and EXPIRY_DATE=?";
	
	public static final String FNO_OVERVIEW_NSE="select LAST_PRICE from NSE_QUOTE where SYMBOL=?";
	
	public static final String FNO_OVERVIEW_NFO="select OPENINTEREST,(OI_CHANGE/PREV_OPENINTEREST)*100 AS CHANGE_PER,OI_CHANGE,EXPIRY_DATE,LAST_PRICE from NFO_QUOTE where SYMBOL=? ";

	public static final String FNO_OVERVIEW_MFO="select OPENINTEREST,(OI_CHANGE/PREV_OPENINTEREST)*100 AS CHANGE_PER,OI_CHANGE,EXPIRY_DATE,LAST_PRICE from MFO_QUOTE where SYMBOL=? ";

	public static final String FNO_OVERVIEW_NCD="select OPENINTEREST,(OI_CHANGE/PREV_OPENINTEREST)*100 AS CHANGE_PER,OI_CHANGE,EXPIRY_DATE,LAST_PRICE from NCD_QUOTE where SYMBOL=? ";

	public static final String FNO_OVERVIEW_ROLLOVER="select ROLLOVER_PERCENTAGE, ROLLOVER_COST from ROLLOVER where BASE_SYMBOL=? and EXPIRY_DATE=?";

	public static final String FNO_OVERVIEW_SCRIPMASTER="select nIssueStartDate,nIssueMaturityDate,nNoDeliveryStartDate,nNoDeliveryEndDate,nMaxSingleTransactionQty,nAssetToken,sQtyUnit from SCRIPMASTER where nTokenSegment=?";
	
	public static final String VOLUME_SHOCKERS_OVERVIEW = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND ((Q.TOTAL_VOLUME - Q.20_DAY_AVG_VOL)/Q.TOTAL_VOLUME)*100 > 50 ORDER BY Q.TOTAL_VOLUME/Q.20_DAY_AVG_VOL DESC limit %s";
	
	public static final String VOLUME_SHOCKERS_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN from INDEX_DETAILS I  INNER JOIN %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND ((Q.TOTAL_VOLUME - Q.20_DAY_AVG_VOL)/Q.TOTAL_VOLUME)*100 > 50 ORDER BY Q.TOTAL_VOLUME/Q.20_DAY_AVG_VOL DESC";
	
	public static final String PRICE_SHOCKERS_OVERVIEW = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.PRE_CLOSE_PRICE, Q.10_DAY_AVG_PRC, ((Q.PRE_CLOSE_PRICE - Q.LAST_PRICE)/Q.PRE_CLOSE_PRICE)*100 as C1, ((Q.10_DAY_AVG_PRC - Q.LAST_PRICE)/Q.10_DAY_AVG_PRC)*100 as C2, ((Q.LAST_PRICE - Q.PRE_CLOSE_PRICE)/Q.LAST_PRICE)*100 as C3, ((Q.10_DAY_AVG_PRC - Q.LAST_PRICE)/Q.10_DAY_AVG_PRC)*100 as C4, I.INDEX_NAME  from INDEX_DETAILS I INNER JOIN %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND ((((Q.PRE_CLOSE_PRICE - Q.LAST_PRICE)/Q.PRE_CLOSE_PRICE)*100 > 2 AND ((Q.10_DAY_AVG_PRC - Q.LAST_PRICE)/Q.10_DAY_AVG_PRC)*100 > 3 ) OR  (((Q.LAST_PRICE - Q.PRE_CLOSE_PRICE)/Q.LAST_PRICE)*100 > 2 AND ((Q.LAST_PRICE - Q.10_DAY_AVG_PRC)/Q.LAST_PRICE)*100 > 3 )) ORDER BY CHANGE_PER desc "
			+ " limit %s";
	
	public static final String PRICE_SHOCKERS_ALL = "SELECT Q.CHANGE, Q.CHANGE_PER, Q.LAST_PRICE,Q.EXCHANGE_TOKEN, Q.PRE_CLOSE_PRICE, Q.10_DAY_AVG_PRC, ((Q.PRE_CLOSE_PRICE - Q.LAST_PRICE)/Q.PRE_CLOSE_PRICE)*100 as C1, ((Q.10_DAY_AVG_PRC - Q.LAST_PRICE)/Q.10_DAY_AVG_PRC)*100 as C2, ((Q.LAST_PRICE - Q.PRE_CLOSE_PRICE)/Q.LAST_PRICE)*100 as C3, ((Q.10_DAY_AVG_PRC - Q.LAST_PRICE)/Q.10_DAY_AVG_PRC)*100 as C4, I.INDEX_NAME  from INDEX_DETAILS I INNER JOIN %s Q on I.SYMBOL = Q.SYMBOL WHERE Q.SYMBOL IS NOT NULL AND I.INDEX_NAME = ? AND Q.EXCHANGE = ? AND ((((Q.PRE_CLOSE_PRICE - Q.LAST_PRICE)/Q.PRE_CLOSE_PRICE)*100 > 2 AND ((Q.10_DAY_AVG_PRC - Q.LAST_PRICE)/Q.10_DAY_AVG_PRC)*100 > 3 ) OR  (((Q.LAST_PRICE - Q.PRE_CLOSE_PRICE)/Q.LAST_PRICE)*100 > 2 AND ((Q.LAST_PRICE - Q.10_DAY_AVG_PRC)/Q.LAST_PRICE)*100 > 3 )) ORDER BY CHANGE_PER desc "
			+ "";

	public static final String GET_NSE_CHART_HISTORY = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NSE_CHART_INTRADAY_HISTORY where SYMBOL = ? AND TIME >= ? and TIME <= ?";

	public static final String GET_NSECDS_CHART_HISTORY = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NCD_CHART_INTRADAY_HISTORY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_NCDEX_CHART_HISTORY = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NCO_CHART_INTRADAY_HISTORY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_NFO_CHART_HISTORY = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from NFO_CHART_INTRADAY_HISTORY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_BSE_CHART_HISTORY = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from BSE_CHART_INTRADAY_HISTORY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_BSECDS_CHART_HISTORY = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from BCD_CHART_INTRADAY_HISTORY where SYMBOL = ? AND TIME >= ? and TIME <= ?";
	
	public static final String GET_MCX_CHART_HISTORY = "SELECT OPEN, HIGH, LOW, CLOSE, VOLUME, TIME from MFO_CHART_INTRADAY_HISTORY where SYMBOL = ? AND TIME >= ? and TIME <= ?";

	//OMDF based Queries
	
	public static final String GET_NSE_LTP_OMDF = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE from NSE_QUOTE where SYMBOL = ?";

	public static final String GET_NFO_LTP_OMDF = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE from NFO_QUOTE where SYMBOL = ?";

	public static final String GET_BSE_LTP_OMDF = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE from BSE_QUOTE where SYMBOL = ?";

	public static final String GET_MCX_LTP_OMDF = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE from MCX_QUOTE where SYMBOL = ?";

	public static final String GET_NCDEX_LTP_OMDF = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE from NCDEX_QUOTE where SYMBOL = ?";

	public static final String GET_NSECDS_LTP_OMDF = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE from NSECDS_QUOTE where SYMBOL = ?";
	
	public static final String GET_BSECDS_LTP_OMDF = "SELECT LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE from BSECDS_QUOTE where SYMBOL = ?";
	
	public static final String GET_GROUP_NSE_LTP_OMDF = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE "
			+ "from NSE_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_NFO_LTP_OMDF = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE "
			+ "from NFO_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_BSE_LTP_OMDF = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE "
			+ "from BSE_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_MCX_LTP_OMDF = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE "
			+ "from MCX_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_NCDEX_LTP_OMDF = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE "
			+ "from NCDEX_QUOTE where SYMBOL IN(%s)";
	
	public static final String GET_GROUP_NSECDS_LTP_OMDF = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE "
			+ "from NSECDS_QUOTE where SYMBOL IN(%s)";

	public static final String GET_GROUP_BSECDS_LTP_OMDF = "SELECT SYMBOL, LAST_PRICE, `CHANGE`, CHANGE_PER,CLOSE_PRICE, OPEN_PRICE "
			+ "from BSECDS_QUOTE where SYMBOL IN(%s)";

	public static final String GET_NSE_QUOTE_OMDF = "SELECT LAST_PRICE, CLOSE_PRICE,  UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NSE_QUOTE where SYMBOL = ?";

	public static final String GET_NFO_QUOTE_OMDF = "SELECT LAST_PRICE, CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NFO_QUOTE where SYMBOL = ?";

	public static final String GET_BSE_QUOTE_OMDF = "SELECT LAST_PRICE, CLOSE_PRICE, UPPER_CIR_LIMIT,  "
			+ "LOWER_CIR_LIMIT from BSE_QUOTE where SYMBOL = ?";

	public static final String GET_MCX_QUOTE_OMDF = "SELECT LAST_PRICE, CLOSE_PRICE, UPPER_CIR_LIMIT,  "
			+ "LOWER_CIR_LIMIT from MCX_QUOTE where SYMBOL = ?";

	public static final String GET_NCDEX_QUOTE_OMDF = "SELECT LAST_PRICE, CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NCDEX_QUOTE where SYMBOL = ?";

	public static final String GET_NSECDS_QUOTE_OMDF = "SELECT LAST_PRICE, CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from NSECDS_QUOTE where SYMBOL = ?";

	public static final String GET_BSECDS_QUOTE_OMDF = "SELECT LAST_PRICE, CLOSE_PRICE, UPPER_CIR_LIMIT, "
			+ "LOWER_CIR_LIMIT from BSECDS_QUOTE where SYMBOL = ?";
	
	public static final String GET_SESSION_INFO = "SELECT j_key, ft_session_id, app_id from CLIENT_SESSION where USER_ID = ?";
	
	public static final String INSERT_PAYIN_DETAILS ="INSERT INTO PAYMENT(MERCHANT_REF_NO,CLIENT_ID,AMOUNT,BANK_ACCOUNT_NO,PG_ORDER_ID,PG_STATUS,STATUS,GATEWAY,PAYMENT_CHANNEL,STAGE,PGTRANS_UPDATE_TIME, TRANS_ADDITIONAL_INFO) VALUES(?,?,?,?,?,?,?,?,?,?,NOW(),?)";
	
	public static final String GET_PAYIN_DETAILS = "SELECT AMOUNT, STATUS, MERCHANT_REF_NO, CREATED_AT, PAYMENT_CHANNEL FROM PAYMENT WHERE CLIENT_ID = ? and CREATED_AT BETWEEN ? AND  ?";
	
	public static final String GET_CLIENT_HOLDINGS = "SELECT HOLDINGS from CLIENT_HOLDINGS WHERE CLIENT_CODE = ?";
	
	public static final String GET_FO_POSITIONS = "SELECT POSITIONS from FO_COMBINED_POSITIONS WHERE CLIENT_CODE = ?";
	
	public static final String INSERT_CLIENT_HOLDINGS = "INSERT INTO CLIENT_HOLDINGS (`CLIENT_CODE`,`HOLDINGS`) VALUES (?,?)";
	
	public static final String INSERT_FO_POSITIONS = "INSERT INTO FO_COMBINED_POSITIONS (`CLIENT_CODE`,`POSITIONS`) VALUES (?,?)";
	
	public static final String VERSION_UPDATE_DATA =  "SELECT ai.build,ai.device_type,ai.channel,ait.os_version,ait.app_version FROM APP_INFO ai, APP_INFO_TRANS ait WHERE ai.app_id = ? AND ai.app_id = ait.app_id ORDER BY ait.created_at DESC LIMIT 1";
	
	public static final String GET_TRANSACTION_DETAILS = "SELECT STAGE, STATUS FROM PAYMENT WHERE MERCHANT_REF_NO = ?";
	
	//webhook
	
	public static final String GET_MERCHANT_DETAILS = "select MERCHANT_REF_NO, TRANS_ADDITIONAL_INFO from PAYMENT where PG_ORDER_ID =? and PG_STATUS!= ? ";
	
	public static final String GET_MERCHANT_APP_ID = "SELECT app_id from CLIENT_SESSION where USER_ID = ?";

	public static final String UPDATE_CLIENT_ORD_NO = "UPDATE CLIENT_SESSION SET client_order_no = ? WHERE user_id = ?";

	//Notification
	public static final String UPDATE_NOTIFICATION_ACTIVE = "UPDATE ADVANCE_LOGIN SET NOTIFICATION_ACTIVE = 'Y'"
			+ " WHERE USER_ID = ?";
	
	public static final String UPDATE_NOTIFICATION_INACTIVE = "UPDATE ADVANCE_LOGIN SET NOTIFICATION_ACTIVE = 'N'"
			+ " WHERE USER_ID = ?";
	
	public static final String CHECK_NOTIFICATION_ACTIVE = "SELECT NOTIFICATION_ACTIVE FROM ADVANCE_LOGIN "
			+ "WHERE USER_ID = ?";
	

	// Session extention
	
	public static final String GET_USER_PASSWORD = "SELECT password FROM ADVANCE_SESSION WHERE user_id = ? and app_id = ?";
	
	public static final String GET_USER_DETAILS = "select user_id , app_id from CLIENT_SESSION where session_id = ?";
	
	public static final String GET_USER_DETAILS_ADVANCE = "select user_id from ADVANCE_SESSION where app_id =? order by updated_at desc limit 1";
	
	public static final String OTP_VALIDATION_PROCEDURE = "{CALL getOTPFlag(?,?,?,?)}";
	
	public static final String SET_OTP_STATUS="UPDATE ADVANCE_SESSION  SET is_otp_reqd=? ,app_id=? WHERE user_id=?";
	
	public static final String UPDATE_OTP_STATUS="UPDATE ADVANCE_SESSION SET is_otp_reqd='Y' WHERE app_id=? AND user_id !=?";
	
	public static final String RESET_OTP="UPDATE ADVANCE_SESSION SET is_otp_reqd='Y' WHERE app_id=?";
	
	public static final String GET_OTP_FLAG = "select user_id from ADVANCE_SESSION WHERE is_otp_reqd = ? and app_id = ?";
	
	public static final String INSERT_USER_RESESSION = "INSERT INTO CLIENT_SESSION (user_id, session_id, app_id, "
            + "build, created_at, last_active, user_type, user_info, ft_session, ft_session_id, j_key, "
            + "client_order_no) VALUES(?, ?, ?, (select BUILD from APP_INFO where APP_ID = ?)," 
            + "NOW(), UNIX_TIMESTAMP(), ?, ?,?,?, ?, ?)"
            + "ON DUPLICATE KEY UPDATE session_id = VALUES( session_id ), " + "app_id = VALUES(app_id),"
            + "created_at = NOW(), last_active = UNIX_TIMESTAMP(), "
            + "user_type= VALUES(user_type), user_info= VALUES(user_info), ft_session=VALUES(ft_session),"
            + "ft_session_id=VALUES(ft_session_id),"
            + "j_key = VALUES(j_key), client_order_no = VALUES(client_order_no)";
	
	public static final String UPDATE_USER_SESSION = "update CLIENT_SESSION set ft_session=?, ft_session_id=?, j_key=?, "
            + "client_order_no=?, created_at=now(), last_active= UNIX_TIMESTAMP() where user_id=? and session_id=?";

	public static final String CREATE_BASKET_ORDER_QUERY = "INSERT INTO BASKET_ORDER (BASKET_ID,BASKET_NAME,USER_ID,CREATED_AT) VALUES (?,?,?,now())"; 
	
	public static final String DELETE_BASKET_ORDER_QUERY = "DELETE FROM BASKET_ORDER WHERE BASKET_ID IN (%s)";
	
	public static final String RENAME_BASKET_ORDER_QUERY = "UPDATE BASKET_ORDER SET BASKET_NAME = ?,UPDATED_AT = NOW() WHERE BASKET_ID = ?";

	public static final String UPDATE_BASKET_ORDER_QUERY = "UPDATE BASKET_ORDER SET BASKET_SYMBOLS = ?, UPDATED_AT = NOW() WHERE BASKET_ID = ?";

	public static final String GET_BASKET_SYMBOLS_QUERY = "SELECT BASKET_SYMBOLS FROM BASKET_ORDER WHERE BASKET_ID = ?";
	
	public static final String GET_BASKETS_NAME_QUERY = "SELECT BASKET_NAME, BASKET_SYMBOLS, CREATED_AT,BASKET_ID FROM BASKET_ORDER WHERE USER_ID = ? order by cast(CREATED_AT as datetime) ASC";

	public static final String GET_BASKET_ORDER_SYMBOL_FOR_EXPIRY_JOB = "SELECT BASKET_ID, BASKET_SYMBOLS FROM BASKET_ORDER ";

	//OMEX
    public static final String INSERT_HEARTBEAT="INSERT INTO OMEX_HEARTBEAT (DATE,LAST_UPDATE_TIME) VALUES(?,?) ON DUPLICATE KEY UPDATE LAST_UPDATE_TIME=?";
    public static final String GET_LAST_UPDATE_TIME="SELECT LAST_UPDATE_TIME FROM OMEX_HEARTBEAT WHERE DATE=?";
    public static final String IS_TABLE_EMPTY="SELECT CASE WHEN EXISTS(SELECT 1 FROM OMEX_HEARTBEAT) THEN 0 ELSE 1 END AS IsEmpty";
    
    public static final String INSERT_ORDER_DATA="INSERT INTO ORDER_DATA (user_id,is_refresh_required,todays,derivatives,last_updated_time) values(?,?,?,?,now()) ON DUPLICATE KEY UPDATE is_refresh_required=?,todays=?,derivatives=?,last_updated_time=now()";
    public static final String GET_ORDER_DATA="SELECT * FROM ORDER_DATA WHERE user_id=?";
    public static final String IS_DATA_EXIST="SELECT CASE WHEN EXISTS(SELECT * FROM ORDER_DATA WHERE user_id=?) THEN 0 ELSE 1 END AS IsEmpty";
}
