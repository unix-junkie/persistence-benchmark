/*-
 * $Id$
 */
package com.intersystems.persistence;

import java.sql.DatabaseMetaData;

import com.intersys.globals.GlobalsException;
import com.intersys.util.VersionInfo;
import com.intersys.xep.EventPersister;
import com.intersys.xep.PersisterFactory;
import com.intersys.xep.annotations.Id;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9ExtremePersister extends AbstractPersister {
	private final String namespace;

	private final String user;

	private final String password;


	/**
	 * Whether to suspend journalling when inserting (<em>unsafe</em>).
	 * The default is {@false}.
	 */
	private final boolean suspendJournalling;

	private final boolean flatSchema;


	private final String host;

	private final int port;


	private EventPersister persister;

	private com.intersys.xep.Event persisterEvent;

	/**
	 * @param namespace
	 * @param user
	 * @param password
	 * @param suspendJournalling
	 * @param flatSchema whether generated Cach\u00e9 classes should be final (flat schema)
	 * or inheritable (full schema). Flat schema is the default.
	 *
	 * <ul>
	 *
	 * <li>Flat schema:</li>
	 * <ul>
	 * <li>{@code $lb(...)} structure is correct in case identifiers are
	 * {@linkplain Id#generated() generated}
	 * (the first field is {@code %%CLASSNAME} which is blank);</li>
	 * <li>storage definition is <em>not</em> correct
	 * ({@code %%CLASSNAME} is <em>always</em> missing from the storage,
	 * field numbering starts from 1 (should be 2));</li>
	 * <li>SQL results are correct in case identifiers are <em>not</em> generated.</li>
	 * </ul>
	 *
	 * <li>Full schema:</li>
	 * <ul>
	 * <li>{@code $lb(...)} structure is <em>not</em> correct
	 * (first <em>2</em> entries are blank, field values start from the 3rd entry)
	 * unless identifiers are <em>not</em> generated
	 * (this is fixed in <em>2014.1+</em> with
	 * <a href = "http://turbo.iscinternal.com/prodlog/devview.csp?Key=AND1380">AND1380</a>);</li>
	 * <li>storage definition is correct;</li>
	 * <li>SQL results are correct in case identifiers are <em>not</em> generated.</li>
	 * </ul>
	 *
	 * </ul>
	 * @param host
	 * @param port
	 */
	public Cach\u00e9ExtremePersister(final String namespace,
			final String user,
			final String password,
			final boolean suspendJournalling,
			final boolean flatSchema,
			final String host,
			final int port) {
		this.namespace = namespace;
		this.user = user;
		this.password = password;

		this.suspendJournalling = suspendJournalling;
		this.flatSchema = flatSchema;

		this.host = host;
		this.port = port;
	}

	public Cach\u00e9ExtremePersister(final String namespace,
			final String user,
			final String password,
			final boolean suspendJournalling,
			final boolean flatSchema) {
		this(namespace, user, password, suspendJournalling, flatSchema, null, -1);
	}

	public Cach\u00e9ExtremePersister(final String namespace,
			final String user,
			final String password) {
		this(namespace, user, password, false, true, null, -1);
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "InterSystems Cach\u00e9 eXtreme " + VersionInfo.getClientVersion()
				+ " (" + (this.useShm() ? "SHM" : "TCP") + "; "
				+ (this.suspendJournalling ? '-' : '+') + "J; "
				+ (this.flatSchema ? "flat" : "full") + " schema)";
	}

	/**
	 * @see Persister#setUp()
	 */
	@Override
	public void setUp() {
		this.persister = PersisterFactory.createPersister();

		if (this.useShm()) {
			try {
				this.persister.connect(this.namespace, this.user, this.password);
			} catch (final GlobalsException ge) {
				/*
				 * Most probably, an UnsatisfiedLinkError
				 */
				System.out.println(ge.getMessage());
				return;
			}
		} else {
			try {
				this.persister.connect(this.host, this.port, this.namespace, this.user, this.password);
			} catch (final OutOfMemoryError oome) {
				System.out.println(oome.getMessage());
				return;
			}
		}
		try {
			final DatabaseMetaData metaData = this.persister.getJDBCConnection().getMetaData();
			this.setServerVersion(metaData.getDatabaseProductName() + ", version " + metaData.getDatabaseProductVersion() + " at " + metaData.getURL() + '\n'
					+ this.persister.callClassMethod("%SYSTEM.Version", "GetVersion"));
			this.setRunning(true);

			final int major = metaData.getDatabaseMajorVersion();
			final int minor = metaData.getDatabaseMinorVersion();
			final boolean is20132Plus = major > 2013 || major == 2013 && minor >= 2;
			if (this.suspendJournalling && is20132Plus) {
				/*
				 * We're only suspending journalling on 2013.2+ (see below).
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

			if (this.flatSchema) {
				this.persister.importSchema(cach\u00e9ClassName);
			} else {
				this.persister.importSchemaFull(cach\u00e9ClassName);
			}

			this.persisterEvent = this.persister.getEvent(cach\u00e9ClassName);
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

			if (this.suspendJournalling && this.isConnected()) {
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
				this.persister.close();
			}

			this.persister = null;
			this.persisterEvent = null;

			this.setRunning(false);
		}
	}

	private boolean useShm() {
		return this.host == null || this.host.length() == 0 || this.port < 0 || this.port > 65535;
	}

	private boolean isConnected() {
		return this.persisterEvent != null;
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public Cach\u00e9ExtremeConnectionParameters getConnectionParameters() {
		return new Cach\u00e9ExtremeConnectionParameters();
	}
}
