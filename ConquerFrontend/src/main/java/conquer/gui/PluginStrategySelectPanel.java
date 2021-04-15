package conquer.gui;

import conquer.data.ConquerInfo;
import conquer.data.GlobalContext;
import conquer.data.strategy.StrategyProvider;
import conquer.gui.debug.DebugPlugin;
import conquer.plugins.Plugin;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

final class PluginStrategySelectPanel extends JPanel {
	@Serial
	private static final long serialVersionUID = 3909874317355075179L;
	private final transient GlobalContext context;
	private final List<JCheckBox> plugins = new ArrayList<>();
	private final List<JCheckBox> strategies = new ArrayList<>();
	private final transient ConquerInfo info;

	PluginStrategySelectPanel(final GlobalContext context, final ConquerInfo info) {
		this.context = context;
		this.info = info;
		this.init();
	}

	private void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		final var pluginPanel = new JPanel();
		pluginPanel.setLayout(new BoxLayout(pluginPanel, BoxLayout.PAGE_AXIS));
		this.context.getPlugins().forEach(a -> {
			final var jb = new JCheckBox(a.getName(), true);
			pluginPanel.add(jb);
			final var required = this.info.requiredPlugins().stream().noneMatch(b -> b == a.getClass());
			jb.setEnabled(required);
			this.plugins.add(jb);
		});
		final var pluginScrollPanel = new JScrollPane(pluginPanel);
		this.add(pluginScrollPanel);
		final var strategiesPanel = new JPanel();
		strategiesPanel.setLayout(new BoxLayout(strategiesPanel, BoxLayout.PAGE_AXIS));
		this.context.getStrategies().forEach(a -> {
			// Skip builtin strategies
			final var module = a.getClass().getModule();
			final var name = module == null ? null : module.getName();
			if ((module != null) && "conquer".equals(name)) {
				return;
			}
			final var jb = new JCheckBox(a.getName(), true);
			// If the strategy is required, set it to disabled
			jb.setEnabled(this.info.requiredStrategyProviders().stream().noneMatch(b -> b == a.getClass()));
			strategiesPanel.add(jb);
			this.strategies.add(jb);
		});
		this.add(strategiesPanel);
	}

	GlobalContext modifyContext() {
		final List<Plugin> newPlugins = new ArrayList<>();
		this.plugins.forEach(a -> {
			if (!a.isSelected()) {
				return;
			}
			for (final var oldPlugin : this.context.getPlugins()) {
				if (a.getText().equals(oldPlugin.getName())) {
					newPlugins.add(oldPlugin);
				}
			}
		});
		if(Utils.isDebug()) {
			newPlugins.add(new DebugPlugin());
		}
		final var pluginNames = newPlugins.stream().map(a -> a.getClass().getName()).toList();
		this.context.getPlugins().clear();
		this.context.getPlugins().addAll(newPlugins);
		this.context.getPluginNames().clear();
		this.context.getPluginNames().addAll(pluginNames);
		final List<StrategyProvider> newStrategies = new ArrayList<>();
		this.strategies.forEach(a -> {
			if (!a.isSelected()) {
				return;
			}
			for (final var oldStrategy : this.context.getStrategies()) {
				if (a.getText().equals(oldStrategy.getName())) {
					newStrategies.add(oldStrategy);
				}
			}
		});
		this.context.getStrategies().forEach(a -> {
			final var module = a.getClass().getModule();
			final var name = module == null ? null : module.getName();
			if ((module != null) && "conquer".equals(name)) {
				newStrategies.add(a);
			}
		});
		final var strategyNames = newStrategies.stream().map(a -> a.getClass().getName()).toList();
		this.context.getStrategies().clear();
		this.context.getStrategies().addAll(newStrategies);
		this.context.getStrategyNames().clear();
		this.context.getStrategyNames().addAll(strategyNames);
		return this.context;
	}

}
