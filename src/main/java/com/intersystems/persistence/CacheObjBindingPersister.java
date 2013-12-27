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
 * <p>Objects are instantiated server-side using {@code %New()},
 * downloaded to the client, initialized with data and saved
 * using {@code %Save()}.</p>
 *
 * <p>Modes which should be implemented:
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
 * </p>
 *
 * <p>Server-side classes required:
 * <ul>
 * 	<li>{@code com.intersystems.persistence.objbinding.Event}</li>
 * 	<li>{@code com.intersystems.persistence.objbinding.PersistenceManager}</li>
 * </ul>
 * </p>
 *
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public class CacheObjBindingPersister extends CacheJdbcPersister {
	private static final String CACHE_CLASS_NAME = "com.intersystems.persistence.objbinding.Event";

	private static final Object NO_ARGS[] = new Object[0];

	protected Database database;

	protected CacheClass clazz;

	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public CacheObjBindingPersister(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		super(new CacheObjBindingConnectionParameters(host,
				port,
				namespace,
				username,
				password,
				autoCommit));
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "InterSystems Cach\u00e9 Object Binding " + VersionInfo.getClientVersion() + " (auto-commit: " + this.connectionParameters.getAutoCommit() + ')';
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
		if (this.database == null || this.clazz == null) {
			return;
		}

		try {
			final CacheMethod factoryMethod = this.clazz.getMethod("%New");
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
			final Persistent e = (Persistent) factoryMethod.invoke(null, new Object[] {null});
			final CacheObject proxy = e.getProxy();
			fillProxy(proxy, event);
			final int statusCode = e.save();
			if (statusCode != 1) {
				out.println("%Save() returned " + statusCode);
			}
		} catch (final CacheException ce) {
			out.println(ce.getMessage());
		}
	}

	/**
	 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
	 */
	protected static abstract class EventConsumer<T> {
		protected EventConsumer() {
			// empty
		}

		/**
		 * @param ticker
		 * @param per
		 * @param timestamp
		 * @param last
		 * @param vol
		 * @throws CacheException
		 */
		protected abstract T consume(Dataholder ticker,
				Dataholder per,
				Dataholder timestamp,
				Dataholder last,
				Dataholder vol)
		throws CacheException;

		/**
		 * @param event
		 * @throws CacheException
		 */
		protected final T consume(final Event event) throws CacheException {
			return this.consume(new Dataholder(event.ticker),
					new Dataholder(Integer.valueOf(event.per)),
					new Dataholder(new Timestamp(event.getTimestamp().getTime())),
					new Dataholder(Double.valueOf(event.last)),
					new Dataholder(Long.valueOf(event.vol)));
		}
	}

	/**
	 * @param proxy
	 * @param event
	 * @throws CacheException
	 */
	private static void fillProxy(final CacheObject proxy, final Event event) throws CacheException {
		new EventConsumer<Object>() {
			/**
			 * @see EventConsumer#consume(Dataholder, Dataholder, Dataholder, Dataholder, Dataholder)
			 */
			@Override
			protected Object consume(final Dataholder ticker, final Dataholder per, final Dataholder timestamp, final Dataholder last, final Dataholder vol)
			throws CacheException {
				proxy.setProperty("Ticker", ticker);
				proxy.setProperty("Per", per);
				proxy.setProperty("TimeStamp", timestamp);
				proxy.setProperty("Last", last);
				proxy.setProperty("Vol", vol);
				return null;
			}
		}.consume(event);
	}

	/**
	 * @see JdbcPersister#getInsertSql()
	 */
	@Override
	protected String getInsertSql() {
		throw new UnsupportedOperationException();
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
