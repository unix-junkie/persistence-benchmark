/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.intersys.xep.annotations.Id;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Event {
	//*
	private static java.util.concurrent.atomic.AtomicLong ID_GENERATOR = new java.util.concurrent.atomic.AtomicLong();

	@Id(generated = false)
	private final long id = ID_GENERATOR.incrementAndGet();
	/*/
	@Id(generated = true)
	private Long id;
	//*/

	public final String ticker;

	public final int per;

	private final Date timestamp;

	public final double last;

	public final long vol;

	private static final DateFormat FORMAT = new SimpleDateFormat("yyyyMMddHH:mm:ss");

	private static final Object FORMAT_LOCK = new Object();

	public Event(final String ticker,
		final int per,
		final Date timestamp,
		final double last,
		final long vol) {
		this.ticker = ticker;
		this.per = per;
		this.timestamp = (Date) timestamp.clone();
		this.last = last;
		this.vol = vol;
	}

	private static Date parseDate(final String date, final String time) {
		try {
			synchronized (FORMAT_LOCK) {
				return FORMAT.parse(date + time);
			}
		} catch (final ParseException pe) {
			System.out.println(pe.getMessage());
			return new Date();
		}
	}

	public Date getTimestamp() {
		return (Date) this.timestamp.clone();
	}

	public static Event valueOf(final String s) {
		final String[] fields = s.split("\\,");
		if (fields.length != 6) {
			throw new IllegalArgumentException(String.valueOf(fields.length));
		}
		return new Event(fields[0],
				parseInt(fields[1]),
				parseDate(fields[2], fields[3]),
				parseDouble(fields[4].replaceAll("\\ ", "")),
				parseInt(fields[5].replaceAll("\\ ", "")));
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.last);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + this.per;
		result = prime * result + (this.ticker == null ? 0 : this.ticker.hashCode());
		result = prime * result + (this.timestamp == null ? 0 : this.timestamp.hashCode());
		result = prime * result + (int) (this.vol ^ this.vol >>> 32);
		return result;
	}
}
