package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Set;

public class DescendantOrSelfAxisResolver extends AbstractAxisResolver {

    private final boolean self;

    public DescendantOrSelfAxisResolver(QName name, boolean self) {
        super(name);
        this.self = self;
    }

    @Override
    <N extends Node> Iterable<N> traverseAxis(Navigator<N> navigator, NodeView<N> view) {
        return () -> {
            final Iterator<N> descendantOrSelf = new DescendantOrSelf<>(navigator).apply(view.getNode());
            if (!self) {
                descendantOrSelf.next();
            }
            return descendantOrSelf;
        };
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

    private static final class DescendantOrSelf<T extends Node> implements Function<T, Iterator<T>> {

        private final Navigator<T> navigator;

        private DescendantOrSelf(Navigator<T> navigator) {
            this.navigator = navigator;
        }

        @Override
        public Iterator<T> apply(T self) {
            return new TransformingAndFlatteningIterator<>(navigator.elementsOf(self).iterator(),
                    Set.of(self).iterator(), this);
        }

    }

}
