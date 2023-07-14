package com.globecapital.api.ft.edis;

import java.util.List;
import org.json.JSONObject;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("rawtypes")
public class GetEDISScripDetailsObject extends JSONObject {

	@SerializedName("TodayFreeQty")
	protected int todayFreeQty;
	
	@SerializedName("ApprovedQuantity")
	protected int approvedQuantity;
	
	@SerializedName("eDISRequestQty")
	protected int eDISRequestQty;
	
	@SerializedName("TotalFreeQty")
	protected int totalFreeQty;
	
	@SerializedName("sISINCode")
	protected String sISINCode;
	
	@SerializedName("eDISCheckQty")
	protected int eDISCheckQty;
	
	@SerializedName("ClosePrice")
	protected double closePrice;

	@SerializedName("eDISDPQty")
	protected int eDISDPQty;
	
	@SerializedName("nSettlementType")
	protected int nSettlementType;

	public int getTodayFreeQty() {
		return todayFreeQty;
	}

	public void setTodayFreeQty(int todayFreeQty) {
		this.todayFreeQty = todayFreeQty;
	}

	public int getApprovedQuantity() {
		return approvedQuantity;
	}

	public void setApprovedQuantity(int approvedQuantity) {
		this.approvedQuantity = approvedQuantity;
	}

	public int geteDISRequestQty() {
		return eDISRequestQty;
	}

	public void seteDISRequestQty(int eDISRequestQty) {
		this.eDISRequestQty = eDISRequestQty;
	}

	public int getTotalFreeQty() {
		return totalFreeQty;
	}

	public void setTotalFreeQty(int totalFreeQty) {
		this.totalFreeQty = totalFreeQty;
	}

	public String getISINCode() {
		return sISINCode;
	}

	public void setISINCode(String sISINCode) {
		this.sISINCode = sISINCode;
	}

	public int geteDISCheckQty() {
		return eDISCheckQty;
	}

	public void seteDISCheckQty(int eDISCheckQty) {
		this.eDISCheckQty = eDISCheckQty;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public int geteDISDPQty() {
		return eDISDPQty;
	}

	public void seteDISDPQty(int eDISDPQty) {
		this.eDISDPQty = eDISDPQty;
	}
	
	public int getSettlementType() {
		return nSettlementType;
	}

	public void setSettlementType(int nSettlementType) {
		this.nSettlementType = nSettlementType;
	}
	
}
