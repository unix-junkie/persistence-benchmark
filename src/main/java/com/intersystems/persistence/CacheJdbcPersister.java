/*-
 * $Id$
 */
package com.intersystems.persistence;

import java.sql.DatabaseMetaData;

import com.intersystems.jdbc.IRISDriver;
import com.intersystems.util.VersionInfo;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public class CacheJdbcPersister extends JdbcPersister {
	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public CacheJdbcPersister(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		this(new CacheJdbcConnectionParameters(host,
				port,
				namespace,
				username,
				password,
				autoCommit));
	}

	/**
	 * @param connectionParameters
	 */
	protected CacheJdbcPersister(final CacheJdbcConnectionParameters connectionParameters) {
		super(connectionParameters);
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "InterSystems Cach\u00e9 " + VersionInfo.getClientVersion() + " (auto-commit: " + this.connectionParameters.getAutoCommit() + ")";
	}

	/**
	 * @see JdbcPersister#getDriverClass()
	 */
	@Override
	protected final Class<IRISDriver> getDriverClass() {
		return IRISDriver.class;
	}

	/**
	 * @see JdbcPersister#getCreateSql(DatabaseMetaData)
	 */
	@Override
	protected final String getCreateSql(final DatabaseMetaData metaData) {
		return "create table events(ticker %String(MAXLEN=32) not null, per %Integer(MINVAL=" + Integer.MIN_VALUE + ", MAXVAL=" + Integer.MAX_VALUE + ") not null, timestamp %TimeStamp not null, \"LAST\" %Double not null, vol %Integer(MINVAL=" + Long.MIN_VALUE + ", MAXVAL=" + Long.MAX_VALUE + ") not null)";
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public CacheJdbcConnectionParameters getConnectionParameters() {
		return (CacheJdbcConnectionParameters) this.connectionParameters;
	}
}
