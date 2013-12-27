/*-
 * $Id$
 */
package com.intersystems.persistence;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class CacheJdbcOverObjectBindingPersister extends CacheObjBindingPersister {
	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public CacheJdbcOverObjectBindingPersister(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		super(host, port, namespace, username, password, autoCommit);
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public final CacheObjBindingConnectionParameters getConnectionParameters() {
		/*
		 * No empty panels until they're implemented.
		 */
		return null;
	}

	/**
	 * @see JdbcPersister#getInsertSql()
	 */
	@Override
	protected final String getInsertSql() {
		return "insert into com_intersystems_persistence_objbinding.event(ticker, per, timestamp, \"LAST\", vol) values (?, ?, ?, ?, ?)";
	}
}
