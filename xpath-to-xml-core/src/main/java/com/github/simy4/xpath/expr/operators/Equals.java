package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

class Equals implements Operator {

    @Override
    public <N> boolean test(View<N> left, View<N> right) {
        return 0 == left.compareTo(right);
    }

    @Override
    public <N> void apply(Navigator<N> navigator, View<N> left, View<N> right) throws XmlBuilderException {
        left.visit(new EqualsApplicationVisitor<N>(navigator, right));
    }

    @Override
    public String toString() {
        return "=";
    }

    private static final class EqualsApplicationVisitor<N> extends AbstractViewVisitor<N, Void> {

        private final Navigator<N> navigator;
        private final View<N> right;

        private EqualsApplicationVisitor(Navigator<N> navigator, View<N> right) {
            this.navigator = navigator;
            this.right = right;
        }

        @Override
        public final Void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            for (View<N> node : nodeSet) {
                node.visit(this);
            }
            return null;
        }

        @Override
        public Void visit(NodeView<N> node) throws XmlBuilderException {
            navigator.setText(node.getNode(), right.toString());
            return null;
        }

        @Override
        protected final Void returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

}
