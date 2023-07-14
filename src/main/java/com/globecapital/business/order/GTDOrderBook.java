package com.globecapital.business.order;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
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
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;
import com.msf.sbu2.service.exception.NoDataAvailableException;

public class GTDOrderBook {

	private static Logger log = Logger.getLogger(GTDOrderBook.class);
	JSONArray gtdPendingOrder = new JSONArray();
	JSONObject orderBookObj = new JSONObject();
	
	static Map<String, Collection<GetGTDOrderBookObjectRow>> sPendingOrderMap = new HashMap<>();
	Map<String, Collection<GetGTDOrderBookObjectRow>> pendingOrderMap = new HashMap<>();
	Map<String, Collection<GetGTDOrderBookObjectRow>> executedOrderMap = new HashMap<>();
    Map<String, Collection<GetGTDOrderBookObjectRow>> cancelledOrderMap = new HashMap<>();
	Map<String, Collection<GetGTDOrderBookObjectRow>> orderMap = new HashMap<>();
	LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();
	
	public GTDOrderBook (List<GetGTDOrderBookObjectRow> gtdOrderBook) throws Exception {
		listToMap(gtdOrderBook);
	}
	
	public GTDOrderBook (List<GetGTDOrderBookObjectRow> gtdOrderBook, Session session) throws Exception {
		listToMap(gtdOrderBook);
		getOrderBookDetails( session, orderBookObj );
		setOrderTimeInGTDOrder(gtdPendingOrder);
	}

