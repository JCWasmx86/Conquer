package org.jel.gui;

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

import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.game.data.Reader;
import org.jel.game.data.SavedGame;
import org.jel.game.data.Shared;
import org.jel.game.data.XMLReader;
import org.jel.gui.utils.ImageResource;

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
		this.context = XMLReader.getInstance().readInfo();
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
					try {
						jl.setToolTipText(Messages.getMessage("LevelSelectFrame.imageNotFound", //$NON-NLS-1$
								new File(value.thumbnail()).toURI().toURL()).replace("file:/", "file:///")); //$NON-NLS-1$ //$NON-NLS-2$
					} catch (final MalformedURLException e) {
						Shared.LOGGER.exception(e);
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
					final var savedGame = new SavedGame(item);
					try {
						final var game = savedGame.restore();
						final var frame = new GameFrame(game);
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
		this.add(savedScenariosScrollPane);
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
		@SuppressWarnings("unchecked")
		final var scenario = ((JList<InstalledScenario>) e.getSource()).getSelectedValue();
		if (scenario == null) {
			return;
		}
		this.shouldExit = false;
		this.dispose();
		final var r = new Reader(scenario.file());
		final var game = r.buildGame();
		final var li = new LevelInfo(game, scenario, this.getLocation(), this.context);
		li.setVisible(true);
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
