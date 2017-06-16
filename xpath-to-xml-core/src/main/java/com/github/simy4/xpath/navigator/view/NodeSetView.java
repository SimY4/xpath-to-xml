package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Immutable
public final class NodeSetView<N> implements View<N>, Iterable<View<N>> {

    @SuppressWarnings("unchecked")
    private static final NodeSetView EMPTY_NODE_SET = new NodeSetView(Collections.EMPTY_SET);

    @SuppressWarnings("unchecked")
    public static <T> NodeSetView<T> empty() {
        return (NodeSetView<T>) EMPTY_NODE_SET;
    }

    public static <T> NodeSetView<T> singleton(View<T> node) {
        return new NodeSetView<T>(Collections.singleton(node));
    }

    public static <T> NodeSetView.Builder<T> builder() {
        return new NodeSetView.Builder<T>();
    }

    public static <T> NodeSetView.Builder<T> builder(int initialCapacity) {
        return new NodeSetView.Builder<T>(initialCapacity);
    }

    private final Set<View<N>> nodeSet;

    private NodeSetView(Set<View<N>> nodeSet) {
        this.nodeSet = nodeSet;
    }

    @Override
    public int compareTo(View<N> other) {
        return other.visit(new NodeSetComparatorVisitor());
    }

    @Override
    public boolean isEmpty() {
        return nodeSet.isEmpty();
    }

    @Override
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
    }

    @Override
    public Iterator<View<N>> iterator() {
        return nodeSet.iterator();
    }

    public int size() {
        return nodeSet.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NodeSetView<?> that = (NodeSetView<?>) o;

        return nodeSet.equals(that.nodeSet);
    }

    @Override
    public int hashCode() {
        return nodeSet.hashCode();
    }

    @Override
    public String toString() {
        return nodeSet.toString();
    }

    public static final class Builder<T> {

        private final Set<View<T>> nodeSet;

        private Builder() {
            nodeSet = new LinkedHashSet<View<T>>();
        }

        private Builder(int initialCapacity) {
            nodeSet = new LinkedHashSet<View<T>>(initialCapacity);
        }

        /**
         * Adds new view to a constructing set. Flattens view if necessary.
         *
         * @param node node to add
         */
        public void add(View<T> node) {
            if (node instanceof NodeSetView) {
                for (View<T> n : (NodeSetView<T>) node) {
                    add(n);
                }
            } else {
                nodeSet.add(node);
            }
        }

        public NodeSetView<T> build() {
            return new NodeSetView<T>(Collections.unmodifiableSet(nodeSet));
        }

    }

    @NotThreadSafe
    private final class NodeSetComparatorVisitor implements ViewVisitor<N, Integer> {

        @Override
        public Integer visit(NodeSetView<N> nodeSet) {
            int thisSize = NodeSetView.this.nodeSet.size();
            int thatSize = nodeSet.size();
            return (thisSize < thatSize) ? -1 : ((thisSize == thatSize) ? 0 : 1);
        }

        @Override
        public Integer visit(LiteralView<N> literal) {
            return NodeSetView.this.nodeSet.isEmpty() ? -1
                    : NodeSetView.this.nodeSet.iterator().next().compareTo(literal);
        }

        @Override
        public Integer visit(NumberView<N> number) {
            return NodeSetView.this.nodeSet.isEmpty() ? -1
                    : NodeSetView.this.nodeSet.iterator().next().compareTo(number);
        }

        @Override
        public Integer visit(NodeView<N> node) {
            return NodeSetView.this.nodeSet.isEmpty() ? -1
                    : NodeSetView.this.nodeSet.iterator().next().compareTo(node);
        }

    }

}
