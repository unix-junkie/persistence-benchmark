/*-
 * $Id$
 */
package com.intersystems.persistence;

import java.io.UnsupportedEncodingException;

import com.intersys.objects.CacheException;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;
import com.intersys.util.VersionInfo;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class FastObjBindingPersister extends CacheObjBindingPersister {
	static final String PERSISTENCE_MANAGER_CLASS_NAME = "com.intersystems.persistence.objbinding.PersistenceManager";

	static final String GET_MAX_STRING_LENGTH_METHOD_NAME = "GetMaxStringLength";

	private static final String SAVE_METHOD_NAME = "Save";

	private CacheClass persistenceManagerClass;

	CacheMethod save;

	private EventWriter out;

	private final EventBatchProcessor batchProcessor = compressedBatch -> {
		try {
			FastObjBindingPersister.this.save.invoke(null, new String[] {new String(compressedBatch, "ISO-8859-1")});
		} catch (final CacheException ce) {
			ce.printStackTrace(System.out);
		} catch (final UnsupportedEncodingException uee) {
			uee.printStackTrace(System.out);
		}
	};

	/**
	 * @param host
	 * @param port
	 * @param namespace
	 * @param username
	 * @param password
	 * @param autoCommit
	 */
	public FastObjBindingPersister(final String host,
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
		return "Fast InterSystems Cach\u00e9 Object Binding " + VersionInfo.getClientVersion() + " (auto-commit: " + this.connectionParameters.getAutoCommit() + ')';
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public final CacheObjBindingConnectionParameters getConnectionParameters() {
		/*
		 * No empty panels until they're implemented.
		 */
		return null;
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
			this.persistenceManagerClass = this.database.getCacheClass(PERSISTENCE_MANAGER_CLASS_NAME);
			final CacheMethod getMaxStringLength = this.persistenceManagerClass.getMethod(GET_MAX_STRING_LENGTH_METHOD_NAME);
			final int maxStringLength = ((Integer) getMaxStringLength.invoke(null, new Object[0])).intValue();
			this.out = new EventWriter(this.batchProcessor, maxStringLength);
			this.save = this.persistenceManagerClass.getMethod(SAVE_METHOD_NAME);
		} catch (final CacheException ce) {
			return new TestResult(ce);
		}

		return TestResult.NO_DATA;
	}

	/**
	 * @see CacheObjBindingPersister#persist(Event)
	 */
	@Override
	public void persist(final Event event) {
		if (this.database == null || this.clazz == null
				|| this.persistenceManagerClass == null
				|| this.save == null
				|| this.out == null) {
			return;
		}

		this.out.write(event);
	}

	/**
	 * @see CacheObjBindingPersister#tearDown()
	 */
	@Override
	public void tearDown() {
		if (this.out != null) {
			this.out.flush();
			this.out = null;
		}

		this.save = null;

		if (this.persistenceManagerClass != null) {
			this.persistenceManagerClass.close();
			this.persistenceManagerClass = null;
		}

		super.tearDown();
	}
}
