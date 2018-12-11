/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.lang.System.getProperty;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.intersys.util.VersionInfo;
import com.intersys.xep.EventPersister;
import com.intersys.xep.PersisterFactory;
import com.intersys.xep.XEPException;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheExtremePersister extends AbstractPersister {
	private final CacheExtremeConnectionParameters connectionParameters;

	private EventPersister persister;

	private com.intersys.xep.Event persisterEvent;

	/**
	 * @param namespace
	 * @param user
	 * @param password
	 * @param suspendJournalling
	 * @param flatSchema
	 * @param host
	 * @param port
	 */
	public CacheExtremePersister(final String namespace,
			final String user,
			final String password,
			final boolean suspendJournalling,
			final boolean flatSchema,
			final String host,
			final int port) {
		this.connectionParameters = new CacheExtremeConnectionParameters(namespace,
				user,
				password,
				suspendJournalling,
				flatSchema,
				host,
				port);
	}

	/**
	 * @param namespace
	 * @param user
	 * @param password
	 * @param suspendJournalling
	 * @param flatSchema
	 */
	public CacheExtremePersister(final String namespace,
			final String user,
			final String password,
			final boolean suspendJournalling,
			final boolean flatSchema) {
		this(namespace, user, password, suspendJournalling, flatSchema, null, -1);
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "InterSystems Cach\u00e9 eXTreme " + VersionInfo.getClientVersion()
				+ " (" + (this.connectionParameters.useShm() ? "SHM" : "TCP") + "; "
				+ (this.connectionParameters.getSuspendJournalling() ? '-' : '+') + "J; "
				+ (this.connectionParameters.isFlatSchema() ? "flat" : "full") + " schema)";
	}

	/**
	 * @see Persister#setUp()
	 */
	@Override
	public TestResult setUp() {
		this.persister = PersisterFactory.createPersister();

		final String namespace = this.connectionParameters.getNamespace();
		final String user = this.connectionParameters.getUser();
		final String password = this.connectionParameters.getPassword();
		if (this.connectionParameters.useShm()) {
			final String osVersion = getProperty("os.version");
			final String osName = getProperty("os.name");
			final String javaSpecificationVersion = getProperty("java.specification.version");
			final String javaVmSpecificationVersion = getProperty("java.vm.specification.version");
			final boolean isMacOsX = osName.equals("Mac OS X");
			final boolean isJava16 = javaSpecificationVersion.equals("1.6") && javaVmSpecificationVersion.equals("1.6");
			if (isMacOsX && !isJava16) {
				/*
				 * PL 115230
				 */
				return new TestResult("On " + osName + ' ' + osVersion + ", JNI connection is unstable when using Java " + javaSpecificationVersion);
			}

			try {
				this.persister.connect(namespace, user, password);
			} catch (final GlobalsException ge) {
				/*
				 * Most probably, an UnsatisfiedLinkError
				 */
				return new TestResult(ge);
			}
		} else {
			try {
				this.persister.connect(this.connectionParameters.getHost(),
						this.connectionParameters.getPort(), namespace, user, password);
			} catch (final OutOfMemoryError oome) {
				/*
				 * Java heap size needs to be at least 768m
				 */
				return new TestResult(oome);
			} catch (final XEPException xepe) {
				/*
				 * Connection failed.
				 */
				return new TestResult(xepe);
			}
		}
		try {
			final DatabaseMetaData metaData = this.persister.getMetaData();
			this.setServerVersion(metaData.getDatabaseProductName() + ", version " + metaData.getDatabaseProductVersion() + " at " + metaData.getURL() + '\n'
					+ this.persister.callClassMethod("%SYSTEM.Version", "GetVersion"));
			this.setRunning(true);

			final int major = metaData.getDatabaseMajorVersion();
			final int minor = metaData.getDatabaseMinorVersion();
			final boolean is20132Plus = major > 2013 || major == 2013 && minor >= 2;
			if (this.connectionParameters.getSuspendJournalling() && is20132Plus) {
				/*
				 * We're only suspending journalling on 2014.1+ (former 2013.2, see below).
				 */
				this.persister.callProcedure("DISABLE", "%NOJRN");
			}

			final String cach\u00e9ClassName = Event.class.getName();

			/*
			 * This can trigger "XEP error: 400" error if client and server versions mismatch,
			 * and "<SYNTAX>MethodCalls+20^%apiXEP" if server version
			 * is lower than 2013.2.0.246.0
			 * and journalling is disabled (see AND1350).
			 */
			this.persister.deleteExtent(cach\u00e9ClassName);

			if (this.connectionParameters.isFlatSchema()) {
				this.persister.importSchema(cach\u00e9ClassName);
			} else {
				this.persister.importSchemaFull(cach\u00e9ClassName);
			}

			this.persisterEvent = this.persister.getEvent(cach\u00e9ClassName);

			return TestResult.NO_DATA;
		} catch (final Exception e) {
			return new TestResult(e);
		}
	}

	/**
	 * @see Persister#persist(Event)
	 */
	@Override
	public void persist(final Event event) {
		if (!this.isConnected()) {
			return;
		}

		try {
			this.persisterEvent.store(event);
		} catch (final Exception e) {
			e.printStackTrace(System.out);
			final Throwable cause = e.getCause();
			if (cause != null) {
				System.out.println("--------------");
				cause.printStackTrace(System.out);
			}
		}
	}

	/**
	 * @see Persister#tearDown()
	 */
	@Override
	public void tearDown() {
		try {
			if (this.persisterEvent != null) {
				this.persisterEvent.close();
			}

			if (this.connectionParameters.getSuspendJournalling() && this.isConnected()) {
				this.persister.callProcedure("ENABLE", "%NOJRN");
			}
		} catch (final Exception e) {
			e.printStackTrace(System.out);
			final Throwable cause = e.getCause();
			if (cause != null) {
				System.out.println("--------------");
				cause.printStackTrace(System.out);
			}
		} finally {
			if (this.persister != null) {
				try {
					this.persister.close();
				} catch (final SQLException sqle) {
					sqle.printStackTrace(System.out);
				}
			}

			this.persister = null;
			this.persisterEvent = null;

			this.setRunning(false);
		}
	}

	private boolean isConnected() {
		return this.persisterEvent != null;
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public CacheExtremeConnectionParameters getConnectionParameters() {
		return this.connectionParameters;
	}
}
