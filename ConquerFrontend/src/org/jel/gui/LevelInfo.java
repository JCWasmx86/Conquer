package org.jel.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.jel.game.data.Game;
import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.gui.utils.ImageResource;

final class LevelInfo extends JFrame implements WindowListener {
	private static final long serialVersionUID = 5849067897050863981L;
	private boolean shouldExit = false;

	LevelInfo(Game game, InstalledScenario is, Point location, GlobalContext context) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.addWindowListener(this);
		final var assocs = new DefaultListModel<ClanColorAssociation>();
		for (var i = 0; i < game.getNumPlayers(); i++) {
			assocs.addElement(new ClanColorAssociation(game.getClan(i).getName(), game.getClan(i).getColor()));
		}
		final var jlist = new JList<>(assocs);
		jlist.setCellRenderer(new ListCellRenderer<>() {
			private final Map<ClanColorAssociation, JLabel> map = new HashMap<>();

			@Override
			public Component getListCellRendererComponent(JList<? extends ClanColorAssociation> list,
					ClanColorAssociation value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel jl;
				if (!this.map.containsKey(value)) {
					jl = new JLabel(value.clanName() + (index == 0 ? " (Player)" : ""));
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
		final JButton backButton = new RoundButton(new ImageResource("back.png"));
		final JButton forwardButton = new RoundButton(new ImageResource("forward.png"));
		backButton.setSize(backButton.getIcon().getIconWidth(), backButton.getIcon().getIconHeight());
		backButton.setLocation(0, 0);
		backButton.addActionListener(a -> {
			final var lsf = new LevelSelectFrame();
			lsf.init(this.getWidth(), this.getHeight(), this.getLocation());
			this.shouldExit = true;
			this.dispose();
		});
		forwardButton.setSize(forwardButton.getIcon().getIconWidth(), forwardButton.getIcon().getIconHeight());
		forwardButton.setLocation(800 - 32, 0);
		forwardButton.addActionListener(a -> {
			game.addContext(context);
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
		this.pack();
	}

	private int getComplementaryColor(int color) {
		var r = color & 255;
		var g = (color >> 8) & 255;
		var b = (color >> 16) & 255;
		final var a = (color >> 24) & 255;
		r = 255 - r;
		g = 255 - g;
		b = 255 - b;
		return r + (g << 8) + (b << 16) + (a << 24);
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (!this.shouldExit) {
			System.exit(0);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

}

final record ClanColorAssociation(String clanName, Color color) {
}