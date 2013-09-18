/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import com.intersystems.persistence.Cach\u00e9ExtremePersister;
import com.intersystems.persistence.ConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class Cach\u00e9ExtremeConnectionParametersPanel extends ConnectionParametersPanel<Cach\u00e9ExtremePersister> {
	private static final long serialVersionUID = -6964626668633337492L;

	/**
	 * @param connectionParameters
	 */
	protected Cach\u00e9ExtremeConnectionParametersPanel(final ConnectionParameters<Cach\u00e9ExtremePersister> connectionParameters) {
		super(connectionParameters);
	}
}