package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetResearchResponse extends GCApiResponse {
	
	@SerializedName("Segment")
	protected String segment;

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}
	
}