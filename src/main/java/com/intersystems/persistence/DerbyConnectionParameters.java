/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersystems.persistence.ui.ConnectionParametersPanel;
import com.intersystems.persistence.ui.DerbyConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class DerbyConnectionParameters extends JdbcConnectionParameters {
	private String databaseName;

	private DerbyConnectionParametersPanel view;

	/**
	 * @param databaseName
	 * @param autoCommit
	 */
	public DerbyConnectionParameters(final String databaseName, final boolean autoCommit) {
		super("jdbc:derby:" + databaseName + ";create=true", autoCommit);
		this.databaseName = databaseName;
	}

	/**
	 * @param databaseName
	 */
	public void setDatabaseName(final String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseName() {
		return this.databaseName;
	}

	/**
	 * @see ConnectionParameters#getView()
	 */
	@Override
	public ConnectionParametersPanel getView() {
		return this.view == null
				? this.view = new DerbyConnectionParametersPanel(this)
				: this.view;
	}
}
