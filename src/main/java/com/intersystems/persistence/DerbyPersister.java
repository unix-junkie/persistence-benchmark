/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.sql.DriverManager.getConnection;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class DerbyPersister extends JdbcPersister {
	private static final String DBNAME = "XEP";

	public DerbyPersister(final boolean autoCommit) {
		super("jdbc:derby:" + DBNAME + ";create=true", autoCommit);
	}

	/**
	 * @see Persister#getName()
	 */
	@Override
	public String getName() {
		return "Apache Derby (auto-commit: " + this.autoCommit + ")";
	}

	/**
	 * @see JdbcPersister#dispose(boolean)
	 */
	@Override
	public void dispose(final boolean debug) {
		super.dispose(debug);

		if (debug) {
			System.out.print("Shutting Derby instance '" + DBNAME + "' down... ");
		}
		try {
			getConnection("jdbc:derby:" + DBNAME + ";shutdown=true");
			if (debug) {
				System.out.println("failed.");
			}
		} catch (final SQLException sqle) {
			if (debug) {
				System.out.println("done.");
				System.out.println(sqle.getMessage());
			}
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
}
