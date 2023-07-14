package com.globecapital.api.ft.order;

import com.google.gson.annotations.SerializedName;

public class FetchMarginPlusParamsObject {
	
	@SerializedName("Status")
	protected String sStatus;

	@SerializedName("LTP")
	protected String sLTP;

	@SerializedName("ClosePrice")
	protected String sClosePrice;

	@SerializedName("DecimalLocator")
	protected String sDecimalLocator;
	
	@SerializedName("LimitTrigPerc")
	protected String sLimitTrigPerc;
	
	@SerializedName("Profit Price Low")
	protected String sProfitPriceLow;
	
	@SerializedName("Profit Price High")
	protected String sProfitPriceHigh;
	
	@SerializedName("Overall Price Low")
	protected String sOverallPriceLow;
	
	@SerializedName("Overall Price High")
	protected String sOverallPriceHigh;
	
	@SerializedName("ExpiryValue")
	protected String sExpiryValue;

	@SerializedName("BasePrice")
	protected String sBasePrice;
	
	@SerializedName("StrikeValue")
	protected String sStrikeValue;
	
	@SerializedName("HighPriceRange")
	protected String sHighPriceRange;
	
	@SerializedName("LowPriceRange")
	protected String sLowPriceRange;
	
	@SerializedName("LimitTrigPercMin")
	protected String sLimitTrigPercMin;
	
	@SerializedName("PriceTick")
	protected String sPriceTick;
	
	@SerializedName("TrigLimitPerc")
    protected String sTrigLimitPerc;
	
	@SerializedName("DPRRange")
    protected String sDPRRange;
    
    public void setTrigLimitPerc(String sTrigLimitPerc) {
        this.sTrigLimitPerc = sTrigLimitPerc;
    }
    
    public String getTrigLimitPerc() {
        return sTrigLimitPerc;
    }
	
	public void setStrikeValue(String sStrikeValue) {
		this.sStrikeValue = sStrikeValue;
	}
	
	public String getStrikeValue() {
		return sStrikeValue;
	}

	public void setStatus(String sStatus) {
		this.sStatus = sStatus;
	}
	
	public String getStatus() {
		return sStatus;
	}
	
	public void setClosePrice(String sClosePrice) {
		this.sClosePrice = sClosePrice;
	}
	
	public String getClosePrice() {
		return sClosePrice;
	}
	
	public void setLTP(String sLTP) {
		this.sLTP = sLTP;
	}
	
	public String getLTP() {
	    if(sLTP.equals(""))
	        return "0";
	    else
	        return sLTP;
	}
	
	public void setBasePrice(String sBasePrice) {
		this.sBasePrice = sBasePrice;
	}
	
	public String getBasePrice() {
		return sBasePrice;
	}
	
	public void setExpiryValue(String sExpiryValue) {
		this.sExpiryValue = sExpiryValue;
	}
	
	public String getExpiryValue() {
		return sExpiryValue;
	}

	public String getProfitPriceLow() {
		return sProfitPriceLow;
	}

	public void setProfitPriceLow(String sProfitPriceLow) {
		this.sProfitPriceLow = sProfitPriceLow;
	}
	
	public String getProfitPriceHigh() {
		return sProfitPriceHigh;
	}

	public void setProfitPriceHigh(String sProfitPriceHigh) {
		this.sProfitPriceHigh = sProfitPriceHigh;
	}
	
	public String getOverallPriceLow() {
		return sOverallPriceLow;
	}

	public void setOverallPriceLow(String sOverallPriceLow) {
		this.sOverallPriceLow = sOverallPriceLow;
	}
	
	public String getOverallPriceHigh() {
		return sOverallPriceHigh;
	}

	public void setOverallPriceHigh(String sOverallPriceHigh) {
		this.sOverallPriceHigh = sOverallPriceHigh;
	}
	
	public String getHighPriceRange() {
	    if(sHighPriceRange.equals(""))
            return "0";
        else
            return sHighPriceRange;
	}

	public void setHighPriceRange(String sHighPriceRange) {
		this.sHighPriceRange = sHighPriceRange;
	}
	
	public String getLowPriceRange() {
	    if(sLowPriceRange.equals(""))
            return "0";
        else
            return sLowPriceRange;
	}

	public void setLowPriceRange(String sLowPriceRange) {
		this.sLowPriceRange = sLowPriceRange;
	}
	
	public String getDecimalLocator() {
		return sDecimalLocator;
	}

	public void setDecimalLocator(String sDecimalLocator) {
		this.sDecimalLocator = sDecimalLocator;
	}

	public String getLimitTrigPercMin() {
		return sLimitTrigPercMin;
	}

	public void setLimitTrigPercMin(String sLimitTrigPercMin) {
		this.sLimitTrigPercMin = sLimitTrigPercMin;
	}
	
	public String getPriceTick() {
		return sPriceTick;
	}

	public void setPriceTick(String sPriceTick) {
		this.sPriceTick = sPriceTick;
	}
	
	public void setDPRRange(String sDPRRange) {
        this.sDPRRange = sDPRRange;
    }
    
    public String getDPRRange() {
        return sDPRRange;
    }
    
    public String getLimitTrigPerc() {
        return sLimitTrigPercMin;
    }

    public void setLimitTrigPerc(String sLimitTrigPerc) {
        this.sLimitTrigPerc = sLimitTrigPerc;
    }

}
