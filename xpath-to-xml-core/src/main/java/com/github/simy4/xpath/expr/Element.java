package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import javax.xml.namespace.QName;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Element extends AbstractStepExpr {

    private final QName element;

    public Element(QName element, List<Predicate> predicateList) {
        super(predicateList);
        this.element = element;
    }

    @Override
    <N> Set<NodeWrapper<N>> traverseStep(ExprContext<N> navigator, NodeWrapper<N> parentNode) {
        final Set<NodeWrapper<N>> nodes = new LinkedHashSet<NodeWrapper<N>>();
        for (NodeWrapper<N> element : navigator.getNavigator().elementsOf(parentNode)) {
            if (0 == qnameComparator.compare(this.element, element.getNodeName())) {
                nodes.add(element);
            }
        }
        return nodes;
    }

    @Override
    <N> NodeWrapper<N> createStepNode(ExprContext<N> navigator) {
        if ("*".equals(element.getNamespaceURI()) || "*".equals(element.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return navigator.getNavigator().createElement(element);
    }

    @Override
    public String toString() {
        return element.toString() + super.toString();
    }

}
