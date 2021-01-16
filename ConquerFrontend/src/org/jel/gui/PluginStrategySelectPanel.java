package org.jel.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jel.game.data.GlobalContext;
import org.jel.game.data.strategy.StrategyProvider;
import org.jel.game.plugins.Plugin;

final class PluginStrategySelectPanel extends JPanel {
	private transient GlobalContext context;
	private List<JCheckBox> plugins = new ArrayList<>();
	private List<JCheckBox> strategies = new ArrayList<>();

	PluginStrategySelectPanel(GlobalContext context) {
		this.context = context;
		this.init();
	}

	private void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		final var pluginPanel = new JPanel();
		pluginPanel.setLayout(new BoxLayout(pluginPanel, BoxLayout.Y_AXIS));
		this.context.getPlugins().forEach(a -> {
			JCheckBox jb = new JCheckBox(a.getName(), true);
			pluginPanel.add(jb);
			plugins.add(jb);
		});
		final var pluginScrollPanel = new JScrollPane(pluginPanel);
		this.add(pluginScrollPanel);
		final var strategiesPanel = new JPanel();
		strategiesPanel.setLayout(new BoxLayout(strategiesPanel, BoxLayout.Y_AXIS));
		this.context.getStrategies().forEach(a -> {
			// Skip builtin strategies
			final var module = a.getClass().getModule();
			final var name = module == null ? null : module.getName();
			if (module != null && name != null && "org.jel.game".equals(name)) {
				return;
			}
			JCheckBox jb = new JCheckBox(a.getName(), true);
			strategiesPanel.add(jb);
			strategies.add(jb);
		});
	}

	GlobalContext modifyContext() {
		final List<Plugin> newPlugins = new ArrayList<>();
		this.plugins.forEach(a -> {
			if (!a.isSelected()) {
				return;
			}
			for (final var oldPlugin : context.getPlugins()) {
				if (a.getText().equals(oldPlugin.getName())) {
					newPlugins.add(oldPlugin);
				}
			}
		});
		final var pluginNames = newPlugins.stream().map(a -> a.getClass().getName()).collect(Collectors.toList());
		context.getPlugins().clear();
		context.getPlugins().addAll(newPlugins);
		context.getPluginNames().clear();
		context.getPluginNames().addAll(pluginNames);
		final List<StrategyProvider> newStrategies = new ArrayList<>();
		this.strategies.forEach(a -> {
			if (!a.isSelected()) {
				return;
			}
			for (final var oldStrategy : context.getStrategies()) {
				if (a.getText().equals(oldStrategy.getName())) {
					newStrategies.add(oldStrategy);
				}
			}
		});
		context.getStrategies().forEach(a -> {
			final var module = a.getClass().getModule();
			final var name = module == null ? null : module.getName();
			if (module != null && name != null && "org.jel.game".equals(name)) {
				newStrategies.add(a);
			}
		});
		final var strategyNames = newStrategies.stream().map(a -> a.getClass().getName()).collect(Collectors.toList());
		context.getStrategies().clear();
		context.getStrategies().addAll(newStrategies);
		context.getStrategyNames().clear();
		context.getStrategyNames().addAll(strategyNames);
		return this.context;
	}

	private static final long serialVersionUID = 3909874317355075179L;

}
