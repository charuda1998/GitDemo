package com.globecapital.services.order;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.margin.MarginInfoAPI;
import com.globecapital.api.ft.margin.MarginInfoRequest;
import com.globecapital.api.ft.margin.MarginInfoResponse;
import com.globecapital.api.ft.margin.MarginInfoResponseObject;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
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
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidRequestKeyException;
import com.globecapital.services.exception.InvalidSession;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class BrokerageInfo extends SessionService {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(BrokerageInfo.class);

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		try {
			MarginInfoRequest marginReq = new MarginInfoRequest();
			String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			QuoteDetails quoteDetails = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
			String sOrderSide;
			Session session = gcRequest.getSession();
			String sOrderType = gcRequest.getOptFromData(OrderConstants.ORDER_TYPE, OrderType.REGULAR_LOT_LIMIT);
			marginReq.setJKey(session.getjKey());
			marginReq.setJSession(session.getjSessionID());
			marginReq.setUserID(session.getUserID());
			marginReq.setFTUserId(session.getUserID());
			marginReq.setGroupId(session.getGroupId());
			marginReq.setNoOfLegs(1);
			marginReq.setFETraceId("");
			marginReq.setLegNo(1);
			sOrderSide = gcRequest.getFromData(OrderConstants.ORDER_ACTION);
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
			marginReq.setQuantity(gcRequest.getFromData(OrderConstants.QTY));
            String price = PriceFormat.formatPriceToPaise(gcRequest.getFromData(OrderConstants.PRICE).replace(",", ""), symRow.getMultiplier());
            marginReq.setPrice(price);
			if(sOrderType.equalsIgnoreCase(OrderType.REGULAR_LOT_MARKET) || sOrderType.equalsIgnoreCase(OrderType.SL_MKT))
				marginReq.setMktFlag(1);
			else
				marginReq.setMktFlag(0);
			
			marginReq.setProductType(ProductType.formatToMarginInfoAPI(gcRequest.getFromData(OrderConstants.PRODUCT_TYPE),false));
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
			if(marginResp.getResponseStatus()) {
    			MarginInfoResponseObject resp= marginResp.getResponseObject();
    			BrokerageInfo.getBrokerageUrl(gcRequest, gcResponse, resp.getBrokerage());
			} else 
			    throw new GCException(InfoIDConstants.NO_DATA, marginResp.getErrorMsg().get(0));
		}catch(GCException e) { 
		    if(e.getMessage().equalsIgnoreCase("Quantity is not in correct format"))
                throw new GCException(InfoIDConstants.NO_DATA, "Please enter a valid Quantity");
		    else
		        throw new GCException(InfoIDConstants.NO_DATA, e.getMessage());	        
		}
		catch(Exception ex) {
			gcResponse.setNoDataAvailable();
			log.error(ex);
		}
	}

	public static void getBrokerageUrl(GCRequest gcRequest, GCResponse gcResponse, String brokerage) throws InvalidSession,
			InvalidRequestKeyException, Exception, UnsupportedEncodingException, AppConfigNoKeyFoundException {
		Session session = gcRequest.getSession();
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		String sOrderSide = gcRequest.getFromData(OrderConstants.ORDER_ACTION);
		String qty = gcRequest.getFromData(OrderConstants.QTY);
		String price = gcRequest.getFromData(OrderConstants.PRICE);
		String productType = gcRequest.getFromData(OrderConstants.PRODUCT_TYPE);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put(FTConstants.PRODUCT, FTConstants.PRODUCT_WAVE);
		parameters.put(FTConstants.THEME, FTConstants.THEME_D);
		parameters.put(FTConstants.USER_ID, session.getUserID());
		parameters.put(FTConstants.GROUP_ID, session.getGroupId());
		parameters.put(FTConstants.MARKET_SEGMENT_ID, symRow.getMktSegId());
		if(symRow.getSeries().isEmpty())
		    parameters.put(FTConstants.SERIES, "XX");
		else
		    parameters.put(FTConstants.SERIES, symRow.getSeries());
		parameters.put(FTConstants.PRODUCT_TYPE, ProductType.formatToDisplayForBrokerage(productType, symRow.getMktSegId()));
		parameters.put(FTConstants.TRANSACTION_TYPE, String.valueOf(OrderAction.formatToAPI(sOrderSide)));
		parameters.put(FTConstants.QUANTITY, qty);
		parameters.put(FTConstants.PRICE, price);
		parameters.put(FTConstants.BROKERAGE, brokerage);
		parameters.put(FTConstants.LEG_INDICATOR, "1");
		parameters.put(FTConstants.SESSION_ID, session.getjSessionIDWithoutEncryption());	
		if(ExchangeSegment.isEquitySegment(symRow.getMktSegId()))
			parameters.put(FTConstants.INSTRUMENT, "EQUITIES");
		else 
			parameters.put(FTConstants.INSTRUMENT, symRow.getInstrument());
		
		String completeUrl = getParamsString(parameters);
		String params = new String(Base64.getEncoder().encode(completeUrl.getBytes()));
		String url = AppConfig.getValue("ft.api.brokerage_url") + params;
		
		log.info("Brokerage URL without encrypt : "+completeUrl);
		log.info("Brokerage URL with encrypt : "+url);
		
		gcResponse.addToData(DeviceConstants.URL, url);
	}
	
    public static String getParamsString(LinkedHashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
    
    public static void main(String[] args) {
		String s = "UHJvZHVjdD1BRVJPJlRoZW1lPUwmVXNlcklkPU1TMSZHcm91cElkPUhPJk1hcmtldFNlZ21lbnRJZD0xJlNlcmllcz1FUSZQcm9kdWN0VHlwZT1NQVJHSU4mVHJhbnNhY3Rpb25UeXBlPTEmUXVhbnRpdHk9MSZQcmljZT0yNDIyLjUwJkJyb2tlcmFnZT0wLjI0JkxlZ0luZGljYXRvcj0xJlNlc3Npb25JZD0weDAxNDk1OEQ1NEZBRURGQkY4RDM2NDc3NjcyMERGOCZJbnN0cnVtZW50PUVRVUlUSUVTJlByaWNlTG9jYXRvcj0x";
		System.out.println(new String(Base64.getDecoder().decode(s.getBytes())));
	}
}
