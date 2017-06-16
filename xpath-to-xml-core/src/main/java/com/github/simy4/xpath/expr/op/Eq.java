package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.AbstractViewVisitor;
import com.github.simy4.xpath.navigator.view.LiteralView;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.NumberView;
import com.github.simy4.xpath.navigator.view.View;
import com.github.simy4.xpath.navigator.view.ViewVisitor;

public class Eq implements Op {

    @Override
    public <N> boolean test(View<N> left, View<N> right) {
        return 0 == left.compareTo(right);
    }

    @Override
    public <N> void apply(Navigator<N> navigator, View<N> left, View<N> right) throws XmlBuilderException {
        right.visit(new EqRightApplicationVisitor<N>(navigator, left));
    }

    @Override
    public String toString() {
        return "=";
    }

    private static final class EqRightApplicationVisitor<N> implements ViewVisitor<N, Void> {

        private final Navigator<N> navigator;
        private final View<N> left;

        private EqRightApplicationVisitor(Navigator<N> navigator, View<N> left) {
            this.navigator = navigator;
            this.left = left;
        }

        @Override
        public Void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            if (nodeSet.isEmpty()) {
                throw new XmlBuilderException("Can not modify empty node set");
            }
            return nodeSet.iterator().next().visit(this);
        }

        @Override
        public Void visit(final LiteralView<N> literal) throws XmlBuilderException {
            return left.visit(new EqLeftApplicationVisitor<N>() {

                @Override
                public Void visit(NodeView<N> node) throws XmlBuilderException {
                    navigator.setText(node.getNode(), literal.getLiteral());
                    return null;
                }

            });
        }

        @Override
        public Void visit(final NumberView<N> number) throws XmlBuilderException {
            return left.visit(new EqLeftApplicationVisitor<N>() {

                @Override
                public Void visit(NodeView<N> node) throws XmlBuilderException {
                    navigator.setText(node.getNode(), number.getNumber().toString());
                    return null;
                }

            });
        }

        @Override
        public Void visit(final NodeView<N> rightNode) throws XmlBuilderException {
            return left.visit(new EqLeftApplicationVisitor<N>() {

                @Override
                public Void visit(NodeView<N> leftNode) throws XmlBuilderException {
                    navigator.setText(leftNode.getNode(), rightNode.getNode().getText());
                    return null;
                }

            });

        }

    }

    private abstract static class EqLeftApplicationVisitor<N> extends AbstractViewVisitor<N, Void> {

        @Override
        public final Void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            if (nodeSet.isEmpty()) {
                return returnDefault(nodeSet);
            }
            for (View<N> node : nodeSet) {
                node.visit(this);
            }
            return null;
        }

        @Override
        protected final Void returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

}
