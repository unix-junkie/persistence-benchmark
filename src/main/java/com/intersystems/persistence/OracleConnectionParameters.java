/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersystems.persistence.ui.OracleConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class OracleConnectionParameters extends JdbcConnectionParameters<OraclePersister> {
	private OracleConnectionParametersPanel view;

	/**
	 * @param host
	 * @param port
	 * @param sid
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public OracleConnectionParameters(final String host,
			final int port,
			final String sid,
			final String username,
			final String password,
			final boolean autoCommit) {
		super("jdbc:oracle:thin:@" + host + ':' + port + ':' + sid,
				autoCommit,
				username,
				password);
	}

	/**
	 * @see ConnectionParameters#getView()
	 */
	@Override
	public OracleConnectionParametersPanel getView() {
		return this.view == null
				? this.view = new OracleConnectionParametersPanel(this)
				: this.view;
	}
}
