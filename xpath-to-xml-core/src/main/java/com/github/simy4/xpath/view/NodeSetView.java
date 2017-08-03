package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Immutable
public final class NodeSetView<N extends Node> implements IterableNodeView<N> {

    @SuppressWarnings("unchecked")
    private static final NodeSetView EMPTY_NODE_SET = new NodeSetView(Collections.EMPTY_SET);

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeSetView<T> empty() {
        return (NodeSetView<T>) EMPTY_NODE_SET;
    }

    public static <T extends Node> NodeSetView.Builder<T> builder() {
        return new NodeSetView.Builder<T>();
    }

    private final Set<N> nodeSet;

    private NodeSetView(Set<N> nodeSet) {
        this.nodeSet = nodeSet;
    }

    @Override
    public int compareTo(@Nonnull View<N> other) {
        if (isEmpty()) {
            return other.toBoolean() ? -1 : 0;
        } else {
            return iterator().next().compareTo(other);
        }
    }

    @Override
    public boolean toBoolean() {
        return !isEmpty();
    }

    @Override
    public double toNumber() {
        return isEmpty() ? Double.NaN : iterator().next().toNumber();
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : iterator().next().toString();
    }

    @Override
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
    }

    @Override
    public Iterator<NodeView<N>> iterator() {
        return new WrappingIterator<N>(nodeSet.iterator());
    }

    private boolean isEmpty() {
        return nodeSet.isEmpty();
    }

    public static final class Builder<T extends Node> {

        private final Set<T> nodeSet;

        private Builder() {
            nodeSet = new LinkedHashSet<T>();
        }

        /**
         * Adds new node to a constructing set.
         *
         * @param node node to add
         */
        public void add(NodeView<T> node) {
            nodeSet.add(node.getNode());
        }

        /**
         * Adds new set of nodes to a constructing set.
         *
         * @param nodeSet node set to add
         */
        public void add(IterableNodeView<T> nodeSet) {
            for (NodeView<T> node : nodeSet) {
                add(node);
            }
        }

        public NodeSetView<T> build() {
            return new NodeSetView<T>(Collections.unmodifiableSet(nodeSet));
        }

    }

    private static final class WrappingIterator<T extends Node> implements Iterator<NodeView<T>> {

        private final Iterator<T> delegate;

        private WrappingIterator(Iterator<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public NodeView<T> next() {
            return new NodeView<T>(delegate.next());
        }

        @Override
        public void remove() {
            delegate.remove();
        }

    }

}
