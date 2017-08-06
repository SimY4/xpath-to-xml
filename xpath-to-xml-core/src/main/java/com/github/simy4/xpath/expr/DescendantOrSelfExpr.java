package com.github.simy4.xpath.expr;

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

public class DescendantOrSelfExpr extends AbstractExpr implements StepExpr {

    @Override
    public <N extends Node> IterableNodeView<N> resolve(final ViewContext<N> context) {
        return new NodeSetView<N>(new Iterable<NodeView<N>>() {
            @Nonnull
            @Override
            public Iterator<NodeView<N>> iterator() {
                return descendantOrSelf(context.getNavigator(), context.getCurrent());
            }
        });
    }

    private static <T extends Node> Iterator<NodeView<T>> descendantOrSelf(Navigator<T> navigator, NodeView<T> self) {
        return new FlatteningIterator<NodeView<T>>(self.iterator(),
                new TransformingIterator<T, Iterator<NodeView<T>>>(
                        navigator.elementsOf(self.getNode()).iterator(),
                        new DescendantOrSelf<T>(navigator)));
    }

    @Override
    public String toString() {
        return "";
    }

    private static final class DescendantOrSelf<T extends Node> implements Function<T, Iterator<NodeView<T>>> {

        private final Navigator<T> navigator;

        private DescendantOrSelf(Navigator<T> navigator) {
            this.navigator = navigator;
        }

        @Override
        public Iterator<NodeView<T>> apply(T node) {
            return descendantOrSelf(navigator, new NodeView<T>(node));
        }

    }

}
