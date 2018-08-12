package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

public class ParentAxisResolver extends AbstractAxisResolver {

    public ParentAxisResolver(QName name) {
        super(name);
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseAxis(ViewContext<N> context) {
        final N parent = context.getNavigator().parentOf(context.getCurrent().getNode());
        return null == parent || !test(parent) ? NodeSetView.empty() : new NodeView<>(parent);
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(ViewContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Parent axis cannot modify XML model");
    }

    @Override
    public String toString() {
        return "parent::" + super.toString();
    }

}
