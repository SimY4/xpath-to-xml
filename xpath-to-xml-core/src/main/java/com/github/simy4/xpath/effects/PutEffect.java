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
import com.github.simy4.xpath.view.ViewContext;
import com.github.simy4.xpath.view.ViewVisitor;

public class PutEffect implements Effect {

    private static final ViewVisitor<? extends Node, Void> eagerVisitor = new EagerVisitor<>();

    private final Expr expr;

    public PutEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException {
        final var context = new ViewContext<>(navigator, new NodeView<>(xml), true);
        expr.resolve(context).visit((ViewVisitor<N, Void>) eagerVisitor);
    }

    private static final class EagerVisitor<N extends Node> extends AbstractViewVisitor<N, Void> {

        @Override
        @SuppressWarnings("StatementWithEmptyBody")
        public Void visit(IterableNodeView<N> nodeSet) {
            for (var ignored : nodeSet) { } // eagerly consume resolved iterable
            return null;
        }

        @Override
        protected Void returnDefault(View<N> view) {
            return null;
        }

    }

}
