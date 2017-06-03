package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.List;

public class Identity implements StepExpr {

    @Override
    public <N> List<NodeWrapper<N>> traverse(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes) {
        return parentNodes;
    }

    @Override
    public <N> NodeWrapper<N> createNode(Navigator<N> navigator) {
        throw new XmlBuilderException("Identity node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".";
    }

}
