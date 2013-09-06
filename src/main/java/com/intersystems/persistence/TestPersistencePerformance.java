/*-
 * $Id$
 */
package com.intersystems.persistence;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.FILES_ONLY;
import static javax.swing.JFileChooser.OPEN_DIALOG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
abstract class TestPersistencePerformance {
	private static boolean INCLUDE_WARM_UP_CYCLE = false;

	private static final Persister PERSISTERS[] = {
		new InMemoryPersister(true),

		new DerbyPersister(false),
		new DerbyPersister(true),
		/*
		 * Oracle 9i
		 */
		new OraclePersister("mintaka", 1521, "MINTAKA", "XEP", "XEP", false),
		new OraclePersister("mintaka", 1521, "MINTAKA", "XEP", "XEP", true),
		new OraclePersister("mintaka", 1521, "MINTAKA", "XEPNOLOGGING", "XEP", false),
		new OraclePersister("mintaka", 1521, "MINTAKA", "XEPNOLOGGING", "XEP", true),
		/*
		 * Oracle 11g
		 */
		new OraclePersister("rigel", 1521, "ORCL", "XEP", "XEP", false),
		new OraclePersister("rigel", 1521, "ORCL", "XEP", "XEP", true),
		/*
		 * Oracle 11g XE
		 */
		new OraclePersister("hatsya", 1521, "XE", "XEP", "XEP", false),
		new OraclePersister("hatsya", 1521, "XE", "XEP", "XEP", true),

		new Cach\u00e9JdbcPersister("localhost", 56776, "XEP", "_SYSTEM", "SYS", false),
		new Cach\u00e9JdbcPersister("localhost", 56776, "XEP", "_SYSTEM", "SYS", true),
		new Cach\u00e9ExtremePersister("XEP", "_SYSTEM", "SYS", false, false),
		new Cach\u00e9ExtremePersister("XEP", "_SYSTEM", "SYS", false, false, "localhost", 56776),
	};

	private TestPersistencePerformance() {
		assert false;
	}

	private static void process0(final Persister persister, final File ... files) throws IOException {
		for (final File file : files) {
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
			} finally {
				in.close();
			}
		}
	}

	private static void process(final Persister persister, final File ... files) throws IOException {
		final long t0 = currentTimeMillis();
		long count = 0;
		for (final File file : files) {
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
		}
		final long t1 = currentTimeMillis();
		final double seconds = (t1 - t0) / 1e3;
		System.out.println(count + " event(s) in " + seconds + " second(s) (" + (long) (count / seconds) + " eps).");
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String args[]) throws IOException {
		final File selectedFiles[];
		if (args.length == 0) {
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(getProperty("user.dir")));
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(FILES_ONLY);
			fileChooser.setDialogType(OPEN_DIALOG);
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "Comma-Separated Values (*.csv)";
				}

				@Override
				public boolean accept(final File f) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
				}
			});
			final int option = fileChooser.showOpenDialog(null);
			if (option != APPROVE_OPTION) {
				return;
			}
			selectedFiles = fileChooser.getSelectedFiles();
		} else {
			selectedFiles = new File[args.length];
			for (int i = 0, n = args.length; i < n; i++) {
				selectedFiles[i] = new File(args[i]);
			}
		}

		for (final Persister persister : PERSISTERS) {
			System.out.println(persister.getName());

			/*
			 * Dry-run warm-up cycle
			 */
			if (INCLUDE_WARM_UP_CYCLE) {
				persister.setUp(false);
				for (int i = 0; i < 10; i++) {
					process0(persister, selectedFiles);
				}
				persister.dispose(false);
			}

			persister.setUp(true);
			process(persister, selectedFiles);
			persister.dispose(true);
			System.out.println();
		}
	}
}
