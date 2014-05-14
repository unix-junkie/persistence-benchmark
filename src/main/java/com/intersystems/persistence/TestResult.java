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
		NO_DATA,
		SUCCESS,
		FAILURE,
	}

	public static final TestResult NO_DATA = new TestResult();

	private final TestStatus status;

	private final String message;

	private final long numEvents;

	private final double seconds;

	private TestResult() {
		this.status = TestStatus.NO_DATA;
		this.message = "";
		this.numEvents = 0;
		this.seconds = 0;
	}

	/**
	 * Constructs a test failure with a custom message.
	 *
	 * @param message
	 */
	public TestResult(final String message) {
		this.status = TestStatus.FAILURE;
		this.message = message;
		this.numEvents = 0;
		this.seconds = 0;
	}

	/**
	 * Constructs a test failure from an exception.
	 *
	 * @param t
	 */
	public TestResult(final Throwable t) {
		this(t.getMessage());
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
	 */
	public boolean isSuccessful() {
		return this.status != TestStatus.FAILURE;
	}

	public boolean isCompleted() {
		return this.status == TestStatus.SUCCESS;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return this.getMessage();
	}
}
