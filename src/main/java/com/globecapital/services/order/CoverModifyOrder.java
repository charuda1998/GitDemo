package com.globecapital.services.order;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.SendOrdReqAPI;
import com.globecapital.api.ft.order.SendOrdReqResponse;
import com.globecapital.api.ft.order.SendOrdRequest;
import com.globecapital.audit.GCAuditObject;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
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
import com.globecapital.utils.PriceFormat;

public class CoverModifyOrder extends SessionService{
private static final long serialVersionUID = 1L;
	
	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {
		GCAuditObject auditObj = request.getAuditObj();

		String sOrderSide;
		String sMarketSegID, sParticipantID = "";
		String sStrikePrice, optType;

		/*** session info ***/
		Session session = request.getSession();
		
		String sUserID = session.getUserID();
		JSONObject participantObj = session.getUserInfo().getJSONObject(UserInfoConstants.PARTICIPANT_ID);
				
		JSONObject OrderDetails;
		String sSymbolToken = request.getObjectFromData(SymbolConstants.SYMBOL_OBJ)
				.getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		SendOrdRequest ftrequest = new SendOrdRequest();
		
		ftrequest.setJKey(session.getjKey());
		ftrequest.setJSession(session.getjSessionID());
		ftrequest.setUserID(sUserID);
		ftrequest.setGroupId(session.getGroupId());

		
		sMarketSegID = symRow.getMktSegId();
		sStrikePrice = symRow.getStrikePrice();
		int iMultiplier = symRow.getMultiplier();

		if (participantObj.has(sMarketSegID))
			ftrequest.setParticipantId(sParticipantID);
		
		/*** symbol info ***/
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
		if(request.getFromData(OrderConstants.ORDER_TYPE).equalsIgnoreCase(OrderType.REGULAR_LOT_MARKET))
            ftrequest.setOrdType(3);
        else
            throw new Exception("Invalid Order Type");
		ftrequest.setValidity(Validity.formatToAPI(request.getFromData(OrderConstants.VALIDITY)));
		ftrequest.setOptType(optType);
		ftrequest.setDiscQty(0);

		if (!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc("0");

		/*** Cover order fields ***/
		ftrequest.setMsgCode(FTConstants.NORMAL_MSG_CODE);
		ftrequest.setModifyFlag(true);

		JSONObject oldOrderDetails = request.getObjectFromData(OrderConstants.MODIFY_ORDER_DETAILS);

		String sNewSLPrice = request.getFromData(OrderConstants.SL_PRICE);
		String sNewSLTrigPrice = request.getFromData(OrderConstants.SL_TRIG_PRICE);
		
		String sSLPrice = oldOrderDetails.getString(OrderConstants.SL_PRICE);
		String sSLTrigPrice = oldOrderDetails.getString(OrderConstants.SL_TRIG_PRICE);
		String sQty = oldOrderDetails.getString(OrderConstants.ORDER_QTY);
		
		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		/*** margin plus parameters ***/
		ftrequest.setMPFirstLegPrice(Float.parseFloat(OrderPrice.formatOrderPriceToAPI("", 
                request.getFromData(DeviceConstants.BASE_PRICE), iMultiplier, quoteDetails.sLTP)) );

		boolean isMainLegModification = Boolean.parseBoolean(oldOrderDetails.
				getString(OrderConstants.IS_MAIN_ORDER_MODIFIABLE));

		if (isMainLegModification)
			OrderDetails = oldOrderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
		else 
			OrderDetails = oldOrderDetails.getJSONObject(OrderConstants.SL_ORDER_MODIFY);
		
		ftrequest.setOrgQty(sQty);
		ftrequest.setExchOrdNo(OrderDetails.getString((OrderConstants.EXCH_ORD_NO)));
		ftrequest.setOrdTime(OrderDetails.getString(OrderConstants.ORDER_TIME));
		ftrequest.setGatewayOrdNo(OrderDetails.getString(OrderConstants.GATEWAY_ORD_NO));
		ftrequest.setClientOrdNo(OrderDetails.getString(FTConstants.CLIENT_ORD_NO));
						
		if (sSLPrice.equals(sNewSLPrice)) 
			ftrequest.setOrdPrice(OrderPrice.formatOrderPriceCoverToAPI(request.getFromData(OrderConstants.ORDER_TYPE), sSLPrice, iMultiplier, quoteDetails.sLTP));
		else 
			ftrequest.setOrdPrice(OrderPrice.formatOrderPriceCoverToAPI(request.getFromData(OrderConstants.ORDER_TYPE), sNewSLPrice, iMultiplier, quoteDetails.sLTP));
			
		if (sSLTrigPrice.equals(sNewSLTrigPrice)) 
			ftrequest.setTrigPrice(OrderPrice.formatOrderPriceCoverToAPI(request.getFromData(OrderConstants.ORDER_TYPE), sSLTrigPrice, iMultiplier, quoteDetails.sLTP));
		else 
			ftrequest.setTrigPrice(OrderPrice.formatOrderPriceCoverToAPI(request.getFromData(OrderConstants.ORDER_TYPE), sNewSLTrigPrice, iMultiplier, quoteDetails.sLTP));

		SendOrdReqAPI api = new SendOrdReqAPI();
		SendOrdReqResponse ftresponse = new SendOrdReqResponse();
		try {
            ftresponse = api.post(ftrequest, SendOrdReqResponse.class, session.getAppID(),"SendOrderRequest");
        }catch(GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), request, response)) {
                    session = request.getSession();
                    response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                    response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                    throw new GCException (InfoIDConstants.SUCCESS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            }else
                throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
        }

		if (ftresponse.getResponseStatus()) {
			response.addToData(DeviceConstants.MSG, ftresponse.getResponseObject().getStatus());
			response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_OK);
			response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_SUBMITTED);
			String orderDetails = "";
			if(!ExchangeSegment.isEquitySegment(symRow.getMktSegId()))
				orderDetails =  String.format(InfoMessage.getInfoMSG("info_msg.order_status_success"), String.valueOf(Integer.parseInt(request.getFromData(OrderConstants.ORDER_QTY))), symRow.getCompanyName());
			else
				orderDetails =  String.format(InfoMessage.getInfoMSG("info_msg.order_status_success"), String.valueOf(Integer.parseInt(request.getFromData(OrderConstants.ORDER_QTY))), symRow.getSymbol());
			response.addToData(DeviceConstants.ORDER_DETAILS, orderDetails);
			auditObj.setAuditInfo(session);
			//TO-DO : audit for failure cases
		}
		else
			throw new RequestFailedException();
		 
	}

}
