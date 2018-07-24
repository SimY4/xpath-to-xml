package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FlatteningIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingIterator;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Iterator;

public class DescendantOrSelfAxisResolver extends AbstractAxisResolver {

    private final boolean self;

    public DescendantOrSelfAxisResolver(QName name, boolean self) {
        super(name);
        this.self = self;
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseAxis(ViewContext<N> context) {
        return new NodeSetView<>(() -> {
            final Iterator<N> descendantOrSelf = new DescendantOrSelf<>(context.getNavigator())
                    .apply(context.getCurrent().getNode());
            if (!self) {
                descendantOrSelf.next();
            }
            return descendantOrSelf;
        }, this);
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(ViewContext<N> context) throws XmlBuilderException {
        if (self) {
            throw new XmlBuilderException("Descendant-of-self axis cannot modify XML model");
        } else {
            throw new XmlBuilderException("Descendant axis cannot modify XML model");
        }
    }

    @Override
    public String toString() {
        return (self ? "descendant-or-self::" : "descendant::") + super.toString();
    }

    private static final class DescendantOrSelf<T extends Node> implements Function<T, Iterator<T>> {

        private final Navigator<T> navigator;

        DescendantOrSelf(Navigator<T> navigator) {
            this.navigator = navigator;
        }

        @Override
        public Iterator<T> apply(T self) {
            return new FlatteningIterator<>(Collections.singleton(self).iterator(),
                    new TransformingIterator<>(navigator.elementsOf(self).iterator(), this));
        }

    }

}
