package com.globecapital.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DeviceConstants;
import com.msf.log.Logger;

public class DateUtils {

	private static Logger log = Logger.getLogger(DateUtils.class);

	public static final String formatDate_1980(String source, String frmFormat) {

		int iTime = Integer.parseInt(source) + 315513000; //1980
		Date date = new Date(iTime * 1000L);
		DateFormat format = new SimpleDateFormat(frmFormat);
		format.setTimeZone(TimeZone.getTimeZone("IST"));
		String formatted = format.format(date);
		return formatted;

	}
	
	public static String formatDate(String source, String format) throws Exception
	{

		Date date = new Date(Long.parseLong(source));

		DateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
		
		return sdf.format(date);
	}
	
	
	public static Date getDate(String sSource, String sFormat) throws ParseException
	{
		SimpleDateFormat sdfo = new SimpleDateFormat(sFormat);
		return sdfo.parse(sSource);
	}
	
	public static final String formatDate(String source, String frmFormat, String toFormat) throws ParseException {

		SimpleDateFormat parser = new SimpleDateFormat(frmFormat);
		SimpleDateFormat formatter = new SimpleDateFormat(toFormat);

		return formatter.format(parser.parse(source));

	}
	
	public static final String formatDateWithZone(String sourceDate, String currentFormat, String targetFormat, String timezone) throws ParseException {
		DateFormat sourceDateFormat = new SimpleDateFormat(currentFormat);
	    sourceDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
	    DateFormat destinationDateFormat = new SimpleDateFormat(targetFormat);
	    Date date = sourceDateFormat.parse(sourceDate);
        String targetDate = destinationDateFormat.format(date);
		return targetDate;
	}
	
	public static final String getCurrentDateTime(String sFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(sFormat);
		Date date = new Date();
		return formatter.format(date);
	}

	public static final String getCurrentDate() {

		SimpleDateFormat formatter = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		Date date = new Date();
		return formatter.format(date);
	}
	
