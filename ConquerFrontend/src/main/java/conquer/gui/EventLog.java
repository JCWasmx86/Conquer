package conquer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.Serial;
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

import conquer.data.ConquerInfo;
import conquer.gui.utils.ImageResource;
import conquer.messages.Message;
import conquer.plugins.MessageListener;

/**
 * Shows all events of the game. Only one instance is available.
 */
final class EventLog extends JFrame implements MessageListener {
	private static final float FONT_SIZE = 17.5f;
	@Serial
	private static final long serialVersionUID = 5521609891725906272L;
	private static EventLog log;

	static {
		EventLog.log = new EventLog();
	}

	private final JPanel base;
	private final Timer timer;
	private float currFontSize = EventLog.FONT_SIZE;
	private JCheckBoxMenuItem showGood;
	private JCheckBoxMenuItem showBad;
	private Color defaultColor;
	private AbstractButton showNeutral;

	private EventLog() {
		this.base = new JPanel();
		this.base.setLayout(new BoxLayout(this.base, BoxLayout.PAGE_AXIS));
		final JScrollPane contentPane = new JScrollPane(this.base, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPane.setIgnoreRepaint(true);
		contentPane.getVerticalScrollBar().setUnitIncrement(16);
		this.timer = new Timer(Utils.getRefreshRate(), a -> this.repaint());
		this.timer.start();
		this.add(contentPane);
		final var menubar = new JMenuBar();
		final var menu = new JMenu(Messages.getString("EventLog.settings"));
		final var increaseFontSize = new JMenuItem();
		this.initIncreaseFontSize(increaseFontSize);
		menu.add(increaseFontSize);
		final var decreaseFontSize = new JMenuItem();
		this.initDecreaseFontSize(decreaseFontSize);
		menu.add(decreaseFontSize);
		this.initShowGood();
		menu.add(this.showGood);
		this.initShowNeutral();
		menu.add(this.showNeutral);
		this.initShowNegative();
		menu.add(this.showBad);
		final var clearButton = new JMenuItem();
		clearButton.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 8790065267838524992L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				EventLog.this.base.removeAll();
				EventLog.this.revalidate();
				EventLog.this.repaint();
				EventLog.this.pack();
			}
		});
		clearButton.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		clearButton.setText(Messages.getString("EventLog.clearButton"));
		menu.add(clearButton);
		final var exitButton = new JMenuItem();
		exitButton.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 5612700000874979582L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				EventLog.this.setVisible(false);
			}
		});
		exitButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		exitButton.setText(Messages.getString("EventLog.closeDialog"));
		menu.add(exitButton);
		menubar.add(menu);
		this.setJMenuBar(menubar);
		this.setTitle(Messages.getString("EventLog.eventLog"));
	}

	/**
	 * Clear the instance.
	 */
	public static void clear() {
		EventLog.log.dispose();
		EventLog.log.timer.stop();
		EventLog.log = new EventLog();
	}

	/**
	 * Setup the instance with a specified game
	 *
	 * @param game The game as the source of events.
	 */
	static void init(final ConquerInfo game) {
		final var a = game.getClans();
		for (final var clan : a) {
			final var jlabel = new JLabel(Messages.getString("Shared.clan") + ": " + clan.getName()
				+ (clan == game.getPlayerClan() ? " " + Messages.getString("Shared.player")
				: ""));
			EventLog.log.defaultColor = new Color(jlabel.getForeground().getRGB());
			final var color = clan.getColor();
			jlabel.setOpaque(true);
			jlabel.setBackground(new Color(EventLog.log.getComplementaryColor(color.getRGB())));
			jlabel.setForeground(color);
			jlabel.setFont(jlabel.getFont().deriveFont(EventLog.log.currFontSize));
			EventLog.log.base.add(jlabel);
			EventLog.log.base.add(EventLog.log.generateEmptySpace(color));
		}
		final var jlabel = new JLabel(" ");
		jlabel.setForeground(new Color(21, 21, 21));
		jlabel.setBackground(new Color(23, 23, 23));
		jlabel.setFont(jlabel.getFont().deriveFont(EventLog.log.currFontSize));
		game.addMessageListener(EventLog.log);
		EventLog.log.pack();
	}

	/**
	 * Makes the instance visible
	 */
	static void showWindow() {
		EventLog.log.setVisible(true);
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void added(final Message message) {
		if ((message.getMessageText() == null) || !message.shouldBeShownToThePlayer()) {
			return;
		}
		final var jl = new JLabel(message.getMessageText());
		final var optional = message.getOptionalIconPath();
		optional.ifPresent(s -> jl.setIcon(new ImageResource(s)));
		var c = jl.getForeground();
		this.defaultColor = new Color(c.getRGB());
		if (message.isPlayerInvolved()) {
			jl.setOpaque(true);
			if (message.isBadForPlayer()) {
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

	private Component generateEmptySpace(final Color color) {
		final var jl = new JLabel(" ");
		jl.setForeground(color);
		jl.setFont(jl.getFont().deriveFont(4f));
		return jl;
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

	private void initDecreaseFontSize(final JMenuItem decreaseFontSize) {
		decreaseFontSize.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 4199619851951311213L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final var components = EventLog.this.base.getComponents();
				for (final var c : components) {
					if ((c instanceof JLabel jl) && (!jl.getText().trim().isEmpty())) {
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
		decreaseFontSize.setText(Messages.getString("EventLog.decreaseFontsize"));
	}

	private void initIncreaseFontSize(final JMenuItem increaseFontSize) {
		increaseFontSize.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 8790065267838524992L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final var components = EventLog.this.base.getComponents();
				for (final var c : components) {
					if ((c instanceof JLabel jl) && (!jl.getText().trim().isEmpty())) {
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
		increaseFontSize.setText(Messages.getString("EventLog.increaseFontsize"));
	}

	private void initShowGood() {
		this.showGood = new JCheckBoxMenuItem(Messages.getString("EventLog.showPositiveEvents"));
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
	}

	private void initShowNegative() {
		this.showBad = new JCheckBoxMenuItem(Messages.getString("EventLog.showNegativeEvents"));
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
	}

	private void initShowNeutral() {
		this.showNeutral = new JCheckBoxMenuItem(Messages.getString("EventLog.showNeutralEvents"));
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
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void removed(final Message s) {
		// Do nothing
	}
}
