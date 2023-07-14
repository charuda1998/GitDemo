package com.globecapital.api.ft.order;

import com.google.gson.annotations.SerializedName;

public class GetTradeBookObjectRow {

	@SerializedName("Exch")
	protected String exch;

	@SerializedName("TradeNo")
	protected String tradeNo;

	@SerializedName("Inst")
	protected String inst;

	@SerializedName("Sym")
	protected String sym;

	@SerializedName("Series")
	protected String series;

	@SerializedName("ExpDate")
	protected String expDate;

	@SerializedName("StrPrc")
	protected String strPrc;

	@SerializedName("OptType")
	protected String optType;

	@SerializedName("Time")
	protected String time;

	@SerializedName("ExOrderNo")
	protected String exOrderNo;

	@SerializedName("GatewayOrdNo")
	protected String gatewayOrdNo;

	@SerializedName("BuySell")
	protected String buySell;

	@SerializedName("Qty")
	protected String qty;

	@SerializedName("Prc")
	protected String prc;

	@SerializedName("InitiatedFrm")
	protected String initiatedFrm;

	@SerializedName("ModifiedFrm")
	protected String modifiedFrm;

	@SerializedName("Misc")
	protected String misc;

	@SerializedName("SORID")
	protected String sorid;

	@SerializedName("ClientOrdNo")
	protected String clientOrdNo;

	@SerializedName("ScripCode")
	protected String scripCode;

	public String getExch() {
		return exch;
	}

	public void setExch(String exch) {
		this.exch = exch;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getInst() {
		return inst;
	}

	public void setInst(String inst) {
		this.inst = inst;
	}

	public String getSym() {
		return sym;
	}

	public void setSym(String sym) {
		this.sym = sym;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getStrPrc() {
		return strPrc;
	}

	public void setStrPrc(String strPrc) {
		this.strPrc = strPrc;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getExOrderNo() {
		return exOrderNo;
	}

	public void setExOrderNo(String exOrderNo) {
		this.exOrderNo = exOrderNo;
	}

	public String getGatewayOrdNo() {
		return gatewayOrdNo;
	}

	public void setGatewayOrdNo(String gatewayOrdNo) {
		this.gatewayOrdNo = gatewayOrdNo;
	}

	public String getBuySell() {
		return buySell;
	}

	public void setBuySell(String buySell) {
		this.buySell = buySell;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getPrc() {
		return prc;
	}

	public void setPrc(String prc) {
		this.prc = prc;
	}

	public String getInitiatedFrm() {
		return initiatedFrm;
	}

	public void setInitiatedFrm(String initiatedFrm) {
		this.initiatedFrm = initiatedFrm;
	}

	public String getModifiedFrm() {
		return modifiedFrm;
	}

	public void setModifiedFrm(String modifiedFrm) {
		this.modifiedFrm = modifiedFrm;
	}

	public String getMisc() {
		return misc;
	}

	public void setMisc(String misc) {
		this.misc = misc;
	}

	public String getSorid() {
		return sorid;
	}

	public void setSorid(String sorid) {
		this.sorid = sorid;
	}

	public String getClientOrdNo() {
		return clientOrdNo;
	}

	public void setClientOrdNo(String clientOrdNo) {
		this.clientOrdNo = clientOrdNo;
	}

	public String getScripCode() {
		return scripCode;
	}

	public void setScripCode(String scripCode) {
		this.scripCode = scripCode;
	}

}
