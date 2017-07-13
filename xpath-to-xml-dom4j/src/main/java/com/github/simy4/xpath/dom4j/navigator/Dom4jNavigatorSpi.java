package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jAttribute;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jDocument;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jElement;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jNode;
import com.github.simy4.xpath.effects.Effect;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NavigatorSpi;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * DOM4J model navigator extension SPI.
 */
public class Dom4jNavigatorSpi implements NavigatorSpi {

    @Override
    public boolean canHandle(Object o) {
        return o instanceof Node;
    }

    @Override
    public <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException {
        if (!canHandle(xml)) {
            throw new IllegalArgumentException("XML model is not supported");
        }
        final Node xmlNode = (Node) xml;
        final Dom4jNode node;
        switch (xmlNode.getNodeType()) {
            case Node.DOCUMENT_NODE:
                node = new Dom4jDocument((Document) xmlNode);
                break;
            case Node.ELEMENT_NODE:
                node = new Dom4jElement((Element) xmlNode);
                break;
            case Node.ATTRIBUTE_NODE:
                node = new Dom4jAttribute((Attribute) xmlNode);
                break;
            default:
                throw new IllegalArgumentException("XML node type is not supported");
        }
        final Navigator<Dom4jNode> navigator = new Dom4jNavigator(node);
        for (Effect effect : effects) {
            effect.perform(navigator);
        }
        return xml;
    }

}
