package com.globecapital.jobs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.utils.DateUtils;

public class ScripMasterHelper {
	
	private JSONObject symObj = new JSONObject();

	public ScripMasterHelper( JSONObject obj) {
		this.symObj = obj;
	}

	public String getUniqDesc (String strikePrice) throws Exception {
		String uniqDesc = "";
		
		String marketSegmentId = String.valueOf(symObj.getInt(FTConstants.SEGMENT));
		String symb = symObj.getString(FTConstants.SYMBOL_NAME);
		String exch = ExchangeSegment.getExchangeName(String.valueOf(symObj.getInt(FTConstants.SEGMENT)));
		
		
//	System.out.println("MarketSegment :: "+marketSegmentId);
	if(marketSegmentId.equalsIgnoreCase("1") || marketSegmentId.equalsIgnoreCase("3")) {
		uniqDesc = symb + "_" + exch;
//		System.out.println("Equities :: "+uniqDesc);
	} else {
		String inst = symObj.getString(FTConstants.INSTRUMENT_NAME);
		String exp = DateUtils.formatDate(getExpiry(symObj.getString(FTConstants.EXPIRY_DATE)), DBConstants.EXPIRY_DATE_FORMAT, DBConstants.UNIQ_DESC_DATE_FORMAT).toUpperCase();
		if(inst.startsWith("FUT"))
			uniqDesc = symb+exp+"FUT"+ "_" + exch;
		else if(inst.startsWith("OPT")) {
			String option = getOption(symObj.getString(FTConstants.OPTION),
					symObj.getString(FTConstants.INSTRUMENT_NAME));
			uniqDesc = symb+exp+strikePrice+option+ "_" + exch;
		}
	}
	return uniqDesc;
	}
	
	public static String formatOriginalStrikePrice(String val, int precision , String decimalLocator, String exchange) {
		String originalstrikePrice ="";
		boolean isFormatToFourDecimals=false;
    	if (val == null || val.isEmpty() || val.equals("0"))
			return "0";
		String sPrecision = "";
		for (int i = 0; i < precision; i++)
			sPrecision += "0";
			if (isDouble(val)) {
				if (val.equals("0"))
					if(precision == 0) 
						return "0";
					else
						return "0." + sPrecision;
				final DecimalFormat df;
				final Double dval = Double.parseDouble(val)/Double.parseDouble(decimalLocator);
				originalstrikePrice=String.valueOf(dval);
				if(precision == 0) {
					df = new DecimalFormat("#" + sPrecision);
				}else {
					String strikePrice = String.valueOf(dval);
					if(strikePrice.contains(".")&&strikePrice.split("\\.")[1].length()>2 && exchange.equals(ExchangeSegment.NSECDS)) {
						df = new DecimalFormat("#.####");
						isFormatToFourDecimals = true;
						}else {
						df = new DecimalFormat("#.##");
						}
				}

				df.setRoundingMode(RoundingMode.FLOOR);

				val = df.format(dval);
				if (val.startsWith("."))
					val = "0" + val;
				if(isFormatToFourDecimals && val.contains(".")&&val.split("\\.")[1].length()<4) {
					val+="0";
					
				}
				if(val.contains(".")&&val.split("\\.")[1].length()<2 ) {
					val+="0";
				
					
			}
			}
		
		return val;
	}
    public static String formatStrikePrice(String val, int precision , String decimalLocator, String exchange) {
    	boolean isFormatToFourDecimals = false;
    	if (val == null || val.isEmpty() || val.equals("0"))
			return "0";

		String sPrecision = "";
		for (int i = 0; i < precision; i++)
			sPrecision += "0";

		if (isDouble(val)) {
			if (val.equals("0"))
				if(precision == 0) 
					return "0";
				else
					return "0." + sPrecision;

			final DecimalFormat df;
			final Double dval = Double.parseDouble(val)/Double.parseDouble(decimalLocator);
			if(precision == 0) {
				df = new DecimalFormat("#" + sPrecision);
			}else {
				String strikePrice = String.valueOf(dval);
				if(strikePrice.contains(".")&&strikePrice.split("\\.")[1].length()>2 && exchange.equals(ExchangeSegment.NSECDS)) {
					df = new DecimalFormat("#.####");
					isFormatToFourDecimals = true;
				}else
					df = new DecimalFormat("#.##");
			}

			df.setRoundingMode(RoundingMode.FLOOR);

			val = df.format(dval);
			if (val.startsWith("."))
				val = "0" + val;
			if(isFormatToFourDecimals && val.contains(".")&&val.split("\\.")[1].length()<4) {
				if(!exchange.equals(ExchangeSegment.NSECDS))
				val+="0";
				else
				return val;
			}
			if(val.contains(".")&&val.split("\\.")[1].length()<2 && exchange.equals(ExchangeSegment.NSECDS)) {
				//val+="0";
				return val;
			}
		}
		return val;

	}
    
