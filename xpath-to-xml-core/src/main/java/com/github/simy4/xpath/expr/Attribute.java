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
    private final Predicate<Node> filter;

    /**
     * Constructor.
     *
     * @param attribute  attribute name
     * @param predicates attribute predicates
     */
    public Attribute(QName attribute, Iterable<Expr> predicates) {
        super(predicates);
        this.attribute = attribute;
        this.filter = new QNamePredicate(attribute);
    }

    @Override
    <N extends Node> NodeSetView<N> traverseStep(Navigator<N> navigator, NodeView<N> parentView) {
        return NodeSetView.filtered(navigator.attributesOf(parentView.getNode()), filter);
    }

    @Override
    public <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException {
        if (isWildcard(attribute)) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return new NodeView<N>(context.getNavigator().createAttribute(context.getCurrent().getNode(), attribute));
    }

    @Override
    public String toString() {
        return "@" + attribute + super.toString();
    }

}
