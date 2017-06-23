package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.View;

import java.util.Iterator;
import java.util.List;

public class PathExpr extends AbstractExpr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        ExprContext<N> stepExprContext = context;
        NodeSetView<N> children;
        do {
            final StepExpr stepExpr = pathExprIterator.next();
            children = stepExpr.resolve(stepExprContext, xml);
            stepExprContext = stepExprContext.clone(children.size());
            xml = children;
        } while (pathExprIterator.hasNext() && children.toBoolean());
        return xml;
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
