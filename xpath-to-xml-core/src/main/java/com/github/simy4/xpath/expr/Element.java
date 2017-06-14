package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

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
    <N> Set<NodeView<N>> traverseStep(ExprContext<N> navigator, NodeView<N> parentNode) {
        final Set<NodeView<N>> nodes = new LinkedHashSet<NodeView<N>>();
        for (NodeView<N> element : navigator.getNavigator().elementsOf(parentNode)) {
            if (0 == qnameComparator.compare(this.element, element.getNodeName())) {
                nodes.add(element);
            }
        }
        return nodes;
    }

    @Override
    <N> NodeView<N> createStepNode(ExprContext<N> navigator) {
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
