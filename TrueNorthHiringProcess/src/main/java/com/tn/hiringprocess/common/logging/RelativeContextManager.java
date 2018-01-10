package com.tn.hiringprocess.common.logging;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;




/**
// * Factory responsible for associating RelativeContext Objects to the current
 * running Thread.
 * 
 * @author PwCI
 */
public class RelativeContextManager {
	private static Map instances = null;

	private ElapsedTimeMap contexts;

	/**
	 * Accessor for the Singleton Instance.
	 * 
	 * @return RelativeContextManager
	 */
	public static RelativeContextManager getInstance(String name) {
		if (RelativeContextManager.instances == null) {
			RelativeContextManager.instances = new HashMap();
		}
		if (RelativeContextManager.instances.containsKey(name)) {
			return (RelativeContextManager) RelativeContextManager.instances.get(name);
		}

		RelativeContextManager instance = new RelativeContextManager();
		RelativeContextManager.instances.put(name, instance);
		return instance;
	}

	/**
	 * Constructor for RelativeContextManager
	 */
	private RelativeContextManager() {
		super();
		this.contexts = new ElapsedTimeMap();
	}

	/**
	 * Stores the input RelativeContext relative to the current running Thread.
	 * Use the method retrieveContext() to retrieve this Object at any time on
	 * the current Thread.
	 * 
	 * @param context
	 *            RelativeContext
	 */
	public void storeContext(RelativeContext context) {
		this.contexts.put(Thread.currentThread(), context);
	}

	/**
	 * Retrieves the RelativeContext associated to the current running Thread or
	 * null if none exists.
	 * 
	 * @return RelativeContext
	 */
	public RelativeContext retrieveContext() {
		return (RelativeContext) this.contexts.get(Thread.currentThread());
	}

	/**
	 * Retrieves the RelativeContext associated with the current Thread and
	 * removes it from the collection.
	 * 
	 * @return RelativeContext
	 */
	public RelativeContext removeContext() {
		return (RelativeContext) this.contexts.remove(Thread.currentThread());
	}

	/**
	 * Searches for expired Contexts and removes them if they have not been
	 * 'touched' since before (current time - timeout).
	 * 
	 * @param timeout
	 *            long representing milliseconds length of time to determine if
	 *            the Context has timed out.
	 */
	protected Collection expireContexts(long timeout) {
		return this.contexts.removeExpired(timeout);
	}

}
