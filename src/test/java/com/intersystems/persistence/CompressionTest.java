/*-
 * $Id$
 */
package com.intersystems.persistence;

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
	private static final String CACHE_CLASS_NAME = "com.intersystems.persistence.objbinding.PersistenceManager";

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
	 * @param cacheClass
	 * @throws CacheException
	 */
	static String getLongString(final CacheClass cacheClass) throws CacheException {
		final CacheMethod getMaxStringLength = cacheClass.getMethod("GetMaxStringLength");
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
	 * This test will fail until PL 116875 and PL 116876 are fixed.
	 *
	 * @throws CacheException
	 * @throws IOException
	 */
	@Test
	public void testUnicodeDecompression() throws CacheException, IOException {
		testDecompression("\u0410\u0411");
		/*
		 * PL 116875 and PL 116876:
		 * <SYSTEM>zDecompressImpl+18^com.intersystems.persistence.objbinding.PersistenceManager.1
		 */
		testDecompression(getLongString(clazz));
	}

	/**
	 * @throws CacheException
	 */
	@SuppressWarnings("unused")
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
		final BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(out0), "UTF-8"));
		try {
			out1.write(original);
			out1.flush();
		} finally {
			out1.close();
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
		final BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(compressed)), "UTF-8"));
		try {
			String line;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
		} finally {
			in.close();
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
