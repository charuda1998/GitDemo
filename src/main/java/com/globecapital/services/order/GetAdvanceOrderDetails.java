package com.globecapital.services.order;

import org.json.JSONObject;

import com.globecapital.business.order.AdvanceOrderDetails;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;


public class GetAdvanceOrderDetails extends SessionService{

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
		JSONObject brackerOrderDetails = gcRequest.getObjectFromData(OrderConstants.BRACKET_ORDER_DETAILS);
		
		JSONObject advanceOrderDetailsObj = new JSONObject();
		
		if(sProductType.equals(ProductType.BRACKET_ORDER))
		{
			advanceOrderDetailsObj = AdvanceOrderDetails.getBracketOrderDetails(session, sSymbolToken, sOrderAction, brackerOrderDetails
			        ,getServletContext(), gcRequest, gcResponse);
			gcResponse.setData(advanceOrderDetailsObj);
			gcResponse.addToData(OrderConstants.PRODUCT_TYPE, sProductType);
		}
		
		//TODO: Cover order is not allowed for this sprint 
		else if(sProductType.equals(ProductType.COVER_ORDER))
		{
			throw new RequestFailedException();
		}

	}

}
