package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;

import javax.xml.namespace.QName;
import java.util.List;

public class Attribute extends AbstractStepExpr {

    private final QName attribute;

    public Attribute(QName attribute, List<Expr> predicates) {
        super(predicates);
        this.attribute = attribute;
    }

    @Override
    <N> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> parentNode) {
        final NodeSetView.Builder<N> builder = NodeSetView.builder();
        for (NodeView<N> attribute : context.getNavigator().attributesOf(parentNode)) {
            if (0 == qnameComparator.compare(this.attribute, attribute.getNodeName())) {
                builder.add(attribute);
            }
        }
        return builder.build();
    }

    @Override
    <N> NodeView<N> createStepNode(ExprContext<N> context) {
        if ("*".equals(attribute.getNamespaceURI()) || "*".equals(attribute.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return context.getNavigator().createAttribute(attribute);
    }

    @Override
    public String toString() {
        return "@" + attribute + super.toString();
    }

}
