package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
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
    <N extends Node> NodeSetView<N> traverseStep(ExprContext<N> navigator, NodeView<N> parentView) {
        final NodeSetView.Builder<N> builder = NodeSetView.builder();
        for (N element : navigator.getNavigator().elementsOf(parentView.getNode())) {
            if (0 == qnameComparator.compare(this.element, element.getName())) {
                builder.add(new NodeView<N>(element));
            }
        }
        return builder.build();
    }

    @Override
    <N extends Node> N createStepNode(ExprContext<N> navigator, NodeView<N> parentView) throws XmlBuilderException {
        if ("*".equals(element.getNamespaceURI()) || "*".equals(element.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return navigator.getNavigator().createElement(parentView.getNode(), element);
    }

    @Override
    public String toString() {
        return element.toString() + super.toString();
    }

}
