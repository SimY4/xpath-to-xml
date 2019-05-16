package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewVisitor;

import java.io.Serializable;

public class PutEffect implements Effect, Serializable {

    private static final long serialVersionUID = 1L;

    private static final ViewVisitor<Node, Void> eagerVisitor = new EagerVisitor();

    private final Expr expr;

    public PutEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException {
        expr.resolve(navigator, new NodeView<>(xml), true).visit((ViewVisitor<N, Void>) eagerVisitor);
    }

    private static final class EagerVisitor extends AbstractViewVisitor<Node, Void> {

        @Override
        @SuppressWarnings({"StatementWithEmptyBody", "UnusedVariable"})
        public Void visit(IterableNodeView<Node> nodeSet) throws XmlBuilderException {
            for (var ignored : nodeSet) { } // eagerly consume resolved iterable
            return null;
        }

        @Override
        protected Void returnDefault(View<Node> view) {
            return null;
        }

    }

}
