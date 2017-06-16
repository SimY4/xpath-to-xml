package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewVisitor;

public class UnaryExpr implements Expr {

    private final Expr valueExpr;

    public UnaryExpr(Expr valueExpr) {
        this.valueExpr = valueExpr;
    }

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException {
        return valueExpr.resolve(context, xml).visit(new UnaryExprVisitor<N>());
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return !resolve(context, xml).isEmpty();
    }

    @Override
    public String toString() {
        return "-(" + valueExpr + ')';
    }

    private static final class UnaryExprVisitor<N> implements ViewVisitor<N, NumberView<N>> {

        @Override
        public NumberView<N> visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            if (nodeSet.isEmpty()) {
                return new NumberView<N>(Double.NaN);
            } else {
                return nodeSet.iterator().next().visit(this);
            }
        }

        @Override
        public NumberView<N> visit(LiteralView<N> literal) throws XmlBuilderException {
            try {
                return new NumberView<N>(-Double.parseDouble(literal.getLiteral()));
            } catch (NumberFormatException nfe) {
                return new NumberView<N>(Double.NaN);
            }
        }

        @Override
        public NumberView<N> visit(NumberView<N> number) throws XmlBuilderException {
            return new NumberView<N>(-number.getNumber().doubleValue());
        }

        @Override
        public NumberView<N> visit(NodeView<N> node) throws XmlBuilderException {
            try {
                return new NumberView<N>(-Double.parseDouble(node.getNode().getText()));
            } catch (NumberFormatException nfe) {
                return new NumberView<N>(Double.NaN);
            }
        }

    }

}
