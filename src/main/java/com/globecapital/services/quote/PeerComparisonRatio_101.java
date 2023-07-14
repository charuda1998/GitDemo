package com.globecapital.services.quote;

import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.db.RedisPool;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetCompPeerRatio;
import com.msf.cmots.api.data_v1.CompPeerPerformanceList;
import com.msf.cmots.api.data_v1.CompPeerRatio;
import com.msf.cmots.api.data_v1.CompPeerRatioList;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class PeerComparisonRatio_101 extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).
				getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		RedisPool redisPool=new RedisPool();
		String sCoCode = symRow.getCMCoCode();
		if(sCoCode.isEmpty())
		{
			gcResponse.setNoDataAvailable();
			return;
		}
		
		String sType = gcRequest.getFromData(DeviceConstants.TYPE);
		
		GetCompPeerRatio compPeerRatioObj = new 
				GetCompPeerRatio(AdvanceQuote.getAppIDForLogging(session.getAppID()));
		
		compPeerRatioObj.setCoCode(sCoCode);
		compPeerRatioObj.setFinFormat(sType);
		compPeerRatioObj.setCount(AppConfig.getValue("peer_comparison_record_count"));
		
		CompPeerRatioList compPeerRatioList = new CompPeerRatioList();
		try {
		if(redisPool.isExists(RedisConstants.PEER_COMPARISON_RATIO+"_"+sCoCode+"_"+sType)) {
			compPeerRatioList = new Gson().fromJson(redisPool.getValue(RedisConstants.PEER_COMPARISON_RATIO+"_"+sCoCode+"_"+sType), CompPeerRatioList.class);
		}else {
			compPeerRatioList=compPeerRatioObj.invoke();
			redisPool.setValues(RedisConstants.PEER_COMPARISON_RATIO+"_"+sCoCode+"_"+sType, new Gson().toJson(compPeerRatioList));
		}
		}catch (Exception e) {
			log.error(e);
			compPeerRatioList=compPeerRatioObj.invoke();
		}
		
		JSONArray finalCompPeerRatio = new JSONArray();
		LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
		
		for(CompPeerRatio  compPeerRatio : compPeerRatioList)
		{
			try
			{
			JSONObject obj = new JSONObject();
			SymbolRow compSymRow = AdvanceQuote.getSymbolRowUsingISIN(compPeerRatio.getISIN());
			if(compSymRow != null)
			{
			linkedsetSymbolToken.add(compSymRow.getSymbolToken());
			obj.put(SymbolConstants.SYMBOL_OBJ, 
					compSymRow.getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ));
			
			obj.put(DeviceConstants.PE, PriceFormat.formatPrice(compPeerRatio.getPE(),  symRow.getPrecisionInt(), 
					true));
			obj.put(DeviceConstants.PB, PriceFormat.formatPrice(compPeerRatio.getPB(), symRow.getPrecisionInt(), 
					true));
			obj.put(DeviceConstants.CE, PriceFormat.formatPrice(compPeerRatio.getCE(), symRow.getPrecisionInt(),
					true));
			obj.put(DeviceConstants.DE, PriceFormat.formatPrice(compPeerRatio.getDE(), symRow.getPrecisionInt(),
					true));
			obj.put(DeviceConstants.NET_PROFIT_RATIO, PriceFormat.formatPrice(compPeerRatio.getNetProfitRation_PerChange(),
					symRow.getPrecisionInt(), true));
			
			finalCompPeerRatio.put(obj);
			}
			}
			catch(Exception e)
			{
				log.warn(e);
			}
		}
		
		AdvanceQuote.getLTPPeerComparison(finalCompPeerRatio, linkedsetSymbolToken);
		gcResponse.addToData(DeviceConstants.RATIOS, finalCompPeerRatio);
		
		
	}

}
