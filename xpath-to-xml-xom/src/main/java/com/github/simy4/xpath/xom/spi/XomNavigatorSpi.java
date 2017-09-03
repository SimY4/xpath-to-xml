package com.github.simy4.xpath.xom.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.effects.Effect;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.NavigatorSpi;
import com.github.simy4.xpath.xom.navigator.XomNavigator;
import com.github.simy4.xpath.xom.navigator.node.XomAttribute;
import com.github.simy4.xpath.xom.navigator.node.XomDocument;
import com.github.simy4.xpath.xom.navigator.node.XomElement;
import com.github.simy4.xpath.xom.navigator.node.XomNode;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

/**
 * XOM model navigator extension SPI.
 */
public class XomNavigatorSpi implements NavigatorSpi {

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
        final XomNode<?> node;
        if (xmlNode instanceof Document) {
            node = new XomDocument((Document) xmlNode);
        } else if (xmlNode instanceof Element) {
            node = new XomElement((Element) xmlNode);
        } else if (xmlNode instanceof Attribute) {
            node = new XomAttribute((Attribute) xmlNode);
        } else {
            throw new IllegalArgumentException("XML node type is not supported");
        }
        final Navigator<XomNode> navigator = new XomNavigator(xmlNode);
        for (Effect effect : effects) {
            effect.perform(navigator, node);
        }
        return xml;
    }

}
