package com.globecapital.services.order;

import org.json.JSONObject;

import com.globecapital.business.order.AdvanceOrderDetails;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;


public class GetCoverOrderRange extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		
		Session session = gcRequest.getSession();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		String sProductType = gcRequest.getFromData(OrderConstants.PRODUCT_TYPE);
		String sOrderAction = gcRequest.getFromData(OrderConstants.ORDER_ACTION);
		JSONObject coverOrderDetails = gcRequest.getObjectFromData(OrderConstants.COVER_ORDER_DETAILS);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		JSONObject advanceOrderDetailsObj = new JSONObject();
		String basePrice = coverOrderDetails.getString(OrderConstants.PRICE);
		if(sProductType.equals(ProductType.COVER_ORDER))
		{
		    try {
                advanceOrderDetailsObj = AdvanceOrderDetails.getBracketOrderDetails(session, sSymbolToken, sOrderAction, coverOrderDetails
                    ,getServletContext(), gcRequest, gcResponse);
            }catch (GCException e) {
                throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getValue("info_msg.invalid.request_failed"));
            }
			advanceOrderDetailsObj.remove(OrderConstants.PROFIT_PRICE);
			if( basePrice.equals("0") || basePrice.isEmpty())
			    advanceOrderDetailsObj.put(DeviceConstants.BASE_PRICE, quoteDetails.sLTP);
			else
			    advanceOrderDetailsObj.put(DeviceConstants.BASE_PRICE, coverOrderDetails.getString(OrderConstants.PRICE));
			gcResponse.setData(advanceOrderDetailsObj);
			gcResponse.addToData(OrderConstants.PRODUCT_TYPE, sProductType);
		}
		else 
		{
			throw new RequestFailedException();
		}

	}

}
