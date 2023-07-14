package com.globecapital.jmx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.configuration.ConfigurationException;

import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.msf.log.Logger;
import com.msf.monitoring.jmx.MonitorRegistrySingleton;
import com.msf.monitoring.jmx.beans.FailureBean;

public class Monitor {
	
	private static Logger log = Logger.getLogger(Monitor.class);
	
	public static final String SCRIPMASTER_DUMP_NSE = "SCRIPMASTER_DUMP_NSE";
	public static final String SCRIPMASTER_DUMP_BSE = "SCRIPMASTER_DUMP_BSE";
	public static final String SCRIPMASTER_DUMP_NFO = "SCRIPMASTER_DUMP_NFO";
	public static final String SCRIPMASTER_DUMP_MCX = "SCRIPMASTER_DUMP_MCX";
	public static final String SCRIPMASTER_DUMP_NCDEX = "SCRIPMASTER_DUMP_NCDEX";
	public static final String SCRIPMASTER_DUMP_NSECDS = "SCRIPMASTER_DUMP_NSECDS";
	public static final String SCRIPMASTER_DUMP_BSECDS = "SCRIPMASTER_DUMP_BSECDS";
	public static final String LOAD_AMO_DETAILS_NSE = "LOAD_AMO_DETAILS_NSE"; 
	public static final String LOAD_AMO_DETAILS_BSE = "LOAD_AMO_DETAILS_BSE";
	public static final String LOAD_AMO_DETAILS_NFO = "LOAD_AMO_DETAILS_NFO";
	public static final String LOAD_AMO_DETAILS_MCX = "LOAD_AMO_DETAILS_MCX";
	public static final String LOAD_AMO_DETAILS_NCDEX = "LOAD_AMO_DETAILS_NCDEX";
	public static final String LOAD_AMO_DETAILS_NSECDS = "LOAD_AMO_DETAILS_NSECDS";
	public static final String LOAD_AMO_DETAILS_BSECDS = "LOAD_AMO_DETAILS_BSECDS";
	public static final String PREDEFINED_WATCHLIST_NSE = "PREDEFINED_WATCHLIST_NSE";
	public static final String PREDEFINED_WATCHLIST_BSE = "PREDEFINED_WATCHLIST_BSE";
	public static final String SENSEX_SYMBOLS = "SENSEX_SYMBOLS";
	public static final String NIFTY50_SYMBOLS = "NIFTY50_SYMBOLS";
	public static final String NIFTY_BANK_SYMBOLS = "NIFTY_BANK_SYMBOLS";
	public static final String NIFTY_PHARMA_SYMBOLS = "NIFTY_PHARMA_SYMBOLS";
	public static final String NIFTY_SMALL100_SYMBOLS = "NIFTY_SMALL100_SYMBOLS";
	public static final String INDICES_NSE = "INDICES_NSE";
	public static final String INDICES_BSE = "INDICES_BSE";
	public static final String INDICES_NFO = "INDICES_NFO";
	public static final String INDICES_NSECDS = "INDICES_NSECDS";
	public static final String INDICES_MCX = "INDICES_MCX";
	public static final String LOAD_NEWS = "LOAD_NEWS";
	public static final String AUDIT_TRANSACTIONS = "AUDIT_TRANSACTIONS";
	
	private static Path path;
	
	// NAGIOS STATUS
	public final static String HEALTH_OK = "OK";

	public final static String HEALTH_CRITICAL = "CRITICAL";

	public final static String HEALTH_WARNING = "WARNING";
	
	public static void setJobsBeans(String beanFile) throws ConfigurationException, AppConfigNoKeyFoundException
	{

		String file = AppConfig.getValue("nagios.beans." + beanFile);
		path = Paths.get(file);
	}

	private static void writeContent(String content)
	{
		try
		{
			Files.write(path, content.getBytes());
		} catch (IOException e)
		{
			log.error(e);
		}
	}

	public static void markWarning(String msg)
	{
		String content = String.format("%s - %s - %s", HEALTH_WARNING, msg, new Date());
		writeContent(content);
	}

	public static void markCritical(String msg)
	{
		String content = String.format("%s - %s - %s", HEALTH_CRITICAL, msg, new Date());
		writeContent(content);
	}

	public static void markSuccess(String msg)
	{
		String content = String.format("%s - %s - %s", HEALTH_OK, msg, new Date());
		writeContent(content);
	}
	
	public static void registerBean(String beanName) throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException, AppConfigNoKeyFoundException {

		FailureBean mkdBean = new FailureBean(beanName);
		ObjectName mkdObjName = new ObjectName(beanName);
		MonitorRegistrySingleton.getInstance().register(mkdObjName, mkdBean, beanName);
	}

	
	public static void release() throws MBeanRegistrationException, InstanceNotFoundException {

		MonitorRegistrySingleton.getInstance().unRegisterAll();
	}

	public static void markFailure(String beanID, String errorMsg) {

		if (beanID == null || beanID.length() == 0)
			return;

		FailureBean bean = (FailureBean) MonitorRegistrySingleton.getInstance().getMXBean(beanID);

		if (bean != null) {
			
			bean.incrementFailCount();
			bean.setFailure(errorMsg);
		}
	}

}
