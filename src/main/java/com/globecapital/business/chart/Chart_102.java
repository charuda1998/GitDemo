package com.globecapital.business.chart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import com.globecapital.api.spyder.chart.HistoricalDataAPI;
import com.globecapital.api.spyder.chart.HistoricalDataObject;
import com.globecapital.api.spyder.chart.HistoricalDataRequest;
import com.globecapital.api.spyder.chart.HistoricalDataResponse;
import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.business.market.Indices;
import com.globecapital.config.AppConfig;
import com.globecapital.config.IndicesChart;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.ChartUtils;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class Chart_102 {

    private static Logger log = Logger.getLogger(Chart_102.class);

    public static JSONArray getIntradayChart(String sSymbolToken, String sFromDate, String sToDate, String sInterval)
            throws Exception {
    	try {
			if(AppConfig.getValue("quote_data.use_exch_quote_updater").equals("true"))
				return LoadExchChartIntraday.getIntradayChart(sSymbolToken, sFromDate, sToDate, sInterval);
			else
				return LoadOMDFChartIntraday.getIntradayChart(sSymbolToken, sFromDate, sToDate, sInterval);
		}catch(AppConfigNoKeyFoundException ex) {
			log.error("quote_data.use_exch_quote_updater Key not found in App Config");
		}
    	return new JSONArray();
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

        List<JSONArray> parsedHistoricalList = new ArrayList<JSONArray>();
        int counter = precision;
        String replaceChar = ".";
        while(counter>0) {
            counter--;
            replaceChar+="0";
        }

        for (int i = 0; i < historicalObj.size(); i++) {
            JSONArray tempArr = new JSONArray();
            tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getOpen()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
            tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getHigh()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
            tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getLow()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
            tempArr.put(Float.parseFloat(PriceFormat.formatPrice(String.valueOf(historicalObj.get(i).getClose()).replace(",", ""), precision, true).replace(",", "").replace(replaceChar, "")));
            tempArr.put(Long.parseLong(historicalObj.get(i).getVolume().replace(",", "")));

            if (sInterval.equalsIgnoreCase(ChartUtils.RESOLUTION_1_DAY)) {
                String sDate = DateUtils.formatDate(historicalObj.get(i).getDate(), SpyderConstants.RES_DATE_FORMAT,
                        DeviceConstants.FROM_DATE_FORMAT) + " 18:00:00";
                String sUTCDate = DateUtils.getUTCTime(sDate, DeviceConstants.DATE_FORMAT);
                tempArr.put(sUTCDate.replace("-", "/"));
            } else {
                String sUTC = DateUtils.getUTCTime(historicalObj.get(i).getDate(), SpyderConstants.RES_DATE_FORMAT);
                String sDate = DateUtils.formatDate(sUTC, SpyderConstants.RES_DATE_FORMAT, DeviceConstants.DATE_FORMAT);
                tempArr.put(sDate.replace("-", "/"));
            }
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

}