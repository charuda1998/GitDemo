package com.globecapital.business.market;

import java.util.HashMap;
import java.util.Map;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.RedisPool;
import com.globecapital.jmx.Monitor;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetResultsInfo;
import com.msf.cmots.api.data_v1.AnnouncementList;
import com.msf.cmots.api.data_v1.CorporateActionsList;
import com.msf.cmots.api.data_v1.ResultsInfoList;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class ResultsInfoCache {

//	private static Map<String,ResultsInfoList> resultInfoMap = new HashMap<>();
	
	private static Logger log = Logger.getLogger(ResultsInfoCache.class);
	private static ResultsInfoList resultsInfoList=null;
	
	public static void loadResultsInfoCache(String type, String sAppID, String exchange) throws CMOTSException  {
		GetResultsInfo resultsInfoObj = new GetResultsInfo(AdvanceQuote.getAppIDForLogging(sAppID));

		if(type.equalsIgnoreCase(DeviceConstants.THIS_WEEK))
			type = DeviceConstants.PERIOD_WEEK;
		resultsInfoObj.setType(type);

		try {
			ResultsInfoList resultsList = resultsInfoObj.invoke();
			if(resultsList!=null) {
//				resultInfoMap.put(type+"_"+exchange, resultsList);
				RedisPool redisPool = new RedisPool();
				String key="ResultsInfoCache_"+type+"_"+exchange;
				redisPool.setValues(key, new Gson().toJson(resultsList));
				log.info("LoadAllCaches: ResultInfo: Response cached for ResultsInfo with Period = "+type+ " and Exchange = "+exchange);
			}
		}catch(CMOTSException ex) {
			Monitor.markFailure(Market.CMOTS_API_BEAN, "Error while invoking CMOTS data for Results Cache : "+"with Period = "+type+" and Exchange = "+exchange+" " +ex.getMessage());
			log.error(ex);
		}catch (Exception e) {
			log.error(e);
			resultsInfoList=resultsInfoObj.invoke();
		}
	}
	
	public static ResultsInfoList getResultInfoList(String sPeriod,String sAppID ,String exchange) throws CMOTSException {
		if(sPeriod.equalsIgnoreCase(DeviceConstants.THIS_WEEK))
			sPeriod = DeviceConstants.PERIOD_WEEK;

		String key="ResultsInfoCache_"+sPeriod+"_"+exchange;
		RedisPool redisPool = new RedisPool();
		try {
		if(redisPool.isExists(key)) {
			return new Gson().fromJson(redisPool.getValue(key), ResultsInfoList.class);
		}
		else {
			loadResultsInfoCache(sPeriod,sAppID,exchange);
			return new Gson().fromJson(redisPool.getValue(key), ResultsInfoList.class);
		}
		}catch (JedisConnectionException e) {
			log.error(e);
			return resultsInfoList;
		}
	}
}