	public static String getLastThirtyDays() {

		String formattedDate = "";
		try {
			LocalDate today = LocalDate.now();
			SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
			SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);

			formattedDate = destinationFormat.format(sourceFormat.parse(String.valueOf(today.minusDays(30))));

		} catch (Exception e) {
			log.error(e);
		}
		return formattedDate;
	}

	public static String getQuarterlyPeriod() {

		String formattedDate = "";
		try {
			LocalDate today = LocalDate.now();
			SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
			SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);

			formattedDate = destinationFormat.format(sourceFormat.parse(String.valueOf(today.minusDays(90))));

		} catch (Exception e) {
			log.error(e);
		}
		return formattedDate;
	}

	public static String getHalfYearlyPeriod() {

		String formattedDate = "";
		try {
			LocalDate today = LocalDate.now();
			SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
			SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);

			formattedDate = destinationFormat.format(sourceFormat.parse(String.valueOf(today.minusDays(180))));

		} catch (Exception e) {
			log.error(e);
		}
		return formattedDate;
	}

	public static String getYearlyPeriod() {

		String formattedDate = "";
		try {
			LocalDate today = LocalDate.now();
			SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
			SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);

			formattedDate = destinationFormat.format(sourceFormat.parse(String.valueOf(today.minusDays(365))));

		} catch (Exception e) {
			log.error(e);
		}
		return formattedDate;
	}

	
	public static String getFinancialYear() {

		int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
		int CurrentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);

		int financialYear;
		int prevYear;
		if (CurrentMonth >= 4) {
			prevYear = CurrentYear;
			financialYear = (CurrentYear + 1);
		} else {
			prevYear = CurrentYear - 1;
			financialYear = CurrentYear;
		}
		return String.valueOf(prevYear + "-" + financialYear);
	}

	public static final String formatDate(String date) throws ParseException {
		SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT);
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		return destinationFormat.format(sourceFormat.parse(date));

	}
	public static final String formatResearchDate(String date) throws ParseException {
		SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_START_DATE_FORMAT);
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.REPORT_DATE_FORMAT);
		return destinationFormat.format(sourceFormat.parse(date));

	}	
	
	public static final String formatResearchHistoryDate(String date) throws ParseException {
		SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_START_DATE_FORMAT);
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_HISTORY_DATE_FORMAT);
		return destinationFormat.format(sourceFormat.parse(date));

	}
	public static String getEndofMonthDate(int month, int year) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month - 1, 1);
	    calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
	    Date date = calendar.getTime();
	    DateFormat DATE_FORMAT = new SimpleDateFormat(DeviceConstants.ADVANCE_QUOTE_DATE_FORMAT);
	    return DATE_FORMAT.format(date);
	}
	
	public static String getUTCTime(String format) {
	    final SimpleDateFormat sdf = new SimpleDateFormat(format);
	    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	    final String utcTime = sdf.format(new Date());

	    return utcTime;
	}
	
	public static String getUTCTime(String dateTime, String format) throws ParseException {

		DateFormat formatterIST = new SimpleDateFormat(format);
		formatterIST.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
		Date date = formatterIST.parse(dateTime);
		//System.out.println(formatterIST.format(date)); 

		DateFormat formatterUTC = new SimpleDateFormat(format);
		formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC")); 

		return formatterUTC.format(date);
	}
	
	public static String getPreviousDate(String sFormat) {
	    DateFormat dateFormat = new SimpleDateFormat(sFormat);

	    Calendar calendar = Calendar.getInstance();

	    calendar.add(Calendar.DATE, -1);

	    Date yesterday = calendar.getTime();
	    return dateFormat.format(yesterday).toString();
	}
	
	public static String getTomorrowDate(String sFormat) {
	    DateFormat dateFormat = new SimpleDateFormat(sFormat);

	    Calendar calendar = Calendar.getInstance();

	    calendar.add(Calendar.DATE, 1);

	    Date tomorrow = calendar.getTime();
	    return dateFormat.format(tomorrow).toString();
	}
	
	public static String getNthDateFromTodayDate(String sFormat, int n) {
	    DateFormat dateFormat = new SimpleDateFormat(sFormat);

	    Calendar calendar = Calendar.getInstance();

	    calendar.add(Calendar.DATE, n);

	    Date date = calendar.getTime();
	    return dateFormat.format(date).toString();
	}
	
	public static String formatTimeInUTC(Date d, String format) throws Exception
	{

		SimpleDateFormat sdfUTC = new SimpleDateFormat(format);
		sdfUTC.setTimeZone(TimeZone.getTimeZone("GMT"));

		return sdfUTC.format(d);
	}

	public static String getNthDateFromGivenDate(Date date, int n, String sFormat) {

		DateFormat dateFormat = new SimpleDateFormat(sFormat);
		
	    Calendar calendar = Calendar.getInstance();

	    calendar.setTime(date);
	    
	    calendar.add(Calendar.DATE, n);

	    Date returnDate = calendar.getTime();

	    return dateFormat.format(returnDate).toString();
	}
	
	public static String getFormattedDay(Date date)
	{
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int day=cal.get(Calendar.DATE);

        if(!((day>10) && (day<19)))
        switch (day % 10) 
        {
	        case 1:  
	            return new SimpleDateFormat("d'st' MMM yyyy").format(date);
	        case 2:  
	            return new SimpleDateFormat("d'nd' MMM yyyy").format(date);
	        case 3:  
	            return new SimpleDateFormat("d'rd' MMM yyyy").format(date);
	        default: 
	            return new SimpleDateFormat("d'th' MMM yyyy").format(date);
        }
        return new SimpleDateFormat("d'th' MMM yyyy").format(date);
	}
	
	public static String getFormattedDayMonth(Date date)
	{
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int day=cal.get(Calendar.DATE);

        if(!((day>10) && (day<19)))
        switch (day % 10) 
        {
	        case 1:  
	            return new SimpleDateFormat("d'st' MMM").format(date);
	        case 2:  
	            return new SimpleDateFormat("d'nd' MMM").format(date);
	        case 3:  
	            return new SimpleDateFormat("d'rd' MMM").format(date);
	        default: 
	            return new SimpleDateFormat("d'th' MMM").format(date);
        }
        return new SimpleDateFormat("d'th' MMM").format(date);
	}
	
	public static String formatDateToString(Date date, String sFormat)
	{
		SimpleDateFormat sdfUTC = new SimpleDateFormat(sFormat);
		return sdfUTC.format(date);
	}
	
	public static String getStringDateTime(Timestamp timestamp)
	{
		return new SimpleDateFormat(DBConstants.DB_DATE_TIME_FORMAT).format(timestamp);
	}
	
	public static String getStringDateTime(Date date)
	{
		return new SimpleDateFormat(DBConstants.DB_DATE_TIME_FORMAT).format(date);
	}
	
	public static String getStringDate(java.sql.Date date)
	{
		return new SimpleDateFormat(DBConstants.DB_DATE_FORMAT).format(date);
	}
	
	public static String convert24HoursTo12HoursTime(String sTime) throws ParseException
	{
		SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm:ss a");
        Date _24HourDt = _24HourSDF.parse(sTime);
		return _12HourSDF.format(_24HourDt);
	}
	
	public static String getPreviousFinancialYear() {

		int CurrentYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
		int CurrentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
		int financialYear;
		int prevYear;
		if (CurrentMonth >= 4) {
			prevYear = CurrentYear;
			financialYear = (CurrentYear + 1);
		} else {
			prevYear = CurrentYear - 1;
			financialYear = CurrentYear;
		}
		return String.valueOf(prevYear + "-" + financialYear);
	}
	
	public static String getFinancialYearByDate(JSONObject reportDates) throws JSONException, ParseException {
		SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);
		Date endDate = destinationFormat.parse(reportDates.getString(DeviceConstants.TO_DATE));
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		String year = "";
		if(endCal.get(Calendar.MONTH) >= 3) {
			year = String.valueOf(endCal.get(Calendar.YEAR)).substring(2);
			 return (year+(Integer.parseInt(year)+1));
		}else {
			year = String.valueOf(endCal.get(Calendar.YEAR)).substring(2);
			return (Integer.parseInt(year)-1+""+year);
		}
	}
	
	public static String getFromDateByYear(String financialYears) {

		String formattedDate = "";
		String[] years=financialYears.split("-");
		String financialYear=years[0];
		try {
			
			Calendar cal = Calendar.getInstance();			
			  cal.set(Calendar.MONTH, Calendar.APRIL);
			  cal.set(Calendar.YEAR, Integer.parseInt(financialYear));
			  cal.set(Calendar.DAY_OF_MONTH, 1);
			  Date date = cal.getTime();			
			SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
			SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);

			formattedDate = destinationFormat.format(date);

		} catch (Exception e) {
			log.error(e);
		}
		return formattedDate;
	}
	
	public static String getToDateByYear(String financialYears) {

		String formattedDate = "";
		String[] years=financialYears.split("-");
		String financialYear=years[1];
		try {
			
			Calendar cal = Calendar.getInstance();			
			  cal.set(Calendar.MONTH, Calendar.MARCH);
			  cal.set(Calendar.YEAR, Integer.parseInt(financialYear));
			  cal.set(Calendar.DAY_OF_MONTH, 31);
			  Date date = cal.getTime();			
			SimpleDateFormat sourceFormat = new SimpleDateFormat(DeviceConstants.FROM_DATE_FORMAT);
			SimpleDateFormat destinationFormat = new SimpleDateFormat(DeviceConstants.TO_DATE_FORMAT);

			formattedDate = destinationFormat.format(date);

		} catch (Exception e) {
			log.error(e);
		}
		return formattedDate;
	}
	
	public static void main(String args[]) throws Exception {
		//System.out.println(getTomorrowDate("dd/MM/YYYY"));
		//System.out.println(getCurrentDateTime("dd MMM yyyy"));
//		System.out.println(getLastThirtyDays());
//		System.out.println(getHalfYearlyPeriod());
//		System.out.println(getQuarterlyPeriod());
//		System.out.println(getYearlyPeriod());
//		System.out.println(getEndofMonthDate(2, 2019));
		//System.out.println(getUTCTime("dd MM yyyy"));
		//System.out.println(getUTCTime("2020-04-15 17:11:00", "yyyy-MM-dd HH:mm:ss"));
		//System.out.println(formatDate("16 May 2019  00:00:00", "dd MMM yyyy hh:mm:ss", "dd MMM yyyy"));
		//System.out.println(formatDate("2020-04-09T00:00:00", DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM,
				//"dd'th' MMM yyyy"));
		//System.out.println(getPreviousDate());
		//System.out.println(getUTCTime("13-04-2020 09:15:59", "dd-MM-yyyy HH:mm:ss")); 
				//SpyderConstants.RES_DATE_FORMAT);
		//System.out.println(formatDate("1586922300000", "yyyy-MM-dd"));
		//System.out.println(formatDate("May 2019", "MMM yyyy", ));
		//String s = "May 2019";
		//System.out.println(s.toUpperCase());
//		String sFormattedDate = formatDate("2020-04-23T00:00:00", 
//				DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_FROM, 
//				DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
//		System.out.println(sFormattedDate);
//		
//		Date dDate = getDate(sFormattedDate, DeviceConstants.ANNOUNCEMENT_DATE_FORMAT_TO);
//		System.out.println(dDate);
//		
//		String sFinalFormattedDate = getFormattedDay(dDate);
//		System.out.println(sFinalFormattedDate);
//		
//		System.out.println("res"+formatResearchDate("11/20/2019 12:00:00 AM"));
//		System.out.println("resssss"+formatDate("25-Jun-2020","dd-MMM-yyyy",DBConstants.UNIQ_DESC_DATE_FORMAT));
		//System.out.println(getNthDateFromTodayDate(DeviceConstants.TO_DATE_FORMAT, 7));
		
	}

}
