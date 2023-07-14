package com.globecapital.business.chart;

import java.sql.CallableStatement;
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
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.spyder.chart.HistoricalDataAPI;
import com.globecapital.api.spyder.chart.HistoricalDataObject;
import com.globecapital.api.spyder.chart.HistoricalDataRequest;
import com.globecapital.api.spyder.chart.HistoricalDataResponse;
import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.business.market.Indices;
import com.globecapital.config.AppConfig;
import com.globecapital.config.IndicesChart;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.ChartDBPool;
import com.globecapital.db.QuoteDataDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.ChartUtils;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Chart {

	private static Logger log = Logger.getLogger(Chart.class);
	
	private static String sChartStartDate;
	
	public static void loadChartDate() throws AppConfigNoKeyFoundException
	{
		int iCountStartDay = AppConfig.getIntValue("chart_1M_past_days");
		setChartStartDate(DateUtils.getNthDateFromTodayDate(DeviceConstants.TO_DATE_FORMAT, 
				iCountStartDay * (-1)));
	}

	public static JSONArray getIntradayChart(String sSymbolToken, String sFromDate, String sToDate, String sInterval)
			throws Exception {

		String sMarketSegID = "";
		int precision = 0;
		if(SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
		} else if(Indices.isValidIndex(sSymbolToken)) {
			SymbolRow  symRow = Indices.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
		}
		
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet res = null;

		String query = DBQueryConstants.GET_CHART_POINTS;
		log.debug("Intraday Query = "+query);
		log.debug("Params:" + sMarketSegID + ", " + sSymbolToken + ", " + sFromDate + ", " + sToDate);

		ArrayList<ChartData> resultData = new ArrayList<ChartData>();

		try {

			conn = ChartDBPool.getInstance().getConnection();
			cs = conn.prepareCall(query);

			cs.setString(1, sMarketSegID);
			cs.setString(2, sSymbolToken);
			cs.setString(3, sFromDate);
			cs.setString(4, sToDate);

			cs.execute();
			res = cs.getResultSet();

			while (res.next()) {
				ChartData cData = new ChartData();

				cData.setOpen(res.getDouble(DBConstants.OPEN_PRICE));
				cData.setHigh(res.getDouble(DBConstants.HIGH_PRICE));
				cData.setLow(res.getDouble(DBConstants.LOW_PRICE));
				cData.setClose(res.getDouble(DBConstants.CLOSE_PRICE));
				cData.setVolume(res.getLong(DBConstants.VOLUME));
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

		if (resolutionInMins == 1440 && resultData.size() > 0) {
			finalArr = getOneDayChart(resultData, precision);
		}
		else {
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
				obj.put(DeviceConstants.VOLUME, String.valueOf(resultData.get(i).getVolume()).replace(",", ""));
				obj.put(DeviceConstants.DT, DateUtils.formatTimeInUTC(
						new Date(resultData.get(i).getTimestamp().getTime()), DeviceConstants.DATE_FORMAT));

				finalArr.put(obj);
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
		JSONObject obj = new JSONObject();

		for (int i = 0; i < resultData.size(); i++) {

			if (i == 0)
				obj.put(DeviceConstants.OPEN, PriceFormat
						.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision));

			if (i == resultData.size()-1)
				obj.put(DeviceConstants.CLOSE, PriceFormat
						.chartFormat(String.valueOf(resultData.get(i).getClose()).replace(",", ""), precision));

			if (tmpHigh < resultData.get(i).getHigh()) {
				tmpHigh = resultData.get(i).getHigh();
				obj.put(DeviceConstants.HIGH,
						PriceFormat.chartFormat(String.valueOf(tmpHigh).replace(",", ""), precision));
			}

			if (tmpLow > resultData.get(i).getLow()) {
				tmpLow = resultData.get(i).getLow();
				obj.put(DeviceConstants.LOW,
						PriceFormat.chartFormat(String.valueOf(tmpLow).replace(",", ""), precision));
			}

			tmpVolume += resultData.get(i).getVolume();

		}
		obj.put(DeviceConstants.VOLUME, String.valueOf(tmpVolume).replace(",", ""));

		obj.put(DeviceConstants.DT, DateUtils.formatTimeInUTC(new Date(), DeviceConstants.DATE_FORMAT));

		finalArr.put(obj);

		return finalArr;
	}

	public static JSONArray getChartExch(String sSymbolToken, String sFromDate, String sToDate, String sInterval)
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
			obj.put(DeviceConstants.VOLUME, String.valueOf(resultData.get(i).getVolume()).replace(",", ""));
			obj.put(DeviceConstants.DT, DateUtils.formatTimeInUTC(
					new Date(resultData.get(i).getTimestamp().getTime()), DeviceConstants.DATE_FORMAT));

			finalArr.put(obj);
		}
//		if (resultData.size() > 0)
//			finalArr = parseIntradayData(resultData, resolutionInMins, precision, exchange);

		return finalArr;

	}
	
	public static JSONArray getChartOMDF(String sSymbolToken, String sFromDate, String sToDate, String sInterval)
			throws Exception {
		
		String sMarketSegID = "", exchange = "";
		int precision = 0;
		if(SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			exchange = symRow.getExchange();
		} else if(Indices.isValidIndex(sSymbolToken)) {
			SymbolRow symRow = Indices.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			precision = symRow.getPrecisionInt();
			exchange = symRow.getExchange();
		}

		Connection conn = null;
		CallableStatement cs = null;
		ResultSet res = null;

		String query = DBQueryConstants.GET_CHART_POINTS;
		log.debug(query);
		log.debug("Params:" + sMarketSegID + ", " + sSymbolToken + ", " + sFromDate + ", " + sToDate);

		ArrayList<ChartData> resultData = new ArrayList<ChartData>();
		;

		try {

			conn = ChartDBPool.getInstance().getConnection();
			cs = conn.prepareCall(query);

			cs.setString(1, sMarketSegID);
			cs.setString(2, sSymbolToken);
			cs.setString(3, sFromDate);
			cs.setString(4, sToDate);

			cs.execute();
			res = cs.getResultSet();

			while (res.next()) {
				ChartData cData = new ChartData();

				cData.setOpen(res.getDouble(DBConstants.OPEN_PRICE));
				cData.setHigh(res.getDouble(DBConstants.HIGH_PRICE));
				cData.setLow(res.getDouble(DBConstants.LOW_PRICE));
				cData.setClose(res.getDouble(DBConstants.CLOSE_PRICE));
				cData.setVolume(res.getLong(DBConstants.VOLUME));
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
			obj.put(DeviceConstants.VOLUME, String.valueOf(resultData.get(i).getVolume()).replace(",", ""));
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
			String sAppID) throws ParseException, JSONException, GCException {
		
		JSONArray finalArr = new JSONArray();
		String sMarketSegID = "", sToken = "";
		int precision = 0;
		if(SymbolMap.isValidSymbolTokenSegmentMap(sSymbolToken)) {
			SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			sToken = symRow.gettokenId();
			precision = symRow.getPrecisionInt();
		} else if(Indices.isValidIndex(sSymbolToken)) {
			SymbolRow symRow = Indices.getSymbolRow(sSymbolToken);
			sMarketSegID = symRow.getMktSegId();
			sToken = symRow.gettokenId();
			precision = symRow.getPrecisionInt();
		}
		int resolutionInMins = ChartUtils.getResolution(sInterval);

		HistoricalDataRequest historicalRequest = new HistoricalDataRequest();

		if (sMarketSegID.equals(ExchangeSegment.NSE_SEGMENT_ID) || sMarketSegID.equals(ExchangeSegment.NFO_SEGMENT_ID))
			historicalRequest.setExch(SpyderConstants.N);
		else if (sMarketSegID.equals(ExchangeSegment.BSE_SEGMENT_ID))
			historicalRequest.setExch(SpyderConstants.B);
		else if (sMarketSegID.equals(ExchangeSegment.MCX_SEGMENT_ID))
			historicalRequest.setExch(SpyderConstants.M);
		else if (sMarketSegID.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
			historicalRequest.setExch(SpyderConstants.C);
		else if (sMarketSegID.equals(ExchangeSegment.BSECDS_SEGMENT_ID))
			historicalRequest.setExch(SpyderConstants.E);

		sToken = IndicesChart.optValue(sToken, sToken);
		
		historicalRequest.setScripCode(sToken);
		historicalRequest.setFromDate(
				DateUtils.formatDate(fromDate, DeviceConstants.DATE_FORMAT, SpyderConstants.DATE_TIME_FORMAT));
		historicalRequest.setToDate(
				DateUtils.formatDate(endDate, DeviceConstants.DATE_FORMAT, SpyderConstants.DATE_TIME_FORMAT));

		historicalRequest.setTimeInterval(Integer.toString(resolutionInMins));

		HistoricalDataAPI historicalAPI = new HistoricalDataAPI();

		HistoricalDataResponse historicalResp = historicalAPI.get(historicalRequest, HistoricalDataResponse.class,
				sAppID,"HistoricalChart");

		List<HistoricalDataObject> historicalObj = historicalResp.getResponseObject();

		List<JSONObject> parsedHistoricalList = new ArrayList<JSONObject>();

		for (int i = 0; i < historicalObj.size(); i++) {
			JSONObject chartObj = new JSONObject();
			chartObj.put(DeviceConstants.OPEN,
					PriceFormat.formatPrice(historicalObj.get(i).getOpen(), precision, true));
			chartObj.put(DeviceConstants.HIGH,
					PriceFormat.formatPrice(historicalObj.get(i).getHigh(), precision, true));
			chartObj.put(DeviceConstants.LOW, PriceFormat.formatPrice(historicalObj.get(i).getLow(), precision, true));
			chartObj.put(DeviceConstants.CLOSE,
					PriceFormat.formatPrice(historicalObj.get(i).getClose(), precision, true));
			chartObj.put(DeviceConstants.VOLUME, historicalObj.get(i).getVolume());

			if (sInterval.equalsIgnoreCase(ChartUtils.RESOLUTION_1_DAY)) {
				String sDate = DateUtils.formatDate(historicalObj.get(i).getDate(), SpyderConstants.RES_DATE_FORMAT,
						DeviceConstants.FROM_DATE_FORMAT) + " 18:00:00";
				String sUTCDate = DateUtils.getUTCTime(sDate, DeviceConstants.DATE_FORMAT);
				chartObj.put(DeviceConstants.DT, sUTCDate);
			} else {
				String sUTC = DateUtils.getUTCTime(historicalObj.get(i).getDate(), SpyderConstants.RES_DATE_FORMAT);
				String sDate = DateUtils.formatDate(sUTC, SpyderConstants.RES_DATE_FORMAT, DeviceConstants.DATE_FORMAT);
				chartObj.put(DeviceConstants.DT, sDate);
			}

			parsedHistoricalList.add(chartObj);

		}

		sortJSONArrayDate(parsedHistoricalList, DeviceConstants.DT);

		Map<String, JSONObject> mapDateToValues = listToMap(parsedHistoricalList);

		for (Entry<String, JSONObject> entry : mapDateToValues.entrySet())
			finalArr.put(entry.getValue());

		return finalArr;
	}

	public static void sortJSONArrayDate(List<JSONObject> listObj, final String sKey) {
		Collections.sort(listObj, new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject a, JSONObject b) {

				SimpleDateFormat sdfo = new SimpleDateFormat(DeviceConstants.DATE_FORMAT);

				Date d1 = null, d2 = null;
				try {
					d1 = sdfo.parse(a.getString(sKey));
					d2 = sdfo.parse(b.getString(sKey));
				} catch (JSONException e) {
					log.error(e);
				} catch (ParseException e) {
					log.error(e);
				}

				return d1.compareTo(d2);
			}
		});

	}

	public static Map<String, JSONObject> listToMap(List<JSONObject> lt) {
		Map<String, JSONObject> map = new LinkedHashMap<>();

		for (int i = 0; i < lt.size(); i++) {
			JSONObject obj = lt.get(i);

			String sKey = obj.getString(DeviceConstants.DT);
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
		Chart.sChartStartDate = sChartStartDate;
	}

}
