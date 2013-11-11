/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import com.intersystems.persistence.CacheExtremeConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class CacheExtremeConnectionParametersPanel extends ConnectionParametersPanel {
	private static final long serialVersionUID = -6964626668633337492L;

	/**
	 * @param connectionParameters
	 */
	protected CacheExtremeConnectionParametersPanel(final CacheExtremeConnectionParameters connectionParameters) {
		super(connectionParameters);
	}
}