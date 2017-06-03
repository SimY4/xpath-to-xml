package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PathExpr implements Expr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N> List<NodeWrapper<N>> apply(Navigator<N> navigator, NodeWrapper<N> xml, boolean greedy) {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        List<NodeWrapper<N>> nodes = Collections.singletonList(xml);
        while (pathExprIterator.hasNext() && !nodes.isEmpty()) {
            final StepExpr stepExpr = pathExprIterator.next();
            List<NodeWrapper<N>> children = stepExpr.traverse(navigator, nodes);
            if (children.isEmpty() && greedy) {
                final NodeWrapper<N> newNode = stepExpr.createNode(navigator);
                final NodeWrapper<N> lastNode = nodes.get(nodes.size() - 1);
                navigator.append(lastNode, newNode);
                children = Collections.singletonList(newNode);
            }
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
