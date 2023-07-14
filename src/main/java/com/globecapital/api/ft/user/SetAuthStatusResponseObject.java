package com.globecapital.api.ft.user;

import com.google.gson.annotations.SerializedName;

public class SetAuthStatusResponseObject {
	
	@SerializedName("login2FA")
	protected String login2FA;
	
	@SerializedName("transaction2FA")
	protected String transaction2FA;
	
	@SerializedName("isCipherEnabled")
    protected String isOTPEnabled;
	
	public void setLogin2FA(String login2FA)
	{
		this.login2FA = login2FA;
	}
	
	public String getLogin2FA()
	{
		return login2FA;
	}
	
	public void setTransaction2FA(String transaction2FA)
	{
		this.transaction2FA = transaction2FA;
	}
	
	public String getTransaction2FA()
	{
		return transaction2FA;
	}
	
	public void setOTPEnabled(String isOTPEnabled)
    {
        this.isOTPEnabled = isOTPEnabled;
    }
    
    public String getOTPEnabled()
    {
        return isOTPEnabled;
    }
}
