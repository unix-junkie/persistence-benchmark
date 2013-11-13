/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersystems.persistence.ui.CacheObjBindingConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheObjBindingConnectionParameters extends CacheJdbcConnectionParameters {
	private CacheObjBindingConnectionParametersPanel view;

	public CacheObjBindingConnectionParameters(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		super(host, port, namespace, username, password, autoCommit);
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
