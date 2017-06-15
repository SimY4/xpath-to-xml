package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

public class PutValueEffect implements Effect {

    private final Expr expr;
    private final String value;

    public PutValueEffect(Expr expr, Object value) {
        this.expr = expr;
        this.value = String.valueOf(value);
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<>(navigator, true, 1);
        final View<N> view = expr.resolve(context, new NodeView<>(navigator.xml()));
        view.visit(new PutValueVisitor<>(navigator, value));
    }

    private static final class PutValueVisitor<N> extends AbstractViewVisitor<N, Void> {

        private final Navigator<N> navigator;
        private final String value;

        private PutValueVisitor(Navigator<N> navigator, String value) {
            this.navigator = navigator;
            this.value = value;
        }

        @Override
        public Void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            for (View<N> node : nodeSet) {
                node.visit(this);
            }
            return null;
        }

        @Override
        public Void visit(NodeView<N> node) throws XmlBuilderException {
            navigator.setText(node.getNode(), value);
            return null;
        }

        @Override
        protected Void returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Failed to put value into XML. Read-only view was resolved: " + view);
        }

    }

}
