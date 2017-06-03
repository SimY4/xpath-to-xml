package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.List;

public class RemoveAction implements Action {

    private final Expr expr;

    public RemoveAction(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final List<NodeWrapper<N>> nodes = expr.apply(navigator, navigator.xml(), false);
        for (NodeWrapper<N> node : nodes) {
            navigator.remove(node);
        }
    }

}
