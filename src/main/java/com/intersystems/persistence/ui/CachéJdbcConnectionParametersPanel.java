/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Component;

import com.intersystems.persistence.Cach\u00e9JdbcConnectionParameters;
import com.intersystems.persistence.Cach\u00e9JdbcPersister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9JdbcConnectionParametersPanel extends JdbcConnectionParametersPanel<Cach\u00e9JdbcPersister> {
	private static final long serialVersionUID = -1733281789467997038L;

	/**
	 * @param connectionParameters
	 */
	public Cach\u00e9JdbcConnectionParametersPanel(final Cach\u00e9JdbcConnectionParameters connectionParameters) {
		super(connectionParameters);

		this.removeAll();

	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Cach\u00e9 JDBC";
	}
}
