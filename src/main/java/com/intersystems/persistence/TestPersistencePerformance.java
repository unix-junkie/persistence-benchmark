/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.lang.Integer.getInteger;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.intersystems.persistence.ui.MainFrame;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class TestPersistencePerformance {
	private static final ExecutorService BACKGROUND = newSingleThreadExecutor();

	private TestPersistencePerformance() {
		assert false;
	}

	/**
	 * @param task
	 */
	public static void submit(final Runnable task) {
		BACKGROUND.submit(task);
	}

	/**
	 * @param persister
	 * @param files
	 */
	public static void process(final Persister persister, final File ... files) {
		final long t0 = currentTimeMillis();
		long count = 0;
		for (final File file : files) {
			try {
				try (final BufferedReader in = new BufferedReader(new FileReader(file))) {
					String line;
					long localCount = 0;
					while ((line = in.readLine()) != null) {
						/*
						 * Skip the header row.
						 */
						if (localCount++ == 0) {
							continue;
						}
						persister.persist(Event.valueOf(line));
					}
					/*
					 * Account for the header row
					 */
					if (localCount > 0) {
						localCount--;
					}
					count += localCount;
				}
			} catch (final IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		}
		final long t1 = currentTimeMillis();
		persister.setTestResult(new TestResult(count, t1 - t0));
	}

	/**
	 * @param args
	 */
	public static void main(final String args[]) {
		final String host = getProperty("benchmark.host", "localhost");
		final int cachePort = getInteger("benchmark.cache.port", 1972).intValue();
		final String cacheNamespace = getProperty("benchmark.cache.namespace", "USER");
		final String cacheUsername = getProperty("benchmark.cache.username", "_SYSTEM");
		final String cachePassword = getProperty("benchmark.cache.password", "SYS");

		final Persister persisters[] = {
			new DerbyPersister(getProperty("benchmark.derby.database", "XEP"), true),
			new OraclePersister(host,
					getInteger("benchmark.oracle.port", 1521).intValue(),
					getProperty("benchmark.oracle.schema", "XE"),
					getProperty("benchmark.oracle.username", "SYSTEM"),
					getProperty("benchmark.oracle.password", "SYSTEM"),
					true),
			new FastObjBindingPersister(host, cachePort, cacheNamespace, cacheUsername, cachePassword, true),
			new CacheObjBindingPersister(host, cachePort, cacheNamespace, cacheUsername, cachePassword, true),
			new CacheResultSetPersister(host, cachePort, cacheNamespace, cacheUsername, cachePassword, true),
			new CacheSqlStatementPersister(host, cachePort, cacheNamespace, cacheUsername, cachePassword, true),
			new CacheJdbcPersister(host, cachePort, cacheNamespace, cacheUsername, cachePassword, true),
			new CacheExtremePersister(cacheNamespace, cacheUsername, cachePassword, false, false, host, cachePort),
			new CacheExtremePersister(cacheNamespace, cacheUsername, cachePassword, true, false, host, cachePort),
		};

		try {
			setLookAndFeel(getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			// ignore
		}

		final MainFrame frame = new MainFrame(asList(persisters));
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setBounds(getLocalGraphicsEnvironment().getMaximumWindowBounds());
		frame.validate();
		frame.setVisible(true);
	}
}
