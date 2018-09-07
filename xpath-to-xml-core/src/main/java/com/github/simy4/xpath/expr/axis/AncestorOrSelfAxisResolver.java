package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.ReadOnlyIterator;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;
import java.util.Iterator;

public class AncestorOrSelfAxisResolver extends AbstractAxisResolver {

    private final boolean self;

    public AncestorOrSelfAxisResolver(QName name, boolean self) {
        super(name);
        this.self = self;
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseAxis(ViewContext<N> context) {
        return new NodeSetView<N>(new AncestorOrSelfIterable<N>(context, self), this);
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(ViewContext<N> context) throws XmlBuilderException {
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

        private final ViewContext<T> context;
        private final boolean self;

        private AncestorOrSelfIterable(ViewContext<T> context, boolean self) {
            this.context = context;
            this.self = self;
        }

        @Override
        public Iterator<T> iterator() {
            return new AncestorOrSelf<T>(context.getNavigator(), context.getCurrent().getNode(), self);
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