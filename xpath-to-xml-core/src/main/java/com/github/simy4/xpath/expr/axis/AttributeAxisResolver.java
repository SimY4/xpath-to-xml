package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

public class AttributeAxisResolver extends AbstractAxisResolver {

    public AttributeAxisResolver(QName name) {
        super(name);
    }

    @Override
    <N extends Node> Iterable<? extends N> traverseAxis(Navigator<N> navigator, NodeView<N> parent) {
        return navigator.attributesOf(parent.getNode());
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(Navigator<N> navigator, NodeView<N> parent, int position)
            throws XmlBuilderException {
        if (isWildcard()) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return new NodeView<N>(navigator.createAttribute(parent.getNode(), name), position);
    }

    @Override
    public String toString() {
        return "attribute::" + super.toString();
    }

}
