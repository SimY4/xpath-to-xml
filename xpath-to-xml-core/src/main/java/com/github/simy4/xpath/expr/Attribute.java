package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class Attribute implements StepExpr {

    private final QName attribute;

    public Attribute(QName attribute) {
        this.attribute = attribute;
    }

    @Override
    public <N> List<NodeWrapper<N>> traverse(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes) {
        final List<NodeWrapper<N>> nodes = new ArrayList<NodeWrapper<N>>();
        for (NodeWrapper<N> parentNode : parentNodes) {
            nodes.addAll(traverseEach(navigator, parentNode));
        }
        return nodes;
    }

    @Override
    public <N> NodeWrapper<N> createNode(Navigator<N> navigator) {
        if ("*".equals(attribute.getNamespaceURI()) || "*".equals(attribute.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return navigator.createAttribute(attribute);
    }

    private <N> List<NodeWrapper<N>> traverseEach(Navigator<N> navigator, NodeWrapper<N> parentNode) {
        final List<NodeWrapper<N>> nodes = new ArrayList<NodeWrapper<N>>();
        for (NodeWrapper<N> attribute : navigator.attributesOf(parentNode)) {
            if (0 == Comparators.QNAME_COMPARATOR.compare(this.attribute, attribute.getNodeName())) {
                nodes.add(attribute);
            }
        }
        return nodes;
    }

    @Override
    public String toString() {
        return "@" + attribute;
    }

}
