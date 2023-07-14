package com.globecapital.jobs;

import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.db.QuoteDataDBPool;
import com.opencsv.CSVReader;  
public class MCXBhavcopyUpdater  
{    
	public static void main(String[] args)  
	{  
		try  
		{  
			String config_file = args[0];
			String dumpFile = args[1];
			String query = "";
	        AppConfig.loadFile(config_file);
	        QuoteDataDBPool.initDataSource(AppConfig.getProperties());
	        CSVReader reader = new CSVReader(new FileReader(dumpFile));
	        query = "UPDATE MFO_QUOTE set PREV_OPENINTEREST = ? , PRE_CLOSE_PRICE = ? where SYMBOL = ?";
	        updateMCXBhavcopy(query, reader, Arrays.asList(15, 5, 4, 6, 9, 8, 14), DBConstants.EXPIRY_DATE_FORMAT, ExchangeSegment.MCX);
		}  
		catch (Exception e)   
		{  
			e.printStackTrace();  
		}  
	}

	private static void updateMCXBhavcopy(String queryAll, CSVReader reader, List<Integer> columnList, String sourceFormat, String exch) throws Exception {
		String[] nextLine;
		Connection conn = null;
		PreparedStatement ps = null;
		conn = QuoteDataDBPool.getInstance().getConnection();
		conn.setAutoCommit(false);
		int count = 0;
		ps = conn.prepareStatement(queryAll);
		while ((nextLine = reader.readNext()) != null)  
		{  
			String instrument = nextLine[columnList.get(2)];
			if(instrument.equalsIgnoreCase(InstrumentType.FUTIDX) || instrument.equalsIgnoreCase(InstrumentType.FUTCOM) || instrument.equalsIgnoreCase(InstrumentType.OPTCOM) || instrument.equalsIgnoreCase("OPTFUT")) {
				//condition added to skip processing of headers
				BigDecimal openInterest = new BigDecimal(nextLine[columnList.get(0)]);
				String symbol = nextLine[columnList.get(1)];
				String expiry = nextLine[columnList.get(3)];
				SimpleDateFormat source = new SimpleDateFormat(sourceFormat);
				SimpleDateFormat destination = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
				String expiryDate = destination.format(source.parse(expiry));
				symbol = frameSymbolByExchange(exch, instrument.trim(), nextLine, columnList, expiryDate);
				String preClosePrice = nextLine[columnList.get(6)];
				ps.setBigDecimal(1, openInterest);
				ps.setString(2, preClosePrice);
				ps.setString(3, symbol);
				ps.addBatch();
		    }else {
		    	continue;
		    }
			count++;
		}
		ps.executeBatch();
		conn.commit();
	}

	private static String frameSymbolByExchange(String exch, String instrument, String[] nextLine, List<Integer> columnList, String expiryDate) {
		String symbol = "";
		if(instrument.equals(InstrumentType.FUTCOM)) {
			symbol = InstrumentType.FUTCOM+ "_"+nextLine[columnList.get(1)].trim()+"_"+ExchangeSegment.MCX+"_"+expiryDate.toString();
		}else if(instrument.equals(InstrumentType.FUTIDX)) {
			symbol = InstrumentType.FUTIDX+ "_"+nextLine[columnList.get(1)].trim()+"_"+ExchangeSegment.MCX+"_"+expiryDate.toString();
		}else {
			symbol = InstrumentType.OPTCOM + "_"+nextLine[columnList.get(1)].trim()+"_"+ExchangeSegment.MCX+"_"+expiryDate+"_"+ (int)(Double.parseDouble(nextLine[columnList.get(5)]))+"_"+nextLine[columnList.get(4)].trim();
		}
		return symbol;
	}
	
	public static String formatStrikePrice(String strikePrice) {
		Double strikePriceDouble = Double.parseDouble(strikePrice);
    	DecimalFormat df ;
    	if(strikePrice.contains(".")) {
    		if(strikePrice.split("\\.")[1].length()==1)
    			df = new DecimalFormat("#.#");
    		else
    			df = new DecimalFormat("#.##");
		}else
			return strikePrice;

    	df.setRoundingMode(RoundingMode.FLOOR);

		return df.format(strikePriceDouble);
	}
}  