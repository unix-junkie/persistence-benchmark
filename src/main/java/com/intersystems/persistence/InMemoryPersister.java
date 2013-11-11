/*-
 * $Id$
 */
package com.intersystems.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class InMemoryPersister extends AbstractPersister {
	public int hash;

	private final boolean keepEvents;

	private List<Event> events;

	public InMemoryPersister(final boolean keepEvents) {
		this.keepEvents = keepEvents;
	}

	/**
	 * @see Persister#getClientVersion()
	 */
	@Override
	public String getClientVersion() {
		return "In-Memory Persister (" + (this.keepEvents ? "keeping" : "discarding") + " events)";
	}

	/**
	 * @see Persister#setUp()
	 */
	@Override
	public TestResult setUp() {
		this.events = this.keepEvents
				? new ArrayList<Event>()
				: Collections.<Event>emptyList();
		this.setRunning(true);
		return TestResult.NO_DATA;
	}

	/**
	 * @see Persister#persist(Event)
	 */
	@Override
	public void persist(final Event event) {
		if (this.keepEvents) {
			this.events.add(event);
		} else {
			this.hash ^= event.hashCode();
		}
	}

	/**
	 * @see Persister#tearDown()
	 */
	@Override
	public void tearDown() {
		this.events.clear();
		this.setRunning(false);
	}

	/**
	 * @see Persister#getConnectionParameters()
	 */
	@Override
	public ConnectionParameters getConnectionParameters() {
		throw new UnsupportedOperationException();
	}
}
