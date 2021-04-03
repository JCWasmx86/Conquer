package conquer.gui;

import conquer.data.GlobalContext;
import conquer.data.Shared;
import conquer.data.XMLReader;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * This is a singleton-dialog, that allows the player to add/remove plugins and
 * strategies.
 */
//TODO: This is deprecated and soon removed, as SPI is the new way.
@Deprecated
final class StrategiesAndPluginsDialog extends JFrame {
	private static final long serialVersionUID = 1100425969083669130L;
	private static final StrategiesAndPluginsDialog INSTANCE = new StrategiesAndPluginsDialog();

	static {
		StrategiesAndPluginsDialog.INSTANCE.init();
	}

	private ArrayList<String> strategyNamesListCopy;
	private ArrayList<String> pluginNamesListCopy;
	private transient GlobalContext context;
	private JList<String> strategies;
	private JList<String> plugins;

	/**
	 * Show the instance.
	 */
	public static void showWindow() {
		StrategiesAndPluginsDialog.INSTANCE.pack();
		StrategiesAndPluginsDialog.INSTANCE.setVisible(true);
	}

	private void init() {
		this.setTitle(Messages.getString("Shared.strategiesAndPlugins")); 
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.context = XMLReader.getInstance().readInfo(false);
		this.pluginNamesListCopy = new ArrayList<>(this.context.getPluginNames());
		this.plugins = new JList<>(this.context.getPluginNames().toArray(new String[0]));
		this.plugins.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					final var ref = StrategiesAndPluginsDialog.this;
					ref.plugins.setSelectedIndex(ref.plugins.locationToIndex(e.getPoint()));
					final var menu = new JPopupMenu();
					final var itemRemove = new JMenuItem(Messages.getString("StrategiesAndPluginsDialog.remove"));
					
					itemRemove.addActionListener(a -> {
						ref.pluginNamesListCopy.remove(ref.plugins.getSelectedValue());
						ref.plugins.setListData(
								ref.pluginNamesListCopy.toArray(new String[0]));
						ref.pack();
					});
					menu.add(itemRemove);
					menu.show(ref.plugins, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		final var addPlugin = new SelectPanel(Messages.getString("StrategiesAndPluginsDialog.addPlugin"), 
				Messages.getString("StrategiesAndPluginsDialog.pluginClassname"), a -> { 
			this.pluginNamesListCopy.add(a);
			this.plugins
					.setListData(this.pluginNamesListCopy.toArray(new String[0]));
		});
		this.strategyNamesListCopy = new ArrayList<>(this.context.getStrategyNames());
		this.strategies = new JList<>(this.context.getStrategyNames().toArray(new String[0]));
		this.strategies.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					final var ref = StrategiesAndPluginsDialog.this;
					ref.strategies.setSelectedIndex(ref.strategies.locationToIndex(e.getPoint()));
					final var menu = new JPopupMenu();
					final var itemRemove = new JMenuItem(Messages.getString("StrategiesAndPluginsDialog.remove"));
					
					itemRemove.addActionListener(a -> {
						ref.strategyNamesListCopy.remove(ref.strategies.getSelectedValue());
						ref.strategies.setListData(
								ref.strategyNamesListCopy.toArray(new String[0]));
						ref.pack();
					});
					menu.add(itemRemove);
					menu.show(ref.strategies, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		final var addStrategy = new SelectPanel(Messages.getString("StrategiesAndPluginsDialog.addStrategy"), //$NON
				// -NLS-1$
				Messages.getString("StrategiesAndPluginsDialog.strategyClassname"), a -> { 
			this.strategyNamesListCopy.add(a);
			this.strategies.setListData(
					this.strategyNamesListCopy.toArray(new String[0]));
		});
		final var contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		contentPanel.add(this.plugins);
		contentPanel.add(addPlugin);
		contentPanel.add(this.strategies);
		contentPanel.add(addStrategy);
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		final var clearChanges = new JButton();
		clearChanges.setAction(new AbstractAction() {
			private static final long serialVersionUID = -6317479161221320082L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				StrategiesAndPluginsDialog.this.reset();
			}
		});
		clearChanges.setText(Messages.getString("StrategiesAndPluginsDialog.resetChanges")); 
		final var saveChanges = new JButton();
		final var f = new File(Shared.BASE_DIRECTORY) + File.separator + File.separator;
		saveChanges.setAction(new AbstractAction() {
			private static final long serialVersionUID = -3344401911394156158L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final var sb = new StringBuilder(
						"""
								<!--Auto generated - DO NOT EDIT, EXCEPT YOU KNOW WHAT YOU ARE DOING!-->
								<info>
								\t<scenarios>
								"""); 
				StrategiesAndPluginsDialog.this.context.getInstalledMaps()
						.forEach(a -> sb.append("\t\t<scenario name=\"").append(a.name()).append("\" file=\"") //$NON
								// -NLS-1$ 
								.append(a.file().replace(f, "")).append("\" thumbnail=\"")  
								.append(a.thumbnail().replace(f, "")).append("\"/>\n"));  
				sb.append("\t</scenarios>\n\t<plugins>\n"); 
				StrategiesAndPluginsDialog.this.pluginNamesListCopy
						.forEach(a -> sb.append("\t\t<plugin className=\"").append(a).append("\"/>\n")); 
				/
				sb.append("\t</plugins>\n\t<strategies>\n"); 
				StrategiesAndPluginsDialog.this.strategyNamesListCopy
						.forEach(a -> sb.append("\t\t<strategy className=\"").append(a).append("\"/>\n")); //$NON-NLS
				// -1$ 
				sb.append("\t</strategies>\n\t<readers>");
				StrategiesAndPluginsDialog.this.context.getReaderNames()
						.forEach(a -> sb.append("\t\t<reader className=\"").append(a).append("\"/>\n"));
				sb.append("\t</readers>\n</info>\n"); 
				final var f = new File(Shared.BASE_DIRECTORY, "info.xml"); 
				try {
					Files.write(Paths.get(f.toURI()), sb.toString().getBytes());
				} catch (final IOException e1) {
					JOptionPane.showMessageDialog(null,
							Messages.getString("StrategiesAndPluginsDialog.writingFailed") + e1.getLocalizedMessage(),
							
							Messages.getString("StrategiesAndPluginsDialog.error"), 
							JOptionPane.ERROR_MESSAGE);
					StrategiesAndPluginsDialog.this.context = XMLReader.getInstance().readInfo(false);
					StrategiesAndPluginsDialog.this.reset();
					return;
				}
				StrategiesAndPluginsDialog.this.context = XMLReader.getInstance().readInfo(false);
				StrategiesAndPluginsDialog.this.reset();
				JOptionPane.showMessageDialog(null, Messages.getString("StrategiesAndPluginsDialog.pleaseRestart"));
			}
		});
		saveChanges.setText(Messages.getString("StrategiesAndPluginsDialog.saveChanges")); 
		buttonPanel.add(clearChanges);
		buttonPanel.add(saveChanges);
		contentPanel.add(buttonPanel);
		final var jsp = new JScrollPane(contentPanel);
		this.add(jsp);
		this.pack();
	}

	private void reset() {
		final var newPluginModel = new DefaultListModel<String>();
		newPluginModel.clear();
		newPluginModel.addAll(this.context.getPluginNames());
		this.plugins.setModel(newPluginModel);
		this.pluginNamesListCopy = new ArrayList<>(this.context.getPluginNames());
		final var newStrategyModel = new DefaultListModel<String>();
		newStrategyModel.clear();
		newStrategyModel.addAll(this.context.getStrategyNames());
		this.strategies.setModel(newStrategyModel);
		this.strategyNamesListCopy = new ArrayList<>(this.context.getStrategyNames());
		this.pack();
	}

}
