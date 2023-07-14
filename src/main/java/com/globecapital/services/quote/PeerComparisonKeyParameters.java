package com.globecapital.services.quote;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
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
import com.msf.cmots.api.corporateInfo_v1.GetCompPeerKeyParameters;
import com.msf.cmots.api.data_v1.CompPeerKeyParameters;
import com.msf.cmots.api.data_v1.CompPeerKeyParametersList;

public class PeerComparisonKeyParameters extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		
		String sCoCode = symRow.getCMCoCode();
		String sType = gcRequest.getFromData(DeviceConstants.TYPE);
		
		GetCompPeerKeyParameters compPeerKeyParamsObj = new 
				GetCompPeerKeyParameters(AdvanceQuote.getAppIDForLogging(session.getAppID()));
		
		compPeerKeyParamsObj.setCoCode(sCoCode);
		compPeerKeyParamsObj.setFinFormat(sType);
		compPeerKeyParamsObj.setCount(AppConfig.getValue("peer_comparison_record_count"));
		
		CompPeerKeyParametersList compPeerKeyParamsList = compPeerKeyParamsObj.invoke();
		
		JSONArray finalCompPeerKeyParams = new JSONArray();
		
		for(CompPeerKeyParameters  compPeerKeyParams : compPeerKeyParamsList)
		{
			try
			{
			JSONObject obj = new JSONObject();
			obj.put(DeviceConstants.SCRIP_NAME, AdvanceQuote.getSymbolRowUsingISIN((compPeerKeyParams.getISIN())).getSymbol()); 
			obj.put(DeviceConstants.MARKET_CAP, PriceFormat.formatPrice(compPeerKeyParams.getMarketCap(), symRow.getPrecisionInt(), true));
			obj.put(DeviceConstants.SALES, PriceFormat.formatPrice(compPeerKeyParams.getSales(), symRow.getPrecisionInt(),true));
			obj.put(DeviceConstants.NET_PROFIT, PriceFormat.formatPrice(compPeerKeyParams.getNetProfit(), symRow.getPrecisionInt(),true));
			finalCompPeerKeyParams.put(obj);
			}
			catch(Exception e)
			{
				log.warn(e);
			}
		}
		gcResponse.addToData(DeviceConstants.KEY_PARAMS, finalCompPeerKeyParams);
	}

}
