package com.globecapital.jobs;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.scripmaster.ScripMasterAPI;
import com.globecapital.api.ft.scripmaster.ScripMasterRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.db.GCDBPool;
import com.globecapital.jmx.Monitor;
import com.globecapital.utils.DateUtils;
import com.msf.cmots.api.data_v1.Company;
import com.msf.cmots.api.data_v1.CompanyList;
import com.msf.cmots.api.equity.GetCompanyMaster_v1;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class ScripMasterCmotsDump {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    private static Logger log;

    public static HashMap<String, JSONObject> token = new HashMap<String, JSONObject>();
    public static HashMap<String, Object> FNOExistsMap = new HashMap<String, Object>();

    public static void main(String[] args) throws Exception {

        long beforeExecution = System.currentTimeMillis();

        FileInputStream stream = null;

        String config_file = args[0];
        Properties JSLogProperties = new Properties();
        try {
            stream = new FileInputStream(config_file);
            JSLogProperties.load(stream);
            Logger.setLogger(JSLogProperties);
            log = Logger.getLogger(ScripMasterDump.class);
            log.info("#############################################################################");
            log.info("##### JOB NAME : Scripmaster Dump - BEGINS");
            log.info("##### TIME : " + DateUtils.getCurrentDateTime(DeviceConstants.OPTIONS_DATE_FORMAT_1));
      } catch (ConfigurationException e) {
    	  e.printStackTrace();
        }

        try {
            AppConfig.loadFile(config_file);
        } catch (Exception e) {
            log.error("Cannot load  config properties %s", e);
            System.exit(1);
        }

        try {
            com.msf.cmots.config.AppConfig.loadFile(config_file);
        } catch (Exception e) {
            log.error("Cannot load cmots config properties %s", e);
            System.exit(1);
        }

        log.info("************************ STATS ************************");

        GCDBPool.initDataSource(AppConfig.getProperties());

        String[] equities = AppConfig.getArray("equities");

        ScripMasterDump(equities);
        
        try {

        GetCompanyMaster_v1 companyMaster = new GetCompanyMaster_v1();

        CompanyList companyMasterList = companyMaster.invoke();

        CompanyMasterDump(companyMasterList);

        CoCodeDump();
        
        } catch (Exception e) {
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NSE);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_BSE);
            Monitor.markCritical("Exception : "+e.getMessage());
            log.info("Error :: " + e);
            e.printStackTrace();
        }

        String[] symbols = AppConfig.getArray("segments");

        ScripMasterDump(symbols);

        deleteExpiredContractsFromDB();
        
        deleteNcdexAndBsecdsScrips();	//This can be removed for phase 2.Temporary support removal for NCDEX/BSECDS scrips

        updateFNOExistsForEquities();
        
        log.info("*******************************************************");

        long afterExecution = System.currentTimeMillis();

        log.info("##### Total time taken for job completion: " + (afterExecution - beforeExecution) + " secs");
        log.info("##### JOB NAME : Scripmaster Dump - ENDS");
        log.info("#############################################################################");

    }

    @SuppressWarnings("rawtypes")
	private static void updateFNOExistsForEquities() throws SQLException {
    	String query = DBQueryConstants.UPDATE_FNO_EXISTS;
    	Set<Entry<String, Object>> fnoResult = FNOExistsMap.entrySet();
    	Iterator<Entry<String, Object>> iterator = fnoResult.iterator();
    	Connection conn = GCDBPool.getInstance().getConnection();
    	PreparedStatement ps = conn.prepareStatement(query);
    	while(iterator.hasNext()) {
    		Map.Entry entry = (Map.Entry) iterator.next();
    		if(entry.getValue().equals((false)))
    			ps.setString(1, String.valueOf(entry.getKey()));
    	}
    	ps.executeBatch();
    }

	public static void CoCodeDump() throws Exception {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int cn = 0;

        String query = DBQueryConstants.SELECT_COCODE;

//        log.info("CoCodeDump query :: " + query);

        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
            	JSONObject obj = new JSONObject();
            	String sToken = rs.getString("nToken");
            	obj.put(DBConstants.CM_CO_CODE, rs.getString("CM_co_code"));
            	obj.put(DBConstants.CM_SECTOR_FORMAT, rs.getString("CM_SectorFormat"));
                token.put(sToken, obj);
                cn++;
            }

        } finally {
            Helper.closeResultSet(rs);
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }
    }

    public static void ScripMasterDump(String[] symbols) throws Exception {

        ScripMasterRequest ftrequest = new ScripMasterRequest();

        try {
            for (String s : symbols) {

                if(s.equals(ExchangeSegment.NSE_SEGMENT_ID))
                    Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NSE);
                else if(s.equals(ExchangeSegment.BSE_SEGMENT_ID))
                    Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_BSE);
                else if(s.equals(ExchangeSegment.NFO_SEGMENT_ID))
                    Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NFO);
                else if(s.equals(ExchangeSegment.MCX_SEGMENT_ID))
                    Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_MCX);
