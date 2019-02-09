package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import java.util.Collection;

public class AxisStepExpr implements StepExpr {

    private final AxisResolver axisResolver;
    private final Collection<Expr> predicates;

    public AxisStepExpr(AxisResolver axisResolver, Collection<Expr> predicates) {
        this.axisResolver = axisResolver;
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException {
        final boolean newGreedy = !view.hasNext() && greedy;
        IterableNodeView<N> result = axisResolver.resolveAxis(navigator, view, newGreedy);
        for (Expr predicate : predicates) {
            result = result.flatMap(new PredicateResolver<N>(navigator, axisResolver, predicate, view, newGreedy));
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
        private final NodeView<T> parent;
        private final boolean greedy;
        private boolean resolved;

        private PredicateResolver(Navigator<T> navigator, AxisResolver axisResolver, Expr predicate, NodeView<T> parent,
                                  boolean greedy) {
            this.navigator = navigator;
            this.axisResolver = axisResolver;
            this.predicate = new PredicateExpr(predicate);
            this.parent = parent;
            this.greedy = greedy;
        }

        @Override
        public IterableNodeView<T> apply(NodeView<T> view) {
            final IterableNodeView<T> result;
            final boolean check = predicate.resolve(navigator, view, false).toBoolean();
            if (check) {
                result = view;
            } else if ((view.isNew() || view.isMarked()) && greedy) {
                if (!predicate.resolve(navigator, view, true).toBoolean()) {
                    throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
                }
                result = view;
            } else if (!view.hasNext() && !resolved && greedy) {
                final NodeView<T> newNode = axisResolver.createAxisNode(navigator, parent, view.getPosition() + 1);
                if (!predicate.resolve(navigator, newNode, true).toBoolean()) {
                    throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
                }
                result = newNode;
            } else {
                result = NodeSetView.empty();
            }
            resolved |= check;
            return result;
        }

    }

}
