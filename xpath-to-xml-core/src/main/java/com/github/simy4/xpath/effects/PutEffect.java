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
    public <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException {
        final ViewContext<N> context = new ViewContext<N>(navigator, new NodeView<N>(xml), true);
        expr.resolve(context).visit(new EagerVisitor<N>());
    }

    private static final class EagerVisitor<N extends Node> extends AbstractViewVisitor<N> {

        @Override
        @SuppressWarnings("StatementWithEmptyBody")
        public void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            for (NodeView<N> ignored : nodeSet) { } // eagerly consume resolved iterable
        }

        @Override
        protected void returnDefault(View<N> view) {
            /* NO OP */
        }

    }

}
