package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.ViewContext;

public class NumberExpr implements Expr {

    private final NumberView number;

    public NumberExpr(double number) {
        this.number = new NumberView(number);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Node> NumberView<N> resolve(ViewContext<N> context) {
        return number;
    }

    @Override
    public boolean test(ViewContext<?> context) {
        double number = resolve(context).toNumber();
        return number == context.getPosition()
                || context.isGreedy() && !context.hasNext() && number > context.getPosition() && test(context, number);
    }

    private <N extends Node> boolean test(ViewContext<N> context, double number) {
        final N node = context.getCurrent().getNode();
        long numberOfNodesToCreate = (long) number - context.getPosition();
        do {
            context.getNavigator().prependCopy(node);
        } while (--numberOfNodesToCreate > 0);
        return true;
    }

    @Override
    public String toString() {
        return number.toString();
    }

}