//                else if(s.equals(ExchangeSegment.NCDEX_SEGMENT_ID))	//Temporary removal. To be added in Phase 2
//                    Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NCDEX);
                else if(s.equals(ExchangeSegment.NSECDS_SEGMENT_ID))
                    Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NSECDS);
//                else if(s.equals(ExchangeSegment.BSECDS_SEGMENT_ID))	//Temporary removal. To be added in Phase 2
//                    Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_BSECDS);

                ftrequest.setMKtSegId(Integer.parseInt(s));
                ftrequest.toString();

                ScripMasterAPI scripMasterAPI = new ScripMasterAPI();
                String scripMasterResponse = scripMasterAPI.post(ftrequest); 
                JSONObject scripMasterObject = new JSONObject(scripMasterResponse);
                JSONArray symbolResp = scripMasterObject.getJSONArray("ResponseObject");

                if( ExchangeSegment.isEquitySegment(s) && symbolResp.length() > 100 )
                    deleteEquityFromDB(s);

                Connection conn = null;
                PreparedStatement ps = null;
                String query = "";

                if ( ExchangeSegment.isEquitySegment(s) )
                    query = DBQueryConstants.INSERT_SYMBOLS;
                else
                    query = DBQueryConstants.INSERT_DERIVATIVES;

//                log.info("Query :: " + query);

                try {

                    conn = GCDBPool.getInstance().getConnection();
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(query);
                    int count = 0, skippedCount = 0;
                    for (int i = 0; i < symbolResp.length(); i++) {

                        JSONObject obj = symbolResp.getJSONObject(i);
                        int nSpread;
                        if (obj.get(FTConstants.N_SPREAD).toString() == null
                                || obj.get(FTConstants.N_SPREAD).toString().equalsIgnoreCase("null"))
                            nSpread = 0;
                        else
                            nSpread = obj.getInt(FTConstants.N_SPREAD);
                        if (nSpread == 0) {
                            ScripMasterHelper scripMasterHelper = new ScripMasterHelper(obj);

                            String nToken = String.valueOf(obj.getInt(FTConstants.TOKEN_ID));
                            String marketSegmentId = String.valueOf(obj.getInt(FTConstants.SEGMENT));
                            String series = obj.getString(FTConstants.SERIES_NAME).trim();
                            String fiiFlag = String.valueOf(obj.getInt(FTConstants.FII_FLAG));
                            String secDesc = obj.getString(FTConstants.SECURITY_DESC);
                            String secStatus = String.valueOf(obj.getInt(FTConstants.SECURITY_STATUS));
                            String normalMarketAllowed =String.valueOf(obj.getInt(FTConstants.MARKET_ALLOWED));
                            String instrumentType = String.valueOf(obj.getInt(FTConstants.INSTRUMENT_TYPE));
                            if ((marketSegmentId.equals(ExchangeSegment.NSE_SEGMENT_ID) && (!series.equals("EQ") && !series.equals("BE") && !series.equals("BZ") && !series.equals("E1") && !series.equals("SM") && !series.equals("ST") )) ||
                                    ((marketSegmentId.equals(ExchangeSegment.BSE_SEGMENT_ID) && (!nToken.startsWith("5") && !series.equals("A")) && !series.equals("XT") ) ) ||
                                    (marketSegmentId.equals("4")) ||
                                    (series.equals("MF")) ||
                                    series.equals("B") && ("0").equals(normalMarketAllowed)||    //Mutual Fund scrips
                                    (series.equals("F")) ||                                        //Mutual Fund scrips
//                                    (series.equals("E")) ||                                        //ETF bond scrips
//                                    (series.equals("EQ") && instrumentType.equals("4")) ||        //ETF bond scrips
                                    (!fiiFlag.equals("0"))||
                                    secDesc.contains("Mutual F") || secDesc.contains("MUTUAL F"))
                                {
                                    skippedCount++;
                                    continue;
                                    // Skipping:
                                    // 1.Unnecessary NSE Scrip
                                    // 2. Unnecessary BSE Scrips
                                    // 3. BSE derv scrips
                                    // 4. MF Scrips
                                    // 5. Spread Contracts
                                }

                            String nTokenSegment = obj.getInt(FTConstants.TOKEN_ID) + "_"
                                    + obj.getInt(FTConstants.SEGMENT);
                            
                            ps.setString(1, nTokenSegment);

                            ps.setString(2, marketSegmentId);

                            ps.setString(3, nToken);

                            try {
	                            String symb = obj.getString(FTConstants.SYMBOL_NAME);
	                            if((ExchangeSegment.isEquitySegment(marketSegmentId)))
	                            	FNOExistsMap.put(symb, false);
	                            else if((ExchangeSegment.NFO_SEGMENT_ID.equals((marketSegmentId)))) {
	                            	if(!Objects.isNull(FNOExistsMap.get(symb)))
	                            		FNOExistsMap.put(symb, true);
	                            }
	                            ps.setString(4, symb);
	                            ps.setString(5, series);
	                            ps.setString(6, obj.getString(FTConstants.INSTRUMENT_NAME).trim());
	                            ps.setString(7, String.valueOf(obj.getInt(FTConstants.N_EXPIRY_DATE)));
	                            String formatExp = obj.getString(FTConstants.EXPIRY_DATE);
	                            if(!formatExp.isEmpty())
	                            	formatExp = getExpiry(formatExp);
	                            ps.setString(8, formatExp);
	
	//                            String strikePrice = formatPrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),
	//                            		String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)),
	//                            		obj.getString(FTConstants.INSTRUMENT_NAME),
	//                            		getPrecision(String.valueOf(obj.getInt(FTConstants.SEGMENT))));
	                            
	                            String strikePrice = "";
	                            String originalStrikePrice="";
	                            if(ExchangeSegment.isCurrencySegment(marketSegmentId)) {
	                            	originalStrikePrice = ScripMasterHelper.formatOriginalStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),2 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), ExchangeSegment.NSECDS);
	                            	strikePrice = ScripMasterHelper.formatStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),2 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), ExchangeSegment.NSECDS);
	                            }else if(ExchangeSegment.getExchangeName((marketSegmentId)).equals(ExchangeSegment.NFO)) {
	                            	originalStrikePrice = ScripMasterHelper.formatOriginalStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),2 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), ExchangeSegment.NFO);                       
	                            	strikePrice = ScripMasterHelper.formatStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),2 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), ExchangeSegment.NFO);
	                            }else if(ExchangeSegment.getExchangeName((marketSegmentId)).equals(ExchangeSegment.MCX)) {
	                            	originalStrikePrice = ScripMasterHelper.formatOriginalStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),2 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), "");                       
	                            	strikePrice = ScripMasterHelper.formatStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),2 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)),"");
	                            }
	                            else {
	                            	originalStrikePrice = ScripMasterHelper.formatOriginalStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),2 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), "");                       
	                            	strikePrice = ScripMasterHelper.formatStrikePrice(String.valueOf(obj.getInt(FTConstants.STRIKE_PRICE)),0 , String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)),"");
	                            }
	                            ps.setString(9, strikePrice);
	                            String option = getOption(obj.getString(FTConstants.OPTION),
	                                    obj.getString(FTConstants.INSTRUMENT_NAME));
	                            ps.setString(10, option);
	                            String isin = obj.getString(FTConstants.ISIN);
	
	                            ps.setString(11, isin);
	                            ps.setString(12, String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)));
	                            ps.setString(13, String.valueOf(obj.getInt(FTConstants.LOT_SIZE)));
	
	                            String dispPriceTick = getDispPriceTick(String.valueOf(obj.getInt(FTConstants.TICK_PRICE)),
	                                    String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)));
	
	                            String priceTick = getPriceTick(String.valueOf(obj.getInt(FTConstants.TICK_PRICE)),
	                                    String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)));
	
	                            ps.setString(14, priceTick);
	
	                            ps.setString(15, secDesc);
	                            String assetToken = String.valueOf(obj.getInt(FTConstants.ASSET));
	                            ps.setString(16, assetToken);
	                            ps.setString(17, String.valueOf(obj.getInt(FTConstants.PRICE_NUM)));
	                            ps.setString(18, String.valueOf(obj.getInt(FTConstants.PRICE_DEN)));
	                            ps.setString(19, instrumentType);
	                            ps.setString(20, normalMarketAllowed);
	                            ps.setString(21, String.valueOf(obj.getInt(FTConstants.MINIMUM_LOT)));
	                            ps.setString(22, String.valueOf(obj.getInt(FTConstants.LOW_PRICE_RANGE)));
	                            ps.setString(23, String.valueOf(obj.getInt(FTConstants.HIGH_PRICE_RANGE)));
	                            ps.setString(24, String.valueOf(obj.getInt(FTConstants.OPEN_INTEREST)));
	
	                            String exch = ExchangeSegment
	                                    .getExchangeName(String.valueOf(obj.getInt(FTConstants.SEGMENT)));
	
	                            ps.setString(25, exch);
	
	                            String precision = getPrecision(String.valueOf(obj.getInt(FTConstants.SEGMENT)));
	
	                            ps.setString(26, precision);
	                            String assetClass = getAssetClass(obj.getString(FTConstants.INSTRUMENT_NAME));
	
	                            ps.setString(27, assetClass);
	                            String expFromSecurityDesc = getSearchExpiryFromDesc(formatExp,
	                                    obj.getString(FTConstants.SECURITY_DESC),
	                                    obj.getString(FTConstants.INSTRUMENT_NAME),
	                                    strikePrice,
	                                    getOption(obj.getString(FTConstants.OPTION),
	                                            obj.getString(FTConstants.INSTRUMENT_NAME)));
	
	                            String symDetails = getSymbolDetails(String.valueOf(obj.getInt(FTConstants.SEGMENT)),
	                                    obj.getString(FTConstants.INSTRUMENT_NAME), expFromSecurityDesc,
	                                    obj.getString(FTConstants.OPTION),
	                                    strikePrice, precision,
	                                    String.valueOf(obj.getInt(FTConstants.DECIMAL_LOCATOR)), exch);
	
	                            ps.setString(28, symDetails);
	                            ps.setString(29, getSearchExpiry(formatExp, obj.getString(FTConstants.INSTRUMENT_NAME)));
	                            ps.setString(30, getSearchInstrument(obj.getString(FTConstants.INSTRUMENT_NAME)));
	
	                            String searchSymbolDetails = getSearchSymbolDetails(
	                                    obj.getString(FTConstants.INSTRUMENT_NAME), exch, expFromSecurityDesc,
	                                    originalStrikePrice,
	                                    getOption(obj.getString(FTConstants.OPTION),
	                                            obj.getString(FTConstants.INSTRUMENT_NAME)));
	
	                            ps.setString(31, searchSymbolDetails);
	                            ps.setString(32, String.valueOf(obj.getInt(FTConstants.N_BASE_PRICE)));
	
	                            ps.setString(33, dispPriceTick);
	                            String sISINCode = obj.getString(FTConstants.ISIN) + "_"
	                                    + String.valueOf(obj.getInt(FTConstants.SEGMENT));
	
	                            ps.setString(34, sISINCode);
	
	                            ps.setString(35, expFromSecurityDesc);
	                            ps.setInt(36, nSpread);
	                            String uniqDesc = scripMasterHelper.getUniqDesc(strikePrice);
	                            ps.setString(37, uniqDesc);
	
	                            String marketSeg = String.valueOf(obj.getInt(FTConstants.SEGMENT));
	                            
	                            if ( !ExchangeSegment.isEquitySegment(marketSeg) ) {
	                                JSONObject companyObj = token.get(assetToken);
	                                if(companyObj != null  && companyObj.has(DBConstants.CM_CO_CODE) && companyObj.has(DBConstants.CM_SECTOR_FORMAT)) {
	                                	ps.setString(38, companyObj.getString(DBConstants.CM_CO_CODE));
	                                	ps.setString(39, companyObj.getString(DBConstants.CM_SECTOR_FORMAT));
	                                }
	                                else
	                                {
	                                	ps.setString(38, null);
	                                	ps.setString(39, null);
	                                }
	                                ps.setString(40, scripMasterHelper.getSymDet(originalStrikePrice));
	                                ps.setString(41, searchSymbolDetails);
	                                ps.setString(42, fiiFlag);
	                                ps.setString(43, secStatus);
	                                ps.setString(44, ScripMasterHelper.getCompanyName(symb, expFromSecurityDesc.replace(" ", ""), originalStrikePrice, option, obj.getString(FTConstants.INSTRUMENT_NAME), secDesc));
	                                ps.setString(45, String.valueOf(FNOExistsMap.get(symb)));
	                                ps.setString(46, formMappingSymbolUniqDescNonEq(obj.getString(FTConstants.INSTRUMENT_NAME).trim(), symb, exch, formatExp, strikePrice, option));
	                                ps.setString(47, String.valueOf(obj.getInt(DeviceConstants.ISSUE_ST_DT)));
	                                ps.setString(48, String.valueOf(obj.getInt(DeviceConstants.ISSUE_MATURITY_DT)));
	                                ps.setString(49, String.valueOf(obj.getInt(DeviceConstants.DELIVERY_ST_DT)));
	                                ps.setString(50, String.valueOf(obj.getInt(DeviceConstants.ISSUE_MATURITY_DT)));
	                                ps.setString(51, String.valueOf(obj.getInt(DeviceConstants.MAX_SINGLE_TRANS_QTY)));
	                                ps.setString(52, obj.getString(DeviceConstants.QTY_UNIT));
	                            } else if ( ExchangeSegment.isEquitySegment(marketSeg) ) {
	                                ps.setString(38, exch);
	                                ps.setString(39, secDesc);
	                                ps.setString(40, fiiFlag);
	                                ps.setString(41, secStatus);
	                                ps.setString(42, secDesc);
	                                ps.setString(43, String.valueOf(FNOExistsMap.get(symb)));
	                                ps.setString(44, formMappingSymbolUniqDescEq("STK", symb, series, exch));
	                            }
                            }catch(Exception ex){
                            	log.error("Error while processing Scrip with MarketSegmentId : "+marketSegmentId +" and nToken : "+nToken);
                            	log.info("Error :: " + ex);
                            }
                            count++;
                            ps.addBatch();
                        }
                    }
                    long beforeExecution = System.currentTimeMillis();
                    int[] rows = ps.executeBatch();
                    conn.commit();
                    long afterExecution = System.currentTimeMillis();
