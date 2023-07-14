package com.globecapital.services.order;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.globecapital.audit.GCAuditObject;
import com.globecapital.business.edis.InsertEDISReqResponseDetails;
import com.globecapital.business.order.OrderStatus;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
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
import com.globecapital.utils.PriceFormat;

public class CoverNewOrder extends SessionService {
private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {
		Session session = request.getSession();
		String sUserID = session.getUserID();
		String optType = request.getOptFromData(OrderConstants.OPT_TYPE,"");
		String sSymbolToken = request.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);

		GCAuditObject auditObj = request.getAuditObj();
		boolean isPOAUser = Boolean.parseBoolean(session.getUserInfo().getString(UserInfoConstants.POA_STATUS));

		String sOrderSide;
		String sParticipantID = "";
		String sMarketSegID = symRow.getMktSegId();
		String sStrikePrice = symRow.getStrikePrice();
		String orderDetails = "";

		/*** session info ***/

		JSONObject participantObj = session.getUserInfo().getJSONObject(UserInfoConstants.PARTICIPANT_ID);

		SendOrdRequest ftrequest = new SendOrdRequest();

		ftrequest.setJKey(session.getjKey());
		ftrequest.setJSession(session.getjSessionID());
		ftrequest.setUserID(sUserID);
		ftrequest.setGroupId(session.getGroupId());


		/*** symbol info ***/

		sMarketSegID = symRow.getMktSegId();
		//sStrikePrice = symRow.getStrikePrice();
		int iMultiplier = symRow.getMultiplier();

		if(participantObj.has(sMarketSegID))
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
		optType = request.getOptFromData(OrderConstants.OPT_TYPE,"");

		if(request.getFromData(OrderConstants.ORDER_TYPE).equalsIgnoreCase(OrderType.REGULAR_LOT_MARKET))
		    ftrequest.setOrdType(3);
		else
		    throw new Exception("Invalid Order Type");
		ftrequest.setOrderSide(sOrderSide);
		ftrequest.setBuyOrSell(OrderAction.formatToAPI(sOrderSide));
		ftrequest.setOrgQty(OrderQty.formatToAPI(request.getFromData(OrderConstants.ORDER_QTY),
				symRow.getLotSizeInt(), sMarketSegID));
		ftrequest.setOrgQty(request.getFromData(OrderConstants.ORDER_QTY));
		ftrequest.setProdType(ProductType.formatToAPI(request.getFromData(OrderConstants.PRODUCT_TYPE), false));
		ftrequest.setValidity(Validity.formatToAPI(request.getFromData(OrderConstants.VALIDITY)));
		ftrequest.setOptType(optType);
		ftrequest.setDiscQty(0);
		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());

		ftrequest.setTrigPrice(OrderPrice.formatOrderPriceCoverToAPI(request.getFromData(OrderConstants.ORDER_TYPE),
				request.getFromData(OrderConstants.SL_TRIG_PRICE), iMultiplier, quoteDetails.sLTP));

		if(!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc("0");

		int clientOrdNo = SessionHelper.updateClientOrderNo(sUserID);
		ftrequest.setClientOrdNo(clientOrdNo);

		/*** margin plus parameters ***/
		ftrequest.setMPFirstLegPrice(Float.parseFloat(OrderPrice.formatOrderPriceToAPI("",
		        request.getFromData(DeviceConstants.BASE_PRICE), iMultiplier, quoteDetails.sLTP)) );

		/*** Cover order fields ***/
		ftrequest.setMsgCode(FTConstants.NORMAL_MSG_CODE);

		SimpleDateFormat transactionDateFormatter = new SimpleDateFormat(FTConstants.MP_DATE_FORMAT);


		ftrequest.setRecoId(SessionHelper.updateClientOrderNo(sUserID)+sUserID+transactionDateFormatter.format(new Date()));
		ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI("",
				request.getFromData(OrderConstants.SL_PRICE), iMultiplier, quoteDetails.sLTP)); //in paise

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
            }
            else {
                response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                throw new GCException (InfoIDConstants.SUCCESS, "");
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
					if(isPOAUser && sOrderSide.equalsIgnoreCase(FTConstants.SELL) && ProductType.DELIVERY.equalsIgnoreCase(request.getFromData(OrderConstants.PRODUCT_TYPE))) {
						InsertEDISReqResponseDetails.insertEDISApprovalDetails(session, symRow, request.getFromData(OrderConstants.ORDER_QTY),getServletContext(),request,response);
					}
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
			} catch(Exception e) {
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
