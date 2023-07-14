package com.globecapital.services.order;

import org.json.JSONObject;

import com.globecapital.api.ft.margin.MarginInfoAPI;
import com.globecapital.api.ft.margin.MarginInfoRequest;
import com.globecapital.api.ft.margin.MarginInfoResponse;
import com.globecapital.api.ft.margin.MarginInfoResponseObject;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderPrice;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidRequestKeyException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.globecapital.utils.PriceFormat;

public class MarginInfo extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		try {
			MarginInfoRequest marginReq = new MarginInfoRequest();
			String sOrderSide;
			Session session = gcRequest.getSession();
			String sOrderType = gcRequest.getFromData(OrderConstants.ORDER_TYPE);
			marginReq.setJKey(session.getjKey());
			marginReq.setJSession(session.getjSessionID());
			marginReq.setUserID(session.getUserID());
			marginReq.setFTUserId(session.getUserID());
			marginReq.setGroupId(session.getGroupId());
			marginReq.setNoOfLegs(1);
			marginReq.setFETraceId("");
			marginReq.setLegNo(1);
			String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
			sOrderSide = gcRequest.getFromData(OrderConstants.ORDER_ACTION);
			sOrderType = gcRequest.getFromData(OrderConstants.ORDER_TYPE);
			marginReq.setBuyOrSell(OrderAction.formatToAPI(sOrderSide));
	      	marginReq.setMarketSegmentId(symRow.getMktSegId());	
			marginReq.setToken(symRow.gettokenId());
//			if(gcRequest.getFromData(OrderConstants.OLD_QUANTITY).isEmpty() && gcRequest.getFromData(OrderConstants.OLD_PRICE).isEmpty()) {
			marginReq.setMode("N");
			marginReq.setOldPrice(OrderPrice.formatOrderPriceToAPI(sOrderType,"0",symRow.getMultiplier(), quoteDetails.sLTP));
			marginReq.setOldQuantity("0");
//			}
//			else{
//				marginReq.setMode("M");
//				marginReq.setOldPrice(PriceFormat.formatPriceToPaise(gcRequest.getFromData(OrderConstants.OLD_PRICE),symRow.getMultiplier()));
//				log.info(symRow.getMultiplier());
//				marginReq.setOldQuantity(gcRequest.getFromData(OrderConstants.OLD_QUANTITY));
//			}
			if(symRow.getMktSegId().equals(ExchangeSegment.MCX_SEGMENT_ID))
			    marginReq.setQuantity(String.valueOf(Integer.parseInt(gcRequest.getFromData(OrderConstants.ORDER_QTY))/symRow.getLotSizeInt()));
			else
			    marginReq.setQuantity(gcRequest.getFromData(OrderConstants.ORDER_QTY));
		    String price = PriceFormat.formatPriceToPaise(gcRequest.getFromData(OrderConstants.PRICE).replace(",", ""), symRow.getMultiplier());
		    marginReq.setPrice(price);
			if(sOrderType.equalsIgnoreCase(OrderType.REGULAR_LOT_MARKET) || sOrderType.equalsIgnoreCase(OrderType.SL_MKT))
				marginReq.setMktFlag(1);
			else
				marginReq.setMktFlag(0);
			
			try {
			    marginReq.setProductType(ProductType.formatToMarginInfoAPI(gcRequest.getFromData(OrderConstants.PRODUCT_TYPE),false));
			} catch(InvalidRequestKeyException e) {
			    JSONObject productType = gcRequest.getObjectFromData(OrderConstants.PRODUCT_TYPE);
			    marginReq.setProductType(ProductType.formatToMarginInfoAPI(productType.getString(OrderConstants.PRODUCT_TYPE),false));
			}
			marginReq.setLegIndicator(0);
			marginReq.setLegDetails();
			
			MarginInfoAPI marginAPI = new MarginInfoAPI();
			MarginInfoResponse marginResp=new MarginInfoResponse();
			try {
			marginResp = marginAPI.post(marginReq, MarginInfoResponse.class, session.getAppID(),"GetMarginInfo");
			}
			catch(GCException e) {
				if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(marginReq,session, getServletContext(), gcRequest, gcResponse)) {
        				marginResp = marginAPI.post(marginReq, MarginInfoResponse.class, session.getAppID(),"GetMarginInfo");
                        session = gcRequest.getSession();
                    }
                    else 
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                } 
                else 
    				throw new RequestFailedException();
			}
			MarginInfoResponseObject resp= marginResp.getResponseObject();
			String amount = PriceFormat.rupeeFormat(resp.getApproxMargin());
			amount = formatPriceToRupee(amount);
			gcResponse.addToData(OrderConstants.REQUIRED_MARGIN, amount);
			gcResponse.addToData(OrderConstants.AVAILABLE_MARGIN, formatPriceToRupee(PriceFormat.rupeeFormat(resp.getAvailableMargin())));
//			BrokerageInfo.getBrokerageUrl(gcRequest, gcResponse, resp.getBrokerage());
		}
		catch(Exception e) {
			gcResponse.setNoDataAvailable();
			log.error(e);
		}
	}

	private String formatPriceToRupee(String amount) {
		if(amount.contains(".")) {
			String splitdata[]=amount.split("\\.");
			String amt = splitdata[0];
			String sign = "";
			if(splitdata[0].contains("-")) {
				amt = splitdata[0].split("-")[1];
				sign = "-";
			}
			
			String left = sign + PriceFormat.rupeeFormat(amt);
			amount = left+"."+splitdata[1];
		}
		return amount;
	}

}
