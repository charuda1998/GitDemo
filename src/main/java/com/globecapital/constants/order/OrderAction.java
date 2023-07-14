package com.globecapital.constants.order;

import com.globecapital.constants.MessageConstants;

public class OrderAction {
	
	/*** Device Constants ***/
	public static final String BUY = "BUY";
	public static final String SELL = "SELL";
	
	public static final String BUYING = "buying";
	public static final String SELLING = "selling";
	
	/*** FT Constants ***/
	public static final int FT_BUY = 1;
	public static final int FT_SELL = 2;
	public static final String FT_B = "B";
	public static final String FT_S = "S";
	
	public static int formatToAPI(String sOrderSide) throws Exception
	{
	if(sOrderSide.equals(BUY))
		return FT_BUY;
	else if(sOrderSide.equals(SELL))
		return FT_SELL;
	else
		throw new Exception(MessageConstants.INVALID_ORDER_ACTION);
	}

	public static String formatToDevice(String sBuyOrSell) throws Exception
	{
		if(sBuyOrSell.equals(FT_B))
			return BUY;
		else if(sBuyOrSell.equals(FT_S))
			return SELL;
		else
			throw new Exception(MessageConstants.INVALID_ORDER_ACTION);
	}
	
	public static String formatToDevice2(String sBuyOrSell) throws Exception
	{
		if(sBuyOrSell.equals(FT_B))
			return BUYING;
		else if(sBuyOrSell.equals(FT_S))
			return SELLING;
		else
			throw new Exception(MessageConstants.INVALID_ORDER_ACTION);
	}
}
