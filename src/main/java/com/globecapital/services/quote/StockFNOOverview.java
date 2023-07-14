package com.globecapital.services.quote;

import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;


public class StockFNOOverview extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).
				getString(SymbolConstants.SYMBOL_TOKEN);

		JSONObject fnoOverviewObj = new JSONObject();
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		String sMarketSegID = symRow.getMktSegId();
		String sAppID = session.getAppID();
		
		if(ExchangeSegment.isEquitySegment(sMarketSegID) 
				|| sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
		{
			fnoOverviewObj = AdvanceQuote.getEquityNFOOverView(symRow, sAppID);
		}
		else if(sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
		{
			fnoOverviewObj = AdvanceQuote.getMCXFNOOverview(symRow, sAppID);
		}
		else if(ExchangeSegment.isCurrencySegment(sMarketSegID))
		{
			fnoOverviewObj = AdvanceQuote.getCurrencyFNOOverview(symRow, sAppID);
		}
		
		if(fnoOverviewObj.length() != 0)
			gcResponse.setData(fnoOverviewObj);
		else {
			gcResponse.setInfoID(InfoIDConstants.NO_DATA);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.data_unavailable"));
		}
		
	}

}
