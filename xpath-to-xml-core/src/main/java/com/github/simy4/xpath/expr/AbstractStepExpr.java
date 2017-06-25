package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

import java.util.Iterator;
import java.util.List;

abstract class AbstractStepExpr extends AbstractExpr implements StepExpr {

    private final List<Predicate> predicateList;

    AbstractStepExpr(List<Predicate> predicateList) {
        this.predicateList = predicateList;
    }

    @Override
    public final <N extends Node> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml)
            throws XmlBuilderException {
        return xml.visit(new StepNodeVisitor<N>(context));
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context    XPath expression context
     * @param parentView XML node to traverse
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N extends Node> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> parentView);

    /**
     * Creates new node of this step type.
     *
     * @param context    XML model navigator
     * @param parentView XML node modify
     * @param <N>        XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N extends Node> NodeView<N> createStepNode(ExprContext<N> context, NodeView<N> parentView)
            throws XmlBuilderException;

    private <N extends Node> NodeSetView<N> resolvePredicates(ExprContext<N> lookupContext, NodeSetView<N> xmlNodes,
                                                 Iterator<Predicate> predicateIterator) throws XmlBuilderException {
        if (predicateIterator.hasNext() && xmlNodes.toBoolean()) {
            final NodeSetView.Builder<N> builder = NodeSetView.builder(xmlNodes.size());
            final Predicate nextPredicate = predicateIterator.next();
            for (View<N> xmlNode : xmlNodes) {
                lookupContext.advance();
                if (nextPredicate.match(lookupContext, xmlNode)) {
                    builder.add(xmlNode);
                }
            }
            final NodeSetView<N> result = builder.build();
            lookupContext = lookupContext.clone(result.size());
            return resolvePredicates(lookupContext, result, predicateIterator);
        } else {
            return xmlNodes;
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Predicate predicate : predicateList) {
            stringBuilder.append('[').append(predicate).append(']');
        }
        return stringBuilder.toString();
    }

    private final class StepNodeVisitor<N extends Node> extends AbstractViewVisitor<N, NodeSetView<N>> {

        private final ExprContext<N> context;

        private StepNodeVisitor(ExprContext<N> context) {
            this.context = context;
        }

        @Override
        public NodeSetView<N> visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            final NodeSetView.Builder<N> builder = NodeSetView.builder();
            for (View<N> node : nodeSet) {
                builder.add(node.visit(this));
            }
            return builder.build();
        }

        @Override
        public NodeSetView<N> visit(NodeView<N> node) throws XmlBuilderException {
            context.advance();
            NodeSetView<N> result = traverseStep(context, node);
            ExprContext<N> lookupContext = context.clone(false, result.size());
            Iterator<Predicate> predicateIterator = predicateList.iterator();
            result = resolvePredicates(lookupContext, result, predicateIterator);

            if (!result.toBoolean() && context.shouldCreate()) {
                final NodeView<N> newNode = createStepNode(context, node);
                predicateIterator = predicateList.iterator();
                lookupContext = lookupContext.clone(true, lookupContext.getSize() + 1, lookupContext.getSize());
                result = resolvePredicates(lookupContext, NodeSetView.singleton(newNode), predicateIterator);
            }
            return result;
        }

        @Override
        protected NodeSetView<N> returnDefault(View<N> view) throws XmlBuilderException {
            context.advance();
            if (context.shouldCreate()) {
                throw new XmlBuilderException("Can not modify read-only node: " + view);
            }
            return NodeSetView.empty();
        }

    }

}
