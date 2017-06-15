package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
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
        left.visit(new EqLeftApplicationVisitor<N>(right));
    }

    @Override
    public String toString() {
        return "=";
    }

    private static final class EqLeftApplicationVisitor<N> implements ViewVisitor<N> {

        private final View<N> right;

        private EqLeftApplicationVisitor(View<N> right) {
            this.right = right;
        }

        @Override
        public void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            right.visit(new ViewVisitor<N>() {
                @Override
                public void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {

                }

                @Override
                public void visit(LiteralView<N> literal) throws XmlBuilderException {

                }

                @Override
                public void visit(NumberView<N> number) throws XmlBuilderException {

                }

                @Override
                public void visit(NodeView<N> node) throws XmlBuilderException {

                }
            });
        }

        @Override
        public void visit(LiteralView<N> literal) throws XmlBuilderException {
            right.visit(new ViewVisitor<N>() {
                @Override
                public void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {

                }

                @Override
                public void visit(LiteralView<N> literal) throws XmlBuilderException {

                }

                @Override
                public void visit(NumberView<N> number) throws XmlBuilderException {

                }

                @Override
                public void visit(NodeView<N> node) throws XmlBuilderException {

                }
            });
        }

        @Override
        public void visit(NumberView<N> number) throws XmlBuilderException {
            right.visit(new ViewVisitor<N>() {
                @Override
                public void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {

                }

                @Override
                public void visit(LiteralView<N> literal) throws XmlBuilderException {

                }

                @Override
                public void visit(NumberView<N> number) throws XmlBuilderException {

                }

                @Override
                public void visit(NodeView<N> node) throws XmlBuilderException {

                }
            });
        }

        @Override
        public void visit(NodeView<N> node) throws XmlBuilderException {
            right.visit(new ViewVisitor<N>() {
                @Override
                public void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {

                }

                @Override
                public void visit(LiteralView<N> literal) throws XmlBuilderException {

                }

                @Override
                public void visit(NumberView<N> number) throws XmlBuilderException {

                }

                @Override
                public void visit(NodeView<N> node) throws XmlBuilderException {

                }
            });
        }

    }

}
