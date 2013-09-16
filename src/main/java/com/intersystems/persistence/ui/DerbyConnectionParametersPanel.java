/*-
 * $Id$
 */
package com.intersystems.persistence.ui;

import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.intersystems.persistence.DerbyConnectionParameters;
import com.intersystems.persistence.DerbyPersister;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public final class DerbyConnectionParametersPanel extends ConnectionParametersPanel<DerbyPersister> {
	private static final long serialVersionUID = -5202714019412398701L;

	/**
	 * @param connectionParameters
	 */
	public DerbyConnectionParametersPanel(final DerbyConnectionParameters connectionParameters) {
		super(connectionParameters);

		final GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);

		final JLabel lblDatabaseName = new JLabel("Database Name:");
		final GridBagConstraints gbc_lblDatabaseName = new GridBagConstraints();
		gbc_lblDatabaseName.insets = new Insets(5, 5, 5, 5);
		gbc_lblDatabaseName.gridwidth = RELATIVE;
		gbc_lblDatabaseName.gridheight = RELATIVE;
		this.add(lblDatabaseName, gbc_lblDatabaseName);

		final JTextField txtDatabaseName = new JTextField();
		lblDatabaseName.setLabelFor(txtDatabaseName);
		txtDatabaseName.setText(connectionParameters.getDatabaseName());
		final GridBagConstraints gbc_txtDatabaseName = new GridBagConstraints();
		gbc_txtDatabaseName.weightx = 1.0;
		gbc_txtDatabaseName.insets = new Insets(5, 0, 5, 5);
		gbc_txtDatabaseName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDatabaseName.gridwidth = REMAINDER;
		gbc_txtDatabaseName.gridheight = RELATIVE;
		this.add(txtDatabaseName, gbc_txtDatabaseName);
		txtDatabaseName.setColumns(10);

		final JCheckBox chckbxAutocommit = new JCheckBox("Auto-Commit");
		chckbxAutocommit.setSelected(connectionParameters.getAutoCommit());
		final GridBagConstraints gbc_chckbxAutocommit = new GridBagConstraints();
		gbc_chckbxAutocommit.anchor = WEST;
		gbc_chckbxAutocommit.weightx = 1.0;
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
		return "Apache Derby";
	}
}
