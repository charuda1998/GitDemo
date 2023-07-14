package com.globecapital.services.order;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.edis.FetchEDISQuantityAPI;
import com.globecapital.api.ft.edis.GetEDISQuantityRequest;
import com.globecapital.api.ft.edis.GetEDISQuantityResponse;
import com.globecapital.api.ft.edis.GetEDISScripDetailsObject;
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
import com.globecapital.business.order.AMODetails;
import com.globecapital.business.order.OrderStatus;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
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

public class NewOrder_101 extends SessionService {

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
		boolean isAMO = false;
		if( gcRequest.getOptFromData(OrderConstants.ORIGINATED_FROM,"").equalsIgnoreCase(OrderConstants.WEB))
		    isAMO = AMODetails.isAMOOrder(symRow.getExchange());
		else
		    isAMO = Boolean.parseBoolean(gcRequest.getOptFromData(OrderConstants.IS_AMO, "false"));
		
		/*** TFC POA handle ***/
        if( gcRequest.getOptFromData(OrderConstants.ORIGINATED_FROM,"").equalsIgnoreCase(OrderConstants.WEB) && !isPOAUser && sOrderSide.equalsIgnoreCase(OrderAction.SELL)  
                && gcRequest.getFromData(OrderConstants.PRODUCT_TYPE).equalsIgnoreCase(ProductType.DELIVERY) && getEDISQuantityDetails(session, symRow.getISIN(), Integer.parseInt(gcRequest.getFromData(OrderConstants.ORDER_QTY)),0,getServletContext(),gcRequest,gcResponse) ) {
            throw new GCException (InfoIDConstants.DYNAMIC_MSG, InfoMessage.getValue("info_msg.valid.order.tfc"));
        } 
		
		ftrequest.setOrderSide(sOrderSide);
		ftrequest.setBuyOrSell(OrderAction.formatToAPI(sOrderSide));

		ftrequest.setOrgQty(OrderQty.formatToAPI(gcRequest.getFromData(OrderConstants.ORDER_QTY),
												symRow.getLotSizeInt(), sMarketSegID));
		log.info(symRow.getDispLotSizeInt() +" "+symRow.getLotSizeInt());
		if(isAMO)
			ftrequest.setProdType(ProductType.formatToAPI(gcRequest.getFromData(OrderConstants.PRODUCT_TYPE), true));
		else
			ftrequest.setProdType(ProductType.formatToAPI(gcRequest.getFromData(OrderConstants.PRODUCT_TYPE), false));
		
		ftrequest.setOrdType(OrderType.formatToAPI(gcRequest.getFromData(OrderConstants.ORDER_TYPE)));
		ftrequest.setValidity(Validity.formatToAPI(gcRequest.getFromData(OrderConstants.VALIDITY)));
		ftrequest.setOptType(optType);
		
