package com.globecapital.api.ft.watchlist;

import com.google.gson.annotations.SerializedName;

public class RenameWatchlistObj {
	
	@SerializedName("status")
	protected String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
