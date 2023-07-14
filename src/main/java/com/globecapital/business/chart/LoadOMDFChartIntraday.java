package com.globecapital.business.chart;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import com.globecapital.business.market.Indices;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.ChartDBPool;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.ChartUtils;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class LoadOMDFChartIntraday {

    private static Logger log = Logger.getLogger(LoadOMDFChartIntraday.class);

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
        log.info("Getting chart data from "+ DBConstants.OMDF_QUOTE);

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
                if(res.getDouble(DBConstants.OPEN_PRICE) > 0 && res.getDouble(DBConstants.HIGH_PRICE) > 0
                && res.getDouble(DBConstants.LOW_PRICE) > 0 && res.getDouble(DBConstants.CLOSE_PRICE) > 0) {
                    ChartData cData = new ChartData();

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
                tempArr.put(DateUtils.formatTimeInUTC(new Date(resultData.get(i).getTimestamp().getTime()), DeviceConstants.DATE_FORMAT).replace("-", "/"));

                finalArr.put(tempArr);
            }
        }

        return finalArr;

    }
}