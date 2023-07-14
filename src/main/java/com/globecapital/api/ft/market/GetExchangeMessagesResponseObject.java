package com.globecapital.api.ft.market;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetExchangeMessagesResponseObject {

	@SerializedName("lstHeaderId")
	protected String lstHeaderId;

	@SerializedName("lstHeaderNm")
	protected String lstHeaderNm;

	@SerializedName("dtRows")
	protected String dtRows;

	@SerializedName("objJSONHeader")
	protected String objJSONHeader;

	@SerializedName("objJSONRows")
	protected List<GetExchangeMessagesObjectRow> objJSONRows;

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

	public List<GetExchangeMessagesObjectRow> getObjJSONRows() {
		return objJSONRows;
	}

	public void setObjJSONRows(List<GetExchangeMessagesObjectRow> objJSONRows) {
		this.objJSONRows = objJSONRows;
	}

}
