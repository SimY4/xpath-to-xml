package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PathExpr implements Expr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N> Set<NodeWrapper<N>> apply(ExprContext<N> context, NodeWrapper<N> xml, boolean greedy) {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        ExprContext<N> stepExprContext = context;
        Set<NodeWrapper<N>> nodes = Collections.singleton(xml);
        while (pathExprIterator.hasNext() && !nodes.isEmpty()) {
            final StepExpr stepExpr = pathExprIterator.next();
            final Set<NodeWrapper<N>> children = new LinkedHashSet<NodeWrapper<N>>();
            for (NodeWrapper<N> node : nodes) {
                children.addAll(stepExpr.apply(context, node, greedy));
            }
            stepExprContext = new ExprContext<N>(stepExprContext.getNavigator(), children.size(), 1);
            nodes = children;
        }
        return nodes;
    }

    @Override
    public String toString() {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        final StringBuilder stringBuilder = new StringBuilder();
        if (pathExprIterator.hasNext()) {
            stringBuilder.append(pathExprIterator.next());
            while (pathExprIterator.hasNext()) {
                stringBuilder.append("/").append(pathExprIterator.next());
            }
        }
        return stringBuilder.toString();
    }

}
