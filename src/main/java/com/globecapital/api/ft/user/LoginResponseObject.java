package com.globecapital.api.ft.user;

import com.google.gson.annotations.SerializedName;

public class LoginResponseObject {

	@SerializedName("MsgCode")
	protected String msgCode;

	@SerializedName("SessionId")
	protected String sessionId;

	@SerializedName("AppSessionId")
	protected String appSessionId;

	@SerializedName("LogonStatus")
	protected Integer logonStatus;

	@SerializedName("GroupId")
	protected String groupId;

	@SerializedName("DaysToExpire")
	protected Integer daysToExpire;

	@SerializedName("ErrStr")
	protected String errStr;

	@SerializedName("LastLogonTime")
	protected String lastLogonTime;

	@SerializedName("UserCode")
	protected String userCode;

	@SerializedName("LoginType")
	protected Integer loginType;

	@SerializedName("LogonSuccess")
	protected Boolean logonSuccess;

	@SerializedName("SSOEnabled")
	protected Boolean ssoEnabled;

	@SerializedName("TransnID")
	protected String transnID;

	@SerializedName("ProductTypeName")
	protected String productTypeName;

	@SerializedName("GTDConfiguration")
	protected String gtdConfiguration;

	@SerializedName("InteractiveWSSPort")
	protected String interactiveWSSPort;

	@SerializedName("BroadcastWSSPort")
	protected String broadcastWSSPort;

	@SerializedName("GTDAllowedExchanges")
	protected String gtdAllowedExchanges;

	@SerializedName("UserName")
	protected String userName;

	@SerializedName("GroupCode")
	protected String groupCode;

	@SerializedName("NSEPartCode")
	protected String nsePartCode;

	@SerializedName("NSEFAOPartCode")
	protected String nsefaoPartCode;
	
	@SerializedName("NSECDSPartCode")
	protected String nsecdsPartCode;

	@SerializedName("BSEPartType")
	protected String bsePartType;

	@SerializedName("UserProduct")
	protected String userProduct;

	@SerializedName("InteractiveIP")
	protected String interactiveIP;

	@SerializedName("InteractivePort")
	protected Integer interactivePort;

	@SerializedName("InteractivePort2")
	protected Integer interactivePort2;

	@SerializedName("BroadCastIP")
	protected String broadCastIP;

	@SerializedName("BroadCastPort")
	protected String broadCastPort;

	@SerializedName("PolicyPort")
	protected Integer policyPort;

	@SerializedName("BroadCastPolicyPort")
	protected Integer broadCastPolicyPort;

	@SerializedName("TemplateID")
	protected String templateID;

	@SerializedName("ProductTypeBit")
	protected String productTypeBit;

	@SerializedName("ManagerVersion")
	protected String managerVersion;

	@SerializedName("OdinUser")
	protected Boolean odinUser;

	@SerializedName("LoginThroughOdin")
	protected Boolean loginThroughOdin;

	@SerializedName("ClientOrdNo")
	protected String clientOrdNo;

	@SerializedName("LastLoginIPAddr")
	protected String lastLoginIPAddr;

	@SerializedName("LastLoginMACAddr")
	protected String lastLoginMACAddr;

	@SerializedName("ManagerIP")
	protected String managerIP;

	@SerializedName("AllowedProducts")
	protected String allowedProducts;

	@SerializedName("AllowedProductList")
	protected String allowedProductList;

	@SerializedName("SmartOrder")
	protected String smartOrder;

	@SerializedName("SBSBinaryIP")
	protected String sbsBinaryIP;

	@SerializedName("SBSBinaryPort")
	protected Integer sbsBinaryPort;

	@SerializedName("MultiOC")
	protected String multiOC;

	@SerializedName("NoticeURL")
	protected String noticeURL;

	@SerializedName("LA")
	protected String la;

	@SerializedName("EA")
	protected String ea;

	@SerializedName("CRPAllowed")
	protected String crpAllowed;

	@SerializedName("StreamingAllowed")
	protected Boolean streamingAllowed;

	@SerializedName("DGCXPartCode")
	protected String dgcxpartcode;

	@SerializedName("AdminSqOff")
	protected String adminSqOff;

	@SerializedName("MarginPlusAllowed")
	protected String marginPlusAllowed;

	@SerializedName("SecondLevelVendorID")
	protected String secondLevelVendorID;

