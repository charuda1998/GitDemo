package com.globecapital.services.order;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.GetOrderBookResponse;
import com.globecapital.api.ft.order.SendOrdReqAPI;
import com.globecapital.api.ft.order.SendOrdReqResponse;
import com.globecapital.api.ft.order.SendOrdRequest;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderPrice;
import com.globecapital.constants.order.OrderQty;
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

public class GTCancelOrder extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String sOrderSide, sOrderType;
		String sMarketSegID, sParticipantID = "";
		String sStrikePrice, optType, sDiscQty;
		
		
		
		/*** session info ***/
		Session session = gcRequest.getSession();
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
		
		/*** Check If Order Is GTD or GTC ***/
		if(sMarketSegID.equals("1")) {
			ftrequest.setMsgCode(FTConstants.GTD_MSG_CODE);
			ftrequest.setDays(OrderConstants.GTD_DEFAULT_DAYS);
			ftrequest.setValidity(Validity.formatToAPI(Validity.GTD_EQ));
		}else {
			ftrequest.setDays(OrderConstants.GTD_DEFAULT_DAYS);
			ftrequest.setValidity(Validity.formatToAPI(Validity.GTC));
		}
		/*** Cancel Order Info ***/
		ftrequest.setCancelFlag(true);
		ftrequest.setExchOrdNo(gcRequest.getFromData(OrderConstants.EXCH_ORD_NO));
		ftrequest.setOrdTime(gcRequest.getFromData(OrderConstants.ORDER_TIME));
		ftrequest.setGatewayOrdNo(gcRequest.getFromData(OrderConstants.GATEWAY_ORD_NO));
		
		
		/*** Order Info ***/
		sOrderSide = gcRequest.getFromData(OrderConstants.ORDER_ACTION);
		sOrderType = gcRequest.getFromData(OrderConstants.ORDER_TYPE);
		optType = gcRequest.getOptFromData(OrderConstants.OPT_TYPE,"");
		sDiscQty = gcRequest.getOptFromData(OrderConstants.DISC_QTY,"");
		boolean isAMO = Boolean.parseBoolean(gcRequest.getOptFromData(OrderConstants.IS_AMO, "false"));
		
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
			ftrequest.setDiscQty(0);
		else
			ftrequest.setDiscQty(Integer.parseInt(OrderQty.formatToAPI(sDiscQty,symRow.getLotSizeInt(), sMarketSegID)));

		ftrequest.setOrdPrice(PriceFormat.formatPriceToPaise(gcRequest.getFromData(OrderConstants.PRICE), symRow.getMultiplier()));
		ftrequest.setTrigPrice("");

		if(!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc("0");
		
		SendOrdReqAPI api = new SendOrdReqAPI();
		SendOrdReqResponse ftresponse = new SendOrdReqResponse();
		try {
		    ftresponse = api.post(ftrequest, SendOrdReqResponse.class, session.getAppID(),"SendOrderRequest");
		}catch(GCException e) {
            log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), gcRequest, gcResponse)) {
                    session = gcRequest.getSession();
                    throw new GCException (InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } else
                throw new RequestFailedException();
        }
		
		if (ftresponse.getResponseStatus()) {
			gcResponse.addToData(DeviceConstants.MSG, ftresponse.getResponseObject().getStatus());
		} else
			throw new RequestFailedException();
	}
}
