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
public final class Cach\u00e9ExtremeShmConnectionParametersPanel extends Cach\u00e9ExtremeConnectionParametersPanel {
	private static final long serialVersionUID = 2173001890984252740L;

	/**
	 * @param connectionParameters
	 */
	protected Cach\u00e9ExtremeShmConnectionParametersPanel(final ConnectionParameters<Cach\u00e9ExtremePersister> connectionParameters) {
		super(connectionParameters);
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Cach\u00e9 Extreme (SHM)";
	}
}