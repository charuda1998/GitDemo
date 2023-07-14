package com.globecapital.constants.order;


public class OrderType {
	
	/*** Device Constants ***/
	public static final String REGULAR_LOT_LIMIT = "RL";
	public static final String REGULAR_LOT_MARKET = "RL MKT";
	public static final String STOP_LOSS_LIMIT = "SL";
	public static final String STOP_LOSS_MARKET = "SL MKT";
	
	public static final String REGULAR_LOT_LIMIT2 = "Limit";
	public static final String REGULAR_LOT_MARKET2 = "Market";
	public static final String STOP_LOSS_LIMIT2 = "SL Limit";
	public static final String STOP_LOSS_MARKET2 = "SL Market";
	public static final String STOP_LOSS_LIMIT3 = "Stop loss limit";
	public static final String STOP_LOSS_MARKET3 = "Stop loss market";
	
	/*** Display Constants ***/
	public static final String NRML = "NRML";
	public static final String SL = "SL";
	public static final String MKT = "MKT";
	public static final String SL_LMT = "SL LMT";
	public static final String SL_MKT = "SL MKT";
	public static final String LMT = "LMT";
	
	/*** FT Constants ***/
	public static final int FT_REGULAR_LOT_LIMIT = 1;
	public static final int FT_REGULAR_LOT_MARKET = 2;
	public static final int FT_STOP_LOSS_LIMIT = 3;
	public static final int FT_STOP_LOSS_MARKET = 4;
	
	public static int formatToAPI(String sOrderType) throws Exception {
		if (sOrderType.equals(REGULAR_LOT_LIMIT))
			return FT_REGULAR_LOT_LIMIT;
		else if (sOrderType.equals(REGULAR_LOT_MARKET))
			return FT_REGULAR_LOT_MARKET;
		else if (sOrderType.equals(STOP_LOSS_LIMIT))
			return FT_STOP_LOSS_LIMIT;
		else if (sOrderType.equals(STOP_LOSS_MARKET))
			return FT_STOP_LOSS_MARKET;
		else
			throw new Exception("Invalid Order Type");

	}
	
	public static int formatToAPI2(String sOrderType) throws Exception {
		if (sOrderType.equals(REGULAR_LOT_LIMIT2))
			return FT_REGULAR_LOT_LIMIT;
		else if (sOrderType.equals(REGULAR_LOT_MARKET2))
			return FT_REGULAR_LOT_MARKET;
		else if (sOrderType.equals(STOP_LOSS_LIMIT2))
			return FT_STOP_LOSS_LIMIT;
		else if (sOrderType.equals(STOP_LOSS_MARKET2))
			return FT_STOP_LOSS_MARKET;
		else
			throw new Exception("Invalid Order Type");

	}
	public static String formatToDeviceDisplay(String sOrderType)
	{
		if(sOrderType.equals(REGULAR_LOT_LIMIT))
			return LMT;
		else if(sOrderType.equals(REGULAR_LOT_MARKET))
			return MKT;
		else if(sOrderType.equals(STOP_LOSS_LIMIT))
			return SL_LMT;
		else if(sOrderType.equals(STOP_LOSS_MARKET))
			return SL_MKT;
		return sOrderType;
	}
	
	public static String formatToDeviceDisplay2(String sOrderType)
	{
		if(sOrderType.equals(REGULAR_LOT_LIMIT))
			return REGULAR_LOT_LIMIT2;
		else if(sOrderType.equals(REGULAR_LOT_MARKET))
			return REGULAR_LOT_MARKET2;
		else if(sOrderType.equals(STOP_LOSS_LIMIT))
			return STOP_LOSS_LIMIT3;
		else if(sOrderType.equals(STOP_LOSS_MARKET))
			return STOP_LOSS_MARKET3;
		return sOrderType;
	}

}
