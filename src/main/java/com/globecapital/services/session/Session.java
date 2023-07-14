package com.globecapital.services.session;

import org.json.JSONException;
import org.json.JSONObject;

public class Session {
	
	public static final String USER_STAGE_LOGGED_IN = "L"; // Logged In User
	
	private String sessionID;
	private String appID;
	private String userID;
	private String build;
	private String groupId;
	private String jSessionID;
	private String jSessionIDWithoutEncryption;
	private String jKey;
	private String userType;
	private int clientOrderNo;
	private String is2FAAuthenticated;
	private JSONObject userInfo;
	
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getAppID() {
		return appID;
	}
	public void setAppID(String appID) {
		this.appID = appID;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserId(String userId) {
		this.userID = userId;
	}
	
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/*public String getUserStage() {
		return userStage;
	}
	public void setUserStage(String userStage) {
		this.userStage = userStage;
	}*/
	public String getjSessionID() {
		return jSessionID;
	}
	public void setjSessionID(String jSessionID) {
		this.jSessionID = jSessionID;
	}
	public String getjSessionIDWithoutEncryption() {
		return jSessionIDWithoutEncryption;
	}
	public void setjSessionIDWithoutEncryption(String jSessionIDWithoutEncryption) {
		this.jSessionIDWithoutEncryption = jSessionIDWithoutEncryption;
	}
	public String getjKey() {
		return jKey;
	}
	public void setjKey(String jKey) {
		this.jKey = jKey;
	}
	public static String getUserStageLoggedIn() {
		return USER_STAGE_LOGGED_IN;
	}
	
	public JSONObject getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(JSONObject obj) throws JSONException { // todo
		this.userInfo = obj;
	}
	
	public void setClientOrderNo(int clientOrdNo) {
		this.clientOrderNo = clientOrdNo;
		
	}
	public int getClientOrderNo()
	{
		return clientOrderNo;
	}
	
	public void setIs2FAAuthenticated(String is2FAAuthenticated) {
		this.is2FAAuthenticated = is2FAAuthenticated;
		
	}
	public String getIs2FAAuthenticated()
	{
		return is2FAAuthenticated;
	}
}
