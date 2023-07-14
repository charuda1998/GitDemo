package com.globecapital.business.wcf.soap;

import java.util.LinkedHashMap;
import java.util.Map;

public class FTResponseHandler {


	public static String handleLogonResponse(String response) throws Exception {
		Map<String, String> loginResponse = parseResponse(response);

		if (loginResponse.get("70").equals("10000") || loginResponse.get("70").equals("10004")
				|| loginResponse.get("70").equals("10006")) {
			String sessionId = loginResponse.get("4");
			return sessionId;
		} else {
			String msg = "Login Failed.";
			if (loginResponse.get("19").length() > 1)
				msg = loginResponse.get("19");
			throw new Exception(msg);
		}
	}

	public static String handleLogOffResponse(String response) {
		Map<String, String> logoffResponse = parseResponse(response);
		String logoffMessage = logoffResponse.get("19");
		return logoffMessage;
	}

	public static String handlePGLimitUpdateResponse(String response) throws Exception {
		Map<String, String> limit = parseResponse(response);
		if (limit.get("18").equals("0"))
			throw new Exception(limit.get("19"));
		String result = null;
		if (limit.get("19") != null)
			result = limit.get("19");
		return result;
	}
	
	public static String getFailureResponse(String response) {
		Map<String, String> limit = parseResponse(response);
		String result = null;
		if (limit.get("19") != null)
			result = limit.get("19");
		return result;
	}

	private static Map<String, String> parseResponse(String request) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String keyValue : request.split(" *\\| *")) {
			String[] pairs = keyValue.split(" *= *", 2);
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
		}
		return map;
	}

}
