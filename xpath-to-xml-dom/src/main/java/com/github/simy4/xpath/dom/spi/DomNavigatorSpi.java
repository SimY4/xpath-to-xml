package com.github.simy4.xpath.dom.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.dom.navigator.DomNavigator;
import com.github.simy4.xpath.dom.navigator.DomNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Standard DOM model navigator extension SPI.
 */
public class DomNavigatorSpi implements NavigatorSpi {

    @Override
    public boolean canHandle(Object o) {
        return o instanceof Node && (o instanceof Document || (null != ((Node) o).getOwnerDocument()));
    }

    @Override
    public <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException {
        if (!canHandle(xml)) {
            throw new IllegalArgumentException("XML model is not supported");
        }
        final Node xmlNode = (Node) xml;
        final DomNode node = new DomNode(xmlNode);
        final Navigator<DomNode> navigator = new DomNavigator(xmlNode);
        for (Effect effect : effects) {
            effect.perform(navigator, node);
        }
        return xml;
    }

}
