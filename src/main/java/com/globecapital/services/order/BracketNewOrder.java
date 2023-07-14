package com.globecapital.services.order;


import java.util.List;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.GetOrderBookAPI;
import com.globecapital.api.ft.order.GetOrderBookObjectRow;
import com.globecapital.api.ft.order.GetOrderBookRequest;
import com.globecapital.api.ft.order.GetOrderBookResponse;
import com.globecapital.api.ft.order.GetOrderBookResponseObject;
import com.globecapital.api.ft.order.SendOrdReqAPI;
import com.globecapital.api.ft.order.SendOrdReqResponse;
import com.globecapital.api.ft.order.SendOrdRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.audit.GCAuditObject;
import com.globecapital.business.order.OrderStatus;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderPrice;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidRequestKeyException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionHelper;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.msf.utils.helper.Helper;

public class BracketNewOrder extends SessionService {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {
		
		GCAuditObject auditObj = request.getAuditObj();

		String sOrderSide;
		String sMarketSegID, sParticipantID = "";
		String sStrikePrice, optType;
		String orderDetails = "";
			
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
		String sSymbolToken = request.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		sMarketSegID = symRow.getMktSegId();
		sStrikePrice = symRow.getStrikePrice();
		int iMultiplier = symRow.getMultiplier();
		
		if(participantObj.has(sMarketSegID))
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
		optType = request.getOptFromData(OrderConstants.OPT_TYPE,"");
		
