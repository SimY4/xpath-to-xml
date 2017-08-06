package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

public class Element extends AbstractStepExpr {

    private final QName element;

    public Element(QName element, Iterable<? extends Predicate> predicates) {
        super(predicates);
        this.element = element;
    }

    @Override
    <N extends Node> NodeSetView<N> traverseStep(Navigator<N> navigator, NodeView<N> parentView) {
        return NodeSetView.filtered(navigator.elementsOf(parentView.getNode()),
                element -> 0 == qnameComparator.compare(Element.this.element, element.getName()));
    }

    @Override
    <N extends Node> N createStepNode(Navigator<N> navigator, NodeView<N> parentView) throws XmlBuilderException {
        if ("*".equals(element.getNamespaceURI()) || "*".equals(element.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return navigator.createElement(parentView.getNode(), element);
    }

    @Override
    public String toString() {
        return element.toString() + super.toString();
    }

}
