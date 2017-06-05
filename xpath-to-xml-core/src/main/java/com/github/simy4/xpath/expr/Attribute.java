package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import javax.xml.namespace.QName;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Attribute extends AbstractStepExpr {

    private final QName attribute;

    public Attribute(QName attribute, List<Expr> predicates) {
        super(predicates);
        this.attribute = attribute;
    }

    @Override
    <N> Set<NodeWrapper<N>> traverseStep(ExprContext<N> context, NodeWrapper<N> parentNode) {
        final Set<NodeWrapper<N>> nodes = new LinkedHashSet<NodeWrapper<N>>();
        for (NodeWrapper<N> attribute : context.getNavigator().attributesOf(parentNode)) {
            if (0 == Comparators.QNAME_COMPARATOR.compare(this.attribute, attribute.getNodeName())) {
                nodes.add(attribute);
            }
        }
        return nodes;
    }

    @Override
    <N> NodeWrapper<N> createStepNode(ExprContext<N> context) {
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
