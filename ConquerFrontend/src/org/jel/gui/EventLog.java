package org.jel.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;

import org.jel.game.data.Game;
import org.jel.game.messages.Message;
import org.jel.game.plugins.MessageListener;
import org.jel.gui.utils.ImageResource;

final class EventLog extends JFrame implements MessageListener {
	private static EventLog log;
	static {
		EventLog.log = new EventLog();
	}
	private static final float FONT_SIZE = 17.5f;
	private static final long serialVersionUID = 5521609891725906272L;

	public static void clear() {
		EventLog.log.dispose();
		EventLog.log.timer.stop();
		EventLog.log = new EventLog();
	}

	static void init(Game game) {
		final var a = game.getClans();
		for (final var clan : a) {
			final var jlabel = new JLabel(
					"Clan: " + clan.getName() + (clan.getName().equals(a.get(0).getName()) ? " (Player)" : ""));
			EventLog.log.defaultColor = new Color(jlabel.getForeground().getRGB());
			jlabel.setOpaque(true);
			jlabel.setBackground(new Color(EventLog.log.getComplementaryColor(clan.getColor().getRGB())));
			jlabel.setForeground(clan.getColor());
			jlabel.setFont(jlabel.getFont().deriveFont(EventLog.log.currFontSize));
			EventLog.log.base.add(jlabel);
			EventLog.log.base.add(EventLog.log.generateEmptySpace(clan.getColor()));
		}
		final var jlabel = new JLabel(" ");
		jlabel.setForeground(new Color(21, 21, 21));
		jlabel.setBackground(new Color(23, 23, 23));
		jlabel.setFont(jlabel.getFont().deriveFont(EventLog.log.currFontSize));
		game.addMessageListener(EventLog.log);
		EventLog.log.pack();
	}

	static void showWindow() {
		EventLog.log.setVisible(true);
	}

	private float currFontSize = EventLog.FONT_SIZE;
	private final JPanel base;
	private final JScrollPane contentPane;
	private final Timer timer;
	private final JCheckBoxMenuItem showGood;

	private final JCheckBoxMenuItem showBad;

	private Color defaultColor;

	private final AbstractButton showNeutral;

