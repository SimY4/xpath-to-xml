package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Parent implements StepExpr {

    @Override
    public <N> List<NodeWrapper<N>> traverse(Navigator<N> navigator, List<NodeWrapper<N>> nodes) {
        final Set<NodeWrapper<N>> parents = new LinkedHashSet<NodeWrapper<N>>();
        for (NodeWrapper<N> node : nodes) {
            NodeWrapper<N> parent = navigator.parentOf(node);
            if (null != parent) {
                parents.add(parent);
            }
        }
        return new ArrayList<NodeWrapper<N>>(parents);
    }

    @Override
    public <N> NodeWrapper<N> createNode(Navigator<N> navigator) {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "..";
    }

}
