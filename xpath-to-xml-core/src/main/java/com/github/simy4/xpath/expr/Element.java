package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;
import java.util.List;

public class Element extends AbstractStepExpr {

    private final QName element;

    public Element(QName element, List<Predicate> predicateList) {
        super(predicateList);
        this.element = element;
    }

    @Override
    <N> NodeSetView<N> traverseStep(ExprContext<N> navigator, NodeView<N> parentView) {
        final NodeSetView.Builder<N> builder = NodeSetView.builder();
        for (Node<N> element : navigator.getNavigator().elementsOf(parentView.getNode())) {
            if (0 == qnameComparator.compare(this.element, element.getNodeName())) {
                builder.add(new NodeView<N>(element));
            }
        }
        return builder.build();
    }

    @Override
    <N> NodeView<N> createStepNode(ExprContext<N> navigator, NodeView<N> parentView) throws XmlBuilderException {
        if ("*".equals(element.getNamespaceURI()) || "*".equals(element.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return new NodeView<N>(navigator.getNavigator().createElement(parentView.getNode(), element));
    }

    @Override
    public String toString() {
        return element.toString() + super.toString();
    }

}
