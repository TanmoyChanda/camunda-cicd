package com.tn.hiringprocess.common.logging;




import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * <p/>Specific implementation of the commons-logging factory abstract class
 * <strong>org.apache.commons.logging.LogFactory</strong>. Unlike the default factory implementation, which
 * can integrate with log4j, or java.util.logging, this class only integrates with log4j.
 * <p/>
 * <p/>The default commons-loggin factory implementation uses log4j to create loggers using log4j's default hierarchy.
 * However, this factory class creates and configures a new log4j <strong>Hierarchy</strong>, and uses
 * <strong>appLog4j.xml</strong>, or <strong>appLog4j.properties</strong> (in that order) to configure
 * the loggers in the new hierarcy (please refer to the
 * javadocs on  <stron>org.apache.log4j.Hierarchy</strong>). The configuration files are located using the
 * Thread Context Classloader (TCL). Every time the TCL locates a new configuration file, a new hierarchy is
 * created for serving loggers.
 * <p/>
 * <p/> To configure commons-logging to use this factory class instead of the default
 * <strong>org.apache.commons.logging.LogFactory</strong> factory, the following 2 properties need to be set:
 * <ul>
 * <li/><strong>org.apache.commons.logging.LogFactory</strong>=com.ssc.wb.common.util.log.Log4JFactoryImpl
 * <li/><strong>org.apache.commons.logging.Log</strong>=com.ssc.wb.common.util.log.Log4JHierarchyLogger
 * </ul>
 * <p/>
 * <p/> The above 2 properties can be specified as System properties or they can be specified in
 * a <strong>commons-logging.properties</strong> file.
 * <p/>
 * <p/><strong>Implementation note:</strong> This factory class works with older version of log4j as well. It has been tested
 * with log4j-1.1.3.
 *
 * @author PwCI
 * @version 1.0
 */
public class Log4JFactoryImpl extends LogFactory
{
	/**
	 * The name of the system property identifying our {@link org.apache.commons.logging.Log}
	 * implementation class.
	 */
	public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";

	/**
	 * The configuration file to confiugre the root logger, or root category
	 * of a hierarchy. This file will only be used to configure log4j if
	 * {@link #LOG4J_CONFIGURATION_XML_FILE} cannot be found
	 */
	public static final String LOG4J_CONFIGURATION_FILE = "appLog4j.properties";

	/**
	 * The configuration file to confiugre the root logger, or root category
	 * of a hierarchy.
	 */
	public static final String LOG4J_CONFIGURATION_XML_FILE = "appLog4j.xml";

	/**
	 * Key = URL of the configuration file, Value =  Hierarchy. Every time a new configuration file
	 * is found on the classpath, a new Hierarchy object is created, and configured using the configuration
	 * file.
	 */
	protected static Hashtable hierarchies = new Hashtable();


	/**
	 * If no configuration file is found, an appenderless hierarchy is created. Any loggers created
	 * using this hierarchy will not have their output logged
	 */
	protected static Hierarchy appenderlessHierarchy = null;

	/**
	 * The older Log4J Category logger class. In version 1.1.3 of log4j, there was no Logger class
	 */
	protected static String CATEGORY_LOGGER = "org.apache.log4j.Category";

	/**
	 * The newer Log4J logger class
	 */
	protected static String LOGGER_LOGGER = "org.apache.log4j.Logger";

	// ----------------------------------------------------- Instance Variables

	/**
	 * Configuration attributes
	 */
	protected Hashtable attributes = new Hashtable();

	/**
	 * The <strong>org.apache.commons.logging.Log</strong> instances that have
	 * already been created, keyed by logger name. All the loggers in this hashtable are created
	 * using {@link #hierarchy}
	 */
	protected Hashtable instances = new Hashtable();

	//protected Hierarchy hierarchy = null;

	/**
	 * The hierarchy associated with this factory. All loggers requested from this factory
	 * are created using this hierarchy
	 */
	protected Hierarchy hierarchy = null;

	/**
	 * Name of the class implementing the Log interface. This is a wrapper class that delegates
	 * the actual logging to log4j
	 * This class is specified by the <strong>org.apache.commons.logging.Log</strong> property
	 */
	private String logClassName;


