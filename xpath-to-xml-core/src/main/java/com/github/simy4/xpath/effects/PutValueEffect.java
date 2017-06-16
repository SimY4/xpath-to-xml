package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.AbstractViewVisitor;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.View;

public class PutValueEffect implements Effect {

    private final Expr expr;
    private final String value;

    public PutValueEffect(Expr expr, Object value) {
        this.expr = expr;
        this.value = String.valueOf(value);
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<N>(navigator, true, 1);
        final View<N> view = expr.resolve(context, new NodeView<N>(navigator.xml()));
        view.visit(new PutValueVisitor<N>(navigator, value));
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
            return returnDefault(nodeSet);
        }

        @Override
        public Void visit(NodeView<N> node) throws XmlBuilderException {
            navigator.setText(node.getNode(), value);
            return returnDefault(node);
        }

        @Override
        protected Void returnDefault(View<N> ignored) {
            return null; /* NO OP */
        }

    }

}
