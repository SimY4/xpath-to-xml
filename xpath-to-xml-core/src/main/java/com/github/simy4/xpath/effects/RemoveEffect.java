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

import java.io.Serializable;

public class RemoveEffect implements Effect, Serializable {

    private static final long serialVersionUID = 1L;

    private final Expr expr;

    public RemoveEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException {
        expr.resolve(navigator, new NodeView<>(xml), false).visit(new RemoveVisitor<>(navigator));
    }

    private static final class RemoveVisitor<N extends Node> extends AbstractViewVisitor<N, Void> {

        private final Navigator<N> navigator;

        private RemoveVisitor(Navigator<N> navigator) {
            this.navigator = navigator;
        }

        @Override
        public Void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
            for (var node : nodeSet) {
                navigator.remove(node.getNode());
            }
            return null;
        }

        @Override
        protected Void returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Failed to remove value into XML. Read-only view was resolved: " + view);
        }

    }

}
