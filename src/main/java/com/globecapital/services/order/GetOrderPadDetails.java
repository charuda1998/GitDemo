package com.globecapital.services.order;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.order.AMODetails;
import com.globecapital.business.order.OrderDetails;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;


public class GetOrderPadDetails extends SessionService{

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

//		JSONObject symbol = OrderDetails.getOrderPadDetails( sSymbolToken, prodList, true);
//			gcResponse.setData(symbol);

		if(gcRequest.getOptFromData(OrderConstants.FOR_MODIFY_ORDER, "").equals("true")
				|| gcRequest.getOptFromData(OrderConstants.FOR_SQUARE_OFF_ORDER, "").equals("true"))
		{
			boolean isSquareOff = Boolean.valueOf(gcRequest.getOptFromData(OrderConstants.FOR_SQUARE_OFF_ORDER, "").equals("true"));
			JSONObject orderInfoObj = gcRequest.getObjectFromData(OrderConstants.ORDER_INFO_OBJ);
			JSONObject orderDetails = OrderDetails.getOrderPadDetailsForModifyOrder(sSymbolToken, orderInfoObj, isSquareOff);
			if(gcRequest.getOptFromData(OrderConstants.FOR_SQUARE_OFF_ORDER, "").equals("true"))
			{
				JSONObject symbolObj = SymbolMap.getSymbolRow(sSymbolToken).getJSONObject(SymbolConstants.SYMBOL_OBJ);
				String sMarketSegID = symbolObj.getString(SymbolConstants.MKT_SEG_ID);
				String sExch = ExchangeSegment.getExchangeName(sMarketSegID);
				orderDetails = OrderDetails.getOrderPadDetails( sSymbolToken, prodList, true, session);
				orderDetails.remove(OrderConstants.IS_AMO);
				orderDetails.put(OrderConstants.IS_AMO, Boolean.toString(AMODetails.isAMOOrder(sExch)));
			}
			gcResponse.setData(orderDetails);
		}
		else
		{
			JSONObject orderDetails = OrderDetails.getOrderPadDetails( sSymbolToken, prodList, true, session);
			gcResponse.setData(orderDetails);
		}	
	}

}
