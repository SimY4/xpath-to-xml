package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

public class Attribute extends AbstractStepExpr {

    private final QName attribute;

    public Attribute(QName attribute, Iterable<Predicate<ViewContext<?>>> predicates) {
        super(predicates);
        this.attribute = attribute;
    }

    @Override
    <N extends Node> NodeSetView<N> traverseStep(Navigator<N> navigator, NodeView<N> parentView) {
        return NodeSetView.filtered(navigator.attributesOf(parentView.getNode()),
                new com.github.simy4.xpath.util.Predicate<N>() {
                    @Override
                    public boolean test(N attribute) {
                        return 0 == qnameComparator.compare(Attribute.this.attribute, attribute.getName());
                    }
                });
    }

    @Override
    <N extends Node> N createStepNode(Navigator<N> navigator, NodeView<N> parentView) throws XmlBuilderException {
        if ("*".equals(attribute.getNamespaceURI()) || "*".equals(attribute.getLocalPart())) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return navigator.createAttribute(parentView.getNode(), attribute);
    }

    @Override
    public String toString() {
        return "@" + attribute + super.toString();
    }

}
