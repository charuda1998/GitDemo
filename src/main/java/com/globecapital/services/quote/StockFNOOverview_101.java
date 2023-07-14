package com.globecapital.services.quote;

import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.msf.log.Logger;



public class StockFNOOverview_101 extends BaseService{
	private static Logger log = Logger.getLogger(StockFNOOverview_101.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		JSONObject fnoOverviewObj = new JSONObject();
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		String sMarketSegID = symRow.getMktSegId();
		String sAppID = gcRequest.getAppID();
		
		if(ExchangeSegment.isEquitySegment(sMarketSegID) || sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
		{
			fnoOverviewObj = AdvanceQuote.getNFOFNOOverView_101(symRow,sSymbolToken, sAppID);
		}
		else if(sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
		{
			fnoOverviewObj = AdvanceQuote.getMCXFNOOverview_101(symRow,sSymbolToken, sAppID);
		}
		else if(ExchangeSegment.isCurrencySegment(sMarketSegID))
		{
			fnoOverviewObj = AdvanceQuote.getCurrencyFNOOverview_101(symRow,sSymbolToken, sAppID);
		}
		
		if(fnoOverviewObj.length() != 0)
			gcResponse.setData(fnoOverviewObj);
		else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.data_unavailable"));
		}
		
	}

}
