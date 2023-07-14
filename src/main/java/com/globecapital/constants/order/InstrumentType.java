package com.globecapital.constants.order;

public class InstrumentType {

    // NSE Future & Options 
    public static final String FUTSTK = "FUTSTK";
    public static final String OPTSTK = "OPTSTK";
    public static final String FUTIDX = "FUTIDX";
    public static final String OPTIDX = "OPTIDX";

    //MCX & NCDEX Future and Options - Commodity
    public static final String FUTCOM ="FUTCOM";
    public static final String OPTCOM = "OPTCOM";

    //NSE & BSE CDS Future and Options - Currency 
    public static final String FUTCUR = "FUTCUR";
    public static final String OPTCUR = "OPTCUR";

    public static final String FUTURES = "FUT";
    public static final String OPTIONS = "OPT";
    public static final String STOCK  = "STK";
    public static final String INDEX = "IDX";
    public static final String COMMODITY = "COM";
    public static final String CURRENCY = "CUR";
    
    //Option
    public static final String CALL_OPTION = "CE";
    public static final String PUT_OPTION = "PE";
    
    //For Display
    public static final String D_INDEX = "Index";
    public static final String D_STOCK = "Stock";
    public static final String D_FUTURES = "Futures";
    public static final String D_OPTIONS = "Options";
    public static final String STOCK_FUTURES = "Stock Futures";
    public static final String STOCK_OPTIONS = "Stock Options";
    public static final String INDEX_FUTURES = "Index Futures";
    public static final String INDEX_OPTIONS = "Index Options";
    
    public static final String DERIVATIVE = "Derivative";
    public static final String CASH = "Cash";
    
    
    public static final String getInstrumentForDisplay(String instrument) {
    	if(isFutures(instrument))
    		return  D_FUTURES;
    	return D_OPTIONS;
    }
    
    public static final String getDetailInstrumentForDisplay(String instrument) {
    	if(instrument.equals(FUTSTK))
    		return STOCK_FUTURES;
    	else if(instrument.equals(FUTIDX))
    		return INDEX_FUTURES;
    	else if(instrument.equals(OPTSTK))
    		return STOCK_OPTIONS;
    	return INDEX_OPTIONS;
    }

    public static final boolean isFutures ( String instrument) {
    	if (instrument.startsWith(FUTURES))
    		return true;
    	return false;
    }
    
    public static final boolean isOptions (String instrument) {
    	if(instrument.startsWith(OPTIONS))
    		return true;
    	return false;
    }
    
    public static final boolean isStock(String instrument) {
    	if(instrument.endsWith(STOCK))
    		return true;
    	return false;
    }

    public static final boolean isIndex(String instrument) {
    	if(instrument.endsWith(INDEX))
    		return true;
    	return false;
    }
    
    public static final boolean isCommodity(String instrument) {
    	if(instrument.endsWith(COMMODITY))
    		return true;
    	return false;
    }
    
    public static final boolean isCurrency(String  instrument) {
    	if(instrument.endsWith(CURRENCY))
    		return true;
    	return false;
    }
    
    
}