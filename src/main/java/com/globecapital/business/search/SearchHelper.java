package com.globecapital.business.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.SearchPatternConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.symbology.SymbolRow;
import com.msf.utils.helper.Helper;

public class SearchHelper {

	static String symbol = "";
	static String optionType = "";
	static String month = "";
	static String price = "";
	static String instrument = "";
	static String exchange = "";
	static String company = "";

	public static JSONArray SymbolSearch(String sSymbol) throws SQLException {

		String searchString = sSymbol.replaceAll("\\s", "").toUpperCase();

		JSONArray symbols = new JSONArray();
		String[] splitString = sSymbol.split("\\s+");

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		con = GCDBPool.getInstance().getConnection();
		try {
			if (splitString.length > 3) {
				ps = DerivativeSearchFourWords(searchString, splitString, sSymbol, con);
			}

			else if (splitString.length > 2) {

				ps = DerivativeSearchThreeWords(searchString, splitString, sSymbol, con);

			} else if (splitString.length > 1) {

				ps = DerivativeSearchTwoWords(searchString, splitString, sSymbol, con);

			} else {

				ps = SearchAllSymbols(searchString, splitString, sSymbol, con);

			}

			rs = ps.executeQuery();

			while (rs.next()) {

				SymbolRow row = new SymbolRow(rs);
				symbols.put(row.getMinimisedSymbolRow());
			}
			return symbols;

		}

		finally {
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
			Helper.closeConnection(con);
		}

	}
	
