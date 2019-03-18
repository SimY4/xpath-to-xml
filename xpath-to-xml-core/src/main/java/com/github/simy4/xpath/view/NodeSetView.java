package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class NodeSetView<N extends Node> implements IterableNodeView<N>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final NodeSetView<?> EMPTY_NODE_SET = new EmptyNodeSet<>();

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeSetView<T> empty() {
        return (NodeSetView<T>) EMPTY_NODE_SET;
    }

    public static <T extends Node> NodeSetView<T> of(Iterable<? extends T> iterable, Predicate<? super T> filter) {
        return new IterableNodeSet<>(iterable, filter);
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
    public IterableNodeView<N> flatMap(Function<? super NodeView<N>, ? extends IterableNodeView<N>> fmap) {
        return new FlatMapNodeSet<>(this, fmap);
    }

    private static final class EmptyNodeSet<T extends Node> extends NodeSetView<T> {

        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<NodeView<T>> iterator() {
            return Collections.emptyIterator();
        }

    }

    private static final class IterableNodeSet<T extends Node> extends NodeSetView<T> implements Predicate<T> {

        private static final long serialVersionUID = 1L;

        private final Set<T> cache = new LinkedHashSet<>();
        private final transient Iterable<? extends T> nodeSet;
        private final transient Predicate<? super T> filter;

        private IterableNodeSet(Iterable<? extends T> nodeSet, Predicate<? super T> filter) {
            this.nodeSet = nodeSet;
            this.filter = filter;
        }

        @Override
        public Iterator<NodeView<T>> iterator() {
            return new IteratorImpl();
        }

        @Override
        public boolean test(T node) {
            return filter.test(node) && cache.add(node);
        }

        @SuppressWarnings({"StatementWithEmptyBody", "UnusedVariable"})
        private void writeObject(ObjectOutputStream out) throws IOException {
            for (NodeView<T> ignored : this) { } // eagerly consume this node set to populate cache
            out.writeObject(cache);
        }

        private final class IteratorImpl extends PositionAwareIterator<NodeView<T>> {

            private Iterator<? extends T> current = cache.iterator();
            private boolean swapped;

            @Override
            public boolean hasNext() {
                boolean hasNext = current.hasNext();
                if (!hasNext && !swapped && null != nodeSet) {
                    current = new FilteringIterator<T>(nodeSet.iterator(), IterableNodeSet.this);
                    swapped = true;
                    hasNext = current.hasNext();
                }
                return hasNext;
            }

            @Override
            public NodeView<T> next(int position) {
                return new NodeView<>(current.next(), position, hasNext());
            }

        }

    }

    private static final class FlatMapNodeSet<T extends Node> extends NodeSetView<T>
            implements Function<NodeView<T>, Iterator<NodeView<T>>>, Predicate<NodeView<T>> {

        private static final long serialVersionUID = 1L;

        private final Set<T> cache = new LinkedHashSet<>();
        private final transient NodeSetView<T> nodeSetView;
        private final transient Function<? super NodeView<T>, ? extends IterableNodeView<T>> fmap;

        private FlatMapNodeSet(NodeSetView<T> nodeSetView,
                               Function<? super NodeView<T>, ? extends IterableNodeView<T>> fmap) {
            this.nodeSetView = nodeSetView;
            this.fmap = fmap;
        }

        @Override
        public Iterator<NodeView<T>> iterator() {
            return new IteratorImpl();
        }

        @Override
        public boolean test(NodeView<T> view) {
            return cache.add(view.getNode());
        }

        @Override
        public Iterator<NodeView<T>> apply(NodeView<T> view) {
            return fmap.apply(view).iterator();
        }

        @SuppressWarnings({"StatementWithEmptyBody", "UnusedVariable"})
        private void writeObject(ObjectOutputStream out) throws IOException {
            for (NodeView<T> ignored : this) { } // eagerly consume this node set to populate cache
            out.writeObject(cache);
        }

        private final class IteratorImpl extends PositionAwareIterator<NodeView<T>> {

            private Iterator<?> current = cache.iterator();
            private boolean swapped;

            @Override
            public boolean hasNext() {
                boolean hasNext = current.hasNext();
                if (!hasNext && !swapped && null != nodeSetView) {
                    current = new FilteringIterator<>(new TransformingAndFlatteningIterator<>(nodeSetView.iterator(),
                            FlatMapNodeSet.this), FlatMapNodeSet.this);
                    swapped = true;
                    hasNext = current.hasNext();
                }
                return hasNext;
            }

            @Override
            @SuppressWarnings("unchecked")
            public NodeView<T> next(int position) {
                return swapped ? ((NodeView<T>) current.next()).copy(position, hasNext())
                        : new NodeView<>((T) current.next(), position, hasNext());
            }

        }

    }

    private abstract static class PositionAwareIterator<T> implements Iterator<T> {

        private int position = 1;

        @Override
        public final T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return next(position++);
        }

        protected abstract T next(int position);

    }

}
