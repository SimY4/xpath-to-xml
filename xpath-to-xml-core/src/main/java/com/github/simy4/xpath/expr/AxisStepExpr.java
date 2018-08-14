package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

import java.util.Iterator;

public class AxisStepExpr implements StepExpr {

    private final AxisResolver axisResolver;
    private final Iterable<Expr> predicates;

    public AxisStepExpr(AxisResolver axisResolver, Iterable<Expr> predicates) {
        this.axisResolver = axisResolver;
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> result = axisResolver.resolveAxis(context);
        for (Expr predicate : predicates) {
            result = resolvePredicate(context, result, predicate);
        }
        return result;
    }

    private <N extends Node> IterableNodeView<N> resolvePredicate(final ViewContext<N> context,
                                                                  final IterableNodeView<N> filtered,
                                                                  final Expr predicate) throws XmlBuilderException {
        final PredicateContext<N> predicateContext = new PredicateContext<N>();
        IterableNodeView<N> result = new NodeSetView<N>(new Iterable<NodeView<N>>() {
            @Override
            public Iterator<NodeView<N>> iterator() {
                final Iterator<NodeView<N>> iterator = filtered.iterator();
                return new FilteringIterator<NodeView<N>>(iterator,
                        new PredicateResolver<N>(context, predicateContext, iterator, predicate));
            }
        });
        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            final NodeView<N> last = predicateContext.last;
            final int position = predicateContext.position;
            final NodeView<N> newNode;
            final ViewContext<N> newContext;
            if (null != last && last.isNew()) {
                newNode = last;
                newContext = new ViewContext<N>(context.getNavigator(), last, true, false, position);
            } else {
                newNode = axisResolver.createAxisNode(context);
                newContext = new ViewContext<N>(context.getNavigator(), newNode, true, false, position + 1);
            }
            final View<N> resolve = predicate.resolve(newContext);
            if (!resolve.toBoolean()) {
                throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
            }
            result = newNode;
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(axisResolver.toString());
        for (Expr predicate : predicates) {
            stringBuilder.append(predicate);
        }
        return stringBuilder.toString();
    }

    private static final class PredicateResolver<T extends Node> implements Predicate<NodeView<T>> {

        private final ViewContext<T> parentContext;
        private final PredicateContext<T> predicateContext;
        private final Iterator<NodeView<T>> iterator;
        private final Expr predicate;
        private int position = 1;

        private PredicateResolver(ViewContext<T> parentContext, PredicateContext<T> predicateContext,
                                  Iterator<NodeView<T>> iterator, Expr predicate) {
            this.parentContext = parentContext;
            this.predicateContext = predicateContext;
            this.iterator = iterator;
            this.predicate = predicate;
        }

        @Override
        public boolean test(NodeView<T> view) {
            if (null == predicateContext.last || !predicateContext.last.isNew()) {
                predicateContext.last = view;
                predicateContext.position = position;
            }
            final ViewContext<T> context = new ViewContext<T>(parentContext.getNavigator(), view, false,
                    iterator.hasNext(), position++);
            return predicate.resolve(context).toBoolean();
        }

    }

    private static final class PredicateContext<T extends Node> {

        private NodeView<T> last;
        private int position;

    }

}
