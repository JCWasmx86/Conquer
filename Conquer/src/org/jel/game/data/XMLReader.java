package org.jel.game.data;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jel.game.data.strategy.StrategyProvider;
import org.jel.game.plugins.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A class that reads the configuration file and returns a {@link GlobalContext}
 */
public final class XMLReader {
	private static final XMLReader INSTANCE = new XMLReader();
	private static final String XMLFILE = Shared.BASE_DIRECTORY + "/info.xml";

	/**
	 * Get the singleton instance.
	 * 
	 * @return The instance
	 */
	public static XMLReader getInstance() {
		return XMLReader.INSTANCE;
	}

	private Class<?> checkedLoading(String s) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(s);
		} catch (final ClassNotFoundException cnfe) {
			return Class.forName(s);
		}
	}

	private <T> List<T> distinct(Collection<T> collection) {
		return collection.stream().distinct().collect(Collectors.toList());
	}

	private List<Plugin> loadPlugins(final List<String> pluginNames) {
		final List<Plugin> ret = new ArrayList<>();
		for (final String s : pluginNames) {
			try {
				final var clazz = this.checkedLoading(s);
				final var rawObject = clazz.getConstructor().newInstance();
				if (!(rawObject instanceof Plugin)) {
					Shared.LOGGER.error("Couldn't load " + clazz.getName() + " as it doesn't implement Plugin!");
					continue;
				}
				final var plugin = (Plugin) rawObject;
				ret.add(plugin);
				Shared.LOGGER.message("Loaded plugin: " + plugin.getName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Shared.LOGGER.exception(e);
			}
		}
		return ret;
	}

	private List<StrategyProvider> loadStrategies(final List<String> strategyNames) {
		final List<StrategyProvider> ret = new ArrayList<>();
		for (final String s : strategyNames) {
			try {
				final var clazz = this.checkedLoading(s);
				final var rawObject = clazz.getConstructor().newInstance();
				if (!(rawObject instanceof StrategyProvider)) {
					Shared.LOGGER
							.error("Couldn't load " + clazz.getName() + " as it doesn't implement StrategyProvider!");
					continue;
				}
				final var strategy = (StrategyProvider) rawObject;
				ret.add(strategy);
				Shared.LOGGER.message("Loaded StrategyProvider: " + strategy.getName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Shared.LOGGER.exception(e);
			}
		}
		return ret;
	}

	/**
	 * Read the info and instantiate all plugins and strategyproviders.
	 * 
	 * @return The read context. On error, an empty context is returned.
	 */
	public GlobalContext readInfo() {
		return this.readInfo(true);
	}

	/**
	 * Read the context and instantiate the {@link Plugin}s and
	 * {@link StrategyProvider}s if {@code instantiate} is true.
	 * 
	 * @param instantiate Whether the classes are instantiated.
	 * @return The context that was read. On error, an empty context is returned.
	 */
	public GlobalContext readInfo(boolean instantiate) {
		final List<InstalledScenario> installedMaps = new ArrayList<>();
		final List<String> pluginNames = new ArrayList<>();
		final List<String> strategyNames = new ArrayList<>();
		Document d;
		try {
			d = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(XMLReader.XMLFILE);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Shared.LOGGER.exception(e);
			return new GlobalContext(installedMaps, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
					new ArrayList<>());
		}
		final var childs = d.getChildNodes();
		Node infoNode = null;
		for (var i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeName().equals("info")) {
				infoNode = childs.item(i);
				break;
			}
		}
		final var topNodes = infoNode.getChildNodes();
		for (var i = 0; i < topNodes.getLength(); i++) {
			final var node = topNodes.item(i);
			if ((node == null) || node.getNodeName().equals("#text") || node.getNodeName().equals("#comment")) {
				continue;
			}
			if (node.getNodeName().equals("scenarios")) {
				this.readScenarios(node, installedMaps);
			} else if (node.getNodeName().equals("plugins")) {
				this.readPlugins(node, pluginNames);
			} else if (node.getNodeName().equals("strategies")) {
				this.readStrategies(node, strategyNames);
			} else {
				Shared.LOGGER.error("Unknown attribute: " + node.getNodeName());
			}
		}
		final List<Plugin> plugins = instantiate ? this.loadPlugins(pluginNames) : new ArrayList<>();
		final List<StrategyProvider> strategies = instantiate ? this.loadStrategies(strategyNames) : new ArrayList<>();
		return new GlobalContext(this.distinct(installedMaps), this.distinct(plugins), this.distinct(strategies),
				this.distinct(pluginNames), this.distinct(strategyNames));
	}

	private void readPlugins(final Node node, final List<String> pluginNames) {
		final var pluginList = node.getChildNodes();
		for (var j = 0; j < pluginList.getLength(); j++) {
			final var pluginInformation = pluginList.item(j);
			if ((pluginInformation == null) || pluginInformation.getNodeName().equals("#text")
					|| pluginInformation.getNodeName().equals("#comment")) {
				continue;
			}
			final var className = pluginInformation.getAttributes().getNamedItem("className").getNodeValue();
			pluginNames.add(className);
		}
	}

	private void readScenarios(final Node node, final List<InstalledScenario> installedMaps) {
		final var scenarioList = node.getChildNodes();
		for (var j = 0; j < scenarioList.getLength(); j++) {
			final var scenarioInformation = scenarioList.item(j);
			if ((scenarioInformation == null) || scenarioInformation.getNodeName().equals("#text")
					|| scenarioInformation.getNodeName().equals("#comment")) {
				continue;
			}
			final var attributes = scenarioInformation.getAttributes();
			installedMaps.add(new InstalledScenario(attributes.getNamedItem("name").getNodeValue(),
					Shared.BASE_DIRECTORY + "/" + attributes.getNamedItem("file").getNodeValue(),
					Shared.BASE_DIRECTORY + "/" + attributes.getNamedItem("thumbnail").getNodeValue()));
		}
	}

	private void readStrategies(final Node node, final List<String> strategyNames) {
		final var strategyList = node.getChildNodes();
		for (var j = 0; j < strategyList.getLength(); j++) {
			final var strategyInformation = strategyList.item(j);
			if ((strategyInformation == null) || strategyInformation.getNodeName().equals("#text")
					|| strategyInformation.getNodeName().equals("#comment")) {
				continue;
			}
			final var className = strategyInformation.getAttributes().getNamedItem("className").getNodeValue();
			strategyNames.add(className);
		}
	}
}
