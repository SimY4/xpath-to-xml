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

public class PutEffect implements Effect {

    private final Expr expr;

    public PutEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N extends Node> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final NodeView<N> xml = new NodeView<N>(navigator.xml());
        final ViewContext<N> context = new ViewContext<N>(navigator, xml, true);
        expr.resolve(context).visit(new EagerVisitor<N>());
    }

    private static final class EagerVisitor<N extends Node> extends AbstractViewVisitor<N, Void> {

        @Override
        @SuppressWarnings("StatementWithEmptyBody")
        public Void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            for (NodeView<N> ignored : nodeSet) { } // eagerly consume resolved iterable
            return null;
        }

        @Override
        protected Void returnDefault(View<N> view) {
            return null;
        }

    }

}
