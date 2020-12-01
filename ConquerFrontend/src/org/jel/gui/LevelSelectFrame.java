package org.jel.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.game.data.Reader;
import org.jel.game.data.Shared;
import org.jel.game.data.XMLReader;
import org.jel.gui.utils.ImageResource;

final class LevelSelectFrame extends JFrame implements MouseListener, WindowListener {
	private static final long serialVersionUID = -6919213661998844224L;
	private boolean shouldExit = true;
	private transient GlobalContext context;

	void init(Point location) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.context = XMLReader.getInstance().readInfo();
		final var list = new DefaultListModel<InstalledScenario>();
		for (final var scenario : this.context.getInstalledMaps()) {
			list.addElement(scenario);
		}
		final JButton jb = new RoundButton(new ImageResource("back.png"));
		jb.addActionListener(a -> {
			this.shouldExit = false;
			this.dispose();
			MainScreen.forward(this.getLocation(), false);
		});
		final var jlist = new JList<>(list);
		jlist.setCellRenderer(new ListCellRenderer<>() {
			private final Map<InstalledScenario, JLabel> map = new HashMap<>();

			@Override
			public Component getListCellRendererComponent(JList<? extends InstalledScenario> list,
					InstalledScenario value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel jl;
				if (!this.map.containsKey(value)) {
					jl = new JLabel(value.name());
					jl.setFont(jl.getFont().deriveFont(30.0f));
					try {
						jl.setToolTipText(String.format(
								"<html><head><title>a</title></head><body>"
										+ "<img src='%s' alt='Oops, image not found'> </body></html>",
								new File(value.thumbnail()).toURI().toURL()).replace("file:/", "file:///"));
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
		final var scrollPane = new JScrollPane();
		scrollPane.setViewportView(jlist);
		jlist.addMouseListener(this);
		this.add(jb);
		this.add(scrollPane);
		this.pack();
		this.setVisible(true);
		this.setLocation(location);
		this.addWindowListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
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

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (this.shouldExit) {
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
