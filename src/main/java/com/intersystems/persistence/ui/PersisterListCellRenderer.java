/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import com.intersystems.persistence.Persister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class PersisterListCellRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = -2082144226505905593L;

	private final JTextField clientVersion;

	private final JTextArea serverVersion;
	private final JProgressBar progressBar;

	public PersisterListCellRenderer() {
		final GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);

		final JLabel lblClientVersion = new JLabel("Client Version:");
		final GridBagConstraints gbc_lblClientVersion = new GridBagConstraints();
		gbc_lblClientVersion.insets = new Insets(5, 5, 5, 5);
		gbc_lblClientVersion.anchor = GridBagConstraints.WEST;
		gbc_lblClientVersion.gridwidth = RELATIVE;
		this.add(lblClientVersion, gbc_lblClientVersion);

		this.clientVersion = new JTextField();
		this.clientVersion.setEditable(false);
		final GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(5, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridwidth = REMAINDER;
		this.add(this.clientVersion, gbc_textField);
		this.clientVersion.setColumns(10);

		final JLabel lblServerVersion = new JLabel("Server Version:");
		final GridBagConstraints gbc_lblServerVersion = new GridBagConstraints();
		gbc_lblServerVersion.weighty = 1.0;
		gbc_lblServerVersion.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblServerVersion.insets = new Insets(0, 5, 5, 5);
		gbc_lblServerVersion.gridwidth = RELATIVE;
		this.add(lblServerVersion, gbc_lblServerVersion);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = REMAINDER;
		this.add(scrollPane, gbc_scrollPane);

		this.serverVersion = new JTextArea();
		this.serverVersion.setEditable(false);
		scrollPane.setViewportView(this.serverVersion);

		this.progressBar = new JProgressBar();
		this.progressBar.setStringPainted(true);
		final GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.weightx = 1.0;
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.fill = GridBagConstraints.BOTH;
		gbc_progressBar.insets = new Insets(0, 5, 5, 5);
		gbc_progressBar.gridwidth = REMAINDER;
		gbc_progressBar.gridheight = RELATIVE;
		this.add(this.progressBar, gbc_progressBar);

		final JSeparator separator = new JSeparator();
		final GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = REMAINDER;
		gbc_separator.gridheight = REMAINDER;
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.weightx = 1.0;
		gbc_separator.insets = new Insets(0, 5, 5, 5);
		this.add(separator, gbc_separator);
	}

	/**
	 * @see ListCellRenderer#getListCellRendererComponent(JList, Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
		final Persister persister = (Persister) value;
		this.clientVersion.setText(persister.getClientVersion());
		this.serverVersion.setText(persister.getServerVersion());
		this.progressBar.setIndeterminate(persister.isRunning());
		this.progressBar.setString(persister.getTestResultMessage());

		return this;
	}
}
