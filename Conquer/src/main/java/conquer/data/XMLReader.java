package conquer.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import conquer.data.strategy.StrategyProvider;
import conquer.plugins.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A class that reads the configuration file and returns a {@link GlobalContext}
 */
@Deprecated(forRemoval = true)
public final class XMLReader {
	static final String XMLFILE = Shared.BASE_DIRECTORY + "/info.xml";
	private static final XMLReader INSTANCE = new XMLReader();
	private static Consumer<Throwable> throwableConsumer;

	/**
	 * Get the singleton instance.
	 *
	 * @return The instance
	 */
	public static XMLReader getInstance() {
		return XMLReader.INSTANCE;
	}

	/**
	 * Set a consumer that is called, as soon as an exception occurs.
	 *
	 * @param throwable The consumer
	 */
	public static synchronized void setThrowableConsumer(final Consumer<Throwable> throwable) {
		XMLReader.throwableConsumer = throwable;
	}

	private boolean bad(final Node n) {
		return (n == null) || (n.getNodeValue() == null);
	}

	private Class<?> checkedLoading(final String s) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(s);
		} catch (final ClassNotFoundException cnfe) {
			return Class.forName(s);
		}
	}

	private <T> List<T> distinct(final Collection<T> collection) {
		return collection.stream().distinct().collect(Collectors.toList());
	}

	private Node findNode(final NodeList children) {
		for (var i = 0; i < children.getLength(); i++) {
			final var child = children.item(i);
			if ("info".equals(child.getNodeName())) {
				return child;
			}
		}
		return null;
	}

	private boolean goodNode(final Node n) {
		return (n != null) && !"#text".equals(n.getNodeName()) && !"#comment".equals(n.getNodeName())
			&& n.hasAttributes();
	}

	private List<Plugin> loadPlugins(final List<String> pluginNames) {
		final List<Plugin> ret = new ArrayList<>();
		for (final String s : pluginNames) {
			try {
				final var clazz = this.checkedLoading(s);
				final var rawObject = clazz.getConstructor().newInstance();
				if (!(rawObject instanceof Plugin plugin)) {
					Shared.LOGGER.error("Couldn't load " + clazz.getName() + " as it doesn't implement Plugin!");
					continue;
				}
				ret.add(plugin);
				Shared.LOGGER.message("Loaded plugin: " + plugin.getName());
			} catch (final Exception e) {
				Shared.LOGGER.exception(e);
				if (XMLReader.throwableConsumer != null) {
					XMLReader.throwableConsumer.accept(e);
				}
			}
		}
		return ret;
	}

	private List<ConquerInfoReaderFactory> loadReaders(final List<String> readerFactories) {
		final List<ConquerInfoReaderFactory> ret = new ArrayList<>();
		for (final String s : readerFactories) {
			try {
				final var clazz = this.checkedLoading(s);
				final var rawObject = clazz.getConstructor().newInstance();
				if (!(rawObject instanceof ConquerInfoReaderFactory plugin)) {
					Shared.LOGGER.error(
						"Couldn't load " + clazz.getName() + " as it doesn't implement ConquerInfoReaderFactory!");
					continue;
				}
				ret.add(plugin);
				Shared.LOGGER.message("Loaded reader: " + s);
			} catch (final Exception e) {
				Shared.LOGGER.exception(e);
				if (XMLReader.throwableConsumer != null) {
					XMLReader.throwableConsumer.accept(e);
				}
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
				if (!(rawObject instanceof StrategyProvider strategy)) {
					Shared.LOGGER
						.error("Couldn't load " + clazz.getName() + " as it doesn't implement StrategyProvider!");
					continue;
				}
				ret.add(strategy);
				Shared.LOGGER.message("Loaded StrategyProvider: " + strategy.getName());
			} catch (final Exception e) {
				Shared.LOGGER.exception(e);
				if (XMLReader.throwableConsumer != null) {
					XMLReader.throwableConsumer.accept(e);
				}
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
	 *
	 * @return The context that was read. On error, an empty context is returned.
	 */
	public GlobalContext readInfo(final boolean instantiate) {
		final Document d;
		try {
			d = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(XMLReader.XMLFILE);
		} catch (final SAXException | IOException | ParserConfigurationException e) {
			Shared.LOGGER.exception(e);
			if (XMLReader.throwableConsumer != null) {
				XMLReader.throwableConsumer.accept(e);
			}
			return new GlobalContext(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}
		final var infoNode = this.findNode(d.getChildNodes());
		if (infoNode == null) {
			return new GlobalContext(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}
		return this.fill(infoNode, instantiate);
	}

	private GlobalContext fill(final Node infoNode, final boolean instantiate) {
		final List<InstalledScenario> installedMaps = new ArrayList<>();
		final List<String> pluginNames = new ArrayList<>();
		final List<String> strategyNames = new ArrayList<>();
		final List<String> readerFactoryNames = new ArrayList<>();
		final var topNodes = infoNode.getChildNodes();
		for (var i = 0; i < topNodes.getLength(); i++) {
			final var node = topNodes.item(i);
			if ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE)) {
				final var nodeName = node.getNodeName();
				switch (nodeName) {
					case "scenarios" -> this.readScenarios(node, installedMaps);
					case "plugins" -> this.readList(node, pluginNames);
					case "strategies" -> this.readList(node, strategyNames);
					case "readers" -> this.readList(node, readerFactoryNames);
					default -> Shared.LOGGER.error("Unknown attribute: " + nodeName);
				}
			}
		}
		final List<Plugin> plugins = instantiate ? this.loadPlugins(pluginNames) : new ArrayList<>();
		final List<StrategyProvider> strategies = instantiate ? this.loadStrategies(strategyNames) : new ArrayList<>();
		final List<ConquerInfoReaderFactory> readerFactories = instantiate ? this.loadReaders(readerFactoryNames)
			: new ArrayList<>();
		return new GlobalContext(this.distinct(installedMaps), this.distinct(plugins), this.distinct(strategies),
			readerFactories, this.distinct(pluginNames), this.distinct(strategyNames),
			this.distinct(readerFactoryNames));
	}

	private void readList(final Node node, final List<String> list) {
		final var nodeList = node.getChildNodes();
		for (var j = 0; j < nodeList.getLength(); j++) {
			final var dataNode = nodeList.item(j);
			if (this.goodNode(dataNode)) {
				final var attributes = dataNode.getAttributes();
				final var classNameNode = attributes.getNamedItem("className");
				if (this.bad(classNameNode)) {
					Shared.LOGGER.error("readList - classNameNode==null");
					continue;
				}
				final var className = classNameNode.getNodeValue();
				list.add(className);
			}
		}
	}

	private void readScenarios(final Node node, final List<InstalledScenario> installedMaps) {
		final var scenarioList = node.getChildNodes();
		for (var j = 0; j < scenarioList.getLength(); j++) {
			final var scenarioInformation = scenarioList.item(j);
			if (this.goodNode(scenarioInformation)) {
				final var attributes = scenarioInformation.getAttributes();
				final var name = attributes.getNamedItem("name");
				final var file = attributes.getNamedItem("file");
				final var thumbnail = attributes.getNamedItem("thumbnail");
				if (this.bad(name) || this.bad(file) || this.bad(thumbnail)) {
					Shared.LOGGER.error(name + "//" + file + "//" + thumbnail);
					continue;
				}
				installedMaps.add(
					new InstalledScenario(name.getNodeValue(), Shared.BASE_DIRECTORY + "/" + file.getNodeValue(),
						Shared.BASE_DIRECTORY + "/" + thumbnail.getNodeValue(), null));
			}
		}
	}
}