	/**
	 * The two-argument constructor of the
	 * {@link #logClassName} implementation class that will be used to create new instances.
	 * This value is initialized by {@link #getLogConstructor()} and then returned repeatedly.
	 */
	protected Constructor logConstructor = null;


	/**
	 * The signature of {@link #logConstructor}to be used to initialize
	 * an implementation of the {@link org.apache.commons.logging.Log}
	 * class
	 */
	protected Class logConstructorSignature[] = {org.apache.log4j.Hierarchy.class, java.lang.String.class};


	/**
	 * Hierarchy constructor. In log4j-1.1.3, the hierarchy constructor is <strong>Hierarchy(Category)</strong>,
	 * whereas in later version of log4j, the constructor is specified as <strong>Hierarchy(Logger)</strong>
	 */
	protected Constructor hierarchyConstructor = null;

	/**
	 * One of <strong>org.apache.log4j.Logger.class</strong> or
	 * <strong>org.apache.log4j.Category.class</strong>
	 */
	protected Class log4JClass = null;


	/**
	 * Invoke super (), and create a new hierarchy if the TCL finds a new configuration file on the TCL's
	 * classpath. The first configuration file to be searched for is: {@link #LOG4J_CONFIGURATION_XML_FILE}.
	 * Failing that, the configuration file {@link #LOG4J_CONFIGURATION_FILE} is located. In either case,
	 * only one of these files is used to configure the new hierarchy.
	 * <ul>
	 * <li> If a new configuration file is found, create a new hierarchy, and configure it using the new
	 * configuration file. All loggers will be created using this hierarchy
	 * <li> If a configuration file is found, and a hierarchy has already been configured using that file,
	 * reuse the hierarchy to create loggers.
	 * <li>If no configuration file is found, use the default appenderless hierarchy. The appenderless hierarchy
	 * creates loggers that log output to a null appender -- that is, the output is lost
	 * </ul>
	 *
	 * @throws LogConfigurationException thrown if any exception is encountered during initialization
	 */
	public Log4JFactoryImpl () throws LogConfigurationException
	{
		super();
		createHierarchy();
	}

	/**
	 * Create a new hierarchy. To understand when a new hierarchy is created,
	 * refer to {@link #Log4JFactoryImpl()}
	 *
	 * @throws LogConfigurationException
	 */
	protected void createHierarchy () throws LogConfigurationException
	{
		URL appLog4jConfig = null;

		if (this.hierarchy == null)
		{
			appLog4jConfig = getConfigFile();

			if (appLog4jConfig == null)
			{
				createAppenderlessHierarchy();
			}
			else
			{
				createHierarchy(appLog4jConfig);
			}
		}
	}

	/**
	 * Return the url of the configuration file. The search for the configuration file involves the following:
	 * <ul>
	 * <li> Search for {@link #LOG4J_CONFIGURATION_XML_FILE}. If a file is found, return the url
	 * <li> Search for {@link #LOG4J_CONFIGURATION_FILE}. If a file is found, return the url
	 * </ul>
	 * The above files are searched using the TCL
	 *
	 * @return URL of the configuration file, or null if a file is not found
	 * @throws LogConfigurationException
	 */
	protected URL getConfigFile () throws LogConfigurationException
	{

		URL appLog4jConfig = Log4JFactoryImpl.getResource(LogFactory.getContextClassLoader(), Log4JFactoryImpl.LOG4J_CONFIGURATION_XML_FILE);

		if (appLog4jConfig == null)
		{
			appLog4jConfig = Log4JFactoryImpl.getResource(LogFactory.getContextClassLoader(), Log4JFactoryImpl.LOG4J_CONFIGURATION_FILE);
		}

		return appLog4jConfig;
	}

	/**
	 * This method creates a default hierarchy, whose root logger is not configured with any appenders.
	 * This hierarchy is created, when a configuration file to configure the root logger in a hierarchy cannot
	 * be found.
	 *
	 * @throws LogConfigurationException
	 */
	protected void createAppenderlessHierarchy () throws LogConfigurationException
	{
		if (Log4JFactoryImpl.appenderlessHierarchy == null)
		{
			Log4JFactoryImpl.appenderlessHierarchy = createLog4JHierarchy();
			Category rootCategory = Log4JFactoryImpl.appenderlessHierarchy.getRootLogger();
			rootCategory.addAppender(new ConsoleAppender());
			System.out.println("Creating a null hierarchy");
		}

		this.hierarchy = Log4JFactoryImpl.appenderlessHierarchy;
		System.out.println("No config file found...assigning null hierarchy");

	}

