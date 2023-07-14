package com.globecapital.services.order;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.FetchMarginPlusParamsObject;
import com.globecapital.api.ft.order.FetchMarginPlusParamsResponse;
import com.globecapital.api.ft.order.SendOrdReqAPI;
import com.globecapital.api.ft.order.SendOrdReqResponse;
import com.globecapital.api.ft.order.SendOrdRequest;
import com.globecapital.business.order.GetMarginPlusParameter;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.*;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.globecapital.utils.PriceFormat;

public class CoverCancelOrder extends SessionService{
private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {
		String sOrderSide;
		String sMarketSegID, sParticipantID = "";
		String sStrikePrice, optType;

		/*** session info ***/
		Session session = request.getSession();
		String sUserID = session.getUserID();

		String sSymbolToken = request.getObjectFromData(SymbolConstants.SYMBOL_OBJ)
				.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		JSONObject participantObj = session.getUserInfo().getJSONObject(UserInfoConstants.PARTICIPANT_ID);
		int moneyMode=GetMarginPlusParameter.getStrPrcMoneyMode(request.getFromData(OrderConstants.ORDER_ACTION), symRow.getStrikePrice(),request.getFromData(OrderConstants.PRICE));
		FetchMarginPlusParamsResponse marginPlusResponse=GetMarginPlusParameter.getMarginPlusParameter(symRow, session, moneyMode);
		FetchMarginPlusParamsObject marginPlusParams = marginPlusResponse.getResponseObject();
		SendOrdRequest ftrequest = new SendOrdRequest();

		ftrequest.setJKey(session.getjKey());
		ftrequest.setJSession(session.getjSessionID());
		ftrequest.setUserID(sUserID);
		ftrequest.setGroupId(session.getGroupId());

		/*** symbol info ***/
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
		ftrequest.setMktLot(symRow.getLotSize());
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
		ftrequest.setTrigPrice(request.getFromData(FTConstants.TRIG_PRICE));

		if (!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc(FTConstants.ZERO_FLAG);


		/*** Cover Order order fields ***/
		ftrequest.setMsgCode(FTConstants.NORMAL_MSG_CODE);
		ftrequest.setCancelFlag(true);
		
		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());

		/*** margin plus parameters ***/
		ftrequest.setMPFirstLegPrice(Float.parseFloat(OrderPrice.formatOrderPriceToAPI("",
                request.getFromData(DeviceConstants.BASE_PRICE), iMultiplier, quoteDetails.sLTP)) );

		JSONObject orderDetails = request.getObjectFromData(OrderConstants.MODIFY_ORDER_DETAILS);
		ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2,
				orderDetails.getString(OrderConstants.PRICE), iMultiplier, quoteDetails.sLTP));
		ftrequest.setOrgQty(orderDetails.getString(OrderConstants.ORDER_QTY));

		/*** order details ***/
		//JSONObject mainOrder = orderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
		ftrequest.setExchOrdNo(request.getFromData(OrderConstants.EXCH_ORD_NO));
		ftrequest.setOrdTime(request.getFromData(OrderConstants.ORDER_TIME));
		ftrequest.setGatewayOrdNo(request.getFromData(OrderConstants.GATEWAY_ORD_NO));
		ftrequest.setClientOrdNo(request.getFromData(FTConstants.CLIENT_ORD_NO));

		SendOrdReqAPI api = new SendOrdReqAPI();
        SendOrdReqResponse ftresponse = new SendOrdReqResponse();
        try {
            ftresponse = api.post(ftrequest, SendOrdReqResponse.class, session.getAppID(),"SendOrderRequest");
        }catch (GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), request, response)) {
                    session = request.getSession();
                    throw new GCException (InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } else if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.DYNAMIC_MSG)){
                throw new GCException (InfoIDConstants.DYNAMIC_MSG, e.getMessage());
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
