package com.globecapital.api.ft.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.config.InfoMessage;
import com.globecapital.services.session.Session;
import com.google.gson.annotations.SerializedName;

public class LoginResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected LoginResponseObject responseObject;
	
	public static final String PASSWORD_EXPIRED = "PASSWORD_EXPIRED";
	public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
	public static final String ACCOUNT_BLOCKED = "ACCOUNT_BLOCKED";
	public static final String INVALID_PASSSWORD = "INVALID_PASSWORD";
	public static final String INVALID_USER = "INVALID_USER";
	public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
	public static final String PWD_VALIDATION_FAILED = "PWD_VALIDATION_FAILED";
	public static final String INVALID_LICENSE = "INVALID_LICENSE";
	
	private String userStatus;
	
	private String errorMsg;
	
	private JSONObject participantObj;
	
	private String sUserID;
	
	private Session session;
	
	private JSONObject loginObj;
	
	private JSONArray productList,allowedGTDList;
	
	LoginResponse()
	{
		this.loginObj = new JSONObject();
	}

	public LoginResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(LoginResponseObject responseObject) {
		this.responseObject = responseObject;
	}

	public JSONObject getParticipantIDList()
	{
		return this.participantObj;
	}
	
	public void setParticipantIDList(JSONObject obj)
	{
		this.participantObj = obj;
	}
	
	public JSONArray getProductList()
	{
		return this.productList;
	}
	
	public void setProductList(JSONArray arr)
	{
		this.productList = arr;
	}
	
	public void setUserStatus(String status) {
		this.userStatus = status;
	}

	public String getUserStatus() {

		return this.userStatus;
	}
	
	public boolean isAccBlockedOrExpired() {
		if (this.userStatus.equals(ACCOUNT_BLOCKED) || this.userStatus.equals(PASSWORD_EXPIRED))
			return true;
		else
			return false;
	}
	
	public boolean isLogonSucccess() {

		if (this.userStatus.equals(LOGIN_SUCCESS))
			return true;
		else
			return false;
	}
	
	public void setErrorMsg(String sErrorMsg)
	{
		this.errorMsg = sErrorMsg;
	}
	
	public String getErrorMsg()
	{
		return this.errorMsg;
	}

	public void setUserID(String sUserID)
	{
		this.sUserID = sUserID;
	}
	
	public String getUserID()
	{
		return this.sUserID;
	}
	
	public void setSession(Session session)
	{
		this.session = session;
	}
	
	public Session getSession()
	{
		return this.session;
	}
	
	public void addToLoginObj(String key, Object value) throws JSONException {
		this.loginObj.put(key, value);
	}
	
	public JSONObject getLoginObj()
	{
		return this.loginObj;
	}
	
	/*public JSONObject getFinalLoginObj()
	{
		loginObj.remove(UserInfoConstants.PRODUCT_TYPE); // Get product and market seg ids info from the GerOrderPadDetails service
		loginObj.remove(UserInfoConstants.ALLOWED_MKT_SEG_IDS);
		return this.loginObj;
	}*/
	
	public String getRemark() {
		return InfoMessage.getInfoMSG("info_msg.invalid.login." + this.userStatus);
	}
	
	public JSONArray getGTDAllowed()
	{
		return this.allowedGTDList;
	}
	
	public void setGTDAllowed(JSONArray arr)
	{
		this.allowedGTDList = arr;
	}
}