	/**
	 * Helper method to {@link #createHierarchy()}. Create a new hierarchy, whose root logger/Category is
	 * configured using <strong>configFile</strong>
	 *
	 * @param configFile
	 * @throws LogConfigurationException
	 */
	protected void createHierarchy (URL configFile) throws LogConfigurationException
	{
		boolean isXmlConfigFile = true;

		if (!configFile.getFile().endsWith(".xml"))
		{
			isXmlConfigFile = false;
		}

		synchronized (Log4JFactoryImpl.hierarchies)
		{
			if (Log4JFactoryImpl.hierarchies.containsKey(configFile))
			{
				this.hierarchy = (Hierarchy) Log4JFactoryImpl.hierarchies.get(configFile);
			}
			else
			{
				this.hierarchy = createLog4JHierarchy();
				Log4JFactoryImpl.hierarchies.put(configFile, this.hierarchy);

				if (isXmlConfigFile)
				{
					new DOMConfigurator().doConfigure(configFile, this.hierarchy);
				}
				else
				{
					new PropertyConfigurator().doConfigure(configFile, this.hierarchy);
				}

				System.out.println("Created new hierarchy");
				System.out.println("Configured with " + configFile);
			}

		}

	}


	/**
	 * Return the configuration attribute with the specified name (if any),
	 * or <code>null</code> if there is no such attribute.
	 *
	 * @param name Name of the attribute to return
	 */
	@Override
	public Object getAttribute (String name)
	{
		return (this.attributes.get(name));
	}

	/**
	 * Return an array containing the names of all currently defined
	 * configuration attributes.  If there are no such attributes, a zero
	 * length array is returned.
	 */
	@Override
	public String[] getAttributeNames ()
	{
		Vector names = new Vector();
		Enumeration keys = this.attributes.keys();
		while (keys.hasMoreElements())
		{
			names.addElement(keys.nextElement());
		}
		String results[] = new String[names.size()];
		for (int i = 0; i < results.length; i++)
		{
			results[i] = (String) names.elementAt(i);
		}
		return (results);
	}

	/**
	 * Convenience method to derive a name from the specified class and
	 * call <code>getInstance(String)</code> with it.
	 *
	 * @param clazz Class for which a suitable Log name will be derived
	 * @throws org.apache.commons.logging.LogConfigurationException
	 *          if a suitable <code>Log</code>
	 *          instance cannot be returned
	 */
	@Override
	public Log getInstance (Class clazz) throws LogConfigurationException
	{
		return (getInstance(clazz.getName()));
	}

	/**
	 * <p>Construct (if necessary) and return a <code>Log</code> instance,
	 * using the factory's current set of configuration attributes.</p>
	 * <p/>
	 * <p><strong>NOTE</strong> - Depending upon the implementation of
	 * the <code>LogFactory</code> you are using, the <code>Log</code>
	 * instance you are returned may or may not be local to the current
	 * application, and may or may not be returned again on a subsequent
	 * call with the same name argument.</p>
	 *
	 * @param name Logical name of the <code>Log</code> instance to be
	 *             returned (the meaning of this name is only known to the underlying
	 *             logging implementation that is being wrapped)
	 * @throws LogConfigurationException if a suitable <code>Log</code>
	 *                                   instance cannot be returned
	 */
	@Override
	public Log getInstance (String name) throws LogConfigurationException
	{
		Log instance = (Log) this.instances.get(name);
		if (instance == null)
		{
			instance = newInstance(name);
			this.instances.put(name, instance);
		}

		return (instance);

	}

	/**
	 * Release any internal references to previously created {@link Log}
	 * instances returned by this factory.  This is useful environments
	 * like servlet containers, which implement application reloading by
	 * throwing away a ClassLoader.  Dangling references to objects in that
	 * class loader would prevent garbage collection.
	 */
	@Override
	public void release ()
	{
		this.instances.clear();
	}

