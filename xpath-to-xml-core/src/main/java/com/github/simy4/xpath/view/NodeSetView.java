package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.TransformingIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public final class NodeSetView<N extends Node> implements IterableNodeView<N> {

    private static final NodeSetView<?> EMPTY_NODE_SET = new NodeSetView<>(Collections.emptySet());

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeSetView<T> empty() {
        return (NodeSetView<T>) EMPTY_NODE_SET;
    }

    private final Set<NodeView<N>> cache = new LinkedHashSet<>();
    private final Iterable<NodeView<N>> nodeSet;

    /**
     * Creates NodeSetView from given nodes and predicate.
     *
     * @param nodes     nodes to wap in a view
     * @param predicate predicate to apply to nodes
     */
    public NodeSetView(final Iterable<? extends N> nodes, final Predicate<? super N> predicate) {
        this(() -> new TransformingIterator<>(
                new FilteringIterator<N>(nodes.iterator(), predicate), NodeView::new));
    }

    public NodeSetView(Iterable<NodeView<N>> nodeSet) {
        this.nodeSet = nodeSet;
    }

    @Override
    public int compareTo(View<N> other) {
        final Iterator<NodeView<N>> iterator = iterator();
        if (iterator.hasNext()) {
            return iterator.next().compareTo(other);
        } else {
            return other.toBoolean() ? -1 : 0;
        }
    }

    @Override
    public boolean toBoolean() {
        return iterator().hasNext();
    }

    @Override
    public double toNumber() {
        final Iterator<NodeView<N>> iterator = iterator();
        return iterator.hasNext() ? iterator.next().toNumber() : Double.NaN;
    }

    @Override
    public String toString() {
        final Iterator<NodeView<N>> iterator = iterator();
        return iterator.hasNext() ? iterator.next().toString() : "";
    }

    @Override
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
    }

    @Override
    public Iterator<NodeView<N>> iterator() {
        return new NodeSetIterator();
    }

    private final class NodeSetIterator implements Iterator<NodeView<N>>, Predicate<NodeView<N>> {

        private Iterator<NodeView<N>> iterator = cache.iterator();
        private boolean swapped;

        @Override
        public boolean hasNext() {
            if (!iterator.hasNext() && !swapped) {
                iterator = new FilteringIterator<>(nodeSet.iterator(), this);
                swapped = true;
            }
            return iterator.hasNext();
        }

        @Override
        public NodeView<N> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return iterator.next();
        }

        @Override
        public boolean test(NodeView<N> nodeView) {
            return cache.add(nodeView);
        }

    }

}
