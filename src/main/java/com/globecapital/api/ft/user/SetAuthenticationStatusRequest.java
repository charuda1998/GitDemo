package com.globecapital.api.ft.user;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class SetAuthenticationStatusRequest extends FTRequest {
	
	public SetAuthenticationStatusRequest() throws AppConfigNoKeyFoundException
	{
		super();
	}
	
	public void setLogonTag(String sLogonTag) throws JSONException
	{
		addToData(FTConstants.LOGON_TAG, sLogonTag);
	}
	
	public void setVendorID(String sVendorID) throws JSONException
	{
		addToData(FTConstants.VENDOR_ID, sVendorID);
	}
	

}
