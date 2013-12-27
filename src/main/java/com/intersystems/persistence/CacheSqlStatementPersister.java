/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.lang.System.out;

import com.intersys.cache.Dataholder;
import com.intersys.classes.RegisteredObject;
import com.intersys.objects.CacheException;
import com.intersys.objects.StatusCode;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;
import com.intersys.util.VersionInfo;

/**
 * Objects are inserted using {@code %SQL.Statement}.
 *
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheSqlStatementPersister extends CacheJdbcOverObjectBindingPersister {
	private RegisteredObject stmt;

	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public CacheSqlStatementPersister(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		super(host, port, namespace, username, password, autoCommit);
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "InterSystems Cach\u00e9 Object Binding " + VersionInfo.getClientVersion() + " (auto-commit: " + this.connectionParameters.getAutoCommit() + "; %SQL.Statement)";
	}

	/**
	 * @see CacheObjBindingPersister#setUp()
	 */
	@Override
	public TestResult setUp() {
		final TestResult testResult = super.setUp();
		if (!testResult.isSuccessful()) {
			return testResult;
		}

		try {
			final CacheClass sqlStatementClazz = this.database.getCacheClass("%SQL.Statement");
			final CacheMethod factoryMethod = sqlStatementClazz.getMethod("%New");
			this.stmt = (RegisteredObject) factoryMethod.invoke(null, new Object[] {null});
			final CacheMethod prepareMethod = sqlStatementClazz.getMethod("%Prepare");
			final StatusCode status = (StatusCode) prepareMethod.invoke(this.stmt, new String[] {this.getInsertSql()});
			if (status.isError()) {
				return new TestResult(status.toString());
			}
		} catch (final CacheException ce) {
			return new TestResult(ce);
		}

		return TestResult.NO_DATA;
	}

	/**
	 * @see Persister#persist(Event)
	 */
	@Override
	public void persist(final Event event) {
		if (this.database == null || this.clazz == null || this.stmt == null) {
			return;
		}

		try {
			/*
			 * sqlStatementClazz.getMethod("%Execute") always returns null
			 * because methods with varargs are not projected to Java (PL 58630).
			 */
			final CacheMethod executeMethod = this.clazz.getMethod("ExecuteSqlStatement");
			final RegisteredObject sqlStatementResult = executeStmt(event, this.stmt, executeMethod);
			final int sqlCode = getSqlCode(sqlStatementResult);
			if (sqlCode != 0) {
				System.out.println("%SQLCODE = " + sqlCode);
			}
		} catch (final CacheException ce) {
			out.println(ce.getMessage());
		}
	}

	/**
	 * @see CacheObjBindingPersister#tearDown()
	 */
	@Override
	public void tearDown() {
		this.stmt = null;

		super.tearDown();
	}

	/**
	 * Executes an already prepared {@code %SQL.Statement},
	 * passing an {@code event} in.
	 *
	 * @param event
	 * @param stmt
	 * @param executeMethod
	 * @return a proxy for {@code %SQL.StatementResult}.
	 * @throws CacheException
	 */
	private static RegisteredObject executeStmt(final Event event, final RegisteredObject stmt, final CacheMethod executeMethod)
	throws CacheException {
		return new EventConsumer<RegisteredObject>() {
			/**
			 * @see EventConsumer#consume(Dataholder, Dataholder, Dataholder, Dataholder, Dataholder)
			 */
			@Override
			protected RegisteredObject consume(final Dataholder ticker, final Dataholder per, final Dataholder timestamp, final Dataholder last, final Dataholder vol)
			throws CacheException {
				return (RegisteredObject) executeMethod.invoke(null, new Object[] {
						stmt,
						ticker,
						per,
						timestamp,
						last,
						vol,
				});
			}
		}.consume(event);
	}

	/**
	 * @param sqlStatementResult
	 * @throws CacheException
	 */
	@SuppressWarnings("unused")
	private static int getSqlCode(final RegisteredObject sqlStatementResult) throws CacheException {
		final String fieldName = "%SQLCODE";
		return false
				? ((Integer) sqlStatementResult.getField(fieldName)).intValue() // The 1st implementation results in debug output to stdout.
				: sqlStatementResult.getProxy().getProperty(fieldName, false).getIntValue();
	}
}
