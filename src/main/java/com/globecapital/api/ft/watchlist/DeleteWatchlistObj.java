package com.globecapital.api.ft.watchlist;

import com.google.gson.annotations.SerializedName;

public class DeleteWatchlistObj {

	@SerializedName("status")
	protected String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
