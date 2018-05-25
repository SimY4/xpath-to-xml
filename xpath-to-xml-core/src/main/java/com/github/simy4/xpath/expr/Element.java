package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

public class Element extends AbstractStepExpr {

    private final QName element;
    private final Predicate<Node> filter;

    /**
     * Constructor.
     *
     * @param element    element name
     * @param predicates element predicates
     */
    public Element(QName element, Iterable<Predicate<ViewContext<?>>> predicates) {
        super(predicates);
        this.element = element;
        this.filter = new QNamePredicate(element);
    }

    @Override
    <N extends Node> NodeSetView<N> traverseStep(Navigator<N> navigator, NodeView<N> parentView) {
        return NodeSetView.filtered(navigator.elementsOf(parentView.getNode()), filter);
    }

    @Override
    public <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException {
        if (isWildcard(element)) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return new NodeView<N>(context.getNavigator().createElement(context.getCurrent().getNode(), element));
    }

    @Override
    public String toString() {
        return element.toString() + super.toString();
    }

}
