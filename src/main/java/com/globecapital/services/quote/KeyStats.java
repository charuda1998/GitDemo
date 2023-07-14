package com.globecapital.services.quote;

import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.db.RedisPool;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetKeyStats;
import com.msf.cmots.api.data_v1.KeyStatsList;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class KeyStats extends BaseService{
	private static final long serialVersionUID = 1L;
	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).
				getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		
		String sCoCode = symRow.getCMCoCode();
		if(sCoCode.isEmpty())
		{
			gcResponse.setNoDataAvailable();
			return;
		}
		
		int precision = symRow.getPrecisionInt();
		String sType = gcRequest.getFromData(DeviceConstants.TYPE);
		try {
		GetKeyStats keyStatsObj = new GetKeyStats(AdvanceQuote.getAppIDForLogging(gcRequest.getAppID()));
		
		if(symRow.getMktSegId().equalsIgnoreCase("2"))
			keyStatsObj.setExchange("NSE");
		else
			keyStatsObj.setExchange(symRow.getExchange());
		keyStatsObj.setCoCode(sCoCode);
		keyStatsObj.setFinFormat(sType);
		
		
		RedisPool redisPool=new RedisPool();
		KeyStatsList keyStatsList = new KeyStatsList();
		try {
		if(redisPool.isExists(RedisConstants.KEYS_STATS_LIST+"_"+sCoCode+"_"+sType+"_"+symRow.getExchange())) {
			keyStatsList=new Gson().fromJson(redisPool.getValue(RedisConstants.KEYS_STATS_LIST+"_"+sCoCode+"_"+sType+"_"+symRow.getExchange()), KeyStatsList.class);
		}
		else {
			keyStatsList=keyStatsObj.invoke();
			redisPool.setValues(RedisConstants.KEYS_STATS_LIST+"_"+sCoCode+"_"+sType+"_"+symRow.getExchange(), new Gson().toJson(keyStatsList));	
		}
		}catch (Exception e) {
			log.error(e);
			keyStatsList=keyStatsObj.invoke();
		}
		
		
		JSONObject keyStatsfinalObj = new JSONObject();
		
		for(com.msf.cmots.api.data_v1.KeyStats  keystats : keyStatsList)
		{
			keyStatsfinalObj.put(DeviceConstants.MARKET_CAP, PriceFormat.formatPrice(keystats.getMCAP(), 
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.BETA, PriceFormat.formatPrice(keystats.getBeta(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.EPS, PriceFormat.
					formatPrice(keystats.getEPS(), precision, true));
			keyStatsfinalObj.put(DeviceConstants.BOOK_VALUE, PriceFormat.formatPrice(keystats.getBookValue(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.DPS, PriceFormat.formatPrice(keystats.getDPS(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.DIV_YIELD, PriceFormat.formatPrice(keystats.getDivYield(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.PE, PriceFormat.formatPrice(keystats.getPE(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.INDUSTRY_PE, "--");
			keyStatsfinalObj.put(DeviceConstants.MARKET_LOT, symRow.getLotSize());
			keyStatsfinalObj.put(DeviceConstants.RETURNS_1M, PriceFormat.formatPrice(keystats.getR1MRet(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.RETURNS_6M, PriceFormat.formatPrice(keystats.getR6MRet(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.RETURNS_1Y, PriceFormat.formatPrice(keystats.getR1YearRet(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.RETURNS_3Y, PriceFormat.formatPrice(keystats.getR3YearRet(),
					precision, true));
			keyStatsfinalObj.put(DeviceConstants.PRICE_BOOK_VALUE, PriceFormat.
					formatPrice(keystats.getPrice_To_BookValue(), precision, true));
			keyStatsfinalObj.put(DeviceConstants.DEL_PERCENT, 
					PriceFormat.formatPrice(keystats.getDelivery_PerChange(), precision, true));
			
		}
		
		gcResponse.setData(keyStatsfinalObj);
		}
		catch(Exception e) {gcResponse.setNoDataAvailable();}
		
	}

}
