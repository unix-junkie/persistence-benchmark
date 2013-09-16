/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.lang.System.setProperty;
import static oracle.jdbc.OracleDriver.getDriverVersion;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import oracle.jdbc.OracleDriver;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class OraclePersister extends JdbcPersister {
	static {
		/*
		 * Prevents ORA-00604/ORA-01882 when connecting to older Oracle versions.
		 */
		setProperty("oracle.jdbc.timezoneAsRegion", "false");
	}

	/**
	 * @param host
	 * @param port
	 * @param sid
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public OraclePersister(final String host,
			final int port,
			final String sid,
			final String username,
			final String password,
			final boolean autoCommit) {
		super("jdbc:oracle:thin:@" + host + ':' + port + ':' + sid,
				autoCommit,
				username,
				password);
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "Oracle " + getDriverVersion() + " (auto-commit: " + this.autoCommit + ")";
	}

	/**
	 * @see JdbcPersister#getDriverClass()
	 */
	@Override
	protected Class<OracleDriver> getDriverClass() {
		return OracleDriver.class;
	}

	/**
	 * @see JdbcPersister#getCreateSql(DatabaseMetaData)
	 */
	@Override
	protected String getCreateSql(final DatabaseMetaData metaData) {
		return is10gOrLater(metaData)
				? "create table events(ticker varchar2(32 char) not null, per number(10) not null, timestamp date not null, \"LAST\" binary_double    not null, vol number(19) not null)"
				: "create table events(ticker varchar2(32     ) not null, per number(10) not null, timestamp date not null, \"LAST\" double precision not null, vol number(19) not null)";
	}

	/**
	 * @see JdbcPersister#getInsertSql()
	 */
	@Override
	protected String getInsertSql() {
		return "insert /*+ append */ into events(ticker, per, timestamp, \"LAST\", vol) values (?, ?, ?, ?, ?)";
	}

	private static boolean is10gOrLater(final DatabaseMetaData metaData) {
		try {
			return metaData.getDatabaseMajorVersion() >= 10;
		} catch (final SQLException sqle) {
			return false;
		}
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public OracleConnectionParameters getConnectionParameters() {
		return new OracleConnectionParameters();
	}
}
