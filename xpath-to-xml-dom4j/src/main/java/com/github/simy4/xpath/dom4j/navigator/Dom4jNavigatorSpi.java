package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.effects.Effect;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NavigatorSpi;
import org.dom4j.Node;

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
        final Navigator<Node> navigator = new Dom4jNavigator(xmlNode);
        for (Effect effect : effects) {
            effect.perform(navigator);
        }
        return xml;
    }

}
