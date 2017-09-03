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

import java.util.Iterator;

public class DescendantOrSelfExpr implements StepExpr {

    @Override
    public <N extends Node> IterableNodeView<N> resolve(final ViewContext<N> context) {
        return new NodeSetView<>(new DescendantOrSelfIterable<>(context));
    }

    @Override
    public boolean test(ViewContext<?> viewContext) {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }

    private static final class DescendantOrSelfIterable<N extends Node> implements Iterable<NodeView<N>> {

        private final ViewContext<N> context;

        private DescendantOrSelfIterable(ViewContext<N> context) {
            this.context = context;
        }

        @Override
        public Iterator<NodeView<N>> iterator() {
            return new DescendantOrSelf<>(context.getNavigator()).apply(context.getCurrent());
        }

    }

    private static final class DescendantOrSelf<T extends Node> implements Function<T, Iterator<NodeView<T>>> {

        private final Navigator<T> navigator;

        private DescendantOrSelf(Navigator<T> navigator) {
            this.navigator = navigator;
        }

        @Override
        public Iterator<NodeView<T>> apply(T node) {
            return apply(new NodeView<>(node));
        }

        private Iterator<NodeView<T>> apply(NodeView<T> self) {
            return new FlatteningIterator<>(self.iterator(),
                    new TransformingIterator<T, Iterator<NodeView<T>>>(navigator.elementsOf(self.getNode()).iterator(),
                            new DescendantOrSelf<>(navigator)));
        }

    }

}