	public List<GetGTDOrderBookObjectRow> getGTDOrder(Session session, ServletContext servletContext,
            GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		GetGTDOrderBookRequest gtdOrderBookReq = new GetGTDOrderBookRequest();
		gtdOrderBookReq.setUserID(session.getUserID());
		gtdOrderBookReq.setGroupId(session.getGroupId());
		gtdOrderBookReq.setJKey(session.getjKey());
		gtdOrderBookReq.setJSession(session.getjSessionID());
		
		GetGTDOrderBookAPI gtdOrderBookAPI = new GetGTDOrderBookAPI();
		GetGTDOrderBookResponse gtdOrderBookResponse = new GetGTDOrderBookResponse();
		try {
		    gtdOrderBookResponse = gtdOrderBookAPI.post(gtdOrderBookReq,
				GetGTDOrderBookResponse.class, session.getAppID(),"GetGTDOrderBook");
    	}catch(GCException e) {
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(gtdOrderBookReq,session, servletContext, gcRequest, gcResponse)) {
                        gtdOrderBookResponse = gtdOrderBookAPI.post(gtdOrderBookReq,
                                GetGTDOrderBookResponse.class, session.getAppID(),"GetGTDOrderBook");
                        session = gcRequest.getSession();
                    } else {
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                    }
            }else 
                throw new RequestFailedException();
        }
		GetGTDOrderBookResponseObject gtdOrderObj = gtdOrderBookResponse.getResponseObject();
		List<GetGTDOrderBookObjectRow> gtdOrderRows = gtdOrderObj.getObjJSONRows();
		Map<String, Collection<GetGTDOrderBookObjectRow>> gtd = listToMap(gtdOrderRows);
		return gtdOrderRows;
	}
	
	private Map<String, Collection<GetGTDOrderBookObjectRow>> listToMap(List<GetGTDOrderBookObjectRow> lt) throws Exception {
		if(lt!=null) {
			for (GetGTDOrderBookObjectRow obj : lt) {
				
				String sStatus = OrderStatus.getGTDStatus(obj.getGTDOrderStatus());
				try {
					SymbolRow order = new SymbolRow();
					/*** To fetch quote details and symbol object ***/
					String sScripCode = obj.getScripCode();
					String sMktSegID = ExchangeSegment.getMarketSegmentID(obj.getExchange());
					String sTokenMktSegID = sScripCode + "_" + sMktSegID;
					//linkedsetSymbolToken.add(sTokenMktSegID);
					order.extend(SymbolMap.getSymbolRow(sScripCode, sMktSegID).getMinimisedSymbolRow());
					getOrderDetails(obj,order);
					/* ***************/
					String sKey = obj.getGatewayOrderNumber();
					List<GetGTDOrderBookObjectRow> list = new ArrayList<>();
					list.add(obj);
					orderMap.put(sKey, list);
				}catch(Exception e) {
					log.debug(e);
				}
				
				switch(sStatus) {
					case OrderConstants.PENDING:
						try {
							SymbolRow order = new SymbolRow();
							/*** To fetch quote details and symbol object ***/
							String sScripCode = obj.getScripCode();
							String sMktSegID = ExchangeSegment.getMarketSegmentID(obj.getExchange());
							String sTokenMktSegID = sScripCode + "_" + sMktSegID;
							order.extend(SymbolMap.getSymbolRow(sScripCode, sMktSegID).getMinimisedSymbolRow());
							linkedsetSymbolToken.add(sTokenMktSegID);
							getOrderDetails(obj,order);
							gtdPendingOrder.put(order);
							/* ***************/
							log.info(obj.getStatus()+" "+sStatus+" "+obj.getGatewayOrderNumber());
							String sKey = obj.getGatewayOrderNumber();
							List<GetGTDOrderBookObjectRow> list = new ArrayList<>();
							list.add(obj);
							pendingOrderMap.put(sKey, list);
						}catch(Exception e) {
							log.debug(e);
						}
						break;
					case OrderConstants.EXECUTED:
						try {
							SymbolRow order = new SymbolRow();
							/*** To fetch quote details and symbol object ***/
							String sScripCode = obj.getScripCode();
							String sMktSegID = ExchangeSegment.getMarketSegmentID(obj.getExchange());
							String sTokenMktSegID = sScripCode + "_" + sMktSegID;
							//linkedsetSymbolToken.add(sTokenMktSegID);
							order.extend(SymbolMap.getSymbolRow(sScripCode, sMktSegID).getMinimisedSymbolRow());
							getOrderDetails(obj,order);
							/* ***************/
							log.info(obj.getStatus()+" "+sStatus+" "+obj.getGatewayOrderNumber());
							String sKey = obj.getGatewayOrderNumber();
							List<GetGTDOrderBookObjectRow> list = new ArrayList<>();
							list.add(obj);
							executedOrderMap.put(sKey, list);
						}catch(Exception e) {
							log.debug(e);
						}
						break;
					case OrderConstants.CANCELLED:
						try {
							SymbolRow order = new SymbolRow();
							/*** To fetch quote details and symbol object ***/
							String sScripCode = obj.getScripCode();
							String sMktSegID = ExchangeSegment.getMarketSegmentID(obj.getExchange());
							String sTokenMktSegID = sScripCode + "_" + sMktSegID;
							//linkedsetSymbolToken.add(sTokenMktSegID);
							order.extend(SymbolMap.getSymbolRow(sScripCode, sMktSegID).getMinimisedSymbolRow());
							getOrderDetails(obj,order);
							/* ***************/
							log.info(obj.getStatus()+" "+sStatus+" "+obj.getGatewayOrderNumber());
							String sKey = obj.getGatewayOrderNumber();
							List<GetGTDOrderBookObjectRow> list = new ArrayList<>();
							list.add(obj);
							cancelledOrderMap.put(sKey, list);
						}catch(Exception e) {
							log.debug(e);
						}
						break;
				}
			}
		}
		return pendingOrderMap;
	}

	public Map<String, Collection<GetGTDOrderBookObjectRow>> getGTDPendingSummary()
	{
		return pendingOrderMap;
	}
	
	public Map<String, Collection<GetGTDOrderBookObjectRow>> getGTDExecutedSummary()
	{
		return executedOrderMap;
	}
	
	public Map<String, Collection<GetGTDOrderBookObjectRow>> getGTDCancelledSummary()
    {
        return cancelledOrderMap;
    }
	
	public Map<String, Collection<GetGTDOrderBookObjectRow>> getGTDOrderSummary()
	{
		return orderMap;
	}
	
	public  JSONObject getGTDPendingOrder(Session session) throws SQLException, JSONException, GCException, NoDataAvailableException {
		OrderBook_102.getQuote(gtdPendingOrder, linkedsetSymbolToken);
		orderBookObj.put(OrderConstants.ORDERS, gtdPendingOrder);
		return orderBookObj;
		
	}
	
	private static void getOrderDetails(GetGTDOrderBookObjectRow orderRow, SymbolRow order) throws Exception {

		String sStatus = OrderStatus.getGTDStatus( orderRow.getGTDOrderStatus());
		
		order.put(OrderConstants.PENDING_QTY, orderRow.getQuantityOpen());
			
		order.put(OrderConstants.ORDER_QTY, orderRow.getTotalQuantity());
		order.put(OrderConstants.TRADE_QTY,
				Long.toString(Long.parseLong(orderRow.getTotalQuantity()) - Long.parseLong(orderRow.getQuantityOpen())));
		order.put(OrderConstants.PRICE, PriceFormat.formatPrice(orderRow.getPrice(), order.getPrecisionInt(), false));
		
		if(order.getExchange().equalsIgnoreCase(ExchangeSegment.NSECDS))
			order.put(OrderConstants.ORDER_VALUE, OrderBook_103.getOrderTradeValue(PriceFormat.formatPrice(
					String.valueOf((Double.parseDouble(orderRow.getPrice()) * Long.parseLong(orderRow.getTotalQuantity())*order.getDispLotSizeInt())),
					Integer.parseInt(OrderConstants.PRECISION_2), false), SymbolMap.getSymbolRow(order.getSymbolToken())));
		else
			order.put(OrderConstants.ORDER_VALUE, OrderBook_103.getOrderTradeValue(PriceFormat.formatPrice(
					String.valueOf((Double.parseDouble(orderRow.getPrice()) * Long.parseLong(orderRow.getTotalQuantity()))),
					Integer.parseInt(OrderConstants.PRECISION_2), false), SymbolMap.getSymbolRow(order.getSymbolToken())));
		
		order.put(OrderConstants.ORDER_VALUE, PriceFormat.formatPrice(
				String.valueOf((Double.parseDouble(orderRow.getPrice()) * Long.parseLong(orderRow.getTotalQuantity()))),
				Integer.parseInt(OrderConstants.PRECISION_2), false));
		order.put(OrderConstants.ORDER_TIME, orderRow.getOrderTime());
		order.put(OrderConstants.TRIG_PRICE, orderRow.getTriggerPrice());
		order.put(OrderConstants.EXCH_ORDER_TIME, orderRow.getOrderTime());
		order.put(OrderConstants.GTD_DATE, orderRow.getGTDDate());//
		order.put(OrderConstants.GTD_ORDER_ID, orderRow.getGTDOrderId());
		order.put(OrderConstants.DAYS, orderRow.getGTDDay());
		order.put(OrderConstants.PRODUCT_TYPE, ProductType.formatToDisplay(orderRow.getProdType(),
				ExchangeSegment.getMarketSegmentID(orderRow.getExchange())));
		order.put(OrderConstants.ORDER_ACTION, OrderAction.formatToDevice(orderRow.getBuySell()));
		order.put(OrderConstants.GATEWAY_ORD_NO, orderRow.getGatewayOrderNumber());
		order.put(OrderConstants.CLIENT_ORD_NO, orderRow.getClientOrderNumber());
		order.put(OrderConstants.ORDER_TYPE, orderRow.getOrderType());
		order.put(OrderConstants.VALIDITY, OrderConstants.GTD);
		order.put(OrderConstants.DISC_QTY, "0");

		if(orderRow.getOrderTime().isEmpty() || orderRow.getOrderTime().equals("0")) {
			order.put(OrderConstants.DISP_ORDER_TIME, "--:--:--");
		}else {
			order.put(OrderConstants.DISP_ORDER_TIME, orderRow.getOrderTime());
		}

		order.put(OrderConstants.AMO_DETAILS, "");
		order.put(OrderConstants.DISP_ORDER_TYPE, OrderType.formatToDeviceDisplay(orderRow.getOrderType()));
		
		if(orderRow.getExchangeOrderNumber().isEmpty())
			order.put(OrderConstants.EXCH_ORD_NO, "--");
		else
			order.put(OrderConstants.EXCH_ORD_NO, orderRow.getExchangeOrderNumber());
		
		order.put(OrderConstants.RECO_ID, "");
		
		order.put(OrderConstants.BRACKET_ORDER_ID, "");
		order.put(OrderConstants.IS_AMO, Boolean.toString(isAMOOrder(orderRow.getProdType())));
		
		order.put(OrderConstants.IS_BRACKET_ORDER, "false");
		order.put(OrderConstants.EXPIRY_DATE, orderRow.getGTDDate());
		order.put(OrderConstants.IS_GTD_ORDER, "true");
		
		order.put(OrderConstants.AVG_PRICE, "");
		order.put(OrderConstants.TRADED_VALUE, "");
		order.put(OrderConstants.TRADE_SUMMARY, "");

		order.put(OrderConstants.STATUS, sStatus);
		order.put(OrderConstants.DISP_STATUS, OrderStatus.getDispGTDOrderStatus(orderRow.getGTDOrderStatus()));	
		/*** To set, modifiable and cancellable flags ***/
		order.put(OrderConstants.IS_MODIFIABLE, orderRow.getModifyFlag().equals("1")?"true":"false");
		order.put(OrderConstants.IS_CANCELLABLE, orderRow.getCancelFlag().equals("1")?"true":"false");
		
		OrderStatus.getBuyOrSellMoreFlags(sStatus, orderRow.getBuySell(), order);
		
		/*** If status is cancelled, to fetch reason ***/
		order.put(OrderConstants.REASON, "");
	}
	
	private static boolean isAMOOrder(String sProductType)
	{
		if(sProductType.equalsIgnoreCase(ProductType.FT_AMO_DELIVERY_FULL_TEXT))
			return true;
		else
			return false;
	}
	
	public void getOrderBookDetails(Session session, JSONObject order) throws JSONException, GCException, NoDataAvailableException {
		/*** GetOrderBook API ***/
		GetOrderBookRequest orderbookReq = new GetOrderBookRequest();
		orderbookReq.setUserID(session.getUserID());
		orderbookReq.setGroupId(session.getGroupId());
		orderbookReq.setJKey(session.getjKey());
		orderbookReq.setJSession(session.getjSessionID());
		
				
		GetOrderBookAPI orderbookAPI = new GetOrderBookAPI();
		GetOrderBookResponse orderbookResponse = orderbookAPI.post(orderbookReq,
				GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
		
		GetOrderBookResponseObject orderObj = orderbookResponse.getResponseObject();
		List<GetOrderBookObjectRow> orderRows = orderObj.getObjJSONRows();
		if(orderRows.size() == 0) // If there is not record in GetOrderBook API, then return
		{
			throw new NoDataAvailableException();
		}else {
			for (int i = 0; i < orderRows.size(); i++) {
				try {
					GetOrderBookObjectRow orderRow = orderRows.get(i);
					String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());				
					String tempMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst()); 
					SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(orderRow.getScripCode(), tempMktSegID);
					if(tempSymbolRow == null) { 
						continue; 
					}

					if(pendingOrderMap.containsKey(orderRow.getGatewayOrdNo())) {
						pendingOrderMap.get(orderRow.getGatewayOrdNo()).iterator().next().setOrderTime(orderRow.getTime());
						order.put(OrderConstants.GTC_COUNT, order.has(OrderConstants.GTC_COUNT) ? String.valueOf(Integer.parseInt(order.getString(OrderConstants.GTC_COUNT))+1) : "1");
					}else if(sStatus.equalsIgnoreCase(OrderConstants.PENDING)) {
						order.put(DeviceConstants.PENDING_COUNT, order.has(DeviceConstants.PENDING_COUNT) ? String.valueOf(Integer.parseInt(order.getString(DeviceConstants.PENDING_COUNT))+1) : "1");
					}else if(sStatus.equalsIgnoreCase(OrderConstants.EXECUTED)) {
						order.put(DeviceConstants.EXECUTED_COUNT, order.has(DeviceConstants.EXECUTED_COUNT) ? String.valueOf(Integer.parseInt(order.getString(DeviceConstants.EXECUTED_COUNT))+1) : "1");
					}else if(sStatus.equalsIgnoreCase(OrderConstants.CANCELLED)) {
                        order.put(DeviceConstants.CANCELLED_COUNT, order.has(DeviceConstants.CANCELLED_COUNT) ? String.valueOf(Integer.parseInt(order.getString(DeviceConstants.CANCELLED_COUNT))+1) : "1");
                    }
				}catch(Exception e) {
					log.error(e);
				}
			}
		}
		
	}
	
	   public static void getOrderBookDetail(Session session, JSONObject order) throws JSONException, GCException, NoDataAvailableException {
	        /*** GetOrderBook API ***/
	        GetOrderBookRequest orderbookReq = new GetOrderBookRequest();
	        orderbookReq.setUserID(session.getUserID());
	        orderbookReq.setGroupId(session.getGroupId());
	        orderbookReq.setJKey(session.getjKey());
	        orderbookReq.setJSession(session.getjSessionID());
	        
	                
	        GetOrderBookAPI orderbookAPI = new GetOrderBookAPI();
	        GetOrderBookResponse orderbookResponse = orderbookAPI.post(orderbookReq,
	                GetOrderBookResponse.class, session.getAppID(),"GetOrderBook");
	        
	        GetOrderBookResponseObject orderObj = orderbookResponse.getResponseObject();
	        List<GetOrderBookObjectRow> orderRows = orderObj.getObjJSONRows();
	        if(orderRows.size() == 0) // If there is not record in GetOrderBook API, then return
	        {
	            throw new NoDataAvailableException();
	        }else {
	            for (int i = 0; i < orderRows.size(); i++) {
	                try {
	                    GetOrderBookObjectRow orderRow = orderRows.get(i);
	                    if (!orderRow.getBracketOrdId().isEmpty() && !(orderRow.getLegIndicator().equals(OrderConstants.MAIN_LEG_INDICATOR_9))) {
	                        continue;
	                    }
	                    
	                    if ((!orderRow.getRecoId().isEmpty()
	                            && orderRow.getProdType().equalsIgnoreCase(FTConstants.MARGIN_PLUS)) &&
	                            !((orderRow.getLegIndicator().equals("0"))
	                                ? orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL_MKT)
	                                : orderRow.getLegIndicator().equals(FTConstants.ONE_FLAG))) {
	                        continue;
	                    }
	                    String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());              
	                    String tempMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst()); 
	                    SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(orderRow.getScripCode(), tempMktSegID);
	                    if(tempSymbolRow == null) { 
	                        continue; 
	                    }

	                    if(sPendingOrderMap.containsKey(orderRow.getGatewayOrdNo())) {
	                        sPendingOrderMap.get(orderRow.getGatewayOrdNo()).iterator().next().setOrderTime(orderRow.getTime());
	                        order.put(OrderConstants.GTC_COUNT, order.has(OrderConstants.GTC_COUNT) ? String.valueOf(Integer.parseInt(order.getString(OrderConstants.GTC_COUNT))+1) : "1");
	                    }else if(sStatus.equalsIgnoreCase(OrderConstants.PENDING)) {
	                        order.put(DeviceConstants.PENDING_COUNT, order.has(DeviceConstants.PENDING_COUNT) ? String.valueOf(Integer.parseInt(order.getString(DeviceConstants.PENDING_COUNT))+1) : "1");
	                    }else if(sStatus.equalsIgnoreCase(OrderConstants.EXECUTED)) {
	                        order.put(DeviceConstants.EXECUTED_COUNT, order.has(DeviceConstants.EXECUTED_COUNT) ? String.valueOf(Integer.parseInt(order.getString(DeviceConstants.EXECUTED_COUNT))+1) : "1");
	                    }else if(sStatus.equalsIgnoreCase(OrderConstants.CANCELLED)) {
                            order.put(DeviceConstants.CANCELLED_COUNT, order.has(DeviceConstants.CANCELLED_COUNT) ? String.valueOf(Integer.parseInt(order.getString(DeviceConstants.CANCELLED_COUNT))+1) : "1");
                        }
	                }catch(Exception e) {
	                    log.error(e);
	                }
	            }
	        }
	        
	    }
	
	private void setOrderTimeInGTDOrder(JSONArray order) throws JSONException, GCException, NoDataAvailableException {
		for(int i=0; i<order.length();i++) {
			if(pendingOrderMap.containsKey(order.getJSONObject(i).getString(OrderConstants.GATEWAY_ORD_NO)))
				order.getJSONObject(i).put(OrderConstants.DISP_ORDER_TIME, pendingOrderMap.get(order.getJSONObject(i).getString(OrderConstants.GATEWAY_ORD_NO)).iterator().next().getOrderTime());
		}		
	}

}
