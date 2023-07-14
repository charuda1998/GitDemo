package com.globecapital.api.ft.watchlist;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetSymbolWatchlistObject {

	@SerializedName("Table1")
	protected List<GetSymbolWatchlistObjTable1> table1;

	@SerializedName("Table2")
	protected List<GetSymbolWatchlistObjTable2> table2;

	public List<GetSymbolWatchlistObjTable1> getTable1() {
		return table1;
	}

	public void setTable1(List<GetSymbolWatchlistObjTable1> table1) {
		this.table1 = table1;
	}

	public List<GetSymbolWatchlistObjTable2> getTable2() {
		return table2;
	}

	public void setTable2(List<GetSymbolWatchlistObjTable2> table2) {
		this.table2 = table2;
	}
	
	
}
