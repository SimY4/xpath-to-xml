package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.List;

public class Identity extends AbstractStepExpr {

    public Identity(List<Expr> predicateList) {
        super(predicateList);
    }

    @Override
    <N> List<NodeWrapper<N>> traverseStep(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes) {
        return parentNodes;
    }

    @Override
    <N> NodeWrapper<N> createStepNode(Navigator<N> navigator) {
        throw new XmlBuilderException("Identity node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "." + super.toString();
    }

}