	public static PreparedStatement DerivativeSearchFourWords(String searchString, String[] splitString,
			String sSymbol, Connection con) throws SQLException {

		PreparedStatement ps = null;
		try {
			
			if ((searchString.matches(SearchPatternConstants.SYMBOL_DATE_MONTH_OPTION)	
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DATE_MONTH_OPTION))) {

				symbol = splitString[0];	//Example NIFTY 26 DEC CE
				month = splitString[1];
				price = splitString[2];
				optionType = splitString[3];
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_MONTH_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + month + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%" + price + "%");
				ps.setString(5, optionType+ "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DATE_MONTH_INSTRUMENT)	
					|| searchString.matches(SearchPatternConstants.SYMBOL_DATE_MONTH_INSTRUMENT_SYMBOLS)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DATE_MONTH_INSTRUMENT_SYMBOLS)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				month = splitString[1];
				price = splitString[2];	//Example NIFTY 26 DEC FUT
				optionType = splitString[3];
				
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_MONTH_INSTRUMENT);

				ps.setString(1, symbol + "%"); 	
				ps.setString(2, "%" + month + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%" + price + "%");
				ps.setString(5, optionType + "%");
				return ps;
			} else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_DATE_MONTH)	
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_DATE_MONTH)) {

				symbol = splitString[0];
				month = splitString[2];
				price = splitString[3];	//Example NIFTY 26 DEC FUT
				optionType = splitString[1];
				
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_MONTH_INSTRUMENT);

				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + month + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%" + price + "%");
				ps.setString(5, optionType + "%");
				return ps;
			}  else if ((searchString.matches(SearchPatternConstants.SYMBOL_DATE_MONTH_STRIKE_PRICE)	
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DATE_MONTH_STRIKE_PRICE)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY) ) {

				symbol = splitString[0];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 26 DEC 98
				price = splitString[3];

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_MONTH_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, month + "%");
				ps.setString(3, "%" + optionType + "%");
				ps.setString(4, price + "%");
				return ps;

			} 
			else if ((searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_DATE_MONTH)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKE_PRICE_DATE_MONTH)) && splitString[2].length() == 2 && splitString[1].matches(SearchPatternConstants.NUMBERS)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 98 26 DEC

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_MONTH_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, month + "%");
				ps.setString(3, "%" + optionType + "%");
				ps.setString(4, price + "%");
				return ps;
			} else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_MONTH_OPTION)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKE_PRICE_MONTH_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_STRIKEPRICE_MONTH_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				return ps;
			}
			//Newly added patterns
			else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_DATE_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_EXCHANGE_DATE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_DATE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_MONTH_EXCHANGE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_MONTH_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_EXCHANGE_MONTH)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_MONTH)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_INSTRUMENT_EXCHANGE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_INSTRUMENT_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_EXCHANGE_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_EXCHANGE_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_INSTRUMENT_EXCHANGE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_INSTRUMENT_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_EXCHANGE_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_EXCHANGE_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_INSTRUMENT_DAY)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_INSTRUMENT_MONTH)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_MONTH)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_MONTH_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_MONTH_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_DAY_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_DAY_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4,  month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_DATE_OPTION)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_DATE_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_MONTH_OPTION)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_MONTH_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_OPTION_MONTH)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_OPTION_MONTH)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_OPTION_DAY)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_OPTION_DAY)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_INSTRUMENT_OPTION)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_INSTRUMENT_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_OPTION_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_OPTION_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_OPTION_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_OPTION_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_INSTRUMENT_OPTION)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_INSTRUMENT_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}
			else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_INSTRUMENT_MONTH)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_INSTRUMENT_MONTH)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_INSTRUMENT_DAY)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_INSTRUMENT_DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_DAY_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_DAY_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_MONTH_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_MONTH_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_DATE_STRIKEPRICE)) && splitString[2].length() == 2) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_MONTH_STRIKEPRICE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_MONTH_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_STRIKEPRICE_MONTH)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_MONTH)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_STRIKEPRICE_DAY)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_DAY)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_INSTRUMENT_STRIKEPRICE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_INSTRUMENT_STRIKEPRICE)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_STRIKEPRICE_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_STRIKEPRICE_INSTRUMENT)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_STRIKE_PRICE_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_STRIKE_PRICE_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_INSTRUMENT_STRIKEPRICE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_INSTRUMENT_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_INSTRUMENT_MONTH)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_MONTH)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_INSTRUMENT_DAY)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_DAY_INSTRUMENT)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_DAY_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_MONTH_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_MONTH_DATE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_MONTH_DATE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_MONTH_DATE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				ps.setString(5, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_INSTRUMENT_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_INSTRUMENT_MONTH)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_MONTH_DATE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				ps.setString(5, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_INSTRUMENT_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_INSTRUMENT_DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_MONTH_DATE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				ps.setString(5, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_DAY_INSTRUMENT)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_DAY_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_MONTH_DATE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				ps.setString(5, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_EXCHANGE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_OPTION_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_OPTION_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_INSTRUMENT_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_INSTRUMENT_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_OPTION_INSTRUMENT)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_OPTION_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_INSTRUMENT_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_EXCHANGE_INSTRUMENT)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_EXCHANGE_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_INSTRUMENT_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_INSTRUMENT_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_INSTRUMENT_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_INSTRUMENT_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_STRIKEPRICE_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_INSTRUMENT_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_STRIKEPRICE_INSTRUMENT)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_EXCHANGE_INSTRUMENT)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_INSTRUMENT_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_STRIKEPRICE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_INSTRUMENT_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_INSTRUMENT_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_STRIKE_PRICE_INSTRUMENT)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_STRIKE_PRICE_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_OPTION_INSTRUMENT)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_OPTION_INSTRUMENT)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_INSTRUMENT_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_EXCHANGE_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_EXCHANGE_MONTH)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_MONTH_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_MONTH_EXCHANGE)) && splitString[1].matches(SearchPatternConstants.DAY) && splitString[1].length() == 2) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_DAY_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_DAY_MONTH)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_MONTH_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_MONTH_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_MONTH_EXCHANGE_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_EXCHANGE_DAY))) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_DAY_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_DAY_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_EXCHANGE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_EXCHANGE_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_OPTION_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_OPTION_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_DAY_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_DAY_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_OPTION_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_OPTION_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_EXCHANGE_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_EXCHANGE_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_DAY_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_DAY_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_EXCHANGE_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_EXCHANGE_STRIKEPRICE)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_STRIKEPRICE_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_STRIKEPRICE_EXCHANGE)) && splitString[1].matches(SearchPatternConstants.DAY) && splitString[1].length() == 2) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_DAY_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_DAY_STRIKEPRICE)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_STRIKEPRICE_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_DAY)) && splitString[3].matches(SearchPatternConstants.DAY) && splitString[3].length() == 2) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_DAY_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_DAY_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_EXCHANGE_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_OPTION_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_OPTION_MONTH)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DATE_MONTH_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DATE_MONTH_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_DAY_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_DAY_MONTH)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_MONTH_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_MONTH_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_OPTION_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_OPTION_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_DAY_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_DAY_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_STRIKEPRICE_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_STRIKEPRICE_MONTH)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_STRIKEPRICE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_DATE_MONTH_STRIKE_PRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DATE_MONTH_STRIKE_PRICE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_STRIKEPRICE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_MONTH_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_STRIKEPRICE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_STRIKE_PRICE_DAY) && splitString[1].matches(SearchPatternConstants.MONTHS)&& splitString[3].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_STRIKEPRICE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_MONTH_DAY_STRIKEPRICE)) && splitString[2].length() == 2 && splitString[2].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_STRIKEPRICE_MONTH);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_OPTION_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_OPTION_STRIKEPRICE))) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY_STRIKEPRICE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_STRIKEPRICE_OPTION)) && splitString[1].length() == 2 && splitString[1].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_OPTION_DAY_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_DAY_STRIKEPRICE)) && splitString[2].length() == 2 && splitString[2].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_OPTION_STRIKE_PRICE_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_STRIKE_PRICE_DAY)) && 
					splitString[3].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if ((searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_DAY_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_DAY_OPTION)) && splitString[2].length() == 2 && splitString[2].matches(SearchPatternConstants.DAY)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_OPTION_DAY)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_OPTION_DAY)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_DAY_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, price + "%");
				ps.setString(4, "%"+ month + "%");
				ps.setString(5, optionType + "%");
				ps.setString(6, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_EXCHANGE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_EXCHANGE_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_OPTION_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_OPTION_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_MONTH_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_MONTH_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_OPTION_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_OPTION_MONTH)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_MONTH_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_MONTH_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_EXCHANGE_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_EXCHANGE_MONTH)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_OPTION);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, "%"+ optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_EXCHANGE_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_EXCHANGE_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_STRIKE_PRICE_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_STRIKE_PRICE_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_MONTH_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_MONTH_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_STRIKEPRICE_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_MONTH)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_EXCHANGE_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_MONTH)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_MONTH_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKE_PRICE_MONTH_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_EXCHANGE_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_OPTION_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_OPTION_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION_STRIKE_PRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_STRIKE_PRICE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_STRIKE_PRICE_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION_STRIKE_PRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_MONTH_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_MONTH_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION_STRIKE_PRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_STRIKE_PRICE_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_STRIKE_PRICE_MONTH)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION_STRIKE_PRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_OPTION_MONTH)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_OPTION_MONTH)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION_STRIKE_PRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_MONTH_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKE_PRICE_MONTH_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION_STRIKE_PRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_OPTION_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_OPTION_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[2];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_STRIKEPRICE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_OPTION)) {

				symbol = splitString[0];
				price = splitString[1];
				month = splitString[3];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_EXCHANGE_STRIKEPRICE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_EXCHANGE_STRIKEPRICE)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[1];
				optionType = splitString[3];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_STRIKE_PRICE_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_STRIKE_PRICE_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[1];
				optionType = splitString[2];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_OPTION_EXCHANGE)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_OPTION_EXCHANGE)) {

				symbol = splitString[0];
				price = splitString[3];
				month = splitString[2];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_EXCHANGE_OPTION)
					||searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_OPTION)) {

				symbol = splitString[0];
				price = splitString[2];
				month = splitString[3];
				optionType = splitString[1];	//Example NIFTY 80 DEC CE

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + price + "%");
				ps.setString(3, "%"+ month + "%");
				ps.setString(4, optionType + "%");
				return ps;
			}else {
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_COMPANY);
				ps.setString(1, sSymbol + "%");
				ps.setString(2, sSymbol + "%");
				return ps;
			}
			
		} catch (Exception e) {
			e.getMessage();
		}

		return ps;
	}

	 public static PreparedStatement DerivativeSearchThreeWords(String searchString, String[] splitString,
	            String sSymbol, Connection con) throws SQLException {

	        PreparedStatement ps = null;
	        try {

	            if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_OPTION)	//Example NIFTY JAN CE
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_OPTION)) {

	                symbol = splitString[0];
	                month = splitString[1];
	                optionType = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + month + "%");
	                ps.setString(3, optionType);
	                return ps;
	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_MONTH)	//Example NIFTY CE JAN
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_MONTH)) {

	                symbol = splitString[0];
	                optionType = splitString[1];
	                month = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + month + "%");
	                ps.setString(3, optionType);
	                return ps;

	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_INSTRUMENT)	
	                    || searchString.matches(SearchPatternConstants.SYMBOL_MONTH_INSTRUMENT_SYMBOLS)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_INSTRUMENT_SYMBOLS)) {

	                symbol = splitString[0];
	                month = splitString[1];
	                optionType = splitString[2];	//Example NIFTY JAN FUT

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_INSTRUMENT);

	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + month + "%");
	                ps.setString(3,  optionType + "%");
	                
	                return ps;
	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_MONTH)	
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_MONTH)) {

	                symbol = splitString[0];
	                month = splitString[2];		//Example NIFTY FUT JAN
	                optionType = splitString[1];
	                
	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_INSTRUMENT);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + month + "%");
	                ps.setString(3,  optionType + "%");
	                
	                return ps;
	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_OPTION) //unneccesary pattern
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_OPTION)) {

	                symbol = splitString[0];
	                instrument = splitString[1];	//Example NIFTY FUT CE
	                optionType = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, instrument);
	                ps.setString(3, optionType+ "%");
	                return ps;

	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_INSTRUMENT) //unneccesary pattern
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_INSTRUMENT)) {

	                symbol = splitString[0];	//Example NIFTY CE FUT
	                optionType = splitString[1];
	                instrument = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, instrument);
	                ps.setString(3, optionType+ "%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_DATE_MONTH)	
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DATE_MONTH)) {
		
					symbol = splitString[0];
					month = splitString[1];
					optionType = splitString[2];
					
					ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_MONTH);
					ps.setString(1, symbol + "%");
					ps.setString(2, "%" + month + "%");
					ps.setString(3, month + "%");
					ps.setString(4, "%" + optionType + "%");
			
					return ps;
					
				}else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_STRIKE_PRICE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_STRIKE_PRICE)) {

	                symbol = splitString[0];
	                month = splitString[1];		//Example NIFTY DEC 98
	                price = splitString[2];
                
					ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_STRIKEPRICE);
					ps.setString(1, symbol + "%");
					ps.setString(2, "%" + month + "%");
					ps.setString(3, "%" + price + "%");
					ps.setString(4, price + "%");

					return ps;

	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_MONTH)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKE_PRICE_MONTH)){

	                symbol = splitString[0];
	                price = splitString[1];		//Example NIFTY 98 DEC will match if strikeprice > 31, or else it matches date
	                month = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_STRIKEPRICE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + month + "%");
	                ps.setString(3, "%" + price + "%");
	                ps.setString(4, price + "%");
	                return ps;
	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE_OPTION)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKE_PRICE_OPTION)) {

	                symbol = splitString[0];
	                price = splitString[1];		//Example NIFTY 98 CE
	                optionType = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_STRIKEPRICE_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + price + "%");
	                ps.setString(3, price + "%");
	                ps.setString(4, optionType+"%");
	                return ps;

	            } else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_STRIKE_PRICE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_STRIKE_PRICE)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_STRIKEPRICE_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + price + "%");
	                ps.setString(3, price+"%");
	                ps.setString(4, optionType+"%");
	                return ps;

	            } 
