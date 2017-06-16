package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.effects.Effect;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NavigatorSpi;
import org.w3c.dom.Node;

public class DomNavigatorSpi implements NavigatorSpi {

    @Override
    public boolean canHandle(Object o) {
        return o instanceof Node;
    }

    @Override
    public <T> T process(T xml, Iterable<Effect> effects) {
        if (!canHandle(xml)) {
            throw new IllegalArgumentException("XML model is not supported");
        }
        final Node xmlNode = (Node) xml;
        final Navigator<Node> navigator = new DomNavigator(xmlNode);
        for (Effect effect : effects) {
            effect.perform(navigator);
        }
        return xml;
    }

}
