package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.FlatteningIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.TransformingIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Immutable
public final class NodeSetView<N extends Node> implements IterableNodeView<N> {

    private static final NodeSetView<?> EMPTY_NODE_SET = new NodeSetView<>(Collections.emptySet());

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
        return new NodeSetView<>(() -> new TransformingIterator<>(
                new FilteringIterator<T>(nodes.iterator(), predicate), new NodeWrapper<>()));
    }

    private final Iterable<NodeView<N>> nodeSet;

    public NodeSetView(Iterable<NodeView<N>> nodeSet) {
        this.nodeSet = nodeSet;
    }

    @Override
    public int compareTo(@Nonnull View<N> other) {
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
        return new NodeSetView<>(() -> {
            final Iterator<NodeView<N>> iterator = nodeSet.iterator();
            return new FilteringIterator<>(iterator,
                    new PredicateWrapper<>(navigator, iterator, greedy, position, predicate));
        });
    }

    @Override
    public IterableNodeView<N> flatMap(final Navigator<N> navigator, final boolean greedy,
                                       final Function<ViewContext<N>, IterableNodeView<N>> fmap)
            throws XmlBuilderException {
        return new NodeSetView<>(() -> {
            final Iterator<NodeView<N>> iterator = nodeSet.iterator();
            return new FilteringIterator<>(new FlatteningIterator<>(new TransformingIterator<>(iterator,
                    new TransformerWrapper<>(navigator, iterator, greedy, fmap))
            ), new Distinct<>());
        });
    }

    private static final class NodeWrapper<T extends Node> implements Function<T, NodeView<T>> {

        @Override
        public NodeView<T> apply(T node) {
            return new NodeView<>(node);
        }

    }

    private abstract static class AbstractWrapper<T extends Node> {

        protected final Navigator<T> navigator;
        protected final Iterator<NodeView<T>> wrappingNodeSet;
        protected final boolean greedy;
        private int position;

        AbstractWrapper(Navigator<T> navigator, Iterator<NodeView<T>> wrappingNodeSet, boolean greedy, int position) {
            this.navigator = navigator;
            this.wrappingNodeSet = wrappingNodeSet;
            this.greedy = greedy;
            this.position = position;
        }

        final ViewContext<T> wrap(NodeView<T> node) {
            return new ViewContext<>(navigator, node, greedy, wrappingNodeSet.hasNext(), position++);
        }

    }

    @NotThreadSafe
    private static final class PredicateWrapper<T extends Node> extends AbstractWrapper<T>
            implements Predicate<NodeView<T>> {

        private final Predicate<ViewContext<?>> delegate;

        private PredicateWrapper(Navigator<T> navigator, Iterator<NodeView<T>> wrappingNodeSet, boolean greedy,
                                 int position, Predicate<ViewContext<?>> delegate) {
            super(navigator, wrappingNodeSet, greedy, position);
            this.delegate = delegate;
        }

        @Override
        public boolean test(NodeView<T> node) {
            return delegate.test(wrap(node));
        }

    }

    @NotThreadSafe
    private static final class TransformerWrapper<T extends Node> extends AbstractWrapper<T>
            implements Function<NodeView<T>, Iterator<NodeView<T>>> {

        private final Function<ViewContext<T>, IterableNodeView<T>> delegate;

        private TransformerWrapper(Navigator<T> navigator, Iterator<NodeView<T>> wrappingNodeSet, boolean greedy,
                                   Function<ViewContext<T>, IterableNodeView<T>> delegate) {
            super(navigator, wrappingNodeSet, greedy, 1);
            this.delegate = delegate;
        }

        @Override
        public Iterator<NodeView<T>> apply(NodeView<T> node) {
            return delegate.apply(wrap(node)).iterator();
        }

    }

    private static final class Distinct<T extends Node> implements Predicate<NodeView<T>> {

        private final Set<T> visited = new HashSet<>();

        @Override
        public boolean test(NodeView<T> node) {
            return visited.add(node.getNode());
        }

    }

}
