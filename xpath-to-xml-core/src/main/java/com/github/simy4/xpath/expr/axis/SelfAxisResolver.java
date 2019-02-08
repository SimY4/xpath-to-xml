package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;
import java.util.Collections;

public class SelfAxisResolver extends AbstractAxisResolver {

    public SelfAxisResolver(QName name) {
        super(name);
    }

    @Override
    <N extends Node> Iterable<N> traverseAxis(Navigator<N> navigator, NodeView<N> view) {
        return Collections.singletonList(view.getNode());
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(Navigator<N> navigator, NodeView<N> view, int position)
            throws XmlBuilderException {
        throw new XmlBuilderException("Self axis cannot modify XML model");
    }

    @Override
    public String toString() {
        return "self::" + super.toString();
    }

}
