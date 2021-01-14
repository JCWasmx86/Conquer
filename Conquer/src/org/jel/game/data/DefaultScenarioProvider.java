package org.jel.game.data;

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
		try {
			final var d = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(XMLReader.XMLFILE);
			final var infoNode = this.findNode(d.getChildNodes());
			final var topNodes = infoNode.getChildNodes();
			for (var i = 0; i < topNodes.getLength(); i++) {
				final var node = topNodes.item(i);
				if ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE)) {
					final var nodeName = node.getNodeName();
					if (nodeName.equals("scenarios")) {
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
								ret.add(new InstalledScenario(name.getNodeValue(),
										Shared.BASE_DIRECTORY + "/" + file.getNodeValue(),
										Shared.BASE_DIRECTORY + "/" + thumbnail.getNodeValue()));
							}
						}
					}
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Shared.LOGGER.exception(e);
			return List.of();
		}
		return ret;
	}

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
