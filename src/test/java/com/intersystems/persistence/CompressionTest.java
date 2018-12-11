/*-
 * $Id$
 */
package com.intersystems.persistence;

import static com.intersystems.persistence.FastObjBindingPersister.GET_MAX_STRING_LENGTH_METHOD_NAME;
import static com.intersystems.persistence.FastObjBindingPersister.PERSISTENCE_MANAGER_CLASS_NAME;
import static java.lang.Integer.getInteger;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.lang.System.out;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import junit.framework.AssertionFailedError;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.objects.CacheException;
import com.intersys.objects.Database;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
@SuppressWarnings("static-method")
public final class CompressionTest {
	private static Database database;

	private static CacheClass clazz;

	/**
	 * @throws CacheException
	 */
	@BeforeClass
	public static void setUp() throws CacheException {
		final String host = getProperty("benchmark.host", "localhost");
		final int cachePort = getInteger("benchmark.cache.port", 1972).intValue();
		final String cacheNamespace = getProperty("benchmark.cache.namespace", "USER");
		final String cacheUsername = getProperty("benchmark.cache.username", "_SYSTEM");
		final String cachePassword = getProperty("benchmark.cache.password", "SYS");

		database = new JBindDatabase(format("jdbc:Cache://%s:%d/%s", host, cachePort, cacheNamespace),
				cacheUsername,
				cachePassword);
		clazz = database.getCacheClass(PERSISTENCE_MANAGER_CLASS_NAME);
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
	 * @param cacheClass
	 * @throws CacheException
	 */
	static String getLongString(final CacheClass cacheClass) throws CacheException {
		final CacheMethod getMaxStringLength = cacheClass.getMethod(GET_MAX_STRING_LENGTH_METHOD_NAME);
		final int maxStringLength = ((Integer) getMaxStringLength.invoke(null, new Object[0])).intValue();
		final StringBuilder stringBuilder = new StringBuilder(maxStringLength);
		for (int i = 0; i < maxStringLength; i++) {
			stringBuilder.append((char) ('\u0410' + (char) (i % (32 * 2)))); // The length of Cyrillic alphabet without the \u0401\u0451 (YO) character is 32
		}
		assertEquals(maxStringLength, stringBuilder.length());
		return stringBuilder.toString();
	}

	/**
	 * @throws CacheException
	 * @throws IOException
	 */
	@Test
	public void testAsciiCompression() throws CacheException, IOException {
		final String data[] = {
				"ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
				"\0",
				"something\0with\0null-terminators\0",
		};


		for (final String s : data) {
			testCompression(s);
		}
	}

	/**
	 * Not testing "\0" here, as Cache' will send it back as an empty string.
	 *
	 * @throws CacheException
	 * @throws IOException
	 */
	@Test
	public void testAsciiDecompression() throws CacheException, IOException {
		final String data[] = {
				"ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
				"",
				"something\0with\0null-terminators\0",
		};


		for (final String s : data) {
			testDecompression(s);
		}
	}

	/**
	 * @throws CacheException
	 * @throws IOException
	 */
	@Test
	public void testUnicodeCompression() throws CacheException, IOException {
		testCompression("\u0410\u0411");
		testCompression(getLongString(clazz));
	}

	/**
	 * @throws CacheException
	 * @throws IOException
	 */
	@Test
	public void testUnicodeDecompression() throws CacheException, IOException {
		testDecompression("\u0410\u0411");
	}

	/**
	 * Requires at least Cache 2013.1.4 to pass (see <a href =
	 * "http://turbo.iscinternal.com/prodlog/devview.csp?Key=HYY1886">HYY1886</a>).
	 *
	 * @throws CacheException
	 * @throws IOException
	 * @see <a href = "http://turbo.iscinternal.com/prodlog/devview.csp?Key=HYY1886">HYY1886</a>
	 */
	@Test
	public void testUnicodeDecompressionLong() throws CacheException, IOException {
		testDecompression(getLongString(clazz));
	}

	/**
	 * @throws CacheException
	 */
	@Test
	public void testFailedCompression() throws CacheException {
		/*
		 * $system.Util.Compress() -- Cache'-proprietary wrapper over ZLIB (RFC1950)
		 *
		 * Uses a 3-byte header instead of 0x1f8b0800000000000003
		 * used by gzip, and a 5-byte tail (gzip uses 8 bytes).
		 */
		final CacheMethod method = database.getCacheClass("%SYSTEM.Util").getMethod("Compress");
		final String compressedA = (String) method.invoke(null, new String[] {"ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ"});
		try {
			new GZIPInputStream(new ByteArrayInputStream(compressedA.getBytes("ISO-8859-1")));
			assert false;
		} catch (final IOException ioe) {
			// ignore
		}
	}

	/**
	 * @param original
	 * @throws CacheException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static void testCompression(final String original)
	throws CacheException, IOException {
		final byte clientCompressed[] = compressClient(original);
		assertEquals(original, decompressClient(clientCompressed).toString());
		final byte compressed[] = compressServer(original);
		/*
		 * Don't compare compressed data, as it may have different length indeed.
		 */
		assertTrue(true || Arrays.equals(clientCompressed, compressed));
		final String decompressed = decompressClient(compressed).toString();
		final int length = original.length();
		if (length > 80) {
			/*
			 * On long strings, fail early.
			 */
			assertEquals(length, decompressed.length());
		}
		assertEquals(original, decompressed);
	}

