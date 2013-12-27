/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.lang.System.out;

import com.intersys.cache.Dataholder;
import com.intersys.objects.CacheException;
import com.intersys.objects.StatusCode;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;
import com.intersys.util.VersionInfo;

/**
 * Objects are inserted using {@code %Library.ResultSet}.
 *
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheResultSetPersister extends CacheJdbcOverObjectBindingPersister {
	@SuppressWarnings("deprecation")
	private com.intersys.classes.ResultSet rset;

	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public CacheResultSetPersister(final String host,
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
		return "InterSystems Cach\u00e9 Object Binding " + VersionInfo.getClientVersion() + " (auto-commit: " + this.connectionParameters.getAutoCommit() + "; %Library.ResultSet)";
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
			this.rset = this.newResultSet();
			/*
			 * Calling ResultSet._prepare(...) with arbitrary arguments leads to
			 * <PARAMETER>Prepare^%SYS.DynamicQuery
			 * because %ResultSet.Prepare(...) sends 17 (1+16) arguments
			 * to Prepare^%SYS.DynamicQuery(...) while it only accepts 6 (1+5).
			 *
			 * On the other hand, resultSetClazz.getMethod("Prepare") returns null,
			 * because methods with varargs are not projected to Java (PL 58630).
			 */
			final CacheMethod prepareMethod = this.clazz.getMethod("PrepareResultSet");
			final StatusCode status = (StatusCode) prepareMethod.invoke(null, new Object[] {this.rset, this.getInsertSql()});
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
		if (this.database == null || this.clazz == null || this.rset == null) {
			return;
		}

		try {
			this.rset._execute(toArray(event));
		} catch (final CacheException ce) {
			out.println(ce.getMessage());
		}
	}

	/**
	 * @see CacheObjBindingPersister#tearDown()
	 */
	@Override
	public void tearDown() {
		this.rset = null;

		super.tearDown();
	}

	/**
	 * Converts an {@code event} to an array of {@link Dataholder}s.
	 *
	 * @param event
	 * @return an array of {@link Dataholder}s holding event data.
	 * @throws CacheException
	 */
	private static Dataholder[] toArray(final Event event) throws CacheException {
		return new EventConsumer<Dataholder[]>() {
			/**
			 * @see EventConsumer#consume(Dataholder, Dataholder, Dataholder, Dataholder, Dataholder)
			 */
			@Override
			protected Dataholder[] consume(final Dataholder ticker, final Dataholder per, final Dataholder timestamp, final Dataholder last, final Dataholder vol)
			throws CacheException {
				final Dataholder args0[] = new Dataholder[5];
				args0[0] = ticker;
				args0[1] = per;
				args0[2] = timestamp;
				args0[3] = last;
				args0[4] = vol;
				return args0;
			}
		}.consume(event);
	}

	/**
	 * @throws CacheException
	 */
	@SuppressWarnings("deprecation")
	private com.intersys.classes.ResultSet newResultSet() throws CacheException {
		final CacheClass resultSetClazz = this.database.getCacheClass("%Library.ResultSet");
		final CacheMethod factoryMethod = resultSetClazz.getMethod("%New");
		return (com.intersys.classes.ResultSet) factoryMethod.invoke(null, new String[] {"%DynamicQuery:SQL"});
	}
}
