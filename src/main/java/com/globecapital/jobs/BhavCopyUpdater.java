package com.globecapital.jobs;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class BhavCopyUpdater  
{    
	//Usage - java -cp com.globecapital.jobs.BhavCopyUpdater config.properties bhavcopyfile.csv BSE
	public static void main(String[] args)  
	{  
		try  
		{  
			String config_file = args[0];
			String dumpFile = args[1];
			String query = "UPDATE NSE_QUOTE set PRE_CLOSE_PRICE = ?, UPDATED_AT = NOW() where ISIN = ?";
	        AppConfig.loadFile(config_file);
	        QuoteDataDBPool.initDataSource(AppConfig.getProperties());
	        CSVReader reader = new CSVReader(new FileReader(dumpFile));
	        if(args[2].equalsIgnoreCase("MCX")) {
	        	query = "UPDATE MFO_QUOTE set PRE_CLOSE_PRICE = ?, UPDATED_AT = NOW() where SYMBOL = ?";
	        	updatePreClosePriceBySymbol(query, reader, Arrays.asList(1, 2, 3, 4, 5, 9));
	        }else {
	        	query = "UPDATE BSE_QUOTE set PRE_CLOSE_PRICE = ?, UPDATED_AT = NOW() where ISIN = ?";
	        	updatePreClosePriceByISIN(query, reader, Arrays.asList(7, 14));
	        }
		}  
		catch (Exception e)   
		{  
			e.printStackTrace();  
		}  
	}

	private static void updatePreClosePriceByISIN(String query, CSVReader reader, List<Integer> columnList) throws Exception {
		String[] nextLine;
		Connection conn = null;
		PreparedStatement ps = null;
		conn = QuoteDataDBPool.getInstance().getConnection();
		conn.setAutoCommit(false);
		int count = 0;
		ps = conn.prepareStatement(query);
		while ((nextLine = reader.readNext()) != null)  
		{  
			if(count!=0) {
				ps.setString(1, nextLine[columnList.get(0)]);
				ps.setString(2, nextLine[columnList.get(1)]);
			    ps.addBatch();
		    }
			count++;
		}
		ps.executeBatch();
		conn.commit();
	}
	
	private static void updatePreClosePriceBySymbol(String query, CSVReader reader, List<Integer> columnList) throws Exception {
		String[] nextLine;
		Connection conn = null;
		PreparedStatement ps = null;
		conn = QuoteDataDBPool.getInstance().getConnection();
		conn.setAutoCommit(false);
		int count = 0;
		ps = conn.prepareStatement(query);
		while ((nextLine = reader.readNext()) != null)  
		{  
			if(count!=0) {
				String instrument = nextLine[columnList.get(0)];
				String expiry = nextLine[columnList.get(2)];
				String symbol = "";
				SimpleDateFormat source = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FORMAT);
				SimpleDateFormat destination = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
				String expiryDate = destination.format(source.parse(expiry));
				ps.setString(1, nextLine[columnList.get(5)]);
				if(!instrument.equals(InstrumentType.FUTCOM)) {
					symbol = InstrumentType.OPTCOM + "_"+nextLine[columnList.get(1)].trim()+"_"+ExchangeSegment.MCX+"_"+expiryDate+"_"+ (int)(Double.parseDouble(nextLine[columnList.get(4)]))+"_"+nextLine[columnList.get(3)];
				}else {
					symbol = InstrumentType.FUTCOM+ "_"+nextLine[columnList.get(1)].trim()+"_"+ExchangeSegment.MCX+"_"+expiryDate.toString();
				}
				ps.setString(2, symbol);
			    ps.addBatch();
		    }
			count++;
		}
		ps.executeBatch();
		conn.commit();
	}
}  