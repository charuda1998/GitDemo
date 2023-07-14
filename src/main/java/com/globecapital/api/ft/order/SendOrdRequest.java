package com.globecapital.api.ft.order;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class SendOrdRequest extends FTRequest {

	public SendOrdRequest() throws AppConfigNoKeyFoundException, JSONException {
		
		super();
		
		/*** For a normal order following fields should be initialized to default values***/
		
		setMsgCode(FTConstants.NORMAL_MSG_CODE);
		setModifyFlag(false);
		setCancelFlag(false);
		setSpread(false);
		setIsSpreadScrip(0);
		setDays("");
		setOrdGTD("0");
		setExchOrdNo("0"); 
		setInstrumentId("");
		setOrdTime("0");
		setGatewayOrdNo("");
		setMPExpVal("-1");
		setMPStrkPrcVal("-1");
		setMPBasePrice("");
		setMPMFFlag("0");
		setMPFirstLegPrice(0);
		//setBOSLOrderType("");
		setSLTriggerPrice("");
		setProfitOrderPrice("");
		setSLJumpPrice("0");
		setLTPJumpPrice("0");
		setBracketOrderId("");
		//setLegIndicator("");
		//setBOModifyTerms("");
		setSPOSType("0");
		setFIILimit("0");
		setNRILimit("0");
		setIsSOR(false);
		setMktProt(0);
		setCOL(1);
		setOFSMargin("");
		setRecoId("");
	}

	public void setSpread(boolean IsSpread) throws JSONException {
		addToData(FTConstants.IS_SPREAD, IsSpread);
	}

	public void setModifyFlag(boolean modifyFlag) throws JSONException {
		addToData(FTConstants.MODIFY_FLAG, modifyFlag);
	}

	public void setCancelFlag(boolean cancelFlag) throws JSONException {
		addToData(FTConstants.CANCEL_FLAG, cancelFlag);
	}

	public void setOrderSide(String OrderSide) throws JSONException {
		addToData(FTConstants.ORDER_SIDE, OrderSide);
	}

	public void setScripTkn(String sScripToken) throws JSONException {
		addToData(FTConstants.SCRIP_TKN, sScripToken);
	}

	public void setOrdType(int OrdType) throws JSONException {
		addToData(FTConstants.ORD_TYPE, OrdType);
	}

	public void setDiscQty(int DiscQty) throws JSONException {
		addToData(FTConstants.DISC_QTY, DiscQty);
	}

	public void setOrdPrice(String OrdPrice) throws JSONException {
		addToData(FTConstants.ORD_PRICE, OrdPrice);
	}

	public void setTrigPrice(String TrigPrice) throws JSONException {
		addToData(FTConstants.TRIG_PRICE, TrigPrice);
	}

	public void setValidity(int Validity) throws JSONException {
		addToData(FTConstants.VALIDITY, Validity);
	}

	public void setDays(String Days) throws JSONException {
		addToData(FTConstants.DAYS, Days);
	}

	public void setInstrument(String Instrument) throws JSONException {
		addToData(FTConstants.INSTRUMENT, Instrument);
	}

	public void setOrdGTD(String string) throws JSONException {
		addToData(FTConstants.ORD_GTD, string);
	}

	public void setExchOrdNo(String string) throws JSONException {
		addToData(FTConstants.EXCH_ORD_NO, string);
	}

	public void setProdType(String ProdType) throws JSONException {
		addToData(FTConstants.PROD_TYPE, ProdType);
	}

	public void setInstrumentId(String InstrumentId) throws JSONException {
		addToData(FTConstants.INSTRUMENT_ID, InstrumentId);
	}

	public void setExpDt(String ExpDt) throws JSONException {
		addToData(FTConstants.EXP_DT, ExpDt);
	}

	public void setStrkPrc(String sStrikePrice) throws JSONException {
		addToData(FTConstants.STRK_PRC, sStrikePrice);
	}

	public void setOptType(String OptType) throws JSONException {
		addToData(FTConstants.OPT_TYPE, OptType);
	}

	public void setMktLot(String sMktLot) throws JSONException {
		addToData(FTConstants.MKT_LOT, sMktLot);
	}

	public void setPrcTick(String sPricTick) throws JSONException {
		addToData(FTConstants.PRC_TICK, sPricTick);
	}

	public void setOrdTime(String string) throws JSONException {
		addToData(FTConstants.ORD_TIME, string);
	}

	public void setClientOrdNo(int ClientOrdNo) throws JSONException {
		addToData(FTConstants.CLIENT_ORD_NO, ClientOrdNo);
	}

	public void setGatewayOrdNo(String GatewayOrdNo) throws JSONException {
		addToData(FTConstants.GATEWAY_ORD_NO, GatewayOrdNo);
	}

	public void setMKtSegId(int MKtSegId) throws JSONException {
		addToData(FTConstants.MKT_SEG_ID_ORDER, MKtSegId);
	}

	public void setMPExpVal(String string) throws JSONException {
		addToData(FTConstants.MP_EXP_VAL, string);
	}

	public void setMPStrkPrcVal(String string) throws JSONException {
		addToData(FTConstants.MP_STRK_PRC_VAL, string);
	}

	public void setMPBasePrice(String string) throws JSONException {
		addToData(FTConstants.MP_BASE_PRICE, string);
	}

	public void setMPMFFlag(String string) throws JSONException {
		addToData(FTConstants.MPMF_FLAG, string);
	}

	public void setMPFirstLegPrice(float MPFirstLegPrice) throws JSONException {
		addToData(FTConstants.MP_FIRST_LEG_PRICE, MPFirstLegPrice);
	}

	public void setBOSLOrderType(int istring) throws JSONException {
		addToData(FTConstants.BOS_LORDER_TYPE, istring);
	}

	public void setSLOrderPrice(String sSLOrderPrice) throws JSONException {
		addToData(FTConstants.SL_ORDER_PRICE, sSLOrderPrice);
	}

	public void setSLTriggerPrice(String sSLTriggerPrice) throws JSONException {
		addToData(FTConstants.SL_TRIGGER_PRICE, sSLTriggerPrice);
	}

	public void setProfitOrderPrice(String sProfitOrderPrice) throws JSONException {
		addToData(FTConstants.PROFIT_ORDER_PRICE, sProfitOrderPrice);
	}

	public void setSLJumpPrice(String sSLJumpPrice) throws JSONException {
		addToData(FTConstants.SL_JUMP_PRICE, sSLJumpPrice);
	}

	public void setBracketOrderId(String string) throws JSONException {
		addToData(FTConstants.BRACKET_ORDER_ID, string);
	}

	public void setLegIndicator(int iLegIndicator) throws JSONException {
		addToData(FTConstants.LEG_INDICATOR, iLegIndicator);
	}

	public void setBOModifyTerms(int iBOModifyTerms) throws JSONException {
		addToData(FTConstants.BO_MODIFY_TERMS, iBOModifyTerms);
	}

	public void setBOGatewayOrderNo(String BOGatewayOrderNo) throws JSONException {
		addToData(FTConstants.BO_GATE_WAY_ORDER_NO, BOGatewayOrderNo);
	}

	public void setSPOSType(String string) throws JSONException {
		addToData(FTConstants.SPOS_TYPE, string);
	}

	public void setFIILimit(String string) throws JSONException {
		addToData(FTConstants.FII_LIMIT, string);
	}

	public void setNRILimit(String string) throws JSONException {
		addToData(FTConstants.NRI_LIMIT, string);
	}

	public void setIsSOR(Boolean IsSOR) throws JSONException {
		addToData(FTConstants.IS_SOR, IsSOR);
	}

	public void setMktProt(int MktProt) throws JSONException {
		addToData(FTConstants.MKT_PROT, MktProt);
	}

	public void setCOL(int col) throws JSONException {
		addToData(FTConstants.COL, col);
	}

	public void setOFSMargin(String OFSMargin) throws JSONException {
		addToData(FTConstants.OFS_MARGIN, OFSMargin);
	}

	public void setRecoId(String string) throws JSONException {
		addToData(FTConstants.RECOID, string);
	}

	public void setLTPJumpPrice(String sLTPJumpPrice) throws JSONException {
		addToData(FTConstants.LTP_JUMP_PRICE, sLTPJumpPrice);
	}

	public void setParticipantId(String ParticipantId) throws JSONException {
		addToData(FTConstants.PARTICIPANT_ID, ParticipantId);
	}
	
	public void setOrgQty(String string) throws JSONException {
		addToData(FTConstants.ORG_QTY, string);
	}
	
	public void setSeries(String Series) throws JSONException {
		addToData(FTConstants.SERIES, Series);
	}
	
	public void setBuyOrSell(int BuyOrSell) throws JSONException {
		addToData(FTConstants.BUY_OR_SELL, BuyOrSell);
	}
	
	public void setSymbol(String Symbol) throws JSONException {
		addToData(FTConstants.SYMBOL, Symbol);
	}
	
	public void setIsSpreadScrip(int i) throws JSONException {
		addToData(FTConstants.IS_SPREAD_SCRIP, i);
	}
	
	public void setMsgCode(int normalMsgCode) throws JSONException {
		addToData(FTConstants.MSG_CODE, normalMsgCode);
	}
}
