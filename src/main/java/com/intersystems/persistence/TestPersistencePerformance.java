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

import com.intersystems.persistence.ui.Cach\u00e9JdbcConnectionParametersPanel;
import com.intersystems.persistence.ui.ConnectionParametersPanel;
import com.intersystems.persistence.ui.DerbyConnectionParametersPanel;
import com.intersystems.persistence.ui.MainFrame;
import com.intersystems.persistence.ui.OracleConnectionParametersPanel;

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
		final DerbyPersister derbyPersister = new DerbyPersister(true);

		/*
		 * Oracle 9i
		 */
//		final OraclePersister oraclePersister = new OraclePersister("mintaka", 1521, "MINTAKA", "XEP", "XEP", false);
//		final OraclePersister oraclePersister = new OraclePersister("mintaka", 1521, "MINTAKA", "XEP", "XEP", true);
//		final OraclePersister oraclePersister = new OraclePersister("mintaka", 1521, "MINTAKA", "XEPNOLOGGING", "XEP", false);
//		final OraclePersister oraclePersister = new OraclePersister("mintaka", 1521, "MINTAKA", "XEPNOLOGGING", "XEP", true);
		/*
		 * Oracle 11g
		 */
//		final OraclePersister oraclePersister = new OraclePersister("rigel", 1521, "ORCL", "XEP", "XEP", false);
//		final OraclePersister oraclePersister = new OraclePersister("rigel", 1521, "ORCL", "XEP", "XEP", true);
		/*
		 * Oracle 11g XE
		 */
//		final OraclePersister oraclePersister = new OraclePersister("hatsya", 1521, "XE", "XEP", "XEP", false);
		final OraclePersister oraclePersister = new OraclePersister("hatsya", 1521, "XE", "XEP", "XEP", true);

//		final Cach\u00e9JdbcPersister cach\u00e9JdbcPersister = new Cach\u00e9JdbcPersister("ashcheglov", 56777, "XEP", "_SYSTEM", "SYS", false);
		final Cach\u00e9JdbcPersister cach\u00e9JdbcPersister = new Cach\u00e9JdbcPersister("ashcheglov", 56777, "XEP", "_SYSTEM", "SYS", true);

		final Persister persisters[] = {
			derbyPersister,
			oraclePersister,
			cach\u00e9JdbcPersister,

			new Cach\u00e9ExtremePersister("XEP", "_SYSTEM", "SYS", false, false),
			new Cach\u00e9ExtremePersister("XEP", "_SYSTEM", "SYS", false, false, "ashcheglov", 56777),
			new Cach\u00e9ExtremePersister("XEP", "_SYSTEM", "SYS", true, false),
			new Cach\u00e9ExtremePersister("XEP", "_SYSTEM", "SYS", true, false, "ashcheglov", 56777),
		};

		try {
			setLookAndFeel(getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			// ignore
		}

		final ConnectionParametersPanel<?> connectionProperties[] = {
			new DerbyConnectionParametersPanel(derbyPersister.getConnectionParameters()),
			new OracleConnectionParametersPanel(oraclePersister.getConnectionParameters()),
			new Cach\u00e9JdbcConnectionParametersPanel(cach\u00e9JdbcPersister.getConnectionParameters()),
		};

		final MainFrame frame = new MainFrame(asList(persisters),
				asList(connectionProperties));
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setBounds(getLocalGraphicsEnvironment().getMaximumWindowBounds());
		frame.validate();
		frame.setVisible(true);
	}
}
