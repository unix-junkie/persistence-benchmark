/*-
 * $Id$
 */
package com.intersystems.persistence;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class TestResult {
	/**
	 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
	 */
	private static enum TestStatus {
		READY,
		SUCCESS,
		FAILURE,
	}

	private final TestStatus status;

	private final String message;

	private final long numEvents;

	private final double seconds;

	public TestResult() {
		this.status = TestStatus.READY;
		this.message = this.status.name();
		this.numEvents = 0;
		this.seconds = 0;
	}

	/**
	 * @param t
	 */
	public TestResult(final Throwable t) {
		this.status = TestStatus.FAILURE;
		this.message = t.getMessage();
		this.numEvents = 0;
		this.seconds = 0;
	}

	/**
	 * @param numEvents
	 * @param timeMillis
	 */
	public TestResult(final long numEvents, final long timeMillis) {
		this.status = TestStatus.SUCCESS;
		this.message = null;
		this.numEvents = numEvents;
		this.seconds = timeMillis / 1e3;
	}

	public long getRateEps() {
		return (long) (this.numEvents / this.seconds);
	}

	public String getMessage() {
		switch (this.status) {
		case SUCCESS:
			return this.numEvents + " event(s) in " + this.seconds + " second(s) (" + this.getRateEps() + " eps)";
		default:
			return this.message;
		}
	}

	/**
	 * Either ready or success.
	 *
	 * @return
	 */
	public boolean isSuccessful() {
		return this.status != TestStatus.FAILURE;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return this.getMessage();
	}
}
