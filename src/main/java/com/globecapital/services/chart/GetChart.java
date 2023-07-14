package com.globecapital.services.chart;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.business.chart.Chart;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.utils.DateUtils;

public class GetChart extends BaseService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
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
		
		if( startDate.compareTo(currentDate) == 0 && endDate.compareTo(currentDate) >= 0 ) 
		{
			
			//Just for testing.. need to modify this later.
			String cDate = DateUtils.getCurrentDateTime("yyyy-MM-dd");
			log.info("cDate = "+cDate+" 09:15:00");
			//Intraday
			gcResponse.addToData(DeviceConstants.DATA_POINTS, 
					Chart.getChartOMDF(sSymbolToken, cDate+" 09:15:00", sEndDateTime, sInterval));
			
		}
		else if( startDate.compareTo(currentDate) < 0 && endDate.compareTo(currentDate) >= 0)
		{
			//API + Intraday
			log.info("GetChart = API+INTRADAY");
			JSONArray historicalArr = Chart.getHistoricalChart(sSymbolToken, sStartDateTime, 
					DateUtils.getPreviousDate(SpyderConstants.DATE_FORMAT) + " 23:59:59",
					sInterval, "");
			JSONArray todayArr = Chart.getChartOMDF(sSymbolToken,
			DateUtils.getCurrentDateTime(SpyderConstants.DATE_FORMAT) + " 00:00:00", 
			sEndDateTime, sInterval);
			
			JSONArray finalArr = new JSONArray();
			
			for(int i = 0; i < historicalArr.length(); i++)
				finalArr.put(historicalArr.get(i));
			
			for(int i = 0; i < todayArr.length(); i++)
				finalArr.put(todayArr.get(i));
			
			gcResponse.addToData(DeviceConstants.DATA_POINTS, finalArr);
		}
		else if( startDate.compareTo(currentDate) < 0 && endDate.compareTo(currentDate) < 0)
		{
			//API
			JSONArray historicalArr = Chart.getHistoricalChart(sSymbolToken, sStartDateTime, 
					sEndDateTime, sInterval, "");
			gcResponse.addToData(DeviceConstants.DATA_POINTS, historicalArr);
			
		}
		
	}

}