//                    log.info("total time taken: " + (afterExecution - beforeExecution));
//                    log.info("Total rows inserted :: " + rows.length);

                    log.info("Total no of records received for " + ExchangeSegment.getExchangeName(s) + " : " + symbolResp.length());
                    log.info("Total no of records inserted for " + ExchangeSegment.getExchangeName(s) + " : " + rows.length );
                    log.info("No of skipped scrips : " + skippedCount);

                    Monitor.markSuccess(String.format("No of Rows received : %d, No of Rows inserted : %d",symbolResp.length(), rows.length));
                }

                catch (Exception e) {
                    Monitor.markCritical("Exception : "+e.getMessage());
                    log.info("Error :: " + e);
                } finally {
                    Helper.closeStatement(ps);
                    Helper.closeConnection(conn);
                }
            }
        } catch (Exception e) {
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NSE);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_BSE);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NFO);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_MCX);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NCDEX);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NSECDS);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_BSECDS);
            Monitor.markCritical("Exception : "+e.getMessage());
            log.info("Error :: " + e);
            e.printStackTrace();
        }

    }

	private static void CompanyMasterDump(CompanyList companyList) throws Exception {

        Connection conn = null;
        PreparedStatement ps = null;
        String query = DBQueryConstants.UPDATE_COCODE;
//        log.info("Query :: " + query);
        try {
            conn = GCDBPool.getInstance().getConnection();

            ps = conn.prepareStatement(query);

            for (Company company : companyList) {

                ps.setString(1, company.getCoCode());
                ps.setString(2, company.getBsecode());
                ps.setString(3, company.getCategoryname());
                ps.setString(4, company.getBsegroup());
                ps.setString(5, company.getMcaptype());
                ps.setString(6, company.getSectorcode());

                String sector = company.getSectorname();
                if(sector!=null)
                    sector = sector.replaceAll(" ", "_").toLowerCase();

                String shortSector = AppConfig.optValue(sector, "");
                if(!shortSector.contentEquals(""))
                    ps.setString(7, shortSector);
                else
                    ps.setString(7, company.getSectorname());

                ps.setString(8, company.getFormatType());
                ps.setString(9, company.getIsin());

                ps.addBatch();

            }

            int[] rows = ps.executeBatch();
//            log.info("Updated rows :: " + rows.length);

        } catch (Exception e) {
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_NSE);
            Monitor.markCritical("Exception : "+e.getMessage());
            Monitor.setJobsBeans(Monitor.SCRIPMASTER_DUMP_BSE);
            Monitor.markCritical("Exception : "+e.getMessage());
            log.info("Error :: " + e);
            e.printStackTrace();
        } finally {
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }

    }
	

    public static String getPrecision(String marketsegment) throws ClassNotFoundException {

        if (marketsegment.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID)
                || marketsegment.equalsIgnoreCase(ExchangeSegment.BSECDS_SEGMENT_ID))

            return OrderConstants.PRECISION_4;

        else
            return OrderConstants.PRECISION_2;

    }

    public static String getPriceTick(String priceTick, String locator) {

        BigDecimal dispPriceTick = null;

        try {
            dispPriceTick = new BigDecimal(priceTick).divide(new BigDecimal(locator));

        } catch (Exception e) {
        	log.info("Error :: " + e);
        }

        if (String.valueOf(dispPriceTick).replaceAll("[.]", "").length() == 2) {

            return String.valueOf(dispPriceTick).replaceAll("[0,.]", "").concat("0");
        } else

            return String.valueOf(dispPriceTick).replaceAll("[0,.]", "");

    }

    public static String getSymbolDetails(String segId, String instName, String expDate, String opttype,
            String strikeprice, String prec, String decimallocator, String exchange) {

        String detail = "";
        String strikePrice = "";

        if (instName.length() < 1 || instName.equals(FTConstants.EQUITIES)) { // detail will always be empty for
                                                                                // Equities
            return detail;
        }

        else {
            detail = "" + expDate;
            if ( InstrumentType.isOptions(instName)) {
//            	if(InstrumentType.isCurrency(instName))
//            		strikePrice = formatStrikePrice(strikePrice, 2, decimallocator);
//            	else
//            		strikePrice = formatStrikePrice(strikePrice, 0, decimallocator);
                detail = expDate + strikeprice + opttype;

            } else if ( InstrumentType.isFutures(instName) ) {
                detail = InstrumentType.FUTURES + expDate;
            }

        }
        return detail;
    }

    public static String getAssetClass(String instName) {
        if ( InstrumentType.isFutures(instName) || InstrumentType.isOptions(instName) )
            return InstrumentType.DERIVATIVE;
        else
            return InstrumentType.CASH;
    }

    public static String getExpiry(String expdate) {
        String formattedDate = "";
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FROM_FORMAT);
            SimpleDateFormat destinationFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FORMAT);

            formattedDate = destinationFormat.format(sourceFormat.parse(expdate));
        } catch (Exception e) {
        	log.info("Error :: " + e);
        }
        return formattedDate.toUpperCase();
    }

    public static String getDispPriceTick(String Price, String DecimalLocator) {

        return String.valueOf(new BigDecimal(Price).divide(new BigDecimal(DecimalLocator)));

    }

    public static String getSearchExpiryFromDesc(String exp, String security, String instrument, String strikeprice,
            String option) {
        String date = "";
        String sec = security.replace(".", "");

        HashMap<String, String> month = new HashMap<>();
        month.put("JAN", "1");
        month.put("FEB", "2");
        month.put("MAR", "3");
        month.put("APR", "4");
        month.put("MAY", "5");
        month.put("JUN", "6");
        month.put("JUL", "7");
        month.put("AUG", "8");
        month.put("SEP", "9");
        month.put("OCT", "10");
        month.put("NOV", "11");
        month.put("DEC", "12");

        if (exp.length() > 0) {
            String[] splitno = exp.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

            String[] split = exp.split("(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)");
            String descyearDdatewithsplit = "[A-Z]+[0-9]{2}[D]" + split[0] + "[A-Z0-9]+";
            String descdateDyearwithsplit = "[A-Z]+" + split[0] + "[D]" + split[1].substring(2, 4) + "[A-Z0-9]+";
            String descyearMonthNodatewithsplit = "[A-Z]+[0-9]{2}" + month.get(splitno[1]) + split[0] + "[A-Z0-9]+";
            String descdateMonthNoyearwithsplit = "[A-Z]+" + split[0] + month.get(splitno[1]) + split[1].substring(2, 4)
                    + "[A-Z0-9]+";

            if (exp != null) {

                try {
                    SimpleDateFormat sourceFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FORMAT);
                    SimpleDateFormat destinationFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_TO_FORMAT);
                    String formattedDate1 = destinationFormat.format(sourceFormat.parse(exp));

                    SimpleDateFormat monthDestinationFormat = new SimpleDateFormat(DBConstants.SCRIP_EXPIRY_DATE_FORMAT);

                    SimpleDateFormat dateMonthDestinationFormat = new SimpleDateFormat(DBConstants.DATE_MONTH_FORMAT);

                    date = dateMonthDestinationFormat.format(Date.parse(formattedDate1));
//	For handling of confusion between weekly/monthly expiry contracts			
//                    if (sec.matches(descyearDdatewithsplit) || sec.matches(descdateDyearwithsplit)
//                            || sec.matches(descyearMonthNodatewithsplit) || sec.matches(descdateMonthNoyearwithsplit)) {
//                        date = dateMonthDestinationFormat.format(Date.parse(formattedDate1));
//                    } else {
//                        date = monthDestinationFormat.format(Date.parse(formattedDate1));
//                    }
                } catch (Exception e) {
                	log.info("Error :: " + e);
                }
            }
        } else {
            date = "";
        }
        String formattedDate = date.toUpperCase();

        return formattedDate;

    }

    public static String getSearchExpiry(String formattedDate, String inst) {

        String month = "";
        if (formattedDate != null && !formattedDate.isEmpty()) {
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FORMAT);
                SimpleDateFormat destinationFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_TO_FORMAT);
                String formattedDate1 = destinationFormat.format(sourceFormat.parse(formattedDate));

                SimpleDateFormat futuresDestinationFormat = new SimpleDateFormat(DBConstants.SCRIP_EXPIRY_DATE_FORMAT);
                SimpleDateFormat optionsDestinationFormat = new SimpleDateFormat(DBConstants.DATE_MONTH_FORMAT);

                if (InstrumentType.isFutures(inst)) {
                    month = futuresDestinationFormat.format(Date.parse(formattedDate1));
                } else {
                    month = optionsDestinationFormat.format(Date.parse(formattedDate1));
                }

            } catch (Exception e) {
            	log.info("Error :: " + e);
            }
        } else {
            month = "";
        }
        return month.toUpperCase();
    }

    public static String getSearchInstrument(String inst) {
        String instrument = "";
        if (inst != null) {
            try {
                if (InstrumentType.isFutures(inst)) {
                    instrument = InstrumentType.FUTURES;
                } else if (InstrumentType.isOptions(inst)) {
                    instrument = InstrumentType.OPTIONS;

                } else
                    instrument = "";
            } catch (Exception e) {
            	log.info("Error :: " + e);
            }
        } else
            instrument = "";
        return instrument;
    }

    public static String getOption(String option, String instrument)

    {
        String opt = "";
        try {
            if (InstrumentType.isOptions(instrument)) {
                opt = option;
            }
        } catch (Exception e) {
        	log.info("Error :: " + e);
        }
        return opt;
    }

    public static String getSearchSymbolDetails(String instrument, String exch, String exp, String strikeprice,
            String option) {
        String searchDetail = "";

        if (InstrumentType.isFutures(instrument)) {
            searchDetail += exp + " " + getSearchInstrument(instrument);
        } else if (InstrumentType.isOptions(instrument)) {
            searchDetail += exp + " " + strikeprice + " " + option;
        } else {
            searchDetail += "";
        }
        return searchDetail;

    }

    public static void deleteEquityFromDB(String mrktSegment) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String query = DBQueryConstants.DELETE_EQUITIES;
