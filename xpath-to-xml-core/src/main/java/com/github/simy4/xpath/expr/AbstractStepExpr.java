package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractStepExpr implements StepExpr {

    private final List<Expr> predicateList;

    AbstractStepExpr(List<Expr> predicateList) {
        this.predicateList = predicateList;
    }

    @Override
    public final <N> Set<NodeWrapper<N>> apply(ExprContext<N> context, NodeWrapper<N> xml, boolean greedy)
            throws XmlBuilderException {
        final ExprContext<N> stepExprContext = new ExprContext<N>(context.getNavigator());
        Set<NodeWrapper<N>> children = traverse(context, stepExprContext, xml);
        if (children.isEmpty() && context.isLast() && greedy) {
            final NodeWrapper<N> newNode = createNode(context, stepExprContext);
            context.getNavigator().append(xml, newNode);
            children = Collections.singleton(newNode);
        }
        return children;
    }

    abstract <N> Set<NodeWrapper<N>> traverseStep(ExprContext<N> context, NodeWrapper<N> parentNode);

    abstract <N> NodeWrapper<N> createStepNode(ExprContext<N> context) throws XmlBuilderException;

    private <N> Set<NodeWrapper<N>> traverse(ExprContext<N> pathContext, ExprContext<N> stepContext,
                                             NodeWrapper<N> parentNode) {
        final Set<NodeWrapper<N>> nodes = traverseStep(pathContext, parentNode);
        final Set<NodeWrapper<N>> result = new LinkedHashSet<NodeWrapper<N>>(nodes.size());
        stepContext.setSize(nodes.size());
        for (NodeWrapper<N> node : nodes) {
            stepContext.advance();
            final Iterator<Expr> predicatesIterator = predicateList.iterator();
            if (test(stepContext, node, predicatesIterator)) {
                result.add(node);
            }
        }
        return result;
    }

    private <N> boolean test(ExprContext<N> stepContext, NodeWrapper<N> node, Iterator<Expr> predicateIterator) {
        if (predicateIterator.hasNext()) {
            final Expr predicate = predicateIterator.next();
            final Set<NodeWrapper<N>> children = predicate.apply(stepContext, node, false);
            return !children.isEmpty() && test(stepContext, node, predicateIterator);
        } else {
            return true;
        }
    }

    private <N> NodeWrapper<N> createNode(ExprContext<N> pathContext, ExprContext<N> stepContext)
            throws XmlBuilderException {
        final NodeWrapper<N> newNode = createStepNode(pathContext);
        for (Expr predicate : predicateList) {
            predicate.apply(stepContext, newNode, true);
        }
        return newNode;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("[");
        final Iterator<Expr> predicatesIterator = predicateList.iterator();
        if (predicatesIterator.hasNext()) {
            stringBuilder.append(predicatesIterator.next());
            while (predicatesIterator.hasNext()) {
                stringBuilder.append("][").append(predicatesIterator.next());
            }
        }
        return stringBuilder.append(']').toString();
    }

}
