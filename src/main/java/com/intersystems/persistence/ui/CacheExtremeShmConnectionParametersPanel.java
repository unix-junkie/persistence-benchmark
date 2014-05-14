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
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.intersystems.persistence.CacheExtremeConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class CacheExtremeShmConnectionParametersPanel extends CacheExtremeConnectionParametersPanel {
	private static final long serialVersionUID = 2173001890984252740L;

	/**
	 * @param connectionParameters
	 */
	public CacheExtremeShmConnectionParametersPanel(final CacheExtremeConnectionParameters connectionParameters) {
		super(connectionParameters);


		this.setLayout(new GridLayout());

		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Connection", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.add(panel);
		panel.setLayout(new GridBagLayout());

		final JLabel lblNewLabel = new JLabel("Namespace:");
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

		final JCheckBox chckbxSuspendJournalling = new JCheckBox("Suspend Journalling (2014.1+)");
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
		return "Cach\u00e9 eXtreme (SHM)";
	}
}