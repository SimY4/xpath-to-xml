package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PathExpr extends AbstractExpr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N> Set<NodeView<N>> resolve(ExprContext<N> context, NodeView<N> xml) {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        Set<NodeView<N>> nodes = Collections.singleton(xml);
        ExprContext<N> stepExprContext = context;
        while (pathExprIterator.hasNext() && !nodes.isEmpty()) {
            final StepExpr stepExpr = pathExprIterator.next();
            final Set<NodeView<N>> children = new LinkedHashSet<NodeView<N>>();
            for (NodeView<N> node : nodes) {
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
