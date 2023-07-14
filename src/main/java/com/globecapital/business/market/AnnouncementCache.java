package com.globecapital.business.market;

import com.globecapital.constants.RedisConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.RedisPool;
import com.globecapital.jmx.Monitor;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetAnnouncement;
import com.msf.cmots.api.data_v1.AnnouncementList;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class AnnouncementCache {

	private static Logger log = Logger.getLogger(AnnouncementCache.class);
	private static AnnouncementList announcementListnse = null;
	private static AnnouncementList announcementListbse = null;

	public static void loadNSEAnnouncementCache() throws CMOTSException {
		GetAnnouncement announcementObj = new GetAnnouncement("NSE Announcement ");
		announcementObj.setExchange(ExchangeSegment.NSE);

		try {
			RedisPool redisPool = new RedisPool();
			redisPool.setValues(RedisConstants.COMP_ANNOUNCE_NSE, new Gson().toJson(announcementObj.invoke()));
			log.info("LoadAllCaches: CMOTS API Cache : Announcement cache done for Segment = " + ExchangeSegment.NSE);
		} catch (CMOTSException e) {
			Monitor.markFailure(Market.CMOTS_API_BEAN, "Error while invoking CMOTS data for Announcement Cache : "
					+ ExchangeSegment.NSE + " " + e.getMessage());
			log.error(e);
		} catch (Exception e) {
			log.error(e);
			announcementListnse = announcementObj.invoke();
		}

	}

	public static void loadBSEAnnouncementCache() throws CMOTSException {
		GetAnnouncement announcementObj = new GetAnnouncement("BSE Announcement ");
		announcementObj.setExchange(ExchangeSegment.BSE);

		try {
			RedisPool redisPool = new RedisPool();
			redisPool.setValues(RedisConstants.COMP_ANNOUNCE_BSE, new Gson().toJson(announcementObj.invoke()));
			log.info("LoadAllCaches: CMOTS API Cache : Announcement cache done for Segment = " + ExchangeSegment.BSE);
		} catch (CMOTSException e) {
			Monitor.markFailure(Market.CMOTS_API_BEAN, "Error while invoking CMOTS data for Announcement Cache : "
					+ ExchangeSegment.BSE + " " + e.getMessage());
			log.error(e);
		} catch (Exception e) {
			log.error(e);
			announcementListbse = announcementObj.invoke();
		}

	}

	public static AnnouncementList getNSEAnnouncementList() throws CMOTSException {
		RedisPool redisPool = new RedisPool();
		try {
			if (redisPool.isExists(RedisConstants.COMP_ANNOUNCE_NSE)) {
				return new Gson().fromJson(redisPool.getValue(RedisConstants.COMP_ANNOUNCE_NSE),
						AnnouncementList.class);
			} else {
				loadNSEAnnouncementCache();
				return new Gson().fromJson(redisPool.getValue(RedisConstants.COMP_ANNOUNCE_NSE),
						AnnouncementList.class);
			}

		} catch (JedisConnectionException e) {
			log.error(e);
			return announcementListnse;

		}

	}

	public static AnnouncementList getBSEAnnouncementList() throws CMOTSException {
		RedisPool redisPool = new RedisPool();
		try {
			if (redisPool.isExists(RedisConstants.COMP_ANNOUNCE_NSE)) {
				return new Gson().fromJson(redisPool.getValue(RedisConstants.COMP_ANNOUNCE_BSE),
						AnnouncementList.class);
			}

			else {
				loadBSEAnnouncementCache();
				return new Gson().fromJson(redisPool.getValue(RedisConstants.COMP_ANNOUNCE_BSE),
						AnnouncementList.class);
			}

		} catch (JedisConnectionException e) {
			log.error(e);
			return announcementListbse;
		}
	}

}
