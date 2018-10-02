package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

public class AttributeAxisResolver extends AbstractAxisResolver {

    public AttributeAxisResolver(QName name) {
        super(name);
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseAxis(ViewContext<N> context) {
        return new NodeSetView<>(context.getNavigator().attributesOf(context.getCurrent().getNode()), this);
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(ViewContext<N> context) throws XmlBuilderException {
        if (isWildcard()) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return new NodeView<>(context.getNavigator().createAttribute(context.getCurrent().getNode(), name), true);
    }

    @Override
    public String toString() {
        return "attribute::" + super.toString();
    }

}
