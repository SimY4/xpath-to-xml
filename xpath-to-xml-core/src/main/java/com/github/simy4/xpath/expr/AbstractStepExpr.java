package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractStepExpr implements StepExpr {

    private final List<Expr> predicateList;

    AbstractStepExpr(List<Expr> predicateList) {
        this.predicateList = predicateList;
    }

    @Override
    public final <N> Set<NodeWrapper<N>> resolve(ExprContext<N> context, NodeWrapper<N> xml)
            throws XmlBuilderException {
        context.advance();
        final Set<NodeWrapper<N>> stepNodes = traverseStep(context, xml);
        ExprContext<N> lookupContext = context.clone(false, stepNodes.size());
        Set<NodeWrapper<N>> result = traverse(lookupContext, stepNodes);

        if (result.isEmpty() && context.shouldCreate()) {
            final NodeWrapper<N> newNode = createStepNode(context);
            lookupContext = context.clone(1);
            lookupContext.advance();
            createNode(lookupContext, newNode);
            result = Collections.singleton(newNode);
            context.getNavigator().append(xml, newNode);
        }
        return result;
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context    XPath expression context
     * @param parentNode XML node to traverse
     * @param <N>        XML node type
     * @return list of matching nodes
     */
    abstract <N> Set<NodeWrapper<N>> traverseStep(ExprContext<N> context, NodeWrapper<N> parentNode);

    /**
     * Creates new node of this step type.
     *
     * @param context XML model navigator
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N> NodeWrapper<N> createStepNode(ExprContext<N> context) throws XmlBuilderException;

    private <N> Set<NodeWrapper<N>> traverse(ExprContext<N> lookupContext, Set<NodeWrapper<N>> stepNodes) {
        final Set<NodeWrapper<N>> result = new LinkedHashSet<NodeWrapper<N>>(stepNodes.size());
        for (NodeWrapper<N> child : stepNodes) {
            lookupContext.advance();
            if (test(lookupContext, child)) {
                result.add(child);
            }
        }
        return result;
    }

    private <N> boolean test(ExprContext<N> stepContext, NodeWrapper<N> xml) {
        for (Expr predicate : predicateList) {
            Set<NodeWrapper<N>> result = predicate.resolve(stepContext, xml);
            if (result.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private <N> void createNode(ExprContext<N> stepContext, NodeWrapper<N> newNode) throws XmlBuilderException {
        for (Expr predicate : predicateList) {
            predicate.resolve(stepContext, newNode);
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

}
