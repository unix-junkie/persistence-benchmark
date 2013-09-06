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
public final class InMemoryPersister implements Persister {
	public int hash;

	private final boolean keepEvents;

	private List<Event> events;

	public InMemoryPersister(final boolean keepEvents) {
		this.keepEvents = keepEvents;
	}

	/**
	 * @see Persister#getName()
	 */
	@Override
	public String getName() {
		return "In-Memory Persister (" + (this.keepEvents ? "keeping" : "discarding") + " events)";
	}

	/**
	 * @see Persister#setUp(boolean)
	 */
	@Override
	public void setUp(final boolean debug) {
		this.events = this.keepEvents
				? new ArrayList<Event>()
				: Collections.<Event>emptyList();
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
	 * @see com.intersystems.persistence.Persister#dispose()
	 */
	@Override
	public void dispose(final boolean debug) {
		this.events.clear();
	}
}
