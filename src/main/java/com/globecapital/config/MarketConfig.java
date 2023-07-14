package com.globecapital.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class MarketConfig {

	static PropertiesConfiguration config = null;

	public static void loadFile(String fileName) throws ConfigurationException {

		config = new PropertiesConfiguration();
		config.setDelimiterParsingDisabled(true);
		config.load(fileName);
	}
	
	public static String getValue(String key) throws AppConfigNoKeyFoundException {

		String s = config.getString(key);
		if (null == s)
			return "";

		return s;
	}

}