	public static boolean isDouble(final String str) {
		if (str != null) {
			try {
				final double v = Double.parseDouble(str);
				return true;
			} catch (final NumberFormatException nfe) {
			}
		}
		return false;
	}

	
	public String getSymDet (String strikePrice) throws Exception {
		String symDet ="";
		String exch = ExchangeSegment.getExchangeName(String.valueOf(symObj.getInt(FTConstants.SEGMENT)));
		String inst = symObj.getString(FTConstants.INSTRUMENT_NAME);
		String exp = DateUtils.formatDate(getExpiry(symObj.getString(FTConstants.EXPIRY_DATE)), DBConstants.EXPIRY_DATE_FORMAT, DBConstants.DATE_MONTH_FORMAT).toUpperCase();
		//changed to Date_Month DATE_MONTH_FORMAT from Scrip_expiry format to avoid confusion b/w weekly expiry and monthly expiry contracts
		if(inst.startsWith("FUT"))
			symDet = exch+" "+exp+" FUT";
		else if(inst.startsWith("OPT")) {
			String option = getOption(symObj.getString(FTConstants.OPTION),
					symObj.getString(FTConstants.INSTRUMENT_NAME));
			symDet = exch+" "+exp+" "+strikePrice+" "+option;
		}
		return symDet;
	}
	
	public String getSymDet2 (String strikePrice) throws Exception {
		String symDet ="";
		String exch = ExchangeSegment.getExchangeName(String.valueOf(symObj.getInt(FTConstants.SEGMENT)));
		String inst = symObj.getString(FTConstants.INSTRUMENT_NAME);
		String exp = DateUtils.formatDate(getExpiry(symObj.getString(FTConstants.EXPIRY_DATE)), DBConstants.EXPIRY_DATE_FORMAT, DBConstants.SCRIP_EXPIRY_DATE_FORMAT).toUpperCase();
		if(inst.startsWith("FUT"))
			symDet = exp+" FUT";
		else if(inst.startsWith("OPT")) {
			String option = getOption(symObj.getString(FTConstants.OPTION),
					symObj.getString(FTConstants.INSTRUMENT_NAME));
			symDet = exp+" "+strikePrice+" "+option;
		}
		return symDet;
	}
	
	public static String getExpiry(String expdate) {
		String formattedDate = "";
		try {
			SimpleDateFormat sourceFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FROM_FORMAT);
			SimpleDateFormat destinationFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FORMAT);

			formattedDate = destinationFormat.format(sourceFormat.parse(expdate));
		} catch (Exception e) {
			e.getMessage();
		}
		return formattedDate.toUpperCase();
	}
	
	public static String getPrecision(String marketsegment) throws ClassNotFoundException {

		if (marketsegment.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID))

			return OrderConstants.PRECISION_4;

		else
			return OrderConstants.PRECISION_2;

	}
	
	public static String getOption(String option, String instrument)

	{
		String opt = "";
		try {
			if (instrument.startsWith("OPT")) {
				opt = option;
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return opt;
	}

	public static String getCompanyName(String symb, String formatExp, String strikePrice, String option, String instrumentName, String secDesc) {
		if(InstrumentType.isOptions(instrumentName))
			return symb+" "+formatExp+" "+strikePrice+" "+option;
		else if(InstrumentType.isFutures(instrumentName))
			return symb+" "+formatExp+" "+"FUT";
		return secDesc;
	}
}
