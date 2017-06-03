package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.List;

public class PutValueAction implements Action {

    private final Expr expr;
    private final String value;

    public PutValueAction(Expr expr, Object value) {
        this.expr = expr;
        this.value = String.valueOf(value);
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final List<NodeWrapper<N>> nodes = expr.apply(navigator, navigator.xml(), true);
        for (NodeWrapper<N> node : nodes) {
            navigator.setText(node, value);
        }
    }

}