	/**
	 * Remove any configuration attribute associated with the specified name.
	 * If there is no such attribute, no action is taken.
	 *
	 * @param name Name of the attribute to remove
	 */
	@Override
	public void removeAttribute (String name)
	{
		this.attributes.remove(name);
	}

	/**
	 * Set the configuration attribute with the specified name.  Calling
	 * this with a <code>null</code> value is equivalent to calling
	 * <code>removeAttribute(name)</code>.
	 *
	 * @param name  Name of the attribute to set
	 * @param value Value of the attribute to set, or <code>null</code>
	 *              to remove any setting for this attribute
	 */
	@Override
	public void setAttribute (String name, Object value)
	{

		if (value == null)
		{
			this.attributes.remove(name);
		}
		else
		{
			this.attributes.put(name, value);
		}
	}

	/**
	 * Return the fully qualified Java classname of the {@link Log}
	 * implementation we will be using.
	 */
	protected String getLogClassName () throws LogConfigurationException
	{

		// Return the previously identified class name (if any)
		if (this.logClassName != null)
		{
			return this.logClassName;
		}

		this.logClassName = (String) getAttribute(Log4JFactoryImpl.LOG_PROPERTY);

		if (this.logClassName == null)
		{
			try
			{
				this.logClassName = System.getProperty(Log4JFactoryImpl.LOG_PROPERTY);
			}
			catch (SecurityException e)
			{
				;
			}
		}

		if (this.logClassName == null)
		{
			throw new LogConfigurationException("Logger implementation not specified");
		}
		return (this.logClassName);

	}


	/**
	 * Load and return the appropriate log4j logger class, based on the version of log4j.
	 * For log4j-1.1.3, the returned class is <strong>org.apache.log4j.Category</strong>.
	 * For later versions, the returned class is <strong>org.apache.log4j.Logger</strong>
	 *
	 * @return the log4j logger class
	 * @throws LogConfigurationException
	 */
	protected Class getLog4JClass () throws LogConfigurationException
	{

		if (this.log4JClass != null)
		{
			return this.log4JClass;
		}

		try
		{
			this.log4JClass = Log4JFactoryImpl.loadClass("org.apache.log4j.Logger");
		}
		catch (Throwable t)
		{

		}

		if (this.log4JClass == null)
		{
			try
			{
				this.log4JClass = Log4JFactoryImpl.loadClass("org.apache.log4j.Category");
			}
			catch (ClassNotFoundException e)
			{
				throw new LogConfigurationException(e);
			}
		}

		return this.log4JClass;
	}

	/**
	 * Return the <strong>Hierarchy</strong> class's constructor based on the version of lo4j. This
	 * can be one of <strong>Hierarchy(Category)</strong> or <strong>Hierarchy(Logger)</strong>
	 *
	 * @return the <strong>Hierarchy</strong> class's constructor
	 * @throws LogConfigurationException
	 */
	protected Constructor getHierarchyConstructor () throws LogConfigurationException
	{
		if (this.hierarchyConstructor != null)
		{
			return this.hierarchyConstructor;
		}


		try
		{
			Class[] signature = new Class[]{getLog4JClass()};

			this.hierarchyConstructor = Hierarchy.class.getConstructor(signature);
			return this.hierarchyConstructor;
		}
		catch (Throwable t)
		{
			throw new LogConfigurationException(t);
		}

	}


	/**
	 * Return a new instance of <strong>RootCategory</strong>.
	 *
	 * @return an instance of <strong>RootCategory</strong>
	 * @throws LogConfigurationException
	 */
	protected Object getRootCategory () throws LogConfigurationException
	{

		Class priorityClass = null;
		Class rootCategoryClass = null;

		try
		{
			rootCategoryClass = Log4JFactoryImpl.loadClass("org.apache.log4j.spi.RootCategory");

			if (getLog4JClass().getName().equals(Log4JFactoryImpl.LOGGER_LOGGER))
			{
				priorityClass = Log4JFactoryImpl.loadClass("org.apache.log4j.Level");

			}
			else if (getLog4JClass().getName().equals(Log4JFactoryImpl.CATEGORY_LOGGER))
			{
				priorityClass = Log4JFactoryImpl.loadClass("org.apache.log4j.Priority");
			}

			Field f = priorityClass.getField("DEBUG");
			Object priority = f.get(null);
			Constructor cnst = rootCategoryClass.getConstructor(new Class[]{priorityClass});
			return cnst.newInstance(new Object[]{priority});
		}
		catch (Throwable t)
		{
			throw new LogConfigurationException(t);
		}
	}

