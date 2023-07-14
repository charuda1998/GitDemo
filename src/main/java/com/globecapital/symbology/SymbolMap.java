package com.globecapital.symbology;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.jmx.Monitor;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class SymbolMap {

	private static Logger log = Logger.getLogger(SymbolMap.class);

	private static Map<String, SymbolRow> tokenSegmentidSymbolMap = new HashMap<String, SymbolRow>();
	private static Map<String, SymbolRow> symbolISINMap = new HashMap<String, SymbolRow>();
	private static Map<String, SymbolRow> symbolUniqueDescMap = new HashMap<String, SymbolRow>();
	private static Map<String, String> symbolNFOExistsMap = new HashMap<String, String>();
	private static Map<String, SymbolRow> mappingSymbolUniqDesc = new HashMap<String, SymbolRow>();

//	public SymbolMap() {
//
//	}

	public static void loadSymbols() {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;

		String query = DBQueryConstants.GET_SYMBOLS;

		try {

			
			conn = GCDBPool.getInstance().getConnection();

			log.debug("connection established");

			ps = conn.prepareStatement(query);
			res = ps.executeQuery();
			
			if (!res.isBeforeFirst()) {
				log.debug("No Symbols Found");
			    log.info("No Symbols Found");
			}
			
			while (res.next()) {

				SymbolRow row = new SymbolRow(res);
				
				SymbolMap.tokenSegmentidSymbolMap.put(res.getString(DBConstants.TOKEN_SEGMENT), row);
				//For filtering out records which have empty ISIN and are of the form _1 and _3
				if(Objects.nonNull(res.getString(DBConstants.ISINSEGMENT)) && res.getString(DBConstants.ISINSEGMENT).length() > 2)
					SymbolMap.symbolISINMap.put(res.getString(DBConstants.ISINSEGMENT), row);
				if (!((res.getString(DBConstants.SYMBOL_UNIQ_DESC)) == null)) {
					SymbolMap.symbolUniqueDescMap.put(res.getString(DBConstants.SYMBOL_UNIQ_DESC), row);
				}
				if((res.getString(DBConstants.IS_FNO_EXISTS).equals("true")))
					SymbolMap.symbolNFOExistsMap.put(res.getString(DBConstants.SYMBOL_NAME), res.getString(DBConstants.IS_FNO_EXISTS));
				if((res.getString(DBConstants.MAPPING_SYMBOL_UNIQ_DESC)!=null))
					SymbolMap.mappingSymbolUniqDesc.put(res.getString(DBConstants.MAPPING_SYMBOL_UNIQ_DESC), row);
			}
			
			log.debug("LoadAllCaches: Symbol :contents-symbolmap " + tokenSegmentidSymbolMap.size());
			log.debug("LoadAllCaches: Symbol :contents-symbolISINmap " + symbolISINMap.size());
			log.debug("LoadAllCaches: Symbol :contents-symbolUniqDescmap " + symbolUniqueDescMap.size());
			log.info("LoadAllCaches: Symbol :contents-symbolmap " + tokenSegmentidSymbolMap.size());
			log.info("LoadAllCaches: Symbol :contents-symbolISINmap " + symbolISINMap.size());
			log.info("LoadAllCaches: Symbol :contents-symbolUniqDescmap " + symbolUniqueDescMap.size());
			log.info("LoadAllCaches: Symbol :contents-mappingSymbolUniqDesc " + mappingSymbolUniqDesc.size());
			log.info("LoadAllCaches: Symbol  Total Scrips : " + tokenSegmentidSymbolMap.size() + " Scrips By isin : " + symbolISINMap.size()+ "  Scrips by symbolUniqDes : "+ symbolUniqueDescMap.size()+ "Scrips by mappingSymbolUniqDesc : "+mappingSymbolUniqDesc.size());

		} catch (Exception e) {
		
			log.info("LoadAllCaches: Symbol : load symbol Cache Exception "+e.getMessage());
			log.error(e);

		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
	}

	public static SymbolRow getSymbolRow(String tokensegId) {

		SymbolRow symbol = new SymbolRow();

		symbol = SymbolMap.tokenSegmentidSymbolMap.get(tokensegId);

		if (symbol == null) {

			log.warn("Not able to find symbol from symbolMap > " + tokensegId);
			return symbol;

		}

		return symbol;
	}

	public static SymbolRow getSymbolRow(String token, String segId) {

		String key = token + "_" + segId;
		SymbolRow symbol = new SymbolRow();
		symbol = SymbolMap.tokenSegmentidSymbolMap.get(key);
		if (symbol == null) {

			log.warn("Not able to find symbol from symbolMap > " + key);

			return symbol;

		}

		return symbol;
	}

	public static SymbolRow getISINSymbolRow(String key) {

		SymbolRow symbol = new SymbolRow();

		symbol = SymbolMap.symbolISINMap.get(key);

		if (symbol == null) {

			log.warn("Not able to find symbol from symbolMap > " + key);
			return symbol;

		}

		return symbol;

	}

	public static SymbolRow getSymbolUniqDescRow(String key) {

		SymbolRow symbolRow = new SymbolRow();

		symbolRow = SymbolMap.symbolUniqueDescMap.get(key);

		if (symbolRow == null) {

			log.warn("Not able to find symbol from symbolMap > " + key);
			return symbolRow;

		}

		return symbolRow;

	}
	
	public static SymbolRow getMappingSymbolUniqDescRow(String key) {

		SymbolRow symbolRow = new SymbolRow();

		symbolRow = SymbolMap.mappingSymbolUniqDesc.get(key);

		if (symbolRow == null) {

			log.warn("Not able to find symbol from symbolMap > " + key);
			return symbolRow;

		}

		return symbolRow;

	}

	public static boolean isValidMappingSymbolUniqDescRow(String symbol) {

		return mappingSymbolUniqDesc.containsKey(symbol);
	}
	
	public static boolean isValidSymbol(String symbol) {

		return symbolISINMap.containsKey(symbol);
	}

	public static boolean isValidSymbolTokenSegmentMap(String symbol) {

		return tokenSegmentidSymbolMap.containsKey(symbol);
	}

	public static boolean isValidSymbolUniqDescMap(String symbol) {

		return symbolUniqueDescMap.containsKey(symbol);
	}
	
	public static boolean isNFOExists(String symbol) {

		return symbolNFOExistsMap.containsKey(symbol);
	}

}
