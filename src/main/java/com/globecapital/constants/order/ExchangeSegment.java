package com.globecapital.constants.order;

import java.util.ArrayList;
import java.util.List;

import com.globecapital.constants.MessageConstants;

public class ExchangeSegment {

	/************* Allowed Exchange Segments - Name ************** */
	public static final String NSE = "NSE";
	public static final String BSE = "BSE";
	public static final String NFO ="NFO"; //NSE DERIVATIVES
	public static final String NCD="NCD";
	public static final String MCX = "MCX"; //MCX Futures
	public static final String NCDEX = "NCDEX"; //NCDEX Futures
	public static final String NSECDS = "NSECDS"; //NSECDS Futures
	public static final String BSECDS = "BSECDS"; //BSECDS Futures
	/************* Allowed Exchange Segments - Name ************** */

	/************* Allowed Exchange Segments - Id ************** */
	public static final String NSE_SEGMENT_ID = "1";
	public static final String BSE_SEGMENT_ID = "3";
	public static final String NFO_SEGMENT_ID ="2"; //NSE DERIVATIVES
	public static final String MCX_SEGMENT_ID = "5"; //MCX Futures
	public static final String NCDEX_SEGMENT_ID = "7"; //NCDEX Futures
	public static final String NSECDS_SEGMENT_ID = "13"; //NSECDS Futures
	public static final String BSECDS_SEGMENT_ID = "38"; //BSECDS Futures
	private static List<String> allowedSegmentIdList;
	static
	{
		allowedSegmentIdList = new ArrayList<String>();
		allowedSegmentIdList.add(NSE_SEGMENT_ID);
		allowedSegmentIdList.add(BSE_SEGMENT_ID);
		allowedSegmentIdList.add(NFO_SEGMENT_ID);
		allowedSegmentIdList.add(MCX_SEGMENT_ID);
		allowedSegmentIdList.add(NCDEX_SEGMENT_ID);
		allowedSegmentIdList.add(NSECDS_SEGMENT_ID);
		allowedSegmentIdList.add(BSECDS_SEGMENT_ID);
	}

	public static final String getExchangeName(final String marketSegmentID) throws Exception {
		if (marketSegmentID.equals(NSE_SEGMENT_ID))
			return NSE;
		else if (marketSegmentID.equals(BSE_SEGMENT_ID))
			return BSE;
		else if (marketSegmentID.equals(NFO_SEGMENT_ID))
			return NFO;
		else if (marketSegmentID.equals(MCX_SEGMENT_ID))
			return MCX;
		else if (marketSegmentID.equals(NCDEX_SEGMENT_ID))
			return NCDEX;
		else if (marketSegmentID.equals(NSECDS_SEGMENT_ID))
			return NSECDS;
		else if (marketSegmentID.equals(BSECDS_SEGMENT_ID))
			return BSECDS;
		else
			throw new Exception(MessageConstants.INVALID_MARKETSEGMENT_ID);

	}

	public static final String getMarketSegmentID(final String exchangeName) throws Exception {
		if (exchangeName.equals(NSE))
			return NSE_SEGMENT_ID;
		else if (exchangeName.equals(BSE))
			return BSE_SEGMENT_ID;
		else if (exchangeName.equals(NFO))
			return NFO_SEGMENT_ID;
		else if (exchangeName.equals(MCX))
			return MCX_SEGMENT_ID;
		else if (exchangeName.equals(NCDEX))
			return NCDEX_SEGMENT_ID;
		else if (exchangeName.equals(NSECDS))
			return NSECDS_SEGMENT_ID;
		else if (exchangeName.equals(BSECDS))
			return BSECDS_SEGMENT_ID;
		else
			throw new Exception(MessageConstants.INVALID_Exchange_NAME);
	}

	public static final String getMarketSegmentID(final String exchangeName, final String instrumentType)
			throws Exception {
		if (exchangeName.equals(NSE) && instrumentType.startsWith("EQ"))
			return NSE_SEGMENT_ID;
		else if (exchangeName.equals(BSE))
			return BSE_SEGMENT_ID;
		else if (exchangeName.equals(NSE) && (instrumentType.startsWith(InstrumentType.FUTURES)
				|| instrumentType.startsWith(InstrumentType.OPTIONS)))
			return NFO_SEGMENT_ID;
		else if (exchangeName.equals(MCX))
			return MCX_SEGMENT_ID;
		else if (exchangeName.equals(NCDEX))
			return NCDEX_SEGMENT_ID;
		else if (exchangeName.equals(NSECDS))
			return NSECDS_SEGMENT_ID;
		else if (exchangeName.equals(BSECDS))
			return BSECDS_SEGMENT_ID;
		else
			throw new Exception(MessageConstants.INVALID_Exchange_NAME);
	}

	public static final boolean isEquitySegment(String marketSegmentID) {
		if (marketSegmentID.equals(NSE_SEGMENT_ID) || marketSegmentID.equals(BSE_SEGMENT_ID))
			return true;
		else
			return false;
	}

	public static final boolean isCommoditySegment(String marketSegmentID) {
		if (marketSegmentID.equals(MCX_SEGMENT_ID) || marketSegmentID.equals(NCDEX_SEGMENT_ID))
			return true;
		else
			return false;
	}

	public static final boolean isCurrencySegment(String marketSegmentID) {
		if (marketSegmentID.equals(NSECDS_SEGMENT_ID) || marketSegmentID.equals(BSECDS_SEGMENT_ID))
			return true;
		else
			return false;
	}

	public static final boolean isValidSegmentID(String marketSegmentID) {
		return allowedSegmentIdList.contains(marketSegmentID);
	}
}
