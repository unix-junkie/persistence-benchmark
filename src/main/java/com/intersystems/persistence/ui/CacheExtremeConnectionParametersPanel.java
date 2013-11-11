/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import com.intersystems.persistence.CacheExtremeConnectionParameters;
import com.intersystems.persistence.CacheExtremePersister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class CacheExtremeConnectionParametersPanel extends ConnectionParametersPanel<CacheExtremePersister> {
	private static final long serialVersionUID = -6964626668633337492L;

	/**
	 * @param connectionParameters
	 */
	protected CacheExtremeConnectionParametersPanel(final CacheExtremeConnectionParameters connectionParameters) {
		super(connectionParameters);
	}
}