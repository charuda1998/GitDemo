package com.globecapital.services.order;


import org.json.JSONObject;

import com.globecapital.api.ft.order.SendOrdReqAPI;
import com.globecapital.api.ft.order.SendOrdReqResponse;
import com.globecapital.api.ft.order.SendOrdRequest;
import com.globecapital.config.InfoMessage;
import com.globecapital.audit.GCAuditObject;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.AppConstants;
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
import com.globecapital.services.session.SessionHelper;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;

public class NewOrder extends SessionService {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		GCAuditObject auditObj = gcRequest.getAuditObj();
		
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
		ftrequest.setMktLot(symRow.getLotSize());
		ftrequest.setPrcTick(symRow.getTickPrice());
		
		
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
		
		ftrequest.setClientOrdNo( SessionHelper.updateClientOrderNo(sUserID) );
		
		
		if (!SessionHelper.validateDuplicateOrder(session.getSessionID(), gcRequest.getData().toString())) {

			gcResponse.setInfoID(InfoIDConstants.DUPLICATE_ORDER);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.duplicate_order"));

			return;
		}
		
		SendOrdReqAPI api = new SendOrdReqAPI();

		SendOrdReqResponse ftresponse = null;
		try {
			ftresponse = api.post(ftrequest, SendOrdReqResponse.class, session.getAppID(),"SendOrderRequest");
		}catch(GCException ex) {
            if(ex.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(ftrequest,session, getServletContext(), gcRequest, gcResponse)) {
                    session = gcRequest.getSession();
                    gcResponse.addToData(DeviceConstants.MSG, ex.getMessage());
                    throw new GCException (InfoIDConstants.SUCCESS, InfoMessage.getInfoMSG("info_msg.timeout.order_session_failed"));
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            }    
            else {
                gcResponse.addToData(DeviceConstants.MSG, ex.getMessage());
                throw new GCException (InfoIDConstants.SUCCESS, ex.getMessage());
            }
		}
		
		if (ftresponse.getResponseStatus()) {
			gcResponse.addToData(DeviceConstants.MSG, ftresponse.getResponseObject().getStatus());			
			
			auditObj.setAuditInfo(session);
			//TO-DO : audit for failure cases
			
		}
		else
			throw new RequestFailedException();
		
	}
	
	

}
