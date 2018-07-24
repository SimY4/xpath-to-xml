package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.TransformingIterator;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

import java.util.Iterator;
import java.util.StringJoiner;

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
        final PredicateContext<N> predicateContext = new PredicateContext<>();
        IterableNodeView<N> result = new NodeSetView<>(() -> {
            final Iterator<NodeView<N>> iterator = filtered.iterator();
            return new FilteringIterator<>(new TransformingIterator<>(iterator, predicateContext),
                    new PredicateResolver<>(context, iterator, predicate));
        });
        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            final NodeView<N> last = predicateContext.last;
            final int position = predicateContext.position;
            final NodeView<N> newNode;
            final ViewContext<N> newContext;
            if (null != last && last.isNew()) {
                newNode = last;
                newContext = new ViewContext<>(context.getNavigator(), last, true, false, position);
            } else {
                newNode = axisResolver.createAxisNode(context);
                newContext = new ViewContext<>(context.getNavigator(), newNode, true, false, position + 1);
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
        final StringJoiner stringJoiner = new StringJoiner("", axisResolver.toString(), "");
        for (Expr predicate : predicates) {
            stringJoiner.add(predicate.toString());
        }
        return stringJoiner.toString();
    }

    private static final class PredicateResolver<T extends Node> implements Predicate<NodeView<T>> {

        private final ViewContext<T> parentContext;
        private final Iterator<NodeView<T>> iterator;
        private final Expr predicate;
        private int position = 1;

        private PredicateResolver(ViewContext<T> parentContext, Iterator<NodeView<T>> iterator, Expr predicate) {
            this.parentContext = parentContext;
            this.iterator = iterator;
            this.predicate = predicate;
        }

        @Override
        public boolean test(NodeView<T> view) {
            final ViewContext<T> context = new ViewContext<>(parentContext.getNavigator(), view, false,
                    iterator.hasNext(), position++);
            return predicate.resolve(context).toBoolean();
        }

    }

    private static final class PredicateContext<T extends Node> implements Function<NodeView<T>, NodeView<T>> {

        private NodeView<T> last;
        private int position;

        @Override
        public NodeView<T> apply(NodeView<T> view) {
            if (null == last || !last.isNew()) {
                last = view;
                position++;
            }
            return view;
        }

    }

}
