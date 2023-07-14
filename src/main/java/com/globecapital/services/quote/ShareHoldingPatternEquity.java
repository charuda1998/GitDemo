package com.globecapital.services.quote;

import org.json.JSONArray;
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
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetShareHoldingPatternEquity;
import com.msf.cmots.api.data_v1.ShareHoldingEquity;
import com.msf.cmots.api.data_v1.ShareHoldingEquityList;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class ShareHoldingPatternEquity extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws StringIndexOutOfBoundsException,Exception {
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		RedisPool redisPool=new RedisPool();
		String sCoCode = symRow.getCMCoCode();
		int index = sCoCode.indexOf(".");
		sCoCode = sCoCode.substring(0, index);
		
		GetShareHoldingPatternEquity shareHoldingObj = new 
				GetShareHoldingPatternEquity(AdvanceQuote.getAppIDForLogging(gcRequest.getAppID()));
		
		shareHoldingObj.setCoCode(sCoCode);
		try {
			ShareHoldingEquityList shareHoldingList = new ShareHoldingEquityList();
			try {
			    if(redisPool.isExists(RedisConstants.SHARE_HOLDINGS_EQUITY_LIST+"_"+sCoCode)) {
			        shareHoldingList = new Gson().fromJson(redisPool.getValue(RedisConstants.SHARE_HOLDINGS_EQUITY_LIST+"_"+sCoCode), ShareHoldingEquityList.class);
    			}else {
    				shareHoldingList=shareHoldingObj.invoke();
    				redisPool.setValues(RedisConstants.SHARE_HOLDINGS_EQUITY_LIST+"_"+sCoCode, new Gson().toJson(shareHoldingList));
    			}
			}catch (Exception e) {
				log.error(e);
				shareHoldingList=shareHoldingObj.invoke();
			}
			JSONArray periods = new JSONArray();
			JSONArray table = new JSONArray();
			JSONObject diagram = new JSONObject();
			JSONArray promoters = new JSONArray();
			JSONArray pledged = new JSONArray();
			JSONArray fii = new JSONArray();
			JSONArray totalDii = new JSONArray();
			JSONArray finInsts = new JSONArray();
			JSONArray insuranceCo = new JSONArray();
			JSONArray mf = new JSONArray();
			JSONArray otherDii = new JSONArray();
			JSONArray others = new JSONArray();
			JSONObject finalShareHolding = new JSONObject();
		
			for(ShareHoldingEquity  shareHolding : shareHoldingList)
			{
				JSONObject diagramObj = new JSONObject();
			
				String period = DateUtils.formatDate(shareHolding.getYRC(), DeviceConstants.SHARE_HOLDING_FROM_DATE, DeviceConstants.SHARE_HOLDING_TO_DATE);
				periods.put(period);
			
				promoters.put(PriceFormat.formatPrice(shareHolding.getPromotersPer(), symRow.getPrecisionInt(), true));
				pledged.put(PriceFormat.formatPrice(shareHolding.getPledgedPer(), symRow.getPrecisionInt(),true));
				fii.put(PriceFormat.formatPrice(shareHolding.getFIIPer(), symRow.getPrecisionInt(),true));
				totalDii.put(PriceFormat.formatPrice(shareHolding.getDiiMfPer(), symRow.getPrecisionInt(),true));
				finInsts.put(PriceFormat.formatPrice(shareHolding.getFinInstBankPer(), symRow.getPrecisionInt(), true));
				insuranceCo.put(PriceFormat.formatPrice(shareHolding.getInsurancePer(), symRow.getPrecisionInt(),true));
				mf.put(PriceFormat.formatPrice(shareHolding.getMfPer(), symRow.getPrecisionInt(),true));
				otherDii.put(PriceFormat.formatPrice(shareHolding.getOtherDIIPer(), symRow.getPrecisionInt(),true));
				others.put(PriceFormat.formatPrice(shareHolding.getOthers_Per(), symRow.getPrecisionInt(),true));

				diagramObj.put(DeviceConstants.PROMOTERS, PriceFormat.formatPrice(shareHolding.getPromotersPer(), symRow.getPrecisionInt(), true));
				diagramObj.put(DeviceConstants.FII, PriceFormat.formatPrice(shareHolding.getFIIPer(), symRow.getPrecisionInt(),true));
				diagramObj.put(DeviceConstants.TOTAL_DII, PriceFormat.formatPrice(shareHolding.getDiiMfPer(), symRow.getPrecisionInt(),true));
				diagramObj.put(DeviceConstants.OTHERS, PriceFormat.formatPrice(shareHolding.getOthers_Per(), symRow.getPrecisionInt(),true));
			
				diagram.put(period, diagramObj);
			}

			table.put(setValues(DeviceConstants.CAP_PROMOTERS, promoters, "true"));
			table.put(setValues(DeviceConstants.CAP_PLEDGED, pledged, "false"));
			table.put(setValues(DeviceConstants.CAP_FII, fii, "true"));
			table.put(setValues(DeviceConstants.CAP_TOTAL_DII, totalDii, "true"));
			table.put(setValues(DeviceConstants.FIN_INSTS, finInsts, "false"));
			table.put(setValues(DeviceConstants.INSURANCE_CO, insuranceCo, "false"));
			table.put(setValues(DeviceConstants.MF, mf, "false"));
			table.put(setValues(DeviceConstants.OTHER_DII, otherDii, "false"));
			table.put(setValues(DeviceConstants.CAP_OTHERS, others, "true"));
		
			finalShareHolding.put(DeviceConstants.PERIOD,periods);
			finalShareHolding.put(DeviceConstants.TABLE, table);
			finalShareHolding.put(DeviceConstants.DIAGRAM,diagram);
			gcResponse.addToData(DeviceConstants.SHARE_HOLDING_EQUITY, finalShareHolding);
		}
		catch(Exception e) {
			gcResponse.setNoDataAvailable();
		}
		
	}
	
	protected JSONObject setValues(String keys, JSONArray values, String highlight) {
		JSONObject obj = new JSONObject();
		obj.put(DeviceConstants.KEY, keys);
		obj.put(DeviceConstants.VALUE, values);
		obj.put(DeviceConstants.HIGHLIGHT, highlight);
		return obj;
	}

}
