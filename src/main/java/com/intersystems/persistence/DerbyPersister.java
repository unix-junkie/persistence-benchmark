/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.sql.DriverManager.getConnection;

import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class DerbyPersister extends JdbcPersister {
	private static final String DBNAME = "XEP";

	private final DerbyConnectionParameters connectionParameters = new DerbyConnectionParameters();

	public DerbyPersister(final boolean autoCommit) {
		super("jdbc:derby:" + DBNAME + ";create=true", autoCommit);
		this.connectionParameters.setDatabaseName(DBNAME);
		this.connectionParameters.setAutoCommit(autoCommit);
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		final Driver driver = new EmbeddedDriver();
		return "Apache Derby " + driver.getMajorVersion() + '.' + driver.getMinorVersion() + " (auto-commit: " + this.autoCommit + ")";
	}

	/**
	 * @see JdbcPersister#tearDown()
	 */
	@Override
	public void tearDown() {
		super.tearDown();

		try {
			getConnection("jdbc:derby:" + DBNAME + ";shutdown=true");
			System.out.println("Failed to shut down Derby instance '" + DBNAME + '\'');
		} catch (final SQLException sqle) {
			/*
			 * Ignore: an exception is always thrown on shutdown.
			 */
		}
	}

	/**
	 * @see JdbcPersister#getDriverClass()
	 */
	@Override
	protected Class<EmbeddedDriver> getDriverClass() {
		return EmbeddedDriver.class;
	}

	/**
	 * @see JdbcPersister#getCreateSql(DatabaseMetaData)
	 */
	@Override
	protected String getCreateSql(final DatabaseMetaData metaData) {
		return "create table events(ticker varchar(32) not null, per int not null, timestamp timestamp not null, \"LAST\" double not null, vol bigint not null)";
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public DerbyConnectionParameters getConnectionParameters() {
		return this.connectionParameters;
	}
}
