/*-
 * $Id$
 */
package com.intersystems.persistence;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class JdbcConnectionParameters<T extends JdbcPersister> implements ConnectionParameters<T> {
	private boolean autoCommit = true;

	public final boolean getAutoCommit() {
		return this.autoCommit;
	}

	/**
	 * @param autoCommit
	 */
	public final void setAutoCommit(final boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
}
