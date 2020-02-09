package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

public class DescendantOrSelfAxisResolver extends AbstractAxisResolver {

    private static final long serialVersionUID = 1L;

    private final boolean self;

    public DescendantOrSelfAxisResolver(QName name, boolean self) {
        super(name);
        this.self = self;
    }

    @Override
    protected <N extends Node> Iterable<N> traverseAxis(Navigator<N> navigator, NodeView<N> view) {
        return () -> {
            final var descendantOrSelf = new DescendantOrSelf<>(navigator, Set.of(view.getNode()));
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

    static final class DescendantOrSelf<T extends Node> implements Iterator<T> {

        private final Navigator<T> navigator;
        private final Queue<Iterable<? extends T>> stack = new ArrayDeque<>();
        private Iterator<? extends T> current;

        private DescendantOrSelf(Navigator<T> navigator, Iterable<? extends T> current) {
            this(navigator, current.iterator());
        }

        DescendantOrSelf(Navigator<T> navigator, Iterator<? extends T> current) {
            this.navigator = navigator;
            this.current = current;
        }

        @Override
        public boolean hasNext() {
            boolean currentHasNext;
            while (!(currentHasNext = current.hasNext()) && !stack.isEmpty()) {
                current = stack.poll().iterator();
            }
            return currentHasNext;
        }

        @Override
        public T next() {
            final var next = current.next();
            stack.offer(navigator.elementsOf(next));
            return next;
        }

    }

}
