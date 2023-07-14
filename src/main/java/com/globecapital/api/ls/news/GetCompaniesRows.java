package com.globecapital.api.ls.news;

import com.google.gson.annotations.SerializedName;

public class GetCompaniesRows {
	
	@SerializedName("nameOfCompany")
	protected String nameOfCompany;
	
	@SerializedName("nse")
	protected String nse;
	
	@SerializedName("bse")
	protected String bse;
	
	@SerializedName("_id")
	protected String _id;
	
	public String getNameOfCompany() {
		return nameOfCompany;
	}

	public void setNameOfCompany(String nameOfCompany) {
		this.nameOfCompany = nameOfCompany;
	}
	
	public String getNSE() {
		return nse;
	}

	public void setNSE(String nse) {
		this.nse = nse;
	}
	
	public String getBSE() {
		return bse;
	}

	public void setBSE(String bse) {
		this.bse = bse;
	}
	
	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}
}
