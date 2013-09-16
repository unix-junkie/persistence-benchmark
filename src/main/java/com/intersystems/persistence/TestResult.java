/*-
 * $Id$
 */
package com.intersystems.persistence;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class TestResult {
	private final long numEvents;

	private final double seconds;

	/**
	 * @param numEvents
	 * @param timeMillis
	 */
	public TestResult(final long numEvents, final long timeMillis) {
		this.numEvents = numEvents;
		this.seconds = timeMillis / 1e3;
	}

	public long getRateEps() {
		return (long) (this.numEvents / this.seconds);
	}

	public String getMessage() {
		return this.numEvents + " event(s) in " + this.seconds + " second(s) (" + this.getRateEps() + " eps)";
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return this.getMessage();
	}
}
