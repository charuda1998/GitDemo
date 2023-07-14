package com.globecapital.api.ft.user;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.order.GetTradeBookObjectRow;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.session.Session;
import com.google.gson.annotations.SerializedName;

public class GenerateOTPResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected List<GenerateOTPResponseObject> responseObject;
	
	
	public boolean isSuccessResponse() {
	    
	    if(this.responseObject.get(0).param4==null ? false : this.responseObject.get(0).param4.contains(DeviceConstants.SUCCESS_OTP_MSG))
	        return true;
	    else if(this.responseObject.get(0).param5==null ? false : this.responseObject.get(0).param5.contains(DeviceConstants.SUCCESS_OTP_MSG))
            return true;
        else if(this.responseObject.get(0).param6==null ? false : this.responseObject.get(0).param6.contains(DeviceConstants.SUCCESS_OTP_MSG))
            return true;
        else if(this.responseObject.get(0).param1==null ? false : this.responseObject.get(0).param1.contains(DeviceConstants.SUCCESS_OTP_MSG))
            return true;
        else
	        return false;
	}
	
	public String getErrorMsg() {
	    if( this.responseObject.get(0).param2==null )
	        return DeviceConstants.FAILED_OTP_MSG;
	    else
	        return this.responseObject.get(0).param2;
	}
	
}
