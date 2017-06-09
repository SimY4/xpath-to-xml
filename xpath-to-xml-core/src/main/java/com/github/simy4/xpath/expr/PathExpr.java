package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PathExpr extends AbstractExpr implements Expr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N> Set<NodeWrapper<N>> resolve(ExprContext<N> context, NodeWrapper<N> xml) {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        Set<NodeWrapper<N>> nodes = Collections.singleton(xml);
        ExprContext<N> stepExprContext = context;
        while (pathExprIterator.hasNext() && !nodes.isEmpty()) {
            final StepExpr stepExpr = pathExprIterator.next();
            final Set<NodeWrapper<N>> children = new LinkedHashSet<NodeWrapper<N>>();
            for (NodeWrapper<N> node : nodes) {
                children.addAll(stepExpr.resolve(stepExprContext, node));
            }
            stepExprContext = stepExprContext.clone(children.size());
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
