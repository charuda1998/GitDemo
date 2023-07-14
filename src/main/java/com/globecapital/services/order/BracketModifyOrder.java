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

public class BracketModifyOrder extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest request, GCResponse response) throws Exception {

		GCAuditObject auditObj = request.getAuditObj();

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

		if (!sStrikePrice.isEmpty())
			ftrequest.setStrkPrc(sStrikePrice);
		else
			ftrequest.setStrkPrc("0");

		/*** Bracket order fields ***/
		ftrequest.setMsgCode(FTConstants.BRACKET_MSG_CODE);
		ftrequest.setModifyFlag(true);

		JSONObject oldOrderDetails = request.getObjectFromData(OrderConstants.MODIFY_ORDER_DETAILS);
		ftrequest.setBracketOrderId(oldOrderDetails.getString(OrderConstants.BRACKET_ORDER_ID));
		ftrequest.setBOSLOrderType(FTConstants.BRACKET_SL_LIMIT);

		String sNewTrailingSL = request.getFromData(OrderConstants.TRAILING_SL);
		String sNewPrice = request.getFromData(OrderConstants.PRICE);
		String sNewQty = request.getFromData(OrderConstants.ORDER_QTY);
		String sNewSLPrice = request.getFromData(OrderConstants.SL_PRICE);
		String sNewSLTrigPrice = request.getFromData(OrderConstants.SL_TRIG_PRICE);
		String sNewProfitPrice = request.getFromData(OrderConstants.PROFIT_PRICE);
		
		String sTrailingSL = oldOrderDetails.getString(OrderConstants.TRAILING_SL);
		String sPrice = oldOrderDetails.getString(OrderConstants.PRICE);
		String sQty = oldOrderDetails.getString(OrderConstants.ORDER_QTY);
		String sSLPrice = oldOrderDetails.getString(OrderConstants.SL_PRICE);
		String sSLTrigPrice = oldOrderDetails.getString(OrderConstants.SL_TRIG_PRICE);
		String sProfitPrice = oldOrderDetails.getString(OrderConstants.PROFIT_PRICE);
		
		boolean isTrailingSLZero = checkTrailingSLZero(sTrailingSL, sNewTrailingSL);
		boolean isTrailingSLEmpty = checkTrailingSLEmpty(sTrailingSL, sNewTrailingSL);
		
		boolean isChangeInTrailingSL = false, isSLValueModification = false,
				isProfitValueModification = false;

		boolean isMainValueModification = changeInValue(sPrice, sNewPrice) | changeInValue(sQty, sNewQty);
		isSLValueModification = changeInValue(sSLPrice, sNewSLPrice) 
											| changeInValue(sSLTrigPrice, sNewSLTrigPrice);
		isProfitValueModification = changeInValue(sProfitPrice, sNewProfitPrice);
		
		boolean isMainLegModification = Boolean.parseBoolean(oldOrderDetails.
				getString(OrderConstants.IS_MAIN_ORDER_MODIFIABLE));
		
		boolean isSLLegModification = Boolean.parseBoolean(oldOrderDetails.
				getString(OrderConstants.IS_SL_ORDER_MODIFIABLE));
		
		boolean isTargetLegModification = Boolean.parseBoolean(oldOrderDetails.
				getString(OrderConstants.IS_TARGET_ORDER_MODIFIABLE));
				
		if (isTrailingSLEmpty || isTrailingSLZero) {
			ftrequest.setSLJumpPrice("0");
			ftrequest.setLTPJumpPrice("0");
		} else
			isChangeInTrailingSL = changeInValue(sTrailingSL, sNewTrailingSL);

		SendOrdReqResponse ftresponse = new SendOrdReqResponse();
		
		QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());

		if (isMainLegModification) {
			
			ftrequest.setLegIndicator(FTConstants.LEG_INDICATOR_MAIN_NEW_MODIFICATION);
			
			JSONObject mainOrder = oldOrderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
			
			ftrequest.setExchOrdNo(mainOrder.getString((OrderConstants.EXCH_ORD_NO)));
			ftrequest.setOrdTime(mainOrder.getString(OrderConstants.ORDER_TIME));
			ftrequest.setGatewayOrdNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			ftrequest.setBOGatewayOrderNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			
			
			int iBOModifyTerms = 0;
			
			if (!(sPrice.equals(sNewPrice)) && !(sQty.equals(sNewQty)) ) {
				iBOModifyTerms = FTConstants.BO_MODIFY_TERMS_MAIN_LEG_PRICE + 
						FTConstants.BO_MODIFY_TERMS_MAIN_LEG_QTY;
				ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewPrice, iMultiplier, quoteDetails.sLTP));
				ftrequest.setOrgQty(sNewQty);

			} else {
				

				if (!(sPrice.equals(sNewPrice))) {
					iBOModifyTerms = FTConstants.BO_MODIFY_TERMS_MAIN_LEG_PRICE;
					ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
							sNewPrice, iMultiplier, quoteDetails.sLTP));
					ftrequest.setOrgQty(sQty);
				} else if (!(sQty.equals(sNewQty))) {
					iBOModifyTerms = FTConstants.BO_MODIFY_TERMS_MAIN_LEG_QTY;
					ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
							sPrice, iMultiplier, quoteDetails.sLTP));
					ftrequest.setOrgQty(sNewQty);
				}else
				{
					ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
							sPrice, iMultiplier, quoteDetails.sLTP));
					ftrequest.setOrgQty(sQty);
				}

			}
			
			if(isSLValueModification)
			{
				iBOModifyTerms = iBOModifyTerms + FTConstants.BO_MODIFY_TERMS_LEG_PRICE;
			if (!(sSLPrice.equals(sNewSLPrice))
					&& !(sSLTrigPrice.equals(sNewSLTrigPrice)) ) {

				ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLPrice, iMultiplier, quoteDetails.sLTP));
				
				ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLTrigPrice, iMultiplier, quoteDetails.sLTP));

			} else if (!(sSLPrice.equals(sNewSLPrice))) {
				
				ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLPrice, iMultiplier, quoteDetails.sLTP));
				
				ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLTrigPrice, iMultiplier, quoteDetails.sLTP));

			} else if (!(sSLTrigPrice.equals(sNewSLTrigPrice))) {
				
				ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLTrigPrice, iMultiplier, quoteDetails.sLTP));
				
				ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLPrice, iMultiplier, quoteDetails.sLTP));
			}
			}else
			{
				ftrequest.setSLOrderPrice( OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLPrice, iMultiplier, quoteDetails.sLTP) );
				ftrequest.setSLTriggerPrice( OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLTrigPrice, iMultiplier, quoteDetails.sLTP) ); 
			}
			
			
			if (isProfitValueModification)
			{
				iBOModifyTerms = iBOModifyTerms + FTConstants.BO_MODIFY_PROFIT_PRICE;
				ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewProfitPrice, iMultiplier, quoteDetails.sLTP));
			}
			else
			{
				ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sProfitPrice, iMultiplier, quoteDetails.sLTP));
			}

			if(isChangeInTrailingSL)
			{
				iBOModifyTerms = iBOModifyTerms + 
						FTConstants.BO_MODIFY_TERMS_JUMP_PRICE + FTConstants.BO_MODIFY_TRAILING_SL;
				ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
				
			}
			else
			{
				ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sTrailingSL, iMultiplier, quoteDetails.sLTP));
			}


			if(!isMainValueModification && !isSLValueModification && !isProfitValueModification && !isChangeInTrailingSL)
			{
				iBOModifyTerms = FTConstants.BO_MODIFY_TERMS_MAIN_LEG_PRICE + 
						FTConstants.BO_MODIFY_TERMS_MAIN_LEG_QTY + FTConstants.BO_MODIFY_TERMS_LEG_PRICE +
						FTConstants.BO_MODIFY_PROFIT_PRICE + FTConstants.BO_MODIFY_TERMS_JUMP_PRICE + 
						FTConstants.BO_MODIFY_TRAILING_SL;
			}
			ftrequest.setBOModifyTerms(iBOModifyTerms);

			SendOrdReqAPI api = new SendOrdReqAPI();
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
	            } else {
	                response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
	                response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
	                throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
	            }
			}
		}
		
		if(isSLLegModification && isSLValueModification && !isMainLegModification)
		{
			JSONObject slOrder = oldOrderDetails.getJSONObject(OrderConstants.SL_ORDER_MODIFY);
			JSONObject mainOrder = oldOrderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
			
			ftrequest.setExchOrdNo(slOrder.getString((OrderConstants.EXCH_ORD_NO)));
			ftrequest.setOrdTime(slOrder.getString(OrderConstants.ORDER_TIME));
			ftrequest.setGatewayOrdNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			ftrequest.setBOGatewayOrderNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			
			int iBOModifyTerms =  0;
			
			ftrequest.setLegIndicator(FTConstants.LEG_INDICATOR_MAIN_NEW_MODIFICATION);
			if(isTargetLegModification && isProfitValueModification) {
				ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sNewProfitPrice, iMultiplier, quoteDetails.sLTP)); 
				iBOModifyTerms = FTConstants.BO_MODIFY_PROFIT_PRICE;
			}
			else 
				ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sProfitPrice, iMultiplier, quoteDetails.sLTP));
			ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sPrice, iMultiplier, quoteDetails.sLTP));
			ftrequest.setOrgQty(sQty);
			iBOModifyTerms = iBOModifyTerms + FTConstants.BO_MODIFY_TERMS_LEG_PRICE;
			
			if(isSLValueModification)
			{
				
			if (!(sSLPrice.equals(sNewSLPrice))
					&& !(sSLTrigPrice.equals(sNewSLTrigPrice)) ) {

				ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLPrice, iMultiplier, quoteDetails.sLTP));
				
				ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLTrigPrice, iMultiplier, quoteDetails.sLTP));

			} else if (!(sSLPrice.equals(sNewSLPrice))) {
				ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLPrice, iMultiplier, quoteDetails.sLTP));
				
				ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLTrigPrice, iMultiplier, quoteDetails.sLTP));

			} else if (!(sSLTrigPrice.equals(sNewSLTrigPrice))) {
				
				ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewSLTrigPrice, iMultiplier, quoteDetails.sLTP));
				
				ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLPrice, iMultiplier, quoteDetails.sLTP));
			}
			}else
			{
				ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLPrice, iMultiplier, quoteDetails.sLTP)); 
				ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sSLTrigPrice, iMultiplier, quoteDetails.sLTP)); 
			}
			
			if(isChangeInTrailingSL)
			{
				iBOModifyTerms = iBOModifyTerms + 
						FTConstants.BO_MODIFY_TERMS_JUMP_PRICE;
				ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setBOModifyTerms(iBOModifyTerms);				
			}else
			{
				ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sTrailingSL, iMultiplier, quoteDetails.sLTP));
			}

			ftrequest.setBOModifyTerms(iBOModifyTerms);

			
			SendOrdReqAPI api = new SendOrdReqAPI();
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
                } else {
                    response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                    response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                    throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
                }
			}
		}

		if(isTargetLegModification && isProfitValueModification && !isMainLegModification && !isSLValueModification)
		{
			
			JSONObject targetOrder = oldOrderDetails.getJSONObject(OrderConstants.TARGET_ORDER_MODIFY);
			JSONObject mainOrder = oldOrderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
			
			ftrequest.setExchOrdNo(targetOrder.getString((OrderConstants.EXCH_ORD_NO)));
			ftrequest.setOrdTime(targetOrder.getString(OrderConstants.ORDER_TIME));
			ftrequest.setGatewayOrdNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			ftrequest.setBOGatewayOrderNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			
			ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sPrice, iMultiplier, quoteDetails.sLTP));
			ftrequest.setOrgQty(sQty);
			ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sSLPrice, iMultiplier, quoteDetails.sLTP)); 
			ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sSLTrigPrice, iMultiplier, quoteDetails.sLTP)); 
			
			int iBOModifyTerms = FTConstants.BO_MODIFY_PROFIT_PRICE;
			ftrequest.setBOModifyTerms(iBOModifyTerms);
			ftrequest.setLegIndicator(FTConstants.LEG_INDICATOR_MAIN_NEW_MODIFICATION);
			
			ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sNewProfitPrice, iMultiplier, quoteDetails.sLTP));
			
			if(isChangeInTrailingSL)
			{
				iBOModifyTerms = iBOModifyTerms + 
						FTConstants.BO_MODIFY_TERMS_JUMP_PRICE ;
				ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setBOModifyTerms(iBOModifyTerms);
				
			}else
			{
				ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sTrailingSL, iMultiplier, quoteDetails.sLTP));
				ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
						sTrailingSL, iMultiplier, quoteDetails.sLTP));
			}
			
				
			SendOrdReqAPI api = new SendOrdReqAPI();
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
                } else {
                    response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                    response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                    throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
                }
			}
				
		}
		
		if(!isSLValueModification && !isProfitValueModification && !isMainValueModification && !isMainLegModification && isChangeInTrailingSL)
		{
			int iBOModifyTerms = FTConstants.BO_MODIFY_TERMS_JUMP_PRICE ;
			JSONObject slOrder = oldOrderDetails.getJSONObject(OrderConstants.SL_ORDER_MODIFY);
			JSONObject mainOrder = oldOrderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
			
			ftrequest.setExchOrdNo(slOrder.getString((OrderConstants.EXCH_ORD_NO)));
			ftrequest.setOrdTime(slOrder.getString(OrderConstants.ORDER_TIME));
			ftrequest.setGatewayOrdNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			ftrequest.setBOGatewayOrderNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));

			ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sPrice, iMultiplier, quoteDetails.sLTP));
			ftrequest.setOrgQty(sQty);
			ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sSLPrice, iMultiplier, quoteDetails.sLTP)); 
			ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sSLTrigPrice, iMultiplier, quoteDetails.sLTP)); 
			ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
			sProfitPrice, iMultiplier, quoteDetails.sLTP));


			ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
			ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sNewTrailingSL, iMultiplier, quoteDetails.sLTP));
			ftrequest.setBOModifyTerms(iBOModifyTerms);
			ftrequest.setLegIndicator(FTConstants.LEG_INDICATOR_MAIN_NEW_MODIFICATION);
			SendOrdReqAPI api = new SendOrdReqAPI();
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
                } else {
                    response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                    response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                    throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
                }
			}

		}
		if(!isSLValueModification && !isProfitValueModification && !isMainValueModification && !isMainLegModification && !isChangeInTrailingSL)
		{
			int iBOModifyTerms = FTConstants.BO_MODIFY_TERMS_JUMP_PRICE ;
			JSONObject slOrder = oldOrderDetails.getJSONObject(OrderConstants.SL_ORDER_MODIFY);
			JSONObject mainOrder = oldOrderDetails.getJSONObject(OrderConstants.MAIN_ORDER_MODIFY);
			
			ftrequest.setExchOrdNo(slOrder.getString((OrderConstants.EXCH_ORD_NO)));
			ftrequest.setOrdTime(slOrder.getString(OrderConstants.ORDER_TIME));
			ftrequest.setGatewayOrdNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));
			ftrequest.setBOGatewayOrderNo(mainOrder.getString(OrderConstants.GATEWAY_ORD_NO));

			ftrequest.setOrdPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sPrice, iMultiplier, quoteDetails.sLTP));
			ftrequest.setOrgQty(sQty);
			ftrequest.setSLOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sSLPrice, iMultiplier, quoteDetails.sLTP));
			ftrequest.setSLTriggerPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sSLTrigPrice, iMultiplier, quoteDetails.sLTP)); 
			ftrequest.setProfitOrderPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sProfitPrice, iMultiplier, quoteDetails.sLTP));
			
			ftrequest.setSLJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sTrailingSL, iMultiplier, quoteDetails.sLTP));
			ftrequest.setLTPJumpPrice(OrderPrice.formatOrderPriceToAPI(OrderType.REGULAR_LOT_LIMIT2, 
					sTrailingSL, iMultiplier, quoteDetails.sLTP));
			
			ftrequest.setBOModifyTerms(iBOModifyTerms);
			ftrequest.setLegIndicator(FTConstants.LEG_INDICATOR_MAIN_NEW_MODIFICATION);
			SendOrdReqAPI api = new SendOrdReqAPI();
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
                } else {
                    response.addToData(DeviceConstants.ACTION, DeviceConstants.ACTION_REJECT);
                    response.addToData(DeviceConstants.STATUS, DeviceConstants.STATUS_REJECT);
                    throw new GCException (InfoIDConstants.SUCCESS, e.getMessage());
                }
			}
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

	private boolean changeInValue(String sValue, String sNewValue) {
		if (!sValue.equals(sNewValue))
			return true;
		return false;
	}

	private boolean checkTrailingSLZero(String sTrailingSL, String sNewTrailingSL) {

		if ((sTrailingSL.equals("0") || sTrailingSL.equals("0.00")) && 
				(sNewTrailingSL.equals("0") || sNewTrailingSL.equals("0.00")))
			return true;
		return false;
	}
	
	private boolean checkTrailingSLEmpty(String sTrailingSL, String sNewTrailingSL)
	{
		if(sNewTrailingSL.isEmpty() &&  (sTrailingSL.equals("0") || sTrailingSL.equals("0.00")))
			return true;
		return false;
	}

}

