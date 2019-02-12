package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

import java.io.Serializable;
import java.util.Iterator;

public class EqualsExpr extends AbstractOperationExpr implements Serializable {

    private static final long serialVersionUID = 1L;

    public EqualsExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public <N extends Node> View<N> resolve(Navigator<N> navigator, View<N> left, View<N> right, boolean greedy)
            throws XmlBuilderException {
        boolean eq = 0 == left.compareTo(right);
        if (!eq && greedy) {
            eq = left.visit(new EqualsVisitor<N>(navigator, right));
        }
        return BooleanView.of(eq);
    }

    @Override
    protected String operator() {
        return "=";
    }

    protected static final class EqualsVisitor<N extends Node> extends AbstractViewVisitor<N, Boolean> {

        private final Navigator<N> navigator;
        private final View<N> right;

        EqualsVisitor(Navigator<N> navigator, View<N> right) {
            this.navigator = navigator;
            this.right = right;
        }

        @Override
        public Boolean visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            final Iterator<NodeView<N>> iterator = nodeSet.iterator();
            if (!iterator.hasNext()) {
                throw new XmlBuilderException("Unable to satisfy not equals criteria for: " + right);
            }
            while (iterator.hasNext()) {
                navigator.setText(iterator.next().getNode(), right.toString());
            }
            return true;
        }

        @Override
        protected Boolean returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

}
