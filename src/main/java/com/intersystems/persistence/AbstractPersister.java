/*-
 * $Id$
 */
package com.intersystems.persistence;

import static javax.swing.SwingUtilities.invokeLater;

import javax.swing.DefaultListModel;

/**
 * @author Andrey Shcheglov &lt;mailto:andrey.shcheglov@intersystems.com&gt;
 */
public abstract class AbstractPersister implements Persister {
	private volatile String serverVersion = "";

	private volatile boolean running;

	private volatile TestResult testResult = TestResult.NO_DATA;

	DefaultListModel<Persister> listModel;

	int index;

	/**
	 * @see Persister#getServerVersion()
	 */
	@Override
	public final String getServerVersion() {
		return this.serverVersion;
	}

	/**
	 * @param serverVersion
	 */
	protected final void setServerVersion(final String serverVersion) {
		this.serverVersion = serverVersion;
		this.fireContentsChanged();
	}

	/**
	 * @see Persister#isRunning()
	 */
	@Override
	public final boolean isRunning() {
		return this.running;
	}

	/**
	 * @param running
	 */
	protected final void setRunning(final boolean running) {
		this.running = running;
		if (this.running) {
			this.testResult = TestResult.NO_DATA;
		}
		this.fireContentsChanged();
	}

	/**
	 * @see Persister#setTestResult(TestResult)
	 */
	@Override
	public final void setTestResult(final TestResult testResult) {
		this.testResult = testResult;
		this.fireContentsChanged();
	}

	/**
	 * @see Persister#getTestResult()
	 */
	@Override
	public TestResult getTestResult() {
		return this.testResult;
	}

	/**
	 * @see Persister#setListModel(DefaultListModel, int)
	 */
	@Override
	public final void setListModel(final DefaultListModel<Persister> listModel, final int index) {
		this.listModel = listModel;
		this.index = index;
	}

	private void fireContentsChanged() {
		if (this.listModel != null) {
			invokeLater(new Runnable() {
				/**
				 * @see Runnable#run()
				 */
				@Override
				public void run() {
					AbstractPersister.this.listModel.setElementAt(AbstractPersister.this, AbstractPersister.this.index);
				}
			});
		}
	}
}