//Newly added search patterns	            
	            else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_INSTRUMENT)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_INSTRUMENT)) {

	                symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_INSTRUMENT);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + price + "%");
	                ps.setString(3, "%" +optionType+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_DAY)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_DAY)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" +optionType+"%");
	                ps.setString(3, "%" + price + "%");
	                ps.setString(4, price + "%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_MONTH)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_MONTH)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_OPTION)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_OPTION)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE_STRIKEPRICE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_STRIKEPRICE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, price+"%");
	                return ps;

	            }else if ((searchString.matches(SearchPatternConstants.SYMBOL_MONTH_DAY)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_DAY))) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH_STRIKEPRICE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_MONTH_EXCHANGE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH_EXCHANGE)) {

	                symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_DAY)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_DAY)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_STRIKEPRICE_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + price + "%");
	                ps.setString(3, price+"%");
	                ps.setString(4, "%" +optionType+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION_EXCHANGE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION_EXCHANGE)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_OPTION);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + price + "%");
	                ps.setString(3, "%" + optionType+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_DAY)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_DAY)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_EXCHANGE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_EXCHANGE)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_INSTRUMENT);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + price + "%");
	                ps.setString(3, "%" +optionType+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT_STRIKEPRICE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_INSTRUMENT)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_INSTRUMENT)) {

	                symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_MONTH)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_MONTH)) {

	                symbol = splitString[0];
	                price = splitString[1];
	                optionType = splitString[2];	//Example NIFTY CE 98

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_MONTH);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" +price+"%");
	                ps.setString(3, price+"%");
	                ps.setString(4, "%" + optionType + "%");
	                
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_EXCHANGE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_EXCHANGE)) {

	                symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_OPTION)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_OPTION)) {

	                symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_OPTION_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_DAY_STRIKEPRICE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_DAY_STRIKEPRICE)) {

	                symbol = splitString[0];
	                optionType = splitString[1];	//Example NIFTY CE 98
	                price = splitString[2];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_STRIKEPRICE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, optionType + "%");
	                ps.setString(4, price+"%");
	                ps.setString(5, "%" + price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_INSTRUMENT)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT)) {

	                symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_DAY)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_DAY)) {

	                symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_DATE_STRIKEPRICE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, optionType + "%");
	                ps.setString(4, price+"%");
	                ps.setString(5, "%" + price+"%");
	                return ps;

	            }else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKEPRICE_EXCHANGE)
	                    || searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE)) {

	            	symbol = splitString[0];
	                optionType = splitString[2];	//Example NIFTY CE 98
	                price = splitString[1];

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE_DATE);
	                ps.setString(1, symbol + "%");
	                ps.setString(2, "%" + optionType + "%");
	                ps.setString(3, "%" +price+"%");
	                ps.setString(4, price+"%");

	            }
	            else if (sSymbol.toUpperCase().matches(SearchPatternConstants.SYMBOL_COMPANY)) {

	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_COMPANY);
	                ps.setString(1, sSymbol.toUpperCase() + "%");
	                ps.setString(2, sSymbol.toUpperCase() + "%");
	                return ps;
	            } else {
	                ps = con.prepareStatement(DBQueryConstants.SYMBOL_COMPANY);
	                ps.setString(1, sSymbol + "%");
	                ps.setString(2, sSymbol + "%");
	                return ps;
	            }

	        } catch (Exception e) {
	            e.getMessage();
	        }

	        return ps;
	    }
	
	public static PreparedStatement DerivativeSearchTwoWords(String searchString, String[] splitString, String sSymbol,
			Connection con) throws SQLException {

		PreparedStatement ps = null;
		try {
			if(splitString[1].length() == 1 && splitString[1].matches(SearchPatternConstants.SYMBOL_COMPANY_1)) {
				symbol = splitString[0];
				optionType = splitString[1];
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_COMPANY_1);	//Example NIFTY N
				ps.setString(1, symbol + "%");
				ps.setString(2, "%"+optionType + "%");
			}
			else if (searchString.matches(SearchPatternConstants.SYMBOL_INSTRUMENT)	//Example NIFTY FUT
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_INSTRUMENT_SYMBOLS)) {

				symbol = splitString[0];
				//To enable searching even when only one character is present
				optionType = splitString[1];
				if(optionType.toUpperCase().matches(SearchPatternConstants.EXCHANGE_FORMAT)) { 
					ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE);	//Example NIFTY NFO
					ps.setString(2, "%"+optionType + "%");
				}	
				else {
					ps = con.prepareStatement(DBQueryConstants.SYMBOL_INSTRUMENT);
					ps.setString(2, optionType + "%");
				}
				ps.setString(1, symbol + "%");

				return ps;
			} else if ((searchString.matches(SearchPatternConstants.SYMBOL_DAY))&&Integer.parseInt(splitString[1])<=31) {
				symbol = splitString[0];		//Example NIFTY 26
				month = splitString[1];
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH);

				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + month + "%");
				ps.setString(3, month + "%");
				return ps;

			}else if (searchString.matches(SearchPatternConstants.SYMBOL_EXCHANGE)) {
				symbol = splitString[0];
				exchange = splitString[1];		//Example NIFTY NFO

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_EXCHANGE);
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + exchange + "%");
				return ps;

			} else if (searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_MONTH)) {
				symbol = splitString[0];		//Example NIFTY NOV
				month = splitString[1];
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_MONTH);

				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + month + "%");
				ps.setString(3, month + "%");
				return ps; 
			}else if (searchString.matches(SearchPatternConstants.SYMBOL_OPTION)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_OPTION)) {

				symbol = splitString[0];
				if (splitString[1].length() > 2) {
					optionType = splitString[1].substring(0, 2);
				} else {
					optionType = splitString[1];
				}
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_OPTION);	//Example NIFTY CE
				ps.setString(1, symbol + "%");
				ps.setString(2, "%" + optionType + "%");
				return ps;
			} else if (searchString.matches(SearchPatternConstants.SYMBOL_STRIKE_PRICE)
					|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL_STRIKE_PRICE)) {

				symbol = splitString[0];	//Example GBPINR 76.2
				price = splitString[1];

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_STRIKEPRICE);
				ps.setString(1, symbol + "%");
				ps.setString(2, price + "%");
				return ps;
			} else if (sSymbol.toUpperCase().matches(SearchPatternConstants.SYMBOL_COMPANY)) {

				ps = con.prepareStatement(DBQueryConstants.SYMBOL_COMPANY);	//Example NIFTY
				ps.setString(1, sSymbol.toUpperCase() + "%");
				ps.setString(2, sSymbol.toUpperCase() + "%");
				return ps;
			} else {
				ps = con.prepareStatement(DBQueryConstants.SYMBOL_COMPANY);
				ps.setString(1, sSymbol + "%");
				ps.setString(2, sSymbol + "%");
				return ps;
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return ps;
	}

	public static PreparedStatement SearchAllSymbols(String searchString, String[] splitString, String sSymbol,
			Connection con) throws SQLException {

		PreparedStatement ps = null;

		if (searchString.matches(SearchPatternConstants.SYMBOL)
				|| searchString.matches(SearchPatternConstants.NUMBER_SYMBOL)) {
			symbol = splitString[0];

			ps = con.prepareStatement(DBQueryConstants.SYMBOL_COMPANY);
			ps.setString(1, symbol + "%");
			ps.setString(2, sSymbol + "%");
		} else {
			symbol = splitString[0];
			ps = con.prepareStatement(DBQueryConstants.SYMBOL);
			ps.setString(1, symbol + "%");
			return ps;
		}

		return ps;

	}
}
