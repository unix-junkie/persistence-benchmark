/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import javax.swing.JPanel;

import com.intersystems.persistence.ConnectionParameters;
import com.intersystems.persistence.Persister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class ConnectionParametersPanel<T extends Persister> extends JPanel {
	private static final long serialVersionUID = 5916463217646645329L;

	protected final ConnectionParameters<T> connectionParameters;

	protected ConnectionParametersPanel(final ConnectionParameters<T> connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	public final ConnectionParameters<T> getConnectionParameters() {
		return this.connectionParameters;
	}
}
