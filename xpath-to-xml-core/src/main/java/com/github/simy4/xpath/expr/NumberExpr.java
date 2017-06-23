package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

public class NumberExpr implements Expr {

    private final NumberView number;

    public NumberExpr(double number) {
        this.number = new NumberView(number);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N> NumberView<N> resolve(ExprContext<N> context, View<N> xml) {
        return number;
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        double number = resolve(context, xml).toNumber();
        if (number == context.getPosition()) {
            return true;
        } else if (number > context.getPosition() && context.shouldCreate()) {
            return xml.visit(new NumberPredicateVisitor<N>(context, (long) number - context.getPosition()));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return number.toString();
    }

    private static final class NumberPredicateVisitor<N> extends AbstractViewVisitor<N, Boolean> {

        private final ExprContext<N> context;
        private final long numberOfNodesToCreate;

        private NumberPredicateVisitor(ExprContext<N> context, long numberOfNodesToCreate) {
            this.context = context;
            this.numberOfNodesToCreate = numberOfNodesToCreate;
        }

        @Override
        public Boolean visit(NodeView<N> view) throws XmlBuilderException {
            final Node<N> node = view.getNode();
            long numberOfNodesToCreate = this.numberOfNodesToCreate;
            do {
                context.getNavigator().prependCopy(node);
            } while (--numberOfNodesToCreate > 0);
            return true;
        }

        @Override
        protected Boolean returnDefault(View<N> view) throws XmlBuilderException {
            throw new XmlBuilderException("Can not modify read-only node: " + view);
        }

    }

}
