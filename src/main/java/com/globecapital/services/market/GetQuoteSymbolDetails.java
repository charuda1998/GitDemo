package com.globecapital.services.market;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.order.OrderDetails;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class GetQuoteSymbolDetails extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		JSONObject userInfoObj = session.getUserInfo();
		
		JSONArray prodList = userInfoObj.getJSONArray(UserInfoConstants.PRODUCT_TYPE);
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		
		JSONObject symbol = OrderDetails.getOrderPadDetails( sSymbolToken, prodList, false, session);
		gcResponse.setData(symbol);
		
	}

}
