package com.globecapital.services.order;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.GetOrderBookResponse;
import com.globecapital.api.ft.order.SendOrdReqAPI;
import com.globecapital.api.ft.order.SendOrdReqResponse;
import com.globecapital.api.ft.order.SendOrdRequest;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderPrice;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;

public class BracketCancelOrder extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {

		String sOrderSide;
		String sMarketSegID, sParticipantID = "";
		String sStrikePrice, optType;

		/*** session info ***/
		Session session = request.getSession();
		String sUserID = session.getUserID();
		JSONObject participantObj = session.getUserInfo().getJSONObject(UserInfoConstants.PARTICIPANT_ID);

		SendOrdRequest ftrequest = new SendOrdRequest();

		ftrequest.setJKey(session.getjKey());
		ftrequest.setJSession(session.getjSessionID());
		ftrequest.setUserID(sUserID);
		ftrequest.setGroupId(session.getGroupId());

		/*** symbol info ***/
		String sSymbolToken = request.getObjectFromData(SymbolConstants.SYMBOL_OBJ)
				.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		sMarketSegID = symRow.getMktSegId();
		sStrikePrice = symRow.getStrikePrice();
		int iMultiplier = symRow.getMultiplier();

		if (participantObj.has(sMarketSegID))
			ftrequest.setParticipantId(sParticipantID);

		ftrequest.setMKtSegId(Integer.parseInt(sMarketSegID));
		ftrequest.setScripTkn(symRow.gettokenId());
		ftrequest.setInstrument(symRow.getInstrument());
		ftrequest.setSymbol(symRow.getSymbol());
		ftrequest.setSeries(symRow.getSeries());
		ftrequest.setExpDt(symRow.getExpiry());
		ftrequest.setMktLot(String.valueOf(symRow.getLotSizeInt()));
		ftrequest.setPrcTick(symRow.getTickPrice());

		/*** Order Info ***/
		sOrderSide = request.getFromData(OrderConstants.ORDER_ACTION);
		optType = request.getOptFromData(OrderConstants.OPT_TYPE, "");

		ftrequest.setOrderSide(sOrderSide);
		ftrequest.setBuyOrSell(OrderAction.formatToAPI(sOrderSide));
		ftrequest.setProdType(ProductType.formatToAPI(request.getFromData(OrderConstants.PRODUCT_TYPE), false));
		ftrequest.setOrdType(OrderType.formatToAPI2(request.getFromData(OrderConstants.ORDER_TYPE)));
		ftrequest.setValidity(Validity.formatToAPI(request.getFromData(OrderConstants.VALIDITY)));
		ftrequest.setOptType(optType);
		ftrequest.setDiscQty(0);
		ftrequest.setTrigPrice("0");

		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		
		if (!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc("0");

//		ftrequest.setClientOrdNo( iClientOrderNo );
//		SessionHelper.updateClientOrderNo(sUserID);

		/*** Bracket order fields ***/
		ftrequest.setMsgCode(FTConstants.BRACKET_MSG_CODE);
		ftrequest.setCancelFlag(true);

		JSONObject orderDetails = request.getObjectFromData(OrderConstants.MODIFY_ORDER_DETAILS);
		ftrequest.setBOSLOrderType(FTConstants.BRACKET_SL_LIMIT);
		ftrequest.setBracketOrderId(orderDetails.getString(OrderConstants.BRACKET_ORDER_ID));
		ftrequest.setLegIndicator(FTConstants.LEG_INDICATOR_MAIN_NEW_MODIFICATION);
		ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
				orderDetails.getString(OrderConstants.PRICE), iMultiplier, quoteDetails.sLTP));
		ftrequest.setOrgQty(orderDetails.getString(OrderConstants.ORDER_QTY));
		ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
				orderDetails.getString(OrderConstants.SL_PRICE), iMultiplier, quoteDetails.sLTP));
		ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
				orderDetails.getString(OrderConstants.SL_TRIG_PRICE), iMultiplier, quoteDetails.sLTP));
		ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
				orderDetails.getString(OrderConstants.PROFIT_PRICE), iMultiplier, quoteDetails.sLTP));
		
		JSONObject mainOrder = orderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
		ftrequest.setExchOrdNo(mainOrder.getString((OrderConstants.EXCH_ORD_NO)));
		ftrequest.setOrdTime(mainOrder.getString(OrderConstants.ORDER_TIME));
		ftrequest.setGatewayOrdNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
		ftrequest.setBOGatewayOrderNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
		
		String sTrailingSL = orderDetails.getString(OrderConstants.TRAILING_SL);
		
		if(sTrailingSL.equals("0") || sTrailingSL.equals("0.00"))
		{
			ftrequest.setSLJumpPrice("0");
			ftrequest.setLTPJumpPrice("0");
		}
		else
		{
			ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sTrailingSL, iMultiplier, quoteDetails.sLTP));
			ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sTrailingSL, iMultiplier, quoteDetails.sLTP));
		}
		
		SendOrdReqAPI api = new SendOrdReqAPI();
		SendOrdReqResponse ftresponse = new SendOrdReqResponse();
		try {
		    ftresponse = api.post(ftrequest, SendOrdReqResponse.class, session.getAppID(),"SendOrderRequest");
    	}catch(GCException e) {
            log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), request, response)) {
                    throw new GCException (InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } else
                throw new RequestFailedException();
        }
		if (ftresponse.getResponseStatus()) {
			response.addToData(DeviceConstants.MSG, ftresponse.getResponseObject().getStatus());			
		}
		else
			throw new RequestFailedException();
		 
	}

	

}
