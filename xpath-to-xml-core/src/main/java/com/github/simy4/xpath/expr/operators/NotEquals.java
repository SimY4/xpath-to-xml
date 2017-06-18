package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

class NotEquals implements Operator {

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> left, View<N> right) throws XmlBuilderException {
        boolean eq = 0 == left.compareTo(right);
        if (eq && context.shouldCreate()) {
            left.visit(new ApplicationVisitor<N>(context.getNavigator(), right));
            eq = false;
        }
        return BooleanView.of(!eq);
    }

    @Override
    public String toString() {
        return "!=";
    }

    private static final class ApplicationVisitor<N> extends AbstractViewVisitor<N, View<N>> {

        private final Navigator<N> navigator;
        private final View<N> right;

        private ApplicationVisitor(Navigator<N> navigator, View<N> right) {
            this.navigator = navigator;
            this.right = right;
        }

        @Override
        public View<N> visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            for (View<N> node : nodeSet) {
                node.visit(this);
            }
            return nodeSet;
        }

        @Override
        public View<N> visit(NodeView<N> node) throws XmlBuilderException {
            navigator.setText(node.getNode(), Boolean.toString(!right.toBoolean()));
            return node;
        }

        @Override
        protected View<N> returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

}
