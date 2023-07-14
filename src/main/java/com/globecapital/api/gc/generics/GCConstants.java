package com.globecapital.api.gc.generics;

public class GCConstants {

	public static final String VENDOR_NAME="GC";

	public static final String TOKEN = "token";

	public static final String CLIENT_CODE = "clientCode";

	public static final String CODE = "code";

	public static final String YEAR = "year";

	public static final String SEGMENT = "segment";

	public static final String SYMBOL = "Symbol";

	public static final String TYPE = "type";

	public static final String ACCOUNT_NO = "accountNo";

	public static final String FROM_DATE = "fromDate";

	public static final String TO_DATE = "toDate";

	public static final String ENTRY_TYPE = "entryType";

	public static final String EQUITY = "EQ";

	public static final String COMMODITY = "CO";

	public static final String SID = "sid";

	public static final String USERNAME = "userName";

	public static final String PASSWORD = "password";

	public static final String SCRIP_CODE = "scripName";

	public static final String MAX_SLOT = "maxSlot";

	// Advanced Filter Types

	public static final String BUY = "Buy";

	public static final String SELL = "Sell";

	public static final String ALL = "All";

	public static final String SMALL_CAP = "SmallCap";

	public static final String MID_CAP = "MidCap";

	public static final String LARGE_CAP = "LargeCap";

	public static final String PROFIT = "Profit";

	public static final String LOSS = "Loss";

	public static final String WITH_MARGIN = "WithMargin";

	public static final String WITHOUT_MARGIN = "WithoutMargin";

	public static final String INTRADAY_TAX = "IntradayTax";

	public static final String SHORT_TERM_TAX = "ShortTermTax";

	public static final String LONG_TERM_TAX = "LongTermTax";
	
	public static final String OPTIONS="Options";
	
	public static final String FUTURES="Future";

	// Available Sort options

	public static final String QUANTITY = "Quantity";

	public static final String AMOUNT = "amount";

	public static final String REFERENCE_NO = "referenceNo";

	public static final String DATE = "Date";

	public static final String MARKET_VALUE = "MarketValue";

	public static final String CREDIT_DEBIT = "CreditOrDebit";

	public static final String BALANCE = "Balance";

	public static final String DAY_PL = "DaysP&L";

	public static final String UNREALISED_PL = "UnrealisedP&L";

	public static final String TOTAL_TAX = "TotalTax";

	public static final String CALL_ACTION = "Action";

	public static final String ENTRY_DATE = "EntryDate";

	public static final String CALL_TYPE = "CallType";

	public static final String BUY_SELL = "BuySell";

	public static final String IS_REVISED = "isRevised";

	public static final String EXIT_PRICE = "ExitPrice";

	public static final String TARGET_PRICE = "TargetPrice";

	public static final String RETURN = "Return";

	public static final String ENTRY_PRICE = "EntryPrice";

	public static final String SL_PRICE = "SLPrice";

	public static final String PARTIAL_GAIN_LOSS = "partialRealizedGainLoss";

	public static final String TOTAL_GAIN_LOSS = "totalRealizedGainLoss";

	public static final String START_DATE = "EntryDate";

	public static final String END_DATE = "EndDate";

	public static final String CALL_HISTORY = "callHistory";

	public static final String ACTION_DETAILS = "Act";

	public static final String RESEARCH_SEGMENT = "Segment";

	public static final String ISIN = "ISIN";
	
	public static final String Y = "Y";
	
	public static final String N = "N";
	
	public static final String ALPHABETICALLY="Alphabetically";
	
	public static final String ALPHA="alpha";
	
	public static final String PNL="pnl";
	
	public static final String PROFIT_N_LOSS="Profit & Loss";
	
	public static final String PROFIT_LOSS="ProfitNLoss";

	public static final String MKTVAL="mktval";
	
	public static final String MKT_VAL="Market Value";
	
	public static final String DATE_="date";
	
	public static final String QTY="qty";
	
	public static final String TTL_TX="ttltax";
	
	public static final String TTL_TAX="Total Tax";


	// AMO
	public static final String NSEC = "NSEC";
	public static final String BSEC = "BSEC";
	
	//Combined position
	public static final String EQUITY_FO = "EquityFo";
	public static final String COMMODITY_POSITION = "Commodity";
	public static final String CURRENCY = "Currency";
	public static final String FUTURE = "FUTURE";
	public static final String B = "B";
	public static final String S = "S";

	// API Index constants
	public static final int INDEX_SESSIONID = 1;

	// Login index constants
	public static final int INDEX_LOGIN_USERNAME = 2;
	public static final int INDEX_LOGIN_PASSWORD = 3;

	// Ledger index constants
	public static final int INDEX_LEDGER_CLIENTCODE = 2;
	public static final int INDEX_LEDGER_ENTRYTYPE = 3;
	public static final int INDEX_LEDGER_YEAR = 4;
	public static final int INDEX_LEDGER_SEGMENT = 5;
	public static final int INDEX_LEDGER_FROM_DATE = 6;
	public static final int INDEX_LEDGER_TO_DATE = 7;

