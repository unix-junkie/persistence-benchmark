/*-
 * $Id$
 */
package com.intersystems.persistence;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;

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

	@Test
	@SuppressWarnings("static-method")
	public void testSerialization() {
		final Event e0 = new Event("This is a veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery looooooooooooooooooong ASCII string                                                                               ",
				0,
				new Date(),
				.0d,
				0L);
		final Event e1 = Event.valueOf(e0.toByteArray());
		assertNotSame(e0, e1);
		assertEquals(e0, e1);
	}
}
