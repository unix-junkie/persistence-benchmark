/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.lang.System.currentTimeMillis;
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
				final BufferedReader in = new BufferedReader(new FileReader(file));
				try {
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
				} finally {
					in.close();
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
	 * @throws IOException
	 */
	public static void main(final String args[]) {
		final String host = "localhost";
		final int cachePort = 1972;
		final String cacheNamespace = "USER";
		final String cacheUsername = "_SYSTEM";
		final String cachePassword = "SYS";

		final Persister persisters[] = {
			new DerbyPersister("XEP", true),
			new OraclePersister(host, 1521, "XE", "SYSTEM", "SYSTEM", true),
			new Cach\u00e9JdbcPersister(host, cachePort, cacheNamespace, cacheUsername, cachePassword, true),
			new Cach\u00e9ExtremePersister(cacheNamespace, cacheUsername, cachePassword, false, false),
			new Cach\u00e9ExtremePersister(cacheNamespace, cacheUsername, cachePassword, false, false, host, cachePort),
			new Cach\u00e9ExtremePersister(cacheNamespace, cacheUsername, cachePassword, true, false),
			new Cach\u00e9ExtremePersister(cacheNamespace, cacheUsername, cachePassword, true, false, host, cachePort),
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