	// Transaction index constants
	public static final int INDEX_TRANSACTION_CLIENTCODE = 2;
	public static final int INDEX_TRANSACTION_FROM_DATE = 3;
	public static final int INDEX_TRANSACTION_TO_DATE = 4;
	public static final int INDEX_TRANSACTION_YEAR = 5;

	// Realised PL index constants
	public static final int INDEX_REALISED_CLIENTCODE = 2;
	public static final int INDEX_REALISED_FROM_DATE = 3;
	public static final int INDEX_REALISED_TO_DATE = 4;
	public static final int INDEX_REALISED_TYPE = 5;
	public static final int INDEX_REALISED_YEAR = 6;

	// unrealised PL index constants
	public static final int INDEX_UNREALISED_CLIENTCODE = 2;
	public static final int INDEX_UNREALISED_DATE = 3;
	public static final int INDEX_UNREALISED_YEAR = 4;

	// equity trxn index constants
	public static final int INDEX_EQ_TRXN_CLIENTCODE = 2;
	public static final int INDEX_EQ_TRXN_SCODE = 3;
	public static final int INDEX_EQ_TRXN_MAXSLOT = 4;

	// derivative trxn index constants
	public static final int INDEX_DERIVATIVE_TRXN_CLIENTCODE = 2;
	public static final int INDEX_DERIVATIVE_TRXN_SNAME = 3;
	public static final int INDEX_DERIVATIVE_TRXN_YEAR = 4;

	// other reports index constants
	public static final int INDEX_OTHER_CLIENTCODE = 2;
	public static final int INDEX_OTHER_FROM_DATE = 3;
	public static final int INDEX_OTHER_TO_DATE = 4;
	public static final int INDEX_OTHER_SEGMENT = 5;
	public static final int INDEX_OTHER_YEAR = 6;

	// Tax index constants

	public static final int INDEX_TAX_CLIENTCODE = 2;
	public static final int INDEX_TAX_YEAR = 3;

	// holdings index constants
	public static final int INDEX_HOLDINGS_CLIENTCODE = 2;

	// portfolio index constants
	public static final int INDEX_PORTFOLIO_CLIENTCODE = 2;

	// sauda download index constants
	public static final int INDEX_SAUDA_CLIENTCODE = 2;
	public static final int INDEX_SAUDA_DATE = 3;
	public static final int INDEX_SAUDA_YEAR = 4;

	// Download equity index constants
	public static final int INDEX_DOWNLOAD_CLIENTCODE = 2;
	public static final int INDEX_DOWNLOAD_SEGMENT = 3;
	public static final int INDEX_DOWNLOAD_FROM_DATE = 4;
	public static final int INDEX_DOWNLOAD_TO_DATE = 5;
	public static final int INDEX_DOWNLOAD_YEAR = 6;

	// Download Derivative index constants
	public static final int INDEX_DOWNLOAD_DATE = 3;
	public static final int INDEX_DOWNLOAD_TYPE = 4;

	// contract download index constants
	public static final int INDEX_CONTRACT_DOWNLOAD_CLIENTCODE = 2;
	public static final int INDEX_CONTRACT_DOWNLOAD_DATE = 3;
	public static final int INDEX_CONTRACT_DOWNLOAD_SEGMENT = 4;
	public static final int INDEX_CONTRACT_DOWNLOAD_YEAR = 5;

	// Trxn email index constants

	public static final int INDEX_TRXN_EMAIL_CLIENTCODE = 2;
	public static final int INDEX_TRXN_EMAIL_FROMDATE = 3;
	public static final int INDEX_TRXN_EMAIL_TODATE = 4;
	public static final int INDEX_TRXN_EMAIL_YEAR = 5;

	// Ledger email index constants

	public static final int INDEX_LEDGER_EMAIL_CLIENTCODE = 2;
	public static final int INDEX_LEDGER_EMAIL_ENTRYTYPE = 3;
	public static final int INDEX_LEDGER_EMAIL_YEAR = 4;
	public static final int INDEX_LEDGER_EMAIL_SEGMENT = 5;
	public static final int INDEX_LEDGER_EMAIL_FROMDATE = 6;
	public static final int INDEX_LEDGER_EMAIL_TODATE = 7;

	// contract email index constants

	public static final int INDEX_CONTRACT_EMAIL_CLIENTCODE = 2;
	public static final int INDEX_CONTRACT_EMAIL_DATE = 3;
	public static final int INDEX_CONTRACT_EMAIL_SEGMENT = 4;
	public static final int INDEX_CONTRACT_EMAIL_YEAR = 5;

	// sauda email index constants

	public static final int INDEX_SAUDA_EMAIL_CLIENTCODE = 2;
	public static final int INDEX_SAUDA_EMAIL_DATE = 3;
	public static final int INDEX_SAUDA_EMAIL_YEAR = 4;