	private EventLog() {
		this.base = new JPanel();
		this.base.setLayout(new BoxLayout(this.base, BoxLayout.Y_AXIS));
		this.contentPane = new JScrollPane(this.base, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.contentPane.setIgnoreRepaint(true);
		this.contentPane.getVerticalScrollBar().setUnitIncrement(16);
		this.timer = new Timer(17, a -> this.repaint());
		this.timer.start();
		this.add(this.contentPane);
		final var menubar = new JMenuBar();
		final var menu = new JMenu("Settings");
		final var increaseFontSize = new JMenuItem();
		increaseFontSize.setAction(new AbstractAction() {
			private static final long serialVersionUID = 8790065267838524992L;

			@Override
			public void actionPerformed(ActionEvent e) {
				final var components = EventLog.this.base.getComponents();
				for (final var c : components) {
					if ((c instanceof JLabel jl) && (jl.getText().trim().length() > 0)) {
						final var font = jl.getFont();
						jl.setFont(font.deriveFont(font.getSize2D() + 1));
						jl.repaint();
					}
				}
				EventLog.this.currFontSize++;
				EventLog.this.repaint();
				EventLog.this.pack();
			}
		});
		increaseFontSize.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		increaseFontSize.setText("Increase fontsize");
		menu.add(increaseFontSize);
		final var decreaseFontSize = new JMenuItem();
		decreaseFontSize.setAction(new AbstractAction() {
			private static final long serialVersionUID = 4199619851951311213L;

			@Override
			public void actionPerformed(ActionEvent e) {
				final var components = EventLog.this.base.getComponents();
				for (final var c : components) {
					if ((c instanceof JLabel jl) && (jl.getText().trim().length() > 0)) {
						final var font = jl.getFont();
						final var pts = font.getSize2D();
						jl.setFont(font.deriveFont(pts - 1));
						jl.repaint();
					}
				}
				EventLog.this.currFontSize--;
				EventLog.this.repaint();
				EventLog.this.pack();
			}
		});
		decreaseFontSize.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		decreaseFontSize.setText("Decrease fontsize");
		menu.add(decreaseFontSize);
		this.showGood = new JCheckBoxMenuItem("Show positive events");
		this.showGood.setSelected(true);
		this.showGood.addItemListener(e -> {
			final var components = EventLog.this.base.getComponents();
			var flag = false;
			for (final var c : components) {
				if ((!flag) && c.getForeground().equals(new Color(21, 21, 21))) {
					flag = true;
					continue;
				}
				if ((c instanceof JLabel jl) && (jl.getForeground() == Color.GREEN)) {
					jl.setVisible(e.getStateChange() == ItemEvent.SELECTED);
				}
				EventLog.this.revalidate();
				EventLog.this.repaint();
			}
		});
		menu.add(this.showGood);
		this.showNeutral = new JCheckBoxMenuItem("Show neutral events");
		this.showNeutral.setSelected(true);
		this.showNeutral.addItemListener(e -> {
			final var components = EventLog.this.base.getComponents();
			var flag = false;
			for (final var c : components) {
				if ((!flag) && c.getForeground().equals(new Color(21, 21, 21))) {
					flag = true;
					continue;
				}
				if ((c instanceof JLabel jl) && jl.getForeground().equals(EventLog.this.defaultColor)) {
					jl.setVisible(e.getStateChange() == ItemEvent.SELECTED);
				}
				EventLog.this.revalidate();
				EventLog.this.repaint();
			}
		});
		menu.add(this.showNeutral);
		this.showBad = new JCheckBoxMenuItem("Show negative events");
		this.showBad.setSelected(true);
		this.showBad.addItemListener(e -> {
			final var components = EventLog.this.base.getComponents();
			var flag = false;
			for (final var c : components) {
				if ((!flag) && c.getForeground().equals(new Color(21, 21, 21))) {
					flag = true;
					continue;
				}
				if ((c instanceof JLabel jl) && (jl.getForeground() == Color.RED)) {
					jl.setVisible(e.getStateChange() == ItemEvent.SELECTED);
				}
				EventLog.this.revalidate();
				EventLog.this.repaint();
			}
		});
		menu.add(this.showBad);
		final var exit = new JMenuItem();
		exit.setAction(new AbstractAction() {
			private static final long serialVersionUID = 5612700000874979582L;

			@Override
			public void actionPerformed(ActionEvent e) {
				EventLog.this.setVisible(false);
			}
		});
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		exit.setText("Exit");
		menu.add(exit);
		menubar.add(menu);
		this.setJMenuBar(menubar);
		this.setTitle("Event log");
	}

	@Override
	public void added(Message s) {
		if (s.getMessageText() == null) {
			return;
		}
		final var jl = new JLabel(s.getMessageText());
		final var optional = s.getOptionalIconPath();
		if (!optional.isEmpty()) {
			jl.setIcon(new ImageResource(optional.get()));
		}
		var c = jl.getForeground();
		this.defaultColor = new Color(c.getRGB());
		if (s.isPlayerInvolved()) {
			jl.setOpaque(true);
			if (s.isBadForPlayer()) {
				c = Color.RED;
				if (!this.showBad.isSelected()) {
					jl.setVisible(false);
				}
			} else {
				c = Color.GREEN;
				if (!this.showGood.isSelected()) {
					jl.setVisible(false);
				}
			}
		}
		if (c.equals(this.defaultColor) && !this.showNeutral.isSelected()) {
			jl.setVisible(false);
		}
		jl.setForeground(c);
		jl.setFont(jl.getFont().deriveFont(EventLog.log.currFontSize));
		this.base.invalidate();
		this.base.add(jl);
		this.base.add(this.generateEmptySpace(c));
		this.pack();
		this.repaint();
	}

	private Component generateEmptySpace(Color color) {
		final var jl = new JLabel(" ");
		jl.setForeground(color);
		jl.setFont(jl.getFont().deriveFont(4f));
		return jl;
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
	public void removed(Message s) {
		// Do nothing
	}
}
