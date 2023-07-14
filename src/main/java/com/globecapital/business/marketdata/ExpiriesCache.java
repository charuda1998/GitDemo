package com.globecapital.business.marketdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONArray;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.GCDBPool;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class ExpiriesCache {

	private static Map<String,Object> expiryCache = new LinkedHashMap<>();
	
	private static Logger log = Logger.getLogger(ExpiriesCache.class);
	
	public static void loadExpiriesCache()  {
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;
		String query = DBQueryConstants.LOAD_EXPIRIES;
		JSONArray NFO_STK_FUT = new JSONArray();
		JSONArray NFO_IDX_FUT = new JSONArray();
		JSONArray NFO_STK_OPT = new JSONArray();
		JSONArray NFO_IDX_OPT = new JSONArray();
		JSONArray NSECDS_FUT_CUR = new JSONArray();
		JSONArray NSECDS_OPT_CUR = new JSONArray();
		JSONArray MCX_FUT_COM = new JSONArray();
		JSONArray MCX_OPT_COM = new JSONArray();
		Map<String,Object> nfoStockFutureMap = new LinkedHashMap<>();
		Map<String,Object> nfoStockOptionMap = new LinkedHashMap<>();
		Map<String,Object> nfoIndexFutureMap = new LinkedHashMap<>();
		Map<String,Object> nfoIndexOptionMap = new LinkedHashMap<>();
		Map<String,Object> nsecdsFutureMap = new LinkedHashMap<>();
		Map<String,Object> nsecdsOptionMap = new LinkedHashMap<>();
		Map<String,Object> mcxFutureMap = new LinkedHashMap<>();
		Map<String,Object> mcxOptionMap = new LinkedHashMap<>();
		
		Map<String,Object> nfoFinalMap = new LinkedHashMap<>();
		Map<String,Object> nsecdsFinalMap = new LinkedHashMap<>();
		Map<String,Object> mcxFinalMap = new LinkedHashMap<>();
		
		NFO_STK_FUT.put(DeviceConstants.ALL_EXPIRIES);
		NFO_IDX_FUT.put(DeviceConstants.ALL_EXPIRIES);
		NFO_STK_OPT.put(DeviceConstants.ALL_EXPIRIES);
		NFO_IDX_OPT.put(DeviceConstants.ALL_EXPIRIES);
		NSECDS_FUT_CUR.put(DeviceConstants.ALL_EXPIRIES);
		NSECDS_OPT_CUR.put(DeviceConstants.ALL_EXPIRIES);
		MCX_FUT_COM.put(DeviceConstants.ALL_EXPIRIES);
		MCX_OPT_COM.put(DeviceConstants.ALL_EXPIRIES);
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				String expiryString = rs.getString(DBConstants.EXPIRY_DATE);
				String instrumentName = rs.getString(DBConstants.INSTRUMENT_NAME);
				SimpleDateFormat sourceFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FORMAT);
		        SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT);
		        String formattedDate = destinationFormat.format(sourceFormat.parse(expiryString));
				if(rs.getString(DBConstants.EXCHANGE_NAME).equals(ExchangeSegment.NFO)) {
					if(instrumentName.equals(DeviceConstants.FUTIDX))
						NFO_IDX_FUT.put(formattedDate);
					else if(instrumentName.equals(DeviceConstants.FUTSTK))
						NFO_STK_FUT.put(formattedDate);
					else if(instrumentName.equals(DeviceConstants.OPTSTK))
						NFO_STK_OPT.put(formattedDate);
					else if(instrumentName.equals(DeviceConstants.OPTIDX))
						NFO_IDX_OPT.put(formattedDate);
				}else if(rs.getString(DBConstants.EXCHANGE_NAME).equals(ExchangeSegment.NSECDS)) {
					if(instrumentName.equals(DeviceConstants.FUTCUR))
						NSECDS_FUT_CUR.put(formattedDate);
					else if(instrumentName.equals(DeviceConstants.OPTCUR))
						NSECDS_OPT_CUR.put(formattedDate);	
				}else if(rs.getString(DBConstants.EXCHANGE_NAME).equals(ExchangeSegment.MCX)) {
					if(instrumentName.equals(DeviceConstants.FUTCOM))
						MCX_FUT_COM.put(formattedDate);
					else if(instrumentName.equals(DeviceConstants.OPTCOM) || instrumentName.equals(DeviceConstants.OPTFUT))
						MCX_OPT_COM.put(formattedDate);	
				}
			}
			nfoStockFutureMap.put(DeviceConstants.EXPIRY_LIST, NFO_STK_FUT);
			nfoStockFutureMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			nfoFinalMap.put(DeviceConstants.STOCK_FUTURE, nfoStockFutureMap);
			
			nfoIndexFutureMap.put(DeviceConstants.EXPIRY_LIST, NFO_IDX_FUT);
			nfoIndexFutureMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			nfoFinalMap.put(DeviceConstants.INDEX_FUTURE, nfoIndexFutureMap);
			
			nfoStockOptionMap.put(DeviceConstants.EXPIRY_LIST, NFO_STK_OPT);
			nfoStockOptionMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			nfoFinalMap.put(DeviceConstants.STOCK_OPTION, nfoStockOptionMap);
			
			nfoIndexOptionMap.put(DeviceConstants.EXPIRY_LIST, NFO_IDX_OPT);
			nfoIndexOptionMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			nfoFinalMap.put(DeviceConstants.INDEX_OPTION, nfoIndexOptionMap);
			
			nsecdsFutureMap.put(DeviceConstants.EXPIRY_LIST, NSECDS_FUT_CUR);
			nsecdsFutureMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			nsecdsFinalMap.put(DeviceConstants.FILTER_FUTURES, nsecdsFutureMap);
			
			nsecdsOptionMap.put(DeviceConstants.EXPIRY_LIST, NSECDS_OPT_CUR);
			nsecdsOptionMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			nsecdsFinalMap.put(DeviceConstants.FILTER_OPTIONS, nsecdsOptionMap);
			
			mcxFutureMap.put(DeviceConstants.EXPIRY_LIST, MCX_FUT_COM);
			mcxFutureMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			mcxFinalMap.put(DeviceConstants.FILTER_FUTURES, mcxFutureMap);
			
			mcxOptionMap.put(DeviceConstants.EXPIRY_LIST, MCX_OPT_COM);
			mcxOptionMap.put(DeviceConstants.OPTED_EXPIRY, DeviceConstants.ALL_EXPIRIES);
			mcxFinalMap.put(DeviceConstants.FILTER_OPTIONS, mcxOptionMap);
			
			expiryCache.put(ExchangeSegment.NFO, nfoFinalMap);
			expiryCache.put(ExchangeSegment.NSECDS, nsecdsFinalMap);
			expiryCache.put(ExchangeSegment.MCX, mcxFinalMap);
			
		}catch(SQLException | ParseException ex) {
			log.error(ex);
		}finally {
			Helper.closeConnection(conn);
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
		}
	}
	
	public static Map<String, Object> getExpiries() {
		return expiryCache;
	}
}
