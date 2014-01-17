/*-
 * $Id$
 */
package com.intersystems.persistence;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public interface EventBatchProcessor {
	/**
	 * @param compressedBatch the gzip-compressed series of events.
	 */
	void processBatch(final byte compressedBatch[]);
}
