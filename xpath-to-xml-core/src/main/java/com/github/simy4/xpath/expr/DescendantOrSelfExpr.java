package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FlatteningIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingIterator;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class DescendantOrSelfExpr extends AbstractExpr {

    @Override
    public <N extends Node> IterableNodeView<N> resolve(final ViewContext<N> context) throws XmlBuilderException {
        return new NodeSetView<N>(new Iterable<NodeView<N>>() {
            @Nonnull
            @Override
            public Iterator<NodeView<N>> iterator() {
                return new DescendantOrSelfIterator<N>(context.getNavigator(), context.getCurrent());
            }
        });
    }

    private static final class DescendantOrSelfIterator<T extends Node> implements Iterator<NodeView<T>> {

        private final Iterator<NodeView<T>> descendantOfSelf;

        private DescendantOrSelfIterator(Navigator<T> navigator, NodeView<T> self) {
            this.descendantOfSelf = new FlatteningIterator<NodeView<T>>(self.iterator(),
                    new TransformingIterator<T, Iterator<NodeView<T>>>(
                            navigator.elementsOf(self.getNode()).iterator(),
                            new DescendantOrSelf<T>(navigator)));
        }

        @Override
        public boolean hasNext() {
            return descendantOfSelf.hasNext();
        }

        @Override
        public NodeView<T> next() {
            return descendantOfSelf.next();
        }

        @Override
        public void remove() {
            descendantOfSelf.remove();
        }

    }

    private static final class DescendantOrSelf<T extends Node> implements Function<T, Iterator<NodeView<T>>> {

        private final Navigator<T> navigator;

        private DescendantOrSelf(Navigator<T> navigator) {
            this.navigator = navigator;
        }

        @Override
        public Iterator<NodeView<T>> apply(T node) {
            return new DescendantOrSelfIterator<T>(navigator, new NodeView<T>(node));
        }

    }

}
