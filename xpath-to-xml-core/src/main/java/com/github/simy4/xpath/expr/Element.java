package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class Element extends AbstractStepExpr {

    private final QName element;

    public Element(QName element, List<Expr> predicateList) {
        super(predicateList);
        this.element = element;
    }

    @Override
    <N> List<NodeWrapper<N>> traverseStep(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes) {
        final List<NodeWrapper<N>> nodes = new ArrayList<NodeWrapper<N>>();
        for (NodeWrapper<N> parentNode : parentNodes) {
            nodes.addAll(traverseEach(navigator, parentNode));
        }
        return nodes;
    }

    @Override
    <N> NodeWrapper<N> createStepNode(Navigator<N> navigator) {
        if ("*".equals(element.getNamespaceURI()) || "*".equals(element.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return navigator.createElement(element);
    }

    private <N> List<NodeWrapper<N>> traverseEach(Navigator<N> navigator, NodeWrapper<N> parentNode) {
        final List<NodeWrapper<N>> nodes = new ArrayList<NodeWrapper<N>>();
        for (NodeWrapper<N> element : navigator.elementsOf(parentNode)) {
            if (0 == Comparators.QNAME_COMPARATOR.compare(this.element, element.getNodeName())) {
                nodes.add(element);
            }
        }
        return nodes;
    }

    @Override
    public String toString() {
        return element.toString() + super.toString();
    }

}
