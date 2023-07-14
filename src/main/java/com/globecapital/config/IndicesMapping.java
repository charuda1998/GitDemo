package com.globecapital.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.msf.log.Logger;

public class IndicesMapping {

	static PropertiesConfiguration config = null;
	
	public static Logger log = Logger.getLogger(IndicesMapping.class);

	public static void loadFile(String fileName) throws ConfigurationException {

		config = new PropertiesConfiguration(fileName);
	}
	
	public static String getIndicesScrip(String key) 
	{
		return optValue(key, "--index mapping failed");
	}

	public static String getValue(String key) throws AppConfigNoKeyFoundException {

		String s = config.getString(key);
		if (null == s)
			throw new AppConfigNoKeyFoundException(String.format("'%s' key not found in Indices mapping properties", key));

		return s;
	}

	public static String optValue(String key, String defaultValue) {
		
		return config.getString(key, defaultValue);
	}

}
