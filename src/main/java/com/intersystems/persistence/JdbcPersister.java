/*-
 * $Id$
 */
package com.intersystems.persistence;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class JdbcPersister extends AbstractPersister {
	private final String url;

	protected final boolean autoCommit;

	private final String username;

	private final String password;

	private Connection conn;

	private PreparedStatement pstmt;

	/**
	 * @param url
	 * @param autoCommit
	 * @param username
	 * @param password
	 */
	protected JdbcPersister(final String url,
			final boolean autoCommit,
			final String username,
			final String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.autoCommit = autoCommit;
	}

	/**
	 * @param url
	 * @param autoCommit
	 */
	protected JdbcPersister(final String url,
			final boolean autoCommit) {
		this(url, autoCommit, null, null);
	}

	/**
	 * @see Persister#setUp()
	 */
	@Override
	public TestResult setUp() {
		try {
			this.initConnection();
		} catch (final ClassNotFoundException cnfe) {
			this.pstmt = null;

			return new TestResult(cnfe);
		} catch (final SQLException sqle) {
			this.pstmt = null;

			printExceptionChain(sqle, System.out);

			return new TestResult(sqle);
		}

		try {
			final Statement stmt = this.conn.createStatement();
			try {
				try {
					stmt.executeUpdate(this.getCreateSql(this.conn.getMetaData()));
					this.conn.commit();
				} catch (final SQLException sqle) {
					// ignore
				}
				try {
					stmt.executeUpdate("truncate table events");
					this.conn.commit();
				} catch (final SQLException sqle) {
					printExceptionChain(sqle, System.out);

					return new TestResult(sqle);
				}
			} finally {
				stmt.close();
			}
		} catch (final SQLException sqle) {
			printExceptionChain(sqle, System.out);

			return new TestResult(sqle);
		}

		try {
			this.pstmt = this.getPreparedStatement();
			return TestResult.READY;
		} catch (final SQLException sqle) {
			printExceptionChain(sqle, System.out);

			return new TestResult(sqle);
		}
	}

	/**
	 * @param sqle
	 * @param out
	 */
	private static void printExceptionChain(final SQLException sqle, final PrintStream out) {
		out.println(sqle.getMessage());

		SQLException nextException = sqle;
		while ((nextException = nextException.getNextException()) != null) {
			out.println(nextException.getMessage());
		}
	}

	/**
	 * @see Persister#persist(Event)
	 */
	@Override
	public final void persist(final Event event) {
		if (this.conn == null || this.pstmt == null) {
			return;
		}

		try {
			this.pstmt.setString(1, event.ticker);
			this.pstmt.setInt(2, event.per);
			this.pstmt.setTimestamp(3, new Timestamp(event.getTimestamp().getTime()));
			this.pstmt.setDouble(4, event.last);
			this.pstmt.setLong(5, event.vol);
			this.pstmt.executeUpdate();
		} catch (final SQLException sqle) {
			printExceptionChain(sqle, System.out);
		}
	}

	/**
	 * @see Persister#tearDown()
	 */
	@Override
	public void tearDown() {
		if (this.conn != null) {
			try {
				this.conn.commit();
			} catch (final SQLException sqle) {
				printExceptionChain(sqle, System.out);
			}
			try {
				this.conn.close();
			} catch (final SQLException sqle) {
				printExceptionChain(sqle, System.out);
			}
		}

		this.setRunning(false);
	}

	protected abstract Class<? extends Driver> getDriverClass();

	/**
	 * @param metaData
	 */
	protected abstract String getCreateSql(final DatabaseMetaData metaData);

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void initConnection() throws ClassNotFoundException, SQLException {
		Class.forName(this.getDriverClass().getName());
		this.conn = this.username == null || this.username.length() == 0
				? DriverManager.getConnection(this.url)
				: DriverManager.getConnection(this.url, this.username, this.password);
		this.conn.setAutoCommit(this.autoCommit);
		final DatabaseMetaData metaData = this.conn.getMetaData();
		this.setServerVersion(metaData.getDatabaseProductName() + ", version " + metaData.getDatabaseProductVersion() + " at " + metaData.getURL());
		this.setRunning(true);
	}

	@SuppressWarnings("static-method")
	protected String getInsertSql() {
		return "insert into events(ticker, per, timestamp, \"LAST\", vol) values (?, ?, ?, ?, ?)";
	}

	/**
	 * @throws SQLException
	 */
	private PreparedStatement getPreparedStatement() throws SQLException {
		if (this.conn == null) {
			throw new IllegalStateException();
		}

		return this.conn.prepareStatement(this.getInsertSql());
	}
}
