package com.globecapital.business.market;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.db.RedisPool;
import com.globecapital.jmx.Monitor;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetCorporateActions;
import com.msf.cmots.api.data_v1.AnnouncementList;
import com.msf.cmots.api.data_v1.CorporateActionsList;
import com.msf.cmots.api.data_v1.PerformanceRatioBankList;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class CorporateActionsCache {

	private static Logger log = Logger.getLogger(CorporateActionsCache.class);
	private static CorporateActionsList corporateActionsListThisWeek = null;
	private static CorporateActionsList corporateActionsListNextWeek = null;
	private static CorporateActionsList corporateActionsListToday = null;

	public static void loadCorporateActionCacheNextWeek() throws CMOTSException {
		// Monitor.setJobsBeans(Monitor.CorporateActionCacheNextWeek);
		GetCorporateActions corpActionsObj = new GetCorporateActions("Consolidated Corporate Actions 15Days");

		corpActionsObj.setCoCode("-");
		corpActionsObj.setPeriod(Market.corpActionFormatToAPI(DeviceConstants.NEXT_WEEK));

		try {
			RedisPool redisPool = new RedisPool();
			redisPool.setValues(RedisConstants.CORPORATE_NEXT_WEEK, new Gson().toJson(corpActionsObj.invoke()));
			log.info("LoadAllCaches: Corporate Action: Corporate Action cached for period = "
					+ DeviceConstants.NEXT_WEEK);
		} catch (CMOTSException ex) {
			log.error(ex);
			Monitor.markFailure(Market.CMOTS_API_BEAN, "Error while invoking CMOTS data for Corporate Cache : "
					+ DeviceConstants.NEXT_WEEK + " " + ex.getMessage());
		} catch (Exception e) {
			log.error(e);
			corporateActionsListNextWeek = corpActionsObj.invoke();
		}
	}

	public static void loadCorporateActionCacheThisWeek() throws CMOTSException {
		GetCorporateActions corpActionsObj = new GetCorporateActions("Consolidated Corporate ActionsWeek");

		corpActionsObj.setCoCode("-");
		corpActionsObj.setPeriod(Market.corpActionFormatToAPI(DeviceConstants.THIS_WEEK));

		try {
			RedisPool redisPool = new RedisPool();
			redisPool.setValues(RedisConstants.CORPORATE_THIS_WEEK, new Gson().toJson(corpActionsObj.invoke()));
			;
			log.info("LoadAllCaches: Corporate Action: Corporate Action cached for period = "
					+ DeviceConstants.THIS_WEEK);
		} catch (CMOTSException ex) {
			Monitor.markFailure(Market.CMOTS_API_BEAN, "Error while invoking CMOTS data for Corporate Cache : "
					+ DeviceConstants.THIS_WEEK + " " + ex.getMessage());
			log.error(ex);
		} catch (Exception e) {
			log.error(e);
			corporateActionsListThisWeek = corpActionsObj.invoke();
		}
	}

	public static void loadCorporateActionCacheToday() throws CMOTSException {
		GetCorporateActions corpActionsObj = new GetCorporateActions("Consolidated Corporate ActionsToday");

		corpActionsObj.setCoCode("-");
		corpActionsObj.setPeriod(Market.corpActionFormatToAPI(DeviceConstants.TODAY));

		try {
			RedisPool redisPool = new RedisPool();
			redisPool.setValues(RedisConstants.CORPORATE_TODAY, new Gson().toJson(corpActionsObj.invoke()));
			log.info("LoadAllCaches: Corporate Action: Corporate Action cached for period = " + DeviceConstants.TODAY);
		} catch (CMOTSException ex) {
			Monitor.markFailure(Market.CMOTS_API_BEAN, "Error while invoking CMOTS data for Corporate Cache : "
					+ DeviceConstants.TODAY + " " + ex.getMessage());
			log.error(ex);
		} catch (Exception e) {
			log.error(e);
			corporateActionsListToday = corpActionsObj.invoke();
		}
	}

	public static CorporateActionsList getCorporateActionListNextWeek() throws CMOTSException {
		RedisPool redisPool = new RedisPool();
		try {
			if (redisPool.isExists(RedisConstants.CORPORATE_NEXT_WEEK)) {
				return new Gson().fromJson(redisPool.getValue(RedisConstants.CORPORATE_NEXT_WEEK),
						CorporateActionsList.class);
			} else {
				loadCorporateActionCacheNextWeek();
				return new Gson().fromJson(redisPool.getValue(RedisConstants.CORPORATE_NEXT_WEEK),
						CorporateActionsList.class);
			}
		} catch (JedisConnectionException e) {
			log.error(e);
			return corporateActionsListNextWeek;
		}

	}

	public static CorporateActionsList getCorporateActionListThisWeek() throws CMOTSException {
		RedisPool redisPool = new RedisPool();
		try {
			if (redisPool.isExists(RedisConstants.CORPORATE_THIS_WEEK)) {
				return new Gson().fromJson(redisPool.getValue(RedisConstants.CORPORATE_THIS_WEEK),
						CorporateActionsList.class);
			} else {
				loadCorporateActionCacheNextWeek();
				return new Gson().fromJson(redisPool.getValue(RedisConstants.CORPORATE_THIS_WEEK),
						CorporateActionsList.class);
			}
		} catch (JedisConnectionException e) {
			log.error(e);
			return corporateActionsListThisWeek;
		}
	}

	public static CorporateActionsList getCorporateActionListToday() throws CMOTSException {
		RedisPool redisPool = new RedisPool();
		try {
			if (redisPool.isExists(RedisConstants.CORPORATE_TODAY)) {
				return new Gson().fromJson(redisPool.getValue(RedisConstants.CORPORATE_TODAY),
						CorporateActionsList.class);
			} else {
				loadCorporateActionCacheNextWeek();
				return new Gson().fromJson(redisPool.getValue(RedisConstants.CORPORATE_TODAY),
						CorporateActionsList.class);
			}
		} catch (JedisConnectionException e) {
			log.error(e);
			return corporateActionsListToday;
		}
	}

}
