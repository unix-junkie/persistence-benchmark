/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Component;

import com.intersystems.persistence.OracleConnectionParameters;
import com.intersystems.persistence.OraclePersister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class OracleConnectionParametersPanel extends ConnectionParametersPanel<OraclePersister> {
	private static final long serialVersionUID = -2207277781099705804L;

	/**
	 * @param connectionParameters
	 */
	public OracleConnectionParametersPanel(final OracleConnectionParameters connectionParameters) {
		super(connectionParameters);
		// @todo Auto-generated constructor stub
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Oracle";
	}
}