	/**
	 * @param original
	 * @throws CacheException
	 * @throws IOException
	 */
	private static void testDecompression(final String original)
	throws CacheException, IOException {
		final byte compressed[] = compressClient(original);
		final String decompressed = decompressServer(compressed);
		final int length = original.length();
		if (length > 80) {
			/*
			 * On long strings, fail early.
			 */
			assertEquals(length, decompressed.length());
		}
		assertEquals(decompressClient(compressed).toString(), decompressed);
		assertEquals(original, decompressed);
	}

	/**
	 * Server-side standard gzip compression. Requires {@code
	 * com.intersystems.persistence.objbinding.PersistenceManager} class
	 * to be loaded in Cach\u00e9.
	 *
	 * @param original
	 * @throws CacheException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] compressServer(final String original)
	throws CacheException, UnsupportedEncodingException {
		if (original.length() == 0) {
			/*
			 * Cache' will receive an empty string as "\0".
			 */
			throw new IllegalArgumentException();
		}

		final CacheMethod method = clazz.getMethod("Compress");
		return ((String) method.invoke(null, new String[] {original})).getBytes("ISO-8859-1");
	}

	/**
	 * Server-side standard gzip decompression. Requires {@code
	 * com.intersystems.persistence.objbinding.PersistenceManager} class
	 * to be loaded in Cach\u00e9.
	 *
	 * @param compressed
	 * @throws CacheException
	 * @throws UnsupportedEncodingException
	 */
	private static String decompressServer(final byte compressed[])
	throws CacheException, UnsupportedEncodingException {
		if (compressed.length == 0) {
			/*
			 * Cache' will receive an empty string as "\0".
			 */
			throw new IllegalArgumentException();
		}

		final CacheMethod method = clazz.getMethod("Decompress");
		final String decompressed = (String) method.invoke(null, new String[] {new String(compressed, "ISO-8859-1")});
		/*
		 * An empty string in Cache' becomes a null reference in Java
		 */
		return decompressed == null ? "" : decompressed;
	}

	/**
	 * Client-side compression utility
	 *
	 * @param original
	 * @throws IOException
	 */
	private static byte[] compressClient(final String original)
	throws IOException {
		final ByteArrayOutputStream out0 = new ByteArrayOutputStream();
		/*
		 * Don't rely on system-default encoding when performing char to byte conversion.
		 */
		try (final BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(out0), "UTF-8"))) {
			out1.write(original);
			out1.flush();
		}
		return out0.toByteArray();
	}

	/**
	 * Client-side decompression utility
	 *
	 * @param compressed
	 * @throws IOException
	 */
	private static CharSequence decompressClient(final byte compressed[])
	throws IOException {
		final StringBuilder builder = new StringBuilder();
		/*
		 * Don't rely on system-default encoding when performing byte to char conversion.
		 */
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(compressed)), "UTF-8"))) {
			String line;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
		}

		return builder;
	}

	/**
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void testHexEnconding() throws UnsupportedEncodingException {
		assertEquals("ff80", hexEncoded("\u00ff\u0080").toString());
	}

	/**
	 * @param binaryString
	 * @throws UnsupportedEncodingException
	 */
	static CharSequence hexEncoded(final String binaryString) throws UnsupportedEncodingException {
		return hexEncoded(binaryString.getBytes("ISO-8859-1"));
	}

	/**
	 * @param binaryString
	 */
	private static CharSequence hexEncoded(final byte binaryString[]) {
		final StringBuilder builder = new StringBuilder();
		for (final byte c : binaryString) {
			final String hex = Integer.toHexString(c & 0xff);
			(hex.length() == 2 ? builder : builder.append('0')).append(hex);
		}
		return builder;
	}
}
