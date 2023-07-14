package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetBankListRows {

	@SerializedName("BANKACNO")
	protected String bankNo;
	
	@SerializedName("IFSC")
	protected String ifsc;
	
	@SerializedName("BANKNAME")
	protected String bankName;
	
	@SerializedName("BANKCODE")
	protected String bankCode;

	public String getBankNo() {
		return bankNo;
	}
	
	public String getIfsc() {
		return ifsc;
	}

	public String getBankName() {
		return bankName;
	}

	public String getBankCode() {
		return bankCode;
	}
	
}
