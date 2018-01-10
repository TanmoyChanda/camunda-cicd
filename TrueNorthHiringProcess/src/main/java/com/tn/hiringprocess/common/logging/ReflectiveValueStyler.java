package com.tn.hiringprocess.common.logging;




import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.style.DefaultValueStyler;
import org.springframework.core.style.ToStringCreator;
import org.springframework.core.style.ValueStyler;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * ReflectiveValueStyler - Uses Reflections to recursively call all public getXXX methods
 * on an input POJO to create a textual representation of the Object graph.
 * 
 * @author PwCI
 */
public class ReflectiveValueStyler implements ValueStyler {

	private static final String EMPTY = "[empty]";
	private static final String NULL = "[null]";
	private static final String COLLECTION = "collection";
	private static final String SET = "set";
	private static final String LIST = "list";
	private static final String MAP = "map";
	private static final String ARRAY = "array";

	private final ReflectiveVisitorHelper reflectiveVisitorHelper = new ReflectiveVisitorHelper();

	public ReflectiveValueStyler() {
		super();
	}
	
	/**
	 * Method: ReflectiveValueStyler.style
	 * This class uses the ReflectionVisitorHelper to walk the Object graph and call the 
	 * visit() methods on this class based on the type of Object encountered.
	 * (Double-dispatch pattern)
	 * 
	 * @param value
	 * @return String
	 * @see org.springframework.core.style.ValueStyler#style(java.lang.Object)
	 */
	public String style(Object value) {

		return (String) reflectiveVisitorHelper.invokeVisit(this, value);
				
	}

	String visit(String value) {
		return ('\'' + value + '\'');
	}

	String visit(Number value) {
		return String.valueOf(value);
	}

	String visit(Class clazz) {
		return ClassUtils.getShortName(clazz);
	}

	String visit(Method method) {
		return method.getName() + "@" + ClassUtils.getShortName(method.getDeclaringClass());
	}

	String visit(Map value) {
		StringBuffer buffer = new StringBuffer(value.size() * 8 + 16);
		buffer.append(MAP + "[");
		for (Iterator i = value.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			buffer.append(style(entry));
			if (i.hasNext()) {
				buffer.append(',').append(' ');
			}
		}
		if (value.isEmpty()) {
			buffer.append(EMPTY);
		}
		buffer.append("]");
		return buffer.toString();
	}

	String visit(Map.Entry value) {
		return style(value.getKey()) + " -> " + style(value.getValue());
	}

	String visit(Collection value) {
		StringBuffer buffer = new StringBuffer(value.size() * 8 + 16);
		buffer.append(getCollectionTypeString(value) + "[");
		for (Iterator i = value.iterator(); i.hasNext();) {
			buffer.append(style(i.next()));
			if (i.hasNext()) {
				buffer.append(',').append(' ');
			}
		}
		if (value.isEmpty()) {
			buffer.append(EMPTY);
		}
		buffer.append("]");
		return buffer.toString();
	}

	private String getCollectionTypeString(Collection value) {
		if (value instanceof List) {
			return LIST;
		}
		else if (value instanceof Set) {
			return SET;
		}
		else {
			return COLLECTION;
		}
	}

	String visitNull() {
		return NULL;
	}

	private String styleArray(Object[] array) {
		StringBuffer buffer = new StringBuffer(array.length * 8 + 16);
		buffer.append(ARRAY + "<" + ClassUtils.getShortName(array.getClass().getComponentType()) + ">[");
		for (int i = 0; i < array.length - 1; i++) {
			buffer.append(style(array[i]));
			buffer.append(',').append(' ');
		}
		if (array.length > 0) {
			buffer.append(style(array[array.length - 1]));
		}
		else {
			buffer.append(EMPTY);
		}
		buffer.append("]");
		return buffer.toString();
	}

	private Object[] getObjectArray(Object value) {
		if (value.getClass().getComponentType().isPrimitive()) {
			return ObjectUtils.toObjectArray(value);
		}
		else {
			return (Object[]) value;
		}
	}

	String visit(Date date)
	{
		return '\'' + date.toString() + '\'';
	}
	
	String visit(Object value)
	{
		if (value.getClass().isArray()) {
			return styleArray(getObjectArray(value));
		}
		else {
			StringBuffer buffer = new StringBuffer();
			buffer.append(ClassUtils.getShortName(value.getClass()) + "[");
			
			Class clazz = value.getClass();
			Method[] m = clazz.getMethods();
			for (int i = 0; i < m.length; i++)
			{
				Method method = m[i];
				callMethod(value, buffer, method);
			}
			
			buffer.append("]");
			return buffer.toString();
		}
	}
	
	private void callMethod(Object obj, StringBuffer buff, Method method)
	{
		String methodName = method.getName();
		if (! "getClass".equals(methodName))
		{
			Class[] p = method.getParameterTypes();
			if ((methodName.startsWith("get")) 
				&& ((p == null) || (p.length == 0)) )
			{
				try
				{
					Object[] args = null;
					Object retVal = method.invoke(obj, args);
				
					if (retVal != null)
					{
						String retName = methodName.substring(3);
						buff.append(retName).append('=');
						buff.append(style(retVal));
						buff.append(',').append(' ');
					}
				}
				catch (Exception e)
				{
					System.err.println("Error trying to Render: " + obj 
						+ "... method: " + methodName + " --> " + e);
				}
			}
		}
	}
	
}
