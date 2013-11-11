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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.intersystems.persistence.JdbcConnectionParameters;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public class JdbcConnectionParametersPanel extends ConnectionParametersPanel {
	private static final long serialVersionUID = -1545324097731349065L;

	/**
	 * @param connectionParameters
	 */
	public JdbcConnectionParametersPanel(final JdbcConnectionParameters connectionParameters) {
		super(connectionParameters);

		this.setLayout(new GridBagLayout());

		final JLabel lblJdbcUrl = new JLabel("JDBC URL:");
		lblJdbcUrl.setDisplayedMnemonic('J');
		final GridBagConstraints gbc_lblJdbcUrl = new GridBagConstraints();
		gbc_lblJdbcUrl.insets = new Insets(5, 5, 5, 5);
		gbc_lblJdbcUrl.anchor = GridBagConstraints.WEST;
		gbc_lblJdbcUrl.gridwidth = RELATIVE;
		this.add(lblJdbcUrl, gbc_lblJdbcUrl);

		final JTextField textFieldJdbcUrl = new JTextField();
		textFieldJdbcUrl.setText(connectionParameters.getUrl());
		textFieldJdbcUrl.setEditable(false);
		lblJdbcUrl.setLabelFor(textFieldJdbcUrl);
		final GridBagConstraints gbc_textFieldJdbcUrl = new GridBagConstraints();
		gbc_textFieldJdbcUrl.weightx = 1.0;
		gbc_textFieldJdbcUrl.insets = new Insets(5, 0, 5, 5);
		gbc_textFieldJdbcUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldJdbcUrl.gridwidth = REMAINDER;
		this.add(textFieldJdbcUrl, gbc_textFieldJdbcUrl);

		final JLabel lblUsername = new JLabel("Username:");
		lblUsername.setDisplayedMnemonic('U');
		final GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.WEST;
		gbc_lblUsername.insets = new Insets(0, 5, 5, 5);
		gbc_lblUsername.gridwidth = RELATIVE;
		this.add(lblUsername, gbc_lblUsername);

		final JTextField textFieldUsername = new JTextField();
		textFieldUsername.setText(connectionParameters.getUsername());
		textFieldUsername.setEditable(false);
		lblUsername.setLabelFor(textFieldUsername);
		final GridBagConstraints gbc_textFieldUsername = new GridBagConstraints();
		gbc_textFieldUsername.weightx = 1.0;
		gbc_textFieldUsername.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUsername.gridwidth = REMAINDER;
		this.add(textFieldUsername, gbc_textFieldUsername);

		final JLabel lblPassword = new JLabel("Password:");
		lblPassword.setDisplayedMnemonic('P');
		final GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.insets = new Insets(0, 5, 5, 5);
		gbc_lblPassword.gridwidth = RELATIVE;
		gbc_lblPassword.gridheight = RELATIVE;
		this.add(lblPassword, gbc_lblPassword);

		final JPasswordField passwordField = new JPasswordField();
		passwordField.setText(connectionParameters.getPassword());
		passwordField.setEditable(false);
		lblPassword.setLabelFor(passwordField);
		final GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.weightx = 1.0;
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridwidth = REMAINDER;
		gbc_passwordField.gridheight = RELATIVE;
		this.add(passwordField, gbc_passwordField);

		final JCheckBox chckbxAutocommit = new JCheckBox("Auto-Commit");
		chckbxAutocommit.setSelected(connectionParameters.getAutoCommit());
		chckbxAutocommit.setEnabled(false);
		final GridBagConstraints gbc_chckbxAutocommit = new GridBagConstraints();
		gbc_chckbxAutocommit.anchor = GridBagConstraints.WEST;
		gbc_chckbxAutocommit.insets = new Insets(0, 5, 5, 5);
		gbc_chckbxAutocommit.gridwidth = REMAINDER;
		gbc_chckbxAutocommit.gridheight = REMAINDER;
		this.add(chckbxAutocommit, gbc_chckbxAutocommit);
	}

	/**
	 * @see Component#getName()
	 */
	@Override
	public String getName() {
		return "Generic JDBC";
	}
}
