package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest_v1;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetPayinTransactionsRequest extends GCApiRequest_v1 {

	public GetPayinTransactionsRequest() throws AppConfigNoKeyFoundException {
		super();
		this.respType = RESP_TYPE.JSON;
	}

	public void setAlertSequenceNumber(String alertSequenceNumber) {
		addToReq(GCConstants.ALERT_SEQUENCE_NO, alertSequenceNumber);
	}

	public void setRemitterName(String remitterName) {
		addToReq(GCConstants.REMITTER_NAME, remitterName);
	}

	public void setRemitterAccount(String remitterAccount) {
		addToReq(GCConstants.REMITTER_ACCOUNT, remitterAccount);
	}

	public void setRemitterBank(String remitterBank) {
		addToReq(GCConstants.REMITTER_BANK, remitterBank);
	}
	
	public void setUserReferenceNumber(String userReferenceNumber) {
		addToReq(GCConstants.USER_REFERENCE_NUMBER, userReferenceNumber);
	}
	
	public void setBenefDetails(String benefDetails) {
		addToReq(GCConstants.BENEFICIARY_DETAILS, benefDetails);
	}
	
	public void setAmount(String amount) {
		addToReq(GCConstants.PAYIN_AMOUNT, amount);
	}
	
	public void setMnemonicCode(String mnemonicCode) {
		addToReq(GCConstants.MNEMONIC_CODE, mnemonicCode);
	}
	
	public void setTransactionDate(String transactionDate) {
		addToReq(GCConstants.TRANSACTION_DATE, transactionDate);
	}
	
	public void setDebitCredit(String debitCredit) {
		addToReq(GCConstants.DEBIT_CREDIT, debitCredit);
	}
	
	public void setRemitterIFSC(String remitterIFSC) {
		addToReq(GCConstants.REMITTER_IFSC, remitterIFSC);
	}
	
	public void setChequeNo(String chequeNo) {
		addToReq(GCConstants.CHEQUE_NO, chequeNo);
	}
	
	public void setTransactionDescription(String transactionDescription) {
		addToReq(GCConstants.TRANSACTION_DESCRIPTION, transactionDescription);
	}
	
	public void setAccountnumber(String accountnumber) {
		addToReq(GCConstants.ACCOUNT_NUMBER, accountnumber);
	}
	
	public void setTokenID(String tokenID) {
		addToReq(GCConstants.TOKEN_ID, tokenID);
	}
	
	public void setValueDate(String valueDate) {
		addToReq(GCConstants.VALUE_DATE, valueDate);
	}
	
	public void setTrCode(String trCode) {
		addToReq(GCConstants.TR_CODE, trCode);
	}
	
	public void setRazorApiStatus(String razorApiStatus) {
		addToReq(GCConstants.RAZOR_API_STATUS, razorApiStatus);
	}
	
	public void setFtApiStatus(String ftApiStatus) {
		addToReq(GCConstants.FT_API_STATUS, ftApiStatus);
	}
	
	@Override
	public String toString() {
		return this.reqObj.toString();
	}

}