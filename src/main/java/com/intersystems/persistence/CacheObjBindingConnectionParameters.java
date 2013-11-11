/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersystems.persistence.ui.CacheObjBindingConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheObjBindingConnectionParameters implements ConnectionParameters {
	private String url;

	private boolean autoCommit = true;

	private String username;

	private String password;

	private final String host;

	private final int port;

	private final String namespace;

	private CacheObjBindingConnectionParametersPanel view;

	public CacheObjBindingConnectionParameters(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		this.url = "jdbc:Cache://" + host + ":" + port + "/" + namespace;
		this.host = host;
		this.port = port;
		this.namespace = namespace;
		this.username = username;
		this.password = password;
		this.autoCommit = autoCommit;
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

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public String getNamespace() {
		return this.namespace;
	}

	/**
	 * @see ConnectionParameters#getView()
	 */
	@Override
	public CacheObjBindingConnectionParametersPanel getView() {
		return this.view == null
				? this.view = new CacheObjBindingConnectionParametersPanel(this)
				: this.view;
	}
}
