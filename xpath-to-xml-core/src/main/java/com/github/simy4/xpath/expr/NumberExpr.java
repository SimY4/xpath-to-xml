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
    public <N extends Node> boolean match(ViewContext<N> context) {
        double number = resolve(context).toNumber();
        if (number == context.getPosition()) {
            return true;
        } else if (context.isGreedy() && !context.hasNext() && number > context.getPosition()) {
            final N node = context.getCurrent().getNode();
            long numberOfNodesToCreate = (long) number - context.getPosition();
            do {
                context.getNavigator().prependCopy(node);
            } while (--numberOfNodesToCreate > 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return number.toString();
    }

}
