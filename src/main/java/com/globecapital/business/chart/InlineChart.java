package com.globecapital.business.chart;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;

public class InlineChart {

	public static JSONObject getInlineChart(String sSymbolToken) throws Exception {
		
		JSONObject chartObj = new JSONObject();
		
		JSONArray pointsArr = new JSONArray();
		double dDayMin = Double.MAX_VALUE, dDayMax = 0;
		
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		String sMarketSegID = symRow.getMktSegId();
		
		String sStartDateTime = DateUtils.formatDate(DateUtils.getCurrentDate(), DeviceConstants.TO_DATE_FORMAT
				, DeviceConstants.FROM_DATE_FORMAT) + " 00:00:00";
		String sEndDateTime = DateUtils.getTomorrowDate(DeviceConstants.FROM_DATE_FORMAT) + " 00:00:00";
		
		JSONArray chartArr = new JSONArray();
		
		if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true")) 
			chartArr = Chart.getChartExch(sSymbolToken, sStartDateTime, sEndDateTime, "1m");
		else
			chartArr = Chart.getChartOMDF(sSymbolToken, sStartDateTime, sEndDateTime, "1m");
		QuoteDetails quote = Quote.getLTP(sSymbolToken, symRow.getMappingSymbolUniqDesc());
		pointsArr.put(Double.parseDouble(quote.sOpenPrice));
		
		Date startDateTime = loadChartStartTime(sMarketSegID);
			
		for(int i = 0; i < chartArr.length(); i++)
		{
			JSONObject obj = chartArr.getJSONObject(i);
			double d = Double.parseDouble(obj.getString(DeviceConstants.CLOSE));
			String date = "";
			SimpleDateFormat sdf = null;
			date = obj.getString(DeviceConstants.DT)+" UTC";
			sdf = new SimpleDateFormat(DeviceConstants.DATE_FORMAT+ " Z");
			Date responseDate = sdf.parse(date);
			
			if(d > 0 && startDateTime.compareTo(responseDate) == -1) {
				pointsArr.put(d);
				if(d > dDayMax)
					dDayMax = d;
				if(d < dDayMin)
					dDayMin = d;
			}
		}
		
		if(pointsArr.length() <= 1 )
			throw new GCException(InfoIDConstants.NO_DATA);

		chartObj.put(SymbolConstants.SYMBOL_OBJ, 
				symRow.getMinimisedSymbolRow().get(SymbolConstants.SYMBOL_OBJ));
		chartObj.put(DeviceConstants.DATA_POINTS, pointsArr);
		chartObj.put(DeviceConstants.PREV_CLOSE, Double.parseDouble(quote.sPreviousClose));
		
		if(dDayMax > Double.parseDouble(quote.sPreviousClose))
			chartObj.put(DeviceConstants.DAY_MAX, Double.parseDouble(quote.sPreviousClose));
		else
			chartObj.put(DeviceConstants.DAY_MAX, dDayMax);
		
		if(dDayMin > Double.parseDouble(quote.sPreviousClose))
			chartObj.put(DeviceConstants.DAY_MIN, Double.parseDouble(quote.sPreviousClose));
		else
			chartObj.put(DeviceConstants.DAY_MIN, dDayMin);
		
		if(sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID) 
				|| sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID)
				|| sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			chartObj.put(DeviceConstants.MARKET, DeviceConstants.EQUITY_AND_FNO);
		else if(sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID)
				|| sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
			chartObj.put(DeviceConstants.MARKET, DeviceConstants.COMMODITY);
		else if(sMarketSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID)
				|| sMarketSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
			chartObj.put(DeviceConstants.MARKET, DeviceConstants.CURRENCY);
		
		
		return chartObj;
	}

	private static Date loadChartStartTime(String sMarketSegID) throws AppConfigNoKeyFoundException {
		int hour = 0, minute = 0;
		Date startDateTime = new Date();
		startDateTime.setSeconds(59);
		if(ExchangeSegment.isEquitySegment(sMarketSegID)) {
			hour = Integer.parseInt(AppConfig.getValue("market_open_hour_eq"));
			minute = Integer.parseInt(AppConfig.getValue("market_open_minute_eq"));
			startDateTime.setHours(hour);
			startDateTime.setMinutes(minute);
		}else {
			hour = Integer.parseInt(AppConfig.getValue("market_open_hour_other"));
			minute = Integer.parseInt(AppConfig.getValue("market_open_minute_other"));
			startDateTime.setHours(hour);
			startDateTime.setMinutes(minute);
		}
		return startDateTime;
	}

}
