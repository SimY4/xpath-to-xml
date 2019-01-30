package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.ReadOnlyIterator;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;
import com.github.simy4.xpath.util.TransformingIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class NodeSetView<N extends Node> implements IterableNodeView<N> {

    private static final NodeSetView<?> EMPTY_NODE_SET = new EmptyNodeSet<Node>();

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeSetView<T> empty() {
        return (NodeSetView<T>) EMPTY_NODE_SET;
    }

    public static <T extends Node> NodeSetView<T> of(Iterable<? extends T> iterable, Predicate<? super T> filter) {
        return new IterableNodeSet<T>(iterable, filter);
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
        return new FlatMapNodeSet<N>(this, fmap);
    }

    private static final class EmptyNodeSet<T extends Node> extends NodeSetView<T> {

        @Override
        public Iterator<NodeView<T>> iterator() {
            return Collections.<NodeView<T>>emptyList().iterator();
        }

    }

    private static final class IterableNodeSet<T extends Node> extends NodeSetView<T> implements Predicate<T> {

        private final Set<T> cache = new LinkedHashSet<T>();
        private final Iterable<? extends T> nodeSet;
        private final Predicate<? super T> filter;

        private IterableNodeSet(Iterable<? extends T> nodeSet, Predicate<? super T> filter) {
            this.nodeSet = nodeSet;
            this.filter = filter;
        }

        @Override
        public Iterator<NodeView<T>> iterator() {
            final Iterator<? extends T> nodeIterator = new OneAndIterator<T>(cache.iterator(), new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return new FilteringIterator<T>(nodeSet.iterator(), IterableNodeSet.this);
                }
            });
            return new TransformingIterator<T, NodeView<T>>(nodeIterator, new NodeWrapper<T>(nodeIterator));
        }

        @Override
        public boolean test(T view) {
            return cache.add(view) && filter.test(view);
        }

        private static final class NodeWrapper<T extends Node> implements Function<T, NodeView<T>> {

            private final Iterator<? extends T> iterator;
            private int position = 1;

            private NodeWrapper(Iterator<? extends T> iterator) {
                this.iterator = iterator;
            }

            @Override
            public NodeView<T> apply(T t) {
                return new NodeView<T>(t, position++, iterator.hasNext());
            }

        }

    }

    private static final class FlatMapNodeSet<T extends Node> extends NodeSetView<T>
            implements Function<NodeView<T>, Iterator<NodeView<T>>>, Predicate<NodeView<T>> {

        private final Set<NodeView<T>> cache = new LinkedHashSet<NodeView<T>>();
        private final NodeSetView<T> nodeSetView;
        private final Function<? super NodeView<T>, ? extends IterableNodeView<T>> fmap;

        private FlatMapNodeSet(NodeSetView<T> nodeSetView,
                               Function<? super NodeView<T>, ? extends IterableNodeView<T>> fmap) {
            this.nodeSetView = nodeSetView;
            this.fmap = fmap;
        }

        @Override
        public Iterator<NodeView<T>> iterator() {
            final Iterator<NodeView<T>> nodeIterator = new OneAndIterator<NodeView<T>>(cache.iterator(),
                    new Iterable<NodeView<T>>() {
                        @Override
                        public Iterator<NodeView<T>> iterator() {
                            return new FilteringIterator<NodeView<T>>(
                                    new TransformingAndFlatteningIterator<NodeView<T>, NodeView<T>>(
                                            nodeSetView.iterator(), FlatMapNodeSet.this), FlatMapNodeSet.this);
                        }
                    });
            return new TransformingIterator<NodeView<T>, NodeView<T>>(nodeIterator, new NodeWrapper<T>(nodeIterator));
        }

        @Override
        public Iterator<NodeView<T>> apply(NodeView<T> node) {
            return fmap.apply(node).iterator();
        }

        @Override
        public boolean test(NodeView<T> view) {
            return cache.add(view);
        }

        private static final class NodeWrapper<T extends Node> implements Function<NodeView<T>, NodeView<T>> {

            private final Iterator<NodeView<T>> iterator;
            private int position = 1;

            private NodeWrapper(Iterator<NodeView<T>> iterator) {
                this.iterator = iterator;
            }

            @Override
            public NodeView<T> apply(NodeView<T> view) {
                return new NodeView<T>(view.getNode(), position++, iterator.hasNext());
            }

        }

    }

    private static final class OneAndIterator<T> extends ReadOnlyIterator<T> {

        private Iterator<T> current;
        private Iterable<T> and;

        private OneAndIterator(Iterator<T> one, Iterable<T> and) {
            this.current = one;
            this.and = and;
        }

        @Override
        public boolean hasNext() {
            if (!current.hasNext() && and != null) {
                current = and.iterator();
                and = null;
            }
            return current.hasNext();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return current.next();
        }

    }

}