	/**
	 * Create and retrun a new <strong>Hierarchy</strong>
	 *
	 * @return return a new <strong>Hierarchy</strong>
	 * @throws LogConfigurationException
	 */
	protected Hierarchy createLog4JHierarchy () throws LogConfigurationException
	{

		Constructor cnst = getHierarchyConstructor();

		Object[] params = new Object[]{getRootCategory()};
		try
		{
			return (Hierarchy) cnst.newInstance(params);
		}
		catch (Throwable t)
		{
			throw new LogConfigurationException(t);
		}
	}

	/**
	 * Return the constructor of the logger wrapper as specified by the
	 * <strong>org.apache.commons.logging.Log</strong> property.
	 *
	 * @return the logger delegates constructor
	 * @throws LogConfigurationException
	 */
	protected Constructor getLogConstructor () throws LogConfigurationException
	{
		// Return the previously identified Constructor (if any)
		if (this.logConstructor != null)
		{
			return this.logConstructor;
		}

		String logClassName = getLogClassName();

		// Attempt to load the Log implementation class
		Class logClass = null;
		try
		{
			logClass = Log4JFactoryImpl.loadClass(logClassName);
			if (logClass == null)
			{
				throw new LogConfigurationException("No suitable Log implementation for " + logClassName);
			}
			if (!Log.class.isAssignableFrom(logClass))
			{
				throw new LogConfigurationException("Class " + logClassName + " does not implement Log");
			}
		}
		catch (Throwable t)
		{
			throw new LogConfigurationException(t);
		}

		// Identify the corresponding constructor to be used
		try
		{
			this.logConstructor = logClass.getConstructor(this.logConstructorSignature);
			return (this.logConstructor);
		}
		catch (Throwable t)
		{
			throw new LogConfigurationException(
					"No suitable Log constructor " + this.logConstructorSignature + " for " + logClassName, t);
		}
	}

	/**
	 * Create a new logger using <strong>name</strong>
	 *
	 * @param name the name of the logger
	 * @return a logger instancec
	 * @throws LogConfigurationException
	 */
	protected Log newInstance (String name) throws LogConfigurationException
	{
		Log instance = null;
		try
		{
			Object params[] = new Object[2];
			params[0] = this.hierarchy;
			params[1] = name;
			instance = (Log) getLogConstructor().newInstance(params);
			return (instance);
		}
		catch (Throwable t)
		{
			throw new LogConfigurationException(t);
		}
	}

	/**
	 * A helper method to locate a resource using <strong>loader</strong>
	 *
	 * @param loader
	 * @param name
	 * @return
	 */
	private static URL getResource (final ClassLoader loader, final String name)
	{
		return (URL) AccessController.doPrivileged(new PrivilegedAction()
		{
			@Override
			public Object run ()
			{
				if (loader != null)
				{
					return loader.getResource(name);
				}
				else
				{
					return ClassLoader.getSystemResource(name);
				}
			}
		});
	}

	/**
	 * MUST KEEP THIS METHOD PRIVATE.
	 * <p/>
	 * <p>Exposing this method outside of
	 * <code>org.apache.commons.logging.LogFactoryImpl</code>
	 * will create a security violation:
	 * This method uses <code>AccessController.doPrivileged()</code>.
	 * </p>
	 * <p/>
	 * Load a class, try first the thread class loader, and
	 * if it fails use the loader that loaded this class.
	 */
	private static Class loadClass (final String name) throws ClassNotFoundException
	{
		Object result = AccessController.doPrivileged(new PrivilegedAction()
		{
			@Override
			public Object run ()
			{
				ClassLoader threadCL = LogFactory.getContextClassLoader();
				if (threadCL != null)
				{
					try
					{
						return threadCL.loadClass(name);
					}
					catch (ClassNotFoundException ex)
					{
						// ignore
					}
				}
				try
				{
					return Class.forName(name);
				}
				catch (ClassNotFoundException e)
				{
					return e;
				}
			}
		});

		if (result instanceof Class)
		{
			return (Class) result;
		}

		throw (ClassNotFoundException) result;
	}
}
