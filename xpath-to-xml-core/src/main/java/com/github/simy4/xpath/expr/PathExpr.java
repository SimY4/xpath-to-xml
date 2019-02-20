package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

public class PathExpr implements Expr, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<StepExpr> pathExpr;

    public PathExpr(List<StepExpr> pathExpr) {
        this.pathExpr = pathExpr;
    }

    @Override
    public <N extends Node> IterableNodeView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException {
        final boolean newGreedy = !view.hasNext() && greedy;
        IterableNodeView<N> children = view;
        for (final StepExpr stepExpr : pathExpr) {
            children = children.flatMap(child -> stepExpr.resolve(navigator, child, newGreedy));
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
