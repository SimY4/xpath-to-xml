package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

public class PathExpr implements Expr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N extends Node> IterableNodeView<N> resolve(final ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> children = context.getCurrent();
        for (final var stepExpr : pathExpr) {
            final var currentChildren = children;
            children = new NodeSetView<>(() -> {
                final var iterator = currentChildren.iterator();
                return new TransformingAndFlatteningIterator<>(iterator,
                        new StepResolver<>(context, iterator, stepExpr));
            });
        }
        return children;
    }

    @Override
    public String toString() {
        final var stringJoiner = new StringJoiner("/");
        for (var stepExpr : pathExpr) {
            stringJoiner.add(stepExpr.toString());
        }
        return stringJoiner.toString();
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
            final var context = new ViewContext<T>(parentContext.getNavigator(), view,
                    parentContext.isGreedy(), iterator.hasNext(), position++);
            return stepExpr.resolve(context).iterator();
        }

    }

}
