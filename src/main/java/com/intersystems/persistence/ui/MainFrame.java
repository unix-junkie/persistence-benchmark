/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.lang.System.exit;
import static java.lang.System.getProperty;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.FILES_ONLY;
import static javax.swing.JFileChooser.OPEN_DIALOG;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.intersystems.persistence.Persister;
import com.intersystems.persistence.TestPersistencePerformance;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class MainFrame extends JFrame {
	private static final long serialVersionUID = -517096334257830254L;

	JButton btnRun;

	JProgressBar progressBar;

	JSpinner spinner;


	File selectedFiles[];

	/**
	 * @param persisters
	 * @param connectionParameters
	 */
	public MainFrame(final List<Persister> persisters,
			final List<ConnectionParametersPanel<?>> connectionParameters) {
		this.setTitle("Persistence Performance");
		final GridBagLayout gridBagLayout = new GridBagLayout();
		this.getContentPane().setLayout(gridBagLayout);

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Connection properties:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.weightx = 1.0;
		gbc_tabbedPane.anchor = GridBagConstraints.NORTH;
		gbc_tabbedPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_tabbedPane.insets = new Insets(5, 10, 5, 10);
		gbc_tabbedPane.gridwidth = REMAINDER;
		this.getContentPane().add(tabbedPane, gbc_tabbedPane);

		final JPanel generalSettings = new JPanel();
		final GridBagLayout gbl_generalSettings = new GridBagLayout();
		generalSettings.setLayout(gbl_generalSettings);

		tabbedPane.addTab("General", generalSettings);
		for (final ConnectionParametersPanel<?> connectionParameter : connectionParameters) {
			tabbedPane.addTab(connectionParameter.getName(), connectionParameter);
		}

		final JLabel lblNewLabel = new JLabel("Dry-run cycles:");
		lblNewLabel.setToolTipText("The number of dry-run cycles to warm the JVM up");
		final GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		generalSettings.add(lblNewLabel, gbc_lblNewLabel);

		this.spinner = new JSpinner();
		this.spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		lblNewLabel.setLabelFor(this.spinner);
		final GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(5, 0, 5, 5);
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.weightx = 1.0;
		gbc_spinner.gridwidth = REMAINDER;
		generalSettings.add(this.spinner, gbc_spinner);

		this.btnRun = new JButton("Run");
		final GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.weighty = 1.0;
		gbc_btnRun.fill = GridBagConstraints.VERTICAL;
		gbc_btnRun.insets = new Insets(0, 10, 5, 5);
		gbc_btnRun.gridwidth = RELATIVE;
		gbc_btnRun.gridheight = RELATIVE;
		this.getContentPane().add(this.btnRun, gbc_btnRun);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 10);
		gbc_scrollPane.gridwidth = REMAINDER;
		gbc_scrollPane.gridheight = RELATIVE;
		this.getContentPane().add(scrollPane, gbc_scrollPane);

		final JPanel statusBar = new JPanel();
		final GridBagConstraints gbc_statusBar = new GridBagConstraints();
		gbc_statusBar.insets = new Insets(0, 10, 10, 10);
		gbc_statusBar.weightx = 1.0;
		gbc_statusBar.anchor = GridBagConstraints.NORTH;
		gbc_statusBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_statusBar.gridwidth = REMAINDER;
		gbc_statusBar.gridheight = REMAINDER;
		this.getContentPane().add(statusBar, gbc_statusBar);

		final DefaultListModel listModel = new DefaultListModel();
		int i = 0;
		for (final Persister persister : persisters) {
			listModel.addElement(persister);
			persister.setListModel(listModel, i++);
		}

		final JList list = new JList();
		list.setModel(listModel);
		list.setCellRenderer(new PersisterListCellRenderer());
		scrollPane.setViewportView(list);

		this.btnRun.setDefaultCapable(false);
		this.btnRun.setEnabled(false);
		this.btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				MainFrame.this.btnRun.setEnabled(false);
				MainFrame.this.progressBar.setValue(0);

				TestPersistencePerformance.submit(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						int j = 0;
						for (final Persister persister : persisters) {
							/*
							 * Dry-run warm-up cycle
							 */
							final int warmUpCycles = ((Integer) MainFrame.this.spinner.getValue()).intValue();

							final int cycles = warmUpCycles + 1;

							persister.setUp();
							for (int k = 0; k < cycles; k++) {
								TestPersistencePerformance.process(persister, MainFrame.this.selectedFiles);
							}
							persister.tearDown();

							final int value = ++j;
							invokeLater(new Runnable() {
								/**
								 * @see Runnable#run()
								 */
								@Override
								public void run() {
									MainFrame.this.progressBar.setValue(value);
								}
							});
						}

						invokeLater(new Runnable() {
							@Override
							public void run() {
								MainFrame.this.btnRun.setEnabled(true);
							}
						});
					}
				});
			}
		});

		statusBar.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		statusBar.setLayout(new BorderLayout(5, 5));

		final JLabel lblOverallProgress = new JLabel("Overall Progress:");
		statusBar.add(lblOverallProgress, BorderLayout.WEST);

		this.progressBar = new JProgressBar();
		this.progressBar.setMaximum(persisters.size());
		this.progressBar.setStringPainted(true);

		lblOverallProgress.setLabelFor(this.progressBar);
		statusBar.add(this.progressBar, BorderLayout.CENTER);

		final JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		final JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		final JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
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
				MainFrame.this.selectedFiles = fileChooser.getSelectedFiles();
				MainFrame.this.btnRun.setEnabled(MainFrame.this.selectedFiles.length > 0);
			}
		});
		mntmOpen.setMnemonic('O');
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpen);

		final JSeparator separator = new JSeparator();
		mnFile.add(separator);

		final JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				exit(0);
			}
		});
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		mntmExit.setMnemonic('X');
		mnFile.add(mntmExit);
	}
}
