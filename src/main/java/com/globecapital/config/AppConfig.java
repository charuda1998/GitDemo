package com.globecapital.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class AppConfig {

	static PropertiesConfiguration config = null;

	public static void loadFile(String fileName) throws ConfigurationException {

		config = new PropertiesConfiguration(fileName);

	}

	public static String getValue(String key) throws AppConfigNoKeyFoundException {
		String s = config.getString(key);
		if (null == s)
			throw new AppConfigNoKeyFoundException(String.format("'%s' key not found in AppConfig", key));

		return s;
	}

	public static int getIntValue(String key) throws AppConfigNoKeyFoundException {

		int s = config.getInt(key, 0);
		if (s == 0)
			throw new AppConfigNoKeyFoundException(String.format("'%s' key not found in AppConfig", key));

		return s;
	}

	public static String[] getArray(String key) throws AppConfigNoKeyFoundException {

		String[] s = config.getStringArray(key);
		if (null == s)
			throw new AppConfigNoKeyFoundException(String.format("'%s' key not found in AppConfig", key));

		return s;
	}

	public static String optValue(String key, String defaultValue) {
		return config.getString(key, defaultValue);
	}

	public static PropertiesConfiguration getProperties() {
		return config;
	}
}
