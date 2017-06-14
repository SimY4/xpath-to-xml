package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import javax.xml.namespace.QName;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Attribute extends AbstractStepExpr {

    private final QName attribute;

    public Attribute(QName attribute, List<Predicate> predicates) {
        super(predicates);
        this.attribute = attribute;
    }

    @Override
    <N> Set<NodeView<N>> traverseStep(ExprContext<N> context, NodeView<N> parentNode) {
        final Set<NodeView<N>> nodes = new LinkedHashSet<NodeView<N>>();
        for (NodeView<N> attribute : context.getNavigator().attributesOf(parentNode)) {
            if (0 == qnameComparator.compare(this.attribute, attribute.getNodeName())) {
                nodes.add(attribute);
            }
        }
        return nodes;
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
