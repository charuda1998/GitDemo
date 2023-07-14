package com.globecapital.api.ft.order;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class PositionConversionRequest extends FTRequest {

	public PositionConversionRequest() throws AppConfigNoKeyFoundException {
		super();

		/** Setting fixed value for Position conversion request **/
		setMsgCode(FTConstants.POSITION_CONV_MSG_CODE);
	}

	public void setScripTkn(String scripTkn) throws JSONException {
		addToData(FTConstants.SCRIP_TKN, scripTkn);
	}

	public void setClientOdrNo(String clientOdrNo) throws JSONException {
		addToData(FTConstants.CLIENT_ODR_NO, clientOdrNo);
	}

	public void setGatewayOdrNo(String gatewayOdrNo) throws JSONException {
		addToData(FTConstants.GATEWAY_ODR_NO, gatewayOdrNo);
	}

	public void setMKtSegId(int MKtSegId) throws JSONException {
		addToData(FTConstants.MKT_SEGID, MKtSegId);
	}

	public void setUserRemarks(String userRemarks) throws JSONException {
		addToData(FTConstants.USER_REMARKS, userRemarks);
	}

	public void setIsSpreadScrip(String isSpreadScrip) throws JSONException {
		addToData(FTConstants.IS_SPREAD_SCRIP, isSpreadScrip);
	}

	public void setOdrReqType(int odrReqType) throws JSONException {
		addToData(FTConstants.ODR_REQ_TYPE, odrReqType);
	}

	public void setToProductType(String toProductType) throws JSONException {
		addToData(FTConstants.PRODUCT_TYPE, toProductType);
	}

	public void setFromProductType(String sourceProductType) throws JSONException {
		addToData(FTConstants.SOURCE_PRODUCT_TYPE, sourceProductType);
	}

	public void setOriginalQty(int originalQty) throws JSONException {
		addToData(FTConstants.ORIGINAL_QTY, originalQty);
	}

	public void setSeries(String Series) throws JSONException {
		addToData(FTConstants.SERIES, Series);
	}

	public void setBuyOrSell(String BuyOrSell) throws JSONException {
		addToData(FTConstants.BUY_OR_SELL, BuyOrSell);

	}

	public void setSymbol(String Symbol) throws JSONException {
		addToData(FTConstants.SYMBOL, Symbol);
	}

	public void setMsgCode(String msgCode) throws JSONException {
		addToData(FTConstants.MSG_CODE, msgCode);
	}

	public void setBuyOrSell(int iBuyOrSell) {
		addToData(FTConstants.BUY_OR_SELL, iBuyOrSell);

	}
	
	public void setBOGatewayOdrNo(String boGatewayOdrNo) {
		addToData(FTConstants.BO_GATEWAY_ODR_NO, boGatewayOdrNo);

	}

}