		if(sDiscQty.isEmpty())
			ftrequest.setDiscQty(AppConstants.INT_ZERO);
		else
			ftrequest.setDiscQty(Integer.parseInt(OrderQty.formatToAPI(sDiscQty,symRow.getLotSizeInt(), sMarketSegID)));

		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		
		ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(sOrderType, 
				gcRequest.getFromData(OrderConstants.PRICE), symRow.getMultiplier(), quoteDetails.sLTP));

				
		/*if(sOrderType.equals(OrderType.STOP_LOSS_MARKET) || sOrderType.equals(OrderType.REGULAR_LOT_MARKET))
			ftrequest.setOrdPrice("0");
		else
		{
			Double fOrderPrice = Double.parseDouble(request.getFromData(OrderConstants.PRICE));
			ftrequest.setOrdPrice(String.format("%.0f", (fOrderPrice * symRow.getMultiplier()))); //in paise
		}*/

		ftrequest.setTrigPrice(OrderPrice.formatTriggerPriceToAPI(sOrderType,
				gcRequest.getFromData(OrderConstants.TRIG_PRICE), symRow.getMultiplier(), quoteDetails.sLTP));
		
		/* if(sOrderType.equals(OrderType.STOP_LOSS_LIMIT) || sOrderType.equals(OrderType.STOP_LOSS_MARKET))
		{
			Double triggerPrice = Double.parseDouble(request.getFromData(OrderConstants.TRIG_PRICE));
			ftrequest.setTrigPrice(String.format("%.0f", (triggerPrice * symRow.getMultiplier() )));
		}
		else
			ftrequest.setTrigPrice("0"); */
		
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
			        gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_IN_QUEUE);
	                gcResponse.addToData(DeviceConstants.STATUS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
	                throw new GCException (InfoIDConstants.SUCCESS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
			}    
            else {
                gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                gcResponse.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                throw new GCException (InfoIDConstants.SUCCESS, "");
            }
		}catch(NullPointerException e) {
			gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_IN_QUEUE);
			gcResponse.addToData(DeviceConstants.STATUS, InfoMessage.getInfoMSG("info_msg.timeout.order_failed"));
			throw new GCException (InfoIDConstants.SUCCESS, "");
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
					}else {
						isSuccess = false;
						isPending = true;
					}
				}
				if(isSuccess) {
					gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_OK);
					gcResponse.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_PLACED);
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
			}catch(Exception e) {
				log.debug(e+" Order Book Failed");
				gcResponse.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_IN_QUEUE);
				gcResponse.addToData(DeviceConstants.STATUS, InfoMessage.getInfoMSG("info_msg.order_in_queue"));
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
	
	private static boolean getEDISQuantityDetails(Session session, String isin, int iQty, int modifyApprovedQty,ServletContext servletContext,GCRequest gcRequest,GCResponse gcResponse) throws JSONException, Exception {
        FetchEDISQuantityAPI edisQuantityAPI = new FetchEDISQuantityAPI();
        GetEDISQuantityRequest edisQuantityRequest = new GetEDISQuantityRequest();
        GetEDISQuantityResponse edisQuantityResponse = new GetEDISQuantityResponse();
        edisQuantityRequest.setUserID(session.getUserID());
        edisQuantityRequest.setGroupId(session.getGroupId());
        edisQuantityRequest.setJKey(session.getjKey());
        edisQuantityRequest.setJSession(session.getjSessionID());
        edisQuantityRequest.setMktSegId(FTConstants.EQ_COMBINED_SEGMENT_ID);
        edisQuantityRequest.setToken(FTConstants.ALL_TOKENS);
        try {
        edisQuantityResponse = edisQuantityAPI.post(edisQuantityRequest, GetEDISQuantityResponse.class, session.getAppID(),"EDISQuantityDetails");
        }
        catch(GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(edisQuantityRequest,session, servletContext, gcRequest, gcResponse)) {
                    edisQuantityResponse = edisQuantityAPI.post(edisQuantityRequest, GetEDISQuantityResponse.class, session.getAppID(),"EDISQuantityDetails");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            }
            else 
                throw new RequestFailedException();
        }
        List<GetEDISScripDetailsObject> scripDetailsList = edisQuantityResponse.getResponseObject().getScripDetailList();
        for(int i = 0; i < scripDetailsList.size(); i++) {
            GetEDISScripDetailsObject scripDetail = scripDetailsList.get(i);
            if(scripDetail.getISINCode().equals(isin)) {
                if(iQty <= scripDetail.getTodayFreeQty() || iQty <= scripDetail.getApprovedQuantity() || iQty <= scripDetail.geteDISCheckQty() || scripDetail.getApprovedQuantity() > scripDetail.getTotalFreeQty()) {
                    return false;
                }
                
                if(iQty > scripDetail.getTotalFreeQty()) {
                    iQty = scripDetail.getTotalFreeQty() - scripDetail.getTodayFreeQty() - scripDetail.getApprovedQuantity();
                }
                
                if(iQty - modifyApprovedQty - scripDetail.getApprovedQuantity() - scripDetail.getTodayFreeQty() > 0 )
                    return true;
                else
                    return false;
            }
        }
        return false;
    }
}
