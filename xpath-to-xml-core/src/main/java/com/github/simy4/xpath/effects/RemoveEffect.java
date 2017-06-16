package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

public class RemoveEffect implements Effect {

    private final Expr expr;

    public RemoveEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<N>(navigator, false, 1);
        final View<N> view = expr.resolve(context, new NodeView<N>(navigator.xml()));
        view.visit(new RemoveVisitor<N>(navigator));
    }

    private static final class RemoveVisitor<N> extends AbstractViewVisitor<N, Void> {

        private final Navigator<N> navigator;

        private RemoveVisitor(Navigator<N> navigator) {
            this.navigator = navigator;
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
            navigator.remove(node.getNode());
            return returnDefault(node);
        }

        @Override
        protected Void returnDefault(View<N> ignored) {
            return null; /* NO OP */
        }

    }

}
