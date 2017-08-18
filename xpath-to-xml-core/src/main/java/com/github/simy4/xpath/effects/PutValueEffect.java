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

public class PutValueEffect implements Effect {

    private final Expr expr;
    private final String value;

    public PutValueEffect(Expr expr, Object value) {
        this.expr = expr;
        this.value = String.valueOf(value);
    }

    @Override
    public <N extends Node> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final NodeView<N> xml = new NodeView<N>(navigator.xml());
        final ViewContext<N> context = new ViewContext<N>(navigator, xml, true);
        final View<N> view = expr.resolve(context);
        view.visit(new PutValueVisitor<N>(navigator, value));
    }

    private static final class PutValueVisitor<N extends Node> extends AbstractViewVisitor<N> {

        private final Navigator<N> navigator;
        private final String value;

        private PutValueVisitor(Navigator<N> navigator, String value) {
            this.navigator = navigator;
            this.value = value;
        }

        @Override
        public void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            for (NodeView<N> node : nodeSet) {
                navigator.setText(node.getNode(), value);
            }
        }

        @Override
        protected void returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Failed to put value into XML. Read-only view was resolved: " + view);
        }

    }

}
