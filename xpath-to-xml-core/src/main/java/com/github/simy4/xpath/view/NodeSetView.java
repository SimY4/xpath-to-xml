package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.TransformingIterator;

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
            @Nonnull
            @Override
            public Iterator<NodeView<T>> iterator() {
                return new TransformingIterator<T, NodeView<T>>(
                        new FilteringIterator<T>(nodes.iterator(), predicate), new Wrapper<T>());
            }
        });
    }

    public static <T extends Node> NodeSetView.Builder<T> builder() {
        return new NodeSetView.Builder<T>();
    }

    private final Iterable<NodeView<N>> nodeSet;

    public NodeSetView(Iterable<NodeView<N>> nodeSet) {
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
        return nodeSet.iterator();
    }

    private boolean isEmpty() {
        return !nodeSet.iterator().hasNext();
    }

    public static final class Builder<T extends Node> {

        private final Set<NodeView<T>> nodeSet;

        private Builder() {
            nodeSet = new LinkedHashSet<NodeView<T>>();
        }

        /**
         * Adds new node to a constructing set.
         *
         * @param node node to add
         */
        public void add(NodeView<T> node) {
            nodeSet.add(node);
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
            return new NodeSetView<T>(nodeSet);
        }

    }

    private static final class Wrapper<T extends Node> implements Function<T, NodeView<T>> {

        @Override
        public NodeView<T> apply(T node) {
            return new NodeView<T>(node);
        }

    }

}
