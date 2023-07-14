package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetLedgerRows {

	@SerializedName("Balance")
	protected String Balance;

	@SerializedName("Credit")
	protected String Credit;

	@SerializedName("Debit")
	protected String Debit;

	@SerializedName("LedDate")
	protected String LedDate;

	@SerializedName("Narration")
	protected String Narration;

	public String getBalance() {
		return Balance;
	}

	public void setBalance(String balance) {
		Balance = balance;
	}

	public String getCredit() {
		return Credit;
	}

	public void setCredit(String credit) {
		Credit = credit;
	}

	public String getDebit() {
		return Debit;
	}

	public void setDebit(String debit) {
		Debit = debit;
	}

	public String getLedDate() {
		return LedDate;
	}

	public void setLedDate(String ledDate) {
		LedDate = ledDate;
	}

	public String getNarration() {
		return Narration;
	}

	public void setNarration(String narration) {
		Narration = narration;
	}

}
