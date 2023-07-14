package com.globecapital.api.ft.edis;

import org.json.JSONObject;
import com.google.gson.annotations.SerializedName;

public class GetEDISConfigResponseObject extends JSONObject {

	@SerializedName("URL")
	protected String url;
	
	@SerializedName("BeneficiaryId")
	protected String beneficiaryId;

	@SerializedName("Depository")
	protected String depository;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBeneficiaryId() {
		return beneficiaryId;
	}

	public void setBeneficiaryId(String beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}

	public String getDepository() {
		return depository;
	}

	public void setDepository(String depository) {
		this.depository = depository;
	}
}
