/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Component;

import com.intersystems.persistence.CacheObjBindingConnectionParameters;
import com.intersystems.persistence.CacheObjBindingPersister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheObjBindingConnectionParametersPanel extends ConnectionParametersPanel<CacheObjBindingPersister> {
	private static final long serialVersionUID = 2425121880992010985L;

	/**
	 * @param connectionParameters
	 */
	public CacheObjBindingConnectionParametersPanel(final CacheObjBindingConnectionParameters connectionParameters) {
		super(connectionParameters);
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Cach\u00e9 Object Binding";
	}
}
