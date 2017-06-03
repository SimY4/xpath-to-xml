package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.action.Action;
import org.w3c.dom.Node;

public class DomNavigatorSpi implements NavigatorSpi {

    @Override
    public boolean canHandle(Object o) {
        return o instanceof Node;
    }

    @Override
    public <T> T process(T xml, Iterable<Action> actions) {
        if (!canHandle(xml)) {
            throw new IllegalArgumentException("XML model is not supported");
        }
        final Node xmlNode = (Node) xml;
        final Navigator<Node> navigator = new DomNavigator(xmlNode);
        for (Action action : actions) {
            action.perform(navigator);
        }
        return xml;
    }

}
