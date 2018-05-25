package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.TransformingIterator;

import java.util.Collections;
import java.util.Iterator;

public final class NodeSetView<N extends Node> implements IterableNodeView<N> {

    private static final NodeSetView<?> EMPTY_NODE_SET = new NodeSetView<Node>(Collections.<NodeView<Node>>emptySet());

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeSetView<T> empty() {
        return (NodeSetView<T>) EMPTY_NODE_SET;
    }

    /**
     * Creates filtered node view from given nodes and predicate.
     *
     * @param nodes     nodes to wap in a view
     * @param predicate predicate to apply to nodes
     * @param <T> XML model type
     * @return newly created node set view
     */
    public static <T extends Node> NodeSetView<T> filtered(final Iterable<? extends T> nodes,
                                                           final Predicate<? super T> predicate) {
        return new NodeSetView<T>(new Iterable<NodeView<T>>() {
            @Override
            public Iterator<NodeView<T>> iterator() {
                return new TransformingIterator<T, NodeView<T>>(
                        new FilteringIterator<T>(nodes.iterator(), predicate), new NodeWrapper<T>());
            }
        });
    }

    private final Iterable<NodeView<N>> nodeSet;

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
        return nodeSet.iterator();
    }

    @Override
    public IterableNodeView<N> filter(final Navigator<N> navigator, final boolean greedy,
                                      final Predicate<ViewContext<?>> predicate) throws XmlBuilderException {
        return filter(navigator, greedy, 1, predicate);
    }

    @Override
    public IterableNodeView<N> filter(final Navigator<N> navigator, final boolean greedy, final int position,
                                      final Predicate<ViewContext<?>> predicate) throws XmlBuilderException {
        return new NodeSetView<N>(new Iterable<NodeView<N>>() {
            @Override
            public Iterator<NodeView<N>> iterator() {
                final Iterator<NodeView<N>> iterator = nodeSet.iterator();
                return new FilteringIterator<NodeView<N>>(iterator,
                        new PredicateWrapper<N>(navigator, iterator, greedy, position, predicate));
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof View)) {
            return false;
        }

        final Iterator<NodeView<N>> iterator = iterator();
        if (iterator.hasNext()) {
            return iterator.next().equals(o);
        } else {
            return !((View) o).toBoolean();
        }
    }

    @Override
    public int hashCode() {
        final Iterator<NodeView<N>> iterator = iterator();
        return iterator.hasNext() ? iterator.next().hashCode() : 0;
    }

    private static final class NodeWrapper<T extends Node> implements Function<T, NodeView<T>> {

        @Override
        public NodeView<T> apply(T node) {
            return new NodeView<T>(node);
        }

    }

    private static final class PredicateWrapper<T extends Node> implements Predicate<NodeView<T>> {

        private final Navigator<T> navigator;
        private final Iterator<NodeView<T>> wrappingNodeSet;
        private final boolean greedy;
        private final Predicate<ViewContext<?>> delegate;
        private int position;

        private PredicateWrapper(Navigator<T> navigator, Iterator<NodeView<T>> wrappingNodeSet, boolean greedy,
                                 int position, Predicate<ViewContext<?>> delegate) {
            this.navigator = navigator;
            this.wrappingNodeSet = wrappingNodeSet;
            this.greedy = greedy;
            this.position = position;
            this.delegate = delegate;
        }

        @Override
        public boolean test(NodeView<T> node) {
            final ViewContext<T> context = new ViewContext<T>(navigator, node, greedy, wrappingNodeSet.hasNext(),
                    position++);
            return delegate.test(context);
        }

    }

}
