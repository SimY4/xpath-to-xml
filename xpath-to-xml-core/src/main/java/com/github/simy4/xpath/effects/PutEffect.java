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
        final NodeView<N> xml = new NodeView<>(navigator.xml());
        final ViewContext<N> context = new ViewContext<>(navigator, xml, true);
        expr.resolve(context).visit(new EagerVisitor<>());
    }

    private static final class EagerVisitor<N extends Node> extends AbstractViewVisitor<N> {

        @Override
        public void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            nodeSet.forEach(ignored -> { }); // eagerly consume resolved iterable
        }

        @Override
        protected void returnDefault(View<N> view) {
            /* NO OP */
        }
    }

}
