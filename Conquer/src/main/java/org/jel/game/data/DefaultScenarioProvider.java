package org.jel.game.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DefaultScenarioProvider implements InstalledScenarioProvider {

	@Override
	public List<InstalledScenario> getScenarios() {
		final List<InstalledScenario> ret = new ArrayList<>();
		this.initializeFromDefaultLocation(ret);
		try {
			final var d = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(XMLReader.XMLFILE);
			final var infoNode = this.findNode(d.getChildNodes());
			if (infoNode == null) {
				return ret;
			}
			this.parseNodes(ret, infoNode.getChildNodes());
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Shared.LOGGER.exception(e);
		}
		return ret;
	}

	private void initializeFromDefaultLocation(final List<InstalledScenario> ret) {
		final var directoryName = Shared.isWindows() ? (System.getenv("ProgramFiles") + "\\Conquer\\scenarios")
				: "/usr/share/conquer/scenarios";
		final var directory = new File(directoryName);
		if (!directory.exists()) {
			return;
		}
		final var files = directory.listFiles(File::isDirectory);
		for (final var scenarioDirectory : files) {
			final var name = scenarioDirectory.getName();
			final var file = new File(scenarioDirectory, name + ".data").getAbsolutePath();
			final var thumbnail = new File(scenarioDirectory, name + ".png").getAbsolutePath();
			ret.add(new InstalledScenario(name, file, thumbnail, null));
		}
	}

	private void parseNodes(final List<InstalledScenario> ret, final NodeList topNodes) {
		for (var i = 0; i < topNodes.getLength(); i++) {
			this.parseNode(topNodes.item(i), ret);
		}
	}

	private void parseNode(final Node node, final List<InstalledScenario> ret) {
		if ((node == null) || (node.getNodeType() != Node.ELEMENT_NODE) || (!node.getNodeName().equals("scenarios"))) {
			return;
		}
		final var scenarioList = node.getChildNodes();
		for (var j = 0; j < scenarioList.getLength(); j++) {
			final var scenarioInformation = scenarioList.item(j);
			if (this.goodNode(scenarioInformation)) {
				final var info = this.constructNode(scenarioInformation);
				if (info != null) {
					ret.add(this.constructNode(scenarioInformation));
				}
			}
		}
	}

	private InstalledScenario constructNode(final Node scenarioInformation) {
		final var attributes = scenarioInformation.getAttributes();
		final var name = attributes.getNamedItem("name");
		final var file = attributes.getNamedItem("file");
		final var thumbnail = attributes.getNamedItem("thumbnail");
		if (this.bad(name) || this.bad(file) || this.bad(thumbnail)) {
			Shared.LOGGER.error(name + "//" + file + "//" + thumbnail);
			return null;
		}
		return new InstalledScenario(name.getNodeValue(), Shared.BASE_DIRECTORY + "/" + file.getNodeValue(),
				Shared.BASE_DIRECTORY + "/" + thumbnail.getNodeValue(), null);
	}

	// A bit copied from XMLReader.java
	private Node findNode(final NodeList childs) {
		for (var i = 0; i < childs.getLength(); i++) {
			final var child = childs.item(i);
			if (child.getNodeName().equals("info")) {
				return child;
			}
		}
		return null;
	}

	private boolean bad(final Node n) {
		return (n == null) || (n.getNodeValue() == null);
	}

	private boolean goodNode(final Node n) {
		return (n != null) && !n.getNodeName().equals("#text") && !n.getNodeName().equals("#comment")
				&& n.hasAttributes();
	}
}
