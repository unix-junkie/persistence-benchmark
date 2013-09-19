/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Component;

import com.intersystems.persistence.Cach\u00e9ExtremeConnectionParameters;
import java.awt.GridBagLayout;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9ExtremeShmConnectionParametersPanel extends Cach\u00e9ExtremeConnectionParametersPanel {
	private static final long serialVersionUID = 2173001890984252740L;

	/**
	 * @param connectionParameters
	 */
	public Cach\u00e9ExtremeShmConnectionParametersPanel(final Cach\u00e9ExtremeConnectionParameters connectionParameters) {
		super(connectionParameters);

		this.setLayout(new GridBagLayout());
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Cach\u00e9 eXtreme (SHM)";
	}
}