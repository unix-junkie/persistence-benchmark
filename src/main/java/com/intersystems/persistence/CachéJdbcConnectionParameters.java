/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersystems.persistence.ui.Cach\u00e9JdbcConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9JdbcConnectionParameters extends JdbcConnectionParameters<Cach\u00e9JdbcPersister> {
	private Cach\u00e9JdbcConnectionParametersPanel view;

	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public Cach\u00e9JdbcConnectionParameters(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		super("jdbc:Cache://" + host + ":" + port + "/" + namespace,
				autoCommit,
				username,
				password);
	}

	/**
	 * @see ConnectionParameters#getView()
	 */
	@Override
	public Cach\u00e9JdbcConnectionParametersPanel getView() {
		return this.view == null
				? this.view = new Cach\u00e9JdbcConnectionParametersPanel(this)
				: this.view;
	}
}
