/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersystems.persistence.ui.ConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public interface ConnectionParameters<T extends Persister> {
	ConnectionParametersPanel<T> getView();
}
