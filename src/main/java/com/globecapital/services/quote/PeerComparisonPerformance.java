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
import com.msf.cmots.api.corporateInfo_v1.GetCompPeerPerfomance;
import com.msf.cmots.api.data_v1.CompPeerPerformance;
import com.msf.cmots.api.data_v1.CompPeerPerformanceList;

public class PeerComparisonPerformance extends SessionService{

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		
		String coCode = symRow.getCMCoCode();
		String exch = symRow.getExchange();
		
		GetCompPeerPerfomance compPeerPerformanceObj = new 
				GetCompPeerPerfomance(AdvanceQuote.getAppIDForLogging(session.getAppID()));
		
		compPeerPerformanceObj.setExchange(exch);
		compPeerPerformanceObj.setCoCode(coCode);
		compPeerPerformanceObj.setCount(AppConfig.getValue("peer_comparison_record_count"));
		
		CompPeerPerformanceList compPeerPerformanceList = compPeerPerformanceObj.invoke();
		
		JSONArray finalCompPeerPerformance = new JSONArray();
		
		for(CompPeerPerformance compPeerPerformance : compPeerPerformanceList)
		{
			try
			{
			JSONObject obj = new JSONObject();
			SymbolRow compSymRow = AdvanceQuote.getSymbolRowUsingISIN(compPeerPerformance.getISIN());
			
			obj.put(DeviceConstants.SCRIP_NAME, compSymRow.getSymbol()); 
			
			QuoteDetails quote = Quote.getLTP(compSymRow.getSymbolToken(), compSymRow.getMappingSymbolUniqDesc());
			obj.put(DeviceConstants.LTP, quote.sLTP.isEmpty() ? "--" :
				PriceFormat.formatPrice(quote.sLTP, compSymRow.getPrecisionInt(), false));
			obj.put(DeviceConstants.RETURNS_1M, compPeerPerformance.getR1MRet() == null ? "--" : 
				PriceFormat.formatPrice(compPeerPerformance.getR1MRet(), symRow.getPrecisionInt(),true));
			obj.put(DeviceConstants.RETURNS_3M, compPeerPerformance.getR3MRet() == null ? "--" : 
				PriceFormat.formatPrice(compPeerPerformance.getR3MRet(), symRow.getPrecisionInt(),true));
			obj.put(DeviceConstants.RETURNS_6M, compPeerPerformance.getR6MRet() == null ? "--" : 
				PriceFormat.formatPrice(compPeerPerformance.getR6MRet(), symRow.getPrecisionInt(),true));
			obj.put(DeviceConstants.RETURNS_1Y, compPeerPerformance.getR1YearRet() == null ? "--" : 
				PriceFormat.formatPrice(compPeerPerformance.getR1YearRet(), symRow.getPrecisionInt(),true));
			obj.put(DeviceConstants.RETURNS_2Y, compPeerPerformance.getR2YearRet() == null ? "--" : 
				PriceFormat.formatPrice(compPeerPerformance.getR2YearRet(), symRow.getPrecisionInt(),true));
			obj.put(DeviceConstants.RETURNS_5Y, compPeerPerformance.getR5YearRet() == null ? "--" : 
				PriceFormat.formatPrice(compPeerPerformance.getR5YearRet(), symRow.getPrecisionInt(),true));
			finalCompPeerPerformance.put(obj);
			
			}
			catch(Exception e)
			{
				log.warn(e);
			}
			
		}
		gcResponse.addToData(DeviceConstants.PERFORMANCES, finalCompPeerPerformance);
	}
	
}
