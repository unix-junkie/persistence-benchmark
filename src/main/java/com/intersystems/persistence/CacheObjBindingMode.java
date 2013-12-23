/*-
 * $Id$
 */
package com.intersystems.persistence;


/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public enum CacheObjBindingMode {
	/**
	 * Objects are instantiated server-side using {@code %New()},
	 * downloaded to the client, initialized with data and saved
	 * using {@code %Save()}.
	 */
	PERSISTENT_PROXIES("%Library.Persistent Proxies"),

	/**
	 * Objects are inserted using {@code %Library.ResultSet}.
	 */
	LIBRARY_RESULT_SET("%Library.ResultSet"),

	/**
	 * Objects are inserted using {@code %SQL.Statement}.
	 */
	SQL_STATEMENT("%SQL.Statement");

	private final String description;

	/**
	 * @param description
	 */
	private CacheObjBindingMode(final String description) {
		this.description = description;
	}

	/**
	 * @see Enum#toString()
	 */
	@Override
	public String toString() {
		return this.description;
	}
}
