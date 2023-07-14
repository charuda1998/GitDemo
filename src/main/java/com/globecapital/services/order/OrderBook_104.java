package com.globecapital.services.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.ft.order.GetGTDOrderBookAPI;
import com.globecapital.api.ft.order.GetGTDOrderBookObjectRow;
import com.globecapital.api.ft.order.GetGTDOrderBookRequest;
import com.globecapital.api.ft.order.GetGTDOrderBookResponse;
import com.globecapital.api.ft.order.GetGTDOrderBookResponseObject;
import com.globecapital.api.ft.order.GetOrderBookAPI;
import com.globecapital.api.ft.order.GetOrderBookObjectRow;
import com.globecapital.api.ft.order.GetOrderBookRequest;
import com.globecapital.api.ft.order.GetOrderBookResponse;
import com.globecapital.api.ft.order.GetOrderBookResponseObject;
import com.globecapital.api.ft.order.GetTradeBookAPI;
import com.globecapital.api.ft.order.GetTradeBookObjectRow;
import com.globecapital.api.ft.order.GetTradeBookRequest;
import com.globecapital.api.ft.order.GetTradeBookResponse;
import com.globecapital.api.ft.order.GetTradeBookResponseObject;
import com.globecapital.business.order.GTDOrderBook;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;

public class OrderBook_104 extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception
	 {
		Session session = gcRequest.getSession();
		
		String sGroupID = session.getGroupId();
		String sUserID = session.getUserID();
		String sOrderStatus = gcRequest.getFromData(OrderConstants.STATUS);
		GetGTDOrderBookResponseObject gtdOrderObj = new GetGTDOrderBookResponseObject();
		List<GetOrderBookObjectRow> orderRows = new ArrayList<>();
		
		if(sOrderStatus.equalsIgnoreCase("gtc")) {
		    JSONObject gtdOrders = new JSONObject();
			if (!session.getUserInfo().getJSONArray(UserInfoConstants.ALLOWED_GTD_EXCH).isEmpty()) {
			    GTDOrderBook gtdOrderBook = null;
			    GetGTDOrderBookResponse gtdOrderBookResponse = new GetGTDOrderBookResponse();
				/*** GTDOrderBook API ***/
				GetGTDOrderBookRequest gtdOrderBookReq = new GetGTDOrderBookRequest();
				gtdOrderBookReq.setUserID(sUserID);
				gtdOrderBookReq.setGroupId(sGroupID);
				gtdOrderBookReq.setJKey(session.getjKey());
				gtdOrderBookReq.setJSession(session.getjSessionID());
				GetGTDOrderBookAPI gtdOrderBookAPI = new GetGTDOrderBookAPI();
				try {
    				gtdOrderBookResponse = gtdOrderBookAPI.post(gtdOrderBookReq, GetGTDOrderBookResponse.class, session.getAppID(),"GetGTDOrderBook");
				}catch(GCException e) {
				    log.debug(e);
				    if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
		                    if(GCUtils.reInitiateLogIn(gtdOrderBookReq,session, getServletContext(), gcRequest, gcResponse)) {
		                        gtdOrderBookResponse = gtdOrderBookAPI.post(gtdOrderBookReq, GetGTDOrderBookResponse.class, session.getAppID(),"GetGTDOrderBook");
		                        session = gcRequest.getSession();
		                    } else {
		                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
		                    }
		            } else
		                throw new RequestFailedException();
                }
				if((gtdOrderBookResponse.getResponseStatus()==null) ? false : gtdOrderBookResponse.getResponseStatus()) {
    				gtdOrderObj = gtdOrderBookResponse.getResponseObject();
    				gtdOrderBook = new GTDOrderBook(gtdOrderObj.getObjJSONRows(), session);
    				Map<String, Collection<GetGTDOrderBookObjectRow>> gtdOrderBookRow = gtdOrderBook.getGTDPendingSummary();
    				gtdOrders = gtdOrderBook.getGTDPendingOrder(session);
    				JSONArray orderList =gtdOrders.getJSONArray(OrderConstants.ORDERS);
    				if (orderList.length() > 0)
    					gcResponse.setData(gtdOrders);
    				else if(gtdOrders.length() > 1) {
    				    gcResponse.setData(gtdOrders);
    	                gcResponse.setNoDataAvailable();
    				}
    				else
    					gcResponse.setNoDataAvailable();
			    }else 
			        getOrderBookCount(session,gtdOrders,gcResponse);
			}else
			    getOrderBookCount(session,gtdOrders,gcResponse);
		    
		}else {
			/*** GetOrderBook API ***/
			GetOrderBookRequest orderbookReq = new GetOrderBookRequest();
			orderbookReq.setUserID(sUserID);
			orderbookReq.setGroupId(sGroupID);
			orderbookReq.setJKey(session.getjKey());
			orderbookReq.setJSession(session.getjSessionID());
			GetOrderBookResponse orderbookResponse = new GetOrderBookResponse();
            GetOrderBookAPI orderbookAPI = new GetOrderBookAPI();
			try {
			    log.info(orderbookReq.toString());			    
	            orderbookResponse = orderbookAPI.post(orderbookReq,GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
	        }catch(GCException e) {
                log.debug(e);
                if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(orderbookReq,session, getServletContext(), gcRequest, gcResponse)) {
                        orderbookResponse = orderbookAPI.post(orderbookReq,GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
                        session = gcRequest.getSession();
                    }
                    else 
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                } else
                    throw new RequestFailedException();
            }
			GetOrderBookResponseObject orderObj = orderbookResponse.getResponseObject();
			orderRows = orderObj.getObjJSONRows();
			
			if(orderRows.size() == 0) // If there is not record in GetOrderBook API, then return
			{
				gcResponse.setNoDataAvailable();
				return;
			}
		
			/*** GetTradeBook API ***/
			GetTradeBookRequest tradebookReq = new GetTradeBookRequest();
			tradebookReq.setUserID(sUserID);
			tradebookReq.setGroupId(sGroupID);
			tradebookReq.setJKey(session.getjKey());
			tradebookReq.setJSession(session.getjSessionID());
			
			GetTradeBookAPI tradebookAPI = new GetTradeBookAPI();
			GetTradeBookResponse tradebookResponse = tradebookAPI.post(tradebookReq,
					GetTradeBookResponse.class, session.getAppID(),"GetTradeBook");
			GetTradeBookResponseObject tradeObj = tradebookResponse.getResponseObject();
			List<GetTradeBookObjectRow> tradeRows = tradeObj.getObjJSONRows();
			if (!session.getUserInfo().getJSONArray(UserInfoConstants.ALLOWED_GTD_EXCH).isEmpty()) {
				try {
					/*** GTD OrderBook ***/
					GetGTDOrderBookRequest gtdOrderBookReq = new GetGTDOrderBookRequest();
					gtdOrderBookReq.setUserID(sUserID);
					gtdOrderBookReq.setGroupId(sGroupID);
					gtdOrderBookReq.setJKey(session.getjKey());
					gtdOrderBookReq.setJSession(session.getjSessionID());
					
					GetGTDOrderBookAPI gtdOrderBookAPI = new GetGTDOrderBookAPI();
					GetGTDOrderBookResponse gtdOrderBookResponse = gtdOrderBookAPI.post(gtdOrderBookReq,
							GetGTDOrderBookResponse.class, session.getAppID(),"GetGTDOrderBook");
					gtdOrderObj = gtdOrderBookResponse.getResponseObject();
				}catch(Exception e) {
					log.error(e);
				}
			}
				
			JSONObject orderBookObj = new JSONObject();
			orderBookObj = com.globecapital.business.order.OrderBook_104.getOrderBook(orderRows, tradeRows, sOrderStatus, gtdOrderObj.getObjJSONRows());
			JSONArray orderList = orderBookObj.getJSONArray(OrderConstants.ORDERS);
			if (orderList.length() > 0)
				gcResponse.setData(orderBookObj);
			else if (orderBookObj.length() > 1) {
			    gcResponse.setData(orderBookObj);
			    gcResponse.setNoDataAvailable();
			}
			else
				gcResponse.setNoDataAvailable();
		}
	
		/*** GetTradeBook API ***/
	/*	GetTradeBookRequest tradebookReq = new GetTradeBookRequest();
		tradebookReq.setUserID(sUserID);
		tradebookReq.setGroupId(sGroupID);
		tradebookReq.setJKey(session.getjKey());
		tradebookReq.setJSession(session.getjSessionID());
		
		GetTradeBookAPI tradebookAPI = new GetTradeBookAPI();
		GetTradeBookResponse tradebookResponse = tradebookAPI.post(tradebookReq,
				GetTradeBookResponse.class, session.getAppID(),"GetTradeBook");
		GetTradeBookResponseObject tradeObj = tradebookResponse.getResponseObject();
		List<GetTradeBookObjectRow> tradeRows = tradeObj.getObjJSONRows();
		JSONObject orderBookObj = new JSONObject();
		orderBookObj = com.globecapital.business.order.OrderBook_103.getOrderBook(orderRows, tradeRows, sOrderStatus, null);
		JSONArray orderList = orderBookObj.getJSONArray(OrderConstants.ORDERS);
		if (orderList.length() > 0)
			gcResponse.setData(orderBookObj);
		else if (orderBookObj.length() > 1) {
		    gcResponse.setData(orderBookObj);
		    gcResponse.setNoDataAvailable();
		}
		else
			gcResponse.setNoDataAvailable();*/
	}
	
	private static void getOrderBookCount(Session session, JSONObject gtdOrders, GCResponse gcResponse) {
	    try {
            GTDOrderBook.getOrderBookDetail(session,gtdOrders);
        }catch(NullPointerException e) {
            gcResponse.setNoDataAvailable();
        }catch(Exception e) {
            gcResponse.setNoDataAvailable();
        }
        if(!gtdOrders.isEmpty()) {
            gcResponse.setData(gtdOrders);
            gcResponse.setNoDataAvailable();
        }else
            gcResponse.setNoDataAvailable();
	}
}
