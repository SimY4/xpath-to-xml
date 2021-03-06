package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import java.io.Serializable;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.IntFunction;

public class AxisStepExpr implements StepExpr, Serializable {

    private static final long serialVersionUID = 1L;

    private final AxisResolver axisResolver;
    private final Collection<Expr> predicates;

    public AxisStepExpr(AxisResolver axisResolver, Collection<Expr> predicates) {
        this.axisResolver = axisResolver;
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException {
        final var newGreedy = !view.hasNext() && greedy;
        final var result = axisResolver.resolveAxis(navigator, view, newGreedy);
        return resolvePredicates(navigator, view, result, newGreedy);
    }

    private <N extends Node> IterableNodeView<N> resolvePredicates(Navigator<N> navigator, NodeView<N> view,
                                                                   IterableNodeView<N> axis, boolean greedy)
            throws XmlBuilderException {
        var result = axis;
        if (!predicates.isEmpty()) {
            IntFunction<NodeView<N>> nodeSupplier = position -> axisResolver.createAxisNode(navigator, view, position);
            for (var predicate : predicates) {
                final var predicateExpr = new PredicateExpr(predicate);
                final var predicateResolver = new PredicateResolver<>(navigator, nodeSupplier,
                        predicateExpr, greedy);
                result = result.flatMap(predicateResolver);
                nodeSupplier = predicateResolver;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        final var stringJoiner = new StringJoiner("", axisResolver.toString(), "");
        for (var predicate : predicates) {
            stringJoiner.add(predicate.toString());
        }
        return stringJoiner.toString();
    }

    private static final class PredicateResolver<T extends Node> implements IntFunction<NodeView<T>>,
            Function<NodeView<T>, IterableNodeView<T>> {

        private final Navigator<T> navigator;
        private final IntFunction<NodeView<T>> parentNodeSupplier;
        private final Expr predicate;
        private final boolean greedy;
        private boolean resolved;

        private PredicateResolver(Navigator<T> navigator, IntFunction<NodeView<T>> parentNodeSupplier, Expr predicate,
                                  boolean greedy) {
            this.navigator = navigator;
            this.parentNodeSupplier = parentNodeSupplier;
            this.predicate = predicate;
            this.greedy = greedy;
        }

        @Override
        public NodeView<T> apply(int position) throws XmlBuilderException {
            final var newNode = parentNodeSupplier.apply(position);
            if (!predicate.resolve(navigator, newNode, true).toBoolean()) {
                throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
            }
            return newNode;
        }

        @Override
        public IterableNodeView<T> apply(NodeView<T> view) {
            final IterableNodeView<T> result;
            final var check = predicate.resolve(navigator, view, false).toBoolean();
            if (check) {
                result = view;
            } else if ((view.isNew() || view.isMarked()) && greedy) {
                if (!predicate.resolve(navigator, view, true).toBoolean()) {
                    throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
                }
                result = view;
            } else if (!view.hasNext() && !resolved && greedy) {
                result = apply(view.getPosition() + 1);
            } else {
                result = NodeSetView.empty();
            }
            resolved |= check;
            return result;
        }

    }

}
