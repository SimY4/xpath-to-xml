package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

public class EqualsExpr extends AbstractOperationExpr {

    public EqualsExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right)
            throws XmlBuilderException {
        boolean eq = 0 == left.compareTo(right);
        if (!eq && context.isGreedy() && !context.hasNext()) {
            left.visit(new ApplicationVisitor<>(context.getNavigator(), right));
            eq = true;
        }
        return BooleanView.of(eq);
    }

    @Override
    String operator() {
        return "=";
    }

    private static final class ApplicationVisitor<N extends Node> extends AbstractViewVisitor<N> {

        private final Navigator<N> navigator;
        private final View<N> right;

        private ApplicationVisitor(Navigator<N> navigator, View<N> right) {
            this.navigator = navigator;
            this.right = right;
        }

        @Override
        public void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            for (NodeView<N> node : nodeSet) {
                navigator.setText(node.getNode(), right.toString());
            }
        }

        @Override
        protected void returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

}
