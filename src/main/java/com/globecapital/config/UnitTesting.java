package com.globecapital.config;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.msf.log.Logger;

public class UnitTesting {

	private static Logger log = Logger.getLogger(UnitTesting.class);

	private static String responseFileName = "";

	public static void loadResponse(String fileName) {

		responseFileName = fileName;
	}

	public static String getResponse(String reqPath) {

		PropertiesConfiguration responseProp = null;

		try {
			responseProp = new PropertiesConfiguration();
			responseProp.setDelimiterParsingDisabled(true);
			responseProp.load(responseFileName);

		} catch (Exception e) {

			log.warn("Not able to load UnitTest ", e);
		}

		if (responseProp == null)
			return null;

		return responseProp.getString(reqPath);
	}
}
