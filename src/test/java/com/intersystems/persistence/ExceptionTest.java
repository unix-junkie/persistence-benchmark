/*-
 * $Id$
 */
package com.intersystems.persistence;

import static com.intersys.objects.Database.RET_PRIM;
import static com.intersystems.persistence.CompressionTest.getLongString;
import static java.lang.System.out;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.util.Map;

import junit.framework.AssertionFailedError;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.intersys.cache.Dataholder;
import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.objects.CacheException;
import com.intersys.objects.CacheServerException;
import com.intersys.objects.Database;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;

/**
 * Requires that {@code com.intersystems.persistence.objbinding.ExceptionTest}
 * class is loaded in Cach\u00e9.
 *
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
@SuppressWarnings("static-method")
public final class ExceptionTest {
	private static final String CACHE_CLASS_NAME = "com.intersystems.persistence.objbinding.ExceptionTest";

	private static Database database;

	private static CacheClass clazz;

	/**
	 * @throws CacheException
	 */
	@BeforeClass
	public static void setUp() throws CacheException {
		database = new JBindDatabase("jdbc:Cache://localhost:56777/XEP", "_SYSTEM", "SYS");
		clazz = database.getCacheClass(CACHE_CLASS_NAME);
	}

	/**
	 * @throws CacheException
	 */
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

	/**
	 * @throws CacheException
	 */
	@Test
	public void testTrap() throws CacheException {
		try {
			final CacheMethod method = clazz.getMethod("ThrowTrap");
			method.invoke(null, new Object[0]);
			fail();
		} catch (final CacheServerException cse) {
			out.println(cse.getMessage());
			assertTrue(true);
		}
	}

	/**
	 * @throws CacheException
	 */
	@Test
	public void testException() throws CacheException {
		try {
			final CacheMethod method = clazz.getMethod("ThrowException");
			method.invoke(null, new Object[0]);
			fail();
		} catch (final CacheServerException cse) {
			out.println(cse.getMessage());
			assertTrue(true);
		}
	}

	/**
	 * @throws CacheException
	 */
	@Test
	public void testError() throws CacheException {
		try {
			final CacheMethod method = clazz.getMethod("ThrowError");
			method.invoke(null, new Object[0]);
			fail();
		} catch (final CacheServerException cse) {
			out.println(cse.getMessage());
			assertTrue(true);
		}
	}

	/**
	 * @throws CacheException
	 */
	@Test
	public void testStatus() throws CacheException {
		try {
			final CacheMethod method = clazz.getMethod("ThrowStatus");
			method.invoke(null, new Object[0]);
			fail();
		} catch (final CacheServerException cse) {
			out.println(cse.getMessage());
			assertTrue(true);
		}
	}

	private static final String TEST_MAX_STRING_NAME = "TestMaxString";

	private static CacheMethod testMaxString;

	/**
	 * @throws CacheException
	 */
	private static CacheMethod getTestMaxString() throws CacheException {
		return testMaxString == null
				? testMaxString = clazz.getMethod(TEST_MAX_STRING_NAME)
				: testMaxString;
	}

	/**
	 * @param s
	 * @throws CacheException
	 */
	private static int testMaxStringA(final String s) throws CacheException {
		return ((Integer) getTestMaxString().invoke(null, new String[] {s})).intValue();
	}

	/**
	 * @param s
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
	@Test
	public void testMaxString() throws CacheException {
		final String s0 = getLongString(clazz);
		final String s1 = s0 + '\u0410';

		assertTrue(s1.length() > s0.length());

		assertEquals(s0.length(), testMaxStringA(s0));
		assertEquals(s0.length(), testMaxStringB(s0));

		try {
			testMaxStringA(s1);
			fail();
		} catch (final CacheServerException cse) {
			assertTrue(cse.getMessage().contains("<STRINGSTACK>"));
			assertEquals(400, cse.getCode());
		}

		try {
			testMaxStringB(s1);
			fail();
		} catch (final CacheServerException cse) {
			assertTrue(cse.getMessage().contains("<STRINGSTACK>"));
			assertEquals(400, cse.getCode());
		}

	}
}
