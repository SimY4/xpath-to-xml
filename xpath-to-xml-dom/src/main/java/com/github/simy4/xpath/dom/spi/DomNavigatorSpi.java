package com.github.simy4.xpath.dom.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.dom.navigator.DomNavigator;
import com.github.simy4.xpath.dom.navigator.DomNode;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import org.w3c.dom.Node;

/**
 * Standard DOM model navigator extension SPI.
 */
public class DomNavigatorSpi implements NavigatorSpi {

    @Override
    public boolean canHandle(Object o) {
        return o instanceof Node;
    }

    @Override
    public <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException {
        if (!canHandle(xml)) {
            throw new IllegalArgumentException("XML model is not supported");
        }
        final var xmlNode = (Node) xml;
        final var node = new DomNode(xmlNode);
        final var navigator = new DomNavigator(xmlNode);
        for (var effect : effects) {
            effect.perform(navigator, node);
        }
        return xml;
    }

}
