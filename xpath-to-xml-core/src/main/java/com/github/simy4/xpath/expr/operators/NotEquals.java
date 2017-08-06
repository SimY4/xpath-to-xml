package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

class NotEquals implements Operator {

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right)
            throws XmlBuilderException {
        boolean eq = 0 == left.compareTo(right);
        if (eq && context.isGreedy() && !context.hasNext()) {
            left.visit(new ApplicationVisitor<>(context.getNavigator(), right));
            eq = false;
        }
        return BooleanView.of(!eq);
    }

    @Override
    public String toString() {
        return "!=";
    }

    private static final class ApplicationVisitor<N extends Node> extends AbstractViewVisitor<N, View<N>> {

        private final Navigator<N> navigator;
        private final View<N> right;

        private ApplicationVisitor(Navigator<N> navigator, View<N> right) {
            this.navigator = navigator;
            this.right = right;
        }

        @Override
        public View<N> visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            for (NodeView<N> node : nodeSet) {
                navigator.setText(node.getNode(), Boolean.toString(!right.toBoolean()));
            }
            return nodeSet;
        }

        @Override
        protected View<N> returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

}