//        log.info("Query :: " + query);
        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, mrktSegment);

            int rows = ps.executeUpdate();
            log.info("Deleted Equity : " + mrktSegment + " No of rows deleted :: " + rows);
        } catch (Exception e) {
            log.info("Error :: " + e);
        } finally {
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }
    }

    public static void deleteExpiredContractsFromDB() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String query = DBQueryConstants.DELETE_EXPIRED_CONTRACTS;
//        log.info("Query :: " + query);
        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(query);
            int rows = ps.executeUpdate();
            log.info("Deleted Expired rows :: " + rows);
        } catch (Exception e) {
            log.info("Error :: " + e);
        } finally {
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }

    }
    
    public static void deleteNcdexAndBsecdsScrips() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String query = DBQueryConstants.DELETE_NCDEX_BSECDS_SCRIPS;
        try {
            conn = GCDBPool.getInstance().getConnection();
            ps = conn.prepareStatement(query);
            int rows = ps.executeUpdate();
            log.info("Deleted NCDEX/BSECDS rows :: " + rows);
        } catch (Exception e) {
            log.info("Error :: " + e);
        } finally {
            Helper.closeStatement(ps);
            Helper.closeConnection(conn);
        }

    }
    
    private static String formMappingSymbolUniqDescEq(String instrument, String symb, String series, String exch) {
		return instrument+"_"+symb+"_"+series+"_"+exch;		//STK_RELIANCE_EQ_NSE
	}
    
    private static String formMappingSymbolUniqDescNonEq(String assetClass, String symb, String exch, String formatExp, String strikePrice, String option) throws ParseException {
    	try {
    		SimpleDateFormat sourceFormat = new SimpleDateFormat(DBConstants.EXPIRY_DATE_FORMAT);
            SimpleDateFormat destinationFormat = new SimpleDateFormat(DBConstants.MAPPING_DATE_FORMAT);
            String formattedDate = destinationFormat.format(sourceFormat.parse(formatExp));
            if(exch.equalsIgnoreCase(ExchangeSegment.NSECDS))
    			exch = "CDS";
            if(assetClass.equalsIgnoreCase("OPTFUT") && exch.equals(ExchangeSegment.MCX))
            	assetClass = InstrumentType.OPTCOM;
            if(option.isEmpty())
        		return assetClass+"_"+symb+"_"+exch+"_"+formattedDate;	//OPTSTK_RELIANCE_NFO_2021-04-06
    		else
    			return assetClass+"_"+symb+"_"+exch+"_"+formattedDate+"_"+strikePrice+"_"+option;	//OPTSTK_RELIANCE_NFO_2021-04-06_2160_CE
    	}catch(Exception ex){
    		log.info("Error framing mapping symbol uniq desc :: "+symb+"_"+formatExp+"_"+option+"_"+assetClass);
    	}
    	return StringUtils.EMPTY;
	}
    
    public static String formatStrikePriceForMappingSym(String strikePrice) {
		Double strikePriceDouble = Double.parseDouble(strikePrice);
    	DecimalFormat df ;
    	if(strikePrice.contains(".")) {
    		if(strikePrice.split("\\.")[1].length()==1)
    			df = new DecimalFormat("#.#");
    		else
    			df = new DecimalFormat("#.##");
		}else
			return strikePrice;

    	df.setRoundingMode(RoundingMode.FLOOR);

		return df.format(strikePriceDouble);
	}
}