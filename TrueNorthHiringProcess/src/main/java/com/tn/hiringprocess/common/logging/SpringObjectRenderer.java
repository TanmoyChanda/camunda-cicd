package com.tn.hiringprocess.common.logging;




import org.apache.log4j.or.ObjectRenderer;

/**
 * SpringObjectRenderer - Users the default spring styler to reflectively 
 * render the input Object to a String.
 * 
 * @author PwCI
 */
public class SpringObjectRenderer implements ObjectRenderer
{

    private static ReflectiveValueStyler styler = new ReflectiveValueStyler();

    public SpringObjectRenderer()
    {
	super();
    }

    /**
     * Method: SpringObjectRenderer.doRender
     * Users the default spring styler to reflectively render the input Object to a String.
     * 
     * @param value
     * @return String
     * @see org.apache.log4j.or.ObjectRenderer#doRender(java.lang.Object)
     */
    public String doRender(Object value)
    {
	return styler.style(value);
    }

}
