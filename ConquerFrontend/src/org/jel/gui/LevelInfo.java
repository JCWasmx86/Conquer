package org.jel.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.jel.game.data.ConquerInfo;
import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.gui.utils.ImageResource;

/**
 * This class shows all clans in a scenario in a JList. Furthermore there is a
 * back- and forward-button.
 */
//TODO: Settings button to allow changing the plugins and strategies (Just removing is allowed)
final class LevelInfo extends JFrame implements WindowListener {
	private static final long serialVersionUID = 5849067897050863981L;
	private boolean shouldExit = false;

	/**
	 * Construct a new LevelInfo
	 *
	 * @param game     The source of some data
	 * @param is       The scenario to show
	 * @param location On which location the frame should appear
	 * @param context  The whole context
	 */
	LevelInfo(final ConquerInfo game, final InstalledScenario is, final Point location, final GlobalContext context) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.addWindowListener(this);
		final var assocs = new DefaultListModel<ClanColorAssociation>();
		assocs.addAll(game.getClans().stream().map(a -> new ClanColorAssociation(a.getName(), a.getColor()))
				.collect(Collectors.toList()));
		final var jlist = new JList<>(assocs);
		jlist.setCellRenderer(new ListCellRenderer<>() {
			private final Map<ClanColorAssociation, JLabel> map = new HashMap<>();

			@Override
			public Component getListCellRendererComponent(final JList<? extends ClanColorAssociation> list,
					final ClanColorAssociation value, final int index, final boolean isSelected,
					final boolean cellHasFocus) {
				JLabel jl;
				if (!this.map.containsKey(value)) {
					jl = new JLabel(value.clanName()
							+ (game.getClan(index).isPlayerClan() ? " " + Messages.getString("Shared.player") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					jl.setForeground(value.color());
					jl.setFont(jl.getFont().deriveFont(35F));
					jl.setBackground(new Color(LevelInfo.this.getComplementaryColor(value.color().getRGB())));
					jl.setOpaque(true);
					this.map.put(value, jl);
				} else {
					jl = this.map.get(value);
				}
				return jl;
			}
		});
		final var scrollPane = new JScrollPane();
		scrollPane.setViewportView(jlist);
		final JButton backButton = new RoundButton(new ImageResource("back.png")); //$NON-NLS-1$
		final JButton forwardButton = new RoundButton(new ImageResource("forward.png")); //$NON-NLS-1$
		final var backwardIcon = backButton.getIcon();
		backButton.setSize(backwardIcon.getIconWidth(), backwardIcon.getIconHeight());
		backButton.addActionListener(a -> {
			final var lsf = new LevelSelectFrame();
			lsf.init(this.getLocation());
			this.shouldExit = true;
			this.dispose();
		});
		final var selectPanel = new PluginStrategySelectPanel(context);
		final var forwardIcon = forwardButton.getIcon();
		forwardButton.setSize(forwardIcon.getIconWidth(), forwardIcon.getIconHeight());
		forwardButton.addActionListener(a -> {
			game.addContext(selectPanel.modifyContext());
			game.init();
			final var gf = new GameFrame(game);
			gf.init();
			this.shouldExit = true;
			this.dispose();
		});
		final var p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(backButton);
		p.add(forwardButton);
		this.add(p);
		this.add(scrollPane);
		this.add(selectPanel);
		this.pack();
		this.setLocation(location);
	}

	private int getComplementaryColor(final int color) {
		var r = color & 255;
		var g = (color >> 8) & 255;
		var b = (color >> 16) & 255;
		final var a = (color >> 24) & 255;
		r = 255 - r;
		g = 255 - g;
		b = 255 - b;
		return r + (g << 8) + (b << 16) + (a << 24);
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
	 * Shouldn't be used
	 */
	@Override
	public void windowClosing(final WindowEvent e) {
		if (!this.shouldExit) {
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

/**
 * This record just associates a clan with its corresponding color.
 */
final record ClanColorAssociation(String clanName, Color color) {
}