package com.globecapital.api.ft.user;

import com.google.gson.annotations.SerializedName;

public class GenerateOTPResponseObject {

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
	
	@SerializedName("Param6")
    protected String param6;
	

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
	
	public String getParam6() {
        return param6;
    }

    public void setParam6(String param6) {
        this.param6 = param6;
    }

}
