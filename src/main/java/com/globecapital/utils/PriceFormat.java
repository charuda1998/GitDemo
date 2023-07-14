package com.globecapital.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.globecapital.constants.AppConstants;

public class PriceFormat {

	public static String formatFloat(final float value, final int precision) {
		final String sForm = "%." + precision + "f";

		return String.format(sForm, value);
	}

	public static String formatDouble(final Double value) {
		final DecimalFormat dec = new DecimalFormat("#0.00");

		return dec.format(value);
	}

	public static String formatFloat(final float value, final int decimalLocator, final int precision) {
		final float fValue = value / decimalLocator;
		final String sForm = "%." + precision + "f";

		return String.format(sForm, fValue);
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

	public static String formatPrice(String val, final int precision, final boolean roundoff) {

		if (val == null || val.isEmpty())
			return "0." + genZeros(precision);

		String sPrecision = "";
		for (int i = 0; i < precision; i++)
			sPrecision += "0";

		if (isDouble(val)) {

			if (val.equals("0"))
				return "0." + sPrecision;

			final DecimalFormat df = new DecimalFormat("#,#,##0." + sPrecision);

			if (!roundoff) // pass true for round off the value
				df.setRoundingMode(RoundingMode.FLOOR);

			final Double dval = Double.parseDouble(val);
			val = df.format(dval);
			if (val.startsWith("."))
				val = "0" + val;

		}
		return val;
	}
	
	public static String addComma(int value)
	{
		final DecimalFormat df = new DecimalFormat("#,###,###");
		return df.format(value);
	}

	public static String formatPriceToPaise(final String price, final int multiplier) {
		final Double dPrice = Double.parseDouble(price);
		return String.format("%.0f", (dPrice * multiplier));
	}

	public static String formatPaiseToPrice(final String price, final int multiplier) {
		final Double dPrice = Double.parseDouble(price);
		if(dPrice > 0)
			return String.format("%.0f", (dPrice / multiplier));
		else
			return price;
	}
	
	public static String priceToRupees(String price) {
		return AppConstants.RUPEE + " " + PriceFormat.formatPrice(price, 2, false);
	}
	
	public static String priceToRupee(String price,int precison) {
		return AppConstants.RUPEE + " " + PriceFormat.formatPrice(price, precison, false);
	}


	public static String priceInCrores(String price) {

		Double formattedPrice = 0.0;
		if (!price.isEmpty()) {
			if (Math.abs(Double.parseDouble(price)) >= 10000000) {

				formattedPrice = Double.parseDouble(price) / 10000000;

				return AppConstants.RUPEE + " " + PriceFormat.formatPrice(String.valueOf(formattedPrice), 2, false) + " " + "Cr";

			} else if (Math.abs(Double.parseDouble(price)) >= 100000) {
				formattedPrice = Double.parseDouble(price) / 100000;
				return AppConstants.RUPEE + " " + PriceFormat.formatPrice(String.valueOf(formattedPrice), 2, false) + " " + "L";

			} else
				return AppConstants.RUPEE + " " + PriceFormat.formatPrice(price, 2, false);
		}
		return String.valueOf(formattedPrice);

	}
	
	public static String priceInCrores(String price,int precision) {

		Double formattedPrice = 0.0;
		if (!price.isEmpty()) {
			if (Math.abs(Double.parseDouble(price)) >= 10000000) {

				formattedPrice = Double.parseDouble(price) / 10000000;

				return AppConstants.RUPEE + " " + PriceFormat.formatPrice(String.valueOf(formattedPrice), precision, false) + " " + "Cr";

			} else if (Math.abs(Double.parseDouble(price)) >= 100000) {
				formattedPrice = Double.parseDouble(price) / 100000;
				return AppConstants.RUPEE + " " + PriceFormat.formatPrice(String.valueOf(formattedPrice), precision, false) + " " + "L";

			} else
				return AppConstants.RUPEE + " " + PriceFormat.formatPrice(price, precision, false);
		}
		return String.valueOf(formattedPrice);

	}
	
	public static String chartFormat(String sNumber, int precision)
	{

		sNumber = sNumber.replaceAll(",", "");

		sNumber = BigDecimal.valueOf(Double.parseDouble(sNumber)).toPlainString();

		String units = "";
		String output = sNumber;

		if (units.length() > 0 && precision == 0)
		{
			precision = 2;
		}

		int dotIndex = output.indexOf(".");
		if (dotIndex > -1)
		{

			if (precision > 0)
			{

				int reqExtraPrecision = precision - (output.length() - dotIndex - 1);

				if (reqExtraPrecision < 0)
				{
					// Output contains extra decimals; So stripping off.
					int end = dotIndex + 1 + precision;
					output = output.substring(0, end);

				} else if (reqExtraPrecision > 0)
				{

					output += genZeros(reqExtraPrecision);
				}
			} else
			{
				output = output.substring(0, dotIndex);
			}
		} else if (precision > 0)
		{
			output += "." + genZeros(precision);
		}

		return output;
	}

	public static String genZeros(int nZeros)
	{

		String output = "";
		for (int i = 0; i < nZeros; i++)
		{
			output += "0";
		}
		return output;
	}

	public static String numberFormat(int num) {
		String[] magnitudes = {"K", "L", "Cr"};
		String sign = "", number = String.valueOf(num);
		if(number.startsWith("-")) {
			sign = number.substring(0,1);
			num = Integer.valueOf(number.substring(1,number.length()));
		}
		if(num < 1000)
			return sign + String.valueOf(num);
		double fin =(double) num / 1000;
		if( fin < 100 )
			return sign + fin + magnitudes[0];
		num = num / 1000;
		for (int i=1; i<3 ; i++) {
			int quo = num / 100;
			if( quo < 100) {
				fin = ((double) num) / 100;
				return sign + fin + magnitudes[i];
			} else
				num = num / 100;
		}
		return sign + num + magnitudes[2];
	}
	
	public static String numberFormat(double num, int precision) {
		String[] magnitudes = {"K", "L", "Cr"};
		String sign = "", number = String.valueOf(num);
		if(number.startsWith("-")) {
			sign = number.substring(0,1);
			num = Double.valueOf(number.substring(1,number.length()));
		}
		if(num < 1000)
			return PriceFormat.formatPrice(sign + String.valueOf(num), precision, false);
		double fin =(double) num / 1000;
		if( fin < 100 )
			return PriceFormat.formatPrice(sign + fin, precision, false) + magnitudes[0];
		num = num / 1000;
		for (int i=1; i<3 ; i++) {
			double quo = num / 100;
			if( quo < 100) {
				fin = ((double) num) / 100;
				return PriceFormat.formatPrice(sign + fin, precision, false) + magnitudes[i];
			} else
				num = num / 100;
		}
		return PriceFormat.formatPrice(sign + fin, precision, false) + magnitudes[2];
	}

	public static String rupeeFormat(String value){
        value=value.replace(",","");
        char lastDigit=value.charAt(value.length()-1);
        String result = "";
        int len = value.length()-1;
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--)
        {
            result = value.charAt(i) + result;
            nDigits++;
            if (((nDigits % 2) == 0) && (i > 0))
            {
                result = "," + result;
            }
        }
        return (result+lastDigit);
    }
}
