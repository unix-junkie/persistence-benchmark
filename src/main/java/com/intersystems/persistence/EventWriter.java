/*-
 * $Id$
 */
package com.intersystems.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class EventWriter {
	private final EventBatchProcessor batchProcessor;

	private final int maximumBatchLength;

	private final ByteArrayOutputStream buffer;

	/**
	 * @param batchProcessor
	 * @param maximumBatchLength
	 */
	public EventWriter(final EventBatchProcessor batchProcessor,
			final int maximumBatchLength) {
		if (batchProcessor == null || maximumBatchLength <= 0) {
			throw new IllegalArgumentException();
		}

		this.batchProcessor = batchProcessor;
		this.maximumBatchLength = maximumBatchLength;
		this.buffer = new ByteArrayOutputStream(this.maximumBatchLength);
	}

	/**
	 * @param event
	 */
	public void write(final Event event) {
		final byte serialized[] = event.toByteArray();
		if (serialized.length > this.maximumBatchLength) {
			throw new IllegalArgumentException("Event too long: " + serialized.length + " > " + this.maximumBatchLength);
		}
		if (this.buffer.size() + serialized.length > this.maximumBatchLength) {
			this.flush();
		}
		try {
			this.buffer.write(serialized);
		} catch (final IOException ioe) {
			/*
			 * Never.
			 */
		}
	}

	/**
	 * Flushes this writer's internal buffer, passing any events
	 * contained in the buffer to the {@link EventBatchProcessor}
	 * supplied at creation time.
	 */
	public void flush() {
		try {
			if (this.buffer.size() > 0) {
				this.buffer.flush();
				this.buffer.close();
				final byte batch[] = this.buffer.toByteArray();
				this.buffer.reset();
				this.batchProcessor.processBatch(compressBatch(batch));
			}
		} catch (final IOException ioe) {
			/*
			 * Never.
			 */
		}
	}

	/**
	 * @param batch
	 * @throws IOException
	 */
	private static byte[] compressBatch(final byte batch[]) throws IOException {
		final ByteArrayOutputStream out0 = new ByteArrayOutputStream();
		try (final OutputStream out1 = new GZIPOutputStream(out0)) {
			out1.write(batch);
			out1.flush();
		}
		return out0.toByteArray();
	}
}
