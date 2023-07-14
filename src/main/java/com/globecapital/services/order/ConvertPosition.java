package com.globecapital.services.order;

import javax.servlet.ServletContext;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.business.edis.InsertEDISReqResponseDetails;
import com.globecapital.business.order.PositionConversion;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;

public class ConvertPosition extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		JSONObject bracketOrderDetails = new JSONObject();
		String symbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ)
				.getString(SymbolConstants.SYMBOL_TOKEN);
		String orderAction = gcRequest.getFromData(OrderConstants.ORDER_ACTION);
		String isBracketOrder = gcRequest.getOptFromData(OrderConstants.IS_BRACKET_ORDER, 
																AppConstants.EMPTY_STR);
		boolean isPOAUser = Boolean.parseBoolean(session.getUserInfo().getString(UserInfoConstants.POA_STATUS));
		if (isBracketOrder.equalsIgnoreCase(DeviceConstants.TRUE)) {
			bracketOrderDetails = gcRequest.getObjectFromData(OrderConstants.MODIFY_ORDER_DETAILS);
		}
		String orderQty = gcRequest.getFromData(OrderConstants.ORDER_QTY);
		String sourceProductType = gcRequest.getFromData(DeviceConstants.TO_PRODUCT_TYPE);
		String destinationProductType = gcRequest.getFromData(DeviceConstants.FROM_PRODUCT_TYPE);

		if (PositionConversion.convertPosition(session, symbolToken, orderAction, orderQty, sourceProductType,
				destinationProductType,isBracketOrder,bracketOrderDetails, getServletContext(), gcRequest, gcResponse)) {
			gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.order.position_converted"));
			if(isPOAUser && orderAction.equalsIgnoreCase(FTConstants.SELL) && sourceProductType.equalsIgnoreCase(ProductType.DELIVERY)) {
				InsertEDISReqResponseDetails.insertEDISApprovalDetails(session, SymbolMap.getSymbolRow(symbolToken), orderQty, getServletContext(), gcRequest, gcResponse);
			}
		}
	}
}
