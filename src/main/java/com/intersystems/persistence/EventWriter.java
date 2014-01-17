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
		try {
			if (this.buffer.size() + serialized.length <= this.maximumBatchLength) {
				this.buffer.write(serialized);
			} else {
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
		final OutputStream out1 = new GZIPOutputStream(out0);
		try {
			out1.write(batch);
			out1.flush();
		} finally {
			out1.close();
		}
		return out0.toByteArray();
	}
}
