package com.github.simy4.xpath.jdom.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.jdom.navigator.JDomNavigator;
import com.github.simy4.xpath.jdom.navigator.node.JDomAttribute;
import com.github.simy4.xpath.jdom.navigator.node.JDomDocument;
import com.github.simy4.xpath.jdom.navigator.node.JDomElement;
import com.github.simy4.xpath.jdom.navigator.node.JDomNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Parent;

/**
 * XOM model navigator extension SPI.
 */
public class JDomNavigatorSpi implements NavigatorSpi {

    public JDomNavigatorSpi() {
    }

    @Override
    public boolean canHandle(Object o) {
        return (o instanceof Parent && null != ((Parent) o).getDocument())
                || (o instanceof Attribute && null != ((Attribute) o).getDocument());
    }

    @Override
    public <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException {
        final JDomNode node;
        if (xml instanceof Document) {
            node = new JDomDocument((Document) xml);
        } else if (xml instanceof Element) {
            node = new JDomElement((Element) xml);
        } else if (xml instanceof Attribute) {
            node = new JDomAttribute((Attribute) xml);
        } else {
            throw new IllegalArgumentException("XML model is not supported");
        }
        final Navigator<JDomNode> navigator = new JDomNavigator(node.getRoot());
        for (var effect : effects) {
            effect.perform(navigator, node);
        }
        return xml;
    }

}
