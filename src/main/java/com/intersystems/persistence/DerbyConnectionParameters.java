/*-
 * $Id$
 */
package com.intersystems.persistence;


/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class DerbyConnectionParameters extends JdbcConnectionParameters<DerbyPersister> {
	private String databaseName;

	/**
	 * @param databaseName
	 */
	public void setDatabaseName(final String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseName() {
		return this.databaseName;
	}
}
