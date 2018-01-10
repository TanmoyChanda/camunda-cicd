package com.tn.hiringprocess.common.logging;
//*********************************************************
// NOTICE: All rights reserved. This material contains the
// trade secrets and confidential information of
// PriceWaterhouseCoopers which embody substantial creative
// effort,ideas and expressions. No part of this material may
// be reproduced or transmitted in any form or by any means,
// electronic, mechanical, optical or otherwise, including
// photocopying and recording or in connection with any
// information storage or retrieval system, without
// specific written permission from PriceWaterhouseCoopers.
// Copyright PriceWaterhouseCoopers 2003-2010. All rights
// reserved.
//*********************************************************


import org.apache.commons.logging.Log;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;

/**
 * A specific implementation of the <strong>org.apache.commons.logging.Log</strong> interface.
 * This implementation is a wrapper around a log4j Logger. All the methods in this class
 * delegate to the undelying log4j Logger instance
 *
 * @author Kousik Maiti
 * @version 1.0
 */

public class Log4JHierarchyLogger implements Log
{
	// ------------------------------------------------------------- Attributes

	/**
	 * The fully qualified name of the Log4JLogger class.
	 */
	private static final String FQCN = Log4JHierarchyLogger.class.getName();

	/**
	 * Log to this logger
	 */
	private Logger logger = null;


	// ------------------------------------------------------------ Constructor

	public Log4JHierarchyLogger ()
	{
	}


	/**
	 * Base constructor.
	 * @param hierarchy the hierarchy ths is used to create a logger using <strong>name</strong> as the logger
	 * name
	 * @param name the name of the logger to create
	 */
	public Log4JHierarchyLogger (Hierarchy hierarchy, String name)
	{
		this.logger = hierarchy.getLogger(name);
	}



	// ---------------------------------------------------------- Implmentation


	/**
	 * Log a message to the Log4j Logger with <code>TRACE</code> priority.
	 * Currently logs to <code>DEBUG</code> level in Log4J.
	 */
	@Override
	public void trace (Object message)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.DEBUG, message, null);
	}


	/**
	 * Log an error to the Log4j Logger with <code>TRACE</code> priority.
	 * Currently logs to <code>DEBUG</code> level in Log4J.
	 */
	@Override
	public void trace (Object message, Throwable t)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.DEBUG, message, t);
	}


	/**
	 * Log a message to the Log4j Logger with <code>DEBUG</code> priority.
	 */
	@Override
	public void debug (Object message)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.DEBUG, message, null);
	}

	/**
	 * Log an error to the Log4j Logger with <code>DEBUG</code> priority.
	 */
	@Override
	public void debug (Object message, Throwable t)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.DEBUG, message, t);
	}


	/**
	 * Log a message to the Log4j Logger with <code>INFO</code> priority.
	 */
	@Override
	public void info (Object message)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.INFO, message, null);
	}


	/**
	 * Log an error to the Log4j Logger with <code>INFO</code> priority.
	 */
	@Override
	public void info (Object message, Throwable t)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.INFO, message, t);
	}


	/**
	 * Log a message to the Log4j Logger with <code>WARN</code> priority.
	 */
	@Override
	public void warn (Object message)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.WARN, message, null);
	}


	/**
	 * Log an error to the Log4j Logger with <code>WARN</code> priority.
	 */
	@Override
	public void warn (Object message, Throwable t)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.WARN, message, t);
	}


	/**
	 * Log a message to the Log4j Logger with <code>ERROR</code> priority.
	 */
	@Override
	public void error (Object message)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.ERROR, message, null);
	}


	/**
	 * Log an error to the Log4j Logger with <code>ERROR</code> priority.
	 */
	@Override
	public void error (Object message, Throwable t)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.ERROR, message, t);
	}


	/**
	 * Log a message to the Log4j Logger with <code>FATAL</code> priority.
	 */
	@Override
	public void fatal (Object message)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.FATAL, message, null);
	}


	/**
	 * Log an error to the Log4j Logger with <code>FATAL</code> priority.
	 */
	@Override
	public void fatal (Object message, Throwable t)
	{
		this.logger.log(Log4JHierarchyLogger.FQCN, Level.FATAL, message, t);
	}


	/**
	 * Return the native Logger instance we are using.
	 */
	public Logger getLogger ()
	{
		return (this.logger);
	}


	/**
	 * Check whether the Log4j Logger used is enabled for <code>DEBUG</code> priority.
	 */
	@Override
	public boolean isDebugEnabled ()
	{
		return this.logger.isDebugEnabled();
	}


	/**
	 * Check whether the Log4j Logger used is enabled for <code>ERROR</code> priority.
	 */
	@Override
	public boolean isErrorEnabled ()
	{
		return this.logger.isEnabledFor(Level.ERROR);
	}


	/**
	 * Check whether the Log4j Logger used is enabled for <code>FATAL</code> priority.
	 */
	@Override
	public boolean isFatalEnabled ()
	{
		return this.logger.isEnabledFor(Level.FATAL);
	}


	/**
	 * Check whether the Log4j Logger used is enabled for <code>INFO</code> priority.
	 */
	@Override
	public boolean isInfoEnabled ()
	{
		return this.logger.isInfoEnabled();
	}


	/**
	 * Check whether the Log4j Logger used is enabled for <code>TRACE</code> priority.
	 * For Log4J, this returns the value of <code>isDebugEnabled()</code>
	 */
	@Override
	public boolean isTraceEnabled ()
	{
		return this.logger.isDebugEnabled();
	}

	/**
	 * Check whether the Log4j Logger used is enabled for <code>WARN</code> priority.
	 */
	@Override
	public boolean isWarnEnabled ()
	{
		return this.logger.isEnabledFor(Level.WARN);
	}


	public RendererSupport getRendererSupport() {
		LoggerRepository repository = this.logger.getLoggerRepository();
		if (repository instanceof RendererSupport)
		{
			return (RendererSupport)repository;
		}
		return null;
	}
}
