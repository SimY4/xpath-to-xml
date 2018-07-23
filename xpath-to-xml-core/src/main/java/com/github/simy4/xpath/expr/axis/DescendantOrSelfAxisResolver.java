package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;
import java.util.Iterator;

public class DescendantOrSelfAxisResolver extends AbstractAxisResolver {

    public DescendantOrSelfAxisResolver(QName name) {
        super(name);
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseAxis(ViewContext<N> context) {
        return new NodeSetView<N>(new DescendantOrSelfIterable<N>(context), this);
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(ViewContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Descendant-of-self axis cannot modify XML model");
    }

    @Override
    public String toString() {
        return "descendant-or-self::" + super.toString();
    }

    private static final class DescendantOrSelfIterable<T extends Node> implements Iterable<T> {

        private final ViewContext<T> context;

        private DescendantOrSelfIterable(ViewContext<T> context) {
            this.context = context;
        }

        @Override
        public Iterator<T> iterator() {
            return new DescendantOrSelf<T>(context.getNavigator()).apply(context.getCurrent().getNode());
        }

    }

}
