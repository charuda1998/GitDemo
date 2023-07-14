package com.globecapital.api.ft.order;

import com.google.gson.annotations.SerializedName;

public class GetGTDOrderBookObjectRow {

	@SerializedName("ModifyFlag")
	protected String sModifyFlag;

	@SerializedName("GTDOrderId")
	protected String sGTDOrderId;

	@SerializedName("Exchange")
	protected String sExchange;

	@SerializedName("Symbol")
	protected String sSymbol;

	@SerializedName("Series")
	protected String sSeries;

	@SerializedName("BuySell")
	protected String sBuySell;

	@SerializedName("GTDDate")
	protected String sGTDDate;

	@SerializedName("Price")
	protected String sPrice;

	@SerializedName("GTDOrderStatus")
	protected String sGTDOrderStatus;

	@SerializedName("Status")
	protected String sStatus;

	@SerializedName("TotalQuantity")
	protected String sTotalQuantity;

	@SerializedName("QuantityOpen")
	protected String sQuantityOpen;

	@SerializedName("QuantityExecuted")
	protected String sQuantityExecuted;

	@SerializedName("GTDDay")
	protected String sGTDDay;

	@SerializedName("PrevTradedQuantity")
	protected String sPrevTradedQuantity;

	@SerializedName("OrderType")
	protected String sOrderType;

	@SerializedName("CancelFlag")
	protected String sCancelFlag;

	@SerializedName("TriggerPrice")
	protected String sTriggerPrice;

	@SerializedName("ExchangeOrderNumber")
	protected String sExchangeOrderNumber;

	@SerializedName("DiscQty")
	protected String sDiscQty;

	@SerializedName("GatewayOrderNumber")
	protected String sGatewayOrderNumber;

	@SerializedName("OrderTime")
	protected String sOrderTime;

	@SerializedName("ClientOrderNumber")
	protected String sClientOrderNumber;

	@SerializedName("ProdType")
	protected String sProdType;

	@SerializedName("ScripCode")
	protected String sScripCode;

	@SerializedName("MarketLot")
	protected String sMarketLot;

	@SerializedName("PriceTick")
	protected String sPriceTick;

	@SerializedName("DecimalLoc")
	protected String sDecimalLoc;

	@SerializedName("GTDOrdTime")
	protected String sGTDOrdTime;

	public String getModifyFlag() {
		return sModifyFlag;
	}

	public void setModifyFlag(String sModifyFlag) {
		this.sModifyFlag = sModifyFlag;
	}

	public String getGTDOrderId() {
		return sGTDOrderId;
	}

	public void setGTDOrderId(String sGTDOrderId) {
		this.sGTDOrderId = sGTDOrderId;
	}

	public String getExchange() {
		return sExchange;
	}

	public void setExchange(String sExchange) {
		this.sExchange = sExchange;
	}

	public String getSymbol() {
		return sSymbol;
	}

	public void setSymbol(String sSymbol) {
		this.sSymbol = sSymbol;
	}

	public String getSeries() {
		return sSeries;
	}

	public void setSeries(String sSeries) {
		this.sSeries = sSeries;
	}

	public String getBuySell() {
		return sBuySell;
	}

	public void setBuySell(String sBuySell) {
		this.sBuySell = sBuySell;
	}

	public String getGTDDate() {
		return sGTDDate;
	}

	public void setGTDDate(String sGTDDate) {
		this.sGTDDate = sGTDDate;
	}

	public String getPrice() {
		return sPrice;
	}

	public void setPrice(String sPrice) {
		this.sPrice = sPrice;
	}

	public String getGTDOrderStatus() {
		return sGTDOrderStatus;
	}

	public void setGTDOrderStatus(String sGTDOrderStatus) {
		this.sGTDOrderStatus = sGTDOrderStatus;
	}

	public String getStatus() {
		return sStatus;
	}

	public void setStatus(String sStatus) {
		this.sStatus = sStatus;
	}

	public String getTotalQuantity() {
		return sTotalQuantity;
	}

	public void setTotalQuantity(String sTotalQuantity) {
		this.sTotalQuantity = sTotalQuantity;
	}

	public String getQuantityOpen() {
		return sQuantityOpen;
	}

	public void setQuantityOpen(String sQuantityOpen) {
		this.sQuantityOpen = sQuantityOpen;
	}

	public String getQuantityExecuted() {
		return sQuantityExecuted;
	}

	public void setQuantityExecuted(String sQuantityExecuted) {
		this.sQuantityExecuted = sQuantityExecuted;
	}

	public String getGTDDay() {
		return sGTDDay;
	}

	public void setGTDDay(String sGTDDay) {
		this.sGTDDay = sGTDDay;
	}

	public String getPrevTradedQuantity() {
		return sPrevTradedQuantity;
	}

	public void setPrevTradedQuantity(String sPrevTradedQuantity) {
		this.sPrevTradedQuantity = sPrevTradedQuantity;
	}

	public String getOrderType() {
		return sOrderType;
	}

	public void setOrderType(String sOrderType) {
		this.sOrderType = sOrderType;
	}

	public String getCancelFlag() {
		return sCancelFlag;
	}

	public void setCancelFlag(String sCancelFlag) {
		this.sCancelFlag = sCancelFlag;
	}

	public String getTriggerPrice() {
		return sTriggerPrice;
	}

	public void setTriggerPrice(String sTriggerPrice) {
		this.sTriggerPrice = sTriggerPrice;
	}

	public String getExchangeOrderNumber() {
		return sExchangeOrderNumber;
	}

	public void setExchangeOrderNumber(String sExchangeOrderNumber) {
		this.sExchangeOrderNumber = sExchangeOrderNumber;
	}

	public String getDiscQty() {
		return sDiscQty;
	}

	public void setDiscQty(String sDiscQty) {
		this.sDiscQty = sDiscQty;
	}

	public String getGatewayOrderNumber() {
		return sGatewayOrderNumber;
	}

	public void setGatewayOrderNumber(String sGatewayOrderNumber) {
		this.sGatewayOrderNumber = sGatewayOrderNumber;
	}

	public String getOrderTime() {
		return sOrderTime;
	}

	public void setOrderTime(String sOrderTime) {
		this.sOrderTime = sOrderTime;
	}

	public String getClientOrderNumber() {
		return sClientOrderNumber;
	}

	public void setClientOrderNumber(String sClientOrderNumber) {
		this.sClientOrderNumber = sClientOrderNumber;
	}

	public String getProdType() {
		return sProdType;
	}

	public void setProdType(String sProdType) {
		this.sProdType = sProdType;
	}

	public String getScripCode() {
		return sScripCode;
	}

	public void setScripCode(String sScripCode) {
		this.sScripCode = sScripCode;
	}

	public String getMarketLot() {
		return sMarketLot;
	}

	public void setMarketLot(String sMarketLot) {
		this.sMarketLot = sMarketLot;
	}

	public String getPriceTick() {
		return sPriceTick;
	}

	public void setPriceTick(String sPriceTick) {
		this.sPriceTick = sPriceTick;
	}

	public String getDecimalLoc() {
		return sDecimalLoc;
	}

	public void setDecimalLoc(String sDecimalLoc) {
		this.sDecimalLoc = sDecimalLoc;
	}

	public String getGTDOrdTime() {
		return sGTDOrdTime;
	}

	public void setGTDOrdTime(String sGTDOrdTime) {
		this.sGTDOrdTime = sGTDOrdTime;
	}

}
