package com.globecapital.services.order;

import java.util.List;

import org.json.JSONArray;

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
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class OrderBook_101 extends SessionService {

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
		
		/*** GetOrderBook API ***/
		GetOrderBookRequest orderbookReq = new GetOrderBookRequest();
		orderbookReq.setUserID(sUserID);
		orderbookReq.setGroupId(sGroupID);
		orderbookReq.setJKey(session.getjKey());
		orderbookReq.setJSession(session.getjSessionID());
		
				
		GetOrderBookAPI orderbookAPI = new GetOrderBookAPI();
		GetOrderBookResponse orderbookResponse = orderbookAPI.post(orderbookReq,
				GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
		
		GetOrderBookResponseObject orderObj = orderbookResponse.getResponseObject();
		List<GetOrderBookObjectRow> orderRows = orderObj.getObjJSONRows();
		
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
		
		JSONArray orderList = new JSONArray();
		orderList = com.globecapital.business.order.OrderBook_101.getOrderBook(orderRows, tradeRows, sOrderStatus);
		
		if (orderList.length() > 0)
			gcResponse.addToData(OrderConstants.ORDERS, orderList);
		else
			gcResponse.setNoDataAvailable();
		
	}
}
