package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.View;

import java.util.Iterator;
import java.util.List;

public class PathExpr implements Expr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> xml) {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        ExprContext<N> stepExprContext = context;
        NodeSetView<N> children;
        do {
            final StepExpr stepExpr = pathExprIterator.next();
            children = stepExpr.resolve(stepExprContext, xml);
            stepExprContext = stepExprContext.clone(children.size());
            xml = children;
        } while (pathExprIterator.hasNext() && !children.isEmpty());
        return xml;
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return !resolve(context, xml).isEmpty();
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
