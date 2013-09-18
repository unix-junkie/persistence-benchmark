/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersystems.persistence.ui.ConnectionParametersPanel;
import com.intersystems.persistence.ui.JdbcConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class JdbcConnectionParameters<T extends JdbcPersister> implements ConnectionParameters<T> {
	private String url;

	private boolean autoCommit = true;

	private String username;

	private String password;


	private JdbcConnectionParametersPanel<T> view;

	/**
	 * @param url
	 * @param autoCommit
	 * @param username
	 * @param password
	 */
	protected JdbcConnectionParameters(final String url,
			final boolean autoCommit,
			final String username,
			final String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.autoCommit = autoCommit;
	}

	/**
	 * @param url
	 * @param autoCommit
	 */
	protected JdbcConnectionParameters(final String url,
			final boolean autoCommit) {
		this(url, autoCommit, null, null);
	}

	public final String getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 */
	public final void setUrl(final String url) {
		this.url = url;
	}

	public final String getUsername() {
		return this.username;
	}

	/**
	 * @param username
	 */
	public final void setUsername(final String username) {
		this.username = username;
	}

	public final String getPassword() {
		return this.password;
	}

	/**
	 * @param password
	 */
	public final void setPassword(final String password) {
		this.password = password;
	}

	public final boolean getAutoCommit() {
		return this.autoCommit;
	}

	/**
	 * @param autoCommit
	 */
	public final void setAutoCommit(final boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * @see ConnectionParameters#getView()
	 */
	@Override
	public ConnectionParametersPanel<T> getView() {
		return this.view == null
				? this.view = new JdbcConnectionParametersPanel<T>(this)
				: this.view;
	}
}
