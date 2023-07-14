package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetHoldingsObject {

	@SerializedName("lstHeaderId")
	protected String lstHeaderId;

	@SerializedName("lstHeaderNm")
	protected String lstHeaderNm;

	@SerializedName("dtRows")
	protected String dtRows;

	@SerializedName("objJSONHeader")
	protected String objJSONHeader;

	@SerializedName("objJSONRows")
	protected List<GetHoldingsRows> objJSONRows;

	public String getLstHeaderId() {
		return lstHeaderId;
	}

	public void setLstHeaderId(String lstHeaderId) {
		this.lstHeaderId = lstHeaderId;
	}

	public String getLstHeaderNm() {
		return lstHeaderNm;
	}

	public void setLstHeaderNm(String lstHeaderNm) {
		this.lstHeaderNm = lstHeaderNm;
	}

	public String getDtRows() {
		return dtRows;
	}

	public void setDtRows(String dtRows) {
		this.dtRows = dtRows;
	}

	public String getObjJSONHeader() {
		return objJSONHeader;
	}

	public void setObjJSONHeader(String objJSONHeader) {
		this.objJSONHeader = objJSONHeader;
	}

	public List<GetHoldingsRows> getObjJSONRows() {
		return objJSONRows;
	}

	public void setObjJSONRows(List<GetHoldingsRows> objJSONRows) {
		this.objJSONRows = objJSONRows;
	}

//	@SerializedName("Details")
//	protected List<GetHoldingsRows> Details;
//
//	public List<GetHoldingsRows> getDetails() {
//		return Details;
//	}
//
//	public void setDetails(List<GetHoldingsRows> details) {
//		Details = details;
//	}

}
