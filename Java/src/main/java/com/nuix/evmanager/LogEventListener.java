package com.nuix.evmanager;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;

/***
 * Callback for listening receiving log events from {@link BroadcastingLogAppender}
 * @author Jason Wells
 *
 */
public interface LogEventListener {
	public void eventLogged(Appender source, LoggingEvent event);
}
