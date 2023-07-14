package com.globecapital.business.wcf.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.globecapital.constants.WCFConstants;
import com.globecapital.business.wcf.soap.SoapReqResHandler;

public class WCFHelper {

	public static String generateCheckSum(char[] buf, long bufLen) {
		try {
			char[] tmpBuf = new char[4];
			Integer idx;
			Integer cks;

			for (idx = 0, cks = 0; idx < bufLen; cks += (int) buf[idx++])
				;
			cks = cks % 256;
			return cks.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getCurrentDateTime() {
		String date = null;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyHHmmss");
		LocalDateTime now = LocalDateTime.now();
		date = dtf.format(now);
		return date;
	}

	public static void setElements(String soapAction) {

		if (soapAction.contains("Login")) {
			SoapReqResHandler.element1 = WCFConstants.LOGIN_ELEMENT1;
			SoapReqResHandler.element2 = WCFConstants.LOGIN_ELEMENT2;
			SoapReqResHandler.responseElement = WCFConstants.LOGIN_RESPONSE_ELEMENT;
		} else if (soapAction.contains("UpdatePGLimits")) {
			SoapReqResHandler.element1 = WCFConstants.PGUPDATE_ELEMENT1;
			SoapReqResHandler.element2 = WCFConstants.PGUPDATE_ELEMENT2;
			SoapReqResHandler.responseElement = WCFConstants.PGUPDATE_RESPONSE_ELEMENT;
		} else if (soapAction.contains("LogOff")) {
			SoapReqResHandler.element1 = WCFConstants.LOGOFF_ELEMENT1;
			SoapReqResHandler.element2 = WCFConstants.LOGOFF_ELEMENT2;
			SoapReqResHandler.responseElement = WCFConstants.LOGOFF_RESPONSE_ELEMENT;
		}
	}
}
