package com.globecapital.business.market;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.RedisPool;
import com.globecapital.jmx.Monitor;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetFIIDII;
import com.msf.cmots.api.data_v1.FIIDIIList;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;
import redis.clients.jedis.exceptions.*;

public class FIIDIICache {

//	private static Map<String,FIIDIIList> fiiDiiListMap = new HashMap<>();

	private static Logger log = Logger.getLogger(FIIDIICache.class);
	private static FIIDIIList fiidiiList = null;
	private static Map<String,FIIDIIList> fiiDiiListMap = new HashMap<>();

	public static void loadFIIDIICache(String sPeriod, String sCategory, String sAppID)
			throws ParseException, CMOTSException {
		GetFIIDII fiiDiiObj = new GetFIIDII(AdvanceQuote.getAppIDForLogging(sAppID));
		fiiDiiObj.setType(fiiDiiPeriodFormatToAPI(sPeriod));
		fiiDiiObj.setCategory(fiiDiiCategoryFormatToAPI(sCategory));
		try {
			RedisPool redisPool = new RedisPool();
			fiidiiList = fiiDiiObj.invoke();
			log.info("fiidii API response :" + fiidiiList);
			if (fiidiiList != null) {
				
				String key = "FIIDIICache_" + fiiDiiPeriodFormatToAPI(sPeriod) + "_" + sCategory;
				redisPool.setValues(key, new Gson().toJson(fiidiiList));
				log.info("LoadAllCaches: FIDICache : Response cached for FIIDII with Period = " + sPeriod
						+ " and Category = " + sCategory);
			}
		} catch (CMOTSException ex) {
			Monitor.markFailure(Market.CMOTS_API_BEAN, "Error while invoking CMOTS data for FIIDII Cache : "
					+ "with Period = " + sPeriod + " and Category = " + sCategory + " " + ex.getMessage());
			log.error(ex);
		} catch (Exception e) {
			log.error(e);
			fiidiiList = fiiDiiObj.invoke();
			fiiDiiListMap.put(fiiDiiPeriodFormatToAPI(sPeriod)+"_"+sCategory, fiidiiList);
		}
	}

	private static String fiiDiiPeriodFormatToAPI(String sPeriod) {
		if (sPeriod.equals(DeviceConstants.MONTHLY))
			return DeviceConstants.CMOTS_M;
		else if (sPeriod.equals(DeviceConstants.YEARLY))
			return DeviceConstants.CMOTS_Y;
		else
			return DeviceConstants.CMOTS_D;
	}

	private static String fiiDiiCategoryFormatToAPI(String sCategory) {
		if (sCategory.equals(DeviceConstants.FII_CASH))
			return DeviceConstants.CMOTS_FII_CASH;
		else if (sCategory.equals(DeviceConstants.DII_CASH))
			return DeviceConstants.CMOTS_DII_CASH;
		else if (sCategory.equals(DeviceConstants.FII_FUTURE))
			return DeviceConstants.CMOTS_FII_FUT;
		else if (sCategory.equals(DeviceConstants.FII_OPTION))
			return DeviceConstants.CMOTS_FII_OPT;
		else
			return DeviceConstants.ALL;
	}

	public static FIIDIIList getFIIDIIList(String sPeriod, String sCategory, String sAppID)
			throws ParseException, CMOTSException {
		RedisPool redisPool = new RedisPool();
		String key = "FIIDIICache_" + fiiDiiPeriodFormatToAPI(sPeriod) + "_" + sCategory;
		try {
			if (redisPool.isExists(key)) {
				return new Gson().fromJson(redisPool.getValue(key), FIIDIIList.class);
			} else {
				loadFIIDIICache(sPeriod, sCategory, sAppID);
				return new Gson().fromJson(redisPool.getValue(key), FIIDIIList.class);
			}
		} catch (JedisConnectionException e) {
			log.error(e);
			if(fiiDiiListMap.containsKey(fiiDiiPeriodFormatToAPI(sPeriod)+"_"+sCategory)) {
				return fiiDiiListMap.get(fiiDiiPeriodFormatToAPI(sPeriod)+"_"+sCategory);
			}
			else
				return new FIIDIIList();

		}
	}

}
