package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.ReadOnlyIterator;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Iterator;

public class AncestorOrSelfAxisResolver extends AbstractAxisResolver implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean self;

    public AncestorOrSelfAxisResolver(QName name, boolean self) {
        super(name);
        this.self = self;
    }

    @Override
    protected <N extends Node> Iterable<? extends N> traverseAxis(Navigator<N> navigator, NodeView<N> parent) {
        return new AncestorOrSelfIterable<N>(navigator, parent.getNode(), self);
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(Navigator<N> navigator, NodeView<N> parent, int position)
            throws XmlBuilderException {
        if (self) {
            throw new XmlBuilderException("Ancestor-of-self axis cannot modify XML model");
        } else {
            throw new XmlBuilderException("Ancestor axis cannot modify XML model");
        }
    }

    @Override
    public String toString() {
        return (self ? "ancestor-or-self::" : "ancestor::") + super.toString();
    }

    private static final class AncestorOrSelfIterable<T extends Node> implements Iterable<T> {

        private final Navigator<T> navigator;
        private final T node;
        private final boolean self;

        private AncestorOrSelfIterable(Navigator<T> navigator, T node, boolean self) {
            this.navigator = navigator;
            this.node = node;
            this.self = self;
        }

        @Override
        public Iterator<T> iterator() {
            return new AncestorOrSelf<T>(navigator, node, self);
        }

    }

    private static final class AncestorOrSelf<T extends Node> extends ReadOnlyIterator<T> {

        private final Navigator<T> navigator;
        private T current;

        private AncestorOrSelf(Navigator<T> navigator, T current, boolean self) {
            this.navigator = navigator;
            this.current = self ? current : navigator.parentOf(current);
        }

        @Override
        public boolean hasNext() {
            return null != current;
        }

        @Override
        public T next() {
            final T next = current;
            current = navigator.parentOf(next);
            return next;
        }

    }

}
