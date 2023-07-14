package com.globecapital.utils;

import java.util.Date;
import java.util.HashMap;

import com.globecapital.business.chart.Chart;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;

public class ChartUtils {
	
	private static HashMap<String, Integer> resolutionInMins = new HashMap<String, Integer>();
	
	private static Integer minutes = 1;
	private static Integer days = 1440;
	
	public static String RESOLUTION_1_MINUTE = "1m";
	public static String RESOLUTION_2_MINUTE = "2m";
	public static String RESOLUTION_5_MINUTE = "5m";
	public static String RESOLUTION_10_MINUTE = "10m";
	public static String RESOLUTION_15_MINUTE = "15m";
	public static String RESOLUTION_30_MINUTE = "30m";
	public static String RESOLUTION_1_DAY = "1d";
	private static String sChartStartDate;
	
//	public static final String NFO_CHART_TYPE_NEAR = "NEAR";
//	public static final String NFO_CHART_TYPE_FAR = "FAR";
//	public static final String NFO_CHART_TYPE_MED = "MID";
	
	static{
		
		resolutionInMins.put(RESOLUTION_1_MINUTE, new Integer(1 * minutes));
		resolutionInMins.put(RESOLUTION_2_MINUTE, new Integer(2 * minutes));
		resolutionInMins.put(RESOLUTION_5_MINUTE, new Integer(5 * minutes));
		resolutionInMins.put(RESOLUTION_10_MINUTE, new Integer(10 * minutes));
		resolutionInMins.put(RESOLUTION_15_MINUTE, new Integer(15 * minutes));
		resolutionInMins.put(RESOLUTION_30_MINUTE, new Integer(30 * minutes));
		resolutionInMins.put(RESOLUTION_1_DAY, new Integer(1 * days));
	}

	public static Integer getResolution(String resolution) throws GCException{
		
		if( resolutionInMins.get(resolution) == null || resolutionInMins.get(resolution) == 0 )
			throw new GCException(InfoIDConstants.NO_DATA);
		
		return resolutionInMins.get(resolution).intValue();
	}

	public static void loadChartDate() throws AppConfigNoKeyFoundException
	{
		int iCountStartDay = AppConfig.getIntValue("chart_1M_past_days");
		setChartStartDate(DateUtils.getNthDateFromTodayDate(DeviceConstants.TO_DATE_FORMAT, 
				iCountStartDay * (-1)));
	}
	
	public static void loadChartDate(Date date) throws AppConfigNoKeyFoundException
	{
		int iCountStartDay = AppConfig.getIntValue("chart_1M_past_days");
		setChartStartDate(DateUtils.getNthDateFromGivenDate(date, iCountStartDay * (-1), 
				DeviceConstants.TO_DATE_FORMAT));
	}
	
	public static String getChartStartDate() {
		return sChartStartDate;
	}

	public static void setChartStartDate(String sChartStartDate) {
		ChartUtils.sChartStartDate = sChartStartDate;
	}

}
