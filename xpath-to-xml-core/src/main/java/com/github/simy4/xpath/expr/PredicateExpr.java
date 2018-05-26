package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

public class PredicateExpr implements Expr {

    private final Expr predicate;

    public PredicateExpr(Expr predicate) {
        this.predicate = predicate;
    }

    @Override
    public <N extends Node> BooleanView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        return predicate.resolve(context).visit(new PredicateVisitor<N>(context));
    }

    @Override
    public String toString() {
        return "[" + predicate + "]";
    }

    private static final class PredicateVisitor<T extends Node> extends AbstractViewVisitor<T, BooleanView<T>> {

        private final ViewContext<T> context;

        private PredicateVisitor(ViewContext<T> context) {
            this.context = context;
        }

        @Override
        public BooleanView<T> visit(NumberView<T> numberView) throws XmlBuilderException {
            final double number = numberView.toNumber();
            return BooleanView.of(0 == Double.compare(number, context.getPosition())
                    || (context.isGreedy() && !context.hasNext() && number > context.getPosition()
                    && test(context, number)));
        }

        @Override
        protected BooleanView<T> returnDefault(View<T> view) throws XmlBuilderException {
            return BooleanView.of(view.toBoolean());
        }

        private <N extends Node> boolean test(ViewContext<N> context, double number) {
            final N node = context.getCurrent().getNode();
            long numberOfNodesToCreate = (long) number - context.getPosition();
            do {
                context.getNavigator().prependCopy(node);
            } while (--numberOfNodesToCreate > 0);
            return true;
        }

    }

}
