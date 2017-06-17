package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;
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
        if (nodeSet.isEmpty()) {
            return other.toBoolean() ? -1 : 0;
        } else {
            return nodeSet.iterator().next().compareTo(other);
        }
    }

    @Override
    public boolean toBoolean() {
        return !nodeSet.isEmpty();
    }

    @Override
    public double toNumber() {
        if (nodeSet.isEmpty()) {
            return Double.NaN;
        } else {
            return nodeSet.iterator().next().toNumber();
        }
    }

    @Override
    public String toString() {
        if (nodeSet.isEmpty()) {
            return "";
        } else {
            return nodeSet.iterator().next().toString();
        }
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

}
