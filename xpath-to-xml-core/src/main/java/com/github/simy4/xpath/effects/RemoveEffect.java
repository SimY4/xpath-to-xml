package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

public class RemoveEffect implements Effect {

    private final Expr expr;

    public RemoveEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N extends Node> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final NodeView<N> xml = new NodeView<>(navigator.xml());
        final ViewContext<N> context = new ViewContext<>(navigator, xml, false);
        final View<N> view = expr.resolve(context);
        view.visit(new RemoveVisitor<>(navigator));
    }

    private static final class RemoveVisitor<N extends Node> extends AbstractViewVisitor<N> {

        private final Navigator<N> navigator;

        private RemoveVisitor(Navigator<N> navigator) {
            this.navigator = navigator;
        }

        @Override
        public void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            for (NodeView<N> node : nodeSet) {
                navigator.remove(node.getNode());
            }
            returnDefault(nodeSet);
        }

        @Override
        protected void returnDefault(View<N> ignored) {
            /* NO OP */
        }

    }

}
