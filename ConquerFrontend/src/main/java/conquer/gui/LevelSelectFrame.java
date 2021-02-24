package conquer.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import conquer.data.GlobalContext;
import conquer.data.InstalledScenario;
import conquer.data.SPIContextBuilder;
import conquer.data.Shared;
import conquer.data.XMLReader;
import conquer.gui.utils.ImageResource;

/**
 * This frame shows all installed scenarios in a JList.
 */
final class LevelSelectFrame extends JFrame implements MouseListener, WindowListener {
	private static final long serialVersionUID = -6919213661998844224L;
	private boolean shouldExit = true;
	private transient GlobalContext context;

	/**
	 * Initialise the frame at the specified location
	 *
	 * @param location The location of the created frame.
	 */
	void init(final Point location) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.context = Shared.useSPI() ? new SPIContextBuilder().buildContext() : XMLReader.getInstance().readInfo();
		final var newScenarios = new DefaultListModel<InstalledScenario>();
		newScenarios.addAll(this.context.getInstalledMaps());
		final JButton jb = new RoundButton(new ImageResource("back.png")); //$NON-NLS-1$
		jb.addActionListener(a -> {
			this.shouldExit = false;
			this.dispose();
			MainScreen.forward(this.getLocation(), false);
		});
		final var freshNewScenarios = new JList<>(newScenarios);
		freshNewScenarios.setCellRenderer(new ListCellRenderer<>() {
			private final Map<InstalledScenario, JLabel> map = new HashMap<>();

			@Override
			public Component getListCellRendererComponent(final JList<? extends InstalledScenario> list,
														  final InstalledScenario value, final int index, final boolean isSelected,
														  final boolean cellHasFocus) {
				JLabel jl;
				if (!this.map.containsKey(value)) {
					jl = new JLabel(value.name());
					jl.setFont(jl.getFont().deriveFont(30.0f));
					if (value.thumbnail() != null) {
						try {
							final var url = new File(value.thumbnail()).toURI().toURL().toString().replace("file:/",
								"file:///");
							// A dirty hack...
							jl.setToolTipText(
								Messages.getMessage("LevelSelectFrame.imageNotFound", url).replace("{0}", url));//$NON-NLS-1$
						} catch (final MalformedURLException e) {
							Shared.LOGGER.exception(e);
						}
					}
					this.map.put(value, jl);
				} else {
					jl = this.map.get(value);
				}
				return jl;
			}
		});
		final var newScenariosScrollPane = new JScrollPane();
		newScenariosScrollPane.setViewportView(freshNewScenarios);
		freshNewScenarios.addMouseListener(this);
		final var savedScenarios = new JList<>(Shared.savedGames());
		savedScenarios.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent event) {
				if (SwingUtilities.isRightMouseButton(event)) {
					savedScenarios.setSelectedIndex(savedScenarios.locationToIndex(event.getPoint()));
					final var menu = new JPopupMenu();
					final var itemRemove = new JMenuItem(Messages.getString("StrategiesAndPluginsDialog.remove")); //$NON-NLS-1$
					itemRemove.addActionListener(a -> {
						if (savedScenarios.getSelectedValue() == null) {
							return;
						}
						try {
							Shared.deleteDirectory(new File(Shared.SAVE_DIRECTORY, savedScenarios.getSelectedValue()));
						} catch (final IOException e1) {
							Shared.LOGGER.exception(e1);
							JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
							return;
						}
						savedScenarios.setListData(Shared.savedGames());
						LevelSelectFrame.this.pack();
					});
					menu.add(itemRemove);
					menu.show(savedScenarios, event.getPoint().x, event.getPoint().y);
				} else if (SwingUtilities.isLeftMouseButton(event)) {
					final var item = savedScenarios.getSelectedValue();
					if (item == null) {
						return;
					}
					try {
						final var game = Shared.restore(item);
						final var frame = new GameFrame(item, game);
						frame.init();
					} catch (final Exception e1) {
						e1.printStackTrace();
						Shared.LOGGER.exception(e1);
						JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
						return;
					}
					LevelSelectFrame.this.shouldExit = false;
					LevelSelectFrame.this.dispose();
				}
			}
		});
		final var savedScenariosScrollPane = new JScrollPane();
		savedScenariosScrollPane.setViewportView(savedScenarios);
		this.add(jb);
		this.add(newScenariosScrollPane);
		if (Shared.savedGames().length > 0) {
			this.add(savedScenariosScrollPane);
		}
		this.pack();
		this.setVisible(true);
		this.setLocation(location);
		this.addWindowListener(this);
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void mouseExited(final MouseEvent e) {

	}

	/**
	 * Exits the frame and opens a new LevelInfo
	 */
	@Override
	public void mousePressed(final MouseEvent e) {
		@SuppressWarnings("unchecked") final var scenario = ((JList<InstalledScenario>) e.getSource()).getSelectedValue();
		if (scenario == null) {
			return;
		}
		this.shouldExit = false;
		try {
			final var game = this.context.loadInfo(scenario);
			this.dispose();
			final var li = new LevelInfo(game, scenario, this.getLocation(), this.context);
			li.setVisible(true);
		} catch (final UnsupportedOperationException uoe) {
			JOptionPane.showMessageDialog(null, Messages.getString("LevelSelectFrame.noReaderFound"),
				Messages.getString("LevelSelectFrame.error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void mouseReleased(final MouseEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowActivated(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowClosed(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used. Closes the window and kills the JVM.
	 */
	@Override
	public void windowClosing(final WindowEvent e) {
		if (this.shouldExit) {
			System.exit(0);
		}
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeactivated(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeiconified(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowIconified(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowOpened(final WindowEvent e) {

	}
}
