package com.globecapital.services.quote;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.cmots.api.corporateInfo_v1.GetCompPeerRatio;
import com.msf.cmots.api.data_v1.CompPeerRatio;
import com.msf.cmots.api.data_v1.CompPeerRatioList;

public class PeerComparisonRatio extends SessionService{

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
		
		String sCoCode = symRow.getCMCoCode();
		String sType = gcRequest.getFromData(DeviceConstants.TYPE);
		
		GetCompPeerRatio compPeerRatioObj = new 
				GetCompPeerRatio(AdvanceQuote.getAppIDForLogging(session.getAppID()));
		
		compPeerRatioObj.setCoCode(sCoCode);
		compPeerRatioObj.setFinFormat(sType);
		compPeerRatioObj.setCount(AppConfig.getValue("peer_comparison_record_count"));
		
		CompPeerRatioList compPeerRatioList = compPeerRatioObj.invoke();
		
		JSONArray finalCompPeerRatio = new JSONArray();
		
		for(CompPeerRatio  compPeerRatio : compPeerRatioList)
		{
			try
			{
			JSONObject obj = new JSONObject();
			SymbolRow compSymRow = AdvanceQuote.getSymbolRowUsingISIN(compPeerRatio.getISIN());
			
			obj.put(DeviceConstants.SCRIP_NAME, compSymRow.getSymbol()); 
			
			QuoteDetails quote = Quote.getLTP(compSymRow.getSymbolToken(), compSymRow.getMappingSymbolUniqDesc());
			obj.put(DeviceConstants.LTP, quote.sLTP.isEmpty() ? "--" :
				PriceFormat.formatPrice(quote.sLTP, compSymRow.getPrecisionInt(), false));
			
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
			catch(Exception e)
			{
				log.warn(e);
			}
		}
		
		gcResponse.addToData(DeviceConstants.RATIOS, finalCompPeerRatio);
		
		
	}

}
