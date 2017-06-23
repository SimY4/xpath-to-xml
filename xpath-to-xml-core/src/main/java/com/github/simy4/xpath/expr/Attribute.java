package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;
import java.util.List;

public class Attribute extends AbstractStepExpr {

    private final QName attribute;

    public Attribute(QName attribute, List<Predicate> predicates) {
        super(predicates);
        this.attribute = attribute;
    }

    @Override
    <N> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> parentView) {
        final NodeSetView.Builder<N> builder = NodeSetView.builder();
        for (Node<N> attribute : context.getNavigator().attributesOf(parentView.getNode())) {
            if (0 == qnameComparator.compare(this.attribute, attribute.getNodeName())) {
                builder.add(new NodeView<>(attribute));
            }
        }
        return builder.build();
    }

    @Override
    <N> NodeView<N> createStepNode(ExprContext<N> context, NodeView<N> parentView) throws XmlBuilderException {
        if ("*".equals(attribute.getNamespaceURI()) || "*".equals(attribute.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return new NodeView<>(context.getNavigator().createAttribute(parentView.getNode(), attribute));
    }

    @Override
    public String toString() {
        return "@" + attribute + super.toString();
    }

}
