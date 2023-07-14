package com.globecapital.business.chart;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.globecapital.config.IndicesChart;
import com.globecapital.config.MarketConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.ChartDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.ChartUtils;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Chart_103 {

	private static Logger log = Logger.getLogger(Chart.class);
	
	public static JSONArray getIntradayChart(String sSymbolToken, String sFromDate, String sToDate, String sInterval, String groupInterval)
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

			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
			String marketConfig = "";
			String exchName = ExchangeSegment.getExchangeName(sMarketSegID);
			if(exchName.equals(ExchangeSegment.NSE) || exchName.equals(ExchangeSegment.BSE) || exchName.equals(ExchangeSegment.NFO))
				marketConfig = MarketConfig.getValue("MarketEQ");
			else if(exchName.equals(ExchangeSegment.NSECDS) || exchName.equals(ExchangeSegment.BSECDS))
				marketConfig = MarketConfig.getValue("MarketCurr");
			else if(exchName.equals(ExchangeSegment.MCX))
				marketConfig = MarketConfig.getValue("MarketCOM");
			
			loadMarketTimingFromConfig(dayOfWeek, marketConfig, startCal , true);
			loadMarketTimingFromConfig(dayOfWeek, marketConfig, endCal , false);
			
			while (res.next()) {
				ChartData cData = new ChartData();
				Calendar cal=GregorianCalendar.getInstance();
				cal.setTime(res.getTimestamp(DBConstants.TIME));
				cal.set(Calendar.SECOND, 0);
				if((cal.getTime().after(startCal.getTime()) && cal.getTime().before(endCal.getTime()))){
					cData.setOpen(res.getDouble(DBConstants.OPEN_PRICE));
					cData.setHigh(res.getDouble(DBConstants.HIGH_PRICE));
					cData.setLow(res.getDouble(DBConstants.LOW_PRICE));
					cData.setClose(res.getDouble(DBConstants.CLOSE_PRICE));
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

		if (resolutionInMins == 1440 && resultData.size() > 0 ) {
			finalArr = getOneDayChart(resultData, precision);
		}else if (ChartUtils.getResolution(groupInterval) == 1) {
			int counter = precision;
			String replaceChar = ".";
			while(counter>0) {
				counter--;
				replaceChar+="0";
			}
			for(int i=0;i<resultData.size();i++) {
				JSONArray chartPointArr = new JSONArray();
				chartPointArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				chartPointArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getHigh()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				chartPointArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getLow()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				chartPointArr.put(Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getClose()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
				chartPointArr.put(Long.parseLong(String.valueOf(resultData.get(i).getVolume()).replace(",", "")));
				chartPointArr.put(DateUtils.formatTimeInUTC(resultData.get(i).getTimestamp(), DeviceConstants.DATE_FORMAT).replace("-", "/"));
				finalArr.put(chartPointArr);
			}
		}	
		else {
			finalArr = parseIntradayData(resultData, ChartUtils.getResolution(groupInterval), precision, ExchangeSegment.getExchangeName(sMarketSegID));
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
		for (int i = 0; i < resultData.size(); i++) {
			
			if (i == 0)
				tempArr.put(0,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getOpen()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));

			if (tmpHigh < resultData.get(i).getHigh()) {
				tmpHigh = resultData.get(i).getHigh();
				tempArr.put(1,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getHigh()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
			}

			if (tmpLow > resultData.get(i).getLow()) {
				tmpLow = resultData.get(i).getLow();
				tempArr.put(2,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getLow()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
			}

			if (i == resultData.size()-1)
				tempArr.put(3,Float.parseFloat(PriceFormat.chartFormat(String.valueOf(resultData.get(i).getClose()).replace(",", ""), precision).replace(",", "").replace(replaceChar, "")));
			
			tmpVolume += resultData.get(i).getVolume();

		}
		tempArr.put(4,Long.parseLong(String.valueOf(tmpVolume).replace(",", "")));
		tempArr.put(5,DateUtils.formatTimeInUTC(new Date(), DeviceConstants.DATE_FORMAT).replace("-", "/"));
		finalArr.put(tempArr);
		return finalArr;
	}

	private static JSONArray parseIntradayData(ArrayList<ChartData> resultData, int chartResolution, int precision, String exchange)
			throws Exception {

		Timestamp recentUpdatedTimeStamp = null;
		JSONArray respArray = new JSONArray();

		double sopen = 0.00;
		double shigh = 0.00;
		double slow = Double.MAX_VALUE;
		double sclose = 0.00;
		long svolume = 0;

		Timestamp dbTimeStamp = null;
		Timestamp upperTimestamp = null;

		Timestamp startIgnoreTimestamp = null;
		Timestamp endIgnoreTimestamp = null;

		Calendar cal = Calendar.getInstance();
		Calendar newCal = Calendar.getInstance();

		Calendar chartStartTime = Calendar.getInstance();
		Calendar chartEndTime = Calendar.getInstance();
		Calendar recentTime = Calendar.getInstance();

		boolean isFirstTime = true, doContinue = true, newDay = false;

		int min;
		int tmp=0;
		
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
					recentUpdatedTimeStamp = null;
				}

			}
			
			// Calculating upper time-stamp based on the data
			newCal.setTimeInMillis(dbTimeStamp.getTime());
			min = newCal.get(Calendar.MINUTE);
			min+= chartResolution;

			newCal.set(Calendar.MINUTE, min);

			// Checking for the first time in the particular interval block.
			// Sets the upper time-stamp for the current interval
			if (doContinue) {

				cal.setTimeInMillis(dbTimeStamp.getTime());
				min = cal.get(Calendar.MINUTE);
				
				tmp = chartResolution == 1 ? 0 : chartResolution - (min % chartResolution);
				if(min%5==0)
					min+=chartResolution;
				else
					min+=tmp;
				cal.set(Calendar.MINUTE, min);
				
				cal.set(Calendar.SECOND, 0);
				
				if(!newDay)	
					upperTimestamp = new Timestamp(cal.getTimeInMillis());

				if (!isFirstTime && cal.getTimeInMillis() > endIgnoreTimestamp.getTime()) {
					cal.set(Calendar.MINUTE, tmp);
					if (!isFirstTime && cal.getTimeInMillis() > endIgnoreTimestamp.getTime()) {
						continue;
					}
				}	
			}

			// Checking once the control block started.
			if (isFirstTime) {

				chartStartTime.setTimeInMillis(dbTimeStamp.getTime());
				chartEndTime.setTimeInMillis(dbTimeStamp.getTime());

				setMarketTiming(chartStartTime, chartEndTime, exchange);
				log.info("DebuggingInfo : "+chartStartTime.getTime()+" "+chartEndTime.getTime());

				startIgnoreTimestamp = new Timestamp(chartStartTime.getTimeInMillis());
				endIgnoreTimestamp = new Timestamp(chartEndTime.getTimeInMillis());
				
				if (cal.getTimeInMillis() < startIgnoreTimestamp.getTime())
					continue;
				
				if (cal.getTimeInMillis() > endIgnoreTimestamp.getTime())
					continue;

				int chartStartMin = chartStartTime.get(Calendar.MINUTE);
				int tmpMinutes = chartResolution == 1 ? 0 : chartResolution - (chartStartMin % chartResolution);

				chartStartMin += tmpMinutes;
				chartStartTime.set(Calendar.MINUTE, chartStartMin);

				isFirstTime = false;
			}

			if (recentUpdatedTimeStamp != null && (!isFirstTime)) {

				recentTime.setTimeInMillis(recentUpdatedTimeStamp.getTime());
				cal.setTimeInMillis(upperTimestamp.getTime());
				newDay = false;
			}

			// Validating the interval and the data received belongs to current
			// interval
			if (dbTimeStamp.getTime() < upperTimestamp.getTime()){

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
				if(sopen!=0.00&&svolume>0) 
					fillDetailsToArray(sopen, shigh, slow, sclose, svolume, upperTimestamp, precision, respArray,chartResolution);

				sopen = 0;
				sclose = 0;
				shigh = 0;
				slow = Double.MAX_VALUE;
				svolume = 0;

				doContinue = true;
				i--;
				upperTimestamp = new Timestamp(cal.getTimeInMillis());
				recentUpdatedTimeStamp = upperTimestamp;
			}
		}

		if (!doContinue) {

			Calendar recent = Calendar.getInstance();
			Calendar old = Calendar.getInstance();

			if (recentUpdatedTimeStamp != null)
				old.setTimeInMillis(recentUpdatedTimeStamp.getTime());

			recent.setTimeInMillis(upperTimestamp.getTime());


			if (chartResolution == 1) {
				fillDetailsToArray(sopen, shigh, slow, sclose, svolume, upperTimestamp, precision, respArray,chartResolution);
				cal.setTimeInMillis(upperTimestamp.getTime());
			}

		if(sopen!=0.00&&svolume>0)
			fillDetailsToArray(sopen, shigh, slow, sclose, svolume, upperTimestamp, precision, respArray,chartResolution);
		}
		return respArray;

	}

	private static void setMarketTiming(Calendar startCal, Calendar endCal, String exchangeName) {
		String marketConfig = "";
		int startTime_dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
		int endTime_dayOfWeek = endCal.get(Calendar.DAY_OF_WEEK);
		
		try {
			switch (exchangeName) {
	
			case ExchangeSegment.NSE:
				marketConfig = MarketConfig.getValue("MarketEQ");
				loadMarketTimingFromConfig(startTime_dayOfWeek, marketConfig, startCal , true);
				loadMarketTimingFromConfig(endTime_dayOfWeek, marketConfig, endCal , false);
				break;
	
			case ExchangeSegment.BSE:
				marketConfig = MarketConfig.getValue("MarketEQ");
				loadMarketTimingFromConfig(startTime_dayOfWeek, marketConfig, startCal , true);
				loadMarketTimingFromConfig(endTime_dayOfWeek, marketConfig, endCal , false);
				break;
	
			case ExchangeSegment.NFO:
				marketConfig = MarketConfig.getValue("MarketEQ");
				loadMarketTimingFromConfig(startTime_dayOfWeek, marketConfig, startCal , true);
				loadMarketTimingFromConfig(endTime_dayOfWeek, marketConfig, endCal , false);
				break;
	
			case ExchangeSegment.NSECDS:
				marketConfig = MarketConfig.getValue("MarketCurr");
				loadMarketTimingFromConfig(startTime_dayOfWeek, marketConfig, startCal , true);
				loadMarketTimingFromConfig(endTime_dayOfWeek, marketConfig, endCal , false);
				break;
	
			case ExchangeSegment.BSECDS:
				marketConfig = MarketConfig.getValue("MarketCurr");
				loadMarketTimingFromConfig(startTime_dayOfWeek, marketConfig, startCal , true);
				loadMarketTimingFromConfig(endTime_dayOfWeek, marketConfig, endCal , false);
				break;
	
			case ExchangeSegment.MCX:
				marketConfig = MarketConfig.getValue("MarketCOM");
				loadMarketTimingFromConfig(startTime_dayOfWeek, marketConfig, startCal , true);
				loadMarketTimingFromConfig(endTime_dayOfWeek, marketConfig, endCal , false);
				break;
	
			case ExchangeSegment.NCDEX:
				marketConfig = MarketConfig.getValue("MarketCOM");
				loadMarketTimingFromConfig(startTime_dayOfWeek, marketConfig, startCal , true);
				loadMarketTimingFromConfig(endTime_dayOfWeek, marketConfig, endCal , false);
				break;
	
			default:
				startCal.set(Calendar.HOUR_OF_DAY, 9);
				startCal.set(Calendar.MINUTE, 0);
				endCal.set(Calendar.HOUR_OF_DAY, 17);
				endCal.set(Calendar.MINUTE, 0);
				break;
			}
		}catch(AppConfigNoKeyFoundException | ParseException ex) {
			log.debug(ex);
		}
	}

	private static void loadMarketTimingFromConfig(int dayOfWeek, String marketConfig, Calendar cal, boolean isOpen) throws ParseException {
		boolean isHoliday = false;
		cal.set(Calendar.SECOND, 0);
		int hour = 0, minutes = 0;
		JSONObject jsonRes = new JSONObject(marketConfig);
		JSONArray rules = jsonRes.getJSONArray("rules");
		for(int i=0; i<rules.length(); i++) {
			hour = 0;
			minutes = 0;
			JSONObject ruleObj = (JSONObject)rules.get(i);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = new Date();
			dt = sdf.parse(sdf.format(dt));
			if(ruleObj.has("date")) {
				String ruleDateStr = (String) ruleObj.get("date");
				Date ruleDate = sdf.parse(ruleDateStr);
				Date dtCal = cal.getTime();
				String dateCal = sdf.format(dtCal);
				dtCal = sdf.parse(dateCal);
				if(dtCal.compareTo(ruleDate)==0) {
					if(ruleObj.has("openHour") && ruleObj.has("closeHour")) {
						if(isOpen) {
							hour = Integer.parseInt((String) ruleObj.get("openHour"));
							minutes = Integer.parseInt((String) ruleObj.get("openMinutes"));
						}else {
							hour = Integer.parseInt((String) ruleObj.get("closeHour"));
							minutes = Integer.parseInt((String) ruleObj.get("closeMinutes"));
						}
						break;
					}else {
						isHoliday = true;
						break;
					}
					
				}
			}
		}
		if(hour > 0 || minutes > 0) {
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minutes);
		}else if((dayOfWeek == 1 && isHoliday == false) || (dayOfWeek == 7 && isHoliday == false) || isHoliday == true) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
		}
		else {
			JSONObject marketConfigToday = (JSONObject) rules.get(dayOfWeek-2);
			if(isOpen) {
				if(marketConfigToday.has("openHour")) {
					hour = Integer.parseInt((String) marketConfigToday.get("openHour"));
					minutes = Integer.parseInt((String) marketConfigToday.get("openMinutes"));
				}
			}else {
				if(marketConfigToday.has("closeHour")) {
					hour = Integer.parseInt((String) marketConfigToday.get("closeHour"));
					minutes = Integer.parseInt((String) marketConfigToday.get("closeMinutes"));
				}
			}
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minutes);
		}
	}

	private static void fillDetailsToArray(double dOpen, double dHigh, double dLow, double dClose, long lVolume,
			Timestamp timeStamp, int precision, JSONArray respArray, int chartResolution) throws Exception {

		JSONArray obj = new JSONArray();
		String replaceChar = ".";
		int counter = precision;
		while(counter>0) {
			counter--;
			replaceChar+="0";
		}
		obj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(dOpen).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
		obj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(dHigh).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
		obj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(dLow).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
		obj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(dClose).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
		obj.put(lVolume);
		Date formattedDate = new Date(timeStamp.getTime());
		int mins = formattedDate.getMinutes()-chartResolution;
		formattedDate.setMinutes(mins);
		obj.put(DateUtils.formatTimeInUTC(formattedDate, DeviceConstants.DATE_FORMAT).replace("-", "/"));
		respArray.put(obj);
	}

	public static JSONArray getHistoricalChart(String sSymbolToken, String fromDate, String endDate, String sInterval,
			String sAppID, String groupInterval) throws Exception {
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
		
		HistoricalDataResponse historicalResp = historicalAPI.get(historicalRequest, HistoricalDataResponse.class, sAppID,"HistoricalChart");

		List<HistoricalDataObject> historicalObj = historicalResp.getResponseObject();

		List<JSONArray> parsedHistoricalList = new ArrayList<JSONArray>();

		ArrayList<ChartData> resultData = new ArrayList<>();
		String replaceChar = ".";
		int counter = precision;
		while(counter>0) {
			counter--;
			replaceChar+="0";
		}
        if (resolutionInMins == 1440 || ChartUtils.getResolution(groupInterval) == 1) {
            for (int i = 0; i < historicalObj.size(); i++) {
                JSONArray chartObj = new JSONArray();
                chartObj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getOpen()),precision,true).replace(replaceChar, "").replace(",", "")));
                chartObj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getHigh()),precision,true).replace(replaceChar, "").replace(",", "")));
                chartObj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getLow()),precision,true).replace(replaceChar, "").replace(",", "")));
                chartObj.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getClose()),precision,true).replace(replaceChar, "").replace(",", "")));
                chartObj.put(Long.parseLong(String.valueOf(historicalObj.get(i).getVolume())));
                
                if (sInterval.equalsIgnoreCase(ChartUtils.RESOLUTION_1_DAY)) {
                    String sDate = DateUtils.formatDate(historicalObj.get(i).getDate(), SpyderConstants.RES_DATE_FORMAT,
                            DeviceConstants.FROM_DATE_FORMAT) + " 18:00:00";
                    String sUTCDate = DateUtils.getUTCTime(sDate, DeviceConstants.DATE_FORMAT).replace("-", "/");
                    chartObj.put(sUTCDate);
                } else {
                    String sUTC = DateUtils.getUTCTime(historicalObj.get(i).getDate(), SpyderConstants.RES_DATE_FORMAT);
                    String sDate = DateUtils.formatDate(sUTC, SpyderConstants.RES_DATE_FORMAT, DeviceConstants.DATE_FORMAT).replace("-", "/");
                    chartObj.put(sDate);
                }
                parsedHistoricalList.add(chartObj);
            }
            sortJSONArrayDate(parsedHistoricalList, 5);

    		Map<String, JSONArray> mapDateToValues = listToMap(parsedHistoricalList);
    		
    		for (Entry<String, JSONArray> entry : mapDateToValues.entrySet())
    			finalArr.put(entry.getValue());
        }
        else {
			for (int i = 0; i < historicalObj.size(); i++) {
				ChartData chartObj = new ChartData();
				chartObj.setOpen(Double.parseDouble(historicalObj.get(i).getOpen().replace(",", "")));
				chartObj.setClose(Double.parseDouble(historicalObj.get(i).getClose().replace(",", "")));
				chartObj.setHigh(Double.parseDouble(historicalObj.get(i).getHigh().replace(",", "")));
				chartObj.setLow(Double.parseDouble(historicalObj.get(i).getLow().replace(",", "")));
				chartObj.setVolume(Long.parseLong(historicalObj.get(i).getVolume().replace(",", "")));
				String date = historicalObj.get(i).getDate();
				SimpleDateFormat sf = new SimpleDateFormat(SpyderConstants.API_DATE_FORMAT);
				Date formattedDate = sf.parse(date);
				chartObj.setTimestamp(new Timestamp(formattedDate.getTime()));
				resultData.add(chartObj);
			}	
			long start = System.currentTimeMillis();
			finalArr = parseIntradayData(resultData, ChartUtils.getResolution(groupInterval), precision, ExchangeSegment.getExchangeName(sMarketSegID));
			long end = System.currentTimeMillis();
			log.info("Grouping time taken for :"+groupInterval + " "+(end-start));
			for(int i=0;i<finalArr.length();i++)
				parsedHistoricalList.add(finalArr.getJSONArray(i));
        }

		return finalArr;
	}

	public static void sortJSONArrayDate(List<JSONArray> listObj, final int index) {
		Collections.sort(listObj, new Comparator<JSONArray>() {

			@Override
			public int compare(JSONArray a, JSONArray b) {

				SimpleDateFormat sdfo = new SimpleDateFormat(DeviceConstants.DATE_FORMAT);

				Date d1 = null, d2 = null;
				try {
					d1 = sdfo.parse(String.valueOf(a.get(index)).replace("/", "-"));
					d2 = sdfo.parse(String.valueOf(b.get(index)).replace("/", "-"));
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

}
