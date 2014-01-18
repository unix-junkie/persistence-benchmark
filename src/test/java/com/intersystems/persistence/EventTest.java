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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class EventTest {
	int batchEventCount;

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
		final ByteArrayOutputStream out0 = new ByteArrayOutputStream(maxStringLength);
		int eventCount = 0;
		try {
			final int serializedLength = serializedSingle.length;
			/*
			 * In real life, once the buffer is full we'll be
			 * compressing its contents and resetting the buffer itself.
			 */
			while (out0.size() + serializedLength <= maxStringLength) {
				out0.write(serializedSingle);
				eventCount++;
			}
		} finally {
			out0.flush();
			out0.close();
		}

		assertTrue(out0.size() <= maxStringLength);
		final byte serializedMultiple[] = out0.toByteArray();
		try {
			Event.valueOf(serializedMultiple);
			fail("Reading a single event from a long byte array should fail");
		} catch (final IllegalArgumentException iae) {
			assertTrue(true);
		}
		System.out.println(eventCount + " event(s) written; array length " + out0.size());
		assertEquals(eventCount, readAll(serializedMultiple).length);

		/*
		 * Now almost the same tests as above,
		 * with a batch processor.
		 */
		this.batchEventCount = 0;
		final EventBatchProcessor batchProcessor = new EventBatchProcessor() {
			/**
			 * @see EventBatchProcessor#processBatch(byte[])
			 */
			@Override
			public void processBatch(final byte compressedBatch[]) {
				try {
					EventTest.this.batchEventCount += readAll(decompress(compressedBatch)).length;
				} catch (final IOException ioe) {
					ioe.printStackTrace();
					fail(ioe.getMessage());
				}
			}
		};
		final EventWriter out1 = new EventWriter(batchProcessor, maxStringLength);
		/*
		 * Make sure we have 1 complete and 1 incomplete batch.
		 */
		final int expectedEventCount = (int) (1.5 * eventCount);
		int i = 0;
		while (i++ < expectedEventCount) {
			out1.write(e0);
		}
		out1.flush();
		assertEquals(expectedEventCount, this.batchEventCount);
	}

	static byte[] decompress(final byte compressed[])
	throws IOException {
		/*
		 * Allocating at least compressed.length bytes.
		 */
		final ByteArrayOutputStream out = new ByteArrayOutputStream(compressed.length);
		try {
			final InputStream in = new GZIPInputStream(new ByteArrayInputStream(compressed), compressed.length);
			try {
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
			} finally {
				in.close();
			}
			out.flush();
		} finally {
			out.close();
		}

		return out.toByteArray();
	}
}
