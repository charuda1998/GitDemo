package com.globecapital.symbology;

import org.json.JSONObject;

public class Symbol extends JSONObject {
	public Symbol() {
		// TODO Auto-generated constructor stub
	}

	public Symbol(JSONObject symBlock) {
		String[] fields = JSONObject.getNames(symBlock);
		if (fields == null)
			return;

		for (String f : fields) {
			this.put(f, symBlock.get(f));
		}
	}
}
