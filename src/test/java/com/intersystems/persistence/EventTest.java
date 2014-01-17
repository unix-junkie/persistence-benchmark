/*-
 * $Id$
 */
package com.intersystems.persistence;

import static com.intersystems.persistence.Event.readAll;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class EventTest {
	@Test
	@SuppressWarnings("static-method")
	public void testEquals() {
		final Event e = new Event("foo", 0, new Date(), .0d, 0L);
		assertTrue(e.equals(e));
		assertFalse(e.equals(null));
	}

	/**
	 * @throws IOException
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testSerialization() throws IOException {
		final Event e0 = new Event("This is a veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery looooooooooooooooooong ASCII string                                                                               ",
				0,
				new Date(),
				.0d,
				0L);
		final byte serializedSingle[] = e0.toByteArray();
		final Event e1 = Event.valueOf(serializedSingle);
		assertNotSame(e0, e1);
		assertEquals(e0, e1);
		assertEquals(e0, readAll(serializedSingle)[0]);

		final int maxStringLength = 3000000;
		final ByteArrayOutputStream out = new ByteArrayOutputStream(maxStringLength);
		int eventCount = 0;
		try {
			final int serializedLength = serializedSingle.length;
			/*
			 * In real life, once the buffer is full we'll be
			 * compressing its contents and resetting the buffer itself.
			 */
			while (out.size() + serializedLength <= maxStringLength) {
				out.write(serializedSingle);
				eventCount++;
			}
		} finally {
			out.flush();
			out.close();
		}

		assertTrue(out.size() <= maxStringLength);
		final byte serializedMultiple[] = out.toByteArray();
		try {
			Event.valueOf(serializedMultiple);
			fail("Reading a single event from a long byte array should fail");
		} catch (final IllegalArgumentException iae) {
			assertTrue(true);
		}
		System.out.println(eventCount + " event(s) written; array length " + out.size());
		assertEquals(eventCount, readAll(serializedMultiple).length);
	}
}
