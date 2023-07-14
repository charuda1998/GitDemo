package com.globecapital.jobs;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.scripmaster.ScripMasterAPI;
import com.globecapital.api.ft.scripmaster.ScripMasterRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.GCDBPool;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class ScripMasterDump {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private static Logger log;

	public static void main(String[] args) throws Exception {

		ScripMasterRequest ftrequest = new ScripMasterRequest();
		FileInputStream stream = null;

//        String config_file = "/home/keerthana/eclipse-workspace/globe-capital.middleware/gcservices/src/main/resources/config_dev.properties.exclude";
//        String log_file="/home/keerthana/eclipse-workspace/globe-capital.middleware/gcservices/src/main/resources/jslog_dev.properties.exclude";

		String config_file = args[0];
		Properties JSLogProperties = new Properties();
		try {
			AppConfig.loadFile(config_file);
			stream = new FileInputStream(config_file);
			JSLogProperties.load(stream);
			Logger.setLogger(JSLogProperties);
			log = Logger.getLogger(ScripMasterDump.class);

		} catch (ConfigurationException e) {
			e.getMessage();
		}

		GCDBPool.initDataSource(AppConfig.getProperties());

		String[] symbol = AppConfig.getArray("segments");

		try {
			for (String s : symbol) {

				ftrequest.setMKtSegId(Integer.parseInt(s));
				ftrequest.toString();
				log.debug(ftrequest.toString());

				ScripMasterAPI scripMasterAPI = new ScripMasterAPI();
				String scripMasterResponse = scripMasterAPI.post(ftrequest);

				JSONObject scripMasterObject = new JSONObject(scripMasterResponse);
				JSONArray symbolResp = scripMasterObject.getJSONArray("ResponseObject");

				Connection conn = null;
				PreparedStatement ps = null;

				String query = DBQueryConstants.INSERT_SYMBOLS;

				try {

					conn = GCDBPool.getInstance().getConnection();
					conn.setAutoCommit(false);
					ps = conn.prepareStatement(query);
					int count = 0;

					for (int i = 0; i < symbolResp.length(); i++) {

						JSONObject obj = symbolResp.getJSONObject(i);

						if (obj.getInt(FTConstants.N_SPREAD) == 0) {

							String nTokenSegment = obj.getInt(FTConstants.TOKEN_ID) + "_"
									+ obj.getInt(FTConstants.SEGMENT);
							ps.setString(1, nTokenSegment);
							ps.setString(2, String.valueOf(obj.getInt(FTConstants.SEGMENT)));
							ps.setString(3, String.valueOf(obj.getInt(FTConstants.TOKEN_ID)));
							ps.setString(4, obj.getString(FTConstants.SYMBOL_NAME));

							ps.setString(5, obj.getString(FTConstants.SERIES_NAME).trim());
							ps.setString(6, obj.getString(FTConstants.INSTRUMENT_NAME).trim());
							ps.setString(7, String.valueOf(obj.getInt(FTConstants.N_EXPIRY_DATE)));

							String formatExp = getExpiry(obj.getString(FTConstants.EXPIRY_DATE));

							ps.setString(8, formatExp);
							String strikePrice = formatPrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),
									String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)),
									obj.getString(FTConstants.INSTRUMENT_NAME),
									getPrecision(String.valueOf(obj.getInt(FTConstants.SEGMENT))));

							ps.setString(9, strikePrice);
							String option = getOption(obj.getString(FTConstants.OPTION),
									obj.getString(FTConstants.INSTRUMENT_NAME));
							ps.setString(10, option);
							ps.setString(11, obj.getString(FTConstants.ISIN));
							ps.setString(12, String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)));
							ps.setString(13, String.valueOf(obj.getInt(FTConstants.LOT_SIZE)));

							String dispPriceTick = getDispPriceTick(String.valueOf(obj.getInt(FTConstants.TICK_PRICE)),
									String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)));

							String priceTick = getPriceTick(String.valueOf(obj.getInt(FTConstants.TICK_PRICE)),
									String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)));

							ps.setString(14, priceTick);
							ps.setString(15, obj.getString(FTConstants.SECURITY_DESC));
							ps.setString(16, String.valueOf(obj.getInt(FTConstants.ASSET)));
							ps.setString(17, String.valueOf(obj.getInt(FTConstants.PRICE_NUM)));
							ps.setString(18, String.valueOf(obj.getInt(FTConstants.PRICE_DEN)));
							ps.setString(19, String.valueOf(obj.getInt(FTConstants.INSTRUMENT_TYPE)));
							ps.setString(20, String.valueOf(obj.getInt(FTConstants.MARKET_ALLOWED)));
							ps.setString(21, String.valueOf(obj.getInt(FTConstants.MINIMUM_LOT)));
							ps.setString(22, String.valueOf(obj.getInt(FTConstants.LOW_PRICE_RANGE)));
							ps.setString(23, String.valueOf(obj.getInt(FTConstants.HIGH_PRICE_RANGE)));
							ps.setString(24, String.valueOf(obj.getInt(FTConstants.OPEN_INTEREST)));

							String exch = ExchangeSegment
									.getExchangeName(String.valueOf(obj.getInt(FTConstants.SEGMENT)));

							ps.setString(25, exch);

							String precision = getPrecision(String.valueOf(obj.getInt(FTConstants.SEGMENT)));

							ps.setString(26, precision);
							String assetClass = getAssetClass(obj.getString(FTConstants.INSTRUMENT_NAME));

							ps.setString(27, assetClass);
							String expFromSecurityDesc = getSearchExpiryFromDesc(formatExp,
									obj.getString(FTConstants.SECURITY_DESC),
									obj.getString(FTConstants.INSTRUMENT_NAME),
									formatPrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),
											String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)),
											obj.getString(FTConstants.INSTRUMENT_NAME),
											getPrecision(String.valueOf(obj.getInt(DBConstants.SEGMENT)))),
									getOption(obj.getString(FTConstants.OPTION),
											obj.getString(FTConstants.INSTRUMENT_NAME)));

							String symDetails = getSymbolDetails(String.valueOf(obj.getInt(FTConstants.SEGMENT)),
									obj.getString(FTConstants.INSTRUMENT_NAME), expFromSecurityDesc,
									obj.getString(FTConstants.OPTION),
									String.valueOf(obj.getInt(DBConstants.STRIKE_PRICE)), precision,
									String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), exch);

							ps.setString(28, symDetails);
							ps.setString(29, getSearchExpiry(formatExp, obj.getString(FTConstants.INSTRUMENT_NAME)));
							ps.setString(30, getSearchInstrument(obj.getString(FTConstants.INSTRUMENT_NAME)));

							String searchSymbolDetails = getSearchSymbolDetails(
									obj.getString(FTConstants.INSTRUMENT_NAME), exch, expFromSecurityDesc,

									formatPrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),
											String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)),
											obj.getString(FTConstants.INSTRUMENT_NAME),
											getPrecision(String.valueOf(obj.getInt(DBConstants.SEGMENT)))),
									getOption(obj.getString(FTConstants.OPTION),
											obj.getString(FTConstants.INSTRUMENT_NAME)));

							ps.setString(31, searchSymbolDetails);
							ps.setString(32, String.valueOf(obj.getInt(FTConstants.N_BASE_PRICE)));

							ps.setString(33, dispPriceTick);
							ps.setString(34, obj.getString(FTConstants.ISIN) + "_"
									+ String.valueOf(obj.getInt(FTConstants.SEGMENT)));

							ps.setString(35, expFromSecurityDesc);

							count++;
							ps.addBatch();
						}
					}
					long beforeExecution = System.currentTimeMillis();
					ps.executeBatch();
					conn.commit();
					long afterExecution = System.currentTimeMillis();
					log.debug("total time taken: " + (afterExecution - beforeExecution));
					log.debug("count" + count);
					log.debug("response count" + symbolResp.length());
				}

				catch (Exception e) {

					log.debug("error is" + e.getMessage());

				} finally {
					Helper.closeStatement(ps);
					Helper.closeConnection(conn);
				}
			}
		} catch (Exception e) {

			log.debug("error is" + e.getMessage());
		}
	}

	public static String getPrecision(String marketsegment) throws ClassNotFoundException {

		if (marketsegment.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID)
				|| marketsegment.equalsIgnoreCase(ExchangeSegment.BSECDS_SEGMENT_ID))

			return OrderConstants.PRECISION_4;

		else
			return OrderConstants.PRECISION_2;

	}

	public static String getPriceTick(String priceTick, String locator) {

		BigDecimal dispPriceTick = null;

		try {
			dispPriceTick = new BigDecimal(priceTick).divide(new BigDecimal(locator));

		} catch (Exception e) {
			e.getMessage();
		}

		if (String.valueOf(dispPriceTick).replaceAll("[.]", "").length() == 2) {

			return String.valueOf(dispPriceTick).replaceAll("[0,.]", "").concat("0");
		} else

			return String.valueOf(dispPriceTick).replaceAll("[0,.]", "");

	}

	public static String getSymbolDetails(String segId, String instName, String expDate, String opttype,
			String strikeprice, String prec, String decimallocator, String exchange) {

		String detail = "";
		String strikePrice = "";

		if (instName.length() < 1 || instName.equals(FTConstants.EQUITIES)) { // detail will always be empty for
																				// Equities
			return detail;
		}

		else {
			detail = "" + expDate;
			if (instName.startsWith("OPT")) {

				strikePrice = formatPrice(strikeprice, decimallocator, instName, prec);
				detail = expDate + strikePrice + opttype;

			} else if (instName.startsWith("FUT")) {
				detail = "FUT" + expDate;
			}

		}
		return detail;
	}

	public static String formatPrice(String strikeprice, String decimallocator, String inst, String prec) {
		double d_strikePrice = 0.0;
		double finalstrikeprice = 0.0;
		String price = "";
		if (Double.parseDouble(strikeprice) > 0.0 && inst.startsWith("OPT")) {
			try {
				if (strikeprice.contains("."))
					strikeprice += ".0000";
				else

					d_strikePrice = Double.parseDouble(strikeprice);

				int ilocator = (int) Double.parseDouble(decimallocator);

				finalstrikeprice = d_strikePrice / ilocator;

				price = String.valueOf(Math.round(finalstrikeprice));

			} catch (NumberFormatException nfe) {
			}
		} else
			price = "";

		return price;
	}

	public static String getAssetClass(String instName) {
		if (instName.startsWith("FUT") || instName.startsWith("OPT"))
			return "Derivative";
		else
			return "Cash";
	}

	public static String getExpiry(String expdate) {
		String formattedDate = "";
		try {
			SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat destinationFormat = new SimpleDateFormat("ddMMMyyyy");

			formattedDate = destinationFormat.format(sourceFormat.parse(expdate));
		} catch (Exception e) {
			e.getMessage();
		}
		return formattedDate.toUpperCase();
	}

	public static String getDispPriceTick(String Price, String DecimalLocator) {

		return String.valueOf(new BigDecimal(Price).divide(new BigDecimal(DecimalLocator)));

	}

	public static String getSearchExpiryFromDesc(String exp, String security, String instrument, String strikeprice,
			String option) {
		String date = "";
		String sec = security.replace(".", "");

		HashMap<String, String> month = new HashMap<>();
		month.put("JAN", "1");
		month.put("FEB", "2");
		month.put("MAR", "3");
		month.put("APR", "4");
		month.put("MAY", "5");
		month.put("JUN", "6");
		month.put("JUL", "7");
		month.put("AUG", "8");
		month.put("SEP", "9");
		month.put("OCT", "10");
		month.put("NOV", "11");
		month.put("DEC", "12");

		if (exp.length() > 0) {
			String[] splitno = exp.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

			String[] split = exp.split("(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)");
			String descyearDdatewithsplit = "[A-Z]+[0-9]{2}[D]" + split[0] + "[A-Z0-9]+";
			String descdateDyearwithsplit = "[A-Z]+" + split[0] + "[D]" + split[1].substring(2, 4) + "[A-Z0-9]+";
			String descyearMonthNodatewithsplit = "[A-Z]+[0-9]{2}" + month.get(splitno[1]) + split[0] + "[A-Z0-9]+";
			String descdateMonthNoyearwithsplit = "[A-Z]+" + split[0] + month.get(splitno[1]) + split[1].substring(2, 4)
					+ "[A-Z0-9]+";

			if (exp != null) {

				try {
					SimpleDateFormat sourceFormat = new SimpleDateFormat("ddMMMyyyy");
					SimpleDateFormat destinationFormat = new SimpleDateFormat("dd-MMM-yyyy");
					String formattedDate1 = destinationFormat.format(sourceFormat.parse(exp));

					SimpleDateFormat monthDestinationFormat = new SimpleDateFormat("MMM");
					SimpleDateFormat dateMonthDestinationFormat = new SimpleDateFormat("dd MMM");

					if (sec.matches(descyearDdatewithsplit) || sec.matches(descdateDyearwithsplit)
							|| sec.matches(descyearMonthNodatewithsplit) || sec.matches(descdateMonthNoyearwithsplit)) {
						date = dateMonthDestinationFormat.format(Date.parse(formattedDate1));
					} else {
						date = monthDestinationFormat.format(Date.parse(formattedDate1));
					}
				} catch (Exception e) {
					e.getMessage();
				}
			}
		} else {
			date = "";
		}
		String formattedDate = date.toUpperCase();

		return formattedDate;

	}

	public static String getSearchExpiry(String formattedDate, String inst) {

		String month = "";
		if (formattedDate != null) {
			try {
				SimpleDateFormat sourceFormat = new SimpleDateFormat("ddMMMyyyy");
				SimpleDateFormat destinationFormat = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate1 = destinationFormat.format(sourceFormat.parse(formattedDate));

				SimpleDateFormat futuresDestinationFormat = new SimpleDateFormat("MMM");
				SimpleDateFormat optionsDestinationFormat = new SimpleDateFormat("dd MMM");

				if (inst.startsWith("FUT")) {
					month = futuresDestinationFormat.format(Date.parse(formattedDate1));
				} else {
					month = optionsDestinationFormat.format(Date.parse(formattedDate1));
				}

			} catch (Exception e) {
				e.getMessage();
			}
		} else {
			month = "";
		}
		return month.toUpperCase();
	}

	public static String getSearchInstrument(String inst) {
		String instrument = "";
		if (inst != null) {
			try {
				if (inst.startsWith("FUT")) {
					instrument = "FUT";
				} else if (inst.startsWith("OPT")) {
					instrument = "OPT";

				} else
					instrument = "";
			} catch (Exception e) {
				e.getMessage();
			}
		} else
			instrument = "";
		return instrument;
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

	public static String getSearchSymbolDetails(String instrument, String exch, String exp, String strikeprice,
			String option) {
		String searchDetail = "";

		if (instrument.startsWith("FUT")) {
			searchDetail += exp + " " + getSearchInstrument(instrument);
		} else if (instrument.startsWith("OPT")) {
			searchDetail += exp + " " + strikeprice + " " + option;
		} else {
			searchDetail += "";
		}
		return searchDetail;

	}

}