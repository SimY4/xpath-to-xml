package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

public class AxisStepExpr implements StepExpr {

    private final AxisResolver axisResolver;
    private final Iterable<Expr> predicates;

    public AxisStepExpr(AxisResolver axisResolver, Iterable<Expr> predicates) {
        this.axisResolver = axisResolver;
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException {
        IterableNodeView<N> result = axisResolver.resolveAxis(navigator, view, greedy);
        for (Expr predicate : predicates) {
            result = result.flatMap(new PredicateResolver<N>(navigator, axisResolver, predicate, greedy));
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

    private static final class PredicateResolver<T extends Node> implements Function<NodeView<T>, IterableNodeView<T>> {

        private final Navigator<T> navigator;
        private final AxisResolver axisResolver;
        private final Expr predicate;
        private final boolean greedy;

        private PredicateResolver(Navigator<T> navigator, AxisResolver axisResolver, Expr predicate, boolean greedy) {
            this.navigator = navigator;
            this.axisResolver = axisResolver;
            this.predicate = predicate;
            this.greedy = greedy;
        }

        @Override
        public IterableNodeView<T> apply(NodeView<T> view) {
            final IterableNodeView<T> result;
            final boolean check = predicate.resolve(navigator, view, false).toBoolean();
            if (check) {
                result = view;
            } else if (!view.hasNext() && greedy) {
                final NodeView<T> newNode = view.isNew() ? view
                        : axisResolver.createAxisNode(navigator, view, view.getPosition() + 1);
                final View<T> resolve = predicate.resolve(navigator, newNode, true);
                if (!resolve.toBoolean()) {
                    throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
                }
                result = newNode;
            } else {
                result = NodeSetView.empty();
            }
            return result;
        }

    }

}
