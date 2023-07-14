package com.globecapital.audit;

import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.msf.log.Logger;

public class GCAuditObject {
	
	public static Logger log = Logger.getLogger(GCAuditObject.class);
	
	private GCRequest gcRequest;
	private GCResponse gcResponse;
	
	private boolean needAudit = false;
	
	private String msgID;
	private String svcGroup;
	private String svcName;
	private String svcVersion;
	private String infoID;
	private String infoMsg;
	private String appID;
	private String userName;
	private String userType;
	private String apiTime;
	private String reqTime;
	private String respTime;
	private String srcIP;
	
	public GCAuditObject( ) {

	}
	
	public boolean needAudit()
	{
		return this.needAudit;
	}
	
	public void setNeedAudit( boolean needAudit)
	{
		this.needAudit = needAudit;
	}

	public String getMsgID() {
		return msgID;
	}
	
	public void setMsgID(String msgId) {
		this.msgID = msgId;
	}
	
	public String getSvcGroup() {
		return svcGroup;
	}
	
	public void setSvcGroup(String svcGroup) {
		this.svcGroup = svcGroup;
	}
	
	public String getSvcName() {
		return svcName;
	}
	
	public void setSvcName(String svcName) {
		this.svcName = svcName;
	}
	
	public String getSvcVersion() {
		return svcVersion;
	}
	
	public void setSvcVersion(String svcVersion) {
		this.svcVersion = svcVersion;
	}
	
	public String getInfoID() {
		return infoID;
	}
	
	public void setInfoID(String infoId) {
		this.infoID = infoId;
	}
	
	public String getInfoMsg() {
		return infoMsg;
	}
	
	public void setInfoMsg(String infoMsg) {
		this.infoMsg = infoMsg;
	}
	
	public String getAppID() {
		return appID;
	}
	
	public void setAppID(String appId) {
		this.appID = appId;
	}
	
	public String getUsername() {
		return userName;
	}
	
	public void setUsername(String username) {
		this.userName = username;
	}
	
	public String getUsertype() {
		return userType;
	}
	
	public void setUsertype(String usertype) {
		this.userType = usertype;
	}
	
	public String getAPITime() {
		return apiTime;
	}
	
	public void setAPITime(String apiTime) {
		this.apiTime = apiTime;
	}
	
	public String getReqTime() {
		return reqTime;
	}
	
	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}
	
	public String getRespTime() {
		return respTime;
	}
	
	public void setRespTime(String respTime) {
		this.respTime = respTime;
	}
	
	public String getSrcIP() {
		return srcIP;
	}
	
	public void setSrcIP(String srcIP) {
		this.srcIP = srcIP;
	}
	
	public void setAuditInfo(Session session) {
		try {
			this.setUsername(session.getUserID());
			this.setUsertype(session.getUserType());
			this.setNeedAudit(true);
		} catch (Exception e) {
			log.info("Exception in Audit : " + e);
		}
	}
}
