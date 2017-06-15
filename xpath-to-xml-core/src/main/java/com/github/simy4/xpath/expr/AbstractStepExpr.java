package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

import java.util.Iterator;
import java.util.List;

abstract class AbstractStepExpr implements StepExpr {

    private final List<Expr> predicateList;

    AbstractStepExpr(List<Expr> predicateList) {
        this.predicateList = predicateList;
    }

    @Override
    public final <N> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException {
        return xml.visit(new StepNodeVisitor<>(context));
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context    XPath expression context
     * @param parentNode XML node to traverse
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> parentNode);

    /**
     * Creates new node of this step type.
     *
     * @param context XML model navigator
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N> NodeView<N> createStepNode(ExprContext<N> context) throws XmlBuilderException;

    private <N> NodeSetView<N> resolvePredicates(ExprContext<N> lookupContext, NodeSetView<N> xmlNodes,
                                                 Iterator<Expr> predicateIterator) throws XmlBuilderException {
        if (predicateIterator.hasNext() && xmlNodes.toBoolean()) {
            final NodeSetView.Builder<N> builder = NodeSetView.builder(xmlNodes.size());
            final Expr nextPredicate = predicateIterator.next();
            for (View<N> xmlNode : xmlNodes) {
                lookupContext.advance();
                if (nextPredicate.resolve(lookupContext, xmlNode).toBoolean()) {
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
        for (Expr predicate : predicateList) {
            stringBuilder.append('[').append(predicate).append(']');
        }
        return stringBuilder.toString();
    }

    private final class StepNodeVisitor<N> extends AbstractViewVisitor<N, NodeSetView<N>> {

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
            Iterator<Expr> predicateIterator = predicateList.iterator();
            result = resolvePredicates(lookupContext, result, predicateIterator);

            if (!result.toBoolean() && context.shouldCreate()) {
                final NodeView<N> newNode = createStepNode(context);
                context.getNavigator().append(node.getNode(), newNode.getNode());
                result = NodeSetView.singleton(newNode);
                predicateIterator = predicateList.iterator();
                lookupContext = lookupContext.clone(true, lookupContext.getSize() + 1, lookupContext.getSize());
                resolvePredicates(lookupContext, result, predicateIterator);
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