	@SerializedName("PTN")
	protected Integer ptn;

	@SerializedName("VendorCode")
	protected String vendorCode;

	@SerializedName("NewsCategories")
	protected String newsCategories;

	@SerializedName("ManagerTime")
	protected String managerTime;

	@SerializedName("ManagerTimeSpanLost")
	protected String managerTimeSpanLost;

	@SerializedName("ManagerTimeDiff")
	protected Integer managerTimeDiff;

	@SerializedName("MCXSXDQEQPERC")
	protected Integer mcxsxdqeqperc;

	@SerializedName("MCXSXDQFAOPERC")
	protected Integer mcxsxdqfaoperc;

	@SerializedName("OEPRODUCT")
	protected String oeproduct;

	@SerializedName("MktProt")
	protected String mktProt;

	@SerializedName("COL")
	protected Boolean col;

	@SerializedName("JUMPBOTHLTPANDTRIGPRICE")
	protected Boolean jUMPBOTHLTPANDTRIGPRICE;

	@SerializedName("OdinCategory")
	protected Integer odinCategory;

	@SerializedName("Param1")
	protected String param1;

	@SerializedName("Param2")
	protected String param2;

	@SerializedName("Param3")
	protected String param3;

	@SerializedName("Param4")
	protected String param4;

	@SerializedName("Param5")
	protected String param5;
	
	@SerializedName("POAStatus")
	protected String poaStatus;
	
	@SerializedName("MobileNo")
    protected String mobileNo;
	
	public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
    
	public String getPOAStatus() {
		return poaStatus;
	}

	public void setPOAStatus(String poaStatus) {
		this.poaStatus = poaStatus;
	}

	public String getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

//	public Boolean getAppSessionId() {
//		return appSessionId;
//	}
//
//	public void setAppSessionId(Boolean appSessionId) {
//		this.appSessionId = appSessionId;
//	}

	public Integer getLogonStatus() {
		return logonStatus;
	}

