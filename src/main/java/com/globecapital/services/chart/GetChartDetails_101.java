package com.globecapital.services.chart;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.business.chart.Chart;
import com.globecapital.business.chart.Chart_102;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.InvalidSession;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionHelper;
import com.globecapital.utils.ChartUtils;
import com.globecapital.utils.DateUtils;

public class GetChartDetails_101  extends BaseService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
		protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		String sSessionID = gcRequest.getRequest().getString(AppConstants.SESSIONID);
		
		Session session = SessionHelper.validateSessionAndAppID(sSessionID, gcRequest.getAppID(), getServletContext(), gcRequest, gcResponse);
		
		if(session == null)
			throw new InvalidSession();

		JSONObject symObj = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ);
		
		String sStartDateTime = gcRequest.getFromData(DeviceConstants.START_DATE);
		String sEndDateTime = gcRequest.getFromData(DeviceConstants.END_DATE);
		
		String sStartDate = DateUtils.formatDate(sStartDateTime, DeviceConstants.DATE_FORMAT, 
				DeviceConstants.TO_DATE_FORMAT);
		String sEndDate = DateUtils.formatDate(sEndDateTime, DeviceConstants.DATE_FORMAT, 
				DeviceConstants.TO_DATE_FORMAT);
		String sCurrentDate = DateUtils.getCurrentDateTime(DeviceConstants.TO_DATE_FORMAT);
		
		Date startDate = DateUtils.getDate(sStartDate, DeviceConstants.TO_DATE_FORMAT);
		Date endDate = DateUtils.getDate(sEndDate, DeviceConstants.TO_DATE_FORMAT);
		Date currentDate = DateUtils.getDate(sCurrentDate, DeviceConstants.TO_DATE_FORMAT);
		
		String sInterval = gcRequest.getFromData(DeviceConstants.INTERVAL);
		String sSymbolToken = symObj.getString(SymbolConstants.SYMBOL_TOKEN);
		
		if(sInterval.equals("5m")||sInterval.equals("10m")||sInterval.equals("15m")||
				sInterval.equals("30m"))
			sInterval = "1m";
		
		if( startDate.compareTo(currentDate) == 0 && endDate.compareTo(currentDate) >= 0 ) 
		{
			
			//Intraday
			JSONArray todayArr = Chart_102.getIntradayChart(sSymbolToken, sStartDateTime, sEndDateTime, sInterval);
			if(todayArr.length() > 1)
				gcResponse.addToData(DeviceConstants.DATA_POINTS, todayArr);
			else
				gcResponse.addToData(DeviceConstants.DATA_POINTS, new JSONArray());
			
		}
		else if( startDate.compareTo(currentDate) < 0 && endDate.compareTo(currentDate) >= 0)
		{
			//API + Intraday
			log.info("GetChart = API+INTRADAY");
			
			if(sInterval.equalsIgnoreCase("1m"))
			{
				String sMonthStartDate = ChartUtils.getChartStartDate();
				log.debug("sMonthStartDate:" + sMonthStartDate);
				
				if(startDate.compareTo(DateUtils.getDate(sMonthStartDate, DeviceConstants.TO_DATE_FORMAT)) < 0)
					sStartDateTime = DateUtils.formatDate(sMonthStartDate, DeviceConstants.TO_DATE_FORMAT, 
							DeviceConstants.FROM_DATE_FORMAT) + " 00:00:00";
			}
			
			JSONArray historicalArr = Chart_102.getHistoricalChart(sSymbolToken, sStartDateTime, 
					DateUtils.getPreviousDate(SpyderConstants.DATE_FORMAT) + " 23:59:59",
					sInterval, gcRequest.getAppID());
			JSONArray todayArr = Chart_102.getIntradayChart(sSymbolToken,
			DateUtils.getCurrentDateTime(SpyderConstants.DATE_FORMAT) + " 00:00:00", 
			sEndDateTime, sInterval);
			
			JSONArray finalArr = new JSONArray();
			
			for(int i = 0; i < historicalArr.length(); i++)
				finalArr.put(historicalArr.get(i));
			
			for(int i = 0; i < todayArr.length(); i++)
				finalArr.put(todayArr.get(i));
			
			if(finalArr.length() > 1)
				gcResponse.addToData(DeviceConstants.DATA_POINTS, finalArr);
			else
				gcResponse.addToData(DeviceConstants.DATA_POINTS, new JSONArray());
			
		}
		else if( startDate.compareTo(currentDate) < 0 && endDate.compareTo(currentDate) < 0)
		{
			//API
			
			if(sInterval.equalsIgnoreCase("1m")) 
			{
				String sMonthStartDate = ChartUtils.getChartStartDate();
				log.debug("sMonthStartDate:" + sMonthStartDate);
				
				if(startDate.compareTo(DateUtils.getDate(sMonthStartDate, DeviceConstants.TO_DATE_FORMAT)) < 0)
					sStartDateTime = DateUtils.formatDate(sMonthStartDate, DeviceConstants.TO_DATE_FORMAT, 
							DeviceConstants.FROM_DATE_FORMAT) + " 00:00:00";
				
				if(endDate.compareTo(DateUtils.getDate(sMonthStartDate, DeviceConstants.TO_DATE_FORMAT)) < 0)
				{
					log.debug("End time less than JUN 2020");
					gcResponse.addToData(DeviceConstants.DATA_POINTS, new JSONArray());
					return;
				}
			}
			JSONArray historicalArr = Chart_102.getHistoricalChart(sSymbolToken, sStartDateTime, 
					sEndDateTime, sInterval, gcRequest.getAppID());
			
			if(historicalArr.length() > 1)
				gcResponse.addToData(DeviceConstants.DATA_POINTS, historicalArr);
			else
				gcResponse.addToData(DeviceConstants.DATA_POINTS, new JSONArray());
			
		}
	}

	@Override
	protected boolean isEncryptionApplicableWS()
	{
		return false;
	}
}