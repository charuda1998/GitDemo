package com.globecapital.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class InfoMessage {

	static PropertiesConfiguration config = null;

	public static void loadFile(String fileName) throws ConfigurationException {

		config = new PropertiesConfiguration(fileName);
	}
	
	public static String getInfoMSG(String key) 
	{
		return optValue(key, "--info-message not found");
	}

	public static String getValue(String key) throws AppConfigNoKeyFoundException {

		String s = config.getString(key);
		if (null == s)
			throw new AppConfigNoKeyFoundException(String.format("'%s' key not found in InfoMessage", key));

		return s;
	}

	public static String optValue(String key, String defaultValue) {
		return config.getString(key, defaultValue);
	}

}
