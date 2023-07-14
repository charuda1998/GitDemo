package com.globecapital.business.order;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.symbology.SymbolRow;

public class OrderStatus {
	
	public static String getStatus(String sStatus) {
		if (sStatus.equalsIgnoreCase(FTConstants.CLIENT_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.GATEWAY_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.OMS_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.EXCHANGE_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_PENDING)
				|| sStatus.equalsIgnoreCase(FTConstants.MINI_ADMIN_PENDING)
				|| sStatus.equalsIgnoreCase(FTConstants.AMO_SUBMITTED) || sStatus.equalsIgnoreCase(FTConstants.PENDING))
			return OrderConstants.PENDING;
		else if (sStatus.equalsIgnoreCase(FTConstants.EXECUTED) || sStatus.equalsIgnoreCase(FTConstants.COMPLETED)
				|| sStatus.equalsIgnoreCase(FTConstants.CONVERTED))
			return OrderConstants.EXECUTED;
		else if (sStatus.equalsIgnoreCase(FTConstants.GATEWAY_REJECT)
				|| sStatus.equalsIgnoreCase(FTConstants.OMS_REJECT) || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
				|| sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_REJECT) || sStatus.equalsIgnoreCase(FTConstants.A_REJECT)
				|| sStatus.equalsIgnoreCase(FTConstants.STOPPED) || sStatus.equalsIgnoreCase(FTConstants.CANCELLED)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_CANCEL)
				|| sStatus.equalsIgnoreCase(FTConstants.AMO_CANCELLED))
			return OrderConstants.CANCELLED;
		else
			return sStatus;
	}
	
	public static String getDispOrderStatus(String sStatus) {
		if (sStatus.equalsIgnoreCase(FTConstants.CLIENT_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.GATEWAY_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.OMS_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.EXCHANGE_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_PENDING)
				|| sStatus.equalsIgnoreCase(FTConstants.MINI_ADMIN_PENDING)
				|| sStatus.equalsIgnoreCase(FTConstants.AMO_SUBMITTED) || sStatus.equalsIgnoreCase(FTConstants.PENDING))
			return OrderConstants.DISP_PENDING;
		else if (sStatus.equalsIgnoreCase(FTConstants.EXECUTED) || sStatus.equalsIgnoreCase(FTConstants.COMPLETED)
				|| sStatus.equalsIgnoreCase(FTConstants.CONVERTED))
			return OrderConstants.DISP_EXECUTED;
		else if (sStatus.equalsIgnoreCase(FTConstants.STOPPED) || sStatus.equalsIgnoreCase(FTConstants.CANCELLED)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_CANCEL)
				|| sStatus.equalsIgnoreCase(FTConstants.AMO_CANCELLED))
			return OrderConstants.DISP_CANCELLED;
		else if (sStatus.equalsIgnoreCase(FTConstants.GATEWAY_REJECT)
				|| sStatus.equalsIgnoreCase(FTConstants.OMS_REJECT) || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
				|| sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_REJECT) || sStatus.equalsIgnoreCase(FTConstants.A_REJECT)
				)
			return OrderConstants.DISP_REJECTED;
		else
			return sStatus;
	}
	
	public static String getDisplayStatus(String sStatus) {
		if (sStatus.equalsIgnoreCase(OrderConstants.EXECUTED))
			return OrderConstants.DISP_EXECUTED;
		else if (sStatus.equalsIgnoreCase(OrderConstants.CANCELLED))
			return OrderConstants.DISP_CANCELLED;
		else if (sStatus.equalsIgnoreCase(OrderConstants.PENDING))
			return OrderConstants.DISP_PENDING;
		else
			return sStatus;
	}
	
	public static String getErrorMsg(String sStatus, String sError)
	{
		// if (sStatus.equals(OrderConstants.CANCELLED) && sError.length() == 0)
		// 	return MessageConstants.CANCELLED_ORDER_REASON;
		// else
			return sError;
	}
	
	public static boolean isModifiable(String sStatus)
	{
		if (sStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
		
	}
	
	public static boolean isCancellable(String sStatus)
	{
		if (sStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
	}
	
	public static boolean isBracketOrderModifiable(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus)
	{
		if(sMainOrderStatus.equals(OrderConstants.CANCELLED) || sSLOrderStatus.equals(OrderConstants.CANCELLED)
				|| sProfitOrderStatus.equals(OrderConstants.CANCELLED))
			return false;
		else if (sMainOrderStatus.equals(OrderConstants.PENDING) || sSLOrderStatus.equals(OrderConstants.PENDING)
				|| sProfitOrderStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
		
	}
	
	public static boolean isBracketOrderModifiable_101(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus, String flag)
    {
	    switch(flag) {
	        case OrderConstants.IS_BRACKET_MAIN_ORDER :
	            if(sMainOrderStatus.equals(OrderConstants.CANCELLED) || sSLOrderStatus.equals(OrderConstants.CANCELLED)
	                    || sProfitOrderStatus.equals(OrderConstants.CANCELLED))
	                return false;
	            else if (sMainOrderStatus.equals(OrderConstants.PENDING))
	                return true;
	            else
	                return false;
	        case OrderConstants.IS_BRACKET_STOPLOSS_ORDER :
	            if (sSLOrderStatus.equals(OrderConstants.PENDING) && (sMainOrderStatus.equals(OrderConstants.PENDING) 
                        || sProfitOrderStatus.equals(OrderConstants.PENDING)))
                    return true;
                else if (sSLOrderStatus.equals(OrderConstants.PENDING) && (sMainOrderStatus.equals(OrderConstants.PENDING) 
                        || sProfitOrderStatus.equals(OrderConstants.CANCELLED)))
                    return true;
                else
                    return false;
	        case OrderConstants.IS_BRACKET_PROFIT_ORDER :
	            if (sProfitOrderStatus.equals(OrderConstants.PENDING) && (sSLOrderStatus.equals(OrderConstants.PENDING)
                        || sMainOrderStatus.equals(OrderConstants.PENDING)))
                    return true;
                else if (sProfitOrderStatus.equals(OrderConstants.PENDING) && (sSLOrderStatus.equals(OrderConstants.CANCELLED)
                        || sMainOrderStatus.equals(OrderConstants.PENDING)))
                    return true;
                else
                    return false;
	        case OrderConstants.IS_BRACKET_SQUAREOFF_ORDER :
	                return false;
	        default:
	            return false;
	    }
    }
	
	public static boolean isBracketOrderCancellable(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus)
	{
		if(sMainOrderStatus.equals(OrderConstants.CANCELLED) || sSLOrderStatus.equals(OrderConstants.CANCELLED)
				|| sProfitOrderStatus.equals(OrderConstants.CANCELLED))
			return false;
		else if (sMainOrderStatus.equals(OrderConstants.PENDING) || sSLOrderStatus.equals(OrderConstants.PENDING)
				|| sProfitOrderStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
	}
	
	public static boolean getBracketOrderExitOption(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus)
	{
		if (sMainOrderStatus.equals(OrderConstants.EXECUTED) && sSLOrderStatus.equals(OrderConstants.PENDING)
				&& sProfitOrderStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
	}
	
	public static boolean getBracketOrderConvertOption(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus)
	{
		if (sMainOrderStatus.equals(OrderConstants.EXECUTED) && sSLOrderStatus.equals(OrderConstants.PENDING)
				&& sProfitOrderStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
	}
	
	public static boolean checkAnyLegOrderExecuted(String sSLOrderStatus, String sProfitOrderStatus, String sSquareoffStatus)
	{
		if (sSLOrderStatus.equals(OrderConstants.EXECUTED) || sProfitOrderStatus.equals(OrderConstants.EXECUTED)
				|| sSquareoffStatus.equals(OrderConstants.EXECUTED))
			return true;
		else if (sSLOrderStatus.equals(OrderConstants.PENDING) || sProfitOrderStatus.equals(OrderConstants.PENDING)
				|| sSquareoffStatus.equals(OrderConstants.PENDING))
			return false;
		
		return false;
	}
	
	public static boolean checkAnyLegOrderPending(String sSLOrderStatus, String sProfitOrderStatus, String sSquareoffStatus)
	{
		
		if (sSLOrderStatus.equals(OrderConstants.PENDING) || sProfitOrderStatus.equals(OrderConstants.PENDING)
				|| sSquareoffStatus.equals(OrderConstants.PENDING))
			return true;
		return false;
		
	}
	
	
	
	public static String getLegOrderDisplayStatus(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus, 
			String sSquareoffStatus)
	{
		if (sMainOrderStatus.equals(OrderConstants.PENDING) || 
				sSLOrderStatus.equals(OrderConstants.PENDING) || sProfitOrderStatus.equals(OrderConstants.PENDING)
				|| sSquareoffStatus.equals(OrderConstants.PENDING))
			return OrderConstants.DISP_PENDING;
		else if (sSLOrderStatus.equals(OrderConstants.EXECUTED) || sProfitOrderStatus.equals(OrderConstants.EXECUTED)
				|| sSquareoffStatus.equals(OrderConstants.EXECUTED))
			return OrderConstants.DISP_EXECUTED;
		else if (sSLOrderStatus.equals(OrderConstants.CANCELLED) && sProfitOrderStatus.equals(OrderConstants.CANCELLED))
			return OrderConstants.DISP_CANCELLED;
		
		return sMainOrderStatus.toUpperCase();
	}
	
	public static boolean isLegOrderCancelled(String sSLOrderStatus, String sProfitOrderStatus)
	{
		if (sSLOrderStatus.equals(OrderConstants.CANCELLED) 
				|| sProfitOrderStatus.equals(OrderConstants.CANCELLED))
			return true;
		else
			return false;
	}
	
	public static void getBuyOrSellMoreFlags(String sStatus, String sBuyOrSell, SymbolRow order) {
		if(sStatus.equalsIgnoreCase(OrderConstants.EXECUTED))
		{
			if(sBuyOrSell.equalsIgnoreCase(OrderAction.FT_B))
			{
				order.put(DeviceConstants.IS_BUY_MORE, "true");
				order.put(DeviceConstants.IS_SELL_MORE, "false");
			}
			else if(sBuyOrSell.equalsIgnoreCase(OrderAction.FT_S))
			{
				order.put(DeviceConstants.IS_BUY_MORE, "false");
				order.put(DeviceConstants.IS_SELL_MORE, "true");
			}
		}
		else
		{
			order.put(DeviceConstants.IS_BUY_MORE, "false");
			order.put(DeviceConstants.IS_SELL_MORE, "false");
			
		}
		
	}

	public static void getBuyOrSellMoreFlags(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus,
			String sSquareoffStatus, String sBuyOrSell, SymbolRow order) {
		
		if(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus))
			getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
		else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
				&& sSLOrderStatus.equalsIgnoreCase(OrderConstants.CANCELLED) 
				&& sProfitOrderStatus.equalsIgnoreCase(OrderConstants.CANCELLED) 
				&& sSquareoffStatus.isEmpty() ? true : sSquareoffStatus.equalsIgnoreCase(OrderConstants.CANCELLED))
			getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
		else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
				&& sSLOrderStatus.equalsIgnoreCase(OrderConstants.PENDING) 
				&& sProfitOrderStatus.equalsIgnoreCase(OrderConstants.PENDING))
			getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order);
		else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
				&& OrderStatus.checkAnyLegOrderPending(sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus))
			getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
		else
			getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order);		
	}

	public static boolean getStatusForNewOrder(String sStatus) {
		if (sStatus.equalsIgnoreCase(FTConstants.ADMIN_PENDING)
				|| sStatus.equalsIgnoreCase(FTConstants.MINI_ADMIN_PENDING)
				|| sStatus.equalsIgnoreCase(FTConstants.AMO_SUBMITTED) 
				|| sStatus.equalsIgnoreCase(FTConstants.PENDING)
				|| sStatus.equalsIgnoreCase(FTConstants.EXECUTED) || sStatus.equalsIgnoreCase(FTConstants.COMPLETED)
				|| sStatus.equalsIgnoreCase(FTConstants.CONVERTED)
				||sStatus.equalsIgnoreCase(FTConstants.CLIENT_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.GATEWAY_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.OMS_XMITTED)
				|| sStatus.equalsIgnoreCase(FTConstants.EXCHANGE_XMITTED))
			return true;
		else if (sStatus.equalsIgnoreCase(FTConstants.GATEWAY_REJECT)
				|| sStatus.equalsIgnoreCase(FTConstants.OMS_REJECT) || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
				|| sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_REJECT) || sStatus.equalsIgnoreCase(FTConstants.A_REJECT)
				|| sStatus.equalsIgnoreCase(FTConstants.STOPPED) || sStatus.equalsIgnoreCase(FTConstants.CANCELLED)
				|| sStatus.equalsIgnoreCase(FTConstants.ADMIN_CANCEL)
				|| sStatus.equalsIgnoreCase(FTConstants.AMO_CANCELLED))
			return false;
		return false;
	}
	
	public static boolean isCoverOrderModifiable(String sMainOrderStatus, String sSLOrderStatus)
	{
		if(sMainOrderStatus.equals(OrderConstants.CANCELLED) || sSLOrderStatus.equals(OrderConstants.CANCELLED))
			return false;
		else if (sMainOrderStatus.equals(OrderConstants.PENDING) || sSLOrderStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
		
	}
	
	public static boolean isCoverOrderCancellable(String sMainOrderStatus, String sSLOrderStatus, boolean isMainOrder)
	{
		if(sMainOrderStatus.equals(OrderConstants.CANCELLED) || sSLOrderStatus.equals(OrderConstants.CANCELLED))
			return false;
		else if (sMainOrderStatus.equals(OrderConstants.PENDING) && isMainOrder)
			return true;
		else
			return false;
	}
	
	public static boolean getCoverOrderExitOption(String sMainOrderStatus, String sSLOrderStatus)
	{
		if (sMainOrderStatus.equals(OrderConstants.EXECUTED) && sSLOrderStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
	}
	
	public static boolean getCoverOrderConvertOption(String sMainOrderStatus, String sSLOrderStatus)
	{
		if (sMainOrderStatus.equals(OrderConstants.EXECUTED) && sSLOrderStatus.equals(OrderConstants.PENDING))
			return true;
		else
			return false;
	}
	
	public static String getCoverLegOrderDisplayStatus(String sMainOrderStatus, String sSLOrderStatus, String sSquareoffStatus)
	{
		if (sMainOrderStatus.equals(OrderConstants.PENDING) || 
				sSLOrderStatus.equals(OrderConstants.PENDING) || sSquareoffStatus.equals(OrderConstants.PENDING))
			return OrderConstants.DISP_PENDING;
		else if (sSLOrderStatus.equals(OrderConstants.EXECUTED) || sSquareoffStatus.equals(OrderConstants.EXECUTED))
			return OrderConstants.DISP_EXECUTED;
		else if (sSLOrderStatus.equals(OrderConstants.CANCELLED))
			return OrderConstants.DISP_CANCELLED;
		
		return sMainOrderStatus.toUpperCase();
	}
	
	public static void getCoverBuyOrSellMoreFlags(String sMainOrderStatus, String sSLOrderStatus, String sSquareoffStatus, 
			String sBuyOrSell, SymbolRow order) {
		
		if(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus, "", sSquareoffStatus))
			getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
		else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
				&& sSLOrderStatus.equalsIgnoreCase(OrderConstants.CANCELLED)
				&& sSquareoffStatus.isEmpty() ? true : sSquareoffStatus.equalsIgnoreCase(OrderConstants.CANCELLED))
			getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
		else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
				&& sSLOrderStatus.equalsIgnoreCase(OrderConstants.PENDING))
			getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order);
		else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
				&& OrderStatus.checkAnyLegOrderPending(sSLOrderStatus, "", sSquareoffStatus))
			getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
		else
			getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order);		
	}
	
	public static String getGTDStatus(String sStatus) {
        if (sStatus.equalsIgnoreCase(FTConstants.CLIENT_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.GATEWAY_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.OMS_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.EXCHANGE_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.ADMIN_PENDING)
                || sStatus.equalsIgnoreCase(FTConstants.MINI_ADMIN_PENDING)
                || sStatus.equalsIgnoreCase(FTConstants.AMO_SUBMITTED) 
                || sStatus.equalsIgnoreCase(FTConstants.ACTIVE) || sStatus.equalsIgnoreCase(FTConstants.PENDING))
            return OrderConstants.PENDING;
        else if (sStatus.equalsIgnoreCase(FTConstants.EXECUTED) || sStatus.equalsIgnoreCase(FTConstants.COMPLETED)
                || sStatus.equalsIgnoreCase(FTConstants.CONVERTED))
            return OrderConstants.EXECUTED;
        else if (sStatus.equalsIgnoreCase(FTConstants.GATEWAY_REJECT)
                || sStatus.equalsIgnoreCase(FTConstants.OMS_REJECT) || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
                || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
                || sStatus.equalsIgnoreCase(FTConstants.ADMIN_REJECT) || sStatus.equalsIgnoreCase(FTConstants.A_REJECT)
                || sStatus.equalsIgnoreCase(FTConstants.STOPPED) 
                || sStatus.equalsIgnoreCase(FTConstants.CANCELLED) || sStatus.equalsIgnoreCase(FTConstants.ADMIN_CANCEL) 
                || sStatus.equalsIgnoreCase(FTConstants.AMO_CANCELLED) 
                || sStatus.equalsIgnoreCase(FTConstants.WITHDRAWN) || sStatus.equalsIgnoreCase(FTConstants.EXPIRED) 
                || sStatus.equalsIgnoreCase(FTConstants.REJECTED))
            return OrderConstants.CANCELLED;
        else
            return sStatus;
    }
	
	public static String getDispGTDOrderStatus(String sStatus) {
        if (sStatus.equalsIgnoreCase(FTConstants.CLIENT_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.GATEWAY_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.OMS_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.EXCHANGE_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.ADMIN_PENDING)
                || sStatus.equalsIgnoreCase(FTConstants.MINI_ADMIN_PENDING) || sStatus.equalsIgnoreCase(FTConstants.ACTIVE)
                || sStatus.equalsIgnoreCase(FTConstants.AMO_SUBMITTED) || sStatus.equalsIgnoreCase(FTConstants.PENDING))
            return OrderConstants.DISP_PENDING;
        else if (sStatus.equalsIgnoreCase(FTConstants.EXECUTED) || sStatus.equalsIgnoreCase(FTConstants.COMPLETED)
                || sStatus.equalsIgnoreCase(FTConstants.CONVERTED))
            return OrderConstants.DISP_EXECUTED;
        else if (sStatus.equalsIgnoreCase(FTConstants.STOPPED) || sStatus.equalsIgnoreCase(FTConstants.CANCELLED)
                || sStatus.equalsIgnoreCase(FTConstants.ADMIN_CANCEL)
                || sStatus.equalsIgnoreCase(FTConstants.AMO_CANCELLED)
                || sStatus.equalsIgnoreCase(FTConstants.WITHDRAWN) || sStatus.equalsIgnoreCase(FTConstants.EXPIRED))
            return OrderConstants.DISP_CANCELLED;
        else if (sStatus.equalsIgnoreCase(FTConstants.GATEWAY_REJECT)
                || sStatus.equalsIgnoreCase(FTConstants.OMS_REJECT) || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
                || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
                || sStatus.equalsIgnoreCase(FTConstants.ADMIN_REJECT) || sStatus.equalsIgnoreCase(FTConstants.A_REJECT)
                || sStatus.equalsIgnoreCase(FTConstants.REJECTED)
                )
            return OrderConstants.DISP_REJECTED;
        else
            return sStatus;
    }
	
	public static boolean getStatusForNewGTDOrder(String sStatus) {
        if (sStatus.equalsIgnoreCase(FTConstants.ADMIN_PENDING)
                || sStatus.equalsIgnoreCase(FTConstants.MINI_ADMIN_PENDING)
                || sStatus.equalsIgnoreCase(FTConstants.AMO_SUBMITTED) 
                || sStatus.equalsIgnoreCase(FTConstants.PENDING)
                || sStatus.equalsIgnoreCase(FTConstants.EXECUTED) || sStatus.equalsIgnoreCase(FTConstants.COMPLETED)
                || sStatus.equalsIgnoreCase(FTConstants.CONVERTED)
                ||sStatus.equalsIgnoreCase(FTConstants.CLIENT_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.GATEWAY_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.OMS_XMITTED)
                || sStatus.equalsIgnoreCase(FTConstants.EXCHANGE_XMITTED) || sStatus.equalsIgnoreCase(FTConstants.WITHDRAWN) 
                || sStatus.equalsIgnoreCase(FTConstants.EXPIRED) 
                || sStatus.equalsIgnoreCase(FTConstants.ACTIVE))
            return true;
        else if (sStatus.equalsIgnoreCase(FTConstants.GATEWAY_REJECT)
                || sStatus.equalsIgnoreCase(FTConstants.OMS_REJECT) || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
                || sStatus.equalsIgnoreCase(FTConstants.ORDER_ERROR)
                || sStatus.equalsIgnoreCase(FTConstants.ADMIN_REJECT) || sStatus.equalsIgnoreCase(FTConstants.A_REJECT)
                || sStatus.equalsIgnoreCase(FTConstants.STOPPED) || sStatus.equalsIgnoreCase(FTConstants.CANCELLED)
                || sStatus.equalsIgnoreCase(FTConstants.ADMIN_CANCEL)
                || sStatus.equalsIgnoreCase(FTConstants.AMO_CANCELLED) || sStatus.equalsIgnoreCase(FTConstants.REJECTED))
            return false;
        return false;
    }
	
	public static void setBuyMoreSellMoreFalse(SymbolRow order) {
            order.put(DeviceConstants.IS_BUY_MORE, "false");
            order.put(DeviceConstants.IS_SELL_MORE, "false");
	}
	
	public static boolean isBracketOrderModifyOrCancellable(String sPivotOrderStatus, String sSubOrderStatus, String sSecondaryOrderStatus,boolean isMainOrder)
    {
        if(sPivotOrderStatus.equals(OrderConstants.CANCELLED) || sSubOrderStatus.equals(OrderConstants.CANCELLED)
                || sSecondaryOrderStatus.equals(OrderConstants.CANCELLED))
            return false;
        else if(sPivotOrderStatus.equals(OrderConstants.PENDING) && isMainOrder)
        	return true;
        else if(sPivotOrderStatus.equals(OrderConstants.PENDING) && sSubOrderStatus.equals(OrderConstants.EXECUTED)
                && sSecondaryOrderStatus.equals(OrderConstants.CANCELLED))
            return true;
        else if (sPivotOrderStatus.equals(OrderConstants.PENDING) && (sSubOrderStatus.equals(OrderConstants.PENDING)
                || sSecondaryOrderStatus.equals(OrderConstants.PENDING)))
            return true;
        else
            return false;
        
    }
	
	public static boolean getBracketOrderExitOption_101(String sMainOrderStatus, String sPivotOrderStatus, boolean isMainOrder)
    {
        if (sMainOrderStatus.equals(OrderConstants.EXECUTED) && sPivotOrderStatus.equals(OrderConstants.PENDING) && !isMainOrder )
            return true;
        else
            return false;
    }
	
	public static void getBuyOrSellMoreFlags_BO(String sMainOrderStatus, String sSLOrderStatus, String sProfitOrderStatus,
            String sSquareoffStatus, String sBuyOrSell, SymbolRow order, String flag) {
	    
	    switch(flag) {
            case OrderConstants.IS_BRACKET_MAIN_ORDER :
                if(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus)
                        && sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED))
                    getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
                else 
                    getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order);
                break;
