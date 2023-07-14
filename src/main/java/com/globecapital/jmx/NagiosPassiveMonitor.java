package com.globecapital.jmx;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.globecapital.constants.InfoIDConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.exception.GCException;
import com.googlecode.jsendnsca.Level;
import com.googlecode.jsendnsca.NagiosException;
import com.googlecode.jsendnsca.NagiosPassiveCheckSender;
import com.googlecode.jsendnsca.NagiosSettings;
import com.googlecode.jsendnsca.encryption.Encryption;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class NagiosPassiveMonitor {
	
	public static Logger log = Logger.getLogger(NagiosPassiveMonitor.class);
	
	private static NagiosPassiveCheckSender sender = null;
	private static boolean isStatsEnabled = false;
	public static boolean isMonitorEnabled = true;
	private static HashMap<String, JMXBean> beansHistoryMap = new HashMap<String, JMXBean>();
	
	public static final void register(PropertiesConfiguration properties) throws GCException {

		int port = properties.getInt("nagios.nsca.port", 0);
		if (port == 0)
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, "port not specified");

		String host = properties.getString("nagios.nsca.host");
		if (host.isEmpty())
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, "host not specified");

		NagiosSettings settings = new NagiosSettings();
		settings.setPort(port);
		settings.setNagiosHost(host);

		String encryption = properties.getString("nagios.nsca.encryption", "NONE");
		if (encryption.equalsIgnoreCase("TRIPLE_DES"))
			settings.setEncryption(Encryption.TRIPLE_DES);
		else if (encryption.equalsIgnoreCase("XOR"))
			settings.setEncryption(Encryption.XOR);
		else
			settings.setEncryption(Encryption.NONE);

		sender = new NagiosPassiveCheckSender(settings);

		isStatsEnabled = properties.getBoolean("nagios.nsca.monitor.stats", false);

	}
	
	private static String getMonitorCount(JMXBean bean) {

		Connection conn = null;
		CallableStatement st = null;

		try {
			conn = GCDBPool.getInstance().getConnection();
			st = conn.prepareCall("{call get_monitor_count(?,?,?,?,?)}");

			int success = 0, failure = 0;
			if (bean.getLevel().equals(Level.OK))
				success = 1;
			else
				failure = 1;

			st.setString(1, bean.getServiceName());
			st.setInt(2, success);
			st.setInt(3, failure);

			st.registerOutParameter(4, Types.INTEGER);
			st.registerOutParameter(5, Types.INTEGER);

			st.execute();

			String msg = "SUCCESS = " + st.getInt(4) + " FAILURE = " + st.getInt(5);
			return msg;

		} catch (SQLException ex) {
			log.error("Monitor count not updated", ex);
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}

		return "";
	}
	
	
	private static final boolean verifyBeanHistory(JMXBean bean) {

		String beanName = bean.getClass().getCanonicalName();
		JMXBean historybean = beansHistoryMap.get(beanName);
		if (historybean != null) {
			if (historybean.getLevel().equals(bean.getLevel()))
				return false;
		}

		beansHistoryMap.put(beanName, bean);

		return true;
	}
	
	
	public static boolean markStatus(JMXBean bean) throws GCException {

		if (!isMonitorEnabled)
			return false;

		if (sender == null)
			throw new GCException(InfoIDConstants.CONNECT_FAILED, "Monitor not registered");

		if (bean.getServiceName() == null || bean.getServiceName().equals("UNDEFINED"))
			return false;
		// throw new SBU2Exception(INFO_IDS.SERVICE_MONITOR_NOT_FOUND, "Service name not
		// found");

		String stats = "";
		if (isStatsEnabled && bean.isBeanStatsEnabled())
			stats = getMonitorCount(bean); // enable it for monitoring stats

		String statsMsg = String.format("marked %s with level as %s, STATS - %s", bean.getServiceName(),
				bean.getLevel(), stats);
		log.info(statsMsg);

		if (verifyBeanHistory(bean)) {
			try {
				bean.setMessage(statsMsg);
				sender.send(bean);
			} catch (NagiosException | IOException e) {
				log.error(e);
				throw new GCException(InfoIDConstants.CONNECT_FAILED, "NSCA Connection failed");
			}
			return true;
		}

		return false;

	}

}
