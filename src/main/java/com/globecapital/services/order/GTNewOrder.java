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
import com.globecapital.audit.GCAuditObject;
import com.globecapital.business.edis.InsertEDISReqResponseDetails;
import com.globecapital.business.order.OrderStatus;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
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

public class GTNewOrder extends SessionService {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		GCAuditObject auditObj = gcRequest.getAuditObj();
		
		String sOrderSide, sOrderType;
		String sMarketSegID, sParticipantID = "";
		String sStrikePrice, optType, sDiscQty;
		String orderDetails = "";
		int clientOrdNo;
			
		/*** session info ***/
		Session session = gcRequest.getSession();
		boolean isPOAUser = Boolean.parseBoolean(session.getUserInfo().getString(UserInfoConstants.POA_STATUS));
		String sUserID = session.getUserID();
		JSONObject participantObj = session.getUserInfo().getJSONObject(UserInfoConstants.PARTICIPANT_ID);

		SendOrdRequest ftrequest = new SendOrdRequest();
		ftrequest.setJKey(session.getjKey());
		ftrequest.setJSession(session.getjSessionID());
		ftrequest.setUserID(sUserID);
		ftrequest.setGroupId(session.getGroupId());
		
		
		/*** symbol info ***/
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		sMarketSegID = symRow.getMktSegId();
		sStrikePrice = symRow.getStrikePrice();
		
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
		sOrderSide = gcRequest.getFromData(OrderConstants.ORDER_ACTION);
		sOrderType = gcRequest.getFromData(OrderConstants.ORDER_TYPE);
		optType = gcRequest.getOptFromData(OrderConstants.OPT_TYPE,"");
		sDiscQty = gcRequest.getOptFromData(OrderConstants.DISC_QTY,"");
		boolean isAMO = Boolean.parseBoolean(gcRequest.getOptFromData(OrderConstants.IS_AMO, "false"));
		
		/*** Check If Order Is GTD or GTC ***/
		if(sMarketSegID.equals("1")) {
			ftrequest.setMsgCode(FTConstants.GTD_MSG_CODE);
			ftrequest.setDays(OrderConstants.GTD_DEFAULT_DAYS);
			ftrequest.setValidity(Validity.formatToAPI(Validity.GTD_EQ));
		}else {
			ftrequest.setDays(OrderConstants.GTD_DEFAULT_DAYS);
			ftrequest.setValidity(Validity.formatToAPI(Validity.GTC));
		}
			
		ftrequest.setOrderSide(sOrderSide);
		ftrequest.setBuyOrSell(OrderAction.formatToAPI(sOrderSide));

		ftrequest.setOrgQty(OrderQty.formatToAPI(gcRequest.getFromData(OrderConstants.ORDER_QTY),
												symRow.getLotSizeInt(), sMarketSegID));
		if(isAMO)
			ftrequest.setProdType(ProductType.formatToAPI(gcRequest.getFromData(OrderConstants.PRODUCT_TYPE), true));
		else
			ftrequest.setProdType(ProductType.formatToAPI(gcRequest.getFromData(OrderConstants.PRODUCT_TYPE), false));
		
		ftrequest.setOrdType(OrderType.formatToAPI(gcRequest.getFromData(OrderConstants.ORDER_TYPE)));
		ftrequest.setOptType(optType);
		
		if(sDiscQty.isEmpty())
			ftrequest.setDiscQty(AppConstants.INT_ZERO);
		else
			ftrequest.setDiscQty(Integer.parseInt(OrderQty.formatToAPI(sDiscQty,symRow.getLotSizeInt(), sMarketSegID)));

		ftrequest.setOrdPrice(PriceFormat.formatPriceToPaise(gcRequest.getFromData(OrderConstants.PRICE), symRow.getMultiplier()));
		ftrequest.setTrigPrice("");
		
		if(!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc(AppConstants.STR_ZERO);
		
		clientOrdNo =  SessionHelper.updateClientOrderNo(sUserID);
		ftrequest.setClientOrdNo(clientOrdNo);
		
		orderDetails = generateOrderDetails(gcRequest, symRow);
		gcResponse.addToData(DeviceConstants.ORDER_DETAILS, orderDetails);
		if (!SessionHelper.validateDuplicateOrder(session.getSessionID(), gcRequest.getData().toString())) {

			gcResponse.setInfoID(InfoIDConstants.DUPLICATE_ORDER);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.duplicate_order"));

			return;
		}
		
		SendOrdReqAPI api = new SendOrdReqAPI();
		SendOrdReqResponse ftresponse = null;
		try {
			ftresponse = api.post(ftrequest, SendOrdReqResponse.class, session.getAppID(),"SendOrderRequest");
		}catch(GCException e) {
		    if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), gcRequest, gcResponse)) {
                    session = gcRequest.getSession();
                    gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                    gcResponse.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                    throw new GCException (InfoIDConstants.SUCCESS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } else {
                gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                gcResponse.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
            }
		}
		if(ftresponse.getResponseStatus()){
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
			GetOrderBookResponse orderbookResponse = orderbookAPI.post(orderbookReq,
					GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
			GetOrderBookResponseObject orderObj = orderbookResponse.getResponseObject();
			List<GetOrderBookObjectRow> orderRows = orderObj.getObjJSONRows();
			boolean isSuccess = true; 
			boolean isPending = false;
			if(!orderRows.isEmpty()) {
				if(orderRows.get(0).getGTDOrderStatus().equals(FTConstants.OMS_XMITTED)){
					orderRows = callOrderBookAPI(session, orderbookReq, orderbookAPI, sleepTime);
					if(!orderRows.isEmpty()) {
						if(orderRows.get(0).getGTDOrderStatus().equals(FTConstants.OMS_XMITTED)) {
							isSuccess = false;
							isPending = true;
						}else
							isSuccess = OrderStatus.getStatusForNewGTDOrder(orderRows.get(0).getGTDOrderStatus());
					}else
					isSuccess = OrderStatus.getStatusForNewGTDOrder(orderRows.get(0).getGTDOrderStatus());	
				}else
					isSuccess = OrderStatus.getStatusForNewGTDOrder(orderRows.get(0).getGTDOrderStatus());
			}else {
				orderRows = callOrderBookAPI(session, orderbookReq, orderbookAPI, sleepTime);
				if(!orderRows.isEmpty()) {
					if(orderRows.get(0).getGTDOrderStatus().equals(FTConstants.OMS_XMITTED)) {
						isSuccess = false;
						isPending = true;
					}else
						isSuccess = OrderStatus.getStatusForNewGTDOrder(orderRows.get(0).getGTDOrderStatus());
				}
				else {
					isSuccess = false;
					isPending = true;
				}
			}
			if(isSuccess) {
				gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_OK);
				gcResponse.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_PLACED);
				if(isPOAUser && sOrderSide.equalsIgnoreCase(FTConstants.SELL) && ProductType.DELIVERY.equalsIgnoreCase(gcRequest.getFromData(OrderConstants.PRODUCT_TYPE))) {
					InsertEDISReqResponseDetails.insertEDISApprovalDetails(session, symRow, gcRequest.getFromData(OrderConstants.ORDER_QTY),getServletContext(),gcRequest,gcResponse);
				}
			}
			else {
				if(isPending) {
					gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_IN_QUEUE);
					gcResponse.addToData(DeviceConstants.STATUS, InfoMessage.getInfoMSG("info_msg.order_in_queue"));
				}else {
					gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
					gcResponse.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
				}
			}
			auditObj.setAuditInfo(session);
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
