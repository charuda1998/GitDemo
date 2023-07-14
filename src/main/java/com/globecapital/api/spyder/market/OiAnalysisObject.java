package com.globecapital.api.spyder.market;

public class OiAnalysisObject {
	
	protected String ScCode;
	protected String LTP;
	protected String Change;
	protected String PChg;
	protected int OI;
	protected String OIper;
	
	public String getCode() {
		return ScCode;
	}
	public void setCode(String code) {
		this.ScCode = code;
	}
	
	public String getLtp() {
		return LTP;
	}

	public void setLtp(String Ltp) {
		this.LTP = Ltp;
	}
	
	public String getChange() {
		return Change;
	}

	public void setChange(String chng) {
		this.Change = chng;
	}
	
	public String getPChg() {
		return PChg;
	}

	public void setPChg(String PChg) {
		this.PChg = PChg;
	}

	public int getOi() {
		return OI;
	}

	public void setOi(int oi) {
		this.OI = oi;
	}
	
	public String getOiPer() {
		return OIper;
	}

	public void setOiPer(String oiPer) {
		this.OIper = oiPer;
	}
	
}
