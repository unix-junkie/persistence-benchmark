/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.intersystems.persistence.Cach\u00e9ExtremeConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class Cach\u00e9ExtremeTcpConnectionParametersPanel extends Cach\u00e9ExtremeConnectionParametersPanel {
	private static final long serialVersionUID = -5382938842192808463L;

	/**
	 * @param connectionParameters
	 */
	public Cach\u00e9ExtremeTcpConnectionParametersPanel(final Cach\u00e9ExtremeConnectionParameters connectionParameters) {
		super(connectionParameters);

		this.setLayout(new GridLayout());

		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Connection", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.add(panel);
		panel.setLayout(new GridBagLayout());

		final JLabel lblNewLabel = new JLabel("Namespace");
		final GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);

		final JTextField textFieldNamespace = new JTextField();
		lblNewLabel.setLabelFor(textFieldNamespace);
		textFieldNamespace.setEditable(false);
		textFieldNamespace.setText(connectionParameters.getNamespace());
		final GridBagConstraints gbc_textFieldNamespace = new GridBagConstraints();
		gbc_textFieldNamespace.weightx = 1.0;
		gbc_textFieldNamespace.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldNamespace.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldNamespace.gridx = 1;
		gbc_textFieldNamespace.gridy = 0;
		panel.add(textFieldNamespace, gbc_textFieldNamespace);
		textFieldNamespace.setColumns(10);

		final JLabel lblUsername = new JLabel("Username:");
		final GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.WEST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 1;
		panel.add(lblUsername, gbc_lblUsername);

		final JTextField textFieldUsername = new JTextField();
		lblUsername.setLabelFor(textFieldUsername);
		textFieldUsername.setEditable(false);
		textFieldUsername.setText(connectionParameters.getUser());
		final GridBagConstraints gbc_textFieldUsername = new GridBagConstraints();
		gbc_textFieldUsername.weightx = 1.0;
		gbc_textFieldUsername.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUsername.gridx = 1;
		gbc_textFieldUsername.gridy = 1;
		panel.add(textFieldUsername, gbc_textFieldUsername);
		textFieldUsername.setColumns(10);

		final JLabel lblPassword = new JLabel("Password:");
		final GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.weighty = 1.0;
		gbc_lblPassword.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblPassword.insets = new Insets(0, 0, 0, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 2;
		panel.add(lblPassword, gbc_lblPassword);

		final JPasswordField passwordField = new JPasswordField();
		lblPassword.setLabelFor(passwordField);
		passwordField.setEditable(false);
		passwordField.setText(connectionParameters.getPassword());
		final GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.weightx = 1.0;
		gbc_passwordField.anchor = GridBagConstraints.NORTH;
		gbc_passwordField.weighty = 1.0;
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 2;
		panel.add(passwordField, gbc_passwordField);

		final JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Connection over TCP", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.add(panel_1);
		panel_1.setLayout(new GridBagLayout());

		final JLabel lblHost = new JLabel("Host:");
		final GridBagConstraints gbc_lblHost = new GridBagConstraints();
		gbc_lblHost.insets = new Insets(0, 0, 5, 5);
		gbc_lblHost.anchor = GridBagConstraints.EAST;
		gbc_lblHost.gridx = 0;
		gbc_lblHost.gridy = 0;
		panel_1.add(lblHost, gbc_lblHost);

		final JTextField textFieldHost = new JTextField();
		lblHost.setLabelFor(textFieldHost);
		textFieldHost.setEditable(false);
		textFieldHost.setText(connectionParameters.getHost());
		final GridBagConstraints gbc_textFieldHost = new GridBagConstraints();
		gbc_textFieldHost.weightx = 1.0;
		gbc_textFieldHost.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldHost.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldHost.gridx = 1;
		gbc_textFieldHost.gridy = 0;
		panel_1.add(textFieldHost, gbc_textFieldHost);
		textFieldHost.setColumns(10);

		final JLabel lblPort = new JLabel("Port:");
		final GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblPort.weighty = 1.0;
		gbc_lblPort.insets = new Insets(0, 0, 0, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 1;
		panel_1.add(lblPort, gbc_lblPort);

		final JSpinner spinner = new JSpinner();
		lblPort.setLabelFor(spinner);
		spinner.setEnabled(false);
		spinner.setModel(new SpinnerNumberModel(0, 0, 65535, 1));
		spinner.setValue(Integer.valueOf(connectionParameters.getPort()));
		final GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.weightx = 1.0;
		gbc_spinner.anchor = GridBagConstraints.NORTHWEST;
		gbc_spinner.weighty = 1.0;
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 1;
		panel_1.add(spinner, gbc_spinner);

		final JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Misc", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.add(panel_2);
		panel_2.setLayout(new GridBagLayout());

		final JCheckBox chckbxUseFlatSchema = new JCheckBox("Use Flat Schema");
		chckbxUseFlatSchema.setEnabled(false);
		chckbxUseFlatSchema.setSelected(connectionParameters.isFlatSchema());
		final GridBagConstraints gbc_chckbxUseFlatSchema = new GridBagConstraints();
		gbc_chckbxUseFlatSchema.weightx = 1.0;
		gbc_chckbxUseFlatSchema.anchor = GridBagConstraints.WEST;
		gbc_chckbxUseFlatSchema.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxUseFlatSchema.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxUseFlatSchema.gridx = 0;
		gbc_chckbxUseFlatSchema.gridy = 0;
		panel_2.add(chckbxUseFlatSchema, gbc_chckbxUseFlatSchema);

		final JCheckBox chckbxSuspendJournalling = new JCheckBox("Suspend Journalling (2013.2+)");
		chckbxSuspendJournalling.setEnabled(false);
		chckbxSuspendJournalling.setSelected(connectionParameters.getSuspendJournalling());
		final GridBagConstraints gbc_chckbxSuspendJournalling = new GridBagConstraints();
		gbc_chckbxSuspendJournalling.weightx = 1.0;
		gbc_chckbxSuspendJournalling.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxSuspendJournalling.weighty = 1.0;
		gbc_chckbxSuspendJournalling.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxSuspendJournalling.gridx = 0;
		gbc_chckbxSuspendJournalling.gridy = 1;
		panel_2.add(chckbxSuspendJournalling, gbc_chckbxSuspendJournalling);
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Cach\u00e9 eXtreme (TCP)";
	}
}