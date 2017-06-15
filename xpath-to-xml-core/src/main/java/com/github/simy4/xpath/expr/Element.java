package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;

import javax.xml.namespace.QName;
import java.util.List;

public class Element extends AbstractStepExpr {

    private final QName element;

    public Element(QName element, List<Expr> predicateList) {
        super(predicateList);
        this.element = element;
    }

    @Override
    <N> NodeSetView<N> traverseStep(ExprContext<N> navigator, NodeView<N> parentNode) {
        final NodeSetView.Builder<N> builder = NodeSetView.builder();
        for (NodeView<N> element : navigator.getNavigator().elementsOf(parentNode)) {
            if (0 == qnameComparator.compare(this.element, element.getNodeName())) {
                builder.add(element);
            }
        }
        return builder.build();
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
