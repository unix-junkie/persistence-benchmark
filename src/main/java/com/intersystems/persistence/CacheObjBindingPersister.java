/*-
 * $Id$
 */
package com.intersystems.persistence;

import static com.intersys.cache.jbind.JBindDatabase.getDatabase;
import static java.lang.System.out;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import com.intersys.cache.CacheObject;
import com.intersys.cache.Dataholder;
import com.intersys.classes.Persistent;
import com.intersys.objects.CacheException;
import com.intersys.objects.Database;
import com.intersys.objects.StatusCode;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;
import com.intersys.util.VersionInfo;


/**
 * Modes which should be implemented:
 * <ul>
 * 	<li>a server-side facade method is used which accepts primitive
 * 	parameters (one per objects's field);</li>
 * 	<li>a server-side facade method is used which accepts an object
 * 	serialized into a (binary) string;</li>
 * 	<li>a server-side facade method is used which accepts multiple
 * 	objects serialized into a long (up to {@code $$$MaxStringLength}) binary string;</li>
 * 	<li>a server-side facade method is used which accepts multiple
 * 	objects serialized into a string which is then compressed.</li>
 * </ul>
 *
 * Server-side classes required:
 * <ul>
 * 	<li>{@code com.intersystems.persistence.objbinding.Event}</li>
 * 	<li>{@code com.intersystems.persistence.objbinding.PersistenceManager}</li>
 * </ul>
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheObjBindingPersister extends CacheJdbcPersister {
	private static final String CACHE_CLASS_NAME = "com.intersystems.persistence.objbinding.Event";

	private static final Object NO_ARGS[] = new Object[0];

	private final CacheObjBindingMode mode;

	private Database database;

	private CacheClass clazz;

	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 * @param mode
	 */
	public CacheObjBindingPersister(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit,
			final CacheObjBindingMode mode) {
		super(new CacheObjBindingConnectionParameters(host,
				port,
				namespace,
				username,
				password,
				autoCommit));
		this.mode = mode;
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "InterSystems Cach\u00e9 Object Binding " + VersionInfo.getClientVersion() + " (auto-commit: " + this.connectionParameters.getAutoCommit() + "; " + this.mode + ")";
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public CacheObjBindingConnectionParameters getConnectionParameters() {
		return (CacheObjBindingConnectionParameters) this.connectionParameters;
	}

	/**
	 * @see Persister#setUp()
	 */
	@Override
	public TestResult setUp() {
		try {
			this.initConnection();
		} catch (final ClassNotFoundException cnfe) {
			return new TestResult(cnfe);
		} catch (final SQLException sqle) {
			printExceptionChain(sqle, System.out);

			return new TestResult(sqle);
		}

		try {
			this.database = getDatabase(this.conn);
			this.clazz = this.database.getCacheClass(CACHE_CLASS_NAME);
			final CacheMethod killExtent = this.clazz.getMethod("%KillExtent");
			final StatusCode status = (StatusCode) killExtent.invoke(null, NO_ARGS);
			if (status.isError()) {
				System.out.println(status);
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
		if (this.database == null || this.clazz == null) {
			return;
		}

		try {
			switch (this.mode) {
			case PERSISTENT_PROXIES:
				final CacheMethod newMethod = this.clazz.getMethod("%New");
				/*
				 * If we use a zero-length array here,
				 * we'll receive an IllegalArgumentException.
				 *
				 * Java Object Binding code checks the signature of %RegisteredObject.%New(),
				 * which has exactly one argument w/o any default value.
				 *
				 * Cache, on the other hand, passes the arguments to %OnNew()
				 * in case it is overridden. If the number of
				 * mandatory formal arguments (i. e. arguments w/o default values)
				 * differs from the number of effective arguments,
				 * then <PARAMETER> error is returned.
				 *
				 * Bottom line: if you override %OnNew(), it should
				 * contain only a single mandatory formal argument.
				 */
				final Persistent e = (Persistent) newMethod.invoke(null, new Object[] {null});
				final CacheObject proxy = e.getProxy();
				proxy.setProperty("Ticker", new Dataholder(event.ticker));
				proxy.setProperty("Per", new Dataholder(Integer.valueOf(event.per)));
				proxy.setProperty("TimeStamp", new Dataholder(new Timestamp(event.getTimestamp().getTime())));
				proxy.setProperty("Last", new Dataholder(Double.valueOf(event.last)));
				proxy.setProperty("Vol", new Dataholder(Long.valueOf(event.vol)));
				final int statusCode = e.save();
				if (statusCode != 1) {
					out.println("%Save() returned " + statusCode);
				}
				break;
			case LIBRARY_RESULT_SET:
				break;
			case SQL_STATEMENT:
				break;
			default:
				break;
			}
		} catch (final CacheException ce) {
			out.println(ce.getMessage());
		}
	}

	/**
	 * @see Persister#tearDown()
	 */
	@Override
	public void tearDown() {
		if (this.database != null) {
			if (this.clazz != null) {
				this.clazz.close();
				this.clazz = null;
			}

			try {
				final Map<?, ?> m = this.database.close();
				if (!m.isEmpty()) {
					out.println(m);
				}
			} catch (final CacheException ce) {
				out.println(ce.getMessage());
			}
			this.database = null;
		}

		super.tearDown();
	}
}
