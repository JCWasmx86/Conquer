package conquer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;

import conquer.data.ConquerInfo;
import conquer.data.GlobalContext;
import conquer.data.InstalledScenario;
import conquer.frontend.spi.ConfigurationPanelProvider;
import conquer.gui.utils.ImageResource;
import conquer.utils.Pair;

/**
 * This class shows all clans in a scenario in a JList. Furthermore there is a
 * back- and forward-button.
 */
final class LevelInfo extends JFrame implements EmptyWindowListenerImpl {
	@Serial
	private static final long serialVersionUID = 5849067897050863981L;
	private boolean shouldExit;

	/**
	 * Construct a new LevelInfo
	 *
	 * @param game     The source of some data
	 * @param is       The scenario to show
	 * @param location On which location the frame should appear
	 * @param context  The whole context
	 */
	LevelInfo(final ConquerInfo game, final InstalledScenario is, final Point location, final GlobalContext context) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.addWindowListener(this);
		final var assocs = new DefaultListModel<Pair<String, Color>>();
		assocs.addAll(game.getClans().stream().map(a -> new Pair<>(a.getName(), a.getColor()))
			.toList());
		final var jlist = new JList<>(assocs);
		jlist.setCellRenderer(new ListCellRenderer<>() {
			private final Map<Pair<String, Color>, JLabel> map = new HashMap<>();

			@Override
			public Component getListCellRendererComponent(final JList<? extends Pair<String, Color>> list,
														  final Pair<String, Color> value, final int index,
														  final boolean isSelected,
														  final boolean cellHasFocus) {
				final JLabel jl;
				if (this.map.containsKey(value)) {
					jl = this.map.get(value);
				} else {
					jl = new JLabel(value.first()
						+ (game.getClan(index).isPlayerClan() ? " " + Messages.getString("Shared" +
						".player") : ""));

					jl.setForeground(value.second());
					jl.setFont(jl.getFont().deriveFont(35F));
					jl.setBackground(new Color(LevelInfo.this.getComplementaryColor(value.second().getRGB())));
					jl.setOpaque(true);
					this.map.put(value, jl);
				}
				return jl;
			}
		});
		final var scrollPane = new JScrollPane();
		scrollPane.setViewportView(jlist);
		final JButton backButton = new RoundButton(new ImageResource("back.png"));
		final JButton forwardButton = new RoundButton(new ImageResource("forward.png"));
		final var backwardIcon = backButton.getIcon();
		backButton.setSize(backwardIcon.getIconWidth(), backwardIcon.getIconHeight());
		backButton.addActionListener(a -> {
			final var lsf = new LevelSelectFrame();
			lsf.init(this.getLocation());
			this.shouldExit = true;
			this.dispose();
		});
		final var selectPanel = new PluginStrategySelectPanel(context, game);
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
		final var allConfigurationPanels = ServiceLoader.load(ConfigurationPanelProvider.class).stream()
			.map(ServiceLoader.Provider::get).filter(a -> a.forClass(game.getClass()).isPresent()).toList();
		if (!allConfigurationPanels.isEmpty()) {
			this.add(this.buildConfigurationPanel(allConfigurationPanels, game.getClass()));
		}
		this.pack();
		this.setLocation(location);
	}

	private Component buildConfigurationPanel(final List<ConfigurationPanelProvider> allConfigurationPanels,
											  final Class<? extends ConquerInfo> clazz) {
		final var jtp = new JTabbedPane();
		allConfigurationPanels.forEach(a -> jtp.addTab(a.getName(), a.forClass(clazz).get()));
		return jtp;
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
	public void windowClosing(final WindowEvent e) {
		if (!this.shouldExit) {
			System.exit(0);
		}
	}

}