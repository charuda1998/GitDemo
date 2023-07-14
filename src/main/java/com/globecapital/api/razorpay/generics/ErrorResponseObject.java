package com.globecapital.api.razorpay.generics;

import org.json.JSONObject;

import com.google.gson.annotations.SerializedName;

public class ErrorResponseObject extends JSONObject {

	@SerializedName("code")
	protected String code;

	@SerializedName("description")
	protected String description;

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

}
