package com.globecapital.business.chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.json.JSONArray;
import com.globecapital.business.market.Indices;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.QuoteDataDBPool;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.ChartUtils;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class LoadExchChartIntraday {

	private static Logger log = Logger.getLogger(LoadExchChartIntraday.class);
	
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

			log.info("Getting chart data from "+ DBConstants.EXCHANGE_QUOTE);
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
			finalArr = Chart_102.getOneDayChart(resultData, precision);
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
