/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import javax.swing.JPanel;

import com.intersystems.persistence.ConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class ConnectionParametersPanel extends JPanel {
	private static final long serialVersionUID = 5916463217646645329L;

	protected final ConnectionParameters connectionParameters;

	protected ConnectionParametersPanel(final ConnectionParameters connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	public final ConnectionParameters getConnectionParameters() {
		return this.connectionParameters;
	}
}
