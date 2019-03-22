package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.ReadOnlyIterator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class NodeSetView<N extends Node> implements IterableNodeView<N>, Serializable {

    private static final long serialVersionUID = 1L;
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

        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<NodeView<T>> iterator() {
            return Collections.<NodeView<T>>emptyList().iterator();
        }

    }

    private static final class IterableNodeSet<T extends Node> extends NodeSetView<T> {

        private static final long serialVersionUID = 1L;

        private final Set<T> cache = new LinkedHashSet<T>();
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
                    current = new NodeSetIterator();
                    swapped = true;
                    hasNext = current.hasNext();
                }
                return hasNext;
            }

            @Override
            public NodeView<T> next(int position) {
                return new NodeView<T>(current.next(), position, hasNext());
            }

        }

        private final class NodeSetIterator implements Iterator<T> {

            private final Iterator<? extends T> iterator = nodeSet.iterator();
            private T nextElement;
            private boolean hasNext;

            private NodeSetIterator() {
                nextMatch();
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                return nextMatch();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            private T nextMatch() {
                final T oldMatch = nextElement;
                while (iterator.hasNext()) {
                    final T next = iterator.next();
                    if (filter.test(next) && cache.add(next)) {
                        hasNext = true;
                        nextElement = next;
                        return oldMatch;
                    }
                }
                hasNext = false;
                return oldMatch;
            }

        }

    }

    private static final class FlatMapNodeSet<T extends Node> extends NodeSetView<T> {

        private static final long serialVersionUID = 1L;

        private final Set<T> cache = new LinkedHashSet<T>();
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
                    current = new FlatMapIterator();
                    swapped = true;
                    hasNext = current.hasNext();
                }
                return hasNext;
            }

            @Override
            @SuppressWarnings("unchecked")
            public NodeView<T> next(int position) {
                return swapped ? ((NodeView<T>) current.next()).copy(position, hasNext())
                        : new NodeView<T>((T) current.next(), position, hasNext());
            }

        }

        private final class FlatMapIterator extends ReadOnlyIterator<NodeView<T>> {

            private final Iterator<? extends NodeView<T>> nodeSetIterator = nodeSetView.iterator();
            private Iterator<? extends NodeView<T>> current = Collections.<NodeView<T>>emptyList().iterator();
            private NodeView<T> nextElement;
            private boolean hasNext;

            private FlatMapIterator() {
                nextMatch();
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public NodeView<T> next() {
                return nextMatch();
            }

            private NodeView<T> nextMatch() {
                final NodeView<T> oldMatch = nextElement;
                while (tryAdvance()) {
                    final NodeView<T> next = current.next();
                    if (cache.add(next.getNode())) {
                        hasNext = true;
                        nextElement = next;
                        return oldMatch;
                    }
                }
                hasNext = false;
                return oldMatch;
            }

            private boolean tryAdvance() {
                boolean currentHasNext;
                while (!(currentHasNext = current.hasNext()) && nodeSetIterator.hasNext()) {
                    current = fmap.apply(nodeSetIterator.next()).iterator();
                }
                return currentHasNext;
            }

        }

    }

    private abstract static class PositionAwareIterator<T> extends ReadOnlyIterator<T> {

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
