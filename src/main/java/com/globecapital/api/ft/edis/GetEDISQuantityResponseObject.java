package com.globecapital.api.ft.edis;

import java.util.List;
import org.json.JSONObject;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("rawtypes")
public class GetEDISQuantityResponseObject extends JSONObject {

	@SerializedName("Table")
	protected List<GetEDISScripDetailsObject> scripDetailList;
	
	@SerializedName("Table1")
	protected List validationList;

	public List<GetEDISScripDetailsObject> getScripDetailList() {
		return scripDetailList;
	}

	public void setScripDetailList(List<GetEDISScripDetailsObject> scripDetailList) {
		this.scripDetailList = scripDetailList;
	}

	public List getValidationList() {
		return validationList;
	}

	public void setValidationList(List validationList) {
		this.validationList = validationList;
	}
	
}
