package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.FlatteningIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.TransformingIterator;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PathExpr implements Expr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N extends Node> View<N> resolve(final ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> children = context.getCurrent();
        for (final StepExpr stepExpr : pathExpr) {
            final IterableNodeView<N> currentChildren = children;
            children = new NodeSetView<N>(new Iterable<NodeView<N>>() {
                @Override
                public Iterator<NodeView<N>> iterator() {
                    final Iterator<NodeView<N>> iterator = currentChildren.iterator();
                    return new FilteringIterator<NodeView<N>>(new FlatteningIterator<NodeView<N>>(
                            new TransformingIterator<NodeView<N>, Iterator<NodeView<N>>>(iterator,
                                    new StepResolver<N>(context, iterator, stepExpr))), new Distinct<N>());
                }
            });
        }
        return children;
    }

    @Override
    public String toString() {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        final StringBuilder stringBuilder = new StringBuilder();
        if (pathExprIterator.hasNext()) {
            stringBuilder.append(pathExprIterator.next());
            while (pathExprIterator.hasNext()) {
                stringBuilder.append('/').append(pathExprIterator.next());
            }
        }
        return stringBuilder.toString();
    }

    private static final class StepResolver<T extends Node> implements Function<NodeView<T>, Iterator<NodeView<T>>> {

        private final ViewContext<T> parentContext;
        private final Iterator<NodeView<T>> iterator;
        private final StepExpr stepExpr;
        private int position = 1;

        private StepResolver(ViewContext<T> parentContext, Iterator<NodeView<T>> iterator, StepExpr stepExpr) {
            this.parentContext = parentContext;
            this.iterator = iterator;
            this.stepExpr = stepExpr;
        }

        @Override
        public Iterator<NodeView<T>> apply(NodeView<T> view) {
            final ViewContext<T> context = new ViewContext<T>(parentContext.getNavigator(),
                    view, parentContext.isGreedy(), iterator.hasNext(), position++);
            return stepExpr.resolve(context).iterator();
        }

    }

    private static final class Distinct<T extends Node> implements Predicate<NodeView<T>> {

        private final Set<T> visited = new HashSet<T>();

        @Override
        public boolean test(NodeView<T> node) {
            return visited.add(node.getNode());
        }

    }

}
