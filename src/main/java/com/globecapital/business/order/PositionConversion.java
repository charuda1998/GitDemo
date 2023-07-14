package com.globecapital.business.order;

import javax.servlet.ServletContext;

import org.json.JSONObject;

import com.globecapital.api.ft.order.GetGTDOrderBookResponse;
import com.globecapital.api.ft.order.PositionConversionAPI;
import com.globecapital.api.ft.order.PositionConversionRequest;
import com.globecapital.api.ft.order.PositionConversionResponse;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionHelper;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;

public class PositionConversion {


	public static Boolean convertPosition(Session session, String symbolToken, String orderAction, String orderQty,
			String sourceProductType, String destinationProductType, String isBracketOrder,
			JSONObject bracketOrderDetails, ServletContext servletContext, GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		// /**** Session info ****/
		String sUserID = session.getUserID();
		String sGroupID = session.getGroupId();
		boolean flag = true;

		PositionConversionRequest positionConversionRequest = new PositionConversionRequest();

		positionConversionRequest.setUserID(sUserID);
		positionConversionRequest.setGroupId(sGroupID);
		positionConversionRequest.setJKey(session.getjKey());

		/**** Symbol info ****/
		SymbolRow symbolRow = SymbolMap.getSymbolRow(symbolToken);

		positionConversionRequest.setMKtSegId(Integer.parseInt(symbolRow.getMktSegId()));
		positionConversionRequest.setScripTkn(symbolRow.gettokenId());
		positionConversionRequest.setSymbol(symbolRow.getSymbol());
		positionConversionRequest.setSeries(symbolRow.getSeries());
		positionConversionRequest.setClientOdrNo(String.valueOf(SessionHelper.updateClientOrderNo(sUserID)));


		// fix- set gatewayorder no for bracket order

		if (isBracketOrder.equalsIgnoreCase(DeviceConstants.TRUE)) {
			positionConversionRequest.setGatewayOdrNo(bracketOrderDetails
					.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY).getString(OrderConstants.GATEWAY_ORD_NO));
			positionConversionRequest.setBOGatewayOdrNo(bracketOrderDetails
					.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY).getString(OrderConstants.GATEWAY_ORD_NO));
			positionConversionRequest.setIsSpreadScrip("9");

		} else {
			positionConversionRequest.setGatewayOdrNo("");
		}

		/**** Order info ****/
		orderQty = OrderQty.formatToAPI(orderQty,
					symbolRow.getLotSizeInt(), symbolRow.getMktSegId());
		positionConversionRequest.setBuyOrSell(OrderAction.formatToAPI(orderAction));
		positionConversionRequest.setOriginalQty(Integer.parseInt(orderQty));
		positionConversionRequest.setUserRemarks("");
		positionConversionRequest.setOdrReqType(1);
			positionConversionRequest.setToProductType(ProductType.formatToAPI(sourceProductType, false));
			positionConversionRequest.setFromProductType(ProductType.formatToAPI(destinationProductType, false));
		positionConversionRequest.setJSession(session.getjSessionID());
		positionConversionRequest.toString();

		PositionConversionAPI positionapi = new PositionConversionAPI();
		PositionConversionResponse positionConvresponse = new PositionConversionResponse();
		try {
		    positionConvresponse = positionapi.post(positionConversionRequest,
	                PositionConversionResponse.class, session.getAppID(),"SendPosConvRequest");
		} catch(GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(positionConversionRequest,session, servletContext, gcRequest, gcResponse)) {
                        positionConvresponse = positionapi.post(positionConversionRequest,
                                PositionConversionResponse.class, session.getAppID(),"SendPosConvRequest");
                        session = gcRequest.getSession();
                    } else {
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                    }
            } else
                throw new RequestFailedException();
        }
		
		if (!positionConvresponse.getResponseStatus()) {

			throw new RequestFailedException();
		}

		return flag;
	}
}
