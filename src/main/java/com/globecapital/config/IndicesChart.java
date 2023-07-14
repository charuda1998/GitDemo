package com.globecapital.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.msf.log.Logger;

public class IndicesChart {

	static PropertiesConfiguration config = null;
	
	public static Logger log = Logger.getLogger(IndicesChart.class);

	public static void loadFile(String fileName) throws ConfigurationException {

		config = new PropertiesConfiguration(fileName);
	}
	
	public static String getIndicesScrip(String key) 
	{
		return optValue(key, "--spyder scrip not found");
	}

	public static String getValue(String key) throws AppConfigNoKeyFoundException {

		String s = config.getString(key);
		if (null == s)
			throw new AppConfigNoKeyFoundException(String.format("'%s' key not found in Indices chart", key));

		return s;
	}

	public static String optValue(String key, String defaultValue) {
		log.info("In Opt value indices key:" + key);
		log.debug("In Opt value indices key:" + key);
		
		return config.getString(key, defaultValue);
	}

}
