package com.globecapital.constants.order;

import com.globecapital.constants.AppConstants;
import com.globecapital.utils.PriceFormat;

public class OrderPrice {


    public static String formatOrderPriceToAPI(String orderType, String orderPrice, int multiplier, String ltp)
    {
        if(orderType.equalsIgnoreCase(OrderType.STOP_LOSS_MARKET) 
                    || orderType.equals(OrderType.REGULAR_LOT_MARKET))
            return PriceFormat.formatPriceToPaise(ltp, multiplier);
        else
            return PriceFormat.formatPriceToPaise(orderPrice, multiplier);
    }
    
    public static String formatOrderPriceCoverToAPI(String orderType, String orderPrice, int multiplier, String ltp)
    {
        if(orderType.equalsIgnoreCase(OrderType.SL) 
                || orderType.equals(OrderType.REGULAR_LOT_MARKET))
            return PriceFormat.formatPriceToPaise(orderPrice, multiplier);
        else
        	return PriceFormat.formatPriceToPaise(ltp, multiplier);
    }

    public static String formatTriggerPriceToAPI(String orderType, String triggerPrice, int multiplier, String ltp)
    {
        if(orderType.equalsIgnoreCase(OrderType.STOP_LOSS_MARKET) 
                    || orderType.equals(OrderType.STOP_LOSS_LIMIT))
            return PriceFormat.formatPriceToPaise(triggerPrice, multiplier);
        else
        	return PriceFormat.formatPriceToPaise(ltp, multiplier);
    }

    public static String formatPriceToDevice( String price, int multiplier)
    {
        return PriceFormat.formatPaiseToPrice(price, multiplier);
   
    }


}