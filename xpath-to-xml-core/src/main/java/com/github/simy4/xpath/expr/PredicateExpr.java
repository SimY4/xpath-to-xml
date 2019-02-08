package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

public class PredicateExpr implements Expr {

    private final Expr predicate;

    public PredicateExpr(Expr predicate) {
        this.predicate = predicate;
    }

    @Override
    public <N extends Node> BooleanView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException {
        return predicate.resolve(navigator, view, greedy).visit(new PredicateVisitor<>(navigator, view, greedy));
    }

    @Override
    public String toString() {
        return "[" + predicate + "]";
    }

    private static final class PredicateVisitor<T extends Node> extends AbstractViewVisitor<T, BooleanView<T>> {

        private final Navigator<T> navigator;
        private final NodeView<T> view;
        private final boolean greedy;

        private PredicateVisitor(Navigator<T> navigator, NodeView<T> view, boolean greedy) {
            this.navigator = navigator;
            this.view = view;
            this.greedy = greedy;
        }

        @Override
        public BooleanView<T> visit(NumberView<T> numberView) throws XmlBuilderException {
            final var number = numberView.toNumber();
            if (0 == Double.compare(number, view.getPosition())) {
                view.mark();
                return BooleanView.of(true);
            } else if (greedy && number > view.getPosition()) {
                final var node = view.getNode();
                var numberOfNodesToCreate = (long) number - view.getPosition();
                do {
                    navigator.prependCopy(node);
                } while (--numberOfNodesToCreate > 0);
                return BooleanView.of(true);
            } else {
                return BooleanView.of(false);
            }
        }

        @Override
        protected BooleanView<T> returnDefault(View<T> view) throws XmlBuilderException {
            return BooleanView.of(view.toBoolean());
        }

    }

}
