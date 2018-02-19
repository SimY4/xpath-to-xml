package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

import java.util.Iterator;
import java.util.List;

public class PathExpr implements Expr {

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> children = context.getCurrent();
        for (StepExpr stepExpr : pathExpr) {
            children = children.flatMap(context.getNavigator(), context.isGreedy(), stepExpr::resolve);
        }
        return children;
    }

    @Override
    public String toString() {
        final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
        final StringBuilder stringBuilder = new StringBuilder();
        if (pathExprIterator.hasNext()) {
            stringBuilder.append(pathExprIterator.next());
            while (pathExprIterator.hasNext()) {
                stringBuilder.append('/').append(pathExprIterator.next());
            }
        }
        return stringBuilder.toString();
    }

}
