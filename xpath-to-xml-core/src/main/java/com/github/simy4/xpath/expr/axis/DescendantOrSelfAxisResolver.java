package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Iterator;

public class DescendantOrSelfAxisResolver extends AbstractAxisResolver {

    private static final long serialVersionUID = 1L;

    private final boolean self;

    public DescendantOrSelfAxisResolver(QName name, boolean self) {
        super(name);
        this.self = self;
    }

    @Override
    protected <N extends Node> Iterable<N> traverseAxis(Navigator<N> navigator, NodeView<N> view) {
        return new DescendantOrSelfIterable<N>(navigator, view.getNode(), self);
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(Navigator<N> navigator, NodeView<N> view, int position)
            throws XmlBuilderException {
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

    private static final class DescendantOrSelfIterable<T extends Node> implements Iterable<T> {

        private final Navigator<T> navigator;
        private final T node;
        private final boolean self;

        private DescendantOrSelfIterable(Navigator<T> navigator, T node, boolean self) {
            this.navigator = navigator;
            this.node = node;
            this.self = self;
        }

        @Override
        public Iterator<T> iterator() {
            final Iterator<T> descendantOrSelf = new DescendantOrSelf<T>(navigator).apply(node);
            if (!self) {
                descendantOrSelf.next();
            }
            return descendantOrSelf;
        }

    }

    private static final class DescendantOrSelf<T extends Node> implements Function<T, Iterator<T>> {

        private final Navigator<T> navigator;

        private DescendantOrSelf(Navigator<T> navigator) {
            this.navigator = navigator;
        }

        @Override
        public Iterator<T> apply(T self) {
            return new TransformingAndFlatteningIterator<T, T>(navigator.elementsOf(self).iterator(),
                    Collections.singleton(self).iterator(),this);
        }

    }

}
