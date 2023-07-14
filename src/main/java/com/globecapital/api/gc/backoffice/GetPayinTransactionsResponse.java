package com.globecapital.api.gc.backoffice;

import com.globecapital.api.razorpay.generics.RazorPayResponse;
import com.google.gson.annotations.SerializedName;

public class GetPayinTransactionsResponse extends RazorPayResponse {

	@SerializedName("FundPayinResponse")
	protected GetPayInTxnRows Details;

}