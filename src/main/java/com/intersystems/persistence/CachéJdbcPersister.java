/*-
 * $Id$
 */
package com.intersystems.persistence;

import static com.intersys.util.VersionInfo.getClientVersion;

import java.sql.DatabaseMetaData;

import com.intersys.jdbc.CacheDriver;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9JdbcPersister extends JdbcPersister {
	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public Cach\u00e9JdbcPersister(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		super("jdbc:Cache://" + host + ":" + port + "/" + namespace,
				autoCommit,
				username,
				password);
	}

	/**
	 * @see Persister#getName()
	 */
	@Override
	public String getName() {
		return "InterSystems Cach\u00e9 " + getClientVersion() + " (auto-commit: " + this.autoCommit + ")";
	}

	/**
	 * @see JdbcPersister#getDriverClass()
	 */
	@Override
	protected Class<CacheDriver> getDriverClass() {
		return CacheDriver.class;
	}

	/**
	 * @see JdbcPersister#getCreateSql(DatabaseMetaData)
	 */
	@Override
	protected String getCreateSql(final DatabaseMetaData metaData) {
		return "create table events(ticker %String(MAXLEN=32) not null, per %Integer(MINVAL=" + Integer.MIN_VALUE + ", MAXVAL=" + Integer.MAX_VALUE + ") not null, timestamp %TimeStamp not null, \"LAST\" %Double not null, vol %Integer(MINVAL=" + Long.MIN_VALUE + ", MAXVAL=" + Long.MAX_VALUE + ") not null)";
	}
}
