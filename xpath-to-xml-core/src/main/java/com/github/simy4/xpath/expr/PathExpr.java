package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

import java.util.List;
import java.util.StringJoiner;

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
        final StringJoiner stringJoiner = new StringJoiner("/");
        for (StepExpr stepExpr : pathExpr) {
            stringJoiner.add(stepExpr.toString());
        }
        return stringJoiner.toString();
    }

}
