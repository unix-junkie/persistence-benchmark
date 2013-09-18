/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Component;
import java.awt.GridBagLayout;

import com.intersystems.persistence.OracleConnectionParameters;
import com.intersystems.persistence.OraclePersister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class OracleConnectionParametersPanel extends JdbcConnectionParametersPanel<OraclePersister> {
	private static final long serialVersionUID = -2207277781099705804L;

	/**
	 * @param connectionParameters
	 */
	public OracleConnectionParametersPanel(final OracleConnectionParameters connectionParameters) {
		super(connectionParameters);

		this.removeAll();

		this.setLayout(new GridBagLayout());
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Oracle";
	}
}