		ftrequest.setOrderSide(sOrderSide);
		ftrequest.setBuyOrSell(OrderAction.formatToAPI(sOrderSide));
		ftrequest.setOrgQty(OrderQty.formatToAPI(request.getFromData(OrderConstants.ORDER_QTY),
				symRow.getLotSizeInt(), sMarketSegID));
		//ftrequest.setOrgQty(request.getFromData(OrderConstants.ORDER_QTY));
		ftrequest.setProdType(ProductType.formatToAPI(request.getFromData(OrderConstants.PRODUCT_TYPE), false));
		ftrequest.setOrdType(OrderType.formatToAPI2(request.getFromData(OrderConstants.ORDER_TYPE)));
		ftrequest.setValidity(Validity.formatToAPI(request.getFromData(OrderConstants.VALIDITY)));
		ftrequest.setOptType(optType);
		ftrequest.setDiscQty(0);
		ftrequest.setTrigPrice("0");
		
		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		
		if(!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc("0");
		
		int clientOrdNo = SessionHelper.updateClientOrderNo(sUserID);
		ftrequest.setClientOrdNo(clientOrdNo);
		
		/*** Bracket order fields ***/
		ftrequest.setMsgCode(FTConstants.BRACKET_MSG_CODE);
		ftrequest.setBOSLOrderType(FTConstants.BRACKET_SL_LIMIT);
		
		ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
				request.getFromData(OrderConstants.PRICE), iMultiplier, quoteDetails.sLTP)); //in paise
		ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2,
				request.getFromData(OrderConstants.SL_PRICE), iMultiplier, quoteDetails.sLTP));
		ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2,
				request.getFromData(OrderConstants.SL_TRIG_PRICE), iMultiplier, quoteDetails.sLTP));
		ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2,
				request.getFromData(OrderConstants.PROFIT_PRICE), iMultiplier, quoteDetails.sLTP));
		ftrequest.setLegIndicator(FTConstants.LEG_INDICATOR_MAIN_NEW_MODIFICATION);
		
		String sTrailingSL = request.getFromData(OrderConstants.TRAILING_SL);
		
		if(sTrailingSL.equals("0") || sTrailingSL.isEmpty())
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
		
		orderDetails = generateOrderDetails(request, symRow);
		response.addToData(DeviceConstants.ORDER_DETAILS, orderDetails);
		if (!SessionHelper.validateDuplicateOrder(session.getSessionID(), request.getData().toString())) {

			response.setInfoID(InfoIDConstants.DUPLICATE_ORDER);
			response.setInfoMsg(InfoMessage.getInfoMSG("info_msg.duplicate_order"));

			return;
		}
		
		SendOrdReqAPI api = new SendOrdReqAPI();
		SendOrdReqResponse ftresponse = null;
		try {
			ftresponse = api.post(ftrequest, SendOrdReqResponse.class, session.getAppID(),"SendOrderRequest");
		}catch(GCException e) {
		    if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), request, response)) {
                    session = request.getSession();
                    response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_IN_QUEUE);
	                response.addToData(DeviceConstants.STATUS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
	                throw new GCException (InfoIDConstants.SUCCESS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } else {
                response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
            }
		}

		if (ftresponse.getResponseStatus()) {
			GetOrderBookRequest orderbookReq = new GetOrderBookRequest();
			orderbookReq.setUserID(sUserID);
			orderbookReq.setGroupId(session.getGroupId());
			orderbookReq.setJKey(session.getjKey());
			orderbookReq.setJSession(session.getjSessionID());
			orderbookReq.setClientOrdNo(String.valueOf(clientOrdNo));
			
			GetOrderBookAPI orderbookAPI = new GetOrderBookAPI();
			//TODO : Sleep added temporarily since we are getting the updated order response after a small delay
			int sleepTime = AppConfig.getIntValue("ft.api.sleep.millis");
			Thread.sleep(sleepTime);
			try {
				GetOrderBookResponse orderbookResponse = orderbookAPI.post(orderbookReq,
						GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
				GetOrderBookResponseObject orderObj = orderbookResponse.getResponseObject();
				List<GetOrderBookObjectRow> orderRows = orderObj.getObjJSONRows();
				boolean isSuccess = true; 
				boolean isPending = false;
				if(!orderRows.isEmpty()) {
					if(orderRows.get(0).getOrdStat().equals(FTConstants.OMS_XMITTED)){
						orderRows = callOrderBookAPI(session, orderbookReq, orderbookAPI, sleepTime);
						if(!orderRows.isEmpty()) {
							if(orderRows.get(0).getOrdStat().equals(FTConstants.OMS_XMITTED)) {
								isSuccess = false;
								isPending = true;
							}else
								isSuccess = OrderStatus.getStatusForNewOrder(orderRows.get(0).getOrdStat());
						}else
						isSuccess = OrderStatus.getStatusForNewOrder(orderRows.get(0).getOrdStat());	
					}else
						isSuccess = OrderStatus.getStatusForNewOrder(orderRows.get(0).getOrdStat());
				}else {
					orderRows = callOrderBookAPI(session, orderbookReq, orderbookAPI, sleepTime);
					if(!orderRows.isEmpty()) {
						if(orderRows.get(0).getOrdStat().equals(FTConstants.OMS_XMITTED)) {
							isSuccess = false;
							isPending = true;
						}else
							isSuccess = OrderStatus.getStatusForNewOrder(orderRows.get(0).getOrdStat());
					}
					else {
						isSuccess = false;
						isPending = true;
					}
				}
				if(isSuccess) {
					response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_OK);
					response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_PLACED);
				}
				else {
					if(isPending) {
						response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_IN_QUEUE);
						response.addToData(DeviceConstants.STATUS, InfoMessage.getInfoMSG("info_msg.order_in_queue"));
					}else {
						response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
						response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
					}
				}
				auditObj.setAuditInfo(session);
			}catch (Exception e) {
				response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_IN_QUEUE);
				response.addToData(DeviceConstants.STATUS, InfoMessage.getInfoMSG("info_msg.order_in_queue"));
			}
		}
		else
			throw new RequestFailedException();
	}
	
	private String generateOrderDetails(GCRequest gcRequest, SymbolRow symRow) throws InvalidRequestKeyException {
		String orderDetails;
		if(!ExchangeSegment.isEquitySegment(symRow.getMktSegId()))
			orderDetails =  String.format(InfoMessage.getInfoMSG("info_msg.order_status_success"), String.valueOf(Long.parseLong(gcRequest.getFromData(OrderConstants.ORDER_QTY))), symRow.getCompanyName());
		else
			orderDetails =  String.format(InfoMessage.getInfoMSG("info_msg.order_status_success"), String.valueOf(Long.parseLong(gcRequest.getFromData(OrderConstants.ORDER_QTY))), symRow.getSymbol());
		return orderDetails;
	}

	private List<GetOrderBookObjectRow> callOrderBookAPI(Session session, GetOrderBookRequest orderbookReq,
			GetOrderBookAPI orderbookAPI, int sleepTime) throws InterruptedException, GCException {
		GetOrderBookResponse orderbookResponse;
		GetOrderBookResponseObject orderObj;
		List<GetOrderBookObjectRow> orderRows;
		Thread.sleep(sleepTime);
		orderbookResponse = orderbookAPI.post(orderbookReq, GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
		orderObj = orderbookResponse.getResponseObject();
		orderRows = orderObj.getObjJSONRows();
		return orderRows;
	}
	

}
