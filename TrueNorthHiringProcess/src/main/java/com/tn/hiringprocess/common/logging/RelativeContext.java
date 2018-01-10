package com.tn.hiringprocess.common.logging;


import java.io.Serializable;

/**
 * Holds the content of the current RelativeContext. Base implementation
 * includes a creation timestamp.
 * 
 * Sub-classes can provide any other Objects they feel necessary.
 * 
 * It is recommended that sub-classes own and control the types of Objects
 * stored, and not expose a generic Collection structure.
 * 
 * @author Kousik Maiti
 */
public class RelativeContext implements Serializable {
	/** Automatically generated javadoc for: serialVersionUID */
	private static final long serialVersionUID = 4784978724591864766L;

	private final long timestamp;

	/**
	 * Dummy Constructor for Context
	 */
	public RelativeContext() {
		super();
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Accessor for timestamp
	 * 
	 * @return timestamp - long representing milliseconds
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

}
