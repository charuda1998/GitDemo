package com.globecapital.constants.order;

import org.json.JSONArray;

public class Validity {
	
	/*** Device Constants ***/
	public static final String DAY = "DAY";
	public static final String IOC = "IOC";
	public static final String GTD = "GTD";
	public static final String GTD_EQ = "GTD EQ";
	public static final String GTC = "GTC";

	/*** FT Constants ***/
	private static final int FT_DAY = 1;
	private static final int FT_IOC = 4;
	private static final int FT_GTD = 2;
	private static final int FT_GTC = 3;
	private static final int FT_GTD_EQ = 11;

	/*** Only for BSE exchange  */
	private static final String FT_EOSESS_STR = "EOSESS";
	
	public static int formatToAPI(String sValidity) throws Exception
	{
	if(sValidity.equals(DAY))
		return FT_DAY;
	else if(sValidity.equals(IOC))
		return FT_IOC;
	else if(sValidity.equals(GTD))
		return FT_GTD;
	else if(sValidity.equals(GTD_EQ))
		return FT_GTD_EQ;
	else if(sValidity.equals(GTC))
		return FT_GTC;
	else
		throw new Exception("Invalid Validity");
	}

	public static String formatToDevice(String apiValidity) throws Exception
	{
		if(apiValidity.equals(DAY) || apiValidity.equals(FT_EOSESS_STR))
			return DAY;
		else if(apiValidity.equals(IOC))
			return IOC;
		else if(apiValidity.equals(GTC))
			return GTC;
		else if(apiValidity.equals(GTD))
			return GTD;
		else
			throw new Exception("Invalid Validity");
	}
	
	public static JSONArray getValidities()
	{
		JSONArray validityList = new JSONArray();
		validityList.put(DAY);
		validityList.put(IOC);
		return validityList;
		
	}
}