//            case OrderConstants.IS_BRACKET_STOPLOSS_ORDER :
//                if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
//                        && sSLOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) )
//                    getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
//                else 
//                    getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order); 
//                break;
//            case OrderConstants.IS_BRACKET_PROFIT_ORDER :
//                if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
//                        && sProfitOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) )
//                    getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
//                else
//                    getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order); 
//                break;
//            case OrderConstants.IS_BRACKET_SQUAREOFF_ORDER :
//                if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
//                        && sSquareoffStatus.equalsIgnoreCase(OrderConstants.EXECUTED))
//                    getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);       
//                /*
//                 * else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED)
//                 * && sSquareoffStatus.equalsIgnoreCase(OrderConstants.PENDING) )
//                 * getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
//                 * else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED)
//                 * && sSquareoffStatus.isEmpty() ? true :
//                 * sSquareoffStatus.equalsIgnoreCase(OrderConstants.CANCELLED) )
//                 * getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
//                 */    
//                else
//                    getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order); 
//                break;
            default:
                getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order);
                break;
	    } 
    }
	
	public static boolean isCoverOrderModifiable(String sMainOrderStatus)
    {
        if (sMainOrderStatus.equals(OrderConstants.PENDING))
            return true;
        else
            return false;
        
    }
	
    public static boolean isCoverOrderCancellable(String sMainOrderStatus)
    {
        if (sMainOrderStatus.equals(OrderConstants.PENDING))
            return true;
        else
            return false;
    }
	
	public static boolean checkAnyLegOrderExecuted(String sOrderStatus)
    {
        if (sOrderStatus.equals(OrderConstants.EXECUTED))
            return true;
        else 
            return false;
    }
	
	public static void getCoverBuyOrSellMoreFlags(String sMainOrderStatus, String sOrderStatus, String sBuyOrSell, SymbolRow order) {
	    if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
                && sOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) )
            getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
        else if(sMainOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED) 
                && sOrderStatus.isEmpty() ? true : sOrderStatus.equalsIgnoreCase(OrderConstants.CANCELLED))
            getBuyOrSellMoreFlags(OrderConstants.EXECUTED, sBuyOrSell, order);
        else
            getBuyOrSellMoreFlags(OrderConstants.CANCELLED, sBuyOrSell, order);     
    }

}