	public void setLogonStatus(Integer logonStatus) {
		this.logonStatus = logonStatus;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getDaysToExpire() {
		return daysToExpire;
	}

	public void setDaysToExpire(Integer daysToExpire) {
		this.daysToExpire = daysToExpire;
	}

	public String getErrStr() {
		return errStr;
	}

	public void setErrStr(String errStr) {
		this.errStr = errStr;
	}

	public String getLastLogonTime() {
		return lastLogonTime;
	}

	public void setLastLogonTime(String lastLogonTime) {
		this.lastLogonTime = lastLogonTime;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public Boolean getLogonSuccess() {
		return logonSuccess;
	}

	public void setLogonSuccess(Boolean logonSuccess) {
		this.logonSuccess = logonSuccess;
	}

	public Boolean getSsoEnabled() {
		return ssoEnabled;
	}

	public void setSsoEnabled(Boolean ssoEnabled) {
		this.ssoEnabled = ssoEnabled;
	}

	public String getTransnID() {
		return transnID;
	}

	public void setTransnID(String transnID) {
		this.transnID = transnID;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public String getGtdConfiguration() {
		return gtdConfiguration;
	}

	public void setGtdConfiguration(String gtdConfiguration) {
		this.gtdConfiguration = gtdConfiguration;
	}

	public String getInteractiveWSSPort() {
		return interactiveWSSPort;
	}

	public void setInteractiveWSSPort(String interactiveWSSPort) {
		this.interactiveWSSPort = interactiveWSSPort;
	}

	public String getBroadcastWSSPort() {
		return broadcastWSSPort;
	}

	public void setBroadcastWSSPort(String broadcastWSSPort) {
		this.broadcastWSSPort = broadcastWSSPort;
	}

	public String getGtdAllowedExchanges() {
		return gtdAllowedExchanges;
	}

	public void setGtdAllowedExchanges(String gtdAllowedExchanges) {
		this.gtdAllowedExchanges = gtdAllowedExchanges;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getNsePartCode() {
		return nsePartCode;
	}

	public void setNsePartCode(String nsePartCode) {
		this.nsePartCode = nsePartCode;
	}

	public String getNsefaoPartCode() {
		return nsefaoPartCode;
	}

	public void setNsefaoPartCode(String nsefaoPartCode) {
		this.nsefaoPartCode = nsefaoPartCode;
	}
	
	public String getNsecdsPartCode() {
		return nsecdsPartCode;
	}

	public void setNsecdsPartCode(String nsecdsPartCode) {
		this.nsecdsPartCode = nsecdsPartCode;
	}

	public String getUserProduct() {
		return userProduct;
	}

	public void setUserProduct(String userProduct) {
		this.userProduct = userProduct;
	}

	public String getInteractiveIP() {
		return interactiveIP;
	}

	public void setInteractiveIP(String interactiveIP) {
		this.interactiveIP = interactiveIP;
	}

	public Integer getInteractivePort() {
		return interactivePort;
	}

	public void setInteractivePort(Integer interactivePort) {
		this.interactivePort = interactivePort;
	}

	public Integer getInteractivePort2() {
		return interactivePort2;
	}

	public void setInteractivePort2(Integer interactivePort2) {
		this.interactivePort2 = interactivePort2;
	}

	public String getBroadCastIP() {
		return broadCastIP;
	}

	public void setBroadCastIP(String broadCastIP) {
		this.broadCastIP = broadCastIP;
	}

	public String getBroadCastPort() {
		return broadCastPort;
	}

	public void setBroadCastPort(String broadCastPort) {
		this.broadCastPort = broadCastPort;
	}

	public Integer getPolicyPort() {
		return policyPort;
	}

	public void setPolicyPort(Integer policyPort) {
		this.policyPort = policyPort;
	}

	public Integer getBroadCastPolicyPort() {
		return broadCastPolicyPort;
	}

	public void setBroadCastPolicyPort(Integer broadCastPolicyPort) {
		this.broadCastPolicyPort = broadCastPolicyPort;
	}

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public String getManagerVersion() {
		return managerVersion;
	}

	public void setManagerVersion(String managerVersion) {
		this.managerVersion = managerVersion;
	}

	public Boolean getOdinUser() {
		return odinUser;
	}

	public void setOdinUser(Boolean odinUser) {
		this.odinUser = odinUser;
	}

	public Boolean getLoginThroughOdin() {
		return loginThroughOdin;
	}

	public void setLoginThroughOdin(Boolean loginThroughOdin) {
		this.loginThroughOdin = loginThroughOdin;
	}

	public String getLastLoginIPAddr() {
		return lastLoginIPAddr;
	}

	public void setLastLoginIPAddr(String lastLoginIPAddr) {
		this.lastLoginIPAddr = lastLoginIPAddr;
	}

	public String getLastLoginMACAddr() {
		return lastLoginMACAddr;
	}

	public void setLastLoginMACAddr(String lastLoginMACAddr) {
		this.lastLoginMACAddr = lastLoginMACAddr;
	}

	public String getManagerIP() {
		return managerIP;
	}

	public void setManagerIP(String managerIP) {
		this.managerIP = managerIP;
	}

	public String getAllowedProducts() {
		return allowedProducts;
	}

	public void setAllowedProducts(String allowedProducts) {
		this.allowedProducts = allowedProducts;
	}

	public String getAllowedProductList() {
		return allowedProductList;
	}

	public void setAllowedProductList(String allowedProductList) {
		this.allowedProductList = allowedProductList;
	}

	public String getSmartOrder() {
		return smartOrder;
	}

	public void setSmartOrder(String smartOrder) {
		this.smartOrder = smartOrder;
	}

	public String getSbsBinaryIP() {
		return sbsBinaryIP;
	}

	public void setSbsBinaryIP(String sbsBinaryIP) {
		this.sbsBinaryIP = sbsBinaryIP;
	}

	public Integer getSbsBinaryPort() {
		return sbsBinaryPort;
	}

	public void setSbsBinaryPort(Integer sbsBinaryPort) {
		this.sbsBinaryPort = sbsBinaryPort;
	}

	public String getMultiOC() {
		return multiOC;
	}

	public void setMultiOC(String multiOC) {
		this.multiOC = multiOC;
	}

	public String getNoticeURL() {
		return noticeURL;
	}

	public void setNoticeURL(String noticeURL) {
		this.noticeURL = noticeURL;
	}

	public String getLa() {
		return la;
	}

	public void setLa(String la) {
		this.la = la;
	}

	public String getEa() {
		return ea;
	}

	public void setEa(String ea) {
		this.ea = ea;
	}

	public String getCrpAllowed() {
		return crpAllowed;
	}

	public void setCrpAllowed(String crpAllowed) {
		this.crpAllowed = crpAllowed;
	}

	public Boolean getStreamingAllowed() {
		return streamingAllowed;
	}

	public void setStreamingAllowed(Boolean streamingAllowed) {
		this.streamingAllowed = streamingAllowed;
	}

	public String getDgcxpartcode() {
		return dgcxpartcode;
	}

	public void setDgcxpartcode(String dgcxpartcode) {
		this.dgcxpartcode = dgcxpartcode;
	}

	public String getAdminSqOff() {
		return adminSqOff;
	}

	public void setAdminSqOff(String adminSqOff) {
		this.adminSqOff = adminSqOff;
	}

	public String getMarginPlusAllowed() {
		return marginPlusAllowed;
	}

	public void setMarginPlusAllowed(String marginPlusAllowed) {
		this.marginPlusAllowed = marginPlusAllowed;
	}

	public String getSecondLevelVendorID() {
		return secondLevelVendorID;
	}

	public void setSecondLevelVendorID(String secondLevelVendorID) {
		this.secondLevelVendorID = secondLevelVendorID;
	}

	public Integer getPtn() {
		return ptn;
	}

	public void setPtn(Integer ptn) {
		this.ptn = ptn;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getNewsCategories() {
		return newsCategories;
	}

	public void setNewsCategories(String newsCategories) {
		this.newsCategories = newsCategories;
	}

	public String getManagerTime() {
		return managerTime;
	}

	public void setManagerTime(String managerTime) {
		this.managerTime = managerTime;
	}

	public String getManagerTimeSpanLost() {
		return managerTimeSpanLost;
	}

	public void setManagerTimeSpanLost(String managerTimeSpanLost) {
		this.managerTimeSpanLost = managerTimeSpanLost;
	}

	public Integer getManagerTimeDiff() {
		return managerTimeDiff;
	}

	public void setManagerTimeDiff(Integer managerTimeDiff) {
		this.managerTimeDiff = managerTimeDiff;
	}

	public Integer getMcxsxdqeqperc() {
		return mcxsxdqeqperc;
	}

	public void setMcxsxdqeqperc(Integer mcxsxdqeqperc) {
		this.mcxsxdqeqperc = mcxsxdqeqperc;
	}

	public Integer getMcxsxdqfaoperc() {
		return mcxsxdqfaoperc;
	}

	public void setMcxsxdqfaoperc(Integer mcxsxdqfaoperc) {
		this.mcxsxdqfaoperc = mcxsxdqfaoperc;
	}

	public String getOeproduct() {
		return oeproduct;
	}

	public void setOeproduct(String oeproduct) {
		this.oeproduct = oeproduct;
	}

	public String getMktProt() {
		return mktProt;
	}

	public void setMktProt(String mktProt) {
		this.mktProt = mktProt;
	}

	public Boolean getCol() {
		return col;
	}

	public void setCol(Boolean col) {
		this.col = col;
	}

	public Boolean getjUMPBOTHLTPANDTRIGPRICE() {
		return jUMPBOTHLTPANDTRIGPRICE;
	}

	public void setjUMPBOTHLTPANDTRIGPRICE(Boolean jUMPBOTHLTPANDTRIGPRICE) {
		this.jUMPBOTHLTPANDTRIGPRICE = jUMPBOTHLTPANDTRIGPRICE;
	}

	public Integer getOdinCategory() {
		return odinCategory;
	}

	public void setOdinCategory(Integer odinCategory) {
		this.odinCategory = odinCategory;
	}

	public String getAppSessionId() {
		return appSessionId;
	}

	public void setAppSessionId(String appSessionId) {
		this.appSessionId = appSessionId;
	}

	public String getBsePartType() {
		return bsePartType;
	}

	public void setBsePartType(String bsePartType) {
		this.bsePartType = bsePartType;
	}

	public String getProductTypeBit() {
		return productTypeBit;
	}

	public void setProductTypeBit(String productTypeBit) {
		this.productTypeBit = productTypeBit;
	}

	public String getClientOrdNo() {
		return clientOrdNo;
	}

	public void setClientOrdNo(String clientOrdNo) {
		this.clientOrdNo = clientOrdNo;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

}
