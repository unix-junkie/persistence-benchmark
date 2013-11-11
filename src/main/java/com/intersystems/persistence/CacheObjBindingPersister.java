/*-
 * $Id$
 */
package com.intersystems.persistence;

import com.intersys.util.VersionInfo;

/**
 * Modes which should be implemented:
 * <ul>
 * 	<li>objects are inserted using {@code %Library.ResultSet},</li>
 * 	<li>objects are inserted using {@code %SQL.Statement},</li>
 * 	<li>objects are instantiated server-side using {@code %New()},
 * 	downloaded to the client, initialized with data and saved using {@code %Save()};</li>
 * 	<li>a server-side facade method is used which accepts primitive
 * 	parameters (one per objects's field);</li>
 * 	<li>a server-side facade method is used which accepts an object
 * 	serialized into a (binary) string;</li>
 * 	<li>a server-side facade method is used which accepts multiple
 * 	objects serialized into a long (up to {@code $$$MaxStringLength}) binary string;</li>
 * 	<li>a server-side facade method is used which accepts multiple
 * 	objects serialized into a string which is then compressed.</li>
 * </ul>
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheObjBindingPersister extends AbstractPersister {
	private final CacheObjBindingConnectionParameters connectionParameters;

	public CacheObjBindingPersister(final String host,
			final int port,
			final String namespace,
			final String username,
			final String password,
			final boolean autoCommit) {
		this.connectionParameters = new CacheObjBindingConnectionParameters(host,
				port,
				namespace,
				username,
				password,
				autoCommit);
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
 		return "InterSystems Cach\u00e9 " + VersionInfo.getClientVersion() + " (auto-commit: " + this.connectionParameters.getAutoCommit() + ")";
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public CacheObjBindingConnectionParameters getConnectionParameters() {
		return this.connectionParameters;
	}

	/**
	 * @see Persister#setUp()
	 */
	@Override
	public TestResult setUp() {
		return TestResult.NO_DATA;
	}

	/**
	 * @see Persister#persist(Event)
	 */
	@Override
	public void persist(final Event event) {
		// empty
	}

	/**
	 * @see Persister#tearDown()
	 */
	@Override
	public void tearDown() {
		// empty
	}
}
