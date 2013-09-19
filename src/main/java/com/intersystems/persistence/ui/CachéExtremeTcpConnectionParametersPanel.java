/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Component;
import java.awt.GridBagLayout;

import com.intersystems.persistence.Cach\u00e9ExtremeConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9ExtremeTcpConnectionParametersPanel extends Cach\u00e9ExtremeConnectionParametersPanel {
	private static final long serialVersionUID = -5382938842192808463L;

	/**
	 * @param connectionParameters
	 */
	public Cach\u00e9ExtremeTcpConnectionParametersPanel(final Cach\u00e9ExtremeConnectionParameters connectionParameters) {
		super(connectionParameters);

		this.setLayout(new GridBagLayout());
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Cach\u00e9 eXtreme (TCP)";
	}
}