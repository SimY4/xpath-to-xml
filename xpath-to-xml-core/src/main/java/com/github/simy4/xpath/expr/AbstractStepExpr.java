package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

import java.util.Iterator;

abstract class AbstractStepExpr extends AbstractExpr implements StepExpr {

    private final Iterable<? extends Predicate> predicates;

    AbstractStepExpr(Iterable<? extends Predicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(ExprContext<N> context, View<N> xml)
            throws XmlBuilderException {
        return xml.visit(new StepNodeVisitor<>(context));
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context    XPath expression context
     * @param parentView XML node to traverse
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N extends Node> IterableNodeView<N> traverseStep(ExprContext<N> context, NodeView<N> parentView);

    /**
     * Creates new node of this step type.
     *
     * @param context    XML model navigator
     * @param parentView XML node modify
     * @param <N>        XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N extends Node> N createStepNode(ExprContext<N> context, NodeView<N> parentView)
            throws XmlBuilderException;

    private <N extends Node> IterableNodeView<N> resolvePredicates(ExprContext<N> lookupContext,
                                                                   IterableNodeView<N> nodes,
                                                                   Iterator<? extends Predicate> predicates)
            throws XmlBuilderException {
        if (predicates.hasNext() && nodes.toBoolean()) {
            final NodeSetView.Builder<N> builder = NodeSetView.builder(nodes.size());
            final Predicate nextPredicate = predicates.next();
            for (NodeView<N> xmlNode : nodes) {
                lookupContext.advance();
                if (nextPredicate.match(lookupContext, xmlNode)) {
                    builder.add(xmlNode);
                }
            }
            final NodeSetView<N> result = builder.build();
            lookupContext = lookupContext.clone(result.size());
            return resolvePredicates(lookupContext, result, predicates);
        } else {
            return nodes;
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Predicate predicate : predicates) {
            stringBuilder.append('[').append(predicate).append(']');
        }
        return stringBuilder.toString();
    }

    private final class StepNodeVisitor<N extends Node> extends AbstractViewVisitor<N, IterableNodeView<N>> {

        private final ExprContext<N> context;

        private StepNodeVisitor(ExprContext<N> context) {
            this.context = context;
        }

        @Override
        public IterableNodeView<N> visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            final NodeSetView.Builder<N> builder = NodeSetView.builder();
            for (NodeView<N> node : nodeSet) {
                builder.add(visit(node));
            }
            return builder.build();
        }

        private IterableNodeView<N> visit(NodeView<N> node) throws XmlBuilderException {
            context.advance();
            IterableNodeView<N> result = traverseStep(context, node);
            ExprContext<N> lookupContext = context.clone(false, result.size());
            Iterator<? extends Predicate> predicateIterator = predicates.iterator();
            result = resolvePredicates(lookupContext, result, predicateIterator);

            if (!result.toBoolean() && context.shouldCreate()) {
                final N newNode = createStepNode(context, node);
                predicateIterator = predicates.iterator();
                lookupContext = lookupContext.clone(true, lookupContext.getSize() + 1, lookupContext.getSize());
                result = resolvePredicates(lookupContext, new NodeView<>(newNode), predicateIterator);
            }
            return result;
        }

        @Override
        protected IterableNodeView<N> returnDefault(View<N> view) throws XmlBuilderException {
            context.advance();
            if (context.shouldCreate()) {
                throw new XmlBuilderException("Can not modify read-only node: " + view);
            }
            return NodeSetView.empty();
        }

    }

}
