/*-
 * $Id$
 */
package com.intersystems.persistence;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public interface Persister {
	String getName();

	/**
	 * @param debug
	 */
	void setUp(final boolean debug);

	/**
	 * @param event
	 */
	void persist(final Event event);

	/**
	 * @param debug
	 */
	void dispose(final boolean debug);
}