	// holding email index constants

	public static final int INDEX_HOLDING_EMAIL_CLIENTCODE = 2;

	// Banklist index index constants
	public static final int INDEX_BANKLIST_CLIENTCODE = 2;
	public static final int INDEX_BANKLIST_SEGMENT = 3;

	// Bank trxn index index constants
	public static final int INDEX_BANK_CLIENTCODE = 2;
	public static final int INDEX_BANK_FROMDATE = 3;
	public static final int INDEX_BANK_TODATE = 4;

	// Bank withdraw trxn index constants
	public static final int INDEX_BANK_WITHDRAW_CLIENTCODE = 2;
	public static final int INDEX_BANK_WITHDRAW_SEGMENT = 3;
	public static final int INDEX_BANK_WITHDRAW_AMOUNT = 4;
	public static final int INDEX_BANK_WITHDRAW_REFNO = 5;
	public static final int INDEX_BANK_WITHDRAW_ACCOUNTNO = 6;
	
	// AMO Details
	public static final int INDEX_AMO_DATE = 2;
	
	// Cancel Withdraw request index
	public static final int INDEX_CANCEL_WITHDRAW_REFNO = 3;
	
	// Get Resolved Discrepancy 
	public static final int INDEX_RESOLVED_DISCREPANCY_CLIENTCODE = 2;
	public static final int INDEX_RESOLVED_DISCREPANCY_SHCODE = 3;
	public static final int INDEX_RESOLVED_DISCREPANCY_BUYSELL = 4;
	public static final int INDEX_RESOLVED_DISCREPANCY_QTY = 5;
	public static final int INDEX_RESOLVED_DISCREPANCY_RATE = 6;
	public static final int INDEX_RESOLVED_DISCREPANCY_REMARKS = 7;
	public static final int INDEX_RESOLVED_DISCREPANCY_TRXNDATE = 8;
	public static final int INDEX_RESOLVED_DISCREPANCY_SHNAME = 9;
	public static final int INDEX_RESOLVED_DISCREPANCY_TRXNTYPE = 10;
	
	//Get Resolved view Discrepancy
	public static final int INDEX_RESOLVED_VIEW_DISCREPANCY_CLIENTCODE = 2;
	public static final int INDEX_RESOLVED_VIEW_DISCREPANCY_SHCODE = 3;
	
	//Get Discrepancy Modify
	public static final int INDEX_DISCREPANCY_MODIFY_CLIENTCODE = 2;
	public static final int INDEX_DISCREPANCY_MODIFY_REFNO = 3;
	public static final int INDEX_DISCREPANCY_MODIFY_SHCODE = 4;
	public static final int INDEX_DISCREPANCY_MODIFY_BUYSELL = 5;
	public static final int INDEX_DISCREPANCY_MODIFY_QTY = 6;
	public static final int INDEX_DISCREPANCY_MODIFY_RATE = 7;
	public static final int INDEX_DISCREPANCY_MODIFY_REMARKS = 8;
	public static final int INDEX_DISCREPANCY_MODIFY_TRXNDATE = 9;
	public static final int INDEX_DISCREPANCY_MODIFY_SHNAME = 10;
	public static final int INDEX_DISCREPANCY_MODIFY_TRXNTYPE = 11;
	
	//Get Fund History 
	
	public static final int INDEX_FUND_HISTORY_TR_CODE = 2;

	public static final int INDEX_FUND_HISTORY_FROM_DATE = 3;

	public static final int INDEX_FUND_HISTORY_TO_DATE = 4;
	
	public static final int INDEX_FUND_HISTORY_FUTURE_USE = 5;


	//Payin transaction details

	public static final String ALERT_SEQUENCE_NO = "AlertSequenceNo";
	public static final String REMITTER_NAME = "RemitterName";
	public static final String REMITTER_ACCOUNT = "RemitterAccount";
	public static final String REMITTER_BANK = "RemitterBank";
	public static final String USER_REFERENCE_NUMBER = "UserReferenceNumber";
	public static final String BENEFICIARY_DETAILS = "BenefDetails2";
	public static final String PAYIN_AMOUNT = "Amount";
	public static final String MNEMONIC_CODE = "MnemonicCode";
	public static final String TRANSACTION_DATE = "TransactionDate";
	public static final String DEBIT_CREDIT = "DebitCredit";
	public static final String REMITTER_IFSC = "RemitterIFSC";
	public static final String CHEQUE_NO = "ChequeNo";
	public static final String TRANSACTION_DESCRIPTION = "TransactionDescription";
	public static final String ACCOUNT_NUMBER = "Accountnumber";
	public static final String TOKEN_ID = "TOKENID";
	public static final String VALUE_DATE = "VALUEDATE";
	public static final String TR_CODE = "TRCODE";
	public static final String RAZOR_API_STATUS = "RAZORAPISTATUS";
	public static final String FT_API_STATUS = "FTAPISTATUS";

	
}
