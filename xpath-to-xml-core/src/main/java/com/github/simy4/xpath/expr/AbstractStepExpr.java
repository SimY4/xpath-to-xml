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
        Set<NodeWrapper<N>> children = traverse(stepExprContext, xml);
        if (children.isEmpty() && context.isLast() && greedy) {
            final NodeWrapper<N> newNode = createNode(stepExprContext);
            context.getNavigator().append(xml, newNode);
            children = Collections.singleton(newNode);
        }
        return children;
    }

    @Override
    public final <N> Set<NodeWrapper<N>> traverse(ExprContext<N> context, NodeWrapper<N> parentNode) {
        final Set<NodeWrapper<N>> nodes = traverseStep(context, parentNode);
        final Set<NodeWrapper<N>> result = new LinkedHashSet<NodeWrapper<N>>(nodes.size());
        context.setSize(nodes.size());
        for (NodeWrapper<N> node : nodes) {
            context.advance();
            final Iterator<Expr> predicatesIterator = predicateList.iterator();
            if (test(context, node, predicatesIterator)) {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public final <N> NodeWrapper<N> createNode(ExprContext<N> context) throws XmlBuilderException {
        final NodeWrapper<N> newNode = createStepNode(context);
        for (Expr predicate : predicateList) {
            predicate.apply(context, newNode, true);
        }
        return newNode;
    }

    abstract <N> Set<NodeWrapper<N>> traverseStep(ExprContext<N> context, NodeWrapper<N> parentNode);

    abstract <N> NodeWrapper<N> createStepNode(ExprContext<N> context) throws XmlBuilderException;

    private <N> boolean test(ExprContext<N> context, NodeWrapper<N> node, Iterator<Expr> predicateIterator) {
        if (predicateIterator.hasNext()) {
            final Expr predicate = predicateIterator.next();
            final Set<NodeWrapper<N>> children = predicate.apply(context, node, false);
            return !children.isEmpty() && test(context, node, predicateIterator);
        } else {
            return true;
        }
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
