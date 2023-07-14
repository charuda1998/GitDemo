package com.globecapital.jmx;

import com.googlecode.jsendnsca.Level;
import com.googlecode.jsendnsca.MessagePayload;

public class JMXBean extends MessagePayload{
	
	private static final long serialVersionUID = 1L;

	public static final Level LEVEL_CRITICAL = Level.CRITICAL;
	public static final Level LEVEL_WARNING = Level.WARNING;
	public static final Level LEVEL_OK = Level.OK;
	public static final Level LEVEL_UNKNOWN = Level.UNKNOWN;

	private boolean isStatsEnabled = false;

	public void enableStats() {
		this.isStatsEnabled = true;
	}

	public boolean isBeanStatsEnabled() {
		return this.isStatsEnabled;
	}

}
