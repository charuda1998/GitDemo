package com.globecapital.api.ft.order;

import com.google.gson.annotations.SerializedName;

public class GetOrderBookObjectRow {

	@SerializedName("exitOptFlag")
	protected String exitOptFlag;

	@SerializedName("posConvFlag")
	protected String posConvFlag;

	@SerializedName("ExOrderNo")
	protected String ExOrderNo;

	@SerializedName("Exch")
	protected String exch;

	@SerializedName("BuySell")
	protected String buySell;

	@SerializedName("PendQty")
	protected String pendQty;

	@SerializedName("Qty")
	protected String qty;

	@SerializedName("Sym")
	protected String sym;

	@SerializedName("ExpDate")
	protected String expDate;

	@SerializedName("StrPrc")
	protected String strPrc;

	@SerializedName("OptType")
	protected String optType;

	@SerializedName("Prc")
	protected String prc;

	@SerializedName("AMOID")
	protected String amoid;

	@SerializedName("ProdType")
	protected String prodType;

	@SerializedName("OrdStat")
	protected String ordStat;

	@SerializedName("Series")
	protected String series;

	@SerializedName("Inst")
	protected String inst;

	@SerializedName("OrderType")
	protected String orderType;

	@SerializedName("TrigPrc")
	protected String trigPrc;

	@SerializedName("Days")
	protected String days;

	@SerializedName("Time")
	protected String time;

	@SerializedName("GatewayOrdNo")
	protected String gatewayOrdNo;

	@SerializedName("Validity")
	protected String validity;

	@SerializedName("Error")
	protected String error;

	@SerializedName("ScripCode")
	protected String scripCode;

	@SerializedName("OrderDate")
	protected String orderDate;

	@SerializedName("SecurityDesc")
	protected String securityDesc;

	@SerializedName("DiscQty")
	protected String discQty;

	@SerializedName("InitiatedFrm")
	protected String initiatedFrm;

	@SerializedName("ModifiedFrm")
	protected String modifiedFrm;

	@SerializedName("Misc")
	protected String misc;

	@SerializedName("SORID")
	protected String sorid;

	@SerializedName("ClientOdrNo")
	protected String clientOdrNo;

	@SerializedName("OdrFrom")
	protected String odrFrom;

	@SerializedName("GTTTime")
	protected String gTTTime;

	@SerializedName("Fill")
	protected String fill;

	@SerializedName("Private")
	protected String Private;

	@SerializedName("PrivateVal")
	protected String privateVal;

	@SerializedName("MktLot")
	protected String mktLot;

	@SerializedName("DecimalLoc")
	protected String decimalLoc;

	@SerializedName("PrcTick")
	protected String prcTick;

	@SerializedName("OrderTime")
	protected String orderTime;

	@SerializedName("COL")
	protected String col;

	@SerializedName("SLJumpPrc")
	protected String slJumpPrc;

	@SerializedName("LTPJumpPrc")
	protected String ltpJumpPrc;

	@SerializedName("ProfitOrdPrc")
	protected String profitOrdPrc;

	@SerializedName("SLOrdPrc")
	protected String slOrdPrc;

	@SerializedName("SLTrigPrc")
	protected String slTrigPrc;

	@SerializedName("SPOSFLAG")
	protected String sposFlag;

	@SerializedName("LegIndicator")
	protected String legIndicator;

	@SerializedName("PosIndicator")
	protected String posIndicator;

	@SerializedName("BracketOrdId")
	protected String bracketOrdId;

	@SerializedName("BracketOrdModifyBit")
	protected String bracketOrdModifyBit;

	@SerializedName("BracketOrdSLOrdType")
	protected String bracketOrdSLOrdType;

	@SerializedName("MktProt")
	protected String mktProt;

	@SerializedName("OFSMargin")
	protected String ofsMargin;

	@SerializedName("ClientOrdNo")
	protected String clientOrdNo;

	@SerializedName("ParticipantID")
	protected String participantID;

	@SerializedName("BracketOrderStatus")
	protected String bracketOrderStatus;

	@SerializedName("LastTradedQty")
	protected String lastTradedQty;
	
	@SerializedName("RecoId")
	protected String recoId;

	@SerializedName("OrderId")
	protected String orderId;

    @SerializedName("ISGTD")
	protected String isGTD;
	
	@SerializedName("GTDOrdStatus")
	protected String sGTDOrderStatus;

	public String getExitOptFlag() {
		return exitOptFlag;
	}

	public void setExitOptFlag(String exitOptFlag) {
		this.exitOptFlag = exitOptFlag;
	}

	public String getPosConvFlag() {
		return posConvFlag;
	}

	public void setPosConvFlag(String posConvFlag) {
		this.posConvFlag = posConvFlag;
	}

	public String getExOrderNo() {
		return ExOrderNo;
	}

	public void setExOrderNo(String exOrderNo) {
		ExOrderNo = exOrderNo;
	}

	public String getExch() {
		return exch;
	}

	public void setExch(String exch) {
		this.exch = exch;
	}

	public String getBuySell() {
		return buySell;
	}

	public void setBuySell(String buySell) {
		this.buySell = buySell;
	}

	public String getPendQty() {
		return pendQty;
	}

