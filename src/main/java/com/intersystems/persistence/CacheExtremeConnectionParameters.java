/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersys.xep.annotations.Id;
import com.intersystems.persistence.ui.CacheExtremeConnectionParametersPanel;
import com.intersystems.persistence.ui.CacheExtremeShmConnectionParametersPanel;
import com.intersystems.persistence.ui.CacheExtremeTcpConnectionParametersPanel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheExtremeConnectionParameters implements ConnectionParameters {
	private final String namespace;

	private final String user;

	private final String password;

	/**
	 * Whether to suspend journalling when inserting (<em>unsafe</em>).
	 * The default is {@false}.
	 */
	private final boolean suspendJournalling;

	private final boolean flatSchema;


	private final String host;

	private final int port;


	private CacheExtremeConnectionParametersPanel view;

	/**
	 * @param namespace
	 * @param user
	 * @param password
	 * @param suspendJournalling
	 * @param flatSchema whether generated Cach\u00e9 classes should be final (flat schema)
	 * or inheritable (full schema). Flat schema is the default.
	 *
	 * <ul>
	 *
	 * <li>Flat schema:</li>
	 * <ul>
	 * <li>{@code $lb(...)} structure is correct in case identifiers are
	 * {@linkplain Id#generated() generated}
	 * (the first field is {@code %%CLASSNAME} which is blank);</li>
	 * <li>storage definition is <em>not</em> correct
	 * ({@code %%CLASSNAME} is <em>always</em> missing from the storage,
	 * field numbering starts from 1 (should be 2));</li>
	 * <li>SQL results are correct in case identifiers are <em>not</em> generated.</li>
	 * </ul>
	 *
	 * <li>Full schema:</li>
	 * <ul>
	 * <li>{@code $lb(...)} structure is <em>not</em> correct
	 * (first <em>2</em> entries are blank, field values start from the 3rd entry)
	 * unless identifiers are <em>not</em> generated
	 * (this is fixed in <em>2014.1+</em> with
	 * <a href = "http://turbo.iscinternal.com/prodlog/devview.csp?Key=AND1380">AND1380</a>);</li>
	 * <li>storage definition is correct;</li>
	 * <li>SQL results are correct in case identifiers are <em>not</em> generated.</li>
	 * </ul>
	 *
	 * </ul>
	 * @param host
	 * @param port
	 */
	public CacheExtremeConnectionParameters(final String namespace,
			final String user,
			final String password,
			final boolean suspendJournalling,
			final boolean flatSchema,
			final String host,
			final int port) {
		this.namespace = namespace;
		this.user = user;
		this.password = password;

		this.suspendJournalling = suspendJournalling;
		this.flatSchema = flatSchema;

		this.host = host;
		this.port = port;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getUser() {
		return this.user;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean getSuspendJournalling() {
		return this.suspendJournalling;
	}

	public boolean isFlatSchema() {
		return this.flatSchema;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public boolean useShm() {
		return this.host == null || this.host.length() == 0 || this.port < 0 || this.port > 65535;
	}

	/**
	 * @see ConnectionParameters#getView()
	 */
	@Override
	public CacheExtremeConnectionParametersPanel getView() {
		return this.view == null
				? this.view = this.useShm()
						? new CacheExtremeShmConnectionParametersPanel(this)
						: new CacheExtremeTcpConnectionParametersPanel(this)
				: this.view;
	}
}
