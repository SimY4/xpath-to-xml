package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import com.github.simy4.xpath.view.ViewVisitor;

import java.util.Iterator;

public class NotEqualsExpr extends AbstractOperationExpr {

    public NotEqualsExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right)
            throws XmlBuilderException {
        boolean ne = 0 != left.compareTo(right);
        if (!ne && context.isGreedy() && !context.hasNext()) {
            ne = left.visit(new NotEqualsVisitor<N>(context.getNavigator(), right));
        }
        return BooleanView.of(ne);
    }

    @Override
    String operator() {
        return "!=";
    }

    private static final class NotEqualsVisitor<N extends Node> extends AbstractViewVisitor<N, Boolean> {

        private final Navigator<N> navigator;
        private final View<N> right;

        private NotEqualsVisitor(Navigator<N> navigator, View<N> right) {
            this.navigator = navigator;
            this.right = right;
        }

        @Override
        public Boolean visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            final Iterator<NodeView<N>> iterator = nodeSet.iterator();
            final View<N> negatedRight = right.visit(new ViewNegator<N>());
            if (!iterator.hasNext() || right.toString().equals(negatedRight.toString())) {
                throw new XmlBuilderException("Unable to satisfy not equals criteria for: " + right);
            }
            while (iterator.hasNext()) {
                navigator.setText(iterator.next().getNode(), negatedRight.toString());
            }
            return true;
        }

        @Override
        protected Boolean returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

    private static final class ViewNegator<N extends Node> implements ViewVisitor<N, View<N>> {

        @Override
        public View<N> visit(BooleanView<N> bool) {
            return BooleanView.of(!bool.toBoolean());
        }

        @Override
        public View<N> visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            final Iterator<NodeView<N>> iterator = nodeSet.iterator();
            if (!iterator.hasNext()) {
                throw new XmlBuilderException("Unable to satisfy not equals criteria for: " + nodeSet);
            }
            return new LiteralView<N>(new StringBuilder(iterator.next().toString()).reverse().toString());
        }

        @Override
        public View<N> visit(LiteralView<N> literal) {
            return new LiteralView<N>(new StringBuilder(literal.toString()).reverse().toString());
        }

        @Override
        public View<N> visit(NumberView<N> number) {
            return new NumberView<N>(-number.toNumber());
        }

    }

}
