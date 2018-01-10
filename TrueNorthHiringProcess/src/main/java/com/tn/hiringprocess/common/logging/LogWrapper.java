package com.tn.hiringprocess.common.logging;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.RendererSupport;



public class LogWrapper
{


	private static RelativeContextManager contextManager = RelativeContextManager.getInstance(LogWrapper.class.getName());
	private static Map loggers = Collections.synchronizedMap(new HashMap());

	private final String name;
	private Log log;
	private RendererSupport renderer;

	private LogWrapper(String name)
	{
		super();


		this.name = name;
		
		this.log = LogFactory.getLog(name);
		if (this.log instanceof Log4JHierarchyLogger)
		{
			Log4JHierarchyLogger logger = (Log4JHierarchyLogger)this.log;
			this.renderer = logger.getRendererSupport();
		}
		
	}

	public static LogWrapper getLogger(String name)
	{
		if (LogWrapper.loggers.containsKey(name))
		{
			return (LogWrapper)LogWrapper.loggers.get(name);
		}

		LogWrapper log = new LogWrapper(name);
		LogWrapper.loggers.put(name, log);
		return log;
	}

	public static LogWrapper getLogger(Class clazz)
	{
		return LogWrapper.getLogger(clazz.getName());
	}

	public void debug(String msg, Object object)
	{
		if (this.log.isDebugEnabled())
		{
			StringBuilder builder = null;
			Runtime runtime = Runtime.getRuntime();
			if((null != msg) && (null != runtime)){
				builder = new StringBuilder();
				builder.append(msg);
				builder.append("| Free Memory : ");
				builder.append(runtime.freeMemory());
				builder.append(" |");
				msg = builder.toString();
				builder = null;
			}
			this.log.debug(this.createMessage(msg, object));
		}
	}

	public void debug(String msg)
	{
		if (this.log.isDebugEnabled())
		{
			StringBuilder builder = null;
			Runtime runtime = Runtime.getRuntime();
			if((null != msg) && (null != runtime)){
				builder = new StringBuilder();
				builder.append(msg);
				builder.append("| Free Memory : ");
				builder.append(runtime.freeMemory());
				builder.append(" |");
				msg = builder.toString();
				builder = null;
			}
			this.log.debug(this.createMessage(msg));
		}
	}

	public void error(String msg, Object object, Throwable th)
	{
		this.log.error(this.createMessage(msg, object), th);
	}

	public void error(String msg, Object object)
	{
		this.error(msg, object, null);
	}

	public void fatal(String msg, Object object, Throwable th)
	{
		this.log.fatal(this.createMessage(msg, object), th);
	}

	public void fatal(String msg, Object object)
	{
		this.fatal(msg, object, null);
	}

	public void info(String msg, Object object)
	{
		if (this.log.isInfoEnabled())
		{
			this.log.info(this.createMessage(msg, object));
		}
	}

	public boolean isDebugEnabled()
	{
		return this.log.isDebugEnabled();
	}

	public boolean isInfoEnabled()
	{
		return this.log.isInfoEnabled();
	}

	public void warn(String msg, Object object, Throwable th)
	{
		if (this.log.isWarnEnabled())
		{
			this.log.warn(this.createMessage(msg, object), th);
		}
	}

	public void warn(String msg, Object object)
	{
		this.warn(msg, object, null);
	}

	private LogEvent createMessage(String msg, Object object)
	{
		LogEvent event = new LogEvent(this.name, msg);
		if (object != null)
		{
			if (this.renderer != null)
			{
				String renderedObject = this.renderer.getRendererMap().findAndRender(object);
				event.setLogObject(renderedObject);
			}
			else
			{
				event.setLogObject(object.toString());
			}
		}

		if (LogWrapper.contextManager != null)
		{
			event.setContext(LogWrapper.contextManager.retrieveContext());
		}

		return event;
	}

	private LogEvent createMessage(String msg)
	{
		LogEvent event = new LogEvent(this.name, msg);
		if (LogWrapper.contextManager != null)
		{
			event.setContext(LogWrapper.contextManager.retrieveContext());
		}

		return event;
	}

	public static void enableRelativeContext()
	{
		LogWrapper.contextManager = RelativeContextManager.getInstance(LogWrapper.class.getName());
	}

	public static void disableRelativeContext()
	{
		LogWrapper.contextManager = null;
	}

}
