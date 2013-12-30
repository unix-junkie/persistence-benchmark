/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;

import java.nio.ByteBuffer;
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
	private static final java.util.concurrent.atomic.AtomicLong ID_GENERATOR = new java.util.concurrent.atomic.AtomicLong();

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

	private static final int EXTRA_LENGTH = 29;

	/**
	 * @param ticker a non-empty ASCII string up to 255 characters long.
	 * @param per
	 * @param timestamp
	 * @param last
	 * @param vol
	 */
	public Event(final String ticker,
			final int per,
			final Date timestamp,
			final double last,
			final long vol) {
		if (ticker == null
				|| ticker.length() == 0
				|| ticker.length() > (0xff & (byte) -1)
				|| timestamp == null) {
			throw new IllegalArgumentException();
		}

		this.ticker = ticker;
		this.per = per;
		this.timestamp = (Date) timestamp.clone();
		this.last = last;
		this.vol = vol;
	}

	/**
	 * @param date
	 * @param time
	 */
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

	/**
	 * @param s
	 */
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
	 * @param data
	 * @see #toByteArray()
	 */
	public static Event valueOf(final byte data[]) {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException();
		}
		final ByteBuffer buffer = wrap(data);
		final int tickerLength = 0xff & buffer.get();
		if (tickerLength + EXTRA_LENGTH != data.length) {
			throw new IllegalArgumentException();
		}
		final StringBuilder ticker = new StringBuilder(tickerLength);
		for (int i = 0; i < tickerLength; i ++) {
			ticker.append((char) buffer.get());
		}
		final int per = buffer.getInt();
		final Date timestamp = new Date(buffer.getLong());
		final double last = buffer.getDouble();
		final long vol = buffer.getLong();
		return new Event(ticker.toString(), per, timestamp, last, vol);
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

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Event) {
			final Event that = (Event) obj;
			return doubleToLongBits(this.last) == doubleToLongBits(that.last)
					&& this.per == that.per
					&& this.ticker.equals(that.ticker)
					&& this.timestamp.equals(that.timestamp)
					&& this.vol == that.vol;
		}
		return false;
	}

	/**
	 * @see #valueOf(byte[])
	 */
	public byte[] toByteArray() {
		final int tickerLength = this.ticker.length();
		final ByteBuffer data = allocate(tickerLength + EXTRA_LENGTH);
		// 0. Ticker length in bytes (event length is constant for the same ticker).
		data.put((byte) tickerLength);
		// 1. Ticker (ASCII is assumed)
		for (int i = 0; i < tickerLength; i++) {
			data.put((byte) this.ticker.charAt(i));
		}
		// 2. Per (4 bytes)
		data.putInt(this.per);
		// 3. Timestamp (8 bytes)
		data.putLong(this.timestamp.getTime());
		// 4. Last (8 bytes)
		data.putDouble(this.last);
		// 5. Vol (8 bytes)
		data.putLong(this.vol);
		return data.array();
	}
}
