/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Component;

import com.intersystems.persistence.Cach\u00e9ExtremePersister;
import com.intersystems.persistence.ConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9ExtremeTcpConnectionParametersPanel extends Cach\u00e9ExtremeConnectionParametersPanel {
	private static final long serialVersionUID = -5382938842192808463L;

	/**
	 * @param connectionParameters
	 */
	protected Cach\u00e9ExtremeTcpConnectionParametersPanel(final ConnectionParameters<Cach\u00e9ExtremePersister> connectionParameters) {
		super(connectionParameters);
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Cach\u00e9 Extreme (TCP)";
	}
}