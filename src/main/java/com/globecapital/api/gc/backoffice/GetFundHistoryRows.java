package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetFundHistoryRows {
	
	@SerializedName("AMT")
	protected String amt;
	
	@SerializedName("TRXNTIMESTMP")
	protected String trxnTimeStamp;
	
	@SerializedName("BKACNO")
	protected String bankAccNo;
	
	@SerializedName("RMK")
	protected String rmk;
	
	@SerializedName("TRXNSTATUS")
	protected String trxnStatus;
	
	@SerializedName("TRXNNO")
	protected String trxnNo;
	
	@SerializedName("TRXNTYPE")
	protected String trxnType;
	
	@SerializedName("TRXNDATE")
	protected String trxnDate;
	
	@SerializedName("TRCODE")
	protected String trCode;
	
	@SerializedName("Segments")
	protected String segments;
	
	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getTransTimeStamp() {
		return trxnTimeStamp;
	}

	public void setTransTimeStamp(String tnxnTimeStamp) {
		this.trxnTimeStamp = tnxnTimeStamp;
	}

	public String getBankAccNo() {
		return bankAccNo;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	public String getRmk() {
		return rmk;
	}

	public void setRmk(String rmk) {
		this.rmk = rmk;
	}

	public String getTrxnStatus() {
		return trxnStatus;
	}

	public void setTrxnStatus(String trxnStatus) {
		this.trxnStatus = trxnStatus;
	}
	
	public String getTrxnNo() {
		return trxnNo;
	}

	public void setTrxnNo(String trxnNo) {
		this.trxnNo = trxnNo;
	}
	
	public String getTrxnDate() {
		return trxnDate;
	}

	public void setTrxnDate(String trxnDate) {
		this.trxnDate = trxnDate;
	}
	
	public String getTrxnType() {
		return trxnType;
	}

	public void setTrxnType(String trxnType) {
		this.trxnType = trxnType;
	}
	
	public String getTrCode() {
		return trCode;
	}

	public void setTrCode(String trCode) {
		this.trCode = trCode;
	}
	

	public String getSegments() {
		return segments;
	}

	public void setSegments(String segments) {
		this.segments = segments;
	}
	
}