	public void setPendQty(String pendQty) {
		this.pendQty = pendQty;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getSym() {
		return sym;
	}

	public void setSym(String sym) {
		this.sym = sym;
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

	public String getPrc() {
		return prc;
	}

	public void setPrc(String prc) {
		this.prc = prc;
	}

	public String getAmoid() {
		return amoid;
	}

	public void setAmoid(String amoid) {
		this.amoid = amoid;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getOrdStat() {
		return ordStat;
	}

	public void setOrdStat(String ordStat) {
		this.ordStat = ordStat;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getInst() {
		return inst;
	}

	public void setInst(String inst) {
		this.inst = inst;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getTrigPrc() {
		return trigPrc;
	}

	public void setTrigPrc(String trigPrc) {
		this.trigPrc = trigPrc;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getGatewayOrdNo() {
		return gatewayOrdNo;
	}

	public void setGatewayOrdNo(String gatewayOrdNo) {
		this.gatewayOrdNo = gatewayOrdNo;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getScripCode() {
		return scripCode;
	}

	public void setScripCode(String scripCode) {
		this.scripCode = scripCode;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getSecurityDesc() {
		return securityDesc;
	}

	public void setSecurityDesc(String securityDesc) {
		this.securityDesc = securityDesc;
	}

	public String getDiscQty() {
		return discQty;
	}

	public void setDiscQty(String discQty) {
		this.discQty = discQty;
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

	public String getClientOdrNo() {
		return clientOdrNo;
	}

	public void setClientOdrNo(String clientOdrNo) {
		this.clientOdrNo = clientOdrNo;
	}

	public String getOdrFrom() {
		return odrFrom;
	}

	public void setOdrFrom(String odrFrom) {
		this.odrFrom = odrFrom;
	}

	public String getgTTTime() {
		return gTTTime;
	}

	public void setgTTTime(String gTTTime) {
		this.gTTTime = gTTTime;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public String getPrivate() {
		return Private;
	}

	public void setPrivate(String private1) {
		Private = private1;
	}

	public String getPrivateVal() {
		return privateVal;
	}

	public void setPrivateVal(String privateVal) {
		this.privateVal = privateVal;
	}

	public String getMktLot() {
		return mktLot;
	}

	public void setMktLot(String mktLot) {
		this.mktLot = mktLot;
	}

	public String getDecimalLoc() {
		return decimalLoc;
	}

	public void setDecimalLoc(String decimalLoc) {
		this.decimalLoc = decimalLoc;
	}

	public String getPrcTick() {
		return prcTick;
	}

	public void setPrcTick(String prcTick) {
		this.prcTick = prcTick;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public void setSlTrigPrc(String slTrigPrc) {
		this.slTrigPrc = slTrigPrc;
	}

	public String getSposFlag() {
		return sposFlag;
	}

	public void setSposFlag(String sposFlag) {
		this.sposFlag = sposFlag;
	}

	public String getLegIndicator() {
		return legIndicator;
	}

	public void setLegIndicator(String legIndicator) {
		this.legIndicator = legIndicator;
	}

	public String getPosIndicator() {
		return posIndicator;
	}

	public void setPosIndicator(String posIndicator) {
		this.posIndicator = posIndicator;
	}

	public String getBracketOrdId() {
		return bracketOrdId;
	}

	public void setBracketOrdId(String bracketOrdId) {
		this.bracketOrdId = bracketOrdId;
	}

	public String getBracketOrdModifyBit() {
		return bracketOrdModifyBit;
	}

	public void setBracketOrdModifyBit(String bracketOrdModifyBit) {
		this.bracketOrdModifyBit = bracketOrdModifyBit;
	}

	public String getBracketOrdSLOrdType() {
		return bracketOrdSLOrdType;
	}

	public void setBracketOrdSLOrdType(String bracketOrdSLOrdType) {
		this.bracketOrdSLOrdType = bracketOrdSLOrdType;
	}

	public String getMktProt() {
		return mktProt;
	}

	public void setMktProt(String mktProt) {
		this.mktProt = mktProt;
	}

	public String getOfsMargin() {
		return ofsMargin;
	}

	public void setOfsMargin(String ofsMargin) {
		this.ofsMargin = ofsMargin;
	}

	public String getClientOrdNo() {
		return clientOrdNo;
	}

	public void setClientOrdNo(String clientOrdNo) {
		this.clientOrdNo = clientOrdNo;
	}

	public String getParticipantID() {
		return participantID;
	}

	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}

	public String getBracketOrderStatus() {
		return bracketOrderStatus;
	}

	public void setBracketOrderStatus(String bracketOrderStatus) {
		this.bracketOrderStatus = bracketOrderStatus;
	}

	public String getLastTradedQty() {
		return lastTradedQty;
	}

	public void setLastTradedQty(String lastTradedQty) {
		this.lastTradedQty = lastTradedQty;
	}

	public String getSlJumpPrc() {
		return slJumpPrc;
	}

	public void setSlJumpPrc(String slJumpPrc) {
		this.slJumpPrc = slJumpPrc;
	}

	public String getLtpJumpPrc() {
		return ltpJumpPrc;
	}

	public void setLtpJumpPrc(String ltpJumpPrc) {
		this.ltpJumpPrc = ltpJumpPrc;
	}

	public String getProfitOrdPrc() {
		return profitOrdPrc;
	}

	public void setProfitOrdPrc(String profitOrdPrc) {
		this.profitOrdPrc = profitOrdPrc;
	}

	public String getSlOrdPrc() {
		return slOrdPrc;
	}

	public void setSlOrdPrc(String slOrdPrc) {
		this.slOrdPrc = slOrdPrc;
	}

	public String getSlTrigPrc() {
		return slTrigPrc;
	}
	
	public String getRecoId() {
		return recoId;
	}

	public void setRecoId(String recoId) {
		this.recoId = recoId;
	}

	public String getIsGTD() {
		return isGTD;
	}

	public void setIsGTD(String isGTD) {
		this.isGTD = isGTD;
	}
	
	public String getGTDOrderStatus() {
        return sGTDOrderStatus;
    }

    public void setGTDOrderStatus(String sGTDOrderStatus) {
        this.sGTDOrderStatus = sGTDOrderStatus;
    }

}
