/*-
 * $Id$
 */
package com.intersystems.persistence;

import static com.intersys.objects.Database.RET_PRIM;
import static java.lang.System.out;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.util.Map;

import junit.framework.AssertionFailedError;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.intersys.cache.Dataholder;
import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.objects.CacheException;
import com.intersys.objects.Database;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;

/**
 * Requires that {@code com.intersystems.persistence.objbinding.StringStackTest}
 * class is loaded in Cach\u00e9.
 *
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
@SuppressWarnings("static-method")
public final class StringStackTest {
	private static final String CACHE_CLASS_NAME = "com.intersystems.persistence.objbinding.StringStackTest";

	private static Database database;

	private static CacheClass clazz;

	@BeforeClass
	public static void setUp() throws CacheException {
		database = new JBindDatabase("jdbc:Cache://localhost:56777/XEP", "_SYSTEM", "SYS");
		clazz = database.getCacheClass(CACHE_CLASS_NAME);
	}

	@AfterClass
	public static void tearDown() throws CacheException {
		clazz = null;
		final Map<?, ?> m = database.close();
		if (!m.isEmpty()) {
			out.println(m);
		}
		database = null;
	}

	@Test
	public void testAssertionStatus() {
		try {
			assert false : "Assertions are enabled";
			fail();
		} catch (final AssertionFailedError afe) {
			throw afe;
		} catch (final AssertionError ae) {
			assertTrue(ae.getMessage(), true);
		}
	}

	private static final String TEST_MAX_STRING_NAME = "TestMaxString";

	/**
	 * Call a Cache' ClassMethod ({@code TestMaxString(%String)}), variant A.
	 *
	 * @param s
	 * @return the length of the string
	 * @throws CacheException
	 */
	private static int testMaxStringA(final String s) throws CacheException {
		return ((Integer) clazz.getMethod(TEST_MAX_STRING_NAME).invoke(null, new String[] {s})).intValue();
	}

	/**
	 * Call a Cache' ClassMethod ({@code TestMaxString(%String)}), variant B.
	 *
	 * @param s
	 * @return the length of the string
	 * @throws CacheException
	 */
	private static int testMaxStringB(final String s) throws CacheException {
		return database.runClassMethod(
	        		CACHE_CLASS_NAME,
	        		TEST_MAX_STRING_NAME,
	        		new Dataholder[] {new Dataholder(s)},
	        		RET_PRIM).getInteger().intValue();
	}


	/**
	 * @throws CacheException
	 */
	private static String getLongStringLocal() throws CacheException {
		/*
		 * Request the maximum string length from Cache'...
		 */
		final CacheMethod getMaxStringLength = clazz.getMethod("GetMaxStringLength");
		/*
		 * Maximum string length allowed in Cache' is 3641144
		 * Maximum string length we can *receive* is the same
		 * Maximum Unicode string length we can successfully *send* is only 3608373
		 * (for plain ASCII, the length limit is still 3641144).
		 *
		 * The overhead is 32771 (32k and 3 more bytes).
		 */
		final int maxStringLength = ((Integer) getMaxStringLength.invoke(null, new Object[0])).intValue();

		/*
		 * ... and build the corresponding string in the local JVM.
		 */
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < maxStringLength; i++) {
			stringBuilder.append((char) ('\u0410' + (char) (i % (32 * 2)))); // The length of Cyrillic alphabet without the \u0401\u0451 (YO) character is 32
		}

		/*
		 * Make sure the length of the resulting string is exactly the same as the value returned by Cache'
		 */
		assertEquals(maxStringLength, stringBuilder.length());

		return stringBuilder.toString();
	}

	/**
	 * @throws CacheException
	 */
	private static String getLongStringRemote() throws CacheException {
		return (String) clazz.getMethod("GetMaxString").invoke(null, new Object[0]);
	}

	/**
	 * This test will fail until PL 116022 is fixed.
	 *
	 * @throws CacheException
	 */
	@Test
	@Ignore("This test will fail until PL 116022 is fixed.")
	public void testMaxString() throws CacheException {
		/*
		 * Get the longest possible string from Cache'
		 */
		final String s0 = getLongStringRemote();

		/*
		 * Make sure this is exactly the same string as the one produced locally.
		 */
		assertEquals(s0, getLongStringLocal());

		/*
		 * Pass the same string back to Cache'
		 */
		assertEquals(s0.length(), testMaxStringA(s0));
		assertEquals(s0.length(), testMaxStringB(s0));
	}
}
