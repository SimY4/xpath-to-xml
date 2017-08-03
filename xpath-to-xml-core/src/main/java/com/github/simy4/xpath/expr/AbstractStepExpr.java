package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import java.util.Iterator;

abstract class AbstractStepExpr extends AbstractExpr implements StepExpr {

    private final Iterable<? extends Predicate> predicates;

    AbstractStepExpr(Iterable<? extends Predicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(ExprContext<N> context) throws XmlBuilderException {
        NodeSetView.Builder<N> builder = NodeSetView.builder();
        while (context.hasNext()) {
            builder.add(resolve(context.getNavigator(), context.next(), context.shouldCreate()));
        }
        return builder.build();
    }

    private <N extends Node> IterableNodeView<N> resolve(Navigator<N> navigator, NodeView<N> node, boolean shouldCreate)
            throws XmlBuilderException {
        IterableNodeView<N> result = traverseStep(navigator, node);
        ExprContext<N> lookupContext = new ExprContext<N>(navigator, false, result);
        Iterator<? extends Predicate> predicateIterator = predicates.iterator();
        if (predicateIterator.hasNext()) {
            result = resolvePredicates(lookupContext, predicateIterator);
        }

        if (!result.toBoolean() && shouldCreate) {
            result = new NodeView<N>(createStepNode(navigator, node));
            predicateIterator = predicates.iterator();
            lookupContext = new ExprContext<N>(navigator, true, result, lookupContext.getPosition());
            if (predicateIterator.hasNext()) {
                result = resolvePredicates(lookupContext, predicateIterator);
            }
        }
        return result;
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param navigator  XML model navigator
     * @param parentView XML node to traverse
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N extends Node> IterableNodeView<N> traverseStep(Navigator<N> navigator, NodeView<N> parentView);

    /**
     * Creates new node of this step type.
     *
     * @param navigator  XML model navigator
     * @param parentView XML node modify
     * @param <N>        XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N extends Node> N createStepNode(Navigator<N> navigator, NodeView<N> parentView)
            throws XmlBuilderException;

    private <N extends Node> IterableNodeView<N> resolvePredicates(ExprContext<N> lookupContext,
                                                                   Iterator<? extends Predicate> predicates)
            throws XmlBuilderException {
        final NodeSetView.Builder<N> builder = NodeSetView.builder();
        final Predicate nextPredicate = predicates.next();
        while (lookupContext.hasNext()) {
            NodeView<N> xmlNode = lookupContext.next();
            if (nextPredicate.match(lookupContext)) {
                builder.add(xmlNode);
            }
        }
        IterableNodeView<N> result = builder.build();
        if (predicates.hasNext()) {
            lookupContext = lookupContext.clone(result);
            result = resolvePredicates(lookupContext, predicates);
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Predicate predicate : predicates) {
            stringBuilder.append('[').append(predicate).append(']');
        }
        return stringBuilder.toString();
    }

}
