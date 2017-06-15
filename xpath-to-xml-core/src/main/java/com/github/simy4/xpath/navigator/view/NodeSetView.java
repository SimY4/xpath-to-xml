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
        final NodeSetComparatorVisitor<N> comparatorVisitor = new NodeSetComparatorVisitor<N>(nodeSet);
        other.visit(comparatorVisitor);
        return comparatorVisitor.getResult();
    }

    @Override
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
    }

    @Override
    public Iterator<View<N>> iterator() {
        return nodeSet.iterator();
    }

    public boolean isEmpty() {
        return nodeSet.isEmpty();
    }

    public int size() {
        return nodeSet.size();
    }

    public Set<View<N>> getNodeSet() {
        return nodeSet;
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
    private static final class NodeSetComparatorVisitor<N> implements ViewVisitor<N> {

        private final Set<View<N>> nodeSet;
        private int result;

        private NodeSetComparatorVisitor(Set<View<N>> nodeSet) {
            this.nodeSet = nodeSet;
        }

        @Override
        public void visit(NodeSetView<N> nodeSet) {
            int thisSize = this.nodeSet.size();
            int thatSize = nodeSet.size();
            result = (thisSize < thatSize) ? -1 : ((thisSize == thatSize) ? 0 : 1);
        }

        @Override
        public void visit(LiteralView<N> literal) {
            result = nodeSet.isEmpty() ? -1 : nodeSet.iterator().next().compareTo(literal);
        }

        @Override
        public void visit(NumberView<N> number) {
            result = nodeSet.isEmpty() ? -1 : nodeSet.iterator().next().compareTo(number);
        }

        @Override
        public void visit(NodeView<N> node) {
            result = nodeSet.isEmpty() ? -1 : nodeSet.size() == 1 ? 0 : 1;
        }

        private int getResult() {
            return result;
        }

    }

}
