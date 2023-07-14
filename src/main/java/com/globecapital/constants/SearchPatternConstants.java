package com.globecapital.constants;

public class SearchPatternConstants {

	public static final String SYMBOL = "[A-Z&*]+";
	
	public static final String SYMBOL_COMPANY = "[A-Z a-z&*]+";
	
	public static final String SYMBOL_COMPANY_1 = "[A-Za-z]{1}";
	
	public static final String SYMBOL_EXCHANGE = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL ="[0-9A-Z&*]+";
	
	public static final String DAY ="[0-9]{2}";
	
	public static final String NUMBERS ="[0-9]+(\\.)?([0-9]+)?";

	public static final String SYMBOL_DAY = "[A-Z&*]+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_MONTH = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|MA|AP|JU|AU|SE|OC|NO|DE)";
	
	public static final String MONTHS = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|MA|AP|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_DATE_MONTH_OPTION = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String SYMBOL_DATE_MONTH = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_DATE_MONTH_OPTION = "[0-9A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_DATE_MONTH = "[0-9A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_OPTION_DATE_MONTH = "[A-Z&*]+(CE|PE)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_OPTION_DATE_MONTH = "[0-9A-Z&*]+(CE|PE)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_INSTRUMENT = "[A-Z&*]+(FUT|OPT|FU|OP)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT = "[0-9A-Z]+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_INSTRUMENT_SYMBOLS = "[A-Z]+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_SYMBOLS = "[0-9A-Z&*]+(FUT|OPT|FU|OP)";

	public static final String SYMBOL_DATE_MONTH_INSTRUMENT = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_MONTH_INSTRUMENT = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_DATE_MONTH_INSTRUMENT_SYMBOLS = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_MONTH_INSTRUMENT_SYMBOLS = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_DATE_MONTH_INSTRUMENT_SYMBOLS = "[0-9A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_INSTRUMENT_DATE_MONTH = "[A-Z&*]+(FUT|OPT)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_DATE_MONTH = "[0-9A-Z&*]+(FUT|OPT)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_INSTRUMENT_OPTION = "[A-Z&*]+(FUT|OPT)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_OPTION = "[0-9A-Z&*]+(FUT|OPT)+(CE|PE|C|P)";
	
	public static final String SYMBOL_OPTION_INSTRUMENT = "[A-Z&*]+(CE|PE)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_OPTION_INSTRUMENT = "[0-9A-Z&*]+(CE|PE)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_OPTION = "[A-Z&*]+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_OPTION = "[0-9A-Z&*]+(CE|PE|C|P)";

	public static final String SYMBOL_STRIKE_PRICE = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_STRIKE_PRICE = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?$";

	public static final String SYMBOL_DATE_MONTH_STRIKE_PRICE = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_DATE_MONTH_STRIKE_PRICE = "[0-9A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";

	public static final String SYMBOL_STRIKE_PRICE_DATE_MONTH = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_STRIKE_PRICE_MONTH_OPTION = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_STRIKE_PRICE_MONTH_OPTION = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_STRIKE_PRICE_DATE_MONTH = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_STRIKE_PRICE_OPTION = "[A-Z&*]+[0-9](\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_STRIKE_PRICE_OPTION = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?(CE|PE|C|P)";

	public static final String SYMBOL_OPTION_STRIKE_PRICE = "[A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_OPTION_STRIKE_PRICE = "[0-9A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";

	public static final String EXCHANGE_FORMAT = "(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_MONTH_OPTION = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_MONTH_OPTION = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String SYMBOL_OPTION_MONTH = "[A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_OPTION_MONTH = "[0-9A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_MONTH_INSTRUMENT = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String SYMBOL_INSTRUMENT_MONTH = "[A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_MONTH = "[0-9A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_MONTH_STRIKE_PRICE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_MONTH_STRIKE_PRICE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_STRIKE_PRICE_MONTH = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_STRIKE_PRICE_DATE = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_STRIKE_PRICE_DATE = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_STRIKE_PRICE_MONTH = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_MONTH_INSTRUMENT_SYMBOLS = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";

	//3 word new patterns added
	
	public static final String SYMBOL_EXCHANGE_INSTRUMENT = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_INSTRUMENT = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String SYMBOL_EXCHANGE_DAY = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_DAY = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";
	
	public static final String SYMBOL_EXCHANGE_MONTH = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_MONTH = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_EXCHANGE_OPTION = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_OPTION = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";
	
	public static final String SYMBOL_EXCHANGE_STRIKEPRICE = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_MONTH_DAY = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_MONTH_DAY = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";
	
	public static final String SYMBOL_MONTH_EXCHANGE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_MONTH_EXCHANGE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_OPTION_DAY = "[A-Z&*]+(CE|PE)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_OPTION_DAY = "[0-9A-Z&*]+(CE|PE)+[0-9]{2}";
	
	public static final String SYMBOL_OPTION_EXCHANGE = "[A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_OPTION_EXCHANGE = "[0-9A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_INSTRUMENT_DAY = "[A-Z&*]+(FUT|OPT)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_DAY = "[0-9A-Z&*]+(FUT|OPT)+[0-9]{2}";
	
	public static final String SYMBOL_INSTRUMENT_EXCHANGE = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_EXCHANGE = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_INSTRUMENT_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE = "[0-9A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_DAY_INSTRUMENT = "[A-Z&*]+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_DAY_INSTRUMENT = "[0-9A-Z&*]+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";
	
	public static final String SYMBOL_DAY_MONTH = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_DAY_MONTH = "[0-9A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_DAY_EXCHANGE = "[A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_DAY_EXCHANGE = "[0-9A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_DAY_OPTION = "[A-Z&*]+[0-9]{2}+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_DAY_OPTION = "[0-9A-Z&*]+[0-9]{2}+(CE|PE|C|P)";
	
	public static final String SYMBOL_DAY_STRIKEPRICE = "[A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_DAY_STRIKEPRICE = "[0-9A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_STRIKEPRICE_OPTION = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_OPTION = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String SYMBOL_STRIKEPRICE_DAY = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_DAY = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";
	
	public static final String SYMBOL_STRIKEPRICE_EXCHANGE = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_STRIKEPRICE_INSTRUMENT = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";
	
	//4 word search new patterns added
	
	public static final String SYMBOL_INSTRUMENT_DATE_EXCHANGE = "[A-Z&*]+(FUT|OPT)+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_DATE_EXCHANGE = "[A-Z&*]+(FUT|OPT)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_INSTRUMENT_DATE_OPTION = "[A-Z&*]+(FUT|OPT)+[0-9]{2}+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_DATE_OPTION = "[A-Z&*]+(FUT|OPT)+[0-9]{2}+(CE|PE|C|P)";
	
	public static final String SYMBOL_INSTRUMENT_DATE_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_DATE_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_INSTRUMENT_MONTH_DATE = "[A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_MONTH_DATE = "[0-9A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";
	
	public static final String SYMBOL_INSTRUMENT_MONTH_EXCHANGE = "[A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_MONTH_EXCHANGE = "[0-9A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_INSTRUMENT_MONTH_OPTION = "[A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_MONTH_OPTION = "[0-9A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String SYMBOL_INSTRUMENT_MONTH_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_MONTH_STRIKEPRICE = "[0-9A-Z&*]+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_INSTRUMENT_EXCHANGE_DATE = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_DATE = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";
	
	public static final String SYMBOL_INSTRUMENT_EXCHANGE_MONTH = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_MONTH = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_INSTRUMENT_EXCHANGE_OPTION = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_OPTION = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";
	
	public static final String SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_EXCHANGE_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_INSTRUMENT_OPTION_DAY = "[A-Z&*]+(FUT|OPT)+(CE|PE)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_OPTION_DAY = "[A-Z&*]+(FUT|OPT)+(CE|PE)+[0-9]{2}";
	
	public static final String SYMBOL_INSTRUMENT_OPTION_MONTH = "[A-Z&*]+(FUT|OPT)+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_OPTION_MONTH = "[A-Z&*]+(FUT|OPT)+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_INSTRUMENT_OPTION_EXCHANGE = "[A-Z&*]+(FUT|OPT)+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_OPTION_EXCHANGE = "[A-Z&*]+(FUT|OPT)+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_OPTION_STRIKEPRICE = "[A-Z&*]+(FUT|OPT)+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";

	public static final String SYMBOL_INSTRUMENT_STRIKEPRICE_DAY = "[A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_DAY = "[0-9A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";
	
	public static final String SYMBOL_INSTRUMENT_STRIKEPRICE_MONTH = "[A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_MONTH = "[0-9A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_INSTRUMENT_STRIKEPRICE_EXCHANGE = "[A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_EXCHANGE = "[0-9A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_INSTRUMENT_STRIKEPRICE_OPTION = "[A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_INSTRUMENT_STRIKEPRICE_OPTION = "[0-9A-Z&*]+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String SYMBOL_DAY_INSTRUMENT_MONTH = "[A-Z&*]+[0-9]{2}+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_DAY_INSTRUMENT_MONTH = "[0-9A-Z&*]+[0-9]{2}+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String SYMBOL_DAY_INSTRUMENT_EXCHANGE = "[A-Z&*]+[0-9]{2}+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_DAY_INSTRUMENT_EXCHANGE = "[0-9A-Z&*]+[0-9]{2}+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_DAY_INSTRUMENT_OPTION = "[A-Z&*]+[0-9]{2}+(FUT|OPT)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_DAY_INSTRUMENT_OPTION = "[0-9A-Z&*]+[0-9]{2}+(FUT|OPT)+(CE|PE|C|P)";

	public static final String SYMBOL_DAY_INSTRUMENT_STRIKEPRICE = "[A-Z&*]+[0-9]{2}+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_DAY_INSTRUMENT_STRIKEPRICE = "[0-9A-Z&*]+[0-9]{2}+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";

	public static final String SYMBOL_DAY_MONTH_EXCHANGE = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_DAY_MONTH_EXCHANGE = "[0-9A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_DAY_EXCHANGE_INSTRUMENT = "[A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_DAY_EXCHANGE_INSTRUMENT = "[0-9A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_DAY_EXCHANGE_MONTH = "[A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_DAY_EXCHANGE_MONTH = "[0-9A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_DAY_EXCHANGE_OPTION = "[A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_DAY_EXCHANGE_OPTION = "[0-9A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";

	public static final String SYMBOL_DAY_EXCHANGE_STRIKEPRICE = "[A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_DAY_EXCHANGE_STRIKEPRICE = "[0-9A-Z&*]+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_DAY_OPTION_INSTRUMENT = "[A-Z&*]+[0-9]{2}+(CE|PE)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_DAY_OPTION_INSTRUMENT = "[0-9A-Z&*]+[0-9]{2}+(CE|PE)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_DAY_OPTION_MONTH = "[A-Z&*]+[0-9]{2}+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_DAY_OPTION_MONTH = "[0-9A-Z&*]+[0-9]{2}+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_DAY_OPTION_EXCHANGE = "[A-Z&*]+[0-9]{2}+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_DAY_OPTION_EXCHANGE = "[0-9A-Z&*]+[0-9]{2}+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_DAY_OPTION_STRIKEPRICE = "[A-Z&*]+[0-9]{2}+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_DAY_OPTION_STRIKEPRICE = "[0-9A-Z&*]+[0-9]{2}+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_DAY_STRIKEPRICE_INSTRUMENT = "[A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_DAY_STRIKEPRICE_INSTRUMENT = "[0-9A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_DAY_STRIKEPRICE_MONTH = "[A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_DAY_STRIKEPRICE_MONTH = "[0-9A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_DAY_STRIKEPRICE_EXCHANGE = "[A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_DAY_STRIKEPRICE_EXCHANGE = "[0-9A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_DAY_STRIKEPRICE_OPTION = "[A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_DAY_STRIKEPRICE_OPTION = "[0-9A-Z&*]+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String SYMBOL_DATE_MONTH_INSTRUMENT_DAY = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+[0-9]{2}";
	
	public static final String SYMBOL_MONTH_INSTRUMENT_DAY = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_MONTH_INSTRUMENT_DAY = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+[0-9]{2}";

	public static final String SYMBOL_DATE_MONTH_INSTRUMENT_EXCHANGE = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_MONTH_INSTRUMENT_EXCHANGE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_MONTH_INSTRUMENT_EXCHANGE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_DATE_MONTH_INSTRUMENT_OPTION = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+(CE|PE|C|P)";
	
	public static final String SYMBOL_MONTH_INSTRUMENT_OPTION = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_MONTH_INSTRUMENT_OPTION = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+(CE|PE|C|P)";

	public static final String SYMBOL_DATE_MONTH_INSTRUMENT_STRIKEPRICE = "[A-Z&*]+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_MONTH_INSTRUMENT_STRIKEPRICE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_MONTH_INSTRUMENT_STRIKEPRICE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_MONTH_DAY_INSTRUMENT = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_MONTH_DAY_INSTRUMENT = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_MONTH_DAY_MONTH = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_MONTH_DAY_MONTH = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_MONTH_DAY_EXCHANGE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_MONTH_DAY_EXCHANGE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_MONTH_DAY_STRIKEPRICE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_MONTH_DAY_STRIKEPRICE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_MONTH_EXCHANGE_INSTRUMENT = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_MONTH_EXCHANGE_INSTRUMENT = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_MONTH_EXCHANGE_DAY = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_MONTH_EXCHANGE_DAY = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";

	public static final String SYMBOL_MONTH_EXCHANGE_OPTION = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_MONTH_EXCHANGE_OPTION = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";

	public static final String SYMBOL_MONTH_EXCHANGE_STRIKEPRICE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_MONTH_EXCHANGE_STRIKEPRICE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_MONTH_OPTION_INSTRUMENT = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_MONTH_OPTION_INSTRUMENT = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_MONTH_OPTION_DAY = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_MONTH_OPTION_DAY = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+[0-9]{2}";
	
public static final String SYMBOL_MONTH_DAY_OPTION = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_MONTH_DAY_OPTION = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}+(CE|PE|C|P)";

	public static final String SYMBOL_MONTH_OPTION_EXCHANGE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_MONTH_OPTION_EXCHANGE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_MONTH_OPTION_STRIKEPRICE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_MONTH_OPTION_STRIKEPRICE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_MONTH_STRIKE_PRICE_INSTRUMENT = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_MONTH_STRIKE_PRICE_INSTRUMENT = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_MONTH_STRIKE_PRICE_DAY = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_MONTH_STRIKE_PRICE_DAY = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";

	public static final String SYMBOL_MONTH_STRIKE_PRICE_EXCHANGE = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_MONTH_STRIKE_PRICE_EXCHANGE = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_MONTH_STRIKE_PRICE_OPTION = "[A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_MONTH_STRIKE_PRICE_OPTION = "[0-9A-Z&*]+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String SYMBOL_EXCHANGE_INSTRUMENT_DAY = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_DAY = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+[0-9]{2}";

	public static final String SYMBOL_EXCHANGE_INSTRUMENT_MONTH = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_MONTH = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_EXCHANGE_INSTRUMENT_OPTION = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_OPTION = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+(CE|PE|C|P)";

	public static final String SYMBOL_EXCHANGE_INSTRUMENT_STRIKEPRICE = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_INSTRUMENT_STRIKEPRICE = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_EXCHANGE_DAY_INSTRUMENT = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_DAY_INSTRUMENT = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_EXCHANGE_DAY_MONTH = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_DAY_MONTH = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_EXCHANGE_DAY_OPTION = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_DAY_OPTION = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+(CE|PE|C|P)";

	public static final String SYMBOL_EXCHANGE_DAY_STRIKEPRICE = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_DAY_STRIKEPRICE = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_EXCHANGE_MONTH_INSTRUMENT = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_MONTH_INSTRUMENT = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_EXCHANGE_MONTH_DAY = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_MONTH_DAY = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";

	public static final String SYMBOL_EXCHANGE_MONTH_OPTION = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_MONTH_OPTION = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(CE|PE|C|P)";

	public static final String SYMBOL_EXCHANGE_MONTH_STRIKEPRICE = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_MONTH_STRIKEPRICE = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_EXCHANGE_OPTION_DAY = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_OPTION_DAY = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)+[0-9]{2}";

	public static final String SYMBOL_EXCHANGE_OPTION_MONTH = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_OPTION_MONTH = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_EXCHANGE_OPTION_INSTRUMENT = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_OPTION_INSTRUMENT = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_EXCHANGE_OPTION_STRIKEPRICE = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_OPTION_STRIKEPRICE = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_EXCHANGE_STRIKEPRICE_DAY = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_DAY = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";

	public static final String SYMBOL_EXCHANGE_STRIKEPRICE_MONTH = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_MONTH = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_EXCHANGE_STRIKEPRICE_INSTRUMENT = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_INSTRUMENT = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_EXCHANGE_STRIKEPRICE_OPTION = "[A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_EXCHANGE_STRIKEPRICE_OPTION = "[0-9A-Z&*]+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?+(CE|PE|C|P)";
	
	public static final String SYMBOL_OPTION_INSTRUMENT_DAY = "[A-Z&*]+(CE|PE)+(FUT|OPT)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_OPTION_INSTRUMENT_DAY = "[0-9A-Z&*]+(CE|PE)+(FUT|OPT)+[0-9]{2}";

	public static final String SYMBOL_OPTION_INSTRUMENT_MONTH = "[A-Z&*]+(CE|PE)+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_OPTION_INSTRUMENT_MONTH = "[0-9A-Z&*]+(CE|PE)+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_OPTION_INSTRUMENT_STRIKEPRICE = "[A-Z&*]+(CE|PE)+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_OPTION_INSTRUMENT_STRIKEPRICE = "[0-9A-Z&*]+(CE|PE)+(FUT|OPT)+[0-9]+(\\.)?([0-9]+)?$";

	public static final String SYMBOL_OPTION_INSTRUMENT_EXCHANGE = "[A-Z&*]+(CE|PE)+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_OPTION_INSTRUMENT_EXCHANGE = "[0-9A-Z&*]+(CE|PE)+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_OPTION_DAY_MONTH = "[A-Z&*]+(CE|PE)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_OPTION_DAY_MONTH = "[0-9A-Z&*]+(CE|PE)+[0-9]{2}+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_OPTION_DAY_EXCHANGE = "[A-Z&*]+(CE|PE)+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_OPTION_DAY_EXCHANGE = "[0-9A-Z&*]+(CE|PE)+[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_OPTION_DAY_INSTRUMENT = "[A-Z&*]+(CE|PE)+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_OPTION_DAY_INSTRUMENT = "[0-9A-Z&*]+(CE|PE)+[0-9]{2}+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_OPTION_DAY_STRIKEPRICE = "[A-Z&*]+(CE|PE)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_OPTION_DAY_STRIKEPRICE = "[0-9A-Z&*]+(CE|PE)+[0-9]{2}+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_OPTION_MONTH_DAY = "[A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_OPTION_MONTH_DAY = "[0-9A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";

	public static final String SYMBOL_OPTION_MONTH_EXCHANGE = "[A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_OPTION_MONTH_EXCHANGE = "[0-9A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_OPTION_MONTH_INSTRUMENT = "[A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_OPTION_MONTH_INSTRUMENT = "[0-9A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_OPTION_MONTH_STRIKEPRICE = "[A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_OPTION_MONTH_STRIKEPRICE = "[0-9A-Z&*]+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_OPTION_EXCHANGE_DAY = "[A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_OPTION_EXCHANGE_DAY = "[0-9A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";

	public static final String SYMBOL_OPTION_EXCHANGE_MONTH = "[A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_OPTION_EXCHANGE_MONTH = "[0-9A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_OPTION_EXCHANGE_INSTRUMENT = "[A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_OPTION_EXCHANGE_INSTRUMENT = "[0-9A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_OPTION_EXCHANGE_STRIKEPRICE = "[A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String NUMBER_SYMBOL_OPTION_EXCHANGE_STRIKEPRICE = "[0-9A-Z&*]+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]+(\\.)?([0-9]+)?$";
	
	public static final String SYMBOL_OPTION_STRIKE_PRICE_DAY = "[A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_OPTION_STRIKE_PRICE_DAY = "[0-9A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?[0-9]{2}";

	public static final String SYMBOL_OPTION_STRIKE_PRICE_MONTH = "[A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_OPTION_STRIKE_PRICE_MONTH = "[0-9A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_OPTION_STRIKE_PRICE_INSTRUMENT = "[A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_OPTION_STRIKE_PRICE_INSTRUMENT = "[0-9A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_OPTION_STRIKE_PRICE_EXCHANGE = "[A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_OPTION_STRIKE_PRICE_EXCHANGE = "[0-9A-Z&*]+(CE|PE)+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_STRIKEPRICE_INSTRUMENT_DAY = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_DAY = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+[0-9]{2}";

	public static final String SYMBOL_STRIKEPRICE_INSTRUMENT_MONTH = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_MONTH = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_STRIKEPRICE_INSTRUMENT_OPTION = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_OPTION = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+(CE|PE|C|P)";

	public static final String SYMBOL_STRIKEPRICE_INSTRUMENT_EXCHANGE = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_INSTRUMENT_EXCHANGE = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(FUT|OPT)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_STRIKEPRICE_DAY_OPTION = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_DAY_OPTION = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}+(CE|PE|C|P)";

	public static final String SYMBOL_STRIKEPRICE_DAY_EXCHANGE = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_DAY_EXCHANGE = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_STRIKEPRICE_DAY_INSTRUMENT = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_DAY_INSTRUMENT = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?[0-9]{2}+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_STRIKE_PRICE_MONTH_DAY = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+[0-9]{2}";
	
	public static final String SYMBOL_STRIKE_PRICE_MONTH_EXCHANGE = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_STRIKE_PRICE_MONTH_EXCHANGE = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String SYMBOL_STRIKE_PRICE_MONTH_INSTRUMENT = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String SYMBOL_STRIKEPRICE_EXCHANGE_DAY = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_DAY = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+[0-9]{2}";

	public static final String SYMBOL_STRIKEPRICE_EXCHANGE_MONTH = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_MONTH = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_STRIKEPRICE_EXCHANGE_OPTION = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_OPTION = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+(CE|PE|C|P)";

	public static final String SYMBOL_STRIKEPRICE_EXCHANGE_INSTRUMENT = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_EXCHANGE_INSTRUMENT = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(NSECDS|NSE|BSE|NFO|MCX)+(FUT|OPT|F|FU|O|OP)";

	public static final String SYMBOL_STRIKEPRICE_OPTION_DAY = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+[0-9]{2}";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_OPTION_DAY = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+[0-9]{2}";

	public static final String SYMBOL_STRIKEPRICE_OPTION_MONTH = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_OPTION_MONTH = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|JA|FE|AP|MA|JU|AU|SE|OC|NO|DE)";

	public static final String SYMBOL_STRIKEPRICE_OPTION_EXCHANGE = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_OPTION_EXCHANGE = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+(NSECDS|NSE|BSE|NFO|MCX|NSEC|NSECD|NS|BS|MC|NF)";

	public static final String SYMBOL_STRIKEPRICE_OPTION_INSTRUMENT = "[A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+(FUT|OPT|F|FU|O|OP)";
	
	public static final String NUMBER_SYMBOL_STRIKEPRICE_OPTION_INSTRUMENT = "[0-9A-Z&*]+[0-9]+(\\.)?([0-9]+)?+(CE|PE)+(FUT|OPT|F|FU|O|OP)";
}

