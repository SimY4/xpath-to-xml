package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

public class SelfAxisResolver extends AbstractAxisResolver {

    public SelfAxisResolver(QName name) {
        super(name);
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseAxis(ViewContext<N> context) {
        final NodeView<N> self = context.getCurrent();
        return test(self.getNode()) ? self : NodeSetView.empty();
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(ViewContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Self axis cannot modify XML model");
    }

    @Override
    public String toString() {
        return "self::" + super.toString();
    }

}
