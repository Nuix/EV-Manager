package com.nuix.evmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/***
 * Custom log4j appender used to listen in on Nuix log events and then forward them on
 * to listeners that register with an instance of this class.
 * @author Jason Wells
 *
 */
public class BroadcastingLogAppender extends AppenderSkeleton {
	private List<LogEventListener> listeners = new ArrayList<LogEventListener>();
	private Level minLevel = Level.ALL;
	
	/***
	 * Registers a listener callback that will be forwarded log events that are greater
	 * than or equal to the configured log level. 
	 * @param listener The listener which will receive log events.
	 */
	public void addListener(LogEventListener listener){ listeners.add(listener); }
	
	/***
	 * Unregisters a callback from receiving log events.
	 * @param listener The listener object to unregister.
	 */
	public void removeListener(LogEventListener listener){ listeners.remove(listener); }
	
	@Override
	public void close() {
		// Disconnect all listeners and unhook from root logger
		listeners = new ArrayList<LogEventListener>();
		Logger.getRootLogger().removeAppender(this);
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent event) {
		if(event.getLevel().isGreaterOrEqual(minLevel)){
			for (LogEventListener listener : listeners) {
				listener.eventLogged(this,event);
			}	
		}
	}
	
	/***
	 * Sets the minimum log level that will be forwarded on to registered listeners.
	 * Class default is all levels.
	 * @param level Minimum level you want broadcast to registered listeners.
	 */
	public void setLevel(Level level){
		minLevel = level;
	}
	
	/***
	 * Adds this instance as an appender to the root logger.  This method needs to be
	 * called to have any log events forwarded on to registered listeners.
	 */
	public void hookLogging(){
		Logger.getRootLogger().addAppender(this);
	}
}
