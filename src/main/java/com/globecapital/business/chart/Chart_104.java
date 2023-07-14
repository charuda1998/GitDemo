package com.globecapital.business.chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.business.market.Indices;
import com.globecapital.config.AppConfig;
import com.globecapital.config.IndicesMapping;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.db.QuoteDataDBPool;
import com.globecapital.db.RedisPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.ChartUtils;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.google.gson.Gson;
import com.msf.cmots.api.company_chart.CompanyChartDataList;
import com.msf.cmots.api.company_chart.GetHistoricalChartEquity;
import com.msf.cmots.exception.CMOTSException;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Chart_104 {

	private static Logger log = Logger.getLogger(Chart_104.class);
	
	private static String sChartStartDate;
	
	public static void loadChartDate() throws AppConfigNoKeyFoundException
	{
		int iCountStartDay = AppConfig.getIntValue("chart_1M_past_days");
		setChartStartDate(DateUtils.getNthDateFromTodayDate(DeviceConstants.TO_DATE_FORMAT, 
				iCountStartDay * (-1)));
	}

	public static JSONArray getIntradayChart(String sSymbolToken, String sFromDate, String sToDate, String sInterval)
			throws Exception {

		String sMarketSegID = "", mappingSymbolUniqDesc = "";
		int precision = 0;
		if(SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		} else if(Indices.isValidIndex(sSymbolToken)) {
			SymbolRow  symRow = Indices.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		}
		
		Connection conn = null;
		PreparedStatement cs = null;
		ResultSet res = null;

		String query = DBQueryConstants.GET_CHART_POINTS;

		ArrayList<ChartData> resultData = new ArrayList<ChartData>();

		try {

			conn = QuoteDataDBPool.getInstance().getConnection();
			
			if (sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
				query = DBQueryConstants.GET_NSE_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
				query = DBQueryConstants.GET_NFO_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
				query = DBQueryConstants.GET_BSE_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
				query = DBQueryConstants.GET_MCX_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
				query = DBQueryConstants.GET_NCDEX_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
				query = DBQueryConstants.GET_NSECDS_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
				query = DBQueryConstants.GET_BSECDS_CHART;
			
			cs = conn.prepareStatement(query);
			
			cs.setString(1, mappingSymbolUniqDesc);
			cs.setString(2, sFromDate);
			cs.setString(3, sToDate);

			log.debug("Intraday Query = "+query);
			log.debug("Params:" + mappingSymbolUniqDesc + ", " + sSymbolToken + ", " + sFromDate + ", " + sToDate);

			res = cs.executeQuery();

			while (res.next()) {
				if(res.getDouble(DBConstants.OPEN) > 0 && res.getDouble(DBConstants.HIGH) > 0
				&& res.getDouble(DBConstants.LOW) > 0 && res.getDouble(DBConstants.CLOSE) > 0) {
					ChartData cData = new ChartData();
	
					cData.setOpen(res.getDouble(DBConstants.OPEN));
					cData.setHigh(res.getDouble(DBConstants.HIGH));
					cData.setLow(res.getDouble(DBConstants.LOW));
					cData.setClose(res.getDouble(DBConstants.CLOSE));
					cData.setVolume(res.getLong(DBConstants.VOLUME));
					cData.setTimestamp(res.getTimestamp(DBConstants.TIME));
	
					resultData.add(cData);
				}

			}

		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(cs);
			Helper.closeConnection(conn);
		}
		
		int resolutionInMins = ChartUtils.getResolution(sInterval);

		JSONArray finalArr = new JSONArray();

		if (resolutionInMins == 1440 && resultData.size() > 0) {
			finalArr = getOneDayChart(resultData, precision);
		}
		else {
			String replaceChar = ".";
			int counter = precision;
			while(counter>0) {
				counter--;
				replaceChar+="0";
			}
			for (int i = 0; i < resultData.size(); i++) {
				JSONArray tempArr = new JSONArray();
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getHigh()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getLow()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getClose()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(resultData.get(i).getVolume());
				tempArr.put(DateUtils.formatTimeInUTC(resultData.get(i).getTimestamp(), DeviceConstants.DATE_FORMAT).replace("-", "/"));
				
				finalArr.put(tempArr); 
			}
		}

		return finalArr;

	}

	public static JSONArray getOneDayChart(ArrayList<ChartData> resultData, int precision)
			throws JSONException, Exception {
		JSONArray finalArr = new JSONArray();

		double tmpHigh = 0;
		double tmpLow = Integer.MAX_VALUE;
		long tmpVolume = 0;
		JSONArray tempArr = new JSONArray();
		int counter = precision;
		String replaceChar = ".";
		while(counter>0) {
			counter--;
			replaceChar+="0";
		}

		int prevDate = 0;
		Timestamp ts = null;
		boolean isFirst = true;
		for (int i = 0; i < resultData.size(); i++) {
			
			int dt = resultData.get(i).getTimestamp().getDate();
			if(prevDate == 0)
				prevDate = dt;
			if(dt == prevDate) {
				ts = resultData.get(i).getTimestamp();
				if (isFirst) {
					tempArr.put(0,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
					isFirst = false;
				}
	
				if (tmpHigh < resultData.get(i).getHigh()) {
					tmpHigh = resultData.get(i).getHigh();
					tempArr.put(1,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getHigh()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				}
	
				if (tmpLow > resultData.get(i).getLow()) {
					tmpLow = resultData.get(i).getLow();
					tempArr.put(2,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getLow()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				}
				
				tmpVolume += resultData.get(i).getVolume();
			}else {
				prevDate = 0;
				tmpHigh = 0;
				tmpLow = Integer.MAX_VALUE;
				isFirst = true;
				dt = resultData.get(i).getTimestamp().getDate();
				if(prevDate == 0)
					prevDate = dt;
				tempArr.put(3,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i-1).getClose()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(4,Long.parseLong(String.valueOf(tmpVolume).replace(",", "")));
				tmpVolume = 0;
				if(Objects.isNull(ts))
					ts = resultData.get(i).getTimestamp();
				tempArr.put(5,DateUtils.formatTimeInUTC(new Date(ts.getTime()), DeviceConstants.DATE_FORMAT).replace("-", "/"));

				finalArr.put(tempArr);
				
				if(resultData.size()-1 != i)
					ts = null;
				tempArr = new JSONArray();
				if (isFirst) {
					tempArr.put(0,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
					isFirst = false;
				}
	
				if (tmpHigh < resultData.get(i).getHigh()) {
					tmpHigh = resultData.get(i).getHigh();
					tempArr.put(1,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getHigh()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				}
	
				if (tmpLow > resultData.get(i).getLow()) {
					tmpLow = resultData.get(i).getLow();
					tempArr.put(2,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getLow()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				}
	
				tmpVolume += resultData.get(i).getVolume();
			
				
			}
		}
		if(!tempArr.isEmpty()) {
			tempArr.put(3,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(resultData.size()-1).getClose()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
			tempArr.put(4,Long.parseLong(String.valueOf(tmpVolume).replace(",", "")));
			tempArr.put(5,DateUtils.formatTimeInUTC(new Date(ts.getTime()), DeviceConstants.DATE_FORMAT).replace("-", "/"));
			finalArr.put(tempArr);
		}
		return finalArr;
	}

	public static JSONArray getChart(String sSymbolToken, String sFromDate, String sToDate, String sInterval)
			throws Exception {
		
		String sMarketSegID = "", exchange = "", mappingSymbolUniqDesc = "";
		int precision = 0;
		if(SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			exchange = symRow.getExchange();
			mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		} else if(Indices.isValidIndex(sSymbolToken)) {
			SymbolRow symRow = Indices.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			exchange = symRow.getExchange();
			mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		}

		Connection conn = null;
		PreparedStatement cs = null;
		ResultSet res = null;

		String query = DBQueryConstants.GET_CHART_POINTS;

		ArrayList<ChartData> resultData = new ArrayList<ChartData>();
		;

		try {

			conn = QuoteDataDBPool.getInstance().getConnection();

			if (sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
				query = DBQueryConstants.GET_NSE_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
				query = DBQueryConstants.GET_NFO_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
				query = DBQueryConstants.GET_BSE_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
				query = DBQueryConstants.GET_MCX_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
				query = DBQueryConstants.GET_NCDEX_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
				query = DBQueryConstants.GET_NSECDS_CHART;
			else if (sMarketSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
				query = DBQueryConstants.GET_BSECDS_CHART;
			
			cs = conn.prepareStatement(query);
			
			log.debug("Intraday Query = "+query);
			log.debug("Params:" + mappingSymbolUniqDesc + ", " + sSymbolToken + ", " + sFromDate + ", " + sToDate);
			cs.setString(1, mappingSymbolUniqDesc);
			cs.setString(2, sFromDate);
			cs.setString(3, sToDate);

			res = cs.executeQuery();

			while (res.next()) {
				ChartData cData = new ChartData();

				cData.setOpen(res.getDouble(DBConstants.OPEN));
				cData.setHigh(res.getDouble(DBConstants.HIGH));
				cData.setLow(res.getDouble(DBConstants.LOW));
				cData.setClose(res.getDouble(DBConstants.CLOSE));
				cData.setVolume(Long.parseLong(String.valueOf(res.getBigDecimal(DBConstants.VOLUME))));
				cData.setTimestamp(res.getTimestamp(DBConstants.TIME));
				resultData.add(cData);

			}

		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(cs);
			Helper.closeConnection(conn);
		}
		int resolutionInMins = ChartUtils.getResolution(sInterval);

		JSONArray finalArr = new JSONArray();
		for (int i = 0; i < resultData.size(); i++) {
			JSONObject obj = new JSONObject();

			obj.put(DeviceConstants.OPEN, PriceFormat
					.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision));
			obj.put(DeviceConstants.HIGH, PriceFormat
					.chartFormat(String.valueOf(resultData.get(i).getHigh()).replace(",", ""), precision));
			obj.put(DeviceConstants.LOW, PriceFormat
					.chartFormat(String.valueOf(resultData.get(i).getLow()).replace(",", ""), precision));
			obj.put(DeviceConstants.CLOSE, PriceFormat
					.chartFormat(String.valueOf(resultData.get(i).getClose()).replace(",", ""), precision));
			obj.put(DeviceConstants.VOLUME, Long.parseLong(String.valueOf(resultData.get(i).getVolume()).replace(",", "")));
			obj.put(DeviceConstants.DT, DateUtils.formatTimeInUTC(
					new Date(resultData.get(i).getTimestamp().getTime()), DeviceConstants.DATE_FORMAT));

			finalArr.put(obj);
		}
//		if (resultData.size() > 0)
//			finalArr = parseIntradayData(resultData, resolutionInMins, precision, exchange);

		return finalArr;

	}

	private static JSONArray parseIntradayData(ArrayList<ChartData> resultData, int chartResolution, int precision, String exchange)
			throws Exception {

//		int precision = symRow.getPrecisionInt();

		Timestamp recentUpdatedTimeStamp = null;
		ChartIntervalData intrData = new ChartIntervalData();
		JSONArray respArray = new JSONArray();

		double sopen = 0.00;
		double shigh = 0.00;
		double slow = Double.MAX_VALUE;
		double sclose = 0.00;
		long svolume = 0;

		Timestamp dbTimeStamp = null;
		Timestamp upperTimestamp = null;
		Timestamp currentUpperTimeStamp = null;

		Timestamp startIgnoreTimestamp = null;
		Timestamp endIgnoreTimestamp = null;

		Calendar cal = Calendar.getInstance();
		Calendar newCal = Calendar.getInstance();

		Calendar chartStartTime = Calendar.getInstance();
		Calendar chartEndTime = Calendar.getInstance();
		Calendar recentTime = Calendar.getInstance();

		boolean isFirstTime = true, doContinue = true, newDay = false;

		int min;

		for (int i = 0; i < resultData.size(); i++) {

			ChartData cData = resultData.get(i);

			dbTimeStamp = cData.getTimestamp();

			if (recentUpdatedTimeStamp != null) {

				Calendar tmpCal = Calendar.getInstance();
				Calendar tmpCal1 = Calendar.getInstance();

				tmpCal.setTimeInMillis(recentUpdatedTimeStamp.getTime());
				tmpCal1.setTimeInMillis(dbTimeStamp.getTime());

				if (tmpCal.get(Calendar.DAY_OF_MONTH) != tmpCal1.get(Calendar.DAY_OF_MONTH)) {
					isFirstTime = true;
					newDay = true;
					doContinue = true;
				}

			}

			// Calculating upper time-stamp based on the data
			newCal.setTimeInMillis(dbTimeStamp.getTime());
			min = newCal.get(Calendar.MINUTE);
			int tempAdd = chartResolution == 1 ? 0 : chartResolution - (min % chartResolution);
			min += tempAdd;

			newCal.set(Calendar.MINUTE, min);
			currentUpperTimeStamp = new Timestamp(newCal.getTimeInMillis());

			// Checking for the first time in the particular interval block.
			// Sets the upper time-stamp for the current interval
			if (doContinue) {

				cal.setTimeInMillis(dbTimeStamp.getTime());
				min = cal.get(Calendar.MINUTE);

				int tmp = chartResolution == 1 ? 0 : chartResolution - (min % chartResolution);
				min += tmp;

				cal.set(Calendar.MINUTE, min);

				upperTimestamp = new Timestamp(cal.getTimeInMillis());

				if (!isFirstTime && cal.getTimeInMillis() > endIgnoreTimestamp.getTime())
					continue;
			}

			// Checking once the control block started.
			if (isFirstTime) {

				chartStartTime.setTimeInMillis(dbTimeStamp.getTime());
				chartEndTime.setTimeInMillis(dbTimeStamp.getTime());

				setChartStartTime(chartStartTime, exchange);
				setChartEndTime(chartEndTime, exchange);

				getIntervalData(intrData, exchange);

				startIgnoreTimestamp = new Timestamp(chartStartTime.getTimeInMillis());
				endIgnoreTimestamp = new Timestamp(chartEndTime.getTimeInMillis());

				if (cal.getTimeInMillis() < startIgnoreTimestamp.getTime())
					continue;

				int chartStartMin = chartStartTime.get(Calendar.MINUTE);
				int tmpMinutes = chartResolution == 1 ? 0 : chartResolution - (chartStartMin % chartResolution);

				chartStartMin += tmpMinutes;
				chartStartTime.set(Calendar.MINUTE, chartStartMin);

				if (cal.get(Calendar.DAY_OF_MONTH) == newCal.get(Calendar.DAY_OF_MONTH)) {

					int uHour = cal.get(Calendar.HOUR_OF_DAY);
					int uMinute = cal.get(Calendar.MINUTE);

					if (uHour != 9 || (uHour == 9 && uMinute > chartStartMin)) {
						fillEmptyDetails(cData, cal, chartStartTime, chartResolution, precision, recentUpdatedTimeStamp,
								intrData, respArray);
					}
				}

				isFirstTime = false;
			}

			if (recentUpdatedTimeStamp != null && (!isFirstTime)) {

				recentTime.setTimeInMillis(recentUpdatedTimeStamp.getTime());
				cal.setTimeInMillis(upperTimestamp.getTime());

				int currentDuration = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
				int oldDuration = (recentTime.get(Calendar.HOUR_OF_DAY) * 60) + recentTime.get(Calendar.MINUTE);

				if (!newDay && (currentDuration - oldDuration) != chartResolution) {
					fillEmptyDetails(cal, recentTime, chartResolution, false, recentUpdatedTimeStamp, intrData,
							respArray);
				}

				newDay = false;
			}

			// Validating the interval and the data received belongs to current
			// interval
			if (dbTimeStamp.getTime() <= upperTimestamp.getTime()
					&& upperTimestamp.getTime() == currentUpperTimeStamp.getTime()) {

				if (doContinue)
					sopen = cData.getOpen();

				sclose = cData.getClose();

				if (shigh < cData.getHigh())
					shigh = cData.getHigh();

				if (slow > cData.getLow())
					slow = cData.getLow();

				svolume += cData.getVolume();

				doContinue = false;

			} else {

				fillDetailsToArray(sopen, shigh, slow, sclose, svolume, upperTimestamp, precision, respArray);

				sopen = 0;
				sclose = 0;
				shigh = 0;
				slow = Double.MAX_VALUE;
				svolume = 0;

				doContinue = true;

				i--;
				recentUpdatedTimeStamp = upperTimestamp;
			}
		}

		if (!doContinue) {

			Calendar recent = Calendar.getInstance();
			Calendar old = Calendar.getInstance();

			if (recentUpdatedTimeStamp != null)
				old.setTimeInMillis(recentUpdatedTimeStamp.getTime());

			recent.setTimeInMillis(upperTimestamp.getTime());

			if (recentUpdatedTimeStamp != null) {
				fillEmptyDetails(recent, old, chartResolution, false, recentUpdatedTimeStamp, intrData, respArray);
			}

			if (chartResolution == 1) {
				fillDetailsToArray(sopen, shigh, slow, sclose, svolume, upperTimestamp, precision, respArray);
				cal.setTimeInMillis(upperTimestamp.getTime());
			}
//			fillEmptyDetails(chartEndTime, cal, chartResolution, true);
		}

//		if (respArray.length() >= 3) {
//
//			intraDayChart.put(DeviceConstants.DATA_POINTS, respArray);
//		}
		return respArray;

		// return intraDayChart;
	}

	private static void setChartStartTime(Calendar cal, String exchangeName) {

		switch (exchangeName) {

		case ExchangeSegment.NSE:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 14);
			break;

		case ExchangeSegment.BSE:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 14);
			break;

		case ExchangeSegment.NFO:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 14);
			break;

		case ExchangeSegment.NSECDS:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 0);
			break;

		case ExchangeSegment.BSECDS:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 0);
			break;

		case ExchangeSegment.MCX:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 0);
			break;

		case ExchangeSegment.NCDEX:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 0);
			break;

		default:
			cal.set(Calendar.HOUR_OF_DAY, 9);
			cal.set(Calendar.MINUTE, 0);
			break;
		}

	}

	private static void setChartEndTime(Calendar cal, String exchangeName) {

		switch (exchangeName) {

		case ExchangeSegment.NSE:
			cal.set(Calendar.HOUR_OF_DAY, 15);
			cal.set(Calendar.MINUTE, 30);
			break;

		case ExchangeSegment.BSE:
			cal.set(Calendar.HOUR_OF_DAY, 15);
			cal.set(Calendar.MINUTE, 30);
			break;

		case ExchangeSegment.NFO:
			cal.set(Calendar.HOUR_OF_DAY, 15);
			cal.set(Calendar.MINUTE, 30);
			break;

		case ExchangeSegment.NSECDS:
			cal.set(Calendar.HOUR_OF_DAY, 17);
			cal.set(Calendar.MINUTE, 0);
			break;

		case ExchangeSegment.BSECDS:
			cal.set(Calendar.HOUR_OF_DAY, 17);
			cal.set(Calendar.MINUTE, 0);
			break;

		case ExchangeSegment.MCX:
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			break;

		case ExchangeSegment.NCDEX:
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			break;

		default:
			cal.set(Calendar.HOUR_OF_DAY, 17);
			cal.set(Calendar.MINUTE, 0);
			break;
		}

	}

	private static void getIntervalData(ChartIntervalData intrData, String exchangeName) {

		switch (exchangeName) {

		case ExchangeSegment.NSE:
			intrData.setStartHour(9);
			intrData.setStartMin(15);
			intrData.setEndHour(15);
			intrData.setEndMin(30);
			break;

		case ExchangeSegment.BSE:
			intrData.setStartHour(9);
			intrData.setStartMin(15);
			intrData.setEndHour(15);
			intrData.setEndMin(30);
			break;

		case ExchangeSegment.NFO:
			intrData.setStartHour(9);
			intrData.setStartMin(15);
			intrData.setEndHour(15);
			intrData.setEndMin(30);
			break;

		case ExchangeSegment.NSECDS:
			intrData.setStartHour(9);
			intrData.setStartMin(0);
			intrData.setEndHour(17);
			intrData.setEndMin(0);
			break;

		case ExchangeSegment.BSECDS:
			intrData.setStartHour(9);
			intrData.setStartMin(0);
			intrData.setEndHour(17);
			intrData.setEndMin(0);
			break;

		case ExchangeSegment.MCX:
			intrData.setStartHour(9);
			intrData.setStartMin(0);
			intrData.setEndHour(23);
			intrData.setEndMin(50);
			break;

		case ExchangeSegment.NCDEX:
			intrData.setStartHour(9);
			intrData.setStartMin(0);
			intrData.setEndHour(23);
			intrData.setEndMin(50);
			break;

		default:
			intrData.setStartHour(9);
			intrData.setStartMin(0);
			intrData.setEndHour(15);
			intrData.setEndMin(30);
			break;
		}

	}

	private static void fillEmptyDetails(ChartData cData, Calendar uTime, Calendar lTime, int fillInInterval,
			int precision, Timestamp recentUpdatedTimeStamp, ChartIntervalData intrData, JSONArray respArray)
			throws Exception {

		String closeValue = PriceFormat.chartFormat(String.valueOf(cData.getClose()).replace(",", ""), precision);

		fillInDetails(new Timestamp(uTime.getTimeInMillis()), new Timestamp(lTime.getTimeInMillis()), fillInInterval,
				closeValue, recentUpdatedTimeStamp, intrData, respArray);

	}

	private static void fillInDetails(Timestamp uTimestamp, Timestamp lTimestamp, int fillInInterval, String closeValue,
			Timestamp recentUpdatedTimeStamp, ChartIntervalData intrData, JSONArray respArray) throws Exception {

		long millis = fillInInterval * 60 * 1000;

		long lTime = lTimestamp.getTime();
		long uTime = uTimestamp.getTime();

		Calendar currentTime = Calendar.getInstance();

		currentTime.setTimeInMillis(uTimestamp.getTime());

		while (lTime < uTime) {

			currentTime.setTimeInMillis(lTime);

			int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);
			int hour = currentTime.get(Calendar.HOUR_OF_DAY);
			int minute = currentTime.get(Calendar.MINUTE);

			if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY || hour < intrData.getStartHour()
					|| hour > intrData.getEndHour() || (hour == intrData.getEndHour() && minute > intrData.getEndMin())
					|| (hour == intrData.getStartHour() && minute < intrData.getStartMin())) {
				recentUpdatedTimeStamp = new Timestamp(lTime);
				lTime += millis;
				continue;
			}

			JSONObject obj = new JSONObject();

			obj.put(DeviceConstants.OPEN, closeValue);
			obj.put(DeviceConstants.HIGH, closeValue);
			obj.put(DeviceConstants.LOW, closeValue);
			obj.put(DeviceConstants.CLOSE, closeValue);
			obj.put(DeviceConstants.VOLUME, String.valueOf(0).replace(",", ""));
			obj.put(DeviceConstants.DT, DateUtils.formatTimeInUTC(new Date(lTime), DeviceConstants.DATE_FORMAT));

			respArray.put(obj);

			recentUpdatedTimeStamp = new Timestamp(lTime);

			lTime += millis;

		}
	}

	private static void fillEmptyDetails(Calendar uTime, Calendar lTime, int fillInInterval, boolean isEnd,
			Timestamp recentUpdatedTimeStamp, ChartIntervalData intrData, JSONArray respArray) throws Exception {

		String closeValue = respArray.getJSONObject(respArray.length() - 1).getString(DeviceConstants.CLOSE);

		if (isEnd) {

			Calendar current = Calendar.getInstance();

			if (current.get(Calendar.DAY_OF_MONTH) == uTime.get(Calendar.DAY_OF_MONTH)) {

				uTime.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
				int min = current.get(Calendar.MINUTE);
				int tmp = fillInInterval == 1 ? 0 : fillInInterval - (min % fillInInterval);

				uTime.set(Calendar.MINUTE, min + tmp);
			}

			if (uTime.get(Calendar.HOUR_OF_DAY) >= intrData.getEndHour()) {

				uTime.set(Calendar.HOUR_OF_DAY, intrData.getEndHour());
				uTime.set(Calendar.MINUTE, intrData.getEndMin());

			}

		} else {

			if ((uTime.get(Calendar.YEAR) != lTime.get(Calendar.YEAR))
					|| (uTime.get(Calendar.MONTH) != lTime.get(Calendar.MONTH))
					|| (uTime.get(Calendar.DAY_OF_MONTH) != lTime.get(Calendar.DAY_OF_MONTH))) {

				// uTime.getTimeInMillis() + (fillInInterval * 60 * 1000)
				fillInDetails(new Timestamp(uTime.getTimeInMillis()), new Timestamp(lTime.getTimeInMillis()),
						fillInInterval, closeValue, recentUpdatedTimeStamp, intrData, respArray);

				return;
			}
		}

		int lHour = lTime.get(Calendar.HOUR_OF_DAY);
		int lMinute = lTime.get(Calendar.MINUTE) + fillInInterval;

		lTime.set(Calendar.HOUR_OF_DAY, lHour);
		lTime.set(Calendar.MINUTE, lMinute);

		fillInDetails(new Timestamp(uTime.getTimeInMillis()), new Timestamp(lTime.getTimeInMillis()), fillInInterval,
				closeValue, recentUpdatedTimeStamp, intrData, respArray);
	}

	private static void fillDetailsToArray(double dOpen, double dHigh, double dLow, double dClose, long lVolume,
			Timestamp timeStamp, int precision, JSONArray respArray) throws Exception {

		JSONObject obj = new JSONObject();

		obj.put(DeviceConstants.OPEN, PriceFormat.chartFormat(String.valueOf(dOpen).replace(",", ""), precision));
		obj.put(DeviceConstants.HIGH, PriceFormat.chartFormat(String.valueOf(dHigh).replace(",", ""), precision));
		obj.put(DeviceConstants.LOW, PriceFormat.chartFormat(String.valueOf(dLow).replace(",", ""), precision));
		obj.put(DeviceConstants.CLOSE, PriceFormat.chartFormat(String.valueOf(dClose).replace(",", ""), precision));
		obj.put(DeviceConstants.VOLUME, String.valueOf(lVolume).replace(",", ""));
		obj.put(DeviceConstants.DT,
				DateUtils.formatTimeInUTC(new Date(timeStamp.getTime()), DeviceConstants.DATE_FORMAT));

		respArray.put(obj);
	}

	public static JSONArray getHistoricalChart(String sSymbolToken, String fromDate, String endDate, String sInterval,
			String sAppID) throws ParseException, JSONException, GCException, CMOTSException {
		JSONArray finalArr = new JSONArray();
		String sMarketSegID = "", co_code="";
		int precision = 0;
		List<JSONArray> parsedHistoricalList = new ArrayList<JSONArray>();
		if(SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			co_code = symRow.getCMCoCode().replace(".0", "");
		} else if(Indices.isValidIndex(sSymbolToken)) {
			SymbolRow symRow = Indices.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			co_code = symRow.getCMCoCode().replace(".0", "");
		}

		int counter = precision;
		String replaceChar = ".";
		while(counter > 0) {
			counter--;
			replaceChar+="0";
		}
		
		GetHistoricalChartEquity historicalRequest = new GetHistoricalChartEquity();
		historicalRequest.setCoCode(co_code);
		if (sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
			historicalRequest.setExchange(ExchangeSegment.NSE.toLowerCase());
		else if (sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
			historicalRequest.setExchange(ExchangeSegment.BSE.toLowerCase());
		SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.PL_DATE_FORMAT);
		historicalRequest.setStartDate(destinationFormat.format(sourceFormat.parse(fromDate)));
		historicalRequest.setEndDate(destinationFormat.format(sourceFormat.parse(endDate)));
		
		CompanyChartDataList chartDataList = null;
		RedisPool redisPool = new RedisPool();
		try {
		if(redisPool.isExists(RedisConstants.CHARTS+"_"+sMarketSegID+"_"+co_code+"_"+fromDate+"_"+endDate)) {
			chartDataList = new Gson().fromJson(redisPool.getValue(RedisConstants.CHARTS+"_"+sMarketSegID+"_"+co_code+"_"+fromDate+"_"+endDate), CompanyChartDataList.class);
		}else {
			chartDataList =	historicalRequest.invoke();
			redisPool.setValues(RedisConstants.CHARTS+"_"+sMarketSegID+"_"+co_code+"_"+fromDate+"_"+endDate, new Gson().toJson(chartDataList));
		}
		}catch (Exception e) {
			log.error(e);
			chartDataList =	historicalRequest.invoke();
		}
		
		for(int i = 0 ;i < chartDataList.size(); i++) {
			JSONArray tempArr = new JSONArray();
			tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(chartDataList.get(i).getopen()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
			tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(chartDataList.get(i).gethigh()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
			tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(chartDataList.get(i).getlow()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
			tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(chartDataList.get(i).getclose()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
			tempArr.put(Long.parseLong(String.valueOf(chartDataList.get(i).getVolume().replace(".0", ""))));
			String sDate = DateUtils.formatDateWithZone(chartDataList.get(i).getTradeDate(), DeviceConstants.FROM_DATE_FORMAT, DeviceConstants.DATE_FORMAT, "T");
			tempArr.put(sDate.replace("-", "/"));
			parsedHistoricalList.add(tempArr);
		}
		sortJSONArrayDate(parsedHistoricalList, 5);

		Map<String, JSONArray> mapDateToValues = listToMap(parsedHistoricalList);

		for (Entry<String, JSONArray> entry : mapDateToValues.entrySet())
			finalArr.put(entry.getValue());

		return finalArr;
	}

	public static void sortJSONArrayDate(List<JSONArray> listObj, final int index) {
		Collections.sort(listObj, new Comparator<JSONArray>() {

			@Override
			public int compare(JSONArray a, JSONArray b) {

				SimpleDateFormat sdfo = new SimpleDateFormat(DeviceConstants.DATE_FORMAT_CHART);

				Date d1 = null, d2 = null;
				try {
					d1 = sdfo.parse(String.valueOf(a.get(index)));
					d2 = sdfo.parse(String.valueOf(b.get(index)));
				} catch (JSONException e) {
					log.error(e);
				} catch (ParseException e) {
					log.error(e);
				}

				return d1.compareTo(d2);
			}
		});

	}

	public static Map<String, JSONArray> listToMap(List<JSONArray> lt) {
		Map<String, JSONArray> map = new LinkedHashMap<>();

		for (int i = 0; i < lt.size(); i++) {
			JSONArray obj = lt.get(i);

			String sKey = String.valueOf(obj.get(5));
			if (!map.containsKey(sKey)) {
				map.put(sKey, obj);
			}
		}

		return map;
	}

	public static String getChartStartDate() {
		return sChartStartDate;
	}

	public static void setChartStartDate(String sChartStartDate) {
		Chart_104.sChartStartDate = sChartStartDate;
	}
	
	public static JSONArray getIntradayChartHistory(String sSymbolToken, String sFromDate, String sToDate, String sInterval)
			throws Exception {

		String sMarketSegID = "", mappingSymbolUniqDesc = "";
		int precision = 0;
		if(SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		} else if(Indices.isValidIndex(sSymbolToken)) {
			SymbolRow  symRow = Indices.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			mappingSymbolUniqDesc = symRow.getMappingSymbolUniqDesc();
		}
		
		Connection conn = null;
		PreparedStatement cs = null;
		ResultSet res = null;

		String query = "";

		ArrayList<ChartData> resultData = new ArrayList<ChartData>();

		try {

			conn = QuoteDataDBPool.getInstance().getConnection();
			
			if (sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID))
				query = DBQueryConstants.GET_NSE_CHART_HISTORY;
			else if (sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
				query = DBQueryConstants.GET_NFO_CHART_HISTORY;
			else if (sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
				query = DBQueryConstants.GET_BSE_CHART_HISTORY;
			else if (sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
				query = DBQueryConstants.GET_MCX_CHART_HISTORY;
			else if (sMarketSegID.equals(ExchangeSegment.NCDEX_SEGMENT_ID))
				query = DBQueryConstants.GET_NCDEX_CHART_HISTORY;
			else if (sMarketSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
				query = DBQueryConstants.GET_NSECDS_CHART_HISTORY;
			else if (sMarketSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
				query = DBQueryConstants.GET_BSECDS_CHART_HISTORY;
			
			cs = conn.prepareStatement(query);
			
			cs.setString(1, mappingSymbolUniqDesc);
			cs.setString(2, sFromDate);
			cs.setString(3, sToDate);

			log.debug("Intraday Query = "+query);
			log.debug("Params:" + mappingSymbolUniqDesc + ", " + sFromDate + ", " + sToDate);

			res = cs.executeQuery();

			while (res.next()) {
				if(res.getDouble(DBConstants.OPEN) > 0 && res.getDouble(DBConstants.HIGH) > 0
				&& res.getDouble(DBConstants.LOW) > 0 && res.getDouble(DBConstants.CLOSE) > 0) {
					ChartData cData = new ChartData();
	
					cData.setOpen(res.getDouble(DBConstants.OPEN));
					cData.setHigh(res.getDouble(DBConstants.HIGH));
					cData.setLow(res.getDouble(DBConstants.LOW));
					cData.setClose(res.getDouble(DBConstants.CLOSE));
					cData.setVolume(res.getLong(DBConstants.VOLUME));
					cData.setTimestamp(res.getTimestamp(DBConstants.TIME));
	
					resultData.add(cData);
				}

			}

		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(cs);
			Helper.closeConnection(conn);
		}
		
		int resolutionInMins = ChartUtils.getResolution(sInterval);

		JSONArray finalArr = new JSONArray();

		if (resolutionInMins == 1440 && resultData.size() > 0) {
			finalArr = getOneDayChart(resultData, precision);
		}
		else {
			String replaceChar = ".";
			int counter = precision;
			while(counter>0) {
				counter--;
				replaceChar+="0";
			}
			for (int i = 0; i < resultData.size(); i++) {
				JSONArray tempArr = new JSONArray();
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getHigh()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getLow()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getClose()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				tempArr.put(resultData.get(i).getVolume());
				tempArr.put(DateUtils.formatTimeInUTC(resultData.get(i).getTimestamp(), DeviceConstants.DATE_FORMAT).replace("-", "/"));
				
				finalArr.put(tempArr); 
			}
		}

		return finalArr;

	}
}
