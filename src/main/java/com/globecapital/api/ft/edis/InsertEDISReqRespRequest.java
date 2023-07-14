package com.globecapital.api.ft.edis;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class InsertEDISReqRespRequest extends FTRequest {

	public InsertEDISReqRespRequest() throws AppConfigNoKeyFoundException {
		super();
	} 
	
	public void setApprovedFreeQty(int approvedFreeQty) {
		this.addToData(FTConstants.APPROVED_FREE, approvedFreeQty);
	}
	
	public void setDepository(String depository) {
		this.addToData(FTConstants.DEPOSITORY, depository);
	}
	
	public void setDPId(String DPId) {
		this.addToData(FTConstants.DP_ID, DPId);
	}
	
	public void setISIN(String ISIN) {
		this.addToData(FTConstants.ISIN_CODE, ISIN);
	}
	
	public void setMktSegId(int mktSegId) {
		this.addToData(FTConstants.MKT_SEGID, mktSegId);
	}
	
	public void setOrderQty(int orderQty) {
		this.addToData(FTConstants.ORDER_QTY, orderQty);
	}
	
	public void setRequestType(String requestType) {
		this.addToData(FTConstants.REQUEST_TYPE, requestType);
	}
	
	public void setToken(int token) {
		this.addToData(FTConstants.TOKEN, token);
	}
	
	public void setTotalAvailableQty(int totalAvailableQty) {
		this.addToData(FTConstants.TOTAL_AVAILABLE_QTY, totalAvailableQty);
	}
	
	public void setUserCode(String usercode) {
		this.addToData(FTConstants.USER_CODE, usercode);
	}
	
	public void setProdCode(String usercode) {
		this.addToData(FTConstants.PRODUCT_CODE, usercode);
	}
}
